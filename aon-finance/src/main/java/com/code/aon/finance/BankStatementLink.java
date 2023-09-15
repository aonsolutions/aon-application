package com.code.aon.finance;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.account.Account;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.enumeration.StatementLinkSource;
import com.esferalia.aon.entity.master.BankStatementLinkDB;

@Entity
@Table(name="bank_statement_link")
public class BankStatementLink extends BankStatementLinkDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    private ITransferObject sourceTo;

	@Transient
	public boolean isFinanceTracking() {
		return getSource() == StatementLinkSource.FINANCE_TRACKING;
	}
	@Transient
	public boolean isFinanceBatch() {
		return getSource() == StatementLinkSource.FINANCE_BATCH;
	}
	@Transient
	public boolean isBankConcept() {
		return getSource() == StatementLinkSource.BANK_CONCEPT;
	}
	@Transient
	public boolean isAccount() {
		return getSource() == StatementLinkSource.ACCOUNT;
	}

	@Transient
	public ITransferObject getSourceTo() throws ManagerBeanException {
		if (sourceTo == null) {
			if (isFinanceTracking()) {
				IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
				setSourceTo(trackingBean.get(getSourceId()));
			} else if (isFinanceBatch()) {
				IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
				setSourceTo(fBatchBean.get(getSourceId()));
			} else if (isBankConcept()) {
				IManagerBean conceptBean = BeanManager.getManagerBean(BankConcept.class);
				setSourceTo(conceptBean.get(getSourceId()));
			} else if (isAccount()) {
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				setSourceTo(accountBean.get(getSourceId()));
			}
		}

		return sourceTo;
	}
	public void setSourceTo(ITransferObject sourceTo) {
		this.sourceTo = sourceTo;
	}

	@Transient
	public Date getDate() throws ManagerBeanException {
		if (isFinanceTracking()) {
			FinanceTracking ft = (FinanceTracking)getSourceTo();
			return getBankStatement().isExact() && ft != null ? ft.getTrackingDate() : getSourceDate();
		} else if (isFinanceBatch()) {
			FinanceBatch fb = (FinanceBatch)getSourceTo();
			return fb != null ? fb.getIssueDate() : getSourceDate();
		} else if (isBankConcept()) {
			return (getLinkedBankStatementLink() == null) ? getBankStatement().getOperationDate() : getLinkedBankStatementLink().getSourceDate();
		} else if (isAccount()) {
			return (getLinkedBankStatementLink() == null) ? getBankStatement().getOperationDate() : getLinkedBankStatementLink().getSourceDate();
		}
		return null;
	}
	@Transient
	public Date getDueDate() throws ManagerBeanException {
		if (isFinanceTracking()) {
			FinanceTracking ft = (FinanceTracking)getSourceTo();
			return ft != null ? ft.getFinance().getDueDate() : null;
		}
		return null;
	}
	@Transient
	public boolean isConfidential() throws ManagerBeanException {
		if (isFinanceTracking()) {
			FinanceTracking ft = (FinanceTracking)getSourceTo();
			return ft != null ? ft.getFinance().isConfidential() : getBankStatement().isConfidential();
		} else if (isFinanceBatch()) {
			FinanceBatch fb = (FinanceBatch)getSourceTo();
			return fb != null ? fb.isConfidential() : getBankStatement().isConfidential();
		}
		return getBankStatement().isConfidential();
	}
	@Transient
	public String getConcept() throws ManagerBeanException {
		if (isFinanceTracking()) {
			FinanceTracking ft = (FinanceTracking)getSourceTo();
			return ft != null ? ft.getFinance().getDocumentNumber() : null;
		} else if (isFinanceBatch()) {
			FinanceBatch fb = (FinanceBatch)getSourceTo();
			return fb != null ? Integer.toString(fb.getId()) : null;
		} else if (isBankConcept()) {
			BankConcept bankConcept = (BankConcept)getSourceTo();
			if (bankConcept != null) {
				Account bankConceptAccount = bankConcept.getAccount();
				return (bankConceptAccount != null) ? bankConceptAccount.getCode() : "";
			} 
		} else if (isAccount()) {
			Account account = (Account)getSourceTo();
			return account != null ? account.getCode() : "";
		}
		return null;
	}
	@Transient
	public String getDescription() throws ManagerBeanException {
		if (isFinanceTracking()) {
			FinanceTracking ft = (FinanceTracking)getSourceTo();
			return ft != null ? ft.getFinance().getRegistryName() : null;
		} else if (isFinanceBatch()) {
			FinanceBatch fb = (FinanceBatch)getSourceTo();
			return fb != null ? fb.getDescription() : null;
		} else if (isBankConcept()) {
			BankConcept bankConcept = (BankConcept)getSourceTo();
			if (bankConcept != null) {
				return bankConcept.getName();
			}
		} else if (isAccount()) {
			Account account = (Account)getSourceTo();
			return account != null ? account.getDescription() : null;
		}
		return null;
	}
	@Transient
	public boolean isPayment() throws ManagerBeanException {
		if (isFinanceTracking()) {
			FinanceTracking ft = (FinanceTracking)getSourceTo();
			return ft!= null ? ft.getFinance().isPayment(): getBankStatement().isPayment();
		} else if (isFinanceBatch()) {
			FinanceBatch fb = (FinanceBatch)getSourceTo();
			return fb != null ? fb.isPayment() : getBankStatement().isPayment();
		}
		return getBankStatement().isPayment();
	}
}