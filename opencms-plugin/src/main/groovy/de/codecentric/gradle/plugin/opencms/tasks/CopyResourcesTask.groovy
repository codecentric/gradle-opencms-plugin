package de.codecentric.gradle.plugin.opencms.tasks
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CopyResourcesTask extends DefaultTask {
    File tmpDir
    File deployDir

    @TaskAction
    def void copy() {
            copyLocalResources()
            copyJar()
    }

    def void copyLocalResources() {
        updateOpenCmsManifest();
        copyTagLibDefinition()
        copyModuleConfig()
        copyAdditionalModuleResourcesDirectories()
    }

    def void updateOpenCmsManifest() {
        def nodesToInsert = ["version": project.version.minus("-SNAPSHOT"),  // OpenCms requires solid version numbers
                "group": project.opencms_module_group,
                "authorname": project.opencms_module_authorname,
                "authoremail": project.opencms_module_authoremail,
                "nicename": project.opencms_module_nicename]

        String text = project.file("dev/xml/opencms/manifest.xml").text
        nodesToInsert.each() { String key, String value ->
            text = insertXmlNodeValue(text, key, value) }
        new File(tmpDir, "manifest.xml").write(text)
    }

    def String insertXmlNodeValue(String text, String key, String value) {
        String tx = text
        tx = tx.replaceAll("<${key}>.*</${key}>", "<${key}>${value}</${key}>")
        tx.replaceAll("<${key}/>", "<${key}>${value}</${key}>")
    }

    def void copyTagLibDefinition() {
        def String tld = "dev/xml/tld"
        copyDir(tld, "${deployDir}/system")
    }

    def void copyModuleConfig() {
        def String module_config = "dev/xml/opencms/module.config"
        copyFile(module_config, "${deployDir}/.config")
    }

    def void copyAdditionalModuleResourcesDirectories() {
        def dirList = project.opencms_module_resources.split(",")
        final CopyResourcesTask task = this
        dirList.each { fromDir ->
            fromDir = fromDir.trim()
            if (!fromDir.empty)
                copyDir(fromDir, (String)"${deployDir}/${fromDir}")
        }
    }

    def void copyFile(final String fromPath, final String toPath) {
        // use ant copy task to preserve the last modified date. this will only sync changed files
        if (project.file(fromPath).exists())
            project.ant.copy(tofile: toPath, preservelastmodified: true) { fileset(file: fromPath) }
    }

    def void copyDir(final String fromDir, final String toDir) {
        // use ant copy task to preserve the last modified date. this will only sync changed files
        if (project.file(fromDir).exists())
            project.ant.copy(todir: toDir, preservelastmodified: true) { fileset(dir: fromDir) }
    }

    def void copyJar() {
        def moduleName = project.opencms_module_name
        def String jarPath = "${tmpDir}/system/modules/${moduleName}/lib/${moduleName}.jar"
        copyFile((String) project.jar.archivePath, jarPath)
    }
}
