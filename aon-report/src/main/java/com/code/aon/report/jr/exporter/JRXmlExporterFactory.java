package com.code.aon.report.jr.exporter;

import java.util.Map;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRXmlExporter;

import com.code.aon.report.IReportConstants;
import com.code.aon.report.ReportException;

/**
 * 
 * Returns an exporter for the XML format.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * @since 1.0
 *  
 */
public class JRXmlExporterFactory implements IJRExporterFactory  {

    @Override
	public JRExporter getJRExporter() {
        return new JRXmlExporter();
    }

	@Override
	public void fillJRParametersMap(Map<String, Object> fillMap, Map<JRExporterParameter, Object> exporterMap) throws ReportException {
		fillMap.put(IReportConstants.SHOULD_PRINT_HEADERS, Boolean.FALSE );
	}

}
