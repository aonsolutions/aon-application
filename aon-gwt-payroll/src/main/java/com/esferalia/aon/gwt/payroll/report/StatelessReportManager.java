package com.esferalia.aon.gwt.payroll.report;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.report.config.ReportConfig;
import com.code.aon.report.config.ReportConfigurationManager;
import com.code.aon.report.config.ReportConfigurationParser;
import com.code.aon.report.jr.JRReport;
import com.code.aon.report.jr.JRReportFactory;
import com.code.aon.ui.report.controller.ReportManager;

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
		
		return super.execute(os, reportKey, params);
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
