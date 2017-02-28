package com.esferalia.aon.file.seres.connect;

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
import com.esferalia.aon.file.seres.connect.sales.v2.data.RECTL;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1T;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1P;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1I;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1V;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1D;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1L;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1U;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1G;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1E;

public class ConnectSales extends AbstractFileFiller {
	
	private static String RECTL = "RECTL";
	private static String ERE1C = "ERE1C";
	private static String ERE1T = "ERE1T";
	private static String ERE1P = "ERE1P";
	private static String ERE1I = "ERE1I";
	private static String ERE1V = "ERE1V";
	private static String ERE1D = "ERE1D";
	private static String ERE1L = "ERE1L";
	private static String ERE1U = "ERE1U";
	private static String ERE1G = "ERE1G";
	private static String ERE1E = "ERE1E";
	
	
	private RECTL rectl;
	
	public ConnectSales(RECTL rectl, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		if (rectl == null)  {
			throw new IllegalArgumentException("El registro RECTL no puede ser nulo!");
		}
		this.rectl = rectl;
		
		InputStream input = null;
		input = ConnectSales.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/sales/v2/xml/RECTL.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSales.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/sales/v2/xml/ERE1C.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSales.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/sales/v2/xml/ERE1T.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSales.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/sales/v2/xml/ERE1P.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSales.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/sales/v2/xml/ERE1I.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSales.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/sales/v2/xml/ERE1V.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSales.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/sales/v2/xml/ERE1D.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSales.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/sales/v2/xml/ERE1L.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSales.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/sales/v2/xml/ERE1U.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSales.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/sales/v2/xml/ERE1G.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSales.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/sales/v2/xml/ERE1E.xml");
		DiskRegisterLoader.load(input, manager);
	}
	
	@Override
	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			
			properties.put(RECTL, rectl);
			createLine(RECTL, properties);
			
			if(rectl.ere1c==null) {
				Fd0Exception e = new Fd0Exception( "ERE1C", "Cabecera. La entidad 'ERE1C' es obligatoria");
				exceptions.add (e);
			} else {				
				properties.put(ERE1C, rectl.ere1c);
				createLine(ERE1C, properties);
			}
			
			for (ERE1T value: rectl.ere1tList) {
				properties.put(ERE1T, value);
				createLine(ERE1T, properties);
			}

			if(rectl.ere1pList==null || rectl.ere1pList.isEmpty()) {
				Fd0Exception e = new Fd0Exception( "ERE1P", "Información de partes. La entidad 'ERE1P' es obligatoria");
				exceptions.add (e);
			} else {					
				for (ERE1P value: rectl.ere1pList) {
					properties.put(ERE1P, value);
					createLine(ERE1P,properties);
				}
			}

			for (ERE1I value: rectl.ere1iList) {
				properties.put(ERE1I, value);
				createLine(ERE1I,properties);
			}
			
			for (ERE1V value: rectl.ere1vList) {
				properties.put(ERE1V, value);
				createLine(ERE1V,properties);
			}
			
			for (ERE1D value: rectl.ere1dList) {
				properties.put(ERE1D, value);
				createLine(ERE1D,properties);
			}
			
			if(rectl.ere1lList==null || rectl.ere1lList.isEmpty()) {
				Fd0Exception e = new Fd0Exception( "ERE1L", "Línea detalle. La entidad 'ERE1L' es obligatoria");
				exceptions.add (e);
			} else {
				for (ERE1L value: rectl.ere1lList) {
					properties.put(ERE1L, value);
					createLine(ERE1L,properties);
				}
			}

			for (ERE1U value: rectl.ere1uList) {
				properties.put(ERE1U, value);
				createLine(ERE1U,properties);
			}
			
			for (ERE1G value: rectl.ere1gList) {
				properties.put(ERE1G, value);
				createLine(ERE1G,properties);
			}
			
			for (ERE1E value: rectl.ere1eList) {
				properties.put(ERE1E, value);
				createLine(ERE1E,properties);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(),rectl.toString());
				exceptions.add (e);
			}
		}
		output.flush();
//		writeErrorsFile();
		return exceptions;
	}
		
}
