package de.codecentric.gradle.plugin.opencms.tasks
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertNotNull

public class ExploratoryTest {
	@Test
	public void tasksCanBeAddedToProject() {
        Project project = ProjectBuilder.builder().build()
        project.task('hello')
        assertNotNull(project.hello)
	}
} 