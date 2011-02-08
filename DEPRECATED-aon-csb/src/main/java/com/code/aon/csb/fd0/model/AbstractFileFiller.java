package com.code.aon.csb.fd0.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.code.aon.csb.fd0.core.Register;
import com.code.aon.csb.fd0.core.RegisterManager;

/**
 * Manages file creation and errors generic points
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public abstract class AbstractFileFiller implements FileFiller {

	/**
	 * Error list
	 */
	protected ArrayList<Exception> exceptions = new ArrayList<Exception>();
	
	/**
	 * File generation path
	 */
	private String filePath;
	
	/**
	 * The Register manager
	 */
	protected RegisterManager manager = new RegisterManager();
	
	/**
	 * The lines printer
	 */
	protected LinesOutput output;

	/**
	 * Contructor for a file path
	 * 
	 * @param filePath destination file path
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	protected AbstractFileFiller(String filePath) throws FileNotFoundException, UnsupportedEncodingException{
		this.filePath = filePath;
		this.output = assignFileOutputStream(filePath);
	}
	
	
	
	/**
	 * @return the exceptions
	 */
	public ArrayList getExceptions() {
		return exceptions;
	}

	/**
	 * Gets a print writer
	 * 
	 * @param filePath the destination file path
	 * @return a PrintWriter for this file path
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	protected PrintWriter assignPrintWriter(String filePath) throws FileNotFoundException, UnsupportedEncodingException{
		return new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath)), "iso-8859-1"));
	}
	
	/**
	 * Assigns a LinesOutput for this file path
	 * 
	 * @param filePath the destination file path
	 * @return the lines generator
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	protected LinesOutput assignFileOutputStream(String filePath) throws FileNotFoundException, UnsupportedEncodingException{
		return new LinesOutput(assignPrintWriter(filePath));
	}
	
	/**
	 * Checks for error and writes then in a file in the destination file path
	 */
	protected void writeErrorsFile(){
		if (exceptions.size()>0){
			(new File(filePath)).renameTo(new File(filePath+".err"));
			Iterator iterErr = exceptions.iterator();
			PrintWriter outputErr = null;
			try {
				outputErr = assignPrintWriter(filePath);
            } catch (FileNotFoundException e) {
            } catch (UnsupportedEncodingException e) {
            }
			while (iterErr.hasNext()){
				Fd0Exception fd0 = (Fd0Exception) iterErr.next();
				outputErr.println(fd0.getMessage());
			}
			outputErr.flush();
		}else{
			(new File(filePath+".err")).delete();
		}
	}

	/**
	 * Creates a file line for this registry with this data 
	 * 
	 * @param registryType the registry type
	 * @param properties the data objects map
	 * @return the lines filler
	 */
	protected LinesFiller createLine(String registryType, Map properties) {
		Register register = manager.get(registryType);
		try {
			LinesFiller linesFiller = new GenericLinesFiller();
			linesFiller.addLinesFillerListener(output);
			linesFiller.fillLine(register, properties);
			return linesFiller;
		} catch (Exception ex) {
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} 
			else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(),null);
				exceptions.add (e);
			}
			return null;
		}
	}

	static {
		try {
			Class.forName("com.code.aon.csb.fd0.formats.FormatFactory");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
