package net.aonsolutions.payroll.report;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.Properties;
import java.util.TimeZone;

import org.jooq.DSLContext;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import net.sf.jasperreports.engine.JRException;

public class SQLSalaryReportTestCase {

	private Connection connection;

	public static String getDbPort() {
		return System.getProperty("dbPort", "3306");
	}

	public static String getDbHost() {
		return System.getProperty("dbHost", "127.0.0.1");
	}

	public static String getDbName() {
		//return System.getProperty("dbName", "sig-grupo-esferalia");
		return System.getProperty("dbName", "pro-aonsolutions-net");
	}

	public static String getDbUser() {
		return System.getProperty("dbUser", "dbuser");
	}

	public static String getDbPasswd() {
		return System.getProperty("dbPasswd", "serubd2000");
	}

	public static String getDbTimeZone() {
		return System.getProperty("dbTimeZone", TimeZone.getDefault().getID());
	}

	public static Connection connect() throws ClassNotFoundException, SQLException{
		// first of all load JDBC driver
		Class.forName("com.mysql.jdbc.Driver");

		String dbHost = getDbHost();
		String dbPort = getDbPort();
		String dbName = getDbName();
		String dbUser = getDbUser();
		String dbPasswd = getDbPasswd();
		String dbTimeZone = getDbTimeZone();

		Properties properties = new Properties();
		properties.setProperty("user", dbUser);
		properties.setProperty("password", dbPasswd);
		properties.setProperty("serverTimezone", dbTimeZone);
		String url = String.format("jdbc:mysql://%s:%s/%s", dbHost, dbPort, dbName);
		Connection connection = DriverManager.getConnection(url, properties);


		return connection;
	}

	@Before
	public void setUp() throws ClassNotFoundException, SQLException{
		connection = connect();
	}


	@Ignore("Comming soon")
	@Test
	public void testSalaryReportI() throws SQLException, JRException, IOException {

		Settings settings = new  Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		DSLContext dslContext = DSL.using(connection, settings);


		List<Integer> salaryIds =
		dslContext
		.select()
		.from(SALARY)
		.where(SALARY.TYPE.eq((byte)0))
		.orderBy(SALARY.START_DATE.desc())
		.limit(1)
		.fetch(SALARY.ID)
		;

		FileOutputStream fos = new FileOutputStream("salary.pdf");

		try {
			SalaryReport.print(dslContext, SALARY.ID.in(salaryIds), fos);
		} catch ( Throwable t ) {
			t.printStackTrace();
		}

		fos.close();
	}


}
