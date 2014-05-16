package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ITData implements Serializable {

	private static final long serialVersionUID = -8223289513867055037L;

	/**
	 * key: contractId
	 */
	private Map<Integer, Employee> employees;
	

	/**
	 * key: person registry
	 */
	private Map<Integer, LinkedList<ITDataPerson>> its;
	

	private List<Integer> contracts;

	public ITData() {
		
		employees = new LinkedHashMap<Integer, Employee>();
		
		its = new TreeMap<Integer, LinkedList<ITDataPerson>>();
		
		contracts = new ArrayList<Integer>();
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

		if (!its.containsKey(pKey)) {
			its.put(pKey, new LinkedList<ITDataPerson>());
		}
		return its.get(pKey);
	}

	public void setITDataPerson(int pKey, ITDataPerson pDataPerson) {

		if (!its.containsKey(pKey)) {
			its.put(pKey, new LinkedList<ITDataPerson>());
		}
		its.get(pKey).add(pDataPerson);
	}
	
	public void yearsExistContracts(Date pMin, Date pMax) {
		contracts.clear();
		int inicio = DateUtils.getYear(pMin);

		if (pMax == null) {
			pMax = new Date();
		}

		int end = DateUtils.getYear(pMax);

		while (inicio <= end) {
			contracts.add(inicio);
			inicio++;
		}
	}

	public List<Integer> getYears() {
		return contracts;
	}

}
