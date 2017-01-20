package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.registry.Registry;

public class Carrier extends Registry implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer scope;

	public Integer getScope() {
		return scope;
	}

	public Carrier setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
}
