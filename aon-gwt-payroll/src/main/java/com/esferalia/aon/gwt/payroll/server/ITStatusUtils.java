package com.esferalia.aon.gwt.payroll.server;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus.AndEmployeeITStatus;
import com.esferalia.aon.gwt.payroll.shared.OutOfDateException;
import com.esferalia.aon.in.payroll.tgss.its.ITParse;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.AonDateUtils;

import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.object.It;

public class ITStatusUtils {
	
	public static EnterpriseITStatus getEnterpriseITStatus(Connection connection, Domain domain, String login, Integer userId) {
		
		Date startDate = getFirstDateOfMonth(AonDateUtils.addMonths(new Date(), -1));
		Date endDate = new Date();
		
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), login, userId, "TGSS");
	
		List<CCCInfo> cccs = PAYROLL.getCCCStream(domain.getName(), domain.getId(), "").collect(Collectors.toList());
		  
		List<EmployeeIT> aonEmployeesIT = AON.getEmployeesIT(domain, new User(), 
				f->f.getDomainProperty().eq(domain.getId())
				.and(f.getStartDateProperty().ge(convertDateSql(startDate)).and(f.getStartDateProperty().le(convertDateSql(endDate))))
				.and(f.getCCCProperty().in(cccs.stream().map(CCCInfo::getCccAccount).toArray(String[]::new)))
		).collect(Collectors.toList());
		aonEmployeesIT.forEach(e -> System.out.println(e.getNss() + " : " + e.getStartDate() + "..." + e.getEndDate() ));
		
		AndEmployeeITStatus employeeITStatus = new AndEmployeeITStatus();
		

		for ( CCCInfo ccc: cccs ) {
			try {

				Collection<It> ssIts = SistemaRED.getIts(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), 
						ccc.getCccRegimeCode(), ccc.getCccAccount(), startDate, endDate, Optional.empty());
				for (It it : ssIts) {
					EmployeeIT ssIT = ITParse.parseTGSSToAon(it);
					
					Optional<EmployeeIT> baja = aonEmployeesIT.stream().filter(e-> e.getNss().equals(ssIT.getNss()) && e.getStartDate().equals(ssIT.getStartDate())).findFirst();
					
					if(ssIT.getName().isPresent()) {
						if(baja.isEmpty() ) {
							employeeITStatus.and( 
								new EnterpriseITStatus.ItNotExist()
								.setCcc(ssIT.getCcc())
								.setDate(ssIT.getStartDate())
								.setNaf(ssIT.getNss())
								.setName(ssIT.getName().get())
								.setPart((byte)0)
							);
						}
						
						if( ssIT.getEndDate().isPresent()) {
							Optional<EmployeeIT> alta = aonEmployeesIT.stream().filter(
									e-> e.getNss().equals(ssIT.getNss()) && e.getEndDate().isPresent() && e.getEndDate().get().equals(ssIT.getEndDate().get())
							).findFirst();
							
							if(alta.isEmpty() ) {
								employeeITStatus.and( 
									new EnterpriseITStatus.ItNotExist()
									.setCcc(ssIT.getCcc())
									.setDate(ssIT.getEndDate().get())
									.setNaf(ssIT.getNss())
									.setName(ssIT.getName().get())
									.setPart((byte)2)
								);
							}
						}
	
					}
				} //FOR
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			EnterpriseITStatus.isUp2Date(employeeITStatus); 
			employeeITStatus.and(new EnterpriseITStatus.Up2Date());
		} catch ( OutOfDateException e ) {	e.printStackTrace();	}
		
		EnterpriseITStatus.trace(employeeITStatus);
		
		return employeeITStatus;		
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
}
