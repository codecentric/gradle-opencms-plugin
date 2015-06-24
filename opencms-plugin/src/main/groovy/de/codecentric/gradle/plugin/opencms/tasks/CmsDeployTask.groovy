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
import de.codecentric.gradle.plugin.opencms.OpenCmsModule
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.internal.DocumentationRegistry
import org.gradle.api.internal.file.archive.ZipCopyAction
import org.gradle.api.internal.file.copy.CopyAction
import org.gradle.api.tasks.bundling.Zip


class CmsDeployTask extends Zip {
    OpenCmsModel cms

    @Override
    protected CopyAction createCopyAction() {
        OpenCmsModule module = cms.modules.get(0)
        archiveName = module.archiveName ?: "${module.name}-${module.version}.zip"
        duplicatesStrategy = DuplicatesStrategy.WARN
        includeEmptyDirs = true
        return new ZipCopyAction(getArchivePath(), getCompressor(), new DocumentationRegistry())
    }

}
