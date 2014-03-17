package de.codecentric.gradle.plugin.opencms

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

import static org.junit.Assert.assertEquals
import static org.mockito.MockitoAnnotations.initMocks

class OpenCmsPluginTest {
    public static final String TOMCAT_PATH = "/usr/local/apache-tomcat-7.0.47/webapps/opencms"
    OpenCmsPlugin plugin
    @Mock
    Project project
    @Mock
    ExtensionContainer container
    @Mock
    OpenCmsModule module

    @Before
    def void setUp() {
        initMocks(this)
        plugin = new OpenCmsPlugin()
        plugin.module = module
    }

    def Project createProjectStub() {
        Project project = ProjectBuilder.builder().build()
        project.opencms_dir = TOMCAT_PATH
        project.opencms_user = "Admin"
        project.opencms_password = "admin"
        project.opencms_project = "Online"
        project.opencms_module_name = "testModule"
        project.opencms_module_shortname = "mod"
        project.opencms_module_nicename = "The Test Module"
        project.opencms_module_authorname = "some guy"
        project.opencms_module_authoremail = "someguy@codecentric.de"
        project.opencms_module_group = "de.codecentric"
        return project
    }

    @Test
    def void whenApplied_shouldContainProject() throws Exception {
        def dynProject = createProjectStub()
        plugin.apply(dynProject)
        assertEquals(dynProject, plugin.project)
    }

    @Test
    def void whenApplied_shouldContainVariablesFromConfigurationFile() {
        plugin.apply(createProjectStub())
        assertEquals(TOMCAT_PATH, plugin.openCmsDir)
        assertEquals("Admin", plugin.user)
        assertEquals("admin", plugin.password)
        assertEquals("Online", plugin.openCmsProject)
    }

    @After
    def void tearDown() {
        plugin = null;
    }
} 