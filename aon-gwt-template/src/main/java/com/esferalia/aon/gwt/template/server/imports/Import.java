package com.esferalia.aon.gwt.template.server.imports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Import {
	
	protected Iterator<Row> rowIterator(byte[] data) {
		HSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new HSSFWorkbook(bais);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();

			return rowIterator;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	protected Iterator<Row> rowIteratorX(byte[] data) {
		XSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new XSSFWorkbook(bais);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			return rowIterator;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new LinkedList<Row>().iterator();
	}
	
	protected Date parseDate(Cell cell,Object o) {
		Date date = new Date();
		try{
			date = cell.getDateCellValue();
		} catch (Exception e) {
			date = AonDateUtils.parse(o.toString(), "dd/MM/yyyy");
		}
		return date;
	}
	
	public static Short parseShort(Object object) {
		return parseShort(object.toString());
	}
	
	public static Short parseShort(String value) {
		try {
			if (!AonStringUtils.isBlank(value)) {
				Double d = Double.parseDouble(value);
				return d.shortValue();
			}
		} catch (Exception e) {}
		return null;
	}
	
	public static Double parseDouble(Object object) {
		return parseDouble(object.toString());
	}

	public static Double parseDouble(String value) {
		try {
			if (!AonStringUtils.isBlank(value)) {
				return AonMathUtils.round(Double.parseDouble(value.replace(",", ".")));
			}
		} catch (Exception e) {}
		return 0.0;
	}
	
	public static Boolean parseBoolean(Object object) {
		String val = object.toString();
		return IConstants.TRUE.equalsIgnoreCase(val) 
			|| IConstants.SI.equalsIgnoreCase(val)
			|| "1".equals(val);
		
	}
}
