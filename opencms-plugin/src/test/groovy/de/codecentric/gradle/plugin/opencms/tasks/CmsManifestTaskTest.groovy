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
package de.codecentric.gradle.plugin.opencms.tasks

import de.codecentric.gradle.plugin.opencms.OpenCmsPlugin
import de.codecentric.gradle.plugin.opencms.OpenCmsResourceType
import groovy.xml.MarkupBuilder
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

import static groovy.util.GroovyTestCase.assertEquals

public class CmsManifestTaskTest {
    OpenCmsPlugin plugin
    Project project
    CmsManifestTask task

    final
    static String XML_WITH_DEFAULT_PRINCIPAL = "<explorertype name=\"null\" key=\"fileicon.null\" icon=\"xmlcontent.gif\" bigicon=\"xmlcontent.gif\" reference=\"xmlcontent\">\n" +
            "  <newresource page=\"structurecontent\" uri=\"newresource_xmlcontent.jsp?newresourcetype=null\" order=\"1\" autosetnavigation=\"false\" autosettitle=\"false\" info=\"desc.null\" />\n" +
            "  <accesscontrol>\n" +
            "    <accessentry principal=\"ROLE.WORKPLACE_USER\" permissions=\"+r+v+w+c\" />\n" +
            "    <accessentry principal=\"ROLE.ADMINISTRATOR\" permissions=\"+r+v+w+c\" />\n" +
            "  </accesscontrol>\n" +
            "</explorertype>"

    static
    final String XML_WITH_ONLY_ONE_PRINCIPAL = "<explorertype name=\"null\" key=\"fileicon.null\" icon=\"xmlcontent.gif\" bigicon=\"xmlcontent.gif\" reference=\"xmlcontent\">\n" +
            "  <newresource page=\"structurecontent\" uri=\"newresource_xmlcontent.jsp?newresourcetype=null\" order=\"1\" autosetnavigation=\"false\" autosettitle=\"false\" info=\"desc.null\" />\n" +
            "  <accesscontrol>\n" +
            "    <accessentry principal=\"GROUP.editor1\" permissions=\"+r+v+w+c\" />\n" +
            "    <accessentry principal=\"ROLE.ADMINISTRATOR\" permissions=\"+r+v+w+c\" />\n" +
            "  </accesscontrol>\n" +
            "</explorertype>"

    final
    static String NEW_XML_WITH_MULTIPLE_PRINCIPALS = "<explorertype name=\"null\" key=\"fileicon.null\" icon=\"xmlcontent.gif\" bigicon=\"xmlcontent.gif\" reference=\"xmlcontent\">\n" +
            "  <newresource page=\"structurecontent\" uri=\"newresource_xmlcontent.jsp?newresourcetype=null\" order=\"1\" autosetnavigation=\"false\" autosettitle=\"false\" info=\"desc.null\" />\n" +
            "  <accesscontrol>\n" +
            "    <accessentry principal=\"GROUP.editor2\" permissions=\"+r+v+w+c\" />\n" +
            "    <accessentry principal=\"GROUP.editor3\" permissions=\"+r+v+w+c\" />\n" +
            "    <accessentry principal=\"GROUP.editor4\" permissions=\"+r+v+w+c\" />\n" +
            "    <accessentry principal=\"GROUP.editor5\" permissions=\"+r+v+w+c\" />\n" +
            "    <accessentry principal=\"ROLE.ADMINISTRATOR\" permissions=\"+r+v+w+c\" />\n" +
            "  </accesscontrol>\n" +
            "</explorertype>"


    private static MarkupBuilder getNewXmlWriter(StringWriter writer) {
        def xml = new MarkupBuilder(writer)
        xml.doubleQuotes = true
        return xml
    }

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build()
        plugin = new OpenCmsPlugin()
    }

    @Test
    public void testRegisterPlugin() throws Exception {
        plugin.apply(project)
    }

    @Test
    public void givenResourceTypeWithoutAnyAdditionalPrincipal_shouldReturnXmlAsExpected() throws Exception {
        plugin.apply(project)
        task = project.getTasks().getByName("cms_manifest") as CmsManifestTask

        def writer = new StringWriter()
        def xml = getNewXmlWriter(writer)

        task.explorerType(xml, generateResourceTypeWithPrincipals(null), 0)

        assertEquals(XML_WITH_DEFAULT_PRINCIPAL, writer.toString());
    }

    @Test
    public void givenResourceTypeWithOnePrincipalAsString_shouldReturnXmlAsExpected() throws Exception {
        plugin.apply(project)
        task = project.getTasks().getByName("cms_manifest") as CmsManifestTask

        def writer = new StringWriter()
        def xml = getNewXmlWriter(writer)

        task.explorerType(xml, generateResourceTypeWithPrincipals("GROUP.editor1"), 0)

        assertEquals(XML_WITH_ONLY_ONE_PRINCIPAL, writer.toString());
    }

    @Test
    public void givenResoureTypeWithMultiplePrincipals_shouldReturnXmlAsExpected() throws Exception {
        plugin.apply(project)
        task = project.getTasks().getByName("cms_manifest") as CmsManifestTask

        def writer = new StringWriter()
        def xml = getNewXmlWriter(writer)

        String[] principalsList = ["GROUP.editor2", "GROUP.editor3", "GROUP.editor4", "GROUP.editor5"]
        task.explorerType(xml, generateResourceTypeWithPrincipals(principalsList), 0)

        assertEquals(NEW_XML_WITH_MULTIPLE_PRINCIPALS, writer.toString());
    }

    private OpenCmsResourceType generateResourceTypeWithPrincipals(String[] principals) {
        OpenCmsResourceType resourceType = new OpenCmsResourceType(null, project)
        if (null != principals) {
            resourceType.principal = principals
        }
        return resourceType
    }


    @After
    public void tearDown() {
    }
} 