package com.code.aon.report.jr;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.report.ReportException;
import com.code.aon.report.config.ReportConfig;
import com.code.aon.report.config.ReportConfigurationManager;
import com.code.aon.report.config.ReportConfigurationParser;

/**
 * Factory to <code>JRReports</code> from the
 * <code>com.code.aon.ui.report.config.ReportConfigurationManager</code>.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * 
 */
public class JRReportFactory {

	/**
	 * Obtains a suitable <code>Logger</code>.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(JRReportFactory.class);

	/**
	 * Map of the registered reports.
	 */
	private static Map<String,Supplier<JRReport>> map = new HashMap<String,Supplier<JRReport>>();
	
	/**
	 * Gets the report declared with this identifier.
	 * 
	 * @param id
	 *            The identifier of the report.
	 * @return The report declared with this identifier.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	public static JRReport getJRReport(String id) throws ReportException {
		LOGGER.debug("Searching Report ..: {}",id);
		if (map.containsKey(id)) {
			LOGGER.debug("Report found in cache.");
			return map.get(id).get();
		}
		LOGGER.debug("Report not found in cache.");
		ReportConfigurationParser parser = ReportConfigurationParser
				.getInstance();
		ReportConfigurationManager rcm = parser.getConfigurationManager();
		ReportConfig config = rcm.getReport(id);
		if (config != null) {
			JRReportFactory.register(id, () -> getReport(config) );
			return getReport(config);
		}
		throw new ReportException("No Report found for key " + id);
	}

	/**
	 * Returns the JRReport binded to this ReportConfig.
	 * 
	 * @param config
	 *            The Report configuration.
	 * @return The JRReport object.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	private static JRReport getReport(ReportConfig config){
		JRReport report = new JRReport(config);
		return report;
	}

	/**
	 * Registers a report.
	 * 
	 * @param id
	 *            Identifier of the report.
	 * @param supplier
	 *            The report object.
	 */
	public static void register(String id, Supplier<JRReport> supplier) {
		LOGGER.info("Registering Report: {}", id);
		map.put(id, supplier);
	}
}
