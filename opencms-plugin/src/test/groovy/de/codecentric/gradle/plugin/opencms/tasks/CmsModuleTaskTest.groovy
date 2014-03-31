package de.codecentric.gradle.plugin.opencms.tasks

import de.codecentric.gradle.plugin.opencms.OpenCmsModel
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertTrue

public class CmsModuleTaskTest {
    Project project

    FileSystemHelper helper;
    File tmpDir


    @Before
    def void setUp() {
        project = createTestProjectDummy()
        createTempDir()
        def openCms = new OpenCmsModel(project);
        openCms.module {
            name = "test"
        }
        project.task('cms_module', type: CmsModuleTask) {
            dir tmpDir
            cms openCms
        }
    }

    def createTempDir() {
        helper = new FileSystemHelper(project)
        tmpDir = helper.mkdir('tmp')

    }

    def Project createTestProjectDummy() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    def void shouldCreateSourceDirs() {
        project.cms_module.execute()
        assertFileExists("/src")
        assertFileExists("/src/main")
        assertFileExists("/src/main/java")
        assertFileExists("/src/main/resources")
        assertFileExists("/src/test")
        assertFileExists("/src/test/java")
        assertFileExists("/src/test/resources")
        assertFileExists("/src/vfs")
        assertFileExists("/src/vfs/elements")
        assertFileExists("/src/vfs/formatters")
        assertFileExists("/src/vfs/resources")
        assertFileExists("/src/vfs/schemas")
        assertFileExists("/src/vfs/system")
        assertFileExists("/src/vfs/templates")

        assertFileExists("/src/vfs/module.config")
        assertFileExists("/src/vfs/system/test.tld")
    }

    def void assertFileExists(final String path) {
        assertTrue(project.file("${tmpDir}${path}").exists())
    }

    @After
    def void tearDown() {
        if (tmpDir != null)
            tmpDir.delete()
        project = null
    }
} 