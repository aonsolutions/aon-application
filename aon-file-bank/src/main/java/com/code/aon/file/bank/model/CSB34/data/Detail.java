package com.code.aon.file.bank.model.CSB34.data;

import com.code.aon.file.format.core.Account;

public abstract class Detail {

	private Receiver receiver;
	private Account account;
	private double amount;
	private String concept;

	public abstract String getType();

	public Receiver getReceiver() {
		return receiver;
	}
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}
	public String getConceptPart1() {
		if (concept.length() > 36)
			return concept.substring(0, 36);
		return concept;
	}
	public String getConceptPart2() {
		if (concept.length() > 36) {
			if (concept.length() > 72)
				return concept.substring(36, 72);
			return concept.substring(36);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String description = "RECEIVER ";
		description += receiver == null?"NULL ":"'"+receiver.getCode()+","+receiver.getName()+"'; ";
		description += "ACCOUNT ";
		description += account == null?"NULL ":"'"+account.getCcc()+"'; ";
		description += "AMOUNT '"+amount+"'; ";
		description += "CONCEPT "+concept==null?"NULL":"'"+concept+"'; ";
		return description;
	}
}
