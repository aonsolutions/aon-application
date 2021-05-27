package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Activity implements Serializable {
	
	private Integer id;
	private Integer cnae2009;
	private String description;
	
	private List<CCC> cccs;
	
	public Activity() {
		this.cccs = new LinkedList<CCC>();
	}
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getCnae2009() {
		return cnae2009;
	}
	
	public void setCnae2009(Integer cnae2009) {
		this.cnae2009 = cnae2009;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public List<CCC> getCccs() {
		return cccs;
	}
	
	public void addCcc(CCC ccc) {
		this.cccs.add(ccc);
	}

	public void setCccs(List<CCC> cccs) {
		this.cccs = cccs;
	}
	
}
