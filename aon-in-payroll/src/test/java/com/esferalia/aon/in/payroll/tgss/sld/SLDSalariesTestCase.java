package com.esferalia.aon.in.payroll.tgss.sld;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.impl.DSL;
import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.Cost;
import com.esferalia.aon.occam.api.model.Salary.Deduction;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.github.javafaker.Faker;

import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.TestCalculationQuery;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.WorkerLiquidation;

public class SLDSalariesTestCase extends AbstractOccamTest{
	
	private static double DELTA = 0.001; 

	@Test
	public void testSaveSalaries() throws IOException, SegSocialException {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("/solutions/aon/FNMT.p12") ){
			
			String ccc = "01105360062";
			String regimen = "0111";

			ctx.getDslContext().select(DSL.currentSchema()).fetch().forEach( r -> System.out.println(r.get(0)));
			
			byte certificateData [] = certificateInputStream.readAllBytes();
			
			AON.deleteSalaries(ctx, p -> p.getCCCProperty().eq(ccc));
			
			Domain domain = insertDomain(ctx, Faker.instance().letterify("???.????????????.???"));
			
			Collection<Employee> ssEmployees = 
			SistemaRED.getEmployees(
			certificateData, 
			"jg@FNMT", 
			"pkcs12",
			regimen,
			ccc
			);
			
			List<com.esferalia.aon.occam.api.model.payroll.Employee> occamEmployees = getEmployees(ssEmployees);
			
			saveEmployes(domain, occamEmployees);
			
			ssEmployees.forEach(ssEmployee -> {
				PAYROLL.getEmployee(
				domain.getName(), 
				domain.getId(), 
				"login", 
				p -> p.getDomainProperty().eq(domain.getId())
				.and(p.getNafProperty().eq(ssEmployee.getNss()))
				)
				.ifPresentOrElse(aonEmployee -> {
					Assert.assertEquals(ssEmployee.getIpf(), aonEmployee.getDni());
				}, Assert::fail);
				;
			});
			
			Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(new Date());
			Date from = AonDateUtils.add(firstDayOfMonth, Calendar.MONTH, -2);
			Date to = AonDateUtils.getLastDayOfMonth(from);

			Map<String, Map<String, WorkerLiquidation>> cccWorkersLiquidations = 
			SistemaRED.getWorkersLiquidationsByCCC(
					certificateData, 
					"jg@FNMT", 
					"pkcs12", 
					ccc, 
					SistemaRED.Regime.GENERAL,  
					from, 
					to, 
					SistemaRED.LiquidationType.TODAS, 
					SistemaRED.LiquidationOrigin.TODAS);
			
			Collection<Salary> salaries = 
			SLDSalaries.getSLDSalaries(cccWorkersLiquidations, ccc, from, to);
			
			AON.saveSalaries(ctx, domain.getId(), salaries);
			
			Map<String, List<Salary>> nafSalariesMap = 
			AON.getSalaries(ctx, p -> p.getCCCProperty().eq( ccc )).collect(Collectors.groupingBy( s -> s.getEmployeeSSNumber() ));
			
			AON.saveSalaries(ctx, domain.getId(), salaries);

			nafSalariesMap = 
			AON.getSalaries(ctx, p -> p.getCCCProperty().eq( ccc )).collect(Collectors.groupingBy( s -> s.getEmployeeSSNumber() ));
			
			assertSalaries(ssEmployees, cccWorkersLiquidations, nafSalariesMap);
			
		}
	}


	private void assertSalaries(Collection<Employee> ssEmployees,
			Map<String, Map<String, WorkerLiquidation>> cccWorkersLiquidations,
			Map<String, List<Salary>> nafSalariesMap) {
		ssEmployees.forEach( ssEmployee -> {
			String naf = ssEmployee.getNss();

			Map<Object, List<Salary>> nafSalaries = nafSalariesMap.get(naf).stream().collect(Collectors.groupingBy(s -> s.getSalaryType().name() ));
			
			
			nafSalaries.forEach( (type, list) -> {
				
				Assert.assertEquals(1, list.size());
				
				Salary salary = list.get(0);
				
				Salary salaryData =  AON.getSalaryData(ctx, p -> p.getIdProperty().eq(salary.getId()) ).findFirst().orElseThrow();
				
				
				WorkerLiquidation workerLiquidation = cccWorkersLiquidations.get(type).get(naf);
				
				workerLiquidation.accept(new WorkerLiquidation.NoopVisitor() {
					
					private Stream<Deduction> getDeductions(Salary salary, DeductionType type) {
						return salary.getDeductions().stream().filter(d -> d.getDeductionType() == type );
					}

					private double getDeductionAmount(Salary salary, DeductionType type) {
						return getDeductions(salary, type).collect(Collectors.summingDouble(d -> d.getAmount()));
					}

					private Stream<Cost> getCosts(Salary salary, DeductionType type) {
						return salary.getCosts().stream().filter(d -> d.getCostType() == type );
					}

					private double getCostAmount(Salary salary, DeductionType type) {
						return getCosts(salary, type).collect(Collectors.summingDouble(d -> d.getAmount()));
					}
					
					private double getDataAmount(Salary salary, ContextVariable var) {
						return getDataAmount(salary, var.getName());
					}
					
					private double getDataAmount(Salary salary, String name) {
						return salaryData.getContextData(name, Collectors.summingDouble(expression -> Double.parseDouble(expression)));
					}
					
					// CGC
					//
					@Override
					public void visitCcBase(Float ccBase) {
						visitCcLiquidBase(ccBase);
					}
					
					@Override
					public void visitCcBusinessFee(Float ccBusinessFee) {
						this.visitCcLiquidBusinessFee(ccBusinessFee);
					}

					@Override
					public void visitCcWorkerFee(Float ccWorkerFee) {
						Assert.assertEquals(ccWorkerFee.doubleValue(), getDeductionAmount(salary, DeductionType.COMMON_CONTINGENCY), DELTA);
					}

					@Override
					public void visitCcLiquidBase(Float ccLiquidBase) {
						Assert.assertEquals(ccLiquidBase.doubleValue(), getDataAmount(salary, ContextVariable.CGC_BASE), DELTA);
					}
					
					@Override
					public void visitCcLiquidBusinessFee(Float ccLiquidBusinessFee) {
						Assert.assertEquals(ccLiquidBusinessFee.doubleValue(), getCostAmount(salary, DeductionType.COMMON_CONTINGENCY), DELTA);
					}

					@Override
					public void visitCcLiquidWorkerFee(Float ccLiquidWorkerFee) {
						Assert.assertEquals(ccLiquidWorkerFee.doubleValue(), getDeductionAmount(salary, DeductionType.COMMON_CONTINGENCY), DELTA);
					}
					
					// FOGASA
					@Override
					public void visitFogasaBase(Float fogasaBase) {
						Assert.assertEquals(fogasaBase.doubleValue(), getDataAmount(salary, ContextVariable.CGP_BASE), DELTA);
					}

					@Override
					public void visitFogasaBusinessFee(Float fogasaBusinessFee) {
						Assert.assertEquals(fogasaBusinessFee.doubleValue(), getCostAmount(salary, DeductionType.FOGASA), DELTA);
					}
					
					// IT
					@Override
					public void visitItWorkAccidentBase(Float itBase) {
						Assert.assertEquals(itBase.doubleValue(), getDataAmount(salary, ContextVariable.CGP_BASE), DELTA);
					}

					@Override
					public void visitItWorkAccidentBusinessFee(Float itBusinessFee) {
						Assert.assertEquals(itBusinessFee.doubleValue(), getCostAmount(salary, DeductionType.IT), DELTA);
					}
					
					// IMS
					@Override
					public void visitImsWorkAccidentBase(Float imsBase) {
						Assert.assertEquals(imsBase.doubleValue(), getDataAmount(salary, ContextVariable.CGP_BASE), DELTA);
					}

					@Override
					public void visitImsWorkAccidentBusinessFee(Float imsBusinessFee) {
						Assert.assertEquals(imsBusinessFee.doubleValue(), getCostAmount(salary, DeductionType.IMS), DELTA);
					}
					
					// FP
					@Override
					public void visitJobTrainingBase(Float imsBase) {
						Assert.assertEquals(imsBase.doubleValue(), getDataAmount(salary, ContextVariable.CGP_BASE), DELTA);
					}

					@Override
					public void visitJobTrainingWorkerFee(Float jobTrainningWorkerFee) {
						Assert.assertEquals(jobTrainningWorkerFee.doubleValue(), getDeductionAmount(salary, DeductionType.JOB_TRAINING), DELTA);
					}

					@Override
					public void visitJobTrainingBusinessFee(Float jobTrainningBusinessFee) {
						Assert.assertEquals(jobTrainningBusinessFee.doubleValue(), getCostAmount(salary, DeductionType.JOB_TRAINING), DELTA);
					}

					// DESMPL
					@Override
					public void visitUnemploymentBase(Float imsBase) {
						Assert.assertEquals(imsBase.doubleValue(), getDataAmount(salary, ContextVariable.CGP_BASE), DELTA);
					}

					@Override
					public void visitUnemploymentWorkerFee(Float jobTrainningWorkerFee) {
						Assert.assertEquals(jobTrainningWorkerFee.doubleValue(), getDeductionAmount(salary, DeductionType.UNEMPLOYMENT), DELTA);
					}

					@Override
					public void visitUnemploymentBusinessFee(Float jobTrainningBusinessFee) {
						Assert.assertEquals(jobTrainningBusinessFee.doubleValue(), getCostAmount(salary, DeductionType.UNEMPLOYMENT), DELTA);
					}

				});
			} );
			
			
		});
	}

	
	public static List<com.esferalia.aon.occam.api.model.payroll.Employee> saveEmployes(Domain domain, List<com.esferalia.aon.occam.api.model.payroll.Employee> occamEmployees) {
		List<com.esferalia.aon.occam.api.model.payroll.Employee> savedEmployees = new ArrayList<com.esferalia.aon.occam.api.model.payroll.Employee>();
		occamEmployees.forEach(occamEmployee -> savedEmployees.add(PAYROLL.addEmployee(domain.getName(), domain.getId(), "login", occamEmployee)));
		return savedEmployees;
	}

	public static List<com.esferalia.aon.occam.api.model.payroll.Employee> getEmployees(byte certificateData[], String certificatePassword,
			String certificateType, String regimen, String ccc) throws SegSocialException {
		return getEmployees(SistemaRED.getEmployees(certificateData, certificatePassword, certificateType, regimen, ccc));
	}

	public static List<com.esferalia.aon.occam.api.model.payroll.Employee> getEmployees(Collection<Employee> ssEmployees) {
		return ssEmployees.stream().map(ssEmployee -> {
			com.esferalia.aon.occam.api.model.payroll.Employee aonEmployee = 
			new com.esferalia.aon.occam.api.model.payroll.Employee();
			
			aonEmployee
			.setNaf(ssEmployee.getNss())
			.setDni(ssEmployee.getIpf())
			.setRegime(ssEmployee.getRegime())
			.setStartDate(ssEmployee.getFra())
			;
			
			ssEmployee.getSex().ifPresent(aonEmployee::setSex);
			ssEmployee.getName().ifPresent(aonEmployee::setName);
			ssEmployee.getCtaCti().ifPresent(aonEmployee::setCcc);
			ssEmployee.getFrb().ifPresent(aonEmployee::setEndDate);
			ssEmployee.getGc().ifPresent(aonEmployee::setQuoteGroup);
			ssEmployee.getBirthDate().ifPresent(aonEmployee::setBirthDate);
			aonEmployee.setContractType(ssEmployee.getContract().orElse("000"));
			ssEmployee.getCoef().filter(coef -> coef > 0.00 ).ifPresent( aonEmployee::setFactor);
			
			return aonEmployee;
			
		})
		.collect(Collectors.toList());
	}
	

}
