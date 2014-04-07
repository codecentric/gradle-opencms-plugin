package de.codecentric.gradle.plugin.opencms.files

import de.codecentric.gradle.plugin.opencms.OpenCmsFeature
import org.gradle.api.Project

class OpenCmsFormatterSchema extends OpenCmsVfsFile {
    OpenCmsFormatterSchema(OpenCmsFeature feature, Project project, File dir) {
        builder.doubleQuotes = true
        this.project = project
        this.feature = feature
        this.rootPath = "${dir.absolutePath}/src/vfs/system/modules/${feature.module.name}"

        createFile(project, dir, feature)

        meta = project.file("${rootPath}/schemas/${feature.name}.xsd.meta.xml")
        createMetadata("plain", "system/modules/${feature.module.name}/schemas/${feature.name}.xsd")
    }

    def void createFile(Project project, File dir, OpenCmsFeature feature) {
        file = project.file("${rootPath}/schemas/${feature.name}.xsd")
        if (!file.exists()) {
            prepareConfig()
            writeConfig()
            clearStringWriter()
        }
    }

    def prepareConfig() {
        String name = toFirstUpper(feature.name)
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
                    resourcebundle(name: "${feature.module.name}.workplace")
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
