package de.codecentric.gradle.plugin.opencms.files

import de.codecentric.gradle.plugin.opencms.OpenCmsFeature
import org.gradle.api.Project

class OpenCmsFormatterConfig extends OpenCmsVfsFile {

    OpenCmsFormatterConfig(OpenCmsFeature feature, Project project, File dir) {
        builder.doubleQuotes = true
        this.project = project
        this.feature = feature
        this.rootPath = "${dir.absolutePath}/src/vfs/system/modules/${feature.module.name}"

        createFile(project, dir, feature)

        meta = project.file("${rootPath}/formatters/${feature.name}.xml.meta.xml")
        createMetadata("formatter_config", "system/modules/${feature.module.name}/formatters/${feature.name}.xml")
    }

    def void createFile(Project project, File dir, OpenCmsFeature feature) {
        file = project.file("${rootPath}/formatters/${feature.name}.xml")
        if (!file.exists()) {
            prepareConfig()
            writeConfig()
            clearStringWriter()
        }
    }

    def prepareConfig() {
        builder.NewFormatters('xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance',
                'xsi:noNamespaceSchemaLocation': 'opencms://system/modules/org.opencms.ade' +
                        '.config/schemas/formatters/new_formatter.xsd') {
            ['en', 'de'].each() { lang ->
                NewFormatter(language: lang) {
                    NiceName() { cdata(feature.nicename) }
                    Type() { cdata(feature.type) }
                    Jsp() {
                        link(type: "WEAK") {
                            target() { cdata("/system/modules/${feature.module.name}/formatters/${feature.name}.jsp") }
                            uuid(UUID.randomUUID())
                        }
                    }
                    Rank() { cdata(String.valueOf(featureIndex() + 1000)) }
                    Match() {
                        Types() {
                            ContainerType() { cdata('content') }
                        }
                    }
                    Preview('true')
                    SearchContent('true')
                    AutoEnabled('true')
                    Detail('true')
                }
            }
        }
    }

    def int featureIndex() {
        feature.module.features.indexOf(feature)
    }

    def writeConfig() {
        file.parentFile.mkdirs()
        file.createNewFile()
        file.text = '<?xml version="1.0" encoding="utf-8" ?>\n' + stringWriter.toString();
    }
}
