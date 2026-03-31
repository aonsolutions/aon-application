package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.jooq.Result;
import org.junit.jupiter.api.Test;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.gwt.payroll.jooq.JooqAgreement;
import com.esferalia.aon.jooq.tables.records.AgreementDataRecord;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelDataRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

import static org.junit.jupiter.api.Assertions.*;

public class SQLTrashTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.0001;

	@Test
	public void testDraftAgreement() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		AgreementRecord agreement = newAgreement(aonContext);
		
		List<AgreementLevelCategoryRecord> categories = new ArrayList<AgreementLevelCategoryRecord>();
		for ( int level = 1; level <= 10; level++ )
			for ( int category = 1; category <= 10; category++ )
				categories.add(newAgreementCategory(aonContext, agreement, String.format("%d", level) , String.format("%d.%d", level, category)));

		PaymentConceptRecord conceptPlus = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPaga = addConcept(aonContext, "PAGA");
		PaymentConceptRecord conceptAntiguedad = addConcept(aonContext, "ANTIGUEDAD");

		addPayments(aonContext, agreement, getFirstDayOfYear(getToday()), 
		new Payment [] {
			new Payment(){
				{
					this.concept = conceptBase.getId();
					this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS / DIAS_MES";
				}
			},
			new Payment(){
				{
					this.concept = conceptPlus.getId();
					this.expression = "PLUS * DIAS_TRABAJADOS / DIAS_MES";
				}
			}
		});
		
		
		Date startDate = getFirstDayOfYear(getToday());

		addExtras(aonContext, agreement, startDate, 	
		new Extra[] { new Extra() {
			{
				this.expression = "SALARIO_BASE + PLUS_SALARIAL";
				this.month = Month.DECEMBER;
				this.start = "01/01";
				this.end = "31/12";
				this.issue = "15/12";
			}
		}, new Extra() {
			{
				this.expression = "SALARIO_BASE + PLUS_SALARIAL";
				this.month = Month.JULY;
				this.start = "01/07 -1";
				this.end = "30/06";
				this.issue = "01/07";
			}
		}, });
		
		addData(aonContext, agreement, startDate, new HashMap<String,String>(){
			{
				put("SALARIO_MENSUAL", String.format("%d", 99 ));
				put("PLUS", String.format("%d", 9 ));
			}});

		for ( AgreementLevelCategoryRecord category: categories ) {
			addData(aonContext, category, startDate, null, new HashMap<String,String>(){
				{
					put("SALARIO_MENSUAL", String.format("%d", category.getId() * 100 ));
					put("PLUS", String.format("%d", category.getId() * 10 ));
				}
			});
		}
		
		ContractRecord contract = newContract(aonContext,  getFirstDayOfYear(getToday()), Collections.emptyMap(), categories.get(categories.size()-1));
		
		Date firstdayOfMonth = getFirstDayOfMonth(getToday());
		Date lastdayOfMonth = getLastDayOfMonth(getToday());
		
		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, firstdayOfMonth , lastdayOfMonth, lastdayOfMonth, contract));
		
		// --> trash 
		trashOrRestore(connection, aonContext, agreement, true );
		
		Salary trashSalary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, firstdayOfMonth , lastdayOfMonth, lastdayOfMonth, contract));
		assertEquals(salary.getTotalLiquid(), trashSalary.getTotalLiquid());
		
		AgreementRecord trashAgreement = aonContext.getDslContext().select().from(AGREEMENT).where(AGREEMENT.ID.eq(agreement.getId()*-1)).fetchOneInto(AGREEMENT);
		// <-- trash
		trashOrRestore(connection, aonContext, trashAgreement, false );

		Salary restoreSalary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, firstdayOfMonth , lastdayOfMonth, lastdayOfMonth, contract));
		assertEquals(salary.getTotalLiquid(), restoreSalary.getTotalLiquid());
		
//		agreement = aonContext.getDslContext().select().from(AGREEMENT).where(AGREEMENT.ID.eq(agreement.getId())).fetchOneInto(AGREEMENT);
//		trashAgreement = aonContext.getDslContext().select().from(AGREEMENT).where(AGREEMENT.ID.eq(agreement.getId()*-1)).fetchOneInto(AGREEMENT);
//		
//		assertEquals((int)agreement.getId(), (int)(trashAgreement.getId() * -1));
//		assertEquals(agreement.getDescription(), trashAgreement.getDescription());
//		
//		contract.setAgreementLevel(contract.getAgreementLevel()*-1);
//		contract.update();
//		trashSalary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, firstdayOfMonth , lastdayOfMonth, lastdayOfMonth, contract));
//		assertEquals(restoreSalary.getTotalLiquid(), trashSalary.getTotalLiquid());
//		
//		
//		addPayment(aonContext, agreement, startDate, new Payment() {
//			{
//				this.concept = conceptAntiguedad.getId();
//				this.expression = "( SALARIO_BASE + PLUS_SALARIAL ) * 0.10";
//			}
//		});
//		
//		contract.setAgreementLevel(contract.getAgreementLevel()*-1);
//		contract.update();
//		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, firstdayOfMonth , lastdayOfMonth, lastdayOfMonth, contract));
//		assertEquals(salary.getTotalLiquid() , trashSalary.getTotalLiquid() * 1.10 , DELTA);
//		System.out.println(contract.getAgreementLevel() + "-. " + salary.getTotalLiquid());
//		
//		// <-- trash 
//		trashOrRestore(connection, aonContext, trashAgreement, true );
//		Salary unTrashSalary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, firstdayOfMonth , lastdayOfMonth, lastdayOfMonth, contract));
//		System.out.println(contract.getAgreementLevel() + "-. " + unTrashSalary.getTotalLiquid());
//		assertEquals(trashSalary.getTotalLiquid(), unTrashSalary.getTotalLiquid(), DELTA);
//
//		contract.setAgreementLevel(contract.getAgreementLevel()*-1);
//		contract.update();
//		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, firstdayOfMonth , lastdayOfMonth, lastdayOfMonth, contract));
//		assertEquals(salary.getTotalLiquid(), unTrashSalary.getTotalLiquid() * 1.10, DELTA);
		
		
		
	}

	// ------------------------------------------------------------------------
	private void trashOrRestore(Connection connection, AONContext aonContext, AgreementRecord agreement, boolean delete ) {
		AgreementRecord agreementRecord = aonContext.getDslContext().select().from(AGREEMENT).where(AGREEMENT.ID.eq(agreement.getId())).fetchOneInto(AGREEMENT);
		Result<AgreementDataRecord> datasRecords = aonContext.getDslContext().select().from(AGREEMENT_DATA).where(AGREEMENT_DATA.AGREEMENT.eq(agreement.getId())).fetchInto(AGREEMENT_DATA);
		Result<AgreementLevelRecord> levelsRecords = aonContext.getDslContext().select().from(AGREEMENT_LEVEL).where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId())).fetchInto(AGREEMENT_LEVEL);
		Result<AgreementPaymentRecord> paymentsRecords = aonContext.getDslContext().select().from(AGREEMENT_PAYMENT).where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId())).fetchInto(AGREEMENT_PAYMENT);
		Result<AgreementExtraRecord> extrasRecords = aonContext.getDslContext().select().from(AGREEMENT_EXTRA).where(AGREEMENT_EXTRA.AGREEMENT.eq(agreement.getId())).fetchInto(AGREEMENT_EXTRA);
		
		Result<AgreementLevelCategoryRecord> categoriesRecords = aonContext.getDslContext().select().from(AGREEMENT_LEVEL_CATEGORY).where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.in(levelsRecords.map( l->l.getId()))).fetchInto(AGREEMENT_LEVEL_CATEGORY);
		Result<AgreementLevelDataRecord> levelDatasRecords = aonContext.getDslContext().select().from(AGREEMENT_LEVEL_DATA).where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(levelsRecords.map( l->l.getId()))).fetchInto(AGREEMENT_LEVEL_DATA);
		
		// -> trash 
		JooqAgreement.trashRestoreAgreement(connection, agreement.getId(), delete);
		
		AgreementRecord trashAgreementRecord = aonContext.getDslContext().select().from(AGREEMENT).where(AGREEMENT.ID.eq(agreementRecord.getId()*-1)).fetchOneInto(AGREEMENT);
		assertEquals( agreementRecord.getDomain(), trashAgreementRecord.getDomain() );
		assertEquals( agreementRecord.getDescription(), trashAgreementRecord.getDescription() );
		
		paymentsRecords.forEach( paymentRecord -> {
			AgreementPaymentRecord trashRecord = aonContext.getDslContext()
					.select()
					.from(AGREEMENT_PAYMENT)
					.where(AGREEMENT_PAYMENT.ID.eq(paymentRecord.getId()*-1))
					.and(AGREEMENT_PAYMENT.AGREEMENT.eq(agreementRecord.getId()*-1))
					.fetchOneInto(AGREEMENT_PAYMENT);
			assertEquals( paymentRecord.getDomain(), trashRecord.getDomain() );
			assertEquals( paymentRecord.getEndDate(), trashRecord.getEndDate() );
			assertEquals( paymentRecord.getStartDate(), trashRecord.getStartDate() );
			assertEquals( paymentRecord.getType(), trashRecord.getType() );
			assertEquals( paymentRecord.getMonth(), trashRecord.getMonth() );
			assertEquals( paymentRecord.getExpression(), trashRecord.getExpression() );
			assertEquals( paymentRecord.getDescription(), trashRecord.getDescription() );
			assertEquals( paymentRecord.getIrpfExpression(), trashRecord.getIrpfExpression() );
			assertEquals( paymentRecord.getQuoteExpression(), trashRecord.getQuoteExpression() );
			assertEquals( paymentRecord.getPaymentConcept(), trashRecord.getPaymentConcept() );
		});
		
		extrasRecords.forEach( extraRecord -> {
			AgreementExtraRecord trashRecord = aonContext.getDslContext()
					.select()
					.from(AGREEMENT_EXTRA)
					.where(AGREEMENT_EXTRA.ID.eq(extraRecord.getId()*-1))
					.and(AGREEMENT_EXTRA.AGREEMENT.eq(agreementRecord.getId()*-1))
					.fetchOneInto(AGREEMENT_EXTRA);
			assertEquals( extraRecord.getDomain(), trashRecord.getDomain() );
			assertEquals( extraRecord.getEndDate(), trashRecord.getEndDate() );
			assertEquals( extraRecord.getStartDate(), trashRecord.getStartDate() );
			assertEquals( extraRecord.getIssueDate(), trashRecord.getIssueDate() );
			assertEquals( (int)(extraRecord.getAgreementPayment()*-1), (int)trashRecord.getAgreementPayment() );
		});

		datasRecords.forEach( dataRecord -> {
			AgreementDataRecord trashRecord = aonContext.getDslContext()
					.select()
					.from(AGREEMENT_DATA)
					.where(AGREEMENT_DATA.ID.eq(dataRecord.getId()*-1))
					.and(AGREEMENT_DATA.AGREEMENT.eq(agreementRecord.getId()*-1))
					.fetchOneInto(AGREEMENT_DATA);
			assertEquals( dataRecord.getDomain(), trashRecord.getDomain() );
			assertEquals( dataRecord.getName(), trashRecord.getName() );
			assertEquals( dataRecord.getExpression(), trashRecord.getExpression() );
		});

		levelsRecords.forEach( levelRecord -> {
			AgreementLevelRecord trashRecord = aonContext.getDslContext()
					.select()
					.from(AGREEMENT_LEVEL)
					.where(AGREEMENT_LEVEL.ID.eq(levelRecord.getId()*-1))
					.and(AGREEMENT_LEVEL.AGREEMENT.eq(agreementRecord.getId()*-1))
					.fetchOneInto(AGREEMENT_LEVEL);
			assertEquals( levelRecord.getDomain(), trashRecord.getDomain() );
			assertEquals( levelRecord.getDescription(), trashRecord.getDescription() );
		});
		
		categoriesRecords.forEach( categoryRecord -> {
			AgreementLevelCategoryRecord trashRecord = aonContext.getDslContext()
					.select()
					.from(AGREEMENT_LEVEL_CATEGORY)
					.where(AGREEMENT_LEVEL_CATEGORY.ID.eq(categoryRecord.getId()*-1))
					.fetchOneInto(AGREEMENT_LEVEL_CATEGORY);
			assertEquals( categoryRecord.getDomain(), trashRecord.getDomain() );
			assertEquals( categoryRecord.getDescription(), trashRecord.getDescription() );
		});
		
		levelDatasRecords.forEach( levelDataRecord -> {
			AgreementLevelDataRecord trashRecord = aonContext.getDslContext()
					.select()
					.from(AGREEMENT_LEVEL_DATA)
					.where(AGREEMENT_LEVEL_DATA.ID.eq(levelDataRecord.getId()*-1))
					.fetchOneInto(AGREEMENT_LEVEL_DATA);
			assertEquals( levelDataRecord.getDomain(), trashRecord.getDomain() );
			assertEquals( levelDataRecord.getName(), trashRecord.getName() );
			assertEquals( levelDataRecord.getExpression(), trashRecord.getExpression() );
		});
	}


}
