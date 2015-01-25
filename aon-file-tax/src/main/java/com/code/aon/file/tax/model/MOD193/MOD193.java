package com.code.aon.file.tax.model.MOD193;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.format.model.Fd0Exception;

public class MOD193  extends AbstractFileFiller{

	private static String DEPONENT = "DEPONENT";
	private static String RECEIVER = "RECEIVER";
	private static String EXPENSES = "EXPENSES";

	private Deponent deponent;
	
	public MOD193(Deponent deponent, MOD193Format format, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		if (deponent == null)  {
			throw new IllegalArgumentException("Deponent can not be null!");
		}
		if (format == null)  {
			throw new IllegalArgumentException("Format can not be null!");
		}
		
		this.deponent = deponent;
		
		InputStream input = MOD193.class.getResourceAsStream(format.getDeponentMetadataResource());
		DiskRegisterLoader.load(input, manager);

		input = MOD193.class.getResourceAsStream(format.getReceiverMetadataResource());
		DiskRegisterLoader.load(input, manager);

		input = MOD193.class.getResourceAsStream(format.getExpensesMetadataResource());
		DiskRegisterLoader.load(input, manager);
	}

	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			properties.put(MOD193.DEPONENT, deponent);
			
			if ( !isValidDeponent(deponent)) {
				throw new Fd0Exception( "ERROR: ",deponent.toString());
			}
			
			createLine(MOD193.DEPONENT,properties);

			for (Receiver receiver: deponent.getReceivers()){
				properties.put(MOD193.RECEIVER, receiver);
				try{
					if (!isValidReceiver(receiver)) {
						throw new Fd0Exception( "ERROR: ",receiver.toString());
					}
					createLine(MOD193.RECEIVER,properties);
				} catch (Exception ex) {
					if ( ex instanceof Fd0Exception ) {
						exceptions.add (ex);
					} 
					else {
						Fd0Exception e = new Fd0Exception( ex.getMessage(),receiver.toString());
						exceptions.add (e);
					}
				}
			}

			for (Expenses expenses: deponent.getExpenses()){
				properties.put(MOD193.EXPENSES, expenses);
				try{
					createLine(MOD193.EXPENSES,properties);
				} catch (Exception ex) {
					if ( ex instanceof Fd0Exception ) {
						exceptions.add (ex);
					} 
					else {
						Fd0Exception e = new Fd0Exception( ex.getMessage(),expenses.toString());
						exceptions.add (e);
					}
				}
			}

		} catch (Exception ex) {
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} 
			else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(),deponent.toString());
				exceptions.add (e);
			}
		}
		output.flush();
		writeErrorsFile();
		return exceptions;
	}

	private boolean isValidReceiver(Receiver receiver2) {
		// TODO VALIDACIONES PREVIAS
		return true;
	}

	private boolean isValidDeponent(Deponent deponent2) {
		// TODO VALIDACIONES PREVIAS
		return true;
	}

	
}
