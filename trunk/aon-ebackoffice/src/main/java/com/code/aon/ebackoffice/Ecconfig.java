package com.code.aon.ebackoffice;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

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
	
	@Column(name = "bank_transfer")
	public PayMethod getBankTransfer() {
		return bankTransfer;
	}
	public void setBankTransfer(PayMethod bankTransfer) {
		this.bankTransfer = bankTransfer;
	}
	
	@Column(name = "cash_on_delivery")
	public PayMethod getCashOnDelivery() {
		return cashOnDelivery;
	}
	public void setCashOnDelivery(PayMethod cashOnDelivery) {
		this.cashOnDelivery = cashOnDelivery;
	}
	
	public PayMethod getVisa() {
		return visa;
	}
	public void setVisa(PayMethod visa) {
		this.visa = visa;
	}
	
	
	public PayMethod getPaypal() {
		return paypal;
	}
	public void setPaypal(PayMethod paypal) {
		this.paypal = paypal;
	}
	
	@Column(name = "bank_draft")
	public PayMethod getBankDraft() {
		return bankDraft;
	}
	public void setBankDraft(PayMethod bankDraft) {
		this.bankDraft = bankDraft;
	}
	
	

	
}
	
	
	
	
	
	
	
	
	
	
	
	

	