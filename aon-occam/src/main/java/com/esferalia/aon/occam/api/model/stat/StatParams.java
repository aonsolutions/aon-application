package com.esferalia.aon.occam.api.model.stat;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

public class StatParams implements Serializable {
	
	private static final long serialVersionUID = 8321751053437854437L;
	
	private String domainName;
	private int domain;
	private String user;
	
	private LinkedList<SelectableEnum<InvoiceType>> invoiceTypes;
	private Date from;
	private Date to;

	private LinkedList<ProductCategory> productCategories;
	
	public StatParams(){
		
	}
	
	public StatParams(String domainName,int domain,String user){
		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
	}
	public String getDomainName() {
		return domainName;
	}
	public int getDomain() {
		return domain;
	}
	public String getUser() {
		return user;
	}
	public LinkedList<SelectableEnum<InvoiceType>> getInvoiceTypes() {
		return invoiceTypes;
	}
	public StatParams setInvoiceTypes(LinkedList<SelectableEnum<InvoiceType>> invoiceTypes) {
		this.invoiceTypes = invoiceTypes;
		return this;
	}
	public LinkedList<ProductCategory> getProductCategories() {
		return productCategories;
	}
	public StatParams setProductCategories(LinkedList<ProductCategory> productCategories) {
		this.productCategories = productCategories;
		return this;
	}

	public Date getFrom() {
		return from;
	}
	public StatParams setFrom(Date from) {
		this.from = from;
		return this;
	}
	public Date getTo() {
		return to;
	}
	public StatParams setTo(Date to) {
		this.to = to;
		return this;
	}
	
	
}
