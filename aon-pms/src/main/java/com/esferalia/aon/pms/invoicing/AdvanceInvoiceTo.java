package com.esferalia.aon.pms.invoicing;

import java.util.Date;

import com.code.aon.config.PayMethod;
import com.code.aon.registry.RegistryBank;
import com.esferalia.aon.pms.reservation.IReservationConstants;

public class AdvanceInvoiceTo implements IReservationConstants {

	private Date issueDate;
	private Date financeDate;
	private double percent;
	private boolean guestReservation;
	private PayMethod payMethod;
	private RegistryBank rbank;
	
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
	public Date getFinanceDate() {
		return financeDate;
	}
	public void setFinanceDate(Date financeDate) {
		this.financeDate = financeDate;
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
	public RegistryBank getRbank() {
		return rbank;
	}
	public void setRbank(RegistryBank rbank) {
		this.rbank = rbank;
	}
	
}
