package de.codecentric.gradle.plugin.opencms.files

import de.codecentric.gradle.plugin.opencms.OpenCmsModel
import de.codecentric.gradle.plugin.opencms.tasks.FileSystemHelper
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

public class OpenCmsFormatterConfigTest {
    OpenCmsFormatterConfig config

    FileSystemHelper helper;
    File tmpDir
    private Project project

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build()
        createTempDir()
        OpenCmsModel model = new OpenCmsModel(this.project);
        model.explorerOffset = 5000
        model.module {
            name = "feature.test"
            feature {
                id = "5001"
                name = "myFeature"
                nicename = "A brilliant test feature!"
                type = "myFeature"
            }
        }
        config = new OpenCmsFormatterConfig(model.modules.get(0).features.get(0), this.project, tmpDir)
    }

    def createTempDir() {
        helper = new FileSystemHelper(project)
        tmpDir = helper.mkdir('tmp')
    }

    @Test
    public void shouldCreateFeatureConfig() {
        File file = project.file("${tmpDir.absolutePath}/src/vfs/system/modules/feature.test/formatters/myFeature.xml")
        println file.text
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
                "<NewFormatters xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"opencms://system/modules/org.opencms.ade.config/schemas/formatters/new_formatter.xsd\">\n" +
                "  <NewFormatter language=\"en\">\n" +
                "    <NiceName><![CDATA[A brilliant test feature!]]></NiceName>\n" +
                "    <Type><![CDATA[myFeature]]></Type>\n" +
                "    <Jsp>\n" +
                "      <link type=\"WEAK\">\n" +
                "        <target><![CDATA[/system/modules/feature.test/formatters/myFeature.jsp]]></target>\n" +
                "        <uuid></uuid>\n" +
                "      </link>\n" +
                "    </Jsp>\n" +
                "    <Rank><![CDATA[5000]]></Rank>\n" +
                "    <Match>\n" +
                "      <Types>\n" +
                "        <ContainerType><![CDATA[content]]></ContainerType>\n" +
                "      </Types>\n" +
                "    </Match>\n" +
                "    <Preview>true</Preview>\n" +
                "    <SearchContent>true</SearchContent>\n" +
                "    <AutoEnabled>true</AutoEnabled>\n" +
                "    <Detail>true</Detail>\n" +
                "  </NewFormatter>\n" +
                "  <NewFormatter language=\"de\">\n" +
                "    <NiceName><![CDATA[A brilliant test feature!]]></NiceName>\n" +
                "    <Type><![CDATA[myFeature]]></Type>\n" +
                "    <Jsp>\n" +
                "      <link type=\"WEAK\">\n" +
                "        <target><![CDATA[/system/modules/feature.test/formatters/myFeature.jsp]]></target>\n" +
                "        <uuid></uuid>\n" +
                "      </link>\n" +
                "    </Jsp>\n" +
                "    <Rank><![CDATA[5000]]></Rank>\n" +
                "    <Match>\n" +
                "      <Types>\n" +
                "        <ContainerType><![CDATA[content]]></ContainerType>\n" +
                "      </Types>\n" +
                "    </Match>\n" +
                "    <Preview>true</Preview>\n" +
                "    <SearchContent>true</SearchContent>\n" +
                "    <AutoEnabled>true</AutoEnabled>\n" +
                "    <Detail>true</Detail>\n" +
                "  </NewFormatter>\n" +
                "</NewFormatters>", file.text.replaceAll(/<uuid>.+<\/uuid>/, "<uuid></uuid>"))
    }

    @Test
    public void shouldCreateMetadata() {
        assertFileExists("${tmpDir.absolutePath}/src/vfs/system/modules/feature.test/formatters/myFeature.xml.meta" +
                ".xml")
    }

    def assertFileExists(String path) {
        File file = project.file(path)
        assertTrue(file.exists())
    }

    @After
    public void tearDown() {
        config = null;
        helper = null;
        tmpDir = null;
    }
} 