package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainContrataITObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private List<ITEmployee> allEmployeesList;
	private List<ITEmployee> employeesList;
	private List<IT> allITsList;
	private List<IT> itsList;
	
	private Map<String, Integer> employeesFilterMap;
	private Map<String, Integer> itsFilterMap;
	
	private DomainUserRoles userRoles;
	
	public MainContrataITObject() {
		super();
		this.allEmployeesList = new ArrayList<ITEmployee>();
		this.employeesList = new ArrayList<ITEmployee>();
		this.employeesFilterMap = new HashMap<String, Integer>();
		this.allITsList = new ArrayList<IT>();
		this.itsList = new ArrayList<IT>();
		this.itsFilterMap = new HashMap<String, Integer>();
		this.userRoles = new DomainUserRoles();
	}
	
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
	
	public void getEmployeesInfo(Integer itIds [], Consumer<List<ITEmployee>> success, Consumer<Throwable> failure){
		
		impl.getEmployeesITInfo(itIds, new AsyncCallback<List<ITEmployee>>() {
			
			@Override
			public void onSuccess(List<ITEmployee> employeesInfoList) {
				initEmployeeList(employeesInfoList);
				initITList(employeesInfoList);
				success.accept(employeesInfoList);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}

	public void deleteIT(IT it, Consumer<String> success, Consumer<Throwable> failure) {
		impl.deleteIT(it.getId(), new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String message) {	
				success.accept(message);	
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
	
	public void comunicatePaternityIT(ITEmployee itEmployee, IT it, Consumer<Boolean> success, Consumer<Throwable> failure) {
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
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onSuccess(Boolean result) {
								impl.setComunicationIT(itEmployee, it, new AsyncCallback<Void>() {

									@Override
									public void onFailure(Throwable caught) {}

									@Override
									public void onSuccess(Void res) {
										success.accept(result);
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
	
	public void removeIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
		deleteIT(it, s -> {
			if(isUserComunica() && it.isComunicate())
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
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onSuccess(Void result) {
								success.accept(result);
							}});
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

	public void setEmployeesInfo(List<ITEmployee> employeesInfoList, Consumer<List<ITEmployee>> success, Consumer<Throwable> failure) {
		initEmployeeList(employeesInfoList);
		initITList(employeesInfoList);
		success.accept(employeesInfoList);	
	}
	
	private void initEmployeeList(List<ITEmployee> employeesInfoList) {
		allEmployeesList.clear();
		employeesList.clear();
		allEmployeesList.addAll(employeesInfoList);
		employeesList.addAll(employeesInfoList);
		
		employeesFilterMap.clear();
		// Init map
		for(ITEmployee employee : allEmployeesList) {
			String fullName = employee.getEmployeeInfo().getFullName();
			String document = employee.getEmployeeInfo().getDocument();
			String ssNumber = employee.getEmployeeInfo().getSsNumber();
			Integer contractId = employee.getContractInfo().getContractId();
			employeesFilterMap.put(fullName + ", Documento : " + document + ", SS : " + ssNumber, contractId);
		}
	}
	
	private void initITList(List<ITEmployee> employeesInfoList) {
		allITsList.clear();
		itsList.clear();
		
		Date currentDate = new Date();
		
		itsFilterMap.clear();
		// Init map
		for(ITEmployee employee : allEmployeesList) {
			String fullName = employee.getEmployeeInfo().getFullName();
			for(IT it : employee.getIts()) {
				it.setFullName(fullName);
				
				allITsList.add(it);
				if(it.getEndDate() == null || it.getEndDate().after(currentDate)) {
					itsList.add(it);
					String description = fullName + (AonStringUtils.isBlank(it.getDescription()) ? "" : " " + it.getDescription())
							+ " " + parseShortLowCauseByte(it.getTypeLowPart()) + " (" + formatFullDate.format(it.getStartDate()) + ")" ;
					itsFilterMap.put(description, it.getId());
				}
			}
		}
	}
	
	public List<ITEmployee> getEmployeesList(){
		return employeesList;
	}
	
	public Map<String, Integer> getEmployeesMap(){
		return employeesFilterMap;
	}

	public void resetEmployeesList() {
		this.employeesList.clear();
		this.employeesList.addAll(allEmployeesList);
	}
	
	public List<IT> getITsList(){
		return itsList;
	}
	
	public Map<String, Integer> getITsMap(){
		return itsFilterMap;
	}

	public void resetITsList() {
		this.itsList.clear();
		
		// Init map
		for(IT it : this.allITsList) {
			this.itsList.add(it);
		}
	}

	public List<Integer> getEmployeesContractIds(String value) {
		List<Integer> contractIds = new ArrayList<Integer>();
		
		for(Entry<String, Integer> entry : employeesFilterMap.entrySet()) {
			if(AonStringUtils.containsIgnoreCase(entry.getKey(), value) ||
					AonStringUtils.contains(entry.getKey(), value) ||
					AonStringUtils.equals(entry.getKey(), value) ||
					AonStringUtils.equalsIgnoreCase(entry.getKey(), value)) {
				
				contractIds.add(entry.getValue());
			}
		}
		
		return contractIds;
	}

	public void filterEmployeesList(List<Integer> employeesContractIds) {
		employeesList.clear();
		
		for(ITEmployee employee : allEmployeesList) {
			if(employeesContractIds.contains(employee.getContractInfo().getContractId()))
				employeesList.add(employee);
		}
	}
	
	public List<Integer> getITsContractIds(String value) {
		List<Integer> itIds = new ArrayList<Integer>();
		
		for(Entry<String, Integer> entry : itsFilterMap.entrySet()) {
			if(AonStringUtils.containsIgnoreCase(entry.getKey(), value) ||
					AonStringUtils.contains(entry.getKey(), value) ||
					AonStringUtils.equals(entry.getKey(), value) ||
					AonStringUtils.equalsIgnoreCase(entry.getKey(), value)) {
				
				itIds.add(entry.getValue());
			}
		}
		
		return itIds;
	}

	public void filterITsList(List<Integer> itsContractIds) {
		itsList.clear();
		
		for(IT it : allITsList) {
			if(itsContractIds.contains(it.getId()))
				itsList.add(it);
		}
	}

	public ITEmployee getEmployeeITInfo(Integer itId) {
		for(ITEmployee itEmployee : this.employeesList) {
			for(IT it : itEmployee.getIts())
				if(it.getId() == itId || it.getId().equals(itId))
					return itEmployee;
		}
		
		return null;
	}

	public void setITsList(Boolean allITs) {
		this.itsList.clear();
		
		if(allITs) {
			// Init map
			for(ITEmployee employee : allEmployeesList) {
				for(IT it : employee.getIts()) {
					itsList.add(it);
					allITsList.add(it);
				}
			}
		} else {
			Date currentDate = new Date();
			
			// Init map
			for(ITEmployee employee : allEmployeesList) {
				for(IT it : employee.getIts()) {
					if(it.getEndDate() == null || it.getEndDate().after(currentDate)) {
						itsList.add(it);
						allITsList.add(it);
					}
					
				}
			}
		}
		
	}
	
	private String parseShortLowCauseByte(Byte typeLowPart) {
		switch (typeLowPart) {
			case (byte)0:
				return "ECC";
			case (byte)1:
				return "ATT";
			case (byte)2:
				return "MAT";
			case (byte)3:
				return "PAT";
			case (byte)4:
				return "REM";
			case (byte)5:
				return "RLA";
			case (byte)6:
				return "ANL";
			case (byte)7:
				return "ECC";
			case (byte)8:
				return "COV";
			default:
				return "-";
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
	
	public boolean isUserComunica() {
		boolean isComunica = false;
		try {
			isComunica = this.userRoles.isComunica();
			return isComunica;
		} catch (NullPointerException e) {
			return isComunica;
		}
	}
		
}
