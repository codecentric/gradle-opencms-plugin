package de.codecentric.gradle.plugin.opencms
import de.codecentric.gradle.plugin.opencms.shell.OpenCmsShell
import org.opencms.configuration.CmsConfigurationException
import org.opencms.lock.CmsLockException
import org.opencms.main.CmsException
import org.opencms.security.CmsRoleViolationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Represents an actual OpenCms Module.
 * Encapsulates an OpenCms shell and acts as a delegate to a running OpenCms instance.
 *
 */
class OpenCmsModuleDeployment {
    private static final String ZIP_EXTENSION = ".zip";

    OpenCmsShell shell
    Logger log = LoggerFactory.getLogger(OpenCmsModuleDeployment.class)
    String username
    String password
    boolean loggedIn

    /** Create a new module handler
     *
     * @param basePath Path where OpenCMS is installed, corresponds to parameter -base of CmsShell
     * @param user Name of user to login with
     * @param password Password of user to login with
     */
    OpenCmsModuleDeployment(final String basePath, final String user, final String password) {
        initialize(basePath, user, password);
    }

    /** Use for tests
     *
     * @param basePath Path where OpenCMS is installed, corresponds to parameter -base of CmsShell
     * @param user Name of user to login with
     * @param password Password of user to login with
     */
    OpenCmsModuleDeployment(final String basePath, final String user, final String password, final OpenCmsShell shell) {
        this.shell = shell
        initialize(basePath, user, password)
    }

    /**
     * @param fileName
     * @return String Module name, i.e. the base of the file name.
     */
    def String getModuleName(final String fileName) {
        final File file = new File(fileName);
        final String moduleName = file.getName();
        return moduleName.indexOf(ZIP_EXTENSION) > -1 ?
                moduleName.substring(0, moduleName.length() - ZIP_EXTENSION.length()) :
                moduleName;
    }

    /**
     * Imports a module.<p>
     *
     * The module may exists in the repository before. This method takes care that this does not result in
     * an error.
     *
     * @param filename the absolute path of the import module file
     *
     * @throws Exception if something goes wrong
     */
    def importModule(final String filename) throws Exception {
        log.info("Importing module: '" + filename + "'");
        login()
        shell.replaceModule(getModuleName(filename), filename);
    }

    /**
     * Updates a module.<p>
     *
     * The module may exists in the repository before.
     *
     * @param filename the absolute path of the import module file
     *
     * @throws Exception if something goes wrong
     */
    def updateModule(final String filename) throws Exception {
        log.info("Updating module: '" + filename + "'");
        login()
        shell.replaceModule(getModuleName(filename), filename);
    }

    /**
     * @param filename the name of the module
     * @throws org.opencms.lock.CmsLockException module is locked and cannot be removed
     * @throws org.opencms.security.CmsRoleViolationException role is not allowed to delete module
     * @throws org.opencms.configuration.CmsConfigurationException
     */
    def deleteModule(final String filename) throws CmsRoleViolationException, CmsLockException,
            CmsConfigurationException {
        log.info("Deleting module: '" + filename + "'");
        login()
        def moduleName = getModuleName(filename)
        shell.deleteModule(moduleName);
    }

    /**
     * Switches the CMS project that is being addressed.
     * For import/export, the default is "Offline".
     *
     * @throws Exception
     * @see de.codecentric.gradle.plugin.opencms.shell.OpenCmsShell#OFFLINE_PROJECT
     */
    def switchProject( final String cmsProjectName ) throws Exception {
        log.info("Switching active project to: '" + cmsProjectName + "'");
        login()
        shell.switchProject( cmsProjectName )
    }

        /**
     * Publishes the current project and waits until it finishes.<p>
     *
     * @throws Exception if something goes wrong
     */
    def publishProjectAndWait() throws Exception {
        login()
        shell.publishProject()
    }

    /**
     * Purges the jsp repository.<p>
     *
     * @throws Exception if something goes wrong
     *
     * @see org.opencms.flex.CmsFlexCache#cmsEvent(org.opencms.main.CmsEvent)
     */
    @SuppressWarnings("unchecked")
    def clearCache() throws Exception {
        login()
        shell.purgeJspRepository()
    }

    /** Initializes the OpenCMS-runtime and login with a specified user
     * @param basePath WEB_INF-path of the opencms-installation
     * @param username the user to be used. If null the guest-user is logged in.
     * @param password password of the user. Is not used if username is null.
     * @throws CmsException
     * @throws IOException
     */
    private void initialize(final String basePath, final String username, final String password)
            throws CmsException, IOException {
        this.password = password
        this.username = username

        if( !shell)
            shell = new OpenCmsShell(basePath);
    }

    /**
     * Runs a custom OpenCms shell script
     * @param inputStream
     */
    def runScript(final FileInputStream inputStream) {
        login()
        shell.start( inputStream );
    }

    /**
     * Synchronize the OpenCms virtual file system path with a local directory.
     * @param vfsPath The directory path in OpenCMS' virtual file system.
     * @param localPath The corresponding path on your local disk.
     * @throws Exception If anything goes wrong.
     */
    def void synchronize(final String vfsPath, final String localPath) throws Exception {
        login()
        shell.synchronize( vfsPath, localPath )
    }
        /** Close connection
     *
     */
    def finish() {
        login();
        shell.exit();
    }

    def login() {
        if( !loggedIn ) {
            shell.login(username, password);
            loggedIn = true;
        }
    }
}
