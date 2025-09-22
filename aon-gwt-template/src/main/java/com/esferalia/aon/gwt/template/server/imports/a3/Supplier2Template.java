package com.esferalia.aon.gwt.template.server.imports.a3;

import static com.esferalia.aon.gwt.template.server.imports.a3.Customer2Template.customer2Template;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class Supplier2Template {


	public static void main(String[] args) throws IOException {
		try (FileInputStream is = new FileInputStream(args[0]);
				FileOutputStream os = new FileOutputStream(args[1]);
				XSSFWorkbook a3CustomerXSSFWorkook = new XSSFWorkbook(is);
				XSSFWorkbook aonCustomerXSSFWorkook = new XSSFWorkbook() ) {

			customer2Template(a3CustomerXSSFWorkook, aonCustomerXSSFWorkook, "PROVEEDOR");
			
			aonCustomerXSSFWorkook.write(os);
		}

	}


}
