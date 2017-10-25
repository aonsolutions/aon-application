package com.esferalia.aon.occam.jooq.test;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsVat.FS_VAT;
import static com.esferalia.aon.jooq.tables.FsVatDeclaration.FS_VAT_DECLARATION;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
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
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record8;
import org.jooq.Select;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.pool.AonConnectionException;

public class FiscalModelsReport {
	private static final String NUMBER_PATTERN = "#,###";
	private static final XSSFColor HEADER_COLOR = new XSSFColor(new java.awt.Color(80, 80, 80));

	private static String URL = "jdbc:mysql://127.0.0.1:3306/pro-aonsolutions-net";
	private static String USER = "root";
	private static String PASSWORD = "password";
	private static DSLContext CTX;

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName(org.gjt.mm.mysql.Driver.class.getName());
		Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		CTX = DSL.using(conn, settings);
	}

	@Test
	public void testMatrix() throws IOException {
		final String ID 	= "ID";
		final String NAME 	= "NAME";
		final String OWNER 	= "OWNER";
		final String MODEL 	= "MODEL";
		final String ADMON 	= "ADMON";
		final String PERIOD = "PERIOD";
		final String STATUS = "STATUS";
		final String COUNT 	= "COUNT";
		Domain PARENT = DOMAIN.as("PARENT");
		InvoiceExcelAction action = new InvoiceExcelAction();
		action.initialize("MODELOS FISCALES");
		Field<Integer> count = DSL.count();
		// Field<Integer> vatCount = DSL.count(FS_VAT_DECLARATION.ID);

		Select<Record8<Integer,String,String,String,Byte,Byte,Byte,Integer>> s1 = 
			CTX.select(PARENT.ID.as(ID)
					, PARENT.NAME.as(NAME)
					, PARENT.OWNER.as(OWNER)
					, FS_MODEL.MODEL.as(MODEL)
					, FS_MODEL.ADMINISTRATION.as(ADMON)
					, FS_MODEL.PERIOD.as(PERIOD)
					, FS_MODEL.STATUS.as(STATUS)
					,count.as(COUNT))
					.from(FS_MODEL)
					.innerJoin(DOMAIN).on(DOMAIN.ID.equal(FS_MODEL.DOMAIN))
					.innerJoin(PARENT).on(PARENT.ID.equal(DOMAIN.PARENT))
					.where(FS_MODEL.YEAR.eq(2017))
					.groupBy(PARENT.ID, FS_MODEL.MODEL, FS_MODEL.ADMINISTRATION, FS_MODEL.PERIOD, FS_MODEL.STATUS);
		Select<Record8<Integer,String,String,String,Byte,Byte,Byte,Integer>> s2 = 
			CTX.select(PARENT.ID.as(ID)
					, PARENT.NAME.as(NAME)
					, PARENT.OWNER.as(OWNER)
					, DSL.inline("303").as(MODEL)
					, FS_VAT_DECLARATION.ADMINISTRATION.as(ADMON)
					, FS_VAT.PERIOD.as(PERIOD)
					, FS_VAT_DECLARATION.STATUS.as(STATUS)
					, count.as(COUNT))
				.from(FS_VAT_DECLARATION)
				.innerJoin(DOMAIN).on(DOMAIN.ID.equal(FS_VAT_DECLARATION.DOMAIN))
				.innerJoin(PARENT).on(PARENT.ID.equal(DOMAIN.PARENT))
				.innerJoin(FS_VAT).on(FS_VAT.ID.equal(FS_VAT_DECLARATION.FS_VAT))
				.where(FS_VAT.YEAR.eq(2017))
				.groupBy(PARENT.ID, DSL.inline("303"), FS_VAT_DECLARATION.ADMINISTRATION, FS_VAT.PERIOD, FS_VAT_DECLARATION.STATUS); 
		
		System.out.println(
				CTX.select().from(s1.union(s2))
				.orderBy(1,4,5,6,7)				
				.getSQL()
				);
		
		
		CTX.select().from(s1.union(s2))
		.orderBy(1,4,5,6,7)
			.stream()
			.map(rec -> new Model()
					.setDomainName((String) rec.getValue(NAME))
					.setOwner((String) rec.getValue(OWNER))
					.setModel((String) rec.getValue(MODEL))
					.setAdministration(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Administration.class, (Byte) rec.getValue(ADMON)))
					.setPeriod(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Period.class,(Byte) rec.getValue(PERIOD)))
					.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,(Byte) rec.getValue(STATUS)))
					.setCount((Integer) rec.getValue(COUNT)))
			.forEach(action);
		String fileName = "Modelos fiscales.xls";
		FileOutputStream fos = new FileOutputStream("/home/ecastellano/FISCAL/" + fileName);
		action.finalize(fos);
		fos.flush();
		fos.close();
	}

	@AfterClass
	public static void afterClass() {
		CTX.close();
	}

	private class Model {
		private String domainName;
		private String owner;
		private String model;
		private Period period;
		private Administration administration;
		private FiscalStatus status;
		private int count;

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
			sheet.setColumnWidth(cellCount++, 4*256);
			CellUtil.createCell(row, cellCount, "ADM", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 5*256);
			
			for (int i = 4; i < 55; i = (i+3) ) {
				CellUtil.createCell(row, cellCount, "P", headerCellStyle);
				sheet.setColumnWidth(cellCount++, 4*256);
				CellUtil.createCell(row, cellCount, "F", headerCellStyle);
				sheet.setColumnWidth(cellCount++, 4*256);
				sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, (cellCount-2), (cellCount-1)));
			}


//			sheet.addMergedRegion(new CellRangeAddress((rowCount - 1), (rowCount - 1), 0, 3));

			
			row = sheet.createRow(rowCount++);
			CellUtil.createCell(row, 4, "ENE", headerCellStyle);
			CellUtil.createCell(row, 6, "FEB", headerCellStyle);
			CellUtil.createCell(row, 8, "MAR", headerCellStyle);
			CellUtil.createCell(row, 10, "ABR", headerCellStyle);
			CellUtil.createCell(row, 12, "MAY", headerCellStyle);
			CellUtil.createCell(row, 14, "JUN", headerCellStyle);
			CellUtil.createCell(row, 16, "JUL", headerCellStyle);
			CellUtil.createCell(row, 18, "AGO", headerCellStyle);
			CellUtil.createCell(row, 20, "SEP", headerCellStyle);
			CellUtil.createCell(row, 22, "OCT", headerCellStyle);
			CellUtil.createCell(row, 24, "NOV", headerCellStyle);
			CellUtil.createCell(row, 26, "DIC", headerCellStyle);
			CellUtil.createCell(row, 28, "1ºT", headerCellStyle);
			CellUtil.createCell(row, 30, "2ºT", headerCellStyle);
			CellUtil.createCell(row, 32, "3ºT", headerCellStyle);
			CellUtil.createCell(row, 34, "4ºT", headerCellStyle);
			CellUtil.createCell(row, 36, "ANU", headerCellStyle);

		}

		String lastDomain = null;
		String lastModel = null;
		Administration lastAdmon = null;

		@Override
		public void accept(Model mod) {
			String domain = mod.getDomainName();
			String model = mod.getModel();
			Administration admon = mod.getAdministration();
			if (!AonStringUtils.equals(domain, lastDomain) || !AonStringUtils.equals(model, lastModel)
					|| admon != lastAdmon) {

				lastDomain = domain;
				lastModel = model;
				lastAdmon = admon;
				row = sheet.createRow(rowCount++);
				cellCount = 0;
				addCell(domain);
				addCell(AonStringUtils.abbreviate(mod.getOwner(),22));
				addCell(model);
				addCell( AonStringUtils.upperCase( admon == Administration.COMMON_TERRITORY?"AEAT":AonStringUtils.substring(admon.getDescription(),0,4)));
			}
			int gap = 0;
			if (mod.getStatus() == FiscalStatus.FINISHED || mod.getStatus() == FiscalStatus.SENT) gap = 1;
			cellCount = (mod.getPeriod().ordinal() * 2) + gap + 4;
			Cell cell = row.getCell(cellCount);
			if (cell == null) {
				addCell(mod.getCount());
			} else {
				cell.setCellValue( cell.getNumericCellValue() + mod.getCount() ); 
			}
		}
	}

}
