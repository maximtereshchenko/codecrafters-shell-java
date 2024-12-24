package io.codecrafters.shell.iterator.token;

import java.nio.file.Path;
import java.util.Optional;

interface State {

    Transition onWhitespace(char whitespace);

    Transition onSingleQuote();

    Transition onDoubleQuote();

    Transition onBackslash();

    Transition onRedirectionOperator();

    Transition onTilda(Path path);

    Transition onCharacter(char character);

    Optional<Token> onEnd();
}
