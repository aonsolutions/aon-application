package com.esferalia.aon.gwt.template.server.exports;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.type.Country;

public class RegistryExcelExport extends ExcelExport {

	private static final String ID = "Id";
	private static final String DOCUMENTO = "Documento";
	private static final String RAZON_SOCIAL = "Razón Social";
	private static final String DIRECCION = "Dirección";
	private static final String CODIGO_POSTAL = "Código Postal";
	private static final String PROVINCIA = "Provincia";
	private static final String PAIS = "País";
	
	List<CustomerFull> customers;
	List<SupplierFull> suppliers;
	List<CreditorFull> creditors;
	
	public static RegistryExcelExport getInstance() {
		return new RegistryExcelExport();
	}
	
	public RegistryExcelExport() {
		this.customers = new LinkedList<>();
		this.suppliers = new LinkedList<>();
		this.creditors = new LinkedList<>();
	}
	
	public void createCustomer(OutputStream out, List<CustomerFull> customers) {
		try {
			this.customers = customers;
			sheetName = "Clientes";
			build();
			buildColumns();
			buildHeader();
			buildContentCustomer();
			workbook.write(out);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createSupplier(OutputStream out, List<SupplierFull> suppliers) {
		try {
			this.suppliers = suppliers;
			sheetName = "Proveedores";
			build();
			buildColumns();
			buildHeader();
			buildContentSupplier();
			workbook.write(out);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createCreditor(OutputStream out, List<CreditorFull> creditors) {
		try {
			this.creditors = creditors;
			sheetName = "Acreedores";
			build();
			buildColumns();
			buildHeader();
			buildContentCreditor();
			workbook.write(out);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void buildColumns() {
		columns.add(new AonExcelColumn(ID, 12));
		columns.add(new AonExcelColumn(DOCUMENTO, 12));
		columns.add(new AonExcelColumn(RAZON_SOCIAL, 12));
		columns.add(new AonExcelColumn(DIRECCION, 12));
		columns.add(new AonExcelColumn(CODIGO_POSTAL, 12));
		columns.add(new AonExcelColumn(PROVINCIA, 12));
		columns.add(new AonExcelColumn(PAIS, 12));
	}
	
	private void buildContentCustomer() {
		XSSFCellStyle parStyle = getParStyle();
		XSSFCellStyle imparStyle = getImparStyle();
		for (Integer i = 0; i < customers.size(); i++) {
			buildRegistry(customers.get(i).getRegistry(), customers.get(i).getAddresses(), isPar(i) ? parStyle : imparStyle);
		}
	}
	
	private void buildContentSupplier() {
		XSSFCellStyle parStyle = getParStyle();
		XSSFCellStyle imparStyle = getImparStyle();
		for (Integer i = 0; i < suppliers.size(); i++) {
			buildRegistry(suppliers.get(i).getRegistry(), suppliers.get(i).getAddresses(), isPar(i) ? parStyle : imparStyle);
		}
	}
	
	private void buildContentCreditor() {
		XSSFCellStyle parStyle = getParStyle();
		XSSFCellStyle imparStyle = getImparStyle();
		for (Integer i = 0; i < creditors.size(); i++) {
			buildRegistry(creditors.get(i).getRegistry(), creditors.get(i).getAddresses(), isPar(i) ? parStyle : imparStyle);
		}
	}
	
	
	private void buildRegistry(Registry registry, List<RegistryAddress> addresses, XSSFCellStyle style) {
		addresses.stream().forEach(address -> {
			rowIndex++;
			Row row = sheet.createRow(rowIndex);
	        for(Integer i = 0; i< columns.size(); i++){
	        	Cell celda = row.createCell(i);
	        	celda.setCellValue( getCellValue(registry, address, columns.get(i).getValue()));
	        	celda.setCellStyle(style);
	        }
		});
	}
	
	private String getCellValue(Registry registry, RegistryAddress address, String value) {		
		switch (value) {
		case ID:
			return registry.getId().toString();
		case DOCUMENTO:
			return registry.getDocument();
		case RAZON_SOCIAL:
			return registry.getName();
		case DIRECCION:
			return address.getFullAddress();
		case CODIGO_POSTAL:
			return address.getZip();
		case PROVINCIA:
			return address.getProvince();
		case PAIS:
			return address.getCountry() != null 
				? address.getCountry().getName()
				: Country.ES.getName();
		default:
			return "";
		}
	}
}
