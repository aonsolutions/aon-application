package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryProxy;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseCccColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

public class Main {

	public static class MainSalaryBuilder extends JooqSalaryBuilder<ISalary> {
		
		private List<Integer> olds ;
		
		protected Date endDate;
		protected Date startDate;
		protected Integer contract;

		protected Double cgcBase;
		protected Double totalLiquid;
		protected Double totalPayment;
		
		protected String employeeName ;
		protected String enterpriseName ;
		
		
		public MainSalaryBuilder(Connection connection) {
			super(connection);
			olds = new ArrayList<Integer>();
			head();
		}
		
		@Override
		public ISalary getSalary() {
			ISalary newOne = super.getSalary();
			Salary oldOnes [] = getOlds(contract, startDate, endDate);
			
			if ( oldOnes.length == 0 )
				newOne();
			
			for(Salary oldOne: oldOnes ) {
				olds.add(oldOne.getId());
				
				Double oldCgcBase = oldOne.getCommonContingenciesBase();
				Double oldTotalLiquid = oldOne.getTotalLiquid();
				Double oldTotalPayment = oldOne.getTotalPayment();
				
				if (totalLiquid.equals(oldTotalLiquid)
					&& cgcBase.equals(oldCgcBase)
					&& totalPayment.equals(oldTotalPayment))
					existEqual(oldTotalLiquid, oldTotalPayment, oldCgcBase);
				else
					existNotEqual(oldTotalLiquid, oldTotalPayment, oldCgcBase);
				
			}
			
			return newOne;
		}
		
		@Override
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
			super.setEndDate(endDate);
		}

		@Override
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
			super.setStartDate(startDate);
		}

		@Override
		public void setContract(Object contract) {
			this.contract = ((SQLSalaryProxy)contract).getContractId();
			super.setContract(contract);
		}

		@Override
		public void setEnterpriseName(String enterpriseName) {
			this.enterpriseName = enterpriseName;
			super.setEnterpriseName(enterpriseName);
		}

		@Override
		public void setEmployeeName(String employeeName) {
			this.employeeName = employeeName;
			super.setEmployeeName(employeeName);
		}
		
		@Override
		public void setCgcBase(Double cgcBase) {
			this.cgcBase = Math.round(cgcBase*1000)/1000.00d;;
			super.setCgcBase(cgcBase);
		}

		@Override
		public void setTotalLiquid(Double totalLiquid) {
			this.totalLiquid = Math.round(totalLiquid*1000)/1000.00d;
			super.setTotalLiquid(totalLiquid);
		}
		
		@Override
		public void setTotalPayment(Double totalPayment) {
			this.totalPayment = Math.round(totalPayment*1000)/1000.00d;
			super.setTotalPayment(totalPayment);
		}
		
		
		public void head() {
			System.out.printf("%-20s %-33s %-9s            \t%-9s            \t%-9s            \r\n",
					"ENTERPRISE",
					"EMPLOYEE",
					"LIQUID",
					"PAYMENT",
					"BASE");
			System.out.printf("----------------------------------------------------------------------------------------------------------------------------\r\n");
		}
		
		public void newOne(){
			System.out.printf("%-20s %-33s %9.3f            \t%9.3f            \t%9.3f            \r\n",
					enterpriseName,
					employeeName,
					totalLiquid,
					totalPayment,
					cgcBase);
		}
		
		public void existEqual(Double oldTotalLiquid, Double oldTotalPayment, Double oldCgcBase){
			System.out.printf("%-20s %-35s %9.3f (%9.3f)\t%9.3f (%9.3f)\t%9.3f (%9.3f)\r\n",
					enterpriseName,
					employeeName,
					totalLiquid,
					oldTotalLiquid,
					totalPayment,
					oldTotalPayment,
					cgcBase,
					oldCgcBase
					);
		}
		
		public void existNotEqual(Double oldTotalLiquid, Double oldTotalPayment, Double oldCgcBase){
			System.err.printf("%-20s %-35s %9.3f (%9.3f)\t%9.3f (%9.3f)\t%9.3f (%9.3f)\r\n",
					enterpriseName,
					employeeName,
					totalLiquid,
					oldTotalLiquid,
					totalPayment,
					oldTotalPayment,
					cgcBase,
					oldCgcBase
					);
		}

		public void delete() {
			delete(olds.toArray(new Integer[olds.size()]));
		}

		public void delete(Integer ...olds) {
			getDSLContext().delete(SALARY_DATA)
					.where(SALARY_DATA.SALARY.in(olds)).execute();
			getDSLContext().delete(SALARY_COST)
					.where(SALARY_COST.SALARY.in(olds)).execute();
			getDSLContext().delete(SALARY_BONUS)
					.where(SALARY_BONUS.SALARY.in(olds)).execute();
			getDSLContext().delete(SALARY_PAYMENT)
					.where(SALARY_PAYMENT.SALARY.in(olds))
					.execute();
			getDSLContext().delete(SALARY_DEDUCTION)
					.where(SALARY_DEDUCTION.SALARY.in(olds))
					.execute();
			getDSLContext().delete(SALARY)
					.where(SALARY.ID.in(olds)).execute();
		}

		public Salary []  getOlds(Integer contract, Date startDate, Date endDate){
			
			AONContext ctx = new AONContext(getDSLContext());
			
			return 
			AON.getSalaries(ctx, props->props.getContractProperty().eq(contract)
					.and(props.getStartDateProperty().le(endDate))
					.and(props.getEndDateProperty().ge(startDate))
					.and(props.getIsSalaryProperty().eq(true)))
			.toArray(count->new Salary[count])
			;
		}
	}
	
	/**
	 * 
	 * I know that for printing a colored text, for example red color, the code is:
	 * "\e[1;31m This is red text \e[0m"
	 * and I know that in this example, 31 is code of red color and the number of other colors is:
	 *  
	 * Black       0;30     Dark Gray     1;30
	 * Blue        0;34     Light Blue    1;34
	 * Green       0;32     Light Green   1;32
	 * Cyan        0;36     Light Cyan    1;36
	 * Red         0;31     Light Red     1;31
	 * Purple      0;35     Light Purple  1;35
	 * Brown       0;33     Yellow        1;33
	 * Light Gray  0;37     White         1;37
	 *
	 * @author rtrepiana
	 *
	 */
	private static class PrettyMainSalaryBuilder extends MainSalaryBuilder {

		
		public static final String ANSI_RESET = "\u001B[0m";
		public static final String ANSI_BOLD = "\u001B[1m";
		public static final String ANSI_BLACK = "\u001B[30m";
		public static final String ANSI_RED = "\u001B[31m";
		public static final String ANSI_GREEN = "\u001B[32m";
		public static final String ANSI_YELLOW = "\u001B[33m";
		public static final String ANSI_BLUE = "\u001B[34m";
		public static final String ANSI_PURPLE = "\u001B[35m";
		public static final String ANSI_CYAN = "\u001B[36m";
		public static final String ANSI_WHITE = "\u001B[37m";

		public PrettyMainSalaryBuilder(Connection connection) {
			super(connection);
		}
		
		
		@Override
		public void newOne() {
			super.newOne();
		}
		
		public void head() {
			System.out.printf("%s%-20s %-33s %-9s            \t%-9s            \t%-9s            %s\r\n",
					ANSI_BOLD,
					"ENTERPRISE",
					"EMPLOYEE",
					"LIQUID",
					"PAYMENT",
					"BASE",
					ANSI_RESET);
			System.out.printf("----------------------------------------------------------------------------------------------------------------------------\r\n");
		}

		@Override
		public void existEqual(Double oldTotalLiquid, Double oldTotalPayment,
				Double oldCgcBase) {
			System.out.printf("%-20s %-33s %9.3f (%s%9.3f%s)\t%9.3f (%s%9.3f%s)\t%9.3f (%s%9.3f%s)\r\n",
					enterpriseName,
					employeeName,
					totalLiquid,
					ANSI_YELLOW,
					oldTotalLiquid,
					ANSI_RESET,
					
					totalPayment,
					ANSI_YELLOW,
					oldTotalPayment,
					ANSI_RESET,
					
					cgcBase,
					ANSI_YELLOW,
					oldCgcBase,
					ANSI_RESET
					);
		}
		
		@Override
		public void existNotEqual(Double oldTotalLiquid,
				Double oldTotalPayment, Double oldCgcBase) {
			System.out.printf("%-20s %-33s %9.3f (%s%9.3f%s)\t%9.3f (%s%9.3f%s)\t%9.3f (%s%9.3f%s)\r\n",
					enterpriseName,
					employeeName,
					totalLiquid,
					totalLiquid.equals(oldTotalLiquid) ? ANSI_YELLOW : ANSI_RED,
					oldTotalLiquid,
					ANSI_RESET,
					
					totalPayment,
					totalPayment.equals(oldTotalPayment) ? ANSI_YELLOW : ANSI_RED,
					oldTotalPayment,
					ANSI_RESET,
					
					cgcBase,
					cgcBase.equals(oldCgcBase) ? ANSI_YELLOW : ANSI_RED,
					oldCgcBase,
					ANSI_RESET
					);
		}
		
	}
	

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
		.addOption(delete)
		.addOption(pretty)
		;
		//@formatter:on

		// create the parser
		CommandLineParser parser = new GnuParser();

		try {
			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			Class.forName(com.mysql.jdbc.Driver.class.getName());

			Connection connection = DriverManager.getConnection(String.format(
					"jdbc:mysql://%s:%d/%s",
					cmd.getOptionValue(hostName.getLongOpt(), "127.0.0.1"),
					3306, cmd.getOptionValue(database.getLongOpt())), cmd
					.getOptionValue(user.getLongOpt()), cmd
					.getOptionValue(password.getLongOpt()));

			Calendar calendar = Calendar.getInstance();
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

			MainSalaryBuilder salaryBuilder = cmd.hasOption(pretty.getLongOpt()) ? 
					new PrettyMainSalaryBuilder(connection) : new  MainSalaryBuilder(connection);
			
			RoundSalaryBuilder<ISalary> roundSalaryBuilder = 
					new RoundSalaryBuilder<ISalary>(salaryBuilder, d->Math.round(d*1000.00)/1000.00 );		
					
			Criteria criteria = new Criteria();
			if (cmd.hasOption(ccc.getLongOpt()))
				criteria.addEqualExpression(SQLConstants.ENTERPRISE_CCC + "."
						+ EnterpriseCccColumns.CCC,
						cmd.getOptionValue(ccc.getLongOpt()));
			if (cmd.hasOption(ipf.getLongOpt()))
				criteria.addEqualExpression(SQLContractSalaryCalculatorContext.PERSON_REGISTRY + "."
						+ RegistryColumns.DOCUMENT,
						cmd.getOptionValue(ipf.getLongOpt()));

			connection.setAutoCommit(false);

			SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
					connection, startDate, endDate, issueDate, criteria);

			ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>(
					roundSalaryBuilder);

			while (ctx.next()) {
				try {
					calculator.calculate(ctx);
				} catch (Throwable e) {
					e.printStackTrace();
					System.err.println(e.getMessage());
				}
			}
			
			salaryBuilder.execute();
			
			if (cmd.hasOption(delete.getLongOpt())){
				salaryBuilder.delete();
			}

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
