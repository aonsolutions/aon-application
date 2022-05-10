package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.view.client.ProvidesKey;

public class SSPECData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Boolean system;
	private Date startDate;
	private Date endDate;
	private String description;
	private Byte type;
	private String formula;
	
	public static final ProvidesKey<SSPECData> KEY_PROVIDER = item -> item == null ? null : item.getId();
	
	public SSPECData() {
		super();
	}
	
	public SSPECData(Boolean system, Date startDate, Date endDate, String description, Byte type, String formula) {
		this.system = system;
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
		this.type = type;
		this.formula = formula;
	}
	
	// ------------- GETTERS / SETTERS -------------
	
	public Integer getId() {
		return this.id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Boolean isSystem() {
		return this.system;
	}
	
	public void setSystem(boolean system) {
		this.system = system;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getDescription() {
		return null == description ? "" : description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public String getFormula() {
		return null == formula ? "" : formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

}
