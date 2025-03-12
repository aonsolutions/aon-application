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
		
		// NAF
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// Start
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// End
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// Accumulate
		if(meses.size() > 1) {
			CellUtil.createCell(row, cellCount, "Acumulado Periodo", headerCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		cellCount++;
    		cellCount++;
    		cellCount++;
    		
    		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), cellCount - 4, cellCount));
    		cellCount++;
    		cellCount++;
		}
		
		// Procesar automáticamente los 12 meses del annio
		
		XSSFCellStyle headerMonth = null;
		
		for(WorkplaceMonthlyDaysEntryExcel mes : meses) {
			String monthName = obtenerNombreMes(mes.getMes()) + " " + mes.getYear();
        	
			headerMonth = null == headerMonth || headerMonth == headerOddMonthCellStyle ? headerEvenMonthCellStyle : headerOddMonthCellStyle;
			
    		CellUtil.createCell(row, cellCount, monthName, headerMonth);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		cellCount++;
    		cellCount++;
    		cellCount++;
    		
    		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), cellCount - 4, cellCount));
    		cellCount++;
    		cellCount++;
		}
        
        row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		// Nombre Completo
		CellUtil.createCell(row, cellCount, "Contratos activos en el CT " + workplaceName, headerSecondaryCellStyle);
		sheet.setColumnWidth(cellCount++, 40*256);
		
		// Docuemnto
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// NAF
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// Start
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// End
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// Accumulate
		if(meses.size() > 1) {
			CellUtil.createCell(row, cellCount, "DM", headerSecondaryCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "FS", headerSecondaryCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "DF", headerSecondaryCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "DL", headerSecondaryCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "Hrs. JC", headerSecondaryCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		cellCount++;
		}
		
		// Procesar automáticamente los 12 meses del annio
		this.meses.forEach(mes -> {
			CellUtil.createCell(row, cellCount, "DM", headerSecondaryCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "FS", headerSecondaryCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "DF", headerSecondaryCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "DL", headerSecondaryCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "Hrs. JC", headerSecondaryCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		cellCount++;
		});
        
        row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		// Nombre Completo
		CellUtil.createCell(row, cellCount, "Periodo (" + startDate + " al " + endDate + ")", headerSecondaryCellStyle);
		sheet.setColumnWidth(cellCount++, 40*256);
		
		// Docuemnto
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// NAF
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// Start
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// End
		CellUtil.createCell(row, cellCount, "");
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// Accumulate
		if(meses.size() > 1) {
			addCell( meses.stream().mapToInt(mes -> mes.getDiasMes()).sum() );
			addCell( meses.stream().mapToInt(mes -> mes.getDiasFestivos()).sum() );
			addCell( meses.stream().mapToInt(mes -> mes.getDiasFinDeSemana()).sum() );
			addCell( meses.stream().mapToInt(mes -> mes.getDiasLaborables()).sum() );
			addCell( meses.stream().mapToInt(mes -> mes.getHorasJornada()).sum() );
    		
    		cellCount++;
		}
		
		// Procesar automáticamente los 12 meses del annio
		this.meses.forEach(workplaceMonthlyDaysEntryExcel -> {
        	addCell(workplaceMonthlyDaysEntryExcel.getDiasMes());
        	addCell(workplaceMonthlyDaysEntryExcel.getDiasFestivos());
        	addCell(workplaceMonthlyDaysEntryExcel.getDiasFinDeSemana());
        	addCell(workplaceMonthlyDaysEntryExcel.getDiasLaborables());
        	addCell(workplaceMonthlyDaysEntryExcel.getHorasJornada());
        	
        	cellCount++;
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
		
		// NAF
		CellUtil.createCell(row, cellCount, "NAF", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// Start
		CellUtil.createCell(row, cellCount, "F. Inicio", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// End
		CellUtil.createCell(row, cellCount, "F. Fin", headerCellStyle);
		sheet.setColumnWidth(cellCount++, 15*256);
			
		// Accumulate
		if(meses.size() > 1) {
			CellUtil.createCell(row, cellCount, "DV", headerCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "IT", headerCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "NR", headerCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "DA", headerCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "DT", headerCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "Total", headerCellStyle);
    		sheet.setColumnWidth(cellCount++, 10*256);
		}
		
		// Procesar automáticamente los 12 meses del annio
		headerMonth = null;
		
		for (int i = 0; i < meses.size(); i++) {
			headerMonth = null == headerMonth || headerMonth == headerOddMonthCellStyle ? headerEvenMonthCellStyle : headerOddMonthCellStyle;
			
        	CellUtil.createCell(row, cellCount, "DV", headerMonth);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "IT", headerMonth);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "NR", headerMonth);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "DA", headerMonth);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "DT", headerMonth);
    		sheet.setColumnWidth(cellCount++, 10*256);
    		
    		CellUtil.createCell(row, cellCount, "Total", headerMonth);
    		sheet.setColumnWidth(cellCount++, 10*256);
		}
		
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
		alignCenter( addCell( entry.getStartDate() ) );
		alignCenter( addCell( entry.getEndDate() ) );
		
		// Accumulate
		if(meses.size() > 1) {
			addCell( entry.getMeses().stream().mapToInt(mes -> mes.getDiasVacaciones()).sum() );
			addCell( entry.getMeses().stream().mapToInt(mes -> mes.getDiasIT()).sum() );
			addCell( entry.getMeses().stream().mapToInt(mes -> mes.getDiasNoRecuperables()).sum() );
			addCell( entry.getMeses().stream().mapToInt(mes -> mes.getDiasAusencia()).sum() );
			addCell( entry.getMeses().stream().mapToInt(mes -> mes.getDiasTrabajados()).sum() );
			addCell( entry.getMeses().stream().mapToInt(mes -> mes.getDiasTotal()).sum() );
		}
		
		this.meses.forEach(mes -> {
			MonthlyDaysEntryExcel monthlyDaysEntryExcel = getMes(entry.getMeses(), obtenerNombreMes(mes.getMes()));

			if (checkZeroValue(monthlyDaysEntryExcel.getDiasVacaciones())) alignCenter(addCell("-"));
			else addCell(monthlyDaysEntryExcel.getDiasVacaciones());
			
			if (checkZeroValue(monthlyDaysEntryExcel.getDiasIT())) alignCenter(addCell("-"));
			else addCell(monthlyDaysEntryExcel.getDiasIT());
			
			if (checkZeroValue(monthlyDaysEntryExcel.getDiasNoRecuperables())) alignCenter(addCell("-"));
			else addCell(monthlyDaysEntryExcel.getDiasNoRecuperables());
			
			if (checkZeroValue(monthlyDaysEntryExcel.getDiasAusencia())) alignCenter(addCell("-"));
			else addCell(monthlyDaysEntryExcel.getDiasAusencia());
			
			if (checkZeroValue(monthlyDaysEntryExcel.getDiasTrabajados())) alignCenter(addCell("-"));
			else addCell(monthlyDaysEntryExcel.getDiasTrabajados());
			
			if (checkZeroValue(monthlyDaysEntryExcel.getDiasTotal())) alignCenter(addCell("-"));
			else addCell(monthlyDaysEntryExcel.getDiasTotal());
		});
	}
	
	private boolean checkZeroValue(int value) {
		return 0 == value;
	}

    private static String obtenerNombreMes(int mes) {
        return switch (mes) {
            case 1 -> "Enero";
            case 2 -> "Febrero";
            case 3 -> "Marzo";
            case 4 -> "Abril";
            case 5 -> "Mayo";
            case 6 -> "Junio";
            case 7 -> "Julio";
            case 8 -> "Agosto";
            case 9 -> "Septiembre";
            case 10 -> "Octubre";
            case 11 -> "Noviembre";
            case 12 -> "Diciembre";
            default -> "INV";
        };
    }

	@Override
	protected void headerRow() {}
}
