package io.codecrafters.shell.iterator.token;

import java.util.Optional;

interface State {

    Transition onWhitespace(char whitespace);

    Transition onSingleQuote();

    Transition onDoubleQuote();

    Transition onBackslash();

    Transition onRedirectionOperator();

    Transition onCharacter(char character);

    Optional<Token> onEnd();
}
