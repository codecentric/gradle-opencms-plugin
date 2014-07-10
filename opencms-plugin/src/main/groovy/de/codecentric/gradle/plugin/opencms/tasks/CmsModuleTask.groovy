package de.codecentric.gradle.plugin.opencms.tasks

import de.codecentric.gradle.plugin.opencms.OpenCmsModel
import org.gradle.api.InvalidUserDataException
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.file.copy.CopyAction
import org.gradle.api.internal.file.copy.FileCopyAction
import org.gradle.api.tasks.Copy

class CmsModuleTask extends Copy {
    OpenCmsModel cms

    @Override
    protected CopyAction createCopyAction() {
        if (cms == null)
            throw new InvalidUserDataException("OpenCms was not configured correctly.");
        File destinationDir = getDestinationDir()
        if (destinationDir == null) {
            throw new InvalidUserDataException("No copy destination directory has been specified, use 'into' to specify a target directory.");
        }
        File moduleBaseDir = initModuleBaseDir(destinationDir)
        return new FileCopyAction(getServices().get(FileResolver.class).withBaseDir(moduleBaseDir))
    }

    File initModuleBaseDir(File destinationDir) {
        File baseDir = project.file(destinationDir.absolutePath)
        if (!baseDir.exists())
            baseDir.mkdirs()
        return baseDir
    }
}
