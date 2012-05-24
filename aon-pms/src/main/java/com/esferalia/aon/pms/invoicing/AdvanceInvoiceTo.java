package com.esferalia.aon.pms.invoicing;

import java.util.Date;

import com.code.aon.config.PayMethod;
import com.code.aon.registry.RegistryBank;
import com.esferalia.aon.pms.reservation.IReservationConstants;

public class AdvanceInvoiceTo implements IReservationConstants {

	private boolean guestReservation;
	private Date issueDate;
	private double percent;
	private PayMethod payMethod;
	private RegistryBank registryBank;
	private Date financeDate;

	public boolean isGuestReservation() {
		return guestReservation;
	}
	public void setGuestReservation(boolean guestReservation) {
		this.guestReservation= guestReservation;
	}

	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}

	public PayMethod getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}
	
	public Date getFinanceDate() {
		return financeDate;
	}
	public void setFinanceDate(Date financeDate) {
		this.financeDate = financeDate;
	}

}
