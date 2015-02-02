/*
 * Copyright 2015 codecentric AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.gradle.plugin.opencms.files

import de.codecentric.gradle.plugin.opencms.OpenCmsFeature
import org.gradle.api.Project

class OpenCmsFormatterConfig extends OpenCmsVfsFile {

    OpenCmsFormatterConfig(OpenCmsFeature feature, Project project, File dir) {
        builder.doubleQuotes = true
        this.project = project
        this.resourceType = feature
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
                    NiceName() { cdata(resourceType.nicename) }
                    Type() { cdata(resourceType.type) }
                    Jsp() {
                        link(type: "WEAK") {
                            target() {
                                cdata("/system/modules/${resourceType.module.name}/formatters/${resourceType.name}.jsp")
                            }
                            uuid(UUID.randomUUID())
                        }
                    }
                    Rank() {
                        cdata(String.valueOf(featureIndex() + resourceType.module.cms.explorerOffset.toInteger()))
                    }
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
        resourceType.module.features.indexOf(resourceType)
    }

    def writeConfig() {
        file.parentFile.mkdirs()
        file.createNewFile()
        file.text = '<?xml version="1.0" encoding="utf-8" ?>\n' + stringWriter.toString();
    }

    @Override
    def addProperties() {
        builder.properties() {
            property() {
                name('Title')
                value() { cdata("${resourceType.nicename}") }
            }
        }
    }
}
