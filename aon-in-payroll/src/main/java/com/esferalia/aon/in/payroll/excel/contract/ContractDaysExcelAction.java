package com.esferalia.aon.in.payroll.excel.contract;

import java.util.List;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.in.payroll.excel.AbsExcelAction;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ContractDaysExcelAction extends AbsExcelAction implements Consumer<AnualDaysEntryExcel> {
	
	private XSSFCellStyle style = headerEvenMonthCellStyle;
	private XSSFCellStyle styleCenter = headerEvenMonthCenterCellStyle;
	
	private boolean extended = false;
	private boolean showTotals = false;
	
	public ContractDaysExcelAction() {
		initializeEmpty();
	}
	
	public void setExtended(boolean extended) {
		this.extended = extended;
	}
	
	public void setShowTotals(boolean showTotals) {
		this.showTotals = showTotals;
	}
    
	public void headerRow(String workplaceName, String startDate, String endDate) {
		row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		for (int i = 0 ; i < row.getLastCellNum(); i ++)
			sheet.autoSizeColumn(i);
		
		// Trabajador
		CellUtil.createCell(row, cellCount, "", infoCellStyle);
		sheet.setColumnWidth(cellCount++, 40*256);
		
		blankRow();
		
        row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		// Trabajador
		CellUtil.createCell(row, cellCount, "Contratos activos en el CT " + workplaceName, infoCellStyle);
		sheet.setColumnWidth(cellCount++, 40*256);
		
		blankRow();
        
        row = sheet.createRow(rowCount++);
		cellCount = 0;
		
		// Trabajador
		CellUtil.createCell(row, cellCount, "Periodo (" + startDate + " al " + endDate + ")", infoCellStyle);
		sheet.setColumnWidth(cellCount++, 40*256);
		
		blankRow();
		
        row = sheet.createRow(rowCount++);
        cellCount = 0;
        
        // Trabajador
        CellUtil.createCell(row, cellCount, "", infoCellStyle);
		sheet.setColumnWidth(cellCount++, 40*256);
		
		blankRow();
        
        row = sheet.createRow(rowCount++);
        row.setHeightInPoints(15);
		cellCount = 0;
		
		// Trabajador
		CellUtil.createCell(row, cellCount++, "Trabajador", headerCellStyle);
		
		// Docuemnto
		CellUtil.createCell(row, cellCount++, "Documento", headerCellStyle);
		
		// NAF
		CellUtil.createCell(row, cellCount++, "NAF", headerCellStyle);
		
		// Periodo
		CellUtil.createCell(row, cellCount++, "", headerCellStyle);
		
		// DV
		CellUtil.createCell(row, cellCount++, "DV", headerCellStyle);
		
		// IT
		CellUtil.createCell(row, cellCount++, "IT", headerCellStyle);
		
		// NR
		CellUtil.createCell(row, cellCount++, "PR", headerCellStyle);
		
		// DA
		CellUtil.createCell(row, cellCount++, "DA", headerCellStyle);
	
		// DT
		CellUtil.createCell(row, cellCount++, "DT", headerCellStyle);
		
		// Total
		CellUtil.createCell(row, cellCount++, "Total", headerCellStyle);
		
		// Start
		CellUtil.createCell(row, cellCount++, "F. Inicio", headerCellStyle);
		
		// End
		CellUtil.createCell(row, cellCount++, "F. Fin", headerCellStyle);
		
		// TC2
		CellUtil.createCell(row, cellCount++, "TC2", headerCellStyle);
		
		// Parcialidad
		CellUtil.createCell(row, cellCount++, "Parc.", headerCellStyle);
		
		// Sexo
		CellUtil.createCell(row, cellCount++, "Sexo", headerCellStyle);
		
		// Nivel Retributivo
		CellUtil.createCell(row, cellCount++, "Nivel Retributivo", headerCellStyle);
		
		// P. Trabajo
		CellUtil.createCell(row, cellCount++, "Puesto Trabajo", headerCellStyle);
		
	}
	
	private void blankRow() {
		// Docuemnto
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 12*256);
		
		// NAF
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 15*256);
		
		// Periodo
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 12*256);
		
		// DV
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 8*256);
		
		// IT
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 8*256);
		
		// PR
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 8*256);
		
		// DA
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 8*256);
	
		// DT
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 8*256);
		
		// Total
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 10*256);
		
		// Start
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 12*256);
		
		// End
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 12*256);
		
		// TC2
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 8*256);
		
		// Parcialidad
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 8*256);
		
		// Sexo
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 7*256);
		
		// Nivel Retributivo
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 30*256);
		
		// P. Trabajo
		CellUtil.createCell(row, cellCount, "", blankCellStyle);
		sheet.setColumnWidth(cellCount++, 30*256);
	}
	
	private MonthlyDaysEntryExcel getMes(List<MonthlyDaysEntryExcel> meses, String mes) {
		return meses.stream().filter(mesIt -> AonStringUtils.equalsIgnoreCase(mesIt.getMes(), mes)).findFirst().get();
	}

	@Override
	public void accept(AnualDaysEntryExcel entry) {
		style = style == headerEvenMonthCellStyle ? headerOddMonthCellStyle : headerEvenMonthCellStyle;
		styleCenter = styleCenter == headerEvenMonthCenterCellStyle ? headerOddMonthCenterCellStyle : headerEvenMonthCenterCellStyle;
		
		if(this.extended) {
			for(MonthlyDaysEntryExcel mes : entry.getMeses()){
				row = sheet.createRow(rowCount++);
				row.setHeightInPoints(15);
				cellCount = 0;
				
				createCell( entry.getFullName() );
				createCenterCell( entry.getDocument() );
				createCenterCell( entry.getNaf() );
				createCenterCell( obtenerNombreMes(mes.getMes()) + "/" + mes.getYear() );
				
				MonthlyDaysEntryExcel monthlyDaysEntryExcel = getMes(entry.getMeses(), mes.getMes());
				
				if (checkZeroValue(monthlyDaysEntryExcel.getDiasVacaciones())) createCenterCell("-");
				else createCenterCell(monthlyDaysEntryExcel.getDiasVacaciones());
				
				if (checkZeroValue(monthlyDaysEntryExcel.getDiasIT())) createCenterCell("-");
				else createCenterCell(monthlyDaysEntryExcel.getDiasIT());
				
				if (checkZeroValue(monthlyDaysEntryExcel.getDiasNoRecuperables())) createCenterCell("-");
				else createCenterCell(monthlyDaysEntryExcel.getDiasNoRecuperables());
				
				if (checkZeroValue(monthlyDaysEntryExcel.getDiasAusencia())) createCenterCell("-");
				else createCenterCell(monthlyDaysEntryExcel.getDiasAusencia());
				
				if (checkZeroValue(monthlyDaysEntryExcel.getDiasTrabajados())) createCenterCell("-");
				else createCenterCell(monthlyDaysEntryExcel.getDiasTrabajados());
				
				if (checkZeroValue(monthlyDaysEntryExcel.getDiasTotal())) createCenterCell("-");
				else createCenterCell(monthlyDaysEntryExcel.getDiasTotal());
				
				createCenterCell( entry.getStartDate() );
				createCenterCell( entry.getEndDate() );
				
				createCenterCell( entry.getContractType() );
				createCenterCell( entry.getPartiality() + "%" );
				createCenterCell( entry.getGender() );
				createCell( entry.getAgreementLevel() );
				createCell( entry.getAgreementCategory() );
				
			}
		} else {
			row = sheet.createRow(rowCount++);
			row.setHeightInPoints(15);
			cellCount = 0;
			
			createCell( entry.getFullName() );
			createCenterCell( entry.getDocument() );
			createCenterCell( entry.getNaf() );
			createCenterCell( "Acumulado" );
			
			createCenterCell(entry.getMeses().stream().mapToInt(mes -> mes.getDiasVacaciones()).sum());
			createCenterCell(entry.getMeses().stream().mapToInt(mes -> mes.getDiasIT()).sum());
			createCenterCell(entry.getMeses().stream().mapToInt(mes -> mes.getDiasNoRecuperables()).sum());
			createCenterCell(entry.getMeses().stream().mapToInt(mes -> mes.getDiasAusencia()).sum());
			createCenterCell(entry.getMeses().stream().mapToInt(mes -> mes.getDiasTrabajados()).sum() );
			createCenterCell(entry.getMeses().stream().mapToInt(mes -> mes.getDiasTotal()).sum());
			
			createCenterCell( entry.getStartDate() );
			createCenterCell( entry.getEndDate() );
			
			createCenterCell( entry.getContractType() );
			createCenterCell( entry.getPartiality() + "%" );
			createCenterCell( entry.getGender() );
			createCell( entry.getAgreementLevel() );
			createCell( entry.getAgreementCategory() );
		}
		
		
		if(this.showTotals) {
			row = sheet.createRow(rowCount++);
			row.setHeightInPoints(15);
			cellCount = 0;
			
			createCellTotals( entry.getFullName() );
			createCenterCellTotals( entry.getDocument() );
			createCenterCellTotals( entry.getNaf() );
			createCenterCellTotals( "Acumulado" );
			
			createCenterCellTotals(entry.getMeses().stream().mapToInt(mes -> mes.getDiasVacaciones()).sum());
			createCenterCellTotals(entry.getMeses().stream().mapToInt(mes -> mes.getDiasIT()).sum());
			createCenterCellTotals(entry.getMeses().stream().mapToInt(mes -> mes.getDiasNoRecuperables()).sum());
			createCenterCellTotals(entry.getMeses().stream().mapToInt(mes -> mes.getDiasAusencia()).sum());
			createCenterCellTotals(entry.getMeses().stream().mapToInt(mes -> mes.getDiasTrabajados()).sum() );
			createCenterCellTotals(entry.getMeses().stream().mapToInt(mes -> mes.getDiasTotal()).sum());
			
			createCenterCellTotals( entry.getStartDate() );
			createCenterCellTotals( entry.getEndDate() );
			
			createCenterCellTotals( entry.getContractType() );
			createCenterCellTotals( entry.getPartiality() + "%" );
			createCenterCellTotals( entry.getGender() );
			createCellTotals( entry.getAgreementLevel() );
			createCellTotals( entry.getAgreementCategory() );
		}
	}
	
	private Cell createCell(String value) {
		Cell cell = addCell( value );
		cell.setCellStyle(style);
		return cell;
	}
	
	private Cell createCenterCell(String value) {
		Cell cell = addCell( value );
		cell.setCellStyle(styleCenter);
		return cell;
	}
	
	private Cell createCenterCell(Integer value) {
		Cell cell = addCell( value.toString() );
		cell.setCellStyle(styleCenter);
		return cell;
	}
	
	private Cell createCellTotals(String value) {
		Cell cell = addCell( value );
		cell.setCellStyle(totalStyle);
		return cell;
	}
	
	private Cell createCenterCellTotals(String value) {
		Cell cell = addCell( value );
		cell.setCellStyle(totalCenterStyle);
		return cell;
	}
	
	private Cell createCenterCellTotals(Integer value) {
		Cell cell = addCell( value.toString() );
		cell.setCellStyle(totalCenterStyle);
		return cell;
	}
	
	private boolean checkZeroValue(int value) {
		return 0 == value;
	}

    private static String obtenerNombreMes(String mes) {
        return switch (mes) {
            case "Enero" -> "Ene.";
            case "Febrero" -> "Feb.";
            case "Marzo" -> "Mar.";
            case "Abril" -> "Abr.";
            case "Mayo" -> "May.";
            case "Junio" -> "Jun.";
            case "Julio" -> "Jul.";
            case "Agosto" -> "Ago.";
            case "Septiembre" -> "Sep.";
            case "Octubre" -> "Oct.";
            case "Noviembre" -> "Nov.";
            case "Diciembre" -> "Dic.";
            default -> "INV";
        };
    }

	@Override
	protected void headerRow() {}
}
