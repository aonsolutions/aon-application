package com.code.aon.accounting.summary;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;

public class SummaryCollection implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<Summary> summaryList;

	private double initialDebit;
	private double initialCredit;

	private double openingDebit;
	private double openingCredit;

	private double debit;
	private double credit;

	public SummaryCollection() {
		summaryList = new LinkedList<Summary>();
		initialDebit = 0.0;
		initialCredit = 0.0;
		openingDebit = 0.0;
		openingCredit = 0.0;
		debit = 0.0;
		credit = 0.0;
	}

	public void add(Summary summary) {
		summaryList.add(summary);
		setInitialDebit(CommonUtil.round(getInitialDebit() + summary.getInitialDebit()));
		setInitialCredit(CommonUtil.round(getInitialCredit() + summary.getInitialCredit()));
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

	public double getOpeningDebit() {
		return openingDebit;
	}
	public void setOpeningDebit(double openingDebit) {
		this.openingDebit = openingDebit;
	}

	public double getOpeningCredit() {
		return openingCredit;
	}
	public void setOpeningCredit(double openingCredit) {
		this.openingCredit = openingCredit;
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

	public void print(PrintStream out) {
		for (Summary s : getSummaryList()) {
			out.println(s.toString());
		}
	}

	public double getUnpaidBalance() {
		if (CommonUtil.round(getInitialDebit() + getDebit()) > CommonUtil.round(getInitialCredit() + getCredit())) {
			return CommonUtil.round(getInitialDebit() + getDebit() - getInitialCredit() - getCredit());
		}
		return 0;
	}
	public double getCreditBalance() {
		if (CommonUtil.round(getInitialCredit() + getCredit()) > CommonUtil.round(getInitialDebit() + getDebit())) {
			return CommonUtil.round(getInitialCredit() + getCredit() - getInitialDebit() - getDebit());
		}
		return 0;
	}
	public double getPeriodUnpaidBalance() {
		if (CommonUtil.round(getOpeningDebit() + getDebit()) > CommonUtil.round(getOpeningCredit() + getCredit())) {
			return CommonUtil.round(getOpeningDebit() + getDebit() - getOpeningCredit() - getCredit());
		}
		return 0;
	}
	public double getPeriodCreditBalance() {
		if (CommonUtil.round(getOpeningCredit() + getCredit()) > CommonUtil.round(getOpeningDebit() + getDebit())) {
			return CommonUtil.round(getOpeningCredit() + getCredit() - getOpeningDebit() - getDebit());
		}
		return 0;
	}
	public double getInitialUnpaidBalance() {
		if (getInitialDebit() > getInitialCredit()) {
			return CommonUtil.round(getInitialDebit() - getInitialCredit());
		}
		return 0;
	}
	public double getInitialCreditBalance() {
		if (getInitialCredit() > getInitialDebit()) {
			return CommonUtil.round(getInitialCredit() - getInitialDebit());
		}
		return 0;
	}

}
