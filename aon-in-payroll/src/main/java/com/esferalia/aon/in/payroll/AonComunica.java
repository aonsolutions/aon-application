package com.esferalia.aon.in.payroll;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.esferalia.aon.in.payroll.utils.EmployeeParse;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.EmployeeITPart;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.payroll.ContractAttach;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.ContractAttachType;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailStatus;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDischargeCause;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;

import solutions.aon.seg.social.ServicioRED;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.ITPart;
import solutions.aon.seg.social.object.It;
import solutions.aon.seg.social.object.SituationType;


public class AonComunica {

	private AonComunica() {
	    throw new IllegalStateException("Utility class");
	}
	
	// ADD CONTRACT AND SEND TGSS
	public static Employee addContract(final byte certificateData[], final String certificatePassword,
			final String certificateType, Domain domain, Employee employee, Boolean communicateTGSS) throws Exception {
			
		   Optional<Employee> exist = contractExist(domain, employee.getCcc(), employee.getNaf(), employee.getStartDate(), employee.getEndDate());
		   if(exist.isEmpty()) 
			   addContract(domain, employee);
		   else
			   employee.setEmployeeId(exist.get().getEmployeeId());
		   
		   System.out.println("CONTRACT ID "+ employee.getEmployeeId());
		   if(Boolean.TRUE.equals(communicateTGSS)) 
			   communicateAlta(certificateData, certificatePassword, certificateType, domain, employee);
		  
		  return employee;
	}
	
	/**
	 * deleteContract AON and TGSS
	 * @param certificateData
	 * @param certificatePassword
	 * @param certificateType
	 * @param domain
	 * @param employee( startDate, naf, ccc, regimen, endDate(Optional) )
	 * @param communicateTGSS true communicate (TGSS, AON) or false (AON)
	 * @throws SegSocialException
	 */
	public static void deleteContract(final byte certificateData[], final String certificatePassword,
			final String certificateType, Domain domain, Employee employee, Boolean communicateTGSS) throws SegSocialException {
		
		SituationType situationType = SituationType.ALTA;
		Date date = employee.getStartDate();
		
		// is BAJA
		if(!employee.getEndDate().isEmpty()) {
			 situationType = SituationType.BAJA;
			 date = employee.getEndDate().get();
		}
	
		//---------------DELETE CONTRACT AON
		Optional<Employee> exist = contractExist(domain, employee.getCcc(), employee.getNaf(), employee.getStartDate(), employee.getEndDate());
		if(!exist.isEmpty())  {
			System.out.println("--------DELETE CONTRACT ID: "+ exist.get().getEmployeeId());
			PAYROLL.deleteContracts(domain, "", exist.get().getEmployeeId());
		}
		
		 if(Boolean.TRUE.equals(communicateTGSS)) {
			//---------DELETE CONTRACT TGSS
			if(parseDate(employee.getStartDate()).compareTo(parseDate(new Date())) > 0 ) {
				System.out.println("DELETE MOV PREV TGSS");
				SistemaRED.movPrevDelete(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, 
						situationType, employee.getRegime(), employee.getCcc(), employee.getNaf(), date);
			} else {
				System.out.println("DELETE MOV CONSOLIDATED TGSS");
				SistemaRED.removeMovConsolidated(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, 
						situationType, employee.getRegime(),  employee.getCcc(), employee.getNaf(), employee.getDni(), date);
			}
		 }
	}
	
	/**
	 * 
	 * @param domain
	 * @param employee (ccc, naf, startDate, endDate(Optional))
	 * @return employee( contract exist)
	 */
	public static Optional<Employee> contractExist(Domain domain, String ccc, String nss, Date startDate, Optional<Date>endDate) {
		Optional<Employee> exist = PAYROLL.getEmployee(domain.getName(), domain.getId(), "", 
				f->f.getDomainProperty().eq(domain.getId())
				.and(f.getCCCProperty().eq(ccc))
				.and(f.getNafProperty().eq(nss))
				.and( 
						endDate.isPresent() ?
						f.getStartDateProperty().eq( new java.sql.Date(startDate.getTime()) ).and(f.getEndDateProperty().eq( new java.sql.Date(endDate.get().getTime()) ) )  :
						f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(new java.sql.Date(startDate.getTime()))) 
				)
			
		);
		if (!exist.isEmpty()) 
			System.out.println("--------EXISTING CONTRACT ID "+exist.get().getEmployeeId()+"--------");
		return exist;
	}
		
	/**
	 * communicate MOV TGSS 
	 * @param certificateData
	 * @param certificatePassword
	 * @param certificateType
	 * @param domain
	 * @param employee
	 * @throws SegSocialException
	 * @throws Exception
	 */
	public static void communicateAlta(final byte certificateData[], final String certificatePassword,
			final String certificateType, Domain domain, Employee employee) throws SegSocialException, Exception {
		
		System.out.println("--------PROCESSING ALTA--------");
		
		SistemaRED.sendAlta(certificateData, certificatePassword, certificateType, EmployeeParse.toEmployeeSS(employee));
		
		employee.setInfo("SS_ALTA", "COMUNICADO");
		
		System.out.println("--------ALTA PROCESSED--------");

		saveContractAttach(certificateData, certificatePassword, certificateType, domain, employee);
	}
	
	public static Employee addContract(Domain domain, Employee employee) {
		System.out.println("--------PROCESSING CONTRACT--------");
		employee.addInfo("SEPE_CONTRATO", "PENDING", employee.getStartDate(), null);
		employee.addInfo("SS_ALTA", "PENDING", employee.getStartDate(), null);
		employee = PAYROLL.addEmployee(domain.getName(), domain.getId(), "", employee);
		return employee;
	}
	
	//SINCRONIZAR ITS
	
	public static void syncUpITs(final byte certificateData[], final String certificatePassword,
			final String certificateType, Domain domain, Optional<String> nss) throws IllegalArgumentException {
		
		    List<CCCInfo> cccs = PAYROLL.getCCCStream(domain.getName(), domain.getId(), "")
		    		.filter(distinctByKey(CCCInfo::getCccAccount))
		    		.collect(Collectors.toList());
			
			for (CCCInfo cti : cccs) {
				try {
					String regime = cti.getCccRegimeCode();
					String ccc = cti.getCccAccount();
					
					Date startDate =  AonDateUtils.addYears(new Date(), -1);
					Date endDate = new Date();
					
					List<EmployeeIT> employeeITs= new ArrayList<>();
			
					Collection<It> its = SistemaRED.getIts(certificateData, certificatePassword, certificateType, 
							regime, ccc, startDate, endDate, nss
					);
					
					for (It it : its) {
						ITPart startIT = it.getStart();
						ITPart endIT = null!=it.getEnd() ?  it.getEnd() : new ITPart();
						
						String naf = null;
						if(nss.isPresent())
							naf = nss.get();
						else if(startIT.getNaf().isPresent())
							naf = startIT.getNaf().get();
				
						Date startDateIT = startIT.getWorkLeaveDate().get();
						Optional<Date> endDateIT = endIT.getWorkRestartDate();
						
						Optional<Employee> contract = AonComunica.contractIts(domain, ccc, naf, startDateIT, endDateIT);
			
						if(contract.isPresent()) {
	
							Integer contractId = contract.get().getEmployeeId();
					
							EmployeeIT employeeIT = new EmployeeIT()
							.setContract(contractId)
							.setDomain(domain.getId())
							.setType(ContractLeaveType.valueOfTGSS(startIT.getCauseNumber()))
							.setStartDate(startDateIT)
							;	
	
							
							ContractLeaveDetailStatus status = ContractLeaveDetailStatus.PROCESSED;
							
							endDateIT.ifPresent(employeeIT::setEndDate);
							
							
							{ //-------------ADD ALTA, BAJA
								// ---------ADD BAJA
								employeeIT.addITPart(
										buildPartIt(ContractLeaveDetailType.BAJA, status, employeeIT.getStartDate(), startIT.getCollegiateNumber(), startIT.getCias())
								);
								// --------ADD ALTA
								if(employeeIT.getEndDate().isPresent()) 
									employeeIT.addITPart(
											buildPartIt(ContractLeaveDetailType.ALTA, status, employeeIT.getEndDate().get(), endIT.getCollegiateNumber(), endIT.getCias() )
									); 
							}
						
							if(endIT.getCauseRestart().isPresent()) 
								employeeIT.setDischargeCause(ContractLeaveDischargeCause.safeValueOf(endIT.getCauseNumber()-1));
							
							 it.getConfirmations().forEach(c->{
								 EmployeeITPart itPart = new EmployeeITPart()
								 .setType(ContractLeaveDetailType.CONFIRMACION)
								 .setStatus(status);
								 
								c.getConfirmationDate().ifPresent(itPart::setDate);
								
								c.getCollegiateNumber().ifPresent(itPart::setCollegeNumber);
								
								c.getCias().ifPresent(itPart::setCias);
								
								c.getPartNum().ifPresent(part-> itPart.setConfirmOrder(part.byteValue()));
								
								employeeIT.addITPart(itPart);
							 });
	
							 employeeITs.add(employeeIT);
						} else {
							System.out.println("no exists contract");
						}
					}
				
					AON.setEmployeeIT(domain, new User(), employeeITs.toArray(EmployeeIT[]::new) );
				} catch (Exception e) {
					e.printStackTrace();
					throw new IllegalArgumentException(e);
				}
			}
	
	}
	
	private static void saveContractAttach(final byte certificateData[], final String certificatePassword,
			final String certificateType, Domain domain, Employee emp) {
			new Thread(() -> {
				saveTA(certificateData, certificatePassword, certificateType, domain, emp);
				saveIDC(certificateData, certificatePassword, certificateType, domain, emp);
			})
			.start();
	}
	
	// employee.getEmployeeId(), employee.getCcc(), employee.getRegime(), employee.getNaf(), employee.getStartDate(), employee.getEndDate() (Optional) 
	private static void saveTA(final byte certificateData[], final String certificatePassword,
			final String certificateType, Domain domain, Employee employee) {
		try {
			System.out.println("--------PROCESSING TA---------");
			Date date = employee.getEndDate().isEmpty() ? employee.getStartDate() : employee.getEndDate().get();
			byte[] fileByte = ServicioRED.getTADuplicatePOST(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, 
					employee.getCcc(), employee.getRegime(), SituationType.ALTA, employee.getNaf(), date);

			ContractAttach attach = new ContractAttach()
			.setDomain(domain.getId())
			.setContract(employee.getEmployeeId())
			.setMimeType(MimeType.PDF)
			.setDescription("TGSS - TA")
			.setData(fileByte)
			.setAttachDate(date)
			.setType(ContractAttachType.TA);
			PAYROLL.saveContractAttach(domain.getName(), domain.getId(), "", attach);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void saveIDC(final byte certificateData[], final String certificatePassword,
			final String certificateType, Domain domain, Employee employee) {
		
		if(parseDate(employee.getStartDate()).compareTo(parseDate(new Date())) <= 0 ) {
			try {
				System.out.println("--------PROCESSING IDC--------");
				byte[] fileByte = ServicioRED.getIDCPOST(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, 
						employee.getNaf(), employee.getRegime(), employee.getCcc(), employee.getStartDate());
				ContractAttach attach = new ContractAttach()
				.setDomain(domain.getId())
				.setContract(employee.getEmployeeId())
				.setMimeType(MimeType.PDF)
				.setDescription("TGSS - IDC")
				.setData(fileByte)
				.setAttachDate(employee.getStartDate())
				.setType(ContractAttachType.IDC);
				PAYROLL.saveContractAttach(domain.getName(), domain.getId(), "", attach);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("--------IDC NOT PROCESSED DATE > NOW--------");
		}
	}
	
	private static Date parseDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	private static EmployeeITPart buildPartIt(ContractLeaveDetailType type, ContractLeaveDetailStatus status, Date date,
			Optional<String> collegiateNumber, Optional<String> cias) {
		 EmployeeITPart itPart = new EmployeeITPart()
		 .setType(type)
		 .setStatus(status)
		 .setDate(date);
		 collegiateNumber.ifPresent(itPart::setCollegeNumber);
		 
		 cias.ifPresent(itPart::setCias);
				 
		return itPart;
	}
	
	private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> uniqueMap = new ConcurrentHashMap<>();
		return t -> uniqueMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	/**
	 * 
	 * @param domain
	 * @param employee (ccc, naf, startDate, endDate(Optional))
	 * @return employee( contract exist)
	 */
	private static Optional<Employee> contractIts(Domain domain, String ccc, String nss, Date startDate, Optional<Date>endDate) {
		return PAYROLL.getEmployee(domain.getName(), domain.getId(), "", 
				f->f.getDomainProperty().eq(domain.getId())
				.and(f.getCCCProperty().eq(ccc))
				.and(f.getNafProperty().eq(nss))
				.and( 
					endDate.isPresent() ?
					f.getStartDateProperty().le( new java.sql.Date(startDate.getTime())).and(
							f.getEndDateProperty().ge( new java.sql.Date(endDate.get().getTime())).or(f.getEndDateProperty().isNull())
					) :
					f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(new java.sql.Date(startDate.getTime()))) 
				)
			
		);
	}

}
