package com.esferalia.aon.gwt.template.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Item implements IsSerializable {
	
	private Integer id;
	private Product product;
	private String detail;
	private String detail2;
	private String detail3;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDetail() {
		return detail;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getDetail2() {
		return detail2;
	}
	public void setDetail2(String detail2) {
		this.detail2 = detail2;
	}
	public String getDetail3() {
		return detail3;
	}
	public void setDetail3(String detail3) {
		this.detail3 = detail3;
	}
	
	
}
