package com.code.aon.report.jr.exporter;
import java.util.Map;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;

import com.code.aon.report.IReportConstants;
import com.code.aon.report.ReportException;

/**
 * Returns an exporter for the PDF format.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * @since 1.0
 *
 */
public class JRDocxExporterFactory implements IJRExporterFactory {

	@Override
    public JRExporter getJRExporter() {
        return new JRDocxExporter();
    }

	@Override
	public void fillJRParametersMap(Map<String,Object> fillMap, Map<JRExporterParameter,Object> exporterMap) throws ReportException {
		fillMap.put(IReportConstants.SHOULD_PRINT_HEADERS, Boolean.TRUE);
		// Para encriptar los PDF
		//	map.put(JRPdfExporterParameter.IS_ENCRYPTED, Boolean.TRUE );
		//	map.put(JRPdfExporterParameter.USER_PASSWORD, "password" );
		//
	}
}
