package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.Quartet;

@SuppressWarnings("serial")
public class EmployeeEventsData implements Serializable {
	
	// ------------------------------- EmployeeEventsVariable
	
	public interface EVENTimedVariable<V> {
		
		public Date getStartDate();
		public Date getEndDate();
		public V getValue();

	}
	
	public class EmployeeEventsVariable implements EVENTimedVariable<Double>{

		private Date startDate;
		private Date endDate;
		private Double value;
		
		public EmployeeEventsVariable(Date startDate, Date endDate, Double value) {
			this.startDate = startDate;
			this.endDate = endDate;
			this.value = value;
		}
		
		public EmployeeEventsVariable() {
			this.startDate = null;
			this.endDate = null;
			this.value = null;
		}

		@Override
		public Date getStartDate() {
			return this.startDate;
		}

		@Override
		public Date getEndDate() {
			return this.endDate;
		}

		@Override
		public Double getValue() {
			return this.value;
		}
		
	}
	
	// ------------------------------- VARIABLES
	
	private Map<String,ArrayList<Quartet<Date, Date, String, String>>> contractEventsList;
	
	private boolean fullTimeJourney;
	private String tc2;
	
	private Date contractStartDate;
	private Date contractEndDate;
	// ------------------------------- CONSTRUCTOR
	
	public EmployeeEventsData() {
		super();
	}
	
	public EmployeeEventsData(Map<String,ArrayList<Quartet<Date, Date, String, String>>> contractEventsList,
			boolean fullTimeJourney, String tc2, Date contractStartDate, Date contractEndDate) {
		super();
		this.contractEventsList = contractEventsList;
		this.fullTimeJourney = fullTimeJourney;
		this.tc2 = tc2;
		
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
	}
	
	// ------------------------------- GETTERS / SETTERS

	public Map<String,ArrayList<Quartet<Date, Date, String, String>>> getContractEventsList() {
		return contractEventsList;
	}

	public EmployeeEventsData setContractEventsList(Map<String,ArrayList<Quartet<Date, Date, String, String>>> contractEventsList) {
		this.contractEventsList = contractEventsList;
		return this;
	}
	
	public boolean isFullTimeJourney() {
		return fullTimeJourney;
	}

	public EmployeeEventsData setFullTimeJourney(boolean fullTimeJourney) {
		this.fullTimeJourney = fullTimeJourney;
		return this;
	}
	
	public String getTC2() {
		return this.tc2;
	}
	
	public void setTC2(String tc2) {
		this.tc2 = tc2;
	}
	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate =  contractStartDate;
	}
	
	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate =  contractEndDate;
	}
	
	public Date getContractStartDate() {
		return this.contractStartDate;
	}
	
	public Date getContractEndDate() {
		return this.contractEndDate;
	}
	
	// ------------------- ADD EVENT DATA
	
	public void addEventData(String var, Date startDate, Date endDate, String value) {
		
		// Get varList and sort it
		ArrayList<Quartet<Date, Date, String, String>> varList = contractEventsList.get(var);
		
		varList.sort(new Comparator<Quartet<Date, Date, String, String>>() {
			@Override
			public int compare(Quartet<Date, Date, String, String> o1, Quartet<Date, Date, String, String> o2) {
				return o1.getStartDate().compareTo(o2.getStartDate());
			}
		});
		
		// Inicializamos si variable que nos indica si ha sido insertado
		Boolean added = false;
		
		ArrayList<Quartet<Date, Date, String, String>> newVarList = new ArrayList<Quartet<Date,Date,String,String>>();
		
		if(varList.isEmpty()) {
			newVarList.add(new Quartet<Date, Date, String, String>(startDate, endDate, var, value));
			added = true;
		} 
		
		for(Quartet<Date, Date, String, String> quartet : varList) {
			if(added) {
				if(!isNullOrEndContract(endDate)) {
					if(isNullOrEndContract(quartet.getEndDate())) {
						Quartet<Date, Date, String, String> lastQuartetInsert = newVarList.get(newVarList.size() - 1);
						Date newStartDate = DateUtils.copyDateOnly(lastQuartetInsert.getEndDate());
						newStartDate = DateUtils.addDays2Date(newStartDate, 1);
						
						quartet.setStartDate(newStartDate);
						
						newVarList.add(quartet);
					} else if(afterOrEqual(endDate, quartet.getEndDate())) {
						continue;
					} else if(endDate.before(quartet.getEndDate()) && endDate.after(quartet.getStartDate())) {
						Date newStartDate = DateUtils.copyDateOnly(endDate);
						DateUtils.addDays2Date(newStartDate, 1);
						DateUtils.resetTime(newStartDate);
						
						newVarList.add(new Quartet<Date, Date, String, String>(newStartDate, quartet.getEndDate(), var, quartet.getExpression()));
					} else
						newVarList.add(quartet);
				}
			} else {
				// Si el elemento de la lista es anterior al insertado
				if(beforeOrEqual(quartet.getStartDate(), startDate)) {
					// Si el elemento de la lista tiene fecha fin null, tendremos que meter el nuevo tramo entre medias
					if(quartet.getStartDate().equals(startDate) && null == endDate) {
						newVarList.add(new Quartet<Date, Date, String, String>(startDate, endDate, var, value));
						added = true;
						continue;
					}
					if(isNullOrEndContract(quartet.getEndDate())) {
						if(isNullOrEndContract(endDate)) {
							Date newEndDate = DateUtils.copyDateOnly(startDate);
							newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
							quartet.setEndDate(newEndDate);
							
							if(!quartet.getStartDate().equals(startDate))
								newVarList.add(quartet);
							
							newVarList.add(new Quartet<Date, Date, String, String>(startDate, endDate, var, value));
							
							added = true;
							continue;
						} else {
							Date oldEndDate = null;
							if(null != quartet.getEndDate()) {
								oldEndDate = DateUtils.copyDateOnly(quartet.getEndDate());
								DateUtils.resetTime(oldEndDate);
							}
							
							Date newEndDate = DateUtils.copyDateOnly(startDate);
							newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
							quartet.setEndDate(newEndDate);
							
							newVarList.add(quartet);
							newVarList.add(new Quartet<Date, Date, String, String>(startDate, endDate, var, value));
							
							Date newStartDate = DateUtils.copyDateOnly(endDate);
							newStartDate = DateUtils.addDays2Date(newStartDate, 1);
							Quartet<Date, Date, String, String> postNewQuartet = new Quartet<Date, Date, String, String>(newStartDate, oldEndDate, var, quartet.getExpression());
							
							newVarList.add(postNewQuartet);
							
							added = true;
							continue;
						}
					// Si el elemento a añadir es el mismo tramo de la lista
					} else if(quartet.getEndDate().equals(endDate) && quartet.getStartDate().equals(startDate)) {
						newVarList.add(new Quartet<Date, Date, String, String>(startDate, endDate, var, value));
						added = true;
						continue; 
					// Si el elemento a añadir es posterior al tramo de la lista
					} else if(quartet.getEndDate().before(startDate) && !isBetween(quartet, new Quartet<Date, Date, String, String>(startDate, endDate, var, value))) {
						newVarList.add(quartet);
						continue;
					// Si el elemento a añadir esta en un tramo existente
					} else if(afterOrEqual(quartet.getEndDate(), startDate)) {
						Quartet<Date, Date, String, String> postNewQuartet = null;
						
						if(isNullOrEndContract(endDate)) {
							Date newEndDate = DateUtils.copyDateOnly(startDate);
							newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
							quartet.setEndDate(newEndDate);
							
							newVarList.add(quartet);
							newVarList.add(new Quartet<Date, Date, String, String>(startDate, endDate, var, value));
							added = true;
							continue;
						}
						
						if(!isNullOrEndContract(endDate) && endDate.before(quartet.getEndDate())) {
							Date newStartDate = DateUtils.copyDateOnly(endDate);
							newStartDate = DateUtils.addDays2Date(newStartDate, 1);
							
							postNewQuartet = new Quartet<Date, Date, String, String>(newStartDate, quartet.getEndDate(), var, quartet.getExpression());
						}
						
						if(!startDate.equals(quartet.getStartDate()) &&
							!endDate.equals(quartet.getEndDate()) &&
							!isBetween(quartet, new Quartet<Date, Date, String, String>(startDate, endDate, var, value))) {
							Date newEndDate = DateUtils.copyDateOnly(startDate);
							newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
							quartet.setEndDate(newEndDate);
							
							newVarList.add(quartet);
						} else if(endDate.equals(quartet.getEndDate())) {
							Date newEndDate = DateUtils.copyDateOnly(startDate);
							newEndDate = DateUtils.deleteDays2Date(newEndDate, 1);
							quartet.setEndDate(newEndDate);
							
							newVarList.add(quartet);
						}
						
						newVarList.add(new Quartet<Date, Date, String, String>(startDate, endDate, var, value));
						
						if(null != postNewQuartet)
							newVarList.add(postNewQuartet);
						
						added = true;
						continue;
					}
				} else {
					// Si el elemento a añadir es anterior al tramo de la lista
					if(isNullOrEndContract(endDate)) {
						newVarList.add(new Quartet<Date, Date, String, String>(startDate, endDate, var, value));
						added = true;
						continue;
					} else if (beforeOrEqual(endDate, quartet.getStartDate())) {
						if(endDate.equals(quartet.getStartDate())) {
							Date newStartDate = DateUtils.copyDateOnly(quartet.getStartDate());
							DateUtils.addDays2Date(newStartDate, 1);
							quartet.setStartDate(newStartDate);
							
							newVarList.add(new Quartet<Date, Date, String, String>(startDate, endDate, var, value));
							newVarList.add(quartet);
						} else {
							newVarList.add(new Quartet<Date, Date, String, String>(startDate, endDate, var, value));
							newVarList.add(quartet);
						}
						added = true;
						continue;
					} else if (afterOrEqual(endDate, quartet.getStartDate()))	{
						Date newStartDate = DateUtils.copyDateOnly(endDate);
						DateUtils.resetTime(newStartDate);
						DateUtils.addDays2Date(newStartDate, 1);
						
						quartet.setStartDate(newStartDate);
						
						newVarList.add(new Quartet<Date, Date, String, String>(startDate, endDate, var, value));
						newVarList.add(quartet);
						
						added = true;
						continue;
					} else
						newVarList.add(quartet);
				}
			}
		}
		
		//Si todavía no se ha añadido se añade al final de la lista
		if(!added){
			newVarList.add(new Quartet<Date, Date, String, String>(startDate, endDate, var, value));
		}
		
//		Window.alert("newVarList size : " + newVarList.size());
		
		// Set new list
		this.contractEventsList.put(var, newVarList);
	}
	
	// ----------------------- INIT VAR LIST TO SHOW
	
	public Map<String, ArrayList<EmployeeEventsVariable>> getEventDateVarList() {
		
		Map<String, ArrayList<EmployeeEventsVariable>> mapEventsVar = new HashMap<String, ArrayList<EmployeeEventsVariable>>();
		
		for (Entry<String, ArrayList<Quartet<Date, Date, String, String>>> entry : getContractEventsList().entrySet()){
			
			String varName = entry.getKey();
			ArrayList<EmployeeEventsVariable> varList = new ArrayList<EmployeeEventsVariable>();
			
			if(!entry.getValue().isEmpty()){
				for(Quartet<Date, Date, String, String> quarter : entry.getValue()){
					if(null != quarter.getExpression()) {
						// StartDate
						Date startDate = DateUtils.copyDateOnly(quarter.getStartDate());
						
						// EndDate
						Date endDate = null;
						if(null != quarter.getEndDate())
							endDate = DateUtils.copyDateOnly(quarter.getEndDate());
						else {
							Date currentDate = new Date();
							DateUtils.addYears2Date(currentDate, 1);
							
							Date newEndDate = DateUtils.getLastDayOfYear(currentDate);
							DateUtils.resetTime(newEndDate);
							
							endDate = DateUtils.copyDateOnly(newEndDate);
						}
							
						// Value
						Double value = Double.parseDouble(quarter.getExpression());
						
						// Add to var list
						if(null != endDate) {
							Date itDate = DateUtils.copyDateOnly(startDate);
							while(itDate.before(endDate)) {
								Date actualDate = DateUtils.copyDateOnly(itDate);
								DateUtils.resetTime(actualDate);
								
								Date auxStartDate = DateUtils.getFirstDayOfMonth(actualDate);
								Date auxEndDate = DateUtils.getLastDayOfMonth(actualDate);
								
								EmployeeEventsVariable eVar = new EmployeeEventsVariable(auxStartDate, auxEndDate, value);
								varList.add(eVar);
								
								DateUtils.addMonths2Date(itDate, 1);
							}
						}
					}
					
				}
				
				sortListByStartDate(varList);
			}	
			
			mapEventsVar.put(varName, varList);
		}
		
//		modifyMapEventsVar(mapEventsVar);
		
		return mapEventsVar;
		
	}
	
	private void sortListByStartDate(ArrayList<EmployeeEventsVariable> list){
		Collections.sort(list, new Comparator<EmployeeEventsVariable>(){
			public int compare(EmployeeEventsVariable variable1, EmployeeEventsVariable variable2){
				if (null == variable1.getStartDate() || null == variable2.getStartDate())
			        return 0;
			     
				return variable1.getStartDate().compareTo(variable2.getStartDate());
			}
		});
	}
	
	private void modifyMapEventsVar(Map<String, ArrayList<EmployeeEventsVariable>> mapEventsVar) {
		for (String varName: mapEventsVar.keySet()){
			ArrayList<EmployeeEventsVariable> eventVarList = mapEventsVar.get(varName);
			if(varName.contains("DIAS")){
				ArrayList<EmployeeEventsVariable> newEventVarList = groupDays(varName, eventVarList);
				mapEventsVar.put(varName, newEventVarList);
			}else{
				ArrayList<EmployeeEventsVariable> newEventVarList = checkDuplicateMonths(eventVarList);
				mapEventsVar.put(varName, newEventVarList);
			}	
		}
	}
	
	private ArrayList<EmployeeEventsVariable> checkDuplicateMonths(ArrayList<EmployeeEventsVariable> eventVarList) {
		ArrayList<EmployeeEventsVariable> newEventsList = new ArrayList<>();
		int i = 0;
		while(i < eventVarList.size()){
			if(i+1 < eventVarList.size()){
				if(eventVarList.get(i).getStartDate().getMonth() == eventVarList.get(i+1).getStartDate().getMonth()){
					newEventsList.add(eventVarList.get(i+1));
					i+=2;
				}else{
					newEventsList.add(eventVarList.get(i));
					i++;
				}
			}else{
				newEventsList.add(eventVarList.get(i));
				i++;
			}		
		}
		
		return newEventsList;
	}

	private ArrayList<EmployeeEventsVariable> groupDays(String varName, ArrayList<EmployeeEventsVariable> eventVarList) {
		Double days = 0.00;
		ArrayList<EmployeeEventsVariable> newEventsList = new ArrayList<>();
		int i = 0;
		while(i < eventVarList.size()){
			if(i+1 < eventVarList.size()){
				if(eventVarList.get(i).getStartDate().getMonth() == eventVarList.get(i+1).getStartDate().getMonth()){
					days += eventVarList.get(i).getValue();
					i++;
				}else{
					EmployeeEventsVariable eVar;
					if(days == 0)
						 eVar = new EmployeeEventsVariable(
							DateUtils.getFirstDayOfMonth(eventVarList.get(i).getStartDate()), 
							DateUtils.getLastDayOfMonth(eventVarList.get(i).getStartDate()),
							eventVarList.get(i).getValue());
					else{
						eVar = new EmployeeEventsVariable(
								DateUtils.getFirstDayOfMonth(eventVarList.get(i).getStartDate()), 
								DateUtils.getLastDayOfMonth(eventVarList.get(i).getStartDate()),
								days + eventVarList.get(i).getValue());
						days = 0.00;
					}
					newEventsList.add(eVar);
					i++;
				}
			}else{
				if(days != 0){
					days += eventVarList.get(i).getValue();
					EmployeeEventsVariable eVar = new EmployeeEventsVariable(
							DateUtils.getFirstDayOfMonth(eventVarList.get(i).getStartDate()), 
							DateUtils.getLastDayOfMonth(eventVarList.get(i).getStartDate()),
							days);
					days = 0.00;
					newEventsList.add(eVar);
					i++;
				}else{
					newEventsList.add(eventVarList.get(i));
					i++;
				}
			}		
		}
		return newEventsList;
	}

	public ArrayList<String> getVarsToUpdate() {
		ArrayList<String> varNotToUpdate = new ArrayList<>();
		
		varNotToUpdate.add("DIAS_TRABAJADOS");
		varNotToUpdate.add("DIAS_VACACIONES");
		varNotToUpdate.add("DIAS_INACTIVIDAD");
		varNotToUpdate.add("DIAS_AUSENCIA");
		varNotToUpdate.add("DIAS_HUELGA");
		varNotToUpdate.add("DIAS_ERE");
		varNotToUpdate.add("DIAS_ERE_FZA");
		varNotToUpdate.add("DIAS_ERE_FZA_EXON");
		varNotToUpdate.add("HORAS_COMPLEMENTARIAS");
		varNotToUpdate.add("HORAS_EXTRAS");
		
		ArrayList<String> result = new ArrayList<>();
		
		for(String var : contractEventsList.keySet()) {
			if(varNotToUpdate.contains(var))
				continue;
			
			result.add(var);
		}
		
		return result;
	}

	private boolean isNullOrEndContract(Date date) {
		return null == date || date.equals(getContractEndDate());
	}
	
	private boolean beforeOrEqual(Date date1, Date date2) {
		return date1.before(date2) || date1.equals(date2);
	}
	
	private boolean afterOrEqual(Date date1, Date date2) {
		return date1.after(date2) || date1.equals(date2);
	}
	
	private boolean isBetween(Quartet<Date, Date, String, String> quartet1, Quartet<Date, Date, String, String> quartet2) {
		return afterOrEqual(quartet1.getStartDate(), quartet2.getStartDate()) &&
				beforeOrEqual(quartet1.getEndDate(), quartet2.getEndDate());
	}
	
	public Map<String, ArrayList<Quartet<java.util.Date, java.util.Date, String, String>>> getContractEventsFixedList(){
		Map<String, ArrayList<Quartet<java.util.Date, java.util.Date, String, String>>> fixedMap = new HashMap<String, ArrayList<Quartet<Date,Date,String,String>>>();
		
		//TODO: GET FIXED MAP FOR CONTINUES MONTHS WIHT SAME VALUE
		
		return fixedMap;
	}
	
}
