package com.github.jonasrutishauser.maven.plugin.bpmn;

/*
 * Copyright (C) 2017 Jonas Rutishauser
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.txt>.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

/**
 * Generates a Maven Report which shows the projects BPMN, DMN and CMMN models.
 * 
 * @author jonas
 */
@Mojo(name = "report")
public class ReportMojo extends AbstractMavenReport {

    @Parameter(defaultValue = "${project.basedir}/src", readonly = true)
    private File srcFolder;

    private ReportConfiguration configuration;

    public ReportMojo() {
        this(new File("src"));
    }

    protected ReportMojo(File srcFolder) {
        this.srcFolder = srcFolder;
    }

    protected ReportMojo(ReportConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getName(Locale locale) {
        return "Business Process Models";
    }

    public String getDescription(Locale locale) {
        return "Shows the projects BPMN, DMN and CMMN models.";
    }

    public String getOutputName() {
        return "business-process-models";
    }

    @Override
    public boolean canGenerateReport() {
        try {
            return getConfiguration().canGenerateReport();
        } catch (MavenReportException e) {
            getLog().warn("could not determine if bpmn report should be generated", e);
            return false;
        }
    }

    private ReportConfiguration getConfiguration() throws MavenReportException {
        if (configuration == null) {
            configuration = new ReportConfiguration(getProject().getBasedir(), getModels("bpmn"), getModels("dmn"),
                    getModels("cmmn"));
        }
        return configuration;
    }

    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        final ModelsReportRenderer renderer = createRenderer();
        renderer.render();
        copyModels(renderer);
        copyScripts(renderer);
    }

    protected ModelsReportRenderer createRenderer() throws MavenReportException {
        return new ModelsReportRenderer(getSink(), getConfiguration());
    }

    private void copyScripts(ModelsReportRenderer renderer) throws MavenReportException {
        for (String script : renderer.getScripts()) {
            URL source = getClass().getResource(script.substring(script.lastIndexOf('/') + 1));
            if (source == null) {
                source = getSourceFromDependency(script);
            }
            try (InputStream resource = source.openStream()) {
                Path target = Paths.get(getOutputDirectory(), script);
                Files.createDirectories(target.getParent());
                Files.copy(resource, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new MavenReportException("failed to copy script '" + script + "' to target", e);
            }
        }
    }

    private URL getSourceFromDependency(String script) throws MavenReportException {
        String artifactId = script.substring(0, script.indexOf('/'));
        Properties pomProperties = new Properties();
        try (InputStream properties = getClass()
                .getResourceAsStream("/META-INF/maven/org.webjars.npm/" + artifactId + "/pom.properties")) {
            pomProperties.load(properties);
        } catch (IOException e) {
            throw new MavenReportException("failed to determine version of bower script " + artifactId, e);
        }
        return getClass().getResource("/META-INF/resources/webjars/" + artifactId + "/"
                + pomProperties.getProperty("version") + "/dist/" + script.substring(script.indexOf('/') + 1));
    }

    private void copyModels(final ModelsReportRenderer renderer) throws MavenReportException {
        for (File model : getConfiguration().getModels().toArray(File[]::new)) {
            try {
                Path target = Paths.get(getOutputDirectory(), renderer.getTargetFile(model));
                Files.createDirectories(target.getParent());
                Files.copy(model.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new MavenReportException("failed to copy model '" + model + "' to target", e);
            }
        }
    }

    protected List<File> getModels(String type) throws MavenReportException {
        if (!srcFolder.exists()) {
            return Collections.emptyList();
        }
        try (Stream<Path> stream = Files.find(srcFolder.toPath(), Integer.MAX_VALUE,
                (file, attributes) -> file.toFile().isFile() && file.toString().endsWith("." + type))) {
            return stream.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            throw new MavenReportException("could not find " + type + " files", e);
        }
    }

}
