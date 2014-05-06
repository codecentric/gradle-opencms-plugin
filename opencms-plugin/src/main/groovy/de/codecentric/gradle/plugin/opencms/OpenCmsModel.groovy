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
