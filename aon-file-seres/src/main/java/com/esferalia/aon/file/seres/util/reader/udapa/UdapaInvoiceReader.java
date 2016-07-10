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

import com.esferalia.aon.file.seres.udapa.invoice.data.SINCC;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCD;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCE;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCI;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCL;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCT;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCU;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCV;

public class UdapaInvoiceReader {
	
	public static final String CHARSET_ENCODING = "ISO-8859-1";

	private static String SINCC = "SINCC";
	
	private static String SINCT = "SINCT";
	private static String SINCV = "SINCV";
	private static String SINCD = "SINCD";
	private static String SINCL = "SINCL";
	private static String SINCU = "SINCU";
	private static String SINCE = "SINCE";
	private static String SINCI = "SINCI";
	
	
	public SINCC readFile(InputStream input) throws IOException {
		return readFile(input, CHARSET_ENCODING);
	}
	
	public SINCC readFile(InputStream input, String encoding) throws IOException {
		
		SINCC sincc = new SINCC();
		
		List<SINCT> sinctList = new ArrayList<>();
		List<SINCV> sincvList = new ArrayList<>();
		List<SINCD> sincdList = new ArrayList<>();
		List<SINCL> sinclList = new ArrayList<>();
		List<SINCU> sincuList = new ArrayList<>();
		List<SINCE> sinceList = new ArrayList<>();
		List<SINCI> sinciList = new ArrayList<>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, encoding));
		String currentLine;
		
		while((currentLine = reader.readLine()) != null) {
			if(currentLine.startsWith(SINCC)){
				sincc.parse(currentLine);
			} else if(currentLine.startsWith(SINCT)){
				SINCT value = new SINCT();
				value.parse(currentLine);
				sinctList.add(value);
			} else if(currentLine.startsWith(SINCV)){
				SINCV value = new SINCV();
				value.parse(currentLine);
				sincvList.add(value);
			} else if(currentLine.startsWith(SINCD)){
				SINCD value = new SINCD();
				value.parse(currentLine);
				sincdList.add(value);
			} else if(currentLine.startsWith(SINCL)){
				SINCL value = new SINCL();
				value.parse(currentLine);
				sinclList.add(value);
			} else if(currentLine.startsWith(SINCU)){
				SINCU value = new SINCU();
				value.parse(currentLine);
				sincuList.add(value);
			} else if(currentLine.startsWith(SINCE)){
				SINCE value = new SINCE();
				value.parse(currentLine);
				sinceList.add(value);
			} else if(currentLine.startsWith(SINCI)){
				SINCI value = new SINCI();
				value.parse(currentLine);
				sinciList.add(value);
			}
		}
		
		sincc.sinctList = sinctList;
		sincc.sincvList = sincvList;
		sincc.sincdList = sincdList;
		sincc.sinclList = sinclList;
		sincc.sincuList = sincuList;
		sincc.sinceList = sinceList;
		sincc.sinciList = sinciList;
		
		return sincc;
	}
	
	public static void main(String[] args) {
		UdapaInvoiceReader reader = new UdapaInvoiceReader();
		
		String FILE = String.format("%1$s/Descargas/EDI/udapa/", System.getProperty("user.home"));
		String fileName = "Fichero de ejemplo de facturas UDAPA.txt";
		if (Files.exists(Paths.get(FILE + fileName))) {
			try {
				SINCC value = reader.readFile(new FileInputStream(FILE + fileName), CHARSET_ENCODING);
				value.getTipoFactura_325_380_381_383_385_();
			} catch (FileNotFoundException e) {
				// nada
			} catch (IOException e) {
				// nada
			}
		}
		
	}
	
	
}
