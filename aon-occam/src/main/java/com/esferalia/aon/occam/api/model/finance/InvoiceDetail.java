package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;

public class InvoiceDetail implements Serializable {

	private static final long serialVersionUID = 7597157186868662372L;
	
	private Integer id;
	private int domain;
	
	private Invoice invoice;
	private Seller seller;
	private String project;
	private String warehouse;
	private String workPlace;
	private Item item;
	private short line;
	private String description;
	private double quantity;
	private double price;
	private String discountExpression;
	private InvoiceSource source;
	private Integer sourceId;
	private double taxableBase;
	private double taxes;
	private boolean prepayment;
	
	public Integer getId() {
		return id;
	}
	public InvoiceDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public InvoiceDetail setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Invoice getInvoice() {
		return invoice;
	}
	public InvoiceDetail setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
	public Seller getSeller() {
		return seller;
	}
	public InvoiceDetail setSeller(Seller seller) {
		this.seller = seller;
		return this;
	}
	public String getProject() {
		return project;
	}
	public InvoiceDetail setProject(String project) {
		this.project = project;
		return this;
	}
	public String getWarehouse() {
		return warehouse;
	}
	public InvoiceDetail setWarehouse(String warehouse) {
		this.warehouse = warehouse;
		return this;
	}
	public String getWorkPlace() {
		return workPlace;
	}
	public InvoiceDetail setWorkPlace(String workPlace) {
		this.workPlace = workPlace;
		return this;
	}
	public Item getItem() {
		return item;
	}
	public InvoiceDetail setItem(Item item) {
		this.item = item;
		return this;
	}
	public short getLine() {
		return line;
	}
	public InvoiceDetail setLine(short line) {
		this.line = line;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public InvoiceDetail setDescription(String description) {
		this.description = description;
		return this;
	}
	public double getQuantity() {
		return quantity;
	}
	public InvoiceDetail setQuantity(double quantity) {
		this.quantity = quantity;
		return this;
	}
	public double getPrice() {
		return price;
	}
	public InvoiceDetail setPrice(double price) {
		this.price = price;
		return this;
	}
	public String getDiscountExpression() {
		return discountExpression;
	}
	public InvoiceDetail setDiscountExpression(String discountExpression) {
		this.discountExpression = discountExpression;
		return this;
	}
	public InvoiceSource getSource() {
		return source;
	}
	public InvoiceDetail setSource(InvoiceSource source) {
		this.source = source;
		return this;
	}
	public Integer getSourceId() {
		return sourceId;
	}
	public InvoiceDetail setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
		return this;
	}
	public double getTaxableBase() {
		return taxableBase;
	}
	public InvoiceDetail setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
		return this;
	}
	public double getTaxes() {
		return taxes;
	}
	public InvoiceDetail setTaxes(double taxes) {
		this.taxes = taxes;
		return this;
	}
	public boolean isPrepayment() {
		return prepayment;
	}
	public InvoiceDetail setPrepayment(boolean prepayment) {
		this.prepayment = prepayment;
		return this;
	}

}
