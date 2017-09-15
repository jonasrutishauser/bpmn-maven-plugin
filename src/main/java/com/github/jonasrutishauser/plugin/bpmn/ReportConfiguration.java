package com.github.jonasrutishauser.plugin.bpmn;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

public class ReportConfiguration {

	private final File basedir;
	private final List<File> bpmns;
	private final List<File> dmns;
	private final List<File> cmmns;

	public ReportConfiguration(File basedir, List<File> bpmns, List<File> dmns, List<File> cmmns) {
		this.basedir = basedir;
		this.bpmns = bpmns;
		this.dmns = dmns;
		this.cmmns = cmmns;
	}

	public boolean canGenerateReport() {
		return hasBpmn() || hasDmn() || hasCmmn();
	}

	public boolean hasBpmn() {
		return !bpmns.isEmpty();
	}

	public boolean hasDmn() {
		return !dmns.isEmpty();
	}

	public boolean hasCmmn() {
		return !cmmns.isEmpty();
	}

	public File getBasedir() {
		return basedir;
	}

	public List<File> getBpmns() {
		return bpmns;
	}

	public List<File> getDmns() {
		return dmns;
	}

	public List<File> getCmmns() {
		return cmmns;
	}

	public Stream<File> getModels() {
		return Stream.concat(bpmns.stream(), Stream.concat(dmns.stream(), cmmns.stream()));
	}

}
