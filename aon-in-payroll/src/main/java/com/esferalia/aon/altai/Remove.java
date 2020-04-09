package com.esferalia.aon.altai;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.ContractAttach;
import com.esferalia.aon.jooq.tables.ContractData;
import com.esferalia.aon.jooq.tables.ContractInfo;
import com.esferalia.aon.jooq.tables.Creditor;
import com.esferalia.aon.jooq.tables.Customer;
import com.esferalia.aon.jooq.tables.Enterprise;

public class Remove {
	



	 public static void main(String[] args) {
		

		@SuppressWarnings("static-access")
		Option hostOption = OptionBuilder
		     .hasArg()
			 .isRequired()
		     .withLongOpt("host")
		     .withArgName("name")
		     .withDescription("Connect to host.")
		     .create("h");
	 
		@SuppressWarnings("static-access")
		 Option portOption = OptionBuilder
		     .hasArg()
		     .withLongOpt("port")
		     .withArgName("name")
		     .withDescription("Port number to use for connection, default (3306).")
		     .create("P");

		@SuppressWarnings("static-access")
		Option userOption = OptionBuilder
		     .hasArg()
			 .isRequired()
		     .withLongOpt("user")
		     .withArgName("name")
		     .withDescription("User for login if not current user.")
		     .create("u");

		@SuppressWarnings("static-access")
		Option passwordOption = OptionBuilder
		     .hasArg()
			 .isRequired()
		     .withLongOpt("password")
		     .withArgName("name")
		     .withDescription("Password to use when connecting to server.")
		     .create("p");

		@SuppressWarnings("static-access")
		Option databaseOption = OptionBuilder
		     .hasArg()
			 .isRequired()
		     .withLongOpt("database")
		     .withArgName("name")
		     .withDescription("Database to use")
		     .create("D");


		@SuppressWarnings("static-access")
		Option whereOption = OptionBuilder
		     .hasArg()
		     .withLongOpt("where")
		     .withArgName("where")
		     .withDescription("Insight only selected contracts. Quotes are mandatory.")
		     .create();

		@SuppressWarnings("static-access")
		Option helpOption = OptionBuilder
			 .withLongOpt("help")
	         .withDescription("Display this help and exit.")
	         .create("?");

		Options options = new Options();
		options.addOption(helpOption);
		options.addOption(hostOption);
		options.addOption(portOption);
		options.addOption(userOption);
		options.addOption(whereOption);
		options.addOption(databaseOption);
		options.addOption(passwordOption);
		 
		Connection connection = null;
		try {
			CommandLineParser parser = new GnuParser();
			CommandLine commandLine = parser.parse(options, args);
	
			
			String host = commandLine.getOptionValue(hostOption.getLongOpt());
			String port = commandLine.getOptionValue(portOption.getLongOpt(), "3306");
			String user = commandLine.getOptionValue(userOption.getLongOpt());
			String password = commandLine.getOptionValue(passwordOption.getLongOpt());
			String database = commandLine.getOptionValue(databaseOption.getLongOpt());
			String where = commandLine.getOptionValue(whereOption.getLongOpt(), "`domain`.`name` LIKE 'altai%'");
	
			Properties properties = new Properties();
			properties.setProperty("user", user);
			properties.setProperty("password", password);
			properties.setProperty("useSSL", "false");
			properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
			String url = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
	
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, properties);
			
			remove(connection, where);

		} catch (ParseException e) {
			// oops, somthing went wrong
			System.out.println("Error: " + e.getLocalizedMessage());
			new HelpFormatter().printHelp(Remove.class.getSimpleName(), options);
		} catch (SQLException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getLocalizedMessage());
		} finally {
			try {
				if ( connection != null )
					connection.close();
			} catch ( SQLException e ) {
				System.err.println("Oops, something went wrong, " + e.getLocalizedMessage());
			}
		}
		 
	 }
	 
	 private static void remove (Connection connection, String where) {
			Settings settings;

			settings = new Settings();
			settings.setRenderSchema(false);
			settings.setParamType(ParamType.INLINED);

			DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
			
			Condition condition = DSL.condition(where);
			
			dslContext.transaction(configuration -> {
				
				SelectConditionStep<Record1<Integer>> domains = 
				DSL.select(DOMAIN.ID).from(DOMAIN).where(condition);
				
				String uuid = Long.toString(UUID.randomUUID().getMostSignificantBits());
				
				int updated =
				dslContext.update(REGISTRY).set(REGISTRY.ALIAS, uuid)
				.where(REGISTRY.ID.in(DSL.select(CONTRACT.PERSON).from(CONTRACT).where(CONTRACT.DOMAIN.in(domains))))
				.and(REGISTRY.ID.notIn(DSL.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(REGISTRY.DOMAIN.eq(ENTERPRISE.DOMAIN))))
				.and(REGISTRY.ID.notIn(DSL.select(CREDITOR.REGISTRY).from(CREDITOR).where(REGISTRY.DOMAIN.eq(CREDITOR.DOMAIN))))
				.execute();

				System.out.printf("%d %ss found.\r\n", updated, REGISTRY.getName());
				
				int deleted = configuration.dsl().delete(SALARY_EMBARGO).where(SALARY_EMBARGO.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, SALARY_EMBARGO.getName());
				
				deleted = configuration.dsl().delete(SALARY_COST).where(SALARY_COST.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, SALARY_COST.getName());
				
				deleted = configuration.dsl().delete(SALARY_DEDUCTION).where(SALARY_DEDUCTION.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, SALARY_DEDUCTION.getName());

				deleted = configuration.dsl().delete(SALARY_PAYMENT).where(SALARY_PAYMENT.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, SALARY_PAYMENT.getName());

				deleted = configuration.dsl().delete(SALARY_DATA).where(SALARY_DATA.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, SALARY_DATA.getName());

				deleted = configuration.dsl().delete(SALARY).where(SALARY.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, SALARY.getName());

				deleted = configuration.dsl().delete(CONTRACT_INFO).where(CONTRACT_INFO.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, CONTRACT_INFO.getName());
				
				deleted = configuration.dsl().delete(CONTRACT_ATTACH).where(CONTRACT_ATTACH.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, CONTRACT_ATTACH.getName());
				
				deleted = configuration.dsl().delete(CONTRACT_DATA).where(CONTRACT_DATA.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, CONTRACT_DATA.getName());
				
				deleted = configuration.dsl().delete(CONTRACT_PAYMENT).where(CONTRACT_PAYMENT.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, CONTRACT_PAYMENT.getName());
				
				deleted = configuration.dsl().delete(CONTRACT_EMBARGO).where(CONTRACT_EMBARGO.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, CONTRACT_EMBARGO.getName());
				
				deleted = configuration.dsl().delete(CONTRACT_DEDUCTION).where(CONTRACT_DEDUCTION.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, CONTRACT_DEDUCTION.getName());
				
				deleted = configuration.dsl().delete(CONTRACT_LEAVE).where(CONTRACT_LEAVE.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, CONTRACT_LEAVE.getName());

				deleted = configuration.dsl().delete(CONTRACT).where(CONTRACT.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, CONTRACT.getName());

//				deleted = configuration.dsl().delete(PAYROLL_WORKPLACE).where(PAYROLL_WORKPLACE.DOMAIN.in(domains)).execute() ;
//				System.out.printf("%d %ss deleted.\r\n", deleted, PAYROLL_WORKPLACE.getName());

//				deleted = configuration.dsl().delete(ENTERPRISE_CCC).where(ENTERPRISE_CCC.DOMAIN.in(domains)).execute() ;
//				System.out.printf("%d %ss deleted.\r\n", deleted, ENTERPRISE_CCC.getName());

				deleted = configuration.dsl().delete(PERSON).where(PERSON.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, CONTRACT.getName());
				
				deleted = configuration.dsl().delete(CUSTOMER).where(CUSTOMER.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, CUSTOMER.getName());

//				deleted = configuration.dsl().delete(CREDITOR).where(CREDITOR.DOMAIN.in(domains)).execute() ;
//				System.out.printf("%d %ss deleted.\r\n", deleted, CREDITOR.getName());

				SelectConditionStep<Record1<Integer>> registries = 
				DSL.select(REGISTRY.ID).from(REGISTRY).where(REGISTRY.ALIAS.eq(uuid)).and(REGISTRY.DOMAIN.in(domains));
				
				deleted = configuration.dsl().delete(RMEDIA).where(RMEDIA.REGISTRY.in(registries)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, RMEDIA.getName());

				deleted = configuration.dsl().delete(RADDINFO).where(RADDINFO.REGISTRY.in(registries)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, RADDINFO.getName());

				deleted = configuration.dsl().delete(RADDRESS).where(RADDRESS.REGISTRY.in(registries)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, RADDRESS.getName());

				deleted = configuration.dsl().delete(REGISTRY).where(REGISTRY.ALIAS.eq(uuid)).and(REGISTRY.DOMAIN.in(domains)).execute() ;
				System.out.printf("%d %ss deleted.\r\n", deleted, REGISTRY.getName());
			
			});
			
			
	 }

}
