package com.esferalia.aon.pms.card;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;

public class CardOperationTo implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String cardNumber;
	private Date operationDate;
	private double operationAmount;
	private boolean operationOk;

	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public Date getOperationDate() {
		return operationDate;
	}
	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

	public double getOperationAmount() {
		return operationAmount;
	}
	public void setOperationAmount(double operationAmount) {
		this.operationAmount = operationAmount;
	}

	public boolean isOperationOk() {
		return operationOk;
	}
	public void setOperationOk(boolean operationOk) {
		this.operationOk = operationOk;
	}

}
