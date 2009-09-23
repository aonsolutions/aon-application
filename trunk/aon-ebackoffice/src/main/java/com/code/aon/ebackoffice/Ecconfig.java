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
import com.code.aon.config.Tariff;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.ebackoffice.enumeration.LoginType;
import com.code.aon.ebackoffice.enumeration.ShowPrice;
import com.code.aon.ebackoffice.enumeration.DiscountFormat;
import com.code.aon.ebackoffice.enumeration.SkinType;
import com.code.aon.ebackoffice.enumeration.TaxType;
import com.code.aon.ebackoffice.enumeration.WishList;


/**
 * Transfer Object that represents a eCommerce Target.
 * 
 * @author Esferalia Networks. EKAIN - 3/09/2009
 */
@Entity
@Table(name="ec_config")
public class Ecconfig implements ITransferObject {
	
	
	private Integer id;	
	private boolean active;
	private String name;
	private SkinType skin;	
	private byte[] headerImg;
	private byte[] leftBanner;
	private byte[] welcomeBanner;
	private byte[] rightBanner;
	private boolean commerce;
	private boolean ecommerceStatus;
	private LoginType showLogin;
	private ShowPrice price;
	private TaxType taxInType;
	private DiscountFormat discount;
	private PayMethod bankTransfer;
	private PayMethod cashOnDelivery;
	private PayMethod visa;
	private PayMethod paypal;
	private PayMethod bankDraft;
	private String series;
	private String privatePolicy;
	private String legalNote;
	private String dataProtection;
	private Tariff tariff;
	private String headerColor;
	private Integer rowItems;
	private Integer shippingCosts;
	private Integer freeShipping;
	private String telephone;
	
	
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
	
	@Column(name = "price", length = 1)
	public ShowPrice getPrice() {
		return price;
	}
	public void setPrice(ShowPrice price) {
		this.price = price;
	}
		
	@Column(name = "tax_in_price", length = 1)
	public TaxType getTaxInType() {
		return taxInType;
	}
	
	public void setTaxInType(TaxType taxInType) {
		this.taxInType = taxInType;
	}
	
	@Column(name = "discount", length = 1)
	public DiscountFormat getDiscount() {
		return discount;
	}
	public void setDiscount(DiscountFormat discount) {
		this.discount = discount;
	}		
	
		
	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="bank_transfer")
	@ForeignKey(name = "FK_ECCONFIG_BANK_TRANSFER")
	@Index(name = "IDX_ECCONFIG_BANK_TRANSFER")
	public PayMethod getBankTransfer() {
		return bankTransfer;
	}
	public void setBankTransfer(PayMethod bankTransfer) {
		this.bankTransfer = bankTransfer;
	}

	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="cash_on_delivery")
	@ForeignKey(name = "FK_ECCONFIG_CASH_ON_DELIVERY")
	@Index(name = "IDX_ECCONFIG_CASH_ON_DELIVERY")
	public PayMethod getCashOnDelivery() {
		return cashOnDelivery;
	}
	public void setCashOnDelivery(PayMethod cashOnDelivery) {
		this.cashOnDelivery = cashOnDelivery;
	}
	
	
	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="visa")
	@ForeignKey(name = "FK_ECCONFIG_VISA")
	@Index(name = "IDX_ECCONFIG_VISA")
	public PayMethod getVisa() {
		return visa;
	}
	public void setVisa(PayMethod visa) {
		this.visa = visa;
	}
	
	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="paypal")
	@ForeignKey(name = "FK_ECCONFIG_PAYPAL")
	@Index(name = "IDX_ECCONFIG_PAYPAL")
	public PayMethod getPaypal() {
		return paypal;
	}
	public void setPaypal(PayMethod paypal) {
		this.paypal = paypal;
	}
	
	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="bank_draft")
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
	
	
	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="tariff")
	@ForeignKey(name = "FK_ECCONFIG_TARIFF")
	@Index(name = "IDX_ECCONFIG_TARIFF")
	public Tariff getTariff() {
		return tariff;
	}
	
	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	}
	
	@Column(name = "header_color", length = 7)
	public String getHeaderColor() {
		return headerColor;
	}
	
	public void setHeaderColor(String headerColor) {
		this.headerColor = headerColor;
	}
	
	@Column(name = "row_items")
	public Integer getRowItems() {
		return rowItems;
	}
	
	public void setRowItems(Integer rowItems) {
		this.rowItems = rowItems;
	}
	
	@Column(length=12)
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	@Lob
	@Column(name = "left_banner")
	public byte[] getLeftBanner() {
		return leftBanner;
	}
	public void setLeftBanner(byte[] leftBanner) {
		this.leftBanner = leftBanner;
	}
	
	@Lob
	@Column(name = "welcome_banner")
	public byte[] getWelcomeBanner() {
		return welcomeBanner;
	}
	public void setWelcomeBanner(byte[] welcomeBanner) {
		this.welcomeBanner = welcomeBanner;
	}
	
	@Lob
	@Column(name = "right_banner")
	public byte[] getRightBanner() {
		return rightBanner;
	}
	public void setRightBanner(byte[] rightBanner) {
		this.rightBanner = rightBanner;
	}
	
	@Column(name = "ecommerce_status")
	public boolean isEcommerceStatus() {
		return ecommerceStatus;
	}
	public void setEcommerceStatus(boolean ecommerceStatus) {
		this.ecommerceStatus = ecommerceStatus;
	}
	
	@Column(name = "shipping_costs")
	public Integer getShippingCosts() {
		return shippingCosts;
	}
	public void setShippingCosts(Integer shippingCosts) {
		this.shippingCosts = shippingCosts;
	}
	
	@Column(name = "free_shipping")
	public Integer getFreeShipping() {
		return freeShipping;
	}
	public void setFreeShipping(Integer freeShipping) {
		this.freeShipping = freeShipping;
	}
	
	
	
	
}
	
	
	
	
	
	
	
	
	
	
	
	

	