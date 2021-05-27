package com.esferalia.aon.file.payroll.cra;


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
import com.esferalia.aon.file.payroll.cra.data.CRE;
import com.esferalia.aon.file.payroll.cra.data.DDE;
import com.esferalia.aon.file.payroll.cra.data.ETI;
import com.esferalia.aon.file.payroll.cra.data.TRB;

public class CRA extends AbstractFileFiller{
	
	private static String ETI = "ETI";
	
//	Datos De Empresa
	private static String DDE = "DDE";
	
//	Datos del TRaBajador
	private static String TRB = "TRB";
	
//	Conceptos REtributivos
	private static String CRE = "CRE";
	
	
	
	private ETI eti;
	
	public CRA(ETI eti, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		this.eti = eti;
		InputStream input = null;
		if (eti == null)  {
			throw new IllegalArgumentException("El registro CRA no puede ser nulo!");
		}
		input = CRA.class.getResourceAsStream("/com/esferalia/aon/file/payroll/cra/xml/ETI.xml");
		DiskRegisterLoader.load(input, manager);
		input = CRA.class.getResourceAsStream("/com/esferalia/aon/file/payroll/cra/xml/DDE.xml");
		DiskRegisterLoader.load(input, manager);
		input = CRA.class.getResourceAsStream("/com/esferalia/aon/file/payroll/cra/xml/TRB.xml");
		DiskRegisterLoader.load(input, manager);
		input = CRA.class.getResourceAsStream("/com/esferalia/aon/file/payroll/cra/xml/CRE.xml");
		DiskRegisterLoader.load(input, manager);
		
	}
	
	@Override
	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			properties.put(ETI, eti);
			createLine(ETI,properties);
			for (DDE dde: eti.getDdeList()) {
				properties.put(DDE , dde);
				createLine(DDE,properties);
				for (TRB trb: dde.getTrbList()) {
					properties.put(TRB , trb);
					createLine(TRB,properties);
					for (CRE cre: trb.getCreList()) {
						properties.put(CRE , cre);
						createLine(CRE,properties);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} 
			else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(),eti.toString());
				exceptions.add (e);
			}
		}
		output.flush();
		return exceptions;
	}
	
}
