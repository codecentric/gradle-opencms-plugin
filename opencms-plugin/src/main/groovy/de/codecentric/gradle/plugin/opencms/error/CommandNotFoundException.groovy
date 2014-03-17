package de.codecentric.gradle.plugin.opencms.error

class CommandNotFoundException extends Exception {
    CommandNotFoundException(String message) {
        super(message)
    }
}
