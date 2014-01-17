package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Mod303Results implements Serializable, IsSerializable {
	
	private double depositSum;
	private double paybackSum;
	private double lastPeriodCompensateResult;
	private double lastPeriodPaybackResult;
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
	
}
