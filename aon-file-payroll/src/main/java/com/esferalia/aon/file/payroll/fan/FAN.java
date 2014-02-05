package com.esferalia.aon.file.payroll.fan;




import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.format.model.Fd0Exception;
import com.esferalia.aon.file.payroll.fan.data.DAT;
import com.esferalia.aon.file.payroll.fan.data.EDL;
import com.esferalia.aon.file.payroll.fan.data.EDT;
import com.esferalia.aon.file.payroll.fan.data.EMP;
import com.esferalia.aon.file.payroll.fan.data.ETI;
import com.esferalia.aon.file.payroll.fan.data.TCT;
import com.esferalia.aon.file.payroll.fan.data.TRA;

public class FAN extends AbstractFileFiller{
	
	private static String ETI = "ETI";
	private static String EMP = "EMP";
	private static String RZS = "RZS";
//	private static String CER = "CER";
	private static String EXC = "EXC";
	private static String TRA = "TRA";
	private static String AYN = "AYN";
	private static String DAT = "DAT";
	private static String EDL = "EDL";
	private static String TCT = "TCT";
	private static String EDT = "EDT";
	private static String MPG = "MPG";
	private static String ETF = "ETF";
	
	private ETI eti;
	
	public FAN(ETI eti, String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		super(filePath);
		if (eti == null)  {
			throw new IllegalArgumentException("El registro FAN no puede ser nulo!");
		}
		this.eti = eti;
		InputStream input = FAN.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fan/xml/ETI.xml");
		DiskRegisterLoader.load(input, manager);
		input = FAN.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fan/xml/EMP.xml");
		DiskRegisterLoader.load(input, manager);
		input = FAN.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fan/xml/RZS.xml");
		DiskRegisterLoader.load(input, manager);
//		input = FAN.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fan/xml/CER.xml");
//		DiskRegisterLoader.load(input, manager);
		input = FAN.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fan/xml/EXC.xml");
		DiskRegisterLoader.load(input, manager);
		input = FAN.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fan/xml/TRA.xml");
		DiskRegisterLoader.load(input, manager);
		input = FAN.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fan/xml/AYN.xml");
		DiskRegisterLoader.load(input, manager);
		input = FAN.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fan/xml/DAT.xml");
		DiskRegisterLoader.load(input, manager);
		input = FAN.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fan/xml/EDL.xml");
		DiskRegisterLoader.load(input, manager);
		input = FAN.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fan/xml/TCT.xml");
		DiskRegisterLoader.load(input, manager);
		input = FAN.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fan/xml/EDT.xml");
		DiskRegisterLoader.load(input, manager);
		input = FAN.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fan/xml/MPG.xml");
		DiskRegisterLoader.load(input, manager);
		input = FAN.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fan/xml/ETF.xml");
		DiskRegisterLoader.load(input, manager);
	}
	
	@Override
	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			properties.put(ETI, eti);
			createLine(ETI,properties);
			int numEmp = 0;
			int numTotal = 0;
			String empresa = null;
			for (EMP emp: eti.getEmpresas()) {
//				if (!ObjectUtils.equals(empresa, emp.getNumeroIdentificacion())) {
					++numEmp;
					properties.put(EMP , emp);
					createLine(EMP,properties);
					++numTotal;
					empresa = emp.getNumeroIdentificacion();
//				}
				if (emp.getRzs() != null) {
					properties.put(RZS , emp.getRzs());
					createLine(RZS,properties);
					++numTotal;
				}
				if (emp.getExc() != null) {
					properties.put(EXC , emp.getExc());
					createLine(EXC,properties);
					++numTotal;
				}
				for (TRA tra: emp.getTrabajadores()) {
					properties.put(TRA , tra);
					createLine(TRA,properties);
					++numTotal;
					if (tra.getAyn() != null) {
						properties.put(AYN , tra.getAyn());
						createLine(AYN ,properties);
						++numTotal;
					}
					for (DAT dat: tra.getDat()) {
						properties.put(DAT , dat);
						createLine(DAT,properties);
						++numTotal;
						if (dat.getEdl() != null) {
							for (EDL edl: dat.getEdl().values()) {
								properties.put(EDL , edl);
								createLine(EDL,properties);
								++numTotal;
							}
						}
					}
				}
				for (TCT tct: emp.getTcTotales()) {
					properties.put(TCT , tct);
					createLine(TCT,properties);
					++numTotal;
				}
				for (EDT edt: emp.getEdt().values()) {
					properties.put(EDT , edt);
					createLine(EDT,properties);
					++numTotal;
				}
				if (emp.getMpg() != null) {
					properties.put(MPG , emp.getMpg());
					createLine(MPG,properties);
					++numTotal;
				}
			}
			eti.getEtf().setContador(numEmp);
			eti.getEtf().setContadorTotal(numTotal);
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
