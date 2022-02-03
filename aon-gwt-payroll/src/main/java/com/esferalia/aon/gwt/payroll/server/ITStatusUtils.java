package com.esferalia.aon.gwt.payroll.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus.AndEmployeeITStatus;
import com.esferalia.aon.gwt.payroll.shared.OutOfDateException;
import com.esferalia.aon.in.payroll.tgss.its.ITParse;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.EmployeeITPart;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailStatus;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;
import com.esferalia.aon.watson.server.AonDateUtils;

import solutions.aon.seg.social.SistemaRED;

public class ITStatusUtils {
	
	public static EnterpriseITStatus getEnterpriseITStatus(Domain domain, String login, Integer userId) {
		
		Date startDate = getFirstDateOfMonth(AonDateUtils.addMonths(new Date(), -1));
		Date endDate = new Date();
		
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), login, userId, "TGSS");
	
		List<CCCInfo> cccs = PAYROLL.getCCCStream(domain.getName(), domain.getId(), "").collect(Collectors.toList());
		  
		AndEmployeeITStatus employeeITStatus = new AndEmployeeITStatus();
		for ( CCCInfo ccc: cccs ) {
			try {
				
				List<EmployeeIT> ssIts = new ArrayList<>();
				
				SistemaRED.getIts(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), 
						ccc.getCccRegimeCode(), ccc.getCccAccount(), startDate, endDate, Optional.empty()).forEach(it-> ssIts.add(ITParse.parseTGSSToAon(it)));
				
				if(!ssIts.isEmpty() && ssIts.get(0).getStartDate()!=null) {
	
					List<EmployeeIT> aonEmployeesIT = AON.getEmployeesIT(domain, new User(), 
							f->f.getDomainProperty().eq(domain.getId())
							.and(f.getStartDateProperty().ge(convertDateSql(ssIts.get(0).getStartDate())).and(f.getStartDateProperty().le(convertDateSql(endDate))))
							.and(f.getCCCProperty().eq(ccc.getCccAccount()))
					).collect(Collectors.toList());

			
					compareEmployeesITs(aonEmployeesIT, ssIts, employeeITStatus, domain);	//NO EXIST EN AON
		
					compareEmployeesITs(ssIts, aonEmployeesIT, employeeITStatus, domain); //NO EXIST EN SS
				}
			} catch (Exception e) {
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
	
	/*
	 * FIRST LIST NOT EXIST IN SECOND LIST
	 */
	private static AndEmployeeITStatus compareEmployeesITs(List<EmployeeIT> firstList, List<EmployeeIT>secondList, 
		AndEmployeeITStatus employeeITStatus, Domain domain) {
		List<EmployeeIT> itsUpdate = new ArrayList<>();

		for (EmployeeIT second : secondList) {
			AtomicBoolean change = new AtomicBoolean(false);
			Optional<String> name = second.getName();
			if(name.isPresent() && second.getStartDate()!=null) {
				Optional<EmployeeITPart> ssITBaja = second.getItBaja();
				List<EmployeeITPart> ssITConfirmations = second.getItConfirmations();
				Optional<EmployeeITPart> ssITAlta = second.getItAlta();
				
				Date newStartDate = normalizeITToPaint(second.getType(), second.getStartDate(), second.getId()==null);
				
				List<EmployeeIT> aonEmployeeITs = firstList.stream()
						.filter(e->e.getNss().equals(second.getNss()) && e.getStartDate().equals(newStartDate)).collect(Collectors.toList());

				if(ssITBaja.isPresent()) {
					System.out.println("Baja isPresent NSS:"+second.getNss()+ " date:"+ssITBaja.get().getDate()+" toAon:"+ssITBaja.get().getId());
					aonEmployeeITs.stream().map(EmployeeIT::getItBaja)
					.filter(e-> e.isPresent())
					.map(e->e.get())
					.filter(e->e.getDate().equals(ssITBaja.get().getDate()))
					.findFirst().ifPresentOrElse(e->{
						if(ssITBaja.get().getId()!=null && !ssITBaja.get().getStatus().equals(ContractLeaveDetailStatus.PROCESSED)) {
							ssITBaja.get().setStatus(ContractLeaveDetailStatus.PROCESSED);
							change.getAndSet(true);
						}
					}, ()->{
						employeeITStatus.and( 
							new EnterpriseITStatus.ItNotExist()
							.setEmployeeIT(second)
							.setEmployeeITPart(ssITBaja.get())
						);
					});
				}

				if(ssITAlta.isPresent()) {
					System.out.println();
					System.out.println("Alta isPresent NSS:"+second.getNss()+ " date:"+ssITAlta.get().getDate()+" toAon:"+ssITAlta.get().getId());
					System.out.println();
					aonEmployeeITs.stream().map(EmployeeIT::getItAlta)
					.filter(e-> e.isPresent())
					.map(e->e.get())
					.filter(e->e.getDate().equals(ssITAlta.get().getDate()))
					.findFirst().ifPresentOrElse(e->{
						if(ssITAlta.get().getId()!=null && !ssITAlta.get().getStatus().equals(ContractLeaveDetailStatus.PROCESSED)) {
							ssITAlta.get().setStatus(ContractLeaveDetailStatus.PROCESSED);
							change.getAndSet(true);
						}
					}, ()->{
						employeeITStatus.and( 
							new EnterpriseITStatus.ItNotExist()
							.setEmployeeIT(second)
							.setEmployeeITPart(ssITAlta.get())
						);
					});
				}
				
				if(!ssITConfirmations.isEmpty()) {
					for (EmployeeITPart ssITConfirmation:ssITConfirmations) {
						aonEmployeeITs.stream().map(EmployeeIT::getItConfirmations)
						.flatMap(Collection::stream)
						.filter(e-> e.getConfirmOrder().isPresent() && ssITConfirmation.getConfirmOrder().isPresent() ? 
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

	private static Date normalizeITToPaint(ContractLeaveType type, Date date, boolean toAon) {
		if(type.equals(ContractLeaveType.ACCIDENTE_LABORAL)) {
			Date realStartDate = DateUtils.copyDateOnly(date);
			return DateUtils.addDays2Date(realStartDate, toAon ? 1 : -1);
		}
		return date;
	}
	private static Date getFirstDateOfMonth(Date date){
	     Calendar cal = Calendar.getInstance();
	     cal.setTime(date);
	     cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
	     return cal.getTime();
	 }
}
