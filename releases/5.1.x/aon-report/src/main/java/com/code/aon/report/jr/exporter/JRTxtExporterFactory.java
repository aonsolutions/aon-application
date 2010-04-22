package com.code.aon.report.jr.exporter;

import java.util.Map;

import net.sf.jasperreports.engine.JRExporter;
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

	/* (non-Javadoc)
	 * @see com.code.aon.ui.report.jr.exporter.IJRExporterFactory#getJRExporter()
	 */
	public JRExporter getJRExporter() {
		return new JRTextExporter();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.report.jr.exporter.IJRExporterFactory#fillJRParametersMap(java.util.Map)
	 */
	public void fillJRParametersMap(Map<Object,Object> map) throws ReportException {
		map.put(JRTextExporterParameter.LINE_SEPARATOR, "\n");
		map.put(JRTextExporterParameter.CHARACTER_WIDTH, new Integer(5));
		map.put(JRTextExporterParameter.CHARACTER_HEIGHT, new Integer(8));
		map.put(JRTextExporterParameter.PAGE_HEIGHT, new Integer(50));
		map.put(IReportConstants.SHOULD_PRINT_HEADERS, Boolean.TRUE);
	}
}
