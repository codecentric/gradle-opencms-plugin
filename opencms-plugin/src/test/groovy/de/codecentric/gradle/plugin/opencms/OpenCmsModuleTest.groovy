package de.codecentric.gradle.plugin.opencms

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

public class OpenCmsModuleTest {
    OpenCmsModule module

    @Before
    public void setUp() {
        Project project = ProjectBuilder.builder().build()
        OpenCmsModel model = new OpenCmsModel(project)
        model.explorerOffset = 0
        module = new OpenCmsModule(model, project)
    }

    @Test
    public void shouldConfigureFeatureFromClosure() {

        module.feature {
            id = "1"
            name = "something"
        }
        assertEquals(1, module.features.size())
        assertEquals("1", module.features.get(0).id)
        assertEquals("something", module.features.get(0).name)
    }

    @Test
    public void shouldConfigureResourceTypeFromClosure() {
        module.resourcetype {
            id = "2"
            name = "something"
        }
        assertEquals(1, module.resourceTypes.size())
        assertEquals("2", module.resourceTypes.get(0).id)
        assertEquals("something", module.resourceTypes.get(0).name)
    }

    @After
    public void tearDown() {
        module = null
    }
} 