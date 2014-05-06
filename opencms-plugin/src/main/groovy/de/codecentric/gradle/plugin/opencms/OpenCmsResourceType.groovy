package de.codecentric.gradle.plugin.opencms

import org.gradle.api.Project


class OpenCmsResourceType {
    String id
    OpenCmsModule module
    Project project
    String name

    OpenCmsResourceType(final OpenCmsModule module, final Project project) {
        this.module = module
        this.project = project
    }
}
