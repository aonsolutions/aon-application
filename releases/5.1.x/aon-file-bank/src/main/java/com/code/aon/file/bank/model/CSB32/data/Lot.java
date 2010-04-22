package com.code.aon.file.bank.model.CSB32.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Lot {
	
	/**
	 * Date of the lot
	 */
	private Date fileDate;
	
	/**
	 * Number of this lot, multiple files
	 */
	private Integer fileNumber;
	
	/**
	 * The entity that receives the file
	 */
	private Integer entity;
	
	/**
	 * The office that receives the file
	 */
	private Integer office;

	/**
	 * Deliverys
	 */
	private ArrayList<Delivery> deliveries = new ArrayList<Delivery>();

	
	/**
	 * @return the entity
	 */
	public Integer getEntity() {
		return entity;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(Integer entity) {
		this.entity = entity;
	}

	/**
	 * @return the fileDate
	 */
	public Date getFileDate() {
		return fileDate;
	}

	/**
	 * @param fileDate the fileDate to set
	 */
	public void setFileDate(Date fileDate) {
		this.fileDate = fileDate;
	}

	/**
	 * @return the fileNumber
	 */
	public Integer getFileNumber() {
		return fileNumber;
	}

	/**
	 * @param fileNumber the fileNumber to set
	 */
	public void setFileNumber(Integer fileNumber) {
		this.fileNumber = fileNumber;
	}

	/**
	 * @return the office
	 */
	public Integer getOffice() {
		return office;
	}

	/**
	 * @param office the office to set
	 */
	public void setOffice(Integer office) {
		this.office = office;
	}

	/**
	 * Adds a Delivery 
	 * 
	 * @param delivery
	 */
	public void addDelivery(Delivery delivery) {
		this.deliveries.add(delivery);
	}

	/**
	 * @return deliveries iterator
	 */
	public Iterator<Delivery> getDeliveriesIterator() {
		return this.deliveries.iterator();
	}

	/**
	 * @return the deliveries size
	 */
	public Integer getNumDeliveries() {
		return deliveries.size();
	}


	/**
	 * @return the amount
	 */
	public Double getAmount() {
		Iterator<Delivery> iter = this.getDeliveriesIterator();
		double total = 0.0;
		while (iter.hasNext()){
			total += iter.next().getAmount();
		}
		return total;
	}

	/**
	 * @return the number of individuals
	 */
	public int getNumDeliverys() {
		Iterator<Delivery> iter = this.getDeliveriesIterator();
		int total = 0;
		while (iter.hasNext()){
			iter.next();
			total++;
		}
		return total;
	}

	/**
	 * @return the number of individuals
	 */
	public int getNumEfects() {
		Iterator<Delivery> iter = this.getDeliveriesIterator();
		int total = 0;
		while (iter.hasNext()){
			total += iter.next().getNumIndividuals();
		}
		return total;
	}

	public int getNumRegs() {
		Iterator<Delivery> iter = this.getDeliveriesIterator();
		int total = 2;
		while (iter.hasNext()){
			total += iter.next().getNumRegs();
		}
		return total;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String description = "LOT ";
		description += "DATE "+fileDate+"; ";
		description += "NUMBER "+fileNumber+"; ";
		description += "ENTITY "+entity+"; ";
		description += "OFFICE "+office+"; ";
		return description;
	}

}
