package ch.pmr.plugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReportException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ch.pmr.plugin.junit.extension.TemporaryFolderExtension;
import ch.pmr.plugin.junit.extension.TemporaryFolderExtension.Root;

@DisplayName("ReportMojo")
@ExtendWith(TemporaryFolderExtension.class)
public class ReportMojoTest {

	@Test
	@DisplayName("getModels() in non existing directory returns nothing")
	void getModels_nonExistingDirectory_nothing(@Root Path tempFolder) throws MavenReportException {
		ReportMojo testee = new ReportMojo(tempFolder.resolve("no-such-dir").toFile());

		List<File> models = testee.getModels("bpmn");

		assertThat(models, is(empty()));
	}

	@Test
	@DisplayName("getModels() in empty directory returns nothing")
	void getModels_emptyDirectory_nothing(@Root Path tempFolder) throws MavenReportException {
		ReportMojo testee = new ReportMojo(tempFolder.toFile());

		List<File> models = testee.getModels("bpmn");

		assertThat(models, is(empty()));
	}

	@Test
	@DisplayName("getModels() in directory with one matching file returns this file")
	void getModels_directoryWithMatchingFile_matchingFile(@Root Path tempFolder)
			throws MavenReportException, IOException {
		ReportMojo testee = new ReportMojo(tempFolder.toFile());
		Path testFile = tempFolder.resolve("test.bpmn");
		Files.createFile(testFile);
		Files.createFile(tempFolder.resolve("foo.bar"));
		Files.createDirectory(tempFolder.resolve("other.bpmn"));

		List<File> models = testee.getModels("bpmn");

		assertThat(models, contains(testFile.toFile()));
	}

	@Test
	@DisplayName("getModels() with one matching file in subdirectory returns this file")
	void getModels_matchingFileInSubdirectory_matchingFile(@Root Path tempFolder)
			throws MavenReportException, IOException {
		ReportMojo testee = new ReportMojo(tempFolder.toFile());
		Path testFile = tempFolder.resolve("foo/bar/other/test.bpmn");
		Files.createDirectories(testFile.getParent());
		Files.createFile(testFile);
		Files.createFile(tempFolder.resolve("foo.bar"));
		Files.createDirectory(tempFolder.resolve("other.bpmn"));

		List<File> models = testee.getModels("bpmn");

		assertThat(models, contains(testFile.toFile()));
	}

	@Test
	@DisplayName("getModels() in directory with multiple matching files returns all files")
	void getModels_directoryWithMultipleMatchingFile_allFiles(@Root Path tempFolder)
			throws MavenReportException, IOException {
		ReportMojo testee = new ReportMojo(tempFolder.toFile());
		Path testFile1 = tempFolder.resolve("test.bpmn");
		Files.createFile(testFile1);
		Path testFile2 = tempFolder.resolve("foo.bpmn");
		Files.createFile(testFile2);
		Files.createDirectory(tempFolder.resolve("other.bpmn"));

		List<File> models = testee.getModels("bpmn");

		assertThat(models, containsInAnyOrder(testFile1.toFile(), testFile2.toFile()));
	}

	@Test
	@DisplayName("canGenerateReport() delegates to configuration")
	void canGenerateReport_delegatesToConfiguration() {
		ReportConfiguration configuration = mock(ReportConfiguration.class);
		ReportMojo testee = new ReportMojo(configuration);

		assertAll(() -> {
			doReturn(Boolean.TRUE).when(configuration).canGenerateReport();
			assertTrue(testee.canGenerateReport());
		}, () -> {
			doReturn(Boolean.FALSE).when(configuration).canGenerateReport();
			assertFalse(testee.canGenerateReport());
		});
	}

	@Test
	@DisplayName("canGenerateReport() returns false on configuration error")
	void canGenerateReport_configurationError_false() {
		ReportMojo testee = new ReportMojo() {
			{
				this.project = mock(MavenProject.class);
			}

			@Override
			protected List<File> getModels(String type) throws MavenReportException {
				throw new MavenReportException("test");
			}
		};

		assertFalse(testee.canGenerateReport());
	}

	@Test
	@DisplayName("executeReport() calls renderer.render()")
	void executeReport_callsRenderer() throws MavenReportException {
		ModelsReportRenderer renderer = mock(ModelsReportRenderer.class);
		ReportMojo testee = new ReportMojo(mock(ReportConfiguration.class)) {
			@Override
			protected ModelsReportRenderer createRenderer() throws MavenReportException {
				return renderer;
			}
		};

		testee.executeReport(Locale.ENGLISH);

		verify(renderer).render();
	}

	@Test
	@DisplayName("executeReport() copies models")
	void executeReport_copiesModels(@Root Path tempFolder) throws MavenReportException, IOException {
		ModelsReportRenderer renderer = mock(ModelsReportRenderer.class);
		ReportConfiguration configuration = mock(ReportConfiguration.class);
		ReportMojo testee = new ReportMojo(configuration) {
			@Override
			protected ModelsReportRenderer createRenderer() throws MavenReportException {
				return renderer;
			}

			@Override
			protected String getOutputDirectory() {
				return tempFolder.toAbsolutePath().toString();
			}
		};
		Path testFile1 = tempFolder.resolve("test.bpmn");
		Files.write(testFile1, Arrays.asList("file1"), StandardCharsets.UTF_8);
		Path testFile2 = tempFolder.resolve("foo.bpmn");
		Files.write(testFile2, Arrays.asList("file2"), StandardCharsets.UTF_8);
		when(configuration.getModels()).thenReturn(Stream.of(testFile1.toFile(), testFile2.toFile()));
		when(renderer.getTargetFile(testFile1.toFile())).thenReturn("target1.bpmn");
		when(renderer.getTargetFile(testFile2.toFile())).thenReturn("target2.bpmn");

		testee.executeReport(Locale.ENGLISH);

		assertAll(() -> {
			assertTrue(Files.isRegularFile(tempFolder.resolve("target1.bpmn")));
		}, () -> {
			assertTrue(Files.isRegularFile(tempFolder.resolve("target2.bpmn")));
		}, () -> {
			assertEquals(Arrays.asList("file1"),
					Files.readAllLines(tempFolder.resolve("target1.bpmn"), StandardCharsets.UTF_8));
		}, () -> {
			assertEquals(Arrays.asList("file2"),
					Files.readAllLines(tempFolder.resolve("target2.bpmn"), StandardCharsets.UTF_8));
		});
	}

	@Test
	@DisplayName("executeReport() copies own script")
	void executeReport_copiesOwnScript(@Root Path tempFolder) throws MavenReportException, IOException {
		ModelsReportRenderer renderer = mock(ModelsReportRenderer.class);
		ReportConfiguration configuration = mock(ReportConfiguration.class);
		ReportMojo testee = new ReportMojo(configuration) {
			@Override
			protected ModelsReportRenderer createRenderer() throws MavenReportException {
				return renderer;
			}

			@Override
			protected String getOutputDirectory() {
				return tempFolder.toAbsolutePath().toString();
			}
		};
		when(renderer.getScripts()).thenReturn(Collections.singleton("test/bpmn.js"));

		testee.executeReport(Locale.ENGLISH);

		assertTrue(Files.isRegularFile(tempFolder.resolve("test/bpmn.js")));
	}

	@Test
	@DisplayName("executeReport() copies script from bpmn-js dependency")
	void executeReport_copiesScriptFromDependency(@Root Path tempFolder) throws MavenReportException, IOException {
		ModelsReportRenderer renderer = mock(ModelsReportRenderer.class);
		ReportConfiguration configuration = mock(ReportConfiguration.class);
		ReportMojo testee = new ReportMojo(configuration) {
			@Override
			protected ModelsReportRenderer createRenderer() throws MavenReportException {
				return renderer;
			}

			@Override
			protected String getOutputDirectory() {
				return tempFolder.toAbsolutePath().toString();
			}
		};
		when(renderer.getScripts()).thenReturn(Collections.singleton("bpmn-js/bpmn-viewer.min.js"));

		testee.executeReport(Locale.ENGLISH);

		assertTrue(Files.isRegularFile(tempFolder.resolve("bpmn-js/bpmn-viewer.min.js")));
	}

}
