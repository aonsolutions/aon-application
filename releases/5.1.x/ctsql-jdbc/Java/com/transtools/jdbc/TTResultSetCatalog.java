package com.transtools.jdbc;

import com.transtools.ctsql.CtsqlCursor;
import com.transtools.ctsql.CtsqlException;
import com.transtools.ctsql.CtsqlType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *  Description of the Class
 *
 *@author     eva
 *@created    April 4, 2001
 */
public class TTResultSetCatalog extends TTResultSet {
	private String tablename = null;
	private Connection connection;


	/**
	 *  Constructor for the TTResultSetCatalog object
	 *
	 *@param  connection        Description of Parameter
	 *@param  cursor            Description of Parameter
	 *@param  hosts             Description of Parameter
	 *@param  statement         Description of Parameter
	 *@exception  SQLException  Description of Exception
	 */
	public TTResultSetCatalog(TTConnection connection, CtsqlCursor cursor,
			CtsqlType[] hosts, TTStatement statement) throws SQLException {

		super(connection, cursor, hosts, statement);
		this.connection = connection;
		tablename = null;
	}


	/**
	 *  Constructor for the TTResultSetCatalog object
	 *
	 *@param  connection        Description of Parameter
	 *@param  cursor            Description of Parameter
	 *@param  statement         Description of Parameter
	 *@exception  SQLException  Description of Exception
	 */
	public TTResultSetCatalog(TTConnection connection, CtsqlCursor cursor, TTStatement statement) throws SQLException {
		super(connection, cursor, null, statement);
		this.connection = connection;
		tablename = null;
	}


	/**
	 *  Sets the TableName attribute of the TTResultSetCatalog object
	 *
	 *@param  tablename  The new TableName value
	 */
	public void setTableName(String tablename) {
		this.tablename = tablename;
	}


	/**
	 *  Gets the TableName attribute of the TTResultSetCatalog object
	 *
	 *@return    The TableName value
	 */
	public String getTableName() {
		return tablename;
	}


	/**
	 *  Description of the Method
	 *
	 *@exception  SQLException  Description of Exception
	 */
	public synchronized void close() throws SQLException {
		String table = getTableName();
		super.close();
		try{
			getCursor().release();
		} catch (CtsqlException e) {
		}
		if (table != null) {
			Statement statement;
			statement = connection.createStatement();
			try {

				statement.execute("drop table " + table);
				statement.close();
			}
			catch (Exception e) {
			}
		}
		setTableName(null);
		/*
		 * Eva 5-04-2001
		 * statement1 = (TTStatement) this.getStatement();
		 * this.release();
		 * this.setStatement(null);
		 * Eva 5-04-2001
		 */
		/*
		 * }
		 * catch (CtsqlException e) {
		 * e.toString();
		 * }
		 */
	}

	private static String rTrim(String value) {
		int vLen;
		int len;

		if(value != null){
			vLen = value.length();
		} else {
			vLen = 0;
		}
		len = vLen;

		while (len > 0 && value.charAt(len - 1) == ' ') {
			len--;
		}
		return (len < vLen) ? value.substring(0, len) : value;
	}

	public String getString(int columnIndex) throws java.sql.SQLException {
		return rTrim(super.getString(columnIndex));
	}
	public String getString(String columnName) throws java.sql.SQLException {
		return rTrim(super.getString(columnName));
	}

}
