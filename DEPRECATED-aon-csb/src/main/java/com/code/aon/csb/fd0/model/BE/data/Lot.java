package com.code.aon.csb.fd0.model.BE.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * A Lot
 * 
 * @author Consulting & Development. Iñigo GAyarre - 20/02/2007
 * @since 1.0
 *
 */
public class Lot {

	/**
	 * The presenter's code 
	 */
	private Integer presenterCode;
	/**
	 * The application code
	 */
	private Integer aplicationCode;
	/**
	 * Presenter's name
	 */
	private String presenterName;
	/**
	 * Responsible's name
	 */
	private String responsibleName;
	/**
	 * Responsible's phone number
	 */
	private Integer responsiblePhone;
	/**
	 * Lot generation date
	 */
	private Date generationDate;
	/**
	 * Lot number
	 */
	private Integer number;
	/**
	 * Transfer's Authentication Key sumatory
	 */
	private Integer sumKey;
	/**
	 * Register number
	 */
	private Integer registerNumber;
	/**
	 * Relationships
	 */
	private ArrayList<Relationship> relationships = new ArrayList<Relationship>();

	/**
	 * Adds a Relationship
	 * 
	 * @param relationship
	 */
	public void addRelationship(Relationship relationship) {
		this.relationships.add(relationship);
	}

	/**
	 * @return relationships iterator
	 */
	public Iterator<Relationship> getRelationshipsIterator() {
		return this.relationships.iterator();
	}

	/**
	 * @return the relationships size
	 */
	public Integer getNumRelationships() {
		return relationships.size();
	}


	/**
	 * @return the amount
	 */
	public Double getAmount() {
		Iterator<Relationship> iter = this.getRelationshipsIterator();
		double total = 0.0;
		while (iter.hasNext()){
			total += iter.next().getTransferAmount().doubleValue();
		}
		return total;
	}
	/**
	 * @return the aplicationCode
	 */
	public Integer getAplicationCode() {
		return aplicationCode;
	}
	/**
	 * @param aplicationCode the aplicationCode to set
	 */
	public void setAplicationCode(Integer aplicationCode) {
		this.aplicationCode = aplicationCode;
	}
	/**
	 * @return the generationDate
	 */
	public Date getGenerationDate() {
		return generationDate;
	}
	/**
	 * @param generationDate the generationDate to set
	 */
	public void setGenerationDate(Date generationDate) {
		this.generationDate = generationDate;
	}
	/**
	 * @return the number
	 */
	public Integer getNumber() {
		return number;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}
	/**
	 * @return the numRelations
	 */
	public Integer getNumRelations() {
		return new Integer(this.relationships.size());
	}
	/**
	 * @return the numTransfers
	 */
	public Integer getNumTransfers() {
		Iterator<Relationship> iter = this.getRelationshipsIterator();
		int total = 0;
		while (iter.hasNext()){
			total += iter.next().getTransferCounter().intValue();
		}
		return total;
	}
	/**
	 * @return the presenterCode
	 */
	public Integer getPresenterCode() {
		return presenterCode;
	}
	/**
	 * @param presenterCode the presenterCode to set
	 */
	public void setPresenterCode(Integer presenterCode) {
		this.presenterCode = presenterCode;
	}
	/**
	 * @return the presenterName
	 */
	public String getPresenterName() {
		return presenterName;
	}
	/**
	 * @param presenterName the presenterName to set
	 */
	public void setPresenterName(String presenterName) {
		this.presenterName = presenterName;
	}
	/**
	 * @return the responsibleName
	 */
	public String getResponsibleName() {
		return responsibleName;
	}
	/**
	 * @param responsibleName the responsibleName to set
	 */
	public void setResponsibleName(String responsibleName) {
		this.responsibleName = responsibleName;
	}
	/**
	 * @return the responsiblePhone
	 */
	public Integer getResponsiblePhone() {
		return responsiblePhone;
	}
	/**
	 * @param responsiblePhone the responsiblePhone to set
	 */
	public void setResponsiblePhone(Integer responsiblePhone) {
		this.responsiblePhone = responsiblePhone;
	}
	/**
	 * @return the sumKey
	 */
	public Integer getSumKey() {
		return sumKey;
	}
	/**
	 * @param sumKey the sumKey to set
	 */
	public void setSumKey(Integer sumKey) {
		this.sumKey = sumKey;
	}

	/**
	 * @return the registerNumber
	 */
	public Integer getRegisterNumber() {
		return registerNumber;
	}

	/**
	 * @param registerNumber the registerNumber to set
	 */
	public void setRegisterNumber(Integer registerNumber) {
		this.registerNumber = registerNumber;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String description = "LOT ";
		description += "DESC "+presenterCode+"; ";
		description += "CODE "+aplicationCode+"; ";
		description += "NAME "+presenterName+"; ";
		description += "RESP_NAME"+responsibleName+"; ";
		description += "DATE "+generationDate+"; ";
		description += "NUM "+number+"; ";
		return description;
	}

}
