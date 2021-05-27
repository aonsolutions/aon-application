package com.code.aon.ui.accounting.controller;

import java.io.Serializable;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.util.AccountJournalManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckController;
import com.code.aon.ui.util.AonUtil;

public class AccountRegeneratorController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Period period;
	private SecurityLevel securityLevel;

	private boolean helper;
	private boolean journal;

	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	public boolean isHelper() {
		return helper;
	}
	public void setHelper(boolean helper) {
		this.helper = helper;
	}
	public boolean isJournal() {
		return journal;
	}
	public void setJournal(boolean journal) {
		this.journal = journal;
	}

	public void onEditSearch(ActionEvent event) {
		this.setPeriod(null);
		this.setHelper(false);
		this.setJournal(false);
	}

	public void regenerateAccount(ActionEvent event) {
		try {
			if (isJournal() ) {
				CheckController acc=(CheckController)AonUtil.getRegisteredBean("accountCheck");
				acc.checkEmptyAccountEntry(this.getPeriod());
				if(acc.getCheckEntryList()==null || acc.getCheckEntryList().size()==0){
					regenerateJournalCounter();
				}else{
					String msg = "-Existen apuntes sin lineas. Verifique la integridad de la Contabilidad.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
			}
			if (isHelper()) {
				regenerateAccountHelper();	
			}
		} catch (AonCheckException e) {
			String msg = "- Se produjeron errores al regenerar el número de diario.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} 
	}

	private void regenerateAccountHelper() {
		AonUtil.addInfoMessage("- Las ayudas para las contrapartidas ya no se regeneran.");
	}
	private void regenerateJournalCounter() {
		try {
			Date now = new Date();
			AccountJournalManager ajm = new AccountJournalManager();
			ajm.regenerateJournalCounter(AonUtil.getDomainName(), getPeriod(),getSecurityLevel());
			long milis = (new Date()).getTime() - now.getTime();
			AonUtil.addInfoMessage("- El número de diario se han regenerado correctamente.("+ ( (double) milis / 1000) + " segundos.)");
		} catch (ManagerBeanException e) {
			String msg = "- Se produjeron errores al regenerar el número de diario.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
}
