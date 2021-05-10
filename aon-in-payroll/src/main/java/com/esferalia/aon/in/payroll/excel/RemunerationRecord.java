package com.esferalia.aon.in.payroll.excel;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.SelectSeekStep1;
import org.jooq.impl.DSL;

import com.code.aon.person.enumeration.Gender;
import com.esferalia.aon.in.payroll.excel.IRetributiveConcept.RetributionForm;
import com.esferalia.aon.in.payroll.excel.IRetributiveConcept.RetributionType;
import com.esferalia.aon.jooq.tables.SalaryData;
import com.esferalia.aon.jooq.tables.SalaryPayment;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.payroll.enumeration.FamilySituation;
import com.esferalia.aon.salary.enumeration.PaymentType;

public class RemunerationRecord {
	
	public static class RemunerationRecordEntry implements IRemunerationRecordEntry{
		
		private String name;
		private String socialSecurityNumber;
		private Gender gender;
		private Date birthDate;
		private String studies;
		private FamilySituation familySituation;
		private Integer children;
		private Date hireDate;
		private Date contractEndDate;
		private Date seniorityDate;
		private Date contractSituationStartDate;
		private Date contractSituationEndDate;
		private Double workdayPercent;
		private Double reducedWorkdayPercent;
		private String workdayReductionReason;
		private String contractKey;
		private String enterpriseArea;
		private String enterpriseDepartment;
		private String category;
		private Schedule schedule;
		private Boolean byTurns;
		private String enterpriseScale;
		private String professionalClass;
		private String scale;
		private String agreement;
		private String professionalCategory;
		private String professionalGroup;
		private String level;
		private Integer quoteGroup;
		
		private Map<String, Double> payments;
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public String getSocialSecurityNumber() {
			return socialSecurityNumber;
		}

		@Override
		public Gender getGender() {
			return gender;
		}

		@Override
		public Date getBirthDate() {
			return birthDate;
		}

		@Override
		public String getStudies() {
			return studies;
		}

		@Override
		public FamilySituation getFamilySituation() {
			return familySituation;
		}

		@Override
		public Integer getChildren() {
			return children;
		}

		@Override
		public Date getHireDate() {
			return hireDate;
		}

		@Override
		public Date getContractEndDate() {
			return contractEndDate;
		}

		@Override
		public Date getSeniorityDate() {
			return seniorityDate;
		}

		@Override
		public Date getContractSituationStartDate() {
			return contractSituationStartDate;
		}

		@Override
		public Date getContractSituationEndDate() {
			return contractSituationEndDate;
		}

		@Override
		public Double getWorkdayPercent() {
			return workdayPercent;
		}

		@Override
		public Double getReducedWorkdayPercent() {
			return reducedWorkdayPercent;
		}

		@Override
		public String getWorkdayReductionReason() {
			return workdayReductionReason;
		}

		@Override
		public String getContractKey() {
			return contractKey;
		}

		@Override
		public String getEnterpriseArea() {
			return enterpriseArea;
		}

		@Override
		public String getEnterpriseDepartment() {
			return enterpriseDepartment;
		}

		@Override
		public String getCategory() {
			return category;
		}

		@Override
		public Schedule getSchedule() {
			return schedule;
		}

		@Override
		public Boolean isByTurns() {
			return byTurns;
		}

		@Override
		public String getEnterpriseScale() {
			return enterpriseScale;
		}

		@Override
		public String getProfessionalClass() {
			return professionalClass;
		}

		@Override
		public String getScale() {
			return scale;
		}

		@Override
		public String getAgreement() {
			return agreement;
		}

		@Override
		public String getProfessionalCategory() {
			return professionalCategory;
		}

		@Override
		public String getProfessionalGroup() {
			return professionalGroup;
		}

		@Override
		public String getLevel() {
			return level;
		}

		@Override
		public Integer getQuoteGroup() {
			return quoteGroup;
		}

		@Override
		public Map<String, Double> getPayments() {
			return payments;
		}
		
	}
	
	public static class RetributiveConcept implements IRetributiveConcept {
		
		PaymentType type;
		String name;
		String description;
		RetributionForm retributionForm;
		RetributionType retributionType;
		Boolean normalizable;
		Boolean anualizable;
		
		@Override
		public PaymentType getType() {
			return type;
		}
		
		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public RetributionForm getRetributionForm() {
			return retributionForm;
		}

		@Override
		public RetributionType getRetributionType() {
			return retributionType;
		}

		@Override
		public Boolean isNormalizable() {
			return normalizable;
		}

		@Override
		public Boolean isAnualizable() {
			return anualizable;
		}
		
	}
	

	
//	private static void copyRow (Row newRow, Row originalRow) {
//		Iterator<Cell> it = originalRow.cellIterator();
//		while (it.hasNext()) {
//			Cell c = it.next();
//			newRow.createCell(c.getColumnIndex());
//		}
//	}
	


	protected static InputStream getExcelDraft(Optional<Integer> entries) {
		InputStream is = null;
		if (entries.isEmpty())
			is = RemunerationRecord.class.getResourceAsStream("Herramienta-Registro-Retributivo.xlsx");
		else {
			if (entries.get() <=200)
				is = RemunerationRecord.class.getResourceAsStream("Herramienta-Registro-Retributivo_200.xlsx");
			else if (entries.get() <= 500)
				is = RemunerationRecord.class.getResourceAsStream("Herramienta-Registro-Retributivo_500.xlsx");
			else if (entries.get() <= 1000)
				is = RemunerationRecord.class.getResourceAsStream("Herramienta-Registro-Retributivo_1000.xlsx");
			else
				is = RemunerationRecord.class.getResourceAsStream("Herramienta-Registro-Retributivo.xlsx");
		}
		
		return is;
	}
	
	public static void getExcel(OutputStream os, RemunerationRecordData remunerationRecordData) {
		Integer entryNum = remunerationRecordData.getEntries() != null ? remunerationRecordData.getEntries().size() : null;
		try (Workbook wb = new XSSFWorkbook(getExcelDraft(Optional.ofNullable(entryNum)))) {
			wb.setForceFormulaRecalculation(true);
			XSSFFormulaEvaluator eva = new XSSFFormulaEvaluator((XSSFWorkbook) wb);
			eva.clearAllCachedResultValues();
			
			//-----------------------STYLES-------------------------
			CellStyle grayDate = wb.createCellStyle();
			grayDate.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("d-m-yy"));
			Font greyFont = wb.createFont();
			greyFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
			grayDate.setFont(greyFont);
			
			CellStyle blackDate = wb.createCellStyle();
			blackDate.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("d-m-yy"));
			blackDate.setFillForegroundColor(IndexedColors.BLACK.getIndex());
			
			CellStyle blackDateBold = wb.createCellStyle();
			blackDateBold.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("d-m-yy"));
			blackDateBold.setFillForegroundColor(IndexedColors.BLACK.getIndex());
			Font boldFont = wb.createFont();
			boldFont.setBold(true);
			blackDateBold.setFont(boldFont);
			
			CellStyle grayCellStyle = wb.createCellStyle();
			grayCellStyle.setFont(greyFont);
			
			CellStyle percentageStyle = wb.createCellStyle();
			percentageStyle.setDataFormat(wb.createDataFormat().getFormat(BuiltinFormats.getBuiltinFormat(10)));
			
			CellStyle amountCellStyle = wb.createCellStyle();
			amountCellStyle.setDataFormat(wb.createDataFormat().getFormat(BuiltinFormats.getBuiltinFormat(4)));
			//------------------------------------------------------
			
			
			Sheet sheet;
			Row row;
			Cell cell;
			
			String[] conceptOrder;
			
			//"Inicio" SHEET
			
			//LOGO
			{
				sheet = wb.getSheetAt(0);
				if (!remunerationRecordData.getLogo().isEmpty() ) {
					Drawing<?> drawing = sheet.createDrawingPatriarch();
					ClientAnchor anchor = wb.getCreationHelper().createClientAnchor();
					anchor.setAnchorType( ClientAnchor.AnchorType.MOVE_AND_RESIZE );
					int pictureIndex =
					        wb.addPicture(remunerationRecordData.getLogo().orElse(null), Workbook.PICTURE_TYPE_PNG);
//					anchor.setCol1( 0 );
//					anchor.setRow1(0); // same row is okay
//					anchor.setRow2(0);
//					anchor.setCol2( 1 );
					anchor.setDx1(100 * Units.EMU_PER_PIXEL);
					anchor.setDx2(200 * Units.EMU_PER_PIXEL);
					anchor.setDy1(100* Units.EMU_PER_PIXEL);
					anchor.setDy2(200 * Units.EMU_PER_PIXEL);
					drawing.createPicture( anchor, pictureIndex );
//					pict.resize();
					
					
				}
			}
			
			
			//SOCIAL REASON
			{
				row = sheet.getRow(5);
				cell = row.getCell(2);
				cell.setCellValue(remunerationRecordData.getSocialReason());
			}
			
			//NIF
			{
				row = sheet.getRow(7);
				cell = row.getCell(2);
				cell.setCellValue(remunerationRecordData.getEnterpriseDocument());
			}
			
			//START DATE
			{
				row = sheet.getRow(9);
				cell = row.getCell(4);
				Calendar cal = Calendar.getInstance();
				cal.setTime(remunerationRecordData.getStartDate());
				cal.set(Calendar.HOUR, 12);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cell.setCellValue(cal);
			}
			
			//END DATE
			{
				cell = row.getCell(6);
//				cell.setCellStyle(blackDate);
				Calendar cal = Calendar.getInstance();
				cal.setTime(remunerationRecordData.getEndDate());
				cal.set(Calendar.HOUR, 12);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cell.setCellValue(cal);
			}
			
			
			//"CONC.RETR" SHEET
			{
				conceptOrder = new String[36];
				Collection<IRetributiveConcept> concepts = remunerationRecordData.getConcepts();
				
				AtomicInteger rowNum = new AtomicInteger(11);
				
				concepts.stream().filter(c -> c.getRetributionType() == RetributionType.BASE_SALARY)
				.forEach(c -> {
					int ind = rowNum.getAndIncrement();
					addConcept(wb, c, ind, ind>11 ? "S.BASE" + (ind-11) : "S.BASE");
					conceptOrder[0] = c.getName();
				});
				int lastBase = rowNum.get()-1;
				concepts.stream().filter(c -> c.getRetributionType() != RetributionType.BASE_SALARY)
				.forEach(c -> {
					int ind = rowNum.getAndIncrement();
					int num = ind-lastBase;
					addConcept(wb, c, ind, num>9 ? "Conc.Sal." + num : "Conc.Sal.0" + num);
					conceptOrder[num] = c.getName();
				});
			}
			
			
			//"DATOS" SHEET
			{
				
				
				sheet = wb.getSheetAt(3);
				wb.setActiveSheet(3);
				
				
				Row firstRow = sheet.getRow(8);
				
				
				
				int id = 1;
				int rowNum = 8;
				if (remunerationRecordData.getEntries() != null) {
					for (String nss : remunerationRecordData.getEntries().keySet()) {
						
						LinkedList<IRemunerationRecordEntry> entries = remunerationRecordData.getEntries().get(nss);
						
						for (IRemunerationRecordEntry entry : entries) {
							if (entry != null ) {
								row = sheet.getRow(rowNum) != null ? sheet.getRow(rowNum++) : sheet.createRow(rowNum++);
								//ID
								{
									cell = row.getCell(1) != null ? row.getCell(1) : row.createCell(1);
									copyCellProperties(wb, cell, firstRow.getCell(1));
									cell.setCellValue(id);
								}
								//GENDER
								{
									cell = row.getCell(2) != null ? row.getCell(2) : row.createCell(2);
									copyCellProperties(wb, cell, firstRow.getCell(2));
									cell.setCellValue(entry.getGender() != Gender.UNKNOWN ? entry.getGender().getName(new Locale("es")) : null);
								}
								//BIRTH DATE
								{
									cell = row.getCell(3) != null ? row.getCell(3) : row.createCell(3);
									copyCellProperties(wb, cell, firstRow.getCell(3));
									cell.setCellStyle(grayDate);
									cell.setCellValue(entry.getBirthDate());
								}
								//FAMILY SITUATION
								{
									cell = row.getCell(5) != null ? row.getCell(5) : row.createCell(5);
									copyCellProperties(wb, cell, firstRow.getCell(5));
									if (entry.getFamilySituation() != null)
										cell.setCellValue(entry.getFamilySituation().ordinal());
								}
								//HIRE DATE
								{
									cell = row.getCell(7) != null ? row.getCell(7) : row.createCell(7);
									copyCellProperties(wb, cell, firstRow.getCell(7));
									cell.setCellStyle(blackDate);
									cell.setCellValue(entry.getHireDate());
								}
								//CONTRACT END DATE
								{
									cell = row.getCell(8) != null ? row.getCell(8) : row.createCell(8);
									copyCellProperties(wb, cell, firstRow.getCell(8));
									cell.setCellStyle(grayDate);
									cell.setCellValue(entry.getContractEndDate());
								}
								//SENIORITY DATE
								{
									cell = row.getCell(9) != null ? row.getCell(9) : row.createCell(9);
									cell.setCellStyle(grayDate);
									cell.setCellValue(entry.getSeniorityDate());
								}
								//CONTRACT SITUATION START
								{
									cell = row.getCell(10) != null ? row.getCell(10) : row.createCell(10);
									copyCellProperties(wb, cell, firstRow.getCell(10));
									cell.setCellStyle(blackDate);
									cell.setCellValue(entry.getContractSituationStartDate());
								}
								//CONTRACT SITUATION END
								{
									cell = row.getCell(11) != null ? row.getCell(11) : row.createCell(11);
									copyCellProperties(wb, cell, firstRow.getCell(11));
									cell.setCellStyle(blackDate);
									cell.setCellValue(entry.getContractSituationEndDate());
								}
								//WORDAY PERCENT
								{
									cell = row.getCell(12) != null ? row.getCell(12) : row.createCell(12);
									copyCellProperties(wb, cell, firstRow.getCell(12));
									cell.setCellStyle(percentageStyle);
									cell.setCellValue(entry.getWorkdayPercent());
								}
								//CONTRACT KEY
								{
									cell = row.getCell(15) != null ? row.getCell(15) : row.createCell(15);
									copyCellProperties(wb, cell, firstRow.getCell(15));
									cell.setCellValue(entry.getContractKey());
								}
								//IN-ENTERPRISE CATEGORY
								{
									cell = row.getCell(18) != null ? row.getCell(18) : row.createCell(18);
									copyCellProperties(wb, cell, firstRow.getCell(18));
									cell.setCellValue(entry.getCategory());
								}
								//PROFESSIONAL GROUP
								{
									cell = row.getCell(22) != null ? row.getCell(22) : row.createCell(22);
									copyCellProperties(wb, cell, firstRow.getCell(22));
									cell.setCellValue(entry.getProfessionalGroup());
								}
								//AGREEMENT
								{
									cell = row.getCell(24) != null ? row.getCell(24) : row.createCell(24);
									copyCellProperties(wb, cell, firstRow.getCell(24));
									cell.setCellValue(entry.getAgreement());
								}
								//AGREEMENT LEVEL
								{
									cell = row.getCell(27) != null ? row.getCell(27) : row.createCell(27);
									copyCellProperties(wb, cell, firstRow.getCell(27));
									cell.setCellValue(entry.getLevel());
								}
								//QUOTE GROUP
								{
									cell = row.getCell(28) != null ? row.getCell(28) : row.createCell(28);
									copyCellProperties(wb, cell, firstRow.getCell(28));
									cell.setCellValue(entry.getQuoteGroup());
								}
								
								//PAYMENTS
								{
									int cellNum = 29;
									Map<String, Double> payments = entry.getPayments();
//									System.out.println(payments);
									for (int i=0; i<conceptOrder.length; i++) {
//										System.out.println(conceptOrder[i]);
										cell = row.getCell(cellNum+i) != null ? row.getCell(cellNum+i) : row.createCell(cellNum+i);
//										copyCellProperties(wb, cell, firstRow.getCell(cellNum+i));
										if (payments.get(conceptOrder[i]) != null) {
											cell.setCellValue(payments.get(conceptOrder[i]));
										}
									}
								}	
							}
						}
						id++;
					}
					//DELETE EXCESS ROWS
					{
						
//						for (int i=8;i<=8+remunerationRecordData.getEntries().values().size();i++) {
//							rowNum = i+1;
//							sheet.getRow(i).getCell(65).setCellFormula("+MAX($D$3,$K"+rowNum+")");
//							sheet.getRow(i).getCell(66).setCellFormula("+MIN($D$4,$L"+rowNum+")");
//							sheet.getRow(i).getCell(67).setCellFormula("+$M9*IF($N"+rowNum+"=\"\",1,$N"+rowNum+")");
//							sheet.getRow(i).getCell(68).setCellFormula("+($BO"+rowNum+"-$BN"+rowNum+"+1)/($D$4-$D$3+1)");
//							sheet.getRow(i).getCell(69).setCellFormula("IF(OR(AND($I"+rowNum+"<$D$3,$I"+rowNum+"<>\"\"),$H"+rowNum+">$D$4),\"Fuera\",\"Dentro\")");
//							sheet.getRow(i).getCell(70).setCellFormula("IF(MAX(IF($B$"+rowNum+":$B$"+rowNum+"=$B"+rowNum+",$BO$"+rowNum+":$BO$"+rowNum+"))=$BO"+rowNum+",\"Sí\",\"NO\")");
//							sheet.getRow(i).getCell(71).setCellFormula("IF(AND($BP"+rowNum+"<>1,$BQ"+rowNum+"=1),\"Sí\",\"NO\")");
//							sheet.getRow(i).getCell(72).setCellFormula("IF(AND($BP"+rowNum+"=1,$BQ"+rowNum+"<>1),\"Sí\",\"NO\")");
//							sheet.getRow(i).getCell(73).setCellFormula("IF(AND($BP"+rowNum+"<>1,$BQ"+rowNum+"<>1),\"Sí\",\"NO\")");
//							sheet.getRow(i).getCell(74).setCellFormula("+IF($BP"+rowNum+"*$BQ"+rowNum+"=1,\"NO\",\"Sí\")");
//							sheet.getRow(i).getCell(75).setCellFormula("($AD"+rowNum+"*(1/$BP"+rowNum+")*(1/$BQ"+rowNum+"))*IF($BS"+rowNum+"=\"Sí\",1,0)");
//							sheet.getRow(i).getCell(76).setCellFormula("(SUMPRODUCT((AE3:BM3=$BY$7)*(AE4:BM4=\"Sí\")*(AE5:BM5=\"Sí\")*($AE"+rowNum+":$BM"+rowNum+"))*(1/$BQ"+rowNum+")*(1/$BP"+rowNum+") +SUMPRODUCT((AE3:BM3=$BY$7)*(AE4:BM4=\"Sí\")*(AE5:BM5=\"No\")*($AE"+rowNum+":$BM"+rowNum+"))*(1/$BQ"+rowNum+") +SUMPRODUCT((AE3:BM3=$BY$7)*(AE4:BM4=\"No\")*(AE5:BM5=\"Sí\")*($AE"+rowNum+":$BM"+rowNum+"))*(1/$BP"+rowNum+") +SUMPRODUCT((AE3:BM3=$BY$7)*(AE4:BM4=\"No\")*(AE5:BM5=\"No\")*($AE"+rowNum+":$BM"+rowNum+"))) *IF($BS"+rowNum+"=\"Sí\",1,0)");
//							sheet.getRow(i).getCell(77).setCellFormula("+$BX"+rowNum+"+$BY"+rowNum);
//							sheet.getRow(i).getCell(78).setCellFormula("(SUMPRODUCT((AE3:BM3=$CA$7)*(AE4:BM4=\"Sí\")*(AE5:BM5=\"Sí\")*($AE"+rowNum+":$BM"+rowNum+"))*(1/$BQ"+rowNum+")*(1/$BP"+rowNum+") +SUMPRODUCT((AE3:BM3=$CA$7)*(AE4:BM4=\"Sí\")*(AE5:BM5=\"No\")*($AE"+rowNum+":$BM"+rowNum+"))*(1/$BQ"+rowNum+") +SUMPRODUCT((AE3:BM3=$CA$7)*(AE4:BM4=\"No\")*(AE5:BM5=\"Sí\")*($AE"+rowNum+":$BM"+rowNum+"))*(1/$BP"+rowNum+") +SUMPRODUCT((AE3:BM3=$CA$7)*(AE4:BM4=\"No\")*(AE5:BM5=\"No\")*($AE"+rowNum+":$BM"+rowNum+"))) *IF($BS"+rowNum+"=\"Sí\",1,0)");
//							sheet.getRow(i).getCell(79).setCellFormula("+$BZ"+rowNum+"+$CA"+rowNum);
//							sheet.getRow(i).getCell(80).setCellFormula("$AD"+rowNum);
//							sheet.getRow(i).getCell(81).setCellFormula("+SUM($AE"+rowNum+":$BH"+rowNum+")");
//							sheet.getRow(i).getCell(82).setCellFormula("+$CC"+rowNum+"+$CD"+rowNum);
//							sheet.getRow(i).getCell(83).setCellFormula("+SUM($BI"+rowNum+":$BM"+rowNum+")");
//							sheet.getRow(i).getCell(84).setCellFormula("+$CE"+rowNum+"+$CF"+rowNum);
//							sheet.getRow(i).getCell(85).setCellFormula("MIN(IF($I"+rowNum+"=\"\",$D$4,$I"+rowNum+"),$D$4)-MAX($H"+rowNum+",$D$3)+1");
//							sheet.getRow(i).getCell(86).setCellFormula("IF(COUNTIFS($B$"+rowNum+":$B$"+rowNum+",$B"+rowNum+")>1,$B"+rowNum+",\"\")");
//							sheet.getRow(i).getCell(87).setCellFormula("(Inicio!$G$10-$D"+rowNum+")/365.25");
//							sheet.getRow(i).getCell(88).setCellFormula("(Inicio!$G$10-$J"+rowNum+")/365.25");
//							sheet.getRow(i).getCell(89).setCellFormula("+VLOOKUP($P"+rowNum+",CONTRATOS!$D$5:$G$47,3,0)");
//							sheet.getRow(i).getCell(90).setCellFormula("VLOOKUP($CJ"+rowNum+",CONTRATOS!$M$5:$N$14,2,1)");
//							sheet.getRow(i).getCell(91).setCellFormula("VLOOKUP($CK"+rowNum+",CONTRATOS!$J$5:$K$10,2,1)");
//						}
						
						for (int i= 8+remunerationRecordData.getEntries().values().size(); i<=sheet.getLastRowNum();i++) {
							if (sheet.getRow(i) == null)
								break;
							sheet.removeRow(sheet.getRow(i));
						}
						
					}
				}
//				wb.getSheetAt(6).getRow(10).getCell(2).setCellFormula("SUM(IF($B11=DATOS!$C$9:$C$145,IF(\"Dentro\"=DATOS!$BR$9:$BR$145,1/(COUNTIFS(DATOS!$C$9:$C$145,$B11,DATOS!$BR$9:$BR$145,\"Dentro\",DATOS!$B$9:$B$145,DATOS!$B$9:$B$145)))))");
				
				
				
				//RESIZE
				{
					row = sheet.getRow(sheet.getLastRowNum());
					for (int i=0; i<=row.getLastCellNum(); i++) {
						sheet.autoSizeColumn(i);
					}
				}
				
			}
			
			wb.setActiveSheet(0);
			
			wb.write(os);
			
			
		} catch (IOException e) {
			System.out.println("ERROR");
		}
	}
	
	private static void fillData(RemunerationRecordData remunerationRecordData, AONContext aonContext, Stream<Record3<Byte, String, String>> payments, Condition condition) {
		//ALIASED TABLES
		SalaryData contractKey = SalaryData.SALARY_DATA.as("CONTRACT_KEY");
		SalaryData parcialityCoef = SalaryData.SALARY_DATA.as("PARCIALITY_COEF");
		SalaryData quoteGroup = SalaryData.SALARY_DATA.as("QUOTE_GROUP");
		
		//BUILDING THE QUERY
		SelectSeekStep1<Record, java.sql.Date> sql = aonContext.getDslContext()
		.select()
		.from(SALARY)
		.innerJoin(CONTRACT).on(SALARY.CONTRACT.eq(CONTRACT.ID))
		.innerJoin(IRPF_DATA).on(CONTRACT.ID.eq(IRPF_DATA.CONTRACT))
		.innerJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
		.innerJoin(AGREEMENT_LEVEL).on(CONTRACT.AGREEMENT_LEVEL.eq(AGREEMENT_LEVEL.ID))
		.innerJoin(AGREEMENT).on(AGREEMENT_LEVEL.AGREEMENT.eq(AGREEMENT.ID))
		.innerJoin(contractKey).on(SALARY.ID.eq(contractKey.SALARY)).and(contractKey.NAME.eq("TC2"))
		.innerJoin(parcialityCoef).on(SALARY.ID.eq(parcialityCoef.SALARY)).and(parcialityCoef.NAME.eq("COEFICIENTE_PARCIALIDAD"))
		.innerJoin(quoteGroup).on(SALARY.ID.eq(quoteGroup.SALARY)).and(quoteGroup.NAME.eq("GRUPO_COTIZACION"))
		.where(condition).groupBy(CONTRACT.ID).orderBy(CONTRACT.START_DATE);
		
		
		HashSet<SalaryPayment> aliasedTables = new HashSet<SalaryPayment>();
		LinkedList<IRetributiveConcept> concepts = new LinkedList<IRetributiveConcept>();
		
		List<Record3<Byte, String, String>> paymentList = payments.collect(Collectors.toList());
		
		paymentList.forEach(payment -> {
				//ADDING CONCEPTS TO A LIST TO USE AS ALIASED TABLES
				aliasedTables.add(SALARY_PAYMENT.as(String.valueOf(payment.get("concept"))));
				
				//ADDING CONCEPTS
				{
					RetributiveConcept retributiveConcept = new RetributiveConcept();
					retributiveConcept.type = typeOf(payment.get(SALARY_PAYMENT.TYPE), PaymentType.class);
					retributiveConcept.name = String.valueOf(payment.get("concept"));
					retributiveConcept.description = payment.get(SALARY_PAYMENT.DESCRIPTION);
					retributiveConcept.retributionForm = (retributiveConcept.type != null
							&& retributiveConcept.type.ordinal() >= 13
							&& retributiveConcept.type.ordinal() <=26) ? RetributionForm.IN_KIND : RetributionForm.MONEY;
					Pattern pattern = Pattern.compile(".*?(salario|sueldo).*?base.*", Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(retributiveConcept.name);
					retributiveConcept.retributionType = matcher.matches() ?
							RetributionType.BASE_SALARY : RetributionType.SALARY_COMPLEMET;
					
					concepts.add(retributiveConcept);
				}
				
			});
		
		
		
		//ADDING THE CONCEPT ALIASED TABLES TO THE QUERY
//		for (SalaryPayment pmnt : aliasedTables) {
//			sql = sql
//			.leftJoin(pmnt)
//			.on(
//				pmnt.SALARY.eq(SALARY.ID)
//				.and(DSL.ifnull(pmnt.PAYMENT_CONCEPT, pmnt.DESCRIPTION).eq(pmnt.getName()))
//			);
//		}
		
		//FINISHING THE QUERY
//		SelectSeekStep1<Record, java.sql.Date> newSql = sql.where(condition).groupBy(CONTRACT.ID).orderBy(CONTRACT.START_DATE);
		
		
		LinkedHashMap<String, LinkedList<IRemunerationRecordEntry>> entries = new LinkedHashMap<String, LinkedList<IRemunerationRecordEntry>>();
		
		//ADDING THE ENTRIES TO A MAP WHICH KEY'LL BE THE QUOTE GROUP
		sql.fetchStream().forEach(r -> {
			RemunerationRecordEntry remunerationRecordEntry = new RemunerationRecordEntry();
			{
				remunerationRecordEntry.agreement = r.get(AGREEMENT.DESCRIPTION);
				remunerationRecordEntry.birthDate = r.get(PERSON.BIRTH_DATE);
				remunerationRecordEntry.category = r.get(SALARY.CATEGORY);
				remunerationRecordEntry.contractEndDate = r.get(CONTRACT.END_DATE);
				remunerationRecordEntry.contractKey = r.get(contractKey.EXPRESSION);
				remunerationRecordEntry.familySituation = typeOf(r.get(IRPF_DATA.FAMILY_SITUATION), FamilySituation.class);
				remunerationRecordEntry.gender = typeOf(r.get(PERSON.GENDER), Gender.class);
				remunerationRecordEntry.hireDate = r.get(CONTRACT.START_DATE);
				remunerationRecordEntry.name = r.get(SALARY.EMPLOYEE_NAME);
				try {
					remunerationRecordEntry.quoteGroup = Integer.parseInt(r.get(quoteGroup.EXPRESSION));
				} catch (NullPointerException | NumberFormatException e) {
					remunerationRecordEntry.quoteGroup = null;
				}
				remunerationRecordEntry.workdayPercent = Double.parseDouble(r.get(parcialityCoef.EXPRESSION));
				remunerationRecordEntry.seniorityDate = r.get(CONTRACT.SENIORITY_DATE);
				remunerationRecordEntry.socialSecurityNumber = r.get(PERSON.SOCIAL_SECURITY_NUM);
				remunerationRecordEntry.level = r.get(AGREEMENT_LEVEL.DESCRIPTION);
			}
			
			LinkedHashMap<String, Double> map = new LinkedHashMap<String, Double>();   
			
			
			paymentList.forEach(payment -> {
				Record1<BigDecimal> pmnt = aonContext.getDslContext()
				.select(DSL.sum(SALARY_PAYMENT.AMOUNT).as("total"))
				.from(SALARY_PAYMENT)
				.innerJoin(SALARY).onKey()
				.where(DSL.upper(DSL.ifnull(SALARY_PAYMENT.PAYMENT_CONCEPT, SALARY_PAYMENT.DESCRIPTION)).eq(""+payment.get("concept")))
				.and(SALARY.START_DATE.ge(new java.sql.Date(remunerationRecordData.getStartDate().getTime())))
				.and(SALARY.END_DATE.le(new java.sql.Date(remunerationRecordData.getEndDate().getTime())))
				.and(SALARY.CONTRACT.eq(r.get(CONTRACT.ID)))
				.fetchOne();
				
				map.put(""+payment.get("concept"), pmnt.get("total") != null ? ((BigDecimal)pmnt.get("total")).doubleValue() : null);
				
				
			});
			
//			aliasedTables.forEach(t -> map.put(t.getName(), r.get(t.AMOUNT)));
			
			remunerationRecordEntry.payments = map;
			
			if (entries.containsKey(r.get(PERSON.SOCIAL_SECURITY_NUM))) {
				entries.get(r.get(PERSON.SOCIAL_SECURITY_NUM)).add(remunerationRecordEntry);
			} else {
				LinkedList<IRemunerationRecordEntry> list = new LinkedList<IRemunerationRecordEntry>();
				list.add(remunerationRecordEntry);
				entries.put(r.get(PERSON.SOCIAL_SECURITY_NUM), list);
			}
			
		});
		//GETTING ALL GROUPS
		TreeSet<Integer> groupSet = new TreeSet<Integer>();
		
		for (String key : entries.keySet())
			entries.get(key).stream().map(e -> e.getQuoteGroup()).forEach(group -> groupSet.add(group));
		
		LinkedList<Integer> groups = new LinkedList<Integer>();
		groups.addAll(groupSet);
		
		//ADDING GROUPS
		for (String key : entries.keySet()) {
			LinkedList<IRemunerationRecordEntry> entryList = entries.get(key);
			
			for(int i=0; i<entryList.size(); i++) {
				
				RemunerationRecordEntry ent = ((RemunerationRecordEntry) entryList.get(i));
				
				if (entries.get(key).size() > 1) {
					if(entries.get(key).size() > i+1) {
						if (i>0) {
							ent.contractSituationStartDate = ent.getHireDate();
						}
						ent.contractSituationEndDate = ent.getContractEndDate();
					} else {
						ent.contractSituationStartDate = ent.getHireDate();
					}
					ent.hireDate = entryList.get(0).getHireDate();
				}
				
				Integer groupInd = groups.indexOf(entryList.get(i).getQuoteGroup())+1;
				ent.professionalGroup = groupInd > 9 ? "GRUPO "+groupInd : "GRUPO 0"+groupInd;
			}
		}
		
		
		
		//FILLING THE RemunerationRecordData OBJECT
		remunerationRecordData.setConcepts(concepts);
		remunerationRecordData.setEntries(entries);
		
	}
	
	
	public static RemunerationRecordData getData(String domainName, Optional<Integer> enterpriseId, Date startDate, Date endDate) {
		java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
		java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
		RemunerationRecordData remunerationRecordData = new RemunerationRecordData();
		remunerationRecordData.setStartDate(startDate);
		remunerationRecordData.setEndDate(endDate);
		AONContext aonContext = AONContext.getAONContext(domainName, "");

		Condition condition = SALARY.DOMAIN.eq(aonContext.getDomainId())
				.and(SALARY_PAYMENT.PAYMENT_CONCEPT.isNotNull().or(SALARY_PAYMENT.DESCRIPTION.isNotNull()))
				.and(SALARY.START_DATE.ge(sqlStartDate))
				.and(SALARY.END_DATE.le(sqlEndDate));
		
		Condition condition2 = SALARY.DOMAIN.eq(aonContext.getDomainId())
				.and(SALARY.START_DATE.ge(sqlStartDate))
				.and(SALARY.END_DATE.le(sqlEndDate));
		
		if (!enterpriseId.isEmpty() && enterpriseId.get() > 0) {
			condition = condition.and(WORKPLACE.ENTERPRISE.eq(enterpriseId.get()));
			condition2 = condition2.and(WORKPLACE.ENTERPRISE.eq(enterpriseId.get()));
			
			Enterprise enterprise = AON.getEnterprise(aonContext.getDomainName(), aonContext.getDomainId(), "", enterpriseId.get());
			remunerationRecordData.setSocialReason(enterprise.getName());
			remunerationRecordData.setEnterpriseDocument(enterprise.getDocument());
		} else { 
			RegistryRecord registryRecord = aonContext.getDslContext()
			.select()
			.from(ENTERPRISE)
			.innerJoin(REGISTRY).onKey()
			.where(ENTERPRISE.DOMAIN.eq(aonContext.getDomainId()))
			.fetchOneInto(REGISTRY);
			
			remunerationRecordData.setSocialReason(registryRecord.getName());
			remunerationRecordData.setEnterpriseDocument(registryRecord.getDocument());
		}
			
		
		Attach logoAttach = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
				f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
						.and(f.getDomainProperty().eq(aonContext.getDomainId())),
				AttachType.REGISTRY);
		
		if (logoAttach != null && logoAttach.getData() != null) {
			remunerationRecordData.setLogo(Optional.ofNullable(logoAttach.getData()));
		}
		
		
		Stream<Record3<Byte, String, String>> payments = getSalaryConceptsStream(aonContext, condition);
		
		
		
		fillData(remunerationRecordData, aonContext, payments, condition2);
		return remunerationRecordData;
	}
	
	public static void generateExcel (OutputStream outputStream, String domainName, Optional<Integer> enterpriseId, Date startDate, Date endDate) {
		getExcel(outputStream, getData(domainName, enterpriseId, startDate, endDate));
	}
	
	
	/*public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.YEAR, 2021);
		Date startDate = calendar.getTime();

		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		Date endDate = calendar.getTime();
		
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("/home/igonzalez/Escritorio/pruebasExcel/excelxd.xlsx");
			generateExcel(fos, "b72384936-ayudat.aonsolutions.net", Optional.empty(), startDate, endDate);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
		
		
		System.out.println("PROGRAM COMPLETE");
	}*/
	
	private static Stream<Record3<Byte, String, String>> getSalaryConceptsStream(AONContext aonContext, Condition condition) {
		Stream<Record3<Byte, String, String>> payments = aonContext.getDslContext()
				.select(SALARY_PAYMENT.TYPE,
						DSL.upper(DSL.ifnull(SALARY_PAYMENT.PAYMENT_CONCEPT, SALARY_PAYMENT.DESCRIPTION)).as("concept"),
						SALARY_PAYMENT.DESCRIPTION)
				.from(SALARY_PAYMENT)
				.innerJoin(SALARY).onKey()
				.innerJoin(CONTRACT).onKey()
				.innerJoin(WORKPLACE).onKey()
				.where(condition)
				.groupBy(SALARY_PAYMENT.PAYMENT_CONCEPT)
				.having(DSL.sum(SALARY_PAYMENT.AMOUNT).gt(new BigDecimal(0))).orderBy(1, 2).fetchStream();
		return payments;
	}
	
	private static void addConcept (Workbook wb, IRetributiveConcept c, int rowNum, String valor) {
		Sheet conceptSheet = wb.getSheetAt(2);
		Row conceptRow = conceptSheet.getRow(rowNum);
		//Valor
		{
			conceptRow.createCell(1).setCellValue(valor);
		}
		//Nombre Corto
		{
			conceptRow.createCell(2).setCellValue(c.getName());
		}
		//Descripción
		{
			conceptRow.createCell(3).setCellValue(c.getDescription());
		}
		//Retrib
		{
			conceptRow.createCell(4).setCellValue(c.getRetributionForm() != null ? c.getRetributionForm().getDescription() : null);
		}
		//Tipo
		{
			conceptRow.createCell(5).setCellValue(c.getRetributionType() != null ? c.getRetributionType().getDescription() : null);
		}
		//Normalizable and Anualizable are not set
		conceptRow.createCell(6).setCellValue("Sí");
		conceptRow.createCell(7).setCellValue("Sí");
		
	}
	
	private static void copyCellProperties(Workbook wb, Cell newCell, Cell originalCell) {
		newCell.setCellComment(originalCell.getCellComment());
		if (originalCell.getCellTypeEnum() == CellType.FORMULA) {
			try {
			newCell.setCellFormula(originalCell.getCellFormula());
			} catch (FormulaParseException e) {
			}
		}
		newCell.setCellStyle(originalCell.getCellStyle());
		newCell.setCellType(originalCell.getCellTypeEnum());
		newCell.setHyperlink(originalCell.getHyperlink());
	}
	
	private static Cell getCell (Row row, int ind) {
		if (row.getCell(ind) != null)
			return row.getCell(ind);
		else
			return row.createCell(ind);
	}
	
	
	private static void redoFormulas(Workbook wb) {
		Iterator<Sheet> it = wb.sheetIterator();
		while (it.hasNext()) {
			Sheet sheet = it.next();
			Iterator<Row> rowIt = sheet.rowIterator();
			while (rowIt.hasNext()) {
				Row row = rowIt.next();
				Iterator<Cell> cellIt = row.cellIterator();
				while (cellIt.hasNext()) {
					Cell cell = cellIt.next();
					if (cell.getCellTypeEnum() == CellType.FORMULA) {
						try {
							String formula = cell.getCellFormula();
							cell.setCellFormula(formula);
//							wb.getCreationHelper().createFormulaEvaluator().evaluate(cell);
						} catch (Exception e) {
							System.out.println("Sheet name: "+sheet.getSheetName()+", "+CellReference.convertNumToColString(cell.getColumnIndex())+(cell.getRowIndex()+1));
						}
					}
				}
			}
		}
	}
	
	private static void redoSheetFormulas (Sheet sheet) {
		Iterator<Row> rowIt = sheet.rowIterator();
		while (rowIt.hasNext()) {
			Row row = rowIt.next();
			Iterator<Cell> cellIt = row.cellIterator();
			while (cellIt.hasNext()) {
				Cell cell = cellIt.next();
				if (cell.getCellTypeEnum() == CellType.FORMULA) {
//					try {
						String formula = cell.getCellFormula();
						cell.setCellFormula(formula);
//					} catch (FormulaParseException e) {}
				}
			}
		}
	}
	
	
	
	private static <T extends Enum<?>> T typeOf(Byte ordinal, Class<T> type) {
	    if ( ordinal == null )
	        return null;
	    try {
	        return type.getEnumConstants()[ordinal];
	    } catch ( Exception e ) {
	        return null;
	    }
	}

}
