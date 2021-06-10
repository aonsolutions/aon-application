package com.esferalia.aon.gwt.payroll.server.pdf;

import static com.esferalia.aon.gwt.payroll.tools.Console.Separator.EQUAL;
import static com.esferalia.aon.gwt.payroll.tools.TestTools.assertWithLog;
import static com.esferalia.aon.gwt.payroll.util.DraftPayrollBuilder.getOccamSalary;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Payment.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.tools.Console;
import com.esferalia.aon.gwt.payroll.tools.Console.Status;
import com.esferalia.aon.gwt.payroll.util.JooqPayrollBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll.DefaultPayrollBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFPayment;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.occam.api.model.Salary.Cost;
import com.esferalia.aon.occam.api.model.Salary.Embargo;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.PaymentType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.github.javafaker.Faker;

public class DefaultPayrollUnitTest {

	Console console;
	
	@Rule
	public TestName testName = new TestName();
	
	@Before
	public void prepare() {
		console = new Console();
		console.start(testName.getMethodName());
	}
	
	private <T> void assertDraftToSalary(String name,T draftObj ,T salaryObj) {
		console.log(Status.COMPARE,"Draft " + name, draftObj, EQUAL);
		console.log(Status.COMPARE,"Salary " + name, salaryObj, EQUAL);
		
		assertWithLog("Draft -> Salary", name + " not matching", draftObj, salaryObj);
		console.success("DONE");
		console.jump();
	}
	
	private <T> void assertSalaryToPayroll(String name,T draftObj ,T payrollObj) {
		console.log(Status.COMPARE,"Salary " + name, payrollObj, EQUAL);
		console.log(Status.COMPARE,"Payroll " + name, draftObj, EQUAL);
		
		assertWithLog("Draft -> Salary",  name + " not matching", draftObj, payrollObj);
		console.success("DONE");
		console.jump();
	}
	
	private Deduction newDeduction(Deduction.Type type, double amount, String description) {
		Deduction deduction = new Deduction();
		deduction.setType(type);
		deduction.setAmount(amount);
		deduction.setDescription(description);		
		return deduction;
	}
	
	private Deduction newDeduction(Deduction.Type type, String name, double amount, String description) {
		Deduction deduction = new Deduction();
		deduction.setType(type);
		deduction.setAmount(amount);
		deduction.setName(name);
		deduction.setDescription(description);		
		return deduction;
	}
	
	@Test
	
	/**
	 * Testing random values filled Salary
	 * transpiling (with assertions).
	 */
	public void SalaryTranspileTest() {
		
		SalaryDraft draft = new SalaryDraft();
		
		Double cgcBase = 2100d;
		Double cgpBase = 2250d;
		Double nextrBase = 30d;
		Double extrBase = 30d;
		Date startDate = new Date();
		Date endDate = new Date();
		Double totalPayment = 2500d;
		Double totalDeduction = 500d;
		Double totalLiquid = 2000d;
		Double remuneration = 2120d;
		Double irpfBase = 2100d;
		Double totalEnterprise = 3000d;
		
		String employeeName = "JHON SMITH MCLENNAN, ANDREW JR";
		String employeeDocument = "12348673412X";
		String employeeSS = "17263546576879";
		String employeeCategory = "PROGRAMADOR JEFE";
		Date employeeSeniorityDate = new Date();
		String employeeQuoteGroup = "01";
		
		String enterpriseName = "Pdf4You S.L";
		String enterpriseDocument = "1928346398X";
		String enterpriseCCC = "183723498918";
		String enterpriseAddress = "Avenida de los floreros 2, Atlantis del norte";		

		
		/** Salary basic data */
		console.info("Draft","Setting basic salary data.");
		draft.setCgcBase(cgcBase);
		draft.setCgpBase(cgpBase);
		draft.setNonHExtraBase(nextrBase);
		draft.sethExtraBase(extrBase);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		
		draft.setTotalPayment(totalPayment);
		draft.setTotalDeduction(totalDeduction);
		draft.setTotalLiquid(totalLiquid);
		draft.setRemuneration(remuneration);
		draft.setIrpfBase(irpfBase);
		draft.setTotalEnterprise(totalEnterprise);
		

 		/** Employee data */
		console.info("Draft","Setting basic employee data.");
		draft.setEmployeeAgreementCategory(employeeCategory);
		draft.setEmployeeDocument(employeeDocument);
		draft.setEmployeeName(employeeName);
		draft.setEmployeeQuoteGroup(employeeQuoteGroup);
		draft.setEmployeeSeniorityDate(employeeSeniorityDate);
		draft.setEmployeeSS(employeeSS);
		 
		/** Enterprise data */
		console.info("Draft","Setting basic enterprise data.");
		draft.setEnterpriseName(enterpriseName);
		draft.setEnterpriseDocument(enterpriseDocument);
		draft.setEnterpriseCCC(enterpriseCCC);
//		draft.setEnterpriseAddress(enterpriseAddress);
		
		draft.setTimeUnits(30);
		
		/** Payments  */
		console.info("Draft","Setting payments.");
		for (int i = 0; i < 3; i++) {
			Payment cra = new Payment();
			cra.setType(Type.values()[i]);
			cra.setAmount(100d);
			cra.setDescription("Una descripcion estupenda");
			cra.setQuote(2d);
			draft.addPayment(cra);
		}
		
		/** Deductions */
		console.info("Draft","Setting deductions.");
		
		Deduction advancePaymentDeduction = newDeduction(Deduction.Type.ADVANCE_PAYMENT, 100.00, "Avance del mes");
		Deduction commonContingenciesDeduction = newDeduction(Deduction.Type.COMMON_CONTINGENCY, 100.00, "Contingencias comunes");
		Deduction inkindDeduction = newDeduction(Deduction.Type.IN_KIND, 100.00, "En especie.");
		Deduction irpfDeduction = newDeduction(Deduction.Type.IRPF, 100.00, "Irpf.");
		
		draft.addDeduction(advancePaymentDeduction);
		draft.addDeduction(commonContingenciesDeduction);
		draft.addDeduction(inkindDeduction);
		draft.addDeduction(irpfDeduction);
		
		/** Embargos */
		console.info("Draft","Setting embargos.");
		
		Deduction noSenseEmbargo = newDeduction(Deduction.Type.EMBARGO, "no sense embargo", 10, "embargo de manutencion :)");
		Deduction secondEmbargo  = newDeduction(Deduction.Type.EMBARGO, "second embargo", 78.156, "listo >.<");

		draft.addEmbargo(noSenseEmbargo);
		draft.addEmbargo(secondEmbargo);
		
		/** Costs **/
		console.info("Draft","Setting costs");
	   
		Deduction fogasaCost = newDeduction(Deduction.Type.FOGASA, 20.12, "Fogasa");
		Deduction cgcCost = newDeduction(Deduction.Type.COMMON_CONTINGENCY, 20.13, "Contingencias comunes");
		Deduction unemploymentCost = newDeduction(Deduction.Type.UNEMPLOYMENT, 20.14, "Desempleo");
		Deduction fpCost = newDeduction(Deduction.Type.JOB_TRAINING, 20.15, "Formación profesional");
		Deduction nextrCost = newDeduction(Deduction.Type.NON_STRUCTURAL_OVERTIME, 20.16, "H extras no estructurales");
		Deduction extrCost = newDeduction(Deduction.Type.STRUCTURAL_OVERTIME, 20.17, "H extras estructurales");
		Deduction cgpCost = newDeduction(Deduction.Type.PROFESSIONAL_CONTINGENCY, 20.18, "H extras estructurales");
		Deduction irpfCost = newDeduction(Deduction.Type.IRPF, 20.19, "IRPF");
		Deduction imsCost = newDeduction(Deduction.Type.OTHER, "IMS_E", 20.19, "");
		Deduction itCost = newDeduction(Deduction.Type.OTHER, "IT_E", 20.19,"");		
		
		draft.addCost(fogasaCost);
	    draft.addCost(cgcCost);
	    draft.addCost(unemploymentCost);
	    
	    draft.addCost(fpCost);
	    draft.addCost(nextrCost);
	    draft.addCost(extrCost);
	    draft.addCost(cgpCost);
	    draft.addCost(irpfCost);
	    draft.addCost(imsCost);
	    draft.addCost(itCost);


	    Double baseSalaryVariable = 1108.70;
	    Double liquitVariable = 1108.70;
	    Double workedDaysVariable = 30d;
	    String quoteGroupVariable = "Grupo 1";
	    Double fogasaEnterpriseVariable = 20.80;
	    Double cgcBaseVariable = 99.99;
	    Double cgcEnterpriseVariable = 7.99;
	    Double cgpBaseVariable = 109.99;
	    Double cgcBaseEnterpriseVariable = 109.99;
	    Double imsEnterpriseVariable = 6.99;
	    Double imsRateVariable = 1.99;
	    Double itEnterpriseVariable = 15.99;
	    Double itRate = 15.99;
	    Double unemployEnterpriseVariable = 60.99;
	    Double fpEnterpriseVariable = 29.9;
	    Double prorrationVariable = 30.99;
	    Double nextrVariable = 7.99;
	    Double extrVariable = 7.99;
	    Double irpfBaseVariable = 7.99;
	    
		/** Context data */
		console.info("Draft","Setting context variables.");
	    draft.addVariable(ContextVariable.BASE_SALARY,baseSalaryVariable,new Date(), new Date());
	    draft.addVariable(ContextVariable.LIQUID,liquitVariable,new Date(), new Date());
	    draft.addVariable(ContextVariable.WORKED_DAYS.getName(),workedDaysVariable,new Date(), new Date());
	    draft.addVariable(ContextVariable.QUOTE_GROUP.getName(),quoteGroupVariable,new Date(), new Date());
	    
	    /** Enterprise costs */
	    draft.addVariable(ContextVariable.FOGASA_ENTERPRISE.getName(),fogasaEnterpriseVariable,new Date(), new Date());
	    
	    draft.addVariable(ContextVariable.CGC_BASE.getName(),cgcBaseVariable,new Date(), new Date());
	    draft.addVariable(ContextVariable.CGC_ENTERPRISE.getName(),cgcEnterpriseVariable,new Date(), new Date());
	    
	    draft.addVariable(ContextVariable.CGP_BASE.getName(), cgpBaseVariable,new Date(), new Date());
	    draft.addVariable(ContextVariable.CGP_BASE_ENTERPRISE.getName(),cgcBaseEnterpriseVariable,new Date(), new Date());
	   
	    draft.addVariable(ContextVariable.IMS_ENTERPRISE.getName(),imsEnterpriseVariable,new Date(), new Date());
	    draft.addVariable(ContextVariable.IMS_RATE.getName(),imsRateVariable,new Date(), new Date());
	    
	    draft.addVariable(ContextVariable.IT_ENTERPRISE.getName(), itEnterpriseVariable,new Date(), new Date());
	    draft.addVariable(ContextVariable.IT_RATE.getName(),itRate,new Date(), new Date());
	  
	    draft.addVariable(ContextVariable.UNEMPLOY_ENTERPRISE.getName(), unemployEnterpriseVariable, new Date(), new Date());	    
	    
	    draft.addVariable(ContextVariable.FP_ENTERPRISE.getName(), fpEnterpriseVariable, new Date(), new Date());
	    
	    draft.addVariable(ContextVariable.PRORATION, prorrationVariable, new Date(), new Date());
	    
	    draft.addVariable(ContextVariable.NON_STRUCTURAL_OVERTIME_BASE.getName(), nextrVariable,new Date(), new Date());
	    
	    draft.addVariable(ContextVariable.STRUCTURAL_OVERTIME_BASE.getName(), extrVariable,new Date(), new Date());
	    
	    draft.addVariable(ContextVariable.IRPF_BASE.getName(), irpfBaseVariable, new Date(), new Date());
	   
	    /** Enterprise costs percentage */
	    Double gcgPercentE = 7.99;
	    Double gcpPercentE = 7.99;
	    Double fogasaPercentE = 7.99;
	    Double extrPercentE = 7.99;
	    Double nextrPercentE = 7.99;
	    Double fpPercentE = 7.99;
	    Double unemploymentPercentE = 7.99;
	    Double irpfPercentE = 7.99;
	    Double advancePaymentPercent = 17.99; 
	    Double cgcPercent = 18.99;
	    Double cgpPercent = 16.99;
	    Double inkindPercent = 19.99;
	    
	    draft.addVariable("PORCENTAJE_CGC_E",gcgPercentE,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_CGP_E",gcpPercentE,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_FOGASA",fogasaPercentE,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_EXTR_E",extrPercentE,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_NEXTR_E",nextrPercentE,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_FP_E",fpPercentE,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_DESMPL_E",unemploymentPercentE,new Date(), new Date());
	     
	    /** Deduction context data */
	    draft.addVariable(ContextVariable.IRPF_PERCENT.getName(),irpfPercentE,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_ADELANTO",advancePaymentPercent,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_CGC", cgcPercent, new Date(), new Date());
	    draft.addVariable("PORCENTAJE_CGP", cgpPercent, new Date(), new Date());
	    draft.addVariable("PORCENTAJE_EN_ESPECIE", inkindPercent, new Date(), new Date());
	    
		Salary salary = null;
		try {
			salary = getOccamSalary(draft);
		}catch (Exception e) {
			fail("Unexpected exception");
		}
		
		assertDraftToSalary(draft,salary);
		DefaultPayroll payrollPDF = JooqPayrollBuilder.buildDefaultPayrollFromSalary(new DefaultPayrollBuilder(), salary);
		
		console.jump();
		assertSalaryToDefaultPayroll(salary, payrollPDF);
		
		FileOutputStream out;
		try {
			out = new FileOutputStream(new File("TestPayroll.pdf"));
			PdfMaker.printDefaultPayroll(out, payrollPDF, null, null);
		} 
		catch (FileNotFoundException e) {}
		catch (CanNotCreatePdfException e) {}
	}
	
	/**
	 * Assert data form draft to Salary (occam)
	 * @param draft
	 * @param salary
	 */
	public void assertDraftToSalary(SalaryDraft draft, Salary salary) {
				
		/** --- GENERAL DATA ---------------------------------------------------------- **/
		Date draftEndDate = draft.getEndDate();
		Date salaryEndDate = salary.getEndDate();
		
		String draftCategory = draft.getEmployeeAgreementCategory();
		String salaryCategory = salary.getEmployeeCategory();
		
		String draftEmployeeDocument =  draft.getEmployeeDocument();
		String salaryEmployeeDocument = salary.getEmployeeDocument();
		
		String draftEmployeeName =  draft.getEmployeeName();
		String salaryEmployeeName = salary.getEmployeeName();
		
		String draftEmployeeQuoteGroup = draft.getEmployeeQuoteGroup();
		String salaryEmployeeQuoteGroup = salary.getEmployeeQuoteGroup();
		
		Date draftEmployeeSeniorityDate = draft.getEmployeeSeniorityDate();
		Date salaryEmployeeSeniorityDate = salary.getEmployeeSeniorityDate();
		
		String draftEmployeeSS = draft.getEmployeeSS();
		String salaryEmployeeSS = salary.getEmployeeSSNumber();
		
		String draftEnterpriseAddress = draft.getEnterpriseAddress();
		String salaryEnterpriseAddress = salary.getEnterpriseAddress();
		
		String draftEnterpriseCCC = draft.getEnterpriseCCC();
		String salaryEnterpriseCCC = salary.getEnterpriseCCC();
		
		String draftEnterpriseDocument = draft.getEnterpriseDocument();
		String salaryEnterpriseDocument = salary.getEnterpriseDocument();
		
		String draftEnterpriseName = draft.getEnterpriseName();
		String salaryEnterpriseName =  salary.getEnterpriseName();
		
		
		
		/** --- ENTERPRISE COSTS --------------------------------------------------------- **/
		//CGC
		Double draftCgcBase = draft.getCgcBase();
		Double salaryCgcBase = salary.getCommonContingenciesBase();
		
		Double draftCgcPercent = 0d;
		Double salaryCgcPercent = 0d;
		
		Double draftCgcValue = 0d;
		Double salaryCgcValue = 0d;
		
		//CGP
		Double draftCgpBase = draft.getCgpBase();
		Double salaryCgpBase = salary.getProfessionalContingenciesBase();
		
		Double draftCgpPercent = 0d;
		Double salaryCgpPercent = 0d;
		
		Double draftCgpValue = 0d;
		Double salaryCgpValue = 0d;
		
		//PRORRATION
		Double draftProrrationBase = draft.getProrationBase();
		Double salaryprorrationBase = salary.getExtraProrationBase();
		
		Double draftProrrationPercent = 0d;
		Double salaryProrrationPercent = 0d;
		
		Double draftProrrationValue = 0d;
		Double salaryProrrationValue = 0d;
		
		//AT EP 		
 		Double draftAtEpPercent = 0d;
 		Double salaryAtEpPercent = 0d;
 		
 		Double draftAtEpValue = 0d;
 		Double salaryAtEpValue = 0d;
 		
 		//UNEMPLOYMENT
		Double draftUnemploymentPercent = 0d;
		Double salaryUnemploymentPercent = 0d;
		
		Double draftUnemploymentValue = 0d;
		Double salaryUnemploymentValue = 0d;

		//JOB TRAINIG		
		Double draftFpPercent = 0d;
		Double salaryFpPercent = 0d;
		
		Double draftFpValue = 0d;
		Double salaryFpValue = 0d;
		
		//FOGASA		
		Double draftFogasaPercent = 0d;
		Double salaryFogasaPercent = 0d;
		
		Double draftFogasaValue = 0d;
		Double salaryFogasaValue = 0d;
		
		//IRPF
		Double draftIrpfBase = draft.getIrpfBase();
		Double salaryIrpfBase = salary.getIrpfBase();
		
		//ENTERPRISE TOTAL
		Double draftTotalEnterprise = draft.getTotalEnterprise();
		Double salaryTotalEnterprise = salary.getTotalEnterprise();	
		
		/** Checking salary data integrity */
		console.jump();
		console.start("Comparing Draft & Salary");
		
		assertDraftToSalary("CGC BASE", draftCgcBase,salaryCgcBase);
		assertDraftToSalary("CGC PERCENT", draftCgcPercent,salaryCgcPercent);
		assertDraftToSalary("CGC VALUE", draftCgcValue,salaryCgcValue);
		
		
		assertDraftToSalary("CGP BASE", draftCgpBase,salaryCgpBase);
		assertDraftToSalary("CGP PERCENT", draftCgpPercent,salaryCgpPercent);
		assertDraftToSalary("CGP VALUE", draftCgpValue,salaryCgpValue);

		assertDraftToSalary("AT/EP PERCENT", draftAtEpPercent,salaryAtEpPercent);
		assertDraftToSalary("AT/EP VALUE", draftAtEpValue,salaryAtEpValue);
		
		assertDraftToSalary("UNEMPLOYMENT PERCENT", draftUnemploymentPercent,salaryUnemploymentPercent);
		assertDraftToSalary("UNEMPLOYMENT VALUE", draftUnemploymentValue,salaryUnemploymentValue);
		
		assertDraftToSalary("FP PERCENT", draftFpPercent,salaryFpPercent);
		assertDraftToSalary("FP VALUE", draftFpValue,salaryFpValue);
		
		assertDraftToSalary("FOGASA PERCENT", draftFogasaPercent,salaryFogasaPercent);
		assertDraftToSalary("FOGASA VALUE", draftFogasaValue,salaryFogasaValue);
		
		assertDraftToSalary("End date", draftEndDate, salaryEndDate);
		
 		/** Employee data */
		assertDraftToSalary("Employee category", draftCategory, salaryCategory);
		assertDraftToSalary("Employee document", draftEmployeeDocument, salaryEmployeeDocument);
		assertDraftToSalary("Employee name", draftEmployeeName, salaryEmployeeName);
		assertDraftToSalary("Employee quote group", draftEmployeeQuoteGroup, salaryEmployeeQuoteGroup);
		assertDraftToSalary("Employee seniority date", draftEmployeeSeniorityDate, salaryEmployeeSeniorityDate);
		assertDraftToSalary("Employee SS number", draftEmployeeSS, salaryEmployeeSS);
		
		/** Enterprise data */
		assertDraftToSalary("Enterprise address", draftEnterpriseAddress, salaryEnterpriseAddress);
		assertDraftToSalary("Enterprise CCC", draftEnterpriseCCC, salaryEnterpriseCCC);
		assertDraftToSalary("Enterprise document", draftEnterpriseDocument, salaryEnterpriseDocument);
		assertDraftToSalary("Enterprise name", draftEnterpriseName, salaryEnterpriseName);
		assertDraftToSalary("Enterprise total", draftTotalEnterprise, salaryTotalEnterprise);

		/** Payments */
		console.jump();
		console.start("Comparing Draft & Salary --> PAYMENTS");
		
		for (int i = 0; i < draft.getPayments().size(); i++) {

			Payment draftPayment = draft.getPayments().get(i);
			com.esferalia.aon.occam.api.model.Salary.Payment salaryPayment = salary.getPayments().get(i);
			
			Type draftPaymentType = draftPayment.getType();
			PaymentType salaryPaymentType = salaryPayment.getPaymentType();
		
			assertDraftToSalary("Payment type", "CRA_" + draftPaymentType.ordinal(), "CRA_" + salaryPaymentType.ordinal());
		}
		
		/** Deductions */
		console.jump();
		console.start("Comparing Draft & Salary --> DEDUCTIONS");
		
		for (int i = 0; i < draft.getDeductions().size(); i++) {

			Deduction draftDeduction = draft.getDeductions().get(i);
			com.esferalia.aon.occam.api.model.Salary.Deduction salaryDeduction = salary.getDeductions().get(i);
			
			com.esferalia.aon.gwt.payroll.shared.Deduction.Type draftDeductionType = draftDeduction.getType();
			DeductionType salaryDeductionType = salaryDeduction.getDeductionType();
			
			assertDraftToSalary("Deduction type", draftDeductionType.ordinal(), salaryDeductionType.ordinal());
			String draftDeductionDescription =  draftDeductionType.getDescription();
			String salaryDeductionDescription = salaryDeduction.getDescription();
			
			assertDraftToSalary("Deduction description", draftDeductionDescription, salaryDeductionDescription);
		}
		
		
		/** Embargos */
		for(int i = 0; i < draft.getEmbargos().size(); i++) {
			@SuppressWarnings("unused")
			Deduction draftEmbargos = draft.getEmbargos().get(i);
			@SuppressWarnings("unused")
			Embargo salaryEmbargos = salary.getEmbargos().get(i);			
		
			/**
			 * Some test stuff here
			 */			
		}

		console.jump();
		console.start("Comparing Draft & Salary --> Enterprise costs.");
		
		for(int i = 0; i < draft.getCosts().size() ; i++) {	
			Deduction draftCost = draft.getCosts().get(i);
			Cost salaryCost = salary.getCosts().get(i);
			
			int type = draftCost.getType().ordinal();
			
			if(draftCost.getName() != null) {
				if(draftCost.getName().contains("IMS_E"))
					type = DeductionType.IMS.ordinal();
				
				if(draftCost.getName().contains("IT_E"))
					type = DeductionType.IT.ordinal();
			}
		
			assertDraftToSalary("Cost description", draftCost.getDescription(), salaryCost.getDescription());		
			assertDraftToSalary("Cost type",type,salaryCost.getCostType().ordinal());
			
		}
		
		
		console.jump();
		console.start("Comparing Draft & Salary --> Context variables.");
		
		Map<String, List<ContextData>> data = salary.getContextData();
		data.entrySet().forEach(type -> {
			type.getValue().forEach(var ->{
				console.log(Status.TEST, type.getKey() + " | " + var.getExpression());
			});
		});
		
		
	}
	
	
	/**
	 * Checks conversion between salary (Occam) and payroll (in-payroll)
	 * @param salary
	 * @param payroll
	 */
	public void assertSalaryToDefaultPayroll(Salary salary, DefaultPayroll payroll) {

		Double payrollCGC = payroll.getContingencies().get().getCommonContBase().orElse(null);
		Double salaryCGC = salary.getCommonContingenciesBase();
		
		Double payrollCGP = payroll.getContingencies().get().getProfessionalContBase().orElse(null);
		Double salaryCGP = salary.getProfessionalContingenciesBase();
		
		String payrollCategory = payroll.getProfessionalGroup().orElse(null);
		String salaryCategory = salary.getEmployeeCategory();
		
		String payrollEmployeeDocument =  payroll.getNif().orElse(null);
		String salaryEmployeeDocument = salary.getEmployeeDocument();
		
		String payrollEmployeeName =  payroll.getEmployee().orElse(null);
		String salaryEmployeeName = salary.getEmployeeName();
		
		Date payrollEmployeeSeniorityDate = payroll.getAntiquity().orElse(null);
		Date salaryEmployeeSeniorityDate = salary.getEmployeeSeniorityDate();
		
		String payrollEmployeeSS = payroll.getNss().orElse(null);
		String salaryEmployeeSS = salary.getEmployeeSSNumber();
		
		String payrollEnterpriseAddress = payroll.getAddress().orElse(null);
		String salaryEnterpriseAddress = salary.getEnterpriseAddress();
		
		String payrollEnterpriseCCC = payroll.getCcc().orElse(null);
		String salaryEnterpriseCCC = salary.getEnterpriseCCC();
		
		String payrollEnterpriseDocument = payroll.getCif().orElse(null);
		String salaryEnterpriseDocument = salary.getEnterpriseDocument();
		
		String payrollEnterpriseName = payroll.getEnterprise().orElse(null);
		String salaryEnterpriseName =  salary.getEnterpriseName();

		
		console.jump();
		console.start("Comparing Salary & PDF Default payroll");
		
		/**
		 * Comparing the Contingency bases
		 */
		assertSalaryToPayroll("Common contingencies",payrollCGC,salaryCGC);	
		assertSalaryToPayroll("Professional contingencies",payrollCGP,salaryCGP);
		
		/**
		 * Comparing payroll basic data
		 */
		assertSalaryToPayroll("Employee name", payrollEmployeeName, salaryEmployeeName);
		assertSalaryToPayroll("Category", payrollCategory, salaryCategory);
		assertSalaryToPayroll("Employee document", payrollEmployeeDocument, salaryEmployeeDocument);
		assertSalaryToPayroll("Employee seniority date", payrollEmployeeSeniorityDate, salaryEmployeeSeniorityDate);
		assertSalaryToPayroll("Employee ss", payrollEmployeeSS, salaryEmployeeSS);
		assertSalaryToPayroll("Enterprise Address", payrollEnterpriseAddress, salaryEnterpriseAddress);
		assertSalaryToPayroll("Enterprise CCC", payrollEnterpriseCCC, salaryEnterpriseCCC);
		assertSalaryToPayroll("Enterprise document", payrollEnterpriseDocument, salaryEnterpriseDocument);
		assertSalaryToPayroll("Enterprise name", payrollEnterpriseName, salaryEnterpriseName);
		
		/**
		 * Comparing payments 
		 */
		Map<Integer, ArrayList<PDFPayment>> payments = payroll.getAccruals().get();
		
		payments.keySet().forEach(cra -> {
			 ArrayList<PDFPayment> paymentsForCra = payments.get(cra);
			 paymentsForCra.forEach(payment ->{
//				 console.log(Status.TEST, "CRA_" + cra, payment.getDescription() + " AMOUNT: " + payment.getAmount(), Separator.COLON);
			 });
		});
	}

}
