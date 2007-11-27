package com.code.aon.csb.fd0.model.CSB34.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.code.aon.csb.fd0.core.Account;

/**
 * Master object data
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class Master {
	
	/**
	 * Orderer
	 */
	private Orderer orderer;
	
	/**
	 * Operator
	 */
	private Orderer operator;
	
	/**
	 * Account
	 */
	private Account account;
	
	/**
	 * Sending sate
	 */
	private Date sendDate;
	
	/**
	 * Ordering date
	 */
	private Date orderDate;
	
	/**
	 * Detail
	 */
	private String detail;
	
	/**
	 * Who pays costs 1-orderer 2-receiver 
	 */
	private String costs;
	
	/**
	 * Number of registries
	 */
	private int numreg;
	
	/**
	 * Total amount 
	 */
	private double amount;

	/**
	 * Nomber of 010 type registries
	 */
	private int num010;

	/**
	 * All the receivers, detail objects 
	 */
	private ArrayList<Detail> receivers = new ArrayList<Detail>();

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
	 * Adds a receiver
	 * 
	 * @param receiver a receiver
	 */
	public void addReceiver(Detail receiver){
		this.receivers.add(receiver);
	}

	/**
	 * @return receivers array iterator
	 */
	public Iterator<Detail> getReceiversIterator(){
		return this.receivers.iterator();
	}

	/**
	 * @return the orderDate
	 */
	public Date getOrderDate() {
		return orderDate;
	}

	/**
	 * @param orderDate the orderDate to set
	 */
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	/**
	 * @return the sendDate
	 */
	public Date getSendDate() {
		return sendDate;
	}

	/**
	 * @param sendDate the sendDate to set
	 */
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	/**
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}

	/**
	 * @param detail the detail to set
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}

	/**
	 * @return the operator
	 */
	public Orderer getOperator() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(Orderer operator) {
		this.operator = operator;
	}

	/**
	 * @return the orderer
	 */
	public Orderer getOrderer() {
		return orderer;
	}

	/**
	 * @param orderer the orderer to set
	 */
	public void setOrderer(Orderer orderer) {
		this.orderer = orderer;
	}

	/**
	 * @return the costs
	 */
	public String getCosts() {
		return costs;
	}

	/**
	 * @param costs the costs to set
	 */
	public void setCosts(String costs) {
		this.costs = costs;
	}

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
	 * @return the num010
	 */
	public int getNum010() {
		return num010;
	}

	/**
	 * @param num010 the num010 to set
	 */
	public void setNum010(int num010) {
		this.num010 = num010;
	}

	/**
	 * @return the numreg
	 */
	public int getNumreg() {
		return numreg;
	}

	/**
	 * @param numreg the numreg to set
	 */
	public void setNumreg(int numreg) {
		this.numreg = numreg;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String description = "ORDERER ";
		description += orderer == null?"NULL ":"'"+orderer.getCode()+"','"+orderer.getName()+"'; ";
		description += "OPERATOR ";
		description += operator == null?"NULL ":"'"+operator.getCode()+"''"+orderer.getName()+"'; ";
		description += "ACCOUNT ";
		description += account == null?"NULL ":"'"+account.getCcc()+"'; ";
		description += "SEND DATE "+sendDate==null?"NULL":"'"+sendDate+"'; ";
		description += "ORDER DATE "+orderDate==null?"NULL":"'"+orderDate+"'; ";
		description += "DETAIL "+detail==null?"NULL":"'"+detail+"'; ";
		return description;
	}

}
