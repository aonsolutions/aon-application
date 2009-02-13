package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.finance.controller.InvoiceRecordingController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class InvoiceRecordingControllerListener extends ControllerAdapter {

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
		InvoiceRecordingController invoiceRecording = (InvoiceRecordingController)event.getController();
		try {
			if(invoiceRecording.getRecordingParams() != null){
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(invoiceRecording.getFieldName(IFinanceAlias.INVOICE_STATUS), InvoiceStatus.PENDING);
				criteria.addEqualExpression(invoiceRecording.getFieldName(IFinanceAlias.INVOICE_TYPE), invoiceRecording.getRecordingParams().getInvoiceType());
				if(invoiceRecording.getRecordingParams().getFromDate() != null){
					criteria.addGreaterThanOrEqualExpression(invoiceRecording.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE),invoiceRecording.getRecordingParams().getFromDate());
				}
				if(invoiceRecording.getRecordingParams().getToDate() != null){
					criteria.addLessThanOrEqualExpression(invoiceRecording.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE),invoiceRecording.getRecordingParams().getToDate());
				}
				if(invoiceRecording.getRecordingParams().getRegistryId() != null){
					criteria.addEqualExpression(invoiceRecording.getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID),invoiceRecording.getRecordingParams().getRegistryId());
				}
				if(invoiceRecording.getRecordingParams().getSeries() != null && !"".equals(invoiceRecording.getRecordingParams().getSeries())){
					criteria.addExpression(invoiceRecording.getFieldName(IFinanceAlias.INVOICE_SERIES),invoiceRecording.getRecordingParams().getSeries());
				}
				if(invoiceRecording.getRecordingParams().getFromNumber() != null){
					criteria.addGreaterThanOrEqualExpression(invoiceRecording.getFieldName(IFinanceAlias.INVOICE_NUMBER),invoiceRecording.getRecordingParams().getFromNumber());
				}
				if(invoiceRecording.getRecordingParams().getToNumber() != null){
					criteria.addLessThanOrEqualExpression(invoiceRecording.getFieldName(IFinanceAlias.INVOICE_NUMBER),invoiceRecording.getRecordingParams().getToNumber());
				}
				if(invoiceRecording.getRecordingParams().getSecurityLevel() != null){
					criteria.addEqualExpression(invoiceRecording.getFieldName(IFinanceAlias.INVOICE_SECURITY_LEVEL),invoiceRecording.getRecordingParams().getSecurityLevel());
				}
				invoiceRecording.setCriteria(criteria);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error creating criteria",e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException("Error creating criteria",e);
		}
	}

}