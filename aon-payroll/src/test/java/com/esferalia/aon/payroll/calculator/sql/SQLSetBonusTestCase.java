package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.jooq.tables.records.ContractBonusRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.type.BonusType;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;


public class SQLSetBonusTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.006;
	
	@Test
	public void testSetBonusFromScratchI()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		Date startDate = getFirstDayOfYear(getToday());
		
		String naf = Integer.toString((int)(Math.random() * 1000000000.00));
		
		DomainRecord domain = newDomain(aonContext);
		ContractRecord contract = newContract(aonContext, domain, ccc, ContractCode.C100, "03", CCCType.PRINCIPAL, startDate, null, naf );
		
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		Date lastDayOfMonth = getLastDayOfMonth(firstDayOfMonth);
		
		Bonus bonus1 = 
		new Bonus()
		.setStartDate(firstDayOfMonth)
		.setEndDate(lastDayOfMonth)
		.setType(BonusType.SOCIAL_SECURITY)
		.setDescription("BONIFICACION INEM ( 100%)")
		.setExpression("/**//*read-only*/(CUOTA_EMPRESARIAL) * 100.00 / 100.00 /**/")
		;
		
		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1} );		
		ContractBonusRecord contractBonusRecords [] = getContractBonus(aonContext, contract);		
		assertEquals(1, contractBonusRecords.length);
		ContractBonusRecord contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);
				
		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {} );
		contractBonusRecords = getContractBonus(aonContext, contract);	
		assertEquals(0, contractBonusRecords.length);
		

		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1} );		
		contractBonusRecords = getContractBonus(aonContext, contract);	
		assertEquals(1, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);
		
		bonus1.setDescription("BONIFICACION INEM ( 60%)");
		bonus1.setExpression("/**//*read-only*/(CUOTA_EMPRESARIAL) * 60.00 / 100.00 /**/");
		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1} );		
		contractBonusRecords = getContractBonus(aonContext, contract);	
		assertEquals(1, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);
		
		Date _10DayOfMonth = add(firstDayOfMonth, Calendar.DAY_OF_MONTH, 9);
		bonus1.setEndDate(_10DayOfMonth);
		Date _11DayOfMonth = add(firstDayOfMonth, Calendar.DAY_OF_MONTH, 10);
		Bonus bonus2 = 
		new Bonus()
		.setStartDate(_11DayOfMonth)
		.setEndDate(lastDayOfMonth)
		.setType(BonusType.SOCIAL_SECURITY)
		.setDescription("BONIFICACION INEM ( 35%)")
		.setExpression("/**//*read-only*/(CUOTA_EMPRESARIAL) * 35.00 / 100.00 /**/")
		;
		
		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1, bonus2} );		
		contractBonusRecords = getContractBonus(aonContext, contract);		
		assertEquals(2, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);
		contractBonusRecord = contractBonusRecords[1];		
		assertBonusEquals(bonus2, contractBonusRecord);
		
		Date _20DayOfMonth = add(firstDayOfMonth, Calendar.DAY_OF_MONTH, 19);
		bonus2.setEndDate(_20DayOfMonth);
		Date _21DayOfMonth = add(firstDayOfMonth, Calendar.DAY_OF_MONTH, 20);
		Bonus bonus3 = 
		new Bonus()
		.setStartDate(_21DayOfMonth)
		.setEndDate(lastDayOfMonth)
		.setType(BonusType.SOCIAL_SECURITY)
		.setDescription("BONIFICACION INEM ( 15%)")
		.setExpression("/**//*read-only*/(CUOTA_EMPRESARIAL) * 15.00 / 100.00 /**/")
		;
		
		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1, bonus2, bonus3} );		
		contractBonusRecords = getContractBonus(aonContext, contract);		
		assertEquals(3, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);
		contractBonusRecord = contractBonusRecords[1];		
		assertBonusEquals(bonus2, contractBonusRecord);
		contractBonusRecord = contractBonusRecords[2];		
		assertBonusEquals(bonus3, contractBonusRecord);
		
		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1, bonus2} );		
		contractBonusRecords = getContractBonus(aonContext, contract);		
		assertEquals(2, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);
		contractBonusRecord = contractBonusRecords[1];		
		assertBonusEquals(bonus2, contractBonusRecord);

		bonus1.setDescription("BONIFICACION INEM ( 60%)");
		bonus1.setExpression("/**//*read-only*/(CUOTA_EMPRESARIAL) * 60.00 / 100.00 /**/");
		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1} );		
		contractBonusRecords = getContractBonus(aonContext, contract);	
		assertEquals(1, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);

		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {} );
		contractBonusRecords = getContractBonus(aonContext, contract);	
		assertEquals(0, contractBonusRecords.length);

	}

	@Test
	public void testSetBonusFromScratchII()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		Date startDate = getFirstDayOfYear(getToday());
		
		String naf = Integer.toString((int)(Math.random() * 1000000000.00));
		
		DomainRecord domain = newDomain(aonContext);
		ContractRecord contract = newContract(aonContext, domain, ccc, ContractCode.C100, "03", CCCType.PRINCIPAL, startDate, null, naf );
		
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		Date lastDayOfMonth = null;
		
		Bonus bonus1 = 
		new Bonus()
		.setStartDate(firstDayOfMonth)
		.setEndDate(lastDayOfMonth)
		.setType(BonusType.SOCIAL_SECURITY)
		.setDescription("BONIFICACION INEM ( 100%)")
		.setExpression("/**//*read-only*/(CUOTA_EMPRESARIAL) * 100.00 / 100.00 /**/")
		;
		
		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1} );		
		ContractBonusRecord contractBonusRecords [] = getContractBonus(aonContext, contract);		
		assertEquals(1, contractBonusRecords.length);
		ContractBonusRecord contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);
				
		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1} );		
		contractBonusRecords = getContractBonus(aonContext, contract);	
		assertEquals(1, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);

		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {} );
		contractBonusRecords = getContractBonus(aonContext, contract);	
		assertEquals(0, contractBonusRecords.length);
		

		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1} );		
		contractBonusRecords = getContractBonus(aonContext, contract);	
		assertEquals(1, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);
		
		bonus1.setDescription("BONIFICACION INEM ( 60%)");
		bonus1.setExpression("/**//*read-only*/(CUOTA_EMPRESARIAL) * 60.00 / 100.00 /**/");
		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1} );		
		contractBonusRecords = getContractBonus(aonContext, contract);	
		assertEquals(1, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);
		
		Date _10DayOfMonth = add(firstDayOfMonth, Calendar.DAY_OF_MONTH, 9);
		bonus1.setEndDate(_10DayOfMonth);
		Date _11DayOfMonth = add(firstDayOfMonth, Calendar.DAY_OF_MONTH, 10);
		Bonus bonus2 = 
		new Bonus()
		.setStartDate(_11DayOfMonth)
		.setEndDate(lastDayOfMonth)
		.setType(BonusType.SOCIAL_SECURITY)
		.setDescription("BONIFICACION INEM ( 35%)")
		.setExpression("/**//*read-only*/(CUOTA_EMPRESARIAL) * 35.00 / 100.00 /**/")
		;
		
		for ( int i = 0 ; i < 3; i++) {
			PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1, bonus2} );		
			contractBonusRecords = getContractBonus(aonContext, contract);		
			assertEquals(2, contractBonusRecords.length);
			contractBonusRecord = contractBonusRecords[0];		
			assertBonusEquals(bonus1, contractBonusRecord);
			contractBonusRecord = contractBonusRecords[1];		
			assertBonusEquals(bonus2, contractBonusRecord);
		}
		

		Date _20DayOfMonth = add(firstDayOfMonth, Calendar.DAY_OF_MONTH, 19);
		bonus2.setEndDate(_20DayOfMonth);
		Date _21DayOfMonth = add(firstDayOfMonth, Calendar.DAY_OF_MONTH, 20);
		Bonus bonus3 = 
		new Bonus()
		.setStartDate(_21DayOfMonth)
		.setEndDate(lastDayOfMonth)
		.setType(BonusType.SOCIAL_SECURITY)
		.setDescription("BONIFICACION INEM ( 15%)")
		.setExpression("/**//*read-only*/(CUOTA_EMPRESARIAL) * 15.00 / 100.00 /**/")
		;
		
		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1, bonus2, bonus3} );		
		contractBonusRecords = getContractBonus(aonContext, contract);		
		assertEquals(3, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);
		contractBonusRecord = contractBonusRecords[1];		
		assertBonusEquals(bonus2, contractBonusRecord);
		contractBonusRecord = contractBonusRecords[2];		
		assertBonusEquals(bonus3, contractBonusRecord);
		
		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1, bonus2} );		
		contractBonusRecords = getContractBonus(aonContext, contract);		
		assertEquals(2, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);
		contractBonusRecord = contractBonusRecords[1];		
		assertBonusEquals(bonus2, contractBonusRecord);

		bonus1.setDescription("BONIFICACION INEM ( 60%)");
		bonus1.setExpression("/**//*read-only*/(CUOTA_EMPRESARIAL) * 60.00 / 100.00 /**/");
		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1} );		
		contractBonusRecords = getContractBonus(aonContext, contract);	
		assertEquals(1, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);

		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {} );
		contractBonusRecords = getContractBonus(aonContext, contract);	
		assertEquals(0, contractBonusRecords.length);

		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1, bonus2, bonus3} );		
		contractBonusRecords = getContractBonus(aonContext, contract);		
		assertEquals(3, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);
		contractBonusRecord = contractBonusRecords[1];		
		assertBonusEquals(bonus2, contractBonusRecord);
		contractBonusRecord = contractBonusRecords[2];		
		assertBonusEquals(bonus3, contractBonusRecord);
		
		for ( Bonus bonus : new Bonus [] {bonus1, bonus2, bonus3} )
			bonus.setStartDate(AonDateUtils.add(bonus.getStartDate(), Calendar.MONTH,1));
		
		for ( Bonus bonus : new Bonus [] {bonus1, bonus2 } )
			bonus.setEndDate(AonDateUtils.add(bonus.getEndDate(), Calendar.MONTH,1));
		
		firstDayOfMonth = AonDateUtils.add(firstDayOfMonth, Calendar.MONTH,1);

		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1, bonus2, bonus3} );		

		contractBonusRecords = getContractBonus(aonContext, contract);		
		assertEquals(6, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[3];		
		assertBonusEquals(bonus1, contractBonusRecord);
		contractBonusRecord = contractBonusRecords[4];		
		assertBonusEquals(bonus2, contractBonusRecord);
		contractBonusRecord = contractBonusRecords[5];		
		assertBonusEquals(bonus3, contractBonusRecord);
	}

	@Test
	@Disabled("Not real case")
	public void testSetBonusFromScratchIII()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		Date startDate = getFirstDayOfYear(getToday());
		
		String naf = Integer.toString((int)(Math.random() * 1000000000.00));
		
		DomainRecord domain = newDomain(aonContext);
		ContractRecord contract = newContract(aonContext, domain, ccc, ContractCode.C100, "03", CCCType.PRINCIPAL, startDate, null, naf );
		
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		Date lastDayOfMonth = null;
		
		Bonus bonus1 = 
		new Bonus()
		.setStartDate(firstDayOfMonth)
		.setEndDate(lastDayOfMonth)
		.setType(BonusType.SOCIAL_SECURITY)
		.setDescription("BONIFICACION INEM ( 100%)")
		.setExpression("/**//*read-only*/(CUOTA_EMPRESARIAL) * 100.00 / 100.00 /**/")
		;
		
		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus1} );		
		ContractBonusRecord contractBonusRecords [] = getContractBonus(aonContext, contract);		
		assertEquals(1, contractBonusRecords.length);
		ContractBonusRecord contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);
		
		firstDayOfMonth = AonDateUtils.add(firstDayOfMonth, Calendar.MONTH,1);		
		Bonus bonus2 = 
		new Bonus()
		.setStartDate(firstDayOfMonth)
		.setEndDate(lastDayOfMonth)
		.setType(BonusType.SOCIAL_SECURITY)
		.setDescription("BONIFICACION INEM ( 100%)")
		.setExpression("/**//*read-only*/(CUOTA_EMPRESARIAL) * 100.00 / 100.00 /**/")
		;

		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus2} );		
		contractBonusRecords= getContractBonus(aonContext, contract);		
		assertEquals(2, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);

		PAYROLL.setBonuses(domain.getName(), domain.getId(), "login", ccc, naf, firstDayOfMonth, lastDayOfMonth, new Bonus [] {bonus2} );		
		contractBonusRecords= getContractBonus(aonContext, contract);		
		assertEquals(2, contractBonusRecords.length);
		contractBonusRecord = contractBonusRecords[0];		
		assertBonusEquals(bonus1, contractBonusRecord);
	}

	private void assertBonusEquals(Bonus bonus1, ContractBonusRecord contractBonusRecord1) {
		assertEquals(bonus1.getStartDate(), contractBonusRecord1.getStartDate());
		assertEquals(bonus1.getEndDate(), contractBonusRecord1.getEndDate());
		assertEquals(bonus1.getExpression(), contractBonusRecord1.getExpression());
		assertEquals(bonus1.getDescription(), contractBonusRecord1.getDescription());
	}
	
	protected ContractBonusRecord [] getContractBonus(AONContext aonContext, ContractRecord contractRecord) {
		return
		aonContext
		.getDslContext()
		.select()
		.from(CONTRACT_BONUS)
		.where(CONTRACT_BONUS.CONTRACT.eq(contractRecord.getId()))
		.orderBy(CONTRACT_BONUS.START_DATE)
		.fetchStreamInto(CONTRACT_BONUS).toArray(ContractBonusRecord[]::new)
		
		;
	}
	
	protected static ContractRecord newContract(AONContext aonContext, DomainRecord domain, String ccc, ContractCode contractCode, String quoteGroup, CCCType cccType, Date startDate, Date endDate, String naf ) {
		
		
		ScopeRecord scope = newScope(aonContext, domain.getId());

		EnterpriseActivityRecord enterpriseActivity = newEnterpriseActivity(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				SSRegimeType.GENERAL);

		EnterpriseCccRecord enterpriseCcc = newEnterpriseCcc(aonContext, 
				domain.getId(), 
				scope.getId(), 
				enterpriseActivity.getId(), 
				cccType,
				ccc );

		WorkplaceRecord workplace = newWorkplace(aonContext, 
				domain.getId(), 
				scope.getId(), 
				enterpriseActivity.getEnterprise());

		RegistryRecord person = nevvPerson(
				aonContext, 
				domain.getId(),
				naf 
				);


		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				SSRegimeType.GENERAL, 
				cccType,			
				startDate, //getFirstDayOfYear(getToday()),
				endDate,
				Collections.emptyMap(),
				new String[] {}, 
				new String[] {},
				null,						//category
				domain.getId(), 			//domainId, 
				person.getId(),				//personId, 
				workplace.getId(),			//workplaceId, 
				enterpriseCcc.getId(),		//enterpriseCccId,
				enterpriseActivity.getId()	//enterpriseActivityId
				);
		
		return contract;
	}
	
	protected static final RegistryRecord nevvPerson(AONContext aonContext, int domainId, String naf) {
		RegistryRecord person = aonContext.getDslContext()
				.insertInto(REGISTRY)
				.set(REGISTRY.DOMAIN, domainId)
				.set(REGISTRY.NAME, "")
				.set(REGISTRY.ALIAS, "")
				.set(REGISTRY.DOCUMENT, "")
				.set(REGISTRY.DOCUMENT_COUNTRY, "")
				.set(REGISTRY.DOCUMENT_TYPE, (byte) DocumentType.OTHER.ordinal())
				.set(REGISTRY.NATIONALITY, "")
				.set(REGISTRY.TYPE, (byte) RegistryType.NATURAL.ordinal()).returning()
				.fetchOne();

		aonContext.getDslContext()
			.insertInto(PERSON)
			.set(PERSON.DOMAIN, domainId)
			.set(PERSON.REGISTRY, person.getId())
			.set(PERSON.NAME, "")
			.set(PERSON.FIRST_SURNAME, "")
			.set(PERSON.SECOND_SURNAME, "")
			.set(PERSON.SOCIAL_SECURITY_NUM, naf)
			.set(PERSON.GENDER, (byte) Gender.UNKNOWN.ordinal())
			.set(PERSON.MARITAL_STATUS, (byte) MaritalStatus.UNKNOWN.ordinal())
			.execute();
		return person;
	}	

}
