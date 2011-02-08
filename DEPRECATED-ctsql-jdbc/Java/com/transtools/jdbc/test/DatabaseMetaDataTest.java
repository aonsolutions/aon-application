package com.transtools.jdbc.test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
/**
 *  Description of the Class
 *
 *@author     eva
 *@created    May 21, 2001
 */
public class DatabaseMetaDataTest {

///////////////// PRIVATE //////////////////////////////////////

	java.util.Properties props;
	ResultSet resultset;
	ResultSet resultset1;
	ResultSet resultset2;
	ResultSet resultset3;
	String types[] = {"TABLE"};
	String tablename;
	String url;
	Connection con;
	DatabaseMetaData dbmd;


	/**
	 *  Constructor for the DatabaseMetaDataTester object
	 */
	public DatabaseMetaDataTest() {
		props = new Properties();
	}


	/**
	 *  Description of the Method
	 */
	public void connect() {
		try {
			Class.forName("com.transtools.jdbc.CtsqlJdbcDriver");
			url = "jdbc:ctsql://mother:20031/stock";
			props = new java.util.Properties();
			props.setProperty("user", "dctl");
			props.setProperty("password", "simple1");
			props.setProperty("DBPATH", "/disk3/caravel/demos/stock/bd");
			con = DriverManager.getConnection(url, props);
			dbmd = con.getMetaData();

		}
		catch (Exception ex) {
			System.out.println(ex.toString());
			ex.printStackTrace();
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void test() {
		try {
			resultset1 = dbmd.getColumnPrivileges(null, null, "t", "%");
			System.out.print("\t");
			System.out.println("ColumnPrivileges");

			while (resultset1.next()) {
				System.out.print("\t");
				System.out.print(resultset1.getString("TABLE_NAME"));
				System.out.print("\t");
				System.out.print(resultset1.getString("COLUMN_NAME"));
				System.out.print("\t");
				System.out.print(resultset1.getString("GRANTOR"));
				System.out.print("\t");
				System.out.print(resultset1.getString("GRANTEE"));
				System.out.print("\t");
				System.out.print(resultset1.getString("PRIVILEGE"));
				System.out.print("\t");
				System.out.println(resultset1.getString("IS_GRANTABLE"));
			}
			resultset1.close();
		}
		catch (SQLException ex) {
			System.out.println(ex.toString());
			ex.printStackTrace();
		}

	}


	/**
	 *  Description of the Method
	 */
	public void completeTest() {
		try {
			url = "jdbc:ctsql://mother:20031/stock";
			props = new java.util.Properties();
			props.setProperty("user", "dctl");
			props.setProperty("password", "simple1");
			props.setProperty("DBPATH", "/disk3/caravel/demos/stock/bd");
			con = DriverManager.getConnection(url, props);
			dbmd = con.getMetaData();

			resultset1 = dbmd.getBestRowIdentifier(
					null,
					null,
					"syscmd",
					0,
					false);
//      System.out.print(resultset1.findColumn("SCOPE"));
			while (resultset1.next()) {
				System.out.print("\t");
				System.out.print(resultset1.getShort("SCOPE"));
				System.out.print("\t\t");
				System.out.print(resultset1.getObject("COLUMN_NAME"));
				System.out.print("\t\t");
				System.out.print(resultset1.getShort("DATA_TYPE"));
				System.out.print("\t\t");
				System.out.print(resultset1.getString("TYPE_NAME"));
				System.out.print("\t\t");
				System.out.print(resultset1.getObject("COLUMN_SIZE"));
				System.out.print("\t\t");
				System.out.print(resultset1.getObject("BUFFER_LENGTH"));
				System.out.print("\t\t");
				System.out.print(resultset1.getObject("DECIMAL_DIGITS"));
				System.out.print("\t\t");
				System.out.println(resultset1.getObject("PSEUDO_COLUMN"));
			}
			resultset1.close();
			resultset1 = dbmd.getTableTypes();
			System.out.print("\t");
			System.out.println("TABLETYPES");

			while (resultset1.next()) {
				System.out.print("\t");
				System.out.println(resultset1.getString("TABLE_TYPE"));
			}
			resultset1.close();

			resultset1 = dbmd.getTablePrivileges(null, null, "%");
			System.out.print("\t");
			System.out.println("TablePrivileges");

			while (resultset1.next()) {
				System.out.print("\t");
				System.out.print(resultset1.getString("TABLE_NAME"));
				System.out.print("\t");
				System.out.print(resultset1.getString("GRANTOR"));
				System.out.print("\t");
				System.out.print(resultset1.getString("GRANTEE"));
				System.out.print("\t");
				System.out.print(resultset1.getString("PRIVILEGE"));
				System.out.print("\t");
				System.out.println(resultset1.getString("IS_GRANTABLE"));
			}
			resultset1.close();
			resultset1 = dbmd.getColumnPrivileges(null, null, "t", "%");
			System.out.print("\t");
			System.out.println("ColumnPrivileges");

			while (resultset1.next()) {
				System.out.print("\t");
				System.out.print(resultset1.getString("TABLE_NAME"));
				System.out.print("\t");
				System.out.print(resultset1.getString("COLUMN_NAME"));
				System.out.print("\t");
				System.out.print(resultset1.getString("GRANTOR"));
				System.out.print("\t");
				System.out.print(resultset1.getString("GRANTEE"));
				System.out.print("\t");
				System.out.print(resultset1.getString("PRIVILEGE"));
				System.out.print("\t");
				System.out.println(resultset1.getString("IS_GRANTABLE"));
			}
			resultset1.close();

			resultset1 = dbmd.getColumnPrivileges(null, null, "c", "%");
			System.out.print("\t");
			System.out.println("ColumnPrivileges");

			while (resultset1.next()) {
				System.out.print("\t");
				System.out.print(resultset1.getString("TABLE_NAME"));
				System.out.print("\t");
				System.out.print(resultset1.getString("COLUMN_NAME"));
				System.out.print("\t");
				System.out.print(resultset1.getString("GRANTOR"));
				System.out.print("\t");
				System.out.print(resultset1.getString("GRANTEE"));
				System.out.print("\t");
				System.out.print(resultset1.getString("PRIVILEGE"));
				System.out.print("\t");
				System.out.println(resultset1.getString("IS_GRANTABLE"));
			}
			resultset1.close();

			resultset = dbmd.getTables(null, null, "%", types);
			System.out.println("TABLAS");

			if (resultset.next()) {
				tablename = resultset.getString("TABLE_NAME");
				System.out.print(tablename);
				System.out.print(" ");
				System.out.print(resultset.getString("TABLE_TYPE"));
				System.out.print(" ");
				System.out.println(resultset.getString("REMARKS"));

				resultset1 = dbmd.getColumns(null, null, tablename, "%");
				System.out.println("\tCOLUMNS");

				while (resultset1.next()) {
					System.out.print("\t");
					System.out.print(resultset1.getString("TABLE_NAME"));
					System.out.print("\t");
					System.out.print(resultset1.getString("COLUMN_NAME"));
					System.out.print("\t");
					System.out.print(resultset1.getString("DATA_TYPE"));
					System.out.print("\t");
					System.out.println(resultset1.getString("COLUMN_DEF"));
				}
				resultset1.close();

				System.out.print("\t");
				System.out.println("INDEX");

				resultset2 = dbmd.getIndexInfo(null, null, tablename, false, false);
				while (resultset2.next()) {
					System.out.print("\t");
					System.out.print(resultset2.getString("TABLE_NAME"));
					System.out.print("\t");
					System.out.print(resultset2.getString("INDEX_NAME"));
					System.out.print("\t");
					System.out.print(resultset2.getString("ORDINAL_POSITION"));
					System.out.print("\t");
					System.out.print(resultset2.getString("COLUMN_NAME"));
					System.out.print("\t");
					System.out.println(resultset2.getString("ASC_OR_DESC"));
				}
				resultset2.close();

				resultset1 = dbmd.getPrimaryKeys(null, null, tablename);

				System.out.print("\t");
				System.out.println("PRIMARY");

				while (resultset1.next()) {
					System.out.print("\t");
					System.out.print(resultset1.getString("PK_NAME"));
					System.out.print("\t");
					System.out.print(resultset1.getString("COLUMN_NAME"));
					System.out.print("\t");
					System.out.println(resultset1.getString("KEY_SEQ"));
				}
				resultset1.close();

				resultset1 = dbmd.getCrossReference(
						null,
						null,
						null,
						null,
						null,
						tablename
						);
				System.out.print("\t");
				System.out.println("FK");

				while (resultset1.next()) {
					System.out.print("\t");
					System.out.print(resultset1.getString("PKTABLE_NAME"));
					System.out.print("\t ");
					System.out.print(resultset1.getString("PKCOLUMN_NAME"));
					System.out.print("\t");
					System.out.print(resultset1.getString("KEY_SEQ"));
					System.out.print("\t");
					System.out.print(resultset1.getShort("UPDATE_RULE"));
					System.out.print("\t");
					System.out.print(resultset1.getShort("DELETE_RULE"));
					System.out.print("\t");
					System.out.println(resultset1.getString("FK_NAME"));
				}
				resultset1.close();

				resultset1 = dbmd.getImportedKeys(
						null,
						null,
						tablename
						);
				System.out.print("\t");
				System.out.println("IMPFK");

				while (resultset1.next()) {
					System.out.print("\t");
					System.out.print(resultset1.getString("PKTABLE_NAME"));
					System.out.print("\t ");
					System.out.print(resultset1.getString("PKCOLUMN_NAME"));
					System.out.print("\t");
					System.out.print(resultset1.getString("KEY_SEQ"));
					System.out.print("\t");
					System.out.print(resultset1.getShort("UPDATE_RULE"));
					System.out.print("\t");
					System.out.print(resultset1.getShort("DELETE_RULE"));
					System.out.print("\t");
					System.out.println(resultset1.getString("FK_NAME"));
				}
				resultset1.close();

				resultset1 = dbmd.getExportedKeys(
						null,
						null,
						tablename
						);
				System.out.print("\t");
				System.out.println("EXPFK");

				while (resultset1.next()) {
					System.out.print("\t");
					System.out.print(resultset1.getString("PKTABLE_NAME"));
					System.out.print("\t ");
					System.out.print(resultset1.getString("PKCOLUMN_NAME"));
					System.out.print("\t");
					System.out.print(resultset1.getString("KEY_SEQ"));
					System.out.print("\t");
					System.out.print(resultset1.getShort("UPDATE_RULE"));
					System.out.print("\t");
					System.out.print(resultset1.getShort("DELETE_RULE"));
					System.out.print("\t");
					System.out.println(resultset1.getString("FK_NAME"));
				}
				resultset1.close();

			}
			resultset.close();
			con.close();

			System.out.println("FIN FIN FIN FIN FIN");

		}
		catch (Exception ex) {
			System.out.println(ex.toString());
			ex.printStackTrace();
		}
	}


	/**
	 *  The main program for the CatalogTester class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args) {
		DatabaseMetaDataTest catalogTester1 = new DatabaseMetaDataTest();
		catalogTester1.connect();
		catalogTester1.completeTest();
	}
}
