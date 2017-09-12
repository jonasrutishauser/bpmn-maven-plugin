package ch.pmr.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.maven.doxia.siterenderer.RenderingContext;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.maven.shared.utils.io.IOUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

	private String getContent(String filename) throws IOException {
		return IOUtil.toString(getClass().getResourceAsStream(filename)).replaceAll("[\n\r\t]", "").replaceAll(" +",
				" ");
	}

}
