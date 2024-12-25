package io.codecrafters.shell.iterator.token;

public sealed interface Token permits LineBreak, Literal, Redirection {}
