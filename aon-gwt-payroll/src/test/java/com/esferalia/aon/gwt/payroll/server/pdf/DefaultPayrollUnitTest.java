package com.esferalia.aon.gwt.payroll.server.pdf;

import static com.esferalia.aon.gwt.payroll.tools.TestTools.assertWithLog;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Payment.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.tools.Console;
import com.esferalia.aon.gwt.payroll.tools.Console.Status;
import com.esferalia.aon.gwt.payroll.util.DraftPayrollBuilder;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.PaymentType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class DefaultPayrollUnitTest {

	Console console;
	
	@Rule
	public TestName testName = new TestName();
	
	@Before
	public void prepare() {
		console = new Console();
		console.start(testName.getMethodName());
	}
	
	public <T> void compareSalaryDraftLog(String name,T draftObj ,T salaryObj) {
		console.log(Status.COMPARING,"Draft " + name, draftObj);
		console.log(Status.COMPARING,"Salary " + name, salaryObj);
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
			deduction.setAmount(null);
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
		
		compareSalaryDraftLog("CGC", draftCGC,salaryCGC);
		assertWithLog("Draft -> Salary", "Common contingency base not matching", draftCGC, salaryCGC);
		console.success("DONE");
		console.jump();
		
		compareSalaryDraftLog("CGP", draftCGP,salaryCGP);
		assertWithLog("Draft -> Salary", "Professional contingency base not matching", draftCGP, salaryCGP);
		console.success("DONE");
		console.jump();
		
		compareSalaryDraftLog("End date", draftEndDate, salaryEndDate);
		assertWithLog("Draft -> Salary", "End date not matching", draftEndDate, salaryEndDate);
		console.success("DONE");
		console.jump();
		
 		/** Employee data */
		compareSalaryDraftLog("Employee category", draftCategory, salaryCategory);
		assertWithLog("Draft -> Salary", "Employee category not matching", draftCategory, salaryCategory);
		console.success("DONE");
		console.jump();
		
		compareSalaryDraftLog("Employee document", draftEmployeeDocument, draftEmployeeDocument);
		assertWithLog("Draft -> Salary", "Employee document not matching",draftEmployeeDocument, salaryEmployeeDocument);
		console.success("DONE");
		console.jump();
		
		compareSalaryDraftLog("Employee name", draftEmployeeName, salaryEmployeeName);
		assertWithLog("Draft -> Salary", "Employee name not matching", draftEmployeeName, salaryEmployeeName);
		console.success("DONE");
		console.jump();
		
		
		compareSalaryDraftLog("Employee name", draftEmployeeName, salaryEmployeeName);
		assertWithLog("Draft -> Salary", "Employee quote group bases not matching", draftEmployeeQuoteGroup, salaryEmployeeQuoteGroup);
		console.success("DONE");
		console.jump();
		
		compareSalaryDraftLog("Employee seniority date", draftEmployeeSeniorityDate, salaryEmployeeSeniorityDate);
		assertWithLog("Draft -> Salary", "Employee seniority date not matching", draftEmployeeSeniorityDate, salaryEmployeeSeniorityDate );
		console.success("DONE");
		console.jump();
		
		compareSalaryDraftLog("Employee SS number", draftEmployeeSS, salaryEmployeeSS);
		assertWithLog("Draft -> Salary", "Employee SS number not matching", draftEmployeeSS, salaryEmployeeSS);
		console.success("DONE");
		console.jump();
		
		/** Enterprise data */
		compareSalaryDraftLog("Enterprise address", draftEnterpriseAddress, salaryEnterpriseAddress);
		assertWithLog("Draft -> Salary", "Enterprise address not matching", draftEnterpriseAddress, salaryEnterpriseAddress);
		console.success("DONE");
		console.jump();
		
		compareSalaryDraftLog("Enterprise CCC", draftEnterpriseCCC, salaryEnterpriseCCC);
		assertWithLog("Draft -> Salary", "Enterprise CCC not matching", draftEnterpriseCCC, salaryEnterpriseCCC);
		console.success("DONE");
		console.jump();
		
		compareSalaryDraftLog("Enterprise document", draftEnterpriseDocument, salaryEnterpriseDocument);
		assertWithLog("Draft -> Salary", "Enterprise Document not matching", draftEnterpriseDocument, salaryEnterpriseDocument);
		console.success("DONE");
		console.jump();
		
		compareSalaryDraftLog("Enterprise name", draftEnterpriseName, salaryEnterpriseName);
		assertWithLog("Draft -> Salary", "Enterprise name not matching", draftEnterpriseName, salaryEnterpriseName);
		console.success("DONE");
		console.jump();
				
		/** Payments */
		console.jump();
		console.start("Comparing Draft & Salary --> PAYMENTS");
		
		for (int i = 0; i < draft.getPayments().size(); i++) {

			Payment draftPayment = draft.getPayments().get(i);
			com.esferalia.aon.occam.api.model.Salary.Payment salaryPayment = salary.getPayments().get(i);
			
			Type draftPaymentType = draftPayment.getType();
			PaymentType salaryPaymentType = salaryPayment.getPaymentType();
		
			compareSalaryDraftLog("Payment type", draftPaymentType, salaryPaymentType);
			assertWithLog("Draft -> Salary", "Payment type not matching", draftPaymentType.ordinal() , salaryPaymentType.ordinal());
			console.success("DONE.");
			console.jump();

			
			
			
		}
		
		/** Deductions */
		console.jump();
		console.start("Comparing Draft & Salary --> DEDUCTIONS");
		
		for (int i = 0; i < draft.getDeductions().size(); i++) {

			Deduction draftDeduction = draft.getDeductions().get(i);
			com.esferalia.aon.occam.api.model.Salary.Deduction salaryDeduction = salary.getDeductions().get(i);
			
			com.esferalia.aon.gwt.payroll.shared.Deduction.Type draftDeductionType = draftDeduction.getType();
			DeductionType salaryDeductionType = salaryDeduction.getDeductionType();
			
			compareSalaryDraftLog("Deduction type", draftDeductionType, salaryDeductionType);
			assertWithLog("Draft -> Salary", "Deduction type not matching", draftDeductionType.ordinal() , salaryDeductionType.ordinal());
			console.success("DONE.");
			console.jump();

			String draftDeductionDescription =  draftDeductionType.getDescription();
			String salaryDeductionDescription = salaryDeduction.getDescription();
			
			compareSalaryDraftLog("Deduction description", draftDeductionDescription, salaryDeductionDescription);
			assertWithLog("Draft -> Salary", "Deduction description not matching",draftDeductionDescription , salaryDeductionDescription);
			console.success("DONE.");
			console.jump();
			
		}
		
		
		/** Embargos */
		
	}

}
