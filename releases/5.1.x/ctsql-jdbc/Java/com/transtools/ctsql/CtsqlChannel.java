package com.transtools.ctsql;

import java.io.IOException;

/**
 *  Description of the Interface
 *
 *@author     eva
 *@created    May 21, 2001
 */
public interface CtsqlChannel {
	/**
	 *  Description of the Method
	 *
	 *@exception  IOException  Description of Exception
	 */
	public void flush() throws IOException;


	/**
	 *  Description of the Method
	 *
	 *@param  buffer           Description of Parameter
	 *@param  off              Description of Parameter
	 *@param  len              Description of Parameter
	 *@exception  IOException  Description of Exception
	 */
	public void readData(byte[] buffer, int off, int len) throws IOException;


	/**
	 *  Description of the Method
	 *
	 *@param  data             Description of Parameter
	 *@param  len              Description of Parameter
	 *@exception  IOException  Description of Exception
	 */
	public void writeData(byte[] data, int len) throws IOException;


	/**
	 *  Description of the Method
	 *
	 *@exception  IOException  Description of Exception
	 */
	public void close() throws IOException;
}
