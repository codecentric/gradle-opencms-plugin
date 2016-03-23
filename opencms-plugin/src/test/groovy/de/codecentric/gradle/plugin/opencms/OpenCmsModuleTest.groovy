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
package de.codecentric.gradle.plugin.opencms

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

public class OpenCmsModuleTest {
    OpenCmsModule module

    @Before
    public void setUp() {
        Project project = ProjectBuilder.builder().build()
        OpenCmsModel model = new OpenCmsModel(project)
        model.explorerOffset = 0
        module = new OpenCmsModule(model, project)
    }

    @Test
    public void shouldConfigureFeatureFromClosure() {

        module.feature {
            id = "1"
            name = "something"
        }
        assertEquals(1, module.features.size())
        assertEquals("1", module.features.get(0).id)
        assertEquals("something", module.features.get(0).name)
    }

    @Test
    public void shouldConfigureResourceTypeFromClosure() {
        module.resourcetype {
            id = "2"
            name = "something"
        }
        assertEquals(1, module.resourceTypes.size())
        assertEquals("2", module.resourceTypes.get(0).id)
        assertEquals("something", module.resourceTypes.get(0).name)
    }
    
    @Test
    public void shouldConfigureSpecialResourceTypeFromClosure() {
        module.specialresourcetype {
            id = "3"
            name = "something"
        }
        assertEquals(1, module.specialResourcetypes.size())
        assertEquals("3", module.specialResourcetypes.get(0).id)
        assertEquals("something", module.specialResourcetypes.get(0).name)
    }

    @After
    public void tearDown() {
        module = null
    }
} 