package com.esferalia.aon.gwt.employee.shared;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Workplace implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3342539954330850324L;
	
	private int				id;
	private String 			address;
	private String 			description;
	private List<Cost>		costs;
	private List<Employee> 	employees;
	
	public Workplace() {
		costs = new LinkedList<Cost>();
		employees = new LinkedList<Employee>();
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<Cost> getCosts() {
		return costs;
	}
	
	public void setCosts(List<Cost> costs) {
		this.costs = costs;
	}
	
	public List<Employee> getEmployees() {
		return employees;
	}
	
	public void addEmployee(Employee employee){
		employees.add(employee);
	}
	
}
