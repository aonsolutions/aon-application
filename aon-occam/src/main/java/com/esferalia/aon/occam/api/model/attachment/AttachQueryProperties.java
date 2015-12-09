package com.esferalia.aon.occam.api.model.attachment;

public class AttachQueryProperties {
	Integer limit;
	String orderby;
	
	public Integer getLimit() {
		return limit;
	}
	public AttachQueryProperties setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}
	public String getOrderby() {
		return orderby;
	}
	public AttachQueryProperties setOrderby(String orderby) {
		this.orderby = orderby;
		return this;
	}
}
