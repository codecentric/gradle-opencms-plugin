package de.codecentric.gradle.plugin.opencms.tasks

import de.codecentric.gradle.plugin.opencms.OpenCmsModel
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

public class CmsScaffoldTaskTest {
    Project project

    FileSystemHelper helper;
    File tmpDir


    @Before
    def void setUp() {
        project = ProjectBuilder.builder().build()
        createTempDir()
        def openCms = new OpenCmsModel(project);
        openCms.opencms {
            webInf = "/usr/local/apache-tomcat-7.0.47/webapps/opencms/WEB-INF"
            username = "Admin"
            password = "admin"
            cmsProject = "Offline"
            explorerOffset = 0

            module {
                name = "cms-test"
                group = "codecentric"
                nicename = "OpenCms Testprojekt"
                description = "A project to demonstrate usage of the OpenCms plugin."
                author = "Codecentric AG"
                email = "tobias.goeschel@codecentric.de"

                feature {
                    id = "1"
                    name = "myFeature"
                }
            }
        }
        project.task('cms_scaffold', type: CmsScaffoldTask) {
            dir tmpDir
            cms openCms
        }
    }

    def createTempDir() {
        helper = new FileSystemHelper(project)
        tmpDir = helper.mkdir('tmp')

    }

    @Test
    public void test() {
    }

    @After
    public void tearDown() {
        tmpDir = null;
        helper = null;
        project = null;
    }
} 