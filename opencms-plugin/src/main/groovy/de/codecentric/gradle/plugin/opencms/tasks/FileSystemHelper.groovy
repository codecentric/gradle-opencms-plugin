package de.codecentric.gradle.plugin.opencms.tasks

import org.gradle.api.Project


class FileSystemHelper {
    final Project project

    FileSystemHelper(final Project project) {
        this.project = project
    }

    def File mkdir(final String name) {
        File dir = project.file(name)
        if (dir.exists())
            dir.delete()
        dir.mkdir()
        dir.deleteOnExit()
        return dir
    }

    def File createFile(String name, String content) {
        File file = project.file(name)
        if (file.exists())
            file.delete();
        file.createNewFile()
        file.deleteOnExit()
        file.write(content);
        return file
    }
}
