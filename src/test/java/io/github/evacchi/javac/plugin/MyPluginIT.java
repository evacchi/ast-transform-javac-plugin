package io.github.evacchi.javac.plugin;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyPluginIT extends BaseIT {

    public MyPluginIT() {
        super(Path.of("src/test/resources/project1"));
    }

    @Test
    public void mytest() throws Exception {
        var prj = copySourceToTemp("mytest");
        var fsc = new FileScanner(prj.resolve(sourceDirectory), ".java");
        var compiler =
                new JavaCompiler(
                        fsc.scan(),
                        prj.resolve(sourceDirectory.toAbsolutePath()),
                        prj.resolve(targetDirectory.toAbsolutePath()))
                        .withOption("--module-path=" + targetDirectory.toAbsolutePath())
                        .withOption("-Xplugin:MyPlugin")
                        .withOption("--add-exports=jdk.compiler/com.sun.tools.javac.api=javac.plugin")
                        .withOption("--add-exports=jdk.compiler/com.sun.tools.javac.tree=javac.plugin")
                        .withOption("--add-exports=jdk.compiler/com.sun.tools.javac.util=javac.plugin");
        var compiled = compiler.compile();
        var cl = new DiskClassLoader(prj.resolve(targetDirectory));
        var result = cl.loadAndInvoke("com.example.A", "a");
        assertEquals(List.of("quux"), result);
    }
}
