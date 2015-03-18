package com.github.eirslett.maven.plugins.frontend.lib;

import java.io.File;
import java.util.Arrays;

public interface FisRunner {
    public void execute(String args) throws TaskRunnerException;
}

final class DefaultFisRunner extends NodeTaskExecutor implements GulpRunner {
    private static final String TASK_NAME = "fis";
    private static final String TASK_LOCATION = "/node_modules/fis/bin/fis";

    DefaultFisRunner(Platform platform, File workingDirectory) {
        super(TASK_NAME, TASK_LOCATION, workingDirectory, platform, Arrays.asList("--no-color"));
    }
}
