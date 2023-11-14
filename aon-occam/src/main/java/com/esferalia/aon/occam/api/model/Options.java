package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class Options implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private boolean full;
	private Integer page;
	private Integer perPage;
	private Integer offset;
	private Integer limit;

	public Options() {
		this.full = false;
	}
	
	public boolean isFull() {
		return full;
	}
	
	public Options setFull(boolean full) {
		this.full = full;
		return this;
	}
	
	public Integer getPage() {
		return page;
	}
	
	public Options setPage(Integer page) {
		this.page = page;
		return this;
	}
	
	public Integer getPerPage() {
		return perPage;
	}
	
	public Options setPerPage(Integer perPage) {
		this.perPage = perPage;
		return this;
	}
	
	public boolean isPagination() {
		return getPage() != null && getPerPage() != null;
	}
	
	public Integer getOffset() {
		return offset;
	}
	
	public Options setOffset(Integer offset) {
		this.offset = offset;
		return this;
	}

	public Integer getLimit() {
		return limit;
	}

	public Options setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}


}
