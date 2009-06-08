package com.code.aon.ui.accounting.controller;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Period;
import com.code.aon.ui.accounting.check.AccountEntryEnabledCheck;
import com.code.aon.ui.accounting.check.AccountingCheckException;
import com.code.aon.ui.accounting.check.EmptyAccountEntryCheck;
import com.code.aon.ui.accounting.check.IAccountCheck;
import com.code.aon.ui.accounting.check.ParentEntryCheck;
import com.code.aon.ui.accounting.check.UnbalancedAccountEntryCheck;


public class AccountCheckController {

	private Period period;
	private boolean emptyAccountEntryCheck;
	private boolean unbalancedAccountEntryCheck;
	private boolean accountEntryEnabledCheck;
	private boolean parentEntryCheck;
	private List<AccountEntry> emptyAccountEntryList;
	private List<AccountEntry> unbalancedAccountEntryList;
	private List<Account> accountEntryEnabledList;
	private List<Account> parentEntryList;
	private DataModel emptyAccountEntryModel;
	private DataModel unbalancedAccountEntryModel;
	private DataModel accountEntryEnabledModel;
	private DataModel parentEntryModel;
	
	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}
	
	public boolean isEmptyAccountEntryCheck() {
		return emptyAccountEntryCheck;
	}
	
	public void setEmptyAccountEntryCheck(boolean emptyAccountEntryCheck) {
		this.emptyAccountEntryCheck = emptyAccountEntryCheck;
	}
	
	public boolean isUnbalancedAccountEntryCheck() {
		return unbalancedAccountEntryCheck;
	}
	
	public void setUnbalancedAccountEntryCheck(boolean unbalancedAccountEntryCheck) {
		this.unbalancedAccountEntryCheck = unbalancedAccountEntryCheck;
	}
	
	public boolean isAccountEntryEnabledCheck() {
		return accountEntryEnabledCheck;
	}
	
	public void setAccountEntryEnabledCheck(boolean accountEntryEnabledCheck) {
		this.accountEntryEnabledCheck = accountEntryEnabledCheck;
	}
	
	public boolean isParentEntryCheck() {
		return parentEntryCheck;
	}
	
	public void setParentEntryCheck(boolean parentEntryCheck) {
		this.parentEntryCheck = parentEntryCheck;
	}

	public void onInitialize(ActionEvent event) {
		System.out.println("onInitialize");
		setPeriod(new Period());
		getPeriod().setId(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
		
		setEmptyAccountEntryCheck(true);
		setUnbalancedAccountEntryCheck(true);
		setAccountEntryEnabledCheck(true);
		setParentEntryCheck(true);
		
	}

	public void onExecute(ActionEvent event) {
		executeCheck();
	}
	
	public void executeCheck(){
		IAccountCheck check;
		List<IAccountCheck> checkList = new LinkedList<IAccountCheck>();
		
		if(isEmptyAccountEntryCheck()){
			check = new EmptyAccountEntryCheck();
			((EmptyAccountEntryCheck)check).setPeriod(getPeriod().getId());
			checkList.add(check);
		}
		if(isUnbalancedAccountEntryCheck()){
			check = new UnbalancedAccountEntryCheck();
			((UnbalancedAccountEntryCheck)check).setPeriod(getPeriod().getId());
			checkList.add(check);
		}
		if(isAccountEntryEnabledCheck()){
			check = new AccountEntryEnabledCheck();
			checkList.add(check);
		}
		if(isParentEntryCheck()){
			check = new ParentEntryCheck();
			checkList.add(check);
		}

		emptyAccountEntryList=null;
		unbalancedAccountEntryList=null;
		accountEntryEnabledList=null;
		parentEntryList=null;

		for (IAccountCheck c: checkList) {
			try {
				c.onExecute();
				if(c instanceof EmptyAccountEntryCheck){
					emptyAccountEntryList = ((EmptyAccountEntryCheck)c).getList();
				}
				if(c instanceof UnbalancedAccountEntryCheck){
					unbalancedAccountEntryList = ((UnbalancedAccountEntryCheck)c).getList();
				}
				if(c instanceof AccountEntryEnabledCheck){
					accountEntryEnabledList = ((AccountEntryEnabledCheck)c).getList();
				}
				if(c instanceof ParentEntryCheck){
					parentEntryList = ((ParentEntryCheck)c).getList();
				}
			} catch (AccountingCheckException e) {
				e.printStackTrace();
			}
		}
		//AonUtil.addInfoMessage("Chequeos realizados.");
	}
	
	public List<AccountEntry> getEmptyAccountEntryList() {
		return emptyAccountEntryList;
	}

	public void setEmptyAccountEntryList(List<AccountEntry> emptyAccountEntryList) {
		this.emptyAccountEntryList = emptyAccountEntryList;
	}

	public List<AccountEntry> getUnbalancedAccountEntryList() {
		return unbalancedAccountEntryList;
	}

	public void setUnbalancedAccountEntryList(
			List<AccountEntry> unbalancedAccountEntryList) {
		this.unbalancedAccountEntryList = unbalancedAccountEntryList;
	}

	public List<Account> getAccountEntryEnabledList() {
		return accountEntryEnabledList;
	}

	public void setAccountEntryEnabledList(List<Account> accountEntryEnabledList) {
		this.accountEntryEnabledList = accountEntryEnabledList;
	}

	public List<Account> getParentEntryList() {
		return parentEntryList;
	}

	public void setParentEntryList(List<Account> parentEntryList) {
		this.parentEntryList = parentEntryList;
	}

	public DataModel getAccountEntryEnabledModel() {
		if(accountEntryEnabledModel == null){
			accountEntryEnabledModel = new ListDataModel(accountEntryEnabledList);
		}
		return accountEntryEnabledModel;
	}

	public void setAccountEntryEnabledModel(DataModel accountEntryEnabledModel) {
		this.accountEntryEnabledModel = accountEntryEnabledModel;
	}

	public DataModel getEmptyAccountEntryModel() {
		if(emptyAccountEntryModel == null){
			emptyAccountEntryModel  = new ListDataModel(emptyAccountEntryList);
		}
		return emptyAccountEntryModel;
	}

	public void setEmptyAccountEntryModel(DataModel emptyAccountEntryModel) {
		this.emptyAccountEntryModel = emptyAccountEntryModel;
	}

	public DataModel getUnbalancedAccountEntryModel() {
		if(unbalancedAccountEntryModel == null){
			unbalancedAccountEntryModel = new ListDataModel(unbalancedAccountEntryList);
		}
		return unbalancedAccountEntryModel;
	}

	public void setUnbalancedAccountEntryModel(DataModel unbalancedAccountEntryModel) {
		this.unbalancedAccountEntryModel = unbalancedAccountEntryModel;
	}

	public DataModel getParentEntryModel() {
		if(parentEntryModel == null){
			parentEntryModel = new ListDataModel(parentEntryList);
		}
		return parentEntryModel;
	}

	public void setParentEntryModel(DataModel parentEntryModel) {
		this.parentEntryModel = parentEntryModel;
	}
	
	

}