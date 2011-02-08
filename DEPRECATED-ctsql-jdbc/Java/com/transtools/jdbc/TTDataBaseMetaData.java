/*
 *  Copyright 2003
 *
 *  TransTOOLs, S.A.
 *  All rights reserved
 */
package com.transtools.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.transtools.ctsql.CtsqlType;
import com.transtools.jdbc.metadata.FixedResultsetColumnMetadata;
import com.transtools.jdbc.metadata.FixedResultsetMetadata;
import com.transtools.jdbc.metadata.ForeignKeyRow;
import com.transtools.jdbc.metadata.PrimaryKeyRow;
import com.transtools.jdbc.metadata.PropertiesBeanResultset;
import com.transtools.jdbc.metadata.Syscolumn;
import com.transtools.jdbc.metadata.Systable;

/**
 *  TTDataBaseMetaData Comprehensive information about the database as a whole.
 *  Many of the methods return lists of information in the form of ResultSet
 *  objects. Some of these methods take arguments that are String patterns.
 *  These arguments all have names such as fooPattern. Within a pattern String,
 *  "%" means match any substring of 0 or more characters, and "_" means match
 *  any one character. Only metadata entries matching the search pattern are
 *  returned. If a search pattern argument is set to a null ref, that argument's
 *  criteria will be dropped from the search. An SQLException will be thrown if
 *  a driver does not support a meta data method. In the case of methods that
 *  return a ResultSet, either a ResultSet (which may be empty) is returned or a
 *  SQLException is thrown.
 *
 * @author     eva
 * @created    March 7, 2001
 * @version    $Revision: 1.30 $
 */

class TTDataBaseMetaData implements DatabaseMetaData {
	public static final String DEFAULT_SCHEMA = "DEFAULT_SCHEMA";
// Eva contador para crear las tablas temporales que se necesitan para leer el catálogo.
	static int counter = 0;
	// New Raul (Co-De) 21-04-2003. Monitor para sincronizar el acceso el contador de tablas temporales ( arriba ).
	static Object counter_monitor = new Object();
	// End Raul 21-03-2003.

	private Connection connection;
// New Eva Febrero 2001
	private int[] java_types = {java.sql.Types.CHAR, java.sql.Types.SMALLINT,
		java.sql.Types.INTEGER, java.sql.Types.TIME,
		0, java.sql.Types.DECIMAL, java.sql.Types.INTEGER,
		java.sql.Types.DATE, java.sql.Types.DECIMAL, 0, java.sql.Types.TIMESTAMP, java.sql.Types.BINARY};
	private String[] sql_tnames = {"char", "smallint", "integer", "time", "",
		"decimal", "serial", "date", "money", "", "datetime", "binary"};
// Paco 4-2-2004. Binary también existe
	private String[] TMP_TypeInfo = {
		"'char',     1,32767, '\"', '\"', 'max length',      1,     1, 3, NULL,    0, NULL, NULL, NULL, NULL",
		"'smallint', 5,    5, NULL, NULL, NULL,              1,     0, 3,    0,    0, NULL, NULL, NULL, NULL",
		"'integer',  4,    8, NULL, NULL, NULL,              1,     0, 3,    0,    0, NULL, NULL, NULL, NULL",
		"'decimal',  3,   32, NULL, NULL, 'precision,scale', 1,     0, 3,    0,    0, NULL, NULL, NULL, NULL",
		"'money',    3,   32, NULL, NULL, 'precision',       1,     0, 3,    0,    1, NULL, NULL, NULL, NULL",
		"'date',     9,   10, NULL, NULL, NULL,              1,     0, 3,    0,    0, NULL, NULL, NULL, NULL",
		"'time',    10,    8, NULL, NULL, NULL,              1,     0, 3,    0,    0, NULL, NULL, NULL, NULL",
		"'datetime', 11, 20, NULL, NULL, NULL, 1, 0, 3, 0, 0, NULL, NULL, NULL, NULL",
		"'binary',     1,32767, '\"', '\"', 'max length',      1,     1, 3, NULL,    0, NULL, NULL, NULL, NULL"
		};
	

	private static FixedResultsetMetadata importExportKeysMetadata = new FixedResultsetMetadata(
			new FixedResultsetColumnMetadata[] {
					new FixedResultsetColumnMetadata("PKTABLE_CAT", Types.CHAR, 18, "CHAR", ResultSetMetaData.columnNullable),
					new FixedResultsetColumnMetadata("PKTABLE_SCHEM", Types.CHAR, 18, "CHAR", ResultSetMetaData.columnNullable),
					new FixedResultsetColumnMetadata("PKTABLE_NAME", Types.CHAR, 18, "CHAR", ResultSetMetaData.columnNoNulls),
					new FixedResultsetColumnMetadata("PKCOLUMN_NAME", Types.CHAR, 18, "CHAR", ResultSetMetaData.columnNoNulls),
					new FixedResultsetColumnMetadata("FKTABLE_CAT", Types.CHAR, 18, "CHAR", ResultSetMetaData.columnNullable),
					new FixedResultsetColumnMetadata("FKTABLE_SCHEM", Types.CHAR, 18, "CHAR", ResultSetMetaData.columnNullable),
					new FixedResultsetColumnMetadata("FKTABLE_NAME", Types.CHAR, 18, "CHAR", ResultSetMetaData.columnNoNulls),
					new FixedResultsetColumnMetadata("FKCOLUMN_NAME", Types.CHAR, 18, "CHAR", ResultSetMetaData.columnNoNulls),
					new FixedResultsetColumnMetadata("KEY_SEQ", Types.SMALLINT, 10, "SMALLINT", 6, 0, ResultSetMetaData.columnNoNulls),
					new FixedResultsetColumnMetadata("UPDATE_RULE", Types.SMALLINT, 10, "SMALLINT", 6, 0, ResultSetMetaData.columnNoNulls),
					new FixedResultsetColumnMetadata("DELETE_RULE", Types.SMALLINT, 10, "SMALLINT", 6, 0, ResultSetMetaData.columnNoNulls),
					new FixedResultsetColumnMetadata("FK_NAME", Types.CHAR, 30, "CHAR", ResultSetMetaData.columnNullable),
					new FixedResultsetColumnMetadata("PK_NAME", Types.CHAR, 30, "CHAR", ResultSetMetaData.columnNullable),
					new FixedResultsetColumnMetadata("DEFERRABILITY", Types.SMALLINT, 10, "SMALLINT", 6, 0, ResultSetMetaData.columnNoNulls)
			}
			);
	
	private static FixedResultsetMetadata primaryKeysMetadata = new FixedResultsetMetadata(
			new FixedResultsetColumnMetadata[] {
					new FixedResultsetColumnMetadata("TABLE_CAT", Types.CHAR, 18, "CHAR", ResultSetMetaData.columnNullable),
					new FixedResultsetColumnMetadata("TABLE_SCHEM", Types.CHAR, 18, "CHAR", ResultSetMetaData.columnNullable),
					new FixedResultsetColumnMetadata("TABLE_NAME", Types.CHAR, 18, "CHAR", ResultSetMetaData.columnNoNulls),
					new FixedResultsetColumnMetadata("COLUMN_NAME", Types.CHAR, 18, "CHAR", ResultSetMetaData.columnNoNulls),
					new FixedResultsetColumnMetadata("KEY_SEQ", Types.SMALLINT, 10, "SMALLINT", 6, 0, ResultSetMetaData.columnNoNulls),
					new FixedResultsetColumnMetadata("PK_NAME", Types.CHAR, 30, "CHAR", ResultSetMetaData.columnNullable)
			}
			);
	
// END New Eva Febrero 2001

	
	private Map systablesMap;
	private static final int NPARTS = 8;
	/**
	 *  Constructor for the TTDataBaseMetaData object
	 *
	 * @param  aConnection  Description of Parameter
	 */
	public TTDataBaseMetaData(Connection aConnection) {
		connection = aConnection;
	}


	/*
	 *  @jdbc.pending		falta por implementar
	 */
	/**
	 *  Gets the URL attribute of the TTDataBaseMetaData object
	 *
	 * @return                   The URL value
	 * @exception  SQLException  Description of Exception
	 */
	public String getURL() throws SQLException {
		//paco return null;
		return ((TTConnection)getConnection()).getUrl();
	}


	/*
	 *  @jdbc.pending		falta por implementar
	 */
	/**
	 *  Gets the UserName attribute of the TTDataBaseMetaData object
	 *
	 * @return                   The UserName value
	 * @exception  SQLException  Description of Exception
	 */
	public String getUserName() throws SQLException {
		return null;
	}


	/**
	 *  isReadOnly method: Is the database in read-only mode?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean isReadOnly() throws SQLException {
		return false;
	}


	/**
	 *  getDatabaseProductName method: What's the name of this database product?
	 *
	 * @return                   database product name
	 * @exception  SQLException  Description of Exception
	 */
	public String getDatabaseProductName() throws SQLException {
		return "MultiBase";
	}


	/**
	 *  getDatabaseProductVersion method: What's the version of this database
	 *  product?
	 *
	 * @return                   database version
	 * @exception  SQLException  Description of Exception
	 */
	public String getDatabaseProductVersion() throws SQLException {
		return "3.0";
	}


	/**
	 *  getDriverName method: What's the name of this JDBC driver?
	 *
	 * @return                   JDBC driver name
	 * @exception  SQLException  Description of Exception
	 */
	public String getDriverName() throws SQLException {
		return "TransTOOLs Ctsql JDBC Driver";
	}


	/**
	 *  getDriverVersion method: What's the name of this JDBC driver?
	 *
	 * @return                   JDBC driver version
	 * @exception  SQLException  Description of Exception
	 */
	public String getDriverVersion() throws SQLException {
		StringBuffer version = new StringBuffer();

		version.append(getDriverMajorVersion());
		version.append('.');
		version.append(getDriverMinorVersion());
		return version.toString();
	}


	/**
	 *  getDriverMajorVersion method: What's this JDBC driver's major version
	 *  number?
	 *
	 * @return    JDBC driver major version
	 */
	public int getDriverMajorVersion() {
		return 1;
	}


	/**
	 *  getDriverMinorVersion method: What's this JDBC driver's minor version
	 *  number?
	 *
	 * @return    JDBC driver minor version number
	 */
	public int getDriverMinorVersion() {
		return 0;
	}


	/**
	 *  What's the string used to quote SQL identifiers? This returns a space " "
	 *  if identifier quoting isn't supported. A JDBC compliant driver always uses
	 *  a double quote character.
	 *
	 * @return                   the quoting string
	 * @exception  SQLException  Description of Exception
	 */
	public String getIdentifierQuoteString() throws SQLException {
		return " ";
	}


	/*
	 *  @jdbc.pending falta por implementar impementada por eva 21-03-2001
	 */
	/**
	 *  Gets the SQLKeywords attribute of the TTDataBaseMetaData object
	 *
	 * @return                   The SQLKeywords value
	 * @exception  SQLException  Description of Exception
	 */
	public String getSQLKeywords() throws SQLException {

		return "add,all,alter,and,any,as,asc,avg,before,begin,between,by,cascades,char," +
			"check,close,collating,column,commit,connect,count,create,current," +
			"database,date,day,dba,decimal,default,delete,desc,distinct,downshift," +
			"drop,exclusive,exists,float,for,foreign,format,from,grant,group,having," +
			"hms,hour,in,index,insert,integer,intersect,into,is,key,label,left,like," +
			"lock,log,matches,max,mdy,min,minus,minute,mode,modify,money,month," +
			"noentry,not,noupdate,now,nowait,null,of,on,option,or,order,outer,picture," +
			"primary,privileges,public,references,release,remove,rename,repair," +
			"resource,restrict,revoke,right,rollback,rollforward,rowid,savepoint," +
			"second,select,serial,set,share,smallfloat,smallint,start,statistics," +
			"sum,synonym,table,temp,time,to,today,typelength,union,unique,unlock," +
			"update,upshift,user,values,view,weekday,where,with,work,year,zerofill";
	}


	/**
	 *  getNumericFunctions gets a comma-separated list of math functions. These
	 *  are the X/Open CLI math function names used in the JDBC function escape
	 *  clause.
	 *
	 * @return                   the list.
	 * @exception  SQLException  Description of Exception
	 */
	public String getNumericFunctions() throws SQLException {
		return "";
	}


	/**
	 *  getStringFunctions gets a comma-separated list of string functions. These
	 *  are the X/Open CLI string function names used in the JDBC function escape
	 *  clause.
	 *
	 * @return                   the list.
	 * @exception  SQLException  Description of Exception
	 */
	public String getStringFunctions() throws SQLException {
		return "";
	}


	/**
	 *  getSystemFunctions gets a comma-separated list of system functions. These
	 *  are the X/Open CLI system function names used in the JDBC function escape
	 *  clause.
	 *
	 * @return                   the list.
	 * @exception  SQLException  Description of Exception
	 */
	public String getSystemFunctions() throws SQLException {
// MOD Eva 07-03-2001
//		return "TYPELENGTH";
		return new String("user,today,now,typelength");
// END MOD Eva 07-03-2001
	}


	/**
	 *  getTimeDateFunctions gets a comma-separated list of time and date
	 *  functions.
	 *
	 * @return                   the list.
	 * @exception  SQLException  Description of Exception
	 */
	public String getTimeDateFunctions() throws SQLException {
		return "DATE,DAY,HMS,HOUR,MDY,MINUTE,MONTH,MT,TIME,TOMT,SECOND,WEEKDAY,YEAR";
	}


	/**
	 *  getSearchStringEscape gets the string that can be used to escape wildcard
	 *  characters. This is the string that can be used to escape '_' or '%' in the
	 *  string pattern style catalog search parameters. The '_' character
	 *  represents any single character. The '%' character represents any sequence
	 *  of zero or more characters.
	 *
	 * @return                   the string used to escape wildcard characters
	 * @exception  SQLException  Description of Exception
	 */
	public String getSearchStringEscape() throws SQLException {
		return "\\";
	}


	/**
	 *  getExtraNameCharacters gets all the "extra" characters that can be used in
	 *  unquoted identifier names (those beyond a-z, A-Z, 0-9 and _).
	 *
	 * @return                   the string containing the extra characters
	 * @exception  SQLException  Description of Exception
	 */
	public String getExtraNameCharacters() throws SQLException {
		return "";
	}


	/**
	 *  getSchemaTerm method is not supported.
	 *
	 * @return                   The SchemaTerm value
	 * @exception  SQLException  Description of Exception
	 */
	public String getSchemaTerm() throws SQLException {
		//ExceptionManager.getManager().throwException(301, "getSchemaTerm method is not supported.");
		//return null;
		return "schema";
		// This statement will be never reached, but the compiler needs it.
	}


	/**
	 *  getProcedureTerm method is not supported.
	 *
	 * @return                   The ProcedureTerm value
	 * @exception  SQLException  Description of Exception
	 */
	public String getProcedureTerm() throws SQLException {
		ExceptionManager.getManager().throwException(301, "getProcedureTerm method is not supported.");
		return null;
	}


	/**
	 *  getCatalogTerm method is not supported.
	 *
	 * @return                   The CatalogTerm value
	 * @exception  SQLException  Description of Exception
	 */
	public String getCatalogTerm() throws SQLException {
		ExceptionManager.getManager().throwException(301, "getCatalogTerm method is not supported.");
		return null;
	}


	/**
	 *  isCatalogAtStart method is not supported.
	 *
	 * @return                   The CatalogAtStart value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean isCatalogAtStart() throws SQLException {
		return true;
	}


	/**
	 *  getCatalogSeparator method is not supported.
	 *
	 * @return                   The CatalogSeparator value
	 * @exception  SQLException  Description of Exception
	 */
	public String getCatalogSeparator() throws SQLException {
		return ".";
	}


	/**
	 *  getDefaultTransactionIsolation method: What's the database's default
	 *  transaction isolation level?
	 *
	 * @return                   the default isolation level
	 * @exception  SQLException  Description of Exception
	 */
	public int getDefaultTransactionIsolation() throws SQLException {
		return Connection.TRANSACTION_READ_UNCOMMITTED;
	}


	//--------------------------------------------------------------------
	//   The following group of methods exposes various limitations
	//   based on the target database with the current driver.
	//   Unless otherwise specified, a result of zero means there is no
	//   limit or the limit is not known.
	//------------------------- 	-------------------------------------------

	// Binary not soported

	/**
	 *  getMaxBinaryLiteralLength method is not supported.
	 *
	 * @return                   The MaxBinaryLiteralLength value
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxBinaryLiteralLength() throws SQLException {
		//paco ExceptionManager.getManager().throwException(301, "getMaxBinaryLiteralLength method is not supported.");
		return -1;
		// This statement will be never reached, but the compiler needs it.
	}


	/**
	 *  getMaxCharLiteralLength method: What's the max length for a character
	 *  literal?
	 *
	 * @return                   max literal length; a result of zero means that
	 *      there is no limit or the limit is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxCharLiteralLength() throws SQLException {
		return 32767;
	}


	/**
	 *  getMaxColumnNameLength method: What's the limit on column name length?
	 *
	 * @return                   max column name length; a result of zero means
	 *      that there is no limit or the limit is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxColumnNameLength() throws SQLException {
		return 18;
	}


	/**
	 *  getMaxColumnsInGroupBy method: What's the maximum number of columns in a
	 *  "GROUP BY" clause?
	 *
	 * @return                   max number of columns; a result of zero means that
	 *      there is no limit or the limit is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxColumnsInGroupBy() throws SQLException {
		return 8;
	}


	/**
	 *  getMaxColumnsInIndex method: What's the maximum number of columns allowed
	 *  in an index?
	 *
	 * @return                   max number of columns; a result of zero means that
	 *      there is no limit or the limit is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxColumnsInIndex() throws SQLException {
		return 8;
	}


	/**
	 *  getMaxColumnsInOrderBy method: What's the maximum number of columns in an
	 *  "ORDER BY" clause?
	 *
	 * @return                   max number of columns; a result of zero means that
	 *      there is no limit or the limit is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxColumnsInOrderBy() throws SQLException {
		/*
		 *  Mod. MA 11-10-2001 para contemplar bases de datos Caravel
		 *  return 8;
		 */
		int maxOrderBy = 8;
		try {
			Statement testOrderBy = connection.createStatement();
			testOrderBy.executeQuery("select part16 from systables where tabid = 1");
			maxOrderBy = 16;
			testOrderBy.close();
		} catch (SQLException e) {
			maxOrderBy = 8;
		}
		/*
		 *  End Mod MA 11-10-2001
		 */
		return maxOrderBy;
	}


	/**
	 *  getMaxColumnsInSelect method: What's the maximum number of columns in a
	 *  "SELECT" list?
	 *
	 * @return                   max number of columns; a result of zero means that
	 *      there is no limit or the limit is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxColumnsInSelect() throws SQLException {
		return 32767;
	}


	/**
	 *  getMaxColumnsInTable method: What's the maximum number of columns in a
	 *  table?
	 *
	 * @return                   max number of columns; a result of zero means that
	 *      there is no limit or the limit is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxColumnsInTable() throws SQLException {
		return 32767;
	}


	/**
	 *  getMaxConnections method: How many active connections can we have at a time
	 *  to this database?
	 *
	 * @return                   max number of columns; a result of zero means that
	 *      there is no limit or the limit is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxConnections() throws SQLException {
		return 0;
	}


	/**
	 *  getMaxCursorNameLength method: What's the maximum cursor name length?
	 *
	 * @return                   max number of columns; a result of zero means that
	 *      there is no limit or the limit is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxCursorNameLength() throws SQLException {
		return 18;
	}


	/**
	 *  getMaxIndexLength method: Retrieves the maximum number of bytes for an
	 *  index, including all of the parts of the index.
	 *
	 * @return                   max index length in bytes, which includes the
	 *      composite of all the constituent parts of the index; a result of zero
	 *      means that there is no limit or the limit is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxIndexLength() throws SQLException {
		return 120;
	}


	/**
	 *  getMaxSchemaNameLength method is not supported.
	 *
	 * @return                   The MaxSchemaNameLength value
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxSchemaNameLength() throws SQLException {
		//paco ExceptionManager.getManager().throwException(301, "getMaxSchemaNameLength method is not supported.");
		//paco return -1;
		return 14;
		// This statement will be never reached, but the compiler needs it.
	}


	/**
	 *  getMaxProcedureNameLength method is not supported.
	 *
	 * @return                   The MaxProcedureNameLength value
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxProcedureNameLength() throws SQLException {
		//paco ExceptionManager.getManager().throwException(301, "getMaxProcedureNameLength method is not supported.");
		return 20;
		// This statement will be never reached, but the compiler needs it.
	}


	/**
	 *  getMaxCatalogNameLength method is not supported.
	 *
	 * @return                   The MaxCatalogNameLength value
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxCatalogNameLength() throws SQLException {
		ExceptionManager.getManager().throwException(301, "getMaxCatalogNameLength method is not supported.");
		return -1;
		// This statement will be never reached, but the compiler needs it.
	}


	/**
	 *  getMaxRowSize method: What's the maximum length of a single row?
	 *
	 * @return                   max row size in bytes; a result of zero means that
	 *      there is no limit or the limit is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxRowSize() throws SQLException {
		return 32767;
	}


	/**
	 *  getMaxStatementLength method: What's the maximum length of an SQL
	 *  statement?
	 *
	 * @return                   tmax length in bytes; a result of zero means that
	 *      there is no limit or the limit is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxStatementLength() throws SQLException {
		return 4096;
	}


	/**
	 *  getMaxStatements method: How many active statements can we have open at one
	 *  time to this database?
	 *
	 * @return                   the maximum number of statements that can be open
	 *      at one time; a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxStatements() throws SQLException {
		return 32767;
	}


	/**
	 *  getMaxTableNameLength method: What's the maximum length of a table name?
	 *
	 * @return                   max length in bytes; a result of zero means that
	 *      there is no limit or the limit is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxTableNameLength() throws SQLException {
		/*
		 *  Mod. MA 26-02-2002 para contemplar bases de datos Caravel
		 *  return 18;
		 */
		int maxTableName;
		Statement testIdentifierLen = connection.createStatement();
		ResultSet rs = testIdentifierLen.executeQuery("select collength from syscolumns where tabid = 1 and colno = 1");
		rs.next();
		maxTableName = rs.getInt(1);
		rs.close();
		testIdentifierLen.close();
		return maxTableName;
		/*
		 *  End Mod MA 26-02-2002
		 */
	}


	/**
	 *  getMaxTablesInSelect method: What's the maximum number of tables in a
	 *  SELECT statement?
	 *
	 * @return                   the maximum number of tables allowed in a SELECT
	 *      statement; a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxTablesInSelect() throws SQLException {
//		return 0;
		return 64;
	}


	/**
	 *  getMaxUserNameLength method: What's the maximum length of a user name?
	 *
	 * @return                   max user name length in bytes; a result of zero
	 *      means that there is no limit or the limit is not known
	 * @exception  SQLException  Description of Exception
	 */
	public int getMaxUserNameLength() throws SQLException {
		return 8;
	}


	//--------------------------------------------------------------------
	//     Methods that return ResultSet objects to describe
	//     database objects:
	//--------------------------------------------------------------------

	/**
	 *  getProcedures method is not supported.
	 *
	 * @param  catalog               Description of Parameter
	 * @param  schemaPattern         Description of Parameter
	 * @param  procedureNamePattern  Description of Parameter
	 * @return                       The Procedures value
	 * @exception  SQLException      Description of Exception
	 */
	public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
		return nullResultSet();	
	}


	/**
	 *  getProcedureColumns method is not supported.
	 *
	 * @param  catalog               Description of Parameter
	 * @param  schemaPattern         Description of Parameter
	 * @param  procedureNamePattern  Description of Parameter
	 * @param  columnNamePattern     Description of Parameter
	 * @return                       The ProcedureColumns value
	 * @exception  SQLException      Description of Exception
	 */
	public ResultSet getProcedureColumns(String catalog,
	                                     String schemaPattern,
	                                     String procedureNamePattern,
	                                     String columnNamePattern) throws SQLException {
		ExceptionManager.getManager().throwException(301, "getProcedureColumns method is not supported.");
		return null;
		// This statement will be never reached, but the compiler needs it.
	}


	/*
	 *  @jdbc.pending		falta por implementar Implementada por eva Febrero del 2000
	 */
	/**
	 *  Gets the Tables attribute of the TTDataBaseMetaData object
	 *
	 * @param  catalog           Description of Parameter
	 * @param  schemaPattern     Description of Parameter
	 * @param  tableNamePattern  Description of Parameter
	 * @param  types             Description of Parameter
	 * @return                   The Tables value
	 * @exception  SQLException  Description of Exception
	 */
	public ResultSet getTables(
	                           String catalog,
	                           String schemaPattern,
	                           String tableNamePattern,
	                           String types[]
	                           ) throws SQLException {
		TTStatement statement = (TTStatement) connection.createStatement();
		String mainCondition;
		String tablename;
		int i;
		TTResultSetCatalog resultset;

		tablename = createTempTable("tmp_tables", "TABLE_CAT char(128)," +
			"TABLE_SCHEM char(128), TABLE_NAME char(128), " +
			"TABLE_TYPE char(128), REMARKS char(254)");

		mainCondition = "";
		//Mod Paco-Fermin netbeans
		if (tableNamePattern != null && tableNamePattern.trim().length() != 0) {
			mainCondition = "tabname like '" + tableNamePattern.toLowerCase().trim() + "' and ";
		} else {
			tableNamePattern = null;
		}

		if (types != null) {
			for (i = 0; i < types.length; i++) {
				if (types[i].equals("SYSTEM TABLE")) {
					fillupTables(tablename, mainCondition + "tabid < 150 and tabtype = 'T'", "SYSTEM TABLE");
				}
				if (types[i].equals("TABLE")) {
					fillupTables(tablename, mainCondition + "tabid >= 150 and tabtype = 'T'", "TABLE");
				}
				if (types[i].equals("VIEW")) {
					fillupTables(tablename, mainCondition + "tabid >= 150 and tabtype = 'V'", "VIEW");
				}
				if (types[i].equals("SYNONYM")) {
					fillupSynonyms(tablename, tableNamePattern);
				}
			}
		} else {
			fillupTables(tablename, mainCondition + "tabid < 150 and tabtype = 'T'", "SYSTEM TABLE");
			fillupTables(tablename, mainCondition + "tabid >= 150 and tabtype = 'T'", "TABLE");
			fillupTables(tablename, mainCondition + "tabid >= 150 and tabtype = 'V'", "VIEW");
			fillupSynonyms(tablename, tableNamePattern);
		}

		resultset = (TTResultSetCatalog) statement.executeQueryCatalog
			(
			"select table_cat TABLE_CAT, table_schem TABLE_SCHEM, " +
			" table_name TABLE_NAME, table_type TABLE_TYPE, remarks REMARKS from " +
			tablename + " order by TABLE_TYPE, TABLE_SCHEM, TABLE_NAME"
			);
		resultset.setTableName(tablename);
		return resultset;
	}


	/**
	 *  getSchemas method is not supported.
	 *
	 * @return                   The Schemas value
	 * @exception  SQLException  Description of Exception
	 */
	public ResultSet getSchemas() throws SQLException {
		TTStatement statement = (TTStatement) connection.createStatement();
		return  (TTResultSetCatalog) statement.executeQueryCatalog
		(
		"select '"+TTDataBaseMetaData.DEFAULT_SCHEMA+"' TABLE_SCHEM from systables where tabname='systables'"
		);
		//return null;
	}


	/**
	 *  getCatalogs method is not supported.
	 *
	 * @return                   The Catalogs value
	 * @exception  SQLException  Description of Exception
	 */
	public ResultSet getCatalogs() throws SQLException {
		TTStatement statement = (TTStatement) connection.createStatement();
		return  (TTResultSetCatalog) statement.executeQueryCatalog
		(
		"select 'NONE' TABLE_CAT from systables where tabid<0"
		);
		//return null;
	}	


	/*
	 *  @jdbc.pending		falta por implementar
	 */
	/**
	 *  Gets the TableTypes attribute of the TTDataBaseMetaData object
	 *
	 * @return                   The TableTypes value
	 * @exception  SQLException  Description of Exception
	 */
	public ResultSet getTableTypes() throws SQLException {

		Statement statement = connection.createStatement();
		String tablename;
		TTResultSetCatalog resultset;
		TTStatement statement1;

		tablename = createTempTable("tmp_tabletypes", "TABLE_TYPE char(128)");

		statement.execute("insert into " + tablename + " values ('TABLE')");
		statement.close();
		statement.execute("insert into " + tablename + " values ('VIEW')");
		statement.close();
		statement.execute("insert into " + tablename + " values ('SYNONYM')");
		statement.close();
		statement.execute("insert into " + tablename + " values ('SYSTEM TABLE')");
		statement.close();

		statement1 = (TTStatement) connection.createStatement();
		resultset = (TTResultSetCatalog) statement1.executeQueryCatalog
			(
			"select * from " + tablename + " ORDER BY TABLE_TYPE"
			);

		resultset.setTableName(tablename);
		return resultset;
	}


	/*
	 *  @jdbc.pending		falta por implementar
	 *  Implementada por Eva en Febrero 2001
	 */
	/**
	 *  Gets the Columns attribute of the TTDataBaseMetaData object
	 *
	 * @param  catalog            Description of Parameter
	 * @param  schemaPattern      Description of Parameter
	 * @param  tableNamePattern   Description of Parameter
	 * @param  columnNamePattern  Description of Parameter
	 * @return                    The Columns value
	 * @exception  SQLException   Description of Exception
	 */
	public ResultSet getColumns
		(
	  String catalog,
	  String schemaPattern,
	  String tableNamePattern,
	  String columnNamePattern
	  ) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet resultset;
		TTResultSetCatalog resultsetOut;
		TTStatement statementOut = (TTStatement) connection.createStatement();

		PreparedStatement preparedStatement;
		String condition;
		String tablename;

		/*
		 *  MOD Paco 31-10-2002. TABLE_SCHEM y SQL_DATA_TYPE debe permitir nulos
		 */
		tablename = createTempTable("tmp_cols", "TABLE_CAT char(128), " +
// PACO		"TABLE_SCHEM char(128) NOT NULL, TABLE_NAME char(128) NOT NULL, " +
			"TABLE_SCHEM char(128) , TABLE_NAME char(128) NOT NULL, " +
			"COLUMN_NAME char(128) NOT NULL, DATA_TYPE smallint NOT NULL, " +
			"TYPE_NAME char(128) NOT NULL, COLUMN_SIZE integer," +
			"BUFFER_LENGTH smallint, DECIMAL_DIGITS integer, " +
			"NUM_PREC_RADIX integer, NULLABLE smallint NOT NULL, " +
			"REMARKS char(254), COLUMN_DEF char(128), " +
// PACO		"SQL_DATA_TYPE integer NOT NULL, SQL_DATETIME_SUB integer, " +
			"SQL_DATA_TYPE integer , SQL_DATETIME_SUB integer, " +
			"CHAR_OCTET_LENGTH integer, ORDINAL_POSITION integer NOT NULL, " +
			"IS_NULLABLE char(6)"
			);
		/*
		 *  END-MOD Paco 31-10-2002
		 */
		preparedStatement = connection.prepareStatement("insert into " + tablename +
			" values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (tableNamePattern != null && tableNamePattern.equals("") == false) {
			condition = " and tabname like '" + tableNamePattern + "'";
		} else {
			condition = "";
		}

		if (columnNamePattern != null && columnNamePattern.equals("") == false) {
			condition += " and colname like '" + columnNamePattern + "'";
		}

		resultset = statement.executeQuery(
			"select tabname, colname, coltype, collength, syscolumns.colno, " +
			" systables.tabid " +
			" from systables, syscolumns " +
			" where systables.tabid = syscolumns.tabid "
			+ condition
			+ " ORDER BY colno ");

		while (resultset.next()) {
			String zwStr;
			int zwTyp;
			int len;
			short ordinalPosition;
			short tabid;
			preparedStatement.setNull(1, java.sql.Types.CHAR);
			// TABLE_CAT
			//preparedStatement.setNull(2, java.sql.Types.CHAR);
			//Paco-Fermin netbeans
			preparedStatement.setString(2, TTDataBaseMetaData.DEFAULT_SCHEMA);
			// TABLE_SCHEM

			zwStr = resultset.getString(1);
			preparedStatement.setString(3, zwStr);
			// TABLE_NAME
			zwStr = resultset.getString(2);
			preparedStatement.setString(4, zwStr);
			// COLUMN_NAME

			zwTyp = resultset.getInt(3);
			preparedStatement.setShort(5, (short) java_types[zwTyp & 0xFF]);
			// DATA_TYPE
			preparedStatement.setString(6, sql_tnames[zwTyp & 0xFF]);
			// TYPE_NAME

			len = resultset.getInt(4);
			switch (zwTyp & 0xFF) {
							case CtsqlType.CHAR_TYPE:
								preparedStatement.setInt(7, len);
								// COLUMN_SIZE
								preparedStatement.setInt(10, 2);
								// NUM_PREC_RADIX
								break;
							case CtsqlType.SMALLINT_TYPE:
							case CtsqlType.INTEGER_TYPE:
							case CtsqlType.SERIAL_TYPE:
								preparedStatement.setInt(7, len);
								// COLUMN_SIZE
								preparedStatement.setInt(10, 10);
								// NUM_PREC_RADIX
								break;
							case CtsqlType.DECIMAL_TYPE:
							case CtsqlType.MONEY_TYPE:
								preparedStatement.setInt(7, len >> 8);
								preparedStatement.setInt(10, 10);
								// NUM_PREC_RADIX
								break;
							case CtsqlType.DATE_TYPE:
								preparedStatement.setInt(7, 10);
								preparedStatement.setInt(10, 0);
								// NUM_PREC_RADIX
								break;
							case CtsqlType.TIME_TYPE:
								preparedStatement.setInt(7, 8);
								preparedStatement.setInt(10, 0);
								// NUM_PREC_RADIX
								break;
							/*
							 *  MOD Paco 28-3-2003
							 */
							case CtsqlType.DATETIME_TYPE:
								preparedStatement.setInt(7, 20);
								preparedStatement.setInt(10, 0);
								// NUM_PREC_RADIX
								break;
							/*
							 *  END-MOD Paco 28-3-2003
							 */
							/*
							 *  MOD Paco 4-2-2004
							 */
							case CtsqlType.BINARY_TYPE:
								preparedStatement.setInt(7, len);
								preparedStatement.setInt(10, 0);
								break;
							/*
							 *  END-MOD Paco 4-2-2004
							 */
			}

			preparedStatement.setNull(8, java.sql.Types.SMALLINT);
			// BUFFER_LENGTH
			if ((zwTyp & 0xFF) == 5 || (zwTyp & 0xFF) == 8) {
				if ((len & 0xFF) == 0xFF) {
					preparedStatement.setNull(9, java.sql.Types.INTEGER);
				}
				// DECIMAL_DIGITS
				else {
					preparedStatement.setInt(9, len & 0xFF);
				}
			}
			// DECIMAL_DIGITS
			else {
				preparedStatement.setInt(9, 0);
			}
			// DECIMAL_DIGITS

			if ((zwTyp & 0x100) != 0) {
				preparedStatement.setShort(11, (short) columnNoNulls);
				// NULLABLE
				preparedStatement.setString(18, "NO");
				// IS_NULLABLE
			} else {
				preparedStatement.setShort(11, (short) columnNullable);
				// NULLABLE
				preparedStatement.setString(18, "YES");
				// IS_NULLABLE
			}

			preparedStatement.setNull(12, java.sql.Types.CHAR);
			// REMARKS

			preparedStatement.setNull(13, java.sql.Types.CHAR);
			// COLUMN_DEF
			preparedStatement.setNull(14, java.sql.Types.INTEGER);
			// SQL_DATA_TYPE
			preparedStatement.setNull(15, java.sql.Types.INTEGER);
			// SQL_DATETIME_SUB
			if ((zwTyp & 0xFF) == 0) {
				preparedStatement.setInt(16, len);
			}
			// CHAR_OCTET_LENGTH
			else {
				preparedStatement.setInt(16, 0);
			}
			// CHAR_OCTET_LENGTH
			ordinalPosition = resultset.getShort(5);
			tabid = resultset.getShort(6);

			preparedStatement.setInt(17, ordinalPosition);
			// ORDINAL_POSITION
			preparedStatement.execute();
			getDefault(tablename, tabid, ordinalPosition, zwTyp & 0xFF);
			getLabel(tablename, tabid, ordinalPosition);
		}
// New Eva 14-03-2001

		preparedStatement.close();
		statement.close();
		resultset.close();

// END New Eva 14-03-2001

		resultsetOut = (TTResultSetCatalog) statementOut.executeQueryCatalog("select table_cat TABLE_CAT, " +
			" table_schem TABLE_SCHEM, table_name TABLE_NAME, " +
			" column_name COLUMN_NAME, data_type DATA_TYPE, " +
			" type_name TYPE_NAME, column_size COLUMN_SIZE, " +
			" buffer_length BUFFER_LENGTH, decimal_digits DECIMAL_DIGITS," +
			" num_prec_radix NUM_PREC_RADIX, nullable NULLABLE, " +
			" remarks REMARKS, column_def COLUMN_DEF, " +
			" sql_data_type SQL_DATA_TYPE, sql_datetime_sub SQL_DATETIME_SUB, " +
			" char_octet_length CHAR_OCTET_LENGTH, ordinal_position ORDINAL_POSITION, " +
			" is_nullable IS_NULLABLE from " +
			tablename + " order by TABLE_SCHEM, TABLE_NAME, ORDINAL_POSITION ");
		resultsetOut.setTableName(tablename);
		return resultsetOut;
	}


	/*
	 *  @jdbc.pending falta por implementar impementada por eva 19-03-2001
	 */
	/**
	 *  Gets the ColumnPrivileges attribute of the TTDataBaseMetaData object
	 *
	 * @param  catalog            Description of Parameter
	 * @param  schema             Description of Parameter
	 * @param  table              Description of Parameter
	 * @param  columnNamePattern  Description of Parameter
	 * @return                    The ColumnPrivileges value
	 * @exception  SQLException   Description of Exception
	 */
	public ResultSet getColumnPrivileges(String catalog,
	                                     String schema,
	                                     String table,
	                                     String columnNamePattern)
		 throws SQLException {

		TTResultSetCatalog resultset;
		TTStatement statement;

		String tablename;
		String condition;
		tablename = createTempTable("tmp_colprivilege",
			"TABLE_CAT  CHAR(128)," +
			"TABLE_SCHEM      CHAR(128)," +
			"TABLE_NAME       CHAR(128) NOT NULL," +
			"COLUMN_NAME      CHAR(128) NOT NULL," +
			"GRANTOR          CHAR(128)," +
			"GRANTEE          CHAR(128)," +
			"PRIVILEGE        CHAR(128)," +
			"IS_GRANTABLE     CHAR(3)"
			);

		if (table != null && table.equals("") == false) {
			condition = " and tabname like '" + table + "'";
		} else {
			condition = "";
		}

		if (columnNamePattern != null && columnNamePattern.equals("") == false) {
			condition += " and colname like '" + columnNamePattern + "'";
		}
		fillupColumnPrivileges(tablename, condition);
		statement = (TTStatement) connection.createStatement();
		resultset = (TTResultSetCatalog) statement.executeQueryCatalog(
			"SELECT * FROM " + tablename +
			" ORDER BY COLUMN_NAME, PRIVILEGE"
			);
		resultset.setTableName(tablename);
		return resultset;
	}


	/*
	 *  @jdbc.pending implementada por eva el 21-03-2001
	 */
	/**
	 *  Gets a description of the access rights for each table available in a
	 *  catalog. Note that a table privilege applies to one or more columns in the
	 *  table. It would be wrong to assume that this priviledge applies to all
	 *  columns (this may be true for some systems but is not true for all.) <P>
	 *
	 *  Only privileges matching the schema and table name criteria are returned.
	 *  They are ordered by TABLE_SCHEM, TABLE_NAME, and PRIVILEGE. <P>
	 *
	 *  Each privilige description has the following columns:
	 *  <OL>
	 *    <LI> <B>TABLE_CAT</B> String => table catalog (may be null)
	 *    <LI> <B>TABLE_SCHEM</B> String => table schema (may be null)
	 *    <LI> <B>TABLE_NAME</B> String => table name
	 *    <LI> <B>GRANTOR</B> => grantor of access (may be null)
	 *    <LI> <B>GRANTEE</B> String => grantee of access
	 *    <LI> <B>PRIVILEGE</B> String => name of access (SELECT, INSERT, UPDATE,
	 *    REFRENCES, ...)
	 *    <LI> <B>IS_GRANTABLE</B> String => "YES" if grantee is permitted to grant
	 *    to others; "NO" if not; null if unknown
	 *  </OL>
	 *
	 *
	 * @param  catalog           a catalog name; "" retrieves those without a
	 *      catalog; null means drop catalog name from the selection criteria
	 * @param  schemaPattern     a schema name pattern; "" retrieves those without
	 *      a schema
	 * @param  tableNamePattern  a table name pattern
	 * @return                   <code>ResultSet</code> - each row is a table
	 *      privilege description
	 * @exception  SQLException  if a database access error occurs
	 * @see                      #getSearchStringEscape
	 */
	public ResultSet getTablePrivileges(String catalog,
	                                    String schemaPattern,
	                                    String tableNamePattern)
		 throws SQLException {
		TTResultSetCatalog resultset;
		TTStatement statement;
		String tablename;

		tablename = createTempTable("tmp_tablepriv",
			"TABLE_CAT  CHAR(128), " +
			"TABLE_SCHEM      CHAR(128), " +
			"TABLE_NAME       CHAR(128) NOT NULL, " +
			"GRANTOR          CHAR(128), " +
			"GRANTEE          CHAR(128), " +
			"PRIVILEGE        CHAR(128), " +
			"IS_GRANTABLE     CHAR(3)"
			);

		fillupTablePrivileges(tablename, tableNamePattern, schemaPattern);
		statement = (TTStatement) connection.createStatement();
		resultset = (TTResultSetCatalog) statement.executeQueryCatalog(
			"SELECT * FROM " + tablename +
			" ORDER BY TABLE_SCHEM, TABLE_NAME, PRIVILEGE"
			);
		resultset.setTableName(tablename);
		return resultset;
	}


	/*
	 *  @jdbc.pending	implementada por Eva 21-03-2001
	 */
	/**
	 *  Gets a description of a table's optimal set of columns that uniquely
	 *  identifies a row. They are ordered by SCOPE. <P>
	 *
	 *  Each column description has the following columns:
	 *  <OL>
	 *    <LI> <B>SCOPE</B> short => actual scope of result
	 *    <UL>
	 *      <LI> bestRowTemporary - very temporary, while using row
	 *      <LI> bestRowTransaction - valid for remainder of current transaction
	 *
	 *      <LI> bestRowSession - valid for remainder of current session
	 *    </UL>
	 *
	 *    <LI> <B>COLUMN_NAME</B> String => column name
	 *    <LI> <B>DATA_TYPE</B> short => SQL data type from java.sql.Types
	 *    <LI> <B>TYPE_NAME</B> String => Data source dependent type name, for a
	 *    UDT the type name is fully qualified
	 *    <LI> <B>COLUMN_SIZE</B> int => precision
	 *    <LI> <B>BUFFER_LENGTH</B> int => not used
	 *    <LI> <B>DECIMAL_DIGITS</B> short => scale
	 *    <LI> <B>PSEUDO_COLUMN</B> short => is this a pseudo column like an Oracle
	 *    ROWID
	 *    <UL>
	 *      <LI> bestRowUnknown - may or may not be pseudo column
	 *      <LI> bestRowNotPseudo - is NOT a pseudo column
	 *      <LI> bestRowPseudo - is a pseudo column
	 *    </UL>
	 *
	 *  </OL>
	 *
	 *
	 * @param  catalog           a catalog name; "" retrieves those without a
	 *      catalog; null means drop catalog name from the selection criteria
	 * @param  schema            a schema name; "" retrieves those without a schema
	 * @param  table             a table name
	 * @param  scope             the scope of interest; use same values as SCOPE
	 * @param  nullable          include columns that are nullable?
	 * @return                   <code>ResultSet</code> - each row is a column
	 *      description
	 * @exception  SQLException  if a database access error occurs
	 */

	public ResultSet getBestRowIdentifier(String catalog,
	                                      String schema,
	                                      String table,
	                                      int scope,
	                                      boolean nullable)
		 throws SQLException {
		Statement statement;
		ResultSet resultset = null;
		TTStatement statement1 = (TTStatement) connection.createStatement();
		String tablename;
		TTResultSetCatalog resultset1;
		String type;

		statement = connection.createStatement();
		resultset = statement.executeQuery(
			"select tabtype from systables where tabname = '" + table.toLowerCase() + "'"
			);

		if ((resultset != null) && resultset.next()) {
			type = resultset.getString("tabtype");
			statement.close();
			resultset.close();
			if (type.substring(0, 0).compareTo("V") == 0) {
				return null;
			}
		} else {
			resultset = statement.executeQuery(
				"select tabtype from systables, syssynonyms" +
				" WHERE systables.tabid = syssynonyms.tabid" +
				"   AND synname =  '" + table.toLowerCase() + "'"
				);
			if (resultset != null) {
				if (!resultset.next()) {
					return null;
				}
				type = resultset.getString("tabtype");
				resultset.close();
				if (type.substring(0, 0).compareTo("V") == 0) {
					return null;
				}
			}
		}

		tablename = createTempTable("tmp_bestrow", "SCOPE SMALLINT NOT NULL, " +
			"COLUMN_NAME   CHAR(128) NOT NULL, " +
			"DATA_TYPE     SMALLINT  NOT NULL, " +
			"TYPE_NAME     CHAR(128) NOT NULL, " +
			"COLUMN_SIZE   INTEGER, " +
			"BUFFER_LENGTH INTEGER, " +
			"DECIMAL_DIGITS SMALLINT, " +
			"PSEUDO_COLUMN SMALLINT",
			"PRIMARY KEY (SCOPE)"
			);

		statement.executeUpdate(
			"INSERT INTO " + tablename +
			" VALUES ( " + DatabaseMetaData.bestRowSession + ", '(rowid)', " +
			java.sql.Types.INTEGER +
			", 'INTEGER', NULL, 8, NULL," + DatabaseMetaData.bestRowPseudo + ")"
			);
		statement.close();
		resultset1 = (TTResultSetCatalog) statement1.executeQueryCatalog
			(
			"select * from " + tablename + " ORDER BY SCOPE"
			);
		resultset1.setTableName(tablename);
		return resultset1;
	}


	/*
	 *  @jdbc.pending ya no falta por implementar Eva 22-03-2001
	 */
	/**
	 *  Gets the VersionColumns attribute of the TTDataBaseMetaData object
	 *
	 * @param  catalog           Description of Parameter
	 * @param  schema            Description of Parameter
	 * @param  table             Description of Parameter
	 * @return                   The VersionColumns value
	 * @exception  SQLException  Description of Exception
	 */
	public ResultSet getVersionColumns(String catalog,
	                                   String schema,
	                                   String table)
		 throws SQLException {
		return null;
	}


	
	/**
	 *  Gets the PrimaryKeys attribute of the TTDataBaseMetaData object
	 *
	 * @param  catalog           Description of Parameter
	 * @param  schema            Description of Parameter
	 * @param  table             Description of Parameter
	 * @return                   The PrimaryKeys value
	 * @exception  SQLException  Description of Exception
	 */
	public ResultSet getPrimaryKeys
		(
	  String catalog,
	  String schema,
	  String table
	  ) throws SQLException {
			
		Systable systable = getSystable(table);
		int[] parts = systable.getParts();
		
		PropertiesBeanResultset propertiesBeanResultset = new PropertiesBeanResultset(primaryKeysMetadata);
		for(int i=0; i < NPARTS; i++){
			if(parts[i] == 0){
				break;
			}
			Syscolumn syscolumn = (Syscolumn) systable.getColumns().get(parts[i]-1);
			PrimaryKeyRow row = new PrimaryKeyRow();
			row.setProperty(PrimaryKeyRow.TABLE_NAME, table);			
			row.setProperty(PrimaryKeyRow.COLUMN_NAME, syscolumn.getColname());
			row.setProperty(PrimaryKeyRow.KEY_SEQ, ""+(i+1));
			
			// New Paco-Fermin 20-01-2010. NetBeans
			row.setProperty(PrimaryKeyRow.TABLE_SCHEM, TTDataBaseMetaData.DEFAULT_SCHEMA);
			
			propertiesBeanResultset.add(row);
			
		}
		return propertiesBeanResultset;
	}

	
	/**
	 *  Gets the PrimaryKeys attribute of the TTDataBaseMetaData object
	 *
	 * @param  catalog           Description of Parameter
	 * @param  schema            Description of Parameter
	 * @param  table             Description of Parameter
	 * @return                   The PrimaryKeys value
	 * @exception  SQLException  Description of Exception
	 */
	
	public ResultSet DeprecatedgetPrimaryKeys
		(
	  String catalog,
	  String schema,
	  String table
	  ) throws SQLException {

		String condition;

		TTStatement statement = (TTStatement) connection.createStatement();
		String tablename;
		TTResultSetCatalog resultset;

		tablename = createTempTable("tmp_primaykeys", "TABLE_CAT char(128)," +
		/*
		 *  MOD Paco 16-9-2002. Este campo no es necesario
		 *  "PKTABLE_QUALIFIER  CHAR(128)," +
		 *  END-MOD Paco 16-9-2002
		 */
		/*
		 *  MOD Paco 13-03-2003. No es TABLE_OWNER, sino TABLE_SCHEM
		 *  "TABLE_OWNER        CHAR(128)," +
		 */
			"TABLE_SCHEM        CHAR(128)," +
		/*
		 *  END-MOD Paco 13-03-2003
		 */
			"TABLE_NAME         CHAR(128) NOT NULL," +
			"COLUMN_NAME        CHAR(128) NOT NULL," +
			"KEY_SEQ            SMALLINT  NOT NULL," +
			"PK_NAME            CHAR(128)"
			);

		if (table != null && table.equals("") == false) {
			condition = " and tabname like '" + table + "'";
		} else {
			condition = "";
		}

		fillupPrimaryKeys(tablename, condition);

		resultset = (TTResultSetCatalog) statement.executeQueryCatalog
			(
			"select * from " + tablename + " ORDER BY COLUMN_NAME"
			);
		resultset.setTableName(tablename);
		return resultset;
	}

	

	private Systable getSystable(String table)  throws SQLException {
		if(systablesMap == null){
			systablesMap = new HashMap();			
		}
		
		Systable systable = (Systable) systablesMap.get(table);
		if(systable == null){
			systable =  loadSystable(table);
			systablesMap.put(table, systable);
		}
		if(systable == null){
			throw new SQLException("Table "+table+" not found.");
		}
		return systable;
	}
	
	
	private Systable loadSystable(String table) throws SQLException {
		String s =
			"select tabname, owner, tabid, part1, part2, part3, part4, part5, part6, part7, part8"+
			" from systables where tabname = '"+table+"'";

		Statement stmt = connection.createStatement();		
		ResultSet rs = stmt.executeQuery(s);
		
		Systable systable = null;
		if (rs.next()) {
			systable = new Systable();
			systable.setTabname(rs.getString("tabname"));
			systable.setOwner(rs.getString("owner"));
			systable.setTabid(rs.getInt("tabid"));
			
			for(int i=1; i<=NPARTS; i++){
				int idx = rs.getInt("part"+i);
				if(idx == 0){
					break;
				}
				systable.getParts()[i-1] = idx; 					
			}
			loadSyscolumns(systable);			
		}
		stmt.close();
		rs.close();
		return systable;
	}
	
	private void loadSyscolumns(Systable systable) throws SQLException {
		String s =
			"select colname, colno, coltype, collength"+
			" from syscolumns where tabid = "+systable.getTabid()+" order by colno";

		Statement stmt = connection.createStatement();		
		ResultSet rs = stmt.executeQuery(s);
				
		while (rs.next()) {
			Syscolumn syscolumn = new Syscolumn();
			syscolumn.setColname(rs.getString("colname"));
			syscolumn.setColtype(rs.getInt("coltype"));
			syscolumn.setCollength(rs.getInt("collength"));
			systable.getColumns().add(syscolumn);
			
		}
		stmt.close();
		rs.close();
	}



	/**
	 *  Gets the ImportedKeys attribute of the TTDataBaseMetaData object
	 *
	 * @param  catalog           Description of Parameter
	 * @param  schema            Description of Parameter
	 * @param  table             Description of Parameter
	 * @return                   The ImportedKeys value
	 * @exception  SQLException  Description of Exception
	 */
	public ResultSet getImportedKeys(String catalog,
	                                 String schema,
	                                 String table) throws SQLException
	{
		String s =
		"select st.tabname, sysforeign.*"+
		" from sysforeign, systables, systables st"+ 
		" where systables.tabid = sysforeign.dtabid"+ 
		" and st.tabid = sysforeign.rtabid"+
		" and systables.tabname = '"+table+"'";

		Statement stmt = connection.createStatement();		
		ResultSet rs = stmt.executeQuery(s);
			
		PropertiesBeanResultset propertiesBeanResultset = new PropertiesBeanResultset(importExportKeysMetadata);
		int idx = 1;
		while (rs.next()) {
			String pkTableName = rs.getString(1).trim();
			String fkTableName = table;
			Systable pkTable = getSystable(pkTableName);
			Systable fkTable = getSystable(fkTableName);
			
			for(int i=1; i<=NPARTS; i++){
				ForeignKeyRow row = new ForeignKeyRow();
				int part = rs.getInt("part"+i);
				if(part == 0){
					break;
				}
				Syscolumn fkColumn = (Syscolumn) fkTable.getColumns().get(part-1);
				Syscolumn pkColumn = (Syscolumn) pkTable.getColumns().get(pkTable.getParts()[i-1]-1);
				row.setProperty(ForeignKeyRow.PKTABLE_NAME, pkTableName);
				row.setProperty(ForeignKeyRow.PKCOLUMN_NAME, pkColumn.getColname());
				row.setProperty(ForeignKeyRow.FKTABLE_NAME, fkTableName);
				row.setProperty(ForeignKeyRow.FKCOLUMN_NAME, fkColumn.getColname());
				row.setProperty(ForeignKeyRow.KEY_SEQ, String.valueOf(idx));
				row.setProperty(ForeignKeyRow.FK_NAME, rs.getString("fkname"));
				row.setProperty(ForeignKeyRow.DELETE_RULE, String.valueOf(DatabaseMetaData.importedKeyNoAction));
				row.setProperty(ForeignKeyRow.UPDATE_RULE, String.valueOf(DatabaseMetaData.importedKeyNoAction));
				row.setProperty(ForeignKeyRow.DEFERRABILITY, String.valueOf(DatabaseMetaData.importedKeyNotDeferrable));

				// New Paco-Fermin 20-01-2010. NetBeans
				row.setProperty(ForeignKeyRow.PKTABLE_SCHEM, TTDataBaseMetaData.DEFAULT_SCHEMA);
				
				propertiesBeanResultset.add(row);
			}			
		}
		stmt.close();
		rs.close();
		
		return propertiesBeanResultset;
	}

	
	/**
	 *  Gets the ExportedKeys attribute of the TTDataBaseMetaData object
	 *
	 * @param  catalog           Description of Parameter
	 * @param  schema            Description of Parameter
	 * @param  table             Description of Parameter
	 * @return                   The ExportedKeys value
	 * @exception  SQLException  Description of Exception
	 */
	public ResultSet getExportedKeys(String catalog,
	                                 String schema,
	                                 String table)
		 throws SQLException {
		String s =
			"select systables.tabname, sysforeign.*"+
			" from sysforeign, systables, systables st"+ 
			" where systables.tabid = sysforeign.dtabid"+ 
			" and st.tabid = sysforeign.rtabid"+
			" and st.tabname = '"+table+"'";

			Statement stmt = connection.createStatement();		
			ResultSet rs = stmt.executeQuery(s);
			
			PropertiesBeanResultset propertiesBeanResultset = new PropertiesBeanResultset(importExportKeysMetadata);
			int idx = 1;
			while (rs.next()) {
				String pkTableName = table;
				String fkTableName = rs.getString(1).trim();
				Systable pkTable = getSystable(pkTableName);
				Systable fkTable = getSystable(fkTableName);
				
				for(int i=1; i<=8; i++){
					ForeignKeyRow row = new ForeignKeyRow();
					int part = rs.getInt("part"+i);
					if(part == 0){
						break;
					}
					Syscolumn fkColumn = (Syscolumn) fkTable.getColumns().get(part-1);
					Syscolumn pkColumn = (Syscolumn) pkTable.getColumns().get(pkTable.getParts()[i-1]-1);
					row.setProperty(ForeignKeyRow.PKTABLE_NAME, pkTableName);
					row.setProperty(ForeignKeyRow.PKCOLUMN_NAME, pkColumn.getColname());
					row.setProperty(ForeignKeyRow.FKTABLE_NAME, fkTableName);
					row.setProperty(ForeignKeyRow.FKCOLUMN_NAME, fkColumn.getColname());
					row.setProperty(ForeignKeyRow.KEY_SEQ, String.valueOf(idx));
					row.setProperty(ForeignKeyRow.FK_NAME, rs.getString("fkname"));
					row.setProperty(ForeignKeyRow.DELETE_RULE, String.valueOf(DatabaseMetaData.importedKeyNoAction));
					row.setProperty(ForeignKeyRow.UPDATE_RULE, String.valueOf(DatabaseMetaData.importedKeyNoAction));
					row.setProperty(ForeignKeyRow.DEFERRABILITY, String.valueOf(DatabaseMetaData.importedKeyNotDeferrable));
					
					propertiesBeanResultset.add(row);
				}			
			}
			stmt.close();
			rs.close();
			
			return propertiesBeanResultset;
	}	


	/**
	 *  Gets the ImportedKeys attribute of the TTDataBaseMetaData object
	 *
	 * @param  catalog           Description of Parameter
	 * @param  schema            Description of Parameter
	 * @param  table             Description of Parameter
	 * @return                   The ImportedKeys value
	 * @exception  SQLException  Description of Exception
	 */
	public ResultSet DeprecatedgetImportedKeys(String catalog,
	                                 String schema,
	                                 String table)
		 throws SQLException {
		String condition;
		String tablename;
		TTStatement statement = (TTStatement) connection.createStatement();
		TTResultSetCatalog resultset;

		tablename = createTempTable("tmp_impfk",
			"PKTABLE_CAT        CHAR(128)," +
			"PKTABLE_SCHEM      CHAR(128)," +
			"PKTABLE_NAME       CHAR(128) NOT NULL," +
			"PKCOLUMN_NAME      CHAR(128) NOT NULL," +
			"FKTABLE_CAT        CHAR(128)," +
			"FKTABLE_SCHEM      CHAR(128)," +
			"FKTABLE_NAME       CHAR(128) NOT NULL," +
			"FKCOLUMN_NAME      CHAR(128) NOT NULL," +
			"KEY_SEQ            SMALLINT  NOT NULL," +
			"UPDATE_RULE        SMALLINT, " +
			"DELETE_RULE        SMALLINT, " +
			"FK_NAME            CHAR(128), " +
			"PK_NAME            CHAR(128), " +
			"DEFERRABILITY SMALLINT"
			);
        condition = "";
        
		/* NO USAMOS DE MOMENTO SCHEMAS, y no existe relacion de momento entre SCHEMA y OWNER CTSQL
        if (schema != null && schema.equals("") == false) {
            condition = " and systabf.owner like '" + schema + "'";
        }
        */

        if ((table != null) && (table.equals("") == false)) {
            condition += " and systabf.tabname like '" + table + "'";
        }

		fillupForeignKeys(tablename, condition);

		resultset = (TTResultSetCatalog) statement.executeQueryCatalog
			(
			"select * from " + tablename + " ORDER BY FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, KEY_SEQ"
			);
		resultset.setTableName(tablename);
		return resultset;
	}


	/*
	 *  @jdbc.pending		falta por implementar
	 */
	/**
	 *  Gets the ExportedKeys attribute of the TTDataBaseMetaData object
	 *
	 * @param  catalog           Description of Parameter
	 * @param  schema            Description of Parameter
	 * @param  table             Description of Parameter
	 * @return                   The ExportedKeys value
	 * @exception  SQLException  Description of Exception
	 */
	public ResultSet DeprecatedgetExportedKeys(String catalog,
	                                 String schema,
	                                 String table)
		 throws SQLException {
		String condition;
		String tablename;
		TTStatement statement = (TTStatement) connection.createStatement();
		TTResultSetCatalog resultset;

		tablename = createTempTable("tmp_expfk",
			"PKTABLE_CAT        CHAR(128)," +
			"PKTABLE_SCHEM      CHAR(128)," +
			"PKTABLE_NAME       CHAR(128) NOT NULL," +
			"PKCOLUMN_NAME      CHAR(128) NOT NULL," +
			"FKTABLE_CAT        CHAR(128)," +
			"FKTABLE_SCHEM      CHAR(128)," +
			"FKTABLE_NAME       CHAR(128) NOT NULL," +
			"FKCOLUMN_NAME      CHAR(128) NOT NULL," +
			"KEY_SEQ            SMALLINT  NOT NULL," +
			"UPDATE_RULE        SMALLINT, " +
			"DELETE_RULE        SMALLINT, " +
			"FK_NAME            CHAR(128), " +
			"PK_NAME            CHAR(128), " +
			"DEFERRABILITY SMALLINT"
			);
        condition = "";

        /* NO USAMOS DE MOMENTO SCHEMAS, y no existe relacion de momento entre SCHEMA y OWNER CTSQL
        if (schema != null && schema.equals("") == false) {
            condition += " and systabp.owner like '" + schema + "'";
        }
        */

        if (table != null && table.equals("") == false) {
            condition += " and systabp.tabname like '" + table + "'";
        }

		fillupForeignKeys(tablename, condition);

		resultset = (TTResultSetCatalog) statement.executeQueryCatalog
			(
			"select * from " + tablename + " ORDER BY FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, KEY_SEQ"
			);
		resultset.setTableName(tablename);
		return resultset;
	}


	/*
	 *  @jdbc.pending		falta por implementar. Implementada por Eva Febrero 2001
	 */
	/**
	 *  Gets the CrossReference attribute of the TTDataBaseMetaData object
	 *
	 * @param  primaryCatalog    Description of Parameter
	 * @param  primarySchema     Description of Parameter
	 * @param  primaryTable      Description of Parameter
	 * @param  foreignCatalog    Description of Parameter
	 * @param  foreignSchema     Description of Parameter
	 * @param  foreignTable      Description of Parameter
	 * @return                   The CrossReference value
	 * @exception  SQLException  Description of Exception
	 */
	public ResultSet getCrossReference(String primaryCatalog,
	                                   String primarySchema,
	                                   String primaryTable,
	                                   String foreignCatalog,
	                                   String foreignSchema,
	                                   String foreignTable) throws SQLException {

		String condition;
		String tablename;
		TTStatement statement = (TTStatement) connection.createStatement();
		TTResultSetCatalog resultset;

		tablename = createTempTable("tmp_fk",
			"PKTABLE_CAT        CHAR(128)," +
			"PKTABLE_SCHEM      CHAR(128)," +
			"PKTABLE_NAME       CHAR(128) NOT NULL," +
			"PKCOLUMN_NAME      CHAR(128) NOT NULL," +
			"FKTABLE_CAT        CHAR(128)," +
			"FKTABLE_SCHEM      CHAR(128)," +
			"FKTABLE_NAME       CHAR(128) NOT NULL," +
			"FKCOLUMN_NAME      CHAR(128) NOT NULL," +
			"KEY_SEQ            SMALLINT  NOT NULL," +
			"UPDATE_RULE        SMALLINT, " +
			"DELETE_RULE        SMALLINT, " +
			"FK_NAME            CHAR(128), " +
			"PK_NAME            CHAR(128), " +
			"DEFERRABILITY SMALLINT"
			);
		condition = "";
		
		/* NO USAMOS DE MOMENTO SCHEMAS, y no existe relacion de momento entre SCHEMA y OWNER CTSQL		
		if (primarySchema != null && primarySchema.equals("") == false) {
			 //  MOD Paco 23-04-2002. systabf es para foreignTable
			 // condition = " and systabf.owner like '" + primarySchema + "'";
			condition = " and systabp.owner like '" + primarySchema + "'";
			//  END-MOD Paco 23-04-2002
			 
		}
		*/

		if ((primaryTable != null) && (primaryTable.equals("") == false)) {
			/*
			 *  MOD Paco 23-04-2002. systabf es para foreignTable
			 *  condition += " and systabf.tabname like '" + primaryTable + "'";
			 */
			condition += " and systabp.tabname like '" + primaryTable + "'";
			/*
			 *  END-MOD Paco 23-04-2002
			 */
		}

		/* NO USAMOS DE MOMENTO SCHEMAS, y no existe relacion de momento entre SCHEMA y OWNER CTSQL		
		if (foreignSchema != null && foreignSchema.equals("") == false) {
			 // MOD Paco 23-04-2002. systabp es para primaryTable
			 // condition += " and systabp.owner like '" + foreignSchema + "'";
			condition += " and systabf.owner like '" + foreignSchema + "'";
			//  END-MOD Paco 23-04-2002
		}
		*/

		if (foreignTable != null && foreignTable.equals("") == false) {
			/*
			 *  MOD Paco 23-04-2002. systabp es para primaryTable
			 *  condition += " and systabp.tabname like '" + foreignTable + "'";
			 */
			condition += " and systabf.tabname like '" + foreignTable + "'";
			/*
			 *  END-MOD Paco 23-04-2002
			 */
		}

		fillupForeignKeys(tablename, condition);

		resultset = (TTResultSetCatalog) statement.executeQueryCatalog
			(
			"select * from " + tablename + " ORDER BY FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, KEY_SEQ"
			);
		resultset.setTableName(tablename);
		return resultset;
	}


	/*
	 *  @jdbc.pending falta por implementar. Implemantada por Eva el 20-03-2001
	 */
	/**
	 *  Gets the TypeInfo attribute of the TTDataBaseMetaData object
	 *
	 * @return                   The TypeInfo value
	 * @exception  SQLException  Description of Exception
	 */
	public ResultSet getTypeInfo() throws SQLException {

		String tablename;
		TTStatement statement = (TTStatement) connection.createStatement();
		TTResultSetCatalog resultset;
		Statement statement1 = connection.createStatement();

		tablename = createTempTable("tmp_typeinfo",
			"TYPE_NAME  CHAR(128) NOT NULL LABEL 'TYPE_NAME'," +
			"DATA_TYPE        SMALLINT  NOT NULL  LABEL 'DATA_TYPE'," +
			"PRECISION        INTEGER   LABEL 'PRECISION'," +
			"LITERAL_PREFIX   CHAR(128) LABEL 'LITERAL_PREFIX'," +
			"LITERAL_SUFFIX   CHAR(128) LABEL 'LITERAL_SUFFIX'," +
			"CREATE_PARAMS    CHAR(128) LABEL 'CREATE_PARAMS'," +
			"NULLABLE         SMALLINT  NOT NULL LABEL 'NULLABLE'," +
			"CASE_SENSITIVE   SMALLINT  NOT NULL LABEL 'CASE_SENSITIVE'," +
			"SEARCHABLE       SMALLINT  NOT NULL LABEL 'SEARCHABLE'," +
			"UNSIGNED_ATTRIBUTE SMALLINT LABEL 'UNSIGNED_ATTRIBUTE'," +
			"FIXED_PREC_SCALE SMALLINT  NOT NULL LABEL 'MONEY'," +
			"AUTO_INCREMENT   SMALLINT  LABEL 'AUTO_INCREMENT'," +
			"LOCAL_TYPE_NAME  CHAR(128) LABEL 'LOCAL_TYPE_NAME'," +
			"MINIMUM_SCALE    SMALLINT  LABEL 'MINIMUM_SCALE'," +
			"MAXIMUM_SCALE    SMALLINT  LABEL 'MAXIMUM_SCALE'"
			);

		for (int i = 0; i < TMP_TypeInfo.length; ++i) {
			statement1.executeUpdate("insert into " + tablename + " values(" + TMP_TypeInfo[i] + ")");
		}
		statement1.close();
		resultset = (TTResultSetCatalog) statement.executeQueryCatalog
			(
			"select * from " + tablename + " ORDER BY DATA_TYPE"
			);
		resultset.setTableName(tablename);
		return resultset;
	}


	/*
	 *  @jdbc.pending	falta por implementar. Implementada en Marzo del 2001 por Eva
	 */
	/**
	 *  Gets the IndexInfo attribute of the TTDataBaseMetaData object
	 *
	 * @param  catalog           Description of Parameter
	 * @param  schema            Description of Parameter
	 * @param  table             Description of Parameter
	 * @param  unique            Description of Parameter
	 * @param  approximate       Description of Parameter
	 * @return                   The IndexInfo value
	 * @exception  SQLException  Description of Exception
	 */
	public ResultSet getIndexInfo(String catalog,
	                              String schema,
	                              String table,
	                              boolean unique,
	                              boolean approximate) throws SQLException {

		TTResultSetCatalog resultset;
		TTStatement statement = (TTStatement) connection.createStatement();
		String condition;
		String tablename;
		
		tablename = createTempTable("tmp_index",
		/*
		 *  MOD Paco 13-03-2003. En la documentacion de JDBC 2.0, no existen TABLE_OWNER
		 *  ni TABLE_QUALIFIER, sino TABLE_CAT y TABLE_SCHEM
		 *  "TABLE_QUALIFIER  CHAR(128)," +
		 *  "TABLE_OWNER      CHAR(128)," +
		 */
			"TABLE_CAT  CHAR(128)," +
			"TABLE_SCHEM      CHAR(128)," +
		/*
		 *  END-MOD Paco 13-03-2003
		 */
			"TABLE_NAME       CHAR(128) NOT NULL," +
			"NON_UNIQUE       SMALLINT," +
			"INDEX_QUALIFIER  CHAR(128)," +
			"INDEX_NAME       CHAR(128)," +
			"TYPE             SMALLINT NOT NULL," +
			"ORDINAL_POSITION SMALLINT," +
			"COLUMN_NAME      CHAR(128)," +
			"ASC_OR_DESC      CHAR(1)," +
			"CARDINALITY      INTEGER," +
			"PAGES            INTEGER," +
			"FILTER_CONDITION CHAR(128)"
			);

		condition = "";
		if (table != null && table.equals("") == false) {
			condition = " and tabname like '" + table + "'";
		}
		if (unique == true) {
			condition += " and sysindexes.idxtype == 'U' ";
		}

		fillupIndexInfo(tablename, condition);
		resultset = (TTResultSetCatalog) statement.executeQueryCatalog
			(
			"select * from " + tablename +
			" ORDER BY NON_UNIQUE, TYPE, INDEX_NAME, ORDINAL_POSITION"
			);
		resultset.setTableName(tablename);
		return resultset;
	}


	/*
	 *  @jdbc.pending		Necesario para JDBC 2.0 .Falta por implementar
	 */
	/**
	 *  Gets the UDTs attribute of the TTDataBaseMetaData object
	 *
	 * @param  catalog           Description of Parameter
	 * @param  schemaPattern     Description of Parameter
	 * @param  typeNamePattern   Description of Parameter
	 * @param  types             Description of Parameter
	 * @return                   The UDTs value
	 * @exception  SQLException  Description of Exception
	 */
	public ResultSet getUDTs(String catalog,
	                         String schemaPattern,
	                         String typeNamePattern,
	                         int[] types) throws SQLException {
		return null;
	}


	/*
	 *  @jdbc.pending		Necesario para JDBC 2.0 .Falta por implementar
	 */
	/**
	 *  Gets the Connection attribute of the TTDataBaseMetaData object
	 *
	 * @return                   The Connection value
	 * @exception  SQLException  Description of Exception
	 */
	public Connection getConnection() throws SQLException {
// MOD Eva 05-03-2001
//		return null;
		return this.connection;
// MOD Eva 05-03-2001
	}


	//--------------------------------------------------------------------
	// Information about the target database:
	//--------------------------------------------------------------------

	/**
	 *  allProceduresAreCallable method is not supported.
	 *
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean allProceduresAreCallable() throws SQLException {
		//paco netbeans ExceptionManager.getManager().throwException(301, "allProceduresAreCallable method is not supported.");
		return false;
		// This statement will be never reached, but the compiler needs it.
	}


	/**
	 *  allTablesAreSelectable method: Can all the tables returned by getTable be
	 *  SELECTed by the current user?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean allTablesAreSelectable() throws SQLException {
		return true;
	}


	/**
	 *  nullsAreSortedHigh method: Are NULL values sorted high?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean nullsAreSortedHigh() throws SQLException {
		return false;
	}


	/**
	 *  nullsAreSortedHigh method: Are NULL values sorted low?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean nullsAreSortedLow() throws SQLException {
		return true;
	}


	/**
	 *  nullsAreSortedAtStart method: Are NULL values sorted at the start
	 *  regardless of sort order?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean nullsAreSortedAtStart() throws SQLException {
		return true;
	}


	/**
	 *  nullsAreSortedAtEnd method: Are NULL values sorted at the end regardless of
	 *  sort order?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean nullsAreSortedAtEnd() throws SQLException {
		return false;
	}


	/**
	 *  usesLocalFiles method: Does the database store tables in a local file?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean usesLocalFiles() throws SQLException {
		return true;
	}


	/**
	 *  usesLocalFilePerTable method: Does the database use a file for each table?
	 *
	 * @return                   true if the database uses a local file for each
	 *      table
	 * @exception  SQLException  Description of Exception
	 */
	public boolean usesLocalFilePerTable() throws SQLException {
		return true;
	}


	/**
	 *  supportsMixedCaseIdentifiers method: Does the database treat mixed case
	 *  unquoted SQL identifiers as case sensitive and as a result store them in
	 *  mixed case?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		return false;
	}


	/**
	 *  storesUpperCaseIdentifiers method: Does the database treat mixed case
	 *  unquoted SQL identifiers as case insensitive and store them in upper case?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean storesUpperCaseIdentifiers() throws SQLException {
		return false;
	}


	/**
	 *  storesLowerCaseIdentifiers method: Does the database treat mixed case
	 *  unquoted SQL identifiers as case insensitive and store them in lower case?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean storesLowerCaseIdentifiers() throws SQLException {
		return true;
	}


	/**
	 *  storesMixedCaseIdentifiers method: Does the database treat mixed case
	 *  unquoted SQL identifiers as case insensitive and store them in mixed case?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean storesMixedCaseIdentifiers() throws SQLException {
		return false;
	}


	/**
	 *  supportsMixedCaseQuotedIdentifiers method: Does the database treat mixed
	 *  case quoted SQL identifiers as case sensitive and as a result store them in
	 *  mixed case?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		return false;
	}


	/**
	 *  storesUpperCaseQuotedIdentifiers method: Does the database treat mixed case
	 *  quoted SQL identifiers as case insensitive and store them in upper case?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		return false;
	}


	/**
	 *  storesLowerCaseQuotedIdentifiers method: Does the database treat mixed case
	 *  quoted SQL identifiers as case insensitive and store them in lower case?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		return true;
	}


	/**
	 *  storesMixedCaseQuotedIdentifiers method: Does the database treat mixed case
	 *  quoted SQL identifiers as case insensitive and store them in mixed case?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		return false;
	}


	//--------------------------------------------------------------------
	//        Functions describing which features are supported:
	//--------------------------------------------------------------------

	/**
	 *  supportsAlterTableWithAddColumn method: Is "ALTER TABLE" with add column
	 *  supported?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		return true;
	}


	/**
	 *  supportsAlterTableWithDropColumn method: Is "ALTER TABLE" with drop column
	 *  supported?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		return true;
	}


	/**
	 *  Is column aliasing supported? <P>
	 *
	 *  If so, the SQL AS clause can be used to provide names for computed columns
	 *  or to provide alias names for columns as required. A JDBC compliant driver
	 *  always returns true.
	 *
	 * @return                   true if so
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsColumnAliasing() throws SQLException {
		return true;
	}


	/**
	 *  Are concatenations between NULL and non-NULL values NULL? A JDBC compliant
	 *  driver always returns true.
	 *
	 * @return                   true if so
	 * @exception  SQLException  Description of Exception
	 */
	public boolean nullPlusNonNullIsNull() throws SQLException {
		return true;
	}


	/**
	 *  supportsConvert method: Is the CONVERT function between SQL types
	 *  supported?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsConvert() throws SQLException {
		return false;
	}


	/**
	 *  supportsConvert method: Is the CONVERT function between SQL types
	 *  supported?
	 *
	 * @param  fromType          is the type to convert from.
	 * @param  toType            is the type to convert to.
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsConvert(int fromType, int toType) throws SQLException {
		ExceptionManager.getManager().throwException(301, "supportsConvert method is not supported.");
		return false;
	}


	/**
	 *  Are table correlation names supported? A JDBC compliant driver always
	 *  returns true.
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 * @jdbc.notsure
	 */
	public boolean supportsTableCorrelationNames() throws SQLException {
		return true;
	}


	/**
	 *  If table correlation names are supported, are they restricted to be
	 *  different from the names of the tables? A JDBC compliant driver always
	 *  returns true.
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 * @jdbc.notsure
	 */
	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		return true;
	}


	/**
	 *  supportsExpressionsInOrderBy method: Are expressions in "ORDER BY" lists
	 *  supported?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsExpressionsInOrderBy() throws SQLException {
		return false;
	}


	/**
	 *  supportsOrderByUnrelated method: Can an "ORDER BY" clause use columns not
	 *  in the SELECT statement?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsOrderByUnrelated() throws SQLException {
		return false;
	}


	/**
	 *  supportsGroupBy method: Is some form of "GROUP BY" clause supported?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsGroupBy() throws SQLException {
		return true;
	}


	/**
	 *  supportsGroupByUnrelated method: Can a "GROUP BY" clause use columns not in
	 *  the SELECT?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsGroupByUnrelated() throws SQLException {
		return false;
	}


	/**
	 *  supportsGroupByBeyondSelect method: Can a "GROUP BY" clause add columns not
	 *  in the SELECT provided it specifies all the columns in the SELECT?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsGroupByBeyondSelect() throws SQLException {
		return true;
	}


	/**
	 *  Is the escape character in "LIKE" clauses supported? A JDBC compliant
	 *  driver always returns true.
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsLikeEscapeClause() throws SQLException {
		return false;
	}


	/**
	 *  supportsMultipleResultSets method: Are multiple ResultSet from a single
	 *  execute supported?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsMultipleResultSets() throws SQLException {
		return false;
	}


	/**
	 *  supportsMultipleTransactions method: Can we have multiple transactions open
	 *  at once (on different connections)?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsMultipleTransactions() throws SQLException {
		return true;
	}


	/**
	 *  Can columns be defined as non-nullable? A JDBC compliant driver always
	 *  returns true.
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsNonNullableColumns() throws SQLException {
		return true;
	}


	/**
	 *  Is the ODBC Minimum SQL grammar supported? All JDBC compliant drivers must
	 *  return true.
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsMinimumSQLGrammar() throws SQLException {
		return true;
	}


	/**
	 *  supportsCoreSQLGrammar method: Is the ODBC Core SQL grammar supported?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsCoreSQLGrammar() throws SQLException {
		return true;
	}


	/**
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 * @jdbc.notsure
	 */
	public boolean supportsExtendedSQLGrammar() throws SQLException {
		return true;
	}


	/**
	 *  Is the ANSI92 entry level SQL grammar supported? All JDBC compliant drivers
	 *  must return true.
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 * @jdbc.notsure
	 */
	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		return true;
	}


	/**
	 *  supportsANSI92IntermediateSQL method: Is the ANSI92 intermediate SQL
	 *  grammar supported?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		return true;
	}


	/**
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 * @jdbc.notsure
	 */
	public boolean supportsANSI92FullSQL() throws SQLException {
		return false;
	}


	/**
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 * @jdbc.notsure
	 */
	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		return true;
	}


	/**
	 *  supportsOuterJoins method: Is some form of outer join supported?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsOuterJoins() throws SQLException {
		return true;
	}


	/**
	 *  supportsFullOuterJoins method: Are full nested outer joins supported?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsFullOuterJoins() throws SQLException {
		return true;
	}


	/**
	 *  supportsLimitedOuterJoins method: Is there limited support for outer joins?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsLimitedOuterJoins() throws SQLException {
		return true;
	}


	/**
	 *  supportsSchemasInDataManipulation method is not supported.
	 *
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsSchemasInDataManipulation() throws SQLException {
		return false;
	}


	/**
	 *  supportsSchemasInProcedureCalls method is not supported.
	 *
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		return false;
	}


	/**
	 *  supportsSchemasInTableDefinitions method is not supported.
	 *
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		return false;
	}


	/**
	 *  supportsSchemasInIndexDefinitions method is not supported.
	 *
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		return false;
	}


	/**
	 *  supportsSchemasInPrivilegeDefinitions method is not supported.
	 *
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		return false;
	}


	/**
	 *  supportsCatalogsInDataManipulation method is not supported.
	 *
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		return false;
	}


	/**
	 *  supportsCatalogsInProcedureCalls method is not supported.
	 *
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		return false;
	}


	/**
	 *  supportsCatalogsInTableDefinitions method is not supported.
	 *
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		return false;
	}


	/**
	 *  supportsCatalogsInIndexDefinitions method is not supported.
	 *
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		return false;
	}


	/**
	 *  supportsCatalogsInPrivilegeDefinitions method is not supported.
	 *
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		return false;
	}


	/**
	 *  supportsPositionedDelete method: Is positioned DELETE supported?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsPositionedDelete() throws SQLException {
		return true;
	}


	/**
	 *  supportsPositionedUpdate method: Is positioned UPDATE supported?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsPositionedUpdate() throws SQLException {
		return true;
	}


	/**
	 *  supportsSelectForUpdate method: Is SELECT for UPDATE supported?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsSelectForUpdate() throws SQLException {
		return true;
	}


	/**
	 *  supportsStoredProcedures method: Are stored procedure calls using the
	 *  stored procedure escape syntax supported?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsStoredProcedures() throws SQLException {
		return false;
	}


	/**
	 *  Are subqueries in comparison expressions supported? A JDBC compliant driver
	 *  always returns true.
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsSubqueriesInComparisons() throws SQLException {
		return true;
	}


	/**
	 *  Are subqueries in exists expressions supported? A JDBC compliant driver
	 *  always returns true.
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsSubqueriesInExists() throws SQLException {
		return true;
	}


	/**
	 *  Are subqueries in "in" statements supported? A JDBC compliant driver always
	 *  returns true.
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsSubqueriesInIns() throws SQLException {
		return true;
	}


	/**
	 *  Are subqueries in quantified expressions supported? A JDBC compliant driver
	 *  always returns true.
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 * @jdbc.notsure
	 */
	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		return true;
	}


	/**
	 *  Are correlated subqueries supported? A JDBC compliant driver always returns
	 *  true.
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 * @jdbc.notsure
	 */
	public boolean supportsCorrelatedSubqueries() throws SQLException {
		return false;
	}


	/**
	 *  Is SQL UNION supported? A JDBC compliant driver always returns true.
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsUnion() throws SQLException {
		return true;
	}


	/**
	 *  Is SQL UNION ALL supported? 0 A JDBC compliant driver always returns true.
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsUnionAll() throws SQLException {
		return true;
	}


	/**
	 *  supportsOpenCursorsAcrossCommit method: Can cursors remain open across
	 *  commits?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		return true;
	}


	/**
	 *  supportsOpenCursorsAcrossRollback method: Can cursors remain open across
	 *  rollbacks?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		return true;
	}


	/**
	 *  supportsOpenStatementsAcrossCommit method: Can statements remain open
	 *  across commits?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		return true;
	}


	/**
	 *  supportsOpenStatementsAcrossRollback method: Can statements remain open
	 *  across rollbacks?
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		return true;
	}


	/**
	 *  supportsTransactions method: Are transactions supported? If not, invoking
	 *  the method commit is a noop and the isolation level is TRANSACTION_NONE.
	 *
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsTransactions() throws SQLException {
		return (connection.getTransactionIsolation() != Connection.TRANSACTION_NONE);
	}


	/**
	 *  supportsTransactionIsolationLevel method: Does this database support the
	 *  given transaction isolation level?
	 *
	 * @param  level             Description of Parameter
	 * @return                   true if so;false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
		return (level <= connection.getTransactionIsolation());
	}


	// Blobs not soported

	/**
	 *  doesMaxRowSizeIncludeBlobs method is not supported.
	 *
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		//ExceptionManager.getManager().throwException(301, "doesMaxRowSizeIncludeBlobs method is not supported.");
		return false;
		// This statement will be never reached, but the compiler needs it.
	}


	//--------------------------------------------------------------------
	// Methods specifying whether you can have data definition statements
	// as part of a transaction and what happens if you do:
	//--------------------------------------------------------------------

	/**
	 *  supportsDataDefinitionAndDataManipulationTransactions method: Are both data
	 *  definition and data manipulation statements within a transaction supported?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
		return false;
	}


	/**
	 *  supportsDataManipulationTransactionsOnly method: Are only data manipulation
	 *  statements within a transaction supported?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
		return true;
	}


	/**
	 *  dataDefinitionCausesTransactionCommit method: Does a data definition
	 *  statement within a transaction force the transaction to commit?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		return false;
	}


	/**
	 *  dataDefinitionIgnoredInTransactions method: Is a data definition statement
	 *  within a transaction ignored?
	 *
	 * @return                   true if so; false otherwise
	 * @exception  SQLException  Description of Exception
	 */
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		return false;
	}


	//--------------------------------------------------------------------
	//     Methods of JDBC 2.0
	//--------------------------------------------------------------------

	/*
	 *  @jdbc.pending		Necesario para JDBC 2.0 .Falta por implementar
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  type              Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsResultSetType(int type) throws SQLException {
		return true;
	}


	/*
	 *  @jdbc.pending		Necesario para JDBC 2.0 .Falta por implementar
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  type              Description of Parameter
	 * @param  concurrency       Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
		return true;
	}


	/*
	 *  @jdbc.pending		Necesario para JDBC 2.0 .Falta por implementar
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  type              Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean ownUpdatesAreVisible(int type) throws SQLException {
		return false;
	}


	/*
	 *  @jdbc.pending		Necesario para JDBC 2.0 .Falta por implementar
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  type              Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean ownDeletesAreVisible(int type) throws SQLException {
		return false;
	}


	/*
	 *  @jdbc.pending		Necesario para JDBC 2.0 .Falta por implementar
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  type              Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean ownInsertsAreVisible(int type) throws SQLException {
		return false;
	}


	/*
	 *  @jdbc.pending		Necesario para JDBC 2.0 .Falta por implementar
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  type              Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean othersUpdatesAreVisible(int type) throws SQLException {
		return false;
	}


	/*
	 *  @jdbc.pending		Necesario para JDBC 2.0 .Falta por implementar
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  type              Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean othersDeletesAreVisible(int type) throws SQLException {
		return false;
	}


	/*
	 *  @jdbc.pending		Necesario para JDBC 2.0 .Falta por implementar
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  type              Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean othersInsertsAreVisible(int type) throws SQLException {
		return false;
	}


	/*
	 *  @jdbc.pending		Necesario para JDBC 2.0 .Falta por implementar
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  type              Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean updatesAreDetected(int type) throws SQLException {
		return false;
	}


	/*
	 *  @jdbc.pending		Necesario para JDBC 2.0 .Falta por implementar
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  type              Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean deletesAreDetected(int type) throws SQLException {
		return false;
	}


	/*
	 *  @jdbc.pending		Necesario para JDBC 2.0 .Falta por implementar
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  type              Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean insertsAreDetected(int type) throws SQLException {
		return false;
	}


	/*
	 *  @jdbc.pending		Necesario para JDBC 2.0 .Falta por implementar
	 */
	/**
	 *  Description of the Method
	 *
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean supportsBatchUpdates() throws SQLException {
		return false;
	}


	/**
	 *  Gets the Default attribute of the TTDataBaseMetaData object
	 *
	 * @param  tabid             Description of Parameter
	 * @param  colno             Description of Parameter
	 * @param  typecol           Description of Parameter
	 * @param  tablename         Description of Parameter
	 * @exception  SQLException  Description of Exception
	 */
	private void getDefault(String tablename, int tabid, short colno, int typecol) throws SQLException {
		ResultSet resultset1 = null;
		ResultSet resultset3 = null;
		Statement statement1;
		PreparedStatement preparedStatement2;
		Statement statement3;
		String defaultValue = null;
		String type = null;
		String text = null;

		statement3 = connection.createStatement();
		statement1 = connection.createStatement();

		preparedStatement2 = connection.prepareStatement(
			"UPDATE " + tablename + " SET " +
			" COLUMN_DEF = ?" +
			" where current of jdbc_cursor2"
			);

		resultset1 = statement1.executeQuery(
			"select text , type from syscolattr where colno = " + colno + " and tabid = " + tabid +
			" and (type ='D' or type = 'd' or type = 't' or type = 'c')"
			);
		if (resultset1.next()) {
			text = resultset1.getString(1);
			type = resultset1.getString(2);
		}
		resultset1.close();
// New Eva 14-01-2001
		statement1.close();
// New Eva 14-01-2001
		statement3.setCursorName("jdbc_cursor2");
		resultset3 = statement3.executeQuery(
			"select COLUMN_DEF from " + tablename + " where ORDINAL_POSITION =  " +
			colno +
			" for update"
			);
		while (resultset3.next()) {
			if (type != null) {
				if (type.equals("D")) {
					defaultValue = text;
				}
				if ((type.equals("d")) && (typecol == CtsqlType.DATE_TYPE)) {
					defaultValue = "today";
				}
				if ((type.equals("t")) && (typecol == CtsqlType.TIME_TYPE)) {
					defaultValue = "now";
				}
			}
			if (defaultValue == null) {
				preparedStatement2.setNull(1, java.sql.Types.CHAR);
			} else {
				preparedStatement2.setString(1, defaultValue);
			}
			preparedStatement2.execute();
		}
// New Eva 14-01-2001
		preparedStatement2.close();
		resultset3.close();
		statement3.close();
// New Eva 14-01-2001
	}


	/**
	 *  Gets the Label attribute of the TTDataBaseMetaData object
	 *
	 * @param  tablename         Description of Parameter
	 * @param  tabid             Description of Parameter
	 * @param  colno             Description of Parameter
	 * @exception  SQLException  Description of Exception
	 */
	private void getLabel(String tablename, int tabid, short colno) throws SQLException {
		ResultSet resultset1 = null;
		ResultSet resultset3 = null;
		Statement statement1;
		PreparedStatement preparedStatement2;
		Statement statement3;
		String label = null;
		String type = null;
		String text = null;

		statement3 = connection.createStatement();
		statement1 = connection.createStatement();

		preparedStatement2 = connection.prepareStatement(
			"UPDATE " + tablename + " SET " +
			" REMARKS = ?" +
			" where current of jdbc_cursor3"
			);

		resultset1 = statement1.executeQuery(
			"select text , type from syscolattr where type='L' and colno = " + colno + " and tabid = " + tabid
			);
		if (resultset1.next()) {
			text = resultset1.getString(1);
			type = resultset1.getString(2);
		}
		resultset1.close();
		statement1.close();

		statement3.setCursorName("jdbc_cursor3");
		resultset3 = statement3.executeQuery(
			"select REMARKS from " + tablename + " where ORDINAL_POSITION =  " +
			colno +
			" for update"
			);
		while (resultset3.next()) {
			if ((type != null) && type.equals("L")) {
				label = text;
			}
			if (label == null) {
				preparedStatement2.setNull(1, java.sql.Types.CHAR);
			} else {
				preparedStatement2.setString(1, label);
			}
			preparedStatement2.execute();
		}
		preparedStatement2.close();
		resultset3.close();
		statement3.close();
	}


	/**
	 *  Gets the IndexInfo attribute of the TTDataBaseMetaData object
	 *
	 * @param  ordinal_position  Description of Parameter
	 * @param  tablename         Description of Parameter
	 * @exception  SQLException  Description of Exception
	 */
	private void getIndexInfo(String tablename, int ordinal_position) throws SQLException {
		ResultSet resultset1 = null;
		ResultSet resultset3 = null;
		ResultSet resultset4 = null;
		PreparedStatement preparedStatement1;
		PreparedStatement preparedStatement2;
		PreparedStatement preparedStatement4;
		Statement statement3;
		String columname;
		String index_name;
		String index_type;
		int asc_or_desc;
		String AscOrDesc;
		short non_unique;
		String index_qualifier;
		statement3 = connection.createStatement();

		preparedStatement2 = connection.prepareStatement(
			"update " + tablename + " set COLUMN_NAME = ?, ASC_OR_DESC = ?, NON_UNIQUE = ?, " +
			" TYPE = " + DatabaseMetaData.tableIndexOther + ", PAGES = NULL, CARDINALITY= NULL," +
		/*
		 *  MOD Paco 13-03-2003. En la documentacion JDBC 2.0 no esta TABLE_QUALIFIER
		 *  " TABLE_QUALIFIER = NULL, INDEX_QUALIFIER = ?," +
		 */
			" INDEX_QUALIFIER = ?," +
		/*
		 *  END-MOD Paco 13-03-2003
		 */
			" FILTER_CONDITION = NULL" +
			" where current of jdbc_cursor1"
			);

		preparedStatement1 = connection.prepareStatement(
			"select colname from syscolumns where colno = ? and tabid = ?"
			);

		preparedStatement4 = connection.prepareStatement(
			"select idxtype from sysindexes where idxname = ? and tabid = ?"
			);

		statement3.setCursorName("jdbc_cursor1");
		resultset3 = statement3.executeQuery("select TYPE, PAGES, INDEX_NAME from " +
			tablename +
			" where ORDINAL_POSITION= " + ordinal_position + " for update");

		while (resultset3.next()) {
			short colno = resultset3.getShort(1);
			/*
			 *  MOD Paco 14-03-2003.
			 *  Si es de ordenacion descendente, colno es negativo.
			 *  Pero en syscolumns nunca una columna es negativa, por lo
			 *  que no encontraria registros y se pegaria una galleta.
			 */
			if (colno < 0) {
				colno = (short) -colno;
			}
			/*
			 *  END-MOD Paco 14-03-2003
			 */
			int tabid = resultset3.getInt(2);
			preparedStatement1.setShort(1, colno);
			preparedStatement1.setInt(2, tabid);
			resultset1 = preparedStatement1.executeQuery();
			resultset1.next();
			columname = resultset1.getString(1);
			index_name = resultset3.getString(3);
			if (index_name.equals("primary")) {
				index_qualifier = "\0";
			} else {
				index_qualifier = index_name;
			}
			asc_or_desc = resultset3.getShort(1);
			preparedStatement4.setString(1, index_name);
			preparedStatement4.setInt(2, resultset3.getInt(2));
			resultset4 = preparedStatement4.executeQuery();
			resultset4.next();
			index_type = resultset4.getString(1);
			if (index_type.equals("U")) {
				non_unique = 0;
			} else {
				non_unique = 1;
			}
			if (asc_or_desc < 0) {
				AscOrDesc = "D";
				asc_or_desc = -asc_or_desc;
			} else {
				AscOrDesc = "A";
			}
			preparedStatement2.setString(1, columname);
			preparedStatement2.setString(2, AscOrDesc);
			preparedStatement2.setShort(3, non_unique);
			preparedStatement2.setString(4, index_qualifier);
			preparedStatement2.execute();
			resultset1.close();
			resultset4.close();
		}
// NEW Eva 14-03-2001
		preparedStatement1.close();
		preparedStatement2.close();
		preparedStatement4.close();
		statement3.close();
		resultset3.close();
// END Eva 14-03-2001
	}


	/**
	 *  Description of the Method
	 *
	 * @param  tablename         Description of Parameter
	 * @exception  SQLException  Description of Exception
	 */
	private void getonUpdateAndOnDeleteAndDeferrability(String tablename) throws SQLException {
		short onupdate = 0;
		short ondelete = 0;
		ResultSet resultset1;
		ResultSet resultset2;

		PreparedStatement preparedStatement1;
		PreparedStatement preparedStatement2;
		PreparedStatement preparedStatement3;

		preparedStatement3 = connection.prepareStatement(
			"UPDATE " + tablename +
			" SET UPDATE_RULE = ?" +
			"  ,DELETE_RULE = ?" +
			"  ,DEFERRABILITY = ?"
			);

		preparedStatement1 = connection.prepareStatement(
			"SELECT 1 FROM sysforeign , " + tablename +
			" WHERE sysforeign.fkname = " + tablename + ".fk_name " +
			" AND sysforeign.on_update = 'R' UNION SELECT 2 FROM sysforeign , " + tablename +
			" WHERE sysforeign.fkname = " + tablename + ".fk_name  AND sysforeign.on_update = 'S'"
			);

		preparedStatement2 = connection.prepareStatement(
			"SELECT 1 FROM sysforeign , " + tablename +
			" WHERE sysforeign.fkname = " + tablename + ".fk_name " +
			" AND sysforeign.on_delete = 'R' UNION SELECT 2 FROM sysforeign , " + tablename +
			" WHERE sysforeign.fkname = " + tablename + ".fk_name  AND sysforeign.on_delete = 'S'"
			);

		resultset1 = preparedStatement1.executeQuery();
		resultset2 = preparedStatement2.executeQuery();

		while (resultset1.next() && resultset2.next()) {
			String update;
			String delete;
			update = resultset1.getString(1);
			delete = resultset2.getString(1);
			if (update.equals("1")) {
				onupdate = DatabaseMetaData.importedKeyRestrict;
			}
			if (update.equals("0")) {
				onupdate = DatabaseMetaData.importedKeySetNull;
			}
			if (delete.equals("1")) {
				ondelete = DatabaseMetaData.importedKeyRestrict;
			}
			if (delete.equals("0")) {
				ondelete = DatabaseMetaData.importedKeySetNull;
			}
			preparedStatement3.setShort(1, onupdate);
			preparedStatement3.setShort(2, ondelete);
			preparedStatement3.setInt(3, DatabaseMetaData.importedKeyNotDeferrable);
			preparedStatement3.execute();
		}
// NEW Eva 14-03-2001
		preparedStatement1.close();
		preparedStatement2.close();
		preparedStatement3.close();
		resultset1.close();
		resultset2.close();
// END Eva 14-03-2001
	}


// New Eva Marzo 2001

	/**
	 *  Description of the Method
	 *
	 * @param  condition         Description of Parameter
	 * @param  tablename         Description of Parameter
	 * @exception  SQLException  Description of Exception
	 */
	private void fillupSynonyms(String tablename, String condition) throws SQLException {
		PreparedStatement preparedStatement;
		ResultSet resulSet;
		Statement statement;

		preparedStatement = connection.prepareStatement
			(
			"insert into " + tablename + " values (NULL, NULL, ?, 'SYNONYM', NULL)"
			);
		statement = connection.createStatement();
		resulSet = statement.executeQuery("select synname from syssynonyms " +
			(condition != null ? "where synname like '" + condition.toLowerCase() + "' " : ""));
		while (resulSet.next()) {
			preparedStatement.setString(1, resulSet.getString(1));
			preparedStatement.execute();
		}
// NEW Eva 14-03-2001
		preparedStatement.close();
		statement.close();
		resulSet.close();
// END Eva 14-03-2001
	}


	/**
	 *  Description of the Method
	 *
	 * @param  condition         Description of Parameter
	 * @param  setType           Description of Parameter
	 * @param  tablename         Description of Parameter
	 * @exception  SQLException  Description of Exception
	 */
	private void fillupTables(String tablename, String condition, String setType) throws SQLException {
		PreparedStatement preparedStatement;
		ResultSet resulSet;
		Statement statement;

		preparedStatement = connection.prepareStatement
			(
			"insert into " + tablename + " values (NULL, NULL, ?, ?, NULL)"
			);
		statement = connection.createStatement();
		resulSet = statement.executeQuery("select tabname from systables where " + condition);
		while (resulSet.next()) {
			preparedStatement.setString(1, resulSet.getString(1));
			preparedStatement.setString(2, setType);
			preparedStatement.execute();
		}
// NEW Eva 14-03-2001
		preparedStatement.close();
		statement.close();
		resulSet.close();
// END Eva 14-03-2001
	}


	/**
	 *  Description of the Method
	 *
	 * @param  condition         Description of Parameter
	 * @param  tablename         Description of Parameter
	 * @exception  SQLException  Description of Exception
	 */
	private void fillupPrimaryKeys(String tablename, String condition) throws SQLException {
		PreparedStatement preparedStatement = null;
		for (int i = 1; i <= 8; i++) {
			/*
			 *  MOD Paco 13-03-2003. TABLE_OWNER no aparece en la especificacion JDBC 2.0
			 *  preparedStatement = connection.prepareStatement("insert into " + tablename +
			 *  " (TABLE_OWNER, TABLE_NAME, COLUMN_NAME, KEY_SEQ,PK_NAME) " +
			 *  " SELECT systables.owner, systables.tabname, colname, " + i + ", idxname " +
			 *  " FROM systables, syscolumns, sysindexes " +
			 *  " WHERE systables.tabid = syscolumns.tabid " +
			 *  " AND systables.tabid = sysindexes.tabid " +
			 *  " AND syscolumns.colno = systables.part" + i +
			 *  " AND sysindexes .idxname= 'primary' " +
			 *  " AND systables.part" + i + " <> 0" +
			 *  condition
			 *  );
			 */
			preparedStatement = connection.prepareStatement("insert into " + tablename +
				" (TABLE_NAME, COLUMN_NAME, KEY_SEQ,PK_NAME) " +
				" SELECT systables.tabname, colname, " + i + ", idxname " +
				" FROM systables, syscolumns, sysindexes " +
				" WHERE systables.tabid = syscolumns.tabid " +
				" AND systables.tabid = sysindexes.tabid " +
				" AND syscolumns.colno = systables.part" + i +
				" AND sysindexes .idxname= 'primary' " +
				" AND systables.part" + i + " <> 0" +
				condition
				);
			/*
			 *  END-MOD Paco 13-03-2003
			 */
			preparedStatement.execute();
// NEW Eva 14-03-2001
			preparedStatement.close();
// END Eva 14-03-2001
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  condition         Description of Parameter
	 * @param  tablename         Description of Parameter
	 * @exception  SQLException  Description of Exception
	 */
	private void fillupIndexInfo(String tablename, String condition) throws SQLException {
		PreparedStatement preparedStatement = null;
		for (int i = 1; i <= 8; i++) {
// Introduzco en el campo TYPE sysindexes.parti para determinar si es ASCENDENTE o DESCENDENTE
			preparedStatement = connection.prepareStatement("insert into " + tablename +
			/*
			 *  MOD Paco 13-03-2003. En la documentacion de JDBC 2.0, no existen TABLE_OWNER
			 *  ni TABLE_QUALIFIER, sino TABLE_CAT y TABLE_SCHEM
			 *  /		    " (TABLE_OWNER, TABLE_NAME, ORDINAL_POSITION, " +
			 */
				" (TABLE_SCHEM, TABLE_NAME, ORDINAL_POSITION, " +
			/*
			 *  END-MOD Paco 13-03-2003
			 */
				//MODIFICADO PACO-FERMIN netbeans. Hemos añadido TABLE_SCHEM
				" INDEX_NAME, TYPE, PAGES) " +
//		    " SELECT systables.owner, systables.tabname, " + i +
				" SELECT '"+TTDataBaseMetaData.DEFAULT_SCHEMA+"', systables.tabname, " + i +
				", idxname, sysindexes.part" + i + ", sysindexes.tabid" +
				" FROM systables, sysindexes " +
			/*
			 *  MOD Paco 22-04-2002 Con esta condicion, solamente sacaba las PK. Si quiero sacar las PK
			 *  y el resto de los indices, debo referirme a sysindexes en lugar de systables
			 *  " WHERE systables.part" + i + " <> 0" +
			 */
				" WHERE sysindexes.part" + i + " <> 0" +
			/*
			 *  END-MOD Paco 22-04-2002
			 */
				" AND systables.tabid = sysindexes.tabid " +
				condition
				);
			preparedStatement.execute();
			getIndexInfo(tablename, i);
			preparedStatement.close();
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  condition         Description of Parameter
	 * @param  tablename         Description of Parameter
	 * @exception  SQLException  Description of Exception
	 */
	private void fillupForeignKeys(String tablename, String condition) throws SQLException {
		
		PreparedStatement preparedStatement = null;
		for (int i = 1; i < 9; i++) {
			preparedStatement = connection.prepareStatement
				(
			/*
			 *  MOD Paco 23-04-2002. No sacaba bien los valores
			 *  "INSERT INTO " + tablename + " (PKTABLE_CAT, PKTABLE_NAME, " +
			 *  "PKCOLUMN_NAME, FKTABLE_CAT, FKTABLE_NAME, " +
			 *  "FKCOLUMN_NAME, KEY_SEQ, FK_NAME) " +
			 *  "SELECT systabf.owner, systabf.tabname, " +
			 *  " syscolp.colname, systabp.owner, " +
			 *  " systabp.tabname, syscolf.colname, " + i +
			 *  " , sysforeign.fkname " +
			 *  " FROM sysforeign, systables systabp, " +
			 *  " systables systabf, syscolumns syscolp, " +
			 *  " syscolumns syscolf " +
			 *  " WHERE dtabid = systabp.tabid " +
			 *  " AND dtabid = syscolf.tabid " +
			 *  " AND syscolp.colno = systabp.Part" + i +
			 *  " AND rtabid = systabf.tabid " +
			 *  " AND systabf.tabid = syscolp.tabid " +
			 *  " AND syscolf.colno = sysforeign.Part" + i +
			 *  condition
			 */
		        /* NO USAMOS DE MOMENTO SCHEMAS, y no existe relacion de momento entre SCHEMA y OWNER CTSQL
				"INSERT INTO " + tablename + " (PKTABLE_CAT, PKTABLE_NAME, " +
				"PKCOLUMN_NAME, FKTABLE_CAT, FKTABLE_NAME, " +
				"FKCOLUMN_NAME, KEY_SEQ, FK_NAME) " +
				"SELECT systabp.owner, systabp.tabname, " +
				" syscolp.colname, systabf.owner, " + */
						
				"INSERT INTO " + tablename + " (PKTABLE_NAME, " +
				"PKCOLUMN_NAME, FKTABLE_NAME, " +
				"FKCOLUMN_NAME, KEY_SEQ, FK_NAME) " +
				"SELECT systabp.tabname, " +
				" syscolp.colname, " +
			
				" systabf.tabname, syscolf.colname, " + i +
				" , sysforeign.fkname " +
				" FROM sysforeign, systables systabp, " +
				" systables systabf, syscolumns syscolp, " +
				" syscolumns syscolf " +
				" WHERE dtabid = systabf.tabid " +
				" AND syscolf.tabid = systabf.tabid " +
				" AND systabp.tabid = rtabid " +
				" AND syscolp.tabid = systabp.tabid " +
				" AND syscolp.colno = systabp.Part" + i +
				" AND syscolf.colno = sysforeign.Part" + i +
				condition
				);
			/*
			 *  END-MOD Paco 23-04-2002. No sacaba bien los valores
			 */
			preparedStatement.execute();
// NEW Eva 14-03-2001
			preparedStatement.close();
// END Eva 14-03-2001
		}
		getonUpdateAndOnDeleteAndDeferrability(tablename);
	}


// END New Eva Marzo 2001

// NEW Eva 19-03-2001
	/**
	 *  Description of the Method
	 *
	 * @param  tablename         Description of Parameter
	 * @param  condition         Description of Parameter
	 * @exception  SQLException  Description of Exception
	 */
	private void fillupColumnPrivileges(String tablename, String condition) throws SQLException {
		PreparedStatement preparedStatement = null;
		String Privilege_desc[] = {"SELECT", "UPDATE", "INSERT", "DELETE"};
		String Privilege_Ident[] = {"s", "u", "i", "d"};
		int Privilege_Pos[] = {1, 2, 4, 5};
		Statement statement;

		for (int i = 0; i < 4; i++) {
			//  for tables/views
			preparedStatement = connection.prepareStatement
				(
				/* NO USAMOS DE MOMENTO SCHEMAS, y no existe relacion de momento entre SCHEMA y OWNER CTSQL
				"INSERT INTO " + tablename + " (TABLE_CAT, TABLE_NAME, COLUMN_NAME, " +
				" GRANTOR, GRANTEE, PRIVILEGE) " +
				"SELECT owner, tabname, colname, grantor, grantee, '" +
				*/
				"INSERT INTO " + tablename + " (TABLE_NAME, COLUMN_NAME, " +
				" GRANTOR, GRANTEE, PRIVILEGE) " +
				"SELECT tabname, colname, grantor, grantee, '" +
				
				Privilege_desc[i] +
				"' FROM syscolumns, systabauth, systables " +
				" WHERE syscolumns.tabid = systables.tabid " +
				" AND systabauth.tabid = systables.tabid " +
				" AND tabauth[3] <> '*' " +
				" AND tabauth[" + Privilege_Pos[i] + "] in ('" +
				Privilege_Ident[i] + "', '" + Privilege_Ident[i].toUpperCase() + "')" +
				condition
				);
			preparedStatement.execute();
			preparedStatement.close();
		}

		for (int i = 0; i < 2; i++) {
			// Authorization for tables/views
			preparedStatement = connection.prepareStatement
				(
				/* NO USAMOS DE MOMENTO SCHEMAS, y no existe relacion de momento entre SCHEMA y OWNER CTSQL
				"INSERT INTO " + tablename + " (TABLE_CAT, TABLE_NAME, COLUMN_NAME, " +
				" GRANTOR, GRANTEE, PRIVILEGE) " +
				"SELECT DISTINCT owner, tabname,colname, systabauth.grantor, systabauth.grantee, '" +
				*/
				"INSERT INTO " + tablename + " (TABLE_NAME, COLUMN_NAME, " +
				" GRANTOR, GRANTEE, PRIVILEGE) " +
				"SELECT tabname,colname, systabauth.grantor, systabauth.grantee, '" +
				
				Privilege_desc[i] +
				"' FROM syscolumns, systabauth, systables, syscolauth " +
				" WHERE systabauth.tabid = systables.tabid " +
				" AND syscolumns.tabid = systables.tabid " +
				" AND systabauth.tabid = syscolauth.tabid " +
				" AND colauth[" + (int) (i + 1) + "] in ('" +
				Privilege_Ident[i] + "', '" + Privilege_Ident[i].toUpperCase() + "')" +
				condition
				);
			preparedStatement.execute();
			preparedStatement.close();
		}

		statement = connection.createStatement();

		statement.execute(
			"UPDATE " + tablename + " SET IS_GRANTABLE = 'YES'" +
			" WHERE TABLE_CAT IN " +
			" (SELECT username FROM sysusers " +
			" WHERE usertype = 'D')"
			);
		statement.close();

		statement.execute(
			"UPDATE " + tablename + " SET IS_GRANTABLE = 'NO'" +
			" WHERE IS_GRANTABLE IS NULL"
			);
		statement.close();

	}


	/**
	 *  Description of the Method
	 *
	 * @param  tablename         Description of Parameter
	 * @param  tableNamePattern  Description of Parameter
	 * @param  schemaPattern     Description of Parameter
	 * @exception  SQLException  Description of Exception
	 */
	private void fillupTablePrivileges(String tablename, String tableNamePattern, String schemaPattern) throws SQLException {
		PreparedStatement preparedStatement = null;
		String Privilege_desc[] = {"SELECT", "UPDATE", "INSERT", "DELETE"};
		String Privilege_Ident[] = {"s", "u", "i", "d"};
		int Privilege_Pos[] = {1, 2, 4, 5};
		String condition = "";
		Statement statement;

		for (int i = 0; i < 4; i++) {
			if (tableNamePattern != null && tableNamePattern.equals("") == false) {
				condition = " and tabname like '" + tableNamePattern + "'";
	        /* NO USAMOS DE MOMENTO SCHEMAS, y no existe relacion de momento entre SCHEMA y OWNER CTSQL
			} else if (schemaPattern != null && schemaPattern.equals("") == false) {
				condition += " and owner like '" + schemaPattern + "'";
			*/
			} else {
				condition = "";
			}

			preparedStatement = connection.prepareStatement
				(
		        /* NO USAMOS DE MOMENTO SCHEMAS, y no existe relacion de momento entre SCHEMA y OWNER CTSQL
				"INSERT INTO " + tablename + " (TABLE_CAT, TABLE_NAME," +
				" GRANTOR, GRANTEE, PRIVILEGE) " +
				"SELECT owner, tabname, grantor, grantee, '" +
				*/
				"INSERT INTO " + tablename + " (TABLE_NAME," +
				" GRANTOR, GRANTEE, PRIVILEGE) " +
				"SELECT tabname, grantor, grantee, '" +
				
				Privilege_desc[i] +
				"' FROM systabauth, systables " +
				" where systabauth.tabid = systables.tabid " +
				" AND tabauth[" + Privilege_Pos[i] + "] in ('" +
				Privilege_Ident[i] + "', '" + Privilege_Ident[i].toUpperCase() + "')" +
				condition
				);
			preparedStatement.execute();
			preparedStatement.close();

			if (tableNamePattern != null && tableNamePattern.equals("") == false) {
				condition = " and synname like '" + tableNamePattern + "'";
			/* NO USAMOS DE MOMENTO SCHEMAS, y no existe relacion de momento entre SCHEMA y OWNER CTSQL
			} else	if (schemaPattern != null && schemaPattern.equals("") == false) {
				condition += " and owner like '" + schemaPattern + "'";
			*/
				
			} else {
				condition = "";
			}

			preparedStatement = connection.prepareStatement
				(
				/* NO USAMOS DE MOMENTO SCHEMAS, y no existe relacion de momento entre SCHEMA y OWNER CTSQL
				"INSERT INTO " + tablename + " (TABLE_CAT, TABLE_NAME," +
				" GRANTOR, GRANTEE, PRIVILEGE) " +
				"SELECT owner, synname, grantor, grantee, '" +
				*/
				"INSERT INTO " + tablename + " (TABLE_NAME," +
				" GRANTOR, GRANTEE, PRIVILEGE) " +
				"SELECT synname, grantor, grantee, '" +
				
				Privilege_desc[i] +
				"' FROM systabauth, syssynonyms " +
				" where systabauth.tabid = syssynonyms.tabid " +
				" AND tabauth[3] <> '*' " +
				" AND tabauth[" + Privilege_Pos[i] + "] in ('" +
				Privilege_Ident[i] + "', '" + Privilege_Ident[i].toUpperCase() + "')" +
				condition
				);
			preparedStatement.execute();
			preparedStatement.close();
		}

		statement = connection.createStatement();

		statement.execute(
			"UPDATE " + tablename + " SET IS_GRANTABLE = 'YES'" +
			" WHERE TABLE_CAT IN " +
			" (SELECT username FROM sysusers " +
			" WHERE usertype = 'D')"
			);
		statement.close();

		statement.execute(
			"UPDATE " + tablename + " SET IS_GRANTABLE = 'NO'" +
			" WHERE IS_GRANTABLE IS NULL"
			);
		statement.close();
	}


// NEW Eva 19-03-2001
	/**
	 *  Description of the Method
	 *
	 * @param  tablename         Description of the Parameter
	 * @param  cols              Description of the Parameter
	 * @param  extra             Description of the Parameter
	 * @return                   Description of the Return Value
	 * @exception  SQLException  Description of the Exception
	 */
	private String createTempTable(String tablename, String cols, String extra) throws SQLException {
		Statement statement;
		// New Raul (Co-De) 21-04-2003. Sincrizamos el acceso al contador
		synchronized (counter_monitor) {
			++counter;
			statement = connection.createStatement();
			statement.execute("create temp table " + tablename + counter + " (" + cols + ")" + extra);
			statement.close();
			return tablename + counter;
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  tablename         Description of the Parameter
	 * @param  cols              Description of the Parameter
	 * @return                   Description of the Return Value
	 * @exception  SQLException  Description of the Exception
	 */
	private String createTempTable(String tablename, String cols) throws SQLException {
		return createTempTable(tablename, cols, "");
	}

// Nuevos métodos JDK 1.4

	public ResultSet getAttributes(
		String catalog,
		String schemaPattern,
		String typeNamePattern,
		String attributeNamePattern)
		throws SQLException {
			// TODO getAttributes( String, String, String, String )  method is not supported, UDT not supportted.
			ExceptionManager.getManager().throwException(301, "getAttributes method is not supported.");
			// This statement will be never reached, but the compiler needs it.
			return null;
	}

	public int getDatabaseMajorVersion() throws SQLException {
		return 0;
	}

	public int getDatabaseMinorVersion() throws SQLException {
		return 0;
	}

	public int getJDBCMajorVersion() throws SQLException {
		return 2;
	}

	public int getJDBCMinorVersion() throws SQLException {
		return 0;
	}

	public int getResultSetHoldability() throws SQLException {
		// TODO getResultSetHoldability() method is not supported.
		ExceptionManager.getManager().throwException(301, "getResultSetHoldability method is not supported.");
		// This statement will be never reached, but the compiler needs it.
		return -1;
	}

	/**
	 * @see java.sql.DatabaseMetaData#getSQLStateType()
	 */
	public int getSQLStateType() throws SQLException {
		// TODO getSQLStateType() method is not supported ( ¿ X/Open ,SQL CLI, SQL 99 ? ).
		ExceptionManager.getManager().throwException(301, "getSQLStateType method is not supported.");
		// This statement will be never reached, but the compiler needs it.
		return -1;
	}

	public ResultSet getSuperTables(
		String catalog,
		String schemaPattern,
		String tableNamePattern)
		throws SQLException {
		// TODO getSuperTables( String, String, String ) method is not supported.
		ExceptionManager.getManager().throwException(301, "getSuperTables method is not supported.");
		// This statement will be never reached, but the compiler needs it.
		return null;
	}

	/**
	 * @see java.sql.DatabaseMetaData#getSuperTypes(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getSuperTypes(
		String catalog,
		String schemaPattern,
		String typeNamePattern)
		throws SQLException {
			// TODO getSuperTypes( String, String, String ) method is not supported.
			ExceptionManager.getManager().throwException(301, "getSuperTypes method is not supported.");
			// This statement will be never reached, but the compiler needs it.
			return null;
	}

	public boolean locatorsUpdateCopy() throws SQLException {
		// TODO locatorsUpdateCopy() method is not supported.
		ExceptionManager.getManager().throwException(301, "locatorsUpdateCopy method is not supported.");
		// This statement will be never reached, but the compiler needs it.
		return false;
	}

	public boolean supportsGetGeneratedKeys() throws SQLException {
		// TODO Till now. I hope that don´t supports get auto generated keys.
		return false;
	}

	public boolean supportsMultipleOpenResults() throws SQLException {
		// TODO Till now. I hope that don´t supports multiple open results.
		return false;
	}

	public boolean supportsNamedParameters() throws SQLException {
		// TODO Till now. I hope that don´t supports named parameters.
		return false;
	}

	public boolean supportsResultSetHoldability(int holdability)
		throws SQLException {
		// TODO Till now. I hope that don´t supports resultset holdability.
		return false;
	}

	public boolean supportsSavepoints() throws SQLException {
		// TODO Till now. I hope that don´t supports savepoints
		return false;
	}

	public boolean supportsStatementPooling() throws SQLException {
		// TODO Till now. I hope that don´t supports statement pooling
		return false;
	}

	
	public ResultSet nullResultSet() throws SQLException {
		TTStatement statement = (TTStatement) connection.createStatement();
		return  (TTResultSetCatalog) statement.executeQueryCatalog
		(
		"select 'NONE' from systables where tabid<0"
		);		
	}

}
