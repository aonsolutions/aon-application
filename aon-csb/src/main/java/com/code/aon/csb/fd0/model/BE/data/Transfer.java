package com.code.aon.csb.fd0.model.BE.data;

public class Transfer {

	/**
	 * Transfer sequence code  
	 */
	private Integer numberCode;
	/**
	 * Account
	 */
	private Account ccc;
	/**
	 * The trnsfer amount
	 */
	private Double amount;
	/**
	 * Recipients name part 1
	 */
	private String recipientNamePart1;
	/**
	 * Recipients name part 2
	 */
	private String recipientNamePart2;
	/**
	 * Recipients name part 3
	 */
	private String recipientNamePart3;
	/**
	 * Recipients address
	 */
	private String recipientAddress;
	/**
	 * Recipients county
	 */
	private String recipientCounty;
	/**
	 * Recipients country
	 */
	private String recipientCountry;
	/**
	 * Transfer concept part 1
	 */
	private String conceptPart1;
	/**
	 * Transfer concept part 2
	 */
	private String conceptPart2;
	/**
	 * Transfer account of person ident part 1
	 */
	private String accountOfPart1;
	/**
	 * Transfer account of person ident part 2
	 */
	private String accountOfPart2;
	/**
	 * Transfer type
	 */
	private String type;
	/**
	 * recipient's reference
	 */
	private String recipientReference;
	/**
	 * Transfer auth key
	 */
	private Integer authKey;
	/**
	 * recipient's document
	 */
	private String recipientDocument;
	/**
	 * recipient for reference
	 */
	private String recipientForReference;
	/**
	 * recipient's ident
	 */
	private String recipientIdent;
	
	/**
	 * @return the accountOfPart1
	 */
	public String getAccountOfPart1() {
		return accountOfPart1;
	}
	/**
	 * @param accountOfPart1 the accountOfPart1 to set
	 */
	public void setAccountOfPart1(String accountOfPart1) {
		this.accountOfPart1 = accountOfPart1;
	}
	/**
	 * @return the accountOfPart2
	 */
	public String getAccountOfPart2() {
		return accountOfPart2;
	}
	/**
	 * @param accountOfPart2 the accountOfPart2 to set
	 */
	public void setAccountOfPart2(String accountOfPart2) {
		this.accountOfPart2 = accountOfPart2;
	}
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
	 * @return the authKey
	 */
	public Integer getAuthKey() {
		return authKey;
	}
	/**
	 * @param authKey the authKey to set
	 */
	public void setAuthKey(Integer authKey) {
		this.authKey = authKey;
	}
	/**
	 * @return the ccc
	 */
	public Account getCcc() {
		return ccc;
	}
	/**
	 * @param ccc the ccc to set
	 */
	public void setCcc(Account ccc) {
		this.ccc = ccc;
	}
	/**
	 * @return the conceptPart1
	 */
	public String getConceptPart1() {
		return conceptPart1;
	}
	/**
	 * @param conceptPart1 the conceptPart1 to set
	 */
	public void setConceptPart1(String conceptPart1) {
		this.conceptPart1 = conceptPart1;
	}
	/**
	 * @return the conceptPart2
	 */
	public String getConceptPart2() {
		return conceptPart2;
	}
	/**
	 * @param conceptPart2 the conceptPart2 to set
	 */
	public void setConceptPart2(String conceptPart2) {
		this.conceptPart2 = conceptPart2;
	}
	/**
	 * @return the numberCode
	 */
	public Integer getNumberCode() {
		return numberCode;
	}
	/**
	 * @param numberCode the numberCode to set
	 */
	public void setNumberCode(Integer numberCode) {
		this.numberCode = numberCode;
	}
	/**
	 * @return the recipientAddress
	 */
	public String getRecipientAddress() {
		return recipientAddress;
	}
	/**
	 * @param recipientAddress the recipientAddress to set
	 */
	public void setRecipientAddress(String recipientAddress) {
		this.recipientAddress = recipientAddress;
	}
	/**
	 * @return the recipientCountry
	 */
	public String getRecipientCountry() {
		return recipientCountry;
	}
	/**
	 * @param recipientCountry the recipientCountry to set
	 */
	public void setRecipientCountry(String recipientCountry) {
		this.recipientCountry = recipientCountry;
	}
	/**
	 * @return the recipientCounty
	 */
	public String getRecipientCounty() {
		return recipientCounty;
	}
	/**
	 * @param recipientCounty the recipientCounty to set
	 */
	public void setRecipientCounty(String recipientCounty) {
		this.recipientCounty = recipientCounty;
	}
	/**
	 * @return the recipientDocument
	 */
	public String getRecipientDocument() {
		return recipientDocument;
	}
	/**
	 * @param recipientDocument the recipientDocument to set
	 */
	public void setRecipientDocument(String recipientDocument) {
		this.recipientDocument = recipientDocument;
	}
	/**
	 * @return the recipientForReference
	 */
	public String getRecipientForReference() {
		return recipientForReference;
	}
	/**
	 * @param recipientForReference the recipientForReference to set
	 */
	public void setRecipientForReference(String recipientForReference) {
		this.recipientForReference = recipientForReference;
	}
	/**
	 * @return the recipientIdent
	 */
	public String getRecipientIdent() {
		return recipientIdent;
	}
	/**
	 * @param recipientIdent the recipientIdent to set
	 */
	public void setRecipientIdent(String recipientIdent) {
		this.recipientIdent = recipientIdent;
	}
	/**
	 * @return the recipientNamePart1
	 */
	public String getRecipientNamePart1() {
		return recipientNamePart1;
	}
	/**
	 * @param recipientNamePart1 the recipientNamePart1 to set
	 */
	public void setRecipientNamePart1(String recipientNamePart1) {
		this.recipientNamePart1 = recipientNamePart1;
	}
	/**
	 * @return the recipientNamePart2
	 */
	public String getRecipientNamePart2() {
		return recipientNamePart2;
	}
	/**
	 * @param recipientNamePart2 the recipientNamePart2 to set
	 */
	public void setRecipientNamePart2(String recipientNamePart2) {
		this.recipientNamePart2 = recipientNamePart2;
	}
	/**
	 * @return the recipientNamePart3
	 */
	public String getRecipientNamePart3() {
		return recipientNamePart3;
	}
	/**
	 * @param recipientNamePart3 the recipientNamePart3 to set
	 */
	public void setRecipientNamePart3(String recipientNamePart3) {
		this.recipientNamePart3 = recipientNamePart3;
	}
	/**
	 * @return the recipientReference
	 */
	public String getRecipientReference() {
		return recipientReference;
	}
	/**
	 * @param recipientReference the recipientReference to set
	 */
	public void setRecipientReference(String recipientReference) {
		this.recipientReference = recipientReference;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String description = "LOT ";
		description = "NUMCODE "+numberCode;
		description = "CCC "+ccc+"; ";
		description = "AMOUNT "+amount+"; ";
		description = "RECIPIENT "+recipientNamePart1+"; ";
		description = " "+recipientNamePart2+"; ";
		description = " "+recipientNamePart3+"; ";
		description = " "+recipientAddress+"; ";
		description = " "+recipientCounty+"; ";
		description = " "+recipientCountry+"; ";
		description = " "+recipientDocument+"; ";
		description = " "+recipientIdent+"; ";
		description = "RECIPIENT_FOR "+recipientForReference+"; ";
		description = "CONCEPT "+conceptPart1+"; ";
		description = " "+conceptPart2+"; ";
		description = "ACCOUNT_OF "+accountOfPart1+"; ";
		description = " "+accountOfPart2+"; ";
		description = "TYPE "+type+"; ";
		description = "REFERENCE "+recipientReference+"; ";
		return description;
	}
	
	
}
