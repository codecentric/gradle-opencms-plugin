package de.codecentric.gradle.plugin.opencms.files

import de.codecentric.gradle.plugin.opencms.OpenCmsFeature
import groovy.xml.MarkupBuilder
import org.gradle.api.Project


class OpenCmsVfsFile {
    StringWriter stringWriter = new StringWriter()
    MarkupBuilder builder = new MarkupBuilder(stringWriter)

    File file
    File meta
    Project project
    String type
    OpenCmsFeature feature
    Date date = new Date()
    String path
    String rootPath

    def void createMetadata(String type, String path) {
        this.path = path
        this.type = type
        builder.doubleQuotes = true
        if (!meta.exists()) {
            prepareMetadata(feature.module.cms.username)
            writeMetadata()
            clearStringWriter()
        }
    }

    def clearStringWriter() {
        stringWriter.flush()
        stringWriter = new StringWriter()
        builder = new MarkupBuilder(stringWriter)
        builder.doubleQuotes = true
    }

    def writeMetadata() {
        meta.parentFile.mkdirs()
        meta.createNewFile()
        meta.text = stringWriter.toString();
    }

    def prepareMetadata(String user) {
        builder.file() {
            source(path)
            destination(path)
            type(type)
            uuidstructure(UUID.randomUUID())
            uuidresource(UUID.randomUUID())
            datelastmodified(now())
            userlastmodified(user)
            datecreated(now())
            usercreated(user)
            flags('0')
            if (feature != null && type == "formatter_config") {
                properties() {
                    property() {
                        name('Title')
                        value() { cdata("${feature.nicename}") }
                    }
                }
            } else if (feature != null && type == "jsp") {
                properties() {
                    property(type: "shared") {
                        name('export')
                        value() { cdata("false") }
                    }
                }
            } else properties()
            relations()
            accesscontrol()
        }
    }

    def cdata(String string) {
        String out = '<![CDATA[' + string + ']]>';
        builder.mkp.yieldUnescaped(out)
    }

    def now() {
        return date.format("EEE, d MMM yyyy HH:mm:ss z")
    }

    static String toFirstUpper(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
