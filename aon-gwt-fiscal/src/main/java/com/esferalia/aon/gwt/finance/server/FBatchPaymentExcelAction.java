package com.esferalia.aon.gwt.finance.server;

import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.Finance;

public class FBatchPaymentExcelAction extends AbsExcelAction implements Consumer<Finance> {
	
    
    private FBatch fbatch;
    
    public void setFbatch(FBatch fbatch) {
		this.fbatch = fbatch;
	}
    
    public void fbatchInfo() {
    	row = sheet.createRow(rowCount++);
    	row = sheet.createRow(rowCount++);
    	cellCount = 0;
    	
    	addCell( "Descripci\u00f3n: " );
    	addCell( fbatch.getDescription() );
    	
    	addCell( "Fecha: " + fbatch.getIssueDate() );
    	
    	sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 2));
    	
    	row = sheet.createRow(rowCount++);
    	cellCount = 0;
    	
    	addCell( "Registros: " + fbatch.getBatchDetails().size() );
    	addCell( "Importe: " + fbatch.getBatchDetails().stream().mapToDouble(detail -> detail.getFinance().getAmount()).sum() );
    	addCell( "" );
    	addCell( "Banco: " + fbatch.getRbank().getBankAccount().toString() );
    	addCell( "BIC / SWIFT: " + fbatch.getRbank().getBic() );
    	
    	sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 2));
    	sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 5));
    	
    	row = sheet.createRow(rowCount++);
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
		
	    CellUtil.createCell(row, cellCount, "Fecha", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 15*256);

	    CellUtil.createCell(row, cellCount, "Nº Documento", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 15*256);
		
		CellUtil.createCell(row, cellCount, "Concepto", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 20*256);

		CellUtil.createCell(row, cellCount, "Nombre", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 40*256);

		CellUtil.createCell(row, cellCount, "Forma Pago", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 20*256);		    
	    
	    CellUtil.createCell(row, cellCount, "Cuenta Bancaria", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 30*256);		
	    
	    CellUtil.createCell(row, cellCount, "BIC / SWIFT", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 30*256);		
	    
	     CellUtil.createCell(row, cellCount, "Precio", headerCellStyle);
	    sheet.setColumnWidth(cellCount++, 10*256);		    
	}
	
	@Override
	public void accept(Finance finance) {
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		addCell( finance.getDueDate() );
		alignCenter( addCell( finance.getRegistryDocument() ) );
		addCell( finance.getConcept() );
		addCell( finance.getRegistryName() );
		addCell( finance.getPayMethodName() );
		addCell( finance.getBankAccountSafeValue() );
		addCell( finance.getBic() );
		addCell( finance.getAmount() );
		
	}
	
}
