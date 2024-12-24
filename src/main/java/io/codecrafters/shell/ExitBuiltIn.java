package io.codecrafters.shell;

final class ExitBuiltIn implements ExecutableExpression {

    private final ExecutableExpression downstream;

    ExitBuiltIn(ExecutableExpression downstream) {
        this.downstream = downstream;
    }

    @Override
    public void onNext(String line) {
        //empty
    }

    @Override
    public void onError(String line) {
        downstream.onError(line);
    }

    @Override
    public ExecutionResult onEnd() {
        return new Exit();
    }

    @Override
    public void close() {
        downstream.close();
    }
}
