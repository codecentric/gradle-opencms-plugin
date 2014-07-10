package de.codecentric.gradle.plugin.opencms.tasks

import de.codecentric.gradle.plugin.opencms.OpenCmsModel
import de.codecentric.gradle.plugin.opencms.OpenCmsModule
import de.codecentric.gradle.plugin.opencms.OpenCmsResourceType
import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.internal.file.DefaultFileVisitDetails
import org.gradle.api.tasks.TaskAction

class CmsManifestTask extends DefaultTask {
    File dir
    OpenCmsModel cms
    int resourceCount

    def suffixToResourceType = [
            'jsp'   : 'jsp',
            'tag'   : 'jsp',
            'json'  : 'plain',
            'png'   : 'image',
            'gif'   : 'image',
            'jpg'   : 'image',
            'jpeg'  : 'image',
            'txt'   : 'plain',
            'xml'   : 'plain',
            'css'   : 'plain',
            'js'    : 'plain',
            'xsd'   : 'plain',
            'tld'   : 'plain',
            'config': 'module_config'
    ]
    def date = new Date()

    @TaskAction
    def createManifest() {
        cms.modules.each() { cmsmodule ->
            def writer = new StringWriter()
            def xml = new MarkupBuilder(writer)
            xml.doubleQuotes = true
            def manifestFile = project.file("build/opencms-cmsmodule/manifest.xml")
            xml.export() {
                info() {
                    creator(cmsmodule.cms.username)
                    opencms_version(cms.cmsVersion)
                    createdate(now())
                    infoproject(cmsmodule.cms.cmsProject)
                    export_version("7")
                }
                module() {
                    xml.name(cmsmodule.name)
                    xml.nicename() { cdata(xml, cmsmodule.nicename) }
                    xml.group(cmsmodule.group)
                    if (cmsmodule.actionClass != "")
                        xml.class(cmsmodule.actionClass)
                    else
                        xml.class()
                    xml.description() { cdata(xml, cmsmodule.description) }
                    xml.version(cmsmodule.version)
                    xml.authorname() { cdata(xml, cmsmodule.author) }
                    xml.authoremail() { cdata(xml, cmsmodule.email) }
                    xml.datecreated(now())
                    xml.userinstalled(null)
                    xml.dateinstalled(null)
                    xml.dependencies()
                    xml.exportpoints() {
                        cmsmodule.exportpoints.each {
                            exportpoint(it)
                        }
                    }
                    xml.resources() {
                        cmsmodule.resources.each {
                            resource(it)
                        }
                    }
                    xml.parameters()
                    resourceCount = cmsmodule.cms.adeOffset.toInteger()
                    xml.resourcetypes() {
                        cmsmodule.features.eachWithIndex() { feature, i ->
                            resourceType(xml, feature, i)
                        }
                        cmsmodule.resourceTypes.eachWithIndex() { resource, i ->
                            resourceType(xml, resource, i)
                        }
                    }
                    xml.explorertypes() {
                        cmsmodule.features.eachWithIndex() { feature, i ->
                            explorerType(xml, feature, i);
                        }
                        cmsmodule.resourceTypes.eachWithIndex() { resource, i ->
                            explorerType(xml, resource, i);
                        }
                    }
                }
                files() {
                    xml.mkp.yieldUnescaped "\n<!-- OpenCms VFS files -->"
                    ConfigurableFileTree vfsFiles = project.fileTree(dir: "${dir.absolutePath}/src/vfs",
                            excludes: ["**/*.meta.xml"])
                    vfsFiles.visit { DefaultFileVisitDetails vfsFile ->
                        insertFileBlock(xml, writer, cmsmodule, vfsFile)
                    }

                    ConfigurableFileTree libFiles = project.fileTree(dir: "${dir.absolutePath}/build/" +
                            "opencms-cmsmodule", includes: ["**/system/modules/${cmsmodule.name}/lib/*.jar"])
                    libFiles.visit { DefaultFileVisitDetails vfsFile ->
                        if (!vfsFile.file.isDirectory())
                            insertFileBlock(xml, writer, cmsmodule, vfsFile)
                    }
                }
            }
            manifestFile.parentFile.mkdirs()
            manifestFile.createNewFile()
            manifestFile.text = '<?xml version="1.0" encoding="UTF-8"?>\n' + writer.toString()
        }
    }

    def void insertFileBlock(MarkupBuilder xml, StringWriter writer, OpenCmsModule cmsmodule,
                             DefaultFileVisitDetails vfsFile) {
        String user = cmsmodule.cms.username
        String relativePath = "${vfsFile.path}"
        relativePath = relativePath.endsWith("module.config") ? relativePath.substring(0,
                relativePath.lastIndexOf("module.config")) + ".config" : relativePath
        File meta = project.file(vfsFile.file.absolutePath + ".meta.xml")
        if (meta.exists()) {
            writer.append("\n" + meta.text)
        } else {
            String resourceType = getOpenCmsResourceTypeString(vfsFile.file)
            String structureId = UUID.randomUUID()
            String resourceId = UUID.randomUUID()
            String modified = now()
            fileNode(xml, relativePath, resourceType, structureId, resourceId, modified, user)
        }
    }

    def static void resourceType(MarkupBuilder xml, OpenCmsResourceType resourceType, int i) {
        xml.type(class: "org.opencms.file.types.CmsResourceTypeXmlContent", name: "${resourceType.name}",
                id: "${resourceType.id}") {
            param(name: "schema", "/system/modules/${resourceType.module.name}/schemas/${resourceType.name}.xsd")
        }
    }

    def void explorerType(MarkupBuilder xml, OpenCmsResourceType resourceType, int i) {
        xml.explorertype(name: "${resourceType.name}", key: "fileicon.${resourceType.name}",
                icon: "xmlcontent.gif",
                bigicon: "xmlcontent.gif", reference: "xmlcontent") {
            newresource(page: "structurecontent", uri: "newresource_xmlcontent.jsp?newresourcetype=" +
                    "${resourceType.name}", order: "" + (++resourceCount),
                    autosetnavigation: "false",
                    autosettitle: "false", info: "desc.${resourceType.name}")
            accesscontrol() {
                accessentry(principal: "ROLE.WORKPLACE_USER", permissions: "+r+v+w+c")
            }
        }
    }

    def void fileNode(MarkupBuilder xml,
                      String path,
                      String resourceType,
                      String structureId,
                      String resourceId,
                      String modified,
                      String user) {
        xml.file() {
            if (resourceType != "folder")
                source(path)
            destination(path)
            type(resourceType)
            uuidstructure(structureId)
            if (resourceType != "folder")
                uuidresource(resourceId)
            datelastmodified(modified)
            userlastmodified(user)
            datecreated(now())
            usercreated(user)
            flags('0')
            properties()
            relations()
            accesscontrol()
        }
    }

    def String now() {
        return date.format("EEE, d MMM yyyy HH:mm:ss z")
    }


    def getOpenCmsResourceTypeString(File file) {
        if (file.directory) {
            return "folder"
        } else {
            return getFileTypeFromSuffix(file)
        }
    }

    def String getFileTypeFromSuffix(File file) {
        def suffix = file.name.substring(file.name.lastIndexOf('.') + 1)
        suffixToResourceType.containsKey(suffix) ? suffixToResourceType[suffix] : "binary"
    }

    static def cdata(MarkupBuilder builder, String string) {
        String out = '<![CDATA[' + string + ']]>';
        builder.mkp.yieldUnescaped(out)
    }
}
