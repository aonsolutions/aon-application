package com.esferalia.aon.gwt.template.server.imports;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.esferalia.aon.watson.util.AonMathUtils;

public class Utils {

	public static Object getObjectValue(Cell cell){
		if(cell != null && CellType.STRING == cell.getCellTypeEnum()) {
			return cell.getStringCellValue();
		}
		if(cell != null && CellType.NUMERIC == cell.getCellTypeEnum()) {
			return cell.getNumericCellValue();
		}
		
		if(cell != null && CellType.FORMULA == cell.getCellTypeEnum() && CellType.NUMERIC == cell.getCachedFormulaResultTypeEnum()) {
			return cell.getNumericCellValue();
		} else if(cell != null && CellType.FORMULA == cell.getCellTypeEnum() && CellType.STRING == cell.getCachedFormulaResultTypeEnum()) {
			return cell.getStringCellValue();
		} else if(cell != null && CellType.FORMULA == cell.getCellTypeEnum()) {
			return cell.getCellFormula();
		}
		if(cell != null && CellType.BOOLEAN == cell.getCellTypeEnum()) {
			return cell.getBooleanCellValue() ? 1.0 : 0.0;
		}
		return null;
	}
	
	public static String calculateAccount(String acc) {
		return calculateAccount(acc, 4);
	}
	
	public static String calculateAccount(String acc, Integer pos) {
		if(acc.length() > 9) {
			return acc.substring(0,pos) + acc.substring((acc.length() - 9) + pos);
		} else if(acc.length() > pos && acc.length() < 9) {
			return acc.substring(0, pos) + generateZeros(9 - acc.length()) + acc.substring(pos);
		}
		return acc;
	}
	
	public static String generateZeros(Integer index) {
		String zeros = "";
		for(Integer i = 0; i < index; i++) {
			zeros = zeros + "0";
		}
		return zeros;
	}
	
	public static Boolean isAyudaT(String domainName) {
		return domainName.contains("ayudat");
	}
	
	public static Double parseDouble(Object object) {
		return parseDouble(object.toString());
	}
	
	public static Double parseDouble(String value) {
		return AonMathUtils.round(Double.parseDouble(value.replace(",", ".")));
	}
}
