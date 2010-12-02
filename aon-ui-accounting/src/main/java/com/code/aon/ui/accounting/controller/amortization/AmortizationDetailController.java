package com.code.aon.ui.accounting.controller.amortization;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.accounting.amortization.AmortizationManager;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AmortizationDetailStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.controller.entry.AccountEntryController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class AmortizationDetailController extends LinesController {

	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";

	@SuppressWarnings("unchecked")
	public List<AmortizationDetail> getAmortizationList() throws ManagerBeanException {
		return (List<AmortizationDetail>) getModel().getWrappedData();
	}

	@Override
	public List<ITransferObject> search(int start, int count) throws ManagerBeanException {
		List<ITransferObject> list = super.search(start, count);
		calculateTotals(list);
		return list; 
	}
	
	@SuppressWarnings("unchecked")
	public void forceRefresh() throws ManagerBeanException {
		List<ITransferObject> details = (List<ITransferObject>) getModel().getWrappedData();
		Amortization a = (Amortization) getMasterController().getTo();
		a.setDetailsInitialized(false);
		calculateTotals(details);
	}
	
	@SuppressWarnings("unchecked")
	private void calculateTotals(List<ITransferObject> list) {
		List<?> details = list;
		Amortization a = (Amortization) getMasterController().getTo();
		a.calculateTotals((List<AmortizationDetail>) details);
	}

	public AmortizationDetailStatus getPendingStatus() {
		return AmortizationDetailStatus.PENDING;
	}
	
	public AmortizationDetailStatus getBlockedStatus() {
		return AmortizationDetailStatus.BLOCKED;
	}

	public boolean hasScoredOrBlockedDetails() throws ManagerBeanException {
		List<AmortizationDetail> list = getAmortizationList();
		for (AmortizationDetail detail : list) {
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
