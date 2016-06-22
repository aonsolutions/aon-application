package com.esferalia.aon.file.seres.util.reader.udapa;

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

import com.esferalia.aon.file.seres.udapa.sales.data.ERE1C;
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1D;
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1G;
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1I;
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1L;
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1T;
import com.esferalia.aon.file.seres.udapa.sales.data.ERE1V;

public class UdapaSalesReader {
	
	public static final String CHARSET_ENCODING = "ISO-8859-1";
	
	private static String ERE1C = "ERE1C";
	private static String ERE1T = "ERE1T";
	private static String ERE1I = "ERE1I";
	private static String ERE1V = "ERE1V";
	private static String ERE1D = "ERE1D";
	private static String ERE1L = "ERE1L";
	private static String ERE1G = "ERE1G";
	
	

	public ERE1C readFile(InputStream input) throws IOException {
		return readFile(input, CHARSET_ENCODING);
	}
	
	public ERE1C readFile(InputStream input, String encoding) throws IOException {
		
		ERE1C ere1c = new ERE1C();
		List<ERE1T> ere1tList = new ArrayList<ERE1T>();
		List<ERE1I> ere1iList = new ArrayList<ERE1I>();
		List<ERE1V> ere1vList = new ArrayList<ERE1V>();
		List<ERE1D> ere1dList = new ArrayList<ERE1D>();
		List<ERE1L> ere1lList = new ArrayList<ERE1L>();
		List<ERE1G> ere1gList = new ArrayList<ERE1G>();
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, encoding));
		String currentLine;
		
		while((currentLine = reader.readLine()) != null) {
			if(currentLine.startsWith(ERE1C)){
				ere1c.parse(currentLine);
			} else if(currentLine.startsWith(ERE1T)){
				ERE1T ere1t = new ERE1T();
				ere1t.parse(currentLine);
				ere1tList.add(ere1t);
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
			} else if(currentLine.startsWith(ERE1G)){
				ERE1G ere1g = new ERE1G();
				ere1g.parse(currentLine);
				ere1gList.add(ere1g);
			}
		}
		
		ere1c.ere1tList = ere1tList;
		ere1c.ere1iList = ere1iList;
		ere1c.ere1vList = ere1vList;
		ere1c.ere1dList = ere1dList;
		ere1c.ere1lList = ere1lList;
		ere1c.ere1gList = ere1gList;
		
		return ere1c;
	}
	
	public static void main(String[] args) {
		UdapaSalesReader reader = new UdapaSalesReader();
		
		String FILE = String.format("%1$s/Descargas/EDI/udapa", System.getProperty("user.home"));
		String fileName = "ORDERS EROSKI";
		if (Files.exists(Paths.get(FILE + fileName))) {
			try {
				ERE1C value = reader.readFile(new FileInputStream(FILE + fileName), CHARSET_ENCODING);
				value.getCabecera();
			} catch (FileNotFoundException e) {
				// nada
			} catch (IOException e) {
				// nada
			}
		}
		
	}
	
	
	
}
