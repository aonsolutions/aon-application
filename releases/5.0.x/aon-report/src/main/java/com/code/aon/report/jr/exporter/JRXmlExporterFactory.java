package com.code.aon.report.jr.exporter;

import java.util.Map;

import net.sf.jasperreports.engine.JRExporter;
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

    /* (non-Javadoc)
     * @see com.code.aon.ui.report.jr.exporter.IJRExporterFactory#getJRExporter()
     */
    public JRExporter getJRExporter() {
        return new JRXmlExporter();
    }

	/* (non-Javadoc)
	 * @see com.code.aon.ui.report.jr.exporter.IJRExporterFactory#fillJRParametersMap(java.util.Map)
	 */
	public void fillJRParametersMap(Map<Object,Object> map) throws ReportException {
		map.put(IReportConstants.SHOULD_PRINT_HEADERS, Boolean.FALSE );
	}

}
