package com.code.aon.report.jr.exporter;

import java.util.Map;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import com.code.aon.report.IReportConstants;
import com.code.aon.report.ReportException;

/**
 * Returns an exporter for the HTML format.
 * 
 * @author Consulting & Development. Eugenio Castellano - 05-may-2005
 * @since 1.0
 * 
 */
public class JRHtmlExporterFactory implements IJRExporterFactory {

	@Override
	public JRExporter getJRExporter() {
		return new JRHtmlExporter();
	}

	@Override
	public void fillJRParametersMap(Map<String,Object> fillMap, Map<JRExporterParameter,Object> exporterMap) throws ReportException {
		exporterMap.put(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN,Boolean.FALSE);
		exporterMap.put(JRHtmlExporterParameter.HTML_HEADER, getHtmlHeader());
		exporterMap.put(JRHtmlExporterParameter.HTML_FOOTER, getHtmlFooter());
		exporterMap.put(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
		fillMap.put(IReportConstants.SHOULD_PRINT_HEADERS, Boolean.FALSE);
	}

	/**
	 * Returns A string representing HTML code that will be inserted before the generated report.
	 * @return the text.
	 */
	public String getHtmlHeader() {
		return "";
	}

	/**
	 * Returns A string representing HTML code that will be inserted after the generated report.
	 * @return the text.
	 */
	public String getHtmlFooter() {
		return "";
	}
}
