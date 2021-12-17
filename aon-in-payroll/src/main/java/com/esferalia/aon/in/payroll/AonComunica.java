package com.esferalia.aon.in.payroll;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import com.esferalia.aon.in.payroll.utils.EmployeeParse;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.payroll.ContractAttach;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.type.ContractAttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import solutions.aon.seg.social.ServicioRED;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.SituationType;


public class AonComunica {

	private AonComunica() {
	    throw new IllegalStateException("Utility class");
	}
	
	// ADD CONTRACT AND SEND TGSS
	public static Employee addContract(final byte certificateData[], final String certificatePassword,
			final String certificateType, Domain domain, Employee employee, Boolean communicateTGSS) throws Exception {
			
		   Optional<Employee> exist = contractExist(domain, employee);
		   if(exist.isEmpty()) 
			   addContract(domain, employee);
		   else
			   employee.setEmployeeId(exist.get().getEmployeeId());
		   
		   System.out.println("CONTRACT ID "+ employee.getEmployeeId());
		   if(Boolean.TRUE.equals(communicateTGSS)) 
			   communicateAlta(certificateData, certificatePassword, certificateType, domain, employee);
		  
		  return employee;
	}
	
	//DELETE CONTRACT
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
		Optional<Employee> exist = contractExist(domain, employee);
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
	
	//---CONTRACT EXISTS
	public static Optional<Employee> contractExist(Domain domain, Employee employee) {
		Optional<Employee> exist = PAYROLL.getEmployee(domain.getName(), domain.getId(), "", 
				f->f.getDomainProperty().eq(domain.getId())
				.and(f.getCCCProperty().eq(employee.getCcc()))
				.and(f.getNafProperty().eq(employee.getNaf()))
				.and( 
						employee.getEndDate().isPresent() ?
						f.getStartDateProperty().eq( new java.sql.Date(employee.getStartDate().getTime()) ).and(f.getEndDateProperty().eq( new java.sql.Date(employee.getEndDate().get().getTime()) ) )  :
						f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(new java.sql.Date(employee.getStartDate().getTime()))) 
				)
			
		);
		if (!exist.isEmpty()) 
			System.out.println("--------EXISTING CONTRACT ID "+exist.get().getEmployeeId()+"--------");
		return exist;
	}
	
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
}
