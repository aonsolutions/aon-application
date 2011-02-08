package com.transtools.jdbc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 *  Description of the Class
 *
 *@author     eva
 *@created    May 21, 2001
 */
public class CtsqlChannel implements com.transtools.ctsql.CtsqlChannel {

	private BufferedInputStream input;
	private BufferedOutputStream output;

	// @jdbc.modified		Se declara el socket para poder utilizarlo en close.
	private Socket socket;

	private static String PROPS_FILE_NAME = "CtsqlChannel.properties";
	private static String BUFFER_SIZE_PROP = "bufferSize";
	private static String DEFAULT_BUFFER_SIZE = "1024";

	private static Properties props = loadProperties();

	private static int BUFFER_SIZE = Integer.parseInt(props.getProperty(BUFFER_SIZE_PROP));


	/**
	 *  Constructor for the CtsqlChannel object
	 *
	 *@param  host                      Description of Parameter
	 *@param  port                      Description of Parameter
	 *@exception  UnknownHostException  Description of Exception
	 *@exception  IOException           Description of Exception
	 */
	public CtsqlChannel(String host, int port) throws UnknownHostException, IOException {
		socket = new Socket(host, port);
		initialize(socket.getInputStream(), socket.getOutputStream());
	}


	/**
	 *  Constructor for the CtsqlChannel object
	 *
	 *@param  input   Description of Parameter
	 *@param  output  Description of Parameter
	 */
	public CtsqlChannel(InputStream input, OutputStream output) {
		initialize(input, output);
	}


	/**
	 *  Description of the Method
	 *
	 *@exception  IOException  Description of Exception
	 */
	public void flush() throws IOException {
		output.flush();
	}


	/**
	 *@param  buffer           Description of Parameter
	 *@param  off              Description of Parameter
	 *@param  len              Description of Parameter
	 *@exception  IOException  Description of Exception
	 *@jdbc.modified           Se controla que siempre se lea la longitud adecuada
	 *      de bytes
	 */
	public void readData(byte[] buffer, int off, int len) throws IOException {
		int nbytes;
		do {
			nbytes = input.read(buffer, off, len);
			if(nbytes >= 0){
				len -= nbytes;
				off += nbytes;
			}else throw new IOException("End of file. The communication with the SQL server was interrupted.");
		} while (len > 0);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  data             Description of Parameter
	 *@param  len              Description of Parameter
	 *@exception  IOException  Description of Exception
	 */
	public void writeData(byte[] data, int len) throws IOException {
		output.write(data, 0, len);
	}


	/**
	 *  Description of the Method
	 *
	 *@exception  IOException  Description of Exception
	 */
	public void close() throws IOException {
		input.close();
		output.close();
		if(socket != null)
			socket.close();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  input   Description of Parameter
	 *@param  output  Description of Parameter
	 */
	private void initialize(InputStream input, OutputStream output) {
		this.input = new BufferedInputStream(input, BUFFER_SIZE);
		this.output = new BufferedOutputStream(output, BUFFER_SIZE);
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	private static Properties loadProperties() {
		Properties prop = new Properties();
		try {
			prop.load(CtsqlChannel.class.getResourceAsStream(
					PROPS_FILE_NAME));
		}
		catch (Throwable t) {
			prop.setProperty(BUFFER_SIZE_PROP, DEFAULT_BUFFER_SIZE);
		}
		return prop;
	}

}

