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
