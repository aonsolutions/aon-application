package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.shared.HasId;

public class CCC implements Serializable, HasId<Integer>{

	private Integer id;
	private String code;
	private String geozone;
	private String regime;
	private Byte type;
	
	private List<Employee> employees;

	
	public CCC() {
		this.employees = new LinkedList<Employee>();
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getGeozone() {
		return geozone;
	}
	
	public void setGeozone(String geozone) {
		this.geozone = geozone;
	}
	
	public String getRegime() {
		return regime;
	}
	
	public void setRegime(String regime) {
		this.regime = regime;
	}
	
	public List<Employee> getEmployees() {
		return employees;
	}
	
	public void addEmployee( Employee employee) {
		employees.add(employee);
	}
	
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}
	
}
