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

import com.esferalia.aon.file.seres.connect.income.v2.data.RECTL;
import com.esferalia.aon.file.seres.connect.income.v2.data.RECAP;
import com.esferalia.aon.file.seres.connect.income.v2.data.RECAE;
import com.esferalia.aon.file.seres.connect.income.v2.data.RECAL;
import com.esferalia.aon.file.seres.connect.income.v2.data.RECAV;
import com.esferalia.aon.file.seres.connect.income.v2.data.RECAB;

//TODO
public class ConnectIncome extends AbstractFileFiller{
	
	private static String RECTL = "RECTL";
	private static String RECAC = "RECAC";
	private static String RECAP = "RECAP";
	private static String RECAE = "RECAE";
	private static String RECAL = "RECAL";
	private static String RECAV = "RECAV";
	private static String RECAB = "RECAB";
	
	
	
	private RECTL rectl;
	
	public ConnectIncome(RECTL rectl, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		if (rectl == null)  {
			throw new IllegalArgumentException("El registro RECTL no puede ser nulo!");
		}
		this.rectl = rectl;
		
		InputStream input = null;
		input = ConnectIncome.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/income/v2/xml/RECTL.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectIncome.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/income/v2/xml/RECAC.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectIncome.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/income/v2/xml/RECAP.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectIncome.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/income/v2/xml/RECAE.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectIncome.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/income/v2/xml/RECAL.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectIncome.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/income/v2/xml/RECAV.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectIncome.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/income/v2/xml/RECAB.xml");
		DiskRegisterLoader.load(input, manager);
	}
	
	@Override
	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			
			properties.put(RECTL, rectl);
			createLine(RECTL, properties);
			
			if(rectl.recac==null) {
				Fd0Exception e = new Fd0Exception( "RECAC", "Cabecera. La entidad 'RECAC' es obligatoria");
				exceptions.add (e);
			} else {
				properties.put(RECAC, rectl.recac);
				createLine(RECAC, properties);
			}
			
			if(rectl.recapList==null || rectl.recapList.isEmpty()) {
				Fd0Exception e = new Fd0Exception( "RECAP", "Información partes cabecera. La entidad 'RECAP' es obligatoria");
				exceptions.add (e);
			} else {
				for (RECAP value: rectl.recapList) {
					properties.put(RECAP, value);
					createLine(RECAP, properties);
				}
			}
			if(rectl.recaeList==null || rectl.recaeList.isEmpty()) {
				Fd0Exception e = new Fd0Exception( "RECAE", "Embalajes confirmación de recepción. La entidad 'RECAEP' es obligatoria");
				exceptions.add (e);
			} else {
				for (RECAE value: rectl.recaeList) {
					properties.put(RECAE, value);
					createLine(RECAE, properties);
				}
			}
			if(rectl.recalList==null || rectl.recalList.isEmpty()) {
				Fd0Exception e = new Fd0Exception( "RECAL", "Detalle confirmación de recepción. La entidad 'RECAL' es obligatoria");
				exceptions.add (e);
			} else {
				for (RECAL value: rectl.recalList) {
					properties.put(RECAL, value);
					createLine(RECAL, properties);
				}
			}
			for (RECAV value: rectl.recavList) {
				properties.put(RECAV, value);
				createLine(RECAV, properties);
			}
			for (RECAB value: rectl.recabList) {
				properties.put(RECAB, value);
				createLine(RECAB, properties);
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
