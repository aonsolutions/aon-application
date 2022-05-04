package com.esferalia.aon.file.seres.connect2;

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
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPD;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPE;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPG;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPI;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPL;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPP;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPT;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPU;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPV;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.RECTL;

public class ConnectSalesResponse extends AbstractFileFiller{
	
	private static String RECTL = "RECTL";
	private static String ORSPC = "ORSPC";
	private static String ORSPT = "ORSPT";
	private static String ORSPP = "ORSPP";
	private static String ORSPI = "ORSPI";
	private static String ORSPV = "ORSPV";
	private static String ORSPD = "ORSPD";
	private static String ORSPL = "ORSPL";
	private static String ORSPU = "ORSPU";
	private static String ORSPG = "ORSPG";
	private static String ORSPE = "ORSPE";
	
	
	
	private RECTL rectl;
	
	public ConnectSalesResponse(RECTL rectl, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		if (rectl == null)  {
			throw new IllegalArgumentException("El registro RECTL no puede ser nulo!");
		}
		this.rectl = rectl;
		
		InputStream input = null;
		input = ConnectSalesResponse.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect2/income/v2/xml/RECTL.xml");
		DiskRegisterLoader.load(input, manager);

		input = ConnectSalesResponse.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect2/income/v2/xml/ORSPC.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSalesResponse.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect2/income/v2/xml/ORSPT.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSalesResponse.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect2/income/v2/xml/ORSPI.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSalesResponse.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect2/income/v2/xml/ORSPV.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSalesResponse.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect2/income/v2/xml/ORSPD.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSalesResponse.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect2/income/v2/xml/ORSPL.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSalesResponse.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect2/income/v2/xml/ORSPU.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSalesResponse.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect2/income/v2/xml/ORSPG.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectSalesResponse.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect2/income/v2/xml/ORSPE.xml");
		DiskRegisterLoader.load(input, manager);
	}
	
	@Override
	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			
			properties.put(RECTL, rectl);
			createLine(RECTL, properties);
			
			if(rectl.orspc==null) {
				Fd0Exception e = new Fd0Exception( "ORSPC", "Cabecera. La entidad 'ORSPC' es obligatoria");
				exceptions.add (e);
			} else {
				properties.put(ORSPC, rectl.orspc);
				createLine(ORSPC, properties);
			}
			
			for (ORSPT value: rectl.orsptList) {
				properties.put(ORSPT, value);
				createLine(ORSPT, properties);
			}
				
			if(rectl.orsppList==null || rectl.orsppList.isEmpty()) {
				Fd0Exception e = new Fd0Exception( "ORSPP", "Información de partes. La entidad 'ORSPP' es obligatoria");
				exceptions.add (e);
			} else {
				for (ORSPP value: rectl.orsppList) {
					properties.put(ORSPP, value);
					createLine(ORSPP, properties);
				}
			}
			
			for (ORSPI value: rectl.orspiList) {
				properties.put(ORSPI, value);
				createLine(ORSPI, properties);
			}
			
			for (ORSPV value: rectl.orspvList) {
				properties.put(ORSPV, value);
				createLine(ORSPV, properties);
			}

			for (ORSPD value: rectl.orspdList) {
				properties.put(ORSPD, value);
				createLine(ORSPD, properties);
			}
			
			if(rectl.orsplList==null || rectl.orsplList.isEmpty()) {
				Fd0Exception e = new Fd0Exception( "ORSPL", "Línea detalle. La entidad 'ORSPL' es obligatoria");
				exceptions.add (e);
			} else {
				for (ORSPL value: rectl.orsplList) {
					properties.put(ORSPL, value);
					createLine(ORSPL, properties);
				}
			}

			for (ORSPU value: rectl.orspuList) {
				properties.put(ORSPU, value);
				createLine(ORSPU, properties);
			}
			
			for (ORSPG value: rectl.orspgList) {
				properties.put(ORSPG, value);
				createLine(ORSPG, properties);
			}
			
			for (ORSPE value: rectl.orspeList) {
				properties.put(ORSPE, value);
				createLine(ORSPE, properties);
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
