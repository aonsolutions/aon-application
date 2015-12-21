package com.esferalia.aon.occam.api.model.product;
import com.esferalia.aon.occam.api.model.office.Tag;
public class ProductTag {
	Integer id;
	Integer domain;
	Integer product;
	Tag Tag;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDomain() {
		return domain;
	}
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	public Integer getProduct() {
		return product;
	}
	public void setProduct(Integer product) {
		this.product = product;
	}
	public Tag getTag() {
		return Tag;
	}
	public void setTag(Tag tag) {
		Tag = tag;
	}
	
	
}
