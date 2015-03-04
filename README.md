<!---
 Copyright 2015 codecentric AG

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
gradle-opencms-plugin
=====================
[![Build Status](https://travis-ci.org/codecentric/gradle-opencms-plugin.svg)](https://travis-ci.org/codecentric/gradle-opencms-plugin)
[![Coverage Status](https://coveralls.io/repos/codecentric/gradle-opencms-plugin/badge.svg)](https://coveralls.io/r/codecentric/gradle-opencms-plugin)

The gradle-opencms-plugin lets you build [OpenCMS](http://www.opencms.org/) modules using gradle.

Usage
-----

Use the ```cms_scaffold``` task to create required files and meta data files.

Use the ```cms_deploy``` task to create a OpenCMS module, that can than be deployed to an OpenCMS instance.

Minimal configuration example
-----------------------------

```groovy
opencms {
    webInf = "${project.opencms_dir}/WEB-INF"
    username = "Admin"
    password = "admin"
    cmsProject = "Offline"
    cmsVersion = "9.0.1"
    module {
        name = "de.codecentric.cms.test"
        group = "codecentric"
        nicename = "OpenCms Testprojekt"
        description = "A project to demonstrate usage of the OpenCms plugin."
        author = "Codecentric AG"
        email = "test@codecentric.de"
        version = "0.0.1"
        actionClass = ""
        feature {
            type = "myFeature"
            name = "myFeature"
            nicename = "Brilliant Testfeature"
            listname = "Default myFeature Formatter"
            description = "A brilliant test feature!"
        }
        
        // may contain several features
        
        exportpoint(uri: 'lib/', destination: 'WEB-INF/lib/')
        exportpoint(uri: 'classes/', destination: 'WEB-INF/classes/')
        exportpoint(uri: 'system/', destination: 'WEB-INF/')
        resource(uri: '')
    }
}
```

License
-------
Code is under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt).
