/*
 * Copyright 2015 codecentric AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
