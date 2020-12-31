package io.github.evacchi.javac.plugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import static java.util.Arrays.asList;
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
        this.options = new ArrayList<>(asList(
                "-d", targetDirectory.toAbsolutePath().toString(),
                "-cp", String.join(File.pathSeparator,
                                   targetDirectory.toAbsolutePath().toString(),
                                   Paths.get(".").toAbsolutePath().resolve("target/classes").toString())
        ));
    }

    public JavaCompiler withOption(String option) {
        options.add(option);
        return this;
    }

    /**
     * @return the mapping between compiled source files and corresponding java files
     * @throws CompilationError thrown when javac returns error
     */
    public FileMapper compile() {
        javax.tools.JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

        LOGGER.info("About to compile the following source files:\n" +
                            javaSourcePaths.stream()
                                    .map(Path::toString).collect(joining("\n")));

        StandardJavaFileManager standardFileManager = javac.getStandardFileManager(null, null, null);
        RecordingJavaFileManager recordingJavaFileManager = new RecordingJavaFileManager(standardFileManager, sourceDirectory, targetDirectory);

        List<File> files = javaSourcePaths.stream().map(Path::toFile).collect(Collectors.toList());
        Iterable<? extends JavaFileObject> javaFileObjects = standardFileManager.getJavaFileObjectsFromFiles(files);
        javax.tools.JavaCompiler.CompilationTask task =
                javac.getTask(
                        null,
                        recordingJavaFileManager,
                        diagnostics::add, // append to list all logs
                        options,
                        null,
                        javaFileObjects);
        Boolean compilationResult = task.call();
        FileMapper processedFiles = recordingJavaFileManager.processedFiles();
        LOGGER.info("The following files were compiled: \n" + processedFiles.toString());

        if (!compilationResult) {
            // embed logs if compilation fails
            throw new CompilationError(this.diagnostics);
        }
        return processedFiles;
    }
}
