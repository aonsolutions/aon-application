package com.esferalia.aon.in.payroll;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.payroll.ContractAttach;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.security.Certificate;
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
	
	// ADD CONTRACT AND SEARCH (IDC, TA AND SAVE)
	public static void addContract(Domain domain, Employee employee, Certificate certificate) {
		Optional<Employee> exist = contractExist(domain, employee.getCcc(), employee.getNaf(), employee.getStartDate(), employee.getEndDate());
		
		if(exist.isEmpty()) {
			addContract(domain, employee); 
		} else {
			employee.setEmployeeId(exist.get().getEmployeeId());
		}

		employee.setInfo("SS_ALTA", "COMUNICADO");
		System.out.println("--------SS_ALTA COMUNICADO--------");
		
		saveContractAttach(certificate, domain, employee);
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
	public static void deleteContract(final byte[] certificateData, final String certificatePassword,
			final String certificateType, Domain domain, Employee employee, Boolean communicateTGSS) throws SegSocialException {
		
		SituationType situationType = SituationType.ALTA;
		Date date = employee.getStartDate();
		
		// is BAJA
		if(!employee.getEndDate().isEmpty()) {
			 situationType = SituationType.BAJA;
			 date = employee.getEndDate().get();
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
	
		//---------------DELETE CONTRACT AON
		Optional<Employee> exist = contractExist(domain, employee.getCcc(), employee.getNaf(), employee.getStartDate(), employee.getEndDate());
		if(!exist.isEmpty())  {
			System.out.println("--------DELETE CONTRACT ID: "+ exist.get().getEmployeeId());
			PAYROLL.deleteContracts(domain, "", exist.get().getEmployeeId());
		}
	}
	
	/**
	 * 
	 * @param domain
	 * @param employee (ccc, naf, startDate, endDate(Optional))
	 * @return employee( contract exist)
	 */
	public static Optional<Employee> contractExist(Domain domain, String ccc, String nss, Date startDate, Optional<Date>endDate) {
		System.out.println("NSS:"+nss+" CCC:"+ccc+ " startDate:"+startDate+" endDate:"+endDate);
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
		
		if (!exist.isEmpty()) {
			System.out.println("--------EXISTING CONTRACT ID "+exist.get().getEmployeeId()+"--------");
		}

		return exist;
	}
		
	/**
	 * communicate MOV TGSS 
	 * @param certificateData
	 * @param certificatePassword
	 * @param certificateType
	 * @param domain
	 * @param employee
	 * @return 
	 * @throws SegSocialException
	 * @throws Exception
	 */
	public static byte[] communicateAlta(solutions.aon.seg.social.object.Employee employee, Certificate certificate) throws SegSocialException {
		return SistemaRED.sendAlta(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), employee);
	}
	
	public static Employee addContract(Domain domain, Employee employee) {
		System.out.println("--------PROCESSING CONTRACT--------");
		
		employee.addInfo("SEPE_CONTRATO", "PENDING", employee.getStartDate(), null);
		
		employee.addInfo("SS_ALTA", "PENDING", employee.getStartDate(), null);
		System.out.println("--------SS_ALTA COMUNICADO--------");
		
		employee = PAYROLL.addEmployee(domain.getName(), domain.getId(), "", employee);
		return employee;
	}
	
	private static void saveContractAttach(Certificate certificate, Domain domain, Employee emp) {
		new Thread(() -> {
			saveTA(certificate.getData(), certificate.getPassword(), certificate.getType(), domain, emp);
			saveIDC(certificate.getData(), certificate.getPassword(), certificate.getType(), domain, emp);
		}).start();
	}
	
	// employee.getEmployeeId(), employee.getCcc(), employee.getRegime(), employee.getNaf(), employee.getStartDate(), employee.getEndDate() (Optional) 
	private static void saveTA(final byte[] certificateData, final String certificatePassword,
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
	
	private static void saveIDC(final byte[] certificateData, final String certificatePassword,
			final String certificateType, Domain domain, Employee employee) {
		System.out.println("--------PROCESSING IDC--------");
		try {
			Date startDate = parseDate(employee.getStartDate()).compareTo(parseDate(new Date())) <= 0 ? employee.getStartDate() : new Date();
			
			byte[] fileByte = ServicioRED.getIDCPOST(new ByteArrayInputStream(certificateData), certificatePassword, certificateType, 
					employee.getNaf(), employee.getRegime(), employee.getCcc(), startDate);
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
