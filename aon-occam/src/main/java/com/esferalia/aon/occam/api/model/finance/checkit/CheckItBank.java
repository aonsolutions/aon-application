package com.esferalia.aon.occam.api.model.finance.checkit;

import java.io.Serializable;

public class CheckItBank implements Serializable{
	private static final long serialVersionUID = 6814967126971071007L;
	private Integer id;
	private String name;

	public Integer getId() {
		return id;
	}
	public CheckItBank setId(Integer id) {
		this.id = id;
		return this;
	}
	public String getName() {
		return name;
	}
	public CheckItBank setName(String name) {
		this.name = name;
		return this;
	}
}
