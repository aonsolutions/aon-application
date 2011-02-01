package com.code.aon.accounting.summary;

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.util.CommonUtil;

public class SummaryCollection {

	private List<Summary> summaryList;

	private double initialDebit;
	private double initialCredit;

	private double debit;
	private double credit;

	public SummaryCollection() {
		summaryList = new LinkedList<Summary>();
		initialDebit = 0.0;
		initialCredit = 0.0;
		debit = 0.0;
		credit = 0.0;
	}

	public void add(Summary summary) {
		summaryList.add(summary);
		if (summary.isLastLevel()) {
			setDebit(CommonUtil.round(getDebit() + summary.getDebit()));
			setCredit(CommonUtil.round(getCredit() + summary.getCredit()));
		}
	}

	public double getInitialDebit() {
		return initialDebit;
	}

	public void setInitialDebit(double initialDebit) {
		this.initialDebit = initialDebit;
	}

	public double getInitialCredit() {
		return initialCredit;
	}

	public void setInitialCredit(double initialCredit) {
		this.initialCredit = initialCredit;
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

	public List<Summary> getSummaryList() {
		return summaryList;
	}
	public void setSummaryList(List<Summary> list) {
		this.summaryList = list;
	}

	public List<Summary> getSortedSummaryList() {
		List<Summary> sortedSummaryList = new LinkedList<Summary>();
		sortedSummaryList.addAll(summaryList);
		Collections.sort(sortedSummaryList, new SummaryComparator());
		return sortedSummaryList;
	}

	public void print(PrintStream out) {
		for (Summary s : getSummaryList()) {
			out.println(s.toString());
		}
	}

	/**
	 * Saldo Deudor
	 */
	public double getUnpaidBalance() {
		if (getDebit() > getCredit()) {
			return CommonUtil.round(getDebit() - getCredit());
		}
		return 0;
	}

	/**
	 * Saldo Acreedor
	 */
	public double getCreditBalance() {
		if (getCredit() > getDebit()) {
			return CommonUtil.round(getCredit() - getDebit());
		}
		return 0;
	}

}
