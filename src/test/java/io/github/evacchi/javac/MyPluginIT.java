package io.github.evacchi.javac;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class MyPluginIT extends BaseIT {

    public MyPluginIT() {
        super(Path.of("src/test/resources/project1"));
    }

    @Test
    public void mytest() throws IOException {
        var prj = copySourceToTemp("mytest");
        var fsc = new FileScanner(prj.resolve(sourceDirectory), ".java");
        var compiler =
                new JavaCompiler(
                        fsc.scan(),
                        prj.resolve(sourceDirectory),
                        prj.resolve(targetDirectory))
                        .withPlugin("MyPlugin");
        var results = compiler.compile();
    }
}
