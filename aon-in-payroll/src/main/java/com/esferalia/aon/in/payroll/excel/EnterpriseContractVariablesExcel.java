package com.esferalia.aon.in.payroll.excel;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jooq.Record;
import org.jooq.Result;

import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class EnterpriseContractVariablesExcel {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static Map<ContractCellStyle, CellStyle> stylesMap;
	private static Sheet sheet;
	
	// ------------------------------------------------------------------------------------
	//									EXPORT EXCEL
	// ------------------------------------------------------------------------------------
	
	public static void enterpriseContractVariablesExport(String domainName, String user, Integer domainId, Set<String> variables, Date date, OutputStream outputStream) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, domainId, user)) {
			exportExcel(outputStream, aonContext, variables, date);	
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public static void exportExcel(OutputStream outputStream, CloseableAONContext aonContext, Set<String> variables, Date date) throws IOException {
		
		Workbook wb = new XSSFWorkbook();

		DataFormat format = wb.createDataFormat();

		stylesMap = ContractCellStyle.getStyles(wb, format);
		
		sheet = wb.createSheet(WorkbookUtil.createSafeSheetName("Variables Empresa"));
		
		configSheetWidth(variables);
		
		createInfoHeader();
		writeInfoHeader(aonContext, date);
		
		createHeader(variables);
		
		Integer totalContractDatas = 0;
		
		for(Record record : getEnterpriseActiveContracts(aonContext, date)) {
			Row row = sheet.createRow(sheet.getLastRowNum() + 1);
			writeEmployee(row, record);
			totalContractDatas += writeVariablesDate(aonContext, variables, row, record.get(CONTRACT.ID), date);
		}
		
		wb.write(outputStream);
		outputStream.close();
		wb.close();
		
	}
	
	private static void configSheetWidth(Set<String> variables) {
		sheet.setColumnWidth(0, 30 * 300);
		sheet.setColumnWidth(1, 15 * 300);
		sheet.setColumnWidth(2, 15 * 300);
		sheet.setColumnWidth(3, 10 * 300);
		
		List<String> variablesList = new ArrayList<String>(variables); 
		for(int column=0; column < variablesList.size(); column++) {
			String variable = variablesList.get(column);
			sheet.setColumnWidth(column + 4, (variable.length() < 15 ? 15 : variable.length()) * 400);
		}
	}
	
	private static void createInfoHeader() {
		Row row = sheet.createRow(0);
		int col=0;
		
		Cell cell = row.createCell(col);
		cell.setCellValue("EMPRESA");
		cell.setCellStyle(stylesMap.get(ContractCellStyle.HEADER_INFO_CELL_STYLE));
		col++;
		
		cell = row.createCell(col);
		cell.setCellValue("PERIODO");
		cell.setCellStyle(stylesMap.get(ContractCellStyle.HEADER_INFO_CELL_STYLE));
		col++;
	}
	
	private static void writeInfoHeader(CloseableAONContext aonContext, Date date) {
		Record enterpriseRecord = aonContext.getDslContext().select()
			.from(REGISTRY)
			.innerJoin(ENTERPRISE)
			.on(ENTERPRISE.REGISTRY.eq(REGISTRY.ID))
			.where(ENTERPRISE.DOMAIN.eq(aonContext.getDomainId()))
			.fetchOne();
		
		Row row = sheet.createRow(sheet.getLastRowNum() + 1);
		
		Cell cell = row.createCell(0);
		cell.setCellValue(enterpriseRecord.get(REGISTRY.NAME) + " (" + enterpriseRecord.get(REGISTRY.DOCUMENT) + ")");
		cell.setCellStyle(stylesMap.get(ContractCellStyle.STRING_CELL_LEFT_STYLE));
		
		cell = row.createCell(1);
		cell.setCellValue(dateFormat.format(date));
		cell.setCellStyle(stylesMap.get(ContractCellStyle.STRING_CELL_STYLE));
	}
	
	private static void createHeader(Set<String> variables) {
		Row row = sheet.createRow(sheet.getLastRowNum() + 2);
		int col=0;
		
		Cell cell = row.createCell(col);
		cell.setCellValue("TRABAJADOR");
		cell.setCellStyle(stylesMap.get(ContractCellStyle.HEADER_CELL_STYLE));
		col++;
		
		cell = row.createCell(col);
		cell.setCellValue("DOCUMENTO");
		cell.setCellStyle(stylesMap.get(ContractCellStyle.HEADER_CELL_STYLE));
		col++;
		
		cell = row.createCell(col);
		cell.setCellValue("CCC");
		cell.setCellStyle(stylesMap.get(ContractCellStyle.HEADER_CELL_STYLE));
		col++;
		
		cell = row.createCell(col);
		cell.setCellValue("CODIGO");
		cell.setCellStyle(stylesMap.get(ContractCellStyle.HEADER_CELL_STYLE));
		col++;
		
		for(String variable : variables) {
			cell = row.createCell(col);
			cell.setCellValue(variable);
			cell.setCellStyle(stylesMap.get(ContractCellStyle.HEADER_CELL_STYLE));
			col++;
		}
	}
	
	private static List<Record> getEnterpriseActiveContracts(CloseableAONContext aonContext, Date date) {
		return aonContext.getDslContext().select()
				.from(CONTRACT)
				.innerJoin(REGISTRY)
				.on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.innerJoin(ENTERPRISE_CCC)
				.on(ENTERPRISE_CCC.ID.eq(CONTRACT.ENTERPRISE_CCC))
				.where(CONTRACT.DOMAIN.eq(aonContext.getDomainId()))
				.and(CONTRACT.END_DATE.ge(parseDateSql(date)).or(CONTRACT.END_DATE.isNull()))
				.orderBy(REGISTRY.NAME)
				.fetch();
	}
	
	private static void writeEmployee(Row row, Record record) {
		Cell cell = row.createCell(0);
		cell.setCellValue(record.get(REGISTRY.NAME));
		cell.setCellStyle(stylesMap.get(ContractCellStyle.STRING_CELL_LEFT_STYLE));
		
		cell = row.createCell(1);
		cell.setCellValue(record.get(REGISTRY.DOCUMENT));
		cell.setCellStyle(stylesMap.get(ContractCellStyle.STRING_CELL_LEFT_STYLE));
		
		cell = row.createCell(2);
		cell.setCellValue(getCCCRegimeCode(record.get(ENTERPRISE_CCC.TYPE)) + " " +record.get(ENTERPRISE_CCC.CCC));
		cell.setCellStyle(stylesMap.get(ContractCellStyle.STRING_CELL_LEFT_STYLE));
		
		cell = row.createCell(3);
		cell.setCellValue(record.get(CONTRACT.ID));
		cell.setCellStyle(stylesMap.get(ContractCellStyle.STRING_CELL_STYLE));
	}

	private static Integer writeVariablesDate(CloseableAONContext aonContext, Set<String> variables, Row row, Integer contractId, Date date) {
		Map<String, ContractDataRecord> contractDatas = new HashMap<>();
		aonContext.getDslContext().selectFrom(CONTRACT_DATA)
			.where(CONTRACT_DATA.DOMAIN.eq(aonContext.getDomainId()))
			.and(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.in(variables))
			.and(CONTRACT_DATA.END_DATE.ge(parseDateSql(date)).or(CONTRACT_DATA.END_DATE.isNull()))
			.and(CONTRACT_DATA.START_DATE.le(parseDateSql(date)))
			.orderBy(CONTRACT_DATA.NAME)
			.fetch()
			.map(contractDateRecord -> contractDatas.put(contractDateRecord.getName(), contractDateRecord));
		
		List<String> variablesList = new ArrayList<String>(variables); 
		for(int column=0; column < variablesList.size(); column++) {
			String variable = variablesList.get(column);
			
			Cell cell = row.createCell(column + 4);
			cell.setCellValue(null != contractDatas.get(variable) ? contractDatas.get(variable).getExpression() : "");
			cell.setCellStyle(stylesMap.get(ContractCellStyle.STRING_CELL_RIGHT_STYLE));
		}
		
		return contractDatas.size();
		
	}
	
	// ------------------------------------------------------------------------------------
	//								 	EXCEL STYLES
	// ------------------------------------------------------------------------------------
	
	private enum ContractCellStyle {
		HEADER_CELL_STYLE,
		HEADER_INFO_CELL_STYLE,
		STRING_CELL_STYLE,
		STRING_CELL_LEFT_STYLE,
		STRING_CELL_RIGHT_STYLE
		;

		private static Map<ContractCellStyle, CellStyle> getStyles(Workbook wb, DataFormat format) {
			Font headerFont = wb.createFont();
			headerFont.setBold(true);
			Font headerRedFont = wb.createFont();
			headerRedFont.setBold(true);
			headerRedFont.setColor(IndexedColors.RED.getIndex());
			
			Font headerGreenFont = wb.createFont();
			headerGreenFont.setBold(true);
			headerGreenFont.setColor(IndexedColors.GREEN.getIndex());
			
			Font headerOrangeFont = wb.createFont();
			headerOrangeFont.setBold(true);
			headerOrangeFont.setColor(IndexedColors.ORANGE.getIndex());

			Font wrongFont = wb.createFont();
			wrongFont.setColor(IndexedColors.RED.getIndex());
			wrongFont.setFontHeightInPoints((short) 10);
			
			Font warningFont = wb.createFont();
			warningFont.setColor(IndexedColors.ORANGE.getIndex());
			warningFont.setFontHeightInPoints((short) 10);

			Font okFont = wb.createFont();
			okFont.setColor(IndexedColors.GREEN.getIndex());
			okFont.setFontHeightInPoints((short) 10);
			
			//Declaring different cell styles
			Map<ContractCellStyle, CellStyle> stylesMap = new EnumMap<>(ContractCellStyle.class);
			
			CellStyle headerCellStyle = wb.createCellStyle();
			headerCellStyle.setFont(headerFont);
			headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
			headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headerCellStyle.setBorderTop(BorderStyle.THIN);
			headerCellStyle.setBorderBottom(BorderStyle.THIN);
			headerCellStyle.setBorderLeft(BorderStyle.THIN);
			headerCellStyle.setBorderRight(BorderStyle.THIN);
			headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			stylesMap.put(ContractCellStyle.HEADER_CELL_STYLE, headerCellStyle);
			
			CellStyle headerInfoCellStyle = wb.createCellStyle();
			headerInfoCellStyle.setFont(headerFont);
			headerInfoCellStyle.setAlignment(HorizontalAlignment.CENTER);
			headerInfoCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headerInfoCellStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
			headerInfoCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			stylesMap.put(ContractCellStyle.HEADER_INFO_CELL_STYLE, headerInfoCellStyle);
			
			CellStyle stringCellLeftStyle = wb.createCellStyle();
			stringCellLeftStyle.setAlignment(HorizontalAlignment.LEFT);
			stringCellLeftStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			stringCellLeftStyle.setBorderBottom(BorderStyle.THIN);
			stringCellLeftStyle.setBorderTop(BorderStyle.THIN);
			stylesMap.put(ContractCellStyle.STRING_CELL_LEFT_STYLE, stringCellLeftStyle);
			
			CellStyle stringCellStyle = wb.createCellStyle();
			stringCellStyle.setAlignment(HorizontalAlignment.CENTER);
			stringCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			stringCellStyle.setBorderBottom(BorderStyle.THIN);
			stringCellStyle.setBorderTop(BorderStyle.THIN);
			stylesMap.put(ContractCellStyle.STRING_CELL_STYLE, stringCellStyle);
			
			CellStyle stringCellRightStyle = wb.createCellStyle();
			stringCellRightStyle.setAlignment(HorizontalAlignment.RIGHT);
			stringCellRightStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			stringCellRightStyle.setBorderBottom(BorderStyle.THIN);
			stringCellRightStyle.setBorderTop(BorderStyle.THIN);
			stylesMap.put(ContractCellStyle.STRING_CELL_RIGHT_STYLE, stringCellRightStyle);
			
			return stylesMap;
		}
	}
	
	// ------------------------------------------------------------------------------------
	//								AUXILIAR MEHTODS
	// ------------------------------------------------------------------------------------

	
	private static java.sql.Date parseDateSql(Date date) {
		return null == date ? null : new java.sql.Date(date.getTime());
	}
	
	private static String getCCCRegimeCode(Byte cccRegime) {
		switch (cccRegime) {
		case 0:
			return "0111";
		case 1:
			return "0111";
		case 2:
			return "0111";
		case 3:
			return "0111";
		case 4:
			return "0111";
		case 5:
			return "0111";
		case 6:
			return "0138";
		case 7:
			return "0163";
		case 8:
			return "0112";
		default:
			return "0111";
		}
	}
	
	// ------------------------------------------------------------------------------------
	//									IMPORT EXCEL
	// ------------------------------------------------------------------------------------

//	private static String enterprise;
	private static Date date;

	public static Map<String, List<String>> enterpriseContractVariablesImport(String domainName, String user, Integer domainId, InputStream is) {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, domainId, user)) {
			return importExcel(aonContext, is);	
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	private static Map<String, List<String>> importExcel(CloseableAONContext aonContext, InputStream is) throws IOException {
		Workbook wb = new XSSFWorkbook(is);
		Sheet sheet = wb.getSheetAt(0);
		
		Map<Integer, String> variablesMap = new HashMap<>();
		
		Map<Integer, List<ContractDataExcel>> contractDatasExcel = new HashMap<>();
		Map<Integer, List<ContractDataExcel>> deleteContractDatasExcel = new HashMap<>();
		
		Map<String, List<String>> messages = new HashMap<>();
		messages.put("success", new ArrayList<>());
		messages.put("errors", new ArrayList<>());
		
		Iterable<Row> rowIterable = () -> sheet.rowIterator();
		Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);
		
		Integer totalContractDatas = 0;
		
		for(Row row : rowStream.toList()) {
			Iterator<Cell> cellIterator = row.cellIterator();
			Iterable<Cell> cellIterable = () -> cellIterator;
			Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
			
			String document = "";
			Integer contractId = null;
			
			for(Cell cell : cellStream.toList()) {
//				if(row.getRowNum() == 1 && cell.getColumnIndex() == 0)
//					enterprise = cell.getStringCellValue();
				
				if(row.getRowNum() == 1 && cell.getColumnIndex() == 1) {
					String dateStr = cell.getStringCellValue();
					try {
						date = dateFormat.parse(dateStr);
					} catch (Exception e) {
						messages.get("errors").add("La fecha introducida en la celda[1, 1] no tiene el formato esperado dd/mm/aaaa");
						break;
					}
				}
				
				if(row.getRowNum() == 3 && cell.getColumnIndex() > 2) {
					variablesMap.put(cell.getColumnIndex(), cell.getStringCellValue());
				}
				
				if(row.getRowNum() > 3 && cell.getColumnIndex() == 1) {
					document = cell.getStringCellValue();
				}
				
				if(row.getRowNum() > 3 && cell.getColumnIndex() == 3) {
					contractId = cell.getCellTypeEnum().equals(CellType.STRING) ? Integer.parseInt(cell.getStringCellValue()) : (int) cell.getNumericCellValue();
				}
				
				if(row.getRowNum() > 3 && cell.getColumnIndex() > 3) {
					
					if(((cell.getCellTypeEnum().equals(CellType.STRING) && AonStringUtils.isNotBlank(cell.getStringCellValue())) || 
					(cell.getCellTypeEnum().equals(CellType.NUMERIC) && 0 != cell.getNumericCellValue()))) {
						addContractDatasExcel(
								contractDatasExcel, 
								contractId,
								new ContractDataExcel()
									.setDate(date)
									.setDocument(document)
									.setName(variablesMap.get(cell.getColumnIndex()))
									.setExpression(cell.getCellTypeEnum().equals(CellType.STRING) ? cell.getStringCellValue() : Double.toString(cell.getNumericCellValue()))
						);
						totalContractDatas++;
					} else {
						addContractDatasExcel(
								deleteContractDatasExcel, 
								contractId,
								new ContractDataExcel()
									.setDate(date)
									.setDocument(document)
									.setName(variablesMap.get(cell.getColumnIndex()))
									.setExpression(cell.getCellTypeEnum().equals(CellType.STRING) ? cell.getStringCellValue() : Double.toString(cell.getNumericCellValue()))
						);
					}
				}
				
			}
		}
		
		wb.close();
		
		if(null == date) return messages;
		
		insertContractDatas(aonContext, contractDatasExcel, date, messages);
		deleteContractDatas(aonContext, deleteContractDatasExcel, date, messages);
		
		return messages;
	}

	private static void addContractDatasExcel(Map<Integer, List<ContractDataExcel>> contractDatasExcel, Integer contractId, ContractDataExcel contractDataExcel) {
		List<ContractDataExcel> docContractDatasExcel = contractDatasExcel.get(contractId);
		if(null == docContractDatasExcel) {
			List<ContractDataExcel> list = new ArrayList<>();
			list.add(contractDataExcel);
			contractDatasExcel.put(contractId, list);
		} else {
			docContractDatasExcel.add(contractDataExcel);
			contractDatasExcel.put(contractId, docContractDatasExcel);
		}
		
	}
	
	private static Integer insertContractDatas(CloseableAONContext aonContext, Map<Integer, List<ContractDataExcel>> contractDatasExcel, Date date, Map<String, List<String>> messages) {
		Integer inserts = 0;
		
		for(Entry<Integer, List<ContractDataExcel>> entry : contractDatasExcel.entrySet()) {
			Integer contractId = entry.getKey();
			
			for(ContractDataExcel contractDataExcel : entry.getValue()) {
				ContractDataRecord contractData = aonContext.getDslContext().selectFrom(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.eq(contractDataExcel.getName()))
					.and(CONTRACT_DATA.EXPRESSION.eq(contractDataExcel.getExpression()))
					.and(CONTRACT_DATA.START_DATE.le(parseDateSql(date)))
					.and(CONTRACT_DATA.END_DATE.ge(parseDateSql(date)).or(CONTRACT_DATA.END_DATE.isNull()))
					.fetchOne();
				
				if(null == contractData) {
					if(!createIntersection(aonContext, contractId, date, contractDataExcel, messages)) {
						 Integer newId = aonContext.getDslContext().insertInto(CONTRACT_DATA)
						 	.set(CONTRACT_DATA.DOMAIN, aonContext.getDomainId())
						 	.set(CONTRACT_DATA.CONTRACT, contractId)
						 	.set(CONTRACT_DATA.NAME, contractDataExcel.getName())
						 	.set(CONTRACT_DATA.EXPRESSION, contractDataExcel.getExpression())
						 	.set(CONTRACT_DATA.START_DATE, parseDateSql(AonDateUtils.getMonthFirstDay(date)))
						 	.set(CONTRACT_DATA.END_DATE, parseDateSql(AonDateUtils.getMonthLastDay(date)))
						 	.returning(CONTRACT_DATA.ID)
						 	.fetchOne(CONTRACT_DATA.ID);
					
						 messages.get("success").add("Añadida la variable " + contractDataExcel.getName() + "(" + newId + ")" + ", con valor " + contractDataExcel.getExpression() + ", para el periodo " + dateFormat.format(AonDateUtils.getMonthFirstDay(date)) + " - " + dateFormat.format(AonDateUtils.getMonthLastDay(date)));
					}
					
					inserts++;
				} 
			}
		}
		
		return inserts;
	}

	private static boolean createIntersection(CloseableAONContext aonContext, Integer contractId, Date date, ContractDataExcel contractDataExcel, Map<String, List<String>> messages) {
		Date firstMonthDay = AonDateUtils.getMonthFirstDay(date);
		Date lastMonthDay = AonDateUtils.getMonthLastDay(date);
		
		ContractDataRecord contractData = aonContext.getDslContext().selectFrom(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(contractDataExcel.getName()))
				.and(CONTRACT_DATA.START_DATE.le(parseDateSql(date)))
				.and(CONTRACT_DATA.END_DATE.ge(parseDateSql(date)).or(CONTRACT_DATA.END_DATE.isNull()))
				.fetchOne();
		
		if(null == contractData)
			return false;
		
		if(contractData.getStartDate().equals(parseDateSql(firstMonthDay)) && (contractData.getEndDate() != null && contractData.getEndDate().equals(parseDateSql(lastMonthDay)))) {
			aonContext.getDslContext().update(CONTRACT_DATA)
				.set(CONTRACT_DATA.EXPRESSION, contractDataExcel.getExpression())
				.where(CONTRACT_DATA.ID.eq(contractData.getId()))
				.execute();
			
			messages.get("success").add("Actualizada la variable " + contractData.getName() + "(" + contractData.getId() + ")" + ", con valor " + contractDataExcel.getExpression() + ", para el periodo " + dateFormat.format(contractData.getStartDate()) + " - " + (null == contractData.getEndDate() ? "NULL" : dateFormat.format(contractData.getEndDate())));
			
		} else {
			Integer newId = 0;
			if(contractData.getStartDate().before(parseDateSql(AonDateUtils.addDays(firstMonthDay, -1))) || contractData.getStartDate().equals(parseDateSql(AonDateUtils.addDays(firstMonthDay, -1)))) {
				newId = aonContext.getDslContext().insertInto(CONTRACT_DATA)
				 	.set(CONTRACT_DATA.DOMAIN, aonContext.getDomainId())
				 	.set(CONTRACT_DATA.CONTRACT, contractId)
				 	.set(CONTRACT_DATA.NAME, contractData.getName())
				 	.set(CONTRACT_DATA.EXPRESSION, contractData.getExpression())
				 	.set(CONTRACT_DATA.START_DATE, contractData.getStartDate())
				 	.set(CONTRACT_DATA.END_DATE, parseDateSql(AonDateUtils.addDays(firstMonthDay, -1)))
				 	.returning(CONTRACT_DATA.ID)
				 	.fetchOne(CONTRACT_DATA.ID);
				
				messages.get("success").add("(Intersecci\u00f3n) Añadida la variable " + contractData.getName() + "(" + newId + ")" + ", con valor " + contractData.getExpression() + ", para el periodo " + dateFormat.format(contractData.getStartDate()) + " - " + dateFormat.format(AonDateUtils.addDays(firstMonthDay, -1)));
			}
			
			if(AonStringUtils.isNotBlank(contractDataExcel.getExpression())) {
				newId = aonContext.getDslContext().insertInto(CONTRACT_DATA)
				 	.set(CONTRACT_DATA.DOMAIN, aonContext.getDomainId())
				 	.set(CONTRACT_DATA.CONTRACT, contractId)
				 	.set(CONTRACT_DATA.NAME, contractDataExcel.getName())
				 	.set(CONTRACT_DATA.EXPRESSION, contractDataExcel.getExpression())
				 	.set(CONTRACT_DATA.START_DATE, parseDateSql(firstMonthDay))
				 	.set(CONTRACT_DATA.END_DATE, parseDateSql(lastMonthDay))
				 	.returning(CONTRACT_DATA.ID)
				 	.fetchOne(CONTRACT_DATA.ID);
				
				messages.get("success").add("(Intersecci\u00f3n) Añadida la variable " + contractDataExcel.getName() + "(" + newId + ")" + ", con valor " + contractDataExcel.getExpression() + ", para el periodo " + dateFormat.format(firstMonthDay) + " - " + dateFormat.format(lastMonthDay));
			}
			
			newId = aonContext.getDslContext().insertInto(CONTRACT_DATA)
			 	.set(CONTRACT_DATA.DOMAIN, aonContext.getDomainId())
			 	.set(CONTRACT_DATA.CONTRACT, contractId)
			 	.set(CONTRACT_DATA.NAME, contractData.getName())
			 	.set(CONTRACT_DATA.EXPRESSION, contractData.getExpression())
			 	.set(CONTRACT_DATA.START_DATE, parseDateSql(AonDateUtils.addDays(lastMonthDay, 1)))
			 	.set(CONTRACT_DATA.END_DATE, contractData.getEndDate())
			 	.returning(CONTRACT_DATA.ID)
			 	.fetchOne(CONTRACT_DATA.ID);
			
			messages.get("success").add("(Intersecci\u00f3n) Añadida la variable " + contractData.getName() + "(" + newId + ")" + ", con valor " + contractData.getExpression() + ", para el periodo " + dateFormat.format(AonDateUtils.addDays(lastMonthDay, 1)) + " - " + (null == contractData.getEndDate() ? "NULL" : dateFormat.format(contractData.getEndDate())));
			
			aonContext.getDslContext().delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.ID.eq(contractData.getId()))
				.execute();
			
			messages.get("success").add("(Intersecci\u00f3n) Eliminada la variable " + contractData.getName() + "(" + contractData.getId()  + ")" + ", con valor " + contractData.getExpression() + ", para el periodo " + dateFormat.format(contractData.getStartDate()) + " - " + (contractData.getEndDate() == null ? "NULL" : dateFormat.format(contractData.getEndDate())));
		}
		 
		return true;
	}
	
	private static Integer deleteContractDatas(CloseableAONContext aonContext, Map<Integer, List<ContractDataExcel>> contractDatasExcel, Date date, Map<String, List<String>> messages) {
		Integer deletes = 0;
		
		for(Entry<Integer, List<ContractDataExcel>> entry : contractDatasExcel.entrySet()) {
			Integer contractId = entry.getKey();
			
			for(ContractDataExcel contractDataExcel : entry.getValue()) {
				
				Result<ContractDataRecord> contractDatas = aonContext.getDslContext().selectFrom(CONTRACT_DATA)
				 	.where(CONTRACT_DATA.DOMAIN.eq(aonContext.getDomainId()))
				 	.and(CONTRACT_DATA.CONTRACT.eq(contractId))
				 	.and(CONTRACT_DATA.NAME.eq(contractDataExcel.getName()))
				 	.and(CONTRACT_DATA.START_DATE.le(parseDateSql(date)))
					.and(CONTRACT_DATA.END_DATE.ge(parseDateSql(date)).or(CONTRACT_DATA.END_DATE.isNull()))
				 	.fetch(); 
				
				List<Integer> deleteIds = contractDatas.stream().map(contractData -> contractData.getId()).collect(Collectors.toList());
				if(!deleteIds.isEmpty()) {
					if(!deleteIntersection(aonContext, contractId, date, contractDataExcel, messages)) {
						deletes += aonContext.getDslContext().delete(CONTRACT_DATA)
							.where(CONTRACT_DATA.ID.in(deleteIds))
							.execute();
						
						contractDatas.forEach(contractData -> messages.get("success").add("Eliminada la variable " + contractData.getName() + "(" + contractData.getId()  + ")" + ", con valor " + contractData.getExpression() + ", para el periodo " + dateFormat.format(contractData.getStartDate()) + " - " + (contractData.getEndDate() == null ? "NULL" : dateFormat.format(contractData.getEndDate()))) );	
					}
				}
			}
		}
		
		return deletes;
	}
	
	private static boolean deleteIntersection(CloseableAONContext aonContext, Integer contractId, Date date, ContractDataExcel contractDataExcel, Map<String, List<String>> messages) {
		Date firstMonthDay = AonDateUtils.getMonthFirstDay(date);
		Date lastMonthDay = AonDateUtils.getMonthLastDay(date);
		
		ContractDataRecord contractData = aonContext.getDslContext().selectFrom(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(contractDataExcel.getName()))
				.and(CONTRACT_DATA.START_DATE.le(parseDateSql(date)))
				.and(CONTRACT_DATA.END_DATE.ge(parseDateSql(date)).or(CONTRACT_DATA.END_DATE.isNull()))
				.fetchOne();
		
		if(null == contractData)
			return false;
		
		if(contractData.getStartDate().equals(parseDateSql(firstMonthDay)) && (contractData.getEndDate() != null && contractData.getEndDate().equals(parseDateSql(lastMonthDay)))) {
			aonContext.getDslContext().delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.ID.eq(contractData.getId()))
			.execute();
		
			messages.get("success").add("Eliminada la variable " + contractData.getName() + "(" + contractData.getId()  + ")" + ", con valor " + contractData.getExpression() + ", para el periodo " + dateFormat.format(contractData.getStartDate()) + " - " + (contractData.getEndDate() == null ? "NULL" : dateFormat.format(contractData.getEndDate())));
		} else {
			Integer newId = 0;
			if(contractData.getStartDate().before(parseDateSql(AonDateUtils.addDays(firstMonthDay, -1))) || contractData.getStartDate().equals(parseDateSql(AonDateUtils.addDays(firstMonthDay, -1)))) {
				newId = aonContext.getDslContext().insertInto(CONTRACT_DATA)
				 	.set(CONTRACT_DATA.DOMAIN, aonContext.getDomainId())
				 	.set(CONTRACT_DATA.CONTRACT, contractId)
				 	.set(CONTRACT_DATA.NAME, contractData.getName())
				 	.set(CONTRACT_DATA.EXPRESSION, contractData.getExpression())
				 	.set(CONTRACT_DATA.START_DATE, contractData.getStartDate())
				 	.set(CONTRACT_DATA.END_DATE, parseDateSql(AonDateUtils.addDays(firstMonthDay, -1)))
				 	.returning(CONTRACT_DATA.ID)
				 	.fetchOne(CONTRACT_DATA.ID);
				
				messages.get("success").add("(Borrado Intersecci\u00f3n) Añadida la variable " + contractData.getName() + "(" + newId + ")" + ", con valor " + contractData.getExpression() + ", para el periodo " + dateFormat.format(contractData.getStartDate()) + " - " + dateFormat.format(AonDateUtils.addDays(firstMonthDay, -1)));
			}
			
			newId = aonContext.getDslContext().insertInto(CONTRACT_DATA)
			 	.set(CONTRACT_DATA.DOMAIN, aonContext.getDomainId())
			 	.set(CONTRACT_DATA.CONTRACT, contractId)
			 	.set(CONTRACT_DATA.NAME, contractData.getName())
			 	.set(CONTRACT_DATA.EXPRESSION, contractData.getExpression())
			 	.set(CONTRACT_DATA.START_DATE, parseDateSql(AonDateUtils.addDays(lastMonthDay, 1)))
			 	.set(CONTRACT_DATA.END_DATE, contractData.getEndDate())
			 	.returning(CONTRACT_DATA.ID)
			 	.fetchOne(CONTRACT_DATA.ID);
			
			messages.get("success").add("(Borrado Intersecci\u00f3n) Añadida la variable " + contractData.getName() + "(" + newId + ")" + ", con valor " + contractData.getExpression() + ", para el periodo " + dateFormat.format(AonDateUtils.addDays(lastMonthDay, 1)) + " - " + (null == contractData.getEndDate() ? "NULL" : dateFormat.format(contractData.getEndDate())));
			
			aonContext.getDslContext().delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.ID.eq(contractData.getId()))
				.execute();
			
			messages.get("success").add("(Borrado Intersecci\u00f3n) Eliminada la variable " + contractData.getName() + "(" + contractData.getId()  + ")" + ", con valor " + contractData.getExpression() + ", para el periodo " + dateFormat.format(contractData.getStartDate()) + " - " + (contractData.getEndDate() == null ? "NULL" : dateFormat.format(contractData.getEndDate())));
		}
		 
		return true;
	}
	
}
