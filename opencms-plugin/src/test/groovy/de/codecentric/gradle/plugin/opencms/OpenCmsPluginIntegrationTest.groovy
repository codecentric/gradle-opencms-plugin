package de.codecentric.gradle.plugin.opencms

import de.codecentric.gradle.plugin.opencms.OpenCmsPlugin
import de.oev.test.IntegrationTest
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

import static org.junit.Assert.assertNotNull
import static org.mockito.MockitoAnnotations.initMocks

class OpenCmsPluginIntegrationTest implements IntegrationTest {
    OpenCmsPlugin plugin
    @Mock
    ExtensionContainer container

    @Before
    def void setUp() {
        initMocks(this)
        plugin = new OpenCmsPlugin()
    }

    @Test
    def void whenPluginIsAppliedByGradle_shouldInstantiateOpenCmsModule() {
        Project dynProject = ProjectBuilder.builder().build()
        dynProject.opencms_dir = "/usr/local/apache-tomcat-7.0.47/webapps/opencms"
        dynProject.opencms_user = "Admin"
        dynProject.opencms_password = "admin"
        dynProject.opencms_project = "testProject"
        dynProject.opencms_module_name = "testModule"
        dynProject.opencms_module_shortname = "mod"
        dynProject.opencms_module_nicename = "The Test Module"
        dynProject.opencms_module_authorname = "some guy"
        dynProject.opencms_module_authoremail = "someguy@codecentric.de"
        dynProject.opencms_module_group = "de.codecentric"

        plugin.apply(dynProject)
        assertNotNull(plugin.openCmsModule)
    }

    @After
    def void tearDown() {
        plugin = null;
    }
} 