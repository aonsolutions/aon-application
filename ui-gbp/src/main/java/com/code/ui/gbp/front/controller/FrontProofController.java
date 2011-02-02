package com.code.ui.gbp.front.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.gbp.ProFormaBank;
import com.code.gbp.ProFormaInvoice;
import com.code.gbp.ProFormaSignature;
import com.code.gbp.dao.IGBPAlias;
import com.code.ui.gbp.constants.GBPConstants;

public class FrontProofController extends BasicController implements ICollectionProvider{
	
	private static final Logger LOGGER = Logger.getLogger(FrontProofController.class.getName());
	
	private Date fromDate;
	
	private Date toDate;

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
	
	@Override
	public void onSearch(ActionEvent event) {
		createCriteria();
		super.onSearch(event);
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		setFromDate(null);
		setToDate(null);
		super.onEditSearch(event);
	}

	public void onInitialModel(ActionEvent event) throws ManagerBeanException{
		clearCriteria();
		Date limitDate = obtainLimitDate();
		getCriteria().addLessThanOrEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_INVOICE_PAYMENT_DATE), limitDate);
		this.onSearch(event);
	}

	private void createCriteria() {
		try {
			Date limitDate = obtainLimitDate();
			if(fromDate != null ){
				if(fromDate.before(limitDate)){
					getCriteria().addGreaterThanOrEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_INVOICE_PAYMENT_DATE), fromDate);
				} else {
					SimpleDateFormat dateFormat = new SimpleDateFormat();
					dateFormat.applyPattern("yyyy/MM/dd");
					String message = "Max allowed date:" + " " + dateFormat.format(limitDate);
					AonUtil.addErrorMessage(message);
					throw new AbortProcessingException(message);
				}
			}
			
			if(toDate != null){
				if(toDate.before(limitDate)){
					getCriteria().addLessThanOrEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_INVOICE_PAYMENT_DATE), toDate);
				} else {
					SimpleDateFormat dateFormat = new SimpleDateFormat();
					dateFormat.applyPattern("yyyy/MM/dd");
					String message = "Max allowed date:" + " " + dateFormat.format(limitDate);
					AonUtil.addErrorMessage(message);
					throw new AbortProcessingException(message);
				}	
			} else {
				getCriteria().addLessThanOrEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_INVOICE_PAYMENT_DATE), limitDate);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	private Date obtainLimitDate() {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, GBPConstants.GBP_DEFAULT_PROOF_PRINT_DAYS);
		return calendar.getTime();
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
	
}