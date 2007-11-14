package com.code.aon.ui.finance.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;

public class InvoicePrintController extends BasicController implements ICollectionProvider {
	
	private static final Logger LOGGER = Logger.getLogger(InvoicePrintController.class.getName());
	
	private InvoiceType type;
	
	private String series;
	
	private Integer fromNumber;
	
	private Integer toNumber;
	
	private Date fromDate;
	
	private Date toDate;
	
	private Registry registry;

	public InvoiceType getType() {
		return type;
	}

	public void setType(InvoiceType type) {
		this.type = type;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public Integer getFromNumber() {
		return fromNumber;
	}

	public void setFromNumber(Integer fromNumber) {
		this.fromNumber = fromNumber;
	}

	public Integer getToNumber() {
		return toNumber;
	}

	public void setToNumber(Integer toNumber) {
		this.toNumber = toNumber;
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

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	
	public void onEditSearch(MenuEvent event){
		this.onEditSearch((ActionEvent)event);
	}

	@Override
	public void onSearch(ActionEvent event) {
		try {
			Criteria criteria = createCriteria();
			this.setCriteria(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error creating criteria", e);
		}
		super.onSearch(event);
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		initializeController();
		super.onEditSearch(event);
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		try {
			return this.search(0, this.getModel().getRowCount());
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining collection", e);
		}
		return Collections.EMPTY_LIST;
	}
	
	private Criteria createCriteria() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		IManagerBean invoiceBean = getManagerBean();
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_TYPE), getType());
		if(getSeries() != null && !getSeries().equals("") ){
			criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_SERIES), getSeries());
		}
		if(getFromNumber() != null){
			criteria.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_NUMBER), getFromNumber());
		}
		if(getToNumber() !=  null){
			criteria.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_NUMBER), getToNumber());
		}
		if(getFromDate() != null){
			criteria.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), getFromDate());
		}
		if(getToDate() != null){
			criteria.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), getToDate());
		}
		if(getRegistry().getId() != null){
			criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID), getRegistry().getId());
		}
		return criteria;
	}

	private void initializeController() {
		setType(InvoiceType.SALES);
		setSeries(null);
		setRegistry(new Registry());
		setFromNumber(null);
		setToNumber(null);
		setFromDate(null);
		setToDate(null);
	}
	
	public List<SelectItem> getInvoiceTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(InvoiceType.SALES, InvoiceType.SALES.getName(locale));
		types.add(item);
		item = new SelectItem(InvoiceType.PURCHASE, InvoiceType.PURCHASE.getName(locale));
		types.add(item);
		return types;
	}
}