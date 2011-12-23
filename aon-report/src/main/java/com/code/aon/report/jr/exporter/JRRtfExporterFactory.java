package com.code.aon.report.jr.exporter;

import java.util.Map;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRRtfExporter;

import com.code.aon.report.IReportConstants;
import com.code.aon.report.ReportException;

/**
 * 
 * Returns an exporter for the RTF format.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * @since 1.0
 *  
 */
public class JRRtfExporterFactory implements IJRExporterFactory {

    @Override
	public JRExporter getJRExporter() {
        return new JRRtfExporter();
    }

    @Override
	public void fillJRParametersMap(Map<String,Object> fillMap, Map<JRExporterParameter,Object> exporterMap) throws ReportException {
		fillMap.put(IReportConstants.SHOULD_PRINT_HEADERS, new Boolean(false) );
	}
}
