package com.esferalia.aon.in.payroll.excel;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
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
import java.util.Optional;
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

import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
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
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AggregatedAnnualSummary {
	
	private static final String TOTAL_NAME = "TOTALES";
	private static final String[] months = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
	private static final Visitor<String> DEDUCTION_VISITOR = new DeductionType.Visitor<String>() {
		public String visitCommonContigency(DeductionType deductionType) {
			return "CONTNGENCIAS COMUNES";
		}

		public String visitProfessionalContigency(DeductionType deductionType) {
			return "CONTINGENCIAS PROFESIONALES";
		}

		public String visitUnemployent(DeductionType deductionType) {
			return "DESEMPLEO";
		}

		public String visitJobTraining(DeductionType deductionType) {
			return "FORMACIÓN PROFESIONAL";
		}

		public String visitStructuralOvertime(DeductionType deductionType) {
			return "HORAS ESTRUCTURALES";
		}

		public String visitNonStructuralOvertime(DeductionType deductionType) {
			return "HORAS NO ESTRUCTURALES";
		}

		public String visitIrpf(DeductionType deductionType) {
			return "IRPF";
		}

		public String visitAdvancePayment(DeductionType deductionType) {
			return "ADELANTOS";
		}

		public String visitInkind(DeductionType deductionType) {
			return "EN ESPECIE";
		}

		public String visitOther(DeductionType deductionType) {
			return "OTRAS DEDUCCIONES";
		}

		public String visitFogasa(DeductionType deductionType) {
			return "FOGASA";
		}

		public String visitIT(DeductionType deductionType) {
			return "INCAPACIDAD TEMPORAL";
		}
		public String visitIMS(DeductionType deductionType) {
			return "IMS";
		}
	};
	
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
	
	public static void writeExcel (OutputStream oos, String domainName, Optional<Integer> enterpriseId, Optional<Integer> workplaceId, Integer year, boolean complete) {
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
		try (AONContext aonContext = AONContext.getAONContext(domainName, "");) {
			Condition condition = SALARY.ISSUE_DATE.ge(new java.sql.Date(startDate.getTime()))
					.and(SALARY.ISSUE_DATE.le(new java.sql.Date(endDate.getTime())));
			if (enterpriseId.isPresent() && enterpriseId.get() > 0)
				condition = condition.and(ENTERPRISE.REGISTRY.eq(enterpriseId.get()));
			if (workplaceId.isPresent() && workplaceId.get() > 0)
				condition = condition.and(WORKPLACE.ID.eq(workplaceId.get()));
			
			Map<String, AggregatedAnnualYearlyEntry> entries = getEntries(aonContext, condition);
			
			Enterprise enterprise = null;
			if (!enterpriseId.isEmpty() && enterpriseId.get() > 0) {
				enterprise = getEnterpriseById(aonContext, enterpriseId.get());
			} else if (!workplaceId.isEmpty() && workplaceId.get() > 0) {
				enterprise = getEnterpriseByWorkplaceId(aonContext, workplaceId.get());
			} else {
				enterprise = pickEnterpriseFromDomain(aonContext);
			}
			
			
			getExcel(oos, year, entries, enterprise.getName(), enterprise.getDocument(), complete);
		}
		
	}

	protected static void getExcel(OutputStream oos, Integer year,
			Map<String, AggregatedAnnualYearlyEntry> entries, String enterpriseName, String enterpriseDocument, boolean complete) {
		try (Workbook wb = new XSSFWorkbook()) {
			
			wb.createSheet(TOTAL_NAME);
			((XSSFSheet)wb.getSheet(TOTAL_NAME)).setTabColor(new XSSFColor(Color.GRAY));
			
			int firstDataRow = 0;
			
			DataFormat format = wb.createDataFormat();
			
			//STYLES
			CellStyle monthCellStyle = wb.createCellStyle();
			monthCellStyle.setBorderTop(BorderStyle.THIN);
			monthCellStyle.setBorderBottom(BorderStyle.THIN);
			monthCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			monthCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			
			CellStyle rightBorderCellStyle = wb.createCellStyle();
			rightBorderCellStyle.setBorderRight(BorderStyle.THIN);
			rightBorderCellStyle.setDataFormat(format.getFormat("#,###,##0.#0"));
			
			CellStyle topRightBorderCellStyle = wb.createCellStyle();
			topRightBorderCellStyle.setBorderRight(BorderStyle.THIN);
			topRightBorderCellStyle.setBorderTop(BorderStyle.THIN);
			topRightBorderCellStyle.setBorderBottom(BorderStyle.THIN);
			topRightBorderCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			topRightBorderCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			topRightBorderCellStyle.setDataFormat(format.getFormat("#,###,##0.#0"));

			CellStyle topLeftBorderCellStyle = wb.createCellStyle();
			topLeftBorderCellStyle.setBorderLeft(BorderStyle.THIN);
			topLeftBorderCellStyle.setBorderTop(BorderStyle.THIN);
			topLeftBorderCellStyle.setBorderBottom(BorderStyle.THIN);
			topLeftBorderCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			topLeftBorderCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			topLeftBorderCellStyle.setDataFormat(format.getFormat("#,###,##0.#0"));
			
			CellStyle topLeftBorderCellStyleNoBottom = wb.createCellStyle();
			topLeftBorderCellStyleNoBottom.setBorderLeft(BorderStyle.THIN);
			topLeftBorderCellStyleNoBottom.setBorderTop(BorderStyle.THIN);
			topLeftBorderCellStyleNoBottom.setDataFormat(format.getFormat("#,###,##0.#0"));
			
			CellStyle leftBorderCellStyle = wb.createCellStyle();
			leftBorderCellStyle.setBorderLeft(BorderStyle.THIN);
			leftBorderCellStyle.setDataFormat(format.getFormat("#,###,##0.#0"));
			
			CellStyle bottomBorderCellStyle = wb.createCellStyle();
			bottomBorderCellStyle.setBorderBottom(BorderStyle.THIN);
			bottomBorderCellStyle.setDataFormat(format.getFormat("#,###,##0.#0"));

			CellStyle bottomLeftBorderCellStyle = wb.createCellStyle();
			bottomLeftBorderCellStyle.setBorderBottom(BorderStyle.THIN);
			bottomLeftBorderCellStyle.setBorderLeft(BorderStyle.THIN);
			bottomLeftBorderCellStyle.setDataFormat(format.getFormat("#,###,##0.#0"));

			CellStyle bottomRightBorderCellStyle = wb.createCellStyle();
			bottomRightBorderCellStyle.setBorderBottom(BorderStyle.THIN);
			bottomRightBorderCellStyle.setBorderRight(BorderStyle.THIN);
			bottomRightBorderCellStyle.setDataFormat(format.getFormat("#,###,##0.#0"));
			
			CellStyle numberCellStyle = wb.createCellStyle();
			numberCellStyle.setDataFormat(format.getFormat("#,###,##0.#0"));
			
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> totalsFormulas = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>>();
			LinkedHashMap<String, LinkedHashMap<String, String>> totalSheetFormulas = new LinkedHashMap<String, LinkedHashMap<String, String>>();
			
			totalsFormulas.put(TOTAL_NAME, totalSheetFormulas);
			
			
			LinkedHashMap<String, LinkedHashSet<String>> orderedConcepts = new LinkedHashMap<String, LinkedHashSet<String>>();
			{
				orderedConcepts.put("payments", new LinkedHashSet<String>());
				orderedConcepts.put("deductions", new LinkedHashSet<String>());
				orderedConcepts.put("daysAndHours", new LinkedHashSet<String>());
			}

			Collection<String> workplaces = null;
			if (complete) {
				try {
					workplaces = entries.values().stream().map(ent -> ent.getWorkplace()).filter(w -> w != null).distinct().collect(Collectors.toList());
				} catch (NullPointerException e) {
					workplaces = null;
				}
				
				if (workplaces != null) {
					workplaces.forEach(w -> {
						totalsFormulas.put(w, new LinkedHashMap<String, LinkedHashMap<String, String>>());
						Sheet sh = wb.createSheet(WorkbookUtil.createSafeSheetName(w));
						((XSSFSheet)sh).setTabColor(new XSSFColor(Color.LIGHT_GRAY));
					});
				}
			}
				
			for (String nif : entries.keySet()) {
				AggregatedAnnualYearlyEntry entry = entries.get(nif);
				Sheet sheet = wb.createSheet(WorkbookUtil.createSafeSheetName(nif));
				
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
				
				row = sheet.createRow(2);
				cell = row.createCell(0);
				cell.setCellValue("NIF Empresa:");
				cell = row.createCell(1);
				cell.setCellValue(entry.getNif() != null ? entry.getNif() : "");
				
				row = sheet.getRow(2);
				cell = row.createCell(3);
				cell.setCellValue("Empresa:");
				cell = row.createCell(4);
				cell.setCellValue(enterpriseName != null ? enterpriseName : "");
				
				row = sheet.createRow(3);
				cell = row.createCell(0);
				cell.setCellValue("Centro de Trabajo:");
				cell = row.createCell(1);
				cell.setCellValue(entry.getWorkplace() != null ? entry.getWorkplace() : "");
				
				
				row = sheet.createRow(5);
				cell = row.createCell(0);
				cell.setCellValue("NIF Empleado:");
				cell = row.createCell(1);
				cell.setCellValue(entry.getNif() != null ? entry.getNif() : "");
				
				row = sheet.getRow(5);
				cell = row.createCell(3);
				cell.setCellValue("Empleado:");
				cell = row.createCell(4);
				cell.setCellValue(entry.getEmployeeName() != null ? entry.getEmployeeName() : "");
				
				row = sheet.createRow(6);
				//ORGANIZING INFO
				LinkedHashSet<String> paymentConceptsSet = new LinkedHashSet<String>();
				LinkedHashSet<String> deductionConceptsSet = new LinkedHashSet<String>();
				Pattern noWords = Pattern.compile("[A-Za-z]+");
				entry.getMonthlyEntries().values().stream().map(e -> e.getPayments()).forEach(payments -> {
					payments.stream()
					.filter(p -> p != null)
					.sorted(Comparator.comparing(p -> p.getPaymentType() != null ? p.getPaymentType() : PaymentType.values()[PaymentType.values().length -1]))
					.map(p -> {
						if (p.getDescription() != null) {
							Matcher matcher = noWords.matcher(p.getDescription());
							if (matcher.find())
								return p.getDescription();
							else
								return p.getName();
						} else
							return p.getName();
					})
					.forEach(name -> paymentConceptsSet.add(name));
				});
				
				entry.getMonthlyEntries().values().stream().map(e -> e.getDeductions()).forEach(deductions-> {
					if (deductions != null) {
						deductions.stream()
						.filter(d -> d != null)
						.sorted(Comparator.comparing(d -> d.getDeductionType() != null ? d.getDeductionType() : DeductionType.values()[DeductionType.values().length-1], Comparator.naturalOrder()))
						.forEach(deduction -> {
							String name = deduction.getDescription() != null ? deduction.getDescription() : deduction.getDeductionType().name();
							deductionConceptsSet.add(name);
						});
					}
				});
				
				LinkedList<String> paymentConcepts = new LinkedList<String>();
				paymentConceptsSet.forEach(c -> paymentConcepts.add(c));
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
				
				
				row = sheet.createRow(sheet.getLastRowNum()+1);
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));
				cell = row.createCell(0);
				cell.setCellType(CellType.STRING);
				cell.setCellValue("CONCEPTO");
				int[] cellNum = {3};
				Arrays.stream(months).forEach(month -> {
					Cell monthCell = sheet.getRow(sheet.getLastRowNum()).createCell(cellNum[0]++);
					monthCell.setCellType(CellType.STRING);
					monthCell.setCellValue(month);
					monthCell.setCellStyle(monthCellStyle);
				});
				
				Cell monthCell = sheet.getRow(sheet.getLastRowNum()).createCell(cellNum[0]);
				monthCell.setCellType(CellType.STRING);
				monthCell.setCellValue("TOTAL");
				monthCell.setCellStyle(topRightBorderCellStyle);
				
				row.getCell(3).setCellStyle(topLeftBorderCellStyle);
				firstDataRow = row.getRowNum() + 1;
				//PAYMENTS
				LinkedHashSet<String> conceptSet = orderedConcepts.get("payments");
				paymentConceptsSet.forEach(concept -> {
					Row paymentRow = sheet.createRow(sheet.getLastRowNum()+1);
					sheet.addMergedRegion(new CellRangeAddress(paymentRow.getRowNum(), paymentRow.getRowNum(), 0, 2));
					Cell paymentCell = paymentRow.createCell(0);
					paymentCell.setCellType(CellType.STRING);
					if (concept != null)
						paymentCell.setCellValue(removeUnderscore(concept));
					int[] amountCellNum = {3};
					conceptSet.add(concept);
					
					Arrays.stream(months).forEach(month -> {
						Cell amountCell = paymentRow.createCell(amountCellNum[0]++);
						
						String formula = "'" + nif + "'" + "!" + CellReference.convertNumToColString(amountCell.getColumnIndex()) + (amountCell.getRowIndex()+1);
						
						putPaymentAndDaHFormula(entry.getWorkplace(),totalsFormulas, concept, month, formula, complete);
						
						
						
						if (entry.getMonthlyEntries().get(month) != null) {
							amountCell.setCellType(CellType.NUMERIC);
							Collection<Payment> paym = entry.getMonthlyEntries().get(month).getPayments();
							double amount = paym.stream().filter(p -> {
								if (p != null) {
									String desc = p.getDescription() != null ? p.getDescription() : p.getName();
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
							amountCell.setCellStyle(numberCellStyle);
						}
					});
					Cell totalCell = paymentRow.createCell(amountCellNum[0]);
					totalCell.setCellType(CellType.FORMULA);
					int realRowNum = totalCell.getRowIndex()+1;
					totalCell.setCellFormula("SUM(D"+realRowNum+":"+CellReference.convertNumToColString(totalCell.getColumnIndex()-1)+realRowNum+")");
					
				});
				
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//TOTAL RAW
				putDataRow("Total Bruto", complete, numberCellStyle, totalsFormulas, nif, entry, sheet, row);
				
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//DEDUCTIONS
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
						Arrays.stream(months).forEach(month -> {
							Cell amountCell = deductionRow.createCell(amountCellNum[0]++);
							
							String formula = "'" + nif + "'" + "!" + CellReference.convertNumToColString(amountCell.getColumnIndex()) + (amountCell.getRowIndex()+1);
							
							putDeductionFormula(entry.getWorkplace(), totalsFormulas, concept, month, formula, complete);
							
							if (entry.getMonthlyEntries().get(month) != null) {
								amountCell.setCellType(CellType.NUMERIC);
								
								double amount = entry.getMonthlyEntries().get(month).getDeductions().stream().filter(d -> {
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
								amountCell.setCellStyle(numberCellStyle);
							}
						});
						Cell totalCell = deductionRow.createCell(amountCellNum[0]);
						totalCell.setCellType(CellType.FORMULA);
						int realRowNum = totalCell.getRowIndex()+1;
						totalCell.setCellFormula("SUM(D"+realRowNum+":"+CellReference.convertNumToColString(totalCell.getColumnIndex()-1)+realRowNum+")");
				});
				
				row = sheet.createRow(sheet.getLastRowNum() +1);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));
				
				cell = row.createCell(0);
				cell.setCellType(CellType.STRING);
				cell.setCellValue("Total Deducciones");
				
				int[] totalDeductionCellNum = {3};
				Arrays.stream(months).forEach(month -> {
					AggregatedAnnualEntry ent = entry.getMonthlyEntries().get(month);
					Row totalDedRow = sheet.getRow(sheet.getLastRowNum());
					Cell totalDedCell = totalDedRow.createCell(totalDeductionCellNum[0]++);
					
					String formula = "'" + nif + "'" + "!" + CellReference.convertNumToColString(totalDedCell.getColumnIndex()) + (totalDedCell.getRowIndex()+1);
					
					if (totalsFormulas.get(TOTAL_NAME).containsKey("Total Deducciones")) {
						LinkedHashMap<String, String> monthly = totalsFormulas.get(TOTAL_NAME).get("Total Deducciones");
						if (monthly.containsKey(month)) {
							String form = monthly.get(month);
							monthly.put(month, form + "+" + formula);
						} else {
							monthly.put(month, formula);
						}
					} else {
						LinkedHashMap<String, String> monthly = new LinkedHashMap<String, String>();
						monthly.put(month, formula);
						totalsFormulas.get(TOTAL_NAME).put("Total Deducciones", monthly);
					}
					
					totalDedCell.setCellType(CellType.NUMERIC);
					if (ent != null && ent.getTotalDeduction() != null)
						totalDedCell.setCellValue(ent.getTotalDeduction());
					totalDedCell.setCellStyle(numberCellStyle);
				});
				
				Cell totalCell = row.createCell(totalDeductionCellNum[0]);
				totalCell.setCellType(CellType.FORMULA);
				int rRowNum = totalCell.getRowIndex()+1;
				totalCell.setCellFormula("SUM(D"+rRowNum+":"+CellReference.convertNumToColString(totalCell.getColumnIndex()-1)+rRowNum+")");
				
				row = sheet.createRow(sheet.getLastRowNum() +1);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//TOTAL LIQUID
				putDataRow("TOTAL LÍQUIDO", complete, numberCellStyle, totalsFormulas, nif, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//EXTRA PRORATION
				putDataRow("PRORRATA PAGAS EXTRAS", complete, numberCellStyle, totalsFormulas, nif, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//BONUSES
				putDataRow("BONIFICACIONES/REDUCCIONES", complete, numberCellStyle, totalsFormulas, nif, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//ENTERPRISE SS
				putDataRow("SEG.SOCIAL EMPRESA", complete, numberCellStyle, totalsFormulas, nif, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//ENTERPRISE COST
				putDataRow("COSTE EMPRESA", complete, numberCellStyle, totalsFormulas, nif, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//CC BASE
				putDataRow("BASE CONTINGENCIAS COMUNES", complete, numberCellStyle, totalsFormulas, nif, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//IT BASE
				putDataRow("BASE ACCIDENTES", complete, numberCellStyle, totalsFormulas, nif, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//IRPF MONEY BASE
				putDataRow("BASE IRPF DINERARIA", complete, numberCellStyle, totalsFormulas, nif, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//IRPF IN-KIND BASE
				putDataRow("BASE IRPF EN ESPECIE", complete, numberCellStyle, totalsFormulas, nif, entry, sheet, row);
				row = sheet.createRow(sheet.getLastRowNum() +1);
				//IRPF TOTAL BASE
				putDataRow("BASE IRPF TOTAL", complete, numberCellStyle, totalsFormulas, nif, entry, sheet, row);
				//DAYS AND HOURS
				{
					LinkedHashSet<String> dahNames = new LinkedHashSet<String>();
					entry.getMonthlyEntries().values().stream().map(ent -> ent.getDaysAndHours()).forEach(dah -> {
						if (dah != null)
							dah.keySet().forEach(key -> dahNames.add(key));
					});
					if (!dahNames.isEmpty()) {
						LinkedHashSet<String> dahSet = orderedConcepts.get("daysAndHours");
						dahNames.forEach(dahName -> {
							Row dahRow = sheet.createRow(sheet.getLastRowNum() +1);
							sheet.addMergedRegion(new CellRangeAddress(dahRow.getRowNum(), dahRow.getRowNum(), 0, 2));
							Cell dahCell = dahRow.createCell(0);
							dahCell.setCellType(CellType.STRING);
							dahCell.setCellValue(removeUnderscore(dahName));
							dahSet.add(dahName);
							
							int[] cNum = {3};	
							Arrays.stream(months).forEach(month -> {
								Map<String, Double> dah = entry.getMonthlyEntries().get(month) != null ? entry.getMonthlyEntries().get(month).getDaysAndHours() : null;
								Cell dCell = dahRow.createCell(cNum[0]);
								
								String formula = "'" + nif + "'" + "!" + CellReference.convertNumToColString(dCell.getColumnIndex()) + (dCell.getRowIndex()+1);
								
								putPaymentAndDaHFormula(entry.getWorkplace(), totalsFormulas, dahName, month, formula, complete);
								
								dCell.setCellType(CellType.NUMERIC);
								dCell.setCellStyle(numberCellStyle);
								if (dah != null && dah.get(dahName) != null) {
									dCell.setCellValue(dah.get(dahName));
								}
								cNum[0]++;
							});
							dahCell = dahRow.createCell(cNum[0]);
							int realRowNum = dahCell.getRowIndex()+1;
							dahCell.setCellFormula("SUM(D"+realRowNum+":"+CellReference.convertNumToColString(dahCell.getColumnIndex()-1)+realRowNum+")");
						});
					}
					
				}
				
//				sheet.createFreezePane(0, 6);
				sheet.createFreezePane(3, firstDataRow-1, 3, firstDataRow-1);
				
				
				
			}
			
			//RESIZE AND SOME STYLES
			for (Sheet sheet : wb) {
				if (!totalsFormulas.keySet().contains(sheet.getSheetName()))
					resizeSheet(firstDataRow, rightBorderCellStyle, topLeftBorderCellStyleNoBottom, leftBorderCellStyle,
						bottomBorderCellStyle, bottomLeftBorderCellStyle, bottomRightBorderCellStyle, sheet);
			}
			
			//TOTALS
			for (String workplace : totalsFormulas.keySet()) {
				
				Sheet totalSheet = wb.getSheet(WorkbookUtil.createSafeSheetName(workplace));
				
//				totalsFormulas
				totalSheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
				totalSheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 2));
				totalSheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 9));
				
				
				Row row = totalSheet.createRow(1);
				
				Cell cell = row.createCell(0);
				
				cell.setCellType(CellType.STRING);
				cell.setCellValue("PERÍODO ANUAL DE 01/"+year+" A 12/"+year);
				
				row = totalSheet.createRow(2);
				cell = row.createCell(0);
				cell.setCellValue("NIF Empresa:");
				cell = row.createCell(1);
				cell.setCellValue(enterpriseDocument != null ? enterpriseDocument : "");

				row = totalSheet.getRow(2);
				cell = row.createCell(3);
				cell.setCellValue("Empresa:");
				cell = row.createCell(4);
				cell.setCellValue(enterpriseName != null ? enterpriseName : "");
				
				row = totalSheet.createRow(3);
				
				if (!workplace.equals(TOTAL_NAME)) {
					totalSheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 2));
					cell = row.createCell(0);
					cell.setCellValue("Centro de trabajo:");
					cell = row.createCell(1);
					cell.setCellValue(workplace != null ? workplace : "");
					
					row = totalSheet.createRow(4);
				}
				
				//MONTHS HEADER
			
				row = totalSheet.createRow(totalSheet.getLastRowNum()+1);
				totalSheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));
				cell = row.createCell(0);
				cell.setCellType(CellType.STRING);
				cell.setCellValue("CONCEPTO");
				int[] cellNum = {3};
				Arrays.stream(months).forEach(month -> {
					Cell monthCell = totalSheet.getRow(totalSheet.getLastRowNum()).createCell(cellNum[0]++);
					monthCell.setCellType(CellType.STRING);
					monthCell.setCellValue(month);
					monthCell.setCellStyle(monthCellStyle);
				});
				
				Cell monthCell = totalSheet.getRow(totalSheet.getLastRowNum()).createCell(cellNum[0]);
				monthCell.setCellType(CellType.STRING);
				monthCell.setCellValue("TOTAL");
				monthCell.setCellStyle(topRightBorderCellStyle);
				
				row.getCell(3).setCellStyle(topLeftBorderCellStyle);
				
				firstDataRow = row.getRowNum() + 1;
				
				//PAYMENTS
				LinkedHashSet<String> paymentsSet = orderedConcepts.get("payments");
				paymentsSet.forEach(pay -> {
					Row pRow = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
					totalsCellCreator(numberCellStyle, totalsFormulas.get(workplace), totalSheet, pRow, months, pay);
				});
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//RAW
				totalsCellCreator(numberCellStyle, totalsFormulas.get(workplace), totalSheet, row, months, "Total Bruto");
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//DEDUCTIONS
				{
					LinkedHashSet<String> deductionsSet = orderedConcepts.get("deductions");
					deductionsSet.forEach(ded -> {
						Row dRow = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
						totalsCellCreator(numberCellStyle, totalsFormulas.get(workplace), totalSheet, dRow, months, spaDeduction(ded));
					});
				}
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//TOTAL LIQUID
				totalsCellCreator(numberCellStyle, totalsFormulas.get(workplace), totalSheet, row, months, "TOTAL LÍQUIDO");
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//EXTRA PAY PRO.
				totalsCellCreator(numberCellStyle, totalsFormulas.get(workplace), totalSheet, row, months, "PRORRATA PAGAS EXTRAS");
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//BONUSES
				totalsCellCreator(numberCellStyle, totalsFormulas.get(workplace), totalSheet, row, months, "BONIFICACIONES/REDUCCIONES");
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//ENTERPRISE SS
				totalsCellCreator(numberCellStyle, totalsFormulas.get(workplace), totalSheet, row, months, "SEG.SOCIAL EMPRESA");
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//ENTERPRISE COST
				totalsCellCreator(numberCellStyle, totalsFormulas.get(workplace), totalSheet, row, months, "COSTE EMPRESA");
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//CC BASE
				totalsCellCreator(numberCellStyle, totalsFormulas.get(workplace), totalSheet, row, months, "BASE CONTINGENCIAS COMUNES");
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//MONEY IRPF BASE
				totalsCellCreator(numberCellStyle, totalsFormulas.get(workplace), totalSheet, row, months, "BASE IRPF DINERARIA");
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
								
				//IN-KIND IRPF BASE
				totalsCellCreator(numberCellStyle, totalsFormulas.get(workplace), totalSheet, row, months, "BASE IRPF EN ESPECIE");
				
				row = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
				
				//TOTAL IRPF BASE
				totalsCellCreator(numberCellStyle, totalsFormulas.get(workplace), totalSheet, row, months, "BASE IRPF TOTAL");
				
				//DAYS AND HOURS
				{
					LinkedHashSet<String> dahSet = orderedConcepts.get("daysAndHours");
					dahSet.forEach(dah -> {
						Row dRow = totalSheet.createRow(totalSheet.getLastRowNum() + 1);
						totalsCellCreator(numberCellStyle, totalsFormulas.get(workplace), totalSheet, dRow, months, dah);
					});
				}

				
				totalSheet.createFreezePane(3, firstDataRow - 1, 3, firstDataRow -1);
				
				resizeSheet(firstDataRow, rightBorderCellStyle, topLeftBorderCellStyleNoBottom, leftBorderCellStyle,
						bottomBorderCellStyle, bottomLeftBorderCellStyle, bottomRightBorderCellStyle, totalSheet);
				
			}
			
			
			wb.write(oos);
			
			
			
		} catch (IOException e) {}
	}

	private static void putDataRow(String dataName, boolean complete, CellStyle numberCellStyle,
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> totalsFormulas, String nif,
			AggregatedAnnualYearlyEntry entry, Sheet sheet, Row row) {
		Cell cell;
		{
			cell = row.createCell(0);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(dataName);
			int cellInd = 3;
			boolean hasContent = false;
			for (String month : months) {
				AggregatedAnnualEntry ent = entry.getMonthlyEntries().get(month);
				cell = row.createCell(cellInd++);
				
				String formula = "'" + nif + "'" + "!" + CellReference.convertNumToColString(cell.getColumnIndex()) + (cell.getRowIndex()+1);
				
				putDataFormula(entry.getWorkplace(), dataName, totalsFormulas, month, formula, complete);
				
				cell.setCellType(CellType.NUMERIC);

				Double amount = null;
				if (ent != null) {
					switch (dataName) {
					case "Total Bruto":
						amount = ent.getTotalRaw();
						break;
					case "TOTAL LÍQUIDO":
						amount = ent.getTotalLiquid();
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
						break;
					default:
						amount = null;
					}
				}
				
				
				if (ent != null && amount != null) {
					hasContent = true;
					cell.setCellValue(amount);
				}
				cell.setCellStyle(numberCellStyle);
			}
			cell = row.createCell(cellInd);
			cell.setCellType(CellType.FORMULA);
			int realRowNum = cell.getRowIndex()+1;
			cell.setCellFormula("SUM(D"+realRowNum+":"+CellReference.convertNumToColString(cell.getColumnIndex()-1)+realRowNum+")");
			if (!hasContent)
				sheet.removeRow(row);
			else
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));
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
				LinkedHashMap<String, String> monthly = new LinkedHashMap<String, String>();
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
			LinkedHashMap<String, String> monthly = new LinkedHashMap<String, String>();
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
				LinkedHashMap<String, String> monthly = new LinkedHashMap<String, String>();
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
			LinkedHashMap<String, String> monthly = new LinkedHashMap<String, String>();
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
				LinkedHashMap<String, String> monthly = new LinkedHashMap<String, String>();
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
			LinkedHashMap<String, String> monthly = new LinkedHashMap<String, String>();
			monthly.put(month, formula);
			totalsFormulas.get(TOTAL_NAME).put(concept, monthly);
		}
	}

	private static void resizeSheet(int firstDataRow, CellStyle rightBorderCellStyle,
			CellStyle topLeftBorderCellStyleNoBottom, CellStyle leftBorderCellStyle, CellStyle bottomBorderCellStyle,
			CellStyle bottomLeftBorderCellStyle, CellStyle bottomRightBorderCellStyle, Sheet sheet) {
		int maxCells[] = {0};
		int[] firstDataRowArr = {firstDataRow};
		sheet.rowIterator().forEachRemaining(r -> {
			Cell cel = r.getCell(15) != null ? r.getCell(15) : r.createCell(15);
			cel.setCellStyle(r.getRowNum() > firstDataRowArr[0] - 1 ? rightBorderCellStyle : cel.getCellStyle());
			cel = r.getCell(3) != null ? r.getCell(3) : r.createCell(3);
			cel.setCellStyle(r.getRowNum() > firstDataRowArr[0] - 1 ? leftBorderCellStyle : cel.getCellStyle());

			cel = r.getCell(0) != null ? r.getCell(0) : r.createCell(0);
			cel.setCellStyle(r.getRowNum() > firstDataRowArr[0] - 1 ? leftBorderCellStyle : cel.getCellStyle());
			
			int cellCount[] = {0};
			r.forEach(c -> {
				cellCount[0]++;
			});
			maxCells[0] = maxCells[0] < cellCount[0] ? cellCount[0] : maxCells[0];
			});
		sheet.setColumnWidth(0, 3500);
		for(int i=1;i<=maxCells[0];i++) {
			sheet.setColumnWidth(i, 3000);
		}
		int lastRowNum = sheet.getLastRowNum();
		Row lastRow = sheet.getRow(lastRowNum);
		for (int i=0; i<=14;i++) {
			Cell lastRowCell = lastRow.getCell(i) != null ? lastRow.getCell(i) : lastRow.createCell(i);
			lastRowCell.setCellStyle(bottomBorderCellStyle);
		}
		
		{
			Cell lastRowCell = lastRow.getCell(0) != null ? lastRow.getCell(0) : lastRow.createCell(0);
			lastRowCell.setCellStyle(bottomLeftBorderCellStyle);
			lastRowCell = lastRow.getCell(3) != null ? lastRow.getCell(3) : lastRow.createCell(3);
			lastRowCell.setCellStyle(bottomLeftBorderCellStyle);
			lastRowCell = lastRow.getCell(15) != null ? lastRow.getCell(15) : lastRow.createCell(15);
			lastRowCell.setCellStyle(bottomRightBorderCellStyle);
			if (sheet.getRow(firstDataRow) != null) {
				Cell firstConceptCell = sheet.getRow(firstDataRow).getCell(0) != null ? sheet.getRow(firstDataRow).getCell(0) : sheet.getRow(firstDataRow).createCell(0);
				firstConceptCell.setCellStyle(topLeftBorderCellStyleNoBottom);
			}
		}
	}

	private static void totalsCellCreator(CellStyle numberCellStyle,
			LinkedHashMap<String, LinkedHashMap<String, String>> totalsFormulas, Sheet totalSheet, Row row,
			String[] months, String field) {
		Cell cell;
		{
			LinkedHashMap<String, String> rawFormulas = totalsFormulas.get(field);
			totalSheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));
			cell = row.createCell(0);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(removeUnderscore(field));
			int[] cNum = {3};
			if (rawFormulas != null) {
				for (String month : months) {
					String formula = rawFormulas.get(month);
					Cell formulaCell = row.createCell(cNum[0]++);
					formulaCell.setCellType(CellType.FORMULA);
					formulaCell.setCellStyle(numberCellStyle);
					formulaCell.setCellFormula(formula);
				}
				cell = row.createCell(cNum[0]);
				int realRowNum = cell.getRowIndex()+1;
				cell.setCellFormula("SUM(D"+realRowNum+":"+CellReference.convertNumToColString(cell.getColumnIndex()-1)+realRowNum+")");
			}
			
		}
	}
	
	public static Map<String, AggregatedAnnualYearlyEntry> getEntries (AONContext aonContext, Condition condition) {
		LinkedHashMap<Integer, String> idsAndWorkplaces = new LinkedHashMap<Integer, String>();
		 
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
				s -> s.getIdProperty().in(ids.toArray(new Integer[ids.size()])));
		LinkedHashMap<String, AggregatedAnnualYearlyEntry> entries = new LinkedHashMap<String, AggregatedAnnualYearlyEntry>();
		DateFormat df = new SimpleDateFormat("MMMMMMMMMM", new Locale("es", "ES"));
		salaries
		.filter(s -> s != null)
		.sorted(Comparator.comparing(s -> s.getEmployeeDocument() != null ? s.getEmployeeDocument() : ""))
		.forEach(s -> {
			String month = s.getIssueDate() != null ? df.format(s.getIssueDate()).toUpperCase() : null;
			AggregatedAnnualYearlyEntry yearlyEntry = entries.get(s.getEmployeeDocument()) != null ? entries.get(s.getEmployeeDocument()) : new AggregatedAnnualYearlyEntry(); 
			
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
			
			entries.put(s.getEmployeeDocument(), yearlyEntry);
		});
		return entries;
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
		if (s.getInkindIrpfBase() != 0d)
			entry.setInKindIrpfBase(safeSum(entry.getInKindIrpfBase(), s.getInkindIrpfBase()));
		if (s.getIrpfBase() != null)
			entry.setTotalIrpfBase(safeSum(entry.getTotalIrpfBase(), s.getIrpfBase()));
		if (s.getMoneyIrpfBase() != 0d)
			entry.setMoneyIrpfBase(safeSum(entry.getMoneyIrpfBase(), s.getMoneyIrpfBase()));
		if (s.getInkindIrpfBase() != 0d)
			entry.setInKindIrpfBase(safeSum(entry.getInKindIrpfBase(), s.getInkindIrpfBase()));
		
		if (s.getPayments() != null) {
			Collection<Payment> payments = entry.getPayments() != null ? entry.getPayments() : new LinkedList<Payment>(); 
			payments.addAll(s.getPayments());
			entry.setPayments(payments);
		}
		if (s.getDeductions() != null) {
			Collection<Deduction> deductions =entry.getDeductions() != null ? entry.getDeductions() : new LinkedList<Deduction>();
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
			Map<String, Double> data = entry.getDaysAndHours() != null ? entry.getDaysAndHours() : new LinkedHashMap<String, Double>();
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
			entry.setDaysAndHours(data);
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
			return str.replaceAll("_", " ");
		}
	}
	
	private static String spaDeduction (String dedName) {
		
		try {
			DeductionType type = DeductionType.valueOf(dedName);
			return type.accept(DEDUCTION_VISITOR);
			
		} catch (IllegalArgumentException e) {}
		return dedName;
	}
	
}
