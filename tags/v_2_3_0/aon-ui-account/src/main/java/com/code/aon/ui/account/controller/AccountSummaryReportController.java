package com.code.aon.ui.account.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ui.account.report.resources.ProfitAndLossMonthly;
import com.code.aon.ui.account.report.resources.ProfitAndLossMonthlySummary;
import com.code.aon.ui.account.report.resources.ProfitAndLossSummary;
import com.code.aon.ui.form.BasicController;

public class AccountSummaryReportController extends BasicController {
	
    private String period;
    private Date fromDate;
	private Date toDate;
	private SecurityLevel securityLevel;
	
	@SuppressWarnings("unchecked")
	private Collection grossMarginSummaryCollection;
	@SuppressWarnings("unchecked")
	private Collection totalExpensesSummaryCollection;
    
	private double grossMargin;
    private double totalExpenses;
	
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	@SuppressWarnings("unchecked")
	public Collection getGrossMarginSummaryCollection() {
        return grossMarginSummaryCollection;
    }

	@SuppressWarnings("unchecked")
    public void setGrossMarginSummaryCollection(Collection grossMarginSummaryCollection) {
        this.grossMarginSummaryCollection = grossMarginSummaryCollection;
    }

    @SuppressWarnings("unchecked")
    public Collection getTotalExpensesSummaryCollection() {
        return totalExpensesSummaryCollection;
    }

    @SuppressWarnings("unchecked")
    public void setTotalExpensesSummaryCollection(Collection totalExpensesSummaryCollection) {
        this.totalExpensesSummaryCollection = totalExpensesSummaryCollection;
    }

    public double getGrossMargin() {
        return grossMargin;
    }

    public void setGrossMargin(double grossMargin) {
        this.grossMargin = grossMargin;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public double getTotalResult() {
        return (grossMargin - totalExpenses);
    }

    @Override
    public void onEditSearch(ActionEvent event) {
        this.setPeriod(null);
        this.setFromDate(null);
        this.setToDate(null);
    }

    @SuppressWarnings("unused")
    public void calculateSummaryCollections(ActionEvent event) {
        this.calculateGrossMarginSummaryCollection();
        this.calculateTotalExpensesSummaryCollection();
    }

    @SuppressWarnings("unchecked")
    private void calculateGrossMarginSummaryCollection() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String select = "select new com.code.aon.ui.account.report.resources.ProfitAndLossSummary(" +
                        "substring(summary.account.id,1,3), account.description, " +
                        "sum(summary.debit), sum(summary.credit)) " +
                        "from AccountSummary as summary, Account as account " +
                        "where account.id = substring(summary.account.id,1,3) " +
                        "and (summary.account.id like '60%' or summary.account.id like '7%') " +
                        "and summary.accountPeriod = '" + getPeriod() + "' ";
        select += (getSecurityLevel() != null) ? "and summary.securityLevel = " + getSecurityLevel().ordinal() + " " : "";
        select += (getFromDate() != null) ? "and summary.entryDate >= '" + formatter.format(getFromDate()) + "' " : "";
        select += (getToDate() != null) ? "and summary.entryDate <= '" + formatter.format(getToDate()) + "' " : "";
        select += "group by substring(summary.account.id,1,3) ";
        select += "order by substring(summary.account.id,1,3) desc";

        Session session = HibernateUtil.getSession();
        Query query = session.createQuery(select);

        List list = query.list();
        Comparator comparator = new ProfitAndLossComparator();
        Collections.sort(list, comparator);
        this.setGrossMarginSummaryCollection(list);

        double totalDebit = 0;
        double totalCredit = 0;
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            ProfitAndLossSummary profitAndLoss = (ProfitAndLossSummary)iterator.next();
            totalDebit += profitAndLoss.getDebit();
            totalCredit += profitAndLoss.getCredit();
        }
        this.setGrossMargin(totalCredit - totalDebit);
    }

    @SuppressWarnings("unchecked")
    private void calculateTotalExpensesSummaryCollection() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String select = "select new com.code.aon.ui.account.report.resources.ProfitAndLossSummary(" +
                        "substring(summary.account.id,1,3), account.description, " +
                        "sum(summary.debit), sum(summary.credit)) " +
                        "from AccountSummary as summary, Account as account " +
                        "where account.id = substring(summary.account.id,1,3) " +
                        "and (summary.account.id >= '610' and summary.account.id < '7') " +
                        "and summary.accountPeriod = '" + getPeriod() + "' ";
        select += (getSecurityLevel() != null) ? "and summary.securityLevel = " + getSecurityLevel().ordinal() + " " : "";
        select += (getFromDate() != null) ? "and summary.entryDate >= '" + formatter.format(getFromDate()) + "' " : "";
        select += (getToDate() != null) ? "and summary.entryDate <= '" + formatter.format(getToDate()) + "' " : "";
        select += "group by substring(summary.account.id,1,3) ";
        select += "order by substring(summary.account.id,1,3) desc";

        Session session = HibernateUtil.getSession();
        Query query = session.createQuery(select);

        List list = query.list();
        Comparator comparator = new ProfitAndLossComparator();
        Collections.sort(list, comparator);
        this.setTotalExpensesSummaryCollection(list);

        double totalDebit = 0;
        double totalCredit = 0;
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            ProfitAndLossSummary profitAndLoss = (ProfitAndLossSummary)iterator.next();
            totalDebit += profitAndLoss.getDebit();
            totalCredit += profitAndLoss.getCredit();
        }
        this.setTotalExpenses(totalDebit - totalCredit);
    }

    @SuppressWarnings("unchecked")
	public Collection getCollection(){
    	Collection list = new LinkedList();
        list.addAll(getGrossMarginSummaryCollection());
    	list.addAll(getTotalExpensesSummaryCollection());
    	return list;
    }

    @SuppressWarnings("unchecked")
    public Collection getMonthlyCollection() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String select = "select new com.code.aon.ui.account.report.resources.ProfitAndLossMonthly(" +
                        "substring(summary.account.id,1,3), account.description, month(summary.entryDate), " +
                        "sum(summary.debit), sum(summary.credit)) " +
                        "from AccountSummary as summary, Account as account " +
                        "where account.id = substring(summary.account.id,1,3) " +
                        "and (summary.account.id like '6%' or summary.account.id like '7%') " +
                        "and summary.accountPeriod = '" + getPeriod() + "' ";
        select += (getSecurityLevel() != null) ? "and summary.securityLevel = " + getSecurityLevel().ordinal() + " " : "";
        select += (getFromDate() != null) ? "and summary.entryDate >= '" + formatter.format(getFromDate()) + "' " : "";
        select += (getToDate() != null) ? "and summary.entryDate <= '" + formatter.format(getToDate()) + "' " : "";
        select += "group by substring(summary.account.id,1,3), month(summary.entryDate) ";
        select += "order by substring(summary.account.id,1,3), month(summary.entryDate) ";

        Session session = HibernateUtil.getSession();
        Query query = session.createQuery(select);

        List list = query.list();
        Comparator comparator = new ProfitAndLossComparator();
        Collections.sort(list, comparator);

        List summaryList = new LinkedList();
        ProfitAndLossMonthlySummary palMonthlySummary = null;
        String oldAccount = null;
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            ProfitAndLossMonthly palMonthly = (ProfitAndLossMonthly)iterator.next();
            if (oldAccount == null || !palMonthly.getAccount().equals(oldAccount)) {
                palMonthlySummary = new ProfitAndLossMonthlySummary(palMonthly.getAccount(), palMonthly.getDescription());
                summaryList.add(palMonthlySummary);

                oldAccount = palMonthlySummary.getAccount();
            }
            palMonthlySummary.addTotalMonthValue(palMonthly.getDifference(), palMonthly.getMonth().intValue()-1);
        }

        return summaryList;
    }

}

@SuppressWarnings("unchecked")
class ProfitAndLossComparator implements Comparator {

    public int compare(Object obj1, Object obj2) {
        String account1 = ((ProfitAndLossSummary)obj1).getAccount();
        String account2 = ((ProfitAndLossSummary)obj2).getAccount();

        if (account1.substring(0, 1).equals(account2.substring(0, 1)) ) {
            return account1.compareTo(account2);
        } 
        return (account1.substring(0, 1).equals("7"))? -1 : 1;
    }
}
