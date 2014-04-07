package de.codecentric.gradle.plugin.opencms

import de.codecentric.gradle.plugin.opencms.tasks.CmsDeployTask
import de.codecentric.gradle.plugin.opencms.tasks.CmsManifestTask
import de.codecentric.gradle.plugin.opencms.tasks.CmsModuleTask
import de.codecentric.gradle.plugin.opencms.tasks.CmsScaffoldTask
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
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

        project.task('cms_manifest', type: CmsManifestTask, dependsOn: 'cms_module') {
            description = "Creates the module's manifest.xml configuration file from the contents of the vfs folder."
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

        project.task('cms_deploy', type: CmsDeployTask, dependsOn: 'cms_manifest') {
            from project.file("${project.projectDir}/build/opencms-cmsmodule")
            include "system/**/*", "manifest.xml"
            exclude "**/.DS_Store"
            cms openCms
        }
    }
}

