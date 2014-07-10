package de.codecentric.gradle.plugin.opencms

import org.gradle.api.Project

import javax.naming.ConfigurationException

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
    String actionClass = ""

    List<OpenCmsFeature> features
    List<OpenCmsResourceType> resourceTypes
    List<OpenCmsModuleProperty> properties

    def resources = []
    def exportpoints = []

    def OpenCmsModule(OpenCmsModel openCmsModel, Project project) {
        this.project = project
        this.cms = openCmsModel
        features = new ArrayList<>()
        resourceTypes = new ArrayList<>();
        properties = new ArrayList<>();
    }

    def feature(Closure closure) {
        OpenCmsFeature feature = new OpenCmsFeature(this, project)
        features.add(feature)
        closure.delegate = feature
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
        verifyResourceId(feature)
    }

    def resourcetype(Closure closure) {
        OpenCmsResourceType resourceType = new OpenCmsResourceType(this, project)
        resourceTypes.add(resourceType)
        closure.delegate = resourceType
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
        verifyResourceId(resourceType)
    }

    def void verifyResourceId(OpenCmsResourceType resourceType) {
        int id = Integer.valueOf(resourceType.id);
        if (id < Integer.valueOf(cms.explorerOffset))
            throw new ConfigurationException("ResourceType '${resourceType.name}' has specified an id below " +
                    "its required explorerOffset.");

        List<OpenCmsModel> opencmslist = new ArrayList<>()
        project.rootProject.subprojects.extensions.flatten().each() {
            OpenCmsModel opencms = (OpenCmsModel) it.findByName('opencms')
            if (opencms != null)
                opencmslist.add(opencms)
        }

        opencmslist.modules.resourceTypes.flatten().each() {
            verifyResourceIdsNotSame(it, resourceType)
        }
        opencmslist.modules.features.flatten().each() {
            verifyResourceIdsNotSame(it, resourceType)
        }
    }

    def static void verifyResourceIdsNotSame(OpenCmsResourceType res, OpenCmsResourceType resourceType) {
        if (res != resourceType && res.id == resourceType.id)
            throw new ConfigurationException("ResourceType '${resourceType.name}' has specified the same id as " +
                    "ResourceType '${res.name}'.");
    }

    def resource(map) {
        resources += [uri: "/system/modules/${name}/${map.uri}"]
    }

    def exportpoint(map) {
        exportpoints += [uri: "/system/modules/${name}/${map.uri}", destination: map.destination]
    }

    def moduleProperty(Closure closure) {
        OpenCmsModuleProperty prop = new OpenCmsModuleProperty(this, project)
        properties.add(prop)
        closure.delegate = prop
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }
}
