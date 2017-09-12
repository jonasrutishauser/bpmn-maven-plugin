package ch.pmr.plugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ReportConfiguration")
public class ReportConfigurationTest {

	@Test
	@DisplayName("canGenerateReport() without models returns false")
	void canGenerateReport_noModels_false() {
		ReportConfiguration testee = new ReportConfiguration(null, Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList());

		assertFalse(testee.canGenerateReport());
	}

	@Test
	@DisplayName("canGenerateReport() with a bpmn returns true")
	void canGenerateReport_aBpmn_true() {
		ReportConfiguration testee = new ReportConfiguration(null, Collections.singletonList(new File("target")),
				Collections.emptyList(), Collections.emptyList());

		assertTrue(testee.canGenerateReport());
	}

	@Test
	@DisplayName("canGenerateReport() with a dmn returns true")
	void canGenerateReport_aDmn_true() {
		ReportConfiguration testee = new ReportConfiguration(null, Collections.emptyList(),
				Collections.singletonList(new File("target")), Collections.emptyList());

		assertTrue(testee.canGenerateReport());
	}

	@Test
	@DisplayName("canGenerateReport() with a cmmn returns true")
	void canGenerateReport_aCmmn_true() {
		ReportConfiguration testee = new ReportConfiguration(null, Collections.emptyList(), Collections.emptyList(),
				Collections.singletonList(new File("target")));

		assertTrue(testee.canGenerateReport());
	}

	@Test
	@DisplayName("getModels() returns stream with all bpmn, dmn and cmmn models")
	void getModels_allBpmnDmnCmmn() {
		File bpmn1 = new File("target/bpmn1");
		File bpmn2 = new File("target/bpmn2");
		File dmn = new File("target/dmn");
		File cmmn1 = new File("target/cmmn1");
		File cmmn2 = new File("target/cmmn2");
		File cmmn3 = new File("target/cmmn3");
		ReportConfiguration testee = new ReportConfiguration(null, Arrays.asList(bpmn1, bpmn2), Arrays.asList(dmn),
				Arrays.asList(cmmn1, cmmn2, cmmn3));

		Stream<File> models = testee.getModels();

		assertThat(models.collect(Collectors.toList()), containsInAnyOrder(bpmn1, bpmn2, dmn, cmmn1, cmmn2, cmmn3));
	}

}
