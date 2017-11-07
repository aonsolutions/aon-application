package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.shared.DateUtils;

public class ITData implements Serializable{

	private static final long serialVersionUID = -8223289513867055037L;
	
	/**
	 * key: contractId
	 */
	private Map<Integer, Employee> employees;	

	/**
	 * MapKey: contract id
	 * LinkedHashMap Key: contract leave id
	 */	
	
	private Map<Integer, LinkedHashMap<Integer, ITDataPerson>> dataIts;	
	private List<Integer> contracts;
	
	public static class UnmodifiableEmployee extends Employee {

		public UnmodifiableEmployee() {
			
		}		
		public UnmodifiableEmployee(Employee employee) {
			
			super.setPerson(employee.getPerson());
			super.setId(employee.getId());
			super.setDocument(employee.getDocument());
			super.setName(employee.getName());
			super.setFirstSurname(employee.getFirstSurname());
			super.setSecondSurName(employee.getSecondSurName());
			super.setSocialSecurity(employee.getSocialSecurity());
			super.setStartDate(employee.getStartDate());
			super.setSeniorityDate(employee.getSeniorityDate());
			super.setEndDate(employee.getEndDate());
			
		}
		
		@Override
		public Employee setPerson(int personId) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Employee setId(int id) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Employee setDocument(String document) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Employee setName(String name) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Employee setFirstSurname(String firstSurname) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Employee setSecondSurName(String secondSurName) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Employee setSocialSecurity(String pSocialSecurity) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Employee setStartDate(Date startDate) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Employee setEndDate(Date endDate) {
			throw new UnsupportedOperationException();
		}		
	}

	public static class UnmodifiableITDataPerson extends ITDataPerson {

		public UnmodifiableITDataPerson() {
			
		}

		public UnmodifiableITDataPerson(ITDataPerson itDataPerson) {
			
			super.setContractLeaveId(itDataPerson.getContractLeaveId());
			super.setContractId(itDataPerson.getContractId());
			super.setLeaveStartDate(itDataPerson.getLeaveStartDate());
			super.setLeaveEndDate(itDataPerson.getLeaveEndDate());
			super.setType(itDataPerson.getType());
			super.setDischarge_cause(itDataPerson.getDischarge_cause());
		}
		
		@Override
		public void setContractLeaveId(int pContractLeaveId) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setContractId(int contractId) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setLeaveStartDate(Date start_date) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setLeaveEndDate(Date end_date) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setType(Type type) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setDischarge_cause(int discharge_cause) {
			throw new UnsupportedOperationException();
		}
	}

	public ITData() {
		
		this.dataIts = new LinkedHashMap<Integer, LinkedHashMap<Integer, ITDataPerson>>();		
		this.employees = new LinkedHashMap<Integer, Employee>();			
		this.contracts = new ArrayList<Integer>();		
	}

	public void setEmployee(int pKey, Employee pEmployee) {
		employees.put(pKey, new UnmodifiableEmployee(pEmployee));
	}

	public Map<Integer, Employee> getEmployees() {
		return Collections.unmodifiableMap(employees);
	}
	
	public void setITData(int contractId, int contractLeaveId, 
			ITDataPerson itDataPerson) {
		
		if(dataIts.containsKey(contractId) == false) {	
			
			dataIts.put(contractId, new LinkedHashMap<Integer, ITDataPerson>());
		}		
		dataIts.get(contractId).put(contractLeaveId, new UnmodifiableITDataPerson(itDataPerson));		
	}
	
	public Map<Integer, ITDataPerson> getDataIts(int contractId) {
		
		if(dataIts.containsKey(contractId) == false) {	
			
			dataIts.put(contractId, new LinkedHashMap<Integer, ITDataPerson>());
		}
		
		return Collections.unmodifiableMap(dataIts.get(contractId));
		
	}
	
	public Map<Integer, LinkedHashMap<Integer, ITDataPerson>> getITs() {
		return Collections.unmodifiableMap(dataIts);
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
	
	public ITDataPerson setLeaveItem(ITDataPerson itDataPerson) {
		LinkedHashMap<Integer, ITDataPerson> add = dataIts.get(itDataPerson.getContractId());		
		ITDataPerson oldData = add.put(itDataPerson.getContractLeaveId(), itDataPerson);		
		sortMap(add);
		return oldData;
	}
	
	public ITDataPerson addLeaveItem(ITDataPerson itDataPerson) {		
		LinkedHashMap<Integer, ITDataPerson> add = dataIts.get(itDataPerson.getContractId());		
		ITDataPerson oldData = add.put(itDataPerson.getContractLeaveId(), itDataPerson);		
		sortMap(add);
		return oldData;		
	}
	
	public ITDataPerson removeLeaveItem(ITDataPerson itDataPerson) {		
		LinkedHashMap<Integer, ITDataPerson> remove = dataIts.get(itDataPerson.getContractId());
		ITDataPerson oldData = remove.remove(itDataPerson.getContractLeaveId());		
		sortMap(remove);
		return oldData;			
	}
	
	@SuppressWarnings("unchecked")
	public void sortMap(Map<Integer, ITDataPerson> map) {

		List<ITDataPerson> sortedList = new LinkedList<ITDataPerson>();
		Iterator<?> it = map.entrySet().iterator();

		while (it.hasNext()) {

			Map.Entry<Integer, ITDataPerson> e = (Map.Entry<Integer, ITDataPerson>) it
					.next();
			sortedList.add(e.getValue());
		}

		Collections.sort(sortedList);

		map.clear();

		for (int x = 0; x < sortedList.size(); x++) {

			map.put(sortedList.get(x).getContractLeaveId(), sortedList.get(x));
		}

	}

	

	

	
	
}
