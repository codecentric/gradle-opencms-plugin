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

import de.codecentric.gradle.plugin.opencms.OpenCmsModule
import de.codecentric.gradle.plugin.opencms.OpenCmsModuleProperty
import org.gradle.api.Project

class OpenCmsModuleConfig extends OpenCmsVfsFile {

    private OpenCmsModule module
    private File dir

    OpenCmsModuleConfig(OpenCmsModule module, Project project, File dir) {
        builder.doubleQuotes = true
        this.dir = dir
        this.project = project
        this.module = module
        this.rootPath = "${dir.absolutePath}/src/vfs/system/modules/${module.name}"

        createFile()

        meta = project.file("${rootPath}/module.config.meta.xml")
        createMetadata("module_config", "system/modules/${module.name}/.config")
    }

    @Override
    def void createMetadata(String type, String path) {
        this.path = path
        this.type = type
        if (!meta.exists()) {
            prepareMetadata(module.cms.username)
            writeMetadata()
            clearStringWriter()
        }
    }

    def void createFile() {
        file = project.file("${rootPath}/module.config")
        if (!file.exists()) {
            prepareConfig()
            writeConfig()
            clearStringWriter()
        }
    }

    def prepareConfig() {
        builder.ModuleConfigurations('xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
                'xsi:noNamespaceSchemaLocation': "opencms://system/modules/org.opencms.ade" +
                        ".config/schemas/module_config.xsd") {
            ["en", "de"].each() { lang ->
                int widgetCount = Integer.valueOf(module.cms.widgetOffset);
                ModuleConfiguration(language: lang) {
                    module.features.each() { feature ->
                        ResourceType() {
                            TypeName() {
                                cdata("${feature.name}")
                            }
                            Folder() {
                                Name() {
                                    cdata("${feature.name}_elements")
                                }
                            }
                            NamePattern() {
                                cdata("${feature.name}_%(number).html")
                            }
                            Order() {
                                cdata("${80 + module.features.indexOf(feature)}")
                            }
                        }
                    }
                    module.specialResourcetypes.each() { specialResourcetype ->
                        ResourceType() {
                            TypeName() {
                                cdata("${specialResourcetype.name}")
                            }
                            Folder() {
                                Name() {
                                    cdata("${specialResourcetype.name}")
                                }
                            }
                            NamePattern() {
                                cdata("${specialResourcetype.name}_%(number).html")
                            }
                            Order() {
                                cdata("${200 + module.specialResourcetypes.indexOf(specialResourcetype)}")
                            }
                        }
                    }
                    module.properties.each() { OpenCmsModuleProperty property ->
                        Property() {
                            PropertyName() {
                                cdata("${property.key}")
                            }
                            DisplayName() {
                                cdata("${property.name}")
                            }
                            Widget() {
                                cdata("${property.widget}")
                            }
                            Default() {
                                cdata("${property.defaultValue}")
                            }
                            WidgetConfig() {
                                cdata("${property.widgetConfig}")
                            }
                            Order() {
                                cdata(++widgetCount + '')
                            }
                        }
                    }

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
