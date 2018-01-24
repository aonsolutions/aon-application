package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.filefilter.AgeFileFilter;
import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.Level;
import com.esferalia.aon.gwt.payroll.shared.Payment.Type;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.ibm.icu.util.Calendar;

import junit.framework.Assert;

public class SQLAgreementDraftTestCase extends AbstractSQLTestCase {

	@Test
	public void testNewCategoriesI() throws SQLException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		DomainRecord domain = newDomain(aonContext);

		AgreementDraft draft = new AgreementDraft();
		draft.setId(-1);
		

		Level levelI = new Level();
		levelI.setId(-1);
		draft.addDraftLevel(levelI);
		Set<String> categoriesI = new HashSet<String>();
		categoriesI.add("C 1.1");
		categoriesI.add("C 1.2");
		draft.addDraftCategories(levelI, categoriesI);
		Level levelII = new Level();
		levelII.setId(-2);
		draft.addDraftLevel(levelII);
		Set<String> categoriesII = new HashSet<String>();
		categoriesII.add("C 2.1");
		categoriesII.add("C 2.2");
		draft.addDraftCategories(levelII, categoriesII);


		EmployeesServiceHelper.calculate(connection,
				draft, domain.getId(), null);
		SQLAgreementDraft.save(connection, draft, domain.getId(), null);
		
		draft.clearDrafts();
		EmployeesServiceHelper.calculate(connection,
				draft, domain.getId(), null);
		Assert.assertEquals(2, draft.getCategoriesMap().size());
		
		for( Map.Entry<Integer, Set<String>> entry: draft.getCategoriesMap().entrySet()) 
			Assert.assertEquals(2, entry.getValue().size());			

	}

	@Test
	public void testNewCategoriesII() throws SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		AgreementRecord agreement = newAgreement(aonContext);

		AgreementDraft draft = new AgreementDraft();
		draft.setId(agreement.getId());
		

		Level levelI = new Level();
		levelI.setId(-1);
		draft.addDraftLevel(levelI);
		Set<String> categoriesI = new HashSet<String>();
		categoriesI.add("C 1.1");
		categoriesI.add("C 1.2");
		draft.addDraftCategories(levelI, categoriesI);

		EmployeesServiceHelper.calculate(connection,
				draft, agreement.getDomain(), null);
		SQLAgreementDraft.save(connection, draft, agreement.getDomain(), null);

		draft.clearDrafts();
		EmployeesServiceHelper.calculate(connection,
				draft, agreement.getDomain(), null);
		Assert.assertEquals(1, draft.getCategoriesMap().size());

		Level levelII = new Level();
		levelII.setId(-2);
		draft.addDraftLevel(levelII);
		Set<String> categoriesII = new HashSet<String>();
		categoriesII.add("C 2.1");
		categoriesII.add("C 2.2");
		draft.addDraftCategories(levelII, categoriesII);
		

		EmployeesServiceHelper.calculate(connection,
				draft, agreement.getDomain(), null);
		SQLAgreementDraft.save(connection, draft, agreement.getDomain(), null);
		
		draft.clearDrafts();
		EmployeesServiceHelper.calculate(connection,
				draft, agreement.getDomain(), null);
		
		Assert.assertEquals(2, draft.getCategoriesMap().size());
		
		for( Map.Entry<Integer, Set<String>> entry: draft.getCategoriesMap().entrySet()) 
			for( String category: entry.getValue())
				System.out.println("1 :" + entry.getKey() + "-." +category);

		for( Map.Entry<Integer, Set<String>> entry: draft.getCategoriesMap().entrySet()){ 
			Assert.assertEquals(2, entry.getValue().size());			
		}
				
				
		for ( Level level : draft.getLevels() ){
			Set<String> categories = new HashSet<String>();
			for ( int i = 0 ; i < 100; i++ )
				categories.add("C " + level.getId() + "." + i);
			draft.addDraftCategories(level, categories);
		}
		
		EmployeesServiceHelper.calculate(connection,
				draft, agreement.getDomain(), null);
		SQLAgreementDraft.save(connection, draft, agreement.getDomain(), null);

		draft.clearDrafts();
		EmployeesServiceHelper.calculate(connection,
				draft, agreement.getDomain(), null);
	
		Assert.assertEquals(2, draft.getCategoriesMap().size());

		for( Map.Entry<Integer, Set<String>> entry: draft.getCategoriesMap().entrySet()) 
			for( String category: entry.getValue())
				System.out.println("2 :" + entry.getKey() + "-." +category);

		for( Map.Entry<Integer, Set<String>> entry: draft.getCategoriesMap().entrySet()){ 
			Assert.assertEquals(100, entry.getValue().size());			
		}

		for ( Level level : draft.getLevels() ){
			Set<String> categories = new HashSet<String>();
			for ( int i = 0 ; i < 50; i++ )
				categories.add("c " + level.getId() + "." + i);
			draft.addDraftCategories(level, categories);
		}
		
		EmployeesServiceHelper.calculate(connection,
				draft, agreement.getDomain(), null);
		SQLAgreementDraft.save(connection, draft, agreement.getDomain(), null);

		draft.clearDrafts();
		EmployeesServiceHelper.calculate(connection,
				draft, agreement.getDomain(), null);
	
		Assert.assertEquals(2, draft.getCategoriesMap().size());

		for( Map.Entry<Integer, Set<String>> entry: draft.getCategoriesMap().entrySet()) 
			for( String category: entry.getValue())
				System.out.println("2 :" + entry.getKey() + "-." +category);

		for( Map.Entry<Integer, Set<String>> entry: draft.getCategoriesMap().entrySet()){ 
			Assert.assertEquals(50, entry.getValue().size());			
		}
	}


	@Test
	public void testUpdateCategories() throws SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		AgreementRecord agreement = newAgreement(aonContext);
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext,
				agreement, "I", "Categoria 1");

		ContractRecord contract = newContract(aonContext, new String[] {},
				new String[] {}, category);

		AgreementDraft draft = new AgreementDraft();
		draft.setId(agreement.getId());

		EmployeesServiceHelper.calculate(connection,
				draft, agreement.getDomain(), null);

		Set<String> categories = new HashSet<String>();
		categories.add(category.getDescription());
		categories.add("Categoria 2");
		categories.add("Categoria 3");
		categories.add("Categoria 4");

		Level level = new Level();
		level.setId(category.getAgreementLevel());

		draft.addDraftLevel(level);
		draft.addDraftCategories(level, categories);

		SQLAgreementDraft.save(connection, draft, agreement.getDomain(), null);
	}

	@Test
	public void testRemoveExtras() throws SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date startDate = getFirstDayOfYear(getToday());

		AgreementRecord agreement = newAgreement(aonContext);
		AgreementPaymentRecord payment = addPayment(aonContext, agreement,
				startDate, new Payment() {
					{
						expression = "P_0";
					}
				});
		AgreementExtraRecord extra = addExtra(aonContext, payment, startDate,
				new Extra() {
					{
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";

					}
				});

		AgreementDraft draft = new AgreementDraft();
		draft.setId(agreement.getId());

		com.esferalia.aon.gwt.payroll.shared.Extra draftExtra = new com.esferalia.aon.gwt.payroll.shared.Extra();
		draftExtra.setId(extra.getId());
		draftExtra.setIssueDate("REMOVE()");
		draft.addDraftExtra(draftExtra);

		SQLAgreementDraft.save(connection, draft, agreement.getDomain(), null);
	}

	@Test
	public void testsPaymentsI() throws SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date startDate = getFirstDayOfYear(getToday());

		AgreementRecord agreement = newAgreement(aonContext);

		AgreementDraft draft = new AgreementDraft();
		draft.setId(agreement.getId());
		
		com.esferalia.aon.gwt.payroll.shared.Payment draftPayment = new com.esferalia.aon.gwt.payroll.shared.Payment();
		draftPayment.setId(-1);
		draftPayment.setType(Type.CRA_0001);
		draftPayment.setName("SALARIO_BASE");
		draftPayment.setDescription("SALARIO BASE");
		draftPayment.setExpression("1000.00 * DIAS_TRABAJADOS / DIAS_MES");
		draftPayment.setIrpfExpression("_P");
		draftPayment.setQuoteExpression("_P");
		draftPayment.setStartDate(startDate);
		draftPayment.setSalaryType(com.esferalia.aon.gwt.payroll.shared.Salary.Type.SALARY);
		draft.addDraftPayment(draftPayment);
		
		SQLAgreementDraft.save(connection, draft, agreement.getDomain(), null);
		
		Calendar epoch = Calendar.getInstance();
		epoch.setTimeInMillis(0);
		epoch.set(Calendar.HOUR_OF_DAY, 0);
		
		// insert new payment 
		AgreementPaymentRecord paymentRecords [] = getAgreementPayments(aonContext, agreement.getId());
		for ( AgreementPaymentRecord paymentRecord: paymentRecords ) {
			Assert.assertEquals( epoch.getTime(), paymentRecord.getStartDate());
			Assert.assertNull(paymentRecord.getEndDate());

			PaymentConceptRecord conceptRecord = getPaymentConcept(aonContext, paymentRecord.getPaymentConcept());
			Assert.assertEquals("SALARIO_BASE", conceptRecord.getCode());
			Assert.assertEquals("_P", conceptRecord.getIrpfExpression());
			Assert.assertEquals("_P", conceptRecord.getQuoteExpression());
			Assert.assertEquals("1000.00 * DIAS_TRABAJADOS / DIAS_MES", conceptRecord.getExpression());
			Assert.assertEquals("SALARIO BASE", conceptRecord.getDescription());
			
		}
		Assert.assertEquals(1, paymentRecords.length);
		
		// update this payment
		draft.removeDraftPaymet(draftPayment);
		draftPayment.setId(paymentRecords[0].getId());
		draftPayment.setExpression("666.00 * DIAS_TRABAJADOS / DIAS_MES");
		draftPayment.setDescription("SALARIO MENSUAL");
		draftPayment.setStartDate(getFirstDayOfMonth(getToday()));
		draftPayment.setStartDate(getLastDayOfMonth(getToday()));
		draft.addDraftPayment(draftPayment);
		
		SQLAgreementDraft.save(connection, draft, agreement.getDomain(), null);
		
		paymentRecords = getAgreementPayments(aonContext, agreement.getId());
		for ( AgreementPaymentRecord paymentRecord: paymentRecords ) {
			Assert.assertEquals( epoch.getTime(), paymentRecord.getStartDate());
			Assert.assertNull(paymentRecord.getEndDate());
			
			Assert.assertEquals("666.00 * DIAS_TRABAJADOS / DIAS_MES", paymentRecord.getExpression());
			Assert.assertEquals("SALARIO MENSUAL", paymentRecord.getDescription());
			
			PaymentConceptRecord conceptRecord = getPaymentConcept(aonContext, paymentRecord.getPaymentConcept());
			Assert.assertEquals("SALARIO_BASE", conceptRecord.getCode());
			Assert.assertEquals("_P", conceptRecord.getIrpfExpression());
			Assert.assertEquals("_P", conceptRecord.getQuoteExpression());
			//Assert.assertEquals("666.00 * DIAS_TRABAJADOS / DIAS_MES", conceptRecord.getExpression());
			//Assert.assertEquals("SALARIO MENSUAL", conceptRecord.getDescription());
			
		}
		Assert.assertEquals(1, paymentRecords.length);
		
		//draft.removeDraftPaymet(draftPayment);
		
		// Remove it
		draftPayment.setId(paymentRecords[0].getId());
		draftPayment.setExpression("REMOVE()");
		draftPayment.setStartDate(getFirstDayOfYear(getToday()));
		draftPayment.setStartDate(getLastDayOfMonth(getToday()));
		
		SQLAgreementDraft.save(connection, draft, agreement.getDomain(), null);
		paymentRecords = getAgreementPayments(aonContext, agreement.getId());
		Assert.assertEquals(0, paymentRecords.length);
		
	}

	@Test
	public void testsPaymentsII() throws SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date startDate = getFirstDayOfYear(getToday());

		DomainRecord domain = newDomain(aonContext);
		
		AgreementDraft draft = new AgreementDraft();
		draft.setId(-1);
		draft.setDescription("NEW AGREEMENT (" + System.currentTimeMillis() + ")");
		
		com.esferalia.aon.gwt.payroll.shared.Payment draftPayment = new com.esferalia.aon.gwt.payroll.shared.Payment();
		draftPayment.setId(-1);
		draftPayment.setType(Type.CRA_0001);
		draftPayment.setName("SALARIO_BASE");
		draftPayment.setDescription("SALARIO BASE");
		draftPayment.setExpression("1000.00 * DIAS_TRABAJADOS / DIAS_MES");
		draftPayment.setIrpfExpression("_P");
		draftPayment.setQuoteExpression("_P");
		draftPayment.setStartDate(startDate);
		draftPayment.setSalaryType(com.esferalia.aon.gwt.payroll.shared.Salary.Type.SALARY);
		draft.addDraftPayment(draftPayment);
		
		SQLAgreementDraft.save(connection, draft, domain.getId(), null);
		
		Calendar epoch = Calendar.getInstance();
		epoch.setTimeInMillis(0);
		epoch.set(Calendar.HOUR_OF_DAY, 0);
		
		AgreementRecord agreement = getAgreement(aonContext, draft.getDescription());
		
		// insert new payment 
		AgreementPaymentRecord paymentRecords [] = getAgreementPayments(aonContext, agreement.getId());
		for ( AgreementPaymentRecord paymentRecord: paymentRecords ) {
			Assert.assertEquals( epoch.getTime(), paymentRecord.getStartDate());
			Assert.assertNull(paymentRecord.getEndDate());

			PaymentConceptRecord conceptRecord = getPaymentConcept(aonContext, paymentRecord.getPaymentConcept());
			Assert.assertEquals("SALARIO_BASE", conceptRecord.getCode());
			Assert.assertEquals("_P", conceptRecord.getIrpfExpression());
			Assert.assertEquals("_P", conceptRecord.getQuoteExpression());
			Assert.assertEquals("1000.00 * DIAS_TRABAJADOS / DIAS_MES", conceptRecord.getExpression());
			Assert.assertEquals("SALARIO BASE", conceptRecord.getDescription());
			
		}
		Assert.assertEquals(1, paymentRecords.length);
		
	}
	

}
