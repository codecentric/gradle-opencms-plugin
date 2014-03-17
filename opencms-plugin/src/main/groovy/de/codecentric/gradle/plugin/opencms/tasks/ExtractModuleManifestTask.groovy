package de.codecentric.gradle.plugin.opencms.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.WorkResult

@Slf4j
class ExtractModuleManifestTask extends AbstractShellRunnerTask {
    File tempDir

    @TaskAction
    def void extract() {
        File zipFile = exportModuleZip()
        if (zipFile != null) {
            log.info "Extracting manifest.xml from: " + zipFile
            unzip(zipFile)
            copyManifestXmlIntoDevProject()
            deleteExportDir()
        }
    }

    def boolean deleteExportDir() {
        project.delete("${tempDir}/export")
    }

    def WorkResult copyManifestXmlIntoDevProject() {
        project.copy {
            from new File("${tempDir}/export/manifest.xml")
            into project.file("dev/xml/opencms")
        }
    }

    def WorkResult unzip(File zipFile) {
        project.copy {
            from project.zipTree(zipFile)
            into "${tempDir}/export"
        }
    }

    def File exportModuleZip() {
        deleteArchivedModuleZips()
        runner.run('exportModule')
        def ConfigurableFileTree moduleTree = getModuleArchiveFileTree()
        File zipFile = null
        moduleTree.each { File file -> zipFile = file } // There should be only one file in the dir
        return zipFile
    }

    def void deleteArchivedModuleZips() {
        def ConfigurableFileTree moduleTree = getModuleArchiveFileTree()
        moduleTree.each { File file -> log.info("Delete file: " + file + " : " + project.delete(file)) }
    }

    def ConfigurableFileTree getModuleArchiveFileTree() {
        project.fileTree(dir: "${cmsRoot}/WEB-INF/packages/modules",
                include: "${project.opencms_module_name}*.zip")
    }
}
