package com.code.aon.report.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager of the declared report configurations.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * 
 */
public class ReportConfigurationManager {

	/**
	 * Default report config.
	 */
	private ReportConfig defaultConfig;

	/**
	 * Declared reports Map.
	 */
	private Map<String,ReportConfig> reports;

	/**
	 * Returns the declared default config.
	 * 
	 * @return The declared default config.
	 */
	public ReportConfig getDefaultConfig() {
		return defaultConfig;
	}

	/**
	 * Sets the declared default config.
	 * 
	 * @param defaultConfig
	 *            The declared default config.
	 */
	public void setDefaultConfig(ReportConfig defaultConfig) {
		this.defaultConfig = defaultConfig;
	}

	/**
	 * Add a declared report to the report's map.
	 * @param config The declared report to be added.
	 */
	public void addReport(ReportConfig config) {
		if (reports == null) {
			reports = new HashMap<String,ReportConfig>();
		}
		reports.put(config.getId(), config);
	}

	/**
	 * Returns the configuration of the report that has the specified identifier.
	 * @param id The report identifier.
	 * @return The configuration of the report. 
	 */
	public ReportConfig getReport(String id) {
		return reports.get(id);
	}
}
