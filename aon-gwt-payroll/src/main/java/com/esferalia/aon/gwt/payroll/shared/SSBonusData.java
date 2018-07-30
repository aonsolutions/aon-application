package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class SSBonusData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Date startDate;
	private Date endDate;
	private String description;
	private Byte type;
	private String formula;
	
	public SSBonusData() {
		super();
	}
	
	public SSBonusData(Integer id, Date startDate, Date endDate, String description, Byte type, String formula) {
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
		this.type = type;
		this.formula = formula;
	}
	
	// ------------- GETTERS / SETTERS -------------

	public Integer getId(){
		return id;
	}
	
	public void setId(Integer id){
		this.id = id;
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
		return description;
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
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}
}
