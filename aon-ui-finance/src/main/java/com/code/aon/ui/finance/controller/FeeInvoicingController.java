package com.code.aon.ui.finance.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.IProgression;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Series;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.IInvoicingFeedBack;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.InvoicingParameters;
import com.code.aon.finance.invoicing.ProgressionInvoicingFeedBack;
import com.code.aon.finance.invoicing.engine.IInvoicingEngine;
import com.code.aon.finance.invoicing.engine.InvoicingEngineFactory;
import com.code.aon.finance.invoicing.engine.fee.CustomerFeeInvoicingDAO;
import com.code.aon.finance.invoicing.engine.fee.CustomerFeeInvoicingEngine;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class FeeInvoicingController implements IProgression, IFinanceConstants, IFinanceMessages {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeeInvoicingController.class.getName());

	private InvoicingParameters invoicingParams;
	private IInvoicingEngine engine;
	private AccountEntryInvoiceWriter accountWriter;
	private IInvoicingFeedBack feedBack;

	private boolean progressionPanelVisible;
	private boolean progressionEnabled;
	private Long progressionValue;
	private boolean progressStart;
	private boolean recording;
	private boolean redirect;
	private int invoicesToRecord;
	private int recordingInvoice;

	public InvoicingParameters getParams() {
		return invoicingParams;
	}

	public void setParams(InvoicingParameters invoicingParams) {
		this.invoicingParams = invoicingParams;
	}

	public IInvoicingEngine getEngine() throws InvoicingException {
		if (engine == null) {
			InvoicingEngineFactory.register(InvoicingEngineFactory.CUSTOMER_FEE_ENGINE_KEY, new CustomerFeeInvoicingEngine());
			engine = InvoicingEngineFactory.getInvoicingEngine(InvoicingEngineFactory.CUSTOMER_FEE_ENGINE_KEY);
		}
		return engine;
	}

	public AccountEntryInvoiceWriter getAccountEntryInvoiceWriter() {
		if (accountWriter == null) {
			accountWriter = new AccountEntryInvoiceWriter();
		}
		return accountWriter;
	}

	private IInvoicingFeedBack getInvoicingFeedBack() {
		if (feedBack == null) {
			feedBack = new ProgressionInvoicingFeedBack(); 
		}
		return feedBack;
	}

	public void onInitialize(ActionEvent event) throws ManagerBeanException {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());

		InvoicingParameters params = new InvoicingParameters();
		params.setCustomer(new Customer());
		params.setItem(new Item());
		params.getItem().setProduct(new Product());
		params.setMonth(Month.getMonthByValue(calendar.get(Calendar.MONTH)));
		params.setYear(calendar.get(Calendar.YEAR));
		params.setSecurityLevel(SecurityLevel.OFFICIAL);
		params.setInvoiceNumber(obtainMaxNumber(null));
		params.setInvoiceDate(new Date());
		params.setInvoiceRecordable(true);
		setParams(params);

		setProgressionPanelVisible(false);
		setProgressionEnabled(false);
		setProgressionValue(-1L);
		progressStart = false;
		recording = false;
		invoicesToRecord = 0;
		recordingInvoice = 0;
	}

	public void onInvoiceSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		Series series = (Series) event.getNewValue();
		getParams().setInvoiceNumber(obtainMaxNumber(series));
		getParams().setSecurityLevel(obtainSeriesSecurityLevel(series));
	}

	private int obtainMaxNumber(Series series) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		if (series == null) {
			criteria.addNullExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_SERIES));
		} else {
			criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_SERIES), series.getId());
		}
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_TYPE), InvoiceType.SALES);
		Projection projection = Projection.max(invoiceBean.getFieldName(IFinanceAlias.INVOICE_NUMBER));
		Object value = invoiceBean.getUniqueResult(projection, criteria);
		if (value != null) {
			return ((Integer) value).intValue() + 1;
		}
		return 1;
	}

	private SecurityLevel obtainSeriesSecurityLevel(Series series) {
		if (series != null) {
			return series.getSecurityLevel();
		}
		return null;
	}

	public void onInvoice(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			progressStart = true;
			recording = false;
			setProgressionEnabled(true);

			getEngine().setInvoicingDAO(new CustomerFeeInvoicingDAO());
			getEngine().setInvoicingFeedBack(getInvoicingFeedBack());
			
			HibernateUtil.beginTransaction(sessionName);
			getEngine().invoice(getParams());
			HibernateUtil.getSession(sessionName).flush();					
			HibernateUtil.commitTransaction(sessionName);

			Collection<Invoice> invoicedList = getEngine().getInvoicingDAO().getCollection();
			if (invoicedList.size() > 0) {
				if (getParams().isInvoiceRecordable()) {
					HibernateUtil.beginTransaction(sessionName);
					recording = true;
					invoicesToRecord = invoicedList.size();
					recordingInvoice = 0;
					Iterator<Invoice> iter = invoicedList.iterator();
					while (iter.hasNext()) {
						Invoice invoice = iter.next();
						getAccountEntryInvoiceWriter().recordAndUpdateInvoice(invoice);
						recordingInvoice++;
					}
					HibernateUtil.getSession(sessionName).flush();
					HibernateUtil.commitTransaction(sessionName);
				}

				Invoice firstInvoice = (Invoice)invoicedList.toArray()[0];
				Invoice lastInvoice = (Invoice)invoicedList.toArray()[invoicedList.size()-1];
				IController invoiceController = FormUtil.getController(SALE_INVOICE_CONTROLLER_NAME);
				Criteria criteria = new Criteria();
				criteria.addBetweenExpression(invoiceController.getFieldName(IFinanceAlias.INVOICE_ID), firstInvoice.getId(), lastInvoice.getId());
				invoiceController.setCriteria(criteria);
				invoiceController.onSearch(null);
				setRedirect(true);
			} else {
				setRedirect(false);
				AonUtil.addInfoMessageFromBundle(BUNDLE_KEY, NO_INVOICE_KEY);
			}
		} catch (Exception e) {
			setRedirect(false);
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg =  "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			String msg =  "Error invoicing fees. " + e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
			setProgressionPanelVisible(false);
			setProgressionEnabled(false);
			setProgressionValue(101L);
		}
	}

	public String invoice() {
		return isRedirect()?"saleInvoice_list":null;	
	}

	public void onShowPanel(ActionEvent event) {
		setProgressionPanelVisible(true);
		setProgressionEnabled(true);
		setProgressionValue(-1L);
		progressStart = false;
		recording = false;
		invoicesToRecord = 0;
		recordingInvoice = 0;
	}
	public void onClosePanel(ActionEvent event) {
		setProgressionPanelVisible(false);
		setProgressionEnabled(false);
		setProgressionValue(-101L);
		progressStart = false;
		recording = false;
		invoicesToRecord = 0;
		recordingInvoice = 0;
	}

	public boolean isProgressionPanelVisible() {
		return progressionPanelVisible;
	}

	public void setProgressionPanelVisible(boolean progressionPanelVisible) {
		this.progressionPanelVisible = progressionPanelVisible;
	}

	@Override
	public boolean isProgressionEnabled() {
		return progressionEnabled;
	}
	@Override
	public void setProgressionEnabled(boolean enabled) {
		this.progressionEnabled = enabled;
	}

	@Override
	public Long getProgressionCurrentValue() {
		if (progressStart) {
			if (!recording) {
				int row = getInvoicingFeedBack().getCurrentRow();
				int count = getInvoicingFeedBack().getRowCount();
				if (count > 0) {
					int pro = (int) CommonUtil.round(row * 100 / count);
					if (getParams().isInvoiceRecordable()) {
						pro = pro / 2;
					}
					setProgressionValue(new Long(pro));
				}
			} else {
				if (invoicesToRecord > 0) {
					int pro = (int) CommonUtil.round(((recordingInvoice * 100 / invoicesToRecord) / 2)+50);
					setProgressionValue(new Long(pro));
				}
			}
		}
		return getProgressionValue();
	}

	@Override
	public void setProgressionCurrentValue(Long currentValue) {
	}

	public Long getProgressionValue() {
		return progressionValue;
	}

	public void setProgressionValue(Long progressionValue) {
		this.progressionValue = progressionValue;
	}

	public boolean isRedirect() {
		return redirect;
	}

	public void setRedirect(boolean redirect) {
		this.redirect = redirect;
	}

}