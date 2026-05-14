package com.esferalia.aon.occam.impl.jooq.dao.invoice.fee;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DiscountExpression;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.Month;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.server.AonDateUtils;

public class FeeBilling implements Serializable {
	
	private static final long serialVersionUID = 2762563588116230667L;
	
	private Integer id;
	private Integer domain;
	private Short line;
	private Customer customer;
	private FeeBillingInvoicingGroup invoicingGroup;
	private Integer project;
	private Integer seller;
	private Item item;
	private String description;
	private BillingPeriod period;
	private Date initialDate;
	private Date finalDate;
	private Date billingDate;
	private double quantity;
	private double price;
	private DiscountExpression discountExpression;
	private Integer workplace;
	private SecurityLevel securityLevel;
	
	public Integer getId() {
		return id;
	}
	public FeeBilling setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public FeeBilling setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Short getLine() {
		return line;
	}
	public FeeBilling setLine(Short line) {
		this.line = line;
		return this;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	public FeeBilling setCustomer(Customer customer) {
		this.customer = customer;
		return this;
	}
	
	public Optional<FeeBillingInvoicingGroup> getInvoicingGroup() {
		return Optional.ofNullable( invoicingGroup );
	}
	public FeeBilling setInvoicingGroup(FeeBillingInvoicingGroup invoicingGroup) {
		this.invoicingGroup = invoicingGroup;
		return this;
	}
	
	public Customer getInvoicingCustomer() {
		return getInvoicingGroup()
			.map( ig -> ig.getCustomer() )
			.orElse( getCustomer() );
    }

	public Optional<Integer> getProject() {
		return Optional.ofNullable( project );
	}
	public FeeBilling setProject(Integer project) {
		this.project = project;
		return this;
	}
	
	public Integer getSeller() {
		return seller;
	}
	public FeeBilling setSeller(Integer seller) {
		this.seller = seller;
		return this;
	}
	
	public Item getItem() {
		return item;
	}
	public FeeBilling setItem(Item item) {
		this.item = item;
		return this;
	}
	public boolean isPrepayment() {
		return getItem() != null
			&& getItem().getProduct() != null
			&& getItem().getProduct().getType() == ProductType.PREPAYMENT
		;
	}
	
	public String getDescription() {
		return description;
	}
	public FeeBilling setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public BillingPeriod getPeriod() {
		return period;
	}
	public FeeBilling setPeriod(BillingPeriod period) {
		this.period = period;
		return this;
	}
	
	public Date getInitialDate() {
		return initialDate;
	}
	public FeeBilling setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
		return this;
	}
	
	public Date getFinalDate() {
		return finalDate;
	}
	public FeeBilling setFinalDate(Date finalDate) {
		this.finalDate = finalDate;
		return this;
	}
	
    public Date getBillingDate() {
		return billingDate;
	}
	public FeeBilling setBillingDate(Date billingDate) {
		this.billingDate = billingDate;
		return this;
	}
	
	public Month getBillingDateMonth() {
		return Optional.ofNullable( getBillingDate()  )
			.filter( d -> d != null)
			.flatMap( d -> Month.safeValueOf( AonDateUtils.getMonth( d )) )
			.orElse(Month.JANUARY)
		;
	}
	
	public int getBillingDateYear() {
		return Optional.ofNullable(getBillingDate())
			.filter( d -> d != null)
			.map( AonDateUtils::getYear ) 
			.orElse(AonDateUtils.getYear( new Date() ))
		;
	}

	public double getQuantity() {
		return quantity;
	}
	public FeeBilling setQuantity(double quantity) {
		this.quantity = quantity;
		return this;
	}
	
	public double getPrice() {
		return price;
	}
	public FeeBilling setPrice(double price) {
		this.price = price;
		return this;
	}

	public DiscountExpression getDiscountExpression() {
		if (discountExpression == null) discountExpression = new DiscountExpression();
		return discountExpression;
	}
	public FeeBilling setDiscountExpression(DiscountExpression discountExpression) {
		this.discountExpression = discountExpression;
		return this;
	}
	
	public Integer getWorkplace() {
		return workplace;
	}
	public FeeBilling setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public FeeBilling setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	public boolean isConfidential() {
		return getSecurityLevel() == SecurityLevel.CONFIDENTIAL;
	}
	public FeeBilling setConfidential(boolean confidential) {
		this.securityLevel = confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL;
		return this;
	}
}
