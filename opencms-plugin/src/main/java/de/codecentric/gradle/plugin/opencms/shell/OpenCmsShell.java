package de.codecentric.gradle.plugin.opencms.shell;

import org.opencms.configuration.CmsConfigurationException;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.importexport.CmsImportExportManager;
import org.opencms.importexport.CmsImportParameters;
import org.opencms.lock.CmsLockException;
import org.opencms.main.*;
import org.opencms.module.CmsModuleManager;
import org.opencms.publish.CmsPublishManager;
import org.opencms.report.CmsShellReport;
import org.opencms.security.CmsRoleViolationException;
import org.opencms.synchronize.CmsSynchronize;
import org.opencms.synchronize.CmsSynchronizeSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OpenCmsShell extends CmsShell {

    /**
     * Default name for the "Offline" project used for import/export.
     */
    public static final String OFFLINE_PROJECT = "Offline";

    OpenCmsProxy proxy;

    /**
     * Constructor for use in production
     *
     * @param webInfPath Path to the OpenCms installation's WEB-INF directory
     */
    public OpenCmsShell( final String webInfPath ) {
        super( webInfPath, null, null, null, null );
        proxy = new OpenCmsProxy();
    }

    /**
     * Login with the specified credentials, then update settings accordingly.
     *
     * @param username The user's name.
     * @param password The user's password.
     * @throws org.opencms.main.CmsException If logging in failed.
     */
    public void login( String username, final String password ) throws CmsException {
        username = proxy.getImportExportManager().translateUser( username );
        getCms().loginUser( username, password );
        initSettings(); // This will reset all settings, such as the site root etc.
    }

    /**
     * Imports a module.
     * Note: This code was copied from CmsShellCommand.
     *
     * @param importFile The absolute path of the import module file
     * @throws org.opencms.main.CmsException If something goes wrong, i.e. when a module already exists
     * @see org.opencms.importexport.CmsImportExportManager#importData(org.opencms.file.CmsObject, org.opencms.report.I_CmsReport, org.opencms.importexport.CmsImportParameters)
     */
    public void importModule( final String importFile ) throws CmsException {
        final CmsImportParameters params = new CmsImportParameters( importFile, "/", true );
        proxy.getImportExportManager().importData(
                getCms(),
                new CmsShellReport( getCms().getRequestContext().getLocale() ),
                params );
    }

    /**
     * Updates a module with a new revision.
     * If the module does not exist, a new one is created.
     *
     * @param moduleName Name of the module to update
     * @param filename   The absolute path of the import module file
     * @throws Exception If something goes wrong
     */
    public void replaceModule( final String moduleName, final String filename ) throws Exception {
        deleteModule( moduleName );
        importModule( filename );
    }

    /**
     * Deletes a module. If the module does not exist the method will gracefully return.
     * Note: This code was copied from CmsShellCommand.
     *
     * @param moduleName The name of the module
     * @throws org.opencms.lock.CmsLockException                   If the module is locked and cannot be removed.
     * @throws org.opencms.security.CmsRoleViolationException      If the role is not allowed to delete module.
     * @throws org.opencms.configuration.CmsConfigurationException If the configuration has errors.
     */
    public void deleteModule( final String moduleName ) throws CmsRoleViolationException, CmsLockException,
                                                               CmsConfigurationException {
        if( proxy.getModuleManager().hasModule( moduleName ) ) {
            proxy.getModuleManager().deleteModule(
                    getCms(),
                    moduleName,
                    false,
                    createShellReportForLogMessages() );
        }
    }

    /**
     * Switches the CMS project that is being addressed.
     * For import/export, the default is "Offline".
     *
     * @throws Exception
     * @see de.codecentric.gradle.plugin.opencms.shell.OpenCmsShell#OFFLINE_PROJECT
     */
    public void switchProject( final String cmsProjectName ) throws Exception {
        String projectName = cmsProjectName != null && !cmsProjectName.equals( "" ) ? cmsProjectName : OFFLINE_PROJECT;
        CmsProject project = getCms().readProject( projectName );
        getCms().getRequestContext().setCurrentProject( project );
    }

    /**
     * Publishes the
     *
     * @throws Exception
     */
    public void publishProject() throws Exception {
        CmsPublishManager publishManager = proxy.getPublishManager();
        publishManager.publishProject( getCms() );
        publishManager.waitWhileRunning();
    }

    /**
     * Purges the jsp repository.
     *
     * @see org.opencms.flex.CmsFlexCache#cmsEvent(org.opencms.main.CmsEvent)
     */
    @SuppressWarnings("unchecked")
    public void purgeJspRepository() {
        OpenCms.fireCmsEvent( new CmsEvent( I_CmsEventListener.EVENT_FLEX_PURGE_JSP_REPOSITORY, new HashMap( 0 ) ) );
    }

    /**
     * Synchronize the OpenCms virtual file system path with a local directory.
     * @param vfsPath The directory path in OpenCMS' virtual file system.
     * @param localPath The corresponding path on your local disk.
     * @throws Exception If anything goes wrong.
     */
    public void synchronize(final String vfsPath, final String localPath) throws Exception {
        CmsSynchronizeSettings synchronizeSettings = prepareCmsSyncSettings( vfsPath, localPath );
        new CmsSynchronize(getCms(), synchronizeSettings, createShellReportForLogMessages() );
    }

    public CmsSynchronizeSettings prepareCmsSyncSettings( final String vfsPath, final String localPath ) {
        CmsSynchronizeSettings synchronizeSettings = new CmsSynchronizeSettings();
        synchronizeSettings.setEnabled(true);
        synchronizeSettings.setDestinationPathInRfs(localPath);

        List<String> sourceList = new ArrayList<>();
        sourceList.add( vfsPath );
        synchronizeSettings.setSourceListInVfs( sourceList );
        return synchronizeSettings;
    }

    public CmsShellReport createShellReportForLogMessages() {
        return new CmsShellReport( Locale.GERMANY);
    }

    public void destroy() {
        proxy.destroy();
        proxy = null;
    }

    /**
     * @return CmsObject The currently active OpenCms instance with user context.
     */
    public CmsObject getCms() {
        return m_cms;
    }
}

class OpenCmsProxy {

    private CmsPublishManager      publishManager;
    private CmsImportExportManager importExportManager;
    private CmsModuleManager       moduleManager;

    /**
     * Constructor
     */
    OpenCmsProxy() {
        publishManager = OpenCms.getPublishManager();
        importExportManager = OpenCms.getImportExportManager();
        moduleManager = OpenCms.getModuleManager();

    }

    public void destroy() {
        publishManager = null;
        importExportManager = null;
        moduleManager = null;
    }

    public CmsPublishManager getPublishManager() {
        return publishManager;
    }

    public CmsImportExportManager getImportExportManager() {
        return importExportManager;
    }

    public CmsModuleManager getModuleManager() {
        return moduleManager;
    }
}
