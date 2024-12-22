package io.codecrafters.shell;

interface BuiltInCommandFactory extends CommandFactory {

    BuiltIn type();
}
