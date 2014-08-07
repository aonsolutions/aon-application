package com.code.aon.stat.tas;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.Country;
import com.code.aon.registry.enumeration.DocumentType;


public class TasStatDetail implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Integer id;
	private String series;
	private Integer number;
	private String referenceCode;
	private Date date;
	private String document;
	private DocumentType documentType;
	private Country documentCountry;
	private String name;
	private String taskHolderName;
	private String comments;
	private String status;
	private TasStatDetailType type;
	private double total;
	
    public TasStatDetail(TasStatDetailType type) {
    	this.type = type;
	}
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}

	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	
	public DocumentType getDocumentType() {
		return documentType;
	}
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}
	
	public Country getDocumentCountry() {
		return documentCountry;
	}
	public void setDocumentCountry(Country documentCountry) {
		this.documentCountry = documentCountry;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTaskHolderName() {
		return taskHolderName;
	}
	public void setTaskHolderName(String taskHolderName) {
		this.taskHolderName = taskHolderName;
	}
	
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public TasStatDetailType getType() {
		return type;
	}
	public void setType(TasStatDetailType type) {
		this.type = type;
	}

	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}

	public String getReferenceCode() {
		if (StringUtils.isEmpty(referenceCode)) {
			referenceCode = StringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
			if (!StringUtils.isEmpty(getSeries())) {
				referenceCode = getSeries() + "/" + referenceCode;
			}
		}
    	return referenceCode;
    }
	
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public String getFullDocument() {
		return getDocumentType().toString() + "/" + 
			getDocumentCountry().getValue() + " " +
			getDocument();
	}
	
	public boolean isProject() {
		return getType() == TasStatDetailType.PROJECT;
	}
	public boolean isOffer() {
		return getType() == TasStatDetailType.OFFER;
	}
	public boolean isSaleInvoice() {
		return getType() == TasStatDetailType.SALES_INVOICE;
	}
	public boolean isPurchaseInvoice() {
		return getType() == TasStatDetailType.PURCHASE_INVOICE;
	}
	public boolean isExpenseInvoice() {
		return getType() == TasStatDetailType.EXPENSE_INVOICE;
	}
	public boolean isSales() {
		return getType() == TasStatDetailType.SALES;
	}
	public boolean isPurchase() {
		return getType() == TasStatDetailType.PURCHASE;
	}
	public boolean isDelivery() {
		return getType() == TasStatDetailType.DELIVERY;
	}
	public boolean isIncome() {
		return getType() == TasStatDetailType.INCOME;
	}
	
	
}
