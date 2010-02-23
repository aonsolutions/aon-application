package com.code.aon.file.bank.model.CSB34.data;

import com.code.aon.file.format.core.Account;


/**
 * 
 * Detail lines data object
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public abstract class Detail {

	/**
	 * The receiver 
	 */
	private Receiver receiver;

	/**
	 * Receivers account
	 */
	private Account account;

	/**
	 * The amount
	 */
	private double amount;
	
	/**
	 * The concept
	 */
	private String concept;
	
	/**
	 * The concept´s description
	 */
	private String conceptDesc;
	
	/**
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	
	/**
	 * @return the receiver
	 */
	public Receiver getReceiver() {
		return receiver;
	}

	/**
	 * @param receiver the receiver to set
	 */
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public abstract String getType();

	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * @return the concept
	 */
	public String getConcept() {
		return concept;
	}

	/**
	 * @param concept the concept to set
	 */
	public void setConcept(String concept) {
		this.concept = concept;
	}

	/**
	 * @return the conceptDesc
	 */
	public String getConceptDesc() {
		return conceptDesc;
	}

	/**
	 * @param conceptDesc the conceptDesc to set
	 */
	public void setConceptDesc(String conceptDesc) {
		this.conceptDesc = conceptDesc;
	}

	/**
	 * @return the conceptDesc substring
	 */
	public String getConceptDescPart1() {
		if (conceptDesc.length()>36)return conceptDesc.substring(0, 36);
		return conceptDesc;
	}

	/**
	 * @return the conceptDesc substring
	 */
	public String getConceptDescPart2() {
		if (conceptDesc.length()>36){
			if (conceptDesc.length()>72)return conceptDesc.substring(36,72);
			return conceptDesc.substring(36);
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
		description += "CONCEPT_DESC "+conceptDesc==null?"NULL":"'"+conceptDesc+"'; ";
		return description;
	}
}
