package de.codecentric.gradle.plugin.opencms

import org.gradle.api.Project


class OpenCmsModuleProperty {
    OpenCmsModule module
    Project project
    String key
    String name
    String widget
    String widgetConfig
    String defaultValue

    OpenCmsModuleProperty(final OpenCmsModule module, final Project project) {
        this.module = module
        this.project = project
    }
}
