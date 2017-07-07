package com.esferalia.aon.gwt.payroll.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.payroll.client.EmployeesService;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.CalendarDraft;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.ITData;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.ReportData;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.esferalia.aon.gwt.payroll.shared.Statistics;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class EmployeesServiceTestImpl extends RemoteServiceServlet implements
		EmployeesService {
	
	@Override
	public EmployeeCalendarData getEmployeeCalendar(int contract) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}
	
	@Override
	public void setEmployeeCalendar(int contract, EmployeeCalendarUpdate updateInfo) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
	}
	
	@Override
	public CalendarDraft getCalendar(int workplaceId, Integer pattern,
			Integer calendar) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Map<Integer, String> getHolidayDescription()
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void saveHolidaysAndDays(int workplaceId, String holidayDescription,
			Integer holidayListBox, Map<Date, String> map, CalendarDraft.DayType daysTypes[])
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public void deletePropertyHoliday(Integer id, Date date)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public Statistics getWorkplaceStats(int workplaceId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Statistics getEnterpriseStats(int enterpriseId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public ITData getEnterpriseITData(int enterpriseId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public ITData getWorkplaceITData(int workplaceId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void saveITDataPerson(
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> inserts,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> deletes,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> updates)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public ReportData getA3Report(Date month, int[] workplaces)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public ReportData getFTEReport(Date start, Date end, int[] workplaces)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public ReportData getHolidayReport(Date start, Date end, int[] workplaces)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Enterprise getEnterprise() throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Enterprise[] getEnterprises() throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Payment> getAvailablePayments(int employeeId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Deduction> getAvailableDeductions(int employeeId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}
	
	@Override
	public List<Bonus> getAvailableBonuses(int employeeId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}
	
	@Override
	public List<Cost> getWorkplaceCosts(int workplaceId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Cost> getEnterpriseCosts(int enterpriseId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Salary> getSalaries(Employee employee)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Irpf> getIrpfs(Employee employee)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Extra> getExtras(List<Employee> employee)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getCostReceiptHTML(Cost cost, Type[] types, int zoom)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getIrpfReceiptHTML(Irpf irpf, int zoom)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getSalaryReceiptHTML(Cost cost, Type[] types, int zoom)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getSalaryReceiptHTML(Salary salary, int zoom)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void saveSalaryDraft(SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public SalaryDraft saveSalary(SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public AgreementDraft saveAgreementDraft(AgreementDraft agreementDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public ContextDescriptor getContext(SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public ContextDescriptor getContext(AgreementDraft agreementDraft,
			int levelId) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Result> eval(String expression, SalaryDraft salaryDraft)
			throws IllegalArgumentException, EvalException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Result> eval(String expression, AgreementDraft agreementDraft,
			int levelId) throws IllegalArgumentException, EvalException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Double calculateIrpf(SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public SalaryDraft calculateSalaryDraft(SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public SalaryDraft calculateSalaryDraft4Dummies(SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public AgreementDraft calculateAgreementDraft(AgreementDraft agreementDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getSalaryDraftReceipt(SalaryDraft salaryDraft, String mime)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getSalaryDraftReceiptHTML(SalaryDraft salaryDraft, int zoom)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getAgreementDraftReceiptHTML(AgreementDraft agreementDraft,
			int levelId, Type type, int zoom) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getIrpfDraftReceipt(SalaryDraft salaryDraft, String mime)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getIrpfDraftReceiptHTML(SalaryDraft salaryDraft, int zoom)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getSalaryPreviewReceiptHTML(SalaryPreview salaryPreview,
			int zoom) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void insertPerson(Employee employee) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}
	
	@Override
	public Employee getEmployee(int employeeId) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Employee> getEmployees(int workplaceId, Date endDate,
			String pattern, int offset, int limit)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Employee> getTrashEmployees(int workplaceId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void saveEvents(Events events, Date startDate, Date endDate)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public Events getEvents(Integer workplaceId, Date startDate, Date endDate,
			int offset, int limit, String[] names)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Variable> getVariables(SalaryDraft salaryDraft, Date startDate,
			Date endDate, String[] names) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Period getAvailPeriod(Integer workplaceId, String name)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Map<String, String> getEventsVariables(Integer workplaceId,
			Integer agreementId, Date startDate, Date endDate)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public SortedSet<Date> getChanges(Agreement agreement)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Employee pasteContract(int workplaceId, int contractId,
			String document, Date startDate, Date endDate, boolean check)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void moveContractId(Employee employee)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public void deleteContract(Employee employee)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public void delete(Salary[] salaries) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public Map<String, String> getAvaiableEmployees()
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	

	@Override
	public void setEmployeeEvents(int contract, EmployeeEventsUpdate updateInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ContextDescriptor getEmployeeEventsVariables(Integer employeeId, Date startDate, Date endDate)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EmployeeEventsData getEmployeeEvents(int contract, ArrayList<String> employeeContractVariables) {
		// TODO Auto-generated method stub
		return null;
	}

	

	

}
