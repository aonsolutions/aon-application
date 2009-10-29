package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.util.AonUtil;

public class FinanceFractionController {

	private static final String FINANCE_CONTROLLER_NAME = "finance";
	
	private DataModel fractionModel;
	
	private Finance currentFinance;
	
	private Finance targetFinance;

	private boolean isNew;
	
	public DataModel getFractionModel() {
		if(fractionModel == null){
			fractionModel = new ListDataModel(new LinkedList<Finance>());
		}
		return fractionModel;
	}

	public void setFractionModel(DataModel fractionModel) {
		this.fractionModel = fractionModel;
	}

	public Finance getCurrentFinance() {
		return currentFinance;
	}

	public void setCurrentFinance(Finance currentFinance) {
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

	@SuppressWarnings({"unused","unchecked"})
	public void onFractionFinance(ActionEvent event) throws ManagerBeanException{
		initializeController();
		FinanceController financeController = (FinanceController)AonUtil.getController(FINANCE_CONTROLLER_NAME);
		List list = (List)getFractionModel().getWrappedData();
		targetFinance = (Finance)financeController.getTo();
		Finance finance = initializeFinance();
		finance.setAmount(targetFinance.getAmount());
		finance.setExpenses(targetFinance.getExpenses());
		finance.setId(targetFinance.getId());
		list.add(finance);
		getFractionModel().setWrappedData(list);
	}
	
	@SuppressWarnings({"unused","unchecked"})
	public void onAcceptFractions(ActionEvent event) throws ManagerBeanException{
		try{
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			List list = (List)getFractionModel().getWrappedData();
			Finance finance = (Finance) list.get(0);
			targetFinance.setAmount(finance.getAmount());
			targetFinance.setBank((targetFinance.getBank().getId() == null?null:targetFinance.getBank()));
			targetFinance.setPayMethod((targetFinance.getPayMethod().getId() == null?null:targetFinance.getPayMethod()));
			financeBean.update(targetFinance);
			ResourceBundle bundle = ResourceBundle.getBundle(AonUtil.getConfigurationController().getApplicationBundles().get("financeBundle"),FacesContext.getCurrentInstance().getViewRoot().getLocale());
			FinanceTrackingWriter.addFinanceTracking(targetFinance, FinanceTrackingType.FRACTIONED, bundle.getString("aon_finance_tracking_fractioned"));
			for(int i = 1;i<list.size();i++){
				finance = (Finance)list.get(i);
				finance.setBank((finance.getBank().getId() == null?null:finance.getBank()));
				finance.setPayMethod((finance.getPayMethod().getId() == null?null:finance.getPayMethod()));
				financeBean.insert(finance);
			}
			initializeFinanceControllerList(((List)getFractionModel().getWrappedData()));
		}catch (AonException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	private void initializeController() {
		this.setNew(false);
		this.currentFinance = null;
		this.fractionModel = null;
	}
	
	@SuppressWarnings("unchecked")
	private void initializeFinanceControllerList(List list) throws ManagerBeanException {
		try {
			FinanceController financeController = (FinanceController)AonUtil.getController(FINANCE_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			Iterator iter = list.iterator();
			while(iter.hasNext()){
				Finance finance = (Finance)iter.next();
				criteria.addOrExpression(financeController.getFieldName(IFinanceAlias.FINANCE_ID), finance.getId().toString());
			}
			financeController.setCriteria(criteria);
			financeController.onSearch(null);
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Error initializing financeController");
		} catch (ExpressionException e) {
			throw new ManagerBeanException("Error initializing financeController");
		}
	}

	@SuppressWarnings("unused")
	public void onNewFraction(ActionEvent event){
		this.isNew = true;
		this.currentFinance = initializeFinance();
		this.currentFinance.setAmount(obtainPendingAmount());
	}
	
	@SuppressWarnings("unused")
	public void onSelectFraction(ActionEvent event){
		this.currentFinance = (Finance)getFractionModel().getRowData();
	}
	
	@SuppressWarnings({"unchecked", "unused"})
	public void onAddFraction(ActionEvent event){
		((LinkedList)getFractionModel().getWrappedData()).add(this.currentFinance);
		this.currentFinance = null;
		this.setNew(false);
	}
	
	@SuppressWarnings({"unused","unchecked"})
	public void onRemoveFraction(ActionEvent event){
		((LinkedList)getFractionModel().getWrappedData()).remove(this.currentFinance);
		this.currentFinance = null;
		this.setNew(false);
	}

	@SuppressWarnings("unused")
	public void onCancelFraction(ActionEvent event){
		this.currentFinance = null;
		this.setNew(false);
	}
	
	@SuppressWarnings({"unused", "unchecked"})
	public void onUpdateFraction(ActionEvent event){
		int i = ((LinkedList)getFractionModel().getWrappedData()).indexOf(this.currentFinance);
		((LinkedList)getFractionModel().getWrappedData()).remove(i);
		((LinkedList)getFractionModel().getWrappedData()).add(i, this.currentFinance);
		this.currentFinance = null;
	}

	private Finance initializeFinance() {
		Finance finance = new Finance();
		finance.setBank(targetFinance.getBank());
		finance.setBankAccount(targetFinance.getBankAccount());
		finance.setConcept(targetFinance.getConcept());
		finance.setDueDate(targetFinance.getDueDate());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setInvoice(targetFinance.getInvoice());
		finance.setPayment(targetFinance.isPayment());
		finance.setPayMethod(targetFinance.getPayMethod());
		finance.setRegistry(targetFinance.getRegistry());
		finance.setSecurityLevel(targetFinance.getSecurityLevel());
		return finance;
	}
	
	@SuppressWarnings("unchecked")
	private double obtainPendingAmount() {
		double pending = targetFinance.getTotalAmount();
		Iterator iter =  ((List)getFractionModel().getWrappedData()).iterator();
		while(iter.hasNext()){
			Finance finance = (Finance)iter.next();
			pending -= finance.getTotalAmount();
		}
		return round(pending, 2);
	}
	
	public boolean isFractionable(){
		return (getFractionModel().getRowCount() > 1 && obtainPendingAmount() == 0 && getCurrentFinance() == null );
	}
	
	public boolean isRemovableFraction(){
		return (getCurrentFinance().getId() == null);
	}

	private double round(double value, int precision) {
        double decimal = Math.pow(10, precision);
        return Math.round(decimal*value) / decimal;
    }
}