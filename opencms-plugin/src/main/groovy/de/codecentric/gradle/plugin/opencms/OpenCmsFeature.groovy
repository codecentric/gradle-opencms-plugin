package de.codecentric.gradle.plugin.opencms

import org.gradle.api.Project


class OpenCmsFeature extends OpenCmsResourceType {
    String nicename
    String type
    String description
    String listname

    OpenCmsFeature(final OpenCmsModule module, final Project project) {
        super(module, project)
    }
}
