package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.DiscountExpression;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;

public class InvoiceFlat implements Serializable {

	private static final long serialVersionUID = 7597157186868662372L;

	private Integer id;
	private Integer domain;
	private Invoice invoice;
	private String geozoneCode;
	private String geozoneName;
	private String city;
	private String zip;
	
	private Integer scope;
	private String scopeName;

	private Integer project;
	private String projectName;

	private InvestAsset investAsset;
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
	private Warehouse warehouse;
	private Workplace workplace;
	private Account expAccount;
	
	private List<InvoiceTax> invoiceTaxes;
	
	// SOURCE INFO
	private InvoiceSource source;
	private Integer sourceId;
	
	public Integer getId() {
		return id;
	}
	public InvoiceFlat setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public InvoiceFlat setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Invoice getInvoice() {
		return invoice;
	}
	public InvoiceFlat setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public String getGeozoneCode() {
		return geozoneCode;
	}
	public InvoiceFlat setGeozoneCode(String geozoneCode) {
		this.geozoneCode = geozoneCode;
		return this;
	}
	
	public String getGeozoneName() {
		return geozoneName;
	}
	public InvoiceFlat setGeozoneName(String geozoneName) {
		this.geozoneName = geozoneName;
		return this;
	}
	
	public String getCity() {
		return city;
	}
	public InvoiceFlat setCity(String city) {
		this.city = city;
		return this;
	}
	
	public String getZip() {
		return zip;
	}
	public InvoiceFlat setZip(String zip) {
		this.zip = zip;
		return this;
	}
	
	public Integer getScope() {
		return scope;
	}
	public InvoiceFlat setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	
	public String getScopeName() {
		return scopeName;
	}
	public InvoiceFlat setScopeName(String scopeName) {
		this.scopeName = scopeName;
		return this;
	}
	
	public Integer getProject() {
		return project;
	}
	public InvoiceFlat setProject(Integer project) {
		this.project = project;
		return this;
	}
	
	public String getProjectName() {
		return projectName;
	}
	public InvoiceFlat setProjectName(String projectName) {
		this.projectName = projectName;
		return this;
	}
	
	public Optional<InvestAsset> getInvestAsset() {
		return Optional.ofNullable(investAsset);
	}
	
	public InvoiceFlat setInvestAsset(InvestAsset investAsset) {
		this.investAsset = investAsset;
		return this;
	}
	
	public Optional<Seller> getSeller() {
		return Optional.ofNullable(seller);
	}
	public InvoiceFlat setSeller(Seller seller) {
		this.seller = seller;
		return this;
	}

	public Optional<Item> getItem() {
		return Optional.ofNullable(item);
	}
	public InvoiceFlat setItem(Item item) {
		this.item = item;
		return this;
	}
	
	public short getLine() {
		return line;
	}
	public InvoiceFlat setLine(short line) {
		this.line = line;
		return this;
	}

	
	public String getDescription() {
		return description;
	}
	public InvoiceFlat setDescription(String description) {
		this.description = description;
		return this;
	}

	public double getQuantity() {
		return quantity;
	}
	public InvoiceFlat setQuantity(double quantity) {
		this.quantity = quantity;
		return this;
	}
	
	public double getPrice() {
		return price;
	}
	public InvoiceFlat setPrice(double price) {
		this.price = price;
		return this;
	}
	
	public DiscountExpression getDiscountExpression() {
		if(discountExpression == null)
			discountExpression = new DiscountExpression("0.0");
		return discountExpression;
	}
	public InvoiceFlat setDiscountExpression(DiscountExpression discountExpression) {
		this.discountExpression = discountExpression;
		return this;
	}
	public InvoiceFlat setDiscountExpression(String discountExpression) {
		this.discountExpression = new DiscountExpression(discountExpression);
		return this;
	}
	
	public double getDiscount() {
		return getDiscountExpression().getPercentage();
	}
	public InvoiceFlat setDiscount(double discount) {
		setDiscountExpression(new DiscountExpression(discount));
		return this;
	}

	public double getAmount() {
		return getPrice() * getQuantity() * (1 - getDiscount()/100);
	}

	public double getTaxableBase() {
		return taxableBase;
	}
	public InvoiceFlat setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
		return this;
	}
	
	public double getTaxes() {
		return taxes;
	}
	public InvoiceFlat setTaxes(double taxes) {
		this.taxes = taxes;
		return this;
	}

	public boolean isPrepayment() {
		return prepayment;
	}
	public InvoiceFlat setPrepayment(boolean prepayment) {
		this.prepayment = prepayment;
		return this;
	}

	public Optional<Warehouse> getWarehouse() {
		return Optional.ofNullable(warehouse);
	}
	public InvoiceFlat setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
		return this;
	}
    
	public Workplace getWorkplace() {
		return workplace;
	}
	
	public InvoiceFlat setWorkplace(Workplace workplace) {
		this.workplace = workplace;
		return this;
	}

	public Account getExpAccount() {
		if (expAccount == null) expAccount = new Account();
		return expAccount;
	}
	public InvoiceFlat setExpAccount(Account expAccount) {
		this.expAccount = expAccount;
		return this;
	}
	
	public InvoiceSource getSource() {
		return source;
	}
	public InvoiceFlat setSource(InvoiceSource source) {
		this.source = source;
		return this;
	}
	public boolean isAccountSource() {
		return this.getSource() == InvoiceSource.ACCOUNT;
	}
	
	public Integer getSourceId() {
		return sourceId;
	}
	public InvoiceFlat setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
		return this;
	}
	
	public List<InvoiceTax> getInvoiceTaxes() {
		if(invoiceTaxes == null) {
			invoiceTaxes = new LinkedList<>();
		}
		return invoiceTaxes;
	}
	public InvoiceFlat setInvoiceTaxes(List<InvoiceTax> invoiceTaxes) {
		this.invoiceTaxes = invoiceTaxes;
		return this;
	}
	public InvoiceFlat addInvoiceTax(InvoiceTax invoiceTax) {
		getInvoiceTaxes().add(invoiceTax);
		return this;
	}
	
	// ----------------------------------------------- [VAT]
	public Optional<InvoiceTax> getVatTax() {
		return getInvoiceTaxes()
			.stream()
			.filter(it -> it.isVatType())
			.findFirst();
	}
	public InvoiceTax ensureVatTax() {
		if (!getVatTax().isPresent()) {
			addVatTax();
		}
		return getVatTax().get();
	}
	public InvoiceFlat addVatTax() {
		return addInvoiceTax( new InvoiceTax()
			.setDomain(this.domain)
			.setInvoiceDetail(this.id)
			.setTaxType(TaxType.VAT)
			.setBase(this.taxableBase)
		);
	}
	public InvoiceFlat deleteVatTax() {
		return deleteTax( TaxType.VAT ); 
	}
	
	// -------------------------------------- [WITHHOLDING]

	public Optional<InvoiceTax> getWithholdingTax() {
		return getInvoiceTaxes()
			.stream()
			.filter(it -> it.isWithholdingType())
			.findFirst();
	}
	public InvoiceTax ensureWithholdingTax(Invoice inv) {
		return getWithholdingTax()
			.orElseGet(() -> addWithholdingTax(inv).getWithholdingTax().orElse( null ) ); 
	}
	public InvoiceFlat addWithholdingTax( Invoice inv ) {
		InvoiceWithholding wd = inv.getWithholding()
			.orElseGet( inv::ensureWithholdingData );
		return addInvoiceTax( new InvoiceTax()
			.setDomain(this.domain)
			.setInvoiceDetail(this.id)
			.setTaxType(TaxType.RETENTION)
			.setBase(this.taxableBase)
			.setWithholdingType( wd.getWithholdingType() )
			.setPercentage( wd.getPercentage() )
		);
	}
	public InvoiceFlat deleteWithholdingTax() {
		return deleteTax( TaxType.RETENTION ); 
	}

	private InvoiceFlat deleteTax(TaxType type) {
		return setInvoiceTaxes(
			getInvoiceTaxes()
				.stream()
				.filter(it -> it.getTaxType() != type)
				.collect(Collectors.toCollection(LinkedList::new) )
		);
	
	}
	public Optional<Product> getProduct() {
		return getItem()
			.map(Item::getProduct );
	}
	public Optional<ProductCategory> getCategory() {
		return getItem()
			.map(Item::getProduct )
			.map( Product::getCategory);
	}
	public Optional<Brand> getBrand() {
		return getItem()
			.map(Item::getProduct )
			.map( Product::getBrand);
	}
	
}
