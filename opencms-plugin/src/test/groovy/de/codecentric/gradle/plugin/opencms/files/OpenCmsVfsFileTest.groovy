/*
 * Copyright 2015 codecentric AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        Locale.setDefault(Locale.ENGLISH)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        file.date = new Date(0)
        assertEquals("Thu, 1 Jan 1970 00:00:00 UTC", file.now())
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
