package com.code.aon.ui.account.report.resources;

public class ProfitAndLossSummary {

	private String account;
    private String description;
	private double debit;
	private double credit;

	public ProfitAndLossSummary(String account, String description, double debit, double credit) {
		this.account = account;
        this.description = description;
		this.debit = debit;
		this.credit = credit;
	}

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public double getCredit() {
		return credit;
	}

	public void setCredit(double credit) {
		this.credit = credit;
	}

    public double getDifference() {
        if (account.startsWith("6")) {
            return (debit - credit);
        }
        return (credit - debit);
    }

    public boolean isSales() {
        return (account.startsWith("7"));
    }

    public boolean isPurchase() {
        return (account.startsWith("60"));
    }

    public boolean isExpense() {
        return (account.startsWith("6") && !account.startsWith("60"));
    }

}
