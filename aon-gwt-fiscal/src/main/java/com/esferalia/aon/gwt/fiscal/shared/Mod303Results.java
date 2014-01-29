package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class Mod303Results implements Serializable, IsSerializable {
	
	private double depositSum;
	private double paybackSum;
	private double lastPeriodCompensateResult;
	private double lastPeriodPaybackResult;
	
	private double nationalSales;
	private double reSales;
	private double intracommunitarySales;
	private double extracommunitarySales;
	private double withoutRightSales;
	private double ISPSales;
	private double investmentSales;
	
	
	public double getDepositSum() {
		return depositSum;
	}
	public void setDepositSum(double depositSum) {
		this.depositSum = depositSum;
	}
	public double getPaybackSum() {
		return paybackSum;
	}
	public void setPaybackSum(double paybackSum) {
		this.paybackSum = paybackSum;
	}
	public double getLastPeriodCompensateResult() {
		return lastPeriodCompensateResult;
	}
	public void setLastPeriodCompensateResult(double lastPeriodCompensateResult) {
		this.lastPeriodCompensateResult = lastPeriodCompensateResult;
	}
	public double getLastPeriodPaybackResult() {
		return lastPeriodPaybackResult;
	}
	public void setLastPeriodPaybackResult(double lastPeriodPaybackResult) {
		this.lastPeriodPaybackResult = lastPeriodPaybackResult;
	}
	public double getNationalSales() {
		return nationalSales;
	}
	public void setNationalSales(double nationalSales) {
		this.nationalSales = nationalSales;
	}
	public double getReSales() {
		return reSales;
	}
	public void setReSales(double reSales) {
		this.reSales = reSales;
	}
	public double getIntracommunitarySales() {
		return intracommunitarySales;
	}
	public void setIntracommunitarySales(double intracommunitarySales) {
		this.intracommunitarySales = intracommunitarySales;
	}
	public double getExtracommunitarySales() {
		return extracommunitarySales;
	}
	public void setExtracommunitarySales(double extracommunitarySales) {
		this.extracommunitarySales = extracommunitarySales;
	}
	public double getWithoutRightSales() {
		return withoutRightSales;
	}
	public void setWithoutRightSales(double withoutRightSales) {
		this.withoutRightSales = withoutRightSales;
	}
	public double getISPSales() {
		return ISPSales;
	}
	public void setISPSales(double iSPSales) {
		ISPSales = iSPSales;
	}
	public double getInvestmentSales() {
		return investmentSales;
	}
	public void setInvestmentSales(double investmentSales) {
		this.investmentSales = investmentSales;
	}
}
