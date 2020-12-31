package io.github.evacchi.javac.plugin;

import java.util.Collection;

import javax.tools.Diagnostic;

public class CompilationError extends Error {

    private final Collection<Diagnostic<?>> diagnostics;

    CompilationError(Collection<Diagnostic<?>> diagnostics) {
        super(formatDiagnosticsMessage(diagnostics));
        this.diagnostics = diagnostics;
    }

    private static String formatDiagnosticsMessage(Collection<Diagnostic<?>> diagnostics) {
        StringBuilder stringBuilder = new StringBuilder("Compilation failed.\n")
                .append("The following messages were collected while processing:\n");
        for (Diagnostic<?> diagnostic : diagnostics) {
            stringBuilder.append(diagnostic.toString()).append('\n');
        }
        return stringBuilder.toString();
    }

    public Collection<Diagnostic<?>> diagnostics() {
        return diagnostics;
    }
}
