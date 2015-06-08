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
package de.codecentric.gradle.plugin.opencms

import de.codecentric.gradle.plugin.opencms.tasks.*
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.Project

@Slf4j
class OpenCmsPlugin implements Plugin<Project> {
    Project project
    OpenCmsModel openCms

    /**
     * Executes the plugin.  Called via Gradle
     *
     * @param project The project that instantiated the plugin. Will be passed in by Gradle.
     */
    def void apply(Project project) {
        this.project = project
        openCms = project.extensions.create('opencms', OpenCmsModel, project)

        setupTasks()
    }

    void setupTasks() {
        project.task('cms_scaffold', type: CmsScaffoldTask) {
            description = "Creates the basic structure of OpenCms modules and features, where it does not exist."
            dir project.projectDir
            cms openCms
        }

        project.task('cms_module', type: CmsModuleTask) {
            description = "Creates the deployment module directory from the contents of the vfs folder."
            from "${project.projectDir}/src/vfs"
            into "${project.projectDir}/build/opencms-cmsmodule/"
            cms openCms
            exclude "**/*.meta.xml"
            rename { String fileName ->
                fileName.replace('module.config', '.config')
            }
        }

        project.plugins.withType(JavaPlugin) {
            project.task('cms_jar', type: CmsJarTask, dependsOn: 'cms_module') {
                from project.sourceSets.main.output
                cms openCms
            }
        }

        project.task('cms_manifest', type: CmsManifestTask, dependsOn: 'cms_module') {
            project.plugins.withType(JavaPlugin) {
                dependsOn 'cms_jar'
            }

            description = "Creates the module's manifest.xml configuration file from the contents of the vfs folder."
            dir project.projectDir
            cms openCms
        }

        project.task('cms_deploy', type: CmsDeployTask, dependsOn: 'cms_manifest') {
            from("${project.projectDir}/build/opencms-cmsmodule") {
                exclude "**/.DS_Store"
            }
            cms openCms
        }
    }
}

