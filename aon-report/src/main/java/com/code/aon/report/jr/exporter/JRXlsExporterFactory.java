package com.code.aon.report.jr.exporter;

import java.util.Map;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import com.code.aon.report.IReportConstants;

/**
 * 
 * Returns an exporter for the XLS format.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * @since 1.0
 *  
 */
public class JRXlsExporterFactory implements IJRExporterFactory {

    @Override
	public JRExporter getJRExporter() {
        return new JRXlsExporter();
    }

	@Override
	public void fillJRParametersMap(Map<String, Object> fillMap, Map<JRExporterParameter, Object> exporterMap) {
		exporterMap.put(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
		exporterMap.put(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
		exporterMap.put(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
		exporterMap.put(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
		fillMap.put(IReportConstants.SHOULD_PRINT_HEADERS, Boolean.FALSE );
	}

}
