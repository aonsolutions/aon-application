package com.esferalia.aon.in.payroll.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

import com.esferalia.aon.occam.api.model.payroll.Employee;

import solutions.aon.seg.social.object.Employee.EmployeeBuilder;
import solutions.aon.seg.social.toolkit.Toolkit;

public class EmployeeParse {
	
	 private EmployeeParse() {
		    throw new IllegalStateException("Utility class");
     }

	public static Employee toEmployeeOccam(solutions.aon.seg.social.object.Employee ssEmployee) {
		Employee employee = new Employee();
		ssEmployee.accept(new solutions.aon.seg.social.object.Employee.Visitor() {
			@Override
			public void visitrFra(Date fra) {
				employee.setStartDate(fra);
			}
			
			@Override
			public void visitrFrb(Date frb) {
				employee.setEndDate(frb);
			}
			
			@Override
			public void visitTlf(String tlf) {
				employee.setPhone(tlf);
			}
			
			@Override
			public void visitSex(String sex) {
				employee.setSex(sex);
			}
			
			@Override
			public void visitRegime(String regime) {
				employee.setRegime(regime);
			}
			
			@Override
			public void visitOcup(String ocup) {
				employee.setOccupation(ocup);
			}
			
			@Override
			public void visitName(String name) {
				employee.setName(name);
			}
			
			@Override
			public void visitNSS(String nss) {
				employee.setNaf(nss);
			}
			
			@Override
			public void visitIpf(String ipf) {
				String dni = Toolkit.fillStringLeft(ssEmployee.getIpf().length()>10 ? ssEmployee.getIpf().substring(1) : ssEmployee.getIpf(),  "0", 10);
				employee.setDni(dni);
			}	
			
			@Override
			public void visitGc(String gc) {
				employee.setQuoteGroup(gc);
			}

			@Override
			public void visitCtaCti(String ctaCti) {
				employee.setCcc(ctaCti);
			}
			
			@Override
			public void visitContract(String contract) {
				employee.setContractType(contract);
			}
			
			@Override
			public void visitBirthDate(Date birthDate) {
				employee.setBirthDate(birthDate);
			}
			
			@Override 
			public void visitRlce(String rlce) {
				employee.setRlce(rlce);
			}
			
			@Override
			public void visitFactor(Double factor) {
				employee.setFactor(factor);
			}
			
			@Override
			public void visitCoef(String coef) {
				if(coef.contains(",")) {
					Double factor = Double.parseDouble( coef.replace(",", ".") );
					employee.setFactor(factor);
				}	
			}
			
			@Override
			public void visitCollective(String collective) {
				employee.setCollective(collective);
			}

			@Override public void visitGcDesc(String gcDesc) {}
			@Override public void visitEpig(String epig) {}
			@Override public void visitCompanyName(String companyName) {}
			@Override public void visitCompanyId(String companyId) {}
			@Override public void visitColec(String colec) {}
			@Override public void visitAgricultPromo(Boolean agriculturePromo) {}
			@Override public void visitrFeb(Date feb) {}
			@Override public void visitrFea(Date fea) {}
			@Override public void visitWorkTimeReduct(Boolean workTimeReduct) {}
			@Override public void visitVinFam(String vimFam) {}
			@Override public void visitSituation(String situaction) {}
			@Override public void visitReducingcoefic(String reducingCoefic) {}
			@Override public void visitProfesCat(String profesCat) {}

			@Override
			public void visitFrv(Date frv) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return employee;
	}
	
	public static solutions.aon.seg.social.object.Employee toEmployeeSS(Employee employee) {
		EmployeeBuilder builder = new EmployeeBuilder()
		.setRegime(employee.getRegime())
		.setCtaCti(employee.getCcc())
		.setNss(employee.getNaf())
		.setIpf(employee.getDni())
		.setFra(employee.getStartDate())
		;
	
		employee.getName().ifPresent(builder::setName);	
		employee.getEndDate().ifPresent(builder::setFrb);
		
		LocalDate start = convertToLocalDate(employee.getStartDate());
		
		employee.getOccupation(start).ifPresent(builder::setOcup);
		employee.getQuoteGroup(start).ifPresent(builder::setGc);
		employee.getContractType(start).ifPresent(builder::setContract);
		employee.getRlce(start).ifPresent(builder::setRlce);
		employee.getFactor(start).ifPresent(builder::setFactor);
		employee.getCollective(start).ifPresent(builder::setCollective);

		return builder.build();
	}
	
	private static LocalDate convertToLocalDate(Date date) {
		return new Timestamp(date.getTime()).toLocalDateTime().toLocalDate();
	}	
}
