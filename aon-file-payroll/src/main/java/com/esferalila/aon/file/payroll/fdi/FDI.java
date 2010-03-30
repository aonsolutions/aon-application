package com.esferalila.aon.file.payroll.fdi;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.esferalila.aon.file.payroll.fdi.data.ETI;

public class FDI  extends AbstractFileFiller{

	private static String ETI = "ETI";
	private static String EMP = "EMP";
	private static String TRA = "TRA";
	private static String DOM = "DOM";
	private static String LDD = "LDD";
	private static String DIT = "DIT";
	private static String DEC = "DEC";
	private static String ODP = "ODP";
	private static String ETF = "ETF";
	
	private ETI eti;
	
	public FDI(ETI eti, String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		super(filePath);
		if (eti == null)  {
			throw new IllegalArgumentException("El registro ETI no puede ser nulo!");
		}
		this.eti = eti;
		InputStream input = FDI.class.getResourceAsStream("com/esferalila/aon/file/payroll/fdi/xml/DEC.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("com/esferalila/aon/file/payroll/fdi/xml/DIT.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("com/esferalila/aon/file/payroll/fdi/xml/DOM.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("com/esferalila/aon/file/payroll/fdi/xml/EMP.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("com/esferalila/aon/file/payroll/fdi/xml/ETF.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("com/esferalila/aon/file/payroll/fdi/xml/ETI.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("com/esferalila/aon/file/payroll/fdi/xml/LDD.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("com/esferalila/aon/file/payroll/fdi/xml/ODP.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("com/esferalila/aon/file/payroll/fdi/xml/TRA.xml");
		DiskRegisterLoader.load(input, manager);
	}
	
	@Override
	public ArrayList<Exception> create() {
		/*
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			properties.put(FDI.DEPONENT, deponent);
			
			if (CheckDeponent.parse(deponent,exceptions)==false) {
				throw new Fd0Exception( "ABORTED: ",deponent.toString());
			}
			
			createLine("Declarante",properties);

			for (Declared declared: deponent.getDeclareds()){
				properties.put(FDI.DECLARED, declared);
				try{
					if (CheckDeclared.parse(declared,exceptions)==false) {
						throw new Fd0Exception( "ABORTED: ",declared.toString());
					}
					createLine("Declarado",properties);
				} catch (Exception ex) {
					if ( ex instanceof Fd0Exception ) {
						exceptions.add (ex);
					} 
					else {
						Fd0Exception e = new Fd0Exception( ex.getMessage(),declared.toString());
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
		*/
		return null;
	}
	
}
