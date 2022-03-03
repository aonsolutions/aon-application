package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.occam.api.model.type.ContractLeaveType.MATERNIDAD;
import static com.esferalia.aon.occam.api.model.type.ContractLeaveType.PATERNIDAD;

import java.io.ByteArrayInputStream;
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
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.ContractLeaveProperties;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailStatus;
import com.esferalia.aon.occam.api.model.type.Gender;
import com.esferalia.aon.watson.server.AonDateUtils;

import solutions.aon.seg.social.Paternity;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.object.PaternityCertificate;

public class ITStatusUtils {
	private static Logger logger = Logger.getLogger( ITStatusUtils.class.getName() ); 
	
	private ITStatusUtils() {
	   throw new IllegalStateException("Utility class");
	}
	
	public static EnterpriseITStatus getEnterpriseITStatus(Domain domain, User user) {
		logger.info("getEnterpriseITStatus");
		
		Date startDate = getFirstDateOfMonth(AonDateUtils.addMonths(new Date(), -1));
		Date endDate = new Date();
		
		AndEmployeeITStatus employeeITStatus = new AndEmployeeITStatus();
		
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId(), "TGSS");
		List<CCCInfo> cccs = ITComunica.getCccs(domain);
		  
		for ( CCCInfo ccc: cccs ) {
			
			Thread threadOne = new Thread(() -> {
				employeeITStatus.and( compareComun(domain, certificate, startDate, endDate, ccc) );
			});
			Thread threadTwo = new Thread(() -> {
				employeeITStatus.and( comparePaternity(domain, user, certificate, startDate, endDate, ccc) );
			});
			
			try {
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
	}
	
	private static AndEmployeeITStatus compareComun(Domain domain, Certificate certificate, Date startDate, Date endDate, CCCInfo ccc) {
		AndEmployeeITStatus employeeITStatus = new AndEmployeeITStatus();
		try {	
			logger.info("compareComun");
			List<EmployeeIT> ssIts = getITFromTGSS(certificate, startDate, endDate, ccc);
			
			if(!ssIts.isEmpty()) {
				List<EmployeeIT> aonEmployeesIT = getITFromAon(domain, ssIts.get(0).getStartDate(), endDate, ccc, false);

				logger.info("------------------NO EXIST EN AON COMUN------------------");
				compareEmployeesITs(aonEmployeesIT, ssIts, employeeITStatus, domain);	//NO EXIST EN AON
	
				logger.info("------------------NO EXIST EN SS COMUN------------------");
				compareEmployeesITs(ssIts, aonEmployeesIT, employeeITStatus, domain); //NO EXIST EN SS
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeeITStatus;
	}
	
	private static AndEmployeeITStatus comparePaternity(Domain domain, User user, Certificate certificate, Date startDate, Date endDate, CCCInfo ccc) {
		AndEmployeeITStatus employeeITStatus = new AndEmployeeITStatus();
		try {
			logger.info("comparePaternity");

			List<EmployeeIT> paternitys = getITFromTGSSPaternity(domain, user, certificate, startDate, endDate, ccc, Optional.empty());
			
			if(!paternitys.isEmpty()) {
				List<EmployeeIT> aonEmployeesIT = getITFromAon(domain, paternitys.get(0).getStartDate(), endDate, ccc, true);
				logger.info("------------------NO EXIST EN AON PATERNITY------------------");
				compareEmployeesITs(aonEmployeesIT, paternitys, employeeITStatus, domain);	//NO EXIST EN AON

				logger.info("------------------NO EXIST EN SS PATERNITY------------------");
				compareEmployeesITs(paternitys, aonEmployeesIT, employeeITStatus, domain); //NO EXIST EN SS
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeeITStatus;
	}
	
	private static List<EmployeeIT> getITFromTGSS(Certificate certificate, Date startDate, Date endDate, CCCInfo ccc) {
		List<EmployeeIT> ssIts = new ArrayList<>();
		try {
		
			SistemaRED.getIts(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), 
					ccc.getCccRegimeCode(), ccc.getCccAccount(), startDate, endDate, Optional.empty()).forEach(it-> ssIts.add(ITParse.parseTGSSToAon(it)));
			if(!ssIts.isEmpty() && ssIts.get(0).getStartDate()!=null)
				return ssIts;
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return ssIts;
	} 
	
	private static List<EmployeeIT> getITFromAon(Domain domain, Date startDate, Date endDate, CCCInfo ccc, boolean paternity) {
		return AON.getEmployeesIT(domain, new User(), f-> getFilter(f, domain, startDate, endDate, ccc, paternity))
		.collect(Collectors.toList());
	} 

	//PATERNITY
	public static List<EmployeeIT> getITFromTGSSPaternity(Domain domain, User user, Certificate certificate, Date startDate, Date endDate, CCCInfo ccc, Optional<String>nssOpt) {
		List<EmployeeIT> ssIts = new ArrayList<>();
		try {			

			List<PaternityCertificate> paternitys = Paternity.getPaternitys(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), 
					 ccc.getCccRegimeCode(), ccc.getCccAccount(), startDate, endDate, nssOpt, Optional.empty()
			).stream().distinct().filter(p-> !p.getCanceled()).collect(Collectors.toList());
			
			if(!paternitys.isEmpty()) {
				setEmployeeData(domain, user, paternitys);
				paternitys.forEach(paternity->{
					ssIts.add( ITParse.parsePaternityTGSSToAon(paternity).setDomain(domain.getId()) );
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ssIts;
	} 
	
	/*
	 * FIRST LIST NOT EXIST IN SECOND LIST
	 */
	private static AndEmployeeITStatus compareEmployeesITs(List<EmployeeIT> firstList, List<EmployeeIT>secondList, 
		AndEmployeeITStatus employeeITStatus, Domain domain) {
		List<EmployeeIT> itsUpdate = new ArrayList<>();

		for (EmployeeIT second : secondList) {
			AtomicBoolean change = new AtomicBoolean(false);
			Optional<String> name = second.getName();
			if(!name.isEmpty() && second.getStartDate()!=null) {
				Optional<EmployeeITPart> ssITBaja = second.getItBaja();
				List<EmployeeITPart> ssITConfirmations = second.getItConfirmations();
				Optional<EmployeeITPart> ssITAlta = second.getItAlta();

				List<EmployeeIT> first = firstList.stream().filter(e->e.getNss().equals(second.getNss())).collect(Collectors.toList());
					
				if(!ssITBaja.isEmpty()) {
					Optional<EmployeeIT> exist = first.stream()
					.filter(e-> !e.getItBaja().isEmpty() && e.getItBaja().get().getDate().equals(ssITBaja.get().getDate()))
					.findFirst();
					
					if(!exist.isEmpty()) {
					
						second.setType(exist.get().getType());
						if(ssITBaja.get().getId()!=null && !ssITBaja.get().getStatus().equals(ContractLeaveDetailStatus.PROCESSED)) {
							ssITBaja.get().setStatus(ContractLeaveDetailStatus.PROCESSED);
							change.getAndSet(true);
						}
					} else {
						logger.info("BAJA NSS:"+second.getNss()+ " type: "+second.getType() +" date:"+ssITBaja.get().getDate()+" toAon:"+ssITBaja.get().getId());
						employeeITStatus.and( 
							new EnterpriseITStatus.ItNotExist()
							.setEmployeeIT(second)
							.setEmployeeITPart(ssITBaja.get())
						);
					}
				}

				if(!ssITAlta.isEmpty()) {
					first.stream().map(EmployeeIT::getItAlta)
					.filter(Optional::isPresent)
					.map(Optional::get)
					.filter(e->e.getDate().equals(ssITAlta.get().getDate()))
					.findFirst().ifPresentOrElse(e->{
						if(ssITAlta.get().getId()!=null && !ssITAlta.get().getStatus().equals(ContractLeaveDetailStatus.PROCESSED)) {
							ssITAlta.get().setStatus(ContractLeaveDetailStatus.PROCESSED);
							change.getAndSet(true);
						}
					}, ()->{
						logger.info("ALTA NSS:"+second.getNss()+  " type: "+second.getType() +" date:"+ssITAlta.get().getDate()+" toAon:"+ssITAlta.get().getId());
						employeeITStatus.and( 
							new EnterpriseITStatus.ItNotExist()
							.setEmployeeIT(second)
							.setEmployeeITPart(ssITAlta.get())
						);
					});
				}
				
				if(!ssITConfirmations.isEmpty()) {
					for (EmployeeITPart ssITConfirmation:ssITConfirmations) {
						first.stream()
						.map(EmployeeIT::getItConfirmations)
						.flatMap(Collection::stream)
						.filter(e-> !e.getConfirmOrder().isEmpty() && !ssITConfirmation.getConfirmOrder().isEmpty() ? 
							(
								e.getConfirmOrder().get().equals( ssITConfirmation.getConfirmOrder().get()) && 
								e.getDate().equals(ssITConfirmation.getDate())
							) : 
							e.getDate().equals(ssITConfirmation.getDate())
						)
						.findFirst().ifPresentOrElse(e->{
							if(ssITConfirmation.getId()!=null && !ssITConfirmation.getStatus().equals(ContractLeaveDetailStatus.PROCESSED)) {
								ssITConfirmation.setStatus(ContractLeaveDetailStatus.PROCESSED);
								change.getAndSet(true);
							}
						}, ()->{
							employeeITStatus.and(
								new EnterpriseITStatus.ItNotExist()
								.setEmployeeIT(second)
								.setEmployeeITPart(ssITConfirmation)
							);
						});
					} // FOR CONFIRMATIONS
				} //IF CONFIRMATION NOT EMPTY
				
				if(change.get())
					itsUpdate.add(second);
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
			
			AON.getPersonList(domain.getName(), domain.getId(), user.getLogin(), f->f.getSocialSecurityNumProperty().in(nssAll)).forEach(person->{
				paternitys
				.stream()
				.filter(n-> n.getWorkerNaf().get().contentEquals(person.getSocialSecurityNum()))
				.forEach(p-> {
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
	
	private static Filter getFilter( ContractLeaveProperties f, Domain domain, Date startDate, Date endDate, CCCInfo ccc, boolean paternity) {
		Byte[] types = new Byte[] {MATERNIDAD.value(), PATERNIDAD.value()};
		
		Filter filter = f.getDomainProperty().eq(domain.getId())
				.and(f.getStartDateProperty().ge(convertDateSql(startDate)).and(f.getStartDateProperty().le(convertDateSql(endDate))))
				.and(f.getCCCProperty().eq(ccc.getCccAccount()));
		if(paternity) {
			filter = filter.and(f.getTypeProperty().in(types));
		} else {
			filter = filter.and(f.getTypeProperty().notIn(types));
		}
		return filter;
	}
	
	private static Date getFirstDateOfMonth(Date date){
	     Calendar cal = Calendar.getInstance();
	     cal.setTime(date);
	     cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
	     return cal.getTime();
	 }
}
