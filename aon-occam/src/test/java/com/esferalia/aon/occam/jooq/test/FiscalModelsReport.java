package com.esferalia.aon.occam.jooq.test;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModel390.FS_MODEL390;
import static com.esferalia.aon.jooq.tables.FsModel180.FS_MODEL180;
import static com.esferalia.aon.jooq.tables.FsModel184.FS_MODEL184;
import static com.esferalia.aon.jooq.tables.FsModel190.FS_MODEL190;
import static com.esferalia.aon.jooq.tables.FsModel193.FS_MODEL193;
import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;
import static com.esferalia.aon.jooq.tables.FsMod347.FS_MOD347;
import static com.esferalia.aon.jooq.tables.FsMod349.FS_MOD349;
import static com.esferalia.aon.jooq.tables.FsVat.FS_VAT;
import static com.esferalia.aon.jooq.tables.FsVatDeclaration.FS_VAT_DECLARATION;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Consumer;

import java.util.Properties;
import java.util.TimeZone;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
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
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.jooq.DSLContext;
import org.jooq.Field;
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
	private static String TIMEZONE = "Europe/Madrid";
	private static DSLContext CTX;

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName(com.mysql.jdbc.Driver.class.getName());

		Properties properties = new Properties();
		properties.setProperty("user", USER);
		properties.setProperty("password", PASSWORD);
		properties.setProperty("serverTimezone", TIMEZONE);
		Connection conn = DriverManager.getConnection(URL, properties);
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		CTX = DSL.using(conn, settings);
	}

	@Test
	public void testMatrix() throws IOException {
		final String ID 	= "ID";
		final String DESCRIPTION 	= "DESCRIPTION";
		final String NAME 	= "NAME";
		final String OWNER 	= "OWNER";
		final String MODEL 	= "MODEL";
		final String ADMON 	= "ADMON";
		final String YEAR = "YEAR";
		final String PERIOD = "PERIOD";
		final String STATUS = "STATUS";
		final String COUNT 	= "COUNT";
		Field<Byte> YEAR_PERIOD = DSL.inline( (byte) 16 ).as(PERIOD);

		Domain PARENT = DOMAIN.as("PARENT");
		InvoiceExcelAction action = new InvoiceExcelAction();
		action.initialize("MODELOS FISCALES");
		Field<Integer> count = DSL.count();
		// Field<Integer> vatCount = DSL.count(FS_VAT_DECLARATION.ID);

		CTX.select().from(
			CTX.select(PARENT.ID.as(ID)
					, PARENT.DESCRIPTION.as(DESCRIPTION)
					, PARENT.NAME.as(NAME)
					, PARENT.OWNER.as(OWNER)
					, FS_MODEL.MODEL.as(MODEL)
					, FS_MODEL.ADMINISTRATION.as(ADMON)
					, FS_MODEL.YEAR.as(YEAR)
					, FS_MODEL.PERIOD.as(PERIOD)
					, FS_MODEL.STATUS.as(STATUS)
					,count.as(COUNT))
					.from(FS_MODEL)
					.innerJoin(DOMAIN).on(DOMAIN.ID.equal(FS_MODEL.DOMAIN))
					.innerJoin(PARENT).on(PARENT.ID.equal(DOMAIN.PARENT))
					.where(FS_MODEL.YEAR.gt(2012))
					.groupBy(PARENT.ID, FS_MODEL.MODEL, FS_MODEL.ADMINISTRATION, FS_MODEL.YEAR, FS_MODEL.PERIOD, FS_MODEL.STATUS)
			.union
			(
			CTX.select(PARENT.ID.as(ID)
					, PARENT.DESCRIPTION.as(DESCRIPTION)
					, PARENT.NAME.as(NAME)
					, PARENT.OWNER.as(OWNER)
					, DSL.inline("303 RG").as(MODEL)
					, FS_VAT_DECLARATION.ADMINISTRATION.as(ADMON)
					, FS_VAT.YEAR.as(YEAR)
					, FS_VAT.PERIOD.as(PERIOD)
					, FS_VAT_DECLARATION.STATUS.as(STATUS)
					, count.as(COUNT))
				.from(FS_VAT_DECLARATION)
				.innerJoin(DOMAIN).on(DOMAIN.ID.equal(FS_VAT_DECLARATION.DOMAIN))
				.innerJoin(PARENT).on(PARENT.ID.equal(DOMAIN.PARENT))
				.innerJoin(FS_VAT).on(FS_VAT.ID.equal(FS_VAT_DECLARATION.FS_VAT))
				.where(FS_VAT.YEAR.gt(2012))
				.groupBy(PARENT.ID, DSL.inline("303 RG"), FS_VAT_DECLARATION.ADMINISTRATION, FS_VAT.YEAR, FS_VAT.PERIOD, FS_VAT_DECLARATION.STATUS)
			).union
			(
			CTX.select(PARENT.ID.as(ID)
					, PARENT.DESCRIPTION.as(DESCRIPTION)
					, PARENT.NAME.as(NAME)
					, PARENT.OWNER.as(OWNER)
					, DSL.inline("390").as(MODEL)
					, FS_MODEL390.ADMINISTRATION.as(ADMON)
					, FS_MODEL390.YEAR.as(YEAR)
					, YEAR_PERIOD
					, FS_MODEL390.STATUS.as(STATUS)
					, count.as(COUNT))
				.from(FS_MODEL390)
				.innerJoin(DOMAIN).on(DOMAIN.ID.equal(FS_MODEL390.DOMAIN))
				.innerJoin(PARENT).on(PARENT.ID.equal(DOMAIN.PARENT))
				.where(FS_MODEL390.YEAR.gt(2012))
				.groupBy(PARENT.ID, DSL.inline("390"), FS_MODEL390.ADMINISTRATION, FS_MODEL390.YEAR, YEAR_PERIOD, FS_MODEL390.STATUS)
			).union
			(
			CTX.select(PARENT.ID.as(ID)
					, PARENT.DESCRIPTION.as(DESCRIPTION)
					, PARENT.NAME.as(NAME)
					, PARENT.OWNER.as(OWNER)
					, DSL.inline("180").as(MODEL)
					, FS_MODEL180.ADMINISTRATION.as(ADMON)
					, FS_MODEL180.YEAR.as(YEAR)
					, YEAR_PERIOD
					, FS_MODEL180.STATUS.as(STATUS)
					, count.as(COUNT))
				.from(FS_MODEL180)
				.innerJoin(DOMAIN).on(DOMAIN.ID.equal(FS_MODEL180.DOMAIN))
				.innerJoin(PARENT).on(PARENT.ID.equal(DOMAIN.PARENT))
				.where(FS_MODEL180.YEAR.gt(2012))
				.groupBy(PARENT.ID, DSL.inline("180"), FS_MODEL180.ADMINISTRATION, FS_MODEL180.YEAR, YEAR_PERIOD, FS_MODEL180.STATUS)
			).union
			(
			CTX.select(PARENT.ID.as(ID)
					, PARENT.DESCRIPTION.as(DESCRIPTION)
					, PARENT.NAME.as(NAME)
					, PARENT.OWNER.as(OWNER)
					, DSL.inline("184").as(MODEL)
					, FS_MODEL184.ADMINISTRATION.as(ADMON)
					, FS_MODEL184.YEAR.as(YEAR)
					, YEAR_PERIOD
					, FS_MODEL184.STATUS.as(STATUS)
					, count.as(COUNT))
				.from(FS_MODEL184)
				.innerJoin(DOMAIN).on(DOMAIN.ID.equal(FS_MODEL184.DOMAIN))
				.innerJoin(PARENT).on(PARENT.ID.equal(DOMAIN.PARENT))
				.where(FS_MODEL184.YEAR.gt(2012))
				.groupBy(PARENT.ID, DSL.inline("184"), FS_MODEL184.ADMINISTRATION, FS_MODEL184.YEAR, YEAR_PERIOD, FS_MODEL184.STATUS)
			).union
			(
			CTX.select(PARENT.ID.as(ID)
					, PARENT.DESCRIPTION.as(DESCRIPTION)
					, PARENT.NAME.as(NAME)
					, PARENT.OWNER.as(OWNER)
					, DSL.inline("190").as(MODEL)
					, FS_MODEL190.ADMINISTRATION.as(ADMON)
					, FS_MODEL190.YEAR.as(YEAR)
					, YEAR_PERIOD
					, FS_MODEL190.STATUS.as(STATUS)
					, count.as(COUNT))
				.from(FS_MODEL190)
				.innerJoin(DOMAIN).on(DOMAIN.ID.equal(FS_MODEL190.DOMAIN))
				.innerJoin(PARENT).on(PARENT.ID.equal(DOMAIN.PARENT))
				.where(FS_MODEL190.YEAR.gt(2012))
				.groupBy(PARENT.ID, DSL.inline("190"), FS_MODEL190.ADMINISTRATION, FS_MODEL190.YEAR, YEAR_PERIOD, FS_MODEL190.STATUS)
			).union
			(
			CTX.select(PARENT.ID.as(ID)
					, PARENT.DESCRIPTION.as(DESCRIPTION)
					, PARENT.NAME.as(NAME)
					, PARENT.OWNER.as(OWNER)
					, DSL.inline("193").as(MODEL)
					, FS_MODEL193.ADMINISTRATION.as(ADMON)
					, FS_MODEL193.YEAR.as(YEAR)
					, YEAR_PERIOD
					, FS_MODEL193.STATUS.as(STATUS)
					, count.as(COUNT))
				.from(FS_MODEL193)
				.innerJoin(DOMAIN).on(DOMAIN.ID.equal(FS_MODEL193.DOMAIN))
				.innerJoin(PARENT).on(PARENT.ID.equal(DOMAIN.PARENT))
				.where(FS_MODEL193.YEAR.gt(2012))
				.groupBy(PARENT.ID, DSL.inline("193"), FS_MODEL193.ADMINISTRATION, FS_MODEL193.YEAR, YEAR_PERIOD, FS_MODEL193.STATUS)
			).union
			(
			CTX.select(PARENT.ID.as(ID)
					, PARENT.DESCRIPTION.as(DESCRIPTION)
					, PARENT.NAME.as(NAME)
					, PARENT.OWNER.as(OWNER)
					, DSL.inline("200").as(MODEL)
					, FS_MODEL200.ADMINISTRATION.as(ADMON)
					, FS_MODEL200.YEAR.as(YEAR)
					, YEAR_PERIOD
					, FS_MODEL200.STATUS.as(STATUS)
					, count.as(COUNT))
				.from(FS_MODEL200)
				.innerJoin(DOMAIN).on(DOMAIN.ID.equal(FS_MODEL200.DOMAIN))
				.innerJoin(PARENT).on(PARENT.ID.equal(DOMAIN.PARENT))
				.where(FS_MODEL200.YEAR.gt(2012))
				.groupBy(PARENT.ID, DSL.inline("200"), FS_MODEL200.ADMINISTRATION, FS_MODEL200.YEAR, YEAR_PERIOD, FS_MODEL200.STATUS)
			).union
			(
			CTX.select(PARENT.ID.as(ID)
					, PARENT.DESCRIPTION.as(DESCRIPTION)
					, PARENT.NAME.as(NAME)
					, PARENT.OWNER.as(OWNER)
					, DSL.inline("347").as(MODEL)
					, FS_MOD347.ADMINISTRATION.as(ADMON)
					, FS_MOD347.YEAR.as(YEAR)
					, YEAR_PERIOD
					, FS_MOD347.STATUS.as(STATUS)
					, count.as(COUNT))
				.from(FS_MOD347)
				.innerJoin(DOMAIN).on(DOMAIN.ID.equal(FS_MOD347.DOMAIN))
				.innerJoin(PARENT).on(PARENT.ID.equal(DOMAIN.PARENT))
				.where(FS_MOD347.YEAR.gt(2012))
				.groupBy(PARENT.ID, DSL.inline("347"), FS_MOD347.ADMINISTRATION, FS_MOD347.YEAR, YEAR_PERIOD, FS_MOD347.STATUS)
			).union
			(
			CTX.select(PARENT.ID.as(ID)
					, PARENT.DESCRIPTION.as(DESCRIPTION)
					, PARENT.NAME.as(NAME)
					, PARENT.OWNER.as(OWNER)
					, DSL.inline("349").as(MODEL)
					, FS_MOD349.ADMINISTRATION.as(ADMON)
					, FS_MOD349.YEAR.as(YEAR)
					, FS_MOD349.PERIOD.as(PERIOD)
					, FS_MOD349.STATUS.as(STATUS)
					, count.as(COUNT))
				.from(FS_MOD349)
				.innerJoin(DOMAIN).on(DOMAIN.ID.equal(FS_MOD349.DOMAIN))
				.innerJoin(PARENT).on(PARENT.ID.equal(DOMAIN.PARENT))
				.where(FS_MOD349.YEAR.gt(2012))
				.groupBy(PARENT.ID, DSL.inline("349"), FS_MOD349.ADMINISTRATION, FS_MOD349.YEAR, FS_MOD349.PERIOD, FS_MOD349.STATUS)
			)
		)
		.orderBy(1,7,5,6,8,9)
			.stream()
			.map(rec -> new Model()
					.setDomainDescription((String) rec.getValue(DESCRIPTION))
					.setDomainName((String) rec.getValue(NAME))
					.setOwner((String) rec.getValue(OWNER))
					.setModel((String) rec.getValue(MODEL))
					.setAdministration(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Administration.class, (Byte) rec.getValue(ADMON)))
					.setYear((Integer) rec.getValue(YEAR))
					.setPeriod(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Period.class,(Byte) rec.getValue(PERIOD)))
					.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,(Byte) rec.getValue(STATUS)))
					.setCount((Integer) rec.getValue(COUNT)))
			.filter( mod -> !"3O3".equals(mod.getModel()))
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
			defaultStyle.setAlignment(HorizontalAlignment.LEFT);

			numberStyle = workbook.createCellStyle();
			numberStyle.setDataFormat(dataFormat.getFormat(NUMBER_PATTERN));
			numberStyle.setAlignment(HorizontalAlignment.CENTER);
			numberStyle.setFont(smallFont);

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 8);
			headerFont.setColor(IndexedColors.WHITE.index);

			headerCellStyle = (XSSFCellStyle) workbook.createCellStyle();
			headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
			headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerCellStyle.setFillForegroundColor(HEADER_COLOR);
			headerCellStyle.setFont(headerFont);
			if (printHeaders) {
				headerRow();
			}
		}

		private Cell addCell(String value) {
			Cell cell = row.createCell(cellCount++);
			cell.setCellValue(AonStringUtils.trimToEmpty(value));
			cell.setCellType(CellType.STRING);
			cell.setCellStyle(defaultStyle);
			return cell;
		}

		private Cell addCell(Integer value) {
			Cell cell = row.createCell(cellCount++);
			cell.setCellStyle(numberStyle);
			if (value != null) {
				cell.setCellValue(value);
			}
			cell.setCellType(CellType.NUMERIC);
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
			orientedHeaderCellStyle.setAlignment(HorizontalAlignment.CENTER);
			orientedHeaderCellStyle.setBorderBottom(BorderStyle.MEDIUM);
			orientedHeaderCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
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
