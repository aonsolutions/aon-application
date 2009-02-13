package com.code.aon.ui.account.report.resources;

import java.util.ArrayList;

public class ProfitAndLossMonthlySummary {

    private String account;
    private String description;
    private ArrayList<Double> totalMonth;

	public ProfitAndLossMonthlySummary(String account, String description) {
        this.account = account;
        this.description = description;
        initializeTotals();
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

    public ArrayList<Double> getTotalMonth() {
        return totalMonth;
    }

    public void initializeTotals() {
        totalMonth = new ArrayList<Double>();
        for (int i=0; i<12; i++) {
            totalMonth.add(new Double(0));
        } 
    }

    public void addTotalMonthValue(double total, int month) {
        total += totalMonth.get(month);
        totalMonth.set(month, new Double(total));
    }

    public double getTotalAccount() {
        double totalAccount = 0;
        for (int i=0; i<totalMonth.size(); i++) {
            totalAccount += totalMonth.get(i);
        }
        return totalAccount;
    }

}
