package de.codecentric.gradle.plugin.opencms.files

import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

public class OpenCmsVfsFileTest {
    OpenCmsVfsFile file

    @Before
    public void setUp() {
        file = new OpenCmsVfsFile();
        file.builder.doubleQuotes = true;
        file.path = "/path"
        file.type = "test"
        file.date = new Date()
    }

    @Test
    public void shouldCreateMarkupFile() {
        file.prepareMetadata("Admin")
        assertEquals("<file>\n" +
                "  <source>/path</source>\n" +
                "  <destination>/path</destination>\n" +
                "  <type>test</type>\n" +
                "  <uuidstructure></uuidstructure>\n" +
                "  <uuidresource></uuidresource>\n" +
                "  <datelastmodified>${file.date.format("EEE, d MMM yyyy HH:mm:ss z")}</datelastmodified>\n" +
                "  <userlastmodified>Admin</userlastmodified>\n" +
                "  <datecreated>${file.date.format("EEE, d MMM yyyy HH:mm:ss z")}</datecreated>\n" +
                "  <usercreated>Admin</usercreated>\n" +
                "  <flags>0</flags>\n" +
                "  <properties />\n" +
                "  <relations />\n" +
                "  <accesscontrol />\n" +
                "</file>", file.stringWriter.toString().replaceAll(/<(uuid\w+)>.+<\/(uuid\w+)>/, "<\$1></\$2>"))
    }

    @Test
    public void shouldReturnFormattedDate() throws Exception {
        file.date = new Date(0)
        assertEquals("Thu, 1 Jan 1970 01:00:00 CET", file.now())
    }

    @Test
    public void shouldYieldUnescapedCdataAsTextNode() throws Exception {
        file.builder.root() { file.cdata("Äöü") }
        assertEquals("<root><![CDATA[Äöü]]></root>", file.stringWriter.toString())
    }

    @Test
    public void shouldReturnStringWithUppercaseFirstLetter() throws Exception {
        assertEquals("Something", file.toFirstUpper("something"))
    }

    @Test
    public void shouldClearTheStringWriterForNewInput() throws Exception {
        file.prepareMetadata("Admin")
        file.clearStringWriter()
        assertEquals("", file.stringWriter.toString())
    }

    @After
    public void tearDown() {
        file = null;
    }
} 