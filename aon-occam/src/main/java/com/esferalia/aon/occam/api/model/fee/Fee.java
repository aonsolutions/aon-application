package com.esferalia.aon.occam.api.model.fee;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DiscountExpression;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class Fee implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Domain domain;
	private Project project;
	private Customer customer;
	private Short line;
	private OldItem item;
	private String description;
	private Double quantity;
	private Double price;
	private String discountExpr;
	private Date startDate;
	private Date endDate;
	private Date billingDate;
	private BillingPeriod period;
	private SecurityLevel securityLevel;
	private InvoicingGroup invoicingGroup;
	private Seller seller;
	private Workplace workplace;
	private boolean modify = false;
	private Boolean hasRItem = false;
	private Seller sellerSupport;
	
		
	public Double getQuantity() {
		return quantity;
	}
	
	public Fee setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Fee setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public Fee setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public Date getBillingDate() {
		return billingDate;
	}
	
	public Fee setBillingDate(Date billingDate) {
		this.billingDate = billingDate;
		return this;
	}
	
	public BillingPeriod getPeriod() {
		return period;
	}
	
	public Fee setPeriod(BillingPeriod period) {
		this.period = period;
		return this;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public Fee setPrice(Double price) {
		this.price = price;
		return this;
	}
	
	public Double getDiscount() {
		DiscountExpression de = new DiscountExpression(getDiscountExpr());
		return de.getPercentage();
	}

	public Fee setDiscount(Double discount) {
		this.discountExpr = discount.toString();
		return this;
	}
	
	public Seller getSeller() {
		if(seller == null) {
			this.seller = new Seller();
		}
		return seller;
	}
	
	public Fee setSeller(Seller seller) {
		this.seller = seller;
		return this;
	}
	
	public Seller getSellerSupport() {
		if(sellerSupport == null) {
			this.sellerSupport = new Seller();
		}
		return sellerSupport;
	}
	
	public Fee setSellerSupport(Seller sellerSupport) {
		this.sellerSupport = sellerSupport;
		return this;
	}

	public Workplace getWorkplace() {
		if(workplace == null) {
			this.workplace = new Workplace();
		}
		return workplace;
	}
	
	public Fee setWorkplace(Workplace workplace) {
		this.workplace = workplace;
		return this;
	}
	
	public InvoicingGroup getInvoicingGroup() {
		if(invoicingGroup == null) {
			invoicingGroup = new InvoicingGroup();
		}
		return invoicingGroup;
	}
	
	public Fee setInvoicingGroup(InvoicingGroup invoicingGroup) {
		this.invoicingGroup = invoicingGroup;
		return this;
	}
	
	public Boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL.equals(getSecurityLevel());
	}
	
	public Fee setConfidential(Boolean confidential) {
		this.securityLevel = confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL;
		return this;
	}
	
	public Project getProject() {
		if(project == null) {
			project = new Project();
		}
		return project;
	}
	
	public Fee setProject(Project project) {
		this.project = project;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Fee setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public Short getLine() {
		return line;
	}
	
	public Fee setLine(Short line) {
		this.line = line;
		return this;
	}
	
	public Fee setLine(Double line) {
		this.line = line.shortValue();
		return this;
	}

	public Domain getDomain() {
		if(domain == null) {
			domain = new Domain();
		}
		return domain;
	}
	
	public Fee setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getId() {
		return id;
	}
	
	public Fee setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public OldItem getItem() {
		if(item == null) {
			item = new OldItem();
		}
		return item;
	}
	
	public Fee setItem(OldItem item) {
		this.item = item;
		return this;
	}
	
	public String getDiscountExpr() {
		if(discountExpr == null) {
			this.discountExpr = "0.0";
		}
		return discountExpr;
	}
	
	public Fee setDiscountExpr(String discountExpr) {
		this.discountExpr = discountExpr;
		return this;
	}
	
	public SecurityLevel getSecurityLevel() {
		if(securityLevel == null) {
			securityLevel = SecurityLevel.OFFICIAL;
		}
		return securityLevel;
	}
	
	public Fee setSecurityLevel(SecurityLevel securityLevel){
		this.securityLevel = securityLevel;
		return this;
	}
	
	public Customer getCustomer() {
		if(customer == null) {
			customer = new Customer();
		}
		return customer;
	}
	
	public Fee setCustomer(Customer customer) {
		this.customer = customer;
		return this;
	}

	public boolean isModify() {
		return modify;
	}

	public Fee setModify(boolean modify) {
		this.modify = modify;
		return this;
	}
	
	public Boolean hasRItem() {
		return this.hasRItem;
	}

	public Fee setHasRItem(boolean hasRItem) {
		this.hasRItem = hasRItem;
		return this;
	}
	
}
