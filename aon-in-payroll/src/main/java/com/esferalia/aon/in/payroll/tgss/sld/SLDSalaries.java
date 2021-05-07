package com.esferalia.aon.in.payroll.tgss.sld;


import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

import solutions.aon.seg.social.object.WorkerLiquidation;

public class SLDSalaries {
	
	
	public static Collection<Salary> getSLDSalaries(Map<String, Map<String, WorkerLiquidation>> costs, String ccc, Date from, Date to) {
		
		List<Salary> salaries = new LinkedList<Salary>();

		costs.forEach((type, cccCosts ) -> cccCosts.forEach( (naf, liq) -> salaries.add(getSalary(type, ccc, liq, from, to))));
		// same as above, perhaps this is must more clear 
		//for ( Entry<String, Map<String, WorkerLiquidation>> typeEntry : costs.entrySet() )
		//	for ( Entry<String, WorkerLiquidation > cccEntry: typeEntry.getValue().entrySet()  )
		//		salaries.add(getSalary(typeEntry.getKey(), cccEntry.getKey(), cccEntry.getValue()));
		
		return salaries;
		
	}
	
	private static Salary getSalary(String type, String ccc, WorkerLiquidation liquidation, Date from, Date to ) {
		
		Salary salary = new Salary();
		
		salary.setEndDate(to);
		salary.setIssueDate(to);
		salary.setStartDate(from);
		salary.setEnterpriseCCC(ccc);
		salary.setSalaryType(SalaryType.valueOf(type));
		
		liquidation.accept(new WorkerLiquidation.NoopVisitor() {

			@Override
			public void visitNss(String nss) {
				salary.setEmployeeSSNumber(nss);
			}

			@Override
			public void visitCaf(String caf) {
				salary.setEmployeeName(caf);
			}
			
			@Override
			public void visitCcBase(Float ccBase) {
				salary.setCommonContingenciesBase(ccBase.doubleValue());
				salary.setContextData(ContextVariable.CGC_BASE.getName(), ccBase.toString(), from, to);
			}
			
			@Override
			public void visitCcBusinessFee(Float ccLiquidBusinessFee) {
				salary.addCost(DeductionType.COMMON_CONTINGENCY, ContextVariable.CGC_ENTERPRISE.getName(), ccLiquidBusinessFee.doubleValue(), "CONTINGENCIAS COMUNES");
				salary.setContextData(ContextVariable.CGC_ENTERPRISE.getName(), ccLiquidBusinessFee.toString(), from, to);
			}

			@Override
			public void visitCcWorkerFee(Float ccLiquidWorkerFee) {
				salary.addDeduction(DeductionType.COMMON_CONTINGENCY, ccLiquidWorkerFee.doubleValue(), "CONTINGENCIAS COMUNES");
				salary.setContextData(ContextVariable.CGC_EMPLOYEE.getName(), ccLiquidWorkerFee.toString(), from, to);
			}

			@Override
			public void visitItWorkAccidentBusinessFee(Float itWorkAccidentBusinessFee) {
				salary.addCost(DeductionType.IT, ContextVariable.IT_ENTERPRISE.getName() , itWorkAccidentBusinessFee.doubleValue(), "IT DE ACCIDENTES DE TRABAJO");
				salary.setContextData(ContextVariable.IT_ENTERPRISE.getName(), itWorkAccidentBusinessFee.toString(), from, to);
			}

			@Override
			public void visitItWorkAccidentWorkerFee(Float itWorkAccidentWorkerFee) {
				//salary.addDeduction(DeductionType.IT, itWorkAccidentWorkerFee.doubleValue(), "IT DE ACCIDENTES DE TRABAJO");
			}

			@Override
			public void visitImsWorkAccidentBusinessFee(Float imsWorkAccidentBusinessFee) {
				salary.addCost(DeductionType.IMS, ContextVariable.IMS_ENTERPRISE.getName() , imsWorkAccidentBusinessFee.doubleValue(), "IMS DE ACCIDENTES DE TRABAJO");
				salary.setContextData(ContextVariable.IMS_ENTERPRISE.getName(), imsWorkAccidentBusinessFee.toString(), from, to);
			}

			@Override
			public void visitImsWorkAccidentWorkerFee(Float imsWorkAccidentWorkerFee) {
				//salary.addDeduction(DeductionType.IMS, imsWorkAccidentWorkerFee.doubleValue(), "IMS DE ACCIDENTES DE TRABAJO");
			}

			@Override
			public void visitWorkAccidentLiquidBase(Float workAccidentLiquidBase) {
				salary.setProfessionalContingenciesBase(workAccidentLiquidBase.doubleValue());
				salary.setContextData(ContextVariable.CGP_BASE.getName(), workAccidentLiquidBase.toString(), from, to);
			}


			@Override
			public void visitUnemploymentBusinessFee(Float unemploymentBusinessFee) {
				salary.addCost(DeductionType.UNEMPLOYMENT, ContextVariable.UNEMPLOY_ENTERPRISE.getName() , unemploymentBusinessFee.doubleValue(), "DESEMPLEO");
				salary.setContextData(ContextVariable.UNEMPLOY_ENTERPRISE.getName(), unemploymentBusinessFee.toString(), from, to);
			}

			@Override
			public void visitUnemploymentWorkerFee(Float unemploymentWorkerFee) {
				salary.addDeduction(DeductionType.UNEMPLOYMENT, unemploymentWorkerFee.doubleValue(), "DESEMPLEO");
				salary.setContextData(ContextVariable.UNEMPLOY_EMPLOYEE.getName(), unemploymentWorkerFee.toString(), from, to);
			}

			@Override
			public void visitFogasaBase(Float fogasaBase) {
				salary.setProfessionalContingenciesBase(fogasaBase.doubleValue());
			}
			
			@Override
			public void visitFogasaBusinessFee(Float fogasaBusinessFee) {
				salary.addCost(DeductionType.FOGASA, ContextVariable.FOGASA_ENTERPRISE.getName() , fogasaBusinessFee.doubleValue(), "FOGASA");
				salary.setContextData(ContextVariable.FOGASA_ENTERPRISE.getName(), fogasaBusinessFee.toString(), from, to);
			}

			@Override
			public void visitFogasaWorkerFee(Float fogasaWorkerFee) {
				//salary.addDeduction(DeductionType.FOGASA, fogasaWorkerFee.doubleValue(), "FOGASA");
			}

			@Override
			public void visitJobTrainingBusinessFee(Float jobTrainingBusinessFee) {
				salary.addCost(DeductionType.JOB_TRAINING, ContextVariable.FP_ENTERPRISE.getName() , jobTrainingBusinessFee.doubleValue(), "FORMACIÓN PROFESIONAL");
				salary.setContextData(ContextVariable.FP_ENTERPRISE.getName(), jobTrainingBusinessFee.toString(), from, to);
			}

			@Override
			public void visitJobTrainingWorkerFee(Float jobTrainingWorkerFee) {
				salary.addDeduction(DeductionType.JOB_TRAINING, jobTrainingWorkerFee.doubleValue(), "FORMACIÓN PROFESIONAL");
				salary.setContextData(ContextVariable.FP_EMPLOYEE.getName(), jobTrainingWorkerFee.toString(), from, to);
			}

			@Override
			public void visitGrantsAndBonusesBusinessFee(Float grantsAndBonusesBusinessFee) {
				salary.addBonus(null, "BONIFICACIONES", grantsAndBonusesBusinessFee.doubleValue());
			}

			@Override
			public void visitGrantsAndBonusesWorkerFee(Float grantsAndBonusesWorkerFee) {
				salary.addDeduction("BONIFICACIONES", grantsAndBonusesWorkerFee.doubleValue());
			}

			@Override
			public void visitTotalLiquidBusinessFee(Float totalLiquidBusinessFee) {
				salary.setTotalEnterprise(totalLiquidBusinessFee.doubleValue());
				salary.setContextData(ContextVariable.ENTERPRISE_QUOTA.getName(), totalLiquidBusinessFee.toString(), from, to);
			}

			@Override
			public void visitTotalLiquidWorkerFee(Float totalLiquidWorkerFee) {
				salary.setTotalSSContributions(totalLiquidWorkerFee.doubleValue());
				salary.setContextData(ContextVariable.EMPLOYEE_QUOTA.getName(), totalLiquidWorkerFee.toString(), from, to);
			}

			
		});
		
		
		return salary;
	}
	
	

	

}
