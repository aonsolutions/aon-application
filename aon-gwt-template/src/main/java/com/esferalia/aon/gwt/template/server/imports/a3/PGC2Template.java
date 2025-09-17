package com.esferalia.aon.gwt.template.server.imports.a3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.Objects;
import java.util.Optional;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.gwt.template.server.imports.Utils;
import com.esferalia.aon.gwt.template.server.imports.a3.Customer2Template.A3CustomerColumn;
import com.esferalia.aon.gwt.template.server.imports.a3.Customer2Template.AONCustomerColumn;
import com.esferalia.aon.watson.util.AonStringUtils;

public class PGC2Template {

	public enum A3PGCColumn {

		CUENTA("CUENTA"), 
		DESCRIPCION("DESCRIPCION");

		private String title;

		private A3PGCColumn(String title) {
			this.title = title;
		}
		
		public String getTitle() {
			return title;
		}

		private static Optional<A3PGCColumn> safeValueOf(Object title) {

			String uppercaseTitle = AonStringUtils.upperCase(title.toString());
			String trimmedTitle = AonStringUtils.replace(uppercaseTitle, " ", "");
			String normalizedTitle = AonStringUtils.normalized(trimmedTitle);

			A3PGCColumn[] a3PGCColumns = A3PGCColumn.values();
			return Arrays.stream(a3PGCColumns)
					.filter(col -> AonStringUtils.equalsIgnoreCase(col.title, normalizedTitle)).findFirst();
		}

	}

	public enum AONPGCColumn {
		
		CODIGO("CODIGO"), 
		DESCRIPCION("DESCRIPCIÓN"),
		ALIAS("ALIAS");

		private String title;

		private AONPGCColumn(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}
	}

	public static byte[] pgc2Template(byte[] data)  {
		try (ByteArrayInputStream is = new ByteArrayInputStream(data);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				XSSFWorkbook aonPGCXSSFWorkook = new XSSFWorkbook();
				XSSFWorkbook a3PGCXSSFWorkook = new XSSFWorkbook(is)) {

			pgc2Template(a3PGCXSSFWorkook, aonPGCXSSFWorkook);

			aonPGCXSSFWorkook.write(os);
			
			return os.toByteArray();
		} catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public static void pgc2Template(InputStream is, OutputStream os) throws IOException {
		try (XSSFWorkbook aonPGCXSSFWorkook = new XSSFWorkbook();
				XSSFWorkbook a3PGCXSSFWorkook = new XSSFWorkbook(is)) {

			pgc2Template(a3PGCXSSFWorkook, aonPGCXSSFWorkook);

			aonPGCXSSFWorkook.write(os);
		}
	}

	public static void pgc2Template(XSSFWorkbook a3PGCXSSFWorkook, XSSFWorkbook aonPGCXSSFWorkook) {

		XSSFSheet aonPGCXSSFSheet = aonPGCXSSFWorkook.createSheet();

		int aonRow  = 0 ;
		for (int sheet = 0; sheet < a3PGCXSSFWorkook.getNumberOfSheets(); sheet++) {
			XSSFSheet a3PGCXSSFSheet = a3PGCXSSFWorkook.getSheetAt(sheet);
			
			int a3Row;
			EnumMap<A3PGCColumn, Integer> a3PGCXSSFSheetColumns = new EnumMap<>(A3PGCColumn.class);

			for(a3Row = a3PGCXSSFSheet.getFirstRowNum(); a3Row <= a3PGCXSSFSheet.getLastRowNum(); a3Row++) {
				XSSFRow a3PGCXSSFRow = a3PGCXSSFSheet.getRow(a3Row);
				if (a3PGCXSSFRow == null)
					continue;

				for (int cell = a3PGCXSSFRow.getFirstCellNum(); cell >= 0
						&& cell <= a3PGCXSSFRow.getLastCellNum(); cell++) {
					XSSFCell a3PGCXSSFCell = a3PGCXSSFRow.getCell(cell);
					if (a3PGCXSSFCell == null)
						continue;
					Object cellValue = Utils.getObjectValue(a3PGCXSSFCell);
					if (cellValue != null) {
						int colIndex = cell;
						A3PGCColumn.safeValueOf(cellValue)
								.ifPresent(colKey -> a3PGCXSSFSheetColumns.put(colKey, colIndex));
					}
				}

				if (Arrays.stream(A3PGCColumn.values()).map(a3PGCXSSFSheetColumns::get)
						.allMatch(Objects::nonNull))
					break;

			}
			if ( aonRow == 0 ) {
				XSSFRow aonPGCXSSFSheetRowColumns = aonPGCXSSFSheet.createRow(aonRow++);
				Arrays.stream(AONPGCColumn.values()).forEach(col -> aonPGCXSSFSheetRowColumns
						.createCell(col.ordinal(), CellType.STRING).setCellValue(col.getTitle()));
			}
			
			for (a3Row++; a3Row <= a3PGCXSSFSheet.getLastRowNum(); a3Row++) { 
				XSSFRow a3PGCXSSFRow = a3PGCXSSFSheet.getRow(a3Row);
				if (a3PGCXSSFRow == null)
					continue;

//					for (int cell = a3PGCXSSFRow.getFirstCellNum(); cell <= a3PGCXSSFRow.getLastCellNum(); cell++) {
//						XSSFCell a3PGCXSSFCell = a3PGCXSSFRow.getCell(cell);
//						if (a3PGCXSSFCell == null)
//							continue;
//
//						System.out.print(Utils.getObjectValue(a3PGCXSSFCell) + "|");
//
//					}
//
//					System.out.println();
				


				Object cta = a3PGCXSSFRow.getCell(a3PGCXSSFSheetColumns.get(A3PGCColumn.CUENTA)).getRawValue();
				if ( cta == null )
					break;
				
				XSSFRow aonPGCXSSFSheetRow = aonPGCXSSFSheet.createRow(aonRow++);
				aonPGCXSSFSheetRow.createCell(AONPGCColumn.CODIGO.ordinal(), CellType.STRING).setCellValue(cta.toString());

				{
					Object descrpCta = Utils.getObjectValue(a3PGCXSSFRow.getCell(a3PGCXSSFSheetColumns.get(A3PGCColumn.DESCRIPCION)));
					if ( descrpCta != null )
						aonPGCXSSFSheetRow.createCell(AONPGCColumn.DESCRIPCION.ordinal(), CellType.STRING).setCellValue(descrpCta.toString());
				}


			}

		}
	}

	public static void main(String[] args) throws IOException {
		try (FileInputStream is = new FileInputStream(args[0]);
			FileOutputStream os = new FileOutputStream(args[1]);
			XSSFWorkbook aonPGCXSSFWorkook = new XSSFWorkbook();
			XSSFWorkbook a3PGCXSSFWorkook = new XSSFWorkbook(is);) {

			pgc2Template(a3PGCXSSFWorkook, aonPGCXSSFWorkook);
			
			aonPGCXSSFWorkook.write(os);
		}

	}

}
