package de.codecentric.gradle.plugin.opencms.tasks

import de.codecentric.gradle.plugin.opencms.OpenCmsModel
import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.internal.file.DefaultFileVisitDetails
import org.gradle.api.tasks.TaskAction

class CmsManifestTask extends DefaultTask {
    File dir
    OpenCmsModel cms

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
                    xml.resourcetypes() {
                        cmsmodule.features.each() { feature ->
                            type(class: "org.opencms.file.types.CmsResourceTypeXmlContent", name: "${feature.name}",
                                    id: "" + (2000 + cmsmodule.features.indexOf(feature))) {
                                param(name: "schema", "/system/modules/${cmsmodule.name}/schemas/${feature.name}.xsd")
                            }
                        }
                    }
                    xml.explorertypes() {
                        cmsmodule.features.each() { feature ->
                            explorertype(name: "${feature.name}", key: "fileicon.${feature.name}",
                                    icon: "xmlcontent.gif",
                                    bigicon: "xmlcontent.gif", reference: "xmlcontent") {
                                newresource(page: "structurecontent", uri: "newresource_xmlcontent.jsp?newresourcetype=" +
                                        "${feature.name}", order: "" + (50 + cmsmodule.features.indexOf(feature)), autosetnavigation: "false",
                                        autosettitle: "false", info: "desc.${feature.name}")
                                accesscontrol() {
                                    accessentry(principal: "ROLE.WORKPLACE_USER", permissions: "+r+v+w+c")
                                }
                            }
                        }
                    }
                }
                files() {
                    xml.mkp.yieldUnescaped "\n<!-- OpenCms VFS files -->"
                    String user = cmsmodule.cms.username
                    ConfigurableFileTree vfsFiles = project.fileTree(dir: "${dir.absolutePath}/src/vfs",
                            excludes: ["**/*.meta.xml"])
                    vfsFiles.visit { DefaultFileVisitDetails vfsFile ->
                        String relativePath = "${vfsFile.path}"
                        relativePath = relativePath.endsWith("module.config") ? relativePath.substring(0,
                                relativePath.lastIndexOf("module.config")) + ".config" : relativePath
                        File meta = project.file(vfsFile.file.absolutePath + ".meta.xml")
                        if (meta.exists()) {
                            writer.append("\n" + meta.text)
                        } else {
                            String resourceType = getOpenCmsResourceType(vfsFile.file)
                            String structureId = UUID.randomUUID()
                            String resourceId = UUID.randomUUID()
                            String modified = now()
                            fileNode(xml, relativePath, resourceType, structureId, resourceId, modified, user)
                        }
                    }
                }
            }
            manifestFile.parentFile.mkdirs()
            manifestFile.createNewFile()
            manifestFile.text = '<?xml version="1.0" encoding="UTF-8"?>\n' + writer.toString()
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


    def getOpenCmsResourceType(File file) {
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
