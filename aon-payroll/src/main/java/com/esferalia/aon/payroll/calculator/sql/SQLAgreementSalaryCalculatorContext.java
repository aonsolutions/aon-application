package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelCategoryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RaddressColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLAgreementSalaryCalculatorContext extends
		SQLContractSalaryCalculatorContext {

	//@formatter:off
	private static final String MAIN_SQL = "SELECT * "
			+ " FROM %s"
			+ " LEFT JOIN enterprise_ccc ON (contract.enterprise_ccc = enterprise_ccc.id)"
			+ " LEFT JOIN enterprise_activity ON (contract.enterprise_activity = enterprise_activity.id)"
			+ " LEFT JOIN %s ON (contract.agreement_level_category = agreement_level_category.id)"
			+ " LEFT JOIN agreement_level ON (agreement_level.id = agreement_level_category.agreement_level)"
			+ ", %s"
			+ ", %s " 
			+ ", %s" 
			+ " LEFT JOIN payroll_workplace ON (payroll_workplace.workplace = workplace.id)"
			+ ", %s"
			+ ", %s"
			+ " LEFT JOIN customer ON (customer.registry = " + ENTERPRISE_REGISTRY + ".id)"
			+ ", %s"
			+ " WHERE contract.person = person.registry" // INNER JOIN: person is NOT NULL
			+ " AND person.registry = person_registry.id" // INNER JOIN: // registry is NOT NULL
			+ " AND contract.workplace = workplace.id" // INNER JOIN: workplace is NOT NULL
			+ " AND workplace.enterprise = enterprise.registry" // INNER JOIN: enterprise is NOT NULL
			+ " AND enterprise.registry = enterprise_registry.id" // INNER JOIN: registry is NOT  NULL
			+ " AND workplace.address = raddress.id" // INNER JOIN: address is NOT NULL
			+ " AND contract.start_date <= ? "
			+ " AND ( contract.end_date  IS NULL" + " OR contract.end_date >= ? )";
	//@formatter:on

	//@formatter:off
	private static final String CONTRACT = "( SELECT " +
			"0 " + ContractColumns.ID + 
			" ,0 "  + ContractColumns.DOMAIN +
			" ,0 "  + ContractColumns.PERSON +
			" ,0 "  + ContractColumns.WORKPLACE +
			" ,NULL "  + ContractColumns.ENTERPRISE_CCC +
			" ,'%1$tY-%1$tm-%1$td' "  + ContractColumns.START_DATE + // TODO: 
			" ,NULL "  + ContractColumns.END_DATE +
			" ,NULL "  + ContractColumns.CALENDAR +
			" ,NULL "  + ContractColumns.DESCRIPTION +
			" ,0 "  + ContractColumns.SEPE_STATUS +
			" ,NULL "  + ContractColumns.REGISTRATION +
			" ,'%2$tY-%2$tm-%2$td' "  + ContractColumns.SENIORITY_DATE +
			" ,NULL "  + ContractColumns.ENTERPRISE_ACTIVITY +
			" ,NULL "  + ContractColumns.SS_REGIME +
			" ,0 "  + ContractColumns.AGREEMENT_LEVEL_CATEGORY +
			" ,NULL "  + ContractColumns.MODEL +
			" ,NULL "  + ContractColumns.CATEGORY_DESCRIPTION +
			" ,0 "  + ContractColumns.SS_STATUS +
			" ) AS  " + SQLConstants.CONTRACT;
	//@formatter:on

	//@formatter:off
	private static final String CATEGORY = "( SELECT " +
			"0 " + AgreementLevelCategoryColumns.ID+ 
			" ,0 "  + AgreementLevelCategoryColumns.DOMAIN +
			" ,%d "  + AgreementLevelCategoryColumns.AGREEMENT_LEVEL +
			" ,NULL "  + AgreementLevelCategoryColumns.DESCRIPTION +
			" ) AS  " + SQLConstants.AGREEMENT_LEVEL_CATEGORY;
	//@formatter:on

	//@formatter:off
	private static final String PERSON = "( SELECT " +
			"0 " + PersonColumns.REGISTRY + 
			" ,0 "  + PersonColumns.DOMAIN +
			" ,NULL "  + PersonColumns.BIRTH_DATE +
			" ,0 "  + PersonColumns.GENDER +
			" ,0 "  + PersonColumns.MARITAL_STATUS+
			" ,NULL "  + PersonColumns.SOCIAL_SECURITY_NUM+
			" ,NULL "  + PersonColumns.NAME+
			" ,NULL "  + PersonColumns.FIRST_SURNAME+
			" ,NULL "  + PersonColumns.SECOND_SURNAME+
			" ) AS  " + SQLConstants.PERSON;
	//@formatter:on

	//@formatter:off
	private static final String P_REGISTRY = "( SELECT " +
			"0 " + RegistryColumns.ID + 
			" ,0 "  + RegistryColumns.DOMAIN +
			" ,NULL "  + RegistryColumns.DOCUMENT+
			" ,0 "  + RegistryColumns.DOCUMENT_TYPE+
			" ,'ES' "  + RegistryColumns.DOCUMENT_COUNTRY+
			" ,NULL "  + RegistryColumns.NAME+
			" ,NULL "  + RegistryColumns.ALIAS+
			" ,NULL "  + RegistryColumns.TYPE+
			" ,'ES' "  + RegistryColumns.NATIONALITY+
			" ,0 "  + RegistryColumns.SECURITY_LEVEL+
			" ) AS " + PERSON_REGISTRY;
	//@formatter:on

	//@formatter:off
	private static final String WORKPLACE = "( SELECT " +
			"0 " + WorkplaceColumns.ID + 
			" ,0 "  + WorkplaceColumns.DOMAIN +
			" ,0 "  + WorkplaceColumns.ENTERPRISE+
			" ,'' "  + WorkplaceColumns.DESCRIPTION+
			" ,0 "  + WorkplaceColumns.ADDRESS+
			" ,0 "  + WorkplaceColumns.CUSTOMER+
			" ,0 "  + WorkplaceColumns.SCOPE+
			" ,0 "  + WorkplaceColumns.ECONOMICAGREEMENT+
			" ,1 "  + WorkplaceColumns.ACTIVE+
			" ) AS " + SQLConstants.WORKPLACE;
	//@formatter:on

	//@formatter:off
	private static final String ENTERPRISE = "( SELECT " +
			"0 " + EnterpriseColumns.REGISTRY + 
			" ,0 "  + EnterpriseColumns.DOMAIN +
			" ,0 "  + EnterpriseColumns.SCOPE+
			" ,NULL "  + EnterpriseColumns.CALENDAR+
			" ) AS " + SQLConstants.ENTERPRISE;
	//@formatter:on

	//@formatter:off
	private static final String E_REGISTRY = "( SELECT " +
			"0 " + RegistryColumns.ID + 
			" ,0 "  + RegistryColumns.DOMAIN +
			" ,NULL "  + RegistryColumns.DOCUMENT+
			" ,0 "  + RegistryColumns.DOCUMENT_TYPE+
			" ,'ES' "  + RegistryColumns.DOCUMENT_COUNTRY+
			" ,NULL "  + RegistryColumns.NAME+
			" ,NULL "  + RegistryColumns.ALIAS+
			" ,NULL "  + RegistryColumns.TYPE+
			" ,'ES' "  + RegistryColumns.NATIONALITY+
			" ,0 "  + RegistryColumns.SECURITY_LEVEL+
			" ) AS " + ENTERPRISE_REGISTRY;
	//@formatter:on

	//@formatter:off
	private static final String RADDRESS = "( SELECT " +
			"0 " + RaddressColumns.ID + 
			" ,0 "  + RaddressColumns.DOMAIN +
			" ,0 "  + RaddressColumns.REGISTRY+
			" ,0 "  + RaddressColumns.TYPE+
			" ,NULL "  + RaddressColumns.RECIPIENT+
			" ,0 "  + RaddressColumns.STREET_TYPE+
			" ,NULL "  + RaddressColumns.ADDRESS+
			" ,NULL "  + RaddressColumns.NUMBER+
			" ,NULL "  + RaddressColumns.ADDRESS2+
			" ,NULL "  + RaddressColumns.ADDRESS3+
			" ,NULL "  + RaddressColumns.ZIP+
			" ,NULL "  + RaddressColumns.CITY+
			" ,NULL "  + RaddressColumns.GEOZONE+
			" ,NULL "  + RaddressColumns.ALIAS+
			" ,NULL "  + RaddressColumns.MUNICIPALITY_CODE+
			" ) AS " + SQLConstants.RADDRESS;
	//@formatter:on

	public SQLAgreementSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, int agreementLevelId)
			throws SQLException, ExpressionException {
		this(connection, startDate, endDate, endDate, endDate, agreementLevelId);
	}

	public SQLAgreementSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, int agreementLevelId)
			throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, endDate,
				agreementLevelId);
	}

	public SQLAgreementSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate,
			int agreementLevelId) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, null,
				getPaymentsCriteria(SalaryType.SALARY, SalaryType.EXTRA),
				NEWER, startDate, agreementLevelId);
	}

	// ------------------------------------------------------- Protected methods

	@Override
	protected String getMainSql(Object... args) {
		Date startDate = (Date) args[0];
		int agreementLevelId = (Integer) args[1];
		return String.format(MAIN_SQL,
				String.format(CONTRACT, startDate, startDate),
				String.format(CATEGORY, agreementLevelId), PERSON, P_REGISTRY,
				WORKPLACE, ENTERPRISE, E_REGISTRY, RADDRESS);
	}

	// ------------------------------------------------------------------------
	public static void main(String[] args) throws Exception {
		Class.forName("org.gjt.mm.mysql.Driver");

		Connection c = DriverManager.getConnection(
				"jdbc:mysql://127.0.0.1:3306/tadc055-toledoas-com", "aon",
				"40n");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH,1);
		Date startDate = calendar.getTime();
		
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = calendar.getTime();
		
		SQLAgreementSalaryCalculatorContext ctx = 
				new SQLAgreementSalaryCalculatorContext(c, startDate, endDate, 1);
		ContractSalaryCalculator calculator = new ContractSalaryCalculator();
		calculator.setSalaryBuilder(new SalaryBuilder());
		while ( ctx.next() ) {
			ISalary salary = calculator.calculate(ctx);
			System.out.println(salary.getTotalPayment());
		}
		ctx.close();
	}

}
