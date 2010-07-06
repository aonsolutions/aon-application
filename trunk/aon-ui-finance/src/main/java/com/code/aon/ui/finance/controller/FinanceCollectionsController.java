package com.code.aon.ui.finance.controller;


import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.ui.util.AonUtil;

/**
 * Collections controller
 * 
 * @author Consulting & Development. Joseba Urkiri - 25-may-2006
 * 
 */
public class FinanceCollectionsController {

	private List<SelectItem> billingPeriods;
	private List<SelectItem> creditorStatuses;
	private List<SelectItem> financeTrackingTypes;
	private List<SelectItem> financeBatchStatus;
	private List<SelectItem> financeBatchTypes;
	private List<SelectItem> financeStatuses;
	private List<SelectItem> invoiceTypes;
	private List<SelectItem> invoiceStatuses;
	private List<Invoice> noFinanceInvoiceList;
	private List<Invoice> notEqualAmountInvoiceList;
	private Date fromDate;
	private Date toDate;
	private IPriceStrategy priceStrategy;
	private String reportName;
	private static final String bundle = "financeBundle";
	
	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public List<Invoice> getNotEqualAmountInvoiceList() throws ManagerBeanException {
	
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_STATUS), InvoiceStatus.PENDING);
		if (this.getFromDate()!=null){
			criteria.addGreaterThanOrEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE),this.fromDate);
		}
		if (this.getToDate()!=null){
			criteria.addLessThanOrEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE),this.toDate);
		}
		Iterator<ITransferObject> iterator = invoiceBean.getList(criteria).iterator();
		notEqualAmountInvoiceList = new LinkedList<Invoice>();
		while (iterator.hasNext()) {
			Double invoiceTotal =0.0;
			Double financeTotal=0.0;
			Invoice inv = (Invoice)iterator.next();
			if (InvoiceType.UNDEDUCTIBLE == inv.getType()) {
				invoiceTotal=getPriceStrategy().getTaxableBase((ICalculableContainer)inv);
			}
			else{
				invoiceTotal =getPriceStrategy().getTotalPrice((ICalculableContainer)inv, (ITaxInfo)inv);
			}
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Criteria cri = new Criteria();
			cri.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_REFERENCE_CODE), inv.getReferenceCode());
			Iterator<ITransferObject> iter = financeBean.getList(cri).iterator();
			while(iter.hasNext()) {
				Finance finance = (Finance)iter.next();
				financeTotal += finance.getAmount();
			}
			if(!invoiceTotal.equals(financeTotal)){
				notEqualAmountInvoiceList.add(inv);
			}
		}
		if(notEqualAmountInvoiceList.size()==0){
			AonUtil.addInfoMessage("No hay facturas con vencimientos Erroneos");
		}
		setReportName(AonUtil.getMessage(bundle, "finance_invoice_checking_module_wrong_finance"));	
		return notEqualAmountInvoiceList;
	}

	public void setNotEqualAmountInvoiceList(List<Invoice> notEqualAmountInvoiceList) {
		this.notEqualAmountInvoiceList = notEqualAmountInvoiceList;
	}

	public List<Invoice> getNoFinanceInvoiceList() {
		String dateCriteria ="";
		if(this.getFromDate()!=null){
			dateCriteria=" Invoice.issueDate >= '"+ new java.sql.Date(this.getFromDate().getTime()).toString()	+ "' AND";
		}
		if(this.getToDate()!=null){
			dateCriteria=dateCriteria+" Invoice.issueDate <= '"+ new java.sql.Date(this.getToDate().getTime()).toString() +"' AND " ;
		}
		String select = "select Invoice "
			+ "from Invoice as Invoice "
			+ "where " 
			+  dateCriteria 
			+" Invoice.referenceCode not in (select Finance.invoice.referenceCode from Finance as Finance)";
	Session session = HibernateUtil.getSession(HibernateUtil
			.getSessionFactoryName());
	Query query = session.createQuery(select);
	noFinanceInvoiceList = query.list();
	if(noFinanceInvoiceList.size()==0){
		AonUtil.addErrorMessage("No hay facturas sin vencimientos ");
	}
	setReportName(AonUtil.getMessage(bundle, "finance_invoice_checking_module_no_finance"));
	return noFinanceInvoiceList;
	}

	public void setNoFinanceInvoiceList(List<Invoice> noFinanceInvoiceList) {
		this.noFinanceInvoiceList = noFinanceInvoiceList;
	}

	public List<SelectItem> getBillingPeriods() {
		if (billingPeriods == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			billingPeriods = new LinkedList<SelectItem>();
			for( BillingPeriod period : BillingPeriod.values() ) {
				String name = period.getName(locale);
				SelectItem item = new SelectItem(period, name);
				billingPeriods.add(item);			
			}
		}
		return billingPeriods;
	}

	public List<SelectItem> getCreditorStatuses() {
		if (creditorStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			creditorStatuses = new LinkedList<SelectItem>();
			for (CreditorStatus status:CreditorStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				creditorStatuses.add(item);
			}
		}
		return creditorStatuses;
	}

	public List<SelectItem> getFinanceTrackingTypes() {
		if (financeTrackingTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			financeTrackingTypes = new LinkedList<SelectItem>();
			for (FinanceTrackingType type:FinanceTrackingType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				financeTrackingTypes.add(item);
			}
		}
		return financeTrackingTypes;
	}

	public List<SelectItem> getFinanceBatchStatus() {
		if (financeBatchStatus == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			financeBatchStatus = new LinkedList<SelectItem>();
			for (FinanceBatchStatus type:FinanceBatchStatus.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				financeBatchStatus.add(item);
			}
		}
		return financeBatchStatus;
	}

	public List<SelectItem> getFinanceBatchTypes() {
		if (financeBatchTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			financeBatchTypes = new LinkedList<SelectItem>();
			for (FinanceBatchType type:FinanceBatchType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				financeBatchTypes.add(item);
			}
		}
		return financeBatchTypes;
	}

	public List<SelectItem> getFinanceStatuses() {
		if (financeStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			financeStatuses = new LinkedList<SelectItem>();
			for (FinanceStatus type:FinanceStatus.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				financeStatuses.add(item);
			}
		}
		return financeStatuses;
	}

	public List<SelectItem> getInvoiceTypes() {
		if (invoiceTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			invoiceTypes = new LinkedList<SelectItem>();
			for (InvoiceType type: InvoiceType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				invoiceTypes.add(item);
			}
		}
		return invoiceTypes;
	}

	public List<SelectItem> getInvoiceStatuses() {
		if (invoiceStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			invoiceStatuses = new LinkedList<SelectItem>();
			for (InvoiceStatus type:InvoiceStatus.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				invoiceStatuses.add(item);
			}
		}
		return invoiceStatuses;
	}
	
}