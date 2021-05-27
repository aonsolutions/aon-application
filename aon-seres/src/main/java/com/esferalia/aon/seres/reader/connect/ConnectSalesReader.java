package com.esferalia.aon.seres.reader.connect;

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

import com.esferalia.aon.file.seres.connect.sales.v2.data.RECTL;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1C;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1T;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1P;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1I;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1V;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1D;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1L;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1U;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1G;
import com.esferalia.aon.file.seres.connect.sales.v2.data.ERE1E;

public class ConnectSalesReader {
	
	public static final String CHARSET_ENCODING = "ISO-8859-1";
	
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
	

	public RECTL readFile(InputStream input) throws IOException {
		return readFile(input, CHARSET_ENCODING);
	}
	
	public RECTL readFile(InputStream input, String encoding) throws IOException {
		
		RECTL rectl = new RECTL();
		ERE1C ere1c = new ERE1C();
		List<ERE1T> ere1tList = new ArrayList<>();
		List<ERE1P> ere1pList = new ArrayList<>();
		List<ERE1I> ere1iList = new ArrayList<>();
		List<ERE1V> ere1vList = new ArrayList<>();
		List<ERE1D> ere1dList = new ArrayList<>();
		List<ERE1L> ere1lList = new ArrayList<>();
		List<ERE1U> ere1uList = new ArrayList<>();
		List<ERE1G> ere1gList = new ArrayList<>();
		List<ERE1E> ere1eList = new ArrayList<>();
		
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, encoding));
		String currentLine;
		
		while((currentLine = reader.readLine()) != null) {
			if(currentLine.startsWith(RECTL)){
				rectl.parse(currentLine);
			} else if(currentLine.startsWith(ERE1C)){
				ere1c.parse(currentLine);
			} else if(currentLine.startsWith(ERE1T)){
				ERE1T ere1t = new ERE1T();
				ere1t.parse(currentLine);
				ere1tList.add(ere1t);
			} else if(currentLine.startsWith(ERE1P)){
				ERE1P ere1p = new ERE1P();
				ere1p.parse(currentLine);
				ere1pList.add(ere1p);
			} else if(currentLine.startsWith(ERE1I)){
				ERE1I ere1i = new ERE1I();
				ere1i.parse(currentLine);
				ere1iList.add(ere1i);
			} else if(currentLine.startsWith(ERE1V)){
				ERE1V ere1v = new ERE1V();
				ere1v.parse(currentLine);
				ere1vList.add(ere1v);
			} else if(currentLine.startsWith(ERE1D)){
				ERE1D ere1d = new ERE1D();
				ere1d.parse(currentLine);
				ere1dList.add(ere1d);
			} else if(currentLine.startsWith(ERE1L)){
				ERE1L ere1l = new ERE1L();
				ere1l.parse(currentLine);
				ere1lList.add(ere1l);
			} else if(currentLine.startsWith(ERE1U)){
				ERE1U ere1u = new ERE1U();
				ere1u.parse(currentLine);
				ere1uList.add(ere1u);
			} else if(currentLine.startsWith(ERE1G)){
				ERE1G ere1g = new ERE1G();
				ere1g.parse(currentLine);
				ere1gList.add(ere1g);
			} else if(currentLine.startsWith(ERE1E)){
				ERE1E ere1e = new ERE1E();
				ere1e.parse(currentLine);
				ere1eList.add(ere1e);
			}
		}
		
		rectl.ere1c = ere1c;
		rectl.ere1tList = ere1tList;
		rectl.ere1pList = ere1pList;
		rectl.ere1iList = ere1iList;
		rectl.ere1vList = ere1vList;
		rectl.ere1dList = ere1dList;
		rectl.ere1lList = ere1lList;
		rectl.ere1uList = ere1uList;
		rectl.ere1gList = ere1gList;
		rectl.ere1eList = ere1eList;
		
		return rectl;
	}
	
	public static void main(String[] args) {
		ConnectSalesReader reader = new ConnectSalesReader();
		
		String FILE = String.format("%1$s/Descargas/EDI/udapa/", System.getProperty("user.home"));
		String fileName = "ORDERS EROSKI";
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
