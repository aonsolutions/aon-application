package com.code.gbp;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.aon.common.ITransferObject;
import com.code.gbp.enumeration.CostType;
import com.code.gbp.enumeration.ProFormaInvoiceStatus;

@Entity
@Table(name="proforma_invoice")
public class ProFormaInvoice implements ITransferObject {

	private Integer id;
	
	private Supplier supplier;
	
	private Campaign campaign;
	
	private Office office;
	
	private CostType costType;
	
	private AccountContact accountContact;
	
	private Date invoiceDate;
	
	private String number;
	
	private BigDecimal amount;
	
	private ProFormaInvoiceStatus status;
	
	private Date paymentDate;
	
	private int paymentTerm;
	
	private Offer offer;

	private String concept;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn( name="supplier", nullable=false )
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@ManyToOne
	@JoinColumn( name="campaign", nullable=false )
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	@ManyToOne
	@JoinColumn( name="office")
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	@Column(name="cost_type")
	public CostType getCostType() {
		return costType;
	}

	public void setCostType(CostType costType) {
		this.costType = costType;
	}

	@ManyToOne
	@JoinColumn( name="account_contact" )
	public AccountContact getAccountContact() {
		return accountContact;
	}

	public void setAccountContact(AccountContact accountContact) {
		this.accountContact = accountContact;
	}

	@Temporal(value=TemporalType.DATE)
	@Column(name="invoice_date", nullable=false)
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	@Column(length=16)
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(nullable=false)
	public ProFormaInvoiceStatus getStatus() {
		return status;
	}

	public void setStatus(ProFormaInvoiceStatus status) {
		this.status = status;
	}

	@Temporal(value=TemporalType.DATE)
	@Column(name="payment_date")
	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	@Column(name="payment_term")
	public int getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(int paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	@ManyToOne
	@JoinColumn( name="offer", nullable=false )
	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	@Column(name="concept")
	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}
	
}