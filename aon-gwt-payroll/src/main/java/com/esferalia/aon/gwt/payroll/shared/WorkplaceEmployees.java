package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class WorkplaceEmployees implements Serializable {
	
	ArrayList<EmployeeInfo> workplaceEmployees = new ArrayList<>();
	
	public WorkplaceEmployees() {
		super();
		workplaceEmployees = new ArrayList<>();
	}
	
	// ------------- GETTERS / SETTERS -------------

	public void addEmployee(Integer employeeId, String name, String surName, String document, String ssNumber) {
		EmployeeInfo employee = new EmployeeInfo(employeeId, name, surName, document, ssNumber);
		workplaceEmployees.add(employee);
	}
	
	public void addEmployee(EmployeeInfo employee) {
		workplaceEmployees.add(employee);
	}
	
	public ArrayList<EmployeeInfo> getWorkplaceEmployees() {
		return workplaceEmployees;
	}
	
	public ArrayList<Integer> getWorkplaceEmployeesId(){
		ArrayList<Integer> ids = new ArrayList<>();
		for(EmployeeInfo employeeInfo : workplaceEmployees)
			ids.add(employeeInfo.getEmployeeId());
		return ids;
	}
	
	public ArrayList<String> getWorkplaceEmployeesName(){
		ArrayList<String> names = new ArrayList<>();
		for(EmployeeInfo employeeInfo : workplaceEmployees)
			if("" != employeeInfo.getName())
				names.add(employeeInfo.getName());
		return names;
	}
	
	public ArrayList<String> getWorkplaceEmployeesSurName(){
		ArrayList<String> surNames = new ArrayList<>();
		for(EmployeeInfo employeeInfo : workplaceEmployees)
			if("" != employeeInfo.getSurName())
				surNames.add(employeeInfo.getSurName());
		return surNames;
	}
	
	public ArrayList<String> getWorkplaceEmployeesDocument(){
		ArrayList<String> documents = new ArrayList<>();
		for(EmployeeInfo employeeInfo : workplaceEmployees)
			if("" != employeeInfo.getDocument())
				documents.add(employeeInfo.getDocument());
		return documents;
	}
	
	public ArrayList<String> getWorkplaceEmployeesSSNumber(){
		ArrayList<String> ssNumbers = new ArrayList<>();
		for(EmployeeInfo employeeInfo : workplaceEmployees)
			if("" != employeeInfo.getSsNumber())
				ssNumbers.add(employeeInfo.getSsNumber());
		return ssNumbers;
	}

	
}
