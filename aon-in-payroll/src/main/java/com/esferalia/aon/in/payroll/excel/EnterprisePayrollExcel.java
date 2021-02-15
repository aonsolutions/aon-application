package com.esferalia.aon.in.payroll.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.in.payroll.csv.IEnterprisePayroll;

public class EnterprisePayrollExcel {

	private static final String[] DEFAULT_HEADER = new String[] { "EMPLEADO", "CENTRO DE TRABAJO", "BRUTO",
			"S.S. EMPLEADO", "IRPF", "LÍQUIDO", "S.S. EMPRESA", "COSTE TOTAL", "S.S. TOTAL", "BONIFICACIONES",
			"BASE CGC", "BASE IRPF" };

	public static void write(OutputStream outputStream, Collection<IEnterprisePayroll> payrolls,
			Optional<String[]> header) throws IOException {
		Workbook wb = new XSSFWorkbook();

		Font headerFont = wb.createFont();
		headerFont.setBold(true);
		CellStyle headerCellStyle = wb.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		headerCellStyle.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());
		headerCellStyle.setFillPattern(FillPatternType.FINE_DOTS);

		CellStyle stringCellStyle = wb.createCellStyle();
		stringCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		stringCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		stringCellStyle.setBorderBottom(BorderStyle.THIN);
		stringCellStyle.setBorderTop(BorderStyle.THIN);
		stringCellStyle.setBorderLeft(BorderStyle.THIN);
		stringCellStyle.setBorderRight(BorderStyle.THIN);

		CellStyle doubleCellStyle = wb.createCellStyle();
		doubleCellStyle.setFillForegroundColor(IndexedColors.CORAL.getIndex());
		doubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		doubleCellStyle.setBorderBottom(BorderStyle.THIN);
		doubleCellStyle.setBorderTop(BorderStyle.THIN);
		doubleCellStyle.setBorderLeft(BorderStyle.THIN);
		doubleCellStyle.setBorderRight(BorderStyle.THIN);

		Iterator<String> it = payrolls.stream().map(payroll -> payroll.getWorkplace()).distinct().iterator();

		while (it.hasNext()) {
			try {
				String workplace = it.next();
				Sheet sheet = wb.createSheet(workplace);

				Object[] arr = payrolls.stream().filter(payroll -> payroll.getWorkplace().equals(workplace)).toArray();
				Row row = sheet.createRow(0);
				if (header.isPresent()) {
					for (int j = 0; j < header.get().length; j++) {
						Cell cell = row.createCell(j);
						cell.setCellValue(header.get()[j]);
						cell.setCellStyle(headerCellStyle);
					}
				} else {
					for (int j = 0; j < DEFAULT_HEADER.length; j++) {
						Cell cell = row.createCell(j);
						cell.setCellValue(DEFAULT_HEADER[j]);
						cell.setCellStyle(headerCellStyle);
					}

				}

				for (int i = 0; i < arr.length; i++) {

					IEnterprisePayroll payroll = (IEnterprisePayroll) arr[i];
					row = sheet.createRow(i + 1);

					Cell c1 = row.createCell(0);
					c1.setCellValue(payroll.getEmployee());
					c1.setCellStyle(stringCellStyle);
					Cell c2 = row.createCell(1);
					c2.setCellValue(payroll.getWorkplace());
					c2.setCellStyle(stringCellStyle);
					Cell c3 = row.createCell(2);
					c3.setCellValue(payroll.getRaw());
					c3.setCellStyle(doubleCellStyle);
					Cell c4 = row.createCell(3);
					c4.setCellValue(payroll.getEmployeeSS());
					c4.setCellStyle(doubleCellStyle);
					Cell c5 = row.createCell(4);
					c5.setCellValue(payroll.getIrpf());
					c5.setCellStyle(doubleCellStyle);
					Cell c6 = row.createCell(5);
					c6.setCellValue(payroll.getLiquid());
					c6.setCellStyle(doubleCellStyle);
					Cell c7 = row.createCell(6);
					c7.setCellValue(payroll.getEnterpriseSS());
					c7.setCellStyle(doubleCellStyle);
					Cell c8 = row.createCell(7);
					c8.setCellValue(payroll.getTotalCost());
					c8.setCellStyle(doubleCellStyle);
					Cell c9 = row.createCell(8);
					c9.setCellValue(payroll.getTotalSS());
					c9.setCellStyle(doubleCellStyle);
					Cell c10 = row.createCell(9);
					c10.setCellValue(payroll.getBonuses());
					c10.setCellStyle(doubleCellStyle);
					Cell c11 = row.createCell(10);
					c11.setCellValue(payroll.getCgcBase());
					c11.setCellStyle(doubleCellStyle);
					Cell c12 = row.createCell(11);
					c12.setCellValue(payroll.getIrpfBase());
					c12.setCellStyle(doubleCellStyle);
				}

				for (int i = 0; i < DEFAULT_HEADER.length; i++) {
					sheet.autoSizeColumn(i);
				}
			} catch (java.lang.IllegalArgumentException e) {
				System.err.println(e.getMessage());
			}
		}

		wb.write(outputStream);
		outputStream.close();
		wb.close();
	}

}
