package io.github.evacchi.javac.plugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyPluginIT extends BaseIT {

    public MyPluginIT() {
        super(Paths.get("src/test/resources/project1"));
    }

    @Test
    public void mytest() throws Exception {
        Path prj = copySourceToTemp("mytest");
        FileScanner fsc = new FileScanner(prj.resolve(sourceDirectory), ".java");
        JavaCompiler compiler =
                new JavaCompiler(
                        fsc.scan(),
                        prj.resolve(sourceDirectory).toAbsolutePath(),
                        prj.resolve(targetDirectory).toAbsolutePath())
                        .withOption("-Xplugin:MyPlugin")
                ;
        FileMapper compiled = compiler.compile();
        DiskClassLoader cl = new DiskClassLoader(prj.resolve(targetDirectory));
        Object result = cl.loadAndInvoke("com.example.A", "a");
        assertEquals(asList("quux"), result);
    }
}
