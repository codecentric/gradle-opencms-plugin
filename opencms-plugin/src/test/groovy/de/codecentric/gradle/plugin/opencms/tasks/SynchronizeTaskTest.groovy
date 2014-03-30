package de.codecentric.gradle.plugin.opencms.tasks
import de.codecentric.gradle.plugin.opencms.OpenCmsExtension
import de.codecentric.gradle.plugin.opencms.OpenCmsModuleDeployment
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.verify
import static org.mockito.MockitoAnnotations.initMocks

public class SynchronizeTaskTest {
    Project project
    @Mock
    OpenCmsModuleDeployment module

    FileSystemHelper helper;
    File tmpDir
    File distDir
    File docFile
    File readMe

    @Before
    public synchronized void setUp() {
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
        project.version = "1.0"
        project
    }

    def OpenCmsExtension hookUpFakeOpenCmsExtension(Project project) {
        project.extensions.create('opencms', OpenCmsExtension, module)
    }

    def void createTempFilesAndDirs() {
        tmpDir = helper.mkdir("tmp")
        distDir = helper.mkdir("dist")
        helper.mkdir("dev")
        helper.mkdir("dev/package")
        helper.mkdir("dev/package/doc")
        readMe = helper.createFile(SynchronizeTask.README_PATH, '$package.name $module.version')
        docFile = helper.createFile(SynchronizeTask.DOC_PATH + "/test", "test")
    }

    

    def Task configureTask(Project project) {
        project.task('synchronize', type: SynchronizeTask) {
            tempDir = tmpDir
            distributionsDir = distDir
        }
    }

    @Test
    def void whenExecuted_shouldCopyReadmeFileIntoDistDir() {
        project.synchronize.execute()
        assertEquals(1, distDir.list(new FilenameFilter() {
            @Override
            boolean accept(final File dir, final String name) {
                return dir == distDir && name == SynchronizeTask.README_PATH
            }
        }).size())
    }

    @Test
    def void whenExecuted_shouldCopyFilesFromDocDirIntoDistDir() {
        project.synchronize.execute()
        assertEquals(1, distDir.list(new FilenameFilter() {
            @Override
            boolean accept(final File dir, final String name) {
                return dir == distDir && name == docFile.name
            }
        }).size())
    }

    @Test
    def void whenExecuted_shouldUpdatePackageNameAndVersionInReadme() {
        project.synchronize.execute()
        File newReadme = project.file( "dist/"+SynchronizeTask.README_PATH)
        String expected = "deploy-${project.opencms_module_shortname}-${project.version} ${project.version}"
        assertEquals( expected,
                newReadme.text);
    }

    @Test
    def void whenExecuted_shouldSynchronizeWithOpenCms() {
        project.synchronize.execute()
        String expectedVfsPath = "${SynchronizeTask.VFS_PATH}/${project.opencms_module_name}/"
        verify( module ).synchronize(expectedVfsPath,
                tmpDir.absolutePath )
    }

        @After
    public void tearDown() {
        project = null
        tmpDir = null
        distDir = null
        docFile = null
        readMe = null
    }
} 