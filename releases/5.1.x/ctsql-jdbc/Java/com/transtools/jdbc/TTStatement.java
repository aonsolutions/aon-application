/*
 *  Copyright 2001
 *
 *  TransTOOLs, S.A.
 *  All rights reserved
 */
package com.transtools.jdbc;
import com.transtools.ctsql.CtsqlCursor;
import com.transtools.ctsql.CtsqlException;
import com.transtools.ctsql.CtsqlFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 *  TTStatement class implements Statement. A statement object used for
 *  executing a static SQL statement and obtaining the results produced by it.
 *
 * @author     eva
 * @created    April 5, 2001
 * @version    $Revision: 1.16 $
 */

public class TTStatement implements Statement {

	private final static int MAX_FIELD_SIZE = 32767;
// New 23-03-2001
	private int typeResultSet = ResultSet.TYPE_FORWARD_ONLY;
	private int typeConcurrency = ResultSet.CONCUR_READ_ONLY;

	/**
	 *  connection is a TTConnection object. TTConnection provides a connection
	 */
	private TTConnection connection;
	/*
	 *  NEW Eva 5-4-2001
	 */
	private String cursorName = null;
	/*
	 *  Eva 5-4-2001
	 */
	/**
	 *  ctsqlCursor is a CtsqlCursor object which prepare and execute statements.
	 */
//	Eva 5-4-2001 private CtsqlCursor ctsqlCursor;
	private int nRows;

	private String myStatement;

	/**
	 *  resultSet is a ResultSet object. ResultSet is the current resultSet and
	 *  containts all of the row which satisfied the conditions in a SQL statement.
	 */
	private ResultSet resultSet;

	protected int lastGeneratedSerial = -1;
	protected boolean generatedSerial = false;
	protected int rowIdAfterInsert = -1;


//	private static Vector extent;

// END New 23-03-2001

	/**
	 *  Constructor: receives a connection.
	 *
	 * @param  aConnection  Description of Parameter
	 */
	public TTStatement(TTConnection aConnection) {
//		extent.add(this);
		connection = aConnection;
		nRows = 0;
// Eva 5-4-2001		ctsqlCursor = CtsqlFactory.getFactory().getNewCursor(aConnection.getCtsqlServer());
	}


// New Eva 23-03-2001

	/**
	 *  Constructor for the TTStatement object
	 *
	 * @param  aConnection           Description of Parameter
	 * @param  resultSetType         Description of Parameter
	 * @param  resultSetConcurrency  Description of Parameter
	 */
	public TTStatement(TTConnection aConnection, int resultSetType, int resultSetConcurrency) {

		connection = aConnection;
// Eva 5-4-2001	ctsqlCursor = CtsqlFactory.getFactory().getNewCursor(aConnection.getCtsqlServer());
		nRows = 0;
		typeConcurrency = resultSetConcurrency;
		typeResultSet = resultSetType;
	}


	/**
	 *  Sets the TypeResultSet attribute of the TTStatement object
	 *
	 * @param  typeResultSet  The new TypeResultSet value
	 */
	public void setTypeResultSet(int typeResultSet) {
		this.typeResultSet = typeResultSet;
	}


	/**
	 *  Sets the TypeConcurrency attribute of the TTStatement object
	 *
	 * @param  typeConcurrency  The new TypeConcurrency value
	 */
	public void setTypeConcurrency(int typeConcurrency) {
		this.typeConcurrency = typeConcurrency;
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
	 * @exception  SQLException  <OJO - Put here the exception description>
	 */

	/*
	 *  Eva 5-4-2001
	 *  public void setCursorName(String name) throws SQLException {
	 *  try {
	 *  ctsqlCursor.setName(name);
	 *  }
	 *  catch (CtsqlException ex) {
	 *  ExceptionManager.getManager().throwException(ex);
	 *  }
	 *  }
	 */
	public void setCursorName(String name) throws SQLException {
		this.cursorName = name;
	}


	/*
	 *  END Eva 5-4-2001
	 */
	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @param  direction  The new FetchDirection value
	 */
	public void setFetchDirection(int direction) {
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
	 *  Sets the MyStatement attribute of the TTStatement object
	 *
	 * @param  stmt  The new MyStatement value
	 */
	public void setMyStatement(String stmt) {
		myStatement = new String(stmt);
		/*
		 *  if(connection.getConvert()==TTConnection.CNT_CONVERTANSITOOEM)
		 *  myStatement=oemconv.AnsiToOem(stmt);
		 *  if(connection.getConvert()==TTConnection.CNT_CONVERTOEMTOANSI)
		 *  myStatement=oemconv.OemToAnsi(stmt);
		 */
	}


	/**
	 *  Gets the MyStatement attribute of the TTStatement object
	 *
	 * @return    The MyStatement value
	 */
	public String getMyStatement() {
		if((getResultSetConcurrency() == ResultSet.CONCUR_UPDATABLE) &&
		   (myStatement.toLowerCase().indexOf("for update") == -1))
		{
			return myStatement.concat(" FOR UPDATE NOWAIT");
		}
		else{
			return myStatement;
		}
	}


	/**
	 *  getResultSet method returns the current result as a ResultSet object.
	 *
	 * @return                   the current ResultSet
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 * @jdbc.pending             Probar qué pasa si se llama a este método más de
	 *      una vez. Mirar la especificación de java.
	 */
	public ResultSet getResultSet() throws SQLException {
		((TTResultSet) resultSet).setStatement(this);
		return resultSet;
	}


	/**
	 *  getUpdateCount method calls CtsqlCursor.getUpdateCount
	 *
	 * @return                   the current result as an integer;
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 * @jdbc.pending             Queda ver el valor que debe devolver en el caso
	 *      que no se haga la sentencia apropiada.
	 */
	public int getUpdateCount() throws SQLException {
//		return ctsqlCursor.getUpdateCount();
		return nRows;
	}


	/**
	 *  getQueryTimeout method is not supported.
	 *
	 * @return                   The QueryTimeout value
	 * @exception  SQLException  Description of Exception
	 */
	public int getQueryTimeout() throws SQLException {
		ExceptionManager.getManager().throwException(301, "getQueryTimeout method is not supported.");
		return 0;
		// This statement will be never reached, but the compiler needs it.
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
	 *  Retrieves the first warning reported by calls on this Statement object. Subsequent Statement object warnings will be chained to this SQLWarning object. The warning chain is automatically cleared each time a statement is (re)executed.
	 *
	 * @return                   The first SQLWarning object or null.
	 */
	public SQLWarning getWarnings() {
		return ((TTResultSet) resultSet).getWarnings();
	}


	/**
	 *  getMoreResults method is not supported.
	 *
	 * @return                   The MoreResults value
	 * @exception  SQLException  Description of Exception
	 */
	public boolean getMoreResults() throws SQLException {
		return getMoreResults(0);
	}


	/**
	 * Retrieves the result set concurrency for ResultSet objects generated by this Statement object.
	 *
	 * @return    Either ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE.
	 */
	public int getResultSetConcurrency() {
		return typeConcurrency;
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @return    The Connection value
	 */
	public Connection getConnection() {
		return connection;
		// return null;
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


	/**
	 *  Retrieves the result set type for ResultSet objects generated by this Statement object.
	 *
	 * @return    One of ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE.
	 */
	public int getResultSetType() {
		return typeResultSet;
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 * @return    The FetchDirection value
	 */
	public int getFetchDirection() {
		return 0;
	}


// EVA 16-03-2001

	/**
	 *  Gets the CtsqlCursor attribute of the TTStatement object
	 *
	 * @param  stmt              Description of Parameter
	 * @return                   The CtsqlCursor value
	 * @exception  SQLException  Description of Exception
	 */
	/*
	 *  Eva 5-4-2001
	 *  public CtsqlCursor getCtsqlCursor() {
	 *  return ctsqlCursor;
	 *  }
	 *  END Eva 5-4-2001
	 */
	/**
	 *  Gets the CtsqlCursor attribute of the TTStatement object
	 *
	 *  Gets the CtsqlCursor attribute of the TTStatement object execute method
	 *  executes an SQL statement that may return multiple results. If the
	 *  statement is a select create a TTResultSet If the statement is not a select
	 *  calls CtsqlCursor.prepare and CtsqlCursor.execute
	 *
	 * @param  stmt              Description of Parameter
	 * @return                   The CtsqlCursor value
	 * @return                   The CtsqlCursor value
	 * @return                   true if the statement is Select or false in other
	 *      case
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public synchronized boolean execute(String stmt) throws SQLException {
		generatedSerial = false;
		boolean isSelectStatement = false;
		/*
		 *  Eva 5-4-2001
		 */
		CtsqlCursor ctsqlCursor = CtsqlFactory.getFactory().getNewCursor(this.connection.getCtsqlServer());
		/*
		 *  END Eva 5-4-2001
		 */
		try {
			setMyStatement(stmt);
			ctsqlCursor.prepare(myStatement);
			isSelectStatement = ctsqlCursor.isSelectStatement();
// New Eva 6-4-2001
			if (cursorName != null) {
				ctsqlCursor.setName(cursorName);
			}
// END Eva 6-4-2001
			if (isSelectStatement) {
// MOD Eva 15-03-2001
//				resultSet = new TTResultSet(connection, ctsqlCursor);
				resultSet = new TTResultSet(connection, ctsqlCursor, this);
				nRows = -1;
// End New Eva 15-03-2001
			} else {
				ctsqlCursor.execute(null);

				setLastGeneratedSerial(-1);
				try {
		  			if(ctsqlCursor.hasSerial()){
						setLastGeneratedSerial(ctsqlCursor.getSerial());
		  			}
				} catch ( Exception e ) {
				}

				// Guardar valores
				nRows = ctsqlCursor.getUpdateCount();
				setRowIdAfterInsert(ctsqlCursor.getRowIdAfterInsert());
				ctsqlCursor.release();
			}
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}

		return isSelectStatement;
	}


	/**
	 *  executeQuery method create a TTResultSet object that executes an SQL
	 *  statement that returns a single ResultSet object.
	 *
	 * @param  stmt              represents the statement that will be execute
	 * @return                   a ResultSet object that contains the data produced
	 *      by the given query
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public synchronized ResultSet executeQuery(String stmt) throws SQLException {
		generatedSerial = false;
		/*
		 *  Eva 5-4-2001
		 */
		CtsqlCursor ctsqlCursor = CtsqlFactory.getFactory().getNewCursor(this.connection.getCtsqlServer());
		/*
		 *  END Eva 5-4-2001
		 */
		try {
			setMyStatement(stmt);
			ctsqlCursor.prepare(getMyStatement());
// New Eva 6-4-2001
			if (cursorName != null) {
				ctsqlCursor.setName(cursorName);
			}
// END Eva 6-4-2001
		} catch (CtsqlException ex) {
			//           System.out.println(ctsqlCursor.getLastErrorString());
			ExceptionManager.getManager().throwException(ex);
		}
// MOD Eva 15-03-2001
//        	resultSet = new TTResultSet(this.connection, ctsqlCursor);
		resultSet = new TTResultSet(this.connection, ctsqlCursor, this);
// End MOD Eva 15-03-2001                        }
		((TTResultSet) resultSet).setStatement(this);
		return resultSet;
	}


// New Eva 15-03.2001
	/**
	 *  Description of the Method
	 *
	 * @param  stmt              Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public synchronized ResultSet executeQueryCatalog(String stmt) throws SQLException {
		generatedSerial = false;
		/*
		 *  Eva 5-4-2001
		 */
		CtsqlCursor ctsqlCursor = CtsqlFactory.getFactory().getNewCursor(this.connection.getCtsqlServer());
		/*
		 *  END Eva 5-4-2001
		 */
		try {
			setMyStatement(stmt);
			ctsqlCursor.prepare(myStatement);
// New Eva 6-4-2001
			if (cursorName != null) {
				ctsqlCursor.setName(cursorName);
			}
// END Eva 6-4-2001
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}
// MOD Eva 15-03-2001
//        	resultSet = (ResultSet)new TTResultSetCatalog(this.connection, ctsqlCursor);
		resultSet = new TTResultSetCatalog(this.connection, ctsqlCursor, this);
// End MOD Eva 15-03-2001                        }
		((TTResultSet) resultSet).setStatement(this);
		// Probes
		return resultSet;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  stmt              Description of Parameter
	 * @return                   Description of the Returned Value
	 * @exception  SQLException  Description of Exception
	 */
	public synchronized boolean executeCatalog(String stmt) throws SQLException {
		generatedSerial = false;
		boolean isSelectStatement = false;
		/*
		 *  Eva 5-4-2001
		 */
		CtsqlCursor ctsqlCursor = CtsqlFactory.getFactory().getNewCursor(this.connection.getCtsqlServer());
		/*
		 *  END Eva 5-4-2001
		 */
		try {
			setMyStatement(stmt);
			ctsqlCursor.prepare(myStatement);
// New Eva 6-4-2001
			if (cursorName != null) {
				ctsqlCursor.setName(cursorName);
			}
// END Eva 6-4-2001
			isSelectStatement = ctsqlCursor.isSelectStatement();
			if (isSelectStatement) {
// MOD Eva 15-03-2001
//				resultSet = new TTResultSetCatalog(connection, ctsqlCursor);
				resultSet = new TTResultSetCatalog(connection, ctsqlCursor, this);
// End MOD Eva 15-03-2001                        }

			} else {
				ctsqlCursor.execute(null);
			}
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}

		return isSelectStatement;
	}


// END Eva 15-03.2001

	/**
	 *  executeUpdate method executes an SQL INSERT, UPDATE or DELETE statement.
	 *  Calls CtsqlCursor.prepare and CtsqlCursor.execute
	 *
	 * @param  stmt              represents the statement that will be execute
	 * @return                   the row count for INSERT, UPDATE or DELETE
	 *      statements, or 0 for select statements
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 * @jdbc.pending             Queda chequear qué devuelve CtsqlCursor por
	 *      defecto cuando la sentencia no sea un Insert, update, delete.
	 */
	public synchronized int executeUpdate(String stmt) throws SQLException {		
		generatedSerial = false;
		
		if(stmt.trim().toLowerCase().startsWith("putenv")){
			String s = stmt.trim().substring("putenv".length()).trim();
			try {
				this.connection.getCtsqlServer().putEnv(s);
			} catch (CtsqlException e) {
				ExceptionManager.getManager().throwException(e);
			}
			return 0;
		}
		/*
		 *  Eva 5-4-2001
		 */
		CtsqlCursor ctsqlCursor = CtsqlFactory.getFactory().getNewCursor(this.connection.getCtsqlServer());
		/*
		 *  END Eva 5-4-2001
		 */
		try {
			setMyStatement(stmt);
			ctsqlCursor.prepare(myStatement);
// New Eva 6-4-2001
			if (cursorName != null) {
				ctsqlCursor.setName(cursorName);
			}
// END Eva 6-4-2001
			ctsqlCursor.execute(null);

			try {
				setLastGeneratedSerial(ctsqlCursor.getSerial());
			} catch ( Exception e ) {
				generatedSerial = false;
			}

			// Guardar valores
			nRows = ctsqlCursor.getUpdateCount();
			setRowIdAfterInsert(ctsqlCursor.getRowIdAfterInsert());
			ctsqlCursor.release();
		} catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}
		return nRows;
	}


	public int getSerial() throws SQLException{
		if ( !generatedSerial ) {
			ExceptionManager.getManager().throwException(301, "Bad access to serial value.");
		}
		return lastGeneratedSerial;
	}
	
	private void setLastGeneratedSerial(int serial){
		if(lastGeneratedSerial == -1){
			generatedSerial = false;
		}else{
			generatedSerial = true;
		}
		this.lastGeneratedSerial = serial;
	}

	/**
	 * This method must be called only after an insert operation, and returns the rowid 
	 * value of the inserted row. If this method is called after any other operation the 
	 * value will be indeterminate.
	 * @return The rowid value after insert.
	 */
	public int getRowIdAfterInsert(){
		return rowIdAfterInsert;
	}

	protected void setRowIdAfterInsert(int rowIdAfterInsert){
		this.rowIdAfterInsert = rowIdAfterInsert;
	}

	/**
	 *  close method calls CtsqlCursor.release that releases the Statement object's
	 *  database
	 *
	 * @exception  SQLException  Description of Exception
	 * @jdbc.jcosmos             Necesario de implementar para JCosmos.
	 * @jdbc.pending             Ver si es necesario cerrar el resultSet asociado
	 *      cuando se cierre la sentencia.
	 */
	public synchronized void close() throws SQLException {
		try {
// New Eva 5-4-2001
			if (resultSet != null) {
				resultSet.close();
				/*
				 *  Optimizamos el uso de memoria destruyendo el cursor
				 *  ya que tras el close del statement este ya no puede ser utilizado
				 */
//  			ctsqlCursor.release();
				((TTResultSet)resultSet).getCursor().release();
				resultSet = null;
			}
// END New Eva 5-4-2001
		} catch (CtsqlException ex) {
			// Si el servidor ya no está conectado se captura el error.
			if(ex.getType() != CtsqlException.SERVER_NOT_CONNECTED){
				ExceptionManager.getManager().throwException(ex);
			}
		}
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
//  	  System.out.println("finalize() -> statement: " + myStatement);
		close();
	}
// END New Eva 23-03-2001

// Nuevos métodos JDK 1.4

	public boolean execute(String sql, int autoGeneratedKeys)
		throws SQLException {
		// TODO execute( String, int ) method is not supported.
		ExceptionManager.getManager().throwException(301, "execute method is not supported.");
		// This statement will be never reached, but the compiler needs it.
		return false;
	}

	public boolean execute(String sql, int[] columnIndexes)
		throws SQLException {
		// TODO execute( String, int[] ) method is not supported.
		ExceptionManager.getManager().throwException(301, "execute method is not supported.");
		// This statement will be never reached, but the compiler needs it.
		return false;
	}

	public boolean execute(String sql, String[] columnNames)
		throws SQLException {
			// TODO execute( String, String[] ) method is not supported.
			ExceptionManager.getManager().throwException(301, "execute method is not supported.");
			// This statement will be never reached, but the compiler needs it.
			return false;
	}

	public int executeUpdate(String sql, int autoGeneratedKeys)
		throws SQLException {
			// TODO executeUpdate( String, int ) method is not supported.
			ExceptionManager.getManager().throwException(301, "executeUpdate method is not supported.");
			// This statement will be never reached, but the compiler needs it.
			return -1;
	}

	public int executeUpdate(String sql, int[] columnIndexes)
		throws SQLException {
			// TODO executeUpdate( String, int[] ) method is not supported.
			ExceptionManager.getManager().throwException(301, "executeUpdate method is not supported.");
			// This statement will be never reached, but the compiler needs it.
			return -1;
	}

	public int executeUpdate(String sql, String[] columnNames)
		throws SQLException {
			// TODO executeUpdate( String, String[] ) method is not supported.
			ExceptionManager.getManager().throwException(301, "executeUpdate method is not supported.");
			// This statement will be never reached, but the compiler needs it.
			return -1;
	}

	public ResultSet getGeneratedKeys() throws SQLException {
		// TODO getGeneratedKeys() method is not supported.
		ExceptionManager.getManager().throwException(301, "getGeneratedKeys method is not supported.");
		// This statement will be never reached, but the compiler needs it.
		return null;
	}

	public boolean getMoreResults(int current) throws SQLException {
		close();
		return false;
	}

	public int getResultSetHoldability() throws SQLException {
		// TODO getResultSetHoldability() method is not supported.
		ExceptionManager.getManager().throwException(301, "getResultSetHoldability method is not supported.");
		// This statement will be never reached, but the compiler needs it.
		return -1;
	}

}
