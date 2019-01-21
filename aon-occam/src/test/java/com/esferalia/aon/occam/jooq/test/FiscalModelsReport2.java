package com.esferalia.aon.occam.jooq.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.function.Consumer;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.pool.AonConnectionException;

public class FiscalModelsReport2 {
	private static final String NUMBER_PATTERN = "#,###";
	private static final XSSFColor HEADER_COLOR = new XSSFColor(new java.awt.Color(80, 80, 80));

	private static String FILENAME = "/home/ecastellano/TRABAJO/SELECT MODELOS FISCALES/resultado_select_modelos_fiscales.csv";
	private static String OUTPUT_FILENAME = "/home/ecastellano/TRABAJO/SELECT MODELOS FISCALES/resultado.xlsx";

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
	}

	@Test
	public void testMatrix() throws IOException {
// model   year    period  administration  status  name    owner   name
		FileInputStream fis = new FileInputStream(FILENAME);
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(fis));
		InvoiceExcelAction action = new InvoiceExcelAction();
		action.initialize("MODELOS FISCALES");
 
		String line;
		Model model;
		int x = 0;
		while ((line = reader.readLine()) != null) {
			System.out.println( x );
			String[] tokens = AonStringUtils.splitPreserveAllTokens(line, '\t');
			model = new Model()
				.setModel(tokens[0])
				.setYear(AonNumberUtils.toint( tokens[1] ))
				.setPeriod(Period.safeValueOf(AonNumberUtils.toByte(tokens[2])))
				.setAdministration(Administration.safeValueOf(AonNumberUtils.toByte(tokens[3])))
				.setStatus(FiscalStatus.safeValueOf(AonNumberUtils.toByte(tokens[4])))
				.setDomainName(tokens[5])
				.setOwner(tokens[6])
				.setDomainDescription(tokens[7])
				.setCount(1);
			action.accept(model);
			++x;
		}
		FileOutputStream fos = new FileOutputStream(OUTPUT_FILENAME);
		action.finalize(fos);
		fos.flush();
		fos.close();
		fis.close();
	}

	@AfterClass
	public static void afterClass() {
	}

	private class Model {
		private String domainDescription;
		private String domainName;
		private String owner;
		private String model;
		private Period period;
		private Administration administration;
		private FiscalStatus status;
		private int year;
		private int count;

		public String getDomainDescription() {
			return domainDescription;
		}
		public Model  setDomainDescription(String domainDescription) {
			this.domainDescription = domainDescription;
			return this;
		}
		public String getDomainName() {
			return domainName;
		}

		public Model setDomainName(String domainName) {
			this.domainName = domainName;
			return this;
		}

		public String getOwner() {
			return owner;
		}

		public Model setOwner(String owner) {
			this.owner = owner;
			return this;
		}

		public String getModel() {
			return model;
		}

		public Model setModel(String model) {
			this.model = model;
			return this;
		}

		public Period getPeriod() {
			return period;
		}

		public Model setPeriod(Period period) {
			this.period = period;
			return this;
		}

		public Administration getAdministration() {
			return administration;
		}

		public Model setAdministration(Administration administration) {
			if (administration == null) administration = Administration.UNKNOWN;
			this.administration = administration;
			return this;
		}

		public FiscalStatus getStatus() {
			return this.status;
		}

		public Model setStatus(FiscalStatus status) {
			this.status = status;
			return this;
		}
		private int getYear() {
			return year;
		}
		public Model setYear(int year) {
			this.year = year;
			return this;
		}

		public int getCount() {
			return count;
		}

		public Model setCount(int count) {
			this.count = count;
			return this;
		}
	}

	private class InvoiceExcelAction implements Consumer<Model> {

		private SXSSFWorkbook workbook;
		private SXSSFSheet sheet;
		private Row row;
		private int rowCount;
		private int cellCount;
		private DataFormat dataFormat;
		private CellStyle defaultStyle;
		private CellStyle numberStyle;
		private XSSFCellStyle headerCellStyle;
		private Font smallFont;

		public void initialize(String name) {
			initialize(name, true);
		}

		public void initialize(String name, boolean printHeaders) {
			workbook = new SXSSFWorkbook(1);

			sheet = (SXSSFSheet) workbook.createSheet(name);
			dataFormat = workbook.getCreationHelper().createDataFormat();
			rowCount = 0;
			cellCount = 0;

			smallFont = workbook.createFont();
			smallFont.setFontHeightInPoints((short) 8);

			defaultStyle = workbook.createCellStyle();
			defaultStyle.setFont(smallFont);
			defaultStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);

			numberStyle = workbook.createCellStyle();
			numberStyle.setDataFormat(dataFormat.getFormat(NUMBER_PATTERN));
			numberStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			numberStyle.setFont(smallFont);

			Font headerFont = workbook.createFont();
			headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
			headerFont.setFontHeightInPoints((short) 8);
			headerFont.setColor(IndexedColors.WHITE.index);

			headerCellStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			headerCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			headerCellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
			headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			headerCellStyle.setFillForegroundColor(HEADER_COLOR);
			headerCellStyle.setFont(headerFont);
			if (printHeaders) {
				headerRow();
			}
		}

		private Cell addCell(String value) {
			Cell cell = row.createCell(cellCount++);
			cell.setCellValue(AonStringUtils.trimToEmpty(value));
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellStyle(defaultStyle);
			return cell;
		}

		private Cell addCell(Integer value) {
			Cell cell = row.createCell(cellCount++);
			cell.setCellStyle(numberStyle);
			if (value != null) {
				cell.setCellValue(value);
			}
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			return cell;
		}

		public void finalize(OutputStream out) throws IOException {
			workbook.write(out);
			workbook.dispose();
		}

		private void headerRow() {
			row = sheet.createRow(rowCount++);
			cellCount = 0;

			Font orientedHeaderFont = workbook.createFont();
			orientedHeaderFont.setColor(IndexedColors.WHITE.index);

			XSSFCellStyle orientedHeaderCellStyle = (XSSFCellStyle) workbook.createCellStyle();
			orientedHeaderCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			orientedHeaderCellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
			orientedHeaderCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			orientedHeaderCellStyle.setFillForegroundColor(HEADER_COLOR);
			orientedHeaderCellStyle.setRotation((short) 90);
			orientedHeaderCellStyle.setFont(orientedHeaderFont);

			CellUtil.createCell(row, cellCount, "ENTORNO", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 25*256);
			CellUtil.createCell(row, cellCount, "CREADOR DOMINIO", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 20*256);
			CellUtil.createCell(row, cellCount, "MOD", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 5*256);
			CellUtil.createCell(row, cellCount, "ADM", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 5*256);
			CellUtil.createCell(row, cellCount, "A�O", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 5*256);

			for (int i = 5; i < 55; i = (i+3) ) {
				CellUtil.createCell(row, cellCount, "P", headerCellStyle);
				sheet.setColumnWidth(cellCount++, 4*256);
				CellUtil.createCell(row, cellCount, "F", headerCellStyle);
				sheet.setColumnWidth(cellCount++, 4*256);
				sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, (cellCount-2), (cellCount-1)));
			}


//			sheet.addMergedRegion(new CellRangeAddress((rowCount - 1), (rowCount - 1), 0, 3));


			row = sheet.createRow(rowCount++);
			CellUtil.createCell(row, 5, "ENE", headerCellStyle);
			CellUtil.createCell(row, 7, "FEB", headerCellStyle);
			CellUtil.createCell(row, 9, "MAR", headerCellStyle);
			CellUtil.createCell(row, 11, "ABR", headerCellStyle);
			CellUtil.createCell(row, 13, "MAY", headerCellStyle);
			CellUtil.createCell(row, 15, "JUN", headerCellStyle);
			CellUtil.createCell(row, 17, "JUL", headerCellStyle);
			CellUtil.createCell(row, 19, "AGO", headerCellStyle);
			CellUtil.createCell(row, 21, "SEP", headerCellStyle);
			CellUtil.createCell(row, 23, "OCT", headerCellStyle);
			CellUtil.createCell(row, 25, "NOV", headerCellStyle);
			CellUtil.createCell(row, 27, "DIC", headerCellStyle);
			CellUtil.createCell(row, 29, "1�T", headerCellStyle);
			CellUtil.createCell(row, 31, "2�T", headerCellStyle);
			CellUtil.createCell(row, 33, "3�T", headerCellStyle);
			CellUtil.createCell(row, 35, "4�T", headerCellStyle);
			CellUtil.createCell(row, 37, "ANU", headerCellStyle);

		}

		String lastDomain = null;
		String lastModel = null;
		Administration lastAdmon = null;
		int lastYear = -1;

		@Override
		public void accept(Model mod) {
			String description = mod.getDomainDescription();
			String domain = mod.getDomainName();
			String model = mod.getModel();
			int year = mod.getYear();
			Administration admon = mod.getAdministration();
			if (!AonStringUtils.equals(domain, lastDomain)
				|| !AonStringUtils.equals(model, lastModel)
				|| year != lastYear
				|| admon != lastAdmon) {

				lastDomain = domain;
				lastModel = model;
				lastAdmon = admon;
				lastYear = year;
				row = sheet.createRow(rowCount++);
				cellCount = 0;
				addCell(description);
				addCell(mod.getOwner());
				addCell("303".equals(model)?"303 RS":model);
				addCell( AonStringUtils.upperCase( admon == Administration.COMMON_TERRITORY?"AEAT":AonStringUtils.substring(admon.getDescription(),0,4)));
				addCell(mod.getYear());
			}
			int gap = 0;
			if (mod.getStatus() == FiscalStatus.FINISHED || mod.getStatus() == FiscalStatus.SENT) gap = 1;
			cellCount = (mod.getPeriod().ordinal() * 2) + gap + 5;
			Cell cell = row.getCell(cellCount);
			if (cell == null) {
				addCell(mod.getCount());
			} else {
				cell.setCellValue( cell.getNumericCellValue() + mod.getCount() );
			}
		}
	}

}
