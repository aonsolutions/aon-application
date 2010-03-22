package com.code.aon.ui.accounting.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.accounting.Period;
import com.code.aon.accounting.util.AccountHelperManager;
import com.code.aon.accounting.util.AccountJournalManager;
import com.code.aon.accounting.util.AccountSummaryManager;
import com.code.aon.common.IProgression;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ui.util.AonUtil;

public class AccountRegeneratorController implements IProgression {

	private Period period;
	private SecurityLevel securityLevel;

	private boolean progressionPanelVisible;
	private boolean progressionEnabled;
	private Long progressionValue;
	private boolean progressStart;
	private boolean recording;
	private boolean redirect;
	
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

	public boolean isProgressionPanelVisible() {
		return progressionPanelVisible;
	}

	public void setProgressionPanelVisible(boolean progressionPanelVisible) {
		this.progressionPanelVisible = progressionPanelVisible;
	}


	public void onEditSearch(ActionEvent event) {
		this.setPeriod(null);
		this.setHelper(false);
		this.setJournal(false);
		this.setSummary(false);
	}

	public void regenerateAccount(ActionEvent event) {
		try {
			setProgressionCurrentValue(0L);
			if (isHelper()) {
				regenerateAccountHelper();	
			}
			if (isJournal() ) {
				regenerateJournalCounter();
			}
			if (isSummary()) {
				regenerateAccountSummary();
			}
			onClosePanel(event);
		} finally {
			setProgressionCurrentValue(-1L);
		}
	}

	private void regenerateAccountSummary() {
		try {
			AccountSummaryManager manager = new AccountSummaryManager();
			manager.regenerateAccountSummary(period, this);
			AonUtil.addInfoMessage("- Los Acumulados de Cuentas se han regenerado correctamente.");
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("- No se han podido regenerar los Acumulados de Cuentas. Causa: "
					+ e.getMessage());
		}
	}
	
	private void regenerateAccountHelper() {
		try {
			AccountHelperManager manager = new AccountHelperManager();
			manager.regenerateAccountHelper(this);
			AonUtil.addInfoMessage("- Las ayudas para las contrapartidas de cuentas de han regenerado correctamente.");
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("- No se han podido regenerar las ayudas para las contrapartidas de cuentas . Causa: "
					+ e.getMessage());
		}
	}
	private void regenerateJournalCounter() {
		try {
			AccountJournalManager ajm = new AccountJournalManager();
			ajm.regenerateJournalCounter(getPeriod(),getSecurityLevel(),this);
			AonUtil.addInfoMessage("- El número de diario se han regenerado correctamente.");
		} catch (ManagerBeanException e) {
			String msg = "- Se produjeron errores al regenerar el número de diario.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	public Long getProgressionValue() {
		return progressionValue;
	}
	public void setProgressionValue(Long value) {
		this.progressionValue = value;
	}

	@Override
	public Long getProgressionCurrentValue() {
		if (progressStart) {
//			if (!recording) {
//				int row = getInvoicingFeedBack().getCurrentRow();
//				int count = getInvoicingFeedBack().getRowCount();
//				if (count > 0) {
//					int pro = (int) CommonUtil.round(row * 100 / count);
//					if (getParams().isInvoiceRecordable()) {
//						pro = pro / 2;
//					}
//					setProgressionValue(new Long(pro));
//				}
//			} else {
//				if (invoicesToRecord > 0) {
//					int pro = (int) CommonUtil.round(((recordingInvoice * 100 / invoicesToRecord) / 2)+50);
//					setProgressionValue(new Long(pro));
//				}
//			}
		}
		return getProgressionValue();
	}

	@Override
	public void setProgressionCurrentValue(Long currentValue) {
		setProgressionValue(currentValue);
	}

	@Override
	public boolean isProgressionEnabled() {
		return progressionEnabled;
	}

	@Override
	public void setProgressionEnabled(boolean enabled) {
		this.progressionEnabled = enabled;
	}

	public void onShowPanel(ActionEvent event) {
		setProgressionPanelVisible(true);
		setProgressionEnabled(true);
		setProgressionValue(-1L);
		progressStart = false;
//		recording = false;
//		invoicesToRecord = 0;
//		recordingInvoice = 0;
	}
	public void onClosePanel(ActionEvent event) {
		setProgressionPanelVisible(false);
		setProgressionEnabled(false);
		setProgressionValue(-101L);
		progressStart = false;
//		recording = false;
//		invoicesToRecord = 0;
//		recordingInvoice = 0;
	}

}
