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
import com.esferalia.aon.gwt.payroll.tools.Console;
import com.esferalia.aon.gwt.payroll.tools.Console.Separator;
import com.esferalia.aon.gwt.payroll.tools.Console.Status;
import com.esferalia.aon.gwt.payroll.util.JooqPayrollBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll.DefaultPayrollBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFDeduction;
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
		
		Faker data = new Faker();
		SalaryDraft draft = new SalaryDraft();
		
		/** Salary basic data */
		console.info("Draft","Setting basic salary data.");
		draft.setCgcBase(2100d);
		draft.setCgpBase(2250d);
		draft.setNonHExtraBase(30d);
		draft.sethExtraBase(30d);
		draft.setEndDate(new Date());
		draft.setStartDate(new Date());
		draft.setTotalPayment(2500d);
		draft.setTotalLiquid(2000d);
		draft.setRemuneration(2120d);
		draft.setIrpfBase(2100d);
		draft.setTotalDeduction(500d);
		draft.setTotalEnterprise(3000d);
		

 		/** Employee data */
		console.info("Draft","Setting basic employee data.");
		draft.setEmployeeAgreementCategory("PROGRAMADOR JEFE");
		draft.setEmployeeDocument("12348673412X");
		draft.setEmployeeName("JHON SMITH MCLENNAN, ANDREW JR");
		draft.setEmployeeQuoteGroup("01");
		draft.setEmployeeSeniorityDate(new Date());
		draft.setEmployeeSS("17263546576879");
		 
		/** Enterprise data */
		console.info("Draft","Setting basic enterprise data.");
		//draft.setEnterpriseAddress("Avenida de los floreros 2, Atlantis del norte");
		draft.setEnterpriseCCC("183723498918");
		draft.setEnterpriseDocument("1928346398X");
		draft.setEnterpriseName("Pdf4You S.L");
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
		draft.addDeduction(newDeduction(Deduction.Type.ADVANCE_PAYMENT, 100.00, "Avance del mes"));
		draft.addDeduction(newDeduction(Deduction.Type.COMMON_CONTINGENCY, 100.00, "Contingencias comunes"));
		draft.addDeduction(newDeduction(Deduction.Type.IN_KIND, 100.00, "En especie."));
		draft.addDeduction(newDeduction(Deduction.Type.IRPF, 100.00, "Irpf."));
		
		/** Embargos */
		console.info("Draft","Setting embargos.");
		for (int i = 0; i < 2; i++) {
			Deduction embargo = new Deduction();
			embargo.setType(Deduction.Type.EMBARGO);
			embargo.setAmount(i + 0d);
			embargo.setDescription("Hola soy un embargo :)");		
			
			draft.addEmbargo(embargo);
		}
		
		
		/** Costs **/
		console.info("Draft","Setting costs");
	   
		
		draft.addCost(newDeduction(Deduction.Type.FOGASA, 20.12, "Fogasa"));
	    draft.addCost(newDeduction(Deduction.Type.COMMON_CONTINGENCY, 20.13, "Contingencias comunes"));
	    draft.addCost(newDeduction(Deduction.Type.UNEMPLOYMENT, 20.14, "Desempleo"));
	    draft.addCost(newDeduction(Deduction.Type.JOB_TRAINING, 20.15, "Formación profesional"));
	    draft.addCost(newDeduction(Deduction.Type.NON_STRUCTURAL_OVERTIME, 20.16, "H extras no estructurales"));
	    draft.addCost(newDeduction(Deduction.Type.STRUCTURAL_OVERTIME, 20.17, "H extras estructurales"));
	    draft.addCost(newDeduction(Deduction.Type.PROFESSIONAL_CONTINGENCY, 20.18, "H extras estructurales"));
	    draft.addCost(newDeduction(Deduction.Type.IRPF, 20.19, "IRPF"));
	    draft.addCost(newDeduction(Deduction.Type.OTHER, "IMS_E", 20.19, ""));
	    draft.addCost(newDeduction(Deduction.Type.OTHER, "IT_E", 20.19,""));
		
	    
	    
		/** Context data */
		console.info("Draft","Setting context variables.");
	    draft.addVariable(ContextVariable.BASE_SALARY,1108.70,new Date(), new Date());
	    draft.addVariable(ContextVariable.LIQUID,1108.70,new Date(), new Date());
	    draft.addVariable(ContextVariable.WORKED_DAYS.getName(),30.80,new Date(), new Date());
	    draft.addVariable(ContextVariable.QUOTE_GROUP.getName(),"Grupo 1",new Date(), new Date());
	    
	    /** Enterprise costs */
	    draft.addVariable(ContextVariable.FOGASA_ENTERPRISE.getName(),20.80,new Date(), new Date());
	    
	    draft.addVariable(ContextVariable.CGC_BASE.getName(),99.99,new Date(), new Date());
	    draft.addVariable(ContextVariable.CGC_ENTERPRISE.getName(),7.99,new Date(), new Date());
	    
	    draft.addVariable(ContextVariable.CGP_BASE.getName(),109.99,new Date(), new Date());
	    draft.addVariable(ContextVariable.CGP_BASE_ENTERPRISE.getName(),109.99,new Date(), new Date());
	   
	    draft.addVariable(ContextVariable.IMS_ENTERPRISE.getName(),6.99,new Date(), new Date());
	    draft.addVariable(ContextVariable.IMS_RATE.getName(),1.99,new Date(), new Date());
	    
	    draft.addVariable(ContextVariable.IT_ENTERPRISE.getName(),15.99,new Date(), new Date());
	    draft.addVariable(ContextVariable.IT_RATE.getName(),15.99,new Date(), new Date());
	  
	    draft.addVariable(ContextVariable.UNEMPLOY_ENTERPRISE.getName(),60.99,new Date(), new Date());	    
	    
	    draft.addVariable(ContextVariable.FP_ENTERPRISE.getName(),29.99,new Date(), new Date());
	    
	    draft.addVariable(ContextVariable.PRORATION,30.99,new Date(), new Date());
	    
	    draft.addVariable(ContextVariable.NON_STRUCTURAL_OVERTIME_BASE.getName(),7.99,new Date(), new Date());
	    
	    draft.addVariable(ContextVariable.STRUCTURAL_OVERTIME_BASE.getName(),7.99,new Date(), new Date());
	    
	    draft.addVariable(ContextVariable.IRPF_BASE.getName(),7.99,new Date(), new Date());
	   
	    /** Enterprise costs percentage */
	    draft.addVariable("PORCENTAJE_CGC_E",7.99,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_CGP_E",7.99,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_FOGASA",7.99,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_EXTR_E",7.99,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_NEXTR_E",7.99,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_FP_E",7.99,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_DESMPL_E",7.99,new Date(), new Date());
	     
	    /** Deduction context data */
	    draft.addVariable(ContextVariable.IRPF_PERCENT.getName(),7.99,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_ADELANTO",17.99,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_CGC",18.99,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_CGP",16.99,new Date(), new Date());
	    draft.addVariable("PORCENTAJE_EN_ESPECIE",19.99,new Date(), new Date());
	    
		Salary salary = null;
		try {
			salary = getOccamSalary(draft);
		}catch (Exception e) {
			e.printStackTrace();
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

	
	@Test
	/**
	 * Test case trying the Salary transpile 
	 * with EVERY parameter being null.
	 */
	public void SalaryEmptyTranspileTest() {
				
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
		Double draftAtEpBase = 
				(Double) draft.getContext().get(7).getValue() +
				(Double) draft.getContext().get(8).getValue(); 		
		
 		String salaryAtEpBase = "" + salary.getContextData().get("IMS_E").get(0).getExpression();	
 				 				
 		console.info( salary.getContextData().get("TARIFA_IMS").get(0).getExpression() + "!!!!");
 		
 		Double draftAtEpPercent = 0d;
 		Double salaryAtEpPercent = 0d;
 		
 		Double draftAtEpValue = 0d;
 		Double salaryAtEpValue = 0d;
 		
 		//UNEMPLOYMENT
		Double draftUnemploymentBase = 0d;
		Double salaryUnemploymentBase = 0d;	
		
		Double draftUnemploymentPercent = 0d;
		Double salaryUnemploymentPercent = 0d;
		
		Double draftUnemploymentValue = 0d;
		Double salaryUnemploymentValue = 0d;

		//JOB TRAINIG
		Double draftFpBase = 0d;
		Double salaryFpBase = 0d;
		
		Double draftFpPercent = 0d;
		Double salaryFpPercent = 0d;
		
		Double draftFpValue = 0d;
		Double salaryFpValue = 0d;
		
		//FOGASA
		Double draftFogasaBase = 0d;
		Double salaryFogasaBase = 0d;
		
		Double draftFogasaPercent = 0d;
		Double salaryFogasaPercent = 0d;
		
		Double draftFogasaValue = 0d;
		Double salaryFogasavalue = 0d;
		
		//IRPF
		Double draftIrpfBase = draft.getIrpfBase();
		Double salaryIrpfBase = salary.getIrpfBase();
		
		//ENTERPRISE TOTAL
		Double draftTotalEnterprise = draft.getTotalEnterprise();
		Double salaryTotalEnterprise = salary.getTotalEnterprise();	
		
		/** Checking salary data integrity */
		console.jump();
		console.start("Comparing Draft & Salary");
		
		assertDraftToSalary("CGC", draftCgcBase,salaryCgcBase);
		assertDraftToSalary("CGP", draftCgpBase,salaryCgpBase);
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
		console.log(Status.RUN, "Payments",payments);
		
		payments.keySet().forEach(cra -> {
			 ArrayList<PDFPayment> paymentsForCra = payments.get(cra);
			 paymentsForCra.forEach(payment ->{
				 console.log(Status.TEST, "CRA_" + cra, payment.getDescription() + " AMOUNT: " + payment.getAmount(), Separator.COLON);
			 });
		});
		
		/**
		 * Comparing deductions
		 */
		
		Map<Integer, ArrayList<PDFDeduction>> deductions = payroll.getDeductions().get();
		deductions.keySet().forEach(id -> {
			ArrayList<PDFDeduction> deductionsForID = deductions.get(id);
			deductionsForID.forEach(deduction -> {
				// console.log(Status.TEST, id, deduction.getPercent().orElse(-1d) + " | " + deduction.getDescription().orElse("empty") + " | " + deduction.getAmount().orElse(-1d), Separator.ARROW_REVERSE);
			});
		});
		
		
		/**
		 * Getting embargos
		 */
		
		
		
		
		
		
	}

}
