package com.code.gbp;

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
	
	private boolean centralized;
	
	private Date invoiceDate;
	
	private String number;
	
	private double amount;
	
	private ProFormaInvoiceStatus status;

	
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

	public boolean isCentralized() {
		return centralized;
	}

	public void setCentralized(boolean centralized) {
		this.centralized = centralized;
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

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Column(nullable=false)
	public ProFormaInvoiceStatus getStatus() {
		return status;
	}

	public void setStatus(ProFormaInvoiceStatus status) {
		this.status = status;
	}
}