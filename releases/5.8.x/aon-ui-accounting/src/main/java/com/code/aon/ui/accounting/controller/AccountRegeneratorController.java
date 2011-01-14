package com.code.aon.ui.accounting.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.accounting.Period;
import com.code.aon.accounting.util.AccountHelperManager;
import com.code.aon.accounting.util.AccountJournalManager;
import com.code.aon.accounting.util.AccountSummaryManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ui.accounting.check.AccountingCheckException;
import com.code.aon.ui.util.AonUtil;

public class AccountRegeneratorController {

	private Period period;
	private SecurityLevel securityLevel;

	private boolean summary;
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
	public boolean isSummary() {
		return summary;
	}
	public void setSummary(boolean summary) {
		this.summary = summary;
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
		this.setSummary(false);
	}

	public void regenerateAccount(ActionEvent event) {
		try {
			if (isHelper()) {
				regenerateAccountHelper();	
			}
			if (isJournal() ) {
				AccountCheckController acc=(AccountCheckController)AonUtil.getRegisteredBean("accountCheck");
				acc.checkEmptyAccountEntry(this.getPeriod());
				if(acc.getCheckEntryList()==null || acc.getCheckEntryList().size()==0){
					regenerateJournalCounter();
				}else{
					String msg = "-Existen apuntes sin lineas. Verifique la integridad de la Contabilidad.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
			}
			if (isSummary()) {
				regenerateAccountSummary();
			}
		} catch (AccountingCheckException e) {
			String msg = "- Se produjeron errores al regenerar el número de diario.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} 
	}

	private void regenerateAccountSummary() {
		try {
			AccountSummaryManager manager = new AccountSummaryManager();
			manager.regenerateAccountSummary(getPeriod());
			AonUtil.addInfoMessage("- Los Acumulados de Cuentas se han regenerado correctamente.");
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("- No se han podido regenerar los Acumulados de Cuentas. Causa: "
					+ e.getMessage());
		}
	}
	
	private void regenerateAccountHelper() {
		try {
			AccountHelperManager manager = new AccountHelperManager();
			manager.regenerateAccountHelper();
			AonUtil.addInfoMessage("- Las ayudas para las contrapartidas de cuentas de han regenerado correctamente.");
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("- No se han podido regenerar las ayudas para las contrapartidas de cuentas . Causa: "
					+ e.getMessage());
		}
	}
	private void regenerateJournalCounter() {
		try {
			AccountJournalManager ajm = new AccountJournalManager();
			ajm.regenerateJournalCounter(getPeriod(),getSecurityLevel());
			AonUtil.addInfoMessage("- El número de diario se han regenerado correctamente.");
		} catch (ManagerBeanException e) {
			String msg = "- Se produjeron errores al regenerar el número de diario.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
/*	
	private void regenerateVat() {
		try {
			VatManager vm = new VatManager();
			vm.regenerateVAT(getPeriod(),getSecurityLevel(),this);
			AonUtil.addInfoMessage("- Los número en Facturas de IVA Soportado se han regenerado correctamente.");
		} catch (ManagerBeanException e) {
			String msg = "- Se produjeron errores al regenerar el número en Facturas de IVA Soportado.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
*/
}
