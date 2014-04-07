package de.codecentric.gradle.plugin.opencms.tasks

import de.codecentric.gradle.plugin.opencms.OpenCmsModel
import de.codecentric.gradle.plugin.opencms.OpenCmsModule
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.internal.file.archive.ZipCopyAction
import org.gradle.api.internal.file.copy.CopyAction
import org.gradle.api.tasks.bundling.Zip


class CmsDeployTask extends Zip {
    OpenCmsModel cms

    @Override
    protected CopyAction createCopyAction() {
        OpenCmsModule module = cms.modules.get(0)
        archiveName = module.name + "-" + module.version + ".zip"
        duplicatesStrategy = DuplicatesStrategy.WARN
        includeEmptyDirs = true
        return new ZipCopyAction(getArchivePath(), getCompressor())
    }

}
