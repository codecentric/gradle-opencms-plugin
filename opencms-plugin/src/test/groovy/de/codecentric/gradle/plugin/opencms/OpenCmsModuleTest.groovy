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
        module = new OpenCmsModule(model, project)
    }

    @Test
    public void shouldConfigureFeatureFromClosure() {
        module.feature {
            name = "something"
        }
        assertEquals(1, module.features.size())
        assertEquals("something", module.features.get(0).name)
    }

    @After
    public void tearDown() {
        module = null
    }
} 