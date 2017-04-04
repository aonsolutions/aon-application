package com.esferalia.aon.pms.invoicing;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.PosShift;
import com.code.aon.product.Item;
import com.code.aon.registry.RegistryBank;

public class AdvanceInvoiceTo implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private ReservationInvoiceTo reservationInvoiceTo;
	private boolean guestReservation;
	private Date issueDate;
	private Item item;
	private double percent;
	private double amount;
	private PayMethod payMethod;
	private PayMethod conexFlowPayMethod;
	private RegistryBank registryBank;
	private Date financeDate;
	private PosShift posShift;

	public ReservationInvoiceTo getReservationInvoiceTo() {
		return reservationInvoiceTo;
	}
	public void setReservationInvoiceTo(ReservationInvoiceTo reservationInvoiceTo) {
		this.reservationInvoiceTo = reservationInvoiceTo;
	}

	public boolean isGuestReservation() {
		return guestReservation;
	}
	public void setGuestReservation(boolean guestReservation) {
		this.guestReservation = guestReservation;
	}

	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}

	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}

	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public PayMethod getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	public PayMethod getConexFlowPayMethod() {
		return conexFlowPayMethod;
	}
	public void setConexFlowPayMethod(PayMethod conexFlowPayMethod) {
		this.conexFlowPayMethod = conexFlowPayMethod;
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

	public PosShift getPosShift() {
		return posShift;
	}
	public void setPosShift(PosShift posShift) {
		this.posShift = posShift;
	}

}
