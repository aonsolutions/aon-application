package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class ITData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8223289513867055037L;
		
	/**
	 * key: contractId
	 */
	private Map<Integer, Employee> employees;
	
	/**
	 * key: person registry
	 */
	private Map<Integer, LinkedList<ITDataPerson>> its;
	
	/**
	 * contract leave types.
	 */
	private String[] leaveType;
	
	private List<Integer> contracts;	
	
	public ITData() {		
		employees = new LinkedHashMap<Integer, Employee>();
		its = new TreeMap<Integer, LinkedList<ITDataPerson>>();		
		contracts = new ArrayList<Integer>();		
		addTypes();
	}
	
	private void addTypes() {
		leaveType = new String[7];
		leaveType[0] = "Enfermedad Común";
		leaveType[1] = "Enfermedad Profesional";
		leaveType[2] = "Maternidad";
		leaveType[3] = "Paternidad";
		leaveType[4] = "Riesgo Durante Embarazo";
		leaveType[5] = "Lactancia Materna";
		leaveType[6] = "Enfermedad No Profesional";
	}
	
	public String getLeaveTypePosition(int pIndex) {
		return leaveType[pIndex].toString();
	}
	
	public void setEmployee(int pKey, Employee pEmployee) {
		employees.put(pKey, pEmployee);		
	}
	
	public Map<Integer, Employee> getEmployees() {
		return employees;

	}
	
	public Map<Integer, LinkedList<ITDataPerson>> getIts() {
		return its;
	}

	public List<ITDataPerson> getITDataPerson(int pKey) {
		
		if(!its.containsKey(pKey)) {
			its.put(pKey, new LinkedList<ITDataPerson>());
		}
		return its.get(pKey);
	}	
	
	public void setITDataPerson(int pKey, ITDataPerson pDataPerson) {

		if(!its.containsKey(pKey)) {
			its.put(pKey, new LinkedList<ITDataPerson>());
		}
		its.get(pKey).add(pDataPerson);	
	}
	
	public void yearsExistContracts(Date pMin, Date pMax) {
		contracts.clear();
		int inicio = DateUtils.getYear(pMin);		
		
		if(pMax == null) {
			pMax = new Date();
		}
		
		int end = DateUtils.getYear(pMax);
		
		while(inicio <= end) {
			contracts.add(inicio);
			inicio++;
		}	
		
	}
	
	public List<Integer> getYears() {
		return contracts;
	}
	
	
}
