package com.esferalia.aon.pms.invoicing;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.PayMethod;

public class PaymentSummaryTo {

	private PayMethod payMethod;
	private double reservationReturnAmount;
	private double servicesReturnAmount;
	private double paymentAmount;

	public PayMethod getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	public double getReservationReturnAmount() {
		return reservationReturnAmount;
	}

	public void setReservationReturnAmount(double reservationReturnAmount) {
		this.reservationReturnAmount = reservationReturnAmount;
	}

	public double getServicesReturnAmount() {
		return servicesReturnAmount;
	}

	public void setServicesReturnAmount(double servicesReturnAmount) {
		this.servicesReturnAmount = servicesReturnAmount;
	}

	public double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public double getTotalReturnAmount() {
		return CommonUtil.round(getReservationReturnAmount() + getServicesReturnAmount());
	}

	public double getTotalAmount() {
		return CommonUtil.round(getPaymentAmount() - getReservationReturnAmount() - getServicesReturnAmount());
	}

	public double getTotalAmountAbs() {
		return Math.abs(getTotalAmount());
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
