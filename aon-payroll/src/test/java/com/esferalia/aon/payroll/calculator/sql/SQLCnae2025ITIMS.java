package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Cnae.CNAE;
import static com.esferalia.aon.jooq.tables.Cnae2025Rate.CNAE2025_RATE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collections;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.jooq.tables.records.Cnae2025RateRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionException;


public class SQLCnae2025ITIMS extends AbstractSQLTestCase {

	private static final double DELTA = 0.006;
	

	@Test
	public void testCnae2025ITIMS()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		Date startDate = getFirstDayOfYear(getToday());
		
		String naf = Integer.toString((int)(Math.random() * 1000000000.00));
		
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL, startDate, "IT", DeductionType.PROFESSIONAL_CONTINGENCY, "TARIFA_IT" );
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL, startDate, "IMS", DeductionType.PROFESSIONAL_CONTINGENCY, "TARIFA_IMS" );
		
		aonContext.getDslContext()
		.select(CNAE.CODE).from(CNAE)
		.forEach(cnae2025Code -> {
			DomainRecord domain = newDomain(aonContext);
			ContractRecord contract = newContract(aonContext, domain, ccc, ContractCode.C100, "03", CCCType.PRINCIPAL, startDate, null, naf,  cnae2025Code.get(CNAE.CODE) );
			try {
				Cnae2025RateRecord cnae2025Rate =
				aonContext.getDslContext().select().from(CNAE2025_RATE).where(CNAE2025_RATE.CODE.eq(cnae2025Code.get(CNAE.CODE))).fetchOptionalInto(CNAE2025_RATE)
				.orElseGet(() -> aonContext.getDslContext().select().from(CNAE2025_RATE).where(CNAE2025_RATE.CODE.eq(cnae2025Code.get(CNAE.CODE).substring(0, 3))).fetchOptionalInto(CNAE2025_RATE)
						.orElseGet(() -> aonContext.getDslContext().select().from(CNAE2025_RATE).where(CNAE2025_RATE.CODE.eq(cnae2025Code.get(CNAE.CODE).substring(0, 2))).fetchOptionalInto(CNAE2025_RATE)
								.orElseThrow(() -> new RuntimeException("CNAE2025 IT & IMS code not found: " + cnae2025Code.get(CNAE.CODE)))));
				
				Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, startDate, startDate, contract));
				
				System.out.println(cnae2025Code.get(CNAE.CODE) + " : " + cnae2025Rate.get(CNAE2025_RATE.CODE) + " - " + cnae2025Rate.get(CNAE2025_RATE.IT_AMOUNT) + " - " + cnae2025Rate.get(CNAE2025_RATE.IMS_AMOUNT));
				
				salary.getSalaryCosts().forEach(salaryCost -> {
					System.out.println("  " + salaryCost.getCostConcept() + " : " + salaryCost.getExpression());
				});
				
				salary.getSalaryCosts().stream().filter(cost -> cost.getCostConcept().equals("IT")).forEach(cost -> {
					assertEquals(cnae2025Rate.get(CNAE2025_RATE.IT_AMOUNT) , Double.parseDouble(cost.getExpression()), DELTA);
				});
				
				salary.getSalaryCosts().stream().filter(cost -> cost.getCostConcept().equals("IMS")).forEach(cost -> {
					assertEquals(cnae2025Rate.get(CNAE2025_RATE.IMS_AMOUNT) , Double.parseDouble(cost.getExpression()), DELTA);
				});
				
			} catch (Exception e) {
				if ( cnae2025Code.get(CNAE.CODE).startsWith("98") )
					System.err.println("Expected error for CNAE2025 code: " + cnae2025Code.get(CNAE.CODE) + " - " + e.getMessage());
				else 
					fail("Error calculating salary for CNAE2025 code: " + cnae2025Code.get(CNAE.CODE) + " - " + e.getMessage());
			}
		});
		
		
		
	}

	
	protected static ContractRecord newContract(AONContext aonContext, DomainRecord domain, String ccc, ContractCode contractCode, String quoteGroup, CCCType cccType, Date startDate, Date endDate, String naf, String cnae2025 ) {
		
		
		ScopeRecord scope = newScope(aonContext, domain.getId());

		EnterpriseActivityRecord enterpriseActivity = newEnterpriseActivity(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				SSRegimeType.GENERAL,
				cnae2025
				);

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
