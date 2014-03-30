package de.codecentric.gradle.plugin.opencms

import org.opencms.configuration.CmsConfigurationException
import org.opencms.lock.CmsLockException
import org.opencms.security.CmsRoleViolationException
/**
 * Public method signatures are registered with Gradle
 * to extend the build DSL.
 */
class OpenCmsExtension {
    OpenCmsModuleDeployment module
    private String openCmsDir
    private String user
    private String password

    OpenCmsExtension(String openCmsDir, String user, String password) {

        this.password = password
        this.user = user
        this.openCmsDir = openCmsDir
    }

    def String getModuleName(final String fileName) {
        getModule().getModuleName(fileName)
    }

    def importModule(final String filename) throws Exception {
        getModule().importModule(filename)
    }

    def updateModule(final String filename) throws Exception {
        getModule().updateModule(filename)
    }

    def deleteModule(final String filename)
            throws CmsRoleViolationException, CmsLockException, CmsConfigurationException {
        getModule().deleteModule(filename)
    }

    def runScript( FileInputStream s ) {
        getModule().runScript( s );
    }

    def void synchronize(final String vfsPath, final String localPath) throws Exception {
        getModule().synchronize( vfsPath, localPath )
    }

    def publishProjectAndWait() throws Exception {
        getModule().publishProjectAndWait()
    }

    def clearCache() throws Exception {
        getModule().clearCache()
    }

    def finish() {
        getModule().finish()
    }

    def getModule() {
        if ( module == null)
           module = new OpenCmsModuleDeployment( openCmsDir, user, password);
        return module;
    }
}