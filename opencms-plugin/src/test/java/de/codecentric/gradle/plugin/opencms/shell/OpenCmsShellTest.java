package de.codecentric.gradle.plugin.opencms.shell;

import de.oev.test.IntegrationTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNull;

/**
 * OpenCmsShell has been derived from codecentric OpenCms DeployIt-Plugin
 * https://github.com/codecentric/oev-hosting-deployit
 *
 * This should be used only to test if the connection to a local OpenCMS works.
 */
public class OpenCmsShellTest implements IntegrationTest {
    private static OpenCmsShell shell;

    @BeforeClass
    public static void setUp() throws Exception {
       shell = new OpenCmsShell( "/usr/local/apache-tomcat-7.0.47/webapps/opencms/WEB-INF" );
    }

    @Test
    public void executeIntegrationTestsInFixedOrder() throws Exception {
        shouldLoginSuccessfully();
        whenShellIsDestroyed_openCmsProxyShouldBeDestroyed();
    }

    private void shouldLoginSuccessfully() throws Exception {
        shell.login( "Admin", "admin");
    }

    private void whenShellIsDestroyed_openCmsProxyShouldBeDestroyed() {
        shell.destroy();
        assertNull( shell.proxy );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        shell = null;
    }
} 