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
package de.codecentric.gradle.plugin.opencms

import org.gradle.api.Project


class OpenCmsResourceType {
    String id
    OpenCmsModule module
    Project project
    String name
    String nicename
    String type
    String description
    String listname
    def principal = "ROLE.WORKPLACE_USER"

    OpenCmsResourceType(final OpenCmsModule module, final Project project) {
        this.module = module
        this.project = project
    }

    void principal(String...principals) {
        this.principal = principals
    }
}
