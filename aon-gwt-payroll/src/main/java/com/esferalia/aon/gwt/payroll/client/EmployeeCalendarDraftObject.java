package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.CalendarDayType;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.DayType;
import com.esferalia.aon.gwt.payroll.shared.CalendarExtraHours.DayHourExtra;
import com.esferalia.aon.gwt.payroll.shared.CalendarHours.DayHours.DayHour;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeCalendarDraftObject {
	
	// -------------------------------------------- Variables
	
	// DomainEmployeesServiceAsync
	private DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
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
	
	// DateFormat
	private DateTimeFormat dayOfWeek = DateTimeFormat.getFormat("c");
	
	// -------------------------------------------- Constructor
	
	public EmployeeCalendarDraftObject(Integer contractId) {
		
		// Default data
		this.contractId = contractId;
		
		// IT data
		this.employeesList = new ArrayList<>();
		this.itsList = new ArrayList<>();
		
		// User Roles
		this.userRoles = new DomainUserRoles();
	}
	
	
	// -------------------------------------------- Calendar (DataBase)
	
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
	
	public void saveCalendarInfo(Consumer<Void> success, Consumer<Throwable> failure) {
		this.employeesService.setEmployeeCalendarInfo(contractId, employeeCalendarInfo, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
			
		});
	}
	
	public void resetCalendarInfo(Consumer<Void> success, Consumer<Throwable> failure) {
		this.employeesService.resetEmployeeCalendarInfo(contractId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				initCalendarInfo(
						r -> success.accept(result),
						f->{}
				);
			}
			
		});
	}
	
	// -------------------------------------------- IT (DataBase & Comunications)
	
	public void getEmployeeITInfo(Consumer<List<ITEmployee>> success, Consumer<Throwable> failure){
		
		impl.getEmployeeITInfo(this.contractId, new AsyncCallback<List<ITEmployee>>() {
			
			@Override
			public void onSuccess(List<ITEmployee> employeesInfoList) {
				initEmployeeList(employeesInfoList);
				initITList();
				
				impl.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
					
					@Override
					public void onSuccess(DomainUserRoles result) {
						userRoles = result;
						success.accept(employeesInfoList);	
					}
					
					@Override
					public void onFailure(Throwable caught) {
						failure.accept(caught);
					}
					
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void createUpdateITEmployee(ITEmployee employeeITInfo, Consumer<String> success, Consumer<Throwable> failure) {
		impl.createUpdateITEmployee(employeeITInfo, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String message) {	
				success.accept(message);	
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void deleteIT(IT it, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.deleteIT(it.getId(), new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String message) {	
				success.accept(null);	
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void removeIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
		deleteIT(it, success::accept, failure::accept);
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
								itEmployee.getContractInfo().getAgreementCategory(),
								"Las propias de " + itEmployee.getContractInfo().getAgreementCategory(),
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
						failure.accept(caught);
					}
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
						if(applicantType.equals("P"))
							dateTo = null != it.getEndDate() ? it.getEndDate() : DateUtils.addDays2Date(DateUtils.copyDateOnly(it.getStartDate()), (12*7));
						dateTo = DateUtils.addDays2Date(dateTo, -1);
						
						impl.createITCertificate(affiliationNumber, regime, contributionAccount, docType, docNum, applicantType, reason, dateFrom, dateTo, baseCC, baseCP, days, new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {
								failure.accept(caught);
							}

							@Override
							public void onSuccess(Boolean result) {
								impl.setComunicationIT(itEmployee, it, new AsyncCallback<Void>() {

									@Override
									public void onFailure(Throwable caught) {
										failure.accept(caught);
									}

									@Override
									public void onSuccess(Void res) {
										success.accept(null);
									}
									
								});
							}});
					}
					
					@Override
					public void onFailure(Throwable caught) {
						failure.accept(caught);
					}
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
						Date startDateIT = it.getStartDate();
						
						impl.deleteComunicateIT(affiliationNumber, regime, contributionAccount, dateFrom, dateTo, startDateIT, new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								failure.accept(caught);
							}

							@Override
							public void onSuccess(Void result) {
								success.accept(result);
							}});
					}
					
					@Override
					public void onFailure(Throwable caught) {
						failure.accept(caught);
					}
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
					public void onFailure(Throwable caught) {
						failure.accept(caught);
					}
				});
	}
	
	// -------------------------------------------- IT (Auxiliar Methods)
	
	private void initEmployeeList(List<ITEmployee> employeesInfoList) {
		employeesList.clear();
		employeesList.addAll(employeesInfoList);
	}
	
	private void initITList() {
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
		for(IT it : itsList)
			if(DateUtils.isAfterOrEquals(itDate, it.getStartDate()) && (null == it.getEndDate() || DateUtils.isBeforeOrEquals(itDate, it.getEndDate())))
				return it;
		
		return null;
	}
	
	public List<ITEmployee> getActiveEmployeesList(){
		List<ITEmployee> activeEmployeeList = new ArrayList<>();
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
			return "Cesi\u00F3n/Opci\u00F3n en favor del otro progenitor";
		case (byte)3:
			return "Parto m\u00FAltiple";
		case (byte)4:
			return "Inicio del descanso antes del parto (solo para madre biol\u00F3gica ET)";
		default:
			return "Adopci\u00F3n/Tutela/Acogimiento";
		}
	}
	
	public String checkIPFType(String ipf) {
		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");
		return dniPattern.test(ipf.toUpperCase()) ? "NIF" : "NIE";
	}
	
	private String getContractType(String contractType) {
		Integer contractTypeInt = Integer.parseInt(contractType);
		return (contractTypeInt >= 200 && contractTypeInt < 300) || (contractTypeInt >= 500 && contractTypeInt< 600 || contractTypeInt == 0) ? "FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL" : "RESTO_Y_AUTONOMOS";
	}

	private String getLicenseNumber(IT it) {
		for( ITPart itPart : it.getITParts())
			if(itPart.getType() == (byte)0)
				return itPart.getCollegeNumber();
		
		return "";
	}
	
	private String getCias(IT it) {
		for( ITPart itPart : it.getITParts())
			if(itPart.getType() == (byte)0)
				return itPart.getCias();
		
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
	
	// -------------------------------------------- Calendar
	
	public Date getContractStartDate() {
		return this.startDate;
	}
	
	public Date getContractEndDate() {
		return this.endDate;
	}
	
	public boolean isFullTime() {
		return this.employeeCalendarInfo.isFullTime();
	}
	
	public boolean isAgrarian() {
		return this.employeeCalendarInfo.isAgrarian();
	}
	
	public DayType getDayTypeByDate(Date date) {
		DayType dayType = employeeCalendarInfo.getCalendarDaysType().getDayTypeByDate(date);
		return null == dayType ? DayType.NOTYPEDAY : dayType;
	}
	
	public boolean isPartialityDayTypeByDate(Date date) {
		DayType dayType = employeeCalendarInfo.getPartialityDaysType().getDayTypeByDate(date);
		return null != dayType && DayType.NOTYPEDAY != dayType;
	}
	
	public String getPartialityCoefficientByDate(Date date) {
		String coefficient = employeeCalendarInfo.getPartialityDaysType().getExpressionByDate(date);
		return null == coefficient ? "Revisar Parcialiad" : coefficient;
	}
	
	public boolean isDefaultNonWorkongDay(Date date) {
		try {
			Integer day = Integer.parseInt(dayOfWeek.format(date));
			return employeeCalendarInfo.getCalendarHours().isEmpty() && null != employeeCalendarInfo.getWorkingDays()[day] && employeeCalendarInfo.getWorkingDays()[day] == (byte)1;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public String getExpressionByDate(Date date) {
		return employeeCalendarInfo.getCalendarDaysType().getExpressionByDate(date);
	}
	
	public Double getHourByDate(Date date) {
		return employeeCalendarInfo.getCalendarHours().getHourByDate(date);
	}
	
	public Double getExtraHourByDate(Date date) {
		return employeeCalendarInfo.getCalendarExtraHours().getExtraHourByDate(date);
	}
	
	public Double getComplementaryHourByDate(Date date) {
		return employeeCalendarInfo.getCalendarComplementaryHours().get(date);
	}
	
	public boolean isCalendarHourIsEmpty() {
		return employeeCalendarInfo.getCalendarHours().isEmpty();
	}
	
	public boolean isExtraHourIsEmpty() {
		return employeeCalendarInfo.getCalendarExtraHours().getExtraHours().isEmpty();
	}
	
	public boolean isComplementaryHourIsEmpty() {
		return employeeCalendarInfo.getCalendarComplementaryHours().isEmpty();
	}
	
	public Byte[] getWorkingDays() {
		return this.employeeCalendarInfo.getWorkingDays();
	}

	public void setWorkingDays(Byte[] workingDays) {
		this.employeeCalendarInfo.setWorkingDays(workingDays);
	}
		
	public boolean isDefaultFreeDay(Date date) {
		String freedayDescription = this.employeeCalendarInfo.getFestiveDays().get(date);
		return AonStringUtils.isNotBlank(freedayDescription);
	}
	
	// -------------------------------------------- Calendar Days Type
	
	public void addDayType(Date startDate, Date endDate, DayType dayType, String expression) {
		CalendarDayType newCalendarDayType = new CalendarDayType(startDate, endDate, dayType, expression);
		this.employeeCalendarInfo.getCalendarDaysType().addDayType(newCalendarDayType);
		this.employeeCalendarInfo.getCalendarDaysType().initMapDaysDayType();
	}
	
	public void addPartialityDayType(Date startDate, Date endDate, DayType dayType, String expression) {
		CalendarDayType newCalendarDayType = new CalendarDayType(startDate, endDate, dayType, expression);
		this.employeeCalendarInfo.getPartialityDaysType().addDayType(newCalendarDayType);
		this.employeeCalendarInfo.getPartialityDaysType().initMapDaysDayType();
	}
	
	// -------------------------------------------- Calendar Days Hour
	
	public void addDayHour(Date startDate, Date endDate, int day, Double expression) {
		DayHour dayHour = new DayHour(startDate, endDate, expression);
		this.employeeCalendarInfo.getCalendarHours().getDayHours()[day].addDayHour(dayHour);
		this.employeeCalendarInfo.getCalendarHours().initMapDaysHour();
	}
	
	public void addExtraHour(Date startDate, Date endDate, Double expression) {
		DayHourExtra dayHourExtra = new DayHourExtra(startDate, endDate, expression);
		this.employeeCalendarInfo.getCalendarExtraHours().addExtraHour(dayHourExtra);
		this.employeeCalendarInfo.getCalendarExtraHours().initExtraHoursMap();
	}
	
	public void addComplementaryHour(Date date, Double expression) {
		this.employeeCalendarInfo.getCalendarComplementaryHours().put(date, expression);
	}
	
	public void removeComplementaryHour(Date date) {
		this.employeeCalendarInfo.getCalendarComplementaryHours().remove(date);
	}

	// -------------------------------------------- Leyend
	
	public Integer getTotalYearDays(Integer year, DayType dayType) {
		Integer countDays = 0;
		
		Date startDateTotal = DateUtils.getFirstDayOfYear(year - 1900);
		Date endDateTotal = DateUtils.getLastDayOfYear(startDateTotal);
		Date iteratorDate = DateUtils.copyDateOnly(startDateTotal);
		
		while (DateUtils.isBeforeOrEquals(iteratorDate, endDateTotal)) {
			DayType iteratorDayType = employeeCalendarInfo.getCalendarDaysType().getDayTypeByDate(iteratorDate);
			
			if(null != iteratorDayType && iteratorDayType == dayType)
				countDays++;
			
			DateUtils.addDays2Date(iteratorDate, 1);
		}
		
		return countDays;
	}
}
