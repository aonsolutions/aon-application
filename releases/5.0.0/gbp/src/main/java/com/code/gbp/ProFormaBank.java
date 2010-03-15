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
@Table(name="proforma_bank")
public class ProFormaBank implements ITransferObject {

	private Integer id;
	
	private ProFormaInvoice proFormaInvoice;
	
	private Bank bank;
	
	private double percent;

	
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
	@JoinColumn(name="proforma", nullable=false)
	public ProFormaInvoice getProFormaInvoice() {
		return proFormaInvoice;
	}

	public void setProFormaInvoice(ProFormaInvoice proFormaInvoice) {
		this.proFormaInvoice = proFormaInvoice;
	}

	@ManyToOne
	@JoinColumn(name="bank", nullable=false)
	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

}