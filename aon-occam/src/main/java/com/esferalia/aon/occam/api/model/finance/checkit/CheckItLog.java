package com.esferalia.aon.occam.api.model.finance.checkit;

import java.io.Serializable;
import java.util.Date;

public class CheckItLog implements Serializable {
	
	private static final long serialVersionUID = -3938162242612468636L;
	
	private Integer bankAccountId;
	private String errorMessage;
	private boolean userError;
	private boolean pending;
	private Date created;
	public Integer getBankAccountId() {
		return bankAccountId;
	}
	public CheckItLog setBankAccountId(Integer bankAccountId) {
		this.bankAccountId = bankAccountId;
		return this;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public CheckItLog setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}
	public boolean isUserError() {
		return userError;
	}
	public CheckItLog setUserError(boolean userError) {
		this.userError = userError;
		return this;
	}
	public boolean isPending() {
		return pending;
	}
	public CheckItLog setPending(boolean pending) {
		this.pending = pending;
		return this;
	}
	public Date getCreated() {
		return created;
	}
	public CheckItLog setCreated(Date created) {
		this.created = created;
		return this;
	}
		
}
