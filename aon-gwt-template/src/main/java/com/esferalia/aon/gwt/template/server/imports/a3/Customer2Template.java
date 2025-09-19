package com.esferalia.aon.gwt.template.server.imports.a3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Objects;
import java.util.Optional;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.gwt.template.server.imports.Utils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Customer2Template {



	public enum A3CustomerColumn
	{
		
		CP("C.P."),
		NUM("NUM."),
		NIF("N.I.F."),
		LOCAL("LOCAL"),
		EMAIL("EMAIL"),
		CUENTA("CUENTA"),
		TELEFONO("TELEFONO"),
		MUNICIPIO("MUNICIPIO"),
		VIA_PUBLICA("VIAPUBLICA"),
		DESCRIPCION("DESCRIPCION");

		private String title;

		private A3CustomerColumn(String title){
			this.title = title;
		}
		
		
		private static Optional<A3CustomerColumn> safeValueOf(Object title) {
			
			String uppercaseTitle = AonStringUtils.upperCase(title.toString());
			String trimmedTitle = AonStringUtils.replace(uppercaseTitle, " ", "");
			String normalizedTitle = AonStringUtils.normalized(trimmedTitle);
			
			A3CustomerColumn [] a3CustomerColumns = A3CustomerColumn.values();
			return Arrays.stream(a3CustomerColumns)
			.filter(col -> AonStringUtils.equalsIgnoreCase(col.title, normalizedTitle))
			.findFirst();
		}
		
		public String getTitle() {
			return title;
		}

	};

	public enum AONCustomerColumn
	{
		
		TIPO("TIPO"),
		CUENTA("CUENTA"),

		CIF("CIF"),
		NOMBRE("NOMBRE"),

		DIRECCION("DIRECCIÓN"),
		CODIGO_POSTAL("CÓDIGO POSTAL"),
		CIUDAD("CIUDAD"),
		PROVINCIA("PROVINCIA"),
		PAIS("PAIS"),

		FORMA_DE_PAGO("FORMA DE PAGO"),
		TIPO_DE_PAGO("TIPO DE PAGO"),
		IBAN("IBAN"),
		
		CCC("CCC"),
		
		EMAIL("EMAIL"),
		TELEFONO("TELEFONO");
		
		private String title;

		private AONCustomerColumn(String title){
			this.title = title;
		}
		
		public String getTitle() {
			return title;
		}
		
	};
	public static byte[] customer2Template(byte[] data)  {
		return customer2Template(data, null);
	}

	public static byte[] customer2Template(byte[] data, String tipo)  {
		try (ByteArrayInputStream is = new ByteArrayInputStream(data);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				XSSFWorkbook aonCustomerXSSFWorkook = new XSSFWorkbook();
				XSSFWorkbook a3CustomerXSSFWorkook = new XSSFWorkbook(is)) {

			customer2Template(a3CustomerXSSFWorkook, aonCustomerXSSFWorkook, tipo);

			aonCustomerXSSFWorkook.write(os);
			
			return os.toByteArray();
		} catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public static void customer2Template(InputStream is, OutputStream os, String tipo) throws IOException {
		try (XSSFWorkbook aonCustomerXSSFWorkook = new XSSFWorkbook();
				XSSFWorkbook a3CustomerXSSFWorkook = new XSSFWorkbook(is)) {

			customer2Template(a3CustomerXSSFWorkook, aonCustomerXSSFWorkook, tipo);

			aonCustomerXSSFWorkook.write(os);
		}
	}

	protected static void customer2Template(XSSFWorkbook a3CustomerXSSFWorkook, XSSFWorkbook aonCustomerXSSFWorkook, String tipo) {
		XSSFSheet aonCustomerXSSFSheet = aonCustomerXSSFWorkook.createSheet();
		
		for (int sheet = 0; sheet < a3CustomerXSSFWorkook.getNumberOfSheets(); sheet++) {
			XSSFSheet a3CustomerXSSFSheet = a3CustomerXSSFWorkook.getSheetAt(sheet);

			int a3Row;
			EnumMap<A3CustomerColumn, Integer> a3CustomerXSSFSheetColumns = new EnumMap<>(A3CustomerColumn.class);
			
			for (a3Row = a3CustomerXSSFSheet.getFirstRowNum(); a3Row <= a3CustomerXSSFSheet.getLastRowNum(); a3Row++) {
				XSSFRow a3CustomerXSSFRow = a3CustomerXSSFSheet.getRow(a3Row);
				if (a3CustomerXSSFRow == null)
					continue;
				
				for (int cell = a3CustomerXSSFRow.getFirstCellNum(); cell >= 0 && cell <= a3CustomerXSSFRow
						.getLastCellNum(); cell++) {
					XSSFCell a3CustomerXSSFCell = a3CustomerXSSFRow.getCell(cell);
					if (a3CustomerXSSFCell == null)
						continue;
					Object cellValue = Utils.getObjectValue(a3CustomerXSSFCell);
					if ( cellValue != null ) {
						int colIndex = cell;
						A3CustomerColumn.safeValueOf(cellValue).ifPresent(colKey -> a3CustomerXSSFSheetColumns.put(colKey, colIndex));
					}
				}

				if (Arrays.stream(A3CustomerColumn.values()).map(a3CustomerXSSFSheetColumns::get).allMatch(Objects::nonNull))
					break;
			}
			int aonRow  = 0 ;
			XSSFRow aonCustomerXSSFSheetRowColumns = aonCustomerXSSFSheet.createRow(aonRow++);
			Arrays.stream(AONCustomerColumn.values()).forEach(col -> aonCustomerXSSFSheetRowColumns
					.createCell(col.ordinal(), CellType.STRING).setCellValue(col.getTitle()));					
			for (a3Row++; a3Row <= a3CustomerXSSFSheet.getLastRowNum(); a3Row++) {
				XSSFRow a3CustomerXSSFRow = a3CustomerXSSFSheet.getRow(a3Row);
				if (a3CustomerXSSFRow == null)
					continue;
				
//				for (int cell = a3CustomerXSSFRow.getFirstCellNum(); cell <= a3CustomerXSSFRow
//						.getLastCellNum(); cell++) {
//					XSSFCell a3CustomerXSSFCell = a3CustomerXSSFRow.getCell(cell);
//					if (a3CustomerXSSFCell == null)
//						continue;
//
//					System.out.print(Utils.getObjectValue(a3CustomerXSSFCell) + "|");
//
//				}
//
//				System.out.println();

				XSSFRow aonCustomerXSSFSheetRow = aonCustomerXSSFSheet.createRow(aonRow++);
				if ( tipo != null )
					aonCustomerXSSFSheetRow.createCell(AONCustomerColumn.TIPO.ordinal(), CellType.STRING).setCellValue(tipo);
				{
					Object cuenta = a3CustomerXSSFRow.getCell(a3CustomerXSSFSheetColumns.get(A3CustomerColumn.CUENTA)).getRawValue();
					if ( cuenta != null ) {
						aonCustomerXSSFSheetRow.createCell(AONCustomerColumn.CUENTA.ordinal(), CellType.STRING).setCellValue(cuenta.toString());
						if ( tipo == null ) {
							if ( AonStringUtils.startsWith(cuenta.toString(), "43" ) )
								aonCustomerXSSFSheetRow.createCell(AONCustomerColumn.TIPO.ordinal(), CellType.STRING).setCellValue("CLIENTE");
							else if ( AonStringUtils.startsWith(cuenta.toString(), "41" ) )
								aonCustomerXSSFSheetRow.createCell(AONCustomerColumn.TIPO.ordinal(), CellType.STRING).setCellValue("ACREEDOR");
							else if ( AonStringUtils.startsWith(cuenta.toString(), "40" ) )
								aonCustomerXSSFSheetRow.createCell(AONCustomerColumn.TIPO.ordinal(), CellType.STRING).setCellValue("PROVEEDOR");
							
						}
					}
				}
				
				{
					Object cif = Utils.getObjectValue(a3CustomerXSSFRow.getCell(a3CustomerXSSFSheetColumns.get(A3CustomerColumn.NIF)));
						if ( cif != null )
							aonCustomerXSSFSheetRow.createCell(AONCustomerColumn.CIF.ordinal(), CellType.STRING).setCellValue(cif.toString());
				}
				
				{
					Object description = Utils.getObjectValue(a3CustomerXSSFRow.getCell(a3CustomerXSSFSheetColumns.get(A3CustomerColumn.DESCRIPCION)));
					if ( description != null )
						aonCustomerXSSFSheetRow.createCell(AONCustomerColumn.NOMBRE.ordinal(), CellType.STRING).setCellValue(description.toString());
				}
				
				{
					Object cp = Utils.getObjectValue(a3CustomerXSSFRow.getCell(a3CustomerXSSFSheetColumns.get(A3CustomerColumn.CP)));
					if ( cp != null ) {
						aonCustomerXSSFSheetRow.createCell(AONCustomerColumn.PROVINCIA.ordinal(), CellType.STRING).setCellValue("");
						aonCustomerXSSFSheetRow.createCell(AONCustomerColumn.CODIGO_POSTAL.ordinal(), CellType.STRING).setCellValue(cp.toString());
						
					}
				}

				{
					Object municipio = Utils.getObjectValue(a3CustomerXSSFRow.getCell(a3CustomerXSSFSheetColumns.get(A3CustomerColumn.MUNICIPIO)));
					if ( municipio != null )
						aonCustomerXSSFSheetRow.createCell(AONCustomerColumn.CIUDAD.ordinal(), CellType.STRING).setCellValue(municipio.toString());
				}

				{
					Object email = Utils.getObjectValue(a3CustomerXSSFRow.getCell(a3CustomerXSSFSheetColumns.get(A3CustomerColumn.EMAIL)));
					if ( email != null )
						aonCustomerXSSFSheetRow.createCell(AONCustomerColumn.EMAIL.ordinal(), CellType.STRING).setCellValue(email.toString());
				}

				{
					Object telefono = Utils.getObjectValue(a3CustomerXSSFRow.getCell(a3CustomerXSSFSheetColumns.get(A3CustomerColumn.TELEFONO)));
					if ( telefono != null )
						aonCustomerXSSFSheetRow.createCell(AONCustomerColumn.TELEFONO.ordinal(), CellType.STRING).setCellValue(telefono.toString());
				}
				
				{
					Object num = Utils.getObjectValue(a3CustomerXSSFRow.getCell(a3CustomerXSSFSheetColumns.get(A3CustomerColumn.NUM)));
					Object viaPublica = Utils.getObjectValue(a3CustomerXSSFRow.getCell(a3CustomerXSSFSheetColumns.get(A3CustomerColumn.VIA_PUBLICA)));
					if ( viaPublica != null )
						aonCustomerXSSFSheetRow.createCell(AONCustomerColumn.DIRECCION.ordinal(), CellType.STRING).setCellValue(viaPublica.toString() + ( num != null ? ", " + String.format("%.0f", num): "" ));
				}

				// default values 
				aonCustomerXSSFSheetRow.createCell(AONCustomerColumn.PAIS.ordinal(), CellType.STRING).setCellValue("ES");
				
				
			}

		}
	}

	public static void main(String[] args) throws IOException {
		try (FileInputStream is = new FileInputStream(args[0]);
				FileOutputStream os = new FileOutputStream(args[1]);
				XSSFWorkbook a3CustomerXSSFWorkook = new XSSFWorkbook(is);
				XSSFWorkbook aonCustomerXSSFWorkook = new XSSFWorkbook() ) {

			customer2Template(a3CustomerXSSFWorkook, aonCustomerXSSFWorkook, "CLIENTE");
			
			aonCustomerXSSFWorkook.write(os);
		}

	}


}
