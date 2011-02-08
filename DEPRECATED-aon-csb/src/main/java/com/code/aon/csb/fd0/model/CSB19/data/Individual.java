package com.code.aon.csb.fd0.model.CSB19.data;

import java.util.ArrayList;
import java.util.Iterator;

import com.code.aon.csb.fd0.core.Account;

public class Individual {

	private String referenceCode;
	private String name;
	private Account account;
	private Double amount;
	private String returnCode;
	private String internalCode;
	private String concept;
	private String accountUserName;
	private String accountUserAddress;
	private String accountUserAddress2;
	private Integer accountUserPCode;
	/**
	 * Concepts
	 */
	private ArrayList<String> concepts = new ArrayList<String>();
	
	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
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
	 * @return the internalCode
	 */
	public String getInternalCode() {
		return internalCode;
	}
	/**
	 * @param internalCode the internalCode to set
	 */
	public void setInternalCode(String internalCode) {
		this.internalCode = internalCode;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the referenceCode
	 */
	public String getReferenceCode() {
		return referenceCode;
	}
	/**
	 * @param referenceCode the referenceCode to set
	 */
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}
	/**
	 * @return the returnCode
	 */
	public String getReturnCode() {
		return returnCode;
	}
	/**
	 * @param returnCode the returnCode to set
	 */
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	
	/**
	 * Adds a concept
	 * 
	 * @param concept
	 */
	public void addConcept(String concept) {
		this.concepts.add(concept);
	}

	/**
	 * @return concepts iterator
	 */
	public Iterator<String> getConceptsIterator() {
		return this.concepts.iterator();
	}

	/**
	 * @return the concepts size
	 */
	public Integer getNumConcepts() {
		return concepts.size();
	}
	/**
	 * @return the accountUserAddress
	 */
	public String getAccountUserAddress() {
		return accountUserAddress;
	}
	/**
	 * @param accountUserAddress the accountUserAddress to set
	 */
	public void setAccountUserAddress(String accountUserAddress) {
		this.accountUserAddress = accountUserAddress;
	}
	/**
	 * @return the accountUserAddress2
	 */
	public String getAccountUserAddress2() {
		return accountUserAddress2;
	}
	/**
	 * @param accountUserAddress2 the accountUserAddress2 to set
	 */
	public void setAccountUserAddress2(String accountUserAddress2) {
		this.accountUserAddress2 = accountUserAddress2;
	}
	/**
	 * @return the accountUserName
	 */
	public String getAccountUserName() {
		return accountUserName;
	}
	/**
	 * @param accountUserName the accountUserName to set
	 */
	public void setAccountUserName(String accountUserName) {
		this.accountUserName = accountUserName;
	}
	/**
	 * @return the accountUserPCode
	 */
	public Integer getAccountUserPCode() {
		return accountUserPCode;
	}
	/**
	 * @param accountUserPCode the accountUserPCode to set
	 */
	public void setAccountUserPCode(Integer accountUserPCode) {
		this.accountUserPCode = accountUserPCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String description = "INDIVIDUAL ";
		description += "REF_CODE "+referenceCode+"; ";
		description += "NAME "+name+"; ";
		description += "ACCOUNT "+account.getCcc()+"; ";
		description += "AMOUNT "+amount+"; ";
		description += "RET_CODE "+returnCode+"; ";
		description += "INT_CODE "+internalCode+"; ";
		description += "CONCEPT "+concept+"; ";
		description += "ACCOUNT_1 "+accountUserName+"; ";
		description += "ACCOUNT_2 "+accountUserAddress+"; ";
		description += "ACCOUNT_3 "+accountUserAddress2+"; ";
		description += "ACCOUNT_4 "+accountUserPCode+"; ";
		return description;
	}

}
