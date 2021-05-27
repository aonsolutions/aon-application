package com.esferalia.aon.in.payroll.pdf.maker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Optional;

import com.esferalia.aon.in.payroll.pdf.maker.budget.BudgetPrintConfiguration;
import com.esferalia.aon.in.payroll.pdf.maker.budget.BudgetTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.EnterprisePayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayroll;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.exception.JsonParseException;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceMaker;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.Invoice;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.SettlePrintConfiguration;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.SettlementTemplate;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;

public class PdfMaker {

	/**
	 * CREATE THE INVOICE WITH A JSON
	 * 
	 * @param out
	 * @param json
	 * @param config
	 * @param qrCode
	 * @throws CanNotCreatePdfException
	 * @throws JsonParseException
	 */
	public static void printInvoice(OutputStream out, InputStream json, PrintInvoiceConfiguration config,
			InputStream qrCode) throws CanNotCreatePdfException, JsonParseException {
		InvoiceMaker.createWithJson(out, json, config, qrCode);
	}

	/**
	 * CREATE THE INVOICE WITH A JAVA OBJECT
	 * 
	 * @param out
	 * @param invoice
	 * @param adaptBackground
	 * @throws IOException
	 * @throws CanNotCreatePdfException
	 */
	public static void printInvoice(OutputStream out, Invoice invoice, boolean adaptBackground)
			throws IOException, CanNotCreatePdfException {
		InvoiceMaker.create(out, invoice, adaptBackground);
	}

	/**
	 * CREATE DEMO INVOICE
	 * 
	 * @param out
	 * @param config
	 * @param qrCode
	 * @throws IOException
	 * @throws CanNotCreatePdfException
	 */
	public static void printDemoInvoice(OutputStream out, PrintInvoiceConfiguration config, InputStream qrCode)
			throws IOException, CanNotCreatePdfException {
		InvoiceMaker.demoPdf(out, config, qrCode);
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
		PayrollTemplate.print(out, payroll, Optional.ofNullable(logo), Optional.ofNullable(language));
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
