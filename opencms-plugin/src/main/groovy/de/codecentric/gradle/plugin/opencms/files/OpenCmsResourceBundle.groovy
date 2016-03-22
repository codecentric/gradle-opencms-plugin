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
import org.gradle.api.Project

class OpenCmsResourceBundle extends OpenCmsVfsFile {

    private OpenCmsModule module
    private File dir

    OpenCmsResourceBundle(OpenCmsModule module, Project project, File dir) {
        builder.doubleQuotes = true
        this.dir = dir
        this.project = project
        this.module = module
        this.rootPath = "${dir.absolutePath}/src/vfs/system/modules/${module.name}"

        createFile()

        meta = project.file("${rootPath}/i18n/${module.name}.workplace.meta.xml")
        createMetadata("xmlvfsbundle", "system/modules/${module.name}/i18n/${module.name}.workplace")
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
        file = project.file("${rootPath}/i18n/${module.name}.workplace")
        if (!file.exists()) {
            prepareBundle()
            writeBundle()
            clearStringWriter()
        }
    }

    def prepareBundle() {
        builder.XmlVfsBundles('xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
                'xsi:noNamespaceSchemaLocation': "opencms://system/modules/org.opencms.ade.config/schemas/xmlvfsbundle" +
                        ".xsd") {
            ["en", "de"].each() { lang ->
                Bundle(language: lang) {
                    module.features.each() { feature ->
                        Message() {
                            Key() { cdata("fileicon.${feature.name}") }
                            Value() { cdata("${feature.listname}") }
                        }
                        Message() {
                            Key() { cdata("title.${feature.name}") }
                            Value() { cdata("${feature.nicename}") }
                        }
                        Message() {
                            Key() { cdata("desc.${feature.name}") }
                            Value() { cdata("${feature.description}") }
                        }
                    }
                    module.specialResourcetypes.each() { specialResourcetype ->
                        Message() {
                            Key() { cdata("fileicon.${specialResourcetype.name}") }
                            Value() { cdata("${specialResourcetype.listname}") }
                        }
                        Message() {
                            Key() { cdata("title.${specialResourcetype.name}") }
                            Value() { cdata("${specialResourcetype.nicename}") }
                        }
                        Message() {
                            Key() { cdata("desc.${specialResourcetype.name}") }
                            Value() { cdata("${specialResourcetype.description}") }
                        }
                    }
                    module.resourceTypes.each() { resourceType ->
                        Message() {
                            Key() { cdata("fileicon.${resourceType.name}") }
                            Value() { cdata("${resourceType.listname}") }
                        }
                        Message() {
                            Key() { cdata("title.${resourceType.name}") }
                            Value() { cdata("${resourceType.nicename}") }
                        }
                        Message() {
                            Key() { cdata("desc.${resourceType.name}") }
                            Value() { cdata("${resourceType.description}") }
                        }
                    }
                }
            }
        }
    }

    def writeBundle() {
        file.parentFile.mkdirs()
        file.createNewFile()
        file.text = '<?xml version="1.0" encoding="utf-8" ?>\n' + stringWriter.toString();
    }
}
