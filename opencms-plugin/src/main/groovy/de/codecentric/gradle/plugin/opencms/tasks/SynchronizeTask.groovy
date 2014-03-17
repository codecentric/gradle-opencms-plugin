package de.codecentric.gradle.plugin.opencms.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.TaskAction

class SynchronizeTask extends DefaultTask {
    public static final String DOC_PATH = "dev/package/doc"
    public static final String README_PATH = "readme"
    public static final String VFS_PATH = "/system/modules"

    def File tempDir
    def File distributionsDir

    @TaskAction
    def void sync() {
        updateAndDeployDocumentation(project)
        setPackageNameAndVersion(project)
        synchronizeLocalBuildWithOpenCms()
    }

    private updateAndDeployDocumentation(final Project project) {
        project.copy {
            from README_PATH
            into distributionsDir
        }
        project.copy {
            from DOC_PATH
            include '**/*'
            into distributionsDir
        }
    }

    private setPackageNameAndVersion(final Project project) {
        // TODO: Discuss module name with the ops team
        def String packageName = getModulePackageName(project)

        FileTree tree = project.fileTree(dir: distributionsDir)
        tree.include README_PATH
        tree.each { File file ->
            String text = file.text
            text = text.replaceAll("package.name", packageName)
            text = text.replaceAll("module.version", (String) project.version)
            text = text.replace('$', '');
            file.write(text)
        }
    }

    def String getModulePackageName(Project project) {
        "deploy-" + project.opencms_module_shortname + "-" + project.version
    }

    private synchronizeLocalBuildWithOpenCms() {
        String vfsPath = VFS_PATH +"/${project.opencms_module_name}/"
        String localPath = tempDir.absolutePath
        project.opencms.synchronize(vfsPath, localPath)
    }
}