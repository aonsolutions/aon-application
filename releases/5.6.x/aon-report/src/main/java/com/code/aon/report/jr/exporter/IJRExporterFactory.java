package com.code.aon.report.jr.exporter;

import java.util.Map;

import com.code.aon.report.ReportException;

import net.sf.jasperreports.engine.JRExporter;

/**
 * Factory to obtain a suitable
 * <code>net.sf.jasperreports.engine.JRExporter</code>.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * @since 1.0
 * 
 */
public interface IJRExporterFactory {

	/**
	 * Returns a <code>net.sf.jasperreports.engine.JRExporter</code>
	 * implementation.
	 * 
	 * @return An implementation of
	 *         <code>net.sf.jasperreports.engine.JRExporter</code>.
	 */
	JRExporter getJRExporter();

	/**
	 * Method for passing the specific parameter of each output format.
	 * 
	 * @param map
	 *            The parameters map.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	void fillJRParametersMap(Map<Object,Object> map) throws ReportException;

}
