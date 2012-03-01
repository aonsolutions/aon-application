package com.code.aon.ui.accounting.check;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.accounting.Period;
import com.code.aon.ui.accounting.check.modules.account.AccountEnabledCheck;
import com.code.aon.ui.accounting.check.modules.account.ParentEntryCheck;
import com.code.aon.ui.accounting.check.modules.account.entry.EmptyAccountEntryCheck;
import com.code.aon.ui.accounting.check.modules.account.entry.UnbalancedAccountEntryCheck;
import com.code.aon.ui.accounting.check.modules.balance.BalanceCheck;
import com.code.aon.ui.util.AonUtil;


public class CheckController {

	private CheckParams params;

	private List<ICheckModule> accountChecks;
	private List<ICheckEntry> checkEntryList;
	private DataModel accountCheckModel;
	
	public CheckParams getParams() {
		return params;
	}

	public void setParams(CheckParams params) {
		this.params = params;
	}

	public List<ICheckModule> getAccountChecks() {
		if (accountChecks == null) {
			accountChecks = new LinkedList<ICheckModule>();
			accountChecks.add( new ParentEntryCheck() );
			accountChecks.add( new AccountEnabledCheck() );
			accountChecks.add( new EmptyAccountEntryCheck() );
			accountChecks.add( new UnbalancedAccountEntryCheck() );
			accountChecks.add( new BalanceCheck() );
		}
		return accountChecks;
	}

	public void checkUnbalancedAccountEntry() {
		for (ICheckModule accountCheck: getAccountChecks()) {
			accountCheck.setEnabled((accountCheck instanceof UnbalancedAccountEntryCheck));
		}
	}
	
	public void setAccountChecks(List<ICheckModule> accountChecks) {
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
		setParams(new CheckParams());
		for (ICheckModule accountCheck: getAccountChecks()) {
			accountCheck.setEnabled(true);
		}
		setAccountCheckModel(null);
	}

	public void onExecute(ActionEvent event) {
		try {
			executeCheck();
		} catch (AonCheckException e) {
			String msg = "Error en el proceso de verificación. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void executeCheck() throws AonCheckException{
		setCheckEntryList(null);
		for (ICheckModule accountCheck: getAccountChecks()) {
			if (accountCheck.isEnabled()) {
				Date now = new Date();
				accountCheck.onExecute( getParams() );
				long milis = (new Date()).getTime() - now.getTime();
				System.out.println(accountCheck.getLabel() + ": " + ( (double) milis / 1000) + " segundos.");
				getCheckEntryList().addAll( accountCheck.getCheckList() );
			}
		}
	}

	public void onFix(ActionEvent event) {
		try {
			ICheckEntry entry = (ICheckEntry) getAccountCheckModel().getRowData();
			entry.onFix(event);
		} catch (AonCheckException e) {
			String msg = "Error en la corrección de la incidencia. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	public String fixAction() {
		try {
			ICheckEntry entry = (ICheckEntry) getAccountCheckModel().getRowData();
			return entry.fixAction();
		} catch (AonCheckException e) {
			String msg = "Error en la navegación de la incidencia. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void checkEmptyAccountEntry(Period p) throws AonCheckException{
		setParams(new CheckParams());
		getParams().setPeriod(p);
		List<ICheckModule> list = getAccountChecks();
		for (ICheckModule accountCheck: list) {
			accountCheck.setEnabled(accountCheck instanceof EmptyAccountEntryCheck);
		}
		executeCheck();
	}
}