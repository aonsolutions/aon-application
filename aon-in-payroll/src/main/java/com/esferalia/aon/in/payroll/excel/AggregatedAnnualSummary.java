package com.esferalia.aon.in.payroll.excel;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.CraTypes;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.Bonus;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.occam.api.model.Salary.Cost;
import com.esferalia.aon.occam.api.model.Salary.Deduction;
import com.esferalia.aon.occam.api.model.Salary.Payment;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.DeductionType.Visitor;
import com.esferalia.aon.occam.api.model.type.PaymentType;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.watson.util.AonArrayUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AggregatedAnnualSummary {
	
	public enum SummaryType {
		MONTHLY,
		QUARTERLY;
	}
	
	private static final String TOTAL_NAME = "TOTALES";
	private static final String[] BOLD_FIELDS = {"TOTAL BRUTO","TOTAL LÍQUIDO","COSTE EMPRESA","RLC","BASE IRPF TOTAL","COSTE DIARIO", "TOTAL DEDUCCIONES"};
	private static final String[] MONTHS = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
	private static final String[] QUARTERS = {"1º TRIM", "2º TRIM", "3º TRIM", "4º TRIM"};
	private static final String[]  CRA1_CONCEPT_ORDER= {"Salario Base", "Pluses Salariales", "Otros Conceptos Salariales"};
	private static final Visitor<String> DEDUCTION_VISITOR = new DeductionType.Visitor<String>() {
		@Override
		public String visitCommonContigency(DeductionType deductionType) {
			return "CONTNGENCIAS COMUNES";
		}

		@Override
		public String visitProfessionalContigency(DeductionType deductionType) {
			return "CONTINGENCIAS PROFESIONALES";
		}

		@Override
		public String visitUnemployent(DeductionType deductionType) {
			return "DESEMPLEO";
		}

		@Override
		public String visitJobTraining(DeductionType deductionType) {
			return "FORMACIÓN PROFESIONAL";
		}

		@Override
		public String visitStructuralOvertime(DeductionType deductionType) {
			return "HORAS ESTRUCTURALES";
		}

		@Override
		public String visitNonStructuralOvertime(DeductionType deductionType) {
			return "HORAS NO ESTRUCTURALES";
		}

		@Override
		public String visitIrpf(DeductionType deductionType) {
			return "IRPF";
		}

		@Override
		public String visitAdvancePayment(DeductionType deductionType) {
			return "ADELANTOS";
		}

		@Override
		public String visitInkind(DeductionType deductionType) {
			return "EN ESPECIE";
		}

		@Override
		public String visitOther(DeductionType deductionType) {
			return "OTRAS DEDUCCIONES";
		}

		@Override
		public String visitFogasa(DeductionType deductionType) {
			return "FOGASA";
		}

		@Override
		public String visitIT(DeductionType deductionType) {
			return "INCAPACIDAD TEMPORAL";
		}
		
		@Override
		public String visitIMS(DeductionType deductionType) {
			return "IMS";
		}
	};

	public static void writeExcel (OutputStream oos, String domainName, String user, Optional<Integer> enterpriseId, Optional<Integer> workplaceId, Integer year, SummaryType type, boolean complete) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		Date startDate = calendar.getTime();
		
		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		
		Date endDate = calendar.getTime();
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, user)) {
			Condition condition = SALARY.ISSUE_DATE.ge(new java.sql.Date(startDate.getTime()))
					.and(SALARY.ISSUE_DATE.le(new java.sql.Date(endDate.getTime())));
			if (enterpriseId.isPresent() && enterpriseId.get() > 0)
				condition = condition.and(ENTERPRISE.REGISTRY.eq(enterpriseId.get()));
			if (workplaceId.isPresent() && workplaceId.get() > 0)
				condition = condition.and(WORKPLACE.ID.eq(workplaceId.get()));
			
			Map<String, AggregatedAnnualYearlyEntry> entries = getEntries(aonContext, condition, type);
			
			Enterprise enterprise = null;
			if (!enterpriseId.isEmpty() && enterpriseId.get() > 0) {
				enterprise = getEnterpriseById(aonContext, enterpriseId.get());
			} else if (!workplaceId.isEmpty() && workplaceId.get() > 0) {
				enterprise = getEnterpriseByWorkplaceId(aonContext, workplaceId.get());
			} else {
				enterprise = pickEnterpriseFromDomain(aonContext);
			}
			
			
			getExcel(oos, year, entries, enterprise.getName(), enterprise.getDocument(), type, complete);
		}
		
	}
	
	protected static void getExcel(OutputStream oos, Integer year,
			Map<String, AggregatedAnnualYearlyEntry> entries, String enterpriseName, String enterpriseDocument, SummaryType type, boolean complete) {
		try (Workbook wb = new XSSFWorkbook()) {
			
			String[] periods = null;
			
			if (type == SummaryType.MONTHLY)
				periods = MONTHS;
			else if (type == SummaryType.QUARTERLY)
				periods = QUARTERS;
			
			wb.createSheet(TOTAL_NAME);
			((XSSFSheet)wb.getSheet(TOTAL_NAME)).setTabColor(new XSSFColor(Color.GRAY));
			
			int firstDataRow = 0;
			
			
			//STYLES
			Map<String, CellStyle> stylesMap = getCellStyles(wb);
			
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> totalsFormulas = new LinkedHashMap<>();
			LinkedHashMap<String, LinkedHashMap<String, String>> totalSheetFormulas = new LinkedHashMap<>();
			
			totalsFormulas.put(TOTAL_NAME, totalSheetFormulas);
			
			
			LinkedHashMap<String, LinkedHashSet<String>> orderedConcepts = new LinkedHashMap<>();
				
			orderedConcepts.put("payments", new LinkedHashSet<>());
			orderedConcepts.put("deductions", new LinkedHashSet<>());
			orderedConcepts.put("daysAndHours", new LinkedHashSet<>());

			Collection<String> workplaces = null;
			if (complete) {
				try {
					workplaces = entries.values().stream().map(ent -> ent.getWorkplace()).filter(w -> w != null).distinct().collect(Collectors.toList());
				} catch (NullPointerException e) {
					workplaces = null;
				}
				
				if (workplaces != null) {
					workplaces.forEach(w -> {
						totalsFormulas.put(w, new LinkedHashMap<>());
						Sheet sh = wb.createSheet(WorkbookUtil.createSafeSheetName(w));
						((XSSFSheet)sh).setTabColor(new XSSFColor(Color.LIGHT_GRAY));
					});
				}
			}
				
			for (String identifier : entries.keySet()) {
				AggregatedAnnualYearlyEntry entry = entries.get(identifier);
				Sheet sheet = wb.createSheet(WorkbookUtil.createSafeSheetName(identifier));
				
				sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
				sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 2));
				sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 9));
				sheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 2));
				sheet.addMergedRegion(new CellRangeAddress(5, 5, 1, 2));
				sheet.addMergedRegion(new CellRangeAddress(5, 5, 4, 9));
				
				//TOP INFO
				Row row = sheet.createRow(1);
				
				Cell cell = row.createCell(0);
				
				cell.setCellType(CellType.STRING);
				cell.setCellValue("PERÍODO ANUAL DE 01/"+year+" A 12/"+year);
				cell.setCellStyle(stylesMap.get("headerInfoCellStyle"));
				
				row = sheet.createRow(2);
				cell = row.createCell(0);
				cell.setCellValue("NIF Empresa:");
				cell = row.createCell(1);
				cell.setCellValue(entry.getNif() != null ? entry.getNif() : "");
				cell.setCellStyle(stylesMap.get("headerInfoCellStyle"));
				
				row = sheet.getRow(2);
				cell = row.createCell(3);
				cell.setCellValue("Empresa:");
				cell = row.createCell(4);
				cell.setCellValue(enterpriseName != null ? enterpriseName : "");
				cell.setCellStyle(stylesMap.get("headerInfoCellStyle"));
				
				row = sheet.createRow(3);
				cell = row.createCell(0);
				cell.setCellValue("Centro Trabajo:");
				cell = row.createCell(1);
				cell.setCellValue(entry.getWorkplace() != null ? entry.getWorkplace() : "");
				cell.setCellStyle(stylesMap.get("headerInfoCellStyle"));
				
				row = sheet.createRow(5);
				cell = row.createCell(0);
				cell.setCellValue("NIF Empleado:");
				cell = row.createCell(1);
				cell.setCellValue(entry.getNif() != null ? entry.getNif() : "");
				cell.setCellStyle(stylesMap.get("headerInfoCellStyle"));
				
				row = sheet.getRow(5);
				cell = row.createCell(3);
				cell.setCellValue("Empleado:");
				cell = row.createCell(4);
				cell.setCellValue(entry.getEmployeeName() != null ? entry.getEmployeeName() : "");
				cell.setCellStyle(stylesMap.get("headerInfoCellStyle"));
				
				row = sheet.createRow(6);
				//ORGANIZING INFO
				LinkedHashSet<String> paymentConceptsSet = new LinkedHashSet<>();
				LinkedHashSet<String> deductionConceptsSet = new LinkedHashSet<>();
				
				paymentConceptsSet = getOrderedPaymentConcepts(entry);				
				
				
				entry.getMonthlyEntries().values().stream().map(AggregatedAnnualEntry::getDeductions).forEach(deductions-> {
					if (deductions != null) {
						deductions.stream()
						.filter(Objects::nonNull)
						.sorted(Comparator.comparing(d -> d.getDeductionType() != null ? d.getDeductionType() : DeductionType.values()[DeductionType.values().length-1], Comparator.naturalOrder()))
						.forEach(deduction -> {
							String name = deduction.getDescription() != null ? deduction.getDescription() : deduction.getDeductionType().name();
							deductionConceptsSet.add(name);
						});
					}
				});
				
				LinkedList<String> paymentConcepts = new LinkedList<>();
				paymentConceptsSet.forEach(paymentConcepts::add);
				Pattern pattern = Pattern.compile(".*salario.*base.*", Pattern.CASE_INSENSITIVE);
				Optional<String> optBaseSalary = paymentConcepts.stream().filter(p -> {
					Matcher matcher = pattern.matcher(p);
					if (matcher.matches())
						return true;
					return false;
				}).findFirst();
				
				if (!optBaseSalary.isEmpty() ) {
					int ind = paymentConcepts.indexOf(optBaseSalary.get());
					paymentConcepts.remove(ind);
					Deque<String> dequeue = paymentConcepts;
					dequeue.addFirst(optBaseSalary.get());
					paymentConcepts.clear();
					paymentConcepts.addAll(dequeue);
				}
				
				//MONTHS HEADER
				
				
				writeMonthsHeader(sheet, stylesMap, periods);
				
				firstDataRow = sheet.getLastRowNum() + 1;
				
				//PAYMENTS
				
				writePayments(sheet, stylesMap, paymentConceptsSet, orderedConcepts, totalsFormulas, entry, identifier,
						periods, complete);
				
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//TOTAL RAW
				putDataRow("TOTAL BRUTO", periods, complete, stylesMap, totalsFormulas, identifier, entry, sheet, row);
				
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//DEDUCTIONS
				writeDeductions(sheet, stylesMap, totalsFormulas, orderedConcepts, deductionConceptsSet, entry, identifier,
						periods, complete);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//TOTAL LIQUID
				putDataRow("TOTAL LÍQUIDO", periods, complete, stylesMap, totalsFormulas, identifier, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//EXTRA PRORATION
				putDataRow("PRORRATA PAGAS EXTRAS", periods, complete, stylesMap, totalsFormulas, identifier, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//BONUSES
				putDataRow("BONIFICACIONES/REDUCCIONES", periods, complete, stylesMap, totalsFormulas, identifier, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//ENTERPRISE SS
				putDataRow("SEG.SOCIAL EMPRESA", periods, complete, stylesMap, totalsFormulas, identifier, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//ENTERPRISE COST
				putDataRow("COSTE EMPRESA", periods, complete, stylesMap, totalsFormulas, identifier, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//RLC
				putDataRow("RLC", periods, complete, stylesMap, totalsFormulas, identifier, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//CC BASE
				putDataRow("BASE CONTINGENCIAS COMUNES", periods, complete, stylesMap, totalsFormulas, identifier, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//IT BASE
				putDataRow("BASE ACCIDENTES", periods, complete, stylesMap, totalsFormulas, identifier, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//IRPF MONEY BASE
				putDataRow("BASE IRPF DINERARIA", periods, complete, stylesMap, totalsFormulas, identifier, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//IRPF IN-KIND BASE
				putDataRow("BASE IRPF EN ESPECIE", periods, complete, stylesMap, totalsFormulas, identifier, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//IRPF TOTAL BASE
				putDataRow("BASE IRPF TOTAL", periods, complete, stylesMap, totalsFormulas, identifier, entry, sheet, row);

				//DAYS AND HOURS
				writeDaysAndHours(sheet, stylesMap, orderedConcepts, totalsFormulas, entry, identifier, periods, complete);
				
				sheet.createFreezePane(3, firstDataRow, 3, firstDataRow);
				
				
				
			}
			
			//RESIZE AND SOME STYLES
			for (Sheet sheet : wb) {
				if (!totalsFormulas.keySet().contains(sheet.getSheetName()))
					resizeSheet(firstDataRow, stylesMap, sheet);
			}
			
			//TOTALS
			for (String workplace : totalsFormulas.keySet()) {
				
				Sheet totalSheet = wb.getSheet(WorkbookUtil.createSafeSheetName(workplace));
				
				totalSheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
				totalSheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 2));
				totalSheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 9));
				
				
				Row row = totalSheet.createRow(1);
				
				Cell cell = row.createCell(0);
				
				cell.setCellType(CellType.STRING);
				cell.setCellValue("PERÍODO ANUAL DE 01/"+year+" A 12/"+year);
				cell.setCellStyle(stylesMap.get("headerInfoCellStyle"));
				
				row = totalSheet.createRow(2);
				cell = row.createCell(0);
				cell.setCellValue("NIF Empresa:");
				cell = row.createCell(1);
				cell.setCellValue(enterpriseDocument != null ? enterpriseDocument : "");
				cell.setCellStyle(stylesMap.get("headerInfoCellStyle"));
				
				row = totalSheet.getRow(2);
				cell = row.createCell(3);
				cell.setCellValue("Empresa:");
				cell = row.createCell(4);
				cell.setCellValue(enterpriseName != null ? enterpriseName : "");
				cell.setCellStyle(stylesMap.get("headerInfoCellStyle"));
				
				row = totalSheet.createRow(3);
				
				if (!workplace.equals(TOTAL_NAME)) {
					totalSheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 2));
					cell = row.createCell(0);
					cell.setCellValue("Centro Trabajo:");
					cell = row.createCell(1);
					cell.setCellValue(workplace != null ? workplace : "");
					cell.setCellStyle(stylesMap.get("headerInfoCellStyle"));
					
					row = totalSheet.createRow(4);
				}
				
				//MONTHS HEADER
			
				writeMonthsHeader(totalSheet, stylesMap, periods);
				firstDataRow = totalSheet.getLastRowNum() + 1;
				
				//PAYMENTS
				LinkedHashSet<String> paymentsSet = orderedConcepts.get("payments");
				
				orderSet(paymentsSet);
				
				final String[] finalPeriods = periods;
				
				paymentsSet.forEach(pay -> {
					Row pRow = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
					totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, pRow, finalPeriods, pay);
				});
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//RAW
				totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, row, finalPeriods, "TOTAL BRUTO", true);
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//DEDUCTIONS
				{
					LinkedHashSet<String> deductionsSet = orderedConcepts.get("deductions");
					deductionsSet.forEach(ded -> {
						Row dRow = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
						totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, dRow, finalPeriods, spaDeduction(ded));
					});
				}
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//TOTAL DEDUCTIONS
				totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, row, finalPeriods, "TOTAL DEDUCCIONES", true);
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//TOTAL LIQUID
				totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, row, finalPeriods, "TOTAL LÍQUIDO", true);
				
//				for (String wea : totalsFormulas.get(workplace).keySet()) {
//					System.out.println(wea + " : " + totalsFormulas.get(workplace).get(wea));
//				}
//				System.out.println("----------------------------------------");
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//EXTRA PAY PRO.
				totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, row, finalPeriods, "PRORRATA PAGAS EXTRAS");
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//BONUSES
				totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, row, finalPeriods, "BONIFICACIONES/REDUCCIONES");
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//ENTERPRISE SS
				totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, row, finalPeriods, "SEG.SOCIAL EMPRESA");
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//ENTERPRISE COST
				totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, row, finalPeriods, "COSTE EMPRESA", true);
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				//RLC
				totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, row, finalPeriods, "RLC", true);
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//CC BASE
				totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, row, finalPeriods, "BASE CONTINGENCIAS COMUNES");
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//MONEY IRPF BASE
				totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, row, finalPeriods, "BASE IRPF DINERARIA");
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
								
				//IN-KIND IRPF BASE
				totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, row, finalPeriods, "BASE IRPF EN ESPECIE");
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//TOTAL IRPF BASE
				totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, row, finalPeriods, "BASE IRPF TOTAL", true);
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//DAYS AND HOURS
				{
					LinkedHashSet<String> dahSet = orderedConcepts.get("daysAndHours");
					String[] topConcepts = {"DIAS_TRABAJADOS", "HORAS_NOMINA", "DIAS_NOMINA", "COSTE_DIARIO"};
					
					for (int i=0; i<topConcepts.length; i++) {
						if (dahSet.contains(topConcepts[i])) {
							Row dRow = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
							if(topConcepts[i].equals("COSTE_DIARIO"))
								totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, dRow, finalPeriods, topConcepts[i], true);
							else
								totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, dRow, finalPeriods, topConcepts[i]);
						}						
					}
					row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
					row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
					
					totalSheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));
					cell = row.createCell(0);
					cell.setCellType(CellType.STRING);
					cell.setCellValue("INFORMACIÓN ADICIONAL");
					
					dahSet.stream().filter(str -> !AonArrayUtils.constainsIgnoreCase(topConcepts, str)).forEach(dah -> {
						Row dRow = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
						totalsCellCreator(stylesMap, totalsFormulas.get(workplace), totalSheet, dRow, finalPeriods, dah);
					});
				}

				
				totalSheet.createFreezePane(3, firstDataRow, 3, firstDataRow);
				
				resizeSheet(firstDataRow,stylesMap, totalSheet);
				
			}
			
			
			wb.write(oos);
			
			
			
		} catch (IOException e) {}
	}
	private static void writeDeductions(Sheet sheet, Map<String, CellStyle> stylesMap,
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> totalsFormulas,
			LinkedHashMap<String, LinkedHashSet<String>> orderedConcepts, LinkedHashSet<String> deductionConceptsSet,
			AggregatedAnnualYearlyEntry entry, String nif, String[] periods, boolean complete) {
		Row row;
		Cell cell;
		LinkedHashSet<String> deductionSet = orderedConcepts.get("deductions");
		deductionConceptsSet.forEach(concept -> {
				Row deductionRow = sheet.createRow(sheet.getLastRowNum()+1);
				sheet.addMergedRegion(new CellRangeAddress(deductionRow.getRowNum(), deductionRow.getRowNum(), 0, 2));
				Cell deductionCell = deductionRow.createCell(0);
				deductionCell.setCellType(CellType.STRING);
				if (concept != null)
					deductionCell.setCellValue(spaDeduction(concept));
				deductionSet.add(concept);
				
				int[] amountCellNum = {3};
				Arrays.stream(periods).forEach(period -> {
					Cell amountCell = deductionRow.createCell(amountCellNum[0]++);
					
					String formula = "'" + nif + "'" + "!" + CellReference.convertNumToColString(amountCell.getColumnIndex()) + (amountCell.getRowIndex()+1);
					
					putDeductionFormula(entry.getWorkplace(), totalsFormulas, concept, period, formula, complete);
					
					if (entry.getMonthlyEntries().get(period) != null) {
						amountCell.setCellType(CellType.NUMERIC);
						
						double amount = entry.getMonthlyEntries().get(period).getDeductions().stream().filter(d -> {
							if (d != null) {
								if (d.getDeductionType() != null) {
									String name = d.getDescription() != null ? d.getDescription() : d.getDeductionType().name();
									return name.equals(concept);
								}
							}
							return false;
						}).mapToDouble(d -> d.getAmount()!= null ? d.getAmount() : 0).sum();
						if (amount != 0d)
							amountCell.setCellValue(amount);
						amountCell.setCellStyle(stylesMap.get("numberCellStyle"));
					}
				});
				Cell totalCell = deductionRow.createCell(amountCellNum[0]);
				totalCell.setCellType(CellType.FORMULA);
				int realRowNum = totalCell.getRowIndex()+1;
				totalCell.setCellFormula("SUM(D"+realRowNum+":"+CellReference.convertNumToColString(totalCell.getColumnIndex()-1)+realRowNum+")");
		});
		
		row = sheet.createRow(sheet.getLastRowNum() +1);
		
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));
		
		cell = row.createCell(0);
		cell.setCellType(CellType.STRING);
		cell.setCellValue("TOTAL DEDUCCIONES");
		
		int[] totalDeductionCellNum = {3};
		Arrays.stream(periods).forEach(period -> {
			AggregatedAnnualEntry ent = entry.getMonthlyEntries().get(period);
			Row totalDedRow = sheet.getRow(sheet.getLastRowNum());
			Cell totalDedCell = totalDedRow.createCell(totalDeductionCellNum[0]++);
			
			String formula = "'" + nif + "'" + "!" + CellReference.convertNumToColString(totalDedCell.getColumnIndex()) + (totalDedCell.getRowIndex()+1);
			putDeductionFormula(entry.getWorkplace(), totalsFormulas, "TOTAL DEDUCCIONES", period, formula, complete);
			
			totalDedCell.setCellType(CellType.NUMERIC);
			if (ent != null && ent.getTotalDeduction() != null)
				totalDedCell.setCellValue(ent.getTotalDeduction());
			totalDedCell.setCellStyle(stylesMap.get("boldNumberCellStyle"));
		});
		
		Cell totalCell = row.createCell(totalDeductionCellNum[0]);
		totalCell.setCellType(CellType.FORMULA);
		int rRowNum = totalCell.getRowIndex()+1;
		totalCell.setCellFormula("SUM(D"+rRowNum+":"+CellReference.convertNumToColString(totalCell.getColumnIndex()-1)+rRowNum+")");
	}
	private static void writePayments(Sheet sheet, Map<String, CellStyle> stylesMap,
			LinkedHashSet<String> paymentConceptsSet, LinkedHashMap<String, LinkedHashSet<String>> orderedConcepts,
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> totalsFormulas,
			AggregatedAnnualYearlyEntry entry, String nif, String[] periods, boolean complete) {
		
		LinkedHashSet<String> conceptSet = orderedConcepts.get("payments");
		Pattern noWords = Pattern.compile("[A-Za-z]+");
		paymentConceptsSet.forEach(concept -> {
			Row paymentRow = sheet.createRow(sheet.getLastRowNum()+1);
			sheet.addMergedRegion(new CellRangeAddress(paymentRow.getRowNum(), paymentRow.getRowNum(), 0, 2));
			Cell paymentCell = paymentRow.createCell(0);
			paymentCell.setCellType(CellType.STRING);
			//WRITE PAYMENT NAME
			if (concept != null) {
				String definitive = getDefinitivePaymentConcept(concept);
				
				paymentCell.setCellValue(removeUnderscore(definitive));
					
			}
			
			int[] amountCellNum = {3};
			conceptSet.add(concept);
			
			Arrays.stream(periods).forEach(period -> {
				Cell amountCell = paymentRow.createCell(amountCellNum[0]++);
				
				String formula = "'" + nif + "'" + "!" + CellReference.convertNumToColString(amountCell.getColumnIndex()) + (amountCell.getRowIndex()+1);
				
				putPaymentAndDaHFormula(entry.getWorkplace(),totalsFormulas, concept, period, formula, complete);
				
				
				
				if (entry.getMonthlyEntries().get(period) != null) {
					amountCell.setCellType(CellType.NUMERIC);
					Collection<Payment> paym = entry.getMonthlyEntries().get(period).getPayments();
					
					double amount = paym.stream().filter(p -> {
						if (p != null) {
							String desc = choosePaymentName(p);
							if (desc != null) {
								Matcher matcher = noWords.matcher(desc);
								if (!matcher.find())
									desc = p.getName();
							}
							if (desc != null) {
								return desc.equals(concept);
							} else {
								return desc == concept;
							}
						}
						return false;
					}).mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0).sum();
					if (amount != 0d)
						amountCell.setCellValue(amount);
					amountCell.setCellStyle(stylesMap.get("numberCellStyle"));
				}
			});
			Cell totalCell = paymentRow.createCell(amountCellNum[0]);
			totalCell.setCellType(CellType.FORMULA);
			int realRowNum = totalCell.getRowIndex()+1;
			totalCell.setCellFormula("SUM(D"+realRowNum+":"+CellReference.convertNumToColString(totalCell.getColumnIndex()-1)+realRowNum+")");
			
		});
	}
	private static void writeMonthsHeader(Sheet sheet, Map<String, CellStyle> stylesMap, String[] periods) {
		Row row;
		Cell cell;
		row = sheet.createRow(sheet.getLastRowNum()+1);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));
		cell = row.createCell(0);
		cell.setCellType(CellType.STRING);
		cell.setCellValue("CONCEPTO");
		cell.setCellStyle(stylesMap.get("importantCellStyle"));
		int[] cellNum = {3};
		Arrays.stream(periods).forEach(period -> {
			Cell monthCell = sheet.getRow(sheet.getLastRowNum()).createCell(cellNum[0]++);
			monthCell.setCellType(CellType.STRING);
			monthCell.setCellValue(period);
			monthCell.setCellStyle(stylesMap.get("monthCellStyle"));
		});
		
		Cell monthCell = sheet.getRow(sheet.getLastRowNum()).createCell(cellNum[0]);
		monthCell.setCellType(CellType.STRING);
		monthCell.setCellValue("TOTAL");
		monthCell.setCellStyle(stylesMap.get("topRightBorderCellStyle"));
		
		row.getCell(3).setCellStyle(stylesMap.get("topLeftBorderCellStyle"));
	}
	private static void writeDaysAndHours(Sheet sheet, Map<String, CellStyle> stylesMap,
			LinkedHashMap<String, LinkedHashSet<String>> orderedConcepts,
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> totalsFormulas,
			AggregatedAnnualYearlyEntry entry, String nif, String[] periods, boolean complete) {
		Row row;
		Cell cell;
		{
			LinkedHashSet<String> dahNames = new LinkedHashSet<>();
			entry.getMonthlyEntries().values().stream().map(AggregatedAnnualEntry::getDaysAndHours).forEach(dah -> {
				if (dah != null)
					dah.keySet().forEach(dahNames::add);
			});
			
			if (!dahNames.isEmpty()) {
				LinkedHashSet<String> dahSet = orderedConcepts.get("daysAndHours");
				
				String[] topConcepts = {"DIAS_TRABAJADOS", "HORAS_NOMINA", "DIAS_NOMINA", "COSTE_DIARIO"};
				row = sheet.createRow(sheet.getLastRowNum() +1);
				for (String str : topConcepts) {
					Row dahRow = sheet.createRow(sheet.getLastRowNum() +1);
					createDahRow(periods, complete, stylesMap, totalsFormulas, nif, entry, sheet, dahSet, str,
							dahRow);
				}
				
				row = sheet.createRow(sheet.getLastRowNum() +1);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));
				cell = row.createCell(0);
				cell.setCellType(CellType.STRING);
				cell.setCellValue("INFORMACIÓN ADICIONAL");
				
				
				dahNames.stream().filter(str -> !AonArrayUtils.constainsIgnoreCase(topConcepts, str)).forEach(dahName -> {
					Row dahRow = sheet.createRow(sheet.getLastRowNum() +1);
					createDahRow(periods, complete, stylesMap, totalsFormulas, nif, entry, sheet, dahSet, dahName,
							dahRow);
				});
			}
			
		}
	}
	
	private static int assignNumber (String str) {
		
		if (CRA1_CONCEPT_ORDER[0].equals(str))
			return 1;
		else if (CRA1_CONCEPT_ORDER[1].equals(str))
			return 2;
		else if (CRA1_CONCEPT_ORDER[2].equals(str))
			return 3;
		else {
			try {
				int ordinal = PaymentType.valueOf(str).ordinal();
				return ordinal > 0 ? ordinal + 3 : ordinal;
			} catch (Exception e) {
				return PaymentType.values().length + 3;
			}
		}
	}
	
	private static Enterprise getEnterpriseById (AONContext aonContext, Integer enterpriseId) {
		return AON.getEnterprise(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(), enterpriseId);
	}
	
	private static Enterprise getEnterpriseByWorkplaceId (AONContext aonContext, Integer workplaceId) {
		Workplace workplace = AON.getWorkplace(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(), f -> f.getIdProperty().eq(workplaceId));
		Integer enterpriseId = workplace.getEnterprise();
		return getEnterpriseById(aonContext, enterpriseId);
	}
	
	private static Enterprise pickEnterpriseFromDomain (AONContext aonContext) {
		EnterpriseRecord registry = aonContext.getDslContext()
		.select()
		.from(ENTERPRISE)
		.where(ENTERPRISE.DOMAIN.eq(aonContext.getDomainId()))
		.fetchOneInto(ENTERPRISE);
		
		Integer enterpriseId = registry.getRegistry();
		
		return getEnterpriseById(aonContext, enterpriseId);
	}
	
	private static void orderSet(LinkedHashSet<String> paymentsSet) {
		TreeMap<Integer, String> withOrder = new TreeMap<>();
		paymentsSet.forEach(str -> withOrder.put(assignNumber(str), str));
		paymentsSet.clear();
		paymentsSet.addAll(withOrder.values());
	}

	private static void createDahRow(String[] periods, boolean complete, Map<String, CellStyle> stylesMap,
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> totalsFormulas, String nif,
			AggregatedAnnualYearlyEntry entry, Sheet sheet, LinkedHashSet<String> dahSet, String dahName, Row dahRow) {
		sheet.addMergedRegion(new CellRangeAddress(dahRow.getRowNum(), dahRow.getRowNum(), 0, 2));
		Cell dahCell = dahRow.createCell(0);
		dahCell.setCellType(CellType.STRING);
		dahCell.setCellValue(removeUnderscore(dahName));
		dahSet.add(dahName);
		
		int[] cNum = {3};
		
		Arrays.stream(periods).forEach(period -> {
			Map<String, Double> dah = entry.getMonthlyEntries().get(period) != null ? entry.getMonthlyEntries().get(period).getDaysAndHours() : null;
			Cell dCell = dahRow.createCell(cNum[0]);
			
			String formula = "'" + nif + "'" + "!" + CellReference.convertNumToColString(dCell.getColumnIndex()) + (dCell.getRowIndex()+1);
			
			putPaymentAndDaHFormula(entry.getWorkplace(), totalsFormulas, dahName, period, formula, complete);
			
			dCell.setCellType(CellType.NUMERIC);
			dCell.setCellStyle(stylesMap.get(AonStringUtils.equalsIgnoreCase(dahName, "COSTE_DIARIO") ? "boldNumberCellStyle" : "numberCellStyle"));
			if (dah != null && dah.get(dahName) != null) {
				dCell.setCellValue(dah.get(dahName));
			}
			cNum[0]++;
		});
		dahCell = dahRow.createCell(cNum[0]);
		int realRowNum = dahCell.getRowIndex()+1;
		dahCell.setCellFormula("SUM(D"+realRowNum+":"+CellReference.convertNumToColString(dahCell.getColumnIndex()-1)+realRowNum+")");
	}

	private static String getDefinitivePaymentConcept(String concept) {
		if (concept != null) {
			String definitive = concept;
			if (concept.contains("CRA_00")) {
				try {
					PaymentType type = PaymentType.valueOf(concept);
					definitive = CraTypes.getType(type.ordinal(), new Locale("es", "ES"));
					String craNum = "[" + concept.substring(concept.indexOf('_') + 1) + "] ";
					definitive = craNum + definitive;
				} catch (Exception e) {}
			} else if (AonArrayUtils.constainsIgnoreCase(CRA1_CONCEPT_ORDER, concept)){
				definitive = "[0001] " + definitive;
			}
			return definitive;
		}
		return null;
	}

	private static LinkedHashSet<String> getOrderedPaymentConcepts(AggregatedAnnualYearlyEntry entry) {
		LinkedHashSet<String> paymentConceptsSet = new LinkedHashSet<>();
		
		entry.getMonthlyEntries().values().stream().map(AggregatedAnnualEntry::getPayments).forEach(payments -> {
			payments.stream()
			.filter(Objects::nonNull)
			.map(p -> {
				return choosePaymentName(p);
//				if (p.getPaymentType() != null) {
//					return choosePaymentName(p);
//				} else if (p.getName() != null) {					
//					return p.getName();
//				} else {
//					return "Otros conceptos";
//				}
			})
			.forEach(paymentConceptsSet::add);
		});
		
		orderSet(paymentConceptsSet);
		return paymentConceptsSet;
	}

	private static String choosePaymentName(Payment payment) {
		if (payment.getPaymentType() == PaymentType.CRA_0001) {
			if (AonStringUtils.containsIgnoreCase(payment.getName(), "SALARIO")) {
				return "Salario Base";
			} else if (AonStringUtils.containsIgnoreCase(payment.getName(), "PLUS")) {
				return "Pluses Salariales";
			} else {
				return "Otros Conceptos Salariales";
			}
		} else if (payment.getPaymentType() != null) {
			return payment.getPaymentType().name();
		} /*else if (payment.getName() != null) {		//DESCOMENTAR PARA QUE EN PONGA NOMBRE O DESC EN VEZ DE "OTROS CONCEPTOS"
			return payment.getName();
		} else if (payment.getDescription() != null) {
			return payment.getDescription();
		}*/
		return "Otros conceptos";
	}

	private static void putDataRow(String dataName, String[] periods, boolean complete, Map<String, CellStyle> stylesMap,
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> totalsFormulas, String nif,
			AggregatedAnnualYearlyEntry entry, Sheet sheet, Row row) {
		
		Cell cell;
		{
			cell = row.createCell(0);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(dataName);
			
			int cellInd = 3;
			boolean hasContent = false;
			for (String period : periods) {
				CellStyle amountCellStyle = stylesMap.get("numberCellStyle");
				AggregatedAnnualEntry ent = entry.getMonthlyEntries().get(period);
				cell = row.createCell(cellInd++);
				
				cell.setCellType(CellType.NUMERIC);

				Double amount = null;
				if (ent != null) {
					switch (dataName) {
					case "TOTAL BRUTO":
						amount = ent.getTotalRaw();
						amountCellStyle = stylesMap.get("boldNumberCellStyle");
						break;
					case "TOTAL LÍQUIDO":
						amount = ent.getTotalLiquid();
						amountCellStyle = stylesMap.get("boldNumberCellStyle");
						break;
					case "PRORRATA PAGAS EXTRAS":
						amount = ent.getExtraProrration();
						break;
					case "BONIFICACIONES/REDUCCIONES":
						amount = ent.getBonuses();
						break;
					case "SEG.SOCIAL EMPRESA":
						amount = ent.getEnterpriseSS();
						break;
					case "COSTE EMPRESA":
						amount = ent.getEnterpriseCost();
						amountCellStyle = stylesMap.get("boldNumberCellStyle");
						break;
					case "BASE CONTINGENCIAS COMUNES":
						amount = ent.getCcBase();
						break;
					case "BASE ACCIDENTES":
						amount = ent.getAccBase();
						break;
					case "BASE IRPF DINERARIA":
						amount = ent.getMoneyIrpfBase();
						break;
					case "BASE IRPF EN ESPECIE":
						amount = ent.getInKindIrpfBase();
						break;
					case "BASE IRPF TOTAL":
						amount = ent.getTotalIrpfBase();
						amountCellStyle = stylesMap.get("boldNumberCellStyle");
						break;
					case "RLC":
						amount = ent.getRlc();
						amountCellStyle = stylesMap.get("boldNumberCellStyle");
						break;
					default:
						amount = null;
					}
				}
				
				
				if (ent != null && amount != null) {
					hasContent = true;
					cell.setCellValue(amount);
					
					String formula = "'" + nif + "'" + "!" + CellReference.convertNumToColString(cell.getColumnIndex()) + (cell.getRowIndex()+1);
					putDataFormula(entry.getWorkplace(), dataName, totalsFormulas, period, formula, complete);
					
				}
				cell.setCellStyle(amountCellStyle);
			}
			cell = row.createCell(cellInd);
			cell.setCellType(CellType.FORMULA);
			int realRowNum = cell.getRowIndex()+1;
			cell.setCellFormula("SUM(D"+realRowNum+":"+CellReference.convertNumToColString(cell.getColumnIndex()-1)+realRowNum+")");
			if (!hasContent)
				sheet.removeRow(row);
			else {
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));
			}
		}
	}

	private static void putDataFormula(String sheetName, String concept,
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> totalsFormulas, String month,
			String formula, boolean complete) {
		if (sheetName != null && complete) {
			if (totalsFormulas.get(sheetName).containsKey(concept)) {
				LinkedHashMap<String, String> monthly = totalsFormulas.get(sheetName).get(concept);
				if (monthly.containsKey(month)) {
					String form = monthly.get(month);
					monthly.put(month, form + "+" + formula);
				} else {
					monthly.put(month, formula);
				}
			} else {
				LinkedHashMap<String, String> monthly = new LinkedHashMap<>();
				monthly.put(month, formula);
				totalsFormulas.get(sheetName).put(concept, monthly);
			}
		}
		
		if (totalsFormulas.get(TOTAL_NAME).containsKey(concept)) {
			LinkedHashMap<String, String> monthly = totalsFormulas.get(TOTAL_NAME).get(concept);
			if (monthly.containsKey(month)) {
				String form = monthly.get(month);
				monthly.put(month, form + "+" + formula);
			} else {
				monthly.put(month, formula);
			}
		} else {
			LinkedHashMap<String, String> monthly = new LinkedHashMap<>();
			monthly.put(month, formula);
			totalsFormulas.get(TOTAL_NAME).put(concept, monthly);
		}
	}

	private static void putDeductionFormula(String sheetName,
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> totalsFormulas, String concept,
			String month, String formula, boolean complete) {
		if (sheetName != null && complete) {
			if (totalsFormulas.get(sheetName).containsKey(spaDeduction(concept))) {
				LinkedHashMap<String, String> monthly = totalsFormulas.get(sheetName).get(spaDeduction(concept));
				if (monthly.containsKey(month)) {
					String form = monthly.get(month);
					monthly.put(month, form + "+" + formula);
				} else {
					monthly.put(month, formula);
				}
			} else {
				LinkedHashMap<String, String> monthly = new LinkedHashMap<>();
				monthly.put(month, formula);
				totalsFormulas.get(sheetName).put(spaDeduction(concept), monthly);
			}
		}
		
		if (totalsFormulas.get(TOTAL_NAME).containsKey(spaDeduction(concept))) {
			LinkedHashMap<String, String> monthly = totalsFormulas.get(TOTAL_NAME).get(spaDeduction(concept));
			if (monthly.containsKey(month)) {
				String form = monthly.get(month);
				monthly.put(month, form + "+" + formula);
			} else {
				monthly.put(month, formula);
			}
		} else {
			LinkedHashMap<String, String> monthly = new LinkedHashMap<>();
			monthly.put(month, formula);
			totalsFormulas.get(TOTAL_NAME).put(spaDeduction(concept), monthly);
		}
	}

	private static void putPaymentAndDaHFormula(String sheetName,
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> totalsFormulas, String concept,
			String month, String formula, boolean complete) {
		if (sheetName != null && complete) {
			if (totalsFormulas.get(sheetName).containsKey(concept)) {
				LinkedHashMap<String, String> monthly = totalsFormulas.get(sheetName).get(concept);
				if (monthly.containsKey(month)) {
					String form = monthly.get(month);
					monthly.put(month, form + "+" + formula);
				} else {
					monthly.put(month, formula);
				}
			} else {
				LinkedHashMap<String, String> monthly = new LinkedHashMap<>();
				monthly.put(month, formula);
				totalsFormulas.get(sheetName).put(concept, monthly);
			}
		}
		
		if (totalsFormulas.get(TOTAL_NAME).containsKey(concept)) {
			LinkedHashMap<String, String> monthly = totalsFormulas.get(TOTAL_NAME).get(concept);
			if (monthly.containsKey(month)) {
				String form = monthly.get(month);
				monthly.put(month, form + "+" + formula);
			} else {
				monthly.put(month, formula);
			}
		} else {
			LinkedHashMap<String, String> monthly = new LinkedHashMap<>();
			monthly.put(month, formula);
			totalsFormulas.get(TOTAL_NAME).put(concept, monthly);
		}
	}

	private static void resizeSheet(int firstDataRow, Map<String, CellStyle> stylesMap, Sheet sheet) {
		int[] firstDataRowArr = {firstDataRow};
		Row referenceRow = getHeaderRow(sheet);
		int max = referenceRow != null ? referenceRow.getLastCellNum() - 1 : 0;
		sheet.rowIterator().forEachRemaining(r -> {
			CellStyle left = stylesMap.get("leftBorderCellStyle");
			CellStyle right = stylesMap.get("rightBorderCellStyle");
			if (r.getCell(0) != null && AonArrayUtils.constainsIgnoreCase(BOLD_FIELDS, r.getCell(0).getStringCellValue())) {
				left = stylesMap.get("leftBorderCellStyleBold");
				right = stylesMap.get("rightBorderCellStyleBold");
			}
			
			
			
			if (r.getRowNum() > firstDataRowArr[0] - 1) {
				Cell  cel = null;
				cel = r.getCell(max) != null ? r.getCell(max) : r.createCell(max);
				
				cel.setCellStyle(right);
				cel = r.getCell(3) != null ? r.getCell(3) : r.createCell(3);
				cel.setCellStyle(left);
				cel = r.getCell(0) != null ? r.getCell(0) : r.createCell(0);
				
				CellStyle conceptStyle = stylesMap.get("leftBorderCellStyle");
				if (cel.getCellTypeEnum() == CellType.STRING) {
					switch (cel.getStringCellValue()) {
						case "TOTAL BRUTO":
						case "TOTAL LÍQUIDO":
						case "BASE IRPF TOTAL":
						case "COSTE EMPRESA":
						case "RLC":
						case "COSTE DIARIO":
						case "INFORMACIÓN ADICIONAL":
						case "TOTAL DEDUCCIONES":
							conceptStyle = stylesMap.get("importantCellStyle");
							break;
					}
				}
				
				cel.setCellStyle(r.getRowNum() > firstDataRowArr[0] - 1 ? conceptStyle : cel.getCellStyle());
			}
			
		});
		sheet.setColumnWidth(0, 3500);
		for(int i=1;i<=max;i++) {
			sheet.setColumnWidth(i, 3000);
		}
		int lastRowNum = sheet.getLastRowNum();
		Row lastRow = sheet.getRow(lastRowNum);
		if (lastRow != null) {			
			for (int i=0; i<=max;i++) {
				Cell lastRowCell = lastRow.getCell(i) != null ? lastRow.getCell(i) : lastRow.createCell(i);
				lastRowCell.setCellStyle(stylesMap.get("bottomBorderCellStyle"));
			}
		}
		
		if (lastRow != null) {
			Cell lastRowCell = lastRow.getCell(0) != null ? lastRow.getCell(0) : lastRow.createCell(0);
			lastRowCell.setCellStyle(stylesMap.get("bottomLeftBorderCellStyle"));
			lastRowCell = lastRow.getCell(3) != null ? lastRow.getCell(3) : lastRow.createCell(3);
			lastRowCell.setCellStyle(stylesMap.get("bottomLeftBorderCellStyle"));
			lastRowCell = lastRow.getCell(max) != null ? lastRow.getCell(max) : lastRow.createCell(max);
			lastRowCell.setCellStyle(stylesMap.get("bottomRightBorderCellStyle"));
		}
		if (sheet.getRow(firstDataRow) != null) {
			Cell firstConceptCell = sheet.getRow(firstDataRow).getCell(0) != null ? sheet.getRow(firstDataRow).getCell(0) : sheet.getRow(firstDataRow).createCell(0);
			firstConceptCell.setCellStyle(stylesMap.get("topLeftBorderCellStyleNoBottom"));
		}
	}
	
	private static void totalsCellCreator(Map<String, CellStyle> stylesMap,
			LinkedHashMap<String, LinkedHashMap<String, String>> totalsFormulas, Sheet totalSheet, Row row,
			String[] months, String field) {
		totalsCellCreator(stylesMap,totalsFormulas, totalSheet, row,months, field, false);
	}
	private static void totalsCellCreator(Map<String, CellStyle> stylesMap,
			LinkedHashMap<String, LinkedHashMap<String, String>> totalsFormulas, Sheet totalSheet, Row row,
			String[] months, String field, boolean important) {
		Cell cell;
		LinkedHashMap<String, String> rawFormulas = totalsFormulas.get(field);
		totalSheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));
		cell = row.createCell(0);
		cell.setCellType(CellType.STRING);
		if (field != null && (AonStringUtils.contains(field, "CRA_00") || AonArrayUtils.constainsIgnoreCase(CRA1_CONCEPT_ORDER, field))) {
			cell.setCellValue(getDefinitivePaymentConcept(field));
		} else {
			cell.setCellValue(removeUnderscore(field));				
		}
		int[] cNum = {3};
		if (rawFormulas != null) {
			for (String month : months) {
				String formula = rawFormulas.get(month);
				Cell formulaCell = row.createCell(cNum[0]++);
				formulaCell.setCellType(CellType.FORMULA);
				formulaCell.setCellStyle(stylesMap.get(important ? "boldNumberCellStyle" : "numberCellStyle"));
				formulaCell.setCellFormula(formula);
			}
			cell = row.createCell(cNum[0]);
			int realRowNum = cell.getRowIndex()+1;
			cell.setCellFormula("SUM(D"+realRowNum+":"+CellReference.convertNumToColString(cell.getColumnIndex()-1)+realRowNum+")");
		}
	}
	
	public static Map<String, AggregatedAnnualYearlyEntry> getEntries (AONContext aonContext, Condition condition, SummaryType type) {
		
		LinkedHashMap<Integer, String> idsAndWorkplaces = new LinkedHashMap<>();
		 
		//EACH SALARY'S WORKPLACE
		aonContext.getDslContext()
				.select(SALARY.ID, WORKPLACE.DESCRIPTION)
				.from(SALARY)
				.innerJoin(CONTRACT).onKey()
				.innerJoin(WORKPLACE).onKey()
				.innerJoin(ENTERPRISE).onKey()
				.where(condition)
				.fetchStream().forEach(r -> idsAndWorkplaces.put(r.get(SALARY.ID), r.get(WORKPLACE.DESCRIPTION)));
		
		Collection<Integer> ids = idsAndWorkplaces.keySet();
		
		Stream<Salary> salaries = AON.getSalaries(aonContext,
				s -> s.getIdProperty().in(ids.toArray(new Integer[ids.size()])))
				.filter(s -> s.getSalaryType() != null && s.getSalaryType().ordinal() <= SalaryType.DELAY.ordinal());
		LinkedHashMap<String, AggregatedAnnualYearlyEntry> entries = new LinkedHashMap<>();
		
		DateFormat df = new SimpleDateFormat("MMMMMMMMMM", new Locale("es", "ES"));
		salaries
		.filter(Objects::nonNull)
		.sorted(Comparator.comparing(s -> s.getEmployeeDocument() != null ? s.getEmployeeDocument() : ""))
		.forEach(s -> {
			String month = null;
			if (type == AggregatedAnnualSummary.SummaryType.MONTHLY)
				month = s.getIssueDate() != null ? df.format(s.getIssueDate()).toUpperCase() : null;
			else if (type == AggregatedAnnualSummary.SummaryType.QUARTERLY)
				month = getQuarter(s.getIssueDate());
			
			String identifier = getIdentifier(aonContext, s);
			
			AggregatedAnnualYearlyEntry yearlyEntry = entries.get(identifier) != null ? entries.get(identifier) : new AggregatedAnnualYearlyEntry(); 
			
			Map<String, AggregatedAnnualEntry> monthlyEntries = yearlyEntry.getMonthlyEntries();
			
			AggregatedAnnualEntry entry = monthlyEntries.get(month) != null ? monthlyEntries.get(month) : new AggregatedAnnualEntry();
			
			fillEntry(s, entry);
			
			monthlyEntries.put(month, entry);
			yearlyEntry.setMonthlyEntries(monthlyEntries);
			if (yearlyEntry.getEmployeeName() == null)
				yearlyEntry.setEmployeeName(s.getEmployeeName());
			if (yearlyEntry.getNif() == null)
				yearlyEntry.setNif(s.getEmployeeDocument());
			
			yearlyEntry.setWorkplace(idsAndWorkplaces.get(s.getId()));
			
			entries.put(identifier, yearlyEntry);
		});
		return entries;
	}
	
	private static String getIdentifier(AONContext aonContext, Salary s) {
		if (s.getEmployeeDocument() == null || s.getEmployeeDocument().isEmpty()) {
			return getAlternativeIdentifier(aonContext, s);
		} else {
			return s.getEmployeeDocument();
		}
	}
	
	private static String getAlternativeIdentifier(AONContext aonContext, Salary s) {
		Integer contractId = aonContext.getDslContext()
				.select(SALARY.CONTRACT)
				.from(SALARY)
				.where(SALARY.ID.eq(s.getId()))
				.fetchOneInto(SALARY)
				.getContract();
			Record reg = aonContext.getDslContext()
				.select(PERSON.SOCIAL_SECURITY_NUM, REGISTRY.DOCUMENT, REGISTRY.ID)
				.from(CONTRACT)
				.innerJoin(PERSON)
				.on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
				.innerJoin(REGISTRY).on(PERSON.REGISTRY.eq(REGISTRY.ID))
				.where(CONTRACT.ID.eq(contractId))
				.fetchStream()
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
			
			if (reg == null)//THEORETICALLY CANNOT BE NULL
				return null;
			
			if (reg.get(REGISTRY.DOCUMENT) != null && !reg.get(REGISTRY.DOCUMENT).isEmpty()) {
				s.setEmployeeDocument(reg.get(REGISTRY.DOCUMENT));
				return reg.get(REGISTRY.DOCUMENT);
			} else if (reg.get(PERSON.SOCIAL_SECURITY_NUM) != null && !reg.get(PERSON.SOCIAL_SECURITY_NUM).isEmpty()){
				s.setEmployeeSSNumber(reg.get(PERSON.SOCIAL_SECURITY_NUM));
				return reg.get(PERSON.SOCIAL_SECURITY_NUM);					
			} else {
				return AonNumberUtils.toString(reg.get(REGISTRY.ID));
			}
	}
	
	private static String getQuarter(Date date) {
		try {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		switch (cal.get(Calendar.MONTH)) {
		case Calendar.JANUARY:
		case Calendar.FEBRUARY:
		case Calendar.MARCH:
			return QUARTERS[0];
		case Calendar.APRIL:
		case Calendar.MAY:
		case Calendar.JUNE:
			return QUARTERS[1];
		case Calendar.JULY:
		case Calendar.AUGUST:
		case Calendar.SEPTEMBER:
			return QUARTERS[2];
		case Calendar.OCTOBER:
		case Calendar.NOVEMBER:
		case Calendar.DECEMBER:
			return QUARTERS[3];	

		default:
			return null;
		}
		
		} catch (Exception e) {
			return null;
		}
	}
	

	private static void fillEntry(Salary s, AggregatedAnnualEntry entry) {
		if (s.getProfessionalContingenciesBase() != null)
			entry.setAccBase(AonNumberUtils.zeroIfNull(entry.getAccBase()) + s.getProfessionalContingenciesBase());
		if (s.getBonuses() != null && !s.getBonuses().isEmpty()) {
			Double bonuses = s.getBonuses().stream().mapToDouble(Bonus::getAmount).sum();
			if (bonuses != null && bonuses != 0d)
				entry.setBonuses(AonNumberUtils.zeroIfNull(entry.getBonuses()) + bonuses);
		}
		if (s.getCommonContingenciesBase() != null)
			entry.setCcBase(AonNumberUtils.zeroIfNull(entry.getCcBase()) + s.getCommonContingenciesBase());
		if (s.getTotalEnterprise() != null || s.getTotalPayment() != null) {
			Double totalEnterprise = AonNumberUtils.zeroIfNull(entry.getEnterpriseCost());
			totalEnterprise += AonNumberUtils.zeroIfNull(s.getTotalEnterprise());
			totalEnterprise += AonNumberUtils.zeroIfNull(s.getTotalPayment());
			entry.setEnterpriseCost(totalEnterprise);
		}
		Double enterpriseSS = s.getCosts().stream().mapToDouble(Cost::getAmount).sum();
		if (enterpriseSS != null && enterpriseSS != 0d)
			entry.setEnterpriseSS(safeSum(entry.getEnterpriseSS(), enterpriseSS));
		if (s.getExtraProrationBase() != null)
			entry.setExtraProrration(safeSum(entry.getExtraProrration(), s.getExtraProrationBase()));
		if (s.getIrpfBase() != null)
			entry.setTotalIrpfBase(safeSum(entry.getTotalIrpfBase(), s.getIrpfBase()));
		if (s.getMoneyIrpfBase() != 0d)
			entry.setMoneyIrpfBase(safeSum(entry.getMoneyIrpfBase(), s.getMoneyIrpfBase()));
		if (s.getInkindIrpfBase() != 0d)
			entry.setInKindIrpfBase(safeSum(entry.getInKindIrpfBase(), s.getInkindIrpfBase()));
		
		if (s.getPayments() != null) {
			Collection<Payment> payments = entry.getPayments() != null ? entry.getPayments() : new LinkedList<>(); 
			payments.addAll(s.getPayments());
			entry.setPayments(payments);
		}
		if (s.getDeductions() != null) {
			Collection<Deduction> deductions =entry.getDeductions() != null ? entry.getDeductions() : new LinkedList<>();
			deductions.addAll(s.getDeductions());
			entry.setDeductions(deductions);
		}
		if (s.getTotalLiquid() != null) {
			entry.setTotalLiquid(safeSum(entry.getTotalLiquid(), s.getTotalLiquid()));
		}
		if (s.getTotalDeduction() != null)
			entry.setTotalDeduction(safeSum(entry.getTotalDeduction(), s.getTotalDeduction()));
		if (s.getTotalPayment() != null)
			entry.setTotalRaw(safeSum(entry.getTotalRaw(), s.getTotalPayment()));
		if (s.getContextData() != null) {
			Map<String, Double> data = entry.getDaysAndHours() != null ? entry.getDaysAndHours() : new LinkedHashMap<>();
			s.getContextData().keySet().stream()
			.filter(key -> AonStringUtils.containsIgnoreCase(key, "dias") || AonStringUtils.containsIgnoreCase(key, "horas"))
			.forEach(key -> {
				String expression = s.getContextData().get(key).stream().map(ContextData::getExpression).findFirst().orElse(null);
				if (expression != null) {
					try {
						data.put(key, Double.parseDouble(expression));
					} catch (NumberFormatException e) {}
				}
			});
			
			if (data.containsKey("DIAS_TRABAJADOS")) {
				Double days = data.get("DIAS_TRABAJADOS");
				if (days != null && days > 0) {
					Double dailyCost = entry.getEnterpriseCost() / days;
					data.put("COSTE_DIARIO", dailyCost);
				}
			}
			
			
			entry.setDaysAndHours(data);
		}
		
		if (s.getTotalSSContributions() != null || (enterpriseSS != null && enterpriseSS != 0d)) {
			Double totalSS = safeSum(s.getTotalSSContributions(), enterpriseSS);
			entry.setRlc(safeSum(entry.getRlc(), totalSS));
		}
	}
	
	private static Double safeSum(Double accum, Double newValue) {
		accum = AonNumberUtils.zeroIfNull(accum);
		accum += AonNumberUtils.zeroIfNull(newValue);
		return accum;
	}
	
	private static String removeUnderscore (String str) {
		if (str == null)
			return null;
		else {
			return str.replace("_", " ");
		}
	}
	
	private static String spaDeduction (String dedName) {
		try {
			DeductionType type = DeductionType.valueOf(dedName);
			return type.accept(DEDUCTION_VISITOR);
			
		} catch (IllegalArgumentException e) {			
			return dedName;
		}
	}
	
	private static Map<String, CellStyle> getCellStyles(Workbook wb) {
		String numberFormat = "#,###,##0.#0";
		LinkedHashMap<String, CellStyle> stylesMap = new LinkedHashMap<>();
		
		DataFormat format = wb.createDataFormat();
		
		CellStyle monthCellStyle = wb.createCellStyle();
		monthCellStyle.setBorderTop(BorderStyle.THIN);
		monthCellStyle.setBorderBottom(BorderStyle.THIN);
		monthCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		monthCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		stylesMap.put("monthCellStyle", monthCellStyle);
		
		CellStyle rightBorderCellStyle = wb.createCellStyle();
		rightBorderCellStyle.setBorderRight(BorderStyle.THIN);
		rightBorderCellStyle.setDataFormat(format.getFormat(numberFormat));
		stylesMap.put("rightBorderCellStyle", rightBorderCellStyle);
		
		CellStyle topRightBorderCellStyle = wb.createCellStyle();
		topRightBorderCellStyle.setBorderRight(BorderStyle.THIN);
		topRightBorderCellStyle.setBorderTop(BorderStyle.THIN);
		topRightBorderCellStyle.setBorderBottom(BorderStyle.THIN);
		topRightBorderCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		topRightBorderCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		topRightBorderCellStyle.setDataFormat(format.getFormat(numberFormat));
		stylesMap.put("topRightBorderCellStyle", topRightBorderCellStyle);
		
		CellStyle topLeftBorderCellStyle = wb.createCellStyle();
		topLeftBorderCellStyle.setBorderLeft(BorderStyle.THIN);
		topLeftBorderCellStyle.setBorderTop(BorderStyle.THIN);
		topLeftBorderCellStyle.setBorderBottom(BorderStyle.THIN);
		topLeftBorderCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		topLeftBorderCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		topLeftBorderCellStyle.setDataFormat(format.getFormat(numberFormat));
		stylesMap.put("topLeftBorderCellStyle", topLeftBorderCellStyle);
		
		CellStyle topLeftBorderCellStyleNoBottom = wb.createCellStyle();
		topLeftBorderCellStyleNoBottom.setBorderLeft(BorderStyle.THIN);
		topLeftBorderCellStyleNoBottom.setBorderTop(BorderStyle.THIN);
		topLeftBorderCellStyleNoBottom.setDataFormat(format.getFormat(numberFormat));
		stylesMap.put("topLeftBorderCellStyleNoBottom", topLeftBorderCellStyleNoBottom);
		
		CellStyle leftBorderCellStyle = wb.createCellStyle();
		leftBorderCellStyle.setBorderLeft(BorderStyle.THIN);
		leftBorderCellStyle.setDataFormat(format.getFormat(numberFormat));
		stylesMap.put("leftBorderCellStyle", leftBorderCellStyle);
		
		CellStyle bottomBorderCellStyle = wb.createCellStyle();
		bottomBorderCellStyle.setBorderBottom(BorderStyle.THIN);
		bottomBorderCellStyle.setDataFormat(format.getFormat(numberFormat));
		stylesMap.put("bottomBorderCellStyle", bottomBorderCellStyle);
		
		CellStyle bottomLeftBorderCellStyle = wb.createCellStyle();
		bottomLeftBorderCellStyle.setBorderBottom(BorderStyle.THIN);
		bottomLeftBorderCellStyle.setBorderLeft(BorderStyle.THIN);
		bottomLeftBorderCellStyle.setDataFormat(format.getFormat(numberFormat));
		stylesMap.put("bottomLeftBorderCellStyle", bottomLeftBorderCellStyle);
		
		CellStyle bottomRightBorderCellStyle = wb.createCellStyle();
		bottomRightBorderCellStyle.setBorderBottom(BorderStyle.THIN);
		bottomRightBorderCellStyle.setBorderRight(BorderStyle.THIN);
		bottomRightBorderCellStyle.setDataFormat(format.getFormat(numberFormat));
		stylesMap.put("bottomRightBorderCellStyle", bottomRightBorderCellStyle);
		
		CellStyle numberCellStyle = wb.createCellStyle();
		numberCellStyle.setDataFormat(format.getFormat(numberFormat));
		stylesMap.put("numberCellStyle", numberCellStyle);
		
		Font boldFont = wb.createFont();
		boldFont.setBold(true);
		
		CellStyle leftBorderCellStyleBold = wb.createCellStyle();
		leftBorderCellStyleBold.setBorderLeft(BorderStyle.THIN);
		leftBorderCellStyleBold.setDataFormat(format.getFormat(numberFormat));
		leftBorderCellStyleBold.setFont(boldFont);
		stylesMap.put("leftBorderCellStyleBold", leftBorderCellStyleBold);
		
		CellStyle rightBorderCellStyleBold = wb.createCellStyle();
		rightBorderCellStyleBold.setBorderRight(BorderStyle.THIN);
		rightBorderCellStyleBold.setDataFormat(format.getFormat(numberFormat));
		rightBorderCellStyleBold.setFont(boldFont);
		stylesMap.put("rightBorderCellStyleBold", rightBorderCellStyleBold);
		
		CellStyle boldNumberCellStyle = wb.createCellStyle();
		boldNumberCellStyle.setDataFormat(format.getFormat(numberFormat));
		boldNumberCellStyle.setFont(boldFont);
		stylesMap.put("boldNumberCellStyle", boldNumberCellStyle);
		
		CellStyle importantCellStyle = wb.createCellStyle();
		importantCellStyle.setBorderLeft(BorderStyle.THIN);
		importantCellStyle.setDataFormat(format.getFormat(numberFormat));
		importantCellStyle.setFont(boldFont);
		stylesMap.put("importantCellStyle", importantCellStyle);
		
		CellStyle headerInfoCellStyle = wb.createCellStyle();
		headerInfoCellStyle.setFont(boldFont);
		stylesMap.put("headerInfoCellStyle", headerInfoCellStyle);
		
		return stylesMap;
	}
	
	private static Row getHeaderRow (Sheet sheet) {
		for (Row row : sheet) {
			if (row.getLastCellNum() > 0) {
				Cell cell0 = row.getCell(0);
				if (cell0.getCellTypeEnum() == CellType.STRING && AonStringUtils.equalsIgnoreCase("CONCEPTO", cell0.getStringCellValue())) {
					return  row;
				}
			}
		}
		return null;
	}
}
