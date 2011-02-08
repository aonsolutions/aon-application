package com.transtools.ctsql.impl;

import com.transtools.ctsql.CtsqlChannel;
import com.transtools.ctsql.CtsqlConstants;
import com.transtools.ctsql.CtsqlException;
import com.transtools.ctsql.CtsqlServer;
import com.transtools.ctsql.CtsqlType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 *@author          eva
 *@created         April 25, 2001
 *@jdbc.pending    hay que repasar todo lo referente al lanzamiento de
 *      excepciones en esta clase.
 */

class CtsqlServerImpl implements CtsqlServer {

	private CtsqlChannel channel;
	private boolean connected = false;
	private String currDb;
	private String currDbPath;
	private int transactionFlag = 0;
	private boolean bRowidStandard = true;
	private int rowidType = CtsqlType.INTEGER_TYPE;
	private int rowidSize = 0;
	private byte[] buf4bytes = {0, 0, 0, 0};
	private int count = 0;
	private boolean isUsed = false;
	private int convert = 0;
	private boolean rTrimChar = true;

	/**
	 *  Description of the Field
	 */
	public final static int CNT_CONVERTANSITOOEM = 1;
	/**
	 *  Description of the Field
	 */
	public final static int CNT_CONVERTOEMTOANSI = 2;

	private static String DATABASE = "database";
	private static byte[] EVEN_BYTE = {0};
	private static byte[] DUMB_BUFFER = {0};

	
	private int lastSerial = -1;
	private int lastRowid = -1;

	/**
	 *  Constructor for the CtsqlServerImpl object
	 */
	public CtsqlServerImpl() {
	}


	/**
	 *  Sets the Convert attribute of the CtsqlServerImpl object
	 *
	 *@param  convert  The new Convert value
	 */
	public void setConvert(int convert) {
		this.convert = convert;
	}


	/**
	 *  Sets the IsUsed attribute of the CtsqlServerImpl object
	 *
	 *@param  used  The new IsUsed value
	 */
	public void setIsUsed(boolean used) {
		isUsed = used;
		if (used == true) {
			count++;
		}
		else {
			count--;
		}
//    System.out.println("Count = " + count);
	}


	/**
	 *  Sets the TransactionFlag attribute of the CtsqlServerImpl object
	 *
	 *@param  transactionFlag  The new TransactionFlag value
	 */
	public void setTransactionFlag(int transactionFlag) {
		this.transactionFlag = transactionFlag;
	}


	/**
	 *  Sets the CurrentDBPath attribute of the CtsqlServerImpl object
	 *
	 *@param  currentDBPath  The new CurrentDBPath value
	 */
	public void setCurrentDBPath(String currentDBPath) {
		this.currDbPath = currentDBPath;
	}


	/**
	 *  Sets the CurrentDB attribute of the CtsqlServerImpl object
	 *
	 *@param  currentDB  The new CurrentDB value
	 */
	public void setCurrentDB(String currentDB) {
		this.currDb = currentDB;
	}


	/**
	 *  Sets the RowidTypeSize attribute of the CtsqlServerImpl object
	 *
	 *@param  type  The new RowidTypeSize value
	 *@param  size  The new RowidTypeSize value
	 */
	public void setRowidTypeSize(int type, int size) {
		rowidType = type;
		rowidSize = size;
		bRowidStandard = (type != CtsqlType.INTEGER_TYPE) ? false : true;
	}


	/**
	 *  Gets the Convert attribute of the CtsqlServerImpl object
	 *
	 *@return    The Convert value
	 */
	public int getConvert() {
		return convert;
	}


	/**
	 *  Gets the TransactionFlag attribute of the CtsqlServerImpl object
	 *
	 *@return    The TransactionFlag value
	 */
	public int getTransactionFlag() {
		return transactionFlag;
	}


	/**
	 *  Gets the CurrentDBPath attribute of the CtsqlServerImpl object
	 *
	 *@return    The CurrentDBPath value
	 */
	public String getCurrentDBPath() {
		return currDbPath;
	}


	/**
	 *  Gets the CurrentDB attribute of the CtsqlServerImpl object
	 *
	 *@return    The CurrentDB value
	 */
	public String getCurrentDB() {
		return currDb;
	}


	/**
	 *  Gets the Connected attribute of the CtsqlServerImpl object
	 *
	 *@return    The Connected value
	 */
	public boolean isConnected() {
		return connected;
	}


	/**
	 *  Gets the RowidStandard attribute of the CtsqlServerImpl object
	 *
	 *@return    The RowidStandard value
	 */
	public boolean isRowidStandard() {
		return bRowidStandard;
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public boolean geIsUsed() {
		return isUsed;
	}


	/**
	 *@param  aChannel            Description of Parameter
	 *@param  dbuser              Description of Parameter
	 *@param  dbpassword          Description of Parameter
	 *@param  dbname              Description of Parameter
	 *@param  env                 Description of Parameter
	 *@exception  CtsqlException  Description of Exception
	 *@jdbc.pending               hay que repasar todo lo referente al lanzamiento
	 *      de excepciones en este método.
	 */
	public void connect(CtsqlChannel aChannel, String dbuser, String dbpassword,
			String dbname, String[] env) throws CtsqlException {
		if (isConnected()) {
			disconnect();
		}
		this.channel = aChannel;
		int idx;
		/** @todo contemplar usuario o/y password nula */
		writeString(dbuser);
		writeString(dbpassword);

		for (idx = 0; idx < env.length; idx++) {
			sendPutEnv(env[idx]);
		}

		connected = true;

		if (dbname != null) {
			CtsqlStmt ctsqlStmtImpl = new CtsqlStmt(this);
			ctsqlStmtImpl.prepare(DATABASE + " " + dbname);
			ctsqlStmtImpl.execute(null);
			if (ctsqlStmtImpl.getLastError() == CtsqlConstants.SQ_ERR) {
				CtsqlExceptionManager.getManager().throwException(ctsqlStmtImpl.getLastError());
			}
			//		throw new CtsqlException(ctsqlStmtImpl.getLastError(),ctsqlStmtImpl.getLastErrorString());
			ctsqlStmtImpl.release();
		}
	}


	/**
	 *@exception  CtsqlException  Description of Exception
	 *@jdbc.pending               hay que repasar todo lo referente al lanzamiento
	 *      de excepciones en este método.
	 */
	public void disconnect() throws CtsqlException {
		if (isConnected()) {
			try {
				channel.close();
			}
			catch (IOException e) {
				CtsqlExceptionManager.getManager().throwException(e);
			}
			channel = null;
			connected = false;
		}
	}


	/**
	 *@param  aString             Description of Parameter
	 *@exception  CtsqlException  Description of Exception
	 *@jdbc.pending               hay que repasar todo lo referente al lanzamiento
	 *      de excepciones en este método.
	 */
	public void putEnv(String aString) throws CtsqlException {
		if (!isConnected()) {
			CtsqlExceptionManager.getManager().throwException(CtsqlException.CONNECTION_NOT_OPEN, null);
		}
//			throw new CtsqlException(CtsqlException.CONNECTION_NOT_OPEN);
		sendPutEnv(aString);
	}


	/**
	 *@exception  CtsqlException  Description of Exception
	 *@jdbc.pending               hay que repasar todo lo referente al lanzamiento
	 *      de excepciones en este método.
	 */
	public void flush() throws CtsqlException {
		try {
			channel.flush();
		}
		catch (IOException e) {
			CtsqlExceptionManager.getManager().throwException(e);
		}
	}


	/**
	 *@return                     Description of the Returned Value
	 *@exception  CtsqlException  Description of Exception
	 *@jdbc.pending               hay que repasar todo lo referente al lanzamiento
	 *      de excepciones en este método.
	 */
	public short readSmallint() throws CtsqlException {
		try {
//			System.out.println("CtsqlServerImpl.readSmallint method: antes de readData");
			channel.readData(buf4bytes, 0, 2);
//			System.out.println("CtsqlServerImpl.readSmallint method: después de de readData");
//			CtsqlSmallint ct = new CtsqlSmallint(buf4bytes, 0);
//			return 	ct.getAsShort();
			return CtsqlSmallint.loadShort(buf4bytes, 0);
		}
		catch (IOException e) {
			//CtsqlExceptionManager.getManager().throwException(CtsqlException.COMMUNICATION_INTERRUPTED);
			return (short) CtsqlConstants.SQ_FAIL;
//			throw new CtsqlException(e);
		}
	}


	/**
	 *@return                     Description of the Returned Value
	 *@exception  CtsqlException  Description of Exception
	 *@jdbc.pending               hay que repasar todo lo referente al lanzamiento
	 *      de excepciones en este método.
	 */
	public int readInteger() throws CtsqlException {
		try {
			channel.readData(buf4bytes, 0, 4);
			CtsqlInteger it = new CtsqlInteger(buf4bytes, 0);
			return it.getAsInt();
//			return CtsqlInteger.loadInt(buf4bytes, 0);
		}
		catch (IOException e) {
			CtsqlExceptionManager.getManager().throwException(e);
			return 0;
//			throw new CtsqlException(e);
		}
	}


	/**
	 *@param  data                Description of Parameter
	 *@exception  CtsqlException  Description of Exception
	 *@jdbc.pending               hay que repasar todo lo referente al lanzamiento
	 *      de excepciones en este método.
	 */
	public void writeSmallint(short data) throws CtsqlException {
		try {
			CtsqlSmallint.storeShort(data, buf4bytes);
			channel.writeData(buf4bytes, 2);
		}
		catch (IOException e) {
			CtsqlExceptionManager.getManager().throwException(e);
//			throw new CtsqlException(e);
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  command             Description of Parameter
	 *@exception  CtsqlException  Description of Exception
	 */
	public void writeCommand(short command) throws CtsqlException {
		writeSmallint(command);
	}


	/**
	 *@return                     Description of the Returned Value
	 *@exception  CtsqlException  Description of Exception
	 *@jdbc.pending               hay que repasar todo lo referente al lanzamiento
	 *      de excepciones en este método.
	 */
	public String readString() throws CtsqlException {
		int len;

		len = readSmallint();
		if (len > 0) {
			byte buffer[] = new byte[len];
			try {
				channel.readData(buffer, 0, len);
				evenizeRead(len);
			}
			catch (IOException e) {
				CtsqlExceptionManager.getManager().throwException(e);
			}
			return new String(buffer);
		}
		return "";
	}


	/**
	 *@param  aString             Description of Parameter
	 *@exception  CtsqlException  Description of Exception
	 *@jdbc.pending               hay que repasar todo lo referente al lanzamiento
	 *      de excepciones en este método.
	 */
	public void writeString(String aString) throws CtsqlException {
		int length = aString.length();
		writeSmallint((short) length);
		try {
			byte[]  bytes = aString.getBytes();
			if(convert == CNT_CONVERTOEMTOANSI){
				bytes = OemConverter.AnsiToOem(bytes);
			}
			channel.writeData(bytes, length);
			evenizeWrite(length);
		}
		catch (IOException e) {
			CtsqlExceptionManager.getManager().throwException(e);
//			throw new CtsqlException(e);
		}
	}


	/**
	 *@param  data                Description of Parameter
	 *@param  len                 Description of Parameter
	 *@exception  CtsqlException  Description of Exception
	 *@jdbc.pending               hay que repasar todo lo referente al lanzamiento
	 *      de excepciones en este método.
	 */
	public void writeData(byte[] data, int len) throws CtsqlException {
		try {
			channel.writeData(data, len);
			evenizeWrite(len);
		}
		catch (IOException e) {
			CtsqlExceptionManager.getManager().throwException(e);
		}
	}


	/**
	 *@param  buffer              Description of Parameter
	 *@param  off                 Description of Parameter
	 *@param  len                 Description of Parameter
	 *@exception  CtsqlException  Description of Exception
	 *@jdbc.pending               hay que repasar todo lo referente al lanzamiento
	 *      de excepciones en este método.
	 */
	public void readData(byte buffer[], int off, int len) throws CtsqlException {
		try {
			channel.readData(buffer, off, len);
		}
		catch (IOException e) {
			CtsqlExceptionManager.getManager().throwException(e);
//			throw new CtsqlException(e);
		}
	}


	/**
	 *@param  buffer              Description of Parameter
	 *@param  off                 Description of Parameter
	 *@param  len                 Description of Parameter
	 *@exception  CtsqlException  Description of Exception
	 *@jdbc.pending               hay que repasar todo lo referente al lanzamiento
	 *      de excepciones en este método.
	 */
	public void readDataEven(byte buffer[], int off, int len) throws CtsqlException {
		readData(buffer, off, len);
		try {
			evenizeRead(len);
		}
		catch (IOException e) {
			CtsqlExceptionManager.getManager().throwException(e);
//			throw new CtsqlException(e);
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  var                 Description of Parameter
	 *@exception  CtsqlException  Description of Exception
	 */
	private void sendPutEnv(String var) throws CtsqlException {
		writeCommand(CtsqlConstants.SQ_PUTENV);
		writeString(var);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  length           Description of Parameter
	 *@exception  IOException  Description of Exception
	 */
	private void evenizeWrite(int length) throws IOException {
		if ((length & 1) != 0) {
			channel.writeData(EVEN_BYTE, 1);
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  length           Description of Parameter
	 *@exception  IOException  Description of Exception
	 */
	private void evenizeRead(int length) throws IOException {
		if ((length & 1) != 0) {
			channel.readData(DUMB_BUFFER, 0, 1);
		}
	}

	public void setChannel(InputStream input, OutputStream output) {
		this.channel = new com.transtools.jdbc.CtsqlChannel(input, output);
	}

	public void setConnected() {
		this.connected = true;
	}


	public boolean getRTrimChar() {
		return this.rTrimChar;
	}


	public void setRTrimChar(boolean rTrimChar) {
		this.rTrimChar = rTrimChar;
	}

	public int getLastSerial() {
		return lastSerial;
	}


	public void setLastSerial(int lastSerial) {
		this.lastSerial = lastSerial;
	}


	public int getLastRowid() {
		return lastRowid;
	}


	public void setLastRowid(int lastRowid) {
		this.lastRowid = lastRowid;
	}
}

