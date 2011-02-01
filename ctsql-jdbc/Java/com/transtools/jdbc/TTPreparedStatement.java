/*
 *  Copyright 2003
 *
 *  TransTOOLs, S.A.
 *  All rights reserved
 */
package com.transtools.jdbc;
import com.transtools.ctsql.CtsqlBinary;
import com.transtools.ctsql.CtsqlChar;
import com.transtools.ctsql.CtsqlCursor;
import com.transtools.ctsql.CtsqlDate;
import com.transtools.ctsql.CtsqlDateTime;
import com.transtools.ctsql.CtsqlDecimal;
import com.transtools.ctsql.CtsqlException;
import com.transtools.ctsql.CtsqlFactory;
import com.transtools.ctsql.CtsqlInteger;
import com.transtools.ctsql.CtsqlSmallint;
import com.transtools.ctsql.CtsqlTime;
import com.transtools.ctsql.CtsqlType;

import java.io.Reader;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
// JDK 1.4
//import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
/**
 *  TTPreparedStatement class implements PreparedStatement. PreparedStatement
 *  object represents a precompiled SQL statement that can then be used to
 *  efficiently execute this statement multiple times.
 *
 * @author     eva
 * @created    March 7, 2001
 * @version    $Revision: 1.28 $
 */

class TTPreparedStatement extends TTStatement implements PreparedStatement {

	private final static int MAX_FIELD_SIZE = 32767;

	/*
	 *  Ya está en TTStatement
	 *  private int typeResultSet = ResultSet.TYPE_FORWARD_ONLY;
	 *  private int typeConcurrency = ResultSet.CONCUR_READ_ONLY;
	 */
//	private static Vector extent = new Vector(); // Probe
	/**
	 *  connection is a TTConnection object. TTConnection provides a connection
	 */
	private TTConnection connection;

	/**
	 *  ctsqlCursor is a CtsqlCursor object. CtsqlCursor is a interface that
	 *  represents the cursor to database access.
	 */
	private CtsqlCursor ctsqlCursor;
	/*
	 *  Ya tiene este atributo la superclase
	 *  private String myStatement;
	 */
	/**
	 *  resultSet is a ResultSet object. ResultSet containts all of the row which
	 *  satisfied the conditions in a SQL statement.
	 */
	private ResultSet resultSet;

	private CtsqlType[] hosts = null;


	/**
	 *  Constructor: receives a connection and a string that represents a
	 *  statement.
	 *
	 * @param  aConnection       Description of Parameter
	 * @param  aStatement        Description of Parameter
	 * @exception  SQLException  Description of Exception
	 */
	public TTPreparedStatement(TTConnection aConnection, String aStatement) throws SQLException {
		super(aConnection);
//      extent.add( this ); // Probe
		connection = aConnection;
		ctsqlCursor = CtsqlFactory.getFactory().getNewCursor(connection.getCtsqlServer());
		try {
			setMyStatement(aStatement);
			ctsqlCursor.prepare(getMyStatement());
			hosts = new CtsqlType[ctsqlCursor.getHostsNumber()];
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}
	}


	/**
	 *  Constructor for the TTPreparedStatement object
	 *
	 * @param  aConnection           Description of Parameter
	 * @param  aStatement            Description of Parameter
	 * @param  resultSetType         Description of Parameter
	 * @param  resultSetConcurrency  Description of Parameter
	 * @exception  SQLException      Description of Exception
	 */
	public TTPreparedStatement(TTConnection aConnection,
	                           String aStatement,
	                           int resultSetType,
	                           int resultSetConcurrency) throws SQLException {
		super(aConnection, resultSetType, resultSetConcurrency);
		connection = aConnection;
		ctsqlCursor = CtsqlFactory.getFactory().getNewCursor(connection.getCtsqlServer());
		/*
		 *  Ya está en TTStatement
		 *  typeConcurrency = resultSetConcurrency;
		 *  typeResultSet = resultSetType;
		 */
		try {
			setMyStatement(aStatement);
			ctsqlCursor.prepare(getMyStatement());
			hosts = new CtsqlType[ctsqlCursor.getHostsNumber()];
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}
	}


	/**
	 *  setQueryTimeout method is not supported.
	 *
	 * @param  timeout           The new QueryTimeout value
	 * @exception  SQLException  Description of Exception
	 */
	public void setQueryTimeout(int timeout) throws SQLException {
		ExceptionManager.getManager().throwException(301, "setQueryTimeout method is not supported.");
	}


	/**
	 *  setMaxFieldSize sets the limit for the maximum number of bytes in a column
	 *  to the given number of bytes. This is the maximum number of bytes that can
	 *  be returned for any column value. If the limit is exceeded, the excess data
	 *  is silently discarded. For maximum portability, use values greater than
	 *  256.
	 *
	 * @param  size  the new max column size limit; zero means unlimited Note: the
	 *      server contains a constant MaxFieldSize. Ignores the param size
	 */
	public void setMaxFieldSize(int size) {
	}


	/*
	 *  Ya tiene este método la superclase
	 *  public void setMyStatement(String stmt) {
	 *  myStatement = new String(stmt);
	 *  }
	 */
	/**
	 *  setMaxRows sets the limit for the maximum number of rows that any ResultSet
	 *  object can contain to the given number. If the limit is exceeded, the
	 *  excess rows are silently dropped.
	 *
	 * @param  rows  the new max rows limit; zero means unlimited. Note: the server
	 *      contains a constant MaxRow. Ignores the param size.
	 */
	public void setMaxRows(int rows) {
	}


	/**
	 * @param  scape    The new EscapeProcessing value
	 * @jdbc.pending    Aún por definir
	 */
	public void setEscapeProcessing(boolean scape) {
	}


	/**
	 *  setCursorName defines the SQL cursor name that will be used by subsequent
	 *  Statement object execute methods.
	 *
	 * @param  name              is the new cursor name.
	 * @exception  SQLException  Description of Exception
	 */
	public void setCursorName(String name) throws SQLException {
		try {
			ctsqlCursor.setName(name);
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}
	}


	/**
	 *  setObject method sets the value of the designated parameter using the given
	 *  object.
	 *
	 * @param  index             represents the parameter index
	 * @param  object            object that represent the value which the
	 *      parameter will be set.
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 * @jdbc.pending             Hay que ver que pasa con las conversiones entre
	 *      los diferentes tipos de las columnas.
	 */
	public void setObject(int index, java.lang.Object object) throws SQLException {
		int type = -1;

		type = findType(object);
		setObject(index, object, type);
	}


	/**
	 *  setShort method sets the designated parameter to a Java short value.
	 *
	 * @param  index             represents the parameter index
	 * @param  value             The new Short value
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public void setShort(int index, short value) throws SQLException {
		hosts[index - 1] = CtsqlFactory.getFactory().getNewSmallint(value);
	}

	/*
	 *  END-NEW Paco 30-10-2002
	 */
	/**
	 *  setInt method sets the designated parameter to a Java int value.
	 *
	 * @param  index             represents the parameter index
	 * @param  value             The new Int value
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */

	public void setInt(int index, int value) throws SQLException {
		hosts[index - 1] = CtsqlFactory.getFactory().getNewInteger(value);
	}

	/*
	 *  END-NEW Paco 30-10-2002
	 */
	/**
	 *  setDouble method sets the designated parameter to a Java double value.
	 *
	 * @param  index             represents the parameter index
	 * @param  value             The new Double value
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public void setDouble(int index, double value) throws SQLException {
		hosts[index - 1] = CtsqlFactory.getFactory().getNewDecimal(value);
	}

	/*
	 *  END-NEW Paco 30-10-2002
	 */
	/**
	 *  setString method sets the designated parameter to a Java String value.
	 *
	 * @param  index             represents the parameter index
	 * @param  value             The new String value
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public void setString(int index, String value) throws SQLException {
		hosts[index - 1] = CtsqlFactory.getFactory().getNewChar(value);
	}


	/**
	 *  setDate method sets the designated parameter to a java.sql.Date value.
	 *
	 * @param  index             represents the parameter index
	 * @param  value             The new Date value
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	/*
	 *  MOD Paco 30-10-2002. Tener en cuenta nulos
	 *  public void setDate(int index, java.sql.Date value) throws SQLException {
	 *  Calendar type = Calendar.getInstance();
	 *  type.setTime(value);
	 *  hosts[index - 1] = CtsqlFactory.getFactory().getNewDate(type);
	 *  }
	 */
	public void setDate(int index, java.sql.Date value) throws SQLException {
		if (value != null) {
			Calendar type = Calendar.getInstance();
			type.setTime(value);
			hosts[index - 1] = CtsqlFactory.getFactory().getNewDate(type);
		} else {
			hosts[index - 1] = CtsqlFactory.getFactory().getNewDate(null);
		}
	}

	/*
	 *  END-MOD Paco 30-10-2002.
	 */
	/**
	 *  setTime method sets the designated parameter to a java.sql.Time value.
	 *
	 * @param  index             represents the parameter index
	 * @param  value             The new Time value
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	/*
	 *  MOD Paco 30-10-2002. Tener en cuenta nulos
	 *  public void setTime(int index, java.sql.Time value) throws SQLException {
	 *  Calendar type = Calendar.getInstance(Locale.ITALY);
	 *  type.setTime(value);
	 *  hosts[index - 1] = CtsqlFactory.getFactory().getNewTime(type);
	 *  }
	 */
	public void setTime(int index, java.sql.Time value) throws SQLException {
		if (value != null) {
			Calendar type = Calendar.getInstance(Locale.ITALY);
			type.setTime(value);
			hosts[index - 1] = CtsqlFactory.getFactory().getNewTime(type);
		} else {
			hosts[index - 1] = CtsqlFactory.getFactory().getNewTime(null);
		}
	}

	/*
	 *  END-MOD Paco 30-10-2002.
	 */
	/*
	 *  Necesary for JDBC 1.0
	 */
	/**
	 *  setBoolean method is not supported.
	 *
	 * @param  index             The new Boolean value
	 * @param  value             The new Boolean value
	 * @exception  SQLException  Description of Exception
	 */
	public void setBoolean(int index, boolean value) throws SQLException {
		// ExceptionManager.getManager().throwException(300, "Boolean");
		hosts[index - 1] = CtsqlFactory.getFactory().getNewInteger(value?1:0);
	}


	/**
	 *  setByte method is not supported.
	 *
	 * @param  index             The new Byte value
	 * @param  value             The new Byte value
	 * @exception  SQLException  Description of Exception
	 */
	public void setByte(int index, byte value) throws SQLException {
		ExceptionManager.getManager().throwException(300, "Byte");
	}


	/**
	 *  setLong method is not supported.
	 *
	 * @param  index             The new Long value
	 * @param  value             The new Long value
	 * @exception  SQLException  Description of Exception
	 */
	public void setLong(int index, long value) throws SQLException {
		//ExceptionManager.getManager().throwException(300, "Long");
		//MOD Paco-Rai-MA 4-8-2009.
		hosts[index - 1] = CtsqlFactory.getFactory().getNewDecimal(value);
	}


	/**
	 *  setFloat method is not supported.
	 *
	 * @param  index             The new Float value
	 * @param  value             The new Float value
	 * @exception  SQLException  Description of Exception
	 */
	public void setFloat(int index, float value) throws SQLException {
		ExceptionManager.getManager().throwException(300, "Float");
	}


	/**
	 *  setBigDecimal sets the designated parameter to a java.math.BigDecimal
	 *  value.
	 *
	 * @param  index             is the parameter index.
	 * @param  value             is the parameter value.
	 * @exception  SQLException  Description of Exception
	 */
	public void setBigDecimal(int index, java.math.BigDecimal value) throws SQLException {
		com.transtools.ctsql.CtsqlDecimal d = CtsqlFactory.getFactory().getNewDecimal(value.doubleValue());
		hosts[index - 1] = d;
	}


	/**
	 *  setBytes method is not supported.
	 *
	 * @param  index             The new Bytes value
	 * @param  value             The new Bytes value
	 * @exception  SQLException  Description of Exception
	 */
	public void setBytes(int index, byte[] value) throws SQLException {
//		ExceptionManager.getManager().throwException(300, "Bytes");
		hosts[index - 1] = CtsqlFactory.getFactory().getNewBinary(value);
	}


	/**
	 *  setTimestamp method is not supported.
	 *
	 * @param  index             The new Timestamp value
	 * @param  value             The new Timestamp value
	 * @exception  SQLException  Description of Exception
	 */
	/*
	 *  MOD Paco 30-10-2002. Tener en cuenta nulos
	 *  public void setTimestamp(int index, java.sql.Timestamp value) throws SQLException {
	 *  / Mod Eva 18-04-2001
	 *  /		ExceptionManager.getManager().throwException(300, "Timestamp");
	 *  Calendar type = Calendar.getInstance();
	 *  type.setTime(value);
	 *  hosts[index - 1] = CtsqlFactory.getFactory().getNewDateTime(type);
	 *  / END Mod Eva 18-04-2001
	 *  }
	 */
	public void setTimestamp(int index, java.sql.Timestamp value) throws SQLException {
		if (value != null) {
			Calendar type = Calendar.getInstance();
			type.setTime(value);
			hosts[index - 1] = CtsqlFactory.getFactory().getNewDateTime(type);
		} else {
			hosts[index - 1] = CtsqlFactory.getFactory().getNewDateTime(null);
		}
	}

	/*
	 *  END-MOD Paco 30-10-2002.
	 */
	/**
	 *  setAsciiStream method is not supported.
	 *
	 * @param  index             The new AsciiStream value
	 * @param  stream            The new AsciiStream value
	 * @param  length            The new AsciiStream value
	 * @exception  SQLException  Description of Exception
	 */
	public void setAsciiStream(int index, java.io.InputStream stream, int length) throws SQLException {
		ExceptionManager.getManager().throwException(300, "AsciiStream");
	}


	/**
	 *  setUnicodeStream method is not supported.
	 *
	 * @param  index             The new UnicodeStream value
	 * @param  stream            The new UnicodeStream value
	 * @param  length            The new UnicodeStream value
	 * @exception  SQLException  Description of Exception
	 * 
	 * @deprecated
	 */
	public void setUnicodeStream(int index, java.io.InputStream stream, int length) throws SQLException {
		ExceptionManager.getManager().throwException(300, "UnicodeStream");
	}


	/**
	 *  setBinaryStream method is not supported.
	 *
	 * @param  index             The new BinaryStream value
	 * @param  stream            The new BinaryStream value
	 * @param  length            The new BinaryStream value
	 * @exception  SQLException  Description of Exception
	 */
	public void setBinaryStream(int index, java.io.InputStream stream, int length) throws SQLException {
		ExceptionManager.getManager().throwException(300, "BinaryStream");
	}


	/**
	 *  setObject sets the value of the designated parameter with the given object.
	 *  The second argument must be an object type; for integral values, the
	 *  java.lang equivalent objects should be used.
	 *
	 * @param  index             is the parameter index
	 * @param  object            contain the input parameter value
	 * @param  sqlType           is the SQL type(as defined in java.sql.Types)to be
	 *      sent to the database.
	 * @param  scale             is the number of digits after the decimal point.
	 *      For all other types, this value will be ignored.
	 * @exception  SQLException  Description of Exception
	 */
	public void setObject(int index, java.lang.Object object, int sqlType, int scale) throws SQLException {
		switch (sqlType) {
						case java.sql.Types.CHAR:
							setString(index, convertToString(object));
							break;
						case java.sql.Types.SMALLINT:
							setShort(index, convertToShort(object));
							break;
						case java.sql.Types.INTEGER:
							setInt(index, convertToInt(object));
							break;
						case java.sql.Types.TIME:
							setTime(index, convertToTime(object));
							break;
						case java.sql.Types.DECIMAL:
							setDouble(index, convertToDouble(object));
							break;
						case java.sql.Types.DOUBLE:
							setDouble(index, convertToDouble(object));
							break;
						case java.sql.Types.DATE:
							setDate(index, convertToDate(object));
							break;
						case java.sql.Types.BINARY:
							setBytes(index, convertToBinary(object));
							break;
						case java.sql.Types.TIMESTAMP:
							setTimestamp(index, convertToDateTime(object));
							break;
						case java.sql.Types.TINYINT:
							if(object == null){
								setObject(index, null, java.sql.Types.INTEGER);
							}else{
								ExceptionManager.getManager().throwException(301, "TINYINT type is not supported.");
							}
							break;
						case java.sql.Types.BIGINT:
							if(object == null){
								setObject(index, null, java.sql.Types.INTEGER);
							}else{
								ExceptionManager.getManager().throwException(301, "BIGINT type is not supported.");
							}
							break;
						case java.sql.Types.REAL:
							if(object == null){
								setObject(index, null, java.sql.Types.INTEGER);
							}else{
								ExceptionManager.getManager().throwException(301, "REAL type is not supported.");
							}
							break;
						case java.sql.Types.FLOAT:
							if(object == null){
								setObject(index, null, java.sql.Types.INTEGER);
							}else{
								ExceptionManager.getManager().throwException(301, "FLOAT type is not supported.");
							}
							break;
						case java.sql.Types.VARCHAR:
							if(object == null){
								setObject(index, null, java.sql.Types.INTEGER);
							}else{
								ExceptionManager.getManager().throwException(301, "VARCHAR type is not supported.");
							}
							break;
						case java.sql.Types.LONGVARCHAR:
							if(object == null){
								setObject(index, null, java.sql.Types.INTEGER);
							}else{
								ExceptionManager.getManager().throwException(301, "LONGVARCHAR type is not supported.");
							}
							break;
						default:
							if(object == null){
								setShort(index, convertToShort(object));
							}else{
								ExceptionManager.getManager().throwException(CtsqlException.INVALID_COLUMN_TYPE,
								CtsqlException.getMessage(CtsqlException.INVALID_COLUMN_TYPE));
							}
		}
	}


	/**
	 *  setObject sets the value of the designated parameter with the given object.
	 *  This method is like the method setObject above, except that it assumes a
	 *  scale of zero.
	 *
	 * @param  index             is the parameter index
	 * @param  object            contain the input parameter value
	 * @param  sqlType           is the SQL type(as defined in java.sql.Types)to be
	 *      sent to the database.
	 * @exception  SQLException  Description of Exception
	 */
	public void setObject(int index, java.lang.Object object, int sqlType) throws SQLException {
		setObject(index, object, sqlType, 0);
	}


	/**
	 *  setNull sets the designated parameter to SQL NULL.
	 *
	 * @param  index             is the parameter index
	 * @param  sqlType           is the SQL type code defined in java.sql.Types
	 * @exception  SQLException  Description of Exception
	 */
	public void setNull(int index, int sqlType) throws SQLException {
		setObject(index, null, sqlType);
	}


	/**
	 *  JDBC2.0
	 *
	 * @param  i  The new Ref value
	 * @param  x  The new Ref value
	 */
	/**
	 *  JDBC2.0 JDBC2.0 JDBC2.0 JDBC2.0 JDBC2.0 JDBC2.0 Funcionalidad
	 *  correspondiente a JDBC 2.0. Por ahora no se implementarán estos métodos.
	 *
	 * @param  i  The new Ref value
	 * @param  x  The new Ref value
	 */
	public void setRef(int i, Ref x) {
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @param  i          The new Timestamp value
	 * @param  timestamp  The new Timestamp value
	 * @param  calendar   The new Timestamp value
	 */
	public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) {
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @param  i                 The new Date value
	 * @param  date              The new Date value
	 * @param  calendar          The new Date value
	 * @exception  SQLException  <OJO - Put here the exception description>
	 */
	public void setDate(int i, Date date, Calendar calendar) throws SQLException {
		ExceptionManager.getManager().throwException(301, "setDate(int, Date, Calendar) method is not supported.");
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @param  paramIndex  The new Null value
	 * @param  sqlType     The new Null value
	 * @param  typeName    The new Null value
	 */
	public void setNull(int paramIndex, int sqlType, String typeName) {
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @param  i     The new Clob value
	 * @param  clob  The new Clob value
	 */
	public void setClob(int i, Clob clob) {
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @param  i     The new Blob value
	 * @param  blob  The new Blob value
	 */
	public void setBlob(int i, Blob blob) {
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @param  i      The new Array value
	 * @param  array  The new Array value
	 */
	public void setArray(int i, Array array) {
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @param  i         The new Time value
	 * @param  time      The new Time value
	 * @param  calendar  The new Time value
	 */
	public void setTime(int i, Time time, Calendar calendar) {
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @param  i  The new FetchDirection value
	 */
	public void setFetchDirection(int i) {
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @param  size  The new FetchSize value
	 */
	public void setFetchSize(int size) {
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @param  i       The new CharacterStream value
	 * @param  reader  The new CharacterStream value
	 * @param  j       The new CharacterStream value
	 */
	public void setCharacterStream(int i, Reader reader, int j) {
	}


	/**
	 *  getResultSet method returns the current result as a ResultSet object.
	 *
	 * @return                   the current ResultSet
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public ResultSet getResultSet() throws SQLException {
		return resultSet;
	}


	/**
	 *  getUpdateCount method returns the current result as an int. Calls
	 *  CtsqlCursor.getUpdateCount
	 *
	 * @return                   The UpdateCount value
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public int getUpdateCount() throws SQLException {
		return ctsqlCursor.getUpdateCount();
	}


	/**
	 *  getQueryTimeout method is not supported.
	 *
	 * @return                   The QueryTimeout value
	 * @exception  SQLException  Description of Exception
	 */
	public int getQueryTimeout() throws SQLException {
		return 0;
	}


	/**
	 *  getMaxFieldSize returns the maximum number of bytes allowed for any column
	 *  value. This limit is the maximum number of bytes that can be returned for
	 *  any column value. If the limit is exceeded, the excess data is silently
	 *  discarded.
	 *
	 * @return    the current max column size limit; zero means unlimited
	 */
	public int getMaxFieldSize() {
		return MAX_FIELD_SIZE;
	}


	/**
	 *  getMaxRows retrieves the maximum number of rows that a ResultSet object can
	 *  contain. If the limit is exceeded, the excess rows are silently dropped.
	 *
	 * @return    the current max row limit; zero means unlimited.
	 */
	public int getMaxRows() {
		return 0;
	}


	/**
	 *  Retrieves the first warning reported by calls on this Statement object.
	 *  Subsequent Statement object warnings will be chained to this SQLWarning
	 *  object. The warning chain is automatically cleared each time a statement is
	 *  (re)executed.
	 *
	 * @return    The first SQLWarning object or null.
	 */
	public SQLWarning getWarnings() {
		return ctsqlCursor.getWarnings();
	}


	/**
	 *  getMoreResults method is not supported.
	 *
	 * @return                   The MoreResults value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean getMoreResults() throws SQLException {
		return super.getMoreResults();
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @return    The FetchSize value
	 */
	public int getFetchSize() {
		return 0;
	}


	/*
	 *  Ya está en TTStatement
	 *  public int getResultSetConcurrency() {
	 *  return typeConcurrency;
	 *  }
	 */
	/*
	 *  Ya está en TTStatement
	 *  public int getResultSetType() {
	 *  return typeResultSet;
	 *  }
	 */
	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @return    The FetchDirection value
	 */
	public int getFetchDirection() {
		return 0;
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @return    The Connection value
	 */
	public Connection getConnection() {
		return connection;
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @return                   The MetaData value
	 * @exception  SQLException  Description of Exception
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		if(resultSet == null){
			resultSet = new TTResultSet(this.connection, ctsqlCursor, this);			
		}
		return resultSet.getMetaData();
	}


	/**
	 *  execute method executes any kind of SQL statement. Case SELECT statement:
	 *  create a TTResultSet object an return true. Case other statement: executes
	 *  the statement and return false.
	 *
	 * @param  stmt              the statement which will be execute.
	 * @return                   true if execute a Select Statement and false in
	 *      other case.
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public synchronized boolean execute(String stmt) throws SQLException {
		boolean isSelectStatement = false;

		try {
			isSelectStatement = ctsqlCursor.isSelectStatement();
			if (isSelectStatement) {
				resultSet = new TTResultSet(connection, ctsqlCursor, this);
			} else {
				ctsqlCursor.execute(null);
			}
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}

		return isSelectStatement;
	}


	/**
	 *  executeQuery method create a TTResultSet object which executes the SQL
	 *  query.
	 *
	 * @param  stmt              the statement will be execute.
	 * @return                   the resultSet generated by the query.
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public synchronized ResultSet executeQuery(String stmt) throws SQLException {
		/**
		 * @todo    The statement prepare is missing
		 */
		resultSet = new TTResultSet(this.connection, ctsqlCursor, this);
		return resultSet;
	}


	/**
	 *  executeUpdate method execute SQL INSERT, UPDATE or DELETE statement.
	 *
	 * @param  stmt              the statement will be execute.
	 * @return                   calls CtsqlCursor.getUpdateCount and return the
	 *      row count a statements.
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */

	public synchronized int executeUpdate(String stmt) throws SQLException {
		try {
			System.out.println("Method executeUpdate(String) is not supported with PreparedStatement.");
			ctsqlCursor.execute(null);
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}
		return ctsqlCursor.getUpdateCount();
	}


	/**
	 *  method close the statement. This method destroys the cursor, so you cannot
	 *  use this statement after this.
	 *
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public synchronized void close() throws SQLException {
		resetStatement();
	}


	/**
	 *  cancel method is not supported.
	 *
	 * @exception  SQLException  Description of Exception
	 */
	public void cancel() throws SQLException {
		ExceptionManager.getManager().throwException(301, "cancel method is not supported.");
	}


	/**
	 *  clearWarnings method is not supported.
	 *
	 * @exception  SQLException  Description of Exception
	 */
	public void clearWarnings() throws SQLException {
		ExceptionManager.getManager().throwException(301, "clearWarnings method is not supported.");
	}


	/**
	 *  executeQuery method create a TTResultSet object which executes the SQL
	 *  query.
	 *
	 * @return                   the resultSet generated by the query.
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public synchronized ResultSet executeQuery() throws SQLException {
		CtsqlType[] params = (hosts.length != 0) ? hosts : null;

		/*
		 *  MOD Paco 27-3-2003. Segun la documentacion de JDBC 2.0,
		 *  un resultset se debe cerrar cuando es reejecutado
		 */
		try {
			if (ctsqlCursor.isOpen()) {
				ctsqlCursor.close();
			}
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}

		resultSet = new TTResultSet(this.connection, ctsqlCursor, params, this);
		return resultSet;
	}


	/**
	 *  executeUpdate method execute SQL INSERT, UPDATE or DELETE statement.
	 *
	 * @return                   calls CtsqlCursor.getUpdateCount and return the
	 *      row count a statements.
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 * @jdbc.pending             Queda pendiente obtener el número de filas
	 *      actualizadas a partir del ctsqlCursor.
	 */
	public synchronized int executeUpdate() throws SQLException {
		CtsqlType[] params = (hosts.length != 0) ? hosts : null;

		try {
			ctsqlCursor.execute(params);
			setRowIdAfterInsert(ctsqlCursor.getRowIdAfterInsert());
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}
		return ctsqlCursor.getUpdateCount();
	}


	/**
	 *  execute method executes any kind of SQL statement. Case SELECT statement:
	 *  create a TTResultSet object an return true. Case other statement: executes
	 *  the statement and return false.
	 *
	 * @return                   true if execute a Select Statement and false in
	 *      other case.
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public synchronized boolean execute() throws SQLException {
		boolean isSelectStatement = false;

		try {
			isSelectStatement = ctsqlCursor.isSelectStatement();
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}
		if (isSelectStatement) {
			executeQuery();
		} else {
			executeUpdate();
		}

		return isSelectStatement;
	}


	/**
	 *  clearParameters clears the current parameter values.
	 *
	 * @exception  SQLException  Description of Exception
	 */
	public void clearParameters() throws SQLException {
		for(int i=0; i < hosts.length; i++)
			hosts[i]=null;
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @return    Description of the Returned Value
	 */
	public int[] executeBatch() {
		return new int[0];
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 */
	public void clearBatch() {
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 */
	public void addBatch() {
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @param  batch  The feature to be added to the Batch attribute
	 */
	public void addBatch(String batch) {
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  SQLException  Description of Exception
	 */
	protected void finalize() throws SQLException {
//      System.out.println("finalize() -> preparedStatement: " + myStatement);
		close();
	}

	/*
	 *  NEW Paco 30-10-2002
	 */
	private void setShort(int index, Short value) throws SQLException {
		hosts[index - 1] = CtsqlFactory.getFactory().getNewSmallint(value);
	}

	/*
	 *  NEW Paco 30-10-2002
	 */
	private void setInt(int index, Integer value) throws SQLException {
		hosts[index - 1] = CtsqlFactory.getFactory().getNewInteger(value);
	}

	/*
	 *  NEW Paco 30-10-2002
	 */
	private void setDouble(int index, Double value) throws SQLException {
		hosts[index - 1] = CtsqlFactory.getFactory().getNewDecimal(value);
	}


	/**
	 *  Description of the Method
	 *
	 * @exception  SQLException  Description of Exception
	 */
	private void resetStatement() throws SQLException {
		if (resultSet != null) {
			resultSet.close();
			resultSet = null;
		}
		try {
			ctsqlCursor.release();
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  object            Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	private int findType(Object object) throws SQLException {
		int i = -1;

		if (object instanceof java.lang.String) {
			i = java.sql.Types.CHAR;
		} else if (object instanceof java.lang.Integer) {
			i = java.sql.Types.INTEGER;
		} else if (object instanceof java.lang.Short) {
			i = java.sql.Types.SMALLINT;
		} else if (object instanceof java.lang.Double) {
			i = java.sql.Types.DECIMAL;
		} else if (object instanceof java.math.BigDecimal) {
			i = java.sql.Types.DECIMAL;
		} else if (object instanceof java.sql.Date) {
			i = java.sql.Types.DATE;
		} else if (object instanceof java.sql.Time) {
			i = java.sql.Types.TIME;
		} else {
			ExceptionManager.getManager().throwException(CtsqlException.INVALID_COLUMN_TYPE,
				CtsqlException.getMessage(CtsqlException.INVALID_COLUMN_TYPE));
		}
		return i;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  object  Description of Parameter
	 * @return         Description of the Returned Value
	 */
	/*
	 *  MOD Paco 30-10-2002. Tener en cuenta nulos
	 *  private String convertToString(Object object) {
	 *  String result = " ";
	 *  CtsqlChar charvalue = CtsqlFactory.getFactory().getNewChar(null);
	 *  if (object instanceof java.lang.String) {
	 *  result = charvalue.setAsString((String) object);
	 *  } else if (object instanceof java.lang.Integer) {
	 *  result = charvalue.setAsInteger(((Integer) object).intValue());
	 *  } else if (object instanceof java.lang.Short) {
	 *  result = charvalue.setAsShort(((Short) object).shortValue());
	 *  } else if (object instanceof java.lang.Double) {
	 *  result = charvalue.setAsDouble(((Double) object).doubleValue());
	 *  } else if (object instanceof java.sql.Date) {
	 *  result = charvalue.setAsDateCalendar((java.sql.Date) object);
	 *  } else if (object instanceof java.sql.Time) {
	 *  result = charvalue.setAsTimeCalendar((java.sql.Time) object);
	 *  } else if (object instanceof java.math.BigDecimal) {
	 *  result = charvalue.setAsBigDecimal((java.math.BigDecimal) object);
	 *  }
	 *  return result;
	 *  }
	 */
	private String convertToString(Object object) {
		String result = null;

		if (object != null) {
			CtsqlChar charvalue = CtsqlFactory.getFactory().getNewChar(null);
			if (object instanceof java.lang.String) {
				result = charvalue.setAsString((String) object);
			} else if (object instanceof java.lang.Integer) {
				result = charvalue.setAsInteger(((Integer) object).intValue());
			} else if (object instanceof java.lang.Short) {
				result = charvalue.setAsShort(((Short) object).shortValue());
			} else if (object instanceof java.lang.Double) {
				result = charvalue.setAsDouble(((Double) object).doubleValue());
			} else if (object instanceof java.sql.Date) {
				result = charvalue.setAsDateCalendar((java.sql.Date) object);
			} else if (object instanceof java.sql.Time) {
				result = charvalue.setAsTimeCalendar((java.sql.Time) object);
			} else if (object instanceof java.math.BigDecimal) {
				result = charvalue.setAsBigDecimal((java.math.BigDecimal) object);
			}
		}
		return result;
	}

	/*
	 *  END-MOD Paco 30-10-2002.
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  object  Description of Parameter
	 * @return         Description of the Returned Value
	 */

	/*
	 *  MOD Paco 30-10-2002. Tener en cuenta nulos
	 *  private short convertToShort(Object object) {
	 *  short result = -1;
	 *  CtsqlSmallint shortvalue = CtsqlFactory.getFactory().getNewSmallint(null);
	 *  if (object instanceof java.lang.String) {
	 *  result = shortvalue.setAsString((String) object);
	 *  } else if (object instanceof java.lang.Integer) {
	 *  result = shortvalue.setAsInteger(((Integer) object).intValue());
	 *  } else if (object instanceof java.lang.Short) {
	 *  result = shortvalue.setAsShort(((Short) object).shortValue());
	 *  } else if (object instanceof java.lang.Double) {
	 *  result = shortvalue.setAsDouble(((Double) object).doubleValue());
	 *  } else if (object instanceof java.math.BigDecimal) {
	 *  result = shortvalue.setAsBigDecimal((java.math.BigDecimal) object);
	 *  }
	 *  return result;
	 *  }
	 */
	private Short convertToShort(Object object) {
		Short result = null;

		if (object != null) {
			CtsqlSmallint shortvalue = CtsqlFactory.getFactory().getNewSmallint(null);
			if (object instanceof java.lang.String) {
				result = new Short(shortvalue.setAsString((String) object));
			} else if (object instanceof java.lang.Integer) {
				result = new Short(shortvalue.setAsInteger(((Integer) object).intValue()));
			} else if (object instanceof java.lang.Short) {
				result = new Short(shortvalue.setAsShort(((Short) object).shortValue()));
			} else if (object instanceof java.lang.Double) {
				result = new Short(shortvalue.setAsDouble(((Double) object).doubleValue()));
			} else if (object instanceof java.math.BigDecimal) {
				result = new Short(shortvalue.setAsBigDecimal((java.math.BigDecimal) object));
			}
		}
		return result;
	}

	/*
	 *  END-MOD Paco 30-10-2002.
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  object  Description of Parameter
	 * @return         Description of the Returned Value
	 */
	/*
	 *  MOD Paco 30-10-2002. Tener en cuenta nulos
	 *  private int convertToInt(Object object) {
	 *  int result = -1;
	 *  CtsqlInteger intvalue = CtsqlFactory.getFactory().getNewInteger(null);
	 *  if (object instanceof java.lang.String) {
	 *  result = intvalue.setAsString((String) object);
	 *  } else if (object instanceof java.lang.Integer) {
	 *  result = intvalue.setAsInt(((Integer) object).intValue());
	 *  } else if (object instanceof java.lang.Short) {
	 *  result = intvalue.setAsShort(((Short) object).shortValue());
	 *  } else if (object instanceof java.lang.Double) {
	 *  result = intvalue.setAsDouble(((Double) object).doubleValue());
	 *  } else if (object instanceof java.math.BigDecimal) {
	 *  result = intvalue.setAsBigDecimal((java.math.BigDecimal) object);
	 *  }
	 *  return result;
	 *  }
	 */
	private Integer convertToInt(Object object) {
		Integer result = null;

		if (object != null) {
			CtsqlInteger intvalue = CtsqlFactory.getFactory().getNewInteger(null);
			if (object instanceof java.lang.String) {
				result = new Integer(intvalue.setAsString((String) object));
			} else if (object instanceof java.lang.Integer) {
				result = new Integer(intvalue.setAsInt(((Integer) object).intValue()));
			} else if (object instanceof java.lang.Short) {
				result = new Integer(intvalue.setAsShort(((Short) object).shortValue()));
			} else if (object instanceof java.lang.Double) {
				result = new Integer(intvalue.setAsDouble(((Double) object).doubleValue()));
			} else if (object instanceof java.math.BigDecimal) {
				result = new Integer(intvalue.setAsBigDecimal((java.math.BigDecimal) object));
			}
		}
		return result;
	}

	/*
	 *  END-MOD Paco 30-10-2002.
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  object            Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	/*
	 *  MOD Paco 30-10-2002. Tener en cuenta nulos
	 *  private java.sql.Time convertToTime(Object object) throws SQLException {
	 *  Calendar cal = Calendar.getInstance(Locale.ITALY);
	 *  CtsqlTime timevalue = CtsqlFactory.getFactory().getNewTime(null);
	 *  try {
	 *  if (object instanceof java.lang.String) {
	 *  cal = timevalue.setAsString((String) object);
	 *  }
	 *  } catch (CtsqlException ex) {
	 *  ExceptionManager.getManager().throwException(ex);
	 *  }
	 *  return new java.sql.Time(cal.getTime().getTime());
	 *  ;
	 *  }
	 */
	private java.sql.Time convertToTime(Object object) throws SQLException {
		Calendar cal = Calendar.getInstance(Locale.ITALY);

		try {
			if (object == null) {
				return null;
			}
			CtsqlTime timevalue = CtsqlFactory.getFactory().getNewTime(null);
			if (object instanceof java.lang.String) {
				cal = timevalue.setAsString((String) object);
			}
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}
		return new java.sql.Time(cal.getTime().getTime());
	}

	/*
	 *  END-MOD Paco 30-10-2002.
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  object  Description of Parameter
	 * @return         Description of the Returned Value
	 */
	/*
	 *  MOD Paco 30-10-2002. Tener en cuenta nulos
	 *  private double convertToDouble(Object object) {
	 *  double result = -1;
	 *  CtsqlDecimal decvalue = CtsqlFactory.getFactory().getNewDecimal(null);
	 *  if (object instanceof java.lang.String) {
	 *  result = decvalue.setAsString((String) object);
	 *  } else if (object instanceof java.lang.Integer) {
	 *  result = decvalue.setAsInteger(((Integer) object).intValue());
	 *  } else if (object instanceof java.lang.Short) {
	 *  result = decvalue.setAsShort(((Short) object).shortValue());
	 *  } else if (object instanceof java.lang.Double) {
	 *  result = decvalue.setAsDouble(((Double) object).doubleValue());
	 *  } else if (object instanceof java.math.BigDecimal) {
	 *  result = decvalue.setAsBigDecimal((java.math.BigDecimal) object);
	 *  }
	 *  return result;
	 *  }
	 */
	private Double convertToDouble(Object object) {
		Double result = null;

		if (object != null) {
			CtsqlDecimal decvalue = CtsqlFactory.getFactory().getNewDecimal(null);

			if (object instanceof java.lang.String) {
				result = new Double(decvalue.setAsString((String) object));
			} else if (object instanceof java.lang.Integer) {
				result = new Double(decvalue.setAsInteger(((Integer) object).intValue()));
			} else if (object instanceof java.lang.Short) {
				result = new Double(decvalue.setAsShort(((Short) object).shortValue()));
			} else if (object instanceof java.lang.Double) {
				result = new Double(decvalue.setAsDouble(((Double) object).doubleValue()));
			} else if (object instanceof java.math.BigDecimal) {
				result = new Double(decvalue.setAsBigDecimal((java.math.BigDecimal) object));
				/*
				 *  NEW Paco 28-1-2003
				 */
			} else if (object instanceof java.lang.Long) {
				result = new Double(decvalue.setAsLong(((java.lang.Long) object).longValue()));
			} else if (object instanceof java.lang.Float) {
				result = new Double(decvalue.setAsFloat(((java.lang.Float) object).floatValue()));
			}
		}
		/*
		 *  END-NEW Paco 28-1-2003
		 */
		return result;
	}

	/*
	 *  END-MOD Paco 30-10-2002.
	 */
	/**
	 *  Description of the Method
	 *
	 * @param  object            Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	/*
	 *  MOD Paco 30-10-2002. Tener en cuenta nulos
	 *  private java.sql.Date convertToDate(Object object) throws SQLException {
	 *  Calendar cal = Calendar.getInstance(Locale.ITALY);
	 *  CtsqlDate datevalue = CtsqlFactory.getFactory().getNewDate(null);
	 *  try {
	 *  if (object instanceof java.lang.String) {
	 *  cal = datevalue.setAsString((String) object);
	 *  }
	 *  } catch (CtsqlException ex) {
	 *  ExceptionManager.getManager().throwException(ex);
	 *  }
	 *  return new java.sql.Date(cal.getTime().getTime());
	 *  }
	 */
	private java.sql.Date convertToDate(Object object) throws SQLException {
		Calendar cal = null;

		try {
			if (object == null) {
				return null;
			}

			if (object instanceof java.lang.String) {
				CtsqlDate datevalue = CtsqlFactory.getFactory().getNewDate(null);
				cal = datevalue.setAsString((String) object);
			} else
				cal = Calendar.getInstance(Locale.ITALY);
				if (object instanceof java.util.Date) {
				cal.setTime((java.util.Date) object);
			}
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}
		return new java.sql.Date(cal.getTime().getTime());
	}

	/*
	 *  END-MOD Paco 30-10-2002.
	 */
// New Eva 18-04-2001

	/**
	 *  Description of the Method
	 *
	 * @param  object            Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	/*
	 *  MOD Paco 30-10-2002. Tener en cuenta nulos
	 *  private java.sql.Timestamp convertToDateTime(Object object) throws SQLException {
	 *  Calendar cal = Calendar.getInstance(Locale.ITALY);
	 *  CtsqlDateTime datetimevalue = CtsqlFactory.getFactory().getNewDateTime(null);
	 *  try {
	 *  if (object instanceof java.lang.String) {
	 *  cal = datetimevalue.setAsString((String) object);
	 *  }
	 *  } catch (CtsqlException ex) {
	 *  ExceptionManager.getManager().throwException(ex);
	 *  }
	 *  return new java.sql.Timestamp(cal.getTime().getTime());
	 *  }
	 */
	private java.sql.Timestamp convertToDateTime(Object object) throws SQLException {
		Calendar cal = Calendar.getInstance(Locale.ITALY);
		CtsqlDateTime datetimevalue = CtsqlFactory.getFactory().getNewDateTime(null);

		try {
			if (object == null) {
				return null;
			}
			if (object instanceof java.lang.String) {
				cal = datetimevalue.setAsString((String) object);
			}
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}
		return new java.sql.Timestamp(cal.getTime().getTime());
	}

	/*
	 *  END-MOD Paco 30-10-2002.
	 */
// END New Eva 18-04-2001

	/**
	 *  Description of the Method
	 *
	 * @param  object            Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	private byte[] convertToBinary(Object object) throws SQLException {
		CtsqlBinary ctsqlBinary = CtsqlFactory.getFactory().getNewBinary(null);
		byte[] buf = null;
		try {
			if (object instanceof java.lang.String) {
				buf = ctsqlBinary.setAsString((String) object);
			}
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}
		return buf;
	}

	// Nuevos métodos JDK 1.4

// JDK 1.4	
//	public ParameterMetaData getParameterMetaData() throws SQLException {
//		// TODO getParameterMetaData() method is not supported.
//		ExceptionManager.getManager().throwException(301, "getParameterMetaData method is not supported.");
//		// This statement will be never reached, but the compiler needs it.
//		return null;
//	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		// TODO setURL( int ) method is not supported.
		ExceptionManager.getManager().throwException(301, "setURL method is not supported.");
	}

}