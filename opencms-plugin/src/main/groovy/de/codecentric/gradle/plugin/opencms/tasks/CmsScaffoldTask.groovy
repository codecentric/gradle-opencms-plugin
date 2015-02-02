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
package de.codecentric.gradle.plugin.opencms.tasks

import de.codecentric.gradle.plugin.opencms.OpenCmsFeature
import de.codecentric.gradle.plugin.opencms.OpenCmsModel
import de.codecentric.gradle.plugin.opencms.OpenCmsModule
import de.codecentric.gradle.plugin.opencms.OpenCmsResourceType
import de.codecentric.gradle.plugin.opencms.files.*
import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CmsScaffoldTask extends DefaultTask {
    File dir
    OpenCmsModel cms


    @TaskAction
    def void scaffold() {
        if (dir != null && dir.directory && dir.exists()) {
            cms.modules.each() { module ->
                createSourceDirectories(module)
                createTestDirectories(module)
                createVfsDirectories(module)
                createConfigFiles(module)
                createFeatureFiles(module)
                createResourceTypeFiles(module)
                createResourceBundle(module)
            }
        }
    }


    def createSourceDirectories(OpenCmsModule module) {
        mkdir("/src")
        mkdir("/src/main")
        mkdir("/src/main/java")
        mkdir("/src/main/resources")
    }

    def createTestDirectories(OpenCmsModule module) {
        mkdir("/src/test");
        mkdir("/src/test/java")
        mkdir("/src/test/resources")
    }

    def createVfsDirectories(OpenCmsModule module) {
        String root = "/src/vfs/system/modules/${module.name}"
        mkdir(root);
        createRootFolderMetaFiles(module);
        mkdir("${root}/elements")
        mkdir("${root}/formatters")
        mkdir("${root}/lib")
        mkdir("${root}/resources")
        mkdir("${root}/schemas")
        mkdir("${root}/system")
        mkdir("${root}/templates")
    }

    def createRootFolderMetaFiles(final OpenCmsModule openCmsModule) {
        createRootFolderMetaFile("system")
        createRootFolderMetaFile("system/modules")
    }

    def void createRootFolderMetaFile(String name) {
        String path = "${dir.absolutePath}/src/vfs/${name}"
        File file = project.file("${path}.meta.xml")
        if (!file.exists()) {
            createFolderMetadata(file, name)
        }
    }

    def void createFolderMetadata(File file, String path) {
        file.createNewFile()
        StringWriter writer = new StringWriter();
        MarkupBuilder builder = new MarkupBuilder(writer);
        builder.file() {
            destination(path)
            type("folder")
            uuidstructure(UUID.randomUUID())
            datelastmodified(now())
            userlastmodified(cms.username)
            datecreated(now())
            usercreated(cms.username)
            flags('0')
            properties()
            relations()
            accesscontrol() {
                if (path.endsWith("system") || path.endsWith("modules")) {
                    accessentry() {
                        uuidprincipal('ROLE.WORKPLACE_USER')
                        flags('514')
                        permissionset() {
                            allowed(path.endsWith("system") ? '1' : '5')
                            denied('0')
                        }
                    }
                }
            }
        }
        file.text = writer.toString()
    }

    def void mkdir(final String path) {
        File newDir = project.file("${dir.absolutePath}${path}");
        if (!newDir.exists())
            newDir.mkdirs()
    }

    def createConfigFiles(OpenCmsModule module) {
        new OpenCmsModuleConfig(module, project, dir)
    }

    def createFeatureFiles(OpenCmsModule module) {
        module.features.each() {
            feature ->
                createFormatterJsp(feature)
                createFormatterConfig(feature)
                createResourceTypeSchema(feature)
        }
    }

    def createFormatterJsp(OpenCmsFeature feature) {
        new OpenCmsFormatterJsp(feature, project, dir)
    }

    def createFormatterConfig(OpenCmsFeature feature) {
        new OpenCmsFormatterConfig(feature, project, dir)
    }

    def createResourceTypeSchema(OpenCmsResourceType resourceType) {
        new OpenCmsResourceTypeSchema(resourceType, project, dir)
    }

    def createResourceTypeFiles(OpenCmsModule module) {
        module.resourceTypes.each() {
            resourceType ->
                createResourceTypeSchema(resourceType)
        }
    }


    def createResourceBundle(OpenCmsModule module) {
        new OpenCmsResourceBundle(module, project, dir)
    }

    static def now() {
        return new Date().format("EEE, d MMM yyyy HH:mm:ss z")
    }
}
