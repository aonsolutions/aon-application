package com.esferalia.aon.file.payroll.fdi;



import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.format.model.Fd0Exception;
import com.esferalia.aon.file.payroll.fdi.data.DEC;
import com.esferalia.aon.file.payroll.fdi.data.DIT;
import com.esferalia.aon.file.payroll.fdi.data.DOM;
import com.esferalia.aon.file.payroll.fdi.data.ETI;
import com.esferalia.aon.file.payroll.fdi.data.EMP;
import com.esferalia.aon.file.payroll.fdi.data.LDD;
import com.esferalia.aon.file.payroll.fdi.data.ODP;
import com.esferalia.aon.file.payroll.fdi.data.TRA;

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
		InputStream input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/DEC.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/DIT.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/DOM.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/EMP.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/ETF.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/ETI.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/LDD.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/ODP.xml");
		DiskRegisterLoader.load(input, manager);
		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/TRA.xml");
		DiskRegisterLoader.load(input, manager);
	}
	
	@Override
	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			properties.put(ETI, eti);
			createLine(ETI,properties);
			for (EMP emp: eti.getEmpresas()) {
				properties.put(EMP , emp);
				createLine(EMP,properties);
				for (TRA tra: emp.getTrabajadores()) {
					properties.put(TRA , tra);
					createLine(TRA,properties);
					if (tra.getDom() != null) {
						properties.put(DOM , tra.getDom());
						createLine(DOM,properties);
					}
					if (tra.getLdd() != null) {
						properties.put(LDD , tra.getLdd());
						createLine(LDD ,properties);
					}
					if (tra.getDatosIT() != null) {
						for (DIT dit: tra.getDatosIT()) {
							properties.put(DIT , dit);
							createLine(DIT,properties);
						}
					}
					if (tra.getDec() != null) {
						properties.put(DEC , tra.getDec());
						createLine(DEC ,properties);
					}
					if (tra.getPartesConfirmacion() != null) {
						for (ODP odp: tra.getPartesConfirmacion()) {
							properties.put(ODP , odp);
							createLine(ODP,properties);
						}
					}
				}
			}
			properties.put(ETF, eti.getEtf());
			createLine(ETF,properties);

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
		writeErrorsFile();
		return exceptions;
	}
	
}
