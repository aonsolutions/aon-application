package com.code.ui.gbp.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.gbp.ProFormaBank;
import com.code.gbp.ProFormaInvoice;
import com.code.gbp.ProFormaSignature;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.ProFormaInvoiceStatus;

public class ProFormaInvoiceController extends BasicController implements ICollectionProvider{
	
	private static final Logger LOGGER = Logger.getLogger(ProFormaInvoiceController.class.getName());

	public void addStatusExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			getCriteria().addEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_INVOICE_STATUS), event.getNewValue());
		}
	}

	public void addInvoiceDateExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			getCriteria().addEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_INVOICE_INVOICE_DATE), event.getNewValue());
		}
	}

	public void onInitialModel(ActionEvent event) throws ManagerBeanException{
		clearCriteria();
		getCriteria().addEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_INVOICE_STATUS), ProFormaInvoiceStatus.PENDING);
		this.onSearch(event);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		List list = new LinkedList();
		list.add(getTo());
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return this.getCollection();
	}
	
	@SuppressWarnings("unchecked")
	public List getSignatures(){
		try {
			ProFormaInvoice invoice = (ProFormaInvoice)getTo();
			IManagerBean signatureBean = BeanManager.getManagerBean(ProFormaSignature.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(signatureBean.getFieldName(IGBPAlias.PRO_FORMA_SIGNATURE_PRO_FORMA_INVOICE_ID), invoice.getId());
			return signatureBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}	
	
	@SuppressWarnings("unchecked")
	public List getBanks(){
		try {
			ProFormaInvoice invoice = (ProFormaInvoice)getTo();
			IManagerBean bankBean = BeanManager.getManagerBean(ProFormaBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bankBean.getFieldName(IGBPAlias.PRO_FORMA_BANK_PRO_FORMA_INVOICE_ID), invoice.getId());
			return bankBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}	
	
	public String getToTruncatedConcept(){
		try{
			ProFormaInvoice object = (ProFormaInvoice) this.getModel().getRowData();
			String concept = object.getConcept();
			if (concept.length()>40)
				return concept.substring(0,40)+"..."; 
			return concept;
		}catch (Exception e) {
		}
		return "";
	}
}