package net.aonsolutions.aon.api.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceExcelExport extends ExcelExport {
	
	public enum InvoiceOpType {
		EX,
		PIS,
		ISP,
		EIB,
		VI,
		AI,
		AIS,
		AIB,
		GISP,
		GE,
		NAC,
		INT,
		EXT,
		CCM;
		
		public static InvoiceOpType safeValueOf(String value) {
			if(value == null) return null;
			try {
				return valueOf(value);
			} catch (Exception e) {
				return null;
			}
		}
		
		public static InvoiceOpType safeValueOf(InvoiceTransactionType value) {
			if(value == null) return null;
			try {
				if(InvoiceTransactionType.NATIONAL.equals(value))
					return NAC;
				else if(InvoiceTransactionType.INTRACOMMUNITY.equals(value))
					return INT;
				else if(InvoiceTransactionType.EXTRACOMMUNITY.equals(value))
					return EXT;
				else if(InvoiceTransactionType.CAN_CEU_MEL.equals(value))
					return CCM;
				else if(InvoiceTransactionType.OTHER_ISP.equals(value))
					return ISP;
			} catch (Exception e) {
				return null;
			}
			return null;
		}
	}

	List<Invoice> invoices;
	
	public static InvoiceExcelExport getInstance() {
		return new InvoiceExcelExport();
	}
	
	public InvoiceExcelExport() {
		this.invoices = new LinkedList<>();
	}
	
	public void create(OutputStream out, List<Invoice> invoices) {
		try {
			this.invoices = invoices;
			sheetName = "Facturas";
			build();
			buildColumns();
			buildHeader();
			buildContent();
			workbook.write(out);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void buildColumns() {
		columns.add(new AonExcelColumn("Tipo Operación", 12));
		columns.add(new AonExcelColumn("Tipo Factura", 12));
		columns.add(new AonExcelColumn("Fecha", 12));
		columns.add(new AonExcelColumn("Serie", 12));
		columns.add(new AonExcelColumn("Número", 12));
		columns.add(new AonExcelColumn("Referencia", 12));
		columns.add(new AonExcelColumn("NIF", 12));
		columns.add(new AonExcelColumn("Nombre", 12));
		columns.add(new AonExcelColumn("Cuenta Contraparte", 12));
        columns.add(new AonExcelColumn("Observaciones", 12));
        columns.add(new AonExcelColumn("Dirección", 12));
        columns.add(new AonExcelColumn("Ciudad", 12));
        columns.add(new AonExcelColumn("Provincia", 12));
        columns.add(new AonExcelColumn("Código Postal", 12));
        columns.add(new AonExcelColumn("País", 12));
        columns.add(new AonExcelColumn("Cuenta Explotación", 12));
        columns.add(new AonExcelColumn("Descripción Cuenta", 12));
        columns.add(new AonExcelColumn("Base Imponible", 12));
        columns.add(new AonExcelColumn("%IVA", 12));
        columns.add(new AonExcelColumn("Cuota IVA", 12));
        columns.add(new AonExcelColumn("%RE", 12));
        columns.add(new AonExcelColumn("Couta RE", 12));
        columns.add(new AonExcelColumn("%Retención", 12));
        columns.add(new AonExcelColumn("Cuota Retención", 12));
        columns.add(new AonExcelColumn("Total", 12));
        columns.add(new AonExcelColumn("Clave Retención", 12));
        columns.add(new AonExcelColumn("Subclave Retención", 12));
        columns.add(new AonExcelColumn("Fichero", 12));
	}
	
	private void buildContent() {
		XSSFCellStyle parStyle = getParStyle();
		XSSFCellStyle imparStyle = getImparStyle();
		for (Integer i = 0; i < invoices.size(); i++) {
			buildInvoice(invoices.get(i), isPar(i) ? parStyle : imparStyle);
		}
	}
	
	private void buildInvoice(Invoice invoice, XSSFCellStyle style) {
		invoice.getDetails().stream().forEach(detail -> {
			rowIndex++;
			Row row = sheet.createRow(rowIndex);
	        for(Integer i = 0; i< columns.size(); i++){
	        	Cell celda = row.createCell(i);
	        	celda.setCellValue( getCellValue(invoice, detail, columns.get(i).getValue()));
	        	celda.setCellStyle(style);
	        	if("Fichero".equalsIgnoreCase(columns.get(i).getValue()) && !AonStringUtils.isBlank(invoice.getFileUrl())) {
	        		Hyperlink link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
	        		link.setAddress(invoice.getFileUrl());
	        		celda.setHyperlink(link);
	        	}
	        }
		});
	}
	
	private String getCellValue(Invoice invoice, InvoiceDetail detail, String value) {
		InvoiceTax vat = detail.getInvoiceTaxes().stream().filter(f -> TaxType.VAT.equals(f.getTaxType())).findFirst().orElse(new InvoiceTax());
		InvoiceTax retention = detail.getInvoiceTaxes().stream().filter(f -> TaxType.RETENTION.equals(f.getTaxType())).findFirst().orElse(new InvoiceTax());
		
		switch (value) {
		case "Tipo Operación":
			return InvoiceOpType.safeValueOf(invoice.getTransaction()).name();
		case "Tipo Factura":
			return invoice.getType().getDescription();
		case "Fecha":
			return AonDateUtils.simpleFormat(invoice.getIssueDate());
		case "Serie":
			return invoice.getSeries();
		case "Número":
			return Integer.toString(invoice.getNumber());
		case "Referencia":
			return invoice.getReferenceCode();
		case "NIF":
			return invoice.getRegistryDocument();
		case "Nombre":
			return invoice.getRegistryName();
		case "Cuenta Contraparte":
			return invoice.getRegistryAccount().getCode();
		case "Observaciones":
			return invoice.getRemarks();
		case "Dirección":
			return invoice.getAddress().getFullAddress();
		case "Ciudad":
			return invoice.getAddress().getCity();
		case "Provincia":
			return invoice.getAddress().getProvince();
		case "Código Postal":
			return invoice.getAddress().getZip();
		case "País":
			return invoice.getAddress().getCountry() != null
				? invoice.getAddress().getCountry().getIso2()
				: Country.ES.getIso2();
		case "Cuenta Explotación":
			return detail.getExpAccountCode();
		case "Descripción Cuenta":
			return detail.getExpAccountDescription();
		case "Suplido":
			return detail.isPrepayment() ? "Si" : "No";
		case "Base Imponible":
			return Double.toString(vat.getBase());
		case "%IVA":
			return Double.toString(vat.getPercentage());
		case "Cuota IVA":
			return Double.toString(vat.getQuota());
		case "%RE":
			return Double.toString(vat.getSurcharge());
		case "Couta RE":
			return Double.toString(vat.getSurchargeQuota());
		case "%Retención":
			return Double.toString(retention.getPercentage());
		case "Cuota Retención":
			return Double.toString(retention.getQuota());
		case "Total":
			return Double.toString(AonMathUtils.round(vat.getBase() + vat.getQuota() + vat.getSurchargeQuota() - retention.getQuota()));
		case "Clave Retención":
			return "";
		case "Subclave Retención":
			return "";
		case "Fichero":
			return AonStringUtils.isBlank(invoice.getFileUrl()) ? "" : "Descargar";
		default:
			return "";
		}
	}
}