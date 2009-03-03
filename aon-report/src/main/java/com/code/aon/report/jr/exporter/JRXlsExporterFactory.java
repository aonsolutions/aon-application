package com.code.aon.report.jr.exporter;

import java.util.Map;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import com.code.aon.report.IReportConstants;
import com.code.aon.report.ReportException;

/**
 * 
 * Returns an exporter for the XLS format.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * @since 1.0
 *  
 */
public class JRXlsExporterFactory implements IJRExporterFactory {

    /* (non-Javadoc)
     * @see com.code.aon.ui.report.jr.exporter.IJRExporterFactory#getJRExporter()
     */
    public JRExporter getJRExporter() {
        return new JRXlsExporter();
    }

	/* (non-Javadoc)
	 * @see com.code.aon.ui.report.jr.exporter.IJRExporterFactory#fillJRParametersMap(java.util.Map)
	 */
	public void fillJRParametersMap(Map<Object,Object> map) throws ReportException {
		map.put(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
				Boolean.TRUE);
		map.put(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,
				Boolean.FALSE);
		map.put(IReportConstants.SHOULD_PRINT_HEADERS, Boolean.FALSE );
	}

}
