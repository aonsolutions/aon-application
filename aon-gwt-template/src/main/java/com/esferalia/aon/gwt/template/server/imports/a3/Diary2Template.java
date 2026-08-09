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

public class Diary2Template {

	public enum A3DiaryColumn {

		//REF_INT("REF.INT."), 
		FECHA("FECHA"), 
		ASIENTO("ASIENTO"), 
		APUNTE("APUNTE"), 
		CONCEPTO("CONCEPTO"),
		DOCUMENTO("DOCUMENTO"), 
		CUENTA("CUENTA"), 
		DESCRIPCION_DE_LA_CUENTA("DESCRIPCIONDELACUENTA"),
		IMPORTE_DEBE("IMPORTEDEBE"), 
		IMPORTE_HABER("IMPORTEHABER");

		private String title;

		private A3DiaryColumn(String title) {
			this.title = title;
		}
		
		public String getTitle() {
			return title;
		}

		private static Optional<A3DiaryColumn> safeValueOf(Object title) {

			String uppercaseTitle = AonStringUtils.upperCase(title.toString());
			String trimmedTitle = AonStringUtils.replace(uppercaseTitle, " ", "");
			String normalizedTitle = AonStringUtils.normalized(trimmedTitle);

			A3DiaryColumn[] a3DiaryColumns = A3DiaryColumn.values();
			return Arrays.stream(a3DiaryColumns)
					.filter(col -> AonStringUtils.equalsIgnoreCase(col.title, normalizedTitle)).findFirst();
		}

	}

	public enum AONDiaryColumn {
		
		TIPO("TIPO"), 					// REFERENCIA
		ASIENTO("ASIENTO"),				// NºDIARIO
		APUNTE("APUNTE"),
		FECHA("FECHA"), 
		FACTURA("FACTURA"),				// DOCUMENTO
		CUENTA("CUENTA"),				// SUBCUENTA
		DESC_CUENTA("DESC. CUENTA"),	// TITULO_DE_SUBCUENTA
		CONTRAPARTIDA("CONTRAPARTIDA"), // CONTRAP.
		DESC_CONTRAP("DESC.CONTRAP."),
		CONCEPTO("CONCEPTO"),
		DEBE("DEBE"),
		HABER("HABER"),
		COMENTARIOS("COMENTARIOS");

		private String title;

		private AONDiaryColumn(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}
	}

	public static byte[] diary2Template(byte[] data)  {
		try (ByteArrayInputStream is = new ByteArrayInputStream(data);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				XSSFWorkbook aonDiaryXSSFWorkook = new XSSFWorkbook();
				XSSFWorkbook a3DiaryXSSFWorkook = new XSSFWorkbook(is)) {

			diary2Template(a3DiaryXSSFWorkook, aonDiaryXSSFWorkook);

			aonDiaryXSSFWorkook.write(os);
			
			return os.toByteArray();
		} catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public static void diary2Template(InputStream is, OutputStream os) throws IOException {
		try (XSSFWorkbook aonDiaryXSSFWorkook = new XSSFWorkbook();
				XSSFWorkbook a3DiaryXSSFWorkook = new XSSFWorkbook(is)) {

			diary2Template(a3DiaryXSSFWorkook, aonDiaryXSSFWorkook);

			aonDiaryXSSFWorkook.write(os);
		}
	}

	public static void diary2Template(XSSFWorkbook a3DiaryXSSFWorkook, XSSFWorkbook aonDiaryXSSFWorkook) {
		XSSFCellStyle fechaStyle = aonDiaryXSSFWorkook.createCellStyle();
		fechaStyle.setDataFormat(aonDiaryXSSFWorkook.createDataFormat().getFormat("dd/MM/yyyy"));

		XSSFSheet aonDiaryXSSFSheet = aonDiaryXSSFWorkook.createSheet();

		int aonRow  = 0 ;
		for (int sheet = 0; sheet < a3DiaryXSSFWorkook.getNumberOfSheets(); sheet++) {
			XSSFSheet a3DiaryXSSFSheet = a3DiaryXSSFWorkook.getSheetAt(sheet);
			
			int a3Row;
			EnumMap<A3DiaryColumn, Integer> a3DiaryXSSFSheetColumns = new EnumMap<>(A3DiaryColumn.class);

			for(a3Row = a3DiaryXSSFSheet.getFirstRowNum(); a3Row <= a3DiaryXSSFSheet.getLastRowNum(); a3Row++) {
				XSSFRow a3DiaryXSSFRow = a3DiaryXSSFSheet.getRow(a3Row);
				if (a3DiaryXSSFRow == null)
					continue;

				for (int cell = a3DiaryXSSFRow.getFirstCellNum(); cell >= 0
						&& cell <= a3DiaryXSSFRow.getLastCellNum(); cell++) {
					XSSFCell a3DiaryXSSFCell = a3DiaryXSSFRow.getCell(cell);
					if (a3DiaryXSSFCell == null)
						continue;
					Object cellValue = Utils.getObjectValue(a3DiaryXSSFCell);
					if (cellValue != null) {
						int colIndex = cell;
						A3DiaryColumn.safeValueOf(cellValue)
								.ifPresent(colKey -> a3DiaryXSSFSheetColumns.put(colKey, colIndex));
					}
				}

				if (Arrays.stream(A3DiaryColumn.values()).map(a3DiaryXSSFSheetColumns::get)
						.allMatch(Objects::nonNull))
					break;

			}
			if ( aonRow == 0 ) {
				XSSFRow aonDiaryXSSFSheetRowColumns = aonDiaryXSSFSheet.createRow(aonRow++);
				Arrays.stream(AONDiaryColumn.values()).forEach(col -> aonDiaryXSSFSheetRowColumns
						.createCell(col.ordinal(), CellType.STRING).setCellValue(col.getTitle()));
			}
			
			for (a3Row++; a3Row <= a3DiaryXSSFSheet.getLastRowNum(); a3Row++) { 
				XSSFRow a3DiaryXSSFRow = a3DiaryXSSFSheet.getRow(a3Row);
				if (a3DiaryXSSFRow == null)
					continue;

//					for (int cell = a3DiaryXSSFRow.getFirstCellNum(); cell <= a3DiaryXSSFRow.getLastCellNum(); cell++) {
//						XSSFCell a3DiaryXSSFCell = a3DiaryXSSFRow.getCell(cell);
//						if (a3DiaryXSSFCell == null)
//							continue;
//
//						System.out.print(Utils.getObjectValue(a3DiaryXSSFCell) + "|");
//
//					}
//
//					System.out.println();
				
				Object apunte = Utils.getObjectValue(a3DiaryXSSFRow.getCell(a3DiaryXSSFSheetColumns.get(A3DiaryColumn.APUNTE)));
				if (apunte == null)
					continue;


				XSSFRow aonDiaryXSSFSheetRow = aonDiaryXSSFSheet.createRow(aonRow++);
				
				{
					aonDiaryXSSFSheetRow.createCell(AONDiaryColumn.APUNTE.ordinal(), CellType.NUMERIC).setCellValue(Integer.parseInt(String.format("%.0f", apunte)));
				}

				{
					Object concepto = Utils.getObjectValue(a3DiaryXSSFRow.getCell(a3DiaryXSSFSheetColumns.get(A3DiaryColumn.CONCEPTO)));
					if ( concepto == null )
						concepto = Utils.getObjectValue(a3DiaryXSSFRow.getCell(a3DiaryXSSFSheetColumns.get(A3DiaryColumn.DESCRIPCION_DE_LA_CUENTA)));
					if ( concepto != null )
						aonDiaryXSSFSheetRow.createCell(AONDiaryColumn.CONCEPTO.ordinal(), CellType.STRING).setCellValue(concepto.toString());
				}

				{	
					Object documento = Utils.getObjectValue(a3DiaryXSSFRow.getCell(a3DiaryXSSFSheetColumns.get(A3DiaryColumn.DOCUMENTO)));
					documento = AonStringUtils.trim(documento.toString());
					documento = AonStringUtils.normalized(documento.toString());
					if ( documento != null && AonStringUtils.equalsIgnoreCase(documento.toString(), "Apertura")) {
						aonDiaryXSSFSheetRow.createCell(AONDiaryColumn.TIPO.ordinal(), CellType.STRING).setCellValue("&AP");
					} else if( documento != null && AonStringUtils.equalsIgnoreCase(documento.toString(), "Cierre")) {
						aonDiaryXSSFSheetRow.createCell(AONDiaryColumn.TIPO.ordinal(), CellType.STRING).setCellValue("&CR");
					} else if( documento != null && AonStringUtils.equalsIgnoreCase(documento.toString(), "Explotacion")) {
						aonDiaryXSSFSheetRow.createCell(AONDiaryColumn.TIPO.ordinal(), CellType.STRING).setCellValue("&APG");
					} else {
						//aonDiaryXSSFSheetRow.createCell(AONDiaryColumn.FACTURA.ordinal(), CellType.STRING).setCellValue(documento.toString());
					}
				}
				

				{
					Object asiento = Utils.getObjectValue(a3DiaryXSSFRow.getCell(a3DiaryXSSFSheetColumns.get(A3DiaryColumn.ASIENTO)));
					if ( asiento ==  null && aonDiaryXSSFSheetRow.getRowNum() > 0  ) 
						asiento =  Utils.getObjectValue(aonDiaryXSSFSheet.getRow(aonDiaryXSSFSheetRow.getRowNum()-1 ).getCell(AONDiaryColumn.ASIENTO.ordinal()));
					if ( asiento != null )
						aonDiaryXSSFSheetRow.createCell(AONDiaryColumn.ASIENTO.ordinal(), CellType.NUMERIC).setCellValue(Integer.parseInt(String.format("%.0f", asiento)));
				}
				

				{
					Date fecha = a3DiaryXSSFRow.getCell(a3DiaryXSSFSheetColumns.get(A3DiaryColumn.FECHA)).getDateCellValue();
					if ( fecha != null ) {
						XSSFCell fechaCell = aonDiaryXSSFSheetRow.createCell(AONDiaryColumn.FECHA.ordinal());
						fechaCell.setCellValue(fecha);
						fechaCell.setCellStyle(fechaStyle);
						
					}
				}

				{
					Object cta = a3DiaryXSSFRow.getCell(a3DiaryXSSFSheetColumns.get(A3DiaryColumn.CUENTA)).getRawValue();
					if ( cta != null )
						aonDiaryXSSFSheetRow.createCell(AONDiaryColumn.CUENTA.ordinal(), CellType.STRING).setCellValue(cta.toString());
				}

				{
					Object descrpCta = Utils.getObjectValue(a3DiaryXSSFRow.getCell(a3DiaryXSSFSheetColumns.get(A3DiaryColumn.DESCRIPCION_DE_LA_CUENTA)));
					if ( descrpCta != null )
						aonDiaryXSSFSheetRow.createCell(AONDiaryColumn.DESC_CUENTA.ordinal(), CellType.STRING).setCellValue(descrpCta.toString());
				}


				{
					Object debe = Utils.getObjectValue(a3DiaryXSSFRow.getCell(a3DiaryXSSFSheetColumns.get(A3DiaryColumn.IMPORTE_DEBE)));
					if ( debe != null )
						aonDiaryXSSFSheetRow.createCell(AONDiaryColumn.DEBE.ordinal(), CellType.NUMERIC).setCellValue(Double.parseDouble(debe.toString()));
				}

				{
					Object haber = Utils.getObjectValue(a3DiaryXSSFRow.getCell(a3DiaryXSSFSheetColumns.get(A3DiaryColumn.IMPORTE_HABER)));
					if ( haber != null )
						aonDiaryXSSFSheetRow.createCell(AONDiaryColumn.HABER.ordinal(), CellType.NUMERIC).setCellValue(Double.parseDouble(haber.toString()));
				}
			}

		}
	}

	public static void main(String[] args) throws IOException {
		try (FileInputStream is = new FileInputStream(args[0]);
			FileOutputStream os = new FileOutputStream(args[1]);
			XSSFWorkbook aonDiaryXSSFWorkook = new XSSFWorkbook();
			XSSFWorkbook a3DiaryXSSFWorkook = new XSSFWorkbook(is);) {

			diary2Template(a3DiaryXSSFWorkook, aonDiaryXSSFWorkook);
			
			aonDiaryXSSFWorkook.write(os);
		}

	}

}
