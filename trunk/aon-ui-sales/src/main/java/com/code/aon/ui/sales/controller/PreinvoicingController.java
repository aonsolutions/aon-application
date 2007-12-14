package com.code.aon.ui.sales.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.invoicing.ConsoleInvoicingFeedBack;
import com.code.aon.finance.invoicing.IInvoicingEngine;
import com.code.aon.finance.invoicing.InvoicePriceStrategy;
import com.code.aon.finance.invoicing.InvoicingEngineFactory;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.InvoicingParameters;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.sales.CustomerFeeInvoicingEngine;
import com.code.aon.sales.CustomerFeePreInvoicingDAO;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.report.OutputFormat;
import com.code.aon.ui.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class PreinvoicingController implements ICollectionProvider{

	private InvoicingParameters invoicingParams;
	
	private IInvoicingEngine engine;
	
	public PreinvoicingController() {
		this.invoicingParams = new InvoicingParameters();
	}

	public InvoicingParameters getInvoicingParams() {
		return invoicingParams;
	}

	public void setInvoicingParams(InvoicingParameters invoicingParams) {
		this.invoicingParams = invoicingParams;
	}
	
	@SuppressWarnings("unused")
	public void onInitialize(MenuEvent event){
		this.invoicingParams = new InvoicingParameters();
		this.invoicingParams.setSecurityLevel(SecurityLevel.OFFICIAL);
		this.invoicingParams.setInvoiceDate(new Date());
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		invoicingParams.setMonth(Month.getMonthByValue(calendar.get(Calendar.MONTH)));
		invoicingParams.setYear(calendar.get(Calendar.YEAR));
	}

	@SuppressWarnings("unused")
	public void onReport(ActionEvent event) throws InvoicingException, ManagerBeanException{
		InvoicingEngineFactory.register("customerFeeEngine", new CustomerFeeInvoicingEngine());
		engine = InvoicingEngineFactory.getInvoicingEngine("customerFeeEngine");
		engine.setInvoicingDAO(new CustomerFeePreInvoicingDAO());
		engine.setInvoicingFeedBack(new ConsoleInvoicingFeedBack());
		invoicingParams.setSeries("PRE");
		invoicingParams.setNumber(0);
		invoicingParams.setInvoiceDate(new Date());
		engine.invoice(invoicingParams);
	}
	
	@SuppressWarnings("unchecked")
	public Collection getCollection(){
		return engine.getInvoicingDAO().getCollection();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return this.getCollection();
	}

	public String onExecute() throws ReportException, DAOException{
		ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("preInvoicing");
        manager.setOutputFormat(OutputFormat.PDF);
        String outcome = manager.onExecute();
        /* PARA ELIMINAR LA COLECCION GENERADA DE MEMORIA */
        engine.setInvoicingDAO(null);
        engine = null;
        return outcome;
	}
	
	public IPriceStrategy getPriceStrategy(){
		return new InvoicePriceStrategy();
	}

}