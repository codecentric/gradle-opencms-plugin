package de.codecentric.gradle.plugin.opencms.tasks

import de.codecentric.gradle.plugin.opencms.OpenCmsModel
import org.gradle.api.internal.file.copy.CopyAction
import org.gradle.api.tasks.bundling.Jar


class CmsJarTask extends Jar {
    OpenCmsModel cms

    @Override
    protected CopyAction createCopyAction() {
        archiveName = "${cms.modules[0].name}.jar"
        destinationDir = project.file("${project.projectDir}/build/" +
                "opencms-cmsmodule/system/modules/${cms.modules[0].name}/lib")
        if (!destinationDir.exists())
            destinationDir.mkdirs()
        return super.createCopyAction()
    }
}
