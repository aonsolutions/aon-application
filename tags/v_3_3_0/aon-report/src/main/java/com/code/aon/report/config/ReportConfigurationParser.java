package com.code.aon.report.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.xml.sax.SAXException;

import com.code.aon.report.IReportConstants;
import com.code.aon.report.ReportException;

/**
 * Parses a report configuration file. The configuration file must be a
 * well-formed XML. It will be validate against the following DTD. 
 * <code>
 * <br>
 * <br>
 * &lt;?xml encoding="UTF-8"?&gt;<br>
 * <br>
 * &lt;!ELEMENT report-config (default-config?,report+)&gt;<br>
 * &lt;!ELEMENT default-config (fetch-mode?,params?)&gt;<br>
 * &lt;!ELEMENT report (template,bean?,criteria?,fetch-mode?,params?,nested-report?)&gt;<br>
 * &lt;!ELEMENT template EMPTY&gt;<br>
 * &lt;!ELEMENT bean EMPTY&gt;<br>
 * &lt;!ELEMENT criteria EMPTY&gt;<br>
 * &lt;!ELEMENT nested-report EMPTY&gt;<br>
 * &lt;!ELEMENT fetch-mode EMPTY&gt;<br>
 * &lt;!ELEMENT params (param)+&gt;<br>
 * &lt;!ELEMENT param EMPTY&gt;<br>
 *<br>
 *<br>
 * &lt;!ATTLIST report-config xmlns CDATA #FIXED ''&gt;<br>
 * &lt;!ATTLIST default-config xmlns CDATA #FIXED ''&gt;<br>
 * &lt;!ATTLIST report xmlns CDATA #FIXED ''<br>
 * description CDATA #REQUIRED<br>
 * id NMTOKEN #REQUIRED&gt;<br>
 * &lt;!ATTLIST template xmlns CDATA #FIXED ''<br>
 * path CDATA #REQUIRED&gt;<br>
 * &lt;!ATTLIST bean xmlns CDATA #FIXED ''<br>
 * factory CDATA #REQUIRED<br>
 * method NMTOKEN #REQUIRED&gt;<br>
 * &lt;!ATTLIST criteria xmlns CDATA #FIXED ''<br>
 * provider CDATA #REQUIRED&gt;<br>
 * &lt;!ATTLIST nested-report xmlns CDATA #FIXED ''<br>
 * report NMTOKEN #REQUIRED&gt;<br>
 * &lt;!ATTLIST fetch-mode xmlns CDATA #FIXED ''<br>
 * paginated CDATA #REQUIRED<br>
 * page-count CDATA #IMPLIED<br>
 * virtualizer-max-size CDATA #IMPLIED&gt;<br>
 * &lt;!ATTLIST params<br>
 * xmlns CDATA #FIXED ''&gt;<br>
 * &lt;!ATTLIST param<br>
 * xmlns CDATA #FIXED ''<br>
 * id CDATA #REQUIRED<br>
 * value CDATA #REQUIRED&gt;<br>
 * </code>
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * 
 */
public class ReportConfigurationParser {

	/**
	 * Path of the report config file.
	 */
    public static final String REPORT_CONFIGURATION_FILE_PROPERTY = "com.code.aon.report.config.xml";	

	/**
	 * Default path of the report config file.
	 */
    public static final String DEFAULT_REPORT_CONFIGURATION_FILE_PROPERTY = "/report-config.xml";	
    
	/**
	 * Singleton instance.
	 */
	private static ReportConfigurationParser configurationParser;

	/**
	 * The manager instance.
	 */
	protected static ReportConfigurationManager configurationManager;

	/**
	 * Gets a suitable <code>Logger</code>.
	 */
	private static Logger LOGGER = Logger
			.getLogger(ReportConfigurationParser.class.getName());

	/**
	 * The Digester instance.
	 */
	private static Digester DIGESTER;

	/**
	 * Private Constructor.
	 */
	protected ReportConfigurationParser() {

	}

	/**
	 * Gets the <code>ReportConfigurationParser</code> instance.
	 * 
	 * @return The <code>ReportConfigurationParser</code> instance.
	 */
	public static ReportConfigurationParser getInstance() {
		if (configurationParser == null) {
			configurationParser = new ReportConfigurationParser();
		}
		return configurationParser;
	}

	/**
	 * Returns the Digester instance.
	 * 
	 * @return The Digester instance.
	 */
	private static final Digester getDigester() {
		if (DIGESTER == null) {
			LOGGER.info("Reading config from " + IReportConstants.RULES_FILE);
			DIGESTER = DigesterLoader
					.createDigester(ReportConfigurationParser.class
							.getResource(IReportConstants.RULES_FILE));
			DIGESTER.setPublicId(IReportConstants.CONFIG_FILE_PUBLICID);
			URL url = ReportConfigurationParser.class
					.getResource("report-config.dtd");
			DIGESTER.register(IReportConstants.CONFIG_FILE_PUBLICID, url
					.toString());
			DIGESTER.setValidating(false);
			DIGESTER.setUseContextClassLoader(true);
			// TODO Cambiar la DTD a un XML-Schema cuando cambiemos a la JDK-5
			// (JAXP-1.3 -xerces-).
		}
		return DIGESTER;
	}

	/**
	 * Returns the <code>ReportConfigurationManager</code> for the
	 * configuration file specified in the InputStream
	 * <code>reportConfigFile</code>.
	 * 
	 * 
	 * @param reportConfigFile
	 *            An InputStream pointing to a configuration file.
	 * @return the <code>ReportConfigurationManager</code>.
	 * @throws ReportException
	 *             When an IOException or a SAXException ocurred.
	 */
	public ReportConfigurationManager getConfiguration(
			InputStream reportConfigFile) throws ReportException {
		if (configurationManager != null) {
			return configurationManager;
		} 
		return parse(reportConfigFile);
	}

	/**
	 * Returns the <code>ReportConfigurationManager</code> for the
	 * configuration file specified in the context parameter
	 * <code>IReportConstants.CONFIG_FILE</code>.
	 * 
	 * @return the <code>ReportConfigurationManager</code>.
	 * @throws ReportException
	 *             When an MalformedURLException ocurred.
	 * @see com.code.aon.report.IReportConstants
	 */
	public ReportConfigurationManager getConfigurationManager()
			throws ReportException {
		if (configurationManager != null) {
			return configurationManager;
		} 
		try {
			String reportCfg = System.getProperty(REPORT_CONFIGURATION_FILE_PROPERTY);
			if ( reportCfg == null ) {
				reportCfg = DEFAULT_REPORT_CONFIGURATION_FILE_PROPERTY;
			}
			LOGGER.info("Report config file ..: " + reportCfg);
			return getConfigurationManager( new URL(reportCfg) );
		} catch (MalformedURLException e) {
			throw new ReportException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the <code>ReportConfigurationManager</code> for the
	 * configuration file specified in the URL <code>reportConfigFile</code>.
	 * 
	 * @param reportConfigFile
	 *            An URL pointing to a configuration file.
	 * @return the <code>ReportConfigurationManager</code>.
	 * @throws ReportException
	 *             When an IOException or a SAXException ocurred.
	 */
	public ReportConfigurationManager getConfigurationManager(
			URL reportConfigFile) throws ReportException {
		if (configurationManager != null) {
			return configurationManager;
		} 
		try {
			LOGGER.info("Report config URL ..: " + reportConfigFile);
			InputStream in = reportConfigFile.openStream();
			return parse(in);
		} catch (MalformedURLException e) {
			throw new ReportException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ReportException(e.getMessage(), e);
		}
	}

	/**
	 * Parses the configuration file.
	 * 
	 * @param in
	 *            The InputStream of the configuration file.
	 * @return the <code>ReportConfigurationManager</code>.
	 * @throws ReportException
	 *             When an IOException or a SAXException ocurred.
	 */
	private ReportConfigurationManager parse(InputStream in)
			throws ReportException {
		try {
			Digester digester = getDigester();
			configurationManager = (ReportConfigurationManager) digester
					.parse(in);
			return configurationManager;
		} catch (IOException e) {
			throw new ReportException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new ReportException(e.getMessage(), e);
		}
	}
}
