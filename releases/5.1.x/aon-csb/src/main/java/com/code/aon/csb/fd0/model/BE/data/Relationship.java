package com.code.aon.csb.fd0.model.BE.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Relationship {

	/**
	 * The emitter code
	 */
	private Integer emitterCode;
	/**
	 * Order number
	 */
	private Integer orderNumber;
	/**
	 * Emitter requested date
	 */
	private Date executionDate;
	/**
	 * Key sumatory 
	 */
	private Integer transferSumKey;
	
	/**
	 * Transfers
	 */
	private ArrayList<Transfer> transfers = new ArrayList<Transfer>();

	/**
	 * Adds a Transfer
	 * 
	 * @param transfer
	 */
	public void addTransfer(Transfer transfer) {
		this.transfers.add(transfer);
	}

	/**
	 * @return transfers iterator
	 */
	public Iterator<Transfer> getTransfersIterator() {
		return this.transfers.iterator();
	}

	/**
	 * @return the emitterCode
	 */
	public Integer getEmitterCode() {
		return emitterCode;
	}
	/**
	 * @param emitterCode the emitterCode to set
	 */
	public void setEmitterCode(Integer emitterCode) {
		this.emitterCode = emitterCode;
	}
	/**
	 * @return the executionDate
	 */
	public Date getExecutionDate() {
		return executionDate;
	}
	/**
	 * @param executionDate the executionDate to set
	 */
	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}
	/**
	 * @return the orderNumber
	 */
	public Integer getOrderNumber() {
		return orderNumber;
	}
	/**
	 * @param orderNumber the orderNumber to set
	 */
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	/**
	 * @return the transferAmount
	 */
	public Double getTransferAmount() {
		Iterator<Transfer> iter = this.getTransfersIterator();
		double total = 0;
		while (iter.hasNext()){
			total += iter.next().getAmount().doubleValue();
		}
		return total;
	}
	/**
	 * @return the transferCounter
	 */
	public Integer getTransferCounter() {
		return new Integer(this.transfers.size());
	}
	/**
	 * @return the transferSumKey
	 */
	public Integer getTransferSumKey() {
		return transferSumKey;
	}
	/**
	 * @param transferSumKey the transferSumKey to set
	 */
	public void setTransferSumKey(Integer transferSumKey) {
		this.transferSumKey = transferSumKey;
	}

	
}
