package com.code.aon.ui.accounting.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.accounting.amortization.AmortizationManager;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AmortizationDetailStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class AmortizationDetailController extends LinesController {

	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";

	public DataModel getCalculatedModel() throws ManagerBeanException {
		DataModel model = super.getModel();

		double accumulated = 0.0;
		double pending = 0.0;

		double fiscalAccumulated = 0.0;
		double fiscalPending = 0.0;

		for (int i = 0; i < model.getRowCount(); i++) {
			model.setRowIndex(i);
			AmortizationDetail detail = (AmortizationDetail) model.getRowData();
			if (i == 0) {
				pending = detail.getAmortization().getAmount();
				fiscalPending = detail.getAmortization().getAmount();
			}
			accumulated = CommonUtil.round(accumulated + detail.getAllocation());
			fiscalAccumulated = CommonUtil.round(fiscalAccumulated + detail.getFiscalAllocation());

			pending = CommonUtil.round(pending - detail.getAllocation());
			fiscalPending = CommonUtil.round(fiscalPending - detail.getFiscalAllocation());

			detail.setAccumulated(accumulated);
			detail.setFiscalAccumulated(fiscalAccumulated);
			detail.setPending(pending);
			detail.setFiscalPending(fiscalPending);
		}
		return model;
	}

	public AmortizationDetailStatus getPendingStatus() {
		return AmortizationDetailStatus.PENDING;
	}
	
	public AmortizationDetailStatus getBlockedStatus() {
		return AmortizationDetailStatus.BLOCKED;
	}

	public boolean hasScoredOrBlockedDetails() throws ManagerBeanException {
		DataModel model = super.getModel();
		for (int i = 0; i < model.getRowCount(); i++) {
			model.setRowIndex(i);
			AmortizationDetail detail = (AmortizationDetail) model.getRowData();
			if (detail.getStatus() == AmortizationDetailStatus.BLOCKED ||
				detail.getStatus() == AmortizationDetailStatus.SCORED  ) {
				return false;
			}
		}
		return true;
	}
	
	public void onRecord(ActionEvent event) {
		try {
			AmortizationDetail detail = (AmortizationDetail) getModel().getRowData();
			AmortizationManager am = new AmortizationManager();
			AccountEntry accountEntry = am.recordAllocation( detail ); 
			detail.setStatus(AmortizationDetailStatus.SCORED);
			detail.setAccountEntry(accountEntry);
			onAccept(event);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}

	public void onUnrecord(ActionEvent event) {
		try {
			AmortizationDetail detail = (AmortizationDetail) getModel().getRowData();
			detail.setStatus(AmortizationDetailStatus.PENDING);
			Integer id = detail.getAccountEntry().getId(); 
			detail.setAccountEntry(null);
			onAccept(event);
			AmortizationManager am = new AmortizationManager();
			am.unrecordAllocation( id );
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}

	public void onAccountEntry(ActionEvent event) {
		
		try {
			AmortizationDetail detail = (AmortizationDetail) getModel().getRowData();
			AccountEntryController entryController = (AccountEntryController) FormUtil
					.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(
					IAccountingAlias.ACCOUNT_ENTRY_ID), detail.getAccountEntry().getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
			entryController.setBackAction("amortization_form");
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar el apunte.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
}
