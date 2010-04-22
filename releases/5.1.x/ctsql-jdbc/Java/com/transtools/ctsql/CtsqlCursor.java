/*
 *  Copyright 2002
 *
 *  TransTOOLs, S.A.
 *  All rights reserved
 */
package com.transtools.ctsql;

import java.sql.SQLWarning;

/**
 *  Description of the Interface
 *
 * @author     eva
 * @created    May 21, 2001
 * @version    $Revision: 1.16 $
 */
public interface CtsqlCursor {
// New Eva 22-02-2001
	/**
	 *  Description of the Field
	 */
	public final static short ISFIRST = 0;
	/**
	 *  Description of the Field
	 */
	public final static short ISLAST = 1;
	/**
	 *  Description of the Field
	 */
	public final static short ISNEXT = 2;
	/**
	 *  Description of the Field
	 */
	public final static short ISPREV = 3;
// END New Eva 22-02-2001
	/**
	 *  Description of the Field
	 */
	public final static int NONORETUPLES = CtsqlConstants.SQLNOTFOUND;


	/**
	 *  Description of the Method
	 *
	 * @param  hosts               Description of Parameter
	 * @exception  CtsqlException  Description of Exception
	 */
	public void open(CtsqlType hosts[]) throws CtsqlException;


	/**
	 *  Description of the Method
	 *
	 * @param  statement           Description of Parameter
	 * @exception  CtsqlException  Description of Exception
	 */
	public void prepare(String statement) throws CtsqlException;


	/**
	 *  Description of the Method
	 *
	 * @param  hosts               Description of Parameter
	 * @exception  CtsqlException  Description of Exception
	 */
	public void execute(CtsqlType hosts[]) throws CtsqlException;


	/**
	 *  Description of the Method
	 *
	 * @exception  CtsqlException  Description of Exception
	 */
	public void fetch() throws CtsqlException;


// New Eva 22-02-2001

//	public void longfetch(short mode) throws CtsqlException;

// END New Eva 22-02-2001

	/**
	 *  Description of the Method
	 *
	 * @param  mode                Description of Parameter
	 * @return                     <OJO - Put here the return value description>
	 * @exception  CtsqlException  Description of Exception
	 */
	public boolean fetchMode(short mode) throws CtsqlException;


	/**
	 *  Description of the Method
	 *
	 * @param  idx                 Description of Parameter
	 * @return                     Description of the Returned Value
	 * @exception  CtsqlException  Description of Exception
	 */
	public boolean scrollFetch(int idx) throws CtsqlException;


	/**
	 *  Description of the Method
	 *
	 * @exception  CtsqlException  Description of Exception
	 */
	public void close() throws CtsqlException;


	/**
	 *  Description of the Method
	 *
	 * @exception  CtsqlException  Description of Exception
	 */
	public void release() throws CtsqlException;


	/**
	 *  Gets the SelectStatement attribute of the CtsqlCursor object
	 *
	 * @return                     The SelectStatement value
	 * @exception  CtsqlException  Description of Exception
	 */
	public boolean isSelectStatement() throws CtsqlException;


	/**
	 *  Gets the ColumnCount attribute of the CtsqlCursor object
	 *
	 * @return    The ColumnCount value
	 */
	public int getColumnCount();


	/**
	 *  Gets the UpdateCount attribute of the CtsqlCursor object
	 *
	 * @return    The UpdateCount value
	 */
	public int getUpdateCount();


	/**
	 *  Gets the Stat attribute of the CtsqlCursor object
	 *
	 * @return    The Stat value
	 */
	public short getStat();


	/**
	 *  Gets the HostsNumber attribute of the CtsqlCursor object
	 *
	 * @return    The HostsNumber value
	 */
	public int getHostsNumber();


	/**
	 *  Gets the LastError attribute of the CtsqlCursor object
	 *
	 * @return    The LastError value
	 */
	public int getLastError();


	/**
	 *  Gets the LastErrorString attribute of the CtsqlCursor object
	 *
	 * @return    The LastErrorString value
	 */
	public String getLastErrorString();

	/**
	 *  Retrieves the first warning reported by calls on this Statement object.
	 *  Subsequent Statement object warnings will be chained to this SQLWarning
	 *  object.
	 *
	 * @return    The first SQLWarning object or null.
	 */
	public SQLWarning getWarnings();

	/**
	 *  Gets the Name attribute of the CtsqlCursor object
	 *
	 * @return    The Name value
	 */
	public String getName();


	/**
	 *  Sets the Name attribute of the CtsqlCursor object
	 *
	 * @param  name                The new Name value
	 * @exception  CtsqlException  Description of Exception
	 */
	public void setName(String name) throws CtsqlException;


	/**
	 *  Gets the designated colum of the current row as a CtsqlChar.
	 *
	 * @param  index               column index, first column is 0
	 * @return                     The CtsqlChar value
	 * @exception  CtsqlException  Description of Exception
	 */
	public CtsqlChar getCtsqlChar(int index) throws CtsqlException;

	/**
	 *  Updates the designated colum of the current row with a <code>CtsqlChar
	 *  </code> valuee.
	 *
	 * @param  index               column index, first column is 0
	 * @param  value               <code>CtsqlChar</code> value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public void setCtsqlChar(int index, CtsqlChar value) throws CtsqlException;

	/**
	 *  Updates the designated colum of the current row with a <code>CtsqlBinary
	 *  </code> valuee.
	 *
	 * @param  index               column index, first column is 0
	 * @param  value               <code>CtsqlBinary</code> value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public void setCtsqlBinary(int index, CtsqlBinary value) throws CtsqlException;

	/**
	 *  Updates the designated column with a <code>CtsqlDate</code> value.
	 *
	 * @param  index               the first column is 0, the second is 1, ...
	 * @param  value               the new column value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public void setCtsqlDate(int index, com.transtools.ctsql.CtsqlDate value) throws CtsqlException;

	/**
	 *  Updates the designated column with a <code>CtsqlDateTimeTime</code> value
	 *  object.
	 *
	 * @param  index               the first column is 0, the second is 1, ...
	 * @param  value               the new column value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public void setCtsqlDateTime(int index, com.transtools.ctsql.CtsqlDateTime value) throws CtsqlException;

	/**
	 *  Updates the designated column with a <code>setCtsqlTime</code> value.
	 *
	 * @param  index               the first column is 0, the second is 1, ...
	 * @param  value               the new column value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	public void setCtsqlTime(int index, com.transtools.ctsql.CtsqlTime value) throws CtsqlException;

	/**
	 *  Updates the designated column with a <code>CtsqlDecimal</code> value.
	 *
	 * @param  columnIndex         the first column is 0, the second is 1, ...
	 * @param  x                   the new column value
	 * @exception  CtsqlException  if a database access error occurs
	 */
	void setCtsqlDecimal(int columnIndex, CtsqlDecimal x) throws CtsqlException;

	/**
	 *  Gets the CtsqlDate attribute of the CtsqlCursor object
	 *
	 * @param  index               Description of Parameter
	 * @return                     The CtsqlDate value
	 * @exception  CtsqlException  Description of Exception
	 */
	public CtsqlDate getCtsqlDate(int index) throws CtsqlException;


	/**
	 *  Gets the CtsqlDateTime attribute of the CtsqlCursor object
	 *
	 * @param  index               Description of Parameter
	 * @return                     The CtsqlDateTime value
	 * @exception  CtsqlException  Description of Exception
	 */
	public CtsqlDateTime getCtsqlDateTime(int index) throws CtsqlException;


	/**
	 *  Gets the CtsqlBinary attribute of the CtsqlCursor object
	 *
	 * @param  index               Description of Parameter
	 * @return                     The CtsqlBinary value
	 * @exception  CtsqlException  Description of Exception
	 */
	public CtsqlBinary getCtsqlBinary(int index) throws CtsqlException;


	/**
	 *  Gets the CtsqlSmallint attribute of the CtsqlCursor object
	 *
	 * @param  index               Description of Parameter
	 * @return                     The CtsqlSmallint value
	 * @exception  CtsqlException  Description of Exception
	 */
	public CtsqlSmallint getCtsqlSmallint(int index) throws CtsqlException;


	/**
	 *  Gets the CtsqlInteger attribute of the CtsqlCursor object
	 *
	 * @param  index               Description of Parameter
	 * @return                     The CtsqlInteger value
	 * @exception  CtsqlException  Description of Exception
	 */
	public CtsqlInteger getCtsqlInteger(int index) throws CtsqlException;


	/**
	 *  Gets the CtsqlTime attribute of the CtsqlCursor object
	 *
	 * @param  index               Description of Parameter
	 * @return                     The CtsqlTime value
	 * @exception  CtsqlException  Description of Exception
	 */
	public CtsqlTime getCtsqlTime(int index) throws CtsqlException;


	/**
	 *  Gets the CtsqlDecimal attribute of the CtsqlCursor object
	 *
	 * @param  index               Description of Parameter
	 * @return                     The CtsqlDecimal value
	 * @exception  CtsqlException  Description of Exception
	 */
	public CtsqlDecimal getCtsqlDecimal(int index) throws CtsqlException;


	/**
	 *  Description of the Method
	 *
	 * @param  name                Description of Parameter
	 * @return                     Description of the Returned Value
	 * @exception  CtsqlException  Description of Exception
	 */
	public int findColumn(String name) throws CtsqlException;


	/**
	 *  Gets the ColumnType attribute of the CtsqlCursor object
	 *
	 * @param  index  Description of Parameter
	 * @return        The ColumnType value
	 */
	public int getColumnType(int index);


	/**
	 *  Gets the ColumnLength attribute of the CtsqlCursor object
	 *
	 * @param  index  Description of Parameter
	 * @return        The ColumnLength value
	 */
	public int getColumnLength(int index);


	/**
	 *  Gets the ColumnDisplaySize attribute of the CtsqlCursor object
	 *
	 * @param  index  Description of Parameter
	 * @return        The ColumnDisplaySize value
	 */
	public int getColumnDisplaySize(int index);


	/**
	 *  Gets the ColumnTypeName attribute of the CtsqlCursor object
	 *
	 * @param  index  Description of Parameter
	 * @return        The ColumnTypeName value
	 */
	public String getColumnTypeName(int index);


	/**
	 *  Gets the ColumnLabel attribute of the CtsqlCursor object
	 *
	 * @param  index  Description of Parameter
	 * @return        The ColumnLabel value
	 */
	public String getColumnLabel(int index);


	/**
	 *  Gets the ColumnName attribute of the CtsqlCursor object
	 *
	 * @param  index  Description of Parameter
	 * @return        The ColumnName value
	 */
	public String getColumnName(int index);


	/**
	 *  Gets the TableName attribute of the CtsqlCursor object
	 *
	 * @param  index  Description of Parameter
	 * @return        The ColumnName value
	 */
	public String getTableName(int index);


	/**
	 *  Gets the Precision attribute of the CtsqlCursor object
	 *
	 * @param  index  Description of Parameter
	 * @return        The Precision value
	 */
	public int getPrecision(int index);


	/**
	 *  Gets the Scale attribute of the CtsqlCursor object
	 *
	 * @param  index  Description of Parameter
	 * @return        The Scale value
	 */
	public int getScale(int index);


	/**
	 *  Gets the Open attribute of the CtsqlCursor object
	 *
	 * @return    The Open value
	 */
	public boolean isOpen();

	/**
	 *  Gets the Row attribute of the CtsqlCursor object
	 *
	 * @return    The Row value
	 */
	public int getRow();

	/**
	 *  Gets the IsLast attribute of the CtsqlCursor object
	 *
	 * @return    The IsLast value
	 */
	public boolean getIsLast();

	/**
	 *  Gets the IsAfterLast attribute of the CtsqlStmt object
	 *
	 * @return    The IsLast value
	 */
	public boolean getIsAfterLast();

	/**
	 *  Updates the underlying database with the new contents of the current row of
	 *  this <code>CtsqlCursor</code> object. This method cannot be called when the
	 *  cursor is on the insert row. This is a JDBC 2.0 feature.
	 *
	 * @param  tabname             cursor tablename, it must be the same table than
	 *      the used in the select statement
	 * @exception  CtsqlException  if a database access error occurs or if this
	 *      method is called when the cursor is on the insert row
	 */
	public void update(String tabname) throws CtsqlException;

	/**
	 *  Inserts the contents of the insert row into the database. The cursor must
	 *  be on the insert row when this method is called.
	 *
	 * @param  tabname             cursor tablename, it must be the same table than
	 *      the used in the select statement
	 * @exception  CtsqlException  if a database access error occurs or if this
	 *      method is called when the cursor is not on the insert row
	 */
	public void insert(String tabname) throws CtsqlException;

	/**
	 *  Deletes the underlying database current row of this <code>CtsqlCursor</code>
	 *  object. This method cannot be called when the cursor is on the insert row.
	 *
	 * @param  tabname             cursor tablename, it must be the same table than
	 *      the used in the select statement
	 * @exception  CtsqlException  if a database access error occurs or if this
	 *      method is called when the cursor is on the insert row
	 */
	public void delete(String tabname) throws CtsqlException;


	/**
	 *  Moves the cursor to the insert row. The current cursor position is
	 *  remembered while the cursor is positioned on the insert row. The insert row
	 *  is a special row associated with an updatable result set. It is essentially
	 *  a buffer where a new row may be constructed by calling the updateXXX
	 *  methods prior to inserting the row into the result set. Only the updateXXX,
	 *  getXXX, and insertRow methods may be called when the cursor is on the
	 *  insert row. All of the columns in a result set must be given a value each
	 *  time this method is called before calling insertRow. An updateXXX method
	 *  must be called before a getXXX method can be called on a column value.
	 */
	public void moveToInsertRow();

	/**
	 *  Gets serial value asignated in last insert statement.
	 *
	 * @return                     <OJO - Revisar> The serial value.
	 * @exception  CtsqlException  <OJO - Put here the exception description>
	 */
	public int getSerial() throws CtsqlException;

	/**
	 * This method must be called only after an insert operation, and returns the rowid 
	 * value of the inserted row.
	 * @return The rowid value after insert.
	 */
	public int getRowIdAfterInsert();


	/**
	 *  Returns true if the statement associated has returned a serial value.
	 *
	 * @return                     <OJO - Put here the return value description>
	 * @exception  CtsqlException  <OJO - Put here the exception description>
	 */
	public boolean hasSerial() throws CtsqlException;

}
