package com.code.aon.report.jr.exporter;

import java.util.HashMap;
import java.util.Map;

import com.code.aon.report.OutputFormat;

/**
 * Manager of the factory for obtaingn a suitable exporter.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * @since 1.0
 * 
 */
public class JRExporterFactoryManager {

	/**
	 * Map of the registered factories.
	 */
	private static Map<OutputFormat,IJRExporterFactory> MAP = new HashMap<OutputFormat,IJRExporterFactory>();

	/**
	 * Registers a factory for this output format.
	 * 
	 * @param outFormat
	 *            The output format.
	 * @param factory
	 *            Factory for the output format.
	 */
	public static void register(OutputFormat outFormat,
			IJRExporterFactory factory) {
		MAP.put(outFormat, factory);
	}

	/**
	 * Checks if there is a suitable factory for this output format.
	 * 
	 * @param outFormat
	 *            The output format.
	 * @return TRUE if exists a factory for this output format., FALSE
	 *         otherwise.
	 */
	public static boolean accept(OutputFormat outFormat) {
		return MAP.containsKey(outFormat);
	}

	/**
	 * Return a suitable factory for this output format.
	 * 
	 * @param outFormat
	 *            The output format.
	 * @return A suitable factory for this output format.
	 */
	public static IJRExporterFactory getJRExporterFactory(OutputFormat outFormat) {
		return MAP.get(outFormat);
	}

	static {
		register(OutputFormat.PDF, new JRPdfExporterFactory());
		register(OutputFormat.HTML, new JRHtmlExporterFactory());
		register(OutputFormat.XLS, new JRXlsExporterFactory());
		register(OutputFormat.XML, new JRXmlExporterFactory());
		register(OutputFormat.CSV, new JRCsvExporterFactory());
		register(OutputFormat.RTF, new JRRtfExporterFactory());
		register(OutputFormat.TXT, new JRTxtExporterFactory());
		register(OutputFormat.DOCX, new JRDocxExporterFactory());
	}

}
