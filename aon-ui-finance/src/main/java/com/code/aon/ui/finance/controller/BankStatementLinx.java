package com.code.aon.ui.finance.controller;

import java.util.Date;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.util.AonUtil;

public class BankStatementLinx implements ITransferObject {

	private static final long serialVersionUID = -98770440789965405L;

	private ITransferObject to;
	private double amount;

	public ITransferObject getTo() {
		return to;
	}
	public void setTo(ITransferObject to) {
		this.to = to;
	}

	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public boolean isFinance() {
		return getTo() instanceof Finance;
	}

	public boolean isFBatch() {
		return getTo() instanceof FinanceBatch;
	}

	public boolean isAccount() {
		return getTo() instanceof Account;
	}

	public Date getDate() {
		if (isFinance()) {
			return ((Finance)getTo()).getDueDate();
		} else if (isFBatch()) {
			return ((FinanceBatch)getTo()).getIssueDate();
		}
		return null;
	}

	public String getConcept() {
		if (isFinance()) {
			return ((Finance)getTo()).getDocumentNumber();
		} else if (isFBatch()) {
			return (AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_BATCHED)) + ((FinanceBatch)getTo()).getId();
		} else if (isAccount()) {
			return ((Account)getTo()).getId();
		}
		return null;
	}

	public String getDescription() {
		if (isFinance()) {
			return ((Finance)getTo()).getRegistryName();
		} else if (isFBatch()) {
			return ((FinanceBatch)getTo()).getDescription();
		} else if (isAccount()) {
			return ((Account)getTo()).getDescription();
		}
		return null;
	}

}
