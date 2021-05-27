package com.esferalia.aon.file.pms.webpol;


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
import com.esferalia.aon.file.pms.webpol.data.TIPO0;
import com.esferalia.aon.file.pms.webpol.data.TIPO1;
import com.esferalia.aon.file.pms.webpol.data.TIPO2;

public class WebpolGuests extends AbstractFileFiller{
	
	private static String TIPO0 = "TIPO0";
	
	private static String TIPO1 = "TIPO1";
	
	private static String TIPO2 = "TIPO2";
	
	
	private TIPO0 tipo0;
	private TIPO1 tipo1;
	
	public WebpolGuests(TIPO0 tipo0, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		this(writer);
		if (tipo0 == null)  {
			throw new IllegalArgumentException("El registro TIPO0 no puede ser nulo!");
		}
		this.tipo0 = tipo0;
	}
	
	public WebpolGuests(TIPO1 tipo1, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		this(writer);
		if (tipo1 == null)  {
			throw new IllegalArgumentException("El registro TIPO1 no puede ser nulo!");
		}
		this.tipo1 = tipo1;
	}
		
	public WebpolGuests(PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		InputStream input = null;
		input = WebpolGuests.class.getResourceAsStream("/com/esferalia/aon/file/pms/webpol/xml/TIPO0.xml");
		DiskRegisterLoader.load(input, manager);
		input = WebpolGuests.class.getResourceAsStream("/com/esferalia/aon/file/pms/webpol/xml/TIPO1.xml");
		DiskRegisterLoader.load(input, manager);
		input = WebpolGuests.class.getResourceAsStream("/com/esferalia/aon/file/pms/webpol/xml/TIPO2.xml");
		DiskRegisterLoader.load(input, manager);
		
	}
	
	@Override
	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			if(tipo0!=null){
				createTipo0(properties);
			} else  if (tipo1!=null){
				createTipo1(properties, tipo1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(),tipo0.toString());
				exceptions.add (e);
			}
		}
		output.flush();
		writeErrorsFile();
		return exceptions;
	}
	
	private void createTipo0(Map<String,Object> properties){
		properties.put(TIPO0, tipo0);
		createLine(TIPO0,properties);
		for (TIPO1 tipo1: tipo0.getTipo1List()) {
			createTipo1(properties, tipo1);
		}
	}
	
	private void createTipo1(Map<String,Object> properties, TIPO1 tipo1){
		properties.put(TIPO1 , tipo1);
		createLine(TIPO1,properties);
		for (TIPO2 tipo2: tipo1.getTipo2List()) {
			properties.put(TIPO2 , tipo2);
			createLine(TIPO2,properties);
		}
	}
	
}
