package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.ProductDetail;
import com.code.aon.cms.enumeration.Templates;

public class ProductHandler {

	private String alias;
	
	private String label;

	private boolean active;
	
	private String price;
	
	private String offerPrice;
	
	private BrandHandler brand;
	
	private ProductCategoryHandler productCategory;

	private String url;
	
	public ProductHandler(ProductDetail detail){
		this.alias = detail.getProduct().getAlias();
		this.label = detail.getLabel();
		this.active = detail.getProduct().isActive();
		this.price = detail.getProduct().getPrice().toString();
		this.offerPrice = detail.getProduct().getOfferPrice().toString();
		this.brand = new BrandHandler(detail.getProduct().getBrand());
		this.productCategory = new ProductCategoryHandler(detail.getProduct().getProductCategory());
		this.url = Templates.PRODUCT.getHtmlName();
		this.url = this.url.replaceAll("%NAME%", this.alias);
	}

	public String getAlias() {
		return alias;
	}

	public String getLabel() {
		return label;
	}

	public boolean isActive() {
		return active;
	}

	public String getPrice() {
		return price;
	}

	public String getOfferPrice() {
		return offerPrice;
	}

	public BrandHandler getBrand() {
		return brand;
	}

	public ProductCategoryHandler getProductCategory() {
		return productCategory;
	}
	
	public String getUrl() {
		return url;
	}

}
