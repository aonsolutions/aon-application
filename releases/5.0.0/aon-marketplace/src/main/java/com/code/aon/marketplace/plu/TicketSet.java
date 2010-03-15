package com.code.aon.marketplace.plu;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicketSet {

	private Date saleDate;
	
	private List<Ticket> ticketlist = new ArrayList<Ticket>();
	
	public String getCode(){
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.format(saleDate);
	}
	
	public TicketSet(Date saleDate){
		super();
		this.saleDate = saleDate;
	}
	
	/**
	 * @return the saleDate
	 */
	public Date getSaleDate() {
		return saleDate;
	}

	/**
	 * @param saleDate the saleDate to set
	 */
	public void setSaleDate(Date saleDate) {
		this.saleDate = saleDate;
	}

	/**
	 * @return the details
	 */
	public List<Ticket> getTicketlist() {
		return ticketlist;
	}

	
	
}
