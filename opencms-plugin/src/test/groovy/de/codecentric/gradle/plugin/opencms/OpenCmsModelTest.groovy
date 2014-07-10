package de.codecentric.gradle.plugin.opencms

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

public class OpenCmsModelTest {
    OpenCmsModel model

    @Before
    public void setUp() {
        Project project = ProjectBuilder.builder().build()
        model = new OpenCmsModel(project)
    }

    @Test
    public void whenConfigurationValuesDefinedInClosure_shouldAssumeConfiguredValues() throws Exception {
        def defaultPath = "/usr/local/apache-tomcat-7.0.47/webapps/opencms/WEB-INF"
        model.opencms {
            webInf = defaultPath
            username = "anotherUser"
            password = "anotherPassword"
            cmsProject = "Online"
        }

        assertEquals(defaultPath, model.webInf)
        assertEquals("Online", model.cmsProject)
        assertEquals("anotherUser", model.username)
        assertEquals("anotherPassword", model.password)
    }

    @Test
    public void whenConfigurationValuesAreNotDefinedInClosure_shouldAssumeDefaultValues() throws
            Exception {
        model.opencms {}
        assertNull(model.webInf)
        assertEquals("Offline", model.cmsProject)
        assertEquals("Admin", model.username)
        assertEquals("admin", model.password)
    }

    @Test
    public void whenModuleDefinedInClosure_shouldInstantiateWithConfiguredValues() {
        model.module {
            name = "cms-test"
            group = "codecentric"
            nicename = "OpenCms Test Project"
            description = "A project to demonstrate usage of the OpenCms plugin."
            author = "Codecentric AG"
            email = "opencms@codecentric.de"
            version = "0.0.1"
            resource(uri: "/")
            exportpoint(uri: "lib/", destination: "WEB-INF/lib")
        }

        assertEquals(1, model.modules.size())

        def module = model.modules.get(0)
        assertEquals("cms-test", module.name)
        assertEquals("codecentric", module.group)
        assertEquals("OpenCms Test Project", module.nicename)
        assertEquals("A project to demonstrate usage of the OpenCms plugin.", module.description)
        assertEquals("Codecentric AG", module.author)
        assertEquals("opencms@codecentric.de", module.email)
        assertEquals("0.0.1", module.version)
        assertEquals(1, module.exportpoints.size())
        assertEquals(1, module.resources.size())
    }

    @After
    public void tearDown() {
        model = null
    }
} 