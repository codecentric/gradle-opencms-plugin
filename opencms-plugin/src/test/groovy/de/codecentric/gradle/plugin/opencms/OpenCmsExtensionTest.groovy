package de.codecentric.gradle.plugin.opencms

import de.codecentric.gradle.plugin.opencms.OpenCmsExtension
import de.codecentric.gradle.plugin.opencms.OpenCmsModule
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

import static org.junit.Assert.assertNotNull
import static org.mockito.Mockito.*
import static org.mockito.MockitoAnnotations.initMocks

public class OpenCmsExtensionTest { 
    OpenCmsExtension extension
    @Mock
    OpenCmsModule module
    
	@Before
	public void setUp() { 
        initMocks(this)
        extension = new OpenCmsExtension( module )
	}

    @Test
    def void shouldContainModule () {
        assertNotNull( extension.module )
    }

	@Test
	public void shouldForwardAllApiCallsToCmsModule() {
        extension.clearCache()
        extension.deleteModule("");
        extension.finish()
        extension.getModuleName("");
        extension.importModule("")
        extension.updateModule("")
        extension.publishProjectAndWait()

        verify( module ).clearCache()
        verify( module ).deleteModule("")
        verify( module ).finish()
        verify( module ).getModuleName("");
        verify( module ).importModule("")
        verify( module ).updateModule("")
        verify( module ).publishProjectAndWait()
    }

	@After
	public void tearDown() {
        extension = null
        module = null
	} 
} 