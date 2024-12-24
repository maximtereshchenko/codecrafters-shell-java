package io.codecrafters.shell;

import java.io.PrintStream;

final class Sink implements ExecutableExpression {

    private final PrintStream output;
    private final PrintStream error;

    Sink(PrintStream output, PrintStream error) {
        this.output = output;
        this.error = error;
    }

    @Override
    public void onNext(String line) {
        output.println(line);
    }

    @Override
    public void onError(String line) {
        error.println(line);
    }

    @Override
    public ExecutionResult onEnd() {
        return new NoExecutionResult();
    }

    @Override
    public void close() {
        //empty
    }
}
