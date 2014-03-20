package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class ITPerson implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6320685869251884027L;
	
	private int registry; //id of person
	private String name; 
	private String fSurname; // first surname
	private String sSurname; //second surname	
	private boolean employee; //isWorking 
	private ArrayList<ITDataPerson> dataLeaves;	
	private boolean isWorking;
	
	public ITPerson() {
		this.registry=0;
		this.name="";
		this.fSurname="";
		this.sSurname="";
		this.employee=false;
		dataLeaves = new ArrayList<ITDataPerson>();
		this.isWorking = false;
	}

	public int getRegistry() {
		return registry;
	}

	public void setRegistry(int registry) {
		this.registry = registry;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstSurname() {
		return fSurname;
	}

	public void setFirstSurname(String fSurname) {
		this.fSurname = fSurname;
	}

	public String getSecondSurname() {
		return sSurname;
	}	

	public void setSecondSurname(String sSurname) {
		this.sSurname = sSurname;
	}
	
	public String getFullname() {
		StringBuffer sb = new StringBuffer();

		if (fSurname != null) {
			sb.append(fSurname.trim());
		}
		if (sSurname != null) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(sSurname.trim());
		}
		if (sb.length() > 0) {
			sb.append(", ");
		}
		sb.append(getName());

		return sb.toString();
	}
	
	public void setITDataPerson(ITDataPerson pData) {
		dataLeaves.add(pData);		
	}
	
	public ArrayList<ITDataPerson> getDataPerson() {
		return this.dataLeaves;
	}

	public boolean isEmployee() {
		return employee;
	}

	public void setEmployee(boolean employee) {
		this.employee = employee;
	}

	public boolean isWorking() {
		return isWorking;
	}

	public void setWorking(boolean isWorking) {
		this.isWorking = isWorking;
	}
	
	
	
}
