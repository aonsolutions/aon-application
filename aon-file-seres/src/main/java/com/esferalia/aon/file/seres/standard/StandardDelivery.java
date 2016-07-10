package com.esferalia.aon.file.seres.standard;

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
import com.esferalia.aon.file.seres.standard.delivery.data.RECTL;
import com.esferalia.aon.file.seres.standard.delivery.data.SEH1B;
import com.esferalia.aon.file.seres.standard.delivery.data.SEH1D;
import com.esferalia.aon.file.seres.standard.delivery.data.SEH1G;
import com.esferalia.aon.file.seres.standard.delivery.data.SEH1L;
import com.esferalia.aon.file.seres.standard.delivery.data.SEH1P;

public class StandardDelivery extends AbstractFileFiller{
	
	private static String RECTL = "RECTL";
	private static String SEH1C = "SEH1C";
	private static String SEH1D = "SEH1D";
	private static String SEH1P = "SEH1P";
	private static String SEH1L = "SEH1L";
	private static String SEH1G = "SEH1G";
	private static String SEH1B = "SEH1B";
	
	private RECTL rectl;
	
	public StandardDelivery(RECTL rectl, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		if (rectl == null)  {
			throw new IllegalArgumentException("El registro SEH1C no puede ser nulo!");
		}
		this.rectl = rectl;
		
		InputStream input = null;
		input = StandardDelivery.class.getResourceAsStream("/com/esferalia/aon/file/seres/standard/delivery/xml/SEH1C.xml");
		DiskRegisterLoader.load(input, manager);
		input = StandardDelivery.class.getResourceAsStream("/com/esferalia/aon/file/seres/standard/delivery/xml/SEH1D.xml");
		DiskRegisterLoader.load(input, manager);
		input = StandardDelivery.class.getResourceAsStream("/com/esferalia/aon/file/seres/standard/delivery/xml/SEH1P.xml");
		DiskRegisterLoader.load(input, manager);
		input = StandardDelivery.class.getResourceAsStream("/com/esferalia/aon/file/seres/standard/delivery/xml/SEH1L.xml");
		DiskRegisterLoader.load(input, manager);
		input = StandardDelivery.class.getResourceAsStream("/com/esferalia/aon/file/seres/standard/delivery/xml/SEH1G.xml");
		DiskRegisterLoader.load(input, manager);
		input = StandardDelivery.class.getResourceAsStream("/com/esferalia/aon/file/seres/standard/delivery/xml/SEH1B.xml");
		DiskRegisterLoader.load(input, manager);
	}
	
	@Override
	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			
			properties.put(RECTL, rectl);
			createLine(RECTL, properties);
			
			properties.put(SEH1C , rectl.seh1c);
			createLine(SEH1C, properties);
			
			for (SEH1D value: rectl.seh1dList) {
				properties.put(SEH1D, value);
				createLine(SEH1D, properties);
			}
			for (SEH1P value: rectl.seh1pList) {
				properties.put(SEH1P, value);
				createLine(SEH1P, properties);
			}
			for (SEH1L value: rectl.seh1lList) {
				properties.put(SEH1L, value);
				createLine(SEH1L, properties);
			}
			for (SEH1G value: rectl.seh1gList) {
				properties.put(SEH1G, value);
				createLine(SEH1G, properties);
			}
			for (SEH1B value: rectl.seh1bList) {
				properties.put(SEH1B, value);
				createLine(SEH1B, properties);
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
		writeErrorsFile();
		return exceptions;
	}
	
}
