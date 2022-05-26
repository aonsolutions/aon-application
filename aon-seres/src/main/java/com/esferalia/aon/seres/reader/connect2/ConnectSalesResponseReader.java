package com.esferalia.aon.seres.reader.connect2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.RECTL;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPC;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPT;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPP;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPI;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPV;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPD;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPL;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPU;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPG;
import com.esferalia.aon.file.seres.connect2.salesresponse.v2.data.ORSPE;

public class ConnectSalesResponseReader {
	
	public static final String CHARSET_ENCODING = "ISO-8859-1";
	
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
	

	public RECTL readFile(InputStream input) throws IOException {
		return readFile(input, CHARSET_ENCODING);
	}
	
	public RECTL readFile(InputStream input, String encoding) throws IOException {
		
		RECTL rectl = new RECTL();
		ORSPC orspc = new ORSPC();
		List<ORSPT> orsptList = new ArrayList<>();
		List<ORSPP> orsppList = new ArrayList<>();
		List<ORSPI> orspiList = new ArrayList<>();
		List<ORSPV> orspvList = new ArrayList<>();
		List<ORSPD> orspdList = new ArrayList<>();
		List<ORSPL> orsplList = new ArrayList<>();
		List<ORSPU> orspuList = new ArrayList<>();
		List<ORSPG> orspgList = new ArrayList<>();
		List<ORSPE> orspeList = new ArrayList<>();
		
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, encoding));
		String currentLine;
		
		while((currentLine = reader.readLine()) != null) {
			if(currentLine.startsWith(RECTL)){
				rectl.parse(currentLine);
			} else if(currentLine.startsWith(ORSPC)){
				orspc.parse(currentLine);
			} else if(currentLine.startsWith(ORSPT)){
				ORSPT orspt = new ORSPT();
				orspt.parse(currentLine);
				orsptList.add(orspt);
			} else if(currentLine.startsWith(ORSPP)){
				ORSPP orspp = new ORSPP();
				orspp.parse(currentLine);
				orsppList.add(orspp);
			} else if(currentLine.startsWith(ORSPI)){
				ORSPI orspi = new ORSPI();
				orspi.parse(currentLine);
				orspiList.add(orspi);
			} else if(currentLine.startsWith(ORSPV)){
				ORSPV orspv = new ORSPV();
				orspv.parse(currentLine);
				orspvList.add(orspv);
			} else if(currentLine.startsWith(ORSPD)){
				ORSPD orspd = new ORSPD();
				orspd.parse(currentLine);
				orspdList.add(orspd);
			} else if(currentLine.startsWith(ORSPL)){
				ORSPL orspl = new ORSPL();
				orspl.parse(currentLine);
				orsplList.add(orspl);
			} else if(currentLine.startsWith(ORSPU)){
				ORSPU orspu = new ORSPU();
				orspu.parse(currentLine);
				orspuList.add(orspu);
			} else if(currentLine.startsWith(ORSPG)){
				ORSPG orspg = new ORSPG();
				orspg.parse(currentLine);
				orspgList.add(orspg);
			} else if(currentLine.startsWith(ORSPE)){
				ORSPE orspe = new ORSPE();
				orspe.parse(currentLine);
				orspeList.add(orspe);
			}
		}
		
		rectl.orspc = orspc;
		rectl.orsptList = orsptList;
		rectl.orsppList = orsppList;
		rectl.orspiList = orspiList;
		rectl.orspvList = orspvList;
		rectl.orspdList = orspdList;
		rectl.orsplList = orsplList;
		rectl.orspuList = orspuList;
		rectl.orspgList = orspgList;
		rectl.orspeList = orspeList;
		
		return rectl;
	}
	
	public static void main(String[] args) {
		ConnectSalesResponseReader reader = new ConnectSalesResponseReader();
		
		String FILE = String.format("%1$s/Escritorio/eduardos/", System.getProperty("user.home"));
		String fileName = "sales_PV22_5050.edi";
		if (Files.exists(Paths.get(FILE + fileName))) {
			try {
				RECTL value = reader.readFile(new FileInputStream(FILE + fileName), CHARSET_ENCODING);
				value.getTipoDeMensaje();
			} catch (FileNotFoundException e) {
				// nada
			} catch (IOException e) {
				// nada
			}
		}
		
	}
	
	
	
}
