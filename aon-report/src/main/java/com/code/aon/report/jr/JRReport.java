package com.code.aon.report.jr;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IFinderBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ql.Criteria;
import com.code.aon.report.IReportConstants;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.report.config.ReportConfig;
import com.code.aon.report.config.ReportConfigurationManager;
import com.code.aon.report.config.ReportConfigurationParser;
import com.code.aon.report.config.ReportFetchMode;
import com.code.aon.report.jr.exporter.IJRExporterFactory;
import com.code.aon.report.jr.exporter.JRExporterFactoryManager;

/**
 * Logic class representing a report.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * 
 */
public class JRReport {

	/**
	 * Obtains a suitable <code>Logger</code>.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(JRReport.class);
	
	private static String REPORT_PATH = "/home/COMMON-RESOURCES/aon-report";

	/**
	 * The report configuration of this report.
	 */
	private ReportConfig config;
	
	private Map<String, Object> dynParams = null;
	private Map<String, Object> customParams = null;
	private int generatedPages;
	private Integer currentDomain;

	/**
	 * Constructs a report based on this report configuration.
	 * 
	 * @param config
	 *            The Report configuration.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	public JRReport(ReportConfig config) throws ReportException {
		this.config = config;
	}

	/**
	 * Returns the report configuration of this report.
	 * 
	 * @return The report configuration of this report.
	 */
	public ReportConfig getReportConfig() {
		return this.config;
	}

	/**
	 * Returns a Paged DataSource Provider.
	 * 
	 * @param criteria
	 *            The criteria of th query.
	 * @param count
	 *            The count of th query.
	 * @return A Paged DataSource Provider.
	 * @throws ReportException
	 *             If an error ocurred.
	 * 
	 */
	public JRDataSourceProvider getJRPagedDataSourceProvider(Criteria criteria, int count)
			throws ReportException {
		IFinderBean bean = getFinderBean();
		Class<?> clazz = bean.getPOJOClass();
		JRPagedBeanDataSourceProvider dsp = new JRPagedBeanDataSourceProvider(
				clazz, bean, criteria, count);
		return dsp;
	}

	/**
	 * Returns a DataSource Provider.
	 * 
	 * @param criteria
	 *            The criteria of th query.
	 * @return A DataSource Provider.
	 * @throws ReportException
	 *             If an error ocurred.
	 * 
	 */
	public JRDataSourceProvider getJRDataSourceProvider(Criteria criteria)
			throws ReportException {
		IFinderBean bean = getFinderBean();
		Class<?> clazz = bean.getPOJOClass();
		JRBeanCollectionDataSourceProvider dsp = new JRBeanCollectionDataSourceProvider(
				clazz, bean, criteria);
		return dsp;
	}
	
	private String getCustomTemplate( String reportKey ) {
		String factoryName = HibernateUtil.getSessionFactoryName();
		Session session = HibernateUtil.getSession(factoryName);
		try {
			String name = "REPORT_" + reportKey;
	        String select = "SELECT app_param.value " 
			        		+" FROM ApplicationParameter as app_param " 
			        		+" WHERE "+ (currentDomain != null 
			        					? "app_param.domain = " + currentDomain 
			        					: DomainManager.getSQLWhereClause("app_param.domain"))
			        		+" AND app_param.name = '" + name + "'";
			Query query = session.createQuery(select);
			List<String> list = (List<String>) query.list();
			if (! list.isEmpty() ) {
				return list.get(0);
			}
		} catch ( Throwable th ) {
			LOGGER.error( "Error retrieving report app param", th );
		} finally {
			if (HibernateUtil.mustCloseSession()) {
				HibernateUtil.closeSession(factoryName);
			}
		}
		return null;
	}

	private InputStream getTemplateInputStream( ReportConfig config ) {
		InputStream input = null;
		String customTemplate = getCustomTemplate(config.getId());
		if (! StringUtils.isEmpty(customTemplate) ) {
			File file = new File( config.getTemplate() );
			File customDirectory = new File( REPORT_PATH, customTemplate );
			if ( customDirectory.exists() && customDirectory.canRead() ) {
				File customFile = new File( customDirectory, file.getName() );
				if ( customFile.exists() && customFile.canRead() ) {
					try {
						input = new BufferedInputStream( new FileInputStream(customFile) );
					} catch (FileNotFoundException e) {
						LOGGER.error( "Custome template not found: " + customFile, e);
					}
				}
			}
		} 
		if ( input == null ) {
			input = JRReport.class.getResourceAsStream(config.getTemplate());
		}
		return input;
	}
	
	/**
	 * Returns the JasperReport object that this object represents.
	 * 
	 * @return The JasperReport object that this object represents.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	public JasperReport getJasperReport() throws ReportException {
		InputStream input = getTemplateInputStream( config );
		if (input == null) {
			throw new ReportException("Can not load report template!"); //$NON-NLS-1$
		}
		try {
			Object o = JRLoader.loadObject(input);
			input.close();
			return (JasperReport) o;
		} catch (JRException e) {
			throw new ReportException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ReportException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the IFinderBean that provides the data of this report.
	 * 
	 * @return the IFinderBean that provides the data of this report.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	private IFinderBean getFinderBean() throws ReportException {
		try {
			return BeanManager.getManagerBean(config.getBeanKey());
		} catch (ManagerBeanException e) {
			throw new ReportException(e.getMessage(), e);
		}
	}

	/**
	 * Runs this report.
	 * 
	 * @param outputFormat
	 *            Tht output format.
	 * @param out
	 *            The output stream.
	 * @param bundle
	 *            The resource Bundle used in i18n.
	 * @param criteria
	 *            The criteria of the data.
	 * @param collection
	 *            The collection of the data.
	 * @param params 
	 * 			  Sets export parameters from a specified map.
	 * @return The outcome.
	 * @throws ReportException
	 *             Si se produce algún error. If an error ocurred.
	 */
	public String run(OutputFormat outputFormat, OutputStream out,
			ResourceBundle bundle, Criteria criteria, Collection<?> collection, Map<Object,Object>... params) throws ReportException {
		return run(outputFormat, out, bundle, Locale.getDefault() ,criteria, collection, params);
	}
	
	/**
	 * Runs this report.
	 * 
	 * @param outputFormat
	 *            Tht output format.
	 * @param out
	 *            The output stream.
	 * @param bundle
	 *            The resource Bundle used in i18n.
	 * @param locale 
	 *            The locale of application.
	 * @param criteria
	 *            The criteria of the data.
	 * @param collection
	 *            The collection of the data.
	 * @param params 
	 * 			  Sets export parameters from a specified map.
	 * @return The outcome.
	 * @throws ReportException
	 *             Si se produce algún error. If an error ocurred.
	 */
	public String run(OutputFormat outputFormat, OutputStream out,
			ResourceBundle bundle, Locale locale, Criteria criteria, Collection<?> collection, Map<Object,Object>... params) throws ReportException {
		try {
			LOGGER.info("START Report {}({}) {}", new Object[]{config.getId(), outputFormat, (criteria!=null)?criteria:""});
			setGeneratedPages( 0 );
			if (JRExporterFactoryManager.accept(outputFormat)) {
				Date startDate = new Date();
				IJRExporterFactory factory;
				factory = JRExporterFactoryManager
						.getJRExporterFactory(outputFormat);
				Map<String, Object> fillMap = new HashMap<String, Object>();
				Map<JRExporterParameter, Object> exporterMap = new HashMap<JRExporterParameter, Object>();
				
				factory.fillJRParametersMap(fillMap, exporterMap);

				for (int i = 0; i < params.length; i++) {
					for (Object key : params[i].keySet() ) {
						if (key instanceof JRExporterParameter ) {
							exporterMap.put( (JRExporterParameter) key , params[i].get(key));
						} else if (key instanceof String ) {
							fillMap.put( (String) key , params[i].get(key));							
						} else {
							LOGGER.warn("No sé qué hacer con el parámetro ..: " + key);
						}
					}
				}
				
				passDefaultParameters(fillMap);
				passCustomParameters(fillMap);
				passDynamicParameters( fillMap );
				boolean hasCache = passFetchModeParameters(fillMap);
				passNestedReports(fillMap);
				exporterMap.put(JRExporterParameter.OUTPUT_STREAM, out);
				
				if ( config.getParams() != null && config.getParams().containsKey(
						JRParameter.REPORT_RESOURCE_BUNDLE)) {
					String baseName = (String) config.getParams().get(
							JRParameter.REPORT_RESOURCE_BUNDLE);
					fillMap.put(JRParameter.REPORT_RESOURCE_BUNDLE, ResourceBundle
							.getBundle(baseName, locale));
				} else {
					fillMap.put(JRParameter.REPORT_RESOURCE_BUNDLE, bundle);
				}
				fillMap.put(JRParameter.REPORT_LOCALE, locale);
				JRDataSource ds = null;
				if(config.getCollectionProvider() == null){
					JRDataSourceProvider jrdsp  = null;
					if (hasCache) {
						jrdsp = getJRPagedDataSourceProvider(criteria, config.getFetchMode().getPageCount());
					} else {
						jrdsp = getJRDataSourceProvider(criteria);	
					}
					ds = jrdsp.create(null);
				}else{
					ds = new JRBeanCollectionDataSource(collection);
				}
				
				JasperReport jr = getJasperReport();
				JasperPrint print = JasperFillManager.fillReport(jr, fillMap, ds);
				setGeneratedPages( print.getPages().size() ); 
				exporterMap.put(JRExporterParameter.JASPER_PRINT, print);
				if ( LOGGER.isDebugEnabled()) {
					debugParameters( fillMap );
				}
				JRExporter exporter = factory.getJRExporter();
				exporter.setParameters(exporterMap);
				exporter.exportReport();
				long  delay = (new Date()).getTime() - startDate.getTime(); 
				LOGGER.info("END Report {}: {} seconds.", config.getId(), ((double)(delay/1000)));
				if (hasCache) {
					cleanCache(fillMap);
				}
			} else {
				throw new ReportException("No suitable Exporter for format "
						+ outputFormat);
			}
			return null;
		} catch (JRException e) {
			throw new ReportException(e.getMessage(), e);
		}
	}

	private void debugParameters(Map<String, Object> map) {
		LOGGER.debug( "Begin Parameters:" );
		Set<String> keys = map.keySet();
		for (Object key: keys){
			Object value  = map.get(key);
			LOGGER.debug( "\tParameter: {} ---> {}",key,value );	
		}
		LOGGER.debug( "End Parameters:" );
	}

	private void passDynamicParameters(Map<String, Object> map) {
		if (dynParams != null) {
			LOGGER.debug("Passing Dynamic Parameters");
			map.putAll(dynParams);
		}
	}

	/**
	 * Pass the virtualizer parameter to fill manager, if needed.
	 * 
	 * @param map
	 *            The parameters map.
	 * @return True if the record is virtualized.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	private boolean passFetchModeParameters(Map<String, Object> map) throws ReportException {
		ReportFetchMode fetchMode = null;
		ReportConfig defaultConfig = getDefaultConfig();
		ReportFetchMode defaultFetchMode = (defaultConfig == null) ? null
				: defaultConfig.getFetchMode();
		ReportFetchMode reportFetchMode = config.getFetchMode();
		fetchMode = (reportFetchMode == null) ? defaultFetchMode
				: reportFetchMode;
		if (fetchMode != null && fetchMode.isPaginated()) {
			JRFileVirtualizer virt;
			String path = System.getProperty("java.io.tmpdir");
			int vms = fetchMode.getVirtualizerPageMax();
			LOGGER.info("Report Virtualizer. Page Max: {} Path: {}",vms,path);
			virt = new JRFileVirtualizer(vms, path);
			map.put(JRParameter.REPORT_VIRTUALIZER, virt);
		}
		return (fetchMode != null && fetchMode.isPaginated());
	}

	/**
	 * Cleans the temporary generated files.
	 * 
	 * @param map
	 *            The parameters map.
	 */
	private void cleanCache(Map<String,Object> map) {
		JRFileVirtualizer virt;
		virt = (JRFileVirtualizer) map.get(JRParameter.REPORT_VIRTUALIZER);
		virt.cleanup();
	}

	/**
	 * Pass the nested reports list to the fill manager.
	 * 
	 * @param map
	 *            The parameters map.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	protected void passNestedReports(Map<String, Object> map)
			throws ReportException {
		if (config.getNestedReports() != null) {
			Map<String, JasperReport> nested = new HashMap<String, JasperReport>();
			Iterator<String> iter = config.getNestedReports().iterator();
			while (iter.hasNext()) {
				String nestedReportKey = iter.next();
				JRReport nestedReport = JRReportFactory
						.getJRReport(nestedReportKey);
				JasperReport jnr = nestedReport.getJasperReport();
				LOGGER.info("Nested Report {}",nestedReportKey);
				nested.put(nestedReportKey, jnr);
			}
			map.put(IReportConstants.NESTED_REPORTS, nested);
		}
	}

	/**
	 * Pass the declared parameter to the fill manager.
	 * 
	 * @param map
	 *            The parameters map.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	protected void passCustomParameters(Map<String, Object> map)
			throws ReportException {
		if (customParams != null) {
			LOGGER.debug("Passing Custom Parameters");
			map.putAll(customParams);
		}
	}

	/**
	 * Pass the default parameters to the fill manager.
	 * 
	 * @param map
	 *            The parameters map.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	private void passDefaultParameters(Map<String, Object> map)
			throws ReportException {
		ReportConfig defaultConfig = getDefaultConfig();
		if (defaultConfig != null && defaultConfig.getParams() != null) {
			LOGGER.debug("Passing Default Parameters");
			map.putAll(defaultConfig.getParams());
		}
	}

	/**
	 * Returns the declared default config.
	 * 
	 * @return The declared default config.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	protected ReportConfig getDefaultConfig() throws ReportException {
		ReportConfigurationParser parser = ReportConfigurationParser
				.getInstance();
		ReportConfigurationManager configuration = parser
				.getConfigurationManager();
		return configuration.getDefaultConfig();

	}

	public void setDynamicParams(Map<String, Object> dynParams) {
		if(this.dynParams == null){
			this.dynParams = new HashMap<String,Object>();
		}
		this.dynParams = dynParams;
	}

	public Map<String, Object> getCustomParams() {
		return customParams;
	}

	public void setCustomParams(Map<String, Object> customParams) {
		this.customParams = customParams;
	}

	public int getGeneratedPages() {
		return generatedPages;
	}
	public void setGeneratedPages(int generatedPages) {
		this.generatedPages = generatedPages;
	}

	public Integer getCurrentDomain() {
		return currentDomain;
	}

	public JRReport setCurrentDomain(Integer currentDomain) {
		this.currentDomain = currentDomain;
		return this;
	}
	
}
