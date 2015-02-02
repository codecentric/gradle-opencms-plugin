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


class OpenCmsModel {
    Project project
    List<OpenCmsModule> modules

    String webInf
    String username = "Admin"
    String password = "admin"
    String cmsProject = "Offline"
    String cmsVersion = "9.0.1"
    String explorerOffset = "5000";
    String adeOffset = "100"
    String widgetOffset = "200"

    OpenCmsModel(Project project) {
        this.project = project
        this.modules = new ArrayList<>();
    }

    def opencms(Closure closure) {
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }

    def module(Closure closure) {
        def module = new OpenCmsModule(this, project)
        modules.add(module)
        closure.delegate = module
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }
}
