package com.esferalia.aon.gwt.employee.shared;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Enterprise implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2430564873016945740L;
	
	private int			id;
	
	private String 			name;
	private List<Cost>		costs;
	private List<Workplace> workplaces;
	
	public Enterprise() {
		costs = new LinkedList<Cost>();
		workplaces = new LinkedList<Workplace>();
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<Cost> getCosts() {
		return costs;
	}
	
	public void setCosts(List<Cost> costs) {
		this.costs = costs;
	}
	
	public List<Workplace> getWorkplaces() {
		return workplaces;
	}
	
	public void addWorkplace(Workplace workplace ) {
		workplaces.add(workplace);
	}
	
}
