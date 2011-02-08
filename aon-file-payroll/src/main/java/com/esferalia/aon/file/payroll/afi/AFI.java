package com.esferalia.aon.file.payroll.afi;

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
import com.esferalia.aon.file.payroll.afi.data.ETI;
import com.esferalia.aon.file.payroll.afi.data.EMP;
import com.esferalia.aon.file.payroll.afi.data.TRA;
//import com.esferalia.aon.file.payroll.fdi.data.DIT;

public class AFI extends AbstractFileFiller{

	/**
	 * Etiqueta de inicio
	 */
	private static String ETI = "ETI";
	/**
	 * Etiqueta de fin
	 */
	private static String ETF = "ETF";

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
	
	
	private ETI eti;
	
	public AFI(ETI eti, String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		super(filePath);
		if (eti == null)  {
			throw new IllegalArgumentException("El registro ETI no puede ser nulo!");
		}
		this.eti = eti;
		InputStream input = AFI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/DEC.xml");
		DiskRegisterLoader.load(input, manager);
//		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/DIT.xml");
//		DiskRegisterLoader.load(input, manager);
//		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/DOM.xml");
//		DiskRegisterLoader.load(input, manager);
//		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/EMP.xml");
//		DiskRegisterLoader.load(input, manager);
//		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/ETF.xml");
//		DiskRegisterLoader.load(input, manager);
//		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/ETI.xml");
//		DiskRegisterLoader.load(input, manager);
//		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/LDD.xml");
//		DiskRegisterLoader.load(input, manager);
//		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/ODP.xml");
//		DiskRegisterLoader.load(input, manager);
//		input = FDI.class.getResourceAsStream("/com/esferalia/aon/file/payroll/fdi/xml/TRA.xml");
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
				if (!ObjectUtils.equals(empresa, emp.getNumero())) {
					++numEmp;
					properties.put(EMP , emp);
					createLine(EMP,properties);
					++numTotal;
					empresa = emp.getNumero();
				}
//				for (TRA tra: emp.getTrabajadores()) {
//					properties.put(TRA , tra);
//					createLine(TRA,properties);
//					++numTotal;
//					if (tra.getDom() != null) {
//						properties.put(DOM , tra.getDom());
//						createLine(DOM,properties);
//						++numTotal;
//					}
//					if (tra.getLdd() != null) {
//						properties.put(LDD , tra.getLdd());
//						createLine(LDD ,properties);
//						++numTotal;
//					}
//					if (tra.getDatosIT() != null) {
//						for (DIT dit: tra.getDatosIT()) {
//							properties.put(DIT , dit);
//							createLine(DIT,properties);
//							++numTotal;
//							if ("PB ".equals(dit.getAccion())) {
//								if (dit.getDec() != null) {
//									properties.put(DEC , dit.getDec());
//									createLine(DEC ,properties);
//									++numTotal;
//								}
//							}
//							if ("PC ".equals(dit.getAccion())) {
//								if (dit.getOdp() != null) {
//									properties.put(ODP , dit.getOdp());
//									createLine(ODP ,properties);
//									++numTotal;
//								}
//							}
//						}
//					}
//				}
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
