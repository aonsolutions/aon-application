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
import com.esferalia.aon.file.seres.standard.invoice.data.RECTL;
import com.esferalia.aon.file.seres.standard.invoice.data.SINCD;
import com.esferalia.aon.file.seres.standard.invoice.data.SINCE;
import com.esferalia.aon.file.seres.standard.invoice.data.SINCI;
import com.esferalia.aon.file.seres.standard.invoice.data.SINCL;
import com.esferalia.aon.file.seres.standard.invoice.data.SINCT;
import com.esferalia.aon.file.seres.standard.invoice.data.SINCU;
import com.esferalia.aon.file.seres.standard.invoice.data.SINCV;

public class StandardInvoice extends AbstractFileFiller{
	
	private static String RECTL = "RECTL";
	private static String SINCC = "SINCC";
	private static String SINCT = "SINCT";
	private static String SINCV = "SINCV";
	private static String SINCD = "SINCD";
	private static String SINCL = "SINCL";
	private static String SINCU = "SINCU";
	private static String SINCE = "SINCE";
	private static String SINCI = "SINCI";
	
	private RECTL rectl;	
	
	public StandardInvoice(RECTL rectl, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		if (rectl == null)  {
			throw new IllegalArgumentException("El registro SINCC no puede ser nulo!");
		}
		this.rectl = rectl;
		
		InputStream input = null;
		input = StandardInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/standard/invoice/xml/SINCC.xml");
		DiskRegisterLoader.load(input, manager);
		input = StandardInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/standard/invoice/xml/SINCT.xml");
		DiskRegisterLoader.load(input, manager);
		input = StandardInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/standard/invoice/xml/SINCV.xml");
		DiskRegisterLoader.load(input, manager);
		input = StandardInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/standard/invoice/xml/SINCD.xml");
		DiskRegisterLoader.load(input, manager);
		input = StandardInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/standard/invoice/xml/SINCL.xml");
		DiskRegisterLoader.load(input, manager);
		input = StandardInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/standard/invoice/xml/SINCU.xml");
		DiskRegisterLoader.load(input, manager);
		input = StandardInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/standard/invoice/xml/SINCE.xml");
		DiskRegisterLoader.load(input, manager);
		input = StandardInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/standard/invoice/xml/SINCI.xml");
		DiskRegisterLoader.load(input, manager);
	}
	
	@Override
	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			
			properties.put(RECTL, rectl);
			createLine(RECTL, properties);
			
			properties.put(SINCC , rectl.sincc);
			createLine(SINCC, properties);
			
			for (SINCT value: rectl.sinctList) {
				properties.put(SINCT, value);
				createLine(SINCT, properties);
			}
			for (SINCV value: rectl.sincvList) {
				properties.put(SINCV, value);
				createLine(SINCV, properties);
			}
			for (SINCD value: rectl.sincdList) {
				properties.put(SINCD, value);
				createLine(SINCD, properties);
			}
			for (SINCL value: rectl.sinclList) {
				properties.put(SINCL, value);
				createLine(SINCL, properties);
			}
			for (SINCU value: rectl.sincuList) {
				properties.put(SINCU, value);
				createLine(SINCU, properties);
			}
			for (SINCE value: rectl.sinceList) {
				properties.put(SINCE, value);
				createLine(SINCE, properties);
			}
			for (SINCI value: rectl.sinciList) {
				properties.put(SINCI, value);
				createLine(SINCI, properties);
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
