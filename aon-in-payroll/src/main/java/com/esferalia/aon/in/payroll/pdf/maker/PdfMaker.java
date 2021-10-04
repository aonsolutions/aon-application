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
import com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.SettlePrintConfiguration;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.SettlementTemplate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;

public class PdfMaker {

	/**
	 * CREATE INVOICE PDF
	 * 
	 * @param out
	 * @param invoice, Invoice Object
	 * @param config, print invoice configuration
	 */
	public static void printInvoice(OutputStream out, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config, byte[] qr, byte[] logo) {
		try {
			InvoiceTemplate.create(out, company, invoice, config, qr, logo);
		} catch (IOException | CanNotCreatePdfException e) {
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
