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

public class OpenCmsResourceTypeSchemaTest {
    OpenCmsResourceTypeSchema schema

    FileSystemHelper helper;
    File tmpDir
    private Project project

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build()
        createTempDir()
        OpenCmsModel model = new OpenCmsModel(this.project);
        model.module {
            name = "feature.test"
            feature {
                name = "myFeature"
                nicename = "A brilliant test feature!"
                type = "myFeature"
            }
        }
        schema = new OpenCmsResourceTypeSchema(model.modules.get(0).features.get(0), this.project, tmpDir)
    }

    def createTempDir() {
        helper = new FileSystemHelper(project)
        tmpDir = helper.mkdir('tmp')
    }

    @Test
    public void shouldCreateFeatureConfig() {
        File file = project.file("${tmpDir.absolutePath}/src/vfs/system/modules/feature.test/schemas/myFeature.xsd")
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
                "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\">\n" +
                "  <xsd:include schemaLocation=\"opencms://opencms-xmlcontent.xsd\" />\n" +
                "  <xsd:element name=\"MyFeatures\" type=\"OpenCmsMyFeatures\" />\n" +
                "  <xsd:complexType name=\"OpenCmsMyFeatures\">\n" +
                "    <xsd:sequence>\n" +
                "      <xsd:element name=\"MyFeature\" type=\"OpenCmsMyFeature\" minOccurs=\"0\" maxOccurs=\"unbounded\" />\n" +
                "    </xsd:sequence>\n" +
                "  </xsd:complexType>\n" +
                "  <xsd:complexType name=\"OpenCmsMyFeature\">\n" +
                "    <xsd:sequence>\n" +
                "      <xsd:element name=\"title\" type=\"OpenCmsString\" />\n" +
                "    </xsd:sequence>\n" +
                "    <xsd:attribute name=\"language\" type=\"OpenCmsLocale\" use=\"optional\" />\n" +
                "  </xsd:complexType>\n" +
                "  <xsd:annotation>\n" +
                "    <xsd:appinfo>\n" +
                "      <resourcebundle name=\"feature.test.workplace\" />\n" +
                "    </xsd:appinfo>\n" +
                "  </xsd:annotation>\n" +
                "</xsd:schema>", file.text)
    }

    @Test
    public void shouldCreateMetadata() {
        assertFileExists("${tmpDir.absolutePath}/src/vfs/system/modules/feature.test/schemas/myFeature.xsd.meta.xml")
    }

    def assertFileExists(String path) {
        File file = project.file(path)
        assertTrue(file.exists())
    }

    @After
    public void tearDown() {
        schema = null;
        helper = null;
        tmpDir = null;
    }
} 