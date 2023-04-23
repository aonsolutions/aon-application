package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus.ItNotExist;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EnterpriseITObject {
	
	// --------------------------------------------------- Variables
	
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private List<ITEmployee> employeesList;
	private List<IT> itsList;
	
	private DomainUserRoles userRoles;
	
	// --------------------------------------------------- Constructor
	
	public EnterpriseITObject() {
		super();
		this.employeesList = new ArrayList<ITEmployee>();
		this.itsList = new ArrayList<IT>();
		this.userRoles = new DomainUserRoles();
	}
	
	// --------------------------------------------------- DataBase Methods
	
	public void getEmployeesInfo(Boolean allEmployees, Consumer<List<ITEmployee>> success, Consumer<Throwable> failure){
		
		impl.getEmployeesITInfo(allEmployees, new AsyncCallback<List<ITEmployee>>() {
			
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
	
	public void checkStatus(Consumer<EnterpriseITStatus> success, Consumer<Throwable> failure) {
		impl.getEnterpriseITStatus(new AsyncCallback<EnterpriseITStatus>() {
			@Override
			public void onFailure(Throwable caught) {
				failure.accept( caught );
			}
			
			 @Override
			public void onSuccess(EnterpriseITStatus result) {
				 success.accept(result);
			}
		});
	}
	
	public void removeIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
		deleteIT(it, success::accept, failure::accept);
	}
	
	// --------------------------------------------------- DataBase Comunic@ Methods
	
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
	
	public void comunicateITConfirmation(ITEmployee itEmployee, IT it, ITPart confirmationPart, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.getNafxIpf(itEmployee.getEmployeeInfo().getDocument(), itEmployee.getEmployeeInfo().getSurName(), 
				itEmployee.getEmployeeInfo().getSecondSurName(), new AsyncCallback<EmployeeSegSocial>() {
					
					@Override
					public void onSuccess(EmployeeSegSocial result) {
						String naf = result.getNss();
						String regime = itEmployee.getContractInfo().getCompleteCCC().substring(0, 4);
						String ccc = itEmployee.getContractInfo().getCompleteCCC().substring(4, itEmployee.getContractInfo().getCompleteCCC().length());
							
						impl.registerITConfirmation(
								regime, 
								ccc, 
								naf, 
								getContingency(it.getTypeLowPart()), 
								"ACTIVO", 
								confirmationPart.getCollegeNumber(), 
								confirmationPart.getCias(), 
								confirmationPart.getDate(), 
								confirmationPart.getDate(), 
								null, new AsyncCallback<Void>() {

									@Override
									public void onFailure(Throwable caught) {}

									@Override
									public void onSuccess(Void result) {
										success.accept(result);
									}
								});
					}

					@Override
					public void onFailure(Throwable caught) {}
				});
	}
	
	public void comunicateITAlta(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.getNafxIpf(itEmployee.getEmployeeInfo().getDocument(), itEmployee.getEmployeeInfo().getSurName(), 
				itEmployee.getEmployeeInfo().getSecondSurName(), new AsyncCallback<EmployeeSegSocial>() {
					
					@Override
					public void onSuccess(EmployeeSegSocial result) {
						String naf = result.getNss();
						String regime = itEmployee.getContractInfo().getCompleteCCC().substring(0, 4);
						String ccc = itEmployee.getContractInfo().getCompleteCCC().substring(4, itEmployee.getContractInfo().getCompleteCCC().length());
						
						impl.registerITAlta(
								regime, 
								ccc, 
								naf, 
								getContingency(it.getTypeLowPart()), 
								"ACTIVO", 
								getLicenseNumberAlta(it), 
								getCiasAlta(it), 
								it.getStartDate(), 
								it.getEndDate(), 
								null, 
								null, 
								getCauseType(it.getTypeHighPart()), 
								new AsyncCallback<Void>() {
									
									@Override
									public void onSuccess(Void result) {
										impl.setComunicationIT(itEmployee, it, new AsyncCallback<Void>() {

											@Override
											public void onFailure(Throwable caught) {}

											@Override
											public void onSuccess(Void res) {
												success.accept(result);
											}
											
										});
									}
									
									@Override
									public void onFailure(Throwable caught) {}
								});
						
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
	
	public void syncITs(Consumer<Void> success, Consumer<Throwable> failure) {
		impl.syncITs(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}}
		);
	}
	
	public void communicateITPart(ITEmployee itEmployee, IT it, ITPart part, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.communicateITPart(itEmployee, it, part, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}}
		);
	}

	public void saveITParts(List<ItNotExist> itNotExist, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.saveITParts(itNotExist, new AsyncCallback<Void>() {

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

	public void removeITParts(List<ItNotExist> itNotExist, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.removeITParts(itNotExist, new AsyncCallback<Void>() {

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
	
	// --------------------------------------------------- DataBase Auxiliar Methods
	
	public boolean isUserComunica() {
		try {
			return this.userRoles.isComunica();
		} catch (NullPointerException e) {}
		return false;
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
	
	// --------------------------------------------------- FromJS Methods (msjFIEFormPanel)

	public void setEmployeesInfo(List<ITEmployee> employeesInfoList, Consumer<List<ITEmployee>> success, Consumer<Throwable> failure) {
		initEmployeeList(employeesInfoList);
		initITList(employeesInfoList);
		success.accept(employeesInfoList);	
	}
	
	// --------------------------------------------------- MainContrataITObject.Methods
	
	private void initEmployeeList(List<ITEmployee> employeesInfoList) {
		employeesList.clear();
		employeesList.addAll(employeesInfoList);
	}
	
	private void initITList(List<ITEmployee> employeesInfoList) {
		itsList.clear();

		for(ITEmployee employee : employeesInfoList)
			for(IT it : employee.getIts())
				itsList.add(it);
		
		itsList.sort((IT it1, IT it2) -> it1.getStartDate().compareTo(it2.getStartDate()) );
		
		Collections.reverse(itsList);
	}
	
	public List<ITEmployee> getEmployeesList(boolean contractsWithIT, Date startDate, Date endDate){
		if(!contractsWithIT) return employeesList;
		
		List<ITEmployee> employeeWithITList = new ArrayList<ITEmployee>();
		
		for(ITEmployee itEmployee : employeesList)
			for (IT it : itEmployee.getIts()) {
				if(employeeWithITList.contains(itEmployee))
					continue;
				if(null == it.getEndDate() || isBetween(it.getStartDate(), startDate, endDate) || isBetween(it.getEndDate(), startDate, endDate))
					employeeWithITList.add(itEmployee);
			}
		
		return employeeWithITList;
	}
	
	private boolean isBetween(Date date, Date start, Date end) {
		return DateUtils.isAfterOrEquals(date, start) && DateUtils.isBeforeOrEquals(date, end);
	}

	public List<IT> getITsList(){
		return itsList;
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

	public SortedSet<Integer> getAviableYears() {
		SortedSet<Integer> years = new TreeSet<Integer>().descendingSet();
		
		for(ITEmployee itEmployee : this.employeesList)
			years.add(DateUtils.getYear(itEmployee.getContractInfo().getStartDate()));
		
		// Iterator year
		Integer iteratorYear = years.first();
		// Current year
		int currentYear = DateUtils.getYear();
		
		while(currentYear >= iteratorYear) {
			years.add(iteratorYear);
			iteratorYear++;
		}
		
		return years;
	}

	public IT getITs(int itId) {
		for(IT it : itsList)
			if(AonNumberUtils.equals(it.getId(), itId))
				return it;
			
		return null;
	}

	public ITEmployee getITEmployee(int contractId) {
		for(ITEmployee itEmployee : employeesList)
			if(AonNumberUtils.equals(itEmployee.getContractInfo().getContractId(), contractId))
				return itEmployee;
		
		return null;
	}
		
}
