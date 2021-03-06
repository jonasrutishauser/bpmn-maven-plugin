package com.github.jonasrutishauser.maven.plugin.bpmn;

import static javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD;
import static javax.xml.XMLConstants.ACCESS_EXTERNAL_SCHEMA;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.maven.doxia.markup.HtmlMarkup;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.reporting.AbstractMavenReportRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelsReportRenderer extends AbstractMavenReportRenderer {

    private static final String MODELS_PATH = "models/";

    private static final String SCRIPT_TAG = "script";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ReportConfiguration configuration;

    private final Set<String> scripts = new HashSet<>();

    public ModelsReportRenderer(Sink sink, ReportConfiguration configuration) {
        super(sink);
        this.configuration = configuration;
    }

    @Override
    public void render() {
        sink.head();
        sink.title();
        text(getTitle());
        sink.title_();
        if (configuration.hasDmn()) {
            stylesheet("dmn-js/assets/dmn-js-drd.css");
            stylesheet("dmn-js/assets/dmn-js-decision-table.css");
            stylesheet("dmn-js/assets/dmn-js-literal-expression.css");
            stylesheet("dmn-js/assets/dmn-js-shared.css");
            stylesheet("dmn-js/assets/dmn-font/css/dmn.css");
            scripts.add("dmn-js/assets/dmn-font/font/dmn.eot");
            scripts.add("dmn-js/assets/dmn-font/font/dmn.svg");
            scripts.add("dmn-js/assets/dmn-font/font/dmn.ttf");
            scripts.add("dmn-js/assets/dmn-font/font/dmn.woff");
            scripts.add("dmn-js/assets/dmn-font/font/dmn.woff2");
        }
        sink.head_();

        sink.body();
        renderBody();
        sink.body_();

        sink.flush();
        sink.close();
    }

    @Override
    public String getTitle() {
        return "Business Process Models";
    }

    public String getTargetFile(File model) {
        return MODELS_PATH + getPath(model).replaceAll("[\\\\/]", "-") + "-" + model.getName();
    }

    public Set<String> getScripts() {
        return scripts;
    }

    @Override
    protected void renderBody() {
        startSection("Business Process Models");
        renderModelTable();
        if (configuration.hasBpmn()) {
            startSection("BPMN");
            renderModels(configuration.getBpmns(), "bpmn");
            endSection();
        }
        if (configuration.hasDmn()) {
            startSection("DMN");
            renderModels(configuration.getDmns(), "dmn");
            endSection();
        }
        if (configuration.hasCmmn()) {
            startSection("CMMN");
            renderModels(configuration.getCmmns(), "cmmn");
            endSection();
        }
        endSection();
    }

    private void renderModelTable() {
        startTable(new int[] {Sink.JUSTIFY_LEFT, Sink.JUSTIFY_LEFT, Sink.JUSTIFY_LEFT, Sink.JUSTIFY_LEFT}, false);
        renderTableHeader();
        for (File bpmn : configuration.getBpmns()) {
            renderTableRow(bpmn, getProcessName(bpmn), "BPMN");
        }
        for (File dmn : configuration.getDmns()) {
            renderTableRow(dmn, getDecisionName(dmn), "DMN");
        }
        for (File cmmn : configuration.getCmmns()) {
            renderTableRow(cmmn, getCaseName(cmmn), "CMMN");
        }
        endTable();
    }

    private void renderTableRow(File model, String name, String type) {
        tableRow(new String[] {createLinkPatternedText(model.getName(), "#" + model.getName()), getPath(model), name,
                createLinkPatternedText(type, "#" + type)});
    }

    private String getPath(File bpmn) {
        return configuration.getBasedir().getAbsoluteFile().toPath()
                .relativize(bpmn.getParentFile().getAbsoluteFile().toPath()).toString();
    }

    private void renderModels(List<File> models, String type) {
        sourceJavaScript(type + "-js/" + type + "-viewer.production.min.js");
        sourceJavaScript(type + "-js/" + type + ".js");
        for (File model : models) {
            startSection(model.getName());
            container(getTargetFile(model).replace(MODELS_PATH, ""));
            javaScript("show" + Character.toUpperCase(type.charAt(0)) + type.substring(1) + "(document.getElementById('"
                    + getTargetFile(model).replace(MODELS_PATH, "") + "'), '" + getTargetFile(model) + "');");
            endSection();
        }
    }

    private void container(String id) {
        SinkEventAttributeSet atts = new SinkEventAttributeSet();
        atts.addAttribute(SinkEventAttributes.ID, id);
        sink.unknown("div", new Object[] {Integer.valueOf(HtmlMarkup.TAG_TYPE_START)}, atts);
        sink.unknown("div", new Object[] {Integer.valueOf(HtmlMarkup.TAG_TYPE_END)}, null);
    }

    @Override
    protected void javaScript(String script) {
        SinkEventAttributeSet atts = new SinkEventAttributeSet();
        atts.addAttribute(SinkEventAttributes.TYPE, "text/javascript");
        sink.unknown(SCRIPT_TAG, new Object[] {Integer.valueOf(HtmlMarkup.TAG_TYPE_START)}, atts);
        sink.text(script);
        sink.unknown(SCRIPT_TAG, new Object[] {Integer.valueOf(HtmlMarkup.TAG_TYPE_END)}, null);
    }

    private void stylesheet(String href) {
        scripts.add(href);
        SinkEventAttributeSet atts = new SinkEventAttributeSet();
        atts.addAttribute(SinkEventAttributes.REL, "stylesheet");
        atts.addAttribute(SinkEventAttributes.HREF, href);
        sink.unknown("link", new Object[] {Integer.valueOf(HtmlMarkup.TAG_TYPE_SIMPLE)}, atts);
    }

    private void sourceJavaScript(String src) {
        scripts.add(src);
        SinkEventAttributeSet atts = new SinkEventAttributeSet();
        atts.addAttribute(SinkEventAttributes.SRC, src);
        atts.addAttribute(SinkEventAttributes.TYPE, "text/javascript");
        sink.unknown(SCRIPT_TAG, new Object[] {Integer.valueOf(HtmlMarkup.TAG_TYPE_START)}, atts);
        sink.unknown(SCRIPT_TAG, new Object[] {Integer.valueOf(HtmlMarkup.TAG_TYPE_END)}, null);
    }

    protected String getProcessName(File bpmn) {
        try {
            return getModelName(bpmn, "http://www.omg.org/spec/BPMN/20100524/MODEL", "process");
        } catch (XMLStreamException | FactoryConfigurationError | IOException e) {
            logger.warn("failed to get bpmn process name", e);
            return null;
        }
    }

    protected String getDecisionName(File dmn) {
        try {
            return getModelName(dmn, "https://www.omg.org/spec/DMN/20191111/MODEL/", "decision");
        } catch (XMLStreamException | FactoryConfigurationError | IOException e) {
            logger.warn("failed to get dmn decision name", e);
            return null;
        }
    }

    protected String getCaseName(File cmmn) {
        try {
            return getModelName(cmmn, "http://www.omg.org/spec/CMMN/20151109/MODEL", "case");
        } catch (XMLStreamException | FactoryConfigurationError | IOException e) {
            logger.warn("failed to get cmmn case name", e);
            return null;
        }
    }

    private String getModelName(File model, String namespace, String localName)
            throws XMLStreamException, FactoryConfigurationError, IOException {
        try (InputStream in = new FileInputStream(model)) {
            XMLInputFactory factory = XMLInputFactory.newFactory();
            factory.setProperty(ACCESS_EXTERNAL_DTD, "");
            factory.setProperty(ACCESS_EXTERNAL_SCHEMA, "");
            XMLStreamReader reader = factory.createXMLStreamReader(in);
            while (reader.hasNext()) {
                if (reader.next() == START_ELEMENT && namespace.equals(reader.getNamespaceURI())
                        && localName.equals(reader.getLocalName())) {
                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        if ("name".equals(reader.getAttributeLocalName(i))) {
                            return reader.getAttributeValue(i);
                        }
                    }
                }
            }
        }
        return null;
    }

    private void renderTableHeader() {
        sink.tableRow();
        sink.tableHeaderCell(SinkEventAttributeSet.LEFT);
        sink.text("Filename");
        sink.tableHeaderCell_();
        sink.tableHeaderCell(SinkEventAttributeSet.LEFT);
        sink.text("Path");
        sink.tableHeaderCell_();
        sink.tableHeaderCell(SinkEventAttributeSet.LEFT);
        sink.text("Model name");
        sink.tableHeaderCell_();
        sink.tableHeaderCell(SinkEventAttributeSet.LEFT);
        sink.text("Model type");
        sink.tableHeaderCell_();
        sink.tableRow_();
    }

}
