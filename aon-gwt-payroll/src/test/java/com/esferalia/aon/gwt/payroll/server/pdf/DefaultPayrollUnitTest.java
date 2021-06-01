package com.esferalia.aon.gwt.payroll.server.pdf;

import static com.esferalia.aon.gwt.payroll.tools.Console.Separator.EQUAL;
import static com.esferalia.aon.gwt.payroll.tools.TestTools.assertWithLog;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

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
import com.esferalia.aon.gwt.payroll.util.DraftPayrollBuilder;
import com.esferalia.aon.gwt.payroll.util.JooqPayrollBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll.DefaultPayrollBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFDeduction;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFPayment;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.Embargo;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.PaymentType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

/**
 *  #################################################################################
 * 	# TO DO																			#
 *  #################################################################################
 *  #  1. Context data vs Contigency bases;						  (Draft -> Salary)	#
 *	#  2. Deductions 											  (Draft -> Salary)	#
 * 	#  3. Payrolls   											  (Draft -> Salary) #
 * 	#  4. Embargos												  (Draft -> Salary) #	
 * 	#  5. Total payments, total deductions, TOTAL PAYROLL.		  (Draft -> Salary) #
 *  #################################################################################
 * 
 * */




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
		console.log(Status.COMPARING,"Draft " + name, draftObj, EQUAL);
		console.log(Status.COMPARING,"Salary " + name, salaryObj, EQUAL);
		
		assertWithLog("Draft -> Salary", name + " not matching", draftObj, salaryObj);
		console.success("DONE");
		console.jump();
	}
	
	private <T> void assertSalaryToPayroll(String name,T draftObj ,T payrollObj) {
		console.log(Status.COMPARING,"Salary " + name, payrollObj, EQUAL);
		console.log(Status.COMPARING,"Payroll " + name, draftObj, EQUAL);
		
		assertWithLog("Draft -> Salary",  name + " not matching", draftObj, payrollObj);
		console.success("DONE");
		console.jump();
	}
	
	@Test
	/**
	 * Testing random values filled Salary
	 * transpiling (with assertions).
	 */
	public void SalaryTranspileTest() {
		
		SalaryDraft draft = new SalaryDraft();
		
		
		/** Salary basic data */
		
		console.info("Draft","Setting basic salary data.");
		draft.setCgcBase(null);
		draft.setCgpBase(null);
		draft.setEndDate(null);
		
 		/** Employee data */
		console.info("Draft","Setting basic employee data.");
		draft.setEmployeeAgreementCategory(null);
		draft.setEmployeeDocument(null);
		draft.setEmployeeName(null);
		draft.setEmployeeQuoteGroup(null);
		draft.setEmployeeSeniorityDate(null);
		draft.setEmployeeSS(null);
		 
		/** Enterprise data */
		console.info("Draft","Setting basic enterprise data.");
		draft.setEnterpriseAddress(null);
		draft.setEnterpriseCCC(null);
		draft.setEnterpriseDocument(null);
		draft.setEnterpriseName(null);
		
		/** Payments  */
		console.info("Draft","Setting payments.");
		for (int i = 0; i < Type.values().length; i++) {
			Payment cra = new Payment();
			cra.setType(Type.values()[i]);
			cra.setAmount(null);
			cra.setDescription(null);
			
			draft.addPayment(cra);
		}
		
		/** Deductions */
		console.info("Draft","Setting deductions.");
		for (int i = 0; i < Deduction.Type.values().length; i++) {
			Deduction deduction = new Deduction();
			deduction.setType(Deduction.Type.values()[i]);
			deduction.setAmount(100.99);
			deduction.setDescription("10.99%");		
			
			draft.addDeduction(deduction);
		}
		
		/** Embargos */
		console.info("Draft","Setting embargos.");
		for (int i = 0; i < Deduction.Type.values().length; i++) {
			Deduction embargo = new Deduction();
			embargo.setType(Deduction.Type.values()[i]);
			embargo.setAmount(0d);
			embargo.setDescription("10%");		
			
			draft.addEmbargo(embargo);
		}
		
		/** Context data */
		console.info("Draft","Setting context variables.");
		for (int i = 0; i < ContextVariable.values().length; i++) {		
		    draft.addVariable(
		    	ContextVariable.values()[i].getName(), 
		    	null, 
		    	null, 
		    	null
		    );
		}
		
		Salary salary = null;
		
		try {
			salary = DraftPayrollBuilder.getOccamSalary(draft);
		}catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
		
		assertDraftToSalary(draft,salary);
		DefaultPayroll payrollPDF = JooqPayrollBuilder.buildDefaultPayrollFromSalary(new DefaultPayrollBuilder(), salary);
		
		console.jump();
		console.log(Status.RUN, payrollPDF.getDeductions());	
		
		assertSalaryToDefaultPayroll(salary, payrollPDF);
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
		
		Double draftCGC = draft.getCgcBase();
		Double salaryCGC = salary.getCommonContingenciesBase();
		
		Double draftCGP = draft.getCgpBase();
		Double salaryCGP = salary.getProfessionalContingenciesBase();
		
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
		
		/** Checking salary data integrity */
		console.jump();
		console.start("Comparing Draft & Salary");
		
		assertDraftToSalary("CGC", draftCGC,salaryCGC);
		assertDraftToSalary("CGP", draftCGP,salaryCGP);
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
				 console.log(Status.TEST, "CRA_" + cra, payment.getDescription(), Separator.COLON);
			 });
		});
		
		
		/**
		 * Comparing deductions
		 */
		
		Map<Integer, ArrayList<PDFDeduction>> deductions = payroll.getDeductions().get();
		deductions.keySet().forEach(id -> {
			ArrayList<PDFDeduction> deductionsForID = deductions.get(id);
			deductionsForID.forEach(deduction -> {
				 console.log(Status.TEST, "ID_" + id, deduction.getPercent() + " " + deduction.getDescription(), Separator.COLON);
			});
		});
		
		
		/**
		 * Getting embargos
		 */
		
		
		
		
		
		
	}

}
