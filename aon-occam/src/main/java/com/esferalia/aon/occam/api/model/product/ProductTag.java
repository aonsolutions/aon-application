package com.esferalia.aon.occam.api.model.product;
import com.esferalia.aon.occam.api.model.office.Tag;
public class ProductTag {
	Integer id;
	Integer domain;
	Integer product;
	Tag tag;
		
	public Integer getId() {
		return id;
	}
	public ProductTag setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public ProductTag setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getProduct() {
		return product;
	}
	public ProductTag setProduct(Integer product) {
		this.product = product;
		return this;
		
	}
	public Tag getTag() {
		return tag;
	}
	public ProductTag setTag(Tag tag) {
		this.tag = tag;
		return this;
	}
}
