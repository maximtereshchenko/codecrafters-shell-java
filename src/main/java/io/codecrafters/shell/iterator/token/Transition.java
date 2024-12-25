package io.codecrafters.shell.iterator.token;

record Transition(State next, Result result) {

    Transition(State next) {
        this(next, new Continue());
    }

    Transition(State next, Token token) {
        this(next, new Found(token));
    }

    Transition withPrecedingToken(Token token) {
        return new Transition(next, new Found(token).combined(result));
    }
}
