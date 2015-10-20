package com.esferalia.aon.gwt.office.shared;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.gwt.common.shared.HasId;

public class Notice implements Serializable, HasId<Integer> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static enum Type implements HasDescription {
		PRIORITY("Prioridad"),
		PROPERTY("Propiedades"),
		OWNER("Usuario"),
		STATUS("Status")
		;
		
		private String description;
		
		private Type(String description) {
			this.description = description;
		}
		
		@Override
		public String getDescription() {
			return description;
		}
	}
	
	private Integer id;
	private Integer domain;
	private Date date;
	private Type type;
	
	public Notice() {
		
	}
	
	public void setId (Integer id) {
		this.id = id;
	}

	@Override
	public Integer getId() {
		return this.id;
	}
	
	public void setDomain (Integer domain) {
		this.domain = domain;
	}
	
	public Integer getDomain () {
		return this.domain;
	}
	
	public void setDate (Date date) {
		this.date = date;
	}
	
	public Date getDate () {
		return this.date;
	}
	
	public void setType (Type type) {
		this.type = type;
	}
	
	public Type getType () {
		return this.type;
	}
}
