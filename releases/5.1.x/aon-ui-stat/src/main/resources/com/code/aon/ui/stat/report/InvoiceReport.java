package com.code.aon.ui.stat.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.report.jr.JRReport;
import com.code.aon.report.jr.JRReportFactory;
import com.code.aon.stat.Stat;
import com.code.aon.ui.stat.controller.StatEngineController;

public class InvoiceReport {

	public static void main(String[] args) throws ReportException, ManagerBeanException, FileNotFoundException {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		HibernateUtil.setCloseSession(false);
		HibernateUtil.startSession(sessionFactoryName);
		File file = new File("/tmp/report.pdf");
		OutputStream os = new FileOutputStream(file);
		String reportKey = "yearStatsList"; 
		JRReport report = JRReportFactory.getJRReport(reportKey);
		//IPriceStrategy priceStrategy = new InvoicePriceStrategy();
		Map<String, Object> customParams = new HashMap<String, Object>();
		//customParams.put("PriceStrategy", priceStrategy);
		report.setCustomParams(customParams);
		//IManagerBean bean = BeanManager.getManagerBean(Stat.class);
		Criteria criteria = new Criteria();
		StatEngineController st= new StatEngineController();		
		ActionEvent e = null;
		st.onReset(e);
		st.onSaleType(e);
		st.onAnualStats(e);
		ResourceBundle bundle = ResourceBundle.getBundle("com.code.aon.ui.stat.i18n.report");
		report.run(OutputFormat.PDF, os, bundle, criteria , null);
		HibernateUtil.closeSession(sessionFactoryName);
	}
	
}
