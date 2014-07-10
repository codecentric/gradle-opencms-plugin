package de.codecentric.gradle.plugin.opencms.files

import de.codecentric.gradle.plugin.opencms.OpenCmsResourceType
import org.gradle.api.Project

class OpenCmsResourceTypeSchema extends OpenCmsVfsFile {
    OpenCmsResourceTypeSchema(OpenCmsResourceType resourceType, Project project, File dir) {
        builder.doubleQuotes = true
        this.project = project
        this.resourceType = resourceType
        this.rootPath = "${dir.absolutePath}/src/vfs/system/modules/${resourceType.module.name}"

        createFile(project, dir, resourceType)

        meta = project.file("${rootPath}/schemas/${resourceType.name}.xsd.meta.xml")
        createMetadata("plain", "system/modules/${resourceType.module.name}/schemas/${resourceType.name}.xsd")
    }

    def void createFile(Project project, File dir, OpenCmsResourceType resourceType) {
        file = project.file("${rootPath}/schemas/${resourceType.name}.xsd")
        if (!file.exists()) {
            prepareConfig()
            writeConfig()
            clearStringWriter()
        }
    }

    def prepareConfig() {
        String name = toFirstUpper(resourceType.name)
        builder.'xsd:schema'('xmlns:xsd': 'http://www.w3.org/2001/XMLSchema', 'elementFormDefault': 'qualified') {
            'xsd:include'('schemaLocation': 'opencms://opencms-xmlcontent.xsd')
            'xsd:element'(name: "${name}s", type: "OpenCms${name}s")
            'xsd:complexType'(name: "OpenCms${name}s") {
                'xsd:sequence'() {
                    'xsd:element'(name: "${name}", type: "OpenCms${name}", minOccurs: "0", maxOccurs: "unbounded")
                }
            }
            'xsd:complexType'(name: "OpenCms${name}") {
                'xsd:sequence'() {
                    'xsd:element'(name: "title", type: "OpenCmsString")
                }
                'xsd:attribute'(name: "language", type: "OpenCmsLocale", use: "optional")
            }
            'xsd:annotation'() {
                'xsd:appinfo'() {
                    resourcebundle(name: "${resourceType.module.name}.workplace")
                }
            }
        }
    }

    def writeConfig() {
        file.parentFile.mkdirs()
        file.createNewFile()
        file.text = '<?xml version="1.0" encoding="utf-8" ?>\n' + stringWriter.toString();
    }
}
