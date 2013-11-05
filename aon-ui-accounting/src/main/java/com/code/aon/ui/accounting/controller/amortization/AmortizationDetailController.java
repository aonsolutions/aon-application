package com.code.aon.ui.accounting.controller.amortization;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.accounting.amortization.AmortizationManager;
import com.code.aon.accounting.enumeration.AmortizationDetailStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.entry.AccountEntryController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AmortizationDetailController extends LinesController {

	private DataModel summaryModel;
	private AmortizationDetail annualSummary;
	
	public AmortizationDetail getAnnualSummary() {
		return annualSummary;
	}
	public void setAnnualSummary(AmortizationDetail annualSummary) {
		this.annualSummary = annualSummary;
	}

	public DataModel getSummaryModel() {
		try {
			if (summaryModel == null) {
				summaryModel = initializeSummaryModel();
			}
			return summaryModel;
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}
	
	private DataModel initializeSummaryModel() throws ManagerBeanException {
		List<AmortizationDetail> list = new LinkedList<AmortizationDetail>();
		if (!getMasterController().isNew()) { 
			Amortization a = (Amortization) getMasterController().getTo();
			int year = -1;
			AmortizationDetail detail = null;
			double accumulated = 0.0;
			double pending = a.getAmount();
			double fiscalAccumulated = 0.0;
			double fiscalPending = a.getAmount();
			for (AmortizationDetail ad : getAmortizationList() ) {
				int detailYear= CommonUtil.getYear( ad.getFromDate() );
				if ( year != detailYear ) {
					year = detailYear;
					detail = new AmortizationDetail();
					detail.setFromDate(ad.getFromDate());
					detail.setAllocation(0.0);
					detail.setFiscalAllocation(0.0);
					detail.setCoefficient(0.0);
					detail.setAmortization(a);
					list.add(detail);
				}
				accumulated = CommonUtil.round(accumulated + ad.getAllocation());
				pending = CommonUtil.round(pending - ad.getAllocation());
				fiscalAccumulated= CommonUtil.round(fiscalAccumulated + ad.getFiscalAllocation());
				fiscalPending = CommonUtil.round(fiscalPending - ad.getFiscalAllocation());
				detail.setToDate(ad.getToDate());
				detail.setCoefficient( CommonUtil.round(detail.getCoefficient() + ad.getCoefficient()));
				detail.setAllocation( CommonUtil.round(detail.getAllocation() + ad.getAllocation()));
				detail.setAccumulated( accumulated );
				detail.setPending(pending);
				detail.setFiscalAllocation( CommonUtil.round(detail.getFiscalAllocation() + ad.getFiscalAllocation()));
				detail.setFiscalAccumulated( fiscalAccumulated );
				detail.setFiscalPending(fiscalPending);
			}
		}
		return new ListDataModel( list );
	}
	
	public void setSummaryModel(DataModel summaryModel) {
		this.summaryModel = summaryModel;
	}
	
	@SuppressWarnings("unchecked")
	public List<AmortizationDetail> getAmortizationList() throws ManagerBeanException {
		return (List<AmortizationDetail>) getModel().getWrappedData();
	}
	
	@SuppressWarnings("unchecked")
	public List<AmortizationDetail> getAmortizationSummaryList() throws ManagerBeanException {
		return (List<AmortizationDetail>) getSummaryModel().getWrappedData();
	}

	@SuppressWarnings("unchecked")
	public List<AmortizationDetail> getAmortizationListComplete() throws ManagerBeanException {
		AmortizationController ac = (AmortizationController) getMasterController();
		List<ITransferObject> all = new LinkedList<ITransferObject>(); 
		Criteria criteria = ac.getCriteria();
		List<ITransferObject> list = ac.getManagerBean().getList(criteria);
		String idAlias = getManagerBean().getFieldName(IEntityAlias.AMORTIZATION_DETAIL_AMORTIZATION_ID);
		String dateAlias = getManagerBean().getFieldName(IEntityAlias.AMORTIZATION_DETAIL_FROM_DATE);
		for (ITransferObject to:list) {
			Amortization am = (Amortization) to;
			Criteria c = new Criteria();
			c.addEqualExpression(idAlias, am.getId());
			c.addOrder(dateAlias);
			all.addAll(getManagerBean().getList(c));
		}
		List<?> retList = all; 
		return (List<AmortizationDetail>) retList;
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
		setSummaryModel(null);
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
					.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(
					IEntityAlias.ACCOUNT_ENTRY_ID), detail.getAccountEntry().getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
			entryController.setBackAction(IAccountingConstants.AMORTIZATION_FORM_NAVKEY);
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar el apunte.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onAnnualSelect(ActionEvent event) {
		AmortizationDetail detail = (AmortizationDetail) getSummaryModel().getRowData();
		setAnnualSummary(detail);
	}
	public void onAnnualUpdate(ActionEvent event) {
		try {
			Date yearFirst = getAnnualSummary().getFromDate();
			Date yearLast = getAnnualSummary().getToDate();
			
			// Se averigua cuantas cuotas están dentro del period anual para 
			// repartir la amortización fiscal equitativamente en todas ellas. 
			int count = 0;
			for (AmortizationDetail ad : getAmortizationList() ) {
				Date first = ad.getFromDate();
				Date last = ad.getToDate();
				if ((DateUtils.isSameDay(yearFirst, first) || yearFirst.before(first)) &&
				    (DateUtils.isSameDay(yearLast, last) || yearLast.after(last))) {
					++count;
				}
			}
			
			// Se calcula la dotación fiscal para cada periodo, la última se calcula por diferencia.				
			Double fiscalAllocation = CommonUtil.round(getAnnualSummary().getFiscalAllocation() / count);
			Double diff = CommonUtil.round((fiscalAllocation * count) - getAnnualSummary().getFiscalAllocation());
			Double lastFiscalAllocation = (diff == 0.0)?fiscalAllocation:CommonUtil.round(fiscalAllocation - diff);
							
			// Se modifica las cuotas con las nueva dotación fiscal.
			int i = 0;
			for (AmortizationDetail ad : getAmortizationList() ) {
				Date first = ad.getFromDate();
				Date last = ad.getToDate();
				if ((DateUtils.isSameDay(yearFirst, first) || yearFirst.before(first)) &&
				    (DateUtils.isSameDay(yearLast, last) || yearLast.after(last))) {
					ad.setFiscalAllocation(++i<count?fiscalAllocation:lastFiscalAllocation);
					getManagerBean().update(ad);
				}
				
			}
			forceRefresh();
		} catch (ManagerBeanException e) {
			String msg = "Error al modificar la dotación fiscal.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

	}
	public void onAnnualCancel(ActionEvent event) {
		setAnnualSummary(null);
	}
	
}
