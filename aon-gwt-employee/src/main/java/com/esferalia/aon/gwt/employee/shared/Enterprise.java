package com.esferalia.aon.gwt.employee.shared;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Enterprise implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2430564873016945740L;
	
	private String 			name;
	private List<Workplace> workplaces;
	
	public Enterprise() {
		workplaces = new LinkedList<Workplace>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<Workplace> getWorkplaces() {
		return workplaces;
	}
	
	public void addWorkplace(Workplace workplace ) {
		workplaces.add(workplace);
	}
	
}
