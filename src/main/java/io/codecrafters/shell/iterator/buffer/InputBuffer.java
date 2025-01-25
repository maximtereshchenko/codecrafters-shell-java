package io.codecrafters.shell.iterator.buffer;

import io.codecrafters.shell.iterator.token.Token;

import java.util.Iterator;

public interface InputBuffer {

    String autocomplete();

    void offer(char character);

    boolean endsWith(String suffix);

    Iterator<Token> tokens();

    void clear();
}
