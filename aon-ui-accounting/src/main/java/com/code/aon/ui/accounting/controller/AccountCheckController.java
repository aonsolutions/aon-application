package com.code.aon.ui.accounting.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.ui.accounting.check.AccountEntryEnabledCheck;
import com.code.aon.ui.accounting.check.AccountingCheckException;
import com.code.aon.ui.accounting.check.AccountingCheckParams;
import com.code.aon.ui.accounting.check.EmptyAccountEntryCheck;
import com.code.aon.ui.accounting.check.IAccountCheck;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.accounting.check.ParentEntryCheck;
import com.code.aon.ui.accounting.check.UnbalancedAccountEntryCheck;
import com.code.aon.ui.util.AonUtil;


public class AccountCheckController {

	private AccountingCheckParams params;

	private List<IAccountCheck> accountChecks;
	private List<ICheckEntry> checkEntryList;
	private DataModel accountCheckModel;
	
	public AccountingCheckParams getParams() {
		return params;
	}

	public void setParams(AccountingCheckParams params) {
		this.params = params;
	}

	public List<IAccountCheck> getAccountChecks() {
		if (accountChecks == null) {
			accountChecks = new LinkedList<IAccountCheck>();
			accountChecks.add( new ParentEntryCheck() );
			accountChecks.add( new AccountEntryEnabledCheck() );
			accountChecks.add( new EmptyAccountEntryCheck() );
			accountChecks.add( new UnbalancedAccountEntryCheck() );
		}
		return accountChecks;
	}

	public void setAccountChecks(List<IAccountCheck> accountChecks) {
		this.accountChecks = accountChecks;
	}

	public List<ICheckEntry> getCheckEntryList() {
		if (checkEntryList == null) {
			checkEntryList = new LinkedList<ICheckEntry>();
		}
		return checkEntryList;
	}

	public DataModel getAccountCheckModel() {
		if (accountCheckModel == null) {
			accountCheckModel = new ListDataModel( getCheckEntryList() );	
		}
		return accountCheckModel;
	}

	public void setAccountCheckModel(DataModel accountCheckModel) {
		this.accountCheckModel = accountCheckModel;
	}

	public void setCheckEntryList(List<ICheckEntry> checkEntryList) {
		this.checkEntryList = checkEntryList;
	}
	

	public void onInitialize(ActionEvent event) {
		setParams(new AccountingCheckParams());
		for (IAccountCheck accountCheck: getAccountChecks()) {
			accountCheck.setEnabled(true);
		}
		setAccountCheckModel(null);
	}

	public void onExecute(ActionEvent event) {
		try {
			executeCheck();
		} catch (AccountingCheckException e) {
			String msg = "Error en el proceso de veridifcación. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void executeCheck() throws AccountingCheckException{
		setCheckEntryList(null);
		for (IAccountCheck accountCheck: getAccountChecks()) {
			if (accountCheck.isEnabled()) { 
				accountCheck.onExecute( getParams() );
				getCheckEntryList().addAll( accountCheck.getCheckList() );
			}
		}
	}

	public void onFix(ActionEvent event) {
		try {
			ICheckEntry entry = (ICheckEntry) getAccountCheckModel().getRowData();
			entry.fix();
		} catch (AccountingCheckException e) {
			String msg = "Error en la corrección de la incidencia. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
}