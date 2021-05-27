package com.esferalia.aon.pms.invoicing;

import java.io.Serializable;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.PayMethod;

public class TotalSummaryTo implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private PayMethod payMethod;
	private double reservationAmount;
	private double servicesAmount;

	public PayMethod getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	public double getReservationAmount() {
		return reservationAmount;
	}

	public void setReservationAmount(double reservationAmount) {
		this.reservationAmount = reservationAmount;
	}

	public double getServicesAmount() {
		return servicesAmount;
	}

	public void setServicesAmount(double servicesAmount) {
		this.servicesAmount = servicesAmount;
	}

	public double getTotalAmount() {
		return CommonUtil.round(getReservationAmount() + getServicesAmount());
	}

	public double getReservationAmountAbs() {
		return Math.abs(getReservationAmount());
	}

	public double getServicesAmountAbs() {
		return Math.abs(getServicesAmount());
	}

	public double getTotalAmountAbs() {
		return Math.abs(getTotalAmount());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TotalSummaryTo) {
			TotalSummaryTo summaryTo = (TotalSummaryTo)obj;
			if (this.getPayMethod() == null) {
				return summaryTo.getPayMethod() == null;
			}
			return this.getPayMethod().equals(summaryTo.getPayMethod());
		}
		return false;
	}

}
