package com.code.aon.ui.marketplace.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.code.aon.marketplace.plu.BokaData;
import com.code.aon.marketplace.plu.BokaIf;
import com.code.aon.marketplace.plu.Field;
import com.code.aon.marketplace.plu.FieldUtils;
import com.code.aon.marketplace.plu.HwstIf;
import com.code.aon.marketplace.plu.PlstData;
import com.code.aon.marketplace.plu.PlstIf;
import com.code.aon.marketplace.plu.WgstData;
import com.code.aon.marketplace.plu.WgstIf;
import com.code.aon.ui.form.BasicController;

public class ExtendedScaleController extends BasicController {

	public HwstIf getHwstFieldsZiel(File f) throws IOException {
		int start = 0;
		HwstIf hwst = new HwstIf();

		BufferedReader br = new BufferedReader(new FileReader(f));
	    String line = br.readLine();
		boolean ZIEL = false;
	    while(line != null) {
	    	if (ZIEL) {
	    		if (line.startsWith("HWGN")) hwst.setHWGN(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("HWGT")) hwst.setHWGT(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("coma")) hwst.setComaSeparator(true);
		    	else if (line.startsWith("cr")) hwst.setCR();
		    	else if (line.startsWith("eol")) hwst.setEOL();
		    	else hwst.addMask(FieldUtils.getFieldType(line), FieldUtils.getFieldLength(line), FieldUtils.getFieldCode(line)); 
	    		start = start + FieldUtils.getFieldLength(line);
	    	}
	    	if (line.startsWith("$ZIEL")) ZIEL = true;
	    	line= br.readLine();
	    }
		return hwst;
	}

	public HwstIf getHwstFieldsQuelle(File f) throws IOException {
		int start = 0;
		HwstIf hwst = new HwstIf();

		BufferedReader br = new BufferedReader(new FileReader(f));
	    String line = br.readLine();
		boolean QUELLE = false;
	    while(line != null) {
	    	if (QUELLE) {
	    		if (line.startsWith("HWGN")) hwst.setHWGN(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("HWGT")) hwst.setHWGT(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("coma")) hwst.setComaSeparator(true);
		    	else if (line.startsWith("cr")) hwst.setCR();
		    	else if (line.startsWith("eol")) hwst.setEOL();
		    	else hwst.addMask(FieldUtils.getFieldType(line), FieldUtils.getFieldLength(line), FieldUtils.getFieldCode(line)); 
	    		start = start + FieldUtils.getFieldLength(line);
	    	}
	    	if (line.startsWith("$QUELLE")) QUELLE = true;
	    	if (line.startsWith("$SWZ")) QUELLE = false;
	    	if (line.startsWith("$CODE")) QUELLE = false;
	    	line= br.readLine();
	    }
		return hwst;
	}

	public HashMap<Integer,String> getHwstData(File f, HwstIf hwst) throws IOException {
		HashMap<Integer,String> ret = new HashMap<Integer,String>();
		BufferedReader br = new BufferedReader(new FileReader(f));
	    String line = br.readLine();
	    while(line != null) {
	    	String code = line.substring(hwst.getHWGN().getStart(), hwst.getHWGN().getEnd());
	    	String desc = line.substring(hwst.getHWGT().getStart(), hwst.getHWGT().getEnd());
	    	desc = FieldUtils.clearSpecialTags(desc);
	    	ret.put(Integer.parseInt(code), desc);
	    	line = br.readLine();
	    }
		return ret;
	}

	public WgstIf getWgstFieldsZiel(File f) throws IOException {
		int start = 0;
		WgstIf wgst = new WgstIf();

		BufferedReader br = new BufferedReader(new FileReader(f));
	    String line = br.readLine();
		boolean ZIEL = false;
	    while(line != null) {
	    	if (ZIEL) {
	    		if (line.startsWith("WGNU")) wgst.setWGNU(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("HWGN")) wgst.setHWGN(new Field(start, start + FieldUtils.getFieldLength(line)));
		    	else if (line.startsWith("WGTE")) wgst.setWGTE(new Field(start, start + FieldUtils.getFieldLength(line)));
			    else if (line.startsWith("coma")) wgst.setComaSeparator(true);
			    else if (line.startsWith("cr")) wgst.setCR();
			    else if (line.startsWith("eol")) wgst.setEOL();
			    else wgst.addMask(FieldUtils.getFieldType(line), FieldUtils.getFieldLength(line), FieldUtils.getFieldCode(line));
	    		start = start + FieldUtils.getFieldLength(line);
	    	}
	    	if (line.startsWith("$ZIEL")) ZIEL = true;
	    	line= br.readLine();
	    }
		return wgst;
	}

	public WgstIf getWgstFieldsQuelle(File f) throws IOException {
		int start = 0;
		WgstIf wgst = new WgstIf();

		BufferedReader br = new BufferedReader(new FileReader(f));
	    String line = br.readLine();
		boolean QUELLE = false;
	    while(line != null) {
	    	if (QUELLE) {
	    		if (line.startsWith("WGNU")) wgst.setWGNU(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("HWGN")) wgst.setHWGN(new Field(start, start + FieldUtils.getFieldLength(line)));
		    	else if (line.startsWith("WGTE")) wgst.setWGTE(new Field(start, start + FieldUtils.getFieldLength(line)));
			    else if (line.startsWith("coma")) wgst.setComaSeparator(true);
			    else if (line.startsWith("cr")) wgst.setCR();
			    else if (line.startsWith("eol")) wgst.setEOL();
			    else wgst.addMask(FieldUtils.getFieldType(line), FieldUtils.getFieldLength(line), FieldUtils.getFieldCode(line));
	    		start = start + FieldUtils.getFieldLength(line);
	    	}
	    	if (line.startsWith("$QUELLE")) QUELLE = true;
	    	if (line.startsWith("$SWZ")) QUELLE = false;
	    	if (line.startsWith("$CODE")) QUELLE = false;
	    	line= br.readLine();
	    }
		return wgst;
	}

	public ArrayList<WgstData> getWgstData(File f, WgstIf wgst) throws IOException {
		ArrayList<WgstData> ret = new ArrayList<WgstData>();
		BufferedReader br = new BufferedReader(new FileReader(f));
	    String line = br.readLine();
	    while(line != null) {
	    	String code = line.substring(wgst.getWGNU().getStart(), wgst.getWGNU().getEnd());
	    	String family = line.substring(wgst.getHWGN().getStart(), wgst.getHWGN().getEnd());
	    	String desc = line.substring(wgst.getWGTE().getStart(), wgst.getWGTE().getEnd());
	    	desc = FieldUtils.clearSpecialTags(desc);
	    	ret.add(new WgstData(Integer.parseInt(code), Integer.parseInt(family), desc));
	    	line= br.readLine();
	    }
		return ret;
	}

	public PlstIf getPlstFieldsZiel(File f) throws IOException {
		int start = 0;
		PlstIf plst = new PlstIf();

		BufferedReader br = new BufferedReader(new FileReader(f));
	    String line = br.readLine();
		boolean ZIEL = false;
	    while(line != null) {
	    	if (ZIEL) {
	    		if (line.startsWith("PNUM")) plst.setPNUM(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("GPR1")) plst.setGPR1(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("WGNU")) plst.setWGNU(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("ECO1")) plst.setECO1(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("PLTE")) plst.setPLTE(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("coma")) plst.setComaSeparator(true);
		    	else if (line.startsWith("cr")) plst.setCR();
		    	else if (line.startsWith("eol")) plst.setEOL();
		    	else plst.addMask(FieldUtils.getFieldType(line), FieldUtils.getFieldLength(line), FieldUtils.getFieldCode(line)); 
	    		start = start + FieldUtils.getFieldLength(line);
	    	}
	    	if (line.startsWith("$ZIEL")) ZIEL = true;
	    	line= br.readLine();
	    }
		return plst;
	}

	public PlstIf getPlstFieldsQuelle(File f) throws IOException {
		int start = 0;
		PlstIf plst = new PlstIf();

		BufferedReader br = new BufferedReader(new FileReader(f));
	    String line = br.readLine();
		boolean QUELLE = false;
	    while(line != null) {
	    	if (QUELLE) {
	    		if (line.startsWith("PNUM")) plst.setPNUM(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("GPR1")) plst.setGPR1(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("WGNU")) plst.setWGNU(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("ECO1")) plst.setECO1(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("PLTE")) plst.setPLTE(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("coma")) plst.setComaSeparator(true);
		    	else if (line.startsWith("cr")) plst.setCR();
		    	else if (line.startsWith("eol")) plst.setEOL();
		    	else plst.addMask(FieldUtils.getFieldType(line), FieldUtils.getFieldLength(line), FieldUtils.getFieldCode(line)); 
	    		start = start + FieldUtils.getFieldLength(line);
	    	}
	    	if (line.startsWith("$QUELLE")) QUELLE = true;
	    	if (line.startsWith("$SWZ")) QUELLE = false;
	    	if (line.startsWith("$CODE")) QUELLE = false;
	    	line= br.readLine();
	    }
		return plst;
	}

	public ArrayList<PlstData> getPlstData(File f, PlstIf plst) throws IOException {
		ArrayList<PlstData> ret = new ArrayList<PlstData>();
		BufferedReader br = new BufferedReader(new FileReader(f));
	    String line = br.readLine();
	    while(line != null) {
	    	String code = line.substring(plst.getPNUM().getStart(), plst.getPNUM().getEnd());
	    	String price = line.substring(plst.getGPR1().getStart(), plst.getGPR1().getEnd());
	    	double price_dbl = 0;
	    	if (price.length() > 2) {
	    		price = price.substring(0, price.length() - 2) + "." + price.substring(price.length() - 2, price.length());
	    		try {
	    			price_dbl = Double.parseDouble(price);
				} catch (NumberFormatException e) {
				}  
	    	}
	    	String subfamily = line.substring(plst.getWGNU().getStart(), plst.getWGNU().getEnd());
	    	String barcode = line.substring(plst.getECO1().getStart(), plst.getECO1().getEnd());
	    	String desc = line.substring(plst.getPLTE().getStart(), plst.getPLTE().getEnd());
	    	desc = FieldUtils.clearSpecialTags(desc);
	    	ret.add(new PlstData(Integer.parseInt(code), price_dbl, Integer.parseInt(subfamily), barcode, desc));
	    	line= br.readLine();
	    }
		return ret;
	}

	public HashMap<Integer,String> getPlstDataMap(File f, PlstIf plst) throws IOException {
		HashMap<Integer,String> ret = new HashMap<Integer,String>();
		BufferedReader br = new BufferedReader(new FileReader(f));
	    String line = br.readLine();
	    while(line != null) {
	    	String code = line.substring(plst.getPNUM().getStart(), plst.getPNUM().getEnd());
	    	String desc = line.substring(plst.getPLTE().getStart(), plst.getPLTE().getEnd());
	    	desc = FieldUtils.clearSpecialTags(desc);
	    	ret.put(Integer.parseInt(code), desc);
	    	line= br.readLine();
	    }
		return ret;
	}

	public BokaIf getBokaFieldsZiel(File f) throws IOException {
		int start = 0;
		BokaIf boka = new BokaIf();

		BufferedReader br = new BufferedReader(new FileReader(f));
	    String line = br.readLine();
		boolean ZIEL = false;
	    while(line != null) {
	    	if (ZIEL) {
	    		if (line.startsWith("BONU")) boka.setBONU(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("STYP")) boka.setSTYP(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("SNR1")) boka.setSNR1(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("BT10")) boka.setBT10(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("BT20")) boka.setBT20(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("POS1")) boka.setPOS1(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("GEW1")) boka.setGEW1(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("DZEIS")) boka.setZEIS(new Field(start, start + FieldUtils.getFieldLength(line)));
	    		else if (line.startsWith("coma")) boka.setComaSeparator(true);
		    	else if (line.startsWith("cr")) boka.setCR();
		    	else if (line.startsWith("eol")) boka.setEOL();
		    	else boka.addMask(FieldUtils.getFieldType(line), FieldUtils.getFieldLength(line), FieldUtils.getFieldCode(line)); 
	    		start = start + FieldUtils.getFieldLength(line);
	    	}
	    	if (line.startsWith("$ZIEL")) ZIEL = true;
	    	line= br.readLine();
	    }
		return boka;
	}

	public ArrayList<BokaData> getBokaData(File f, BokaIf boka) throws IOException {
		ArrayList<BokaData> ret = new ArrayList<BokaData>();
		BufferedReader br = new BufferedReader(new FileReader(f));
	    String line = br.readLine();
	    Date date_date = new Date();
	    while(line != null) {
	    	String code = line.substring(boka.getBONU().getStart(), boka.getBONU().getEnd());
	    	String type = line.substring(boka.getSTYP().getStart(), boka.getSTYP().getEnd());
	    	int type_int = Integer.parseInt(type);
	    	String item = line.substring(boka.getSNR1().getStart(), boka.getSNR1().getEnd());
	    	String unit_price = line.substring(boka.getBT10().getStart(), boka.getBT10().getEnd());
	    	double unit_price_dbl = 0;
	    	if (unit_price.length() > 2) {
	    		unit_price = unit_price.substring(0, unit_price.length() - 2) + "." + unit_price.substring(unit_price.length() - 2, unit_price.length());
	    		try {
	    			unit_price_dbl = Double.parseDouble(unit_price);
				} catch (NumberFormatException e) {
				}  
	    	}
	    	String price = line.substring(boka.getBT20().getStart(), boka.getBT20().getEnd());
	    	double price_dbl = 0;
	    	if (price.length() > 2) {
	    		price = price.substring(0, price.length() - 2) + "." + price.substring(price.length() - 2, price.length());
	    		try {
	    			price_dbl = Double.parseDouble(price);
				} catch (NumberFormatException e) {
				}  
	    	}
	    	String quantity = line.substring(boka.getPOS1().getStart(), boka.getPOS1().getEnd());
	    	String weight = line.substring(boka.getGEW1().getStart(), boka.getGEW1().getEnd());
	    	double weight_dbl = 0;
	    	if (weight.length() > 3) {
	    		weight = weight.substring(0, weight.length() - 3) + "." + weight.substring(weight.length() - 3, weight.length());
	    		try {
	    			weight_dbl = Double.parseDouble(weight);
				} catch (NumberFormatException e) {
				}  
	    	}
	    	if (type_int == 1) {
		    	String date = line.substring(boka.getZEIS().getStart(), boka.getZEIS().getEnd());
		    	if (date.length() > 2) {
		    		date = date.trim();
	    			try {
						date_date = new SimpleDateFormat(boka.getDateformat()).parse(date);
					} catch (ParseException e) {
					}
		    	}
	    	}
	    	if (type_int == 2) {
	    		ret.add(new BokaData(Integer.parseInt(code), type_int, Integer.parseInt(item), 
	    							unit_price_dbl, price_dbl, Integer.parseInt(quantity), weight_dbl, date_date));
	    	}
	    	line= br.readLine();
	    }
		return ret;
	}

}