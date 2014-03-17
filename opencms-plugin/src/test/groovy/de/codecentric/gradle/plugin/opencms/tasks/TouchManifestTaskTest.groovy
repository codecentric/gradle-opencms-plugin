package de.codecentric.gradle.plugin.opencms.tasks
import de.codecentric.hamcrest.matcher.IsDateLastModified
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertThat

public class TouchManifestTaskTest {
    Project project
    File fakeManifest
    FileSystemHelper helper

    @Before
    public synchronized void setUp() {
        project = ProjectBuilder.builder().build()
        helper = new FileSystemHelper(project)
        fakeManifest = helper.createFile("fakeManifest.xml","<datelastmodified><something /></datelastmodified>")
        configureTask(project)
    }


    def Task configureTask(Project project) {
        project.task('touchManifest', type: TouchManifestTask) {
            manifest = fakeManifest
        }
    }

    @Test
    public void whenExecuted_shouldReplaceTimeStampInManifest() {
        project.touchManifest.execute()
        assertThat(fakeManifest.text, new IsDateLastModified());
    }

    @After
    public void tearDown() {
        if (fakeManifest != null)
            fakeManifest.delete()
        fakeManifest = null
        project = null
    }
} 

