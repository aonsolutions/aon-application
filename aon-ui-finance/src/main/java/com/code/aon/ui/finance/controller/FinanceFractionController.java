package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_TRACKING_FRACTIONED;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import com.code.aon.common.AonException;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceFractionController extends DataScrollerState implements IFinanceConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Finance currentFinance;
	
	private Finance targetFinance;
	
	private boolean isNew;
	
	@Override
	public DataModel getModel() {
		if (getDirectModel() == null) {
			setModel(new SerializableListDataModel(new LinkedList<Finance>()));
		}
		return getDirectModel();
	}

	public void setFractionModel(DataModel fractionModel) {
		setModel(fractionModel);
	}

	public Finance getTo() {
		return currentFinance;
	}

	public void setTo(Finance currentFinance) {
		this.currentFinance = currentFinance;
	}

	public Finance getTargetFinance() {
		return targetFinance;
	}

	public void setTargetFinance(Finance originalFinance) {
		this.targetFinance = originalFinance;
	}
	
	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	@SuppressWarnings("unchecked")
	public void onFractionFinance(ActionEvent event) {
		initializeController();
		FinanceController financeController = (FinanceController)FormUtil.getController(FINANCE_CONTROLLER_NAME);
		targetFinance = (Finance)financeController.getTo();

		List<Finance> list = (List<Finance>) getModel().getWrappedData();
		Finance finance = initializeFinance(targetFinance.getAmount(), targetFinance.getExpenses());
		finance.setId(targetFinance.getId());
		list.add(finance);
		getModel().setWrappedData(list);
	}

	private void initializeController() {
		setModel(null);
		this.currentFinance = null;
		this.setNew(false);
	}

	private Finance initializeFinance(double amount, double expenses) {
		Finance finance = new Finance();
		finance.setPayment(targetFinance.isPayment());
		finance.setRegistry(targetFinance.getRegistry());
		finance.setRegistryName(targetFinance.getRegistryName());
		finance.setRegistryDocument(targetFinance.getRegistryDocument());
		finance.setRegistryDocumentType(targetFinance.getRegistryDocumentType());
		finance.setRegistryDocumentCountry(targetFinance.getRegistryDocumentCountry());
		finance.setAmount(amount);
		finance.setExpenses(expenses);
		finance.setConcept(targetFinance.getConcept());
		finance.setInvoice(targetFinance.getInvoice());
		finance.setDueDate(targetFinance.getDueDate());
		finance.setPayMethod(targetFinance.getPayMethod());
		finance.setBankAccount(targetFinance.getBankAccount());
		finance.setBankAlias(targetFinance.getBankAlias());
		finance.setBic(targetFinance.getBic());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setSecurityLevel(targetFinance.getSecurityLevel());
		finance.setScope(targetFinance.getScope());
		return finance;
	}

	@SuppressWarnings("unchecked")
	public void onAcceptFractions(ActionEvent event) {
		double amount = targetFinance.getTotalAmount();
		try{
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			List<Finance> list = (List<Finance>) getModel().getWrappedData();
			Finance finance = list.get(0);
			targetFinance.setAmount(finance.getAmount());
			targetFinance.setInvoice((targetFinance.getInvoice().getId() == null) ? null : targetFinance.getInvoice());
			targetFinance.setPayMethod((targetFinance.getPayMethod().getId() == null) ? null : targetFinance.getPayMethod());
			targetFinance.setFinanceGroup(targetFinance.getFinanceGroup().getId() == null ? null : targetFinance.getFinanceGroup());
			financeBean.update(targetFinance);

			String message = AonUtil.getMessage(FINANCE_TRACKING_FRACTIONED, 1, list.size());
			FinanceTrackingWriter.addFinanceTracking(targetFinance, new Date(), FinanceTrackingType.FRACTIONED, message, amount);

			for(int i=1; i<list.size(); i++) {
				finance = (Finance)list.get(i);
				finance.setInvoice((finance.getInvoice().getId() == null) ? null : finance.getInvoice());
				finance.setPayMethod((finance.getPayMethod().getId() == null) ? null : finance.getPayMethod());
				finance.setFinanceGroup(null);
				financeBean.insert(finance);

				message = AonUtil.getMessage(FINANCE_TRACKING_FRACTIONED, i+1, list.size());
				FinanceTrackingWriter.addFinanceTracking(finance, new Date(), FinanceTrackingType.FRACTIONED, message, amount);
			}

			initializeFinanceControllerList(((List<Finance>)getModel().getWrappedData()));
		} catch (AonException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	private void initializeFinanceControllerList(List<Finance> list) throws ManagerBeanException {
		try {
			FinanceController financeController = (FinanceController)FormUtil.getController(FINANCE_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			for (Finance finance : list) {
				criteria.addOrExpression(financeController.getFieldName(IEntityAlias.FINANCE_ID), finance.getId().toString());
			}
			financeController.onEditSearch(null);
			financeController.setCriteria(criteria);
			financeController.onSearch(null);
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Error initializing financeController");
		} catch (ExpressionException e) {
			throw new ManagerBeanException("Error initializing financeController");
		}
	}

	public void onReset(ActionEvent event) {
		this.currentFinance = initializeFinance(obtainPendingAmount(), 0);
		this.setNew(true);
	}

	public void onSelect(ActionEvent event) {
		this.currentFinance = (Finance)getModel().getRowData();
	}

	public void onAccept(ActionEvent event) {
		if (isNew) {
			onAddFraction();
		} else {
			onUpdateFraction();
		}
	}

	@SuppressWarnings("unchecked")
	private void onAddFraction() {
		((List<Finance>) getModel().getWrappedData()).add(this.currentFinance);
		this.currentFinance = null;
		this.setNew(false);
	}

	@SuppressWarnings("unchecked")
	private void onUpdateFraction(){
		List<Finance> list = ((List<Finance>)getModel().getWrappedData());
		int i = list.indexOf(this.currentFinance);
		list.remove(i);
		list.add(i, this.currentFinance);
		this.currentFinance = null;
	}

	public void onCancel(ActionEvent event){
		this.currentFinance = null;
		this.setNew(false);
	}
	
	@SuppressWarnings("unchecked")
	public void onRemove(ActionEvent event){
		((List<Finance>)getModel().getWrappedData()).remove(this.currentFinance);
		this.currentFinance = null;
		this.setNew(false);
	}

	public boolean isFractionable(){
		return ((getModel().getRowCount() > 1) && (obtainPendingAmount() == 0) && (getTo() == null));
	}

	public boolean isRemovableFraction(){
		return ((getTo() != null) && (getTo().getId() == null));
	}

	@SuppressWarnings("unchecked")
	private double obtainPendingAmount() {
		double pending = targetFinance.getTotalAmount();
		List<Finance> list = (List<Finance>) getModel().getWrappedData();
		for (Finance finance : list) {
			pending = CommonUtil.round(pending - finance.getTotalAmount());
		}
		return CommonUtil.round(pending);
	}

}
