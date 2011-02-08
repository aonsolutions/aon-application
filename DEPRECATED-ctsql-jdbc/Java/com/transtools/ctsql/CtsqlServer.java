package com.transtools.ctsql;

import java.io.InputStream;
import java.io.OutputStream;
/**
 *  Description of the Interface
 *
 *@author     eva
 *@created    May 21, 2001
 */
public interface CtsqlServer {
	/**
	 *  Description of the Method
	 *
	 *@param  channel             Description of Parameter
	 *@param  dbuser              Description of Parameter
	 *@param  dbpassword          Description of Parameter
	 *@param  dbname              Description of Parameter
	 *@param  env                 Description of Parameter
	 *@exception  CtsqlException  Description of Exception
	 */
	public void connect(CtsqlChannel channel, String dbuser,
			String dbpassword, String dbname,
			String[] env) throws CtsqlException;


	/**
	 *  Description of the Method
	 *
	 *@exception  CtsqlException  Description of Exception
	 */
	public void disconnect() throws CtsqlException;


	/**
	 *  Gets the Connected attribute of the CtsqlServer object
	 *
	 *@return    The Connected value
	 */
	public boolean isConnected();


	/**
	 *  Gets the Convert attribute of the CtsqlServer object
	 *
	 *@return    The Convert value
	 */
	public int getConvert();


	/**
	 *  Sets the Convert attribute of the CtsqlServer object
	 *
	 *@param  convert  The new Convert value
	 */
	public void setConvert(int convert);
	public void setChannel(InputStream input, OutputStream output);
	public void setConnected();
	
	public boolean getRTrimChar();
	public void setRTrimChar(boolean trimChar);


	/**
	 * Returns the last autoincrement value generated for a Serial column in a Insert statement.  
	 * @return
	 */
	public int getLastSerial();

	/**
	 * Returns the last rowid value used to store a row in an Insert statement.  
	 * @return
	 */
	public int getLastRowid();

	/**
	 * Send a environment variable to the server.  
	 * @return
	 */
	public void putEnv(String aString) throws CtsqlException;
}

