package com.code.aon.ui.finance.controller;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import javax.faces.event.ActionEvent;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Series;
import com.code.aon.finance.invoicing.ConsoleInvoicingFeedBack;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.InvoicingParameters;
import com.code.aon.finance.invoicing.engine.IInvoicingEngine;
import com.code.aon.finance.invoicing.engine.InvoicingEngineFactory;
import com.code.aon.finance.invoicing.engine.fee.CustomerFeeInvoicingEngine;
import com.code.aon.finance.invoicing.engine.fee.CustomerFeePreInvoicingDAO;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ui.config.util.UserUtils;

public class PreInvoicingController implements ICollectionProvider, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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

	public void onInitialize(ActionEvent event) throws ManagerBeanException {
		this.invoicingParams = new InvoicingParameters();
		invoicingParams.initializeParams();
		invoicingParams.setScopes(UserUtils.getInstance().getCurrentUserScopes());
	}

	public void onReport(ActionEvent event) throws InvoicingException, ManagerBeanException {
		InvoicingEngineFactory.register("customerFeeEngine", new CustomerFeeInvoicingEngine());
		engine = InvoicingEngineFactory.getInvoicingEngine("customerFeeEngine");
		engine.setInvoicingDAO(new CustomerFeePreInvoicingDAO());
		engine.setInvoicingFeedBack(new ConsoleInvoicingFeedBack());
		Series series = new Series();
		series.setCode("PRE");
		Calendar calendar = new GregorianCalendar();
		calendar.set(invoicingParams.getYear(), invoicingParams.getMonth().ordinal(), 1);
		invoicingParams.setInvoiceSeries(series);
		invoicingParams.setInvoiceNumber(0);
		invoicingParams.setInvoiceDate(calendar.getTime());
		engine.invoice(invoicingParams);
	}

	@Override
	public Collection<?> getCollection() {
		return engine.getInvoicingDAO().getCollection();
	}

	@Override
	public Collection<?> getCollection(boolean forceRefresh) throws ManagerBeanException {
		return this.getCollection();
	}

	public IPriceStrategy getPriceStrategy() {
		return new InvoicePriceStrategy();
	}

}