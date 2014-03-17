package de.codecentric.gradle.plugin.opencms.tasks
import de.codecentric.gradle.plugin.opencms.OpenCmsExtension
import de.codecentric.gradle.plugin.opencms.OpenCmsModule
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import static org.mockito.MockitoAnnotations.initMocks

public class CopyResourcesTaskTest {
    Project project
    @Mock
    OpenCmsModule module

    FileSystemHelper helper
    File tempDir
    File deplDir
    File fakeManifest

    @Before
    public void setUp() {
        initMocks(this)
        project = createTestProjectDummy()
        helper = new FileSystemHelper(project);
        hookUpFakeOpenCmsExtension(project)
        createTempFilesAndDirs()
        configureTask(project)
    }

    def Project createTestProjectDummy() {
        project = ProjectBuilder.builder().build()
        project.opencms_module_name = "test"
        project.opencms_module_shortname = "tst"
        project.opencms_module_group = "group"
        project.opencms_module_authorname = "author"
        project.opencms_module_authoremail = "author@group"
        project.opencms_module_nicename = "nicetest"
        project.opencms_module_resources = "other,another"
        project.version = "1.0"
        project.jar = [archivePath: "jar"]
        project
    }

    def OpenCmsExtension hookUpFakeOpenCmsExtension(Project project) {
        project.extensions.create('opencms', OpenCmsExtension, module)
    }

    def void createTempFilesAndDirs() {
        helper.mkdir("dev")
        helper.mkdir("dev/xml")
        helper.mkdir( "dev/xml/tld")
        helper.createFile( "dev/xml/tld/test.tld", "sometldcontent")
        helper.mkdir("dev/xml/opencms")
        helper.mkdir("other");
        helper.mkdir("another");
        helper.createFile("dev/xml/opencms/module.config", "somecontent")
        helper.createFile("jar", "supposedlyajar")
        fakeManifest = helper.createFile("dev/xml/opencms/manifest.xml",
                node("group", "") +
                        node("nicename", "") +
                        node("authorname", "") +
                        node("authoremail", "") +
                        node("version", ""))
        tempDir = helper.mkdir("tmp")
        deplDir = helper.mkdir("deploy")
    }

    def String node(String name, String value) {
        return "<${name}>${value}</${name}>"
    }

    def void configureTask(final Project project) {
        project.task('copyResources', type: CopyResourcesTask ) {
            tmpDir = tempDir
            deployDir = deplDir
        }
    }

    @Test
    def void whenExecuted_shouldWriteConfigurationValuesIntoNewManifest() {
        project.copyResources.execute()
        File manifest = project.file("tmp/manifest.xml")
        String expected = node("group", project.opencms_module_group) +
                node("nicename",project.opencms_module_nicename) +
                node("authorname", project.opencms_module_authorname) +
                node("authoremail", project.opencms_module_authoremail) +
                node("version", (String)project.version)
        assertEquals(expected, manifest.text );
    }

    @Test
    def void whenExecuted_shouldCopyTagLibDefinitionsToDeployDir() {
        project.copyResources.execute()
        assertTrue( project.file( "deploy/system/test.tld").exists())
    }

    @Test
    def void whenExecuted_shouldCopyModuleConfigToDeployDir() {
        project.copyResources.execute()
        assertTrue( project.file( "deploy/.config").exists())
    }

    @Test
    def void whenExecuted_shouldCopyConfiguredResourceDirectoriesToDeployDir () {
        project.copyResources.execute()
        assertTrue( project.file( "deploy/other").exists())
        assertTrue( project.file( "deploy/another").exists())
    }

    @Test
    def void whenExecuted_shouldCopyThePreviouslyCreatedJarFileToArchive() {
        project.copyResources.execute()
        assertTrue( project.file(  "${tempDir}/system/modules/test/lib/test.jar").exists())
    }

    @After
    public void tearDown() {
        project = null
    }
} 