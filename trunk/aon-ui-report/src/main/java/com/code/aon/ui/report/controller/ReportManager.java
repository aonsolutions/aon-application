package com.code.aon.ui.report.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ICriteriaProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.code.aon.report.IReportDynamicParamsProvider;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.report.config.ReportConfig;
import com.code.aon.report.jr.JRReport;
import com.code.aon.report.jr.JRReportFactory;
import com.code.aon.ui.util.AonUtil;

/**
 * Bean Manager for running reports.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * @since 1.0
 * 
 */
public class ReportManager {

	/**
	 * Obtains a suitable <code>Logger</code>.
	 */
	private static Logger LOGGER = Logger.getLogger(ReportManager.class.getName());

	/**
	 * Output format of the report.
	 */
	private OutputFormat outputFormat;

	/**
	 * Report idetifier.
	 */
	private String reportKey;

	/**
	 * Returns the report identifier.
	 * 
	 * @return The report identifier.
	 */
	public String getReportKey() {
		return reportKey;
	}

	/**
	 * Sets the report idetntifier.
	 * 
	 * @param reportKey
	 *            The report idetntifier.
	 */
	public void setReportKey(String reportKey) {
		this.reportKey = reportKey;
	}

	/**
	 * Returns the output format of the report.
	 * 
	 * @return The output format of the report.
	 */
	public OutputFormat getOutputFormat() {
		return outputFormat;
	}

	/**
	 * Sets the output format of the report.
	 * 
	 * @param outputFormat
	 *            The output format of the report.
	 */
	public void setOutputFormat(OutputFormat outputFormat) {
		this.outputFormat = outputFormat;
	}

	/**
	 * Returns an array of <code>javax.faces.model.SelectItem</code> of
	 * available output formats.
	 * 
	 * @return An array of available output formats.
	 */
	public SelectItem[] getOutputFormats() {
		SelectItem[] items = {
				new SelectItem(OutputFormat.PDF, OutputFormat.PDF.getType()),
				new SelectItem(OutputFormat.HTML, OutputFormat.HTML.getType()),
				new SelectItem(OutputFormat.XLS, OutputFormat.XLS.getType()),
				// new SelectItem(OutputFormat.XML, OutputFormat.XML.getType()),
				new SelectItem(OutputFormat.CSV, OutputFormat.CSV.getType()),
				new SelectItem(OutputFormat.RTF, OutputFormat.RTF.getType()),
				new SelectItem(OutputFormat.TXT, OutputFormat.TXT.getType()) };
		return items;
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
	public String onExecute() throws ReportException {
		ensureParams();
		String out = execute(getOutputStream(), getReportKey());
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.responseComplete();
		return out;
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
	public String execute( File file, String reportKey ) throws ReportException {
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			return execute(out, reportKey);
		} catch ( IOException e ) {
			throw new ReportException(e.getMessage(), e);	
		} finally {
			if ( out != null ) {
				try {
					out.close();
				} catch (IOException e) {
					throw new ReportException(e.getMessage(), e);
				}
			}
		}
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
	public String execute( OutputStream os, String reportKey ) throws ReportException {

		boolean initTransState = HibernateUtil.mustBeginTransaction();
		boolean initSessionState = HibernateUtil.mustCloseSession();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		try {
			ensureOutputFormat();
			JRReport report = JRReportFactory.getJRReport(reportKey);
			resolveCustomParameters(report);
			Criteria criteria = getCriteria(report);
			Collection collection = getCollection(report);

			HibernateUtil.startSession(sessionFactoryName);
			HibernateUtil.beginTransaction(sessionFactoryName);

			String dp = report.getReportConfig().getDynamicParamsProvider();
			LOGGER.info(dp);
			if (dp != null) {
				IReportDynamicParamsProvider dpp = (IReportDynamicParamsProvider) AonUtil.getRegisteredBean(dp);
				Map<String, Object> dynParams = dpp.getDynamicParamsMap();
				LOGGER.info("" + dynParams.size());
				report.setDynamicParams(dynParams);
				LOGGER.info("Dynamic params set!");
			}

			String out = report.run(outputFormat, os, getBundle(), criteria, collection);
			report.setCustomParams(null);
			HibernateUtil.commitTransaction(sessionFactoryName);
			HibernateUtil.closeSession(sessionFactoryName);
			return out;
		} catch (Throwable t ){
		    try {
				HibernateUtil.rollbackTransaction(sessionFactoryName);
			} catch (DAOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		    AonUtil.addFatalMessage("Report Error:" + t.getMessage());
		    return null;
		} finally {
			if (initTransState != HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.setBeginTransaction(initTransState);
			}
			if (initSessionState != HibernateUtil.mustCloseSession()) {
				HibernateUtil.setCloseSession(initSessionState);
			}
		}
	}
	
	private void ensureParams() throws ReportException{
		ensureReportKey();
		ensureOutputFormat();
	}

	private void ensureReportKey() throws ReportException {
		String key = getReportKey();
		if (key == null) {
			Map<String, String> parameters = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			key = parameters.get("reportKey");
			if (key == null) {
				throw new ReportException("Empty reportKey!");
			}
			setReportKey(key);
		}
	}

	private void ensureOutputFormat() throws ReportException {
		OutputFormat f = getOutputFormat();
		if (f == null) {
			Map<String, String> parameters = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			String of = parameters.get("outputFormat");
			if (of == null) {
				setOutputFormat(OutputFormat.PDF);
			} else {
				OutputFormat ouf = OutputFormat.get(of);
				if (ouf == null) {
					throw new ReportException("Invalid outputFormat '"+of+"'!");
				}
				setOutputFormat(ouf);
			}
		}
	}

	/**
	 * Obtains the OutputStream where the report will be writen. <br>
	 * <code>
	 * 		FacesContext ctx = FacesContext.getCurrentInstance();<br>
	 *		ExternalContext ec = ctx.getExternalContext();<br>
	 *		HttpServletResponse res = (HttpServletResponse) ec.getResponse();<br>
	 *		return res.getOutputStream();
	 * </code>
	 * 
	 * @return The OutputStream where the report will be writen.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	private OutputStream getOutputStream() throws ReportException {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ec = ctx.getExternalContext();
			HttpServletResponse res = (HttpServletResponse) ec.getResponse();
			LOGGER.info("ContentType " + getOutputFormat().getMimeType());
			res.setContentType(getOutputFormat().getMimeType());
			String contentDisposition = getContentDispositionHeader();
			if (contentDisposition != null) {
				res.setHeader("Content-Disposition", contentDisposition);
			}
			return res.getOutputStream();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new ReportException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the value of the "Content-Disposition" HTTP header.
	 * 
	 * @return The value of the "Content-Disposition" HTTP header.
	 */
	private String getContentDispositionHeader() {
		if (getOutputFormat() == OutputFormat.XLS) {
			return "attachment; filename=\"report.xls\";";
		} else if (getOutputFormat() == OutputFormat.TXT) {
			return "attachment; filename=\"report.txt\";";
		} else if (getOutputFormat() == OutputFormat.RTF) {
			return "attachment; filename=\"report.rtf\";";
		}
		return null;
	}

	/**
	 * Obtains the ResourceBundle needed for the report. <br>
	 * <code>
	 * 		FacesContext ctx = FacesContext.getCurrentInstance();<br>
	 *		Application app = ctx.getApplication();<br>
	 *		String baseName = app.getMessageBundle();<br>
	 *		Locale locale = ctx.getViewRoot().getLocale();<br>
	 *		return ResourceBundle.getBundle(baseName, locale);<br>
	 * </code>
	 * 
	 * @return The ResourceBundle needed for the report.
	 */
	private ResourceBundle getBundle() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Application app = ctx.getApplication();
		String baseName = app.getMessageBundle();
		Locale locale = ctx.getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
		return bundle;
	}

	/**
	 * Obtains the <code>com.aon.ql.Criteria</code> that will be passed to
	 * method <code>com.code.aon.common.IFinderBean.getList()</code>. If the
	 * criteria provider declared int the report config begin with the "#"
	 * character, this method will try to recover a
	 * <code>com.code.aon.ui.AbstractController</code> from the Faces context.
	 * Otherwise the litaral must be a valid ICriteriaProvider implementation
	 * class.
	 * 
	 * @param report
	 *            The report to be executed.
	 * @return The criteria.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	private Criteria getCriteria(JRReport report) throws ReportException {
		ReportConfig config = report.getReportConfig();
		String provider = config.getCriteriaProvider();
		try {
			if (provider != null) { // Criteria provider is EL expression ina a
				// faces context.
				if (provider.startsWith("#")) {
					String providerName = strip(provider);
					ICriteriaProvider crpr = (ICriteriaProvider) AonUtil.getRegisteredBean(providerName);
					return crpr.getCriteria();
				}
				// Criteria provider is a class.
				Class criteriaProviderClass = Class.forName(provider);
				ICriteriaProvider criteriaProvider = (ICriteriaProvider) criteriaProviderClass
						.newInstance();
				return criteriaProvider.getCriteria();

			}
			return null;
		} catch (ManagerBeanException e) {
			throw new ReportException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			throw new ReportException(e.getMessage(), e);
		} catch (InstantiationException e) {
			throw new ReportException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new ReportException(e.getMessage(), e);
		}
	}

	/**
	 * Obtains the <code>java.util.Collection</code> that will be passed to
	 * method <code>com.code.aon.common.IFinderBean.getList()</code>. If the
	 * collection provider declared int the report config begin with the "#"
	 * character, this method will try to recover a
	 * <code>com.code.aon.ui.AbstractController</code> from the Faces context.
	 * Otherwise the litaral must be a valid ICollectionProvider implementation
	 * class.
	 * 
	 * @param report
	 *            The report to be executed.
	 * @return The collection.
	 * @throws ReportException
	 *             If an error ocurred.
	 */
	private Collection getCollection(JRReport report) throws ReportException {
		ReportConfig config = report.getReportConfig();
		String provider = config.getCollectionProvider();
		if (provider != null) {
			try {			
				if (provider.startsWith("#")) {
					String providerName = strip(provider);
					Object c = AonUtil.getRegisteredBean(providerName);
					if (c instanceof ICollectionProvider) {
						ICollectionProvider crpr = (ICollectionProvider) c;
						return crpr.getCollection(config.isForceRefresh());
					} else if (c instanceof Collection) {
						return (Collection) c;
					}
				} else {
					// Collection provider is a class.
					ICollectionProvider collectionProvider = null;
					try {
						Class collectionProviderClass = Class.forName(provider);
						collectionProvider = (ICollectionProvider) collectionProviderClass.newInstance();
						return collectionProvider.getCollection(config.isForceRefresh());
					} catch ( Throwable th ) {
						throw new ReportException(th.getMessage(), th);		
					}					
				}
			} catch (ManagerBeanException e) {
				throw new ReportException(e.getMessage(), e);
			}				
		}
		return null;
	}

	private void resolveCustomParameters(JRReport report)
			throws ReportException {
		ReportConfig config = report.getReportConfig();
		if (config.getParams() != null) {
			LOGGER.fine("Passing Custom Parameters");
			Map<String, Object> map = new HashMap<String, Object>();
			Iterator<Object> iter = config.getParams().keySet().iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				String value = (String) config.getParams().get(key);
				if (value.startsWith("#")) {
					String controllerName = value.substring(
							value.indexOf("{") + 1, value.indexOf("."));
					String methodName = value.substring(value.indexOf(".") + 1,
							value.indexOf("}"));
					Object o = AonUtil.getRegisteredBean( controllerName );
					try {
						Method m = o.getClass().getMethod(methodName, new Class[0]);
						Object obj = m.invoke(o, new Object[0]);
						map.put(key, obj);
					} catch (Throwable th) {
						LOGGER.log(Level.SEVERE, "Error resolving expression " + value + ". " + th.getMessage(), th);
					}
				}
			}
			report.setCustomParams(map);
		}
	}
	
	private String strip( String expression ) {
		int start = StringUtils.indexOf(expression, "#{" );
		int end = StringUtils.lastIndexOf(expression, '}' );
		if ( (start != -1) && (end != -1) ) {
			return StringUtils.substring( expression, start+2, end);
		}
		return expression;
	}
	
}
