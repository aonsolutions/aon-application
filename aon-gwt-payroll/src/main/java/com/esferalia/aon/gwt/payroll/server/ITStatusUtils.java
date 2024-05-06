package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.occam.api.model.type.ContractLeaveType.MATERNIDAD;
import static com.esferalia.aon.occam.api.model.type.ContractLeaveType.PATERNIDAD;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus.AndEmployeeITStatus;
import com.esferalia.aon.gwt.payroll.shared.OutOfDateException;
import com.esferalia.aon.in.payroll.tgss.its.ITComunica;
import com.esferalia.aon.in.payroll.tgss.its.ITParse;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.EmployeeITPart;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.security.CertificateNotFoundException;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailStatus;
import com.esferalia.aon.occam.api.model.type.Gender;
import com.esferalia.aon.watson.server.AonDateUtils;

import solutions.aon.seg.social.Paternity;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.PaternityCertificate;

public class ITStatusUtils {
	private static Logger logger = Logger.getLogger( ITStatusUtils.class.getName() ); 
	
	private ITStatusUtils() {
	   throw new IllegalStateException("Utility class");
	}
	
	public static EnterpriseITStatus getEnterpriseITStatus(Domain domain, User user) {
		logger.info("getEnterpriseITStatus");
		
		Date startDate = getFirstDateOfMonth(AonDateUtils.addMonths(new Date(), -7));
		Date endDate = new Date();
		try {
			AndEmployeeITStatus employeeITStatus = new AndEmployeeITStatus();
			Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId(), "TGSS");

			List<CCCInfo> cccs = ITComunica.getCccs(domain);
			  
			for ( CCCInfo ccc: cccs ) {
				try {
					Thread threadOne = new Thread(() -> {
						employeeITStatus.and( compareComun(domain, certificate, startDate, endDate, ccc) );
					});
					Thread threadTwo = new Thread(() -> {
						employeeITStatus.and( comparePaternity(domain, user, certificate, startDate, endDate, ccc) );
					});
			
					threadOne.start();
					threadTwo.start();
					threadOne.join();
					threadTwo.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			try {
				EnterpriseITStatus.isUp2Date(employeeITStatus); 
				employeeITStatus.and(new EnterpriseITStatus.Up2Date());
			} catch ( OutOfDateException e ) {}
			
			employeeITStatus.and(new EnterpriseITStatus.onFinish());
			EnterpriseITStatus.trace(employeeITStatus);
			return employeeITStatus;
		} catch (CertificateNotFoundException e) {
			return new EnterpriseITStatus.CredentialsNotFound();
		} catch (Exception e) {
			return new EnterpriseITStatus.UnknownError().setMessage(e.getMessage());
		}
	}
	
	private static AndEmployeeITStatus compareComun(Domain domain, Certificate certificate, Date startDate, Date endDate, CCCInfo ccc) {
		AndEmployeeITStatus employeeITStatus = new AndEmployeeITStatus();
		try {	
			logger.info("compareComun");
			List<EmployeeIT> ssIts = getITFromTGSS(certificate, startDate, endDate, ccc);
			
			Date start = ssIts.isEmpty() ? startDate :  ssIts.get(0).getStartDate();
			
			List<EmployeeIT> aonEmployeesIT = getITFromAon(domain, start, endDate, ccc, false);

			logger.info("------------------NO EXIST EN AON COMUN------------------");
			compareEmployeesITs(aonEmployeesIT, ssIts, employeeITStatus, domain);	//NO EXIST EN AON

			logger.info("------------------NO EXIST EN SS COMUN------------------");
			compareEmployeesITs(ssIts, aonEmployeesIT, employeeITStatus, domain); //NO EXIST EN SS

		} catch (Exception e) {
			e.printStackTrace();
			return new EnterpriseITStatus.unknownErrorAnd().setMessage(e.getMessage());
		}
		return employeeITStatus;
	}
	
	private static AndEmployeeITStatus comparePaternity(Domain domain, User user, Certificate certificate, Date startDate, Date endDate, CCCInfo ccc) {
		AndEmployeeITStatus employeeITStatus = new AndEmployeeITStatus();
		try {
			logger.info("comparePaternity");

			List<EmployeeIT> ssIts = getITFromTGSSPaternity(domain, user, certificate, startDate, endDate, ccc, Optional.empty());
			
			Date start = ssIts.isEmpty() ? startDate :  ssIts.get(0).getStartDate();
			
			List<EmployeeIT> aonEmployeesIT = getITFromAon(domain, start, endDate, ccc, true);
			logger.info("------------------NO EXIST EN AON PATERNITY------------------");
			compareEmployeesITs(aonEmployeesIT, ssIts, employeeITStatus, domain);	//NO EXIST EN AON

			logger.info("------------------NO EXIST EN SS PATERNITY------------------");
			compareEmployeesITs(ssIts, aonEmployeesIT, employeeITStatus, domain); //NO EXIST EN SS
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeeITStatus;
	}
	
	private static List<EmployeeIT> getITFromAon(Domain domain, Date startDate, Date endDate, CCCInfo ccc, boolean paternity) {
		Byte[] types = new Byte[] {MATERNIDAD.value(), PATERNIDAD.value()};

		return AON.getEmployeesIT(domain, new User(), f-> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getStartDateProperty().ge(convertDateSql(startDate)).and(f.getStartDateProperty().le(convertDateSql(endDate))))
			.and(f.getCCCProperty().eq(ccc.getCccAccount()))
			.and(
				paternity ? 
				f.getTypeProperty().in(types) :
				f.getTypeProperty().notIn(types)
			)
		)
		.collect(Collectors.toList());
	} 
	
	//COMUN
	private static List<EmployeeIT> getITFromTGSS(Certificate certificate, Date startDate, Date endDate, CCCInfo ccc) throws SegSocialException {
		List<EmployeeIT> ssIts = new ArrayList<>();

		SistemaRED.getIts(
			new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), 
			ccc.getCccRegimeCode(), ccc.getCccAccount(), startDate, endDate, Optional.empty()
		)
		.forEach(it->{
			ssIts.add(ITParse.parseTGSSToAon(it));
		});

		return ssIts;
	} 
	
	//PATERNITY
	private static List<EmployeeIT> getITFromTGSSPaternity(Domain domain, User user, Certificate certificate, Date startDate, Date endDate, CCCInfo ccc, Optional<String>nssOpt) throws SegSocialException, InterruptedException, IOException {
		List<EmployeeIT> ssIts = new ArrayList<>();

		List<PaternityCertificate> paternitys = Paternity.getPaternitys(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), 
				 ccc.getCccRegimeCode(), ccc.getCccAccount(), startDate, endDate, nssOpt, Optional.empty()
		).stream().distinct().filter(p-> !p.getCanceled()).collect(Collectors.toList());
		
		if(!paternitys.isEmpty()) {
			setEmployeeData(domain, user, paternitys);
			paternitys.stream().filter(p-> p.getWorkerNif().isPresent()) .forEach(paternity->{
				ssIts.add( ITParse.parsePaternityTGSSToAon(paternity).setDomain(domain.getId()) );
			});
		}

		return ssIts;
	} 
	
	/*
	 * FIRST LIST NOT EXIST IN SECOND LIST
	 */
	private static AndEmployeeITStatus compareEmployeesITs(List<EmployeeIT> oneList, List<EmployeeIT>anotherList, 
		AndEmployeeITStatus employeeITStatus, Domain domain) {
		List<EmployeeIT> itsUpdate = new ArrayList<>();

		for (EmployeeIT anotherIt : anotherList) {
			AtomicBoolean change = new AtomicBoolean(false);
			Optional<String> name = anotherIt.getName();
			if(!name.isEmpty() && anotherIt.getStartDate()!=null) {

				List<EmployeeIT> oneIt = oneList.stream()
						.filter(e -> e.getNss().equals(anotherIt.getNss()))
						.peek(e -> System.out.println(e.getNss() + ":" + e.getStartDate() + ", "
								+ anotherIt.getStartDate() + " = " + e.getStartDate().equals(anotherIt.getStartDate())))
						.filter(e -> e.getStartDate().equals(anotherIt.getStartDate()))
						.collect(Collectors.toList());
				logger.info("BAJA NSS:"+anotherIt.getNss()+ " type: "+anotherIt.getType() +" date:"+anotherIt.getStartDate()+" toAon:"+anotherIt.getId());
				if ( oneIt.isEmpty() ) {
					employeeITStatus.and(new EnterpriseITStatus.ItNotExist().setEmployeeIT(anotherIt)
					// .setEmployeeITPart(null);
					);
				}
				

				
				if(change.get())
					itsUpdate.add(anotherIt);
			} //IF NAME
		}

		if(!itsUpdate.isEmpty()) {
			itUpdate(domain, itsUpdate);
			employeeITStatus.and(new EnterpriseITStatus.UpdatedEnterprise());
		}
		return employeeITStatus;
	}
	
	private static void setEmployeeData(Domain domain, User user, List<PaternityCertificate> paternitys) {
		List<PaternityCertificate> list = paternitys.stream()
				.filter(it-> it.getWorkerName().isEmpty() && !it.getWorkerNaf().isEmpty())
				.filter(ITComunica.distinctByKey(p-> p.getWorkerNaf().get())).collect(Collectors.toList());

		if(!list.isEmpty()) {
			String[] nssAll = list.stream().map(p-> p.getWorkerNaf().get()).toArray(String[]::new);
			
			AON.getPersonList(domain.getName(), domain.getId(), user.getLogin(), f->f.getDocumentProperty().isNotNull().and(f.getSocialSecurityNumProperty().in(nssAll)))
			.forEach(person->{
				paternitys
				.stream()
				.filter(p-> p.getWorkerNaf().isPresent() && p.getWorkerNaf().get().equals(person.getSocialSecurityNum()) )
				.forEach(p-> {
					System.out.println(person.getName()+", "+person.getDocument());
					Gender gender = person.getGender();
					p.setWorkerName(person.getName())
					.setWorkerNif(person.getDocument())
					.setIsFather(gender!=null && gender.ordinal()==0 ? true : false);
				});
			});
		}
	}
	
	private static void itUpdate(Domain domain, List<EmployeeIT> employeeITs) {
		try {
			 AON.setEmployeeIT(domain, new User(), employeeITs.toArray(EmployeeIT[]::new));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static java.sql.Date convertDateSql(Date utilDate) {
		return new java.sql.Date(utilDate.getTime());
	}
		
	private static Date getFirstDateOfMonth(Date date){
	     Calendar cal = Calendar.getInstance();
	     cal.setTime(date);
	     cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
	     return cal.getTime();
	 }
	
//	private static Date getDateBefore(Date date1, Date date2) {
//		return date1.compareTo(date2) < 0  ? date1 : date2;
//	}
}
