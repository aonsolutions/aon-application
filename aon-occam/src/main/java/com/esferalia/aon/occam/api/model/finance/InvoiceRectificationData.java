package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;

public class InvoiceRectificationData implements Serializable {
	
	private static final long serialVersionUID = 8538056747286647779L;
	
	private InvoiceType type;
	private String series;
	private int number;
	private String referenceCode;
	private Date issueDate;
	private String cause; 
	private RectificationType rectificationtype;
	private boolean settleFinances;
	
	public InvoiceType getType() {
		return type;
	}
	public InvoiceRectificationData setType(InvoiceType type) {
		this.type = type;
		return this;
	}
	public boolean isSales() {
		return type == InvoiceType.SALES;
	}
	
	public String getSeries() {
		return series;
	}
	public InvoiceRectificationData setSeries(String series) {
		this.series = series;
		return this;
	}
	public int getNumber() {
		return number;
	}
	public InvoiceRectificationData setNumber(int number) {
		this.number = number;
		return this;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public InvoiceRectificationData setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public InvoiceRectificationData setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	public String getCause() {
		return cause;
	}
	public InvoiceRectificationData setCause(String cause) {
		this.cause = cause;
		return this;
	}
	public RectificationType getRectificationtype() {
		return rectificationtype;
	}
	public InvoiceRectificationData setRectificationtype(RectificationType rectificationtype) {
		this.rectificationtype = rectificationtype;
		return this;
	}
	public boolean isSettleFinances() {
		return settleFinances;
	}
	public InvoiceRectificationData setSettleFinances(boolean settleFinances) {
		this.settleFinances = settleFinances;
		return this;
	}
}
