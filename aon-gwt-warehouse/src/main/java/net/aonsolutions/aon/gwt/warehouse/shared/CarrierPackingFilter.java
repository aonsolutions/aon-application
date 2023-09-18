package net.aonsolutions.aon.gwt.warehouse.shared;

import java.io.Serializable;
import java.util.Date;

public class CarrierPackingFilter implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String value;
	private Integer domain;
	private Date from;
	private Date to;
	private int page;
	private int perPage;

	public CarrierPackingFilter() {
		this.page = 1;
		this.perPage = 30;
	}
	
	public String getValue() {
		return value;
	}

	public CarrierPackingFilter setValue(String value) {
		this.value = value;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public CarrierPackingFilter setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Date getFrom() {
		return from;
	}

	public CarrierPackingFilter setFrom(Date from) {
		this.from = from;
		return this;
	}

	public Date getTo() {
		return to;
	}

	public CarrierPackingFilter setTo(Date to) {
		this.to = to;
		return this;
	}

	public int getPage() {
		return page;
	}

	public CarrierPackingFilter setPage(int page) {
		this.page = page;
		return this;
	}

	public int getPerPage() {
		return perPage;
	}

	public CarrierPackingFilter setPerPage(int perPage) {
		this.perPage = perPage;
		return this;
	}

}
