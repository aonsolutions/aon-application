package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.CalendarDayType;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.DayType;
import com.esferalia.aon.gwt.payroll.shared.CalendarHours.DayHours.DayHour;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeCalendarDraftObject {
	
	// DomainEmployeesServiceAsync
	private DomainEmployeesServiceAsync employeesService;

	// Default data
	private Integer contractId;
	private Date startDate;
	private Date endDate;
	
	// EmployeeCalendarInfo
	private EmployeeCalendarInfo employeeCalendarInfo;
	
	public EmployeeCalendarDraftObject(Integer contractId, DomainEmployeesServiceAsync employeesService) {
		// DomainEmployeesServiceAsync
		this.employeesService = employeesService;
		
		// Default data
		this.contractId = contractId;
	}
	
	
	// ----------------------------------------------------------------------------------
	// 									DB METHODS CALENDAR
	// ----------------------------------------------------------------------------------
	
	public void initCalendarInfo(Consumer<EmployeeCalendarInfo> success, Consumer<Throwable> failure) {
		this.employeesService.getEmployeeCalendarInfo(contractId, new AsyncCallback<EmployeeCalendarInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(EmployeeCalendarInfo employeeCalendarInfoDB) {
				employeeCalendarInfo = employeeCalendarInfoDB;
				startDate = employeeCalendarInfo.getContractStartDate();
				endDate = employeeCalendarInfo.getContractEndDate();
				success.accept(employeeCalendarInfoDB);
			}
			
		});
	}
	
	public void saveCalendarInfo(Consumer<String> success, Consumer<Throwable> failure) {
		this.employeesService.setEmployeeCalendarInfo(contractId, employeeCalendarInfo, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(String message) {
				success.accept(message);
			}
			
		});
	}
	
	public void resetCalendarInfo(Consumer<String> success, Consumer<Throwable> failure) {
		this.employeesService.resetEmployeeCalendarInfo(contractId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(String message) {
				initCalendarInfo(
						r ->{
							success.accept(message);
							},
						f->{}
				);
			}
			
		});
	}
	
	// ----------------------------------------------------------------------------------
	// 								METHODS (PAINT CALENDAR)
	// ----------------------------------------------------------------------------------
	
	public Date getContractStartDate() {
		return this.startDate;
	}
	
	public Date getContractEndDate() {
		return this.endDate;
	}
	
	public boolean isFullTimeJourney() {
		return this.employeeCalendarInfo.getFullTimeJourney();
	}
	
	public boolean isAgrarianContract() {
		return this.employeeCalendarInfo.getAgrarianContract();
	}
	
	public DayType getDayTypeByDate(Date date) {
		DayType dayType = employeeCalendarInfo.getCalendarDaysType().getDayTypeByDate(date);
		
		return null == dayType ? DayType.NOTYPEDAY : dayType;
	}
	
	public boolean isPartialityDayTypeByDate(Date date) {
		DayType dayType = employeeCalendarInfo.getPartialityDaysType().getDayTypeByDate(date);
		
		return (null == dayType || DayType.NOTYPEDAY == dayType)? false : true;
	}
	
	public String getPartialityCoefficientByDate(Date date) {
		String coefficient = employeeCalendarInfo.getPartialityDaysType().getExpressionByDate(date);
		
		return null == coefficient ? "Revisar Parcialiad" : coefficient;
	}
	
	public boolean isDefaultNonWorkongDay(Date date) {
		return employeeCalendarInfo.getCalendarHours().isEmpty() && employeeCalendarInfo.getNonWorkingDays()[date.getDay()] == (byte)1;
	}
	
	public String getExpressionByDate(Date date) {
		return employeeCalendarInfo.getCalendarDaysType().getExpressionByDate(date);
	}
	
	public Double getHourByDate(Date date) {
		return employeeCalendarInfo.getCalendarHours().getHourByDate(date);
	}
	
	public boolean isCalendarHourIsEmpty() {
		return employeeCalendarInfo.getCalendarHours().isEmpty();
	}
	
	public Byte[] getNonWorkingDays() {
		return this.employeeCalendarInfo.getNonWorkingDays();
	}


	public void setNonWorkingDays(Byte[] nonWorkingDays) {
		this.employeeCalendarInfo.setNonWorkingDays(nonWorkingDays);
	}
	
	public String getExtraHourByDate(Date date) {
		return this.employeeCalendarInfo.getMonthExtraHoursMap().get(date);
	}


	public void setExtraHourByDate(Date date, String hourMonth) {
		this.employeeCalendarInfo.getMonthExtraHoursMap().put(date, hourMonth);
	}
	
	public boolean isDefaultFreeDay(Date date) {
		String freedayDescription = this.employeeCalendarInfo.getFestiveDaysMap().get(date);
		return null == freedayDescription ? false : true;
	}
	
	// ----------------------------------------------------------------------------------
	// 										ADD DAYS TYPE
	// ----------------------------------------------------------------------------------
	
	public void addDayType(Date startDate, Date endDate, DayType dayType, String expression) {
		CalendarDayType newCalendarDayType = new CalendarDayType(startDate, endDate, dayType, expression);
		this.employeeCalendarInfo.getCalendarDaysType().addDayType(newCalendarDayType);
//		Window.alert(this.employeeCalendarInfo.getCalendarDaysType().toString(newCalendarDayType));
		this.employeeCalendarInfo.getCalendarDaysType().initMapDaysDayType();
	}
	
	public void addPartialityDayType(Date startDate, Date endDate, DayType dayType, String expression) {
		CalendarDayType newCalendarDayType = new CalendarDayType(startDate, endDate, dayType, expression);
		this.employeeCalendarInfo.getPartialityDaysType().addDayType(newCalendarDayType);
//		Window.alert(this.employeeCalendarInfo.getPartialityDaysType().toString(newCalendarDayType));
		this.employeeCalendarInfo.getPartialityDaysType().initMapDaysDayType();
	}
	
	// ----------------------------------------------------------------------------------
	// 										ADD DAYS HOUR
	// ----------------------------------------------------------------------------------
	
	public void addDayHour(Date startDate, Date endDate, int day, Double expression) {
		DayHour dayHour = new DayHour(startDate, endDate, expression);
		this.employeeCalendarInfo.getCalendarHours().getDayHours()[day].addDayHour(dayHour);
//		Window.alert(this.employeeCalendarInfo.getCalendarHours().getDayHours()[day].toString(dayHour));
		this.employeeCalendarInfo.getCalendarHours().initMapDaysHour();
	}
}
