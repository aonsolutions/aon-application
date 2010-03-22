package com.code.aon.ui.finance.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Series;
import com.code.aon.customer.Customer;
import com.code.aon.finance.invoicing.ConsoleInvoicingFeedBack;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.InvoicingParameters;
import com.code.aon.finance.invoicing.engine.IInvoicingEngine;
import com.code.aon.finance.invoicing.engine.InvoicingEngineFactory;
import com.code.aon.finance.invoicing.engine.fee.CustomerFeeInvoicingEngine;
import com.code.aon.finance.invoicing.engine.fee.CustomerFeePreInvoicingDAO;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.strategy.IPriceStrategy;

public class PreInvoicingController implements ICollectionProvider {

	private InvoicingParameters invoicingParams;

	private IInvoicingEngine engine;

	public PreInvoicingController() {
		this.invoicingParams = new InvoicingParameters();
	}

	public InvoicingParameters getInvoicingParams() {
		return invoicingParams;
	}

	public void setInvoicingParams(InvoicingParameters invoicingParams) {
		this.invoicingParams = invoicingParams;
	}

	public void onInitialize(ActionEvent event) {
		this.invoicingParams = new InvoicingParameters();
		this.invoicingParams.setSecurityLevel(SecurityLevel.OFFICIAL);
		this.invoicingParams.setInvoiceDate(new Date());
		this.invoicingParams.setCustomer(new Customer());
		Item item = new Item();
		item.setProduct(new Product());
		this.invoicingParams.setItem(item);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		invoicingParams.setMonth(Month.getMonthByValue(calendar.get(Calendar.MONTH)));
		invoicingParams.setYear(calendar.get(Calendar.YEAR));
	}

	public void onReport(ActionEvent event) throws InvoicingException, ManagerBeanException {
		InvoicingEngineFactory.register("customerFeeEngine", new CustomerFeeInvoicingEngine());
		engine = InvoicingEngineFactory.getInvoicingEngine("customerFeeEngine");
		engine.setInvoicingDAO(new CustomerFeePreInvoicingDAO());
		engine.setInvoicingFeedBack(new ConsoleInvoicingFeedBack());
		Series series = new Series();
		series.setId("PRE");
		invoicingParams.setInvoiceSeries(series);
		invoicingParams.setInvoiceNumber(0);
		invoicingParams.setInvoiceDate(new Date());
		engine.invoice(invoicingParams);
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		return engine.getInvoicingDAO().getCollection();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return this.getCollection();
	}

	public IPriceStrategy getPriceStrategy() {
		return new InvoicePriceStrategy();
	}

}