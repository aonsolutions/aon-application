package com.transtools.jdbc;

import com.transtools.ctsql.CtsqlException;
import com.transtools.ctsql.CtsqlFactory;
import com.transtools.ctsql.CtsqlServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
// JDK 1.4
//import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
/**
 *  TTConnection class implements Connection and represents a connection with a
 *  database. This class includes the SQL statements that are executed and the
 *  results thats are returned.
 *
 *@author     eva
 *@created    March 7, 2001
 */

public class TTConnection implements Connection {

	/**
	 *  server is a CtsqlServer object. CtsqlServer provides a connection
	 */
	private CtsqlServer server;
	private int convert = 0;
	/**
	 *  Description of the Field
	 */
	public final static int CNT_CONVERTANSITOOEM = 1;
	/**
	 *  Description of the Field
	 */
	public final static int CNT_CONVERTOEMTOANSI = 2;

	private final static String DBUSER = "user";
	private final static String DBPASSWORD = "password";

	/**
	 * Is a D.B. transactional, values: -1 unknown, 0 - no, 1 - yes.
	 */
	private int transactional = -1;
	/**
	 * NEW Paco 20-10-2004
	 * Is autoCommit active, values: -1 unknown, 0 - no, 1 - yes.
	 */
	private int isAutoCommitActive = -1;
	
	/* URL parameters */
	private String host;
	private int port;
	private String dbName;
	private Properties props;
	
	/**
	 *@param  host              Description of Parameter
	 *@param  port              Description of Parameter
	 *@param  dbName            Description of Parameter
	 *@param  props             Description of Parameter
	 *@exception  SQLException  Description of Exception
	 *@jdbc.pending             Queda ver que ocurre en los siguientes casos: -
	 *      user y password sean nulos. ¿Quién debe chequear esto? - channel sea
	 *      nulo. ¿Qué debe ocurrir?
	 */

	/**
	 *  Constructor: receives host variables, port, name and properties that
	 *  connection needs.
	 *
	 *@param  host              Description of Parameter
	 *@param  port              Description of Parameter
	 *@param  dbName            Description of Parameter
	 *@param  props             Description of Parameter
	 *@exception  SQLException  Description of Exception
	 */

	public TTConnection(String host, int port, String dbName, Properties props) throws SQLException {
		this.host = host;
		this.port = port;
		this.dbName = dbName;
		this.props = props;
		
		CtsqlChannel channel = null;
		try {
			/*
			 *  MOD paco 18-12-2001. Stored Procedures
			 */
			String localConnection = props.getProperty("LOCALCONNECTION");

			if (localConnection != null) {
				if (localConnection.compareToIgnoreCase("TRUE") != 0) {
					channel = new CtsqlChannel(host, port);
				}
			}
			else {
				channel = new CtsqlChannel(host, port);
			}
			/*
			 *  END MOD paco 18-12-2001. Stored Procedures
			 */
			if (channel != null) {
				server = CtsqlFactory.getFactory().getNewServer();
				String user = props.getProperty(DBUSER);
				String password = props.getProperty(DBPASSWORD);
				String[] env = createEnviromentArray(props);
				String dbCharSet = props.getProperty("DBCHARSET");
				if (dbCharSet != null) {
					if (dbCharSet.equalsIgnoreCase("ANSI")) {
						convert = CNT_CONVERTANSITOOEM;
					}
					if (dbCharSet.equalsIgnoreCase("OEM")) {
						convert = CNT_CONVERTOEMTOANSI;
					}
				}
				String prop = props.getProperty("RTRIMCHAR");
				if (prop != null) {					
					server.setRTrimChar(!prop.equalsIgnoreCase("FALSE"));
				}
				server.setConvert(convert);
				server.connect(channel, user, password, dbName, env);
			}
			/*
			 *  MOD paco 18-12-2001. Stored Procedures
			 */
			else {
				if (localConnection.compareToIgnoreCase("TRUE") == 0) {
					server = CtsqlFactory.getFactory().getNewServer();
				}
			}
			/*
			 *  END MOD paco 18-12-2001. Stored Procedures
			 */
		}
		catch (UnknownHostException un) {
			ExceptionManager.getManager().throwException(CtsqlException.SERVER_NOT_CONNECTED, CtsqlException.getMessage(CtsqlException.SERVER_NOT_CONNECTED));
		}
		catch (IOException io) {
			ExceptionManager.getManager().throwException(CtsqlException.SERVER_NOT_CONNECTED, CtsqlException.getMessage(CtsqlException.SERVER_NOT_CONNECTED));
		}
		catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(ex);
		}
	}

	/**
	 * Sets this connection's auto-commit mode. If a connection is in auto-commit
	 * mode, then all its SQL statements will be executed and committed as
	 * individual transactions. Otherwise, its SQL statements are grouped into
	 * transactions that are terminated by a call to either the method commit or
	 * the method rollback. By default, new connections are in auto-commit mode.
	 * The commit occurs when the statement completes or the next execute occurs,
	 * whichever comes first.
	 *
	 *@param  autoCommit        true enables auto-commit; false disables auto-commit.
	 *@exception  SQLException  if a database access error occurs
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		boolean transactional = bdIsTransactional();
		if(autoCommit){
			if(transactional){
				// cerramos la transacción en curso.
				try{
					commit();
				}catch (SQLException ex){
					// Si no hay transacción en curso tanto mejor.
				}
			}
			isAutoCommitActive = 1;
		}else{
			if(!transactional){
				// Poría ser un warning.
				System.out.println("setAutoCommit off has no sense in non transactional databases.");
				isAutoCommitActive = 1;
			}else{
				beginWork();
				isAutoCommitActive = 0;
			}
		}
	}

	/**
	 *  setReadOnly method is not supported.
	 *
	 *@param  rd                The new ReadOnly value
	 *@exception  SQLException  Description of Exception
	 */
	public void setReadOnly(boolean rd) throws SQLException {
		ExceptionManager.getManager().throwException(301, "setReadOnly method is not supported.");
	}


	/**
	 *  setCatalog method is not supported.
	 *
	 *@param  catalog           The new Catalog value
	 *@exception  SQLException  Description of Exception
	 */
	public void setCatalog(String catalog) throws SQLException {
		ExceptionManager.getManager().throwException(301, "setCatalog method is not supported.");
	}


	/**
	 *  setTransactionIsolation method is not supported.
	 *
	 *@param  level             The new TransactionIsolation value
	 *@exception  SQLException  Description of Exception
	 */
	public void setTransactionIsolation(int level) throws SQLException {
		if(level != getTransactionIsolation()){
			ExceptionManager.getManager().throwException(301, "setTransactionIsolation method is not supported. ");
		}
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 *@param  map  The new TypeMap value
	 */
	public void setTypeMap(Map map) {
	}


	/**
	 *  Sets the channelServer attribute of the TTConnection object
	 *
	 *@param  input   The new channelServer value
	 *@param  output  The new channelServer value
	 */
	public void setChannelServer(InputStream input, OutputStream output) {
//		this.server.setChannel(aChannel);
		server.setChannel(input, output);
		server.setConnected();
	}


	/**
	 *  Gets the Convert attribute of the TTConnection object
	 *
	 *@return    The Convert value
	 */
	public int getConvert() {
		return convert;
	}


	/**
	 *  isClosed method calls the method CtsqlServer.isConnected that tests to see
	 *  if a connection is closed.
	 *
	 *@return          The Closed value
	 *@jdbc.jcosmos    Necesario de implementar para JCosmos.
	 */
	public boolean isClosed() {
		return !server.isConnected();
	}


	/**
	 *  getAutoCommit method is not supported.
	 *
	 *@return                   The AutoCommit value
	 *@exception  SQLException  Description of Exception
	 */
	public boolean getAutoCommit() throws SQLException {
		boolean ret = false;
		
		if (isAutoCommitActive == -1) {
			if (bdIsTransactional()) {
				isAutoCommitActive = 0;
			} else {
				isAutoCommitActive = 1;
			}
		}
		
		if (isAutoCommitActive == 1)
			ret = true;
		else
			ret = false;

		return ret;
	}


	/**
	 *  getMetaData gets the metadata regarding this connection's database. A
	 *  Connection's database is able to provide information describing its tables,
	 *  its supported SQL grammar, its stored procedures, the capabilities of this
	 *  connection, and so on. This information is made available through a
	 *  DatabaseMetaData object.
	 *
	 *@return                   a DatabaseMetaData object for this Connection
	 *@exception  SQLException  Description of Exception
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		return new TTDataBaseMetaData(this);
	}


	/**
	 *  isReadOnly tests to see if a Connection is closed.
	 *
	 *@return    true if the connection is closed; false in other case.
	 */
	public boolean isReadOnly() {
		return false;
	}


	/**
	 *  getReadOnly method is not supported.
	 *
	 *@return                   The Catalog value
	 *@exception  SQLException  Description of Exception
	 */
	public String getCatalog() throws SQLException {
		//ExceptionManager.getManager().throwException(301, "getCatalog method is not supported.");
		return null;
		// This statement will be never reached, but the compiler needs it.
	}

	private boolean bdIsTransactional() throws SQLException {
		if(transactional < 0){
			Statement stmt = createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM SYSTABLES WHERE TABNAME = 'syslog'");
			if (rs.next()) {
				transactional = 1;
			}else{
				transactional = 0;
			}
			rs.close();
			stmt.close();
		}
		return (transactional > 0);
	}

	private void beginWork() throws SQLException {
		Statement stmt = createStatement();
		stmt.executeUpdate("BEGIN WORK");
		stmt.close();
	}


	/**
	 *  getTransactionIsolation gets this Connection's current transaction
	 *  isolation level.
	 *
	 *@return                   the current transactionIsolation mode value
	 *@exception  SQLException  Description of Exception
	 */
	public int getTransactionIsolation() throws SQLException {
		int isolationLevel = Connection.TRANSACTION_NONE;
		if (bdIsTransactional()) {
			isolationLevel = Connection.TRANSACTION_READ_UNCOMMITTED;
		}
		return isolationLevel;
	}


	/**
	 *  The Ctsql jdbc connection class do not report warnings.
	 *
	 *@return                   Always null.
	 */
	public SQLWarning getWarnings() {
		return null;
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 *@return    The TypeMap value
	 */
	public Map getTypeMap() {
		return null;
	}


	/**
	 *  Creates a array of String with environment's variables.
	 *
	 *@param  props  Description of Parameter
	 *@return        Description of the Returned Value
	 */
	public String[] createEnviromentArray(Properties props) {
		String[] environment = null;

		if (props != null) {
			ArrayList variables = new ArrayList(props.size());

			Enumeration variableNames = props.propertyNames();
			while (variableNames.hasMoreElements()) {
				String name = (String) variableNames.nextElement();
				if (!(name.equals(DBUSER) || name.equals(DBPASSWORD))) {
					String value = name + "=" + props.getProperty(name);
					variables.add(value);
				}
			}
			environment = (String[]) variables.toArray(new String[0]);
		}
		return environment;
	}


	/**
	 *  close method releases the connection.
	 *
	 *@exception  SQLException  Description of Exception
	 *@jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public synchronized void close() throws SQLException {
		try {
			server.disconnect();
		}
		catch (CtsqlException ex) {
			ExceptionManager.getManager().throwException(CtsqlException.SERVER_NOT_CONNECTED, CtsqlException.getMessage(CtsqlException.SERVER_NOT_CONNECTED));
		}
	}


	/**
	 *  createStatemente method creates a TTStatement object for sending SQL
	 *  statements to the database.
	 *
	 *@return                   Description of the Returned Value
	 *@exception  SQLException  Description of Exception
	 *@jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public synchronized Statement createStatement() throws SQLException {
		return new TTStatement(this);
	}


	/**
	 *  preparedStatement creates a TTPreparedStatement object for sending
	 *  parameterized SQL statements to the database
	 *
	 *@param  stmt              Description of Parameter
	 *@return                   Description of the Returned Value
	 *@exception  SQLException  Description of Exception
	 *@jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public synchronized PreparedStatement prepareStatement(String stmt) throws SQLException {
		return new TTPreparedStatement(this, stmt);
	}


	/**
	 *  The server not support procedures
	 *
	 *@param  stmt              Description of Parameter
	 *@return                   Description of the Returned Value
	 *@exception  SQLException  Description of Exception
	 */
	public synchronized CallableStatement prepareCall(String stmt) throws SQLException {
		ExceptionManager.getManager().throwException(301, "CallableStatement method is not supported.");
		return null;
		// This statement will be never reached, but the compiler needs it.
	}


	/**
	 *  nativeSQL converts the given SQL statement into the system's native SQL
	 *  grammar.
	 *
	 *@param  str  Description of Parameter
	 *@return      the native form of this statement
	 */
	public String nativeSQL(String str) {
		return str;
	}


	/**
	 * Makes all changes made since the previous commit/rollback permanent and
	 * releases any database locks currently held by the Connection. This method
	 * should be used only when auto-commit mode has been disabled.
	 *
	 *@exception  SQLException  if a database access error occurs
	 */
	public void commit() throws SQLException {
		if(bdIsTransactional()){
			Statement stmt;
			stmt = this.createStatement();
			stmt.executeUpdate("COMMIT WORK");
			stmt.close();
			beginWork();
		}else{
			// podria ser un warning
			System.out.println("Commit has no sense in non transactional databases.");
		}
	}


	/**
	 *  Drops all changes made since the previous commit/rollback and releases
	 *  any database locks currently held by this Connection. This method should
	 *  be used only when auto- commit has been disabled.
	 *
	 *@exception  SQLException  if a database access error occurs
	 */
	public void rollback() throws SQLException {
		if(bdIsTransactional()){
			Statement stmt;
			stmt = this.createStatement();
			stmt.executeUpdate("ROLLBACK WORK");
			stmt.close();
			beginWork();
		}else{
			// podria ser un warning
			System.out.println("Roll Back has no sense in non transactional databases.");
		}
	}


	/**
	 *  clearWarnigs method is not supported.
	 *
	 *@exception  SQLException  Description of Exception
	 */
	public void clearWarnings() throws SQLException {
		// related to getWarnings();
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 *@param  resultSetType         Description of Parameter
	 *@param  resultSetConcurrency  Description of Parameter
	 *@return                       Description of the Returned Value
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency) {
// MOD Eva 22-02-2001
//      return null;
		return new TTStatement(this, resultSetType, resultSetConcurrency);
// End MOD Eva 22-02-2001
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 *@param  sql                   Description of Parameter
	 *@param  resultSetType         Description of Parameter
	 *@param  resultSetConcurrency  Description of Parameter
	 *@return                       Description of the Returned Value
	 *@exception  SQLException      Description of Exception
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return new TTPreparedStatement(this, sql, resultSetType, resultSetConcurrency);
	}


	/**
	 *  Funcionalidad correspondiente a JDBC 2.0. Por ahora no se implementarán
	 *  estos métodos.
	 *
	 *@param  sql                   Description of Parameter
	 *@param  resultSetType         Description of Parameter
	 *@param  resultSetConcurrency  Description of Parameter
	 *@return                       Description of the Returned Value
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) {
		return null;
	}


	/**
	 *  getCtsqlServer method gets the server
	 *
	 *@return    CtsqlServer object
	 */
	protected CtsqlServer getCtsqlServer() {
		return server;
	}


	/**
	 *  Description of the Method
	 *
	 *@exception  SQLException  Description of Exception
	 */
	protected void finalize() throws SQLException {
		close();
	}

	// Nuevos métodos JDK 1.4

	public Statement createStatement(
		int resultSetType,
		int resultSetConcurrency,
		int resultSetHoldability)
		throws SQLException {
			// TODO createStatement( int, int , int ) method is not supported.
			ExceptionManager.getManager().throwException(301, "createStatement method is not supported.");
			return null;
			// This statement will be never reached, but the compiler needs it.
	}

	public int getHoldability() throws SQLException {
		// TODO getHoldability() method is not supported.
		ExceptionManager.getManager().throwException(301, "getHoldability method is not supported.");
		// This statement will be never reached, but the compiler needs it.
		return -1;
	}

	public CallableStatement prepareCall(
		String sql,
		int resultSetType,
		int resultSetConcurrency,
		int resultSetHoldability)
		throws SQLException {
			// TODO prepareCall( String, int, int, int ) method is not supported.
			ExceptionManager.getManager().throwException(301, "prepareCall method is not supported.");
			// This statement will be never reached, but the compiler needs it.
			return null;
	}

	public PreparedStatement prepareStatement(
		String sql,
		int resultSetType,
		int resultSetConcurrency,
		int resultSetHoldability)
		throws SQLException {
			// TODO prepareStatement( String, int, int, int ) method is not supported.
			ExceptionManager.getManager().throwException(301, "prepareStatement method is not supported.");
			// This statement will be never reached, but the compiler needs it.
			return null;
	}

	public PreparedStatement prepareStatement(
		String sql,
		int autoGeneratedKeys)
		throws SQLException {
			// TODO prepareStatement( String, int ) method is not supported.
			ExceptionManager.getManager().throwException(301, "prepareStatement method is not supported.");
			// This statement will be never reached, but the compiler needs it.
			return null;
	}

	public PreparedStatement prepareStatement(
		String sql,
		int[] columnIndexes)
		throws SQLException {
			// TODO prepareStatement( String, int[] ) method is not supported.
			ExceptionManager.getManager().throwException(301, "prepareStatement method is not supported.");
			// This statement will be never reached, but the compiler needs it.
			return null;
	}

	public PreparedStatement prepareStatement(
		String sql,
		String[] columnNames)
		throws SQLException {
		// TODO prepareStatement( String, String[] ) method is not supported.
		ExceptionManager.getManager().throwException(301, "prepareStatement method is not supported.");
		// This statement will be never reached, but the compiler needs it.
		return null;
	}

// JDK 1.4	
//	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
//		// TODO releaseSavepoint( Savepoint ) method is not supported.
//		ExceptionManager.getManager().throwException(301, "releaseSavepoint method is not supported.");
//	}
//
//	public void rollback(Savepoint savepoint) throws SQLException {
//		// TODO rollback( Savepoint ) method is not supported.
//		ExceptionManager.getManager().throwException(301, "rollback method is not supported.");
//	}

	public void setHoldability(int holdability) throws SQLException {
		// TODO setHoldability( int ) method is not supported.
		ExceptionManager.getManager().throwException(301, "setHoldability method is not supported.");
	}

	public int getLastRowid() {
		return server.getLastRowid();
	}

	public int getLastSerial() {
		return server.getLastSerial();
	}

// JDK 1.4
//	public Savepoint setSavepoint() throws SQLException {
//		// TODO setSavepoint() method is not supported.
//		ExceptionManager.getManager().throwException(301, "setSavepoint method is not supported.");
//		// This statement will be never reached, but the compiler needs it.
//		return null;
//	}
//
//	public Savepoint setSavepoint(String name) throws SQLException {
//		// TODO setSavepoint( String ) method is not supported.
//		ExceptionManager.getManager().throwException(301, "setSavepoint method is not supported.");
//		// This statement will be never reached, but the compiler needs it.
//		return null;
//	}

	public String getUrl(){
		String propstr = "";
		String ret;
		Iterator it = props.entrySet().iterator();
		while(it.hasNext()){
			Entry entry = (Entry) it.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			propstr+=";"+key+"="+value;
		}
		ret =  "jdbc:ctsql://"+host+":"+port+"/"+dbName+propstr;
		return ret;
	}
	
	public void putEnv(String aString) throws CtsqlException{
		getCtsqlServer().putEnv(aString);
	}
}
