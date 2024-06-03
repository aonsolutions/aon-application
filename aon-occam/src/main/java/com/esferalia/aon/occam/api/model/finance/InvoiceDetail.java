package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.DiscountExpression;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
public class InvoiceDetail implements Serializable {

	private static final long serialVersionUID = 7597157186868662372L;
	
	private Integer id;
	private Integer domain;
	
	private Invoice invoice;
	private Integer investAsset;
	private InvestAsset investAssetData;
	private Integer project;
	private String projectName;
	private Seller seller;


	private Item item;
	private short line;
	private String description;
	private double quantity;
	private double price;
	private DiscountExpression discountExpression;
	private double taxableBase;
	private double taxes;
	private boolean prepayment;

	private LinkedList<String> segments;
	@Deprecated // Debería estar en Invoice dentro de Registry...  
	private String sellerSupport;
	
	// WAREHOUSE
	// TODO private Warehouse warehouse;
	private Integer warehouse;
	private String warehouseName;
	
	// WORKPLACE
	private Workplace workplace;
	private Integer workPlace;
	private String workPlaceName;
	
	// ACCOUNT
	// TODO private Account account;
	private Integer account;
	private String accountCode;
	private String accountDescription;
	
	private LinkedList<InvoiceTax> invoiceTaxes;
	
	// SOURCE INFO
	
	private InvoiceSource source;
	private Integer sourceId;
	
	private PurchaseDetail purchaseDetail;
	private SalesDetail salesDetail;
	private DeliveryDetail deliveryDetail;
	private IncomeDetail incomeDetail;
	private OfferDetail offerDetail;
	
	public Integer getId() {
		return id;
	}
	public InvoiceDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public InvoiceDetail setDomain(Integer domain) {
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
	
	public InvestAsset getInvestAssetData() {
		return investAssetData;
	}
	
	public InvoiceDetail setInvestAssetData(InvestAsset investAssetData) {
		this.investAssetData = investAssetData;
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
	
	public Workplace getWorkplace() {
		return workplace;
	}
	
	public InvoiceDetail setWorkplace(Workplace workplace) {
		this.workplace = workplace;
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
	
	public double getAmount() {
		return getPrice() * getQuantity() * (1 - getDiscount()/100);
	}
	
	public DiscountExpression getDiscountExpression() {
		if(discountExpression == null)
			discountExpression = new DiscountExpression("0.0");
		return discountExpression;
	}
	
	public InvoiceDetail setDiscountExpression(DiscountExpression discountExpression) {
		this.discountExpression = discountExpression;
		return this;
	}
	
	public InvoiceDetail setDiscountExpression(String discountExpression) {
		this.discountExpression = new DiscountExpression(discountExpression);
		return this;
	}
	
	public double getDiscount() {
		return getDiscountExpression().getPercentage();
	}
	
	public InvoiceDetail setDiscount(double discount) {
		setDiscountExpression(new DiscountExpression(discount));
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

	public LinkedList<InvoiceTax> getInvoiceTaxes() {
		if(invoiceTaxes == null) {
			invoiceTaxes = new LinkedList<>();
		}
		return invoiceTaxes;
	}
	public InvoiceDetail setInvoiceTaxes(LinkedList<InvoiceTax> invoiceTaxes) {
		this.invoiceTaxes = invoiceTaxes;
		return this;
	}
	public InvoiceDetail addInvoiceTax(InvoiceTax invoiceTax) {
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
	
	public PurchaseDetail getPurchaseDetail() {
		return purchaseDetail;
	}
	
	public void setPurchaseDetail(PurchaseDetail purchaseDetail) {
		this.purchaseDetail = purchaseDetail;
	}
	
	public SalesDetail getSalesDetail() {
		return salesDetail;
	}
	
	public void setSalesDetail(SalesDetail salesDetail) {
		this.salesDetail = salesDetail;
	}
	
	public DeliveryDetail getDeliveryDetail() {
		return deliveryDetail;
	}
	
	public void setDeliveryDetail(DeliveryDetail deliveryDetail) {
		this.deliveryDetail = deliveryDetail;
	}
	
	public IncomeDetail getIncomeDetail() {
		return incomeDetail;
	}
	
	public void setIncomeDetail(IncomeDetail incomeDetail) {
		this.incomeDetail = incomeDetail;
	}
	
	public OfferDetail getOfferDetail() {
		return offerDetail;
	}
	
	public void setOfferDetail(OfferDetail offerDetail) {
		this.offerDetail = offerDetail;
	}

	public LinkedList<String> getSegments() {
		return segments;
	}

	public InvoiceDetail setSegments(LinkedList<String> segments) {
		this.segments = segments;
		return this;
	}
	
	@Deprecated // Debería estar en Invoice dentro de Registry...  
	public String getSellerSupport() {
		return sellerSupport;
	}
	
	@Deprecated // Debería estar en Invoice dentro de Registry... 
	public InvoiceDetail setSellerSupport(String sellerSupport) {
		this.sellerSupport = sellerSupport;
		return this;
	}
}
