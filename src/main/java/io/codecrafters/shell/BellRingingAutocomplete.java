package io.codecrafters.shell;

import java.io.PrintStream;

final class BellRingingAutocomplete implements Autocomplete {

    private final Autocomplete autocomplete;
    private final PrintStream output;

    BellRingingAutocomplete(Autocomplete autocomplete, PrintStream output) {
        this.autocomplete = autocomplete;
        this.output = output;
    }

    @Override
    public String complete(String input) {
        var completed = autocomplete.complete(input);
        if (completed.isEmpty()) {
            output.print('\u0007');
        }
        return completed;
    }
}
