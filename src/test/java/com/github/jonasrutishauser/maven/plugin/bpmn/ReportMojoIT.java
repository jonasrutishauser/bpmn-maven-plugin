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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.takari.maven.testing.TestResources;
import io.takari.maven.testing.executor.MavenExecutionResult;
import io.takari.maven.testing.executor.MavenRuntime;
import io.takari.maven.testing.executor.MavenRuntime.MavenRuntimeBuilder;
import io.takari.maven.testing.executor.MavenVersions;
import io.takari.maven.testing.executor.junit.MavenJUnitTestRunner;

@RunWith(MavenJUnitTestRunner.class)
@MavenVersions({"3.3.3", "3.3.9", "3.5.0"})
public class ReportMojoIT {

    @Rule
    public final TestResources resources = new TestResources();

    public final MavenRuntime mavenRuntime;

    public ReportMojoIT(MavenRuntimeBuilder builder) throws Exception {
        this.mavenRuntime = builder.build();
    }

    @Test
    public void buildExample() throws Exception {
        File basedir = resources.getBasedir("example");
        Path bpmnPath = basedir.toPath().resolve("src/main/resources/diagram.bpmn");
        Files.createDirectories(bpmnPath.getParent());
        Path dmnPath = basedir.toPath().resolve("src/models/table.dmn");
        Files.createDirectories(dmnPath.getParent());
        Path cmmnPath = basedir.toPath().resolve("src/diagram.cmmn");
        Files.createDirectories(cmmnPath.getParent());
        Files.copy(Paths.get("src", "test", "models", "diagram.bpmn"), bpmnPath);
        Files.copy(Paths.get("src", "test", "models", "table.dmn"), dmnPath);
        Files.copy(Paths.get("src", "test", "models", "diagram.cmmn"), cmmnPath);

        MavenExecutionResult result = mavenRuntime.forProject(basedir).execute("clean", "site");

        result.assertErrorFreeLog();
        assertTrue(new File(basedir, "target/site/business-process-models.html").exists());
        assertTrue(new File(basedir, "target/site/models/src-main-resources-diagram.bpmn").exists());
        assertTrue(new File(basedir, "target/site/models/src-models-table.dmn").exists());
        assertTrue(new File(basedir, "target/site/models/src-diagram.cmmn").exists());
        assertTrue(new File(basedir, "target/site/bpmn-js").exists());
        assertTrue(new File(basedir, "target/site/dmn-js").exists());
        assertTrue(new File(basedir, "target/site/cmmn-js").exists());
    }

}
