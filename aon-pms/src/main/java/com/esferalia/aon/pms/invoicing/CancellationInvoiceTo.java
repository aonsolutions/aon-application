package com.esferalia.aon.pms.invoicing;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.PosShift;
import com.code.aon.product.Item;
import com.code.aon.registry.RegistryBank;

public class CancellationInvoiceTo implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean guestReservation;
	private Date issueDate;
	private Item item;
	private Integer penaltyDays;
	private PayMethod payMethod;
	private PayMethod conexFlowPayMethod;
	private RegistryBank registryBank;
	private Date financeDate;
	private boolean keepAdvance;
	private boolean manual;
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

	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
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

	public boolean isKeepAdvance() {
		return keepAdvance;
	}
	public void setKeepAdvance(boolean keepAdvance) {
		this.keepAdvance = keepAdvance;
	}

	public boolean isManual() {
		return manual;
	}
	public void setManual(boolean manual) {
		this.manual = manual;
	}

	public PosShift getPosShift() {
		return posShift;
	}
	public void setPosShift(PosShift posShift) {
		this.posShift = posShift;
	}

}
