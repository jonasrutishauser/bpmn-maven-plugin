package com.github.jonasrutishauser.plugin.bpmn;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.doxia.siterenderer.RenderingContext;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.maven.shared.utils.io.IOUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.jonasrutishauser.plugin.bpmn.ModelsReportRenderer;
import com.github.jonasrutishauser.plugin.bpmn.ReportConfiguration;

@DisplayName("ModelsReportRenderer")
public class ModelsReportRendererTest {

	private static final File MODELS_DIRECTORY = new File("src/test/models");
	private static final File BASEDIR = new File("target/test");

	@Test
	@DisplayName("render() of a bpmn creates correct html")
	void render_aBpmn() throws IOException {
		SiteRendererSink sink = new SiteRendererSink(new RenderingContext(BASEDIR, "test.html"));
		sink.setInsertNewline(false);
		ModelsReportRenderer testee = new ModelsReportRenderer(sink,
				new ReportConfiguration(new File(""),
						Collections.singletonList(new File(MODELS_DIRECTORY, "diagram.bpmn")), Collections.emptyList(),
						Collections.emptyList()));

		testee.render();

		assertEquals(getContent("bpmn.html"), sink.getBody());
	}

	@Test
	@DisplayName("render() of a dmn creates correct html")
	void render_aDmn() throws IOException {
		SiteRendererSink sink = new SiteRendererSink(new RenderingContext(BASEDIR, "test.html"));
		sink.setInsertNewline(false);
		ModelsReportRenderer testee = new ModelsReportRenderer(sink,
				new ReportConfiguration(new File(""), Collections.emptyList(),
						Collections.singletonList(new File(MODELS_DIRECTORY, "table.dmn")), Collections.emptyList()));

		testee.render();

		String content = "<html><head>" + sink.getHead() + "</head><body>" + sink.getBody() + "</body></html>";
		assertEquals(getContent("dmn.html"), content);
	}

	@Test
	@DisplayName("render() of a cmmn creates correct html")
	void render_aCmmn() throws IOException {
		SiteRendererSink sink = new SiteRendererSink(new RenderingContext(BASEDIR, "test.html"));
		sink.setInsertNewline(false);
		ModelsReportRenderer testee = new ModelsReportRenderer(sink,
				new ReportConfiguration(new File(""), Collections.emptyList(), Collections.emptyList(),
						Collections.singletonList(new File(MODELS_DIRECTORY, "diagram.cmmn"))));

		testee.render();

		assertEquals(getContent("cmmn.html"), sink.getBody());
	}

	@Test
	@DisplayName("getProcessName() of a bpmn returns the first process name")
	void getProcessName_aBpmn_processName() throws IOException {
		ModelsReportRenderer testee = new ModelsReportRenderer(null, null);

		String name = testee.getProcessName(new File(MODELS_DIRECTORY, "diagram.bpmn"));

		assertEquals("Test Model", name);
	}

	@Test
	@DisplayName("getProcessName() of a dmn returns null")
	void getProcessName_aDmn_null() throws IOException {
		ModelsReportRenderer testee = new ModelsReportRenderer(null, null);

		String name = testee.getProcessName(new File(MODELS_DIRECTORY, "table.dmn"));

		assertNull(name);
	}

	@Test
	@DisplayName("getProcessName() of an invalid file returns null")
	void getProcessName_invalid_null() throws IOException {
		ModelsReportRenderer testee = new ModelsReportRenderer(null, null);

		String name = testee.getProcessName(new File(MODELS_DIRECTORY, "invalid.bpmn"));

		assertNull(name);
	}

	@Test
	@DisplayName("getDecisionName() of a dmn returns the first decision name")
	void getDecisionName_aDmn_firstDecisionName() throws IOException {
		ModelsReportRenderer testee = new ModelsReportRenderer(null, null);

		String name = testee.getDecisionName(new File(MODELS_DIRECTORY, "table.dmn"));

		assertEquals("Test dmn Model", name);
	}

	@Test
	@DisplayName("getDecisionName() of a bpmn returns null")
	void getDecisionName_aBpmn_null() throws IOException {
		ModelsReportRenderer testee = new ModelsReportRenderer(null, null);

		String name = testee.getDecisionName(new File(MODELS_DIRECTORY, "model.bpmn"));

		assertNull(name);
	}

	@Test
	@DisplayName("getDecisionName() of an invalid file returns null")
	void getDecisionName_invalid_null() throws IOException {
		ModelsReportRenderer testee = new ModelsReportRenderer(null, null);

		String name = testee.getDecisionName(new File(MODELS_DIRECTORY, "invalid.dmn"));

		assertNull(name);
	}

	@Test
	@DisplayName("getCaseName() of a bpmn returns the first case name")
	void getCaseName_aCmmn_processName() throws IOException {
		ModelsReportRenderer testee = new ModelsReportRenderer(null, null);

		String name = testee.getCaseName(new File(MODELS_DIRECTORY, "diagram.cmmn"));

		assertEquals("Test Case", name);
	}

	@Test
	@DisplayName("getCaseName() of a dmn returns null")
	void getCaseNameName_aDmn_null() throws IOException {
		ModelsReportRenderer testee = new ModelsReportRenderer(null, null);

		String name = testee.getCaseName(new File(MODELS_DIRECTORY, "table.dmn"));

		assertNull(name);
	}

	@Test
	@DisplayName("getCaseName() of an invalid file returns null")
	void getCaseName_invalid_null() throws IOException {
		ModelsReportRenderer testee = new ModelsReportRenderer(null, null);

		String name = testee.getCaseName(new File(MODELS_DIRECTORY, "invalid.cmmn"));

		assertNull(name);
	}

	@Test
	@DisplayName("getScripts() after render() of a bpmn returns correct scripts")
	void getScripts_renderABpmn_bpmnScripts() throws IOException {
		ModelsReportRenderer testee = new ModelsReportRenderer(
				new SiteRendererSink(new RenderingContext(BASEDIR, "test.html")),
				new ReportConfiguration(new File(""),
						Collections.singletonList(new File(MODELS_DIRECTORY, "diagram.bpmn")), Collections.emptyList(),
						Collections.emptyList()));
		testee.render();

		Set<String> scripts = testee.getScripts();

		assertEquals(new HashSet<>(Arrays.asList("bpmn-js/bpmn.js", "bpmn-js/bpmn-viewer.min.js")), scripts);
	}

	@Test
	@DisplayName("getScripts() after render() of a dmn returns correct scripts")
	void getScripts_renderADmn_dmnScripts() throws IOException {
		ModelsReportRenderer testee = new ModelsReportRenderer(
				new SiteRendererSink(new RenderingContext(BASEDIR, "test.html")),
				new ReportConfiguration(new File(""), Collections.emptyList(),
						Collections.singletonList(new File(MODELS_DIRECTORY, "table.dmn")), Collections.emptyList()));
		testee.render();

		Set<String> scripts = testee.getScripts();

		assertEquals(new HashSet<>(Arrays.asList("dmn-js/dmn.js", "dmn-js/dmn-viewer.min.js", "dmn-js/css/dmn-js.css")),
				scripts);
	}

	@Test
	@DisplayName("getScripts() after render() of a cmmn returns correct scripts")
	void getScripts_renderACmmn_cmmnScripts() throws IOException {
		ModelsReportRenderer testee = new ModelsReportRenderer(
				new SiteRendererSink(new RenderingContext(BASEDIR, "test.html")),
				new ReportConfiguration(new File(""), Collections.emptyList(), Collections.emptyList(),
						Collections.singletonList(new File(MODELS_DIRECTORY, "diagram.cmmn"))));
		testee.render();

		Set<String> scripts = testee.getScripts();

		assertEquals(new HashSet<>(Arrays.asList("cmmn-js/cmmn.js", "cmmn-js/cmmn-viewer.min.js")), scripts);
	}

	private String getContent(String filename) throws IOException {
		return IOUtil.toString(getClass().getResourceAsStream(filename)).replaceAll("[\n\r\t]", "").replaceAll(" +",
				" ");
	}

}
