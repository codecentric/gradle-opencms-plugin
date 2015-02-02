/*
 * Copyright 2015 codecentric AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.gradle.plugin.opencms.files

import de.codecentric.gradle.plugin.opencms.OpenCmsModel
import de.codecentric.gradle.plugin.opencms.tasks.FileSystemHelper
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

public class OpenCmsFormatterJspTest {
    OpenCmsFormatterJsp jsp

    FileSystemHelper helper;
    File tmpDir
    private Project project

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build()
        createTempDir()
        OpenCmsModel model = new OpenCmsModel(this.project);
        model.explorerOffset = 0
        model.module {
            name = "feature.test"
            feature {
                id = "1"
                name = "myFeature"
                nicename = "A brilliant test feature!"
                type = "myFeature"
            }
        }
        jsp = new OpenCmsFormatterJsp(model.modules.get(0).features.get(0), this.project, tmpDir)
    }

    def createTempDir() {
        helper = new FileSystemHelper(project)
        tmpDir = helper.mkdir('tmp')
    }

    @Test
    public void shouldCreateFeatureJsp() {
        File file = project.file("${tmpDir.absolutePath}/src/vfs/system/modules/feature.test/formatters/myFeature.jsp")
        assertEquals('<%@ page pageEncoding="UTF-8"%>\n' +
                '<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>\n' +
                '<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>\n' +
                '<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>\n' +
                '\n' +
                '<cms:formatter var="content" val="value" rdfa="rdfa">\n' +
                '\t<div>Empty formatter.</div>\n' +
                '</cms:formatter>\n', file.text)
    }

    @Test
    public void shouldCreateMetadata() {
        assertFileExists("${tmpDir.absolutePath}/src/vfs/system/modules/feature.test/formatters/myFeature.jsp.meta.xml")
    }

    def assertFileExists(String path) {
        File file = project.file(path)
        assertTrue(file.exists())
    }

    @After
    public void tearDown() {
        jsp = null;
        helper = null;
        tmpDir = null;

    }
}