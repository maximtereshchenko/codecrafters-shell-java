package io.codecrafters.shell;

sealed interface Token permits LineBreak, Literal {}
