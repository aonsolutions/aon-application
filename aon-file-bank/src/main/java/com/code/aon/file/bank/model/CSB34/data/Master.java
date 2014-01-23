package com.code.aon.file.bank.model.CSB34.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.code.aon.file.format.core.Account;

public class Master {
	
	private Orderer orderer;
	private Account account;
	private Orderer operator;
	private Date sendDate;
	private Date orderDate;
	private String detail;
	private double amount;
	private int num010;
	private int numreg;
	private ArrayList<Detail> receivers = new ArrayList<Detail>();
	private String id;
	private String companyId;

	public Orderer getOrderer() {
		return orderer;
	}
	public void setOrderer(Orderer orderer) {
		this.orderer = orderer;
	}

	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	public Orderer getOperator() {
		return operator;
	}
	public void setOperator(Orderer operator) {
		this.operator = operator;
	}

	public Date getSendDate() {
		return sendDate;
	}
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}

	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getNum010() {
		return num010;
	}
	public void setNum010(int num010) {
		this.num010 = num010;
	}

	public int getNumreg() {
		return numreg;
	}
	public void setNumreg(int numreg) {
		this.numreg = numreg;
	}

	public void addReceiver(Detail receiver){
		this.receivers.add(receiver);
	}
	
	public Iterator<Detail> getReceiversIterator(){
		return this.receivers.iterator();
	}
	
	public ArrayList<Detail> getReceivers() {
		return receivers;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
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
