package com.code.aon.ui.accounting.check;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import com.code.aon.AonVersion;
import com.code.aon.accounting.Period;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ui.accounting.check.modules.account.entry.EmptyAccountEntryCheck;
import com.code.aon.ui.accounting.check.modules.account.entry.UnbalancedAccountEntryCheck;
import com.code.aon.ui.accounting.check.modules.account.invoice.DuplicatedInvoicesCheck;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;


public class CheckController extends DataScrollerState {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private CheckParams params;

	private List<ICheckModule> accountChecks;
	private List<ICheckEntry> checkEntryList;
	
	public CheckParams getParams() {
		return params;
	}

	public void setParams(CheckParams params) {
		this.params = params;
	}

	public List<ICheckModule> getAccountChecks() {
		if (accountChecks == null) {
			accountChecks = new LinkedList<ICheckModule>();
//			accountChecks.add( new ParentEntryCheck() );
//			accountChecks.add( new AccountEnabledCheck() );
//			accountChecks.add( new EmptyAccountEntryCheck() );
//			accountChecks.add( new UnbalancedAccountEntryCheck() );
//			accountChecks.add( new BalanceCheck() );
//			accountChecks.add( new NoRecordedInvoiceCheck() );
			accountChecks.add( new DuplicatedInvoicesCheck() );
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

	@Override
	public DataModel getModel() {
		if (getDirectModel() == null) {
			setModel(new SerializableListDataModel(getCheckEntryList()));	
		}
		return getDirectModel();
	}

	public void setCheckEntryList(List<ICheckEntry> checkEntryList) {
		this.checkEntryList = checkEntryList;
	}
	

	public void onInitialize(ActionEvent event) {
		setParams(getNewCheckParams());
		for (ICheckModule accountCheck: getAccountChecks()) {
			accountCheck.setEnabled(true);
		}
		setModel(null);
	}

	private CheckParams getNewCheckParams() {
		String domainName = AonUtil.getDomainName(); 
		int domainId = DomainManager.getCurrentDomain();
		return new CheckParams(domainName,domainId);
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
			ICheckEntry entry = (ICheckEntry) getModel().getRowData();
			entry.onFix(event);
		} catch (AonCheckException e) {
			String msg = "Error en la corrección de la incidencia. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	public String fixAction() {
		try {
			ICheckEntry entry = (ICheckEntry) getModel().getRowData();
			return entry.fixAction();
		} catch (AonCheckException e) {
			String msg = "Error en la navegación de la incidencia. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void checkEmptyAccountEntry(Period p) throws AonCheckException{
		setParams(getNewCheckParams());
		getParams().setPeriod(p);
		List<ICheckModule> list = getAccountChecks();
		for (ICheckModule accountCheck: list) {
			accountCheck.setEnabled(accountCheck instanceof EmptyAccountEntryCheck);
		}
		executeCheck();
	}
}