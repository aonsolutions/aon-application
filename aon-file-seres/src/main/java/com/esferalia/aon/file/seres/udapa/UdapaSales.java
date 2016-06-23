package com.esferalia.aon.file.seres.udapa;

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
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1C;
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1D;
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1G;
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1I;
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1L;
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1T;
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1V;

public class UdapaSales extends AbstractFileFiller{
	
	private static String ERE1C = "ERE1C";
	private static String ERE1T = "ERE1T";
	private static String ERE1I = "ERE1I";
	private static String ERE1V = "ERE1V";
	private static String ERE1D = "ERE1D";
	private static String ERE1L = "ERE1L";
	private static String ERE1G = "ERE1G";
	
	private ERE1C ere1c;
	
	public UdapaSales(ERE1C ere1c, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		if (ere1c == null)  {
			throw new IllegalArgumentException("El registro ERE1C no puede ser nulo!");
		}
		this.ere1c = ere1c;
		
		InputStream input = null;
		input = UdapaSales.class.getResourceAsStream("/com/esferalia/aon/file/edi/sales/xml/ERE1C.xml");
		DiskRegisterLoader.load(input, manager);
		input = UdapaSales.class.getResourceAsStream("/com/esferalia/aon/file/edi/sales/xml/ERE1D.xml");
		DiskRegisterLoader.load(input, manager);
		input = UdapaSales.class.getResourceAsStream("/com/esferalia/aon/file/edi/sales/xml/ERE1G.xml");
		DiskRegisterLoader.load(input, manager);
		input = UdapaSales.class.getResourceAsStream("/com/esferalia/aon/file/edi/sales/xml/ERE1I.xml");
		DiskRegisterLoader.load(input, manager);
		input = UdapaSales.class.getResourceAsStream("/com/esferalia/aon/file/edi/sales/xml/ERE1L.xml");
		DiskRegisterLoader.load(input, manager);
		input = UdapaSales.class.getResourceAsStream("/com/esferalia/aon/file/edi/sales/xml/ERE1T.xml");
		DiskRegisterLoader.load(input, manager);
		input = UdapaSales.class.getResourceAsStream("/com/esferalia/aon/file/edi/sales/xml/ERE1V.xml");
		DiskRegisterLoader.load(input, manager);
	}
	
	@Override
	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			
			properties.put(ERE1C, ere1c);
			createLine(ERE1C, properties);
			
			for (ERE1T value: ere1c.ere1tList) {
				properties.put(ERE1T, value);
				createLine(ERE1T, properties);
			}
			
			for (ERE1I value: ere1c.ere1iList) {
				properties.put(ERE1I, value);
				createLine(ERE1I,properties);
			}
			
			for (ERE1V value: ere1c.ere1vList) {
				properties.put(ERE1V, value);
				createLine(ERE1V,properties);
			}
			
			for (ERE1D value: ere1c.ere1dList) {
				properties.put(ERE1D, value);
				createLine(ERE1D,properties);
			}
			
			if(ere1c.ere1lList==null || ere1c.ere1lList.isEmpty()) {
				Fd0Exception e = new Fd0Exception( "ERE1L", "La entidad ERE1L es obligatoria");
				exceptions.add (e);
			} else {
				for (ERE1L value: ere1c.ere1lList) {
					properties.put(ERE1L, value);
					createLine(ERE1L,properties);
				}
			}
			
			for (ERE1G value: ere1c.ere1gList) {
				properties.put(ERE1G, value);
				createLine(ERE1G,properties);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(),ere1c.toString());
				exceptions.add (e);
			}
		}
		output.flush();
		writeErrorsFile();
		return exceptions;
	}
		
}
