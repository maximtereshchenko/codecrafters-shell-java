package io.codecrafters.shell.iterator.token;

sealed interface Result permits Continue, Found {

    Result combined(Result result);
}
