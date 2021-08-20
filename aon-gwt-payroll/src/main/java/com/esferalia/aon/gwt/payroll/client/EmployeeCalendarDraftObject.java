package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.CalendarDayType;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.DayType;
import com.esferalia.aon.gwt.payroll.shared.CalendarHours.DayHours.DayHour;
import com.esferalia.aon.gwt.payroll.shared.CalendarHoursExtraCompl.DayHourExtraCompl;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeCalendarDraftObject {
	
	// DomainEmployeesServiceAsync
	private DomainEmployeesServiceAsync employeesService;
	
	// DomainEmployeesServiceAsync
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();

	// Default data
	private Integer contractId;
	private Date startDate;
	private Date endDate;
	
	// EmployeeCalendarInfo
	private EmployeeCalendarInfo employeeCalendarInfo;
	
	// IT data
	private List<ITEmployee> employeesList;
	private List<IT> itsList;
	
	// User Roles
	private DomainUserRoles userRoles;
	
	public EmployeeCalendarDraftObject(Integer contractId, DomainEmployeesServiceAsync employeesService) {
		// DomainEmployeesServiceAsync
		this.employeesService = employeesService;
		
		// Default data
		this.contractId = contractId;
		
		// IT data
		this.employeesList = new ArrayList<ITEmployee>();
		this.itsList = new ArrayList<IT>();
		
		// User Roles
		this.userRoles = new DomainUserRoles();
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
	// 									DB METHODS CALENDAR (IT)
	// ----------------------------------------------------------------------------------
	
	public void getEmployeeITInfo(Consumer<List<ITEmployee>> success, Consumer<Throwable> failure){
		
		impl.getEmployeeITInfo(this.contractId, new AsyncCallback<List<ITEmployee>>() {
			
			@Override
			public void onSuccess(List<ITEmployee> employeesInfoList) {
				initEmployeeList(employeesInfoList);
				initITList(employeesInfoList);
				
				impl.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
					
					@Override
					public void onSuccess(DomainUserRoles result) {
						userRoles = result;
						success.accept(employeesInfoList);	
					}
					
					@Override
					public void onFailure(Throwable caught) {}
					
				});
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	public void createUpdateITEmployee(ITEmployee employeeITInfo, Consumer<String> success, Consumer<Throwable> failure) {
		impl.createUpdateITEmployee(employeeITInfo, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String message) {	
				success.accept(message);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
	}
	
	public void deleteIT(IT it, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.deleteIT(it.getId(), new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String message) {	
				success.accept(null);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
	}
	
	public void removeIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
		deleteIT(it, s -> {
			if(it.isComunicate())
				impl.getNafxIpf(itEmployee.getEmployeeInfo().getDocument(), itEmployee.getEmployeeInfo().getSurName(), 
						itEmployee.getEmployeeInfo().getSecondSurName(), new AsyncCallback<EmployeeSegSocial>() {
							
							@Override
							public void onSuccess(EmployeeSegSocial result) {
								String naf = result.getNss();
								String regime = itEmployee.getContractInfo().getCompleteCCC().substring(0, 4);
								String ccc = itEmployee.getContractInfo().getCompleteCCC().substring(4, itEmployee.getContractInfo().getCompleteCCC().length());
								
								impl.removeIT(
										regime, 
										ccc, 
										naf, 
										"ALTA", 
										it.getStartDate(), 
										it.getStartDate(), 
										new AsyncCallback<Void>() {
									
									@Override
									public void onSuccess(Void result) {
										success.accept(result);
									}
									
									@Override
									public void onFailure(Throwable caught) {
										failure.accept(caught);
									}
								});
								
							}
	
							@Override
							public void onFailure(Throwable caught) {
							
							}
						});
			else
				success.accept(null);
		}, f -> {});
		
	}
	
	public void comunicateITBaja(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.getNafxIpf(itEmployee.getEmployeeInfo().getDocument(), itEmployee.getEmployeeInfo().getSurName(), 
				itEmployee.getEmployeeInfo().getSecondSurName(), new AsyncCallback<EmployeeSegSocial>() {
					
					@Override
					public void onSuccess(EmployeeSegSocial result) {
						String naf = result.getNss();
						String regime = itEmployee.getContractInfo().getCompleteCCC().substring(0, 4);
						String ccc = itEmployee.getContractInfo().getCompleteCCC().substring(4, itEmployee.getContractInfo().getCompleteCCC().length());
							
						float baseCC = it.getDailyCGCBase().floatValue();
						int days = 	30;

						impl.registerITBaja(
								regime, 
								ccc, 
								naf, 
								getContingency(it.getTypeLowPart()), 
								"ACTIVO", 
								getLicenseNumber(it), 
								getCias(it), 
								itEmployee.getContractInfo().getOcupation(), 
								it.getStartDate(), 
								getContractType(itEmployee.getContractInfo().getContractType()), 
								baseCC, 
								days, 
								it.getStartDate(), 
								null, 
								new AsyncCallback<Void>() {
									
									@Override
									public void onSuccess(Void result) {
										success.accept(result);
									}
									
									@Override
									public void onFailure(Throwable caught) {}
								});
					}

					@Override
					public void onFailure(Throwable caught) {}
				});
	}

	public void comunicatePaternityIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.getNafxIpf(itEmployee.getEmployeeInfo().getDocument(), itEmployee.getEmployeeInfo().getSurName(), 
				itEmployee.getEmployeeInfo().getSecondSurName(), new AsyncCallback<EmployeeSegSocial>() {
					
					@Override
					public void onSuccess(EmployeeSegSocial result) {
						String affiliationNumber = result.getNss();
						String regime = itEmployee.getContractInfo().getCompleteCCC().substring(0, 4);
						String contributionAccount = itEmployee.getContractInfo().getCompleteCCC().substring(4, itEmployee.getContractInfo().getCompleteCCC().length());
						String docType = checkIPFType(itEmployee.getEmployeeInfo().getDocument()); 
						String docNum = itEmployee.getEmployeeInfo().getDocument();
						String applicantType = checkITType(it.getMaternityType());
						String reason = checkITReason(it.getMaternityReason());
							
						float baseCC = it.getDailyCGCBase().floatValue();
						float baseCP = it.getDailyCGPBase().floatValue();
						int days = 	30;
						
						Date dateFrom = it.getStartDate();
						Date dateTo = null != it.getEndDate() ? it.getEndDate() : DateUtils.addDays2Date(DateUtils.copyDateOnly(it.getStartDate()), (16*7));
						if("P" == applicantType || applicantType.equals("P"))
							dateTo = null != it.getEndDate() ? it.getEndDate() : DateUtils.addDays2Date(DateUtils.copyDateOnly(it.getStartDate()), (12*7));
						dateTo = DateUtils.addDays2Date(dateTo, -1);
						
						impl.createITCertificate(affiliationNumber, regime, contributionAccount, docType, docNum, applicantType, reason, dateFrom, dateTo, baseCC, baseCP, days, new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {}

							@Override
							public void onSuccess(Boolean result) {
								impl.setComunicationIT(itEmployee, it, new AsyncCallback<Void>() {

									@Override
									public void onFailure(Throwable caught) {}

									@Override
									public void onSuccess(Void res) {
										success.accept(null);
									}
									
								});
							}});
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
	}
	
	public void deleteComunicateIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.getNafxIpf(itEmployee.getEmployeeInfo().getDocument(), itEmployee.getEmployeeInfo().getSurName(), 
				itEmployee.getEmployeeInfo().getSecondSurName(), new AsyncCallback<EmployeeSegSocial>() {
					
					@Override
					public void onSuccess(EmployeeSegSocial result) {
						String affiliationNumber = result.getNss();
						String regime = itEmployee.getContractInfo().getCompleteCCC().substring(0, 4);
						String contributionAccount = itEmployee.getContractInfo().getCompleteCCC().substring(4, itEmployee.getContractInfo().getCompleteCCC().length());
						Date dateFrom = it.getComunicationDate();
						Date dateTo = it.getComunicationDate();
						Date startDate = it.getStartDate();
						
						impl.deleteComunicateIT(affiliationNumber, regime, contributionAccount, dateFrom, dateTo, startDate, new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {}

							@Override
							public void onSuccess(Void result) {
								success.accept(result);
							}});
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
	}
	
	public void getNafxIpf(ITEmployee itEmployee, Consumer<EmployeeSegSocial> success, Consumer<Throwable> failure) {
		impl.getNafxIpf(itEmployee.getEmployeeInfo().getDocument(), itEmployee.getEmployeeInfo().getSurName(), 
				itEmployee.getEmployeeInfo().getSecondSurName(), new AsyncCallback<EmployeeSegSocial>() {
					
					@Override
					public void onSuccess(EmployeeSegSocial result) {
						success.accept(result);
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
	}
	
	// ----------------------------------------------------------------------------------
	// 									IT AUXILIAR METHODS
	// ----------------------------------------------------------------------------------
	
	private void initEmployeeList(List<ITEmployee> employeesInfoList) {
		employeesList.clear();
		employeesList.addAll(employeesInfoList);
	}
	
	private void initITList(List<ITEmployee> employeesInfoList) {
		itsList.clear();

		for(ITEmployee employee : employeesList)
			for(IT it : employee.getIts())
				itsList.add(it);
	}
	
	public ITEmployee getITEmployee() {
		for(ITEmployee itEmployee : employeesList)
			if(AonNumberUtils.equals(itEmployee.getContractInfo().getContractId(), contractId))
				return itEmployee;
		
		return null;
	}
	
	public IT getITByDate(Date itDate) {
		DateUtils.resetTime(itDate);
		for(IT it : itsList) {
			if(DateUtils.isAfterOrEquals(itDate, it.getStartDate()) && (null == it.getEndDate() || DateUtils.isBeforeOrEquals(itDate, it.getEndDate())))
				return it;
		}
		return null;
	}
	
	public List<ITEmployee> getActiveEmployeesList(){
		List<ITEmployee> activeEmployeeList = new ArrayList<ITEmployee>();
		Date today = new Date();
		
		for(ITEmployee itEmployee : employeesList) {
			Date contractEndDate = itEmployee.getContractInfo().getEndDate();
			if(null == contractEndDate || DateUtils.isAfterOrEquals(contractEndDate, today))
				activeEmployeeList.add(itEmployee);
		}
		
		return activeEmployeeList;
	}
	
	public boolean isUserComunica() {
		boolean isComunica = false;
		try {
			isComunica = this.userRoles.isComunica();
			return isComunica;
		} catch (NullPointerException e) {
			return isComunica;
		}
	}
	
	public String checkITType(Byte maternityType) {
		switch (maternityType) {
		case (byte)0:
			return "M";
		case (byte)1:
			return "P";
		case (byte)2:
			return "A";
		default:
			return "B";
		}
	}
	
	public String checkITReason(Byte maternityReason) {
		switch (maternityReason) {
		case (byte)0:
			return "Nacimiento de hijo";
		case (byte)1:
			return "Fallecimiento de la madre";
		case (byte)2:
			return "Cesi" + String.valueOf("\u00F3") + "n/Opci" + String.valueOf("\u00F3") + "n en favor del otro progenitor";
		case (byte)3:
			return "Parto m" + String.valueOf("\u00FA") + "ltiple";
		case (byte)4:
			return "Inicio del descanso antes del parto (solo para madre biol" + String.valueOf("\u00F3") + "gica ET)";
		default:
			return "Adopci" + String.valueOf("\u00F3") + "n/Tutela/Acogimiento";
		}
	}
	
	public String checkIPFType(String ipf) {
		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");

		if (dniPattern.test(ipf.toUpperCase()))
			return "NIF";
		else
			return "NIE";
	}
	
	private String getContractType(String contractType) {
		Integer contractTypeInt = Integer.parseInt(contractType);
		
		if((contractTypeInt >= 200 && contractTypeInt < 300) || (contractTypeInt >= 500 && contractTypeInt< 600 || contractTypeInt == 0))
			return "FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL";
		else
			return "RESTO_Y_AUTONOMOS";
	}

	private String getLicenseNumber(IT it) {
		for( ITPart itPart : it.getITParts()) {
			if(itPart.getType() == (byte)0)
				return itPart.getCollegeNumber();
		}
		return "";
	}
	
	private String getCias(IT it) {
		for( ITPart itPart : it.getITParts()) {
			if(itPart.getType() == (byte)0)
				return itPart.getCias();
		}
		return "";
	}
	
	private String getLicenseNumberAlta(IT it) {
		for( ITPart itPart : it.getITParts()) {
			if(itPart.getType() == (byte)2)
				return itPart.getCollegeNumber();
		}
		return "";
	}
	
	private String getCiasAlta(IT it) {
		for( ITPart itPart : it.getITParts()) {
			if(itPart.getType() == (byte)2)
				return itPart.getCias();
		}
		return "";
	}

	private String getContingency(Byte typeLowPart) {
		switch (typeLowPart) {
		case (byte) 0:
			return "ENFERMEDAD_COMUN";
		case (byte) 1:
			return "ACCIDENT_LABORAL";
		case (byte) 6:
			return "ACCIDENTE_NO_LABORAL";
		case (byte) 7:
			return "PERIODOS_OBSERVACION";
		case (byte) 8:
			return "ENFERMEDAD_PROFESIONAL";
		default:
			return "ENFERMEDAD_COMUN";
		}
	}
	
	private String getCauseType(Byte typeHihgPart) {
		switch (typeHihgPart) {
		case (byte) 0:
			return "CURACION";
		case (byte) 1:
			return "FALLECIMIENTO";
		case (byte) 2:
			return "INSPECCION_MEDICA";
		case (byte) 3:
			return "PROPUESTA_INVALIDEZ";
		case (byte) 4:
			return "AGOTAMIENTO_PLAZO";
		case (byte) 5:
			return "MEJORIA_PERMITE_TRABAJAR";
		case (byte) 6:
			return "INCOMPARECENCIA";
		case (byte) 7:
			return "CONTROL_INSS_12_MESES";
		case (byte) 8:
			return "RECUP_CAPACIDAD_PROF";
		default:
			return "INCOMP_CTOS_FORM";
		}
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
		return null != date && employeeCalendarInfo.getCalendarHours().isEmpty() && null != employeeCalendarInfo.getNonWorkingDays()[date.getDay()] && employeeCalendarInfo.getNonWorkingDays()[date.getDay()] == (byte)1;
	}
	
	public String getExpressionByDate(Date date) {
		return employeeCalendarInfo.getCalendarDaysType().getExpressionByDate(date);
	}
	
	public Double getHourByDate(Date date) {
		return employeeCalendarInfo.getCalendarHours().getHourByDate(date);
	}
	
	public Double getHourExtraComplByDate(Date date) {
		return employeeCalendarInfo.getCalendarHoursExtraCompl().getHourByDate(date);
	}
	
	public boolean isCalendarHourIsEmpty() {
		return employeeCalendarInfo.getCalendarHours().isEmpty();
	}
	
	public boolean isCalendarHourExtraComplIsEmpty() {
		return employeeCalendarInfo.getCalendarHoursExtraCompl().isEmpty();
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
	
	public void addDayHourExtraCompl(Date startDate, Date endDate, Double expression) {
		DayHourExtraCompl dayHourExtraCompl = new DayHourExtraCompl(startDate, endDate, expression);
		this.employeeCalendarInfo.getCalendarHoursExtraCompl().addDayHourComplementary(dayHourExtraCompl);
//		Window.alert(this.employeeCalendarInfo.getCalendarHoursExtraCompl().toStringList());
		this.employeeCalendarInfo.getCalendarHoursExtraCompl().initMapDayHoursComplementary();
	}

	// ----------------------------------------------------------------------------------
	// 										LEYEND METHOD
	// ----------------------------------------------------------------------------------
	
	public Integer getTotalYearDays(DayType dayType) {
		Integer countDays = 0;
		
		Date startDate = DateUtils.getFirstDayOfYear();
		Date endDate = DateUtils.getLastDayOfYear(startDate);
		Date iteratorDate = DateUtils.copyDateOnly(startDate);
		
		while (DateUtils.isBeforeOrEquals(iteratorDate, endDate)) {
			DayType iteratorDayType = employeeCalendarInfo.getCalendarDaysType().getDayTypeByDate(iteratorDate);
			
			if(null != iteratorDayType && iteratorDayType == dayType)
				countDays++;
			
			DateUtils.addDays2Date(iteratorDate, 1);
		}
		
		return countDays;
	}
}
