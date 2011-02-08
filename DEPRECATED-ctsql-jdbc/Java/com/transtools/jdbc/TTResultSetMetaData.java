package com.transtools.jdbc;

import com.transtools.ctsql.CtsqlCursor;
import com.transtools.ctsql.CtsqlType;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 *  TTResultSetMetaData class implements ResultSetmetaData. A resultSetMetaData
 *  object can be used to get information about the types and properties of the
 *  columns in a ResultSet object
 *
 *@author     eva
 *@created    March 7, 2001
 */

class TTResultSetMetaData implements ResultSetMetaData {

	/**
	 *  ctsqlCursor is a CtsqlCursor object. CtsqlCursor is a interface that
	 *  represents the cursor to database access.
	 */
	private CtsqlCursor cursor;


	/**
	 *  Constructor: receives a cursor.
	 *
	 *@param  cursor  represents the cursor to database access.
	 */
	public TTResultSetMetaData(CtsqlCursor cursor) {
		this.cursor = cursor;
	}


	/**
	 *@return          The ColumnCount value
	 *@jdbc.jcosmos    Necesario de implementar para JCosmos.
	 */
	public int getColumnCount() {
		return cursor.getColumnCount();
	}


	/**
	 *  getColumnType method retrieves the designated column's SQL type as an
	 *  integer. This type corresponds to the JDBC types.
	 *
	 *@param  column   given column index return correspond type
	 *@return          SQL type from java.sql.Types as a integer
	 *@jdbc.jcosmos    Necesario de implementar para JCosmos.
	 */
	public int getColumnType(int column) {
		int ctsqlType = cursor.getColumnType(column - 1);
		int jdbcType;
		switch(ctsqlType){
			case CtsqlType.CHAR_TYPE:
				jdbcType = Types.CHAR;
				break;
			case CtsqlType.SMALLINT_TYPE:
				jdbcType = Types.SMALLINT;
				break;
			case CtsqlType.INTEGER_TYPE:
				jdbcType = Types.INTEGER;
				break;
			case CtsqlType.DECIMAL_TYPE:
				jdbcType = Types.DECIMAL;
				break;
			case CtsqlType.DATE_TYPE:
				jdbcType = Types.DATE;
				break;
			case CtsqlType.TIME_TYPE:
				jdbcType = Types.TIME;
				break;
			case CtsqlType.DATETIME_TYPE:
				jdbcType = Types.TIMESTAMP;
				break;
			case CtsqlType.SERIAL_TYPE:
				jdbcType = Types.INTEGER;
				break;
			case CtsqlType.MONEY_TYPE:
				jdbcType = Types.DECIMAL;
				break;
			case CtsqlType.NULL_TYPE:
				jdbcType = Types.NULL;
				break;
			case CtsqlType.BINARY_TYPE:
				jdbcType = Types.BINARY;
				break;
			default:
				jdbcType = Types.OTHER;
		}
		return jdbcType;
	}


	/**
	 *  getColumnTypeName method retrieves the designated column's SQL type as a
	 *  string.
	 *
	 *@param  column   represents the designated column index
	 *@return          SQL type from java.sql.Types as a string
	 *@jdbc.jcosmos    Necesario de implementar para JCosmos.
	 */
	public String getColumnTypeName(int column) {
		return cursor.getColumnTypeName(column - 1);
	}


	/**
	 *  getColumnDisplaySize retrieves the designated column's normal maximum width
	 *  in characters.
	 *
	 *@param  column   represents the designated column index
	 *@return          normal maximun width in characters.
	 *@jdbc.jcosmos    Necesario de implementar para JCosmos.
	 */
	public int getColumnDisplaySize(int column) {
		return cursor.getColumnDisplaySize(column - 1);
	}


	/**
	 *  getColumnLabel gets the designated column's title.
	 *
	 *@param  column   represents the designated column index
	 *@return          the corresponds column's label
	 *@jdbc.jcosmos    Necesario de implementar para JCosmos.
	 */
	public String getColumnLabel(int column) {
		return cursor.getColumnLabel(column - 1);
	}


	/**
	 *  getColumnName method gets the designated column's name.
	 *
	 *@param  column   represents the designated column index
	 *@return          the designated column's name
	 *@jdbc.jcosmos    Necesario de implementar para JCosmos.
	 */
	public String getColumnName(int column) {
		return cursor.getColumnName(column - 1);
	}


	/*
	 * necesary for JDBC 1.0
	 */

	/**
	 *  isAutoIncrement indicates whether the designated column is automatically
	 *  numbered, thus read-only.
	 *
	 *@param  column  It is the column index
	 *@return         true if the designated column is automatically numbered;
	 *      false in other case
	 */
	public boolean isAutoIncrement(int column) {
		return getColumnType(column) == CtsqlType.SERIAL_TYPE;
	}


	/**
	 *  isCaseSensitive indicates whether a column's case matters
	 *
	 *@param  column  It is the column index
	 *@return         true if so; false otherwise
	 */
	public boolean isCaseSensitive(int column) {
		return false;
	}


	/**
	 *  isSearchable indicates whether the designated column can be used in a where
	 *  clause.
	 *
	 *@param  column  It is the column index
	 *@return         true if so; false otherwise
	 */
	public boolean isSearchable(int column) {
		return true;
	}


	/**
	 *  isCurrency indicates whether the designated column is a cash value.
	 *
	 *@param  column  It is the column index
	 *@return         true if so; false otherwise
	 */
	public boolean isCurrency(int column) {
		return (getColumnType(column) != CtsqlType.MONEY_TYPE ? false : true);
	}


	/**
	 *  isSigned indicates whether values in the designated column are signed
	 *  numbers.
	 *
	 *@param  column  It is the column index
	 *@return         true if so; false otherwise
	 */
	public boolean isSigned(int column) {
		int type = 0;
		boolean value = true;

		type = getColumnType(column);
		switch(type){
		  case CtsqlType.SMALLINT_TYPE:
		  case CtsqlType.INTEGER_TYPE:
		  case CtsqlType.DECIMAL_TYPE:
		  case CtsqlType.MONEY_TYPE:
			value = false;
			break;
		  default:
			value = true;
		}
		return value;
	}


	/**
	 *  getSchemaName method is not supported.
	 *
	 *@param  column            Description of Parameter
	 *@return                   The SchemaName value
	 *@exception  SQLException  Description of Exception
	 */
	public String getSchemaName(int column) throws SQLException {
		return TTDataBaseMetaData.DEFAULT_SCHEMA;
	}


	/**
	 *  getPrecision gets the designated column's number of decimal digits.
	 *
	 *@param  column  It is the column index
	 *@return         the precision
	 */
	public int getPrecision(int column) {
		return cursor.getPrecision(column - 1);
	}


	/**
	 *  getScale gets the designated column's number of digits to right of the
	 *  decimal point.
	 *
	 *@param  column  It is the column index
	 *@return         the scale
	 */
	public int getScale(int column) {
		return cursor.getScale(column - 1);
	}


	/**
	 *  getTableName method is not supported.
	 *
	 *@param  tableName         Description of Parameter
	 *@return                   The TableName value
	 *@exception  SQLException  Description of Exception
	 */
	public String getTableName(int column) throws SQLException {
		return cursor.getTableName(column - 1);
	}
	

	/**
	 *  getCatalogName gets the designated column's table's catalog name.
	 *
	 *@param  catalogName  It's the column index
	 *@return              column name or "" if not applicable
	 */
	public String getCatalogName(int catalogName) {
//		String result="systables";
//		return result;
		return null;
	}


	/**
	 *  isReadOnly method is not supported.
	 *
	 *@param  column            Description of Parameter
	 *@return                   The ReadOnly value
	 *@exception  SQLException  Description of Exception
	 */
	public boolean isReadOnly(int column) throws SQLException {
		String name = cursor.getColumnName(column - 1); 
		return (name == null) || name.startsWith("(");
	}


	/**
	 *  isWritable method is not supported.
	 *
	 *@param  column            Description of Parameter
	 *@return                   The Writable value
	 *@exception  SQLException  Description of Exception
	 */
	public boolean isWritable(int column) throws SQLException {
		ExceptionManager.getManager().throwException(301, "isWritable method is not supported.");
		return false;
		// This statement will be never reached, but the compiler needs it.
	}


	/**
	 *  isDefinitelyWritable method is not supported.
	 *
	 *@param  column            Description of Parameter
	 *@return                   The DefinitelyWritable value
	 *@exception  SQLException  Description of Exception
	 */
	public boolean isDefinitelyWritable(int column) throws SQLException {
		ExceptionManager.getManager().throwException(301, "isDefinitelyWritable method is not supported.");
		return false;
		// This statement will be never reached, but the compiler needs it.
	}


	/**
	 *  isNullable Indicates the nullability of values in the designated column.
	 *
	 *@param  column  It's the column index
	 *@return         the nullability status of the given column; one of
	 *      columnNoNulls, columnNullable or columnNullableUnknown Note: in our
	 *      case always return columnNullable
	 */
	public int isNullable(int column) {
		return ResultSetMetaData.columnNullable;
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 *@param  column  Description of Parameter
	 *@return         The ColumnClassName value
	 */
	public String getColumnClassName(int column) {
		return null;
	}

}
