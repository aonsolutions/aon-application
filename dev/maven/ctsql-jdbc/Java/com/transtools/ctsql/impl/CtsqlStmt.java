/*
 *  Copyright 2003
 *
 *  TransTOOLs, S.A.
 *  All rights reserved
 */
package com.transtools.ctsql.impl;
import com.transtools.ctsql.CtsqlConstants;
import com.transtools.ctsql.CtsqlCursor;
import com.transtools.ctsql.CtsqlException;
import com.transtools.ctsql.CtsqlType;

import java.sql.SQLWarning;
import java.util.Arrays;
/**
 * @author          eva
 * @created         March 16, 2001
 * @version         $Revision: 1.40 $
 * @jdbc.pending    hay que repasar todo lo referente al lanzamiento de
 *      excepciones en esta clase.
 */
// This class supports both, statments and cursors.
class CtsqlStmt implements CtsqlCursor {

	private static final String LAST_INSERT_ROWID = "LAST_INSERT_ROWID";
	private static final String LAST_INSERT_ID = "LAST_INSERT_ID";
	private final static int UPDATE_COUNT = 2;
	private final static int ROWID_AFTER_INSERT = 5;

	private final static int NAMEOFFSET_ICOL = 0;
	private final static int TITLEOFFSET_ICOL = 1;
	private final static int DATAOFFSET_ICOL = 2;
	private final static int TYPE_ICOL = 3;
	private final static int LENGTH_ICOL = 4;

	/*
	private final static int SMALLINT_DISPLAY_SIZE = 8;
	private final static int CHAR_DISPLAY_SIZE = 50;
	private final static int INTEGER_DISPLAY_SIZE = 32;
	private final static int DATE_DISPLAY_SIZE = 12;
	private final static int DATETIME_DISPLAY_SIZE = 24;
	private final static int BINARY_DISPLAY_SIZE = 24;
	private final static int NULL_DISPLAY_SIZE = 4;
	*/
	private final static int SMALLINT_DISPLAY_SIZE = 6;
	private final static int INTEGER_DISPLAY_SIZE = 11;
	private final static int DATE_DISPLAY_SIZE = 10;
	private final static int DATETIME_DISPLAY_SIZE = 16;
	private final static int BINARY_DISPLAY_SIZE = 24;
	private final static int NULL_DISPLAY_SIZE = 4;

	private CtsqlServerImpl server = null;
	private CtsqlSqlca sqlca = new CtsqlSqlca();
	private int curop = 0;
	// operacion (FNSELECT,... etc )

	private int id = CtsqlConstants.EMPTY;
	// id. dev. por el SQL

	private short rowSize = 0;
	// tamaño de la tupla
	private boolean bPrepared = false;
	private boolean bOpen = false;
	private int lastCommand = 0;
	private String name;
	private boolean errfl = false;
	private boolean fldone = false;
	private boolean flmov = false;
	private boolean flBrkScroll = false;

	private boolean serialValueObtained = false;

	// buffer de tuplas
	private short recidx = 0;
	// indice a tuplas en buffer
	private short left = 0;
	// numero de tuplas leidas en el buffer

	// Describe
	private int ncols = 0;
	private short noby;
	private byte[] titl;
	// buffer que almacenará los títulos y los nombres de las columnas.
	private byte[] buf;
	// indica si el buffer es OEM
	private boolean bufIsOem;
	private short stat = 0;
	//
	private boolean rTrimChar;

	private short offTuple = 0;
	// offset de la tupla dentro de la caché "buf"
	private short prevrow = 0;
	private short[][] icollis;
	// array que guarda la información sobre las columnas.
	private short[][] oby;

	// Añadido por M.A.
	private int nHost = 0;
	private boolean isLast = false;
	private boolean isAfterLast = false;

	// Para implementación del update de JDBC 2
	private CtsqlType[] columns;
	private boolean[] updated;
	private boolean inInsertRow = false;
	
	private boolean localStatement = false;
	private String localResultType;

//

	/**
	 *  Constructor for the CtsqlStmt object
	 *
	 * @param  aServer  Description of Parameter
	 */
	public CtsqlStmt(CtsqlServerImpl aServer) {
		init();
		server = aServer;
		bufIsOem = aServer.getConvert() == CtsqlServerImpl.CNT_CONVERTOEMTOANSI;
		rTrimChar = aServer.getRTrimChar();
	}


	/**
	 *  Description of the Method
	 *
	 * @param  rsiz  Description of Parameter
	 * @return       Description of the Returned Value
	 */
	private static int sqlbsize(int rsiz) {
		// Hay que definir algun sistema para poder desactivar o aumentar el buffereo
		if (rsiz <= CtsqlConstants.SMALLBUF) {
			return CtsqlConstants.MEDIUMBUF;
		} else if (rsiz <= CtsqlConstants.MEDIUMBUF) {
			return CtsqlConstants.LARGEBUF;
		} else {
			return rsiz;
		}
	}


	/**
	 *  Sets the Name attribute of the CtsqlStmt object
	 *
	 * @param  cursorName          The new Name value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public void setName(String cursorName) throws CtsqlException {
		name = cursorName;
	}

	/**
	 * @param  index
	 * @param  value
	 * @exception  CtsqlException
	 */
	public void setCtsqlChar(int index, com.transtools.ctsql.CtsqlChar value) throws CtsqlException {
		if (value == null) {
			setCtsqlNull(index);
		} else {
			switch (getBasicColumnType(index)) {
				case CtsqlType.CHAR_TYPE:
					columns[index] = value;
					break;
				case CtsqlType.INTEGER_TYPE:
					columns[index] = new CtsqlInteger(value.getAsInteger());
					break;
				case CtsqlType.DECIMAL_TYPE:
					columns[index] = new CtsqlDecimal(value.getAsDouble());
					break;
				case CtsqlType.SMALLINT_TYPE:
					columns[index] = new CtsqlSmallint(value.getAsShort());
					break;
				case CtsqlType.DATE_TYPE:
					columns[index] = new CtsqlDate(value.getAsDateCalendar());
					break;
				case CtsqlType.TIME_TYPE:
					columns[index] = new CtsqlTime(value.getAsTimeCalendar());
					break;
				case CtsqlType.DATETIME_TYPE:
					columns[index] = new CtsqlDateTime(value.getAsDateTimeCalendar());
					break;
				case CtsqlType.BINARY_TYPE:
					columns[index] = new CtsqlBinary(value.getAsByte());
					break;
				default:
					CtsqlExceptionManager.getManager().throwException(CtsqlException.ILLEGAL_CONVERSION_TYPE, null);
			}
			updated[index] = true;
		}
	}

	/**
	 * @param  index
	 * @param  value
	 * @exception  CtsqlException
	 */
	public void setCtsqlBinary(int index, com.transtools.ctsql.CtsqlBinary value) throws CtsqlException {
		if (value == null) {
			setCtsqlNull(index);
		} else {
			switch (getBasicColumnType(index)) {
				case CtsqlType.CHAR_TYPE:
					byte[] bytes = value.getAsBytes();
					int len = bytes.length;
					char[] byteBuf = new char[len];
					for(int i = 0; i < len; i++){
						byteBuf[i] = (char) bytes[i];
						// Para evitar la propagación del negativo.
						byteBuf[i] &= 0x00ff;
					}
					columns[index] = new CtsqlChar(new String(byteBuf));
					break;
				case CtsqlType.BINARY_TYPE:
					columns[index] = value;
					break;
				default:
					CtsqlExceptionManager.getManager().throwException(CtsqlException.ILLEGAL_CONVERSION_TYPE, null);
			}
			updated[index] = true;
		}
	}

	/**
	 *  <OJO - Revisar> Sets the CtsqlDecimal attribute of the <code>CtsqlStmt</code>
	 *  object.
	 *
	 * @param  index               <OJO - Revisar> the new ctsqlDecimal value
	 * @param  value               <OJO - Revisar> the new ctsqlDecimal value
	 * @exception  CtsqlException  <OJO - Put here the exception description>
	 */
	public void setCtsqlDecimal(int index, com.transtools.ctsql.CtsqlDecimal value) throws CtsqlException {
		if (value == null) {
			setCtsqlNull(index);
		} else {
			switch (getBasicColumnType(index)) {
				case CtsqlType.DECIMAL_TYPE:
					columns[index] = value;
					break;
				case CtsqlType.INTEGER_TYPE:
					columns[index] = new CtsqlInteger(value.getAsInteger());
					break;
				case CtsqlType.SMALLINT_TYPE:
					columns[index] = new CtsqlSmallint(value.getAsShort());
					break;
				case CtsqlType.CHAR_TYPE:
					columns[index] = new CtsqlChar(value.getAsString());
					break;
				default:
					CtsqlExceptionManager.getManager().throwException(CtsqlException.ILLEGAL_CONVERSION_TYPE, null);
			}
			updated[index] = true;
		}
	}

	/**
	 *  <OJO - Revisar> Sets the CtsqlDate attribute of the <code>CtsqlStmt</code>
	 *  object.
	 *
	 * @param  index               <OJO - Revisar> the new ctsqlDate value
	 * @param  value               <OJO - Revisar> the new ctsqlDate value
	 * @exception  CtsqlException  <OJO - Put here the exception description>
	 */
	public void setCtsqlDate(int index, com.transtools.ctsql.CtsqlDate value) throws CtsqlException {
		if (value == null) {
			setCtsqlNull(index);
		} else {
			switch (getBasicColumnType(index)) {
				case CtsqlType.DATE_TYPE:
					columns[index] = value;
					break;
				case CtsqlType.CHAR_TYPE:
					columns[index] = new CtsqlChar(value.getAsString());
					break;
				default:
					CtsqlExceptionManager.getManager().throwException(CtsqlException.ILLEGAL_CONVERSION_TYPE, null);
			}
			updated[index] = true;
		}
	}

	/*
	 *  NEW Paco 12-03-2003
	 */
	/**
	 *  <OJO - Revisar> Sets the CtsqlTime attribute of the <code>CtsqlStmt</code>
	 *  object.
	 *
	 * @param  index               <OJO - Revisar> the new ctsqlTime value
	 * @param  value               <OJO - Revisar> the new ctsqlTime value
	 * @exception  CtsqlException  <OJO - Put here the exception description>
	 */
	public void setCtsqlTime(int index, com.transtools.ctsql.CtsqlTime value) throws CtsqlException {
		if (value == null) {
			setCtsqlNull(index);
		} else {
			switch (getBasicColumnType(index)) {
				case CtsqlType.TIME_TYPE:
					columns[index] = value;
					break;
				case CtsqlType.CHAR_TYPE:
					columns[index] = new CtsqlChar(value.getAsString());
					break;
				default:
					CtsqlExceptionManager.getManager().throwException(CtsqlException.ILLEGAL_CONVERSION_TYPE, null);
			}
			updated[index] = true;
		}
	}

	/*
	 *  END-NEW Paco 12-03-2003
	 */
	/**
	 *  <OJO - Revisar> Sets the CtsqlDateTime attribute of the <code>CtsqlStmt</code>
	 *  object.
	 *
	 * @param  index               <OJO - Revisar> the new ctsqlDateTime value
	 * @param  value               <OJO - Revisar> the new ctsqlDateTime value
	 * @exception  CtsqlException  <OJO - Put here the exception description>
	 */
	public void setCtsqlDateTime(int index, com.transtools.ctsql.CtsqlDateTime value) throws CtsqlException {
		if (value == null) {
			setCtsqlNull(index);
		} else {
			switch (getBasicColumnType(index)) {
				case CtsqlType.DATE_TYPE:
					columns[index] = new CtsqlDate(value.getAsCalendar());
					break;
				case CtsqlType.TIME_TYPE:
					columns[index] = new CtsqlTime(value.getAsCalendar());
					break;
				case CtsqlType.DATETIME_TYPE:
					columns[index] = value;
					break;
				case CtsqlType.CHAR_TYPE:
					columns[index] = new CtsqlChar(value.getAsString());
					break;
				default:
					CtsqlExceptionManager.getManager().throwException(CtsqlException.ILLEGAL_CONVERSION_TYPE, null);
			}
			updated[index] = true;
		}
	}


	/**
	 *  Gets the Row attribute of the CtsqlStmt object
	 *
	 * @return    The Row value
	 */

	public int getRow() {
		return recidx;
	}


	/**
	 *  Gets the Stat attribute of the CtsqlStmt object
	 *
	 * @return    The Stat value
	 */
	public short getStat() {
		return stat;
	}


	/**
	 *  Gets the IsLast attribute of the CtsqlStmt object
	 *
	 * @return    The IsLast value
	 */
	public boolean getIsLast() {
		return isLast;
	}

	/**
	 *  Gets the IsLast attribute of the CtsqlStmt object
	 *
	 * @return    The IsLast value
	 */
	public boolean getIsAfterLast() {
		return isAfterLast;
	}


	/**
	 * @return      The HostsNumber value
	 * @jdbc.new    Lo he añadido por ser necesario conocer esta información en las
	 *      capas superiores.
	 */
	public int getHostsNumber() {
		return nHost;
	}


	/**
	 *  Gets the ColumnCount attribute of the CtsqlStmt object
	 *
	 * @return    The ColumnCount value
	 */
	public int getColumnCount() {
		return ncols;
	}


	/**
	 *  Gets the ColumnType attribute of the CtsqlStmt object. This type number
	 *  corresponds to the ctsql primitive number, and not to the jdbc type number.
	 *
	 * @param  numCol  Description of Parameter
	 * @return         The ColumnType value
	 */
	public int getColumnType(int numCol) {
		return getTypeIcol(numCol);
	}

	/**
	 *  Gets the ColumnLength attribute of the CtsqlStmt object
	 *
	 * @param  numCol  Description of Parameter
	 * @return         The ColumnLength value
	 */
	public int getColumnLength(int numCol) {
		return getLenIcol(numCol);
	}


	/**
	 * @param  index  Description of Parameter
	 * @return        The ColumnName value
	 * @jdbc.new      Se ha añadido este método para recuperar el nombre de la
	 *      columna.
	 */

	public String getColumnName(int index) {
		int off = -1;
		if ((icollis != null) && (index >= 0) && (index < ncols)) {
			off = icollis[index][NAMEOFFSET_ICOL];
		}
		return readColumnInfo(off);
	}
	
	


	public String getTableName(int index) {
		int off = -1;
		if ((icollis != null) && (index >= 0) && (index < ncols)) {
			off = icollis[index][NAMEOFFSET_ICOL];
		}
		return readColumnInfo(off);
	}


	/**
	 *  Gets the ColumnLabel attribute of the CtsqlStmt object
	 *
	 * @param  index  Description of Parameter
	 * @return        The ColumnLabel value
	 */
	public String getColumnLabel(int index) {
		int off = -1;
		if ((icollis != null) && (index >= 0) && (index < ncols)) {
			off = icollis[index][TITLEOFFSET_ICOL];
		}
		return readColumnInfo(off);
	}


	/**
	 *  Gets the ColumnTypeName attribute of the CtsqlStmt object
	 *
	 * @param  ind  Description of Parameter
	 * @return      The name of the columns type
	 */
	public String getColumnTypeName(int ind) {
		switch (getColumnType(ind)) {
						case CtsqlType.SMALLINT_TYPE:
							return "SMALLINT";
						case CtsqlType.INTEGER_TYPE:
							return "INTEGER";
						case CtsqlType.DECIMAL_TYPE:
							return "DECIMAL";
						case CtsqlType.DATE_TYPE:
							return "DATE";
						case CtsqlType.TIME_TYPE:
							return "TIME";
						case CtsqlType.CHAR_TYPE:
							return "CHAR";
						case CtsqlType.DATETIME_TYPE:
							return "DATETIME";
						case CtsqlType.SERIAL_TYPE:
							return "SERIAL";
						case CtsqlType.MONEY_TYPE:
							return "MONEY";
						case CtsqlType.NULL_TYPE:
							return "NULL";
						case CtsqlType.BINARY_TYPE:
							return "BINARY";
						default:
							return "UNKNOWN_TYPE";
		}
	}


	/**
	 *  Gets the ColumnDisplaySize attribute of the CtsqlStmt object
	 *
	 * @param  ind  Description of Parameter
	 * @return      The ColumnDisplaySize value
	 */
	public int getColumnDisplaySize(int ind) {
		switch (getColumnType(ind)) {
						case CtsqlType.SMALLINT_TYPE:
							return SMALLINT_DISPLAY_SIZE;
						case CtsqlType.INTEGER_TYPE:
						case CtsqlType.DECIMAL_TYPE:
						case CtsqlType.SERIAL_TYPE:
						case CtsqlType.MONEY_TYPE:
//			case CtsqlType.BINARY_TYPE:
							return INTEGER_DISPLAY_SIZE;
						case CtsqlType.DATE_TYPE:
						case CtsqlType.TIME_TYPE:
							return DATE_DISPLAY_SIZE;
						case CtsqlType.CHAR_TYPE:
							return getColumnLength(ind);
						case CtsqlType.DATETIME_TYPE:
							return DATETIME_DISPLAY_SIZE;
						case CtsqlType.BINARY_TYPE:
							return BINARY_DISPLAY_SIZE;
						case CtsqlType.NULL_TYPE:
							return NULL_DISPLAY_SIZE;
						default:
							return -1;
		}
	}


	/**
	 *  Gets the Precision attribute of the CtsqlStmt object
	 *
	 * @param  index  Description of Parameter
	 * @return        The Precision value
	 */
	public int getPrecision(int index) {
		switch (getColumnType(index)) {
						case CtsqlType.DECIMAL_TYPE:
						case CtsqlType.MONEY_TYPE:
							return CtsqlDecimal.lengthFromPack(getLenIcol(index));
						default:
							return getColumnLength(index);
		}
	}


	/**
	 *  Gets the Scale attribute of the CtsqlStmt object
	 *
	 * @param  index  Description of Parameter
	 * @return        The Scale value
	 */
	public int getScale(int index) {
		switch (getColumnType(index)) {
						case CtsqlType.DECIMAL_TYPE:
						case CtsqlType.MONEY_TYPE:
							return CtsqlDecimal.precisionFromPack(getLenIcol(index));
						default:
							return 0;
		}
	}


	/**
	 * @return      The UpdateCount value
	 * @jdbc.new    Se ha añadido para saber el número de filas actualizadas tras
	 *      la ejecución de cualquier sentencia que no sea un select.
	 */
	public int getUpdateCount() {
		if (sqlca.getSqlErrd(UPDATE_COUNT) == 0) {
			return -1;
		}
		return sqlca.getSqlErrd(UPDATE_COUNT);
	}


	/**
	 *  Gets the Name attribute of the CtsqlStmt object
	 *
	 * @return    The Name value
	 */
	public String getName() {
		return name;
	}


	/**
	 *  Gets the SelectStatement attribute of the CtsqlStmt object
	 *
	 * @return                     The SelectStatement value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public boolean isSelectStatement() throws CtsqlException {
		if (!isPrepared()) {
			CtsqlExceptionManager.getManager().throwException(CtsqlException.STATEMENT_NOT_PREPARED, null);
		}

		return (curop == CtsqlConstants.FNSELECT);
	}


	/**
	 *  Gets the LastError attribute of the CtsqlStmt object
	 *
	 * @return    The LastError value
	 */
	public int getLastError() {
		return sqlca.getSqlCode();
	}

	/**
	 *  Gets the LastErrorString attribute of the CtsqlStmt object
	 *
	 * @return    The LastErrorString value
	 */
	public String getLastErrorString() {
		return sqlca.getSqlErrorString();
	}

	/**
	 * @return    <OJO - Revisar> The warnings value.
	 */
	public SQLWarning getWarnings() {
		SQLWarning warning = null;
		if (sqlca.getNullWarn()) {
			warning = createAndChainWarning(warning, "Null value has been returned.", "01000");
		}
//		if(sqlca.getNoWeredWarn()){
//			warning = createAndChainWarning(warning, "No were clause in update or delete statement.", "01504");
//		}
		return warning;
	}

	/**
	 *  Gets the Open attribute of the CtsqlStmt object
	 *
	 * @return    The Open value
	 */
	public boolean isOpen() {
		return bOpen;
	}


	/**
	 *  Gets the CtsqlChar attribute of the CtsqlStmt object
	 *
	 * @param  index               column index, first column is 0
	 * @return                     The CtsqlChar value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public com.transtools.ctsql.CtsqlChar getCtsqlChar(int index) throws CtsqlException {
		if (getBasicColumnType(index) == CtsqlType.CHAR_TYPE) {
			return (CtsqlChar) columns[index];
		} else {
			if (columns[index].isNull()) {
				return new CtsqlChar();
			} else {
				return new CtsqlChar(columns[index].getAsString());
			}
		}
	}


	/**
	 *  Gets the CtsqlSmallint attribute of the CtsqlStmt object
	 *
	 * @param  index               column index, first column is 0
	 * @return                     The CtsqlSmallint value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public com.transtools.ctsql.CtsqlSmallint getCtsqlSmallint(int index) throws CtsqlException {
		switch (getBasicColumnType(index)) {
						case CtsqlType.SMALLINT_TYPE:
							return (CtsqlSmallint) columns[index];
						case CtsqlType.INTEGER_TYPE:
						{
							CtsqlInteger v = (CtsqlInteger) columns[index];
							if (v.isNull()) {
								return new CtsqlSmallint();
							} else {
								return new CtsqlSmallint(v.getAsShort());
							}
						}
						case CtsqlType.DECIMAL_TYPE:
						{
							CtsqlDecimal v = (CtsqlDecimal) columns[index];
							if (v.isNull()) {
								return new CtsqlSmallint();
							} else {
								return new CtsqlSmallint(v.getAsShort());
							}
						}
						case CtsqlType.CHAR_TYPE:
						{
							CtsqlChar v = (CtsqlChar) columns[index];
							if (v.isNull()) {
								return new CtsqlSmallint();
							} else {
								try {
									return new CtsqlSmallint(v.getAsShort());
								} catch (NumberFormatException e) {
									return new CtsqlSmallint();
								}

							}
						}
						default:
							CtsqlExceptionManager.getManager().throwException(CtsqlException.ILLEGAL_CONVERSION_TYPE, null);
							return null;
						// This statement will be never reached, but the compiler needs it.
		}
	}


	/**
	 *  Gets the CtsqlDate attribute of the CtsqlStmt object
	 *
	 * @param  index               column index, first column is 0
	 * @return                     The CtsqlDate value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public com.transtools.ctsql.CtsqlDate getCtsqlDate(int index) throws CtsqlException {
		switch (getBasicColumnType(index)) {
						case CtsqlType.DATE_TYPE:
							return (CtsqlDate) columns[index];
						case CtsqlType.DATETIME_TYPE:
						{
							CtsqlDateTime v = (CtsqlDateTime) columns[index];
							return v.getAsDate();
						}
						case CtsqlType.CHAR_TYPE:
						{
							CtsqlChar v = (CtsqlChar) columns[index];
							if (v.isNull()) {
								return new CtsqlDate();
							} else {
								try {
									return new CtsqlDate(v.getAsDateCalendar());
								} catch (CtsqlException e) {
									return new CtsqlDate();
								}
							}
						}
						default:
							CtsqlExceptionManager.getManager().throwException(CtsqlException.ILLEGAL_CONVERSION_TYPE, null);
							return null;
						// This statement will be never reached, but the compiler needs it.
		}

	}


// New Eva 18-04-2001

	/**
	 *  Gets the CtsqlDateTime attribute of the CtsqlStmt object
	 *
	 * @param  index               column index, first column is 0
	 * @return                     The CtsqlDateTime value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public com.transtools.ctsql.CtsqlDateTime getCtsqlDateTime(int index) throws CtsqlException {
		switch (getBasicColumnType(index)) {
						case CtsqlType.DATETIME_TYPE:
							return (CtsqlDateTime) columns[index];
						case CtsqlType.DATE_TYPE:
						{
							CtsqlDate v = (CtsqlDate) columns[index];
							if (v.isNull()) {
								return new CtsqlDateTime();
							} else {
								return new CtsqlDateTime(v);
							}
						}
						case CtsqlType.TIME_TYPE:
						{
							CtsqlTime t = (CtsqlTime) columns[index];
							if (t.isNull()) {
								return new CtsqlDateTime();
							} else {
								return new CtsqlDateTime(new CtsqlDate(), t);
							}
						}
						case CtsqlType.CHAR_TYPE:
						{
							CtsqlChar v = (CtsqlChar) columns[index];
							if (v.isNull()) {
								return new CtsqlDateTime();
							} else {
								try {
									return new CtsqlDateTime(v.getAsDateTimeCalendar());
								} catch (CtsqlException e) {
									return new CtsqlDateTime();
								}
							}
						}
						default:
							CtsqlExceptionManager.getManager().throwException(CtsqlException.ILLEGAL_CONVERSION_TYPE, null);
							return null;
						// This statement will be never reached, but the compiler needs it.
		}

	}


// END New Eva 18-04-2001

	/**
	 *  Gets the CtsqlBinary attribute of the CtsqlStmt object
	 *
	 * @param  index               column index, first column is 0
	 * @return                     The CtsqlBinary value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public com.transtools.ctsql.CtsqlBinary getCtsqlBinary(int index) throws CtsqlException {
		switch (getBasicColumnType(index)) {
						case CtsqlType.BINARY_TYPE:
							return (CtsqlBinary) columns[index];
						case CtsqlType.CHAR_TYPE:
						{
							CtsqlChar v = (CtsqlChar) columns[index];
							if (v.isNull()) {
								return new CtsqlBinary();
							} else {
								return new CtsqlBinary(v.getAsByte());
							}
						}
						default:
							CtsqlExceptionManager.getManager().throwException(CtsqlException.ILLEGAL_CONVERSION_TYPE, null);
							return null;
						// This statement will be never reached, but the compiler needs it.
		}

	}


	/**
	 *  Gets the CtsqlTime attribute of the CtsqlStmt object
	 *
	 * @param  index               column index, first column is 0
	 * @return                     The CtsqlTime value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public com.transtools.ctsql.CtsqlTime getCtsqlTime(int index) throws CtsqlException {
		switch (getBasicColumnType(index)) {
						case CtsqlType.TIME_TYPE:
							return (CtsqlTime) columns[index];
						case CtsqlType.DATETIME_TYPE:
						{
							CtsqlDateTime v = (CtsqlDateTime) columns[index];
							return v.getAsTime();
						}
						case CtsqlType.CHAR_TYPE:
						{
							CtsqlChar v = (CtsqlChar) columns[index];
							if (v.isNull()) {
								return new CtsqlTime();
							} else {
								return new CtsqlTime(v.getAsTimeCalendar());
							}
						}
						default:
							CtsqlExceptionManager.getManager().throwException(CtsqlException.ILLEGAL_CONVERSION_TYPE, null);
							return null;
						// This statement will be never reached, but the compiler needs it.
		}
	}


	/**
	 *  Gets the CtsqlInteger attribute of the CtsqlStmt object
	 *
	 * @param  index               column index, first column is 0
	 * @return                     The CtsqlInteger value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public com.transtools.ctsql.CtsqlInteger getCtsqlInteger(int index) throws CtsqlException {
		switch (getBasicColumnType(index)) {
						case CtsqlType.INTEGER_TYPE:
							return (CtsqlInteger) columns[index];
						case CtsqlType.SMALLINT_TYPE:
						{
							CtsqlSmallint v = (CtsqlSmallint) columns[index];
							if (v.isNull()) {
								return new CtsqlInteger();
							} else {
								return new CtsqlInteger(v.getAsInteger());
							}
						}
						case CtsqlType.DECIMAL_TYPE:
						{
							CtsqlDecimal v = (CtsqlDecimal) columns[index];
							if (v.isNull()) {
								return new CtsqlInteger();
							} else {
								return new CtsqlInteger(v.getAsInteger());
							}
						}
						case CtsqlType.CHAR_TYPE:
						{
							CtsqlChar v = (CtsqlChar) columns[index];
							if (v.isNull()) {
								return new CtsqlInteger();
							} else {
								try {
									return new CtsqlInteger(v.getAsShort());
								} catch (NumberFormatException e) {
									return new CtsqlInteger();
								}
							}
						}
						default:
							CtsqlExceptionManager.getManager().throwException(CtsqlException.ILLEGAL_CONVERSION_TYPE, null);
							return null;
						// This statement will be never reached, but the compiler needs it.
		}
	}


	/**
	 *  Gets the CtsqlDecimal attribute of the CtsqlStmt object
	 *
	 * @param  index               column index, first column is 0
	 * @return                     The CtsqlDecimal value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public com.transtools.ctsql.CtsqlDecimal getCtsqlDecimal(int index) throws CtsqlException {
		switch (getBasicColumnType(index)) {
						case CtsqlType.DECIMAL_TYPE:
							return (CtsqlDecimal) columns[index];
						case CtsqlType.INTEGER_TYPE:
						{
							CtsqlInteger v = (CtsqlInteger) columns[index];
							if (v.isNull()) {
								return new CtsqlDecimal();
							} else {
								return new CtsqlDecimal(v.getAsDouble());
							}
						}
						case CtsqlType.SMALLINT_TYPE:
						{
							CtsqlSmallint v = (CtsqlSmallint) columns[index];
							if (v.isNull()) {
								return new CtsqlDecimal();
							} else {
								return new CtsqlDecimal(v.getAsDouble());
							}
						}
						case CtsqlType.CHAR_TYPE:
						{
							CtsqlChar v = (CtsqlChar) columns[index];
							if (v.isNull()) {
								return new CtsqlDecimal();
							} else {
								try {
									return new CtsqlDecimal(v.getAsDouble());
								} catch (NumberFormatException e) {
									return new CtsqlDecimal();
								}
							}
						}
						default:
							CtsqlExceptionManager.getManager().throwException(CtsqlException.ILLEGAL_CONVERSION_TYPE, null);
							return null;
						// This statement will be never reached, but the compiler needs it.
		}
	}

	/**
	 *  <OJO - Revisar> Gets the Serial attribute of the <code>CtsqlStmt</code>
	 *  object.
	 *
	 * @return                     <OJO - Revisar> The serial value.
	 * @exception  CtsqlException  <OJO - Put here the exception description>
	 */
	public int getSerial() throws CtsqlException {
		if (serialValueObtained) {
			return sqlca.getSqlErrd(1);
		} else {
			CtsqlExceptionManager.getManager().throwException(
				new RuntimeException("Bad access to serial value.")
				);
			return -1;
		}
	}

	/**
	 * This method must be called only after an insert operation, and returns the rowid 
	 * value of the inserted row.
	 * @return The rowid value after insert.
	 */
	public int getRowIdAfterInsert(){
		return sqlca.getSqlErrd(ROWID_AFTER_INSERT);
	}

	/**
	 *  Prepares a SQL sentence for later execution.
	 *
	 * @param  statement           SQL sentence
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public void prepare(String statement) throws CtsqlException {
		checkServer();
		synchronized (server) {
			if (isPrepared()) {
				release();
			}
			localPrepare(statement);
			if(!isLocalStatement()){
				sendParse(statement);
				sendDescribe();
				waitResult((short) 0, new Integer(0), false, false);
			}
			if (getLastError() == 0) {
				bPrepared = true;
				stat |= CtsqlConstants.C_PREPD;
			}
			checkLastError();
		}
	}


	private boolean isLocalStatement() {
		return this.localStatement;
	}

	private void localStatementDescribeInfo(String type) {
		int titlen;
		int i;
		int j;
		
		byte[] typeBytes = type.getBytes();

		curop =  CtsqlConstants.FNSELECT;
		id = -1;
		rowSize = 4; // Integer
		ncols = 1;
		
		titlen = typeBytes.length;

		noby = 0;
		// número de columnas en order by.
		buf = realoja(buf, sqlbsize(rowSize));
		offTuple = 0;
		titl = realoja(titl, titlen);
		// buffer que contendrá los nombres y los títulos de las columnas.
		System.arraycopy(typeBytes, 0, titl, 0, typeBytes.length);

		icollis = new short[ncols][7];
		for (i = 0; i < ncols; i++) {
			icollis[i][NAMEOFFSET_ICOL] = 0;
			icollis[i][TITLEOFFSET_ICOL] = 0;
			icollis[i][DATAOFFSET_ICOL] = 0; // del buffer de datos
			icollis[i][LENGTH_ICOL] = 4;
			icollis[i][TYPE_ICOL] = CtsqlType.INTEGER_TYPE;
		}

		if (noby > 0) {
			oby = new short[noby][3];
			for (i = 0; i < noby; i++) {
				for (j = 0; j < 3; j++) {
					oby[i][j] = 0;
				}
			}
		}
		// Array de variables intermedias JDBC 2.
		columns = new CtsqlType[ncols];
		updated = new boolean[ncols];
	}
	

	private void localPrepare(String statement) {
		statement = statement.trim().toLowerCase();
		if(statement.equals("select last_insert_id()")){
			this.localStatement = true;
			this.localResultType = LAST_INSERT_ID;
		}else if(statement.equals("select last_insert_rowid()")){
			this.localStatement = true;
			this.localResultType = LAST_INSERT_ROWID;
		}else{
			this.localStatement = false;
			this.localResultType = null;			
		}
	
		if(this.localStatement){
			resetSqlca();
			localStatementDescribeInfo(this.localResultType);			
		}		
	}


	private void localOpen(){
		if(isLocalStatement()){
			bOpen = true;
			recidx = -1;
		}
	}
	
	private void localClose(){
		if(isLocalStatement()){
			bOpen = false;
		}
	}

	private void localRelease(){
		init();
	}
	
	private void localFetch() throws CtsqlException{
		
		if (!isOpen()) {
			CtsqlExceptionManager.getManager().throwException(CtsqlException.STATEMENT_NOT_OPEN, null);
		}
		
		
		if (recidx == -1) {
			recidx = 0;
			
			if(localResultType == LAST_INSERT_ID){
				CtsqlInteger.storeInt(server.getLastSerial(), buf);		
			}else if(localResultType == LAST_INSERT_ROWID){
				CtsqlInteger.storeInt(server.getLastRowid(), buf);					
			}
			isAfterLast = false;
			loadColumns();
			offTuple += rowSize;
		}else{
			sqlca.setSqlCode(CtsqlConstants.SQLNOTFOUND);
			isAfterLast = true;
			stat |= CtsqlConstants.C_EOF;
		}
	}
	/**
	 *  Finds the column index by column name.
	 *
	 * @param  name  column name
	 * @return       column index
	 */
	public int findColumn(String name) {
		for (int i = 0; i < getColumnCount(); i++) {
			if (name.equalsIgnoreCase(getColumnName(i))) {
				return i + 1;
			}
		}
		return -1;
	}


	/**
	 *  Executes the previous stored SQL sentence.
	 *
	 * @param  hosts               host variables list
	 * @exception  CtsqlException  if a database access error occurs
	 * @jdbc.pending               Hay que repasar todo el lanzamiento de
	 *      excepciones.
	 */
	public void execute(CtsqlType hosts[]) throws CtsqlException {
		checkServer();
		synchronized (server) {
			if (!isPrepared()) {
				CtsqlExceptionManager.getManager().throwException(CtsqlException.STATEMENT_NOT_PREPARED, null);
			}
			setCurrent();
			sendHosts(hosts);
			sendExecute();
			waitResult((short) 0, new Integer(0), false, false);
			checkLastError();
		}
	}


	/**
	 * @exception  CtsqlException  if a database access error occurs
	 * @jdbc.pending               Hay que repasar todo el lanzamiento de
	 *      excepciones.
	 */
	public void close() throws CtsqlException {
		if(isLocalStatement()){
			localClose();
		}else{		
			if (isOpen()) {
				checkServer();
				synchronized (server) {
//                System.out.println("ENTRO en close");
					setCurrent();
					sendCommand(CtsqlConstants.SQ_CLOSE);
					waitResult((short) 0, new Integer(0), false, false);
					if (getLastError() == 0) {
						bOpen = false;
					}
					checkLastError();
	//                System.out.println("SALGO en close");
				}
			}
		}
	}


	/**
	 * @param  hosts               Description of Parameter
	 * @exception  CtsqlException  if a database access error occurs
	 * @jdbc.pending               Hay que repasar todo el lanzamiento de
	 *      excepciones.
	 */
	public void open(CtsqlType hosts[]) throws CtsqlException {
		if (!isPrepared()) {
			CtsqlExceptionManager.getManager().throwException(CtsqlException.STATEMENT_NOT_PREPARED, null);
		}
		if(isLocalStatement()){
			localOpen();
		}else{
			synchronized (server) {
	//            System.out.println("ENTRO en OPEN");
				if (!isOpen()) {
					setCurrent();
					if (name != null) {
						sendCurName(name);
					}
					sendHosts(hosts);
					sendOpen();
					waitResult((short) 0, new Integer(0), false, false);
					if (getLastError() == 0) {
						bOpen = true;
						recidx = -1;
						stat |= CtsqlConstants.C_OPEN;
						stat &= ~(CtsqlConstants.C_EOF | CtsqlConstants.C_BOF | CtsqlConstants.C_PREV);
						left = 0;
					}
					checkLastError();
				}
	//            System.out.println("Salgo de OPEN");
			}
		}
	}

	/**
	 * @exception  CtsqlException  if a database access error occurs
	 * @jdbc.pending               Hay que repasar todo el lanzamiento de
	 *      excepciones.
	 */
	public void fetch() throws CtsqlException {
		if(isLocalStatement()){
			localFetch();
			return;
		}
		
		checkServer();
		synchronized (server) {
			if (!isOpen()) {
				CtsqlExceptionManager.getManager().throwException(CtsqlException.STATEMENT_NOT_OPEN, null);
			}

			if (recidx == -1) {
				recidx = 0;
			}

			if (recidx == 0 && (stat & CtsqlConstants.C_EOF) > 0) {
				sqlca.setSqlCode(CtsqlConstants.SQLNOTFOUND);
				isAfterLast = true;
				return;
			}

			// Is not there more tuples?
			if (recidx == 0) {
				sendId(id);
				sendNFetch();
				waitResult((short) 0, new Integer(1), true, false);
				checkLastError();
				offTuple = 0;
			} else {
				offTuple += rowSize;
			}

			if (getLastError() != 0) {
				CtsqlExceptionManager.getManager().throwException(getLastError());
			}

			if (recidx != 0) {
				recidx--;
				// Para update de JDBC 2
				loadColumns();
				isAfterLast = false;
			} else {
				sqlca.setSqlCode(CtsqlConstants.SQLNOTFOUND);
				isAfterLast = true;
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  mode                Description of Parameter
	 * @return                     Description of the Returned Value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public boolean fetchMode(short mode) throws CtsqlException {

		checkServer();
		synchronized (server) {
			if (!isOpen()) {
				CtsqlExceptionManager.getManager().throwException(CtsqlException.STATEMENT_NOT_OPEN, null);
			}

			if ((mode == CtsqlCursor.ISNEXT) || (mode == CtsqlCursor.ISPREV)) {
				isLast = false;
				return scrollFetch((mode == CtsqlCursor.ISNEXT) ? (short) 1 : (short) -1);
			} else {
				longfetch(mode);
			}
			if (getLastError() != 0) {
				CtsqlExceptionManager.getManager().throwException(getLastError());
			}
			if (left == 0) {
				recidx = 0;
				sqlca.setSqlCode(CtsqlConstants.SQLNOTFOUND);
				if (mode == CtsqlCursor.ISFIRST) {
					stat &= ~CtsqlConstants.C_EOF;
					stat |= CtsqlConstants.C_BOF;
				} else {
					stat &= ~CtsqlConstants.C_BOF;
					stat |= CtsqlConstants.C_EOF;
				}
			} else {
				recidx = mode == CtsqlCursor.ISFIRST ? (short) 0 : (short) (left - 1);
			}
			if (getLastError() == 0) {
				offTuple = (short) (recidx * rowSize);
				stat &= ~(CtsqlConstants.C_BOF | CtsqlConstants.C_EOF);
				// Para update de JDBC 2
				loadColumns();
			}
			return true;
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  idx                 Description of Parameter
	 * @return                     Description of the Returned Value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public boolean scrollFetch(int idx) throws CtsqlException {

		int nrecs;
		short newrec = recidx;
		int oleft;

		flmov = true;

		checkServer();
		isLast = false;
		synchronized (server) {
			if (!isOpen()) {
				CtsqlExceptionManager.getManager().throwException(CtsqlException.STATEMENT_NOT_OPEN, null);
			}

			nrecs = (short) (sqlbsize(rowSize) / rowSize);

			if (recidx == -1) {
				newrec = 0;
			} else if ((stat & CtsqlConstants.C_BOF) > 0) {
				if (idx <= 0) {
					sqlca.setSqlCode(CtsqlConstants.SQLNOTFOUND);
					return false;
				} else {
					newrec = (short) (idx - 1);
				}
			} else if ((stat & CtsqlConstants.C_EOF) > 0) {
				if (idx >= 0) {
					sqlca.setSqlCode(CtsqlConstants.SQLNOTFOUND);
					return false;
				} else {
					newrec = (short) idx;
				}
			} else if ((left == 0) && ((stat & CtsqlConstants.C_EOF) > 0)) {
				stat |= CtsqlConstants.C_EOF;
				sqlca.setSqlCode(CtsqlConstants.SQLNOTFOUND);
				return false;
			} else {
				newrec += idx;
			}
			sqlca.setSqlCode(0);

			if (newrec < 0 || newrec >= left || recidx == -1) {
				oleft = left;
				flmov = scrollfillb((short) newrec);

				if (idx > 0) {
					newrec -= oleft;
				}

				if (getLastError() != 0) {
					CtsqlExceptionManager.getManager().throwException(getLastError());
				}

				if (((stat & CtsqlConstants.C_SQEOF) > 0) || (left == 0)) {
					if (idx < 0) {
						newrec = (short) ((-newrec) % nrecs);

						if ((left <= 0) || (newrec > left) || flBrkScroll) {
							stat |= CtsqlConstants.C_BOF;
							stat &= ~CtsqlConstants.C_EOF;
							recidx = 0;
							sqlca.setSqlCode(CtsqlConstants.SQLNOTFOUND);
						} else {
							recidx = newrec == 0 ? (short) 0 : (short) (left - newrec);
						}
					} else {
						if (left <= 0 || (newrec %= nrecs) >= left) {
							stat |= CtsqlConstants.C_EOF;
							stat &= ~CtsqlConstants.C_BOF;
							recidx = 0;
						} else {
							recidx = newrec;
						}
					}
				} else {
					recidx = idx > 0 ? (short) (newrec % left) :
						((newrec % left) != 0 ? (short) (left + newrec % left) : (short) 0);
				}
			} else {
				recidx = newrec;
			}
			if (getLastError() == 0) {
				offTuple = (short) (recidx * rowSize);
				stat &= ~(CtsqlConstants.C_BOF | CtsqlConstants.C_EOF);
				// Para update de JDBC 2
				loadColumns();
			}

			return flmov;
		}
	}


	/**
	 * @exception  CtsqlException  if a database access error occurs
	 * @jdbc.pending               Hay que repasar todo el lanzamiento de
	 *      excepciones.
	 */
	public void release() throws CtsqlException {
		if(isLocalStatement()){
			localRelease();
		}else if (isPrepared()) {
			checkServer();
			/*
			 *  New MA EVA 6-4-2001 Es conveniente cerrar los cursores
			 */
			if (isOpen()) {
				close();
			}
			/*
			 *  End New MA EVA 6-4-2001
			 */
			synchronized (server) {
//                System.out.println("Entro en release" + id);
				bPrepared = false;
				setCurrent();
				sendCommand(CtsqlConstants.SQ_RELEASE);
				waitResult((short) 0, new Integer(0), false, false);
				if (getLastError() == 0) {
					init();
				}
				checkLastError();
//                System.out.println("Salgo de release");
			}
		}
	}

	/**
	 * @param  tableName
	 * @exception  CtsqlException
	 */
	public void update(String tableName) throws CtsqlException {
		String phrase;
		int hostCount = 0;
		String separator = "";

		if (tableName == null) {
			CtsqlExceptionManager.getManager().throwException(CtsqlException.CANNOT_FIND_TABLE_NAME, null);
		}
		phrase = "update " + tableName + " set ";
		for (int i = 0; i < ncols; i++) {
			if (updated[i]) {
				hostCount++;
				phrase = phrase.concat(separator + getColumnName(i) + " = ? ");
				separator = ", ";
			}
		}
		phrase = phrase.concat("where current of " + name);
		if (hostCount > 0) {
			sendStatementWithUpdatedHosts(phrase, hostCount);
		}
	}

	/**
	 * @param  tableName
	 * @exception  CtsqlException
	 */
	public void insert(String tableName) throws CtsqlException {
		String phrase;
		int hostCount = 0;
		String separator;

		if (tableName == null) {
			CtsqlExceptionManager.getManager().throwException(CtsqlException.CANNOT_FIND_TABLE_NAME, null);
		}
		phrase = "insert into " + tableName;
		separator = " (";
		for (int i = 0; i < ncols; i++) {
			if (updated[i]) {
				hostCount++;
				phrase = phrase.concat(separator + getColumnName(i));
				separator = ", ";
			}
		}
		separator = ") values (";
		for (int i = 0; i < hostCount; i++) {
			phrase = phrase.concat(separator + "?");
			separator = ", ";
		}
		phrase = phrase.concat(")");
		if (hostCount > 0) {
			sendStatementWithUpdatedHosts(phrase, hostCount);
		}
	}

	/**
	 */
	public void moveToInsertRow() {
		// Array de variables intermedias JDBC 2.
		if (columns == null) {
			columns = new CtsqlType[ncols];
		}
		if (updated == null) {
			updated = new boolean[ncols];
		}
		for (int i = 0; i < ncols; i++) {
			columns[i] = null;
			updated[i] = false;
		}
		inInsertRow = true;
	}

	/**
	 * @param  tableName
	 * @exception  CtsqlException
	 */
	public void delete(String tableName) throws CtsqlException {
		String phrase;

		if (tableName == null) {
			CtsqlExceptionManager.getManager().throwException(CtsqlException.CANNOT_FIND_TABLE_NAME, null);
		}
		phrase = "delete from " + tableName + " where current of " + name;
		sendStatementWithUpdatedHosts(phrase, 0);
	}

	/**
	 *  <OJO - Put here the method description>
	 *
	 * @return    <OJO - Put here the return value description>
	 */
	public boolean hasSerial() {
		return serialValueObtained;
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  CtsqlException  if a database access error occurs
	 */
	protected void finalize() throws CtsqlException {
		if (id >= 0) {
//		System.out.println("          finalize() -> cursor: " + id);
			/*
			 *  el metodo release comprueba si ya está liberado
			 */
			release();
		}
	}

	private void setCtsqlNull(int index) throws CtsqlException {
		switch (getBasicColumnType(index)) {
						case CtsqlType.CHAR_TYPE:
							columns[index] = new CtsqlChar();
							break;
						case CtsqlType.INTEGER_TYPE:
							columns[index] = new CtsqlInteger();
							break;
						case CtsqlType.DECIMAL_TYPE:
							columns[index] = new CtsqlDecimal();
							break;
						case CtsqlType.SMALLINT_TYPE:
							columns[index] = new CtsqlSmallint();
							break;
						case CtsqlType.DATE_TYPE:
							columns[index] = new CtsqlDate();
							break;
						case CtsqlType.TIME_TYPE:
							columns[index] = new CtsqlTime();
							break;
						case CtsqlType.DATETIME_TYPE:
							columns[index] = new CtsqlDateTime();
							break;
						case CtsqlType.BINARY_TYPE:
							columns[index] = new CtsqlBinary();
							break;
						default:
							CtsqlExceptionManager.getManager().throwException(CtsqlException.ILLEGAL_CONVERSION_TYPE, null);
		}
		updated[index] = true;
	}


	/**
	 *  Sets the Current attribute of the CtsqlStmt object
	 *
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void setCurrent() throws CtsqlException {
		checkServer();
		sendId(id);
	}

	private int getBasicColumnType(int numCol) {
		int i = getColumnType(numCol);
		if (i == CtsqlType.SERIAL_TYPE) {
			i = CtsqlType.INTEGER_TYPE;
		}
		// SERIAL(6) is managed as SMALLINT(2).
		return i == CtsqlType.MONEY_TYPE ? CtsqlType.DECIMAL_TYPE : i;
		// MONEY(8) is managed as DECIMAL(5).
	}


	/**
	 * @param  idx       Description of Parameter
	 * @return           The TypeIcol value
	 * @jdbc.modified    Se ha añadido el chequear que el índice sea mayor o igual
	 *      que cero.
	 */

	private int getTypeIcol(int idx) {
		if ((idx >= 0) && (idx < ncols)) {
			return icollis[idx][TYPE_ICOL];
		} else {
			return -1;
		}
	}


	/**
	 *  Gets the LenIcol attribute of the CtsqlStmt object
	 *
	 * @param  idx  Description of Parameter
	 * @return      The LenIcol value
	 */
	private int getLenIcol(int idx) {
		// len == lenPrec
		if (idx < ncols) {
			return icollis[idx][LENGTH_ICOL];
		} else {
			return -1;
		}
	}


	/**
	 *  Gets the BufOffsetIcol attribute of the CtsqlStmt object
	 *
	 * @param  idx  Description of Parameter
	 * @return      The BufOffsetIcol value
	 */
	private int getBufOffsetIcol(int idx) {
		return icollis[idx][DATAOFFSET_ICOL];
	}


	/**
	 *  Gets the Prepared attribute of the CtsqlStmt object
	 *
	 * @return    The Prepared value
	 */
	private boolean isPrepared() {
		return bPrepared;
	}

	private SQLWarning createAndChainWarning(SQLWarning previousWarning, String reason, String sqlState) {
		SQLWarning warning = new SQLWarning(reason, sqlState);
		if (previousWarning != null) {
			previousWarning.setNextWarning(warning);
		}
		return warning;
	}

	private void sendStatementWithUpdatedHosts(String phrase, int hostCount) throws CtsqlException {
		CtsqlStmt stmt = new CtsqlStmt(server);
		CtsqlType[] hosts = null;

		stmt.prepare(phrase);

		if (hostCount > 0) {
			hosts = new CtsqlType[hostCount];
			hostCount = 0;
			for (int i = 0; i < ncols; i++) {
				if (updated[i]) {
					hosts[hostCount] = columns[i];
					hostCount++;
				}
			}
		}

		try {
			stmt.execute(hosts);
		} catch (CtsqlException e) {
			stmt.release();
			throw e;
		}
		stmt.release();

		for (int i = 0; i < ncols; i++) {
			updated[i] = false;
		}
	}

	private void loadColumns() throws CtsqlException {
		for (int i = 0; i < ncols; i++) {
			CtsqlType colValue = null;
			int colOffset = offTuple + getBufOffsetIcol(i);
			switch (getBasicColumnType(i)) {
							case CtsqlType.CHAR_TYPE:
								colValue = new CtsqlChar(buf, colOffset, getColumnLength(i), bufIsOem, rTrimChar);
								break;
							case CtsqlType.INTEGER_TYPE:
								colValue = new CtsqlInteger(buf, colOffset);
								break;
							case CtsqlType.DECIMAL_TYPE:
								colValue = new CtsqlDecimal(buf, colOffset, getColumnLength(i));
								break;
							case CtsqlType.SMALLINT_TYPE:
								colValue = new CtsqlSmallint(buf, colOffset);
								break;
							case CtsqlType.DATE_TYPE:
								colValue = new CtsqlDate(buf, colOffset);
								break;
							case CtsqlType.TIME_TYPE:
								colValue = new CtsqlTime(buf, colOffset);
								break;
							case CtsqlType.DATETIME_TYPE:
								colValue = new CtsqlDateTime(buf, colOffset);
								break;
							case CtsqlType.BINARY_TYPE:
								colValue = new CtsqlBinary(buf, colOffset);
								break;
							default:
								CtsqlExceptionManager.getManager().throwException(CtsqlException.ILLEGAL_CONVERSION_TYPE, null);
			}
			columns[i] = colValue;
			updated[i] = false;
		}
		inInsertRow = false;
	}

	/**
	 *  This Method implements a scroll fetch acction.
	 *
	 * @param  mode                fetch mode identifier, first, last, next o
	 *      previous
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void longfetch(short mode) throws CtsqlException {
		Integer incr;
		short n;
		short nrecs;
		fldone = false;
		checkServer();
		synchronized (server) {
			if (!isOpen()) {
				CtsqlExceptionManager.getManager().throwException(CtsqlException.STATEMENT_NOT_OPEN, null);
			}
			n = 0;
			nrecs = (short) (sqlbsize(rowSize) / rowSize);
			if (mode == CtsqlCursor.ISLAST) {
				stat |= CtsqlConstants.C_PREV;
				isLast = true;
			} else {
				stat &= ~CtsqlConstants.C_PREV;
				isLast = false;
			}

			incr = (mode == CtsqlCursor.ISLAST) ? new Integer(-1) : new Integer(1);
			sendId(id);
			sendFetch(mode);
			offTuple = 0;
			if (mode == CtsqlCursor.ISLAST) {
				offTuple = (short) ((nrecs - 1) * rowSize);
			}

			n = waitResult(n, incr, false, false);
			checkLastError();

			if (getLastError() != 0) {
				CtsqlExceptionManager.getManager().throwException(getLastError());
			}
			left = n;
			if (fldone == false) {
				stat &= ~CtsqlConstants.C_SQEOF;
			}
			// Si el buffer no se llena y estamos leyendo de atras a adelante,
			// hay que recolocarlo al principio.
			if ((mode == CtsqlCursor.ISLAST) && (n < nrecs)) {
				System.arraycopy(buf, offTuple + rowSize, buf, 0, n * rowSize);
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  newrec              Description of Parameter
	 * @return                     Description of the Returned Value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private boolean scrollfillb(short newrec) throws CtsqlException {
		int nrecs;
		int incr;
		boolean chdir;
		short n = 0;
		int i;
		boolean buffl = false;

		flmov = false;
		fldone = false;
		errfl = false;

		checkServer();

		synchronized (server) {
			nrecs = (short) (sqlbsize(rowSize) / rowSize);
			incr = newrec >= 0 ? 1 : -1;

			chdir = ((incr == 1) && ((stat & CtsqlConstants.C_PREV) > 0)) ||
				((incr == -1) && !((stat & CtsqlConstants.C_PREV) > 0));

			if (chdir == true) {
				if ((stat & CtsqlConstants.C_SQEOF) > 0) {
					i = left;
				} else {
					i = left - 1;
				}

				travel((incr == -1 ? CtsqlCursor.ISPREV : CtsqlCursor.ISNEXT), i);
			}

			if (incr < 0) {
				stat |= CtsqlConstants.C_PREV;
			} else {
				stat &= ~CtsqlConstants.C_PREV;
			}

			if (recidx == -1 && newrec < nrecs) {
				buffl = true;
			}
			while (newrec < 0 || newrec >= left || buffl == true) {
				offTuple = 0;

				if (incr == -1) {
					offTuple += rowSize * (nrecs - 1);
				}
				n = 0;
				sendId(id);
				sendFetch(incr >= 0 ? CtsqlCursor.ISNEXT : CtsqlCursor.ISPREV);
				n = waitResult(n, new Integer(incr), false, false);
				checkLastError();

				if (newrec < 0) {
					newrec -= incr * n;
				} else if (newrec >= left) {
					newrec -= left;
				}
				left = n;
				if (fldone == false) {
					stat &= ~CtsqlConstants.C_SQEOF;
				}
				if (errfl != false || fldone != false) {
					break;
				}
				buffl = false;
			}
		}
		// Si el buffer no se llena y estamos leyendo de atras a adelante,
		// hay que recolocarlo al principio.
		if (n < nrecs && incr < 0) {
			System.arraycopy(buf, offTuple + rowSize, buf, 0, n * rowSize);
		}

		flBrkScroll = (newrec < 0 || newrec >= left) ? true : false;
		return flmov;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  mode                Description of Parameter
	 * @param  n                   Description of Parameter
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void travel(short mode, int n) throws CtsqlException {
		checkServer();
		synchronized (server) {
			if (n == 0) {
				return;
			}
			if (!isOpen()) {
				CtsqlExceptionManager.getManager().throwException(CtsqlException.STATEMENT_NOT_OPEN, null);
			}
			sendId(id);
			sendTravel(mode, n);
			waitResult((short) 0, new Integer(0), false, true);
			checkLastError();
			if (getLastError() != 0) {
				CtsqlExceptionManager.getManager().throwException(getLastError());
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void checkLastError() throws CtsqlException {

		if (getLastError() < 0) {
			Object[] args = new Object[1];
			args[0] = getLastErrorString();
			CtsqlExceptionManager.getManager().throwException(getLastError(), args, sqlca.getSqlErrd(1));
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  off  Description of Parameter
	 * @return      Description of the Returned Value
	 */
	private String readColumnInfo(int off) {
		String result = null;
		if ((off >= 0) && (titl != null)) {
			int len = 0;
			int titLen = titl.length;
			for (int i = off; (i < titLen) && (titl[i] != (byte) '\0'); i++) {
				len++;
			}
			result = new String(titl, off, len);
		}
		return result;
	}


	/**
	 *  Description of the Method
	 */
	private void init() {
		resetSqlca();
		curop = CtsqlConstants.FNEMPTY;
		name = null;
		id = CtsqlConstants.EMPTY;
		ncols = 0;
		//describeList = null;
		rowSize = 0;
		// tamaño de la tupla
		bPrepared = false;
		bOpen = false;
		lastCommand = 0;
		stat = 0;
		// buffer de tuplas
		recidx = 0;
		// indice a tuplas en buffer
		//buf;
		//offTuple;
		left = 0;
		// numero de tuplas leidas en el buffer

		// describe
		icollis = null;
		// lista de ICOL referidas a las columnas
		titl = null;
		// titulos de las columnas
	}


	/**
	 * @exception  CtsqlException  if a database access error occurs
	 * @jdbc.pending               Hay que repasar todo el lanzamiento de
	 *      excepciones.
	 */
	private void checkServer() throws CtsqlException {
		if (server == null || !server.isConnected()) {
			CtsqlExceptionManager.getManager().throwException(CtsqlException.SERVER_NOT_CONNECTED, null);
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  command             Description of Parameter
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void sendCommand(short command) throws CtsqlException {
		server.writeCommand(command);
		lastCommand = command;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  statement           Description of Parameter
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void sendParse(String statement) throws CtsqlException {

		char chr;
		int idx = 0;

		/*
		 *  MOD Paco 18-09-2001. Maaaal. El caracter ? no siempre indica variable host
		 *  while (idx < statement.length()) {
		 *  chr = statement.charAt(idx);
		 *  if (chr == '?') {
		 *  nHost++;
		 *  }
		 *  idx++;
		 *  }
		 */
		nHost = realhosts(statement);
		/*
		 *  END MOD Paco 18-9-2001
		 */
		sendCommand(CtsqlConstants.SQ_PARSE);
		server.writeString(statement);
		server.writeSmallint((short) nHost);
	}


	/*
	 *  NEW Paco 18-09-2001. Para que cuente el num de hosts decentemente
	 */
	/**
	 *  Parses the statement and return real number of hosts
	 *
	 * @param  stmt  statement to parse
	 * @return       The real number of hosts vars in statement
	 */
	private int realhosts(String stmt) {
		short active = 1;
		// 1 --> no estoy entre comillas ni entre llaves
		short num = 0;
		char chend = ' ';
		char chr;
		int idx = 0;

		while (idx < stmt.length()) {
			chr = stmt.charAt(idx);
			switch (chr) {
							case '\'':
							//  comilla simple
							case '\"':
							//  comilla doble
							case '{':
								if (active == 1) {
									active = 0;
									if (chr == '{') {
										chend = '}';
									} else {
										chend = chr;
									}
								} else if (chr == chend) {
									active = 1;
								}
								break;
							case '}':
								if (active == 0 && chr == chend) {
									active = 1;
								}
								break;
							case '?':
								if (active == 1) {
									num++;
								}
								break;
			}

			idx++;
		}
		return num;
	}


	/*
	 *  END NEW Paco 18-09-2001
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  id                  Description of Parameter
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void sendId(int id) throws CtsqlException {
		sendCommand(CtsqlConstants.SQ_ID);
		server.writeSmallint((short) id);
//                System.out.println("ID = " + id);
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void sendExecute() throws CtsqlException {
		sendCommand(CtsqlConstants.SQ_EXECUTE);
	}


	/**
	 * @param  hosts               Description of Parameter
	 * @exception  CtsqlException  if a database access error occurs
	 * @jdbc.pending               Hay que repasar todo el lanzamiento de
	 *      excepciones.
	 */
	private void sendHosts(CtsqlType hosts[]) throws CtsqlException {

		String value;
		int idx;
		int type;
		int nhost = 0;
		if (hosts != null) {
			nhost = hosts.length;
		}

		if (nhost == 0) {
			return;
		}

		/*
		 *  MOD Paco 11-6-2003. Si el nº de setters es menor que
		 *  el nº de hosts que espera, da null pointer exception
		 *  al hacer el getType. En el caso, envio nº de setters
		 *  Ej.- select * from tab where a = ? or b = ?
		 *  y envio una sola host.
		 */
		for (idx = 0; idx < nhost; idx++) {
			ProtocolType pt;
			try {
				pt = (ProtocolType) hosts[idx];
				if (pt == null) {
					nhost = idx;
				}
			} catch (ClassCastException e) {
				CtsqlExceptionManager.getManager().throwException(e);
				pt = null;
				nhost = 0;
			}
		}

		if (nhost == 0) {
			return;
		}
		/*
		 *  END-MOD Paco 11-6-2003
		 */
		sendCommand(CtsqlConstants.SQ_HOST);
		server.writeSmallint((short) nhost);
		idx = 0;

		for (idx = 0; idx < nhost; idx++) {
			ProtocolType pt;
			try {
				pt = (ProtocolType) hosts[idx];
			} catch (ClassCastException e) {
				CtsqlExceptionManager.getManager().throwException(e);
				pt = null;
			}
			type = pt.getType();
			server.writeSmallint((short) type);
			server.writeSmallint((short) ((pt.isNull()) ? -1 : 0));
			server.writeSmallint((short) (pt.getSqlLength()));
			if ((pt != null) && (!(pt.isNull()))) {
				server.writeData(pt.store(), pt.getStoreLength());
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void sendDescribe() throws CtsqlException {
		sendCommand(CtsqlConstants.SQ_DESCRIBE);
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void sendOpen() throws CtsqlException {
		sendCommand(CtsqlConstants.SQ_OPEN);
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void sendNFetch() throws CtsqlException {
		sendCommand(CtsqlConstants.SQ_NFETCH);
		server.writeSmallint((short) sqlbsize(rowSize));
	}


// NEW Eva 22-02-2001
	/**
	 *  Description of the Method
	 *
	 * @param  mode                Description of Parameter
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void sendFetch(short mode) throws CtsqlException {
		sendCommand(CtsqlConstants.SQ_FETCH);
		server.writeSmallint((short) mode);
		server.writeSmallint((short) sqlbsize(rowSize));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  mode                Description of Parameter
	 * @param  n                   Description of Parameter
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void sendTravel(short mode, int n) throws CtsqlException {
		sendCommand(CtsqlConstants.SQ_TRAVEL);
		server.writeSmallint(mode);
		server.writeSmallint((short) n);
	}


// END NEW Eva 22-02-2001

	/**
	 *  Description of the Method
	 *
	 * @param  cursorName          Description of Parameter
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void sendCurName(String cursorName) throws CtsqlException {
		sendCommand(CtsqlConstants.SQ_CURNAME);
		server.writeString(cursorName);
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void readErrInfo() throws CtsqlException {
		int ret;
		ret = server.readSmallint();
		sqlca.setSqlCode(ret);
		sqlca.setSqlErrd(0, server.readInteger());
		sqlca.setSqlErrd(1, server.readSmallint());
		sqlca.setSqlErrd(4, server.readSmallint());
		if (ret != CtsqlConstants.Z_CTSQLVERS) {
			sqlca.setSqlErrorString(server.readString());
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void readDoneInfo() throws CtsqlException {
		boolean infoExtra = false;
		boolean infoDatabase = false;
		boolean infoInsert = false;

		if (lastCommand == CtsqlConstants.SQ_EXECUTE) {
			infoExtra = true;
			switch (curop) {
							case CtsqlConstants.FNDBCHANGE:
							case CtsqlConstants.FNSTARTDB:
							case CtsqlConstants.FNDBCREATE:
								infoDatabase = true;
								break;
							case CtsqlConstants.FNINSERTV:
								infoInsert = true;
								break;
							default:
								break;
			}

		} else if (lastCommand == CtsqlConstants.SQ_NFETCH) {
			infoExtra = true;
		}

		if (infoExtra) {
			if (server.readSmallint() != 0) {
				sqlca.setNullWarn(true);
			}

			sqlca.setSqlErrd(2, server.readInteger());
			sqlca.setSqlErrd(5, server.readInteger());
			sqlca.setSqlErrd(1, server.readInteger());
		}

		if (infoDatabase) {
			server.setCurrentDBPath(server.readString());
			// dbpath
			server.setCurrentDB(server.readString());
			// dbname
			server.setTransactionFlag(server.readSmallint());
			// flag transaction

			if (sqlca.getSqlErrd(2) != 0) {
				server.setRowidTypeSize(sqlca.getSqlErrd(2) - 1,
					sqlca.getSqlErrd(5));
			}
		}

		if (infoInsert) {
			if (!server.isRowidStandard()) {
				server.readString();
			} else {
				serialValueObtained = true;
				this.server.setLastSerial(getSerial());
				this.server.setLastRowid(getRowIdAfterInsert());
			}
			sqlca.setSqlErrd(3, 1);
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void readLckWarnInfo() throws CtsqlException {
		sqlca.setLocked(true);
		/*
		 *  De momento el acceso a una fila bloqueada da error, falta la posibilidad
		 *  de parametrizar este hecho
		 */
		sqlca.setSqlCode(CtsqlException.LOCKED_BY_ANOTHER_USER);
	}

	/**
	 * @exception  CtsqlException  if a database access error occurs
	 * @jdbc.pending               Hay que repasar todo el lanzamiento de
	 *      excepciones.
	 */
	private void readTupleInfo() throws CtsqlException {
		short size;
		size = server.readSmallint();
		if (size != rowSize) {
			sqlca.setSqlCode(CtsqlConstants.W_ROWSIZE);
			//server.unloadData(); FALTA!!!!
			CtsqlExceptionManager.getManager().throwException(CtsqlException.BAD_ROW_SIZE, null);
		}

		server.readDataEven(buf, offTuple, size);
// MOD Eva 11-05-2001
//		recidx++;
//		offTuple += size;
// Eva 11-05-2001
	}


	/**
	 *  Description of the Method
	 *
	 * @param  buffer  Description of Parameter
	 * @param  len     Description of Parameter
	 * @return         Description of the Returned Value
	 */
	private byte[] realoja(byte[] buffer, int len) {
		if ((buffer == null) || buffer.length < len) {
			return new byte[len];
		} else {
			return buffer;
		}
	}

	/**
	 *  Parámetros del icollis correspondientes a la longitud 7: noff, almacena
	 *  contendrá el offset dónde comienza el nombre de cada columna. toff,
	 *  almacena el offset dónde comenzará cada título de columna. roff, offset del
	 *  valor de cada columna en cada fila. type, tipo de la columna. long,
	 *  longitud del tipo de la columna. Para los char dará la longitud y para los
	 *  tipos Decimal contendrá la longitud almacenada. flags, fmt. Existe un
	 *  buffer que contendrá un array de bytes con los títulos y los nombres de las
	 *  columnas seguidos. La forma de obtener cada uno para cada columna es
	 *  mediante las variables noff y toff. La longitud de cada título y cada
	 *  nombre se sabrá porque irán separadas unas de otras por el caracter nulo.
	 *
	 * @exception  CtsqlException  if a database access error occurs
	 */
	private void readDescribeInfo() throws CtsqlException {
		short titlen;
		int i;
		int j;

		curop = server.readSmallint();
		id = server.readSmallint();
		rowSize = server.readSmallint();
		// almacena el tamaño de las filas
		ncols = server.readSmallint();
		// almacena el número de columnas.
		if (ncols == 0) {
			return;
		}

		titlen = server.readSmallint();
		// almacena la longitud del buffer que contendrá todos los
		// nombres y las etiquetas de las columnas.
		noby = server.readSmallint();
		// número de columnas en order by.
		buf = realoja(buf, sqlbsize(rowSize));
		offTuple = 0;
		titl = realoja(titl, titlen);
		// buffer que contendrá los nombres y los títulos de las columnas.
		server.readDataEven(titl, 0, titlen);
		icollis = new short[ncols][7];

		for (i = 0; i < ncols; i++) {
			for (j = 0; j < 7; j++) {
				icollis[i][j] = server.readSmallint();
			}
		}

		if (noby > 0) {
			oby = new short[noby][3];
			for (i = 0; i < noby; i++) {
				for (j = 0; j < 3; j++) {
					oby[i][j] = server.readSmallint();
				}
			}
		}
		// Array de variables intermedias JDBC 2.
		columns = new CtsqlType[ncols];
		updated = new boolean[ncols];
	}

	private short waitResult(short n, Integer incr, boolean isFetch, boolean travel) throws CtsqlException {
		short ret;
		int count;

		resetSqlca();
		server.writeCommand(CtsqlConstants.SQ_EOT);
		server.flush();
		ret = server.readSmallint();
		while (ret != CtsqlConstants.SQ_EOT) {
			switch (ret) {
							case CtsqlConstants.SQ_ERR:
								readErrInfo();
								errfl = true;
								break;
							case CtsqlConstants.SQ_DONE:
								if (travel) {
									server.readSmallint();
								} else {
									readDoneInfo();
									if (isFetch == true) {
										stat |= CtsqlConstants.C_EOF;
									} else {
										stat |= CtsqlConstants.C_SQEOF;
									}
									fldone = true;
								}
								break;
							case CtsqlConstants.SQ_LCKWARN:
								readLckWarnInfo();
								break;
							case CtsqlConstants.SQ_TUPLE:
								if ((isFetch == true) && (recidx == 0)) {
									offTuple = 0;
								}
								readTupleInfo();
								if (isFetch == true) {
									// He leido una tupla e incremento el número de registros leidos
									recidx++;
								}
								offTuple += incr.intValue() * rowSize;
								n++;
								if (isFetch == false) {
									flmov = true;
								}
								break;
							case CtsqlConstants.SQ_DESCRIBE:
								readDescribeInfo();
								break;
							default:
								if (sqlca.getSqlCode() == 0) {
									sqlca.setSqlCode(CtsqlException.COMMUNICATION_INTERRUPTED);
								}
								return n;
			}
			ret = server.readSmallint();

			if (ret == CtsqlConstants.SQ_EOT) {
				break;
			}
		}
		return n;
	}


	//---------------------- JMD --------------------
	/**
	 * @param  index               Description of Parameter
	 * @param  type                Description of Parameter
	 * @exception  CtsqlException  if a database access error occurs
	 * @jdbc.pending               Hay que repasar todo el lanzamiento de
	 *      excepciones.
	 */
	private void checkColumnType(int index, int type) throws CtsqlException {
		if (getColumnType(index) != type) {
			CtsqlExceptionManager.getManager().throwException(CtsqlException.INVALID_COLUMN_TYPE, null);
		}
	}

	private void resetSqlca() {
		sqlca.resetSqlca();
		serialValueObtained = false;
	}

}
