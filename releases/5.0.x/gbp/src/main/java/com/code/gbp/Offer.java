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
import com.code.gbp.enumeration.OfferStatus;
import com.code.gbp.enumeration.OfferType;

@Entity
@Table(name="offer")
public class Offer implements ITransferObject {

	private Integer id;
	
	private Supplier supplier;
	
	private Campaign campaign;
	
	private Date offerDate;
	
	private BigDecimal price;
	
	private String deliveryDate;
	
	private String quality;
	
	private BigDecimal stockProvision;
	
	private String defectiveGoods;
	
	private String additionalConditions;
	
	private Office office;
	
	private CostType costType;

	private AccountContact accountContact;
	
	private OfferStatus status;
	
	private String giftCode;
	
	private int paymentTerm;
	
	private OfferType type;
	
	private BatchConfig batchConfig;
	
	private String breakdown;
	
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

	@Temporal(value=TemporalType.DATE)
	@Column(name="offer_date", nullable=false)
	public Date getOfferDate() {
		return offerDate;
	}

	public void setOfferDate(Date offerDate) {
		this.offerDate = offerDate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Column(name="delivery_date", length=64)
	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	@Column(length=65535)
	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	@Column(name="stock_provision")
	public BigDecimal getStockProvision() {
		return stockProvision;
	}

	public void setStockProvision(BigDecimal stockProvision) {
		this.stockProvision = stockProvision;
	}

	@Column(name="defective_goods", length=65535)
	public String getDefectiveGoods() {
		return defectiveGoods;
	}

	public void setDefectiveGoods(String defectiveGoods) {
		this.defectiveGoods = defectiveGoods;
	}

	@Column(name="additional_conditions", length=65535)
	public String getAdditionalConditions() {
		return additionalConditions;
	}

	public void setAdditionalConditions(String additionalConditions) {
		this.additionalConditions = additionalConditions;
	}

	@ManyToOne
	@JoinColumn( name="office" )
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

	public OfferStatus getStatus() {
		return status;
	}

	public void setStatus(OfferStatus status) {
		this.status = status;
	}

	@Column(name="gift_code", length=10)
	public String getGiftCode() {
		return giftCode;
	}

	public void setGiftCode(String giftCode) {
		this.giftCode = giftCode;
	}

	@Column(name="payment_term")
	public int getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(int paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public OfferType getType() {
		return type;
	}

	public void setType(OfferType type) {
		this.type = type;
	}

	@ManyToOne
	@JoinColumn( name="batch_config" )
	public BatchConfig getBatchConfig() {
		return batchConfig;
	}

	public void setBatchConfig(BatchConfig batchConfig) {
		this.batchConfig = batchConfig;
	}

	public String getBreakdown() {
		return breakdown;
	}

	public void setBreakdown(String breakdown) {
		this.breakdown = breakdown;
	}
	
	
}