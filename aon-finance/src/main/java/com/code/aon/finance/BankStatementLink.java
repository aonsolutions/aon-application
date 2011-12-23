package com.code.aon.finance;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.enumeration.StatementLinkSource;
import com.esferalia.aon.entity.master.BankStatementLinkDB;

@Entity
@Table(name="bank_statement_link")
public class BankStatementLink extends BankStatementLinkDB {

	private static final long serialVersionUID = 1L;

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
			return (getBankStatement().isExact() ? ((FinanceTracking)getSourceTo()).getTrackingDate() : getSourceDate());
		} else if (isFinanceBatch()) {
			return ((FinanceBatch)getSourceTo()).getIssueDate();
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
			return ((FinanceTracking)getSourceTo()).getFinance().getDueDate();
		}
		return null;
	}
	@Transient
	public boolean isConfidential() throws ManagerBeanException {
		if (isFinanceTracking()) {
			return ((FinanceTracking)getSourceTo()).getFinance().isConfidential();
		} else if (isFinanceBatch()) {
			return ((FinanceBatch)getSourceTo()).isConfidential();
		}
		return getBankStatement().isConfidential();
	}
	@Transient
	public String getConcept() throws ManagerBeanException {
		if (isFinanceTracking()) {
			return ((FinanceTracking)getSourceTo()).getFinance().getDocumentNumber();
		} else if (isFinanceBatch()) {
			return Integer.toString(((FinanceBatch)getSourceTo()).getId());
		} else if (isBankConcept()) {
			Account bankConceptAccount = ((BankConcept)getSourceTo()).getAccount();
			return (bankConceptAccount != null) ? bankConceptAccount.getCode() : "";
		} else if (isAccount()) {
			return ((Account)getSourceTo()).getCode();
		}
		return null;
	}
	@Transient
	public String getDescription() throws ManagerBeanException {
		if (isFinanceTracking()) {
			return ((FinanceTracking)getSourceTo()).getFinance().getRegistryName();
		} else if (isFinanceBatch()) {
			return ((FinanceBatch)getSourceTo()).getDescription();
		} else if (isBankConcept()) {
			return ((BankConcept)getSourceTo()).getName();
		} else if (isAccount()) {
			return ((Account)getSourceTo()).getDescription();
		}
		return null;
	}
	@Transient
	public boolean isPayment() throws ManagerBeanException {
		if (isFinanceTracking()) {
			return ((FinanceTracking)getSourceTo()).getFinance().isPayment();
		} else if (isFinanceBatch()) {
			return ((FinanceBatch)getSourceTo()).isPayment();
		}
		return getBankStatement().isPayment();
	}

}