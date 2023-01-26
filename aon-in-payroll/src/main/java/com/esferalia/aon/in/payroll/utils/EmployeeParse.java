package com.esferalia.aon.in.payroll.utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.tgss.idc.IdcParser;
import com.esferalia.aon.in.payroll.tgss.idc.IdcParserListener;
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
			
			@Override
			public void visitCno(String cno) {
				employee.setCno(cno);
			}

			@Override
			public void visitMdCtz(String mdCtz) {
				employee.setMdCtz(mdCtz);
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
			@Override public void visitFrv(Date frv) {}
			@Override public void visitQuoteMonth(Boolean quoteMonth) {	}

			@Override
			public void visitAsociativeSA(String asociativeSA) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return employee;
	}
	
	public static Employee IdcToEmployeeOccam(byte [] file) throws IOException, UnknownPDFException {
		Employee employee = new Employee();
		
		System.out.println("-------------------IdcToEmployeeOccam-------------------");

		IdcParser.parse(file, new IdcParserListener() {
	
			@Override
			public void onEnterprise(String socialReason, String ccc, String cif, String economicActivityCode,
					String economicActivityDescription, String regime, String fullCCC) {
				employee
				.setRegime(fullCCC.substring(0, 4))
				.setCcc(ccc);
				
				System.out.println("regime: "+fullCCC.substring(0, 4)+" ccc:"+ccc);
			}

			@Override
			public void onEmployee(String nss, String name) {
				employee
				.setNaf(nss)
				.setName(name);
				
				System.out.println("nss: "+nss+" name:"+name);
			}
			
			
			@Override
			public void onEmployeeOtherInfo(String documentType, String document, String gender, Date birthDate) {
				employee
				.setDni(document)
				.setSex(gender)
				.setBirthDate(birthDate);
				
				System.out.println("document: "+document+" gender:"+gender+ " birthDate:"+birthDate);
			}
			
			@Override
			public void onContractStart(Date date) {
				employee.setStartDate(date);
				
				System.out.println("startDate: "+date);
			}

			@Override
			public void onContractEnd(Date date) {
				employee.setEndDate(date);
				
				System.out.println("endDate: "+date);
			}
			
			
			@Override
			public void  onContractType(String contractType) {
				employee.setContractType(contractType);
				
				System.out.println("contractType: "+contractType);
			}

			@Override
			public void onContractQuoteGroup(String quoteGroup) {
				employee.setQuoteGroup(quoteGroup);
				
				System.out.println("quoteGroup: "+quoteGroup);
			}
			
			@Override
			public void onContractOcupation(String ocupation){
				employee.setOccupation(ocupation);
				
				System.out.println("ocupation: "+ocupation);
			}
			
			@Override
			public void onContractPartialCoeficient(String coeficiente){
				if(coeficiente.contains(",")) {
					Double factor = Double.parseDouble( coeficiente.replace(",", ".") );
					employee.setFactor(factor);
				}	
				
				System.out.println("coeficiente: "+coeficiente);
			}
		});
		
		return employee;
	}
	
	public static solutions.aon.seg.social.object.Employee toEmployeeSS(Employee employee) {
		LocalDate start = convertToLocalDate(employee.getStartDate());
		
		EmployeeBuilder builder = new EmployeeBuilder()
		.setRegime(employee.getRegime())
		.setCtaCti(employee.getCcc())
		.setNss(employee.getNaf())
		.setIpf(employee.getDni())
		.setFra(employee.getStartDate())
		;
		
		employee.getName().ifPresent(builder::setName);	
		employee.getEndDate().ifPresent(builder::setFrb);
		employee.getOccupation(start).ifPresent(builder::setOcup);
		employee.getQuoteGroup(start).ifPresent(builder::setGc);
		employee.getContractType(start).ifPresent(builder::setContract);
		employee.getRlce(start).ifPresent(builder::setRlce);
		employee.getFactor(start).ifPresent(builder::setFactor);
		employee.getCollective(start).ifPresent(builder::setCollective);
		employee.getCno(start).ifPresent(builder::setCno);
		employee.getMdCtz(start).ifPresent(builder::setMdctz);

		return builder.build();
	}
	
	private static LocalDate convertToLocalDate(Date date) {
		return new Timestamp(date.getTime()).toLocalDateTime().toLocalDate();
	}	
}
