package com.esferalia.aon.pms.invoicing;

import java.io.Serializable;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.PayMethod;

public class PaymentSummaryTo implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private PayMethod payMethod;
	private boolean service;
	private double returnAmount;
	private double paymentAmount;

	public PayMethod getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	public boolean isService() {
		return service;
	}

	public void setService(boolean service) {
		this.service = service;
	}

	public double getReturnAmount() {
		return returnAmount;
	}

	public void setReturnAmount(double returnAmount) {
		this.returnAmount = returnAmount;
	}

	public double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public double getLiquidationAmount() {
		return CommonUtil.round(getPaymentAmount() - getReturnAmount());
	}

	public double getLiquidationAmountAbs() {
		return Math.abs(getLiquidationAmount());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PaymentSummaryTo) {
			PaymentSummaryTo summaryTo = (PaymentSummaryTo)obj;
			if (this.getPayMethod() == null) {
				return summaryTo.getPayMethod() == null;
			}
			return this.getPayMethod().equals(summaryTo.getPayMethod());
		}
		return false;
	}

}
