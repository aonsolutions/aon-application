package com.esferalia.aon.in.payroll.excel;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jooq.Condition;
import org.jooq.DSLContext;

import com.esferalia.aon.in.payroll.csv.IEnterprisePayroll;
import com.esferalia.aon.jooq.tables.records.SalaryCostRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.watson.util.AonStringUtils;

public class EnterprisePayrollExcel {

	private static final LinkedHashMap<String, String> DEFAULT_HEADER;

	static {
		DEFAULT_HEADER = new LinkedHashMap<String, String>();
		DEFAULT_HEADER.put("employee", "EMPLEADO");
		DEFAULT_HEADER.put("raw", "BRUTO");
		DEFAULT_HEADER.put("employeeSS", "S.S. EMPLEADO");
		DEFAULT_HEADER.put("irpf", "IRPF");
		DEFAULT_HEADER.put("liquid", "LÍQUIDO");
		DEFAULT_HEADER.put("enterpriseSS", "S.S. EMPRESA");
		DEFAULT_HEADER.put("totalCost", "COSTE TOTAL");
		DEFAULT_HEADER.put("totalSS", "S.S. TOTAL");
		DEFAULT_HEADER.put("bonuses", "BONIFICACIONES");
		DEFAULT_HEADER.put("cgcBase", "BASE CGC");
		DEFAULT_HEADER.put("irpfBase", "BASE IRPF");
		DEFAULT_HEADER.put("cgc", "CONT. COMUNES");
		DEFAULT_HEADER.put("unemployment", "DESEMPLEO");
		DEFAULT_HEADER.put("jobTraining", "FORM. PROF.");

	}

//	private static String[] DEFAULT_HEADER = new String[] { "EMPLEADO", "BRUTO", "S.S. EMPLEADO", "IRPF",
//			"LÍQUIDO", "S.S. EMPRESA", "COSTE TOTAL", "S.S. TOTAL", "BONIFICACIONES", "BASE CGC", "BASE IRPF",
//			"CONT. COMUNES", "DESEMPLEO", "FORM. PROF." };

	public static void write(OutputStream outputStream, Collection<IEnterprisePayroll> payrolls,
			Optional<LinkedHashMap<String, String>> header, String enterpriseName, String dateString) throws IOException {
		Workbook wb = new XSSFWorkbook();

		Font headerFont = wb.createFont();
		headerFont.setBold(true);
		
		Font wrongFont = wb.createFont();
		wrongFont.setColor(IndexedColors.RED.getIndex());
		
		Font okFont = wb.createFont();
		okFont.setColor(IndexedColors.GREEN.getIndex());

		CellStyle headerCellStyle = wb.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		headerCellStyle.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
		headerCellStyle.setFillPattern(FillPatternType.FINE_DOTS);

		CellStyle stringCellStyle = wb.createCellStyle();
		stringCellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		stringCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		stringCellStyle.setBorderBottom(BorderStyle.THIN);
		stringCellStyle.setBorderTop(BorderStyle.THIN);
		stringCellStyle.setBorderLeft(BorderStyle.THIN);
		stringCellStyle.setBorderRight(BorderStyle.THIN);

		CellStyle doubleCellStyle = wb.createCellStyle();
		doubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		doubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		doubleCellStyle.setBorderBottom(BorderStyle.THIN);
		doubleCellStyle.setBorderTop(BorderStyle.THIN);
		doubleCellStyle.setBorderLeft(BorderStyle.THIN);
		doubleCellStyle.setBorderRight(BorderStyle.THIN);
		
		
		CellStyle noDiffCellStyle = wb.createCellStyle();
		noDiffCellStyle.setFont(okFont);
		noDiffCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		noDiffCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		noDiffCellStyle.setBorderBottom(BorderStyle.THIN);
		noDiffCellStyle.setBorderTop(BorderStyle.THIN);
		noDiffCellStyle.setBorderLeft(BorderStyle.THIN);
		noDiffCellStyle.setBorderRight(BorderStyle.THIN);
		
		CellStyle diffCellStyle = wb.createCellStyle();
		diffCellStyle.setFont(wrongFont);
		diffCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		diffCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		diffCellStyle.setBorderBottom(BorderStyle.THIN);
		diffCellStyle.setBorderTop(BorderStyle.THIN);
		diffCellStyle.setBorderLeft(BorderStyle.THIN);
		diffCellStyle.setBorderRight(BorderStyle.THIN);

		CellStyle formulaCellStyle = wb.createCellStyle();
		formulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		formulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		formulaCellStyle.setBorderBottom(BorderStyle.THIN);
		formulaCellStyle.setBorderTop(BorderStyle.THIN);
		formulaCellStyle.setBorderLeft(BorderStyle.THIN);
		formulaCellStyle.setBorderRight(BorderStyle.THIN);

		Row row = null;

		Sheet totals = null;
		if (payrolls.stream().map(IEnterprisePayroll::getWorkplace).distinct().count()>1) {
			totals = wb.createSheet(WorkbookUtil.createSafeSheetName("TOTALES"));
			// Sheet of total amounts by workplace

			LinkedHashMap<String, String> finalHeader = new LinkedHashMap<String, String>();

			if (header.isPresent()) {
				LinkedHashMap<String, String> customHeader = header.get();
				finalHeader.putAll(customHeader);
			} else {
				finalHeader.putAll(DEFAULT_HEADER);
			}

			finalHeader.put("employee", "CENTRO DE TRABAJO");

			Iterator<String> itHead = finalHeader.keySet().iterator();

			row = totals.createRow(0);

			int cellCount = 0;

			while (itHead.hasNext()) {
				Cell cell = row.createCell(cellCount++);
				cell.setCellValue(finalHeader.get(itHead.next()));
				cell.setCellStyle(headerCellStyle);
			}

		}

		Iterator<String> it = payrolls.stream().map(payroll -> payroll.getWorkplace()).distinct().iterator();

		while (it.hasNext()) {
			try {
				String workplace = it.next();

				Object[] arr = payrolls.stream().filter(payroll -> payroll.getWorkplace().equals(workplace)).toArray();
				
				System.out.println(arr.length);
				
				Sheet sheet = wb.createSheet(WorkbookUtil.createSafeSheetName(workplace));
				
				boolean thereIsRaw = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getRaw() == null);
				boolean thereIsEmployeeSS = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getEmployeeSS() == null);
				boolean thereIsIrpf = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getIrpf() == null);
				boolean thereIsLiquid = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getLiquid() == null);
				boolean thereIsEnterpriseSS = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getEnterpriseSS() == null);
				boolean thereIsTotalCost = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getTotalCost() == null);
				boolean thereIsTotalSS = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getTotalSS() == null);
				boolean thereIsBonuses = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getBonuses() == null);
				boolean thereIsCgcBase = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getCgcBase() == null);
				boolean thereIsIrpfBase = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getIrpfBase() == null);
				boolean thereIsCgc = !Arrays.stream(arr).allMatch(p -> ((IEnterprisePayroll) p).getCgc() == null);
				boolean thereIsUnemployment = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getUnemployment() == null);
				boolean thereIsJobTraining = !Arrays.stream(arr)
						.allMatch(p -> ((IEnterprisePayroll) p).getJobTraining() == null);

				LinkedHashMap<String, String> finalHeader = new LinkedHashMap<String, String>();
				
				
				
				if (header.isPresent()) {
					LinkedHashMap<String, String> customHeader = header.get();
					finalHeader.putAll(customHeader);
				} else {
					finalHeader.putAll(DEFAULT_HEADER);
				}

				{
					if (!thereIsRaw)
						finalHeader.remove("raw");
					if (!thereIsBonuses)
						finalHeader.remove("bonuses");
					if (!thereIsCgc)
						finalHeader.remove("cgc");
					if (!thereIsCgcBase)
						finalHeader.remove("cgcBase");
					if (!thereIsEmployeeSS)
						finalHeader.remove("employeeSS");
					if (!thereIsEnterpriseSS)
						finalHeader.remove("enterpriseSS");
					if (!thereIsIrpf)
						finalHeader.remove("irpf");
					if (!thereIsIrpfBase)
						finalHeader.remove("irpfBase");
					if (!thereIsJobTraining)
						finalHeader.remove("jobTraining");
					if (!thereIsLiquid)
						finalHeader.remove("liquid");
					if (!thereIsTotalCost)
						finalHeader.remove("totalCost");
					if (!thereIsTotalSS)
						finalHeader.remove("totalSS");
					if (!thereIsUnemployment)
						finalHeader.remove("unemployment");

					row = sheet.createRow(1);

					Iterator<String> headersIt = finalHeader.keySet().iterator();

					int c = 0;

					while (headersIt.hasNext()) {
						Cell cell = row.createCell(c++);
						cell.setCellValue(finalHeader.get(headersIt.next()));
						cell.setCellStyle(headerCellStyle);
					}

				}

//					private static String[] DEFAULT_HEADER = new String[] { "EMPLEADO", "BRUTO", "S.S. EMPLEADO", "IRPF",
//							"LÍQUIDO", "S.S. EMPRESA", "COSTE TOTAL", "S.S. TOTAL", "BONIFICACIONES", "BASE CGC", "BASE IRPF",
//							"CONT. COMUNES", "DESEMPLEO", "FORM. PROF." };

				for (int i = 0, column = 0; i < arr.length; i++) {

					IEnterprisePayroll payroll = (IEnterprisePayroll) arr[i];
					row = sheet.createRow(sheet.getLastRowNum() + 1);

					Cell cell = row.createCell(column++);
					cell.setCellValue(payroll.getEmployee());
					cell.setCellStyle(stringCellStyle);

					if (thereIsRaw) {
							createDoubleCell(row, column++, payroll.getRaw(), doubleCellStyle);
					}
					

					if (thereIsEmployeeSS) {
						if(AonStringUtils.containsIgnoreCase(workplace, "seg. social")
								&& (payroll.getEmployeeSS() == null || payroll.getEmployeeSS() == 0d)) {
							createDoubleCell(row, column++, payroll.getEmployeeSS(), noDiffCellStyle);
						} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
							createDoubleCell(row, column++, payroll.getEmployeeSS(), diffCellStyle);
						} else
							createDoubleCell(row, column++, payroll.getEmployeeSS(), doubleCellStyle);
					}
						

					if (thereIsIrpf) {
							createDoubleCell(row, column++, payroll.getIrpf(), doubleCellStyle);
					}
					

					if (thereIsLiquid) {
							createDoubleCell(row, column++, payroll.getLiquid(), doubleCellStyle);
					}
					

					if (thereIsEnterpriseSS) {
						if(AonStringUtils.containsIgnoreCase(workplace, "seg. social")
								&& (payroll.getEnterpriseSS() == null || payroll.getEnterpriseSS() == 0d)) {
							createDoubleCell(row, column++, payroll.getEnterpriseSS(), noDiffCellStyle);
						} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
							createDoubleCell(row, column++, payroll.getEnterpriseSS(), diffCellStyle);
						} else
							createDoubleCell(row, column++, payroll.getEnterpriseSS(), doubleCellStyle);
					}
						

					if (thereIsTotalCost) {
							createDoubleCell(row, column++, payroll.getTotalCost(), doubleCellStyle);
					}
					

					if (thereIsTotalSS) {
						if(AonStringUtils.containsIgnoreCase(workplace, "seg. social")
								&& (payroll.getTotalSS() == null || payroll.getTotalSS() == 0d)) {
							createDoubleCell(row, column++, payroll.getTotalSS(), noDiffCellStyle);
						} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
							createDoubleCell(row, column++, payroll.getTotalSS(), diffCellStyle);
						} else
							createDoubleCell(row, column++, payroll.getTotalSS(), doubleCellStyle);
					}
						

					if (thereIsBonuses) {
						if(AonStringUtils.containsIgnoreCase(workplace, "seg. social")
								&& (payroll.getBonuses() == null || payroll.getBonuses() == 0d)) {
							createDoubleCell(row, column++, payroll.getBonuses(), noDiffCellStyle);
						} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
							createDoubleCell(row, column++, payroll.getBonuses(), diffCellStyle);
						} else
							createDoubleCell(row, column++, payroll.getBonuses(), doubleCellStyle);
					}
						

					if (thereIsCgcBase) {
						if(AonStringUtils.containsIgnoreCase(workplace, "seg. social")
								&& (payroll.getCgcBase() == null || payroll.getCgcBase() == 0d)) {
							createDoubleCell(row, column++, payroll.getCgcBase(), noDiffCellStyle);
						} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
							createDoubleCell(row, column++, payroll.getCgcBase(), diffCellStyle);
						} else
							createDoubleCell(row, column++, payroll.getCgcBase(), doubleCellStyle);
					}
						

					if (thereIsIrpfBase) {
							createDoubleCell(row, column++, payroll.getIrpfBase(), doubleCellStyle);
					}
					

					if (thereIsCgc) {
						if(AonStringUtils.containsIgnoreCase(workplace, "seg. social")
								&& (payroll.getCgc() == null || payroll.getCgc() == 0d)) {
							createDoubleCell(row, column++, payroll.getCgc(), noDiffCellStyle);
						} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
							createDoubleCell(row, column++, payroll.getCgc(), diffCellStyle);
						} else
							createDoubleCell(row, column++, payroll.getCgc(), doubleCellStyle);
					}
						

					if (thereIsUnemployment) {
						if(AonStringUtils.containsIgnoreCase(workplace, "seg. social")
								&& (payroll.getUnemployment() == null || payroll.getUnemployment() == 0d)) {
							createDoubleCell(row, column++, payroll.getUnemployment(), noDiffCellStyle);
						} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
							createDoubleCell(row, column++, payroll.getUnemployment(), diffCellStyle);
						} else
							createDoubleCell(row, column++, payroll.getUnemployment(), doubleCellStyle);
					}
						

					if (thereIsJobTraining) {
						if(AonStringUtils.containsIgnoreCase(workplace, "seg. social")
								&& (payroll.getJobTraining() == null || payroll.getJobTraining() == 0d)) {
							createDoubleCell(row, column++, payroll.getJobTraining(), noDiffCellStyle);
						} else if (AonStringUtils.containsIgnoreCase(workplace, "seg. social")) {
							createDoubleCell(row, column++, payroll.getJobTraining(), diffCellStyle);
						} else
							createDoubleCell(row, column, payroll.getJobTraining(), doubleCellStyle);
					}

					column = 0;
				}
				
				
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, row.getLastCellNum()-1));
				row = sheet.createRow(0);
				Cell enterpriseCell = row.createCell(0);
				enterpriseCell.setCellType(CellType.STRING);
				enterpriseCell.setCellStyle(headerCellStyle);
				enterpriseCell.setCellValue(enterpriseName+" - "+dateString);
				
				{	
					int lastColumn = sheet.getRow(sheet.getLastRowNum()).getLastCellNum();
					row = sheet.createRow(sheet.getLastRowNum()+1);
					
					Cell totalCell = row.createCell(0);
					totalCell.setCellType(CellType.STRING);
					totalCell.setCellValue("TOTAL");
					totalCell.setCellStyle(headerCellStyle);
					
					for (int i = 1; i < lastColumn; i++) {
						Cell cell = row.createCell(i);
						cell.setCellType(CellType.FORMULA);
						cell.setCellFormula("sum("+CellReference.convertNumToColString(i)+1+":"+CellReference.convertNumToColString(i)+row.getRowNum()+")");
						cell.setCellStyle(formulaCellStyle);
					}
				}

				// TOTALS (IN THE FIRST SHEET)
				if (totals != null) {
					row = totals.createRow(totals.getLastRowNum() + 1);

					Cell tCell = row.createCell(0);
					tCell.setCellValue(workplace);
					tCell.setCellStyle(stringCellStyle);

					char column = 'B';
					int last = sheet.getLastRowNum();

					if (thereIsRaw)
					{
						tCell = row.createCell(1);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + column + "2:" + column + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					}
					else {
						tCell = row.createCell(1);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}

					if (thereIsEmployeeSS) {
						tCell = row.createCell(2);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + column + "1:" + column + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					} else {
						tCell = row.createCell(2);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}

					if (thereIsIrpf)
					{
						tCell = row.createCell(3);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + column + "1:" + column + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					}
					else {
						tCell = row.createCell(3);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}

					if (thereIsLiquid)
					{
						tCell = row.createCell(4);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + column + "1:" + column + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					}
					else {
						tCell = row.createCell(4);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}

					if (thereIsEnterpriseSS) {
						tCell = row.createCell(5);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + column + "1:" + column + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					} else {
						tCell = row.createCell(5);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}

					if (thereIsTotalCost)
					{
						tCell = row.createCell(6);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + column + "1:" + column + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					}
					else {
						tCell = row.createCell(6);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}

					if (thereIsTotalSS) {
						tCell = row.createCell(7);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + column + "1:" + column + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					} else {
						tCell = row.createCell(7);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}

					if (thereIsBonuses) {
						tCell = row.createCell(8);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + column + "1:" + column + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					} else {
						tCell = row.createCell(8);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}

					if (thereIsCgcBase) {
						tCell = row.createCell(9);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + column + "1:" + column + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					} else {
						tCell = row.createCell(9);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}

					if (thereIsIrpfBase)
					{
						tCell = row.createCell(10);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + column + "1:" + column + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					}
					else {
						tCell = row.createCell(10);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}

					if (thereIsCgc) {
						tCell = row.createCell(11);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + column + "1:" + column + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					} else {
						tCell = row.createCell(11);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}

					if (thereIsUnemployment) {
						tCell = row.createCell(12);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + column + "1:" + column + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					} else {
						tCell = row.createCell(12);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}

					if (thereIsJobTraining) {
						tCell = row.createCell(13);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellFormula("sum('" + workplace + "'!" + column + "1:" + column + last + ")");
						tCell.setCellStyle(doubleCellStyle);
						column++;
					} else {
						tCell = row.createCell(13);
						tCell.setCellType(CellType.FORMULA);
						tCell.setCellStyle(doubleCellStyle);
					}
				}

				for (int i = 0; i < finalHeader.size(); i++) {
					sheet.autoSizeColumn(i);
				}
			} catch (java.lang.IllegalArgumentException e) {
				System.err.println(e.getMessage());
			}
		}
		
		if (totals != null) {
			for (int i = 0; i <= 13; i++) {
				totals.autoSizeColumn(i);
			}
		}
		

		wb.write(outputStream);
		outputStream.close();
		wb.close();
	}

//	public static void writeDiff(OutputStream outputStream, Collection<IEnterprisePayroll> payrolls,
//			Optional<LinkedHashMap<String, String>> header) throws IOException {
//		Workbook wb = new XSSFWorkbook();
//
//		Font headerFont = wb.createFont();
//		headerFont.setBold(true);
//
//		CellStyle headerCellStyle = wb.createCellStyle();
//		headerCellStyle.setFont(headerFont);
//		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
//		headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//		headerCellStyle.setBorderBottom(BorderStyle.THIN);
//		headerCellStyle.setBorderTop(BorderStyle.THIN);
//		headerCellStyle.setBorderLeft(BorderStyle.THIN);
//		headerCellStyle.setBorderRight(BorderStyle.THIN);
//		headerCellStyle.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());
//		headerCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
//
//		CellStyle stringCellStyle = wb.createCellStyle();
//		stringCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
//		stringCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
//		stringCellStyle.setBorderBottom(BorderStyle.THIN);
//		stringCellStyle.setBorderTop(BorderStyle.THIN);
//		stringCellStyle.setBorderLeft(BorderStyle.THIN);
//		stringCellStyle.setBorderRight(BorderStyle.THIN);
//
//		CellStyle doubleCellStyle = wb.createCellStyle();
//		doubleCellStyle.setFillForegroundColor(IndexedColors.CORAL.getIndex());
//		doubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
//		doubleCellStyle.setBorderBottom(BorderStyle.THIN);
//		doubleCellStyle.setBorderTop(BorderStyle.THIN);
//		doubleCellStyle.setBorderLeft(BorderStyle.THIN);
//		doubleCellStyle.setBorderRight(BorderStyle.THIN);
//		
//		CellStyle ssCellStyle = wb.createCellStyle();
//		ssCellStyle.setFillForegroundColor(IndexedColors.CORAL.getIndex());
//		ssCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
//		ssCellStyle.setBorderBottom(BorderStyle.THIN);
//		ssCellStyle.setBorderTop(BorderStyle.THIN);
//		ssCellStyle.setBorderLeft(BorderStyle.THIN);
//		ssCellStyle.setBorderRight(BorderStyle.THIN);
//		
//		CellStyle diffCellStyle = wb.createCellStyle();
//		diffCellStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
//		diffCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
//		diffCellStyle.setBorderBottom(BorderStyle.THIN);
//		diffCellStyle.setBorderTop(BorderStyle.THIN);
//		diffCellStyle.setBorderLeft(BorderStyle.THIN);
//		diffCellStyle.setBorderRight(BorderStyle.THIN);
//
//		CellStyle formulaCellStyle = wb.createCellStyle();
//		formulaCellStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
//		formulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
//		formulaCellStyle.setBorderBottom(BorderStyle.THIN);
//		formulaCellStyle.setBorderTop(BorderStyle.THIN);
//		formulaCellStyle.setBorderLeft(BorderStyle.THIN);
//		formulaCellStyle.setBorderRight(BorderStyle.THIN);
//
//		Row row = null;
//
//
//		Iterator<String> it = payrolls.stream().map(payroll -> payroll.getWorkplace()).distinct().iterator();
//
//		while (it.hasNext()) {
//			try {
//				String workplace = it.next();
//				Sheet sheet = wb.createSheet(WorkbookUtil.createSafeSheetName(workplace));
//
//				Object[] arr = payrolls.stream().filter(payroll -> payroll.getWorkplace().equals(workplace)).toArray();
//
//				boolean thereIsRaw = !Arrays.stream(arr)
//						.allMatch(p -> ((IEnterprisePayroll) p).getRaw() == null);
//				boolean thereIsEmployeeSS = !Arrays.stream(arr)
//						.allMatch(p -> ((IEnterprisePayroll) p).getEmployeeSS() == null);
//				boolean thereIsIrpf = !Arrays.stream(arr)
//						.allMatch(p -> ((IEnterprisePayroll) p).getIrpf() == null);
//				boolean thereIsLiquid = !Arrays.stream(arr)
//						.allMatch(p -> ((IEnterprisePayroll) p).getLiquid() == null);
//				boolean thereIsEnterpriseSS = !Arrays.stream(arr)
//						.allMatch(p -> ((IEnterprisePayroll) p).getEnterpriseSS() == null);
//				boolean thereIsTotalCost = !Arrays.stream(arr)
//						.allMatch(p -> ((IEnterprisePayroll) p).getTotalCost() == null);
//				boolean thereIsTotalSS = !Arrays.stream(arr)
//						.allMatch(p -> ((IEnterprisePayroll) p).getTotalSS() == null);
//				boolean thereIsBonuses = !Arrays.stream(arr)
//						.allMatch(p -> ((IEnterprisePayroll) p).getBonuses() == null);
//				boolean thereIsCgcBase = !Arrays.stream(arr)
//						.allMatch(p -> ((IEnterprisePayroll) p).getCgcBase() == null);
//				boolean thereIsIrpfBase = !Arrays.stream(arr)
//						.allMatch(p -> ((IEnterprisePayroll) p).getIrpfBase() == null);
//				boolean thereIsCgc = !Arrays.stream(arr).allMatch(p -> ((IEnterprisePayroll) p).getCgc() == null);
//				boolean thereIsUnemployment = !Arrays.stream(arr)
//						.allMatch(p -> ((IEnterprisePayroll) p).getUnemployment() == null);
//				boolean thereIsJobTraining = !Arrays.stream(arr)
//						.allMatch(p -> ((IEnterprisePayroll) p).getJobTraining() == null);
//
//				LinkedHashMap<String, String> finalHeader = new LinkedHashMap<String, String>();
//
//				row = sheet.createRow(0);
//				if (header.isPresent()) {
//					LinkedHashMap<String, String> customHeader = header.get();
//					finalHeader.putAll(customHeader);
//				} else {
//					finalHeader.putAll(DEFAULT_HEADER);
//				}
//
//				{
//					if (!thereIsRaw)
//						finalHeader.remove("raw");
//					if (!thereIsBonuses)
//						finalHeader.remove("bonuses");
//					if (!thereIsCgc)
//						finalHeader.remove("cgc");
//					if (!thereIsCgcBase)
//						finalHeader.remove("cgcBase");
//					if (!thereIsEmployeeSS)
//						finalHeader.remove("employeeSS");
//					if (!thereIsEnterpriseSS)
//						finalHeader.remove("enterpriseSS");
//					if (!thereIsIrpf)
//						finalHeader.remove("irpf");
//					if (!thereIsIrpfBase)
//						finalHeader.remove("irpfBase");
//					if (!thereIsJobTraining)
//						finalHeader.remove("jobTraining");
//					if (!thereIsLiquid)
//						finalHeader.remove("liquid");
//					if (!thereIsTotalCost)
//						finalHeader.remove("totalCost");
//					if (!thereIsTotalSS)
//						finalHeader.remove("totalSS");
//					if (!thereIsUnemployment)
//						finalHeader.remove("unemployment");
//
//					row = sheet.createRow(0);
//					
//					Row subRow = sheet.createRow(1);
//
//					Iterator<String> headersIt = finalHeader.keySet().iterator();
//
//					int c = 0;
//					
//					Cell cell = row.createCell(c++);
//					CellRangeAddress employeeMerge = new CellRangeAddress(0,1,0,0);
//					sheet.addMergedRegion(employeeMerge);
//					cell.setCellValue(finalHeader.get(headersIt.next()));
//					cell.setCellStyle(headerCellStyle);
//					
//					
//					
//					while (headersIt.hasNext()) {
//						cell = row.createCell(c++);
//						cell = row.createCell(c++);
//						cell = row.createCell(c++);
//						CellRangeAddress mergedRegion = new CellRangeAddress(0,0,(c-3),(c-1));
//						sheet.addMergedRegion(mergedRegion);
//						
//						row.getCell(c-3).setCellValue(finalHeader.get(headersIt.next()));
//						row.getCell(c-3).setCellStyle(headerCellStyle);
//						
//						Cell cell2 = subRow.createCell(c-3);
//						cell2.setCellValue("NÓMINA");
//						cell2.setCellStyle(headerCellStyle);
//						
//						cell2 = subRow.createCell(c-2);
//						cell2.setCellValue("SEG. SOCIAL");
//						cell2.setCellStyle(headerCellStyle);
//						
//						cell2 = subRow.createCell(c-1);
//						cell2.setCellValue("DIFERENCIA");
//						cell2.setCellStyle(headerCellStyle);
//						
//						
//					}
//					
//
//				}
//
////					private static String[] DEFAULT_HEADER = new String[] { "EMPLEADO", "BRUTO", "S.S. EMPLEADO", "IRPF",
////							"LÍQUIDO", "S.S. EMPRESA", "COSTE TOTAL", "S.S. TOTAL", "BONIFICACIONES", "BASE CGC", "BASE IRPF",
////							"CONT. COMUNES", "DESEMPLEO", "FORM. PROF." };
//
//				for (int i = 0, column = 0; i < arr.length; i++) {
//
//					IEnterprisePayroll payroll = (IEnterprisePayroll) arr[i];
//					row = sheet.createRow(sheet.getLastRowNum() + 1);
//
//					Cell cell = row.createCell(column++);
//					cell.setCellValue(payroll.getEmployee());
//					cell.setCellStyle(stringCellStyle);
//
//					if (thereIsRaw) {
//						createDoubleCell(row, column++, payroll.getRaw(), doubleCellStyle);
//						createDoubleCell(row, column++, 0d, ssCellStyle);
//						createDiffCell(sheet, row, column++, diffCellStyle);
//					}
//					
//					
//
//					if (thereIsEmployeeSS) {
//						createDoubleCell(row, column++, payroll.getEmployeeSS(), doubleCellStyle);
//						createDoubleCell(row, column++, 0d, ssCellStyle);
//						createDiffCell(sheet, row, column++, diffCellStyle);
//					}
//						
//
//					if (thereIsIrpf) {
//						createDoubleCell(row, column++, payroll.getIrpf(), doubleCellStyle);
//						createDoubleCell(row, column++, 0d, ssCellStyle);
//						createDiffCell(sheet, row, column++, diffCellStyle);
//					}
//					
//					
//					if (thereIsLiquid) {
//						createDoubleCell(row, column++, payroll.getLiquid(), doubleCellStyle);
//						createDoubleCell(row, column++, 0d, ssCellStyle);
//						createDiffCell(sheet, row, column++, diffCellStyle);
//					}
//					
//
//					if (thereIsEnterpriseSS) {
//						createDoubleCell(row, column++, payroll.getEnterpriseSS(), doubleCellStyle);
//						createDoubleCell(row, column++, 0d, ssCellStyle);
//						createDiffCell(sheet, row, column++, diffCellStyle);
//					}
//						
//
//					if (thereIsTotalCost) {
//						createDoubleCell(row, column++, payroll.getTotalCost(), doubleCellStyle);
//						createDoubleCell(row, column++, 0d, ssCellStyle);
//						createDiffCell(sheet, row, column++, diffCellStyle);
//					}
//					
//
//					if (thereIsTotalSS) {
//						createDoubleCell(row, column++, payroll.getTotalSS(), doubleCellStyle);
//						createDoubleCell(row, column++, 0d, ssCellStyle);
//						createDiffCell(sheet, row, column++, diffCellStyle);
//					}
//						
//
//					if (thereIsBonuses) {
//						createDoubleCell(row, column++, payroll.getBonuses(), doubleCellStyle);
//						createDoubleCell(row, column++, 0d, ssCellStyle);
//						createDiffCell(sheet, row, column++, diffCellStyle);
//					}
//						
//
//					if (thereIsCgcBase) {
//						createDoubleCell(row, column++, payroll.getCgcBase(), doubleCellStyle);
//						createDoubleCell(row, column++, 0d, ssCellStyle);
//						createDiffCell(sheet, row, column++, diffCellStyle);
//					}
//						
//
//					if (thereIsIrpfBase) {
//						createDoubleCell(row, column++, payroll.getIrpfBase(), doubleCellStyle);
//						createDoubleCell(row, column++, 0d, ssCellStyle);
//						createDiffCell(sheet, row, column++, diffCellStyle);
//					}
//					
//
//					if (thereIsCgc) {
//						createDoubleCell(row, column++, payroll.getCgc(), doubleCellStyle);
//						createDoubleCell(row, column++, 0d, ssCellStyle);
//						createDiffCell(sheet, row, column++, diffCellStyle);
//					}
//						
//
//					if (thereIsUnemployment) {
//						createDoubleCell(row, column++, payroll.getUnemployment(), doubleCellStyle);
//						createDoubleCell(row, column++, 0d, ssCellStyle);
//						createDiffCell(sheet, row, column++, diffCellStyle);
//					}
//						
//
//					if (thereIsJobTraining) {
//						createDoubleCell(row, column++, payroll.getJobTraining(), doubleCellStyle);
//						createDoubleCell(row, column++, 0d, ssCellStyle);
//						createDiffCell(sheet, row, column++, diffCellStyle);
//					}
//						
//
//					column = 0;
//				}
//
//				
//				for (int i = 0; i < finalHeader.size()*3-2; i++) {
//					sheet.autoSizeColumn(i);
//				}
//			} catch (java.lang.IllegalArgumentException e) {
//				System.err.println(e.getMessage());
//			}
//		}
//
//		wb.write(outputStream);
//		outputStream.close();
//		wb.close();
//
//	}
	
//	public static Stream<EnterprisePayroll> getEnterprisePayrolls(DSLContext ctx, Date month_year)
//			throws IOException {
//		
//		
//	}
	
	
	//MÉTODO NUEVO NO TERMINADO
	/*
	public static Stream<EnterprisePayroll> getEnterprisePayrolls(AONContext aonContext, final int month, final int year)
			throws IOException {
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		java.sql.Date dayOne = new java.sql.Date(calendar.getTime().getTime());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		java.sql.Date lastDay = new java.sql.Date(calendar.getTime().getTime());
		
		
		aonContext.getDslContext().select(SALARY.ID)
		.from(SALARY).innerJoin(CONTRACT).onKey()
		.innerJoin(WORKPLACE).onKey()
		.where(SALARY.ISSUE_DATE.between(dayOne, lastDay));
		
		
		
		Stream<Salary> salaries = AON.getSalaries(aonContext, s -> s.getIssueDateProperty().between(dayOne, lastDay));
		return salaries.map(s -> {
			
			
			EnterprisePayroll enterprisePayroll = new EnterprisePayroll();
			enterprisePayroll.employee = s.getEmployeeName();
			enterprisePayroll.workplace = null; //no workplace

			enterprisePayroll.irpf = s.getTotalIrpf();

			enterprisePayroll.cgcBase = s.getCommonContingenciesBase();
			enterprisePayroll.irpfBase = s.getIrpfBase();

			enterprisePayroll.raw = s.getTotalPayment();
			enterprisePayroll.liquid = s.getTotalLiquid();
			enterprisePayroll.employeeSS = s.getTotalSSContributions();

			enterprisePayroll.enterpriseSS = s.getCosts().stream().mapToDouble(Cost::getAmount).sum();
			
			enterprisePayroll.totalSS = enterprisePayroll.employeeSS + enterprisePayroll.enterpriseSS;
			enterprisePayroll.totalCost = enterprisePayroll.enterpriseSS + enterprisePayroll.irpf + enterprisePayroll.raw;

			enterprisePayroll.bonuses = s.getCosts().stream().mapToDouble(Cost::getAmount).sum();
			
			Double cgc = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.COMMON_CONTINGENCY.ordinal())
					.mapToDouble(d -> d.getAmount())
					.sum();
			Double unemployment = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.UNEMPLOYMENT.ordinal())
					.mapToDouble(d -> d.getAmount())
					.sum();
			Double jobTraining = s.getDeductions().stream()
					.filter(d -> d.getDeductionType().ordinal() == DeductionType.JOB_TRAINING.ordinal())
					.mapToDouble(d -> d.getAmount())
					.sum();
			
				enterprisePayroll.cgc = cgc;
				enterprisePayroll.unemployment = unemployment;
				enterprisePayroll.jobTraining = jobTraining;

			return enterprisePayroll;	
		});
	}
	*/
	public static Stream<EnterprisePayroll> getEnterprisePayrolls(DSLContext ctx, Condition condition)
			throws IOException {
		
//		EnterprisePayroll pruebaSegSocial = new EnterprisePayroll();
//		pruebaSegSocial.workplace = "PRINCIPAL - SEG. SOCIAL";
//		pruebaSegSocial.employee = "DAVID CASTAÑO, SANCHEZ";
//		pruebaSegSocial.employeeSS = 3d;
//		pruebaSegSocial.enterpriseSS = 0d;
		
		
		

		Map<Integer, Double> bonusesMap = ctx.select().from(SALARY).innerJoin(CONTRACT)
				.on(CONTRACT.ID.eq(SALARY.CONTRACT)).innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
				.innerJoin(SALARY_BONUS).on(SALARY.ID.eq(SALARY_BONUS.SALARY)).where(condition)
				.fetchStreamInto(SALARY_BONUS)
				.collect(Collectors.toMap(s -> s.getSalary(), s -> s.getAmount(), (a1, a2) -> a1 + a2));

//		System.out.println(ctx.select().from(SALARY).innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
//				.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID)).innerJoin(SALARY_BONUS)
//				.on(SALARY.ID.eq(SALARY_BONUS.SALARY)).where(condition).getSQL());

		Map<Integer, Map<String, Double>> deductions = new HashMap<Integer, Map<String, Double>>();
		ctx.select().from(SALARY).innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT)).innerJoin(WORKPLACE)
				.on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID)).innerJoin(SALARY_DEDUCTION)
				.on(SALARY.ID.eq(SALARY_DEDUCTION.SALARY)).where(condition).fetchStream().forEach(s -> {
					if (deductions.get(s.get(SALARY.ID)) != null) {
						deductions.get(s.get(SALARY.ID)).put(s.get(SALARY_DEDUCTION.DEDUCTION_CONCEPT),
								s.get(SALARY_DEDUCTION.AMOUNT));
					} else {
						Map<String, Double> map = new HashMap<String, Double>();
						map.put(s.get(SALARY_DEDUCTION.DEDUCTION_CONCEPT), s.get(SALARY_DEDUCTION.AMOUNT));
						deductions.put(s.get(SALARY.ID), map);

					}
				});

	/*Stream<EnterprisePayroll> ret =*/return ctx.select().from(SALARY).innerJoin(CONTRACT).onKey().innerJoin(WORKPLACE).onKey().where(condition)
				.fetchStream().map(record -> {
					EnterprisePayroll enterprisePayroll = new EnterprisePayroll();
					enterprisePayroll.employee = record.get(SALARY.EMPLOYEE_NAME);
					enterprisePayroll.workplace = record.get(WORKPLACE.DESCRIPTION);

					enterprisePayroll.irpf = record.get(SALARY.TOTAL_IRPF);

					enterprisePayroll.cgcBase = record.get(SALARY.CGC_BASE);
					enterprisePayroll.irpfBase = record.get(SALARY.IRPF_BASE);

					enterprisePayroll.raw = record.get(SALARY.TOTAL_PAYMENT);
					enterprisePayroll.liquid = record.get(SALARY.TOTAL_LIQUID);
					enterprisePayroll.employeeSS = record.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
//					enterprisePayroll.enterpriseSS = record.get(SALARY.TOTAL_ENTERPRISE);
					
					enterprisePayroll.enterpriseSS = ctx.select().from(SALARY_COST)
					.where(SALARY_COST.SALARY.eq(record.get(SALARY.ID)))
					.fetchStreamInto(SALARY_COST)
					.mapToDouble(SalaryCostRecord::getAmount)
					.sum();
					
					enterprisePayroll.totalSS = enterprisePayroll.employeeSS + enterprisePayroll.enterpriseSS;
					enterprisePayroll.totalCost = enterprisePayroll.enterpriseSS + enterprisePayroll.irpf + enterprisePayroll.raw;

					enterprisePayroll.bonuses = bonusesMap.get(record.get(SALARY.ID));

					Map<String, Double> map = deductions.get(record.get(SALARY.ID));
					if (map != null) {

						enterprisePayroll.cgc = map.get("CGC");
						enterprisePayroll.unemployment = map.get("DESMPL");
						enterprisePayroll.jobTraining = map.get("FP");
					}

					return enterprisePayroll;
				});
//		return Stream.concat(ret, Stream.of(pruebaSegSocial));
	}

	private static void createDoubleCell(Row row, int column, Double value, CellStyle doubleCellStyle) {
		Cell cell = row.createCell(column);
		if (value != null)
			cell.setCellValue(value);
			cell.setCellStyle(doubleCellStyle);
			
	}
	
	private static void createDiffCell (Sheet sheet, Row row, int column, CellStyle ssCellStyle) {
		Cell cell = row.createCell(column);
		cell.setCellType(CellType.FORMULA);
		cell.setCellFormula(CellReference.convertNumToColString(column-2)+(sheet.getLastRowNum()+1)+"-"+CellReference.convertNumToColString(column-1)+(sheet.getLastRowNum()+1));
		cell.setCellStyle(ssCellStyle);
	}
	
	public static String getEnterpriseName (String domainName, Integer enterpriseId, Integer workplaceId) {
		
		try (AONContext aonContext = AONContext.getAONContext(domainName, "")) {
			AtomicInteger eId = new AtomicInteger(enterpriseId);
			if (enterpriseId == null || enterpriseId == 0)
				eId.set(AON.getWorkplace(aonContext.getDomainName()
						, aonContext.getDomainId()
						, "", w -> w.getIdProperty().eq(workplaceId)).getEnterprise());
			return AON.getRegistry(aonContext.getDomainName()
					, aonContext.getDomainId()
					, aonContext.getUser()
					, r -> r.getIdProperty().eq(eId.get())).getName();
		}
	}
	public static class EnterprisePayroll implements IEnterprisePayroll {
		private String employee;
		private String workplace;

		private Double raw;
		private Double employeeSS;
		private Double irpf;
		private Double liquid;
		private Double enterpriseSS;
		private Double totalCost;
		private Double totalSS;
		private Double bonuses;

		private Double cgcBase;
		private Double irpfBase;

		private Double cgc;
		private Double unemployment;
		private Double jobTraining;

		@Override
		public String getEmployee() {
			return employee;
		}

		@Override
		public String getWorkplace() {
			return workplace;
		}

		@Override
		public Double getRaw() {
			return raw;
		}

		@Override
		public Double getEmployeeSS() {
			return employeeSS;
		}

		@Override
		public Double getIrpf() {
			return irpf;
		}

		@Override
		public Double getLiquid() {
			return liquid;
		}

		@Override
		public Double getEnterpriseSS() {
			return enterpriseSS;
		}

		@Override
		public Double getTotalCost() {
			return totalCost;
		}

		@Override
		public Double getTotalSS() {
			return totalSS;
		}

		@Override
		public Double getBonuses() {
			return bonuses;
		}

		@Override
		public Double getCgcBase() {
			return cgcBase;
		}

		@Override
		public Double getIrpfBase() {
			return irpfBase;
		}

		@Override
		public Double getCgc() {
			return cgc;
		}

		@Override
		public Double getUnemployment() {
			return unemployment;
		}

		@Override
		public Double getJobTraining() {
			return jobTraining;
		}

	}
}
