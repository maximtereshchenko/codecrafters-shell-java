package io.codecrafters.shell;

import java.io.PrintStream;

final class Redirection implements ExecutableExpression {

    private final PrintStream printStream;
    private final ExecutableExpression downstream;

    Redirection(PrintStream printStream, ExecutableExpression downstream) {
        this.printStream = printStream;
        this.downstream = downstream;
    }

    @Override
    public void onNext(String line) {
        printStream.println(line);
    }

    @Override
    public void onError(String line) {
        downstream.onError(line);
    }

    @Override
    public ExecutionResult onEnd() {
        return downstream.onEnd();
    }

    @Override
    public void close() {
        printStream.close();
        downstream.close();
    }
}
