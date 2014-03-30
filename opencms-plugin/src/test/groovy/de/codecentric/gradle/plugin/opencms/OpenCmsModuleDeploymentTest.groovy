package de.codecentric.gradle.plugin.opencms

import de.codecentric.gradle.plugin.opencms.shell.OpenCmsShell
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify

public class OpenCmsModuleDeploymentTest {
    def module

    OpenCmsShell shell

    @Before
	def void setUp() {
        shell = mock( OpenCmsShell)
        module = new OpenCmsModuleDeployment( ".", "user", "pass", shell)
   }

 @Test
	def void whenModuleIsInitialized_shouldLoginToCmsShell() {
        verify( shell ).login( "user", "pass")
    }

    @Test
    def void whenGettingModuleName_shouldRemoveZipExtension() {
        assertEquals( "test", module.getModuleName( "test.zip"))
    }

    @Test
    def void whenGettingModuleName_shouldReturnNameIfWithoutZipExtension() {
        assertEquals( "test", module.getModuleName( "test"))
    }


    @Test
    def void whenClearingCache_shellShouldPurgeJspRepository() {
        module.clearCache()
        verify( shell ).purgeJspRepository()
    }

    @Test
    def void whenImportingModule_shouldReplaceModuleViaShell() {
        module.importModule("test.zip")
        verify(shell).replaceModule("test", "test.zip")
    }

     @Test
    def void whenUpdatingModule_shouldReplaceModuleViaShell() {
         module.updateModule( "test.zip")
         verify(shell).replaceModule("test", "test.zip")
     }

    @Test
    def void whenDeletingModule_shouldDeleteModuleViaShell() {
        module.deleteModule( "test.zip")
        verify( shell ).deleteModule( "test" )
    }

    @Test
    def void whenSwitchingProject_shouldSwitchProjectViaShell() {
        module.switchProject( "test")
        verify( shell ).switchProject( "test" )
    }


    @Test
    def void whenPublishingProject_shouldPublishViaShell() {
        module.publishProjectAndWait()
        verify( shell ).publishProject()
    }

    @Test
    def void whenFinishing_shouldExitShell() {
        module.finish()
        verify( shell ).exit()
    }

	@After
	def void tearDown() {
        module = null
	} 
} 