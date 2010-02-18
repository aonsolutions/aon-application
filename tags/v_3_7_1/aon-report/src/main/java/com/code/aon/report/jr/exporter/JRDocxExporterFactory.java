package com.code.aon.report.jr.exporter;
import java.util.Map;

import net.sf.jasperreports.engine.JRExporter;
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

    /* (non-Javadoc)
     * @see com.code.aon.ui.report.jr.exporter.IJRExporterFactory#getJRExporter()
     */
    public JRExporter getJRExporter() {
        return new JRDocxExporter();
    }

	/* (non-Javadoc)
	 * @see com.code.aon.ui.report.jr.exporter.IJRExporterFactory#fillJRParametersMap(java.util.Map)
	 */
	public void fillJRParametersMap(Map<Object,Object> map) throws ReportException {
		map.put(IReportConstants.SHOULD_PRINT_HEADERS, Boolean.TRUE);
		// Para encriptar los PDF
		//	map.put(JRPdfExporterParameter.IS_ENCRYPTED, Boolean.TRUE );
		//	map.put(JRPdfExporterParameter.USER_PASSWORD, "password" );
		//
	}
}
