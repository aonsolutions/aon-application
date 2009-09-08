package com.code.aon.ebackoffice;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
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
	private SkinType skin;	
	private String headerImg;
	private String footerImg;
	private Boolean commerce;
	private LoginType showLogin;
	private PriceType showPrice;
	private TaxType taxInType;
	private OriginalPrice showItemPrice;
	private WishList wishList;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.ebackoffice.enumeration.SkinType")} )
	@Column(name = "skin", length = 1)
	public SkinType getSkin() {
		return skin;
	}
	public void setSkin(SkinType skin) {
		this.skin = skin;
	}
	
	
	public String getHeaderImg() {
		return headerImg;
	}
	public void setHeaderImg(String headerImg) {
		this.headerImg = headerImg;
	}
	
	
	public String getFooterImg() {
		return footerImg;
	}
	public void setFooterImg(String footerImg) {
		this.footerImg = footerImg;
	}
	
	
	public Boolean getCommerce() {
		return commerce;
	}
	public void setCommerce(Boolean commerce) {
		this.commerce = commerce;
	}
	
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.ebackoffice.enumeration.LoginType")} )
	@Column(name = "show_login", length = 1)
	public LoginType getShowLogin() {
		return showLogin;
	}
	public void setShowLogin(LoginType showLogin) {
		this.showLogin = showLogin;
	}
	
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.ebackoffice.enumeration.PriceType")} )
	@Column(name = "show_price", length = 1)
	public PriceType getShowPrice() {
		return showPrice;
	}
	public void setShowPrice(PriceType showPrice) {
		this.showPrice = showPrice;
	}
	
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.ebackoffice.enumeration.TaxType")} )
	@Column(name = "tax_in_price", length = 1)
	public TaxType getTaxInType() {
		return taxInType;
	}
	public void setTaxInType(TaxType taxInType) {
		this.taxInType = taxInType;
	}
	
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.ebackoffice.enumeration.OriginalPrice")} )
	@Column(name = "show_item_price", length = 1)
	public OriginalPrice getShowItemPrice() {
		return showItemPrice;
	}
	public void setShowItemPrice(OriginalPrice showItemPrice) {
		this.showItemPrice = showItemPrice;
	}
	
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.ebackoffice.enumeration.WishList")} )
	@Column(name = "show_wish_list", length = 1)
	public WishList getWishList() {
		return wishList;
	}
	public void setWishList(WishList wishList) {
		this.wishList = wishList;
	}
	
	

	
}
	
	
	
	
	
	
	
	
	
	
	
	

	