package de.codecentric.gradle.plugin.opencms

import de.codecentric.gradle.plugin.opencms.tasks.CmsModuleTask
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project

@Slf4j
class OpenCmsPlugin implements Plugin<Project> {
    Project project

    String openCmsDir
    String user
    String password
    String openCmsProject
    String version
    OpenCmsModel openCms

    /**
     * Executes the plugin.  Called via Gradle
     *
     * @param project The project that instantiated the plugin. Will be passed in by Gradle.
     */
    def void apply(Project project) {
        this.project = project
        openCms = project.extensions.create('opencms', OpenCmsModel)

        setupTasks()
    }

    void setupTasks() {
        project.task('cms_module', type: CmsModuleTask) {
            description = "Create the basic folder layout of an OpenCms Module"
            dir project.projectDir
            cms openCms
        }
    }
}

