package com.code.aon.report.jr.exporter;

import java.util.Map;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;

import com.code.aon.report.IReportConstants;
import com.code.aon.report.ReportException;

/**
 * Returns an exporter for the TXT format.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * @since 1.0
 * 
 */
public class JRTxtExporterFactory implements IJRExporterFactory {

	@Override
	public JRExporter getJRExporter() {
		return new JRTextExporter();
	}

	@Override
	public void fillJRParametersMap(Map<String,Object> fillMap, Map<JRExporterParameter,Object> exporterMap) throws ReportException {
		exporterMap.put(JRTextExporterParameter.LINE_SEPARATOR, "\n");
		exporterMap.put(JRTextExporterParameter.CHARACTER_WIDTH, new Integer(5));
		exporterMap.put(JRTextExporterParameter.CHARACTER_HEIGHT, new Integer(8));
		exporterMap.put(JRTextExporterParameter.PAGE_HEIGHT, new Integer(50));
		fillMap.put(IReportConstants.SHOULD_PRINT_HEADERS, Boolean.TRUE);
	}
}
