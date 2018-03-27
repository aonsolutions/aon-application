package net.aonsolutions.payroll.report.aws;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.ConnectionInfo;
import net.aonsolutions.payroll.report.SalaryReport;
import net.sf.jasperreports.engine.JRException;

public class SalaryReportLambdaFunctionHandler implements RequestStreamHandler {

	private static Pattern SALARY_PATTERN = Pattern.compile("[\"'](\\d+)\\.(.{1,4})[\"']");
	private static Pattern WORKPLACE_PATTERN = Pattern.compile("[\"'](\\d{1,2})_(\\d{4})_(\\d+)_(\\d+)\\.(.{1,4})[\"']");

	public static String getDbHost() {
		return System.getenv("DB_HOST");
	}

	public static String getDbUser() {
		return System.getenv("DB_USER");
	}

	public static String getDbPasswd() {
		return System.getenv("DB_PASSWD");
	}
	
	public static Condition getCondition(String criteria) {
		Matcher matcher = 
				WORKPLACE_PATTERN.matcher(criteria);
		
		if ( !matcher.matches() ) {
			matcher = SALARY_PATTERN.matcher(criteria);
			matcher.matches();
			Integer salaryId =Integer.parseInt(matcher.group(1));
			return SALARY.ID.eq(salaryId);
		}
		
		Integer month = Integer.parseInt(matcher.group(1));
		Integer year = Integer.parseInt(matcher.group(2));
		Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.YEAR, year);
		calendar.set( Calendar.MONTH, month);
		calendar.set( Calendar.DAY_OF_MONTH, 1); // The first day of the month has value 1.
		calendar.set( Calendar.HOUR, 0);
		calendar.set( Calendar.MINUTE, 0);
		calendar.set( Calendar.SECOND, 0);
		Date startDate = new Date(calendar.getTimeInMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = new Date(calendar.getTimeInMillis());

		Integer enterpriseId = Integer.parseInt(matcher.group(3));
		Integer workplaceId = Integer.parseInt(matcher.group(4));
		
		return WORKPLACE.ENTERPRISE.eq(enterpriseId)
		.and(CONTRACT.WORKPLACE.eq(workplaceId))
		.and(SALARY.END_DATE.between(startDate, endDate))
		;
		
	}
	

	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		
		
		String dbHost = getDbHost();
		String dbUser = getDbUser();
		String dbPasswd = getDbPasswd();
		String url = String.format("jdbc:mysql://%s:3306", dbHost);
		String domain = "aon.esferalia.com";

		ConnectionInfo connectionInfo = new ConnectionInfo();
		connectionInfo.setUrl(url);
		connectionInfo.setUser(dbUser);
		connectionInfo.setPassword(dbPasswd);
		connectionInfo.setDriverClass("org.gjt.mm.mysql.Driver");

		context.getLogger().log(url + "," + dbUser + "," + dbPasswd);
		
		Scanner scanner = null;
		Connection connection = null;
		try {

			String database = connectionInfo.getDomainDatabase(domain);
			connection = connectionInfo.getDomainConnection(database);
			Settings settings = new Settings();
			settings.setRenderSchema(false);
			settings.setParamType(ParamType.INLINED);
			DSLContext dslContext = DSL.using(connection, settings);
			
			scanner = new Scanner(input);
			String criteria = scanner.next();
			Condition condition = getCondition(criteria);

			ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
			SalaryReport.print(dslContext, condition, byteOutput);
			
			output.write(Base64.getEncoder().encode(byteOutput.toByteArray()));

		} catch (AonConnectionException | JRException e) {
			throw new IOException(e);
		} finally {
			if ( scanner != null )
				scanner.close();
			if ( connection != null )
				try {
					connection.close();
				} catch (SQLException e) {
				}
		}

	}
	
	// ------------------------------------------------------------------------
	
	public static void main(String[] args) {
		getCondition("\"11111.pdf\"");
	}

}
