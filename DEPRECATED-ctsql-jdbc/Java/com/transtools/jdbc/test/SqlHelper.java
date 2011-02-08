package com.transtools.jdbc.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class SqlHelper
{
	private static final String DRIVER = "com.transtools.jdbc.CtsqlJdbcDriver";
	private static final String PORT = "20000";
	private static final String PROTOCOL = "ctsql";

	public Connection connection;
	private String createTableFields;

	public SqlHelper( Connection aConnection )
	{
		connection = aConnection;
	}

	protected String createList(String[] values)
	{
		StringBuffer list = new StringBuffer();

		if ( (values != null) && (values.length > 0) ) {
			int i;

			for( i = 0; i < (values.length-1); i++ ) {
				list.append( values[i] );
				list.append( ", " );
			}
			list.append( values[i] );
		}
		return list.toString();
	}

	public void executeStatement(String stmtString ) throws SQLException
	{
		Statement stmt = connection.createStatement();
		stmt.execute( stmtString);
		stmt.close();
	}

	public boolean existsTable(String tableName) throws SQLException
	{
		boolean exists = false;

		Statement stmt = connection.createStatement();
		exists = stmt.executeQuery( "SELECT tabid FROM systables WHERE tabname ='"+tableName+"'").next();
		stmt.close();
		return exists;
	}

	public void prepareCreateTable()
	{
		createTableFields = "";
	}

	public void setCreateTableFields(String[] fields)
	{
		createTableFields = createList(fields);
	}

	public void addCreateTableField(String field)
	{
		if ( createTableFields.length() > 0 )
			createTableFields += ", ";
		createTableFields += field;
	}

	public void createTable(String tableName) throws SQLException
	{
		String stmtString;

		if (createTableFields.length() == 0)
			return;

		stmtString = "CREATE TABLE " + tableName + " (" + createTableFields + ")";
		executeStatement( stmtString );
	}

	public void createTableEx(String tableName, String[] fields) throws SQLException
	{
		if ( existsTable(tableName) )
			dropTable( tableName );

		prepareCreateTable();
		setCreateTableFields(fields);
		createTable(tableName);
	}

	public void dropTable(String tableName) throws SQLException
	{
		executeStatement( "DROP TABLE " + tableName );
	}

	public static Connection getConnection() {
		String host = "mother";
		String dbpath = "/disk3/caravel/demos/insecuss/bd";
		String dbname = "test";
		String user = "dctl";
		String password = "simple1";

		registerDriver(DRIVER);
		String url = createUrl(host, PORT, dbname);

		Properties props = new Properties();
		props.put("user", user);
		props.put("password", password);

		props.setProperty( "DBPATH", dbpath );
		return openConnection( url, props );
	}

	public static Connection getConnection(String host, String port, String dbname, Properties props) {
		registerDriver(DRIVER);
		String url = createUrl(host, port, dbname);
		return openConnection( url, props );
	}

	private static Class registerDriver(String driver) {
		java.lang.Class obj = null;
		try {
			obj = Class.forName(driver);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return obj;
	}

	private static Connection openConnection(String url, String user, String password) {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return connection;
	}

	private static Connection openConnection(String url) {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(url);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return connection;
	}

	private static Connection openConnection(String url, Properties props ) {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(url, props);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return connection;
	}

	private static String createUrl(String host, String port, String name) {
		return new String("jdbc:" + PROTOCOL + "://" + host + ":" + port + "/" + name );
	}

	private static String createUrl(String host, String name) {
		return new String("jdbc:" + PROTOCOL + "://" + host + ":" + PORT + "/" + name );
	}

}
