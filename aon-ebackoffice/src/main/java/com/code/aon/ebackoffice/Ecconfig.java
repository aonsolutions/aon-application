package com.code.aon.ebackoffice;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.ebackoffice.enumeration.LoginType;
import com.code.aon.ebackoffice.enumeration.OriginalPrice;
import com.code.aon.ebackoffice.enumeration.PriceType;
import com.code.aon.ebackoffice.enumeration.SkinType;
import com.code.aon.ebackoffice.enumeration.TaxType;
import com.code.aon.ebackoffice.enumeration.WishList;


/**
 * Transfer Object that represents a eCommerce Target.
 * 
 * @author Esferalia Networks. David Uriarte - 3/09/2009
 */
@Entity
@Table(name="ec_config")
public class Ecconfig implements ITransferObject {
	
	
	private Integer id;	
	private boolean active;
	private String name;
	private SkinType skin;	
	private byte[] headerImg;
	private byte[] footerImg;
	private boolean commerce;
	private LoginType showLogin;
	private PriceType showPrice;
	private TaxType taxInType;
	private OriginalPrice showItemPrice;
	private WishList wishList;
	private PayMethod bankTransfer;
	private PayMethod cashOnDelivery;
	private PayMethod visa;
	private PayMethod paypal;
	private PayMethod bankDraft;
	private String series;
	private String privatePolicy;
	private String legalNote;
	private String dataProtection;
	
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Column(length = 64)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "skin", length = 1)
	public SkinType getSkin() {
		return skin;
	}
	public void setSkin(SkinType skin) {
		this.skin = skin;
	}
	
	@Lob
	@Column(name = "header_img")
	public byte[] getHeaderImg() {
		return headerImg;
	}
	public void setHeaderImg(byte[] headerImg) {
		this.headerImg = headerImg;
	}
	
	@Lob
	@Column(name = "footer_img")
	public byte[] getFooterImg() {
		return footerImg;
	}
	public void setFooterImg(byte[] footerImg) {
		this.footerImg = footerImg;
	}
	
	
	public boolean isCommerce() {
		return commerce;
	}
	public void setCommerce(boolean commerce) {
		this.commerce = commerce;
	}
	
	@Column(name = "show_login", length = 1)
	public LoginType getShowLogin() {
		return showLogin;
	}
	public void setShowLogin(LoginType showLogin) {
		this.showLogin = showLogin;
	}
	
	@Column(name = "show_price", length = 1)
	public PriceType getShowPrice() {
		return showPrice;
	}
	public void setShowPrice(PriceType showPrice) {
		this.showPrice = showPrice;
	}
	
	@Column(name = "tax_in_price", length = 1)
	public TaxType getTaxInType() {
		return taxInType;
	}
	public void setTaxInType(TaxType taxInType) {
		this.taxInType = taxInType;
	}
	
	@Column(name = "show_item_price", length = 1)
	public OriginalPrice getShowItemPrice() {
		return showItemPrice;
	}
	public void setShowItemPrice(OriginalPrice showItemPrice) {
		this.showItemPrice = showItemPrice;
	}
	
	@Column(name = "show_wish_list", length = 1)
	public WishList getWishList() {
		return wishList;
	}
	public void setWishList(WishList wishList) {
		this.wishList = wishList;
	}
	
	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="bank_transfer", nullable=false)
	@ForeignKey(name = "FK_ECCONFIG_BANK_TRANSFER")
	@Index(name = "IDX_ECCONFIG_BANK_TRANSFER")
	public PayMethod getBankTransfer() {
		return bankTransfer;
	}
	public void setBankTransfer(PayMethod bankTransfer) {
		this.bankTransfer = bankTransfer;
	}

	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="cash_on_delivery", nullable=false)
	@ForeignKey(name = "FK_ECCONFIG_CASH_ON_DELIVERY")
	@Index(name = "IDX_ECCONFIG_CASH_ON_DELIVERY")
	public PayMethod getCashOnDelivery() {
		return cashOnDelivery;
	}
	public void setCashOnDelivery(PayMethod cashOnDelivery) {
		this.cashOnDelivery = cashOnDelivery;
	}
	
	
	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="visa", nullable=false)
	@ForeignKey(name = "FK_ECCONFIG_VISA")
	@Index(name = "IDX_ECCONFIG_VISA")
	public PayMethod getVisa() {
		return visa;
	}
	public void setVisa(PayMethod visa) {
		this.visa = visa;
	}
	
	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="paypal", nullable=false)
	@ForeignKey(name = "FK_ECCONFIG_PAYPAL")
	@Index(name = "IDX_ECCONFIG_PAYPAL")
	public PayMethod getPaypal() {
		return paypal;
	}
	public void setPaypal(PayMethod paypal) {
		this.paypal = paypal;
	}
	
	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="bank_draft", nullable=false)
	@ForeignKey(name = "FK_ECCONFIG_BANK_DRAFT")
	@Index(name = "IDX_ECCONFIG_BANK_DRAFT")
	public PayMethod getBankDraft() {
		return bankDraft;
	}
	public void setBankDraft(PayMethod bankDraft) {
		this.bankDraft = bankDraft;
	}
	
	@Column(length=5)
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	
	@Lob
	@Type(type="stringClob")
	@Column(name = "private_policy")
	public String getPrivatePolicy() {
		return privatePolicy;
	}
	public void setPrivatePolicy(String privatePolicy) {
		this.privatePolicy = privatePolicy;
	}
	
	@Lob
	@Type(type="stringClob")
	@Column(name = "legal_note")
	public String getLegalNote() {
		return legalNote;
	}
	public void setLegalNote(String legalNote) {
		this.legalNote = legalNote;
	}
	
	@Lob
	@Type(type="stringClob")
	@Column(name = "data_protection")
	public String getDataProtection() {
		return dataProtection;
	}
	public void setDataProtection(String dataProtection) {
		this.dataProtection = dataProtection;
	}
	
}
	
	
	
	
	
	
	
	
	
	
	
	

	