package de.codecentric.gradle.plugin.opencms

import org.gradle.api.Project


class OpenCmsModule {
    OpenCmsModel cms
    Project project

    String name = ""
    String nicename = ""
    String group = ""
    String description = ""
    String author = ""
    String email = ""
    String version = ""

    List<OpenCmsFeature> features
    def resources = []
    def exportpoints = []

    def OpenCmsModule(OpenCmsModel openCmsModel, Project project) {
        this.project = project
        this.cms = openCmsModel
        features = new ArrayList<>()
    }

    def feature(Closure closure) {
        OpenCmsFeature feature = new OpenCmsFeature(this, project)
        features.add(feature)
        closure.delegate = feature
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }

    def resource(map) {
        resources += [uri: "/system/modules/${name}/${map.uri}"]
    }

    def exportpoint(map) {
        exportpoints += [uri: "/system/modules/${name}/${map.uri}", destination: map.destination]
    }
}
