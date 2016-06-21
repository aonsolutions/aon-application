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
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCC;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCD;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCE;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCI;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCL;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCT;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCU;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCV;

public class UdapaInvoice extends AbstractFileFiller{
	
	private static String SINCC = "SINCC";
	private static String SINCT = "SINCT";
	private static String SINCV = "SINCV";
	private static String SINCD = "SINCD";
	private static String SINCL = "SINCL";
	private static String SINCU = "SINCU";
	private static String SINCE = "SINCE";
	private static String SINCI = "SINCI";
	
	private SINCC sincc;	
	
	public UdapaInvoice(SINCC sincc, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		if (sincc == null)  {
			throw new IllegalArgumentException("El registro SINCC no puede ser nulo!");
		}
		this.sincc = sincc;
		
		InputStream input = null;
		input = UdapaInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/udapa/invoice/xml/SINCC.xml");
		DiskRegisterLoader.load(input, manager);
		input = UdapaInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/udapa/invoice/xml/SINCT.xml");
		DiskRegisterLoader.load(input, manager);
		input = UdapaInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/udapa/invoice/xml/SINCV.xml");
		DiskRegisterLoader.load(input, manager);
		input = UdapaInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/udapa/invoice/xml/SINCD.xml");
		DiskRegisterLoader.load(input, manager);
		input = UdapaInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/udapa/invoice/xml/SINCL.xml");
		DiskRegisterLoader.load(input, manager);
		input = UdapaInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/udapa/invoice/xml/SINCU.xml");
		DiskRegisterLoader.load(input, manager);
		input = UdapaInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/udapa/invoice/xml/SINCE.xml");
		DiskRegisterLoader.load(input, manager);
		input = UdapaInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/udapa/invoice/xml/SINCI.xml");
		DiskRegisterLoader.load(input, manager);
	}
	
	@Override
	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			
			properties.put(SINCC , sincc);
			createLine(SINCC, properties);
			
			for (SINCT value: sincc.sinctList) {
				properties.put(SINCT, value);
				createLine(SINCT, properties);
			}
			for (SINCV value: sincc.sincvList) {
				properties.put(SINCV, value);
				createLine(SINCV, properties);
			}
			for (SINCD value: sincc.sincdList) {
				properties.put(SINCD, value);
				createLine(SINCD, properties);
			}
			for (SINCL value: sincc.sinclList) {
				properties.put(SINCL, value);
				createLine(SINCL, properties);
			}
			for (SINCU value: sincc.sincuList) {
				properties.put(SINCU, value);
				createLine(SINCU, properties);
			}
			for (SINCE value: sincc.sinceList) {
				properties.put(SINCE, value);
				createLine(SINCE, properties);
			}
			for (SINCI value: sincc.sinciList) {
				properties.put(SINCI, value);
				createLine(SINCI, properties);
			}
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(),sincc.toString());
				exceptions.add (e);
			}
		}
		output.flush();
		writeErrorsFile();
		return exceptions;
	}
	
}
