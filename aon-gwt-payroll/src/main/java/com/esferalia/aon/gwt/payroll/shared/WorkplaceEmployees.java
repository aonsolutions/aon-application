package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import com.esferalia.aon.gwt.payroll.client.Triplet;

@SuppressWarnings("serial")
public class WorkplaceEmployees implements Serializable {
	
	ArrayList<EmployeeInfo> workplaceEmployees = new ArrayList<>();
	
	public WorkplaceEmployees() {
		super();
	}

	public WorkplaceEmployees(ArrayList<Triplet<Integer, String, String>> listEmployeeInfo) {
		workplaceEmployees = new ArrayList<>();
		
		for(Triplet<Integer, String, String> triplet : listEmployeeInfo){
			EmployeeInfo employee = new EmployeeInfo(triplet.getId(), triplet.getName(), triplet.getSurName());
			workplaceEmployees.add(employee);
		}	
	}
	
	// ------------- GETTERS / SETTERS -------------

	public ArrayList<EmployeeInfo> getWorkplaceEmployees() {
		return workplaceEmployees;
	}
	
}
