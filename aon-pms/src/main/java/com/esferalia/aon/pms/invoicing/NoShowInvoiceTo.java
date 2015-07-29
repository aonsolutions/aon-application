package com.esferalia.aon.pms.invoicing;

import java.util.Date;

import com.code.aon.config.PayMethod;
import com.code.aon.finance.PosShift;
import com.code.aon.registry.RegistryBank;
import com.esferalia.aon.pms.reservation.IReservationConstants;

public class NoShowInvoiceTo implements IReservationConstants {

	private boolean guestReservation;
	private Date issueDate;
	private Integer penaltyDays;
	private PayMethod payMethod;
	private RegistryBank registryBank;
	private Date financeDate;
	private boolean keepAdvance;
	private PosShift posShift;

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

	public Integer getPenaltyDays() {
		return penaltyDays;
	}

	public void setPenaltyDays(Integer penaltyDays) {
		this.penaltyDays = penaltyDays;
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

	public boolean isKeepAdvance() {
		return keepAdvance;
	}
	public void setKeepAdvance(boolean keepAdvance) {
		this.keepAdvance = keepAdvance;
	}

	public PosShift getPosShift() {
		return posShift;
	}
	public void setPosShift(PosShift posShift) {
		this.posShift = posShift;
	}

}
