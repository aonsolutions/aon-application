package com.code.gbp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="bank_percent")
public class BankPercent implements ITransferObject {
	
	private Integer id;

	private Bank bank;
	
	private double distributionPercent;
	
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
	@JoinColumn( name="bank", nullable=false )
	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}
	
	@Column(name="distribution_percent")
	public double getDistributionPercent() {
		return distributionPercent;
	}

	public void setDistributionPercent(double distributionPercent) {
		this.distributionPercent = distributionPercent;
	}

	
	
}