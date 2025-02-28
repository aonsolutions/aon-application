package com.esferalia.aon.gwt.fiscal.server.product;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.occam.api.model.tariff.Tariff;

public class TariffCatalogueExcelAction extends AbsExcelAction implements Consumer<ProductTariffsEntryExcel> {
	private List<Tariff> tariffs;
	
	public TariffCatalogueExcelAction() {
		initializeEmpty();
	}
    
	public void setTariffs(List<Tariff> tariffs) {
		this.tariffs = tariffs;
	}
    
	@Override
	protected void headerRow() {
		row = sheet.createRow(rowCount++);
		cellCount = 0;

		Font orientedHeaderFont= workbook.createFont();
		orientedHeaderFont.setColor( IndexedColors.WHITE.index );

		XSSFCellStyle orientedHeaderCellStyle = (XSSFCellStyle) workbook.createCellStyle();
		orientedHeaderCellStyle.setAlignment( HorizontalAlignment.CENTER );
		orientedHeaderCellStyle.setBorderBottom(BorderStyle.MEDIUM);
		orientedHeaderCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);  
		orientedHeaderCellStyle.setFillForegroundColor(AON_BLUE);
		orientedHeaderCellStyle.setRotation( (short) 90 );
		orientedHeaderCellStyle.setFont(orientedHeaderFont);

		for (int i = 0 ; i < row.getLastCellNum(); i ++) {
			sheet.autoSizeColumn(i);
		}
		
	    CellUtil.createCell(row, cellCount, "C\u00f3digo", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 20*256);

		CellUtil.createCell(row, cellCount, "Descripci\u00f3n", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 40*256);
		
		CellUtil.createCell(row, cellCount, "Tipo", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 20*256);

		CellUtil.createCell(row, cellCount, "Precio", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 15*256);	
		
	    this.tariffs.sort(Comparator.comparing(Tariff::getCode));
		this.tariffs.forEach(tariff -> {
			CellUtil.createCell(row, cellCount, tariff.getCode(), headerCellStyle);
		    sheet.setColumnWidth(cellCount++, 15*256);		    
		});
		
	}
	
	@Override
	public void accept(ProductTariffsEntryExcel entry) {
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		addCell( entry.getCode() );
		addCell( entry.getDescription() );
		addCell( entry.getType() );
		alignRight( addCell( formaaAmount(entry.getPrice()) ) );
		
		entry.getTariffs().values().forEach(value -> alignRight( addCell( formaaAmount(value) ) ));
	}

	private static String formaaAmount(Double value) {
		if(null == value) return "";
        // Round to two decimal places
        long scaledValue = Math.round(value * 100); // Scale to avoid floating-point precision issues
        long integerPart = scaledValue / 100;      // Extract integer part
        long decimalPart = scaledValue % 100;      // Extract decimal part

        // Format the result
        return integerPart + "." + (decimalPart < 10 ? "0" : "") + decimalPart + " \u20ac";
    }
}
