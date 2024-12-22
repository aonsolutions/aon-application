package com.esferalia.aon.gwt.payroll.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraft.DateField;
import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraftObject.BooleanEventMetaData;
import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraftObject.DecimalEventMetaData;
import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraftObject.EventMetaData;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Statistics;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.TreeItem;

public class Workers extends Employees {

	public Map<Integer, WorkplaceCostDocuments> workplaceCostsMap = new HashMap<>();
	public Map<Integer, WorkplaceSalaryObject> workplaceSalaryMap = new HashMap<>();
	private Map<Integer, EventsDraftObject> workplaceEventsMap = new HashMap<>();
	
	public Map<Integer, EnterpriseStatistics> enterpriseStatsMap = new HashMap<>();
	public Map<Integer, EnterpriseSalaryObject> enterpriseSalaryMap = new HashMap<>();
	public Map<Integer, EnterpriseCostDocuments> enterpriseCostsMap = new HashMap<>();

	public Workers() {
		super();
	}
	

	public Workers(boolean extended, boolean formers) {
		super(extended, formers);
	}
	
	// -------------------------------------------------------------- Employees
	
	@Override
	protected <T extends HasTreeItems> void addEnterpriseStatsItem(T enterpriseItem, Enterprise enterprise) {
	}

	@Override
	protected <T extends HasTreeItems> void addEnterpriseCostsItem(T enterpriseItem, Enterprise enterprise) {
	}
	
	@Override
	protected <T extends HasTreeItems> void addEnterpriseSalariesItem(T enterpriseItem, Enterprise enterprise) {
	}
		
	@Override
	protected <T extends HasTreeItems> TreeItem addEnterpriseWorkplaceItem(T enterpriseItem, Enterprise enterprise,
			Workplace workplace) {
		return super.addEnterpriseWorkplaceItem(tree, enterprise, workplace);
	}
	
	@Override
	protected <T extends HasTreeItems> void addEnterpriseActivityIem(T enterpriseItem, Activity activity) {
		super.addEnterpriseActivityIem(tree, activity);
	}

	@Override
	protected void loadWorkplaceCosts(TreeItem workplaceItem, Workplace workplace) {
	}
	
	@Override
	protected void loadWorkplaceStats(TreeItem workplaceItem, Workplace workplace) {
	}

	@Override
	protected <T extends HasTreeItems> void addWorkplaceStatsItem(T workplaceItem, Workplace workplace) {
	}

	@Override
	protected <T extends HasTreeItems> void addWorkplaceCostsItem(T workplaceItem, Workplace workplace) {
	}

	@Override
	protected <T extends HasTreeItems> void addWorkplaceCalendarItem(T workplaceItem, Workplace workplace) {
	}
	
	@Override
	protected <T extends HasTreeItems> void addWorkplaceSalariesItem(T workplaceItem, Workplace workplace) {
	}
	
	@Override
	protected <T extends HasTreeItems> void addWorkplaceEventsItem(T workplaceItem, EventsDraftObject eventsDraftObject) {
	}
	
	@Override
	protected <T extends HasTreeItems> void addWorkplaceAgreementItem(T workplaceItem, Enterprise enterprise,
			Agreement agreement) {
		super.addWorkplaceAgreementItem(workplaceItem, enterprise, agreement);
	}
	
	// --------------------------------------------------------------- Employee
	
	@Override
	protected void addWorkplaceEmployeeItems(TreeItem workplaceItem, TreeItem employeeItem, Employee employee,
			EmployeeDraftObject employeeDraftObject) {
	}
	
	// -------------------------------------------------------------- Enterprise

	@Override
	public void getEnterpriseCost(Enterprise enterprise, Consumer<CostDocuments> consumer) {
		EnterpriseCostDocuments enterpriseCostDocuments = enterpriseCostsMap.get(enterprise.getId());
		if ( enterpriseCostDocuments != null ) {
			consumer.accept(enterpriseCostDocuments);
		} else {
			super.getServiceEnterpriseCost(enterprise, costDocuments -> {
				enterpriseCostsMap.put(enterprise.getId(), costDocuments);
				consumer.accept(costDocuments);
			});
		}
	}
	
	@Override
	public void getEnterpriseStatistics(Enterprise enterprise, Consumer<Statistics> consumer) {
		EnterpriseStatistics enterpriseStats = enterpriseStatsMap.get(enterprise.getId());
		if ( enterpriseStats != null ) {
			consumer.accept(enterpriseStats);
		} else {
			super.getServiceEnterpriseStats(enterprise, stats -> {
				enterpriseStatsMap.put(enterprise.getId(), stats);
				consumer.accept(stats);
			});
		}
	}
	
	@Override
	public void getEnterpriseSalary(Enterprise enterprise, Consumer<EnterpriseSalaryObject> consumer) {
		EnterpriseSalaryObject enterpriseSalary = 
		enterpriseSalaryMap.computeIfAbsent(enterprise.getId(), id -> new EnterpriseSalaryObject(enterprise));
		consumer.accept(enterpriseSalary);
	}
	
	// -------------------------------------------------------------- Workplace
	
	@Override
	public void getWorkplaceCost(Workplace workplace, Consumer<CostDocuments> consumer) {
		WorkplaceCostDocuments workplaceCostDocuments = workplaceCostsMap.get(workplace.getId());
		if ( workplaceCostDocuments != null ) {
			consumer.accept(workplaceCostDocuments);
		} else {
			super.getServiceWorkplaceCost(workplace, costDocuments -> {
				workplaceCostsMap.put(workplace.getId(), costDocuments);
				consumer.accept(costDocuments);
			});
		}
	}
	
	@Override
	public void getWorkplaceSalary(Workplace workplace, Consumer<WorkplaceSalaryObject> consumer) {
		WorkplaceSalaryObject workplaceSalary = 
		workplaceSalaryMap.computeIfAbsent(workplace.getId(), id -> new WorkplaceSalaryObject(workplace));
		consumer.accept(workplaceSalary);
	}

	@Override
	public void getWorkplaceEvents(Workplace workplace, Consumer<EventsDraftObject> consumer) {
		Agreement agreement = workplace.getAgreement();
		EventsDraftObject eventsDraftObject = 
		workplaceEventsMap.computeIfAbsent(workplace.getId(), id ->
			new EventsDraftObject(id,
					agreement != null ? agreement.getId() : null,
					new BooleanEventMetaData("DIAS_EFECTIVOS", DateField.DAY),
					new BooleanEventMetaData("DIAS_VACACIONES", DateField.DAY),
					new DecimalEventMetaData("COEFICIENTE_ERE", DateField.DAY),
					new EventMetaData("OBSERVACIONES", DateField.MONTH))
		);
			
		consumer.accept(eventsDraftObject);
	}
	private Map<Integer, CalendarDraftObjectData> workplaceCalendarMap = new HashMap<>();

	@Override
	public void getWorkplaceCalendar(Workplace workplace, Consumer<CalendarDraftObjectData> consumer) {
		CalendarDraftObjectData calendarDraftObjectData = 
		workplaceCalendarMap.computeIfAbsent(workplace.getId(), CalendarDraftObjectData::new);
		consumer.accept(calendarDraftObjectData);		
	}
	
	// --------------------------------------------------------------- Employee

	private Map<Integer, EmployeeSalaryObject> employeeSalaryMap = new HashMap<>();
	
	@Override
	public void getEmployeeSalary(SalaryDraftObject employeeDraftObject, Consumer<EmployeeSalaryObject> consumer) {
		Employee employee = employeeDraftObject.getEmployee();
		EmployeeSalaryObject employeeSalaryObject = 
		employeeSalaryMap.computeIfAbsent(employee.getId(), id -> new EmployeeSalaryObject(id, employee.getFullname()));
		consumer.accept(employeeSalaryObject);
	}
	
	private Map<Integer, EmployeeEventsDraftObject> employeeEventsMap = new HashMap<>();

	@Override
	public void getEmployeeEvents(SalaryDraftObject employeeDraftObject, Consumer<EmployeeEventsDraftObject> consumer) {
		Employee employee = employeeDraftObject.getEmployee();
		EmployeeEventsDraftObject employeeEventsDraftObject= 
		employeeEventsMap .computeIfAbsent(employee.getId(), EmployeeEventsDraftObject::new);
		consumer.accept(employeeEventsDraftObject);
	}
	
	private Map<Integer, SalaryDraftObject> employeeSalaryDraftMap = new HashMap<>();

//	@Override
	public void getEmployeeSalaryDraft(EmployeeDraftObject employeeDraftObject, Consumer<SalaryDraftObject> consumer) {
		Employee employee = employeeDraftObject.getEmployee();
		SalaryDraftObject salaryDraftObject = 
		employeeSalaryDraftMap .computeIfAbsent(employee.getId(), id -> super.newSalaryDraftObject(employee));
		consumer.accept(salaryDraftObject);
	}
	
	private Map<Integer, EmployeeCalendarDraftObject> employeeCalendarMap = new HashMap<>();

	@Override
	public void getEmployeeCalendar(SalaryDraftObject employeeDraftObject, Consumer<EmployeeCalendarDraftObject> consumer) {
		Employee employee = employeeDraftObject.getEmployee();
		EmployeeCalendarDraftObject employeeCalendarDraftObject = 
		employeeCalendarMap.computeIfAbsent(employee.getId(), EmployeeCalendarDraftObject::new);
		consumer.accept(employeeCalendarDraftObject);
	}
	
	private Map<Integer, EmployeeDraftObject> employeeDraftMap = new HashMap<>();

	@Override
	public void getEmployeeDraft(SalaryDraftObject salaryDraftObject, Consumer<EmployeeDraftObject> consumer) {
		Employee employee = salaryDraftObject.getEmployee();
		EmployeeDraftObject employeeDraftObject = 
		employeeDraftMap.computeIfAbsent(employee.getId(), id -> super.newEmployeeDraftObject(employee));
		consumer.accept(employeeDraftObject);
	}
}
