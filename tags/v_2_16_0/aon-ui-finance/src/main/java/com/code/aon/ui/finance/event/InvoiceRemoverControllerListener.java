package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.finance.controller.InvoiceRemover;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class InvoiceRemoverControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			criteria.addOrder(event.getController().getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE));
			criteria.addOrder(event.getController().getFieldName(IFinanceAlias.INVOICE_SERIES));
			criteria.addOrder(event.getController().getFieldName(IFinanceAlias.INVOICE_NUMBER));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Cannot add criteria before model Initialized", e);
		}
	}
	
	@Override
	public void beforeBeanReset(ControllerEvent event) throws ControllerListenerException {
		InvoiceRemover invoiceRemover = (InvoiceRemover)event.getController();
		try {
			if(invoiceRemover.getRemovingParams() != null){
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(invoiceRemover.getFieldName(IFinanceAlias.INVOICE_TYPE), InvoiceType.SALES);
				if(invoiceRemover.getRemovingParams().getFromDate() != null){
					criteria.addGreaterThanOrEqualExpression(invoiceRemover.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE),invoiceRemover.getRemovingParams().getFromDate());
				}
				if(invoiceRemover.getRemovingParams().getToDate() != null){
					criteria.addLessThanOrEqualExpression(invoiceRemover.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE),invoiceRemover.getRemovingParams().getToDate());
				}
				if(invoiceRemover.getRemovingParams().getCustomerId() != null){
					criteria.addEqualExpression(invoiceRemover.getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID),invoiceRemover.getRemovingParams().getCustomerId());
				}
				if(invoiceRemover.getRemovingParams().getSeries() != null && !"".equals(invoiceRemover.getRemovingParams().getSeries())){
					criteria.addExpression(invoiceRemover.getFieldName(IFinanceAlias.INVOICE_SERIES),invoiceRemover.getRemovingParams().getSeries());
				}
				if(invoiceRemover.getRemovingParams().getFromNumber() != null){
					criteria.addGreaterThanOrEqualExpression(invoiceRemover.getFieldName(IFinanceAlias.INVOICE_NUMBER),invoiceRemover.getRemovingParams().getFromNumber());
				}
				if(invoiceRemover.getRemovingParams().getToNumber() != null){
					criteria.addLessThanOrEqualExpression(invoiceRemover.getFieldName(IFinanceAlias.INVOICE_NUMBER),invoiceRemover.getRemovingParams().getToNumber());
				}
				if(invoiceRemover.getRemovingParams().getSecurityLevel() != null){
					criteria.addEqualExpression(invoiceRemover.getFieldName(IFinanceAlias.INVOICE_SECURITY_LEVEL),invoiceRemover.getRemovingParams().getSecurityLevel());
				}
				invoiceRemover.setCriteria(criteria);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error creating criteria",e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException("Error creating criteria",e);
		}
	}
}