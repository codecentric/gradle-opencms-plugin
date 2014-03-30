package de.codecentric.gradle.plugin.opencms

import de.codecentric.gradle.plugin.opencms.tasks.CmsModuleTask
import de.codecentric.gradle.plugin.opencms.tasks.CopyResourcesTask
import de.codecentric.gradle.plugin.opencms.tasks.ExtractModuleManifestTask
import de.codecentric.gradle.plugin.opencms.tasks.SynchronizeTask
import de.codecentric.gradle.plugin.opencms.tasks.TouchManifestTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Compression
import org.gradle.api.tasks.bundling.Tar
import org.gradle.api.tasks.bundling.Zip
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat

class OpenCmsPlugin implements Plugin<Project> {
    final Logger log = LoggerFactory.getLogger(OpenCmsPlugin.class)
    Project project
    OpenCmsModuleDeployment module
    OpenCmsShellScript shellRunner

    String openCmsDir
    String user
    String password
    String openCmsProject
    String openCmsModuleName
    String openCmsModuleShortName

    File tmpDir
    File moduleDir
    File distributionDir
    String version

    /**
     * Executes the plugin.  Called via Gradle
     *
     * @param project The project that instantiated the plugin. Will be passed in by Gradle.
     */
    def void apply(Project project) {
        this.project = project
        initializeOpenCmsExtension(project)
        initializeOpenCmsShellRunner()
        tmpDir = new File(project.buildDir, "tmp")
        moduleDir = new File(tmpDir, "system/modules/${openCmsModuleName}")
        distributionDir = new File(project.buildDir, "distributions")

        project.task('BuildDeploymentPackage', type: Tar, dependsOn: ['buildModuleTar']) {
            description = "Build the project"
            include "readme"
            include "**/*.tar"

            compression Compression.GZIP
            baseName getDeploymentPackageGzipName()
            from distributionDir
        }

        project.task('buildModuleZip', type: Zip, dependsOn: 'copyResources') {
            description = "Create a module.zip"
            from tmpDir
            include "system/**/*", "manifest.xml"
            archiveName "${openCmsModuleName}.zip"
        }

         project.task('copyResources', type: CopyResourcesTask, dependsOn: 'jar') << {
            description = "Copy all specified resource directories into the build folder."
            tempDir tmpDir
            deployDir moduleDir
        }

        project.task('reimportModule', dependsOn: ['deleteOpenCmsModule',
                'initialModuleImport']) << {
            description = "Delete and re-import the current module"
        }

        project.task('deleteOpenCmsModule') << {
            log.info "Deleting Module " + openCmsModuleName
            deleteModule(openCmsModuleName)
        }

        project.task('initialModuleImport', dependsOn: 'buildModuleZip') << {
            description = "Import into OpenCms a module  that has never been imported."
            shellRunner.run("importModule")
        }

        project.task('touchManifest', type: TouchManifestTask) << {
            description = 'Updates the timestamp in manifest.xml.'
            manifest project.file("dev/xml/opencms/manifest.xml")
        }

        project.task('synchronizeAndPublish', dependsOn: ['synchronize', 'publish']) << {
            description = "Synchronize and publish OpenCms module"
        }

        project.task('synchronize', type: SynchronizeTask, dependsOn: 'copyResources') << {
            description = "Synchronisiert zwischen OpenCms und dem lokalen Build-Modul."
            tempDir tmpDir
            distributionsDir distributionDir
        }

        project.task('publish') << {
            description = "Publish OpenCms module"
            shellRunner.run("publishProject")
        }

        project.task('extractModuleManifest', type: ExtractModuleManifestTask) << {
            description = "Fetch the current manifest.xml from OpenCms"
            runner shellRunner
            tempDir tmpDir
            cmsRoot openCmsDir
        }

        project.task('buildModuleTar', type: Tar, dependsOn: 'buildModuleZip') {
            description = "Create a module tarball"
            baseName "opencms_modules"
            from project.buildModuleZip.archivePath
        }

        project.task('cmsModule', type:CmsModuleTask) {
            description = "Create the basic folder layout of an OpenCms Module"
            dir project.projectDir
            moduleName project.opencms_module_name
        }
    }

    def initializeOpenCmsExtension(Project project) {
        openCmsDir = project.opencms_dir
        openCmsProject = project.opencms_project
        user = project.opencms_user
        password = project.opencms_password
        openCmsModuleName = project.opencms_module_name
        openCmsModuleShortName = project.opencms_module_shortname
        version = project.version
        project.extensions.create('opencms', OpenCmsExtension, openCmsDir + "/WEB-INF", user, password)
    }

    def initializeOpenCmsShellRunner() {
        shellRunner = new OpenCmsShellScript(project)
    }

    def String getDeploymentPackageGzipName() {
        "deploy-" + new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()) + "-" + project.opencms_module_shortname + "-" + project.version
    }
}

