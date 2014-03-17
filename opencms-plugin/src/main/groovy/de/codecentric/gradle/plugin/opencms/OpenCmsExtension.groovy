package de.codecentric.gradle.plugin.opencms

import org.opencms.configuration.CmsConfigurationException
import org.opencms.lock.CmsLockException
import org.opencms.security.CmsRoleViolationException

/**
 * Public method signatures are registered with Gradle
 * to extend the build DSL.
 */
class OpenCmsExtension {
    OpenCmsModule module

    OpenCmsExtension(OpenCmsModule module) {
        this.module = module;
    }

    def String getModuleName(final String fileName) {
        module.getModuleName(fileName)
    }

    def importModule(final String filename) throws Exception {
        module.importModule(filename)
    }

    def updateModule(final String filename) throws Exception {
        module.updateModule(filename)
    }

    def deleteModule(final String filename)
            throws CmsRoleViolationException, CmsLockException, CmsConfigurationException {
        module.deleteModule(filename)
    }

    def runScript( FileInputStream s ) {
        module.runScript( s );
    }

    def void synchronize(final String vfsPath, final String localPath) throws Exception {
        module.synchronize( vfsPath, localPath )
    }

    def publishProjectAndWait() throws Exception {
        module.publishProjectAndWait()
    }

    def clearCache() throws Exception {
        module.clearCache()
    }

    def finish() {
        module.finish()
    }
}