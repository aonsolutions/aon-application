package com.esferalia.aon.gwt.template.server.imports;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Utils {

	public static Object getObjectValue(Cell cell){
		if(cell != null && CellType.STRING == cell.getCellType()) {
			return cell.getStringCellValue();
		}
		if(cell != null && CellType.NUMERIC == cell.getCellType()) {
			return cell.getNumericCellValue();
		}
		
		if(cell != null && CellType.FORMULA == cell.getCellType() && CellType.NUMERIC == cell.getCachedFormulaResultType()) {
			return cell.getNumericCellValue();
		} else if(cell != null && CellType.FORMULA == cell.getCellType() && CellType.STRING == cell.getCachedFormulaResultType()) {
			return cell.getStringCellValue();
		} else if(cell != null && CellType.FORMULA == cell.getCellType()) {
			return cell.getCellFormula();
		}
		if(cell != null && CellType.BOOLEAN == cell.getCellType()) {
			return cell.getBooleanCellValue() ? 1.0 : 0.0;
		}
		return null;
	}
	
	public static String calculateAccount(String acc) {
		return calculateAccount(acc, 4);
	}
	
	public static String calculateAccount(String acc, Integer pos) {
		acc = acc.replace(" ", "").replace(" ", "");
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
	
	public static Boolean isAyudaT(Domain domain) {
		return domain.getName().contains("ayudat") && domain.getParentId().equals(1);
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
		return null;
	}
}
