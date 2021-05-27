package com.esferalia.aon.occam.api.model.commission;

import java.io.Serializable;
import java.util.Date;

public class Commission implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private Date startDate;
	private Date endDate;
	
	public Integer getId() {
		return id;
	}
	public Commission setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Commission setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getName() {
		return name;
	}
	public Commission setName(String name) {
		this.name = name;
		return this;
	}
	public Date getStartDate() {
		return startDate;
	}
	public Commission setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	public Date getEndDate() {
		return endDate;
	}
	public Commission setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
}
