package com.code.aon.tas;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.customer.Customer;

@Entity
@Table(name="support_order_insurance")
public class SupportOrderInsurance implements ITransferObject {

	private Integer id;
	
	private SupportOrder supportOrder;
	
	private Customer insurance;
	
	private Appraiser appraiser;
	
	private String claimNumber;
	
	private String policiType;
	
	private String franchise;

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
	@JoinColumn(name = "support_order", nullable = false)
	public SupportOrder getSupportOrder() {
		return supportOrder;
	}

	public void setSupportOrder(SupportOrder supportOrder) {
		this.supportOrder = supportOrder;
	}

	@ManyToOne
	@JoinColumn(name = "insurance", nullable = false)
	public Customer getInsurance() {
		return insurance;
	}

	public void setInsurance(Customer insurance) {
		this.insurance = insurance;
	}

	@ManyToOne
	@JoinColumn(name = "appraiser")
	public Appraiser getAppraiser() {
		return appraiser;
	}

	public void setAppraiser(Appraiser appraiser) {
		this.appraiser = appraiser;
	}

	@Column(name="claim_number", length=32)
	public String getClaimNumber() {
		return claimNumber;
	}

	public void setClaimNumber(String claimNumber) {
		this.claimNumber = claimNumber;
	}

	@Column(name="policy_type",length=32)
	public String getPoliciType() {
		return policiType;
	}

	public void setPoliciType(String policiType) {
		this.policiType = policiType;
	}

	@Column(length=32)
	public String getFranchise() {
		return franchise;
	}

	public void setFranchise(String franchise) {
		this.franchise = franchise;
	}
}