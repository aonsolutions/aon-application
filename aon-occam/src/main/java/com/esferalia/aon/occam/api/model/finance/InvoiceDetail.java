package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;

public class InvoiceDetail implements Serializable {

	private static final long serialVersionUID = 7597157186868662372L;
	
	private Integer id;
	private int domain;
	
	private Invoice invoice;
	private Integer investAsset;
	private Integer project;
	private String projectName;
	private Seller seller;
	private Integer warehouse;
	private String warehouseName;
	private Integer workPlace;
	private String workPlaceName;
	private OldItem item;
	private short line;
	private String description;
	private double quantity;
	private double price;
	private String discountExpression;
	private InvoiceSource source;
	private Integer sourceId;
	private double taxableBase;
	private double taxes;
	private double surcharge;
	private boolean prepayment;
	
	private Integer account;
	private String accountCode;
	private String accountDescription;
	
	private LinkedList<InvoiceTax> invoiceTaxes;
	
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
	public Integer getInvestAsset() {
		return investAsset;
	}
	public InvoiceDetail setInvestAsset(Integer investAsset) {
		this.investAsset = investAsset;
		return this;
	}
	public Integer getProject() {
		return project;
	}
	public InvoiceDetail setProject(Integer project) {
		this.project = project;
		return this;
	}
	public String getProjectName() {
		return projectName;
	}
	public InvoiceDetail setProjectName(String projectName) {
		this.projectName = projectName;
		return this;
	}
	public Seller getSeller() {
		return seller;
	}
	public InvoiceDetail setSeller(Seller seller) {
		this.seller = seller;
		return this;
	}
	public Integer getWarehouse() {
		return warehouse;
	}
	public InvoiceDetail setWarehouse(Integer warehouse) {
		this.warehouse = warehouse;
		return this;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public InvoiceDetail setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
		return this;
	}
	public Integer getWorkPlace() {
		return workPlace;
	}
	public InvoiceDetail setWorkPlace(Integer workPlace) {
		this.workPlace = workPlace;
		return this;
	}
	public String getWorkPlaceName() {
		return workPlaceName;
	}
	public InvoiceDetail setWorkPlaceName(String workPlaceName) {
		this.workPlaceName = workPlaceName;
		return this;
	}
	public OldItem getItem() {
		return item;
	}
	public InvoiceDetail setItem(OldItem item) {
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
	public Double getPrice() {
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
	public double getSurcharge() {
		return surcharge;
	}
	public InvoiceDetail setSurcharge(double surcharge) {
		this.surcharge = surcharge;
		return this;
	}
	public boolean isPrepayment() {
		return prepayment;
	}
	public InvoiceDetail setPrepayment(boolean prepayment) {
		this.prepayment = prepayment;
		return this;
	}

	public LinkedList<InvoiceTax> getInvoiceTaxes() {
		return invoiceTaxes;
	}
	public InvoiceDetail setInvoiceTaxes(LinkedList<InvoiceTax> invoiceTaxes) {
		this.invoiceTaxes = invoiceTaxes;
		return this;
	}
	public InvoiceDetail addInvoiceTax(InvoiceTax invoiceTax) {
		if (getInvoiceTaxes() == null) {
			setInvoiceTaxes(new LinkedList<InvoiceTax>());
		}
		getInvoiceTaxes().add(invoiceTax);
		return this;
	}
	
	public Integer getAccount() {
		return account;
	}
	public InvoiceDetail setAccount(Integer account) {
		this.account = account;
		return this;
	}
	
	public String getAccountCode() {
		return accountCode;
	}
	public InvoiceDetail setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}
	
	public String getAccountDescription() {
		return accountDescription;
	}
	public InvoiceDetail setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
		return this;
	}

	public boolean isDeleted() {
		return (getId() != null && getId() < 0);
	}
	
}
