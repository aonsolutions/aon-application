package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;

import java.io.IOException;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.google.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.SQLContractDelayCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryProxy;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseCccColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

public class Main {
	

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws JAXBException, SQLException,
			ClassNotFoundException, IOException, ExpressionException,
			SalaryException {

		//@formatter:off
		Option hostName =  OptionBuilder.withArgName("name")
					 					.hasArg()
										.withLongOpt("host")
										.withDescription("Connect to host.")
										.create("h");
		Option user =  OptionBuilder.withArgName("name")
					 				.hasArg()
									.isRequired(true)
									.withLongOpt("user")
									.withDescription("User for login.")
									.create("u");
		Option password =  OptionBuilder.withArgName("name")
										.hasArg()
										.isRequired(true)
										.withLongOpt("password")
										.withDescription("Password to use when connecting to server.")
										.create("p");
		Option database =  OptionBuilder.withArgName("name")
										.hasArg()
										.isRequired(true)
										.withLongOpt("database")
										.withDescription("Database to use.")
										.create("D");
		Option year =  OptionBuilder.withArgName("year")
									.hasArg()
									.withLongOpt("year")
									.withDescription("Year (2010...2100)")
									.create("y");
		Option month =  OptionBuilder.withArgName("month")
									.hasArg()
									.withLongOpt("month")
									.withDescription("Month (01..12).")
									.create("m");
		Option ccc =  OptionBuilder.withArgName("name")
									.hasArg()
									.withLongOpt("ccc")
									.withDescription("Calculate only selected CCCs.")
									.create("c");
		Option ipf =  OptionBuilder.withArgName("name")
				.hasArg()
				.withLongOpt("ipf")
				.withDescription("Calculate only selected IPFs (DNI, NIE...)")
				.create("i");
		Option naf=  OptionBuilder.withArgName("name")
				.hasArg()
				.withLongOpt("naf")
				.withDescription("Calculate only selected NAFs")
				.create("n");

		Option type =  OptionBuilder.withArgName("tipo")
				.hasArg()
				.isRequired()
				.withLongOpt("type")
				.withDescription("Salary type, SALARY, EXTRA, DELAY or SETTLE")
				.create("t");

		Option dryRun =  OptionBuilder.withLongOpt("dry-run")
									  .withDescription("Perform a trial run with no changes made.")
									  .create("d");
		
		Option delete =  OptionBuilder.withLongOpt("delete")
									  .withDescription("Delete existing salaries.")
									  .create();
		
		Option pretty =  OptionBuilder.withLongOpt("pretty")
				  .withDescription("Makes the output readable to a human.")
				  .create();

		Options options = new Options()
		.addOption(hostName)
		.addOption(user)
		.addOption(password)
		.addOption(database)
		.addOption(year)
		.addOption(month)
		.addOption(dryRun)
		.addOption(ccc)
		.addOption(ipf)
		.addOption(naf)
		.addOption(delete)
		.addOption(pretty)
		.addOption(type)
		;
		//@formatter:on

		// create the parser
		CommandLineParser parser = new GnuParser();

		try {
			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			Class.forName(com.mysql.jdbc.Driver.class.getName());

			Properties properties = new Properties();
			properties.setProperty("user", cmd.getOptionValue(user.getLongOpt()));
			properties.setProperty("password", cmd.getOptionValue(password.getLongOpt()));
			properties.setProperty("useSSL", "false");
			properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
			String url = String.format("jdbc:mysql://%s:%s/%s", cmd.getOptionValue(hostName.getLongOpt(), "127.0.0.1"), 3306, cmd.getOptionValue(database.getLongOpt()));
			Connection connection = DriverManager.getConnection(url, properties);


			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			
			if (cmd.hasOption(year.getLongOpt()))
				calendar.set(Calendar.YEAR,
						Integer.parseInt(cmd.getOptionValue(year.getLongOpt())));
			if (cmd.hasOption(month.getLongOpt()))
				calendar.set(Calendar.MONTH, Integer.parseInt(cmd
						.getOptionValue(month.getLongOpt())) - 1);
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			Date startDate = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = calendar.getTime();
			Date issueDate = calendar.getTime();

			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) ;		
					
			RoundSalaryBuilder<ISalary> roundSalaryBuilder = 
					new RoundSalaryBuilder<ISalary>(jooqSalaryBuilder, d -> d.setScale(2, RoundingMode.HALF_UP) );		
					
			Criteria criteria = new Criteria();
			if (cmd.hasOption(ccc.getLongOpt()))
				criteria.addEqualExpression(SQLConstants.ENTERPRISE_CCC + "."
						+ EnterpriseCccColumns.CCC,
						cmd.getOptionValue(ccc.getLongOpt()));
			if (cmd.hasOption(ipf.getLongOpt()))
				criteria.addEqualExpression(SQLContractSalaryCalculatorContext.PERSON_REGISTRY + "."
						+ RegistryColumns.DOCUMENT,
						cmd.getOptionValue(ipf.getLongOpt()));
			if (cmd.hasOption(naf.getLongOpt()))
				criteria.addEqualExpression(SQLConstants.PERSON + "."
						+ PersonColumns.SOCIAL_SECURITY_NUM,
						cmd.getOptionValue(naf.getLongOpt()));

			connection.setAutoCommit(false);
			
			SQLContractSalaryCalculatorContext ctx = 
			SalaryType.valueOf(cmd.getOptionValue(type.getLongOpt())).accept(new SalaryType.TypeVisitor<SQLContractSalaryCalculatorContext>() {

				@Override
				public SQLContractSalaryCalculatorContext visitSalary(SalaryType type) {
					try {
						return new SQLContractSalaryCalculatorContext(
								connection, startDate, endDate, issueDate, criteria);
					} catch (SQLException  | ExpressionException e) {
						throw new RuntimeException(e);
					} 
				}

				@Override
				public SQLContractSalaryCalculatorContext visitExtra(SalaryType type) {
					try {
						return new SQLContractSalaryCalculatorContext(
								connection, startDate, endDate, issueDate, criteria);
					} catch (SQLException  | ExpressionException e) {
						throw new RuntimeException(e);
					} 
				}

				@Override
				public SQLContractSalaryCalculatorContext visitSettle(SalaryType type) {
					try {
						return new SQLContractSalaryCalculatorContext(
								connection, startDate, endDate, issueDate, criteria);
					} catch (SQLException  | ExpressionException e) {
						throw new RuntimeException(e);
					} 
				}

				@Override
				public SQLContractSalaryCalculatorContext visitDelay(SalaryType type) {
					try {
						return new SQLContractDelayCalculatorContext(
								connection, startDate, endDate, issueDate, criteria);
					} catch (SQLException  | ExpressionException e) {
						throw new RuntimeException(e);
					} 
				}
				
				@Override
				public SQLContractSalaryCalculatorContext visitProcedural(SalaryType type) {
					try {
						return new SQLContractSalaryCalculatorContext(
								connection, startDate, endDate, issueDate, criteria);
					} catch (SQLException  | ExpressionException e) {
						throw new RuntimeException(e);
					} 
				}

				@Override
				public SQLContractSalaryCalculatorContext visitM190(SalaryType type) {
					try {
						return new SQLContractSalaryCalculatorContext(
								connection, startDate, endDate, issueDate, criteria);
					} catch (SQLException  | ExpressionException e) {
						throw new RuntimeException(e);
					} 
				}
				
			});

			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>(roundSalaryBuilder);

			while (ctx.next()) {
				try {
					calculator.calculate(ctx);
				} catch (Throwable e) {
					e.printStackTrace();
					System.err.println(e.getMessage());
				}
			}
			
			jooqSalaryBuilder.execute();
			
//			if (cmd.hasOption(delete.getLongOpt())){
//				salaryBuilder.delete();
//			}

			if (cmd.hasOption(dryRun.getLongOpt())) {
				connection.rollback();
			} else {
				connection.commit();
			}

			ctx.close();

		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("calc", options);

		}

	}

}
