package com.github.eirslett.maven.plugins.frontend.mojo;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.Scanner;
import org.sonatype.plexus.build.incremental.BuildContext;

import com.github.eirslett.maven.plugins.frontend.lib.FrontendPluginFactory;
import com.github.eirslett.maven.plugins.frontend.lib.TaskRunnerException;

@Mojo(name="fis", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public final class FisMojo extends AbstractMojo {

    /**
     * The base directory for running all Node commands. (Usually the directory that contains package.json)
     */
    @Parameter(defaultValue = "${basedir}", property = "workingDirectory", required = false)
    private File workingDirectory;

    /**
     * Fis arguments. Default is empty (runs just the "fis" command).
     */
    @Parameter(defaultValue ="release", property = "frontend.fis.arguments")
    private String arguments;
    
    /**
     * Files that should be checked for changes, in addition to the srcdir files.
     * Defaults to fis-conf.js in the {@link #workingDirectory}.
     */
    @Parameter(property = "triggerfiles")
    private File[] triggerfiles;
    
    /**
     * The directory containing front end files that will be processed by fis.
     * If this is set then files in the directory will be checked for
     * modifications before running fis.
     */
    @Parameter(property = "srcdir")
    private File srcdir;

    /**
     * The directory where front end files will be output by fis. If this is
     * set then they will be refreshed so they correctly show as modified in
     * Eclipse.
     */
    @Parameter(property = "outputdir")
    private File outputdir;

    /**
     * Skips execution of this mojo.
     */
    @Parameter(property = "skip.fis", defaultValue = "false")
    private Boolean skip;

    @Component
    private BuildContext buildContext;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (shouldExecute()) {
            try {
                MojoUtils.setSLF4jLogger(getLog());
                arguments = (new StringBuilder(String.valueOf(arguments))).append(" --dest ").append(outputdir).toString();
                new FrontendPluginFactory(workingDirectory).getFisRunner().execute(arguments);
            } catch (TaskRunnerException e) {
                throw new MojoFailureException("Failed to run task", e);
            }

            if (outputdir != null) {
                getLog().info("Refreshing files after fis: " + outputdir);
                buildContext.refresh(outputdir);
            }
        } else {
            getLog().info("Skipping fis as no modified files in " + srcdir);
        }
    }
    
    private boolean shouldExecute() {
        if (skip) {
            return false;
        }

        // If there is no buildContext, or this is not an incremental build, always execute.
        if (buildContext == null || !buildContext.isIncremental()) {
            return true;
        }
        
        if (triggerfiles != null) {
            for (int i = 0; i < triggerfiles.length; i++) {
                if (buildContext.hasDelta(triggerfiles[i])) {
                    return true;
                }
            }
        } else {
            // Check for changes in the fis-conf.js
            if (buildContext.hasDelta(new File(workingDirectory, "fis-conf.js"))) {
                return true;
            }
        }

        if (srcdir == null) {
            getLog().info("fis goal doesn't have srcdir set: not checking for modified files");
            return true;
        }

        // Check for changes in the srcdir
        Scanner scanner = buildContext.newScanner(srcdir);
        scanner.scan();
        String[] includedFiles = scanner.getIncludedFiles();
        return (includedFiles != null && includedFiles.length > 0);
    }
    
}