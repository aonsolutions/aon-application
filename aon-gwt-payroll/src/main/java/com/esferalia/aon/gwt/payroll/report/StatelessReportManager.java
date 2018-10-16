package com.esferalia.aon.gwt.payroll.report;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.code.aon.report.IReportDynamicParamsProvider;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.report.config.ReportConfig;
import com.code.aon.report.config.ReportConfigurationManager;
import com.code.aon.report.config.ReportConfigurationParser;
import com.code.aon.report.jr.JRReport;
import com.code.aon.report.jr.JRReportFactory;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

public class StatelessReportManager extends ReportManager {
	
	

	public StatelessReportManager() {
		setLocale(new Locale("es", "ES"));
		setOutputFormat(OutputFormat.PDF);
		setBundle(ResourceBundle.getBundle("com.code.aon.common.i18n.messages", getLocale()));
		
	}
	
	@Override
	public String execute(OutputStream os, String reportKey, Map<Object, Object>... params) throws ReportException {
		
		ReportConfigurationParser parser = ReportConfigurationParser
				.getInstance();
		ReportConfigurationManager rcm = parser.getConfigurationManager();
		ReportConfig config = rcm.getReport(reportKey);
		JRReportFactory.register(reportKey, new StatelessJRReport(config, params));
		
		return executeImpl(os, reportKey, params);
	}
	
	/**
	 * Runs the report. Obtains a
	 * <code>com.code.aon.ui.report.jr.JRReport</code> calling the
	 * <code>JRReportFactory.getJRReport(getReportKey())</code> method. Also,
	 * finalizes the reponse calling the
	 * <code>FacesContext.getCurrentInstance().responseComplete()</code>.
	 * 
	 * @return The outcome (- null - because this method finalizes the reponse).
	 * @throws ReportException
	 *             If an error ocurred.
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public String executeImpl(OutputStream os, String reportKey, Map<Object,Object>... params) throws ReportException {

		JRReport report = null;
		try {
			report = JRReportFactory.getJRReport(reportKey);
			Criteria criteria = null;
			Collection<?> collection = getCollection(report);

			String dp = report.getReportConfig().getDynamicParamsProvider();
			if (dp != null) {
				IReportDynamicParamsProvider dpp = (IReportDynamicParamsProvider) AonUtil
						.getRegisteredBean(dp);
				Map<String, Object> dynParams = dpp.getDynamicParamsMap();
				report.setDynamicParams(dynParams);
			}

			String out = report.run(getOutputFormat(), os, getBundle(), getLocale(), criteria, collection, params);
			if (out == null) {
				out = Integer.toString(report.getGeneratedPages());
			}
			return out;
		} catch (Throwable t) {
			t.printStackTrace();
			AonUtil.addFatalMessage("Report Error:" + t.getMessage());
			if (t instanceof ReportException) {
				throw (ReportException) t;
			}
			throw new ReportException(t.getMessage(), t);
		} finally {
			if ( report != null ) {
				report.setCustomParams(null);	
			}			
		}
	}
	
	
	/**
	 * Obtains the <code>java.util.Collection</code> that will be passed to
	 * method <code>com.code.aon.common.IFinderBean.getList()</code>.
	 * Otherwise the litaral must be a valid ICollectionProvider implementation
	 * class.
	 * 
	 * @param report
	 *            The report to be executed.
	 * @return The collection.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	private Collection<?> getCollection(JRReport report) throws ReportException {
		Collection<?> collection = null;
		ReportConfig config = report.getReportConfig();
		String provider = config.getCollectionProvider();
		ICollectionProvider collectionProvider = getCollectionProvider();
		if ((collectionProvider == null) && (provider != null)) {
			try {
				Class<?> collectionProviderClass = Class.forName(provider);
				collectionProvider = (ICollectionProvider) collectionProviderClass
						.newInstance();
			} catch (Throwable th) {
				throw new ReportException(th.getMessage(), th);
			}
		}
		if ( (collection == null) && (collectionProvider != null) ) {
			try {
				collection = collectionProvider.getCollection(config.isForceRefresh());
			} catch (ManagerBeanException e) {
				throw new ReportException(e.getMessage(), e);
			}
		}
		return collection;
	}

	
	private static class StatelessJRReport extends JRReport {

		
		public StatelessJRReport(ReportConfig reportConfig, Map<Object,Object> params []) throws ReportException {
			super(reportConfig);
			setupCustomParams(params);
		}

		public JasperReport getJasperReport() throws ReportException {
			InputStream input = JRReport.class.getResourceAsStream(getReportConfig().getTemplate());
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
		

		private void setupCustomParams( Map<Object, Object> params []) {
			
			ReportConfig reportConfig = getReportConfig();
			Map<String,Object> configParams =  reportConfig.getParams();
			if ( configParams == null ) 
				return;
			Map<String,Object> customParams =  new HashMap<String, Object>();
			
			for ( Entry<String,Object> configParam: configParams.entrySet()) {
				String key = configParam.getKey();
				Object value = configParam.getValue();
				try {
					Object customValue = get(value, params);
					customParams.put(key, customValue);
				} catch ( NoSuchElementException e ) {
					System.out.println("NoSuchElementException :" + key + " = " + value );
				}
			}
			
			setCustomParams(customParams);
		}
		
		private static Object get(Object key, Map<Object, Object> maps []) {
			for (Map<Object, Object>  map: maps ) {
				Object value = map.get(key);
				if ( value != null )
					return value;
			}
			throw new NoSuchElementException();
		}
		
	}
	

}
