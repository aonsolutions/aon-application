package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

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
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.PageDataModel;
import com.code.aon.ui.util.AonUtil;

public class FinanceFractionController {

	private static final String FINANCE_CONTROLLER_NAME = "finance";
	
	private DataModel model;
	
	private Finance currentFinance;
	
	private Finance targetFinance;

	private String beanName;
	
	private boolean isNew;
	
	public DataModel getModel() {
		if(model == null){
			model = new ListDataModel(new LinkedList<Finance>());
		}
		return model;
	}

	public void setFractionModel(DataModel fractionModel) {
		this.model = fractionModel;
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
	
	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	public int getPageLimit() {
		return PageDataModel.LIMIT;
	}	

	@SuppressWarnings("unchecked")
	public void onFractionFinance(ActionEvent event) {
		initializeController();
		FinanceController financeController = (FinanceController)FormUtil.getController(FINANCE_CONTROLLER_NAME);
		List list = (List)getModel().getWrappedData();
		targetFinance = (Finance)financeController.getTo();
		Finance finance = initializeFinance();
		finance.setAmount(targetFinance.getAmount());
		finance.setExpenses(targetFinance.getExpenses());
		finance.setId(targetFinance.getId());
		list.add(finance);
		getModel().setWrappedData(list);
	}
	

	@SuppressWarnings("unchecked")
	public void onAcceptFractions(ActionEvent event) {
		try{
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			List list = (List)getModel().getWrappedData();
			Finance finance = (Finance) list.get(0);
			targetFinance.setAmount(finance.getAmount());
			targetFinance.setBank((targetFinance.getBank().getId() == null?null:targetFinance.getBank()));
			targetFinance.setPayMethod((targetFinance.getPayMethod().getId() == null?null:targetFinance.getPayMethod()));
			financeBean.update(targetFinance);
			ResourceBundle bundle = AonUtil.getResourceBundle("financeBundle");
			FinanceTrackingWriter.addFinanceTracking(targetFinance, FinanceTrackingType.FRACTIONED, bundle.getString("finance_tracking_fractioned"));
			for(int i = 1;i<list.size();i++){
				finance = (Finance)list.get(i);
				finance.setBank((finance.getBank().getId() == null?null:finance.getBank()));
				finance.setPayMethod((finance.getPayMethod().getId() == null?null:finance.getPayMethod()));
				financeBean.insert(finance);
			}
			initializeFinanceControllerList(((List)getModel().getWrappedData()));
		}catch (AonException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	private void initializeController() {
		this.setNew(false);
		this.currentFinance = null;
		this.model = null;
	}
	
	@SuppressWarnings("unchecked")
	private void initializeFinanceControllerList(List list) throws ManagerBeanException {
		try {
			FinanceController financeController = (FinanceController)FormUtil.getController(FINANCE_CONTROLLER_NAME);
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

	public void onReset(ActionEvent event){
		this.isNew = true;
		this.currentFinance = initializeFinance();
		this.currentFinance.setAmount(obtainPendingAmount());
	}
	
	public void onSelect(ActionEvent event){
		this.currentFinance = (Finance)getModel().getRowData();
	}
	
	public void onAccept(ActionEvent event) {
		if (isNew) {
			onAddFraction(event);
		} else {
			onUpdateFraction(event);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void onAddFraction(ActionEvent event){
		((LinkedList)getModel().getWrappedData()).add(this.currentFinance);
		this.currentFinance = null;
		this.setNew(false);
	}
	
	@SuppressWarnings("unchecked")
	public void onRemove(ActionEvent event){
		((LinkedList)getModel().getWrappedData()).remove(this.currentFinance);
		this.currentFinance = null;
		this.setNew(false);
	}

	public void onCancel(ActionEvent event){
		this.currentFinance = null;
		this.setNew(false);
	}
	
	@SuppressWarnings("unchecked")
	private void onUpdateFraction(ActionEvent event){
		int i = ((LinkedList)getModel().getWrappedData()).indexOf(this.currentFinance);
		((LinkedList)getModel().getWrappedData()).remove(i);
		((LinkedList)getModel().getWrappedData()).add(i, this.currentFinance);
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
		Iterator iter =  ((List)getModel().getWrappedData()).iterator();
		while(iter.hasNext()){
			Finance finance = (Finance)iter.next();
			pending -= finance.getTotalAmount();
		}
		return round(pending, 2);
	}
	
	public boolean isFractionable(){
		return (getModel().getRowCount() > 1 && obtainPendingAmount() == 0 && getTo() == null );
	}
	
	public boolean isRemovableFraction(){
		return (getTo() != null) && (getTo().getId() == null);
	}

	private double round(double value, int precision) {
        double decimal = Math.pow(10, precision);
        return Math.round(decimal*value) / decimal;
    }
}