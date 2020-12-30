package io.github.evacchi.javac;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.tools.Diagnostic;
import javax.tools.ToolProvider;

import static java.util.stream.Collectors.joining;

/**
 * A thin wrapper around {@link javax.tools.JavaCompiler}
 */
public class JavaCompiler {

    private static final Logger LOGGER = Logger.getAnonymousLogger();
    private final Collection<Path> javaSourcePaths;
    private final Path sourceDirectory;
    private final Path targetDirectory;
    private final Collection<Diagnostic<?>> diagnostics;
    private final List<String> options;

    public JavaCompiler(Collection<Path> javaSourcePaths, Path sourceDirectory, Path targetDirectory) {
        this.javaSourcePaths = javaSourcePaths.stream().map(sourceDirectory::resolve).collect(Collectors.toList());
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
        this.diagnostics = new ArrayList<>();
        this.options = new ArrayList<>(List.of(
                "-d", targetDirectory.toAbsolutePath().toString(),
                "-cp", String.join(File.pathSeparator,
                                   targetDirectory.toAbsolutePath().toString(),
                                   Path.of(".").toAbsolutePath().resolve("target/classes").toString())
        ));
    }

    public JavaCompiler withPlugin(String plugin) {
        options.add("-Xplugin:" + plugin);
        return this;
    }

    /**
     * @return the mapping between compiled source files and corresponding java files
     * @throws CompilationError thrown when javac returns error
     */
    public FileMapper compile() {
        var javac = ToolProvider.getSystemJavaCompiler();

        LOGGER.info("About to compile the following source files:\n" +
                            javaSourcePaths.stream()
                                    .map(Path::toString).collect(joining("\n")));

        var standardFileManager = javac.getStandardFileManager(null, null, null);
        var recordingJavaFileManager = new RecordingJavaFileManager(standardFileManager, sourceDirectory, targetDirectory);

        var javaFileObjects = standardFileManager.getJavaFileObjects(javaSourcePaths.toArray(new Path[0]));
        var task =
                javac.getTask(
                        null,
                        recordingJavaFileManager,
                        diagnostics::add, // append to list all logs
                        options,
                        null,
                        javaFileObjects);
        var compilationResult = task.call();
        FileMapper processedFiles = recordingJavaFileManager.processedFiles();
        LOGGER.info("The following files were compiled: \n" + processedFiles.toString());

        if (!compilationResult) {
            // embed logs if compilation fails
            throw new CompilationError(this.diagnostics);
        }
        return processedFiles;
    }
}
