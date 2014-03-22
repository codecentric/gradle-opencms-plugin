package de.codecentric.gradle.plugin.opencms.tasks
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CmsModuleTask extends DefaultTask {
    File dir
    String moduleName

    @TaskAction
    def void setUp() {
         if( dir != null && dir.directory && dir.exists() ) {
             createSourceDirectories()
             createTestDirectories()
             createVfsDirectories()
             createConfigFiles()
         }
    }

    void createSourceDirectories() {
        mkdir("/src")
        mkdir("/src/main")
        mkdir("/src/main/java")
        mkdir("/src/main/resources")
    }

    void createTestDirectories() {
        mkdir("/src/test");
        mkdir("/src/test/java")
        mkdir("/src/test/resources")
    }

    void createVfsDirectories() {
        mkdir("/src/vfs")
        mkdir("/src/vfs/elements")
        mkdir("/src/vfs/formatters")
        mkdir("/src/vfs/resources")
        mkdir("/src/vfs/schemas")
        mkdir("/src/vfs/system")
        mkdir("/src/vfs/templates")
    }

    def void mkdir(final String path) {
        File newDir = project.file("${dir.absolutePath}${path}");
        if (!newDir.exists())
            newDir.mkdir()
    }

    void createConfigFiles() {
        touch("/src/vfs/project.meta.json")
        touch("/src/vfs/module.config")
        touch("/src/vfs/system/${moduleName}.tld")
    }

    def touch(final String path) {
        File newDir = project.file("${dir.absolutePath}${path}");
        if (!newDir.exists())
            newDir.createNewFile()
    }
}
