package io.codecrafters.shell;

sealed interface CommandType permits BuiltIn, External {

    String name();
}
