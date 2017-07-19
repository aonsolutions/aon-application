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
import com.esferalia.aon.file.seres.connect.invoice.v4.data.RECTL;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCD;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCE;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCI;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCL;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCP;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCT;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCU;
import com.esferalia.aon.file.seres.connect.invoice.v4.data.SINCV;

public class ConnectInvoice extends AbstractFileFiller {
	
	private static String RECTL = "RECTL";
	private static String SINCC = "SINCC";
	private static String SINCP = "SINCP";
	private static String SINCT = "SINCT";
	private static String SINCV = "SINCV";
	private static String SINCD = "SINCD";
	private static String SINCL = "SINCL";
	private static String SINCU = "SINCU";
	private static String SINCE = "SINCE";
	private static String SINCI = "SINCI";
	
	
	private RECTL rectl;	
	
	public ConnectInvoice(RECTL rectl, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		if (rectl == null)  {
			throw new IllegalArgumentException("El registro RECTL no puede ser nulo!");
		}
		this.rectl = rectl;
		
		InputStream input = null;
		input = ConnectInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/invoice/v4/xml/RECTL.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/invoice/v4/xml/SINCC.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/invoice/v4/xml/SINCP.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/invoice/v4/xml/SINCT.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/invoice/v4/xml/SINCV.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/invoice/v4/xml/SINCD.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/invoice/v4/xml/SINCL.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/invoice/v4/xml/SINCU.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/invoice/v4/xml/SINCE.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectInvoice.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/invoice/v4/xml/SINCI.xml");
		DiskRegisterLoader.load(input, manager);
	}
	
	@Override
	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			
			properties.put(RECTL, rectl);
			createLine(RECTL, properties);
			
			if(rectl.sincc==null) {
				Fd0Exception e = new Fd0Exception( "SINCC", "Cabecera. La entidad 'SINCC' es obligatoria");
				exceptions.add (e);
			} else {				
				properties.put(SINCC , rectl.sincc);
				createLine(SINCC, properties);
			}
			
			if(rectl.sincpList==null || rectl.sincpList.isEmpty()) {
				Fd0Exception e = new Fd0Exception( "SINCP", "Información partes involucradas. La entidad 'SINCP' es obligatoria");
				exceptions.add (e);
			} else {				
				for (SINCP value: rectl.sincpList) {
					properties.put(SINCP, value);
					createLine(SINCP, properties);
				}
			}
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
			if(rectl.sinclList==null || rectl.sinclList.isEmpty()) {
				Fd0Exception e = new Fd0Exception( "SINCL", "Línea detalle. La entidad 'SINCL' es obligatoria");
				exceptions.add (e);
			} else {				
				for (SINCL value: rectl.sinclList) {
					properties.put(SINCL, value);
					createLine(SINCL, properties);
					if(value.since!=null){
						properties.put(SINCE, value.since);
						createLine(SINCE, properties);		
					}
				}
			}
			for (SINCU value: rectl.sincuList) {
				properties.put(SINCU, value);
				createLine(SINCU, properties);
			}
//			for (SINCE value: rectl.sinceList) {
//				properties.put(SINCE, value);
//				createLine(SINCE, properties);
//			}
			if(rectl.sinciList==null || rectl.sinciList.isEmpty()) {
				Fd0Exception e = new Fd0Exception( "SINCI", "Impuestos. La entidad 'SINCI' es obligatoria");
				exceptions.add (e);
			} else {
				for (SINCI value: rectl.sinciList) {
					properties.put(SINCI, value);
					createLine(SINCI, properties);
				}
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
