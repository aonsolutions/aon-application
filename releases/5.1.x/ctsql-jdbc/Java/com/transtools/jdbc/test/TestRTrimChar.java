package com.transtools.jdbc.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class TestRTrimChar   {


  private static final String DRIVER = "com.transtools.jdbc.CtsqlJdbcDriver";
  public static final String URL = "jdbc:ctsql://localhost:20010/stock;RTRIMCHAR=true;DBPATH=C:\\expand\\databases\\basic_demo";
  public static final String LOGIN = "ctsql";
  public static final String PASSWD = "ctsql";

  private Connection con;
  java.util.Properties props;

  public TestRTrimChar() throws ClassNotFoundException, SQLException {
		props = new Properties();
		Class.forName(DRIVER);

		props = new java.util.Properties();
		props.setProperty( "user", LOGIN );
		props.setProperty( "password", PASSWD );
		this.con = DriverManager.getConnection( URL, props );
  }

  private void test() {
		Statement statement=null;
		ResultSet rs = null;
		short result = -1;

		String stmt = "select * from states";
		try {
			statement = con.createStatement();
			rs = statement.executeQuery(stmt);
			ResultSetMetaData metadata = rs.getMetaData();
			int num = metadata.getColumnCount();

			while(rs.next()){
				for(int i=0; i < num; i++){
					String ret = rs.getString(i+1);
					System.out.print(ret+"|");
				}
				System.out.println("");				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
  }

  public static void main(String[] arg) {
	try {
		new TestRTrimChar().test();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

}
