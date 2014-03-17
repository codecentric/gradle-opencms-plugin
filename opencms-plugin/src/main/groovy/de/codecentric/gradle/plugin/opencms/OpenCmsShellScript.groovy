package de.codecentric.gradle.plugin.opencms

import de.codecentric.gradle.plugin.opencms.error.CommandNotFoundException
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class OpenCmsShellScript {
    final Project project

    String user
    String password
    String openCmsProject
    String openCmsModuleName

    OpenCmsShellScript( final Project project ) {
        this.project = project
        openCmsProject = project.opencms_project
        user = project.opencms_user
        password = project.opencms_password
        openCmsModuleName = project.opencms_module_name
    }
    final Logger log = LoggerFactory.getLogger(OpenCmsShellScript.class)

    def void run(String commandName) throws IOException {
        log.info "Execute OpenCms shell command: ${commandName}"
        String text = readShellCommandTemplateFile(commandName)
        if (text != null) {
            executeOpenCmsShellCommand(commandName, insertConfigurationVariables(text))
        } else throw new CommandNotFoundException("Shell command: ${commandName} could not be found.")
    }

    private String readShellCommandTemplateFile(final String commandName) {
        InputStream s = getClass().getResourceAsStream("/de/codecentric/opencmsplugin/shellCommands/${commandName}")
        return s != null ? s.getText("UTF-8") : null
    }

    private String insertConfigurationVariables(final String text) {
        String modulePath = normalizePathSeparators((String) project.buildModuleZip.archivePath
                .absolutePath)
        def variablesToInsert = [
                "opencms.user": user,
                "opencms.password": password,
                "opencms.module.pfad": modulePath,
                "opencms.module.name": openCmsModuleName,
                "opencms.project": openCmsProject]
        def String tx = text;
        variablesToInsert.each() { key, value -> tx = insertPlainValue(tx, (String) key, (String) value) }
        return tx
    }

    private String insertPlainValue(final String text, final String key, String value) {
        return text.replaceAll(key, value);
    }

    /**
     * Executes a piece of OpenCMS shell code.
     *
     * Background info:
     * 1. The OpenCms shell expects a FileInputStream, therefore a temp file must be created first.
     * 2. OpenCms always reads files with the OS default encoding
     * .
     * @param commandName The name of the shell command
     * @param sourceCode A complete and runnable piece of OpenCMS shell code
     */
    void executeOpenCmsShellCommand(final String commandName, final String sourceCode) {
        File command
        try {
            command = File.createTempFile("command", ".osh")
            command.deleteOnExit()
            command.write(sourceCode)
            project.runScript(new FileInputStream(command));
        } catch (IOException e) {
            log.error("An Exception occurred while trying to execute shell command: ${commandName}.", e);
        } finally {
            if (command != null)
                command.delete()
        }
    }

    private normalizePathSeparators(final String absPath) {
        absPath.replaceAll("\\\\", "/")
    }

}
