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
