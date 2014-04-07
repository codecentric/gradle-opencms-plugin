package de.codecentric.gradle.plugin.opencms.files

import de.codecentric.gradle.plugin.opencms.OpenCmsModule
import org.gradle.api.Project


class OpenCmsModuleConfig extends OpenCmsVfsFile {

    private OpenCmsModule module
    private File dir

    OpenCmsModuleConfig(OpenCmsModule module, Project project, File dir) {
        builder.doubleQuotes = true
        this.dir = dir
        this.project = project
        this.module = module
        this.rootPath = "${dir.absolutePath}/src/vfs/system/modules/${module.name}"

        createFile()

        meta = project.file("${rootPath}/module.config.meta.xml")
        createMetadata("module_config", "system/modules/${module.name}/.config")
    }

    @Override
    def void createMetadata(String type, String path) {
        this.path = path
        this.type = type
        if (!meta.exists()) {
            prepareMetadata(module.cms.username)
            writeMetadata()
            clearStringWriter()
        }
    }

    def void createFile() {
        file = project.file("${rootPath}/module.config")
        if (!file.exists()) {
            prepareConfig()
            writeConfig()
            clearStringWriter()
        }
    }

    def prepareConfig() {
        builder.ModuleConfigurations('xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
                'xsi:noNamespaceSchemaLocation': "opencms://system/modules/org.opencms.ade" +
                        ".config/schemas/module_config.xsd") {
            ["en", "de"].each() { lang ->
                module.features.each() { feature ->
                    ModuleConfiguration(language: lang) {
                        ResourceType() {
                            TypeName() {
                                cdata("${feature.name}")
                            }
                            Folder() {
                                Name() {
                                    cdata("${feature.name}_elements")
                                }
                            }
                            NamePattern() {
                                cdata("${feature.name}_%(number).html")
                            }
                            Order() {
                                cdata("${80 + module.features.indexOf(feature)}")
                            }
                        }
                    }
                }
            }
        }
    }

    def writeConfig() {
        file.parentFile.mkdirs()
        file.createNewFile()
        file.text = '<?xml version="1.0" encoding="utf-8" ?>\n' + stringWriter.toString();
    }
}
