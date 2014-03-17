package de.codecentric.gradle.plugin.opencms.tasks
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.text.DateFormat
import java.text.SimpleDateFormat

class TouchManifestTask extends DefaultTask {
    def File manifest

    @TaskAction
    void touch() {
        File inputFile = manifest != null? manifest : project.file( "dev/xml/opencms/manifest.xml" )
        String manifestText = updateWithCurrentTimeStamp( inputFile.text )
        inputFile.write( manifestText )
    }

    def String updateWithCurrentTimeStamp(String manifestText) {
        manifestText.replaceAll(
                "<datelastmodified>.*</datelastmodified>",
                 "<datelastmodified>${getCurrentTimeStamp()}</datelastmodified>")
    }

    def String getCurrentTimeStamp() {
        DateFormat tiSt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")
        tiSt.format(new Date())
    }
}
