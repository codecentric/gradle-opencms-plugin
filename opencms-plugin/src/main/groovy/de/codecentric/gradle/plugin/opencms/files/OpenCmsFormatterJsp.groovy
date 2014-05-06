package de.codecentric.gradle.plugin.opencms.files

import de.codecentric.gradle.plugin.opencms.OpenCmsFeature
import org.gradle.api.Project

class OpenCmsFormatterJsp extends OpenCmsVfsFile {

    OpenCmsFormatterJsp(OpenCmsFeature feature, Project project, File dir) {
        this.resourceType = feature
        this.rootPath = "${dir.absolutePath}/src/vfs/system/modules/${feature.module.name}"
        createFile(project, dir, feature)
        meta = project.file("${rootPath}/formatters/${feature.name}.jsp.meta.xml")
        createMetadata("jsp", "system/modules/${feature.module.name}/formatters/${feature.name}.jsp")

    }

    def createFile(Project project, File dir, OpenCmsFeature feature) {
        file = project.file("${rootPath}/formatters/${feature.name}.jsp")
        if (!file.exists()) {
            writeJsp()
        }
    }

    def void writeJsp() {
        file.parentFile.mkdirs()
        file.createNewFile()
        file.text = '<%@ page pageEncoding="UTF-8"%>\n' +
                '<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>\n' +
                '<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>\n' +
                '<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>\n' +
                '\n' +
                '<cms:formatter var="content" val="value" rdfa="rdfa">\n' +
                '\t<div>Empty formatter.</div>\n' +
                '</cms:formatter>\n';
    }

    @Override
    def addProperties() {
        builder.properties() {
            property(type: "shared") {
                name('export')
                value() { cdata("false") }
            }
        }
    }
}
