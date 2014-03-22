package de.codecentric.gradle.plugin.opencms.tasks
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
        project.task('cmsModule', type: CmsModuleTask) {
             dir tmpDir
             moduleName "test"
        }
    }

    def createTempDir() {
        helper = new FileSystemHelper( project )
        tmpDir = helper.mkdir( 'tmp' )

    }

    def Project createTestProjectDummy() {
        project = ProjectBuilder.builder().build()
        project.opencms_module_name = "test"
        project.opencms_module_shortname = "tst"
        project.version = "1.0"
        project
    }

    @Test
    def void shouldCreateSourceDirs() {
        project.cmsModule.execute()
        assertFileExists( "/src" )
        assertFileExists( "/src/main" )
        assertFileExists( "/src/main/java" )
        assertFileExists( "/src/main/resources" )
        assertFileExists( "/src/test" )
        assertFileExists( "/src/test/java" )
        assertFileExists( "/src/test/resources" )
        assertFileExists( "/src/vfs" )
        assertFileExists( "/src/vfs/elements" )
        assertFileExists( "/src/vfs/formatters" )
        assertFileExists( "/src/vfs/resources" )
        assertFileExists( "/src/vfs/schemas" )
        assertFileExists( "/src/vfs/system" )
        assertFileExists( "/src/vfs/templates" )

        assertFileExists( "/src/vfs/project.meta.json" )
        assertFileExists( "/src/vfs/module.config" )
        assertFileExists( "/src/vfs/system/test.tld" )
    }

    def void assertFileExists( final String path ) {
        assertTrue( project.file("${tmpDir}${path}").exists() )
    }

    @After
    def void tearDown() {
        if( tmpDir != null)
            tmpDir.delete()
        project = null
    }
} 