package com.esferalia.aon.in.payroll.pdf.maker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.esferalia.aon.in.payroll.pdf.maker.budget.BudgetPrintConfiguration;
import com.esferalia.aon.in.payroll.pdf.maker.budget.BudgetTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.EnterprisePayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayroll;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.DefaultPayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.IPayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.SettlePrintConfiguration;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.SettlementTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.warehouse.MultipleWarehouseTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.warehouse.PackagingTag;
import com.esferalia.aon.in.payroll.pdf.maker.warehouse.WarehouseSaleTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.warehouse.WarehouseTemplate;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;

public class PdfMaker {

	/**
	 * CREATE INVOICE PDF
	 * 
	 * @param out
	 * @param invoice, Invoice Object
	 * @param config, print invoice configuration
	 */
	public static void printInvoice(OutputStream out, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config, String qrUrl, byte[] logo, String tbaiId) {
		try {
			InvoiceTemplate template = new InvoiceTemplate(company, invoice, config, qrUrl, logo, tbaiId);
			template.print(out);
		} catch (CanNotCreatePdfException e) {
			e.printStackTrace();
		}		
	}
	
	public static void printInvoice(OutputStream out, CompanyFull company, List<Invoice> invoices, PrintInvoiceConfiguration config, String qrUrl, byte[] logo, String tbaiId) {
		try {
			InvoiceTemplate template = new InvoiceTemplate(company, invoices, config, qrUrl, logo, tbaiId);
			template.print(out);
		} catch (CanNotCreatePdfException e) {
			e.printStackTrace();
		}		
	}
	
	public static void printPackaging(OutputStream out, CompanyFull company, Item item, byte[] logo, String barcode, Double quantity, String ean128, String sscc) {
		try {
			WarehouseTemplate template = new WarehouseTemplate(company, item, logo, barcode, quantity, ean128, sscc);
			template.print(out);
		} catch (CanNotCreatePdfException e) {
			e.printStackTrace();
		}		
	}
	
	public static void printMultiplePackaging(OutputStream out, PackagingTag packagingTag) {
		try {
			MultipleWarehouseTemplate template = new MultipleWarehouseTemplate(packagingTag);
			template.print(out);
		} catch (CanNotCreatePdfException e) {
			e.printStackTrace();
		}		
	}
	
	public static void printSalesPackaging(OutputStream out, Sales sales) {
		try {
			WarehouseSaleTemplate template = new WarehouseSaleTemplate(sales);
			template.print(out);
		} catch (CanNotCreatePdfException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * CREATE ENTERPRISE PAYROLL
	 * 
	 * @param payroll
	 * @param out
	 * @param locale
	 * @throws IOException
	 * @throws CanNotCreatePdfException
	 */
	public static void printEnterprisePayroll(EnterprisePayroll payroll, OutputStream out, Optional<Locale> locale)
			throws IOException, CanNotCreatePdfException {
		EnterprisePayrollTemplate.print(payroll, out, locale);
	}
	
	
	public static void printEnterprisePayroll(EnterprisePayroll payroll, OutputStream out, Optional<Locale> locale, Date startDate, Date endDate)
			throws IOException, CanNotCreatePdfException {
		EnterprisePayrollTemplate.print(payroll, out, locale, startDate, endDate, true);
	}

	/**
	 * CREATE PAYROLL
	 * 
	 * @param out
	 * @param payroll
	 * @param logo
	 * @param language
	 * @throws CanNotCreatePdfException
	 */
	public static void printDefaultPayroll(OutputStream out, DefaultPayroll payroll, InputStream logo, Locale language)
			throws CanNotCreatePdfException {
		PayrollTemplate template = new PayrollTemplate(payroll, Optional.ofNullable(logo), Optional.ofNullable(language));
		template.print(out);
	}
	
	public static void printDefaultClassicPayroll(OutputStream out, DefaultPayroll payroll, InputStream logo, Locale language)
			throws CanNotCreatePdfException {
		IPayrollTemplate template = new DefaultPayrollTemplate(payroll, Optional.ofNullable(logo), Optional.ofNullable(language));
		template.print(out);
	}

	/**
	 * CREATE BUDGET
	 * 
	 * @param out
	 * @param budget
	 * @throws CanNotCreatePdfException
	 */
	public static void printBudget(OutputStream out, BudgetPrintConfiguration config) throws CanNotCreatePdfException {
		BudgetTemplate.print(out, config);
	}

	/**
	 * CREATE SETTLEMENT
	 * 
	 * @param out
	 * @param settlement
	 * @throws CanNotCreatePdfException
	 */
	public static void printSettlement(OutputStream out,SettlePrintConfiguration config)
			throws CanNotCreatePdfException {
		SettlementTemplate.print(out, config);
	}

}
