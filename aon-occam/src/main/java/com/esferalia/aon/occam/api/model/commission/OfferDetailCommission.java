package com.esferalia.aon.occam.api.model.commission;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.management.OfferDetail;

public class OfferDetailCommission implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	private Integer id;
	private Integer domain;
	private OfferDetail offerDetail;
	private Double commission;
	private Double amount;
	private OfferDetailCommissionStatus status;
	private Date payDate;
	
	public Integer getId() {
		return id;
	}
	public OfferDetailCommission setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public OfferDetailCommission setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public OfferDetail getOfferDetail() {
		return offerDetail;
	}
	public OfferDetailCommission setOfferDetail(OfferDetail offerDetail) {
		this.offerDetail = offerDetail;
		return this;
	}
	public Double getCommission() {
		return commission;
	}
	public OfferDetailCommission setCommission(Double commission) {
		this.commission = commission;
		return this;
	}
	public Double getAmount() {
		return amount;
	}
	public OfferDetailCommission setAmount(Double amount) {
		this.amount = amount;
		return this;
	}
	public OfferDetailCommissionStatus getStatus() {
		return status;
	}
	public OfferDetailCommission setStatus(OfferDetailCommissionStatus status) {
		this.status = status;
		return this;
	}
	public Date getPayDate() {
		return payDate;
	}
	public OfferDetailCommission setPayDate(Date payDate) {
		this.payDate = payDate;
		return this;
	}
	
}
