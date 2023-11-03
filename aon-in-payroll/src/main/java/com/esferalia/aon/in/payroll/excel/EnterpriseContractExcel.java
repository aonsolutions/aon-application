package com.esferalia.aon.in.payroll.excel;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.watson.util.AonStringUtils;

public class EnterpriseContractExcel {

	private static final LinkedHashMap<String, String> DEFAULT_HEADER;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	static {
		//-------------------------COMPLETE-------------------------
		
			DEFAULT_HEADER = new LinkedHashMap<>();
			DEFAULT_HEADER.put("employee", "NOMBRE");
			DEFAULT_HEADER.put("document", "DOCUMENTO");
			DEFAULT_HEADER.put("ssNum", "N\u00BA SS");
			DEFAULT_HEADER.put("contractType", "TIPO CONTRATO");
			DEFAULT_HEADER.put("workplace", "CENTRO TRABAJO");
			
			DEFAULT_HEADER.put("category", "CATEGOR\u00CDA");
			
			DEFAULT_HEADER.put("start", "FECHA INICIO");
			DEFAULT_HEADER.put("end", "FECHA FIN");
			
		//---------------------------------------------------------
		
	}
	
	public static void simpleEnterpriseContractGenerator (String domainName, String user, Integer domainId, Boolean inactive, Integer workplaceId, String employee, OutputStream outputStream) {
		
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, user)) {
			
			List<EnterpriseContract> contracts = getEnterpriseContracts(aonContext, domainId, inactive, workplaceId, employee);
			contracts.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
			
			String enterpriseName = getEnterpriseName(aonContext, domainId);
			
			write(outputStream
					, contracts
					, enterpriseName);	
		} catch (IOException e) {
			// Nothing to do here
		}
	}

	public static void write(OutputStream outputStream, Collection<EnterpriseContract> contracts, String enterpriseName) throws IOException {
		
		Workbook wb = new XSSFWorkbook();

		DataFormat format = wb.createDataFormat();

		Map<ContractCellStyle, CellStyle> stylesMap = ContractCellStyle.getStyles(wb, format);
		
		Sheet sheet = wb.createSheet(WorkbookUtil.createSafeSheetName(enterpriseName));
		
		configSheetWidth(sheet);
		createHeader(stylesMap, sheet);
		
		for(EnterpriseContract enterpriseContract : contracts) {
			Row row = sheet.createRow(sheet.getLastRowNum() + 1);
			writeEmployee(stylesMap, row, 0, enterpriseContract);
			writeNaf(stylesMap, row, 1, enterpriseContract);
			writeSS(stylesMap, row, 2, enterpriseContract);
			writeContractType(stylesMap, row, 3, enterpriseContract);
			writeworkplace(stylesMap, row, 4, enterpriseContract);
			writeCategory(stylesMap, row, 5, enterpriseContract);
			writeContractStart(stylesMap, row, 6, enterpriseContract);
			writeContractEnd(stylesMap, row, 7, enterpriseContract);
		}
		
		wb.write(outputStream);
		outputStream.close();
		wb.close();
		
	}
	
	private static void configSheetWidth(Sheet sheet) {
		sheet.setColumnWidth(0, 40 * 256);
		sheet.setColumnWidth(1, 15 * 256);
		sheet.setColumnWidth(2, 20 * 256);
		sheet.setColumnWidth(3, 15 * 256);
		sheet.setColumnWidth(4, 20 * 256);
		sheet.setColumnWidth(5, 45 * 256);
		sheet.setColumnWidth(6, 15 * 256);
		sheet.setColumnWidth(7, 15 * 256);
	}

	private static void createHeader(Map<ContractCellStyle, CellStyle> stylesMap, Sheet sheet) {
		Row row = sheet.createRow(1);
		int col=0;
		for(String header : DEFAULT_HEADER.values()) {
			Cell cell = row.createCell(col);
			cell.setCellValue(header);
			cell.setCellStyle(stylesMap.get(ContractCellStyle.HEADER_CELL_STYLE));
			col++;
		}
	}

	private enum ContractCellStyle {
		HEADER_CELL_STYLE,
		STRING_CELL_STYLE,
		BLANK_DIFF_CELL_STYLE,
		RED_STRING_CELL_STYLE,
		DOUBLE_CELL_STYLE,
		RED_DOUBLE_CELL_STYLE,
		ORANGE_DOUBLE_CELL_STYLE,
		GREEN_DOUBLE_CELL_STYLE,
		RED_DOUBLE_CELL_STYLE_NO_BORDERS,
		ORANGE_DOUBLE_CELL_STYLE_NO_BORDERS,
		GREEN_DOUBLE_CELL_STYLE_NO_BORDERS,
		IMPORTANT_CELL_STYLE,
		RED_IMPORTANT_CELL_STYLE,
		ORANGE_IMPORTANT_CELL_STYLE,
		GREEN_IMPORTANT_CELL_STYLE,
		RED_IMPORTANT_CELL_STYLE_NO_BORDERS,
		ORANGE_IMPORTANT_CELL_STYLE_NO_BORDERS,
		GREEN_IMPORTANT_CELL_STYLE_NO_BORDERS,
		IMPORTANT_TOTAL_CELL_STYLE,
		RED_IMPORTANT_TOTAL_CELL_STYLE,
		GREEN_IMPORTANT_TOTAL_CELL_STYLE,
		ORANGE_IMPORTANT_TOTAL_CELL_STYLE,
		BOUND_CELL_STYLE_PREV,
		BOUND_CELL_STYLE_PREV_GREEN,		
		BOUND_CELL_STYLE_PREV_ORANGE,		
		BOUND_CELL_STYLE_PREV_RED,		
		FORMULA_CELL_STYLE,
		RED_FORMULA_CELL_STYLE,
		GREEN_FORMULA_CELL_STYLE,
		ORANGE_FORMULA_CELL_STYLE,		
		JOINT_CELL_STYLE;
		
		private static final String DATA_FORMAT = "#,###,##0.#0";

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
			
			CellStyle stringCellStyle = wb.createCellStyle();
//			stringCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
//			stringCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			stringCellStyle.setAlignment(HorizontalAlignment.CENTER);
			stringCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			stringCellStyle.setBorderBottom(BorderStyle.THIN);
			stringCellStyle.setBorderTop(BorderStyle.THIN);
			stylesMap.put(ContractCellStyle.STRING_CELL_STYLE, stringCellStyle);
			
			CellStyle redStringCellStyle = wb.createCellStyle();
			redStringCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			redStringCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			redStringCellStyle.setBorderBottom(BorderStyle.THIN);
			redStringCellStyle.setBorderTop(BorderStyle.THIN);
			redStringCellStyle.setFont(wrongFont);
			stylesMap.put(ContractCellStyle.RED_STRING_CELL_STYLE, redStringCellStyle);
			
			CellStyle blankDiffCellStyle = wb.createCellStyle();
			blankDiffCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			blankDiffCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			stylesMap.put(ContractCellStyle.BLANK_DIFF_CELL_STYLE, blankDiffCellStyle);
			
			CellStyle doubleCellStyle = wb.createCellStyle();
			doubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			doubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			doubleCellStyle.setBorderBottom(BorderStyle.THIN);
			doubleCellStyle.setBorderTop(BorderStyle.THIN);
			doubleCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.DOUBLE_CELL_STYLE, doubleCellStyle);
			
			CellStyle redDoubleCellStyle = wb.createCellStyle();
			redDoubleCellStyle.setFont(wrongFont);
			redDoubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			redDoubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			redDoubleCellStyle.setBorderBottom(BorderStyle.THIN);
			redDoubleCellStyle.setBorderTop(BorderStyle.THIN);
			redDoubleCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.RED_DOUBLE_CELL_STYLE, redDoubleCellStyle);
			
			CellStyle orangeDoubleCellStyle = wb.createCellStyle();
			orangeDoubleCellStyle.setFont(warningFont);
			orangeDoubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			orangeDoubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			orangeDoubleCellStyle.setBorderBottom(BorderStyle.THIN);
			orangeDoubleCellStyle.setBorderTop(BorderStyle.THIN);
			orangeDoubleCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.ORANGE_DOUBLE_CELL_STYLE, orangeDoubleCellStyle);
			
			CellStyle greenDoubleCellStyle = wb.createCellStyle();
			greenDoubleCellStyle.setFont(okFont);
			greenDoubleCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			greenDoubleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			greenDoubleCellStyle.setBorderBottom(BorderStyle.THIN);
			greenDoubleCellStyle.setBorderTop(BorderStyle.THIN);
			greenDoubleCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.GREEN_DOUBLE_CELL_STYLE, greenDoubleCellStyle);
			
			CellStyle redDoubleCellStyleNoBorders = wb.createCellStyle();
			redDoubleCellStyleNoBorders.setFont(wrongFont);
			redDoubleCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			redDoubleCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
			redDoubleCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.RED_DOUBLE_CELL_STYLE_NO_BORDERS, redDoubleCellStyleNoBorders);
			
			
			CellStyle greenDoubleCellStyleNoBorders = wb.createCellStyle();
			greenDoubleCellStyleNoBorders.setFont(okFont);
			greenDoubleCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			greenDoubleCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
			greenDoubleCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.GREEN_DOUBLE_CELL_STYLE_NO_BORDERS, greenDoubleCellStyleNoBorders);
			
			
			CellStyle orangeDoubleCellStyleNoBorders = wb.createCellStyle();
			orangeDoubleCellStyleNoBorders.setFont(warningFont);
			orangeDoubleCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			orangeDoubleCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
			orangeDoubleCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.ORANGE_DOUBLE_CELL_STYLE_NO_BORDERS, orangeDoubleCellStyleNoBorders);
			
			CellStyle importantCellStyle = wb.createCellStyle();
			importantCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			importantCellStyle.setFont(headerFont);
			importantCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			importantCellStyle.setBorderBottom(BorderStyle.THIN);
			importantCellStyle.setBorderTop(BorderStyle.THIN);
			importantCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.IMPORTANT_CELL_STYLE, importantCellStyle);
			
			CellStyle redImportantCellStyle = wb.createCellStyle();
			redImportantCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			redImportantCellStyle.setFont(headerRedFont);
			redImportantCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			redImportantCellStyle.setBorderBottom(BorderStyle.THIN);
			redImportantCellStyle.setBorderTop(BorderStyle.THIN);
			redImportantCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.RED_IMPORTANT_CELL_STYLE, redImportantCellStyle);
			
			CellStyle orangeImportantCellStyle = wb.createCellStyle();
			orangeImportantCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			orangeImportantCellStyle.setFont(headerOrangeFont);
			orangeImportantCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			orangeImportantCellStyle.setBorderBottom(BorderStyle.THIN);
			orangeImportantCellStyle.setBorderTop(BorderStyle.THIN);
			orangeImportantCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.ORANGE_IMPORTANT_CELL_STYLE, orangeImportantCellStyle);
			
			CellStyle greenImportantCellStyle = wb.createCellStyle();
			greenImportantCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			greenImportantCellStyle.setFont(headerGreenFont);
			greenImportantCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			greenImportantCellStyle.setBorderBottom(BorderStyle.THIN);
			greenImportantCellStyle.setBorderTop(BorderStyle.THIN);
			greenImportantCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.GREEN_IMPORTANT_CELL_STYLE, greenImportantCellStyle);
			
			CellStyle greenImportantCellStyleNoBorders = wb.createCellStyle();
			greenImportantCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			greenImportantCellStyleNoBorders.setFont(headerGreenFont);
			greenImportantCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
			greenImportantCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.GREEN_IMPORTANT_CELL_STYLE_NO_BORDERS, greenImportantCellStyleNoBorders);
			
			CellStyle redImportantCellStyleNoBorders = wb.createCellStyle();
			redImportantCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			redImportantCellStyleNoBorders.setFont(headerRedFont);
			redImportantCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
			redImportantCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.RED_IMPORTANT_CELL_STYLE_NO_BORDERS, redImportantCellStyleNoBorders);
			
			CellStyle orangeImportantCellStyleNoBorders = wb.createCellStyle();
			orangeImportantCellStyleNoBorders.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			orangeImportantCellStyleNoBorders.setFont(headerOrangeFont);
			orangeImportantCellStyleNoBorders.setFillPattern(FillPatternType.FINE_DOTS);
			orangeImportantCellStyleNoBorders.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.ORANGE_IMPORTANT_CELL_STYLE_NO_BORDERS, orangeImportantCellStyleNoBorders);
			
			CellStyle importantTotalCellStyle = wb.createCellStyle();
			importantTotalCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			importantTotalCellStyle.setFont(headerFont);
			importantTotalCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			importantTotalCellStyle.setBorderBottom(BorderStyle.THIN);
			importantTotalCellStyle.setBorderTop(BorderStyle.THIN);
			importantTotalCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.IMPORTANT_TOTAL_CELL_STYLE, importantTotalCellStyle);
			
			CellStyle redImportantTotalCellStyle = wb.createCellStyle();
			redImportantTotalCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			redImportantTotalCellStyle.setFont(headerRedFont);
			redImportantTotalCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			redImportantTotalCellStyle.setBorderBottom(BorderStyle.THIN);
			redImportantTotalCellStyle.setBorderTop(BorderStyle.THIN);
			redImportantTotalCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.RED_IMPORTANT_TOTAL_CELL_STYLE, redImportantTotalCellStyle);
			
			CellStyle greenImportantTotalCellStyle = wb.createCellStyle();
			greenImportantTotalCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			greenImportantTotalCellStyle.setFont(headerGreenFont);
			greenImportantTotalCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			greenImportantTotalCellStyle.setBorderBottom(BorderStyle.THIN);
			greenImportantTotalCellStyle.setBorderTop(BorderStyle.THIN);
			greenImportantTotalCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.GREEN_IMPORTANT_TOTAL_CELL_STYLE, greenImportantTotalCellStyle);
			
			CellStyle orangeImportantTotalCellStyle = wb.createCellStyle();
			orangeImportantTotalCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			orangeImportantTotalCellStyle.setFont(headerOrangeFont);
			orangeImportantTotalCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			orangeImportantTotalCellStyle.setBorderBottom(BorderStyle.THIN);
			orangeImportantTotalCellStyle.setBorderTop(BorderStyle.THIN);
			orangeImportantTotalCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.ORANGE_IMPORTANT_TOTAL_CELL_STYLE, orangeImportantTotalCellStyle);

			CellStyle boundCellStylePrev = wb.createCellStyle();
			boundCellStylePrev.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			boundCellStylePrev.setFillPattern(FillPatternType.FINE_DOTS);
			boundCellStylePrev.setBorderBottom(BorderStyle.THIN);
			boundCellStylePrev.setBorderTop(BorderStyle.THIN);
			boundCellStylePrev.setBorderRight(BorderStyle.THIN);
			stylesMap.put(ContractCellStyle.BOUND_CELL_STYLE_PREV, boundCellStylePrev);

			CellStyle boundCellStylePrevGreen = wb.createCellStyle();
			boundCellStylePrevGreen.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			boundCellStylePrevGreen.setFillPattern(FillPatternType.FINE_DOTS);
			boundCellStylePrevGreen.setBorderBottom(BorderStyle.THIN);
			boundCellStylePrevGreen.setBorderTop(BorderStyle.THIN);
			boundCellStylePrevGreen.setBorderRight(BorderStyle.THIN);
			stylesMap.put(ContractCellStyle.BOUND_CELL_STYLE_PREV_GREEN, boundCellStylePrevGreen);		
			
			CellStyle boundCellStylePrevOrange = wb.createCellStyle();
			boundCellStylePrevOrange.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			boundCellStylePrevOrange.setFillPattern(FillPatternType.FINE_DOTS);
			boundCellStylePrevOrange.setBorderBottom(BorderStyle.THIN);
			boundCellStylePrevOrange.setBorderTop(BorderStyle.THIN);
			boundCellStylePrevOrange.setBorderRight(BorderStyle.THIN);
			stylesMap.put(ContractCellStyle.BOUND_CELL_STYLE_PREV_ORANGE, boundCellStylePrevOrange);		
			
			CellStyle boundCellStylePrevRed = wb.createCellStyle();
			boundCellStylePrevRed.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			boundCellStylePrevRed.setFillPattern(FillPatternType.FINE_DOTS);
			boundCellStylePrevRed.setBorderBottom(BorderStyle.THIN);
			boundCellStylePrevRed.setBorderTop(BorderStyle.THIN);
			boundCellStylePrevRed.setBorderRight(BorderStyle.THIN);
			stylesMap.put(ContractCellStyle.BOUND_CELL_STYLE_PREV_RED, boundCellStylePrevRed);		

			CellStyle formulaCellStyle = wb.createCellStyle();
			formulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			formulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			formulaCellStyle.setBorderBottom(BorderStyle.THIN);
			formulaCellStyle.setBorderTop(BorderStyle.THIN);
			formulaCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.FORMULA_CELL_STYLE, formulaCellStyle);
			
			CellStyle redFormulaCellStyle = wb.createCellStyle();
			redFormulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			redFormulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			redFormulaCellStyle.setBorderBottom(BorderStyle.THIN);
			redFormulaCellStyle.setBorderTop(BorderStyle.THIN);
			redFormulaCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			redFormulaCellStyle.setFont(wrongFont);
			stylesMap.put(ContractCellStyle.RED_FORMULA_CELL_STYLE, redFormulaCellStyle);
			
			CellStyle greenFormulaCellStyle = wb.createCellStyle();
			greenFormulaCellStyle.setFont(okFont);
			greenFormulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			greenFormulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			greenFormulaCellStyle.setBorderBottom(BorderStyle.THIN);
			greenFormulaCellStyle.setBorderTop(BorderStyle.THIN);
			greenFormulaCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			stylesMap.put(ContractCellStyle.GREEN_FORMULA_CELL_STYLE, greenFormulaCellStyle);

			CellStyle yellowFormulaCellStyle = wb.createCellStyle();
			yellowFormulaCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			yellowFormulaCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
			yellowFormulaCellStyle.setBorderBottom(BorderStyle.THIN);
			yellowFormulaCellStyle.setBorderTop(BorderStyle.THIN);
			yellowFormulaCellStyle.setDataFormat(format.getFormat(DATA_FORMAT));
			yellowFormulaCellStyle.setFont(warningFont);
			stylesMap.put(ContractCellStyle.ORANGE_FORMULA_CELL_STYLE, yellowFormulaCellStyle);
			
			CellStyle jointCellStyle = wb.createCellStyle();
			jointCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			jointCellStyle.setBorderLeft(BorderStyle.THIN);
			jointCellStyle.setBorderRight(BorderStyle.THIN);
			stylesMap.put(ContractCellStyle.JOINT_CELL_STYLE, jointCellStyle);
			
			return stylesMap;
		}
	}

	private static void writeEmployee(Map<ContractCellStyle, CellStyle> stylesMap, Row row, int column, EnterpriseContract contract) {
		Cell cell = row.createCell(column);
		cell.setCellValue(contract.getName());
		cell.setCellStyle(stylesMap.get(ContractCellStyle.STRING_CELL_STYLE));
	}
	
	private static void writeNaf(Map<ContractCellStyle, CellStyle> stylesMap, Row row, int column, EnterpriseContract contract) {
		Cell cell = row.createCell(column);
		cell.setCellValue(contract.getNaf());
		cell.setCellStyle(stylesMap.get(ContractCellStyle.STRING_CELL_STYLE));
	}
	
	private static void writeSS(Map<ContractCellStyle, CellStyle> stylesMap, Row row, int column, EnterpriseContract contract) {
		Cell cell = row.createCell(column);
		cell.setCellValue(contract.getSS());
		cell.setCellStyle(stylesMap.get(ContractCellStyle.STRING_CELL_STYLE));
	}
	
	private static void writeContractType(Map<ContractCellStyle, CellStyle> stylesMap, Row row, int column, EnterpriseContract contract) {
		Cell cell = row.createCell(column);
		cell.setCellValue(contract.getContractType());
		cell.setCellStyle(stylesMap.get(ContractCellStyle.STRING_CELL_STYLE));
	}
	
	private static void writeworkplace(Map<ContractCellStyle, CellStyle> stylesMap, Row row, int column, EnterpriseContract contract) {
		Cell cell = row.createCell(column);
		cell.setCellValue(contract.getWorkplace());
		cell.setCellStyle(stylesMap.get(ContractCellStyle.STRING_CELL_STYLE));
	}
	
	private static void writeCategory(Map<ContractCellStyle, CellStyle> stylesMap, Row row, int column, EnterpriseContract contract) {
		Cell cell = row.createCell(column);
		cell.setCellValue(contract.getCategory());
		cell.setCellStyle(stylesMap.get(ContractCellStyle.STRING_CELL_STYLE));
	}
	
	private static void writeContractStart(Map<ContractCellStyle, CellStyle> stylesMap, Row row, int column, EnterpriseContract contract) {
		Cell cell = row.createCell(column);
		cell.setCellValue(null == contract.getContractStart() ? null : dateFormat.format(contract.getContractStart()));
		cell.setCellStyle(stylesMap.get(ContractCellStyle.STRING_CELL_STYLE));
	}
	
	private static void writeContractEnd(Map<ContractCellStyle, CellStyle> stylesMap, Row row, int column, EnterpriseContract contract) {
		Cell cell = row.createCell(column);
		cell.setCellValue(null == contract.getContractEnd() ? null : dateFormat.format(contract.getContractEnd()));
		cell.setCellStyle(stylesMap.get(ContractCellStyle.STRING_CELL_STYLE));
	}

	public static List<EnterpriseContract> getEnterpriseContracts(AONContext aonContext, Integer domainId, Boolean inactive, Integer workplaceId, String employee) {
		Date currentDate = new Date();
		java.sql.Date currentDateSQL = new java.sql.Date(currentDate.getTime());
		
		Condition condition = CONTRACT.DOMAIN.eq(domainId);
		condition = condition.and(CONTRACT.ID.gt(0));
		condition = null == workplaceId ? condition : condition.and(CONTRACT.WORKPLACE.eq(workplaceId));
		if(!AonStringUtils.isBlank(employee)) {
			condition = condition.and(
					(PERSON.NAME.contains(employee).or(PERSON.FIRST_SURNAME.contains(employee).or(PERSON.SECOND_SURNAME.contains(employee))))
					.or(REGISTRY.DOCUMENT.contains(employee))
					.or(PERSON.SOCIAL_SECURITY_NUM.contains(employee))
					);
		}
		
		condition = inactive ? condition : CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(currentDateSQL));
		
		Result<Record> contractRecords = aonContext.getDslContext().select().from(CONTRACT)
			.innerJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
			.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
			.innerJoin(REGISTRY).on(CONTRACT.PERSON.eq(REGISTRY.ID))
			.where(condition)
			.fetch();
		
		List<EnterpriseContract> contracts = new ArrayList<>();
		
		for(Record contractRecord : contractRecords) {
			EnterpriseContract enterpriseContract = new EnterpriseContract();
			
			enterpriseContract.setStart(contractRecord.get(CONTRACT.START_DATE));
			enterpriseContract.setEnd(contractRecord.get(CONTRACT.END_DATE));
			
			enterpriseContract.setName(parseName(contractRecord));
			enterpriseContract.setNaf(contractRecord.get(REGISTRY.DOCUMENT));
			enterpriseContract.setSs(contractRecord.get(PERSON.SOCIAL_SECURITY_NUM));
			
			Result<Record> contractTypeRecords = aonContext.getDslContext().select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.NAME.eq("TC2"))
					.and(CONTRACT_DATA.CONTRACT.eq(contractRecord.get(CONTRACT.ID)))
					.orderBy(CONTRACT_DATA.START_DATE.desc()).fetch();
			
			if(contractTypeRecords.isNotEmpty())
				enterpriseContract.setContractType(contractTypeRecords.get(0).get(CONTRACT_DATA.EXPRESSION));
			else
				enterpriseContract.setContractType("RETA");
			
			enterpriseContract.setWorkplace(contractRecord.get(WORKPLACE.DESCRIPTION));
			enterpriseContract.setCategory(contractRecord.get(CONTRACT.CATEGORY_DESCRIPTION));
			
			contracts.add(enterpriseContract);
		}
		
		return contracts;
	}
	
	private static String parseName(Record contractRecord) {
		String fullName = "";
		
		if(AonStringUtils.isNotBlank(contractRecord.get(PERSON.FIRST_SURNAME)))
			fullName += contractRecord.get(PERSON.FIRST_SURNAME);
		
		if(AonStringUtils.isNotBlank(contractRecord.get(PERSON.SECOND_SURNAME)))
			fullName += " " + contractRecord.get(PERSON.SECOND_SURNAME) + ", ";
		else
			fullName += ", ";
		
		fullName += contractRecord.get(PERSON.NAME);
		
		return fullName;
	}

	public static String getEnterpriseName(AONContext aonContext, Integer domainId) {
		Integer enterpriseRegistry = aonContext.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne(ENTERPRISE.REGISTRY);
			
		return AON.getRegistry(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
				r -> r.getIdProperty().eq(enterpriseRegistry)).getName();
	}
	
	public static class EnterpriseContract implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private Date start;
		private Date end;
		
		private String name;
		private String naf;
		private String ss;
		private String contractType;
		private String workplace;
		private String category;
		
		protected EnterpriseContract() {
			super();
		}
		
		public Date getContractStart() {
			return start;
		}
		
		public Date getContractEnd() {
			return end;
		}
		
		public String getName() {
			return name;
		}
		
		public String getNaf() {
			return naf;
		}
		
		public String getSS() {
			return ss;
		}
		
		public String getContractType() {
			return contractType;
		}
		
		public String getWorkplace() {
			return workplace;
		}
		
		public String getCategory() {
			return category;
		}

		public void setStart(Date start) {
			this.start = start;
		}

		public void setEnd(Date end) {
			this.end = end;
		}

		public void setSs(String ss) {
			this.ss = ss;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setNaf(String naf) {
			this.naf = naf;
		}

		public void setContractType(String contractType) {
			if(AonStringUtils.containsIgnoreCase(contractType, "\""))
				contractType = contractType.split("\"")[1];
			this.contractType = contractType;
		}

		public void setWorkplace(String workplace) {
			this.workplace = workplace;
		}

		public void setCategory(String category) {
			this.category = category;
		}

	}
	
}
