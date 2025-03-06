package com.esferalia.aon.in.payroll.excel.contract;

import java.util.List;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.in.payroll.excel.AbsExcelAction;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ContractDaysExcelAction extends AbsExcelAction implements Consumer<AnualDaysEntryExcel> {
	
	private List<WorkplaceMonthlyDaysEntryExcel> meses;
	
	public ContractDaysExcelAction() {
		initializeEmpty();
	}
	
	public void setCTMeses(List<WorkplaceMonthlyDaysEntryExcel> meses) {
		this.meses = meses;
	}
    
	public void headerRow(String workplaceName, String startDate, String endDate) {
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
		
		// Nombre Completo
		CellUtil.createCell(row, cellCount, "", headerSecondaryCellStyle);
		sheet.setColumnWidth(cellCount++, 40*256);
		
		// Docuemnto
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// Docuemnto
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// Procesar automáticamente los 12 meses del annio
		this.meses.forEach(mes -> {
			String monthName = obtenerNombreMes(mes.getMes());
        	
    		CellUtil.createCell(row, cellCount, monthName, headerCellStyle);
    		sheet.setColumnWidth(cellCount++, 15*256);
    		
    		cellCount++;
    		cellCount++;
    		
    		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), cellCount - 3, cellCount));
    		cellCount++;
		});
        
        row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		// Nombre Completo
		CellUtil.createCell(row, cellCount, "Contratos activos en el CT " + workplaceName, headerSecondaryCellStyle);
		sheet.setColumnWidth(cellCount++, 40*256);
		
		// Docuemnto
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// Docuemnto
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// Procesar automáticamente los 12 meses del annio
		this.meses.forEach(mes -> {
			CellUtil.createCell(row, cellCount, "D. Mes", headerSecondaryCellStyle);
    		sheet.setColumnWidth(cellCount++, 15*256);
    		
    		CellUtil.createCell(row, cellCount, "D. Laborables", headerSecondaryCellStyle);
    		sheet.setColumnWidth(cellCount++, 15*256);
    		
    		CellUtil.createCell(row, cellCount, "D. Sab/Dom", headerSecondaryCellStyle);
    		sheet.setColumnWidth(cellCount++, 15*256);
    		
    		CellUtil.createCell(row, cellCount, "D. Festivos", headerSecondaryCellStyle);
    		sheet.setColumnWidth(cellCount++, 15*256);
		});
        
        row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		// Nombre Completo
		CellUtil.createCell(row, cellCount, "Periodo (" + startDate + " al " + endDate + ")", headerSecondaryCellStyle);
		sheet.setColumnWidth(cellCount++, 40*256);
		
		// Docuemnto
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// Docuemnto
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// Procesar automáticamente los 12 meses del annio
		this.meses.forEach(workplaceMonthlyDaysEntryExcel -> {
        	addCell(workplaceMonthlyDaysEntryExcel.getDiasMes());
        	addCell(workplaceMonthlyDaysEntryExcel.getDiasLaborables());
        	addCell(workplaceMonthlyDaysEntryExcel.getDiasFinDeSemana());
        	addCell(workplaceMonthlyDaysEntryExcel.getDiasFestivos());
        	
        });
		
        row = sheet.createRow(rowCount++);
        cellCount = 0;
        
        // Nombre Completo
        CellUtil.createCell(row, cellCount, "", headerSecondaryCellStyle);
		sheet.setColumnWidth(cellCount++, 40*256);
        
        row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		// Nombre Completo
		CellUtil.createCell(row, cellCount, "Trabajador", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 40*256);
		
		// Docuemnto
		CellUtil.createCell(row, cellCount, "Documento", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// Docuemnto
		CellUtil.createCell(row, cellCount, "NAF", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 15*256);
			
		// Procesar automáticamente los 12 meses del annio
		this.meses.forEach(mes -> {
        	CellUtil.createCell(row, cellCount, "D. Trabajados", headerCellStyle);
    		sheet.setColumnWidth(cellCount++, 15*256);
    		
    		CellUtil.createCell(row, cellCount, "D. Vacaciones", headerCellStyle);
    		sheet.setColumnWidth(cellCount++, 15*256);
    		
    		CellUtil.createCell(row, cellCount, "D. IT", headerCellStyle);
    		sheet.setColumnWidth(cellCount++, 15*256);
    		
    		CellUtil.createCell(row, cellCount, "D. No Recuper.", headerCellStyle);
    		sheet.setColumnWidth(cellCount++, 15*256);
        });
		
	}
	
	private MonthlyDaysEntryExcel getMes(List<MonthlyDaysEntryExcel> meses, String mes) {
		return meses.stream().filter(mesIt -> AonStringUtils.equalsIgnoreCase(mesIt.getMes(), mes)).findFirst().get();
	}

	@Override
	public void accept(AnualDaysEntryExcel entry) {
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		addCell( entry.getFullName() ) ;
		alignCenter( addCell( entry.getDocument() ) );
		alignCenter( addCell( entry.getNaf() ) );
		
		this.meses.forEach(mes -> {
			MonthlyDaysEntryExcel monthlyDaysEntryExcel = getMes(entry.getMeses(), obtenerNombreMes(mes.getMes()));

			addCell(monthlyDaysEntryExcel.getDiasTrabajados());
			addCell(monthlyDaysEntryExcel.getDiasVacaciones());
			addCell(monthlyDaysEntryExcel.getDiasIT());
			addCell(0);
		});
	}

    private static String obtenerNombreMes(int mes) {
        return switch (mes) {
            case 1 -> "ENE";
            case 2 -> "FEB";
            case 3 -> "MAR";
            case 4 -> "ABR";
            case 5 -> "MAY";
            case 6 -> "JUN";
            case 7 -> "JUL";
            case 8 -> "AGO";
            case 9 -> "SEP";
            case 10 -> "OCT";
            case 11 -> "NOV";
            case 12 -> "DIC";
            default -> "INV";
        };
    }

	@Override
	protected void headerRow() {}
}
