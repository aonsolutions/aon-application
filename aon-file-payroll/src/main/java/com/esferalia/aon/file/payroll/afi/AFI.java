package com.esferalia.aon.file.payroll.afi;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.format.model.Fd0Exception;
import com.esferalia.aon.file.payroll.afi.data.EMP;
import com.esferalia.aon.file.payroll.afi.data.ETI;
import com.esferalia.aon.file.payroll.afi.data.TRA;

public class AFI extends AbstractFileFiller{

	/**
	 * Etiqueta de inicio
	 */
	private static String ETI = "ETI";
	
	/**
	 * Identificacion de empresa
	 */
	private static String EMP = "EMP";
	/**
	 * razon social
	 */
	private static String RZS = "RZS";
	/**
	 * explotacion y claves
	 */
	private static String EXC = "EXC";
	/**
	 * fechas de control de empresa
	 */
	private static String FCE = "FCE";
	
	/**
	 * trabajador
	 */
	private static String TRA = "TRA";
	/**
	 * apellidos y nombre
	 */
	private static String AYN = "AYN";
	/**
	 * datos personales
	 */
	private static String DAP = "DAP";
	/**
	 * cuidadanos union europea
	 */
	private static String CUE = "CUE";
	/**
	 * datos union europea
	 */
	private static String DUE = "DUE";
	/**
	 * domicilio
	 */
	private static String DOM = "DOM";
	/**
	 * localidad domicilio decodificado
	 */
	private static String LDD = "LDD";
	/**
	 * fechas de alta y baja
	 */
	private static String FAB = "FAB";
	/**
	 * datos asociados al movimiento
	 */
	private static String DAM = "DAM";
	/**
	 * datos de subcontratacion o cesion
	 */
	private static String DSC = "DSC";
	/**
	 * datos de regimen especial agrario
	 */
	private static String DRA = "DRA";
	/**
	 * fechas de control de trabajadores
	 */
	private static String FCT = "FCT";
	/**
	 * periodos de incapacidad temporal
	 */
	private static String PIT = "PIT";
	
	/**
	 * Etiqueta de fin
	 */
	private static String ETF = "ETF";
	
	
	private ETI eti;
	
	public AFI(ETI eti, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		if (eti == null)  {
			throw new IllegalArgumentException("El registro ETI no puede ser nulo!");
		}
		this.eti = eti;
		InputStream input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/ETI.xml");
		DiskRegisterLoader.load(input, manager);
		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/EMP.xml");
		DiskRegisterLoader.load(input, manager);
		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/RZS.xml");
		DiskRegisterLoader.load(input, manager);
//		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/EXC.xml");
//		DiskRegisterLoader.load(input, manager);
//		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/FCE.xml");
//		DiskRegisterLoader.load(input, manager);
		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/TRA.xml");
		DiskRegisterLoader.load(input, manager);
		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/AYN.xml");
		DiskRegisterLoader.load(input, manager);
//		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/DAP.xml");
//		DiskRegisterLoader.load(input, manager);
//		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/CUE.xml");
//		DiskRegisterLoader.load(input, manager);
//		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/DUE.xml");
//		DiskRegisterLoader.load(input, manager);
		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/DOM.xml");
		DiskRegisterLoader.load(input, manager);
//		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/LDD.xml");
//		DiskRegisterLoader.load(input, manager);
		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/FAB.xml");
		DiskRegisterLoader.load(input, manager);
//		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/DAM.xml");
//		DiskRegisterLoader.load(input, manager);
//		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/DSC.xml");
//		DiskRegisterLoader.load(input, manager);
//		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/DRA.xml");
//		DiskRegisterLoader.load(input, manager);
//		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/FCT.xml");
//		DiskRegisterLoader.load(input, manager);
//		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/PIT.xml");
//		DiskRegisterLoader.load(input, manager);
		input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/afi/xml/ETF.xml");
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
				if (!ObjectUtils.equals(empresa, emp.getNumeroIdentificacion())) {
					++numEmp;
					properties.put(EMP , emp);
					createLine(EMP,properties);
					++numTotal;
					empresa = emp.getNumeroIdentificacion();
				}
				if (emp.getRzs() != null) {
					properties.put(RZS , emp.getRzs());
					createLine(RZS,properties);
					++numTotal;
				}
				for (TRA tra: emp.getTrabajadores()) {
					properties.put(TRA , tra);
					createLine(TRA,properties);
					++numTotal;
					if (tra.getAyn() != null) {
						properties.put(AYN,tra.getAyn());
						createLine(AYN,properties);
						++numTotal;
					}
					if (tra.getFab() != null) {
						properties.put(FAB,tra.getFab());
						createLine(FAB,properties);
						++numTotal;
					}
				}
			}
			eti.getEtf().setContadorEmpresas(numEmp);
			numTotal += 2; // por segmento ETI y ETF
			eti.getEtf().setContadorSegmentos(numTotal);
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
