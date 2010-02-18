package com.code.aon.ui.customer.report.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.report.jr.JRBeanCollectionDataSource;
import com.code.aon.report.jr.exporter.IJRExporterFactory;
import com.code.aon.report.jr.exporter.JRExporterFactoryManager;

public abstract class AbsReportPrintTest extends TestCase {

	private JRDataSource dataSource;
	private Map<Object, Object> parameters;

	protected abstract Map<Object, Object> getParameters();
	protected abstract Collection<?> getData();
	protected abstract InputStream getReportTemplate();
	
	public void setUp() throws Exception {
		parameters = getParameters();
		dataSource = new JRBeanCollectionDataSource(getData());
	}
	
	public JasperReport getJasperReport() throws ReportException {
		InputStream input = getReportTemplate();
		if (input == null) {
			throw new ReportException("Can not load report template!");
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

	public void testPrint() throws Exception {
		boolean initTransState = HibernateUtil.mustBeginTransaction();
		boolean initSessionState = HibernateUtil.mustCloseSession();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		try {

			File outFile = File.createTempFile("aon-report-test", ".pdf");
			
			parameters.put(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(outFile));
			
			IJRExporterFactory factory = JRExporterFactoryManager.getJRExporterFactory(getOutputFormat());
			factory.fillJRParametersMap(parameters);
			
			JasperReport jr = getJasperReport();
			JasperPrint print = JasperFillManager.fillReport(jr, parameters, dataSource);
			parameters.put(JRExporterParameter.JASPER_PRINT, print);

			JRExporter exporter = factory.getJRExporter();
			exporter.setParameters(parameters);
			exporter.exportReport();
			System.out.println( "Report created at " + outFile.getAbsolutePath() );

			HibernateUtil.commitTransaction(sessionFactoryName);
			HibernateUtil.closeSession(sessionFactoryName);
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
			try {
				HibernateUtil.rollbackTransaction(sessionFactoryName);
			} catch (DAOException e) {
				fail(e.getMessage());
			}
			if (t instanceof ReportException) {
				throw (ReportException) t;
			}
			throw new ReportException(t.getMessage(), t);
		} finally {
			if (initTransState != HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.setBeginTransaction(initTransState);
			}
			if (initSessionState != HibernateUtil.mustCloseSession()) {
				HibernateUtil.setCloseSession(initSessionState);
			}
		}
		
		
/*		
		List<JRPrintPage> pages = print.getPages();
		for (Iterator iterator = pages.iterator(); iterator.hasNext();) {
			JRPrintPage pageToTest = (JRPrintPage) iterator.next();
			List<JRPrintElement> pageElements = pageToTest.getElements();

			JRTemplatePrintText textField = null;
			for (Iterator element_iterator = pageElements.iterator(); element_iterator.hasNext();) {
				JRPrintElement element = (JRPrintElement) element_iterator.next();
				if (expectedResults.keySet().contains(element.getKey())) {
					textField = (JRTemplatePrintText) element;
					Assert.assertEquals(expectedResults.get(element.getKey()), textField.getText());
					break;
				}
			}
		}
*/		
	}

	protected OutputFormat getOutputFormat() {
		return OutputFormat.PDF;
	}

}
