package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraftObject.EventMetaData;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.VariableDescriptor;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EventsDraftObject {
	
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
	
	// --------------------------------------------- INTERFACE REDO/UNDO -----------------------------------------------
	
	private class CompositeUndoable<T extends Undoable > implements Undoable {

		private Collection<T> undos;

		public CompositeUndoable(Collection<T> undos) {
			this.undos = undos;
		}

		@Override
		public void redo() {
			for (T undo : undos)
				undo.redo();
		}

		@Override
		public void undo() {
			for (T undo : undos){
				undo.undo();
			}
		}

	}
		
	class SetVariableEdit implements Undoable {

		private EmployeeEventsVariable oldEmployeeEventsVariable;
		private EmployeeEventsVariable newEmployeeEventsVariable;
		private String variable;
		
		public SetVariableEdit(EmployeeEventsVariable oldEmployeeEventsVariable, EmployeeEventsVariable newEmployeeEventsVariable, 
							String variable) {
			this.oldEmployeeEventsVariable = oldEmployeeEventsVariable;
			this.newEmployeeEventsVariable = newEmployeeEventsVariable;
			this.variable = variable;
		}
		
		@Override
		public void undo() {
			draftMapEvents.get(this.variable).remove(newEmployeeEventsVariable);
			if (oldEmployeeEventsVariable != null){
				draftMapEvents.get(this.variable).add(oldEmployeeEventsVariable);
			}
		}
		
		@Override
		public void redo() {
			if (oldEmployeeEventsVariable != null)
				draftMapEvents.get(this.variable).remove(this.oldEmployeeEventsVariable);
			
			draftMapEvents.get(this.variable).add(this.newEmployeeEventsVariable);
		}
		
	}

	private Map<String, ArrayList<EmployeeEventsVariable>> mapEvents;
	private Map<String, ArrayList<EmployeeEventsVariable>> draftMapEvents;
	
	private Map<String, ArrayList<EmployeeEventsVariable>> mapEmployeeEventsVar;
	
	private ArrayList<String> workplaceEmployees;
	private ArrayList<Integer> workplaceEmployeesId;
	private Set<String> allVariables;
	
	//LISTA CON LAS VARIABLES QUE TIENE CADA EMPLEADO
	private ArrayList<String> employeeContractVariables;
	private ArrayList<String> employeeContractVariablesDB;
	
	private Integer workplaceId;
	private Integer agreementId;

	public UndoManager<Undoable> undoManager;
	
	private EmployeesServiceAsync employeesServiceAsync;

	public EventsDraftObject(Integer workplaceId, Integer agreeementId,
			EmployeesServiceAsync employeesServiceAsync,
			EventMetaData... eventsMetaData) {
		
		this.mapEmployeeEventsVar = new HashMap<String, ArrayList<EmployeeEventsVariable>>();
		
		this.workplaceId = workplaceId;
		this.agreementId = agreeementId;
		this.employeesServiceAsync = employeesServiceAsync;
		
		this.workplaceEmployees = new ArrayList<>();
		this.workplaceEmployeesId = new ArrayList<>();
		this.allVariables = new HashSet<>();
		
		this.employeeContractVariables = new ArrayList<String>();
		this.employeeContractVariablesDB = new ArrayList<String>();	
	}

	
	// --------------------------------
	//      METODOS SYNC DB
	// --------------------------------
	
	public void getWorkPlaceEmployeesDB(int year, Consumer<WorkplaceEmployees> success, Consumer<Throwable> failure) {
		
		employeesServiceAsync.getWorkplaceEmployees(this.workplaceId, new AsyncCallback<WorkplaceEmployees>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(WorkplaceEmployees result) {
				for(EmployeeInfo employee : result.getWorkplaceEmployees()){
					workplaceEmployeesId.add(employee.getEmployeeId());
					workplaceEmployees.add(employee.getSurName()+", "+employee.getName());
				}
				
				
				initializeDBEventsVariables(year,
					r -> {success.accept(result);}, 
					f -> {});
				
			}
			
		});
	}
	
	public void initializeDBEventsVariables(int year, Consumer<ContextDescriptor> success, Consumer<Throwable> failure) {
		
		Window.alert("METODO PARA VARIABLES BD");
		
		for(Integer employeeId : getEmployeesId()){
			
			employeesServiceAsync.getEmployeeEventsVariables(employeeId, new Date(year,0,1), new Date(year,11,31), 
			new AsyncCallback<ContextDescriptor>() {
			
				@Override
				public void onSuccess(ContextDescriptor context) {
					
					employeeContractVariables.clear();
					
					createAllVariables("DIAS_VACACIONES");
					createAllVariables("DIAS_AUSENCIA");
					createAllVariables("DIAS_HUELGA");
					createAllVariables("DIAS_ERE");
					createAllVariables("HORAS_EXTRAS");
					createAllVariables("HORAS_COMPLEMENTARIAS");
					
					//Lista con las variables a descargar de la base de datos
					employeeContractVariablesDB.add("DIAS_VACACIONES");
					employeeContractVariablesDB.add("DIAS_AUSENCIA");
					employeeContractVariablesDB.add("DIAS_HUELGA");
					employeeContractVariablesDB.add("DIAS_ERE");
					employeeContractVariablesDB.add("HORAS_EXTRAS");
					employeeContractVariablesDB.add("HORAS_COMPLEMENTARIAS");
					employeeContractVariablesDB.add("IMPORTE_HORA_EXTRA");
		
					for (String varName : context.getVariables()){
						ArrayList<EmployeeEventsVariable> varList = new ArrayList<EmployeeEventsVariable>();
						createAllVariables(varName);
						if(context.getList(varName).isEmpty()){
							mapEmployeeEventsVar.put(varName, varList);
							continue;
						}
						for (VariableDescriptor var : context.getList(varName)){
							Date startDate = var.getStartDate();
							Date endDate = var.getEndDate();
							Double value = Double.valueOf(var.getValue());
							if(startDate.getMonth() == endDate.getMonth()){
								EmployeeEventsVariable eVar = new EmployeeEventsVariable(startDate, endDate, value);
								varList.add(eVar);
							}else{
								for(int i = startDate.getMonth(); i <= endDate.getMonth(); i++){
									Date auxStartDate = new Date(startDate.getYear(), i, 1);
									Date auxEndDate = new Date(startDate.getYear(), i+1, 0);
									EmployeeEventsVariable eVar = new EmployeeEventsVariable(auxStartDate, auxEndDate, value);
									varList.add(eVar);
								}
							}
						}
						sortListByStartDate(varList);
						
						mapEmployeeEventsVar.put(varName, varList);
					}
					
					initializeDBCalendar(employeeId,
							s -> { success.accept(context);}, 
							f -> {}
					);
					
				}
				
				private void createAllVariables(String var) {
					employeeContractVariables.add(var);
					allVariables.add(var);	
				}

				@Override
				public void onFailure(Throwable caught) {
					failure.accept(caught);
				}
			});	
		}
	}
	
	public void initializeDBCalendar(int employeeId, Consumer<EmployeeEventsData> success, Consumer<Throwable> failure) {
		
		employeesServiceAsync.getEmployeeEvents(employeeId, this.employeeContractVariablesDB, new AsyncCallback<EmployeeEventsData>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(EmployeeEventsData result) {
				
				for (Entry<String, ArrayList<Quartet<java.sql.Date, java.sql.Date, String, String>>> entry : result.getContractEventsList().entrySet()){
					
					String varName = entry.getKey();
					ArrayList<EmployeeEventsVariable> varList = new ArrayList<EmployeeEventsVariable>();
					
					if(!entry.getValue().isEmpty()){
						for(Quartet<java.sql.Date, java.sql.Date, String, String> quarter : entry.getValue()){
							Date startDate = DateUtils.copyDateOnly(quarter.getStartDate());
							Date endDate = null;
							if(null != quarter.getEndDate())
								endDate = DateUtils.copyDateOnly(quarter.getEndDate());
							Double value = Double.parseDouble(quarter.getExpression());
//							EmployeeEventsVariable var = new EmployeeEventsVariable(startDate, endDate, value);
//							varList.add(var);
							if(startDate.getMonth() == endDate.getMonth()){
								EmployeeEventsVariable eVar = new EmployeeEventsVariable(startDate, endDate, value);
								varList.add(eVar);
							}else{
								for(int i = startDate.getMonth(); i <= endDate.getMonth(); i++){
									Date auxStartDate = new Date(startDate.getYear(), i, 1);
									Date auxEndDate = new Date(startDate.getYear(), i+1, 0);
									EmployeeEventsVariable eVar = new EmployeeEventsVariable(auxStartDate, auxEndDate, value);
									varList.add(eVar);
								}
							}
						}
						sortListByStartDate(varList);
					}	
					mapEmployeeEventsVar.put(varName, varList);
				}
				
				modifyMapEventsVar();
				
				//IMPRIMIR VARIABLES
//				for(String name : mapEventsVar.keySet()){
//					for(EmployeeEventsVariable var: mapEventsVar.get(name)){
//						Window.alert(name+" = "+var.getValue()+", startDate :"+var.getStartDate()+", endDate :"+var.getEndDate());
//					}
//				}
				
				success.accept(result);
			}

			private void modifyMapEventsVar() {
				for (String varName: mapEmployeeEventsVar.keySet()){
					ArrayList<EmployeeEventsVariable> eventVarList = mapEmployeeEventsVar.get(varName);
					if(varName.contains("DIAS")){
						ArrayList<EmployeeEventsVariable> newEventVarList = groupDays(varName, eventVarList);
						mapEmployeeEventsVar.put(varName, newEventVarList);
					}else{
						ArrayList<EmployeeEventsVariable> newEventVarList = checkDuplicateMonths(eventVarList);
						mapEmployeeEventsVar.put(varName, newEventVarList);
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
										days);
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
		});
	}

	
	
	
	// --------------------------------
	//      GETTER & SETTER
	// --------------------------------
	
	public ArrayList<String> getWorkplaceEmployees(){
		return this.workplaceEmployees;
	}
	
	public Set<String> getAllVariables(){
		return this.allVariables;
	}
	
	public ArrayList<Integer> getEmployeesId(){
		return this.workplaceEmployeesId;
	}
	
	/**
	 * METODOS AUX
	 */
	
	private void sortListByStartDate(ArrayList<EmployeeEventsVariable> list){
		Collections.sort(list, new Comparator<EmployeeEventsVariable>(){
			public int compare(EmployeeEventsVariable variable1, EmployeeEventsVariable variable2){
				if (null == variable1.getStartDate() || null == variable2.getStartDate())
			        return 0;
			     
				return variable1.getStartDate().compareTo(variable2.getStartDate());
			}
		});
	}

//	public void save(String event, final SaveCallback callback) {
//		final Events dirtyEvents = getEvents(event);
//		employeesServiceAsync.saveEvents(dirtyEvents, startDate, endDate,
//				new AsyncCallback<Void>() {
//
//					@Override
//					public void onFailure(Throwable caught) {
//						callback.onSaveFailure(caught);
//					}
//
//					@Override
//					public void onSuccess(Void result) {
//						callback.onSaveSucces();
//					}
//
//				});
//	}

}
