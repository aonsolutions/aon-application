package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IRPF_PERCENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.watson.util.AonDateUtils.get;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Ignore;
import org.junit.Test;

import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.gwt.payroll.server.EmployeesServiceImpl;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SmartSQLContractSettleCalculatorContext;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
@Ignore
public class SQLCostTestCase extends AbstractSQLTestCase {
	
	private static final Faker FAKER = new Faker();
	
	@Test
	public void testGetCostReceiptPDFWorkplace() throws ExpressionException, SQLException, SalaryException, IOException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(getToday());
		Date lastDayOfMonth = AonDateUtils.getLastDayOfMonth(firstDayOfMonth);
		
		DomainRecord domain = newDomain(aonContext);
		
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
				CCCType.TRAINING,
				Long.toString(System.currentTimeMillis()).substring(0, 11) );

		WorkplaceRecord workplace = newWorkplace(aonContext, 
				domain.getId(), 
				scope.getId(), 
				enterpriseActivity.getEnterprise());

		for (int i = 0; i < 12; i++) {
			
			newSalary(connection, 
					firstDayOfMonth, 
					lastDayOfMonth, 
					enterpriseCcc, 
					workplace);
		}

		Cost cost = new Cost();
		cost.setWorkplaceId(workplace.getId());
		cost.setYear(get(firstDayOfMonth, Calendar.YEAR));
		cost.setMonth(get(firstDayOfMonth, Calendar.MONTH));
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		EmployeesServiceImpl.printCostReceiptPDF(domain.getName(), cost, new Salary.Type[] {Salary.Type.SALARY} , os);
		
		byte data [] = os.toByteArray();
		PDDocument pddDocument = PDDocument.load(data);
		PDFTextStripper pdfTextStripper = new PDFTextStripper();
		pdfTextStripper.setSortByPosition(true);
		String text = pdfTextStripper.getText(pddDocument);
		
		System.out.println(text);
		
		File file = File.createTempFile("testGetCostReceiptPDF", "pdf");
		pddDocument.save(file);
		System.out.println(file);
	}

	@Test
	public void testGetCostReceiptPDFEnterprise() throws ExpressionException, SQLException, SalaryException, IOException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(getToday());
		Date lastDayOfMonth = AonDateUtils.getLastDayOfMonth(firstDayOfMonth);
		
		DomainRecord domain = newDomain(aonContext);
		
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
				CCCType.TRAINING,
				Long.toString(System.currentTimeMillis()).substring(0, 11) );
		
		for (int j = 0; j < 10; j++ ) {
			WorkplaceRecord workplace = newWorkplace(aonContext, 
					domain.getId(), 
					scope.getId(), 
					enterpriseActivity.getEnterprise());
			
			workplace.setDescription(FAKER.gameOfThrones().city());
			workplace.update();
	
			for (int i = 0; i < FAKER.number().numberBetween(4, 10); i++) {
				
				newSalary(connection, 
						firstDayOfMonth, 
						lastDayOfMonth, 
						enterpriseCcc, 
						workplace);
			}
		}


		Cost cost = new Cost();
		cost.setYear(get(firstDayOfMonth, Calendar.YEAR));
		cost.setMonth(get(firstDayOfMonth, Calendar.MONTH));
		cost.setEnterpriseId(enterpriseActivity.getEnterprise());
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		EmployeesServiceImpl.printCostReceiptPDF(domain.getName(), cost, new Salary.Type[] {Salary.Type.SALARY} , os);
		
		byte data [] = os.toByteArray();
		PDDocument pddDocument = PDDocument.load(data);
		PDFTextStripper pdfTextStripper = new PDFTextStripper();
		pdfTextStripper.setSortByPosition(true);
		String text = pdfTextStripper.getText(pddDocument);
		
		System.out.println(text);
		
		File file = File.createTempFile("testGetCostReceiptPDF", "pdf");
		pddDocument.save(file);
		System.out.println(file);
	}

	@Test
	public void testGetCostReceiptPDFDuplicates() throws ExpressionException, SQLException, SalaryException, IOException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(getToday());
		Date lastDayOfMonth = AonDateUtils.getLastDayOfMonth(firstDayOfMonth);
		
		DomainRecord domain = newDomain(aonContext);
		
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
				CCCType.TRAINING,
				Long.toString(System.currentTimeMillis()).substring(0, 11) );

		WorkplaceRecord workplace = newWorkplace(aonContext, 
				domain.getId(), 
				scope.getId(), 
				enterpriseActivity.getEnterprise());

		for (int i = 0; i < 12; i++) {
			
			Date date = new java.sql.Date(FAKER.date().between(firstDayOfMonth, lastDayOfMonth).getTime());
			
			ContractRecord contract = 
					newSalary(connection, 
					firstDayOfMonth, 
					date, 
					enterpriseCcc, 
					workplace);
			newSalary(connection, 
					AonDateUtils.add(date, Calendar.DAY_OF_MONTH, 1), 
					lastDayOfMonth, 
					contract);
		}

		Cost cost = new Cost();
		cost.setWorkplaceId(workplace.getId());
		cost.setYear(get(firstDayOfMonth, Calendar.YEAR));
		cost.setMonth(get(firstDayOfMonth, Calendar.MONTH));
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		EmployeesServiceImpl.printCostReceiptPDF(domain.getName(), cost, new Salary.Type[] {Salary.Type.SALARY} , os);
		
		byte data [] = os.toByteArray();
		PDDocument pddDocument = PDDocument.load(data);
		PDFTextStripper pdfTextStripper = new PDFTextStripper();
		pdfTextStripper.setSortByPosition(true);
		String text = pdfTextStripper.getText(pddDocument);
		
		System.out.println(text);
		
		File file = File.createTempFile("testGetCostReceiptPDF", "pdf");
		pddDocument.save(file);
		System.out.println(file);
	}

	private ContractRecord newSalary(Connection connection, 
			Date startDate, 
			Date endDate,
			EnterpriseCccRecord enterpriseCcc,
			WorkplaceRecord workplace) throws ExpressionException, SQLException, SalaryException {
		
		AONContext aonContext = new AONContext(connection);

		RegistryRecord person = newPerson(aonContext, workplace.getDomain());
		
		
		
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				SSRegimeType.GENERAL, 
				CCCType.TRAINING,			
				startDate,
				endDate,
				new HashMap<String, String>() {
					{
						put(QUOTE_GROUP.getName(), String.format("'%s'", "10"));
						put(TC2.getName(), String.format("\"%s\"", "421"));
						put(MONTH_DAYS.getName(), String.format("{'%s':30}[%s]", "10", QUOTE_GROUP.getName()));
						put(IRPF_PERCENT.getName(), Double.toString(FAKER.number().randomDouble(2, 2, 25)));
						
					}
				},
				new String[] {
				Double.toString(FAKER.number().randomDouble(2, 100, 250)) ,
				Double.toString(FAKER.number().randomDouble(2, 1000, 2000)) ,
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" ,
				},
				null
				,
				workplace.getDomain(),					//domainId, 
				person.getId(),							//personId, 
				workplace.getId(),						//workplaceId, 
				enterpriseCcc.getId(),					//enterpriseCccId,
				enterpriseCcc.getEnterpriseActivity()	//enterpriseActivityId
				);
		//@formatter:on
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		calculateAndSave(connection, ctx);
		
		return contract;
	}
	
	private ContractRecord newSalary(
		Connection conn, 
			Date startDate, 
			Date endDate,
			ContractRecord contract) throws ExpressionException, SQLException, SalaryException {
		
		AONContext aonContext = new AONContext(conn);

		//@formatter:off
		contract = newContract(aonContext,
				SSRegimeType.GENERAL, 
				CCCType.TRAINING,			
				startDate,
				endDate,
				new HashMap<String, String>() {
					{
						put(QUOTE_GROUP.getName(), String.format("'%s'", "10"));
						put(TC2.getName(), String.format("\"%s\"", "421"));
						put(MONTH_DAYS.getName(), String.format("{'%s':30}[%s]", "10", QUOTE_GROUP.getName()));
						put(IRPF_PERCENT.getName(), Double.toString(FAKER.number().randomDouble(2, 2, 25)));
						
					}
				},
				new String[] {
				Double.toString(FAKER.number().randomDouble(2, 100, 250)) ,
				Double.toString(FAKER.number().randomDouble(2, 1000, 2000)) ,
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" ,
				},
				null
				,
				contract.getDomain(),				//domainId, 
				contract.getPerson(),				//personId, 
				contract.getWorkplace(),			//workplaceId, 
				contract.getEnterpriseCcc(),		//enterpriseCccId,
				contract.getEnterpriseActivity()	//enterpriseActivityId
				);
		//@formatter:on
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(conn, startDate, endDate, endDate, contract);
		
		calculateAndSave(conn, ctx);
		
		return contract;
	}

	private ContractRecord newSettle(
			Connection conn, 
				Date startDate, 
				Date endDate,
				ContractRecord contract) throws ExpressionException, SQLException, SalaryException {
			
			AONContext aonContext = new AONContext(conn);
			
			addPayment(aonContext, contract, "DIAS VACACIONES NO DISFRUTADOS", "DIAS_VACACIONES_NO_DISFRUTADOS * ( SALARIO_DIA + SALARIO_VARIABLE_DIA )", "_P", "_P", PaymentType.CRA_0006 , SalaryType.SETTLE );
			addPayment(aonContext, contract, "INDEMNIZACION FIN CONTRATO TEMPORAL", "DIAS_INDEMNIZACION_FIN(INICIO_CONTRATO) * AÑOS_TRABAJADOS * SALARIO_DIA", "_P", null, PaymentType.CRA_0054 , SalaryType.SETTLE );
			

			//@formatter:off
			//@formatter:on
			ISQLContractSalaryCalculatorContext ctx = getSmartSQLContractSettleContext(conn, startDate, endDate, contract);
			
			calculateAndSave(conn, ctx);
			
			return contract;
	}
	
	private static final RegistryRecord newPerson(AONContext aonContext, int domainId) {
		Name name = FAKER.name();
		
		RegistryRecord person = aonContext.getDslContext().insertInto(REGISTRY).set(REGISTRY.DOMAIN, domainId)
				.set(REGISTRY.NAME, "")
				.set(REGISTRY.ALIAS, name.username())
				.set(REGISTRY.DOCUMENT, FAKER.number().digits(9))
				.set(REGISTRY.DOCUMENT_COUNTRY, "ES").
				set(REGISTRY.DOCUMENT_TYPE, (byte) DocumentType.OTHER.ordinal())
				.set(REGISTRY.NATIONALITY, "ES")
				.set(REGISTRY.TYPE, (byte) RegistryType.NATURAL.ordinal()).returning()
				.fetchOne();

		aonContext.getDslContext().insertInto(PERSON)
				.set(PERSON.DOMAIN, domainId)
				.set(PERSON.REGISTRY, person.getId())
				.set(PERSON.NAME, name.firstName())
				.set(PERSON.FIRST_SURNAME, name.lastName())
				// .set(PERSON.BIRTH_DATE, null)
				.set(PERSON.SOCIAL_SECURITY_NUM, FAKER.number().digits(12))
				.set(PERSON.GENDER, (byte) Gender.UNKNOWN.ordinal())
				.set(PERSON.MARITAL_STATUS, (byte) MaritalStatus.UNKNOWN.ordinal())
				.execute();
		return person;
	}
	

	private static int calculateAndSave(Connection connection,
			ISQLContractSalaryCalculatorContext ctx) throws SalaryException {
		
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		RoundSalaryBuilder<ISalary> roundSalaryBuilder = new RoundSalaryBuilder<ISalary>(jooqSalaryBuilder,
				d -> Math.round(d*100.00)/100.00);
		
		new SmartContractSalaryCalculator<ISalary>(roundSalaryBuilder).calculate(ctx);
		return jooqSalaryBuilder.execute();
	}
	
	private static ISQLContractSalaryCalculatorContext getSmartSQLContractSettleContext(Connection connection, Date contractStart,
			ContractRecord contract) throws SQLException, ExpressionException {
		return getSmartSQLContractSettleContext(connection, contractStart, getToday(), contract);
	}

	private static ISQLContractSalaryCalculatorContext getSmartSQLContractSettleContext(Connection connection, Date contractStart,
			Date endDate, ContractRecord contract) throws SQLException, ExpressionException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		ISQLContractSalaryCalculatorContext ctx = new SmartSQLContractSettleCalculatorContext(connection, contractStart,
				endDate, endDate, criteria);
		ctx.next();
		return ctx;
	}
	
	

}
