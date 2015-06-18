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
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

public class Main {

	public static final class MainSalaryBuilder extends JooqSalaryBuilder {
		
		private List<Integer> olds ;
		
		private Date endDate;
		private Date startDate;
		private Integer contract;

		private Double cgcBase;
		private Double totalLiquid;
		private Double totalPayment;
		
		private String employeeName ;
		private String enterpriseName ;
		
		
		public MainSalaryBuilder(Connection connection) {
			super(connection);
			olds = new ArrayList<Integer>();
		}
		
		@Override
		public ISalary getSalary() {
			ISalary newOne = super.getSalary();
			Salary oldOnes [] = getOlds(contract, startDate, endDate);
			
			if ( oldOnes.length == 0 )
				System.out.printf("%-20s %-35s LIQUID:%9.3f            \tPAYMENT:%9.3f            \tCOMMON BASE:%9.3f            \r\n",
						enterpriseName,
						employeeName,
						totalLiquid,
						totalPayment,
						cgcBase);
			
			for(Salary oldOne: oldOnes ) {
				olds.add(oldOne.getId());
				
				
				Double oldCgcBase = oldOne.getCommonContingenciesBase();
				Double oldTotalLiquid = oldOne.getTotalLiquid();
				Double oldTotalPayment = oldOne.getTotalPayment();
				
				Double newCgcBase = Math.round(cgcBase*1000)/1000.00d;
				Double newTotalLiquid = Math.round(totalLiquid*1000)/1000.00d;;
				Double newTotalPayment = Math.round(totalPayment*1000)/1000.00d;;

				PrintStream os = System.err;
				if (newTotalLiquid.equals(oldTotalLiquid)
					&& newCgcBase.equals(oldCgcBase)
					&& newTotalPayment.equals(oldTotalPayment))
					os = System.out;
				
				os.printf("%-20s %-35s LIQUID:%9.3f (%9.3f)\tPAYMENT:%9.3f (%9.3f)\tCOMMON BASE:%9.3f (%9.3f)\r\n",
						enterpriseName,
						employeeName,
						newTotalLiquid,
						oldTotalLiquid,
						newTotalPayment,
						oldTotalPayment,
						newCgcBase,
						oldCgcBase
						);
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
			this.cgcBase = cgcBase;
			super.setCgcBase(cgcBase);
		}

		@Override
		public void setTotalLiquid(Double totalLiquid) {
			this.totalLiquid = totalLiquid;
			super.setTotalLiquid(totalLiquid);
		}
		
		@Override
		public void setTotalPayment(Double totalPayment) {
			this.totalPayment = totalPayment;
			super.setTotalPayment(totalPayment);
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
		Option dryRun =  OptionBuilder.withLongOpt("dry-run")
									  .withDescription("Perform a trial run with no changes made.")
									  .create("d");
		
		Option delete =  OptionBuilder.withLongOpt("delete")
									  .withDescription("Delete existing salaries.")
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
		.addOption(delete)
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

			MainSalaryBuilder salaryBuilder = new MainSalaryBuilder(connection);

			Criteria criteria = new Criteria();
			if (cmd.hasOption(ccc.getLongOpt()))
				criteria.addEqualExpression(SQLConstants.ENTERPRISE_CCC + "."
						+ EnterpriseCccColumns.CCC,
						cmd.getOptionValue(ccc.getLongOpt()));

			connection.setAutoCommit(false);

			SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
					connection, startDate, endDate, issueDate, criteria);

			ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>(
					salaryBuilder);

			while (ctx.next()) {
				try {
					calculator.calculate(ctx);
				} catch (Throwable e) {
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
