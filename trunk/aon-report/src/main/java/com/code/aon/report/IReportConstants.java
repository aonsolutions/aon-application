package com.code.aon.report;

/**
 * Constants used in this libraries.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * 
 */
public interface IReportConstants {

	/**
	 * Context parameter key for the report configuration file.
	 */
	String CONFIG_FILE = "com.code.aon.report.config.file";

	/**
	 * Public ID of the report configuration file.
	 */
	String CONFIG_FILE_PUBLICID = "-//CODE-AON//DTD Report Config//EN";

	/**
	 * Name of the disgester rules file for parsing the report configuration
	 * file.
	 */
	String RULES_FILE = "report_config_digester.xml";;

	/**
	 * JasperReports parameter. Under this key, a <code>java.lang.Boolean</code>
	 * is passed to the JasperReports fill manager in order to specify if the
	 * report headers should be printed. <table align="center" border="1">
	 * <thead>
	 * <tr>
	 * <th>OutputFormat</th>
	 * <th>SHOULD_PRINT_HEADERS</th>
	 * </tr>
	 * </thead>
	 * <td><code>OutputFormat.PDF</code></td>
	 * <td><code>TRUE</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>OutputFormat.XLS</code></td>
	 * <td><code>FALSE</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>OutputFormat.CSV</code></td>
	 * <td><code>FALSE</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>OutputFormat.TXT</code></td>
	 * <td><code>TRUE</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>OutputFormat.HTML</code></td>
	 * <td><code>FALSE</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>OutputFormat.RTF</code></td>
	 * <td><code>TRUE</code></td>
	 * </tr>
	 * </table>
	 * 
	 */
	String SHOULD_PRINT_HEADERS = "printHeaders";

	/**
	 * JasperReports parameter. Under this key, a <code>java.util.List</code>
	 * of <code>net.sf.jasperreports.engine.JasperReport</code> is passed to 
	 * the jasper reports fill Manager.  
	 */
	String NESTED_REPORTS = "nestedReports";

}
