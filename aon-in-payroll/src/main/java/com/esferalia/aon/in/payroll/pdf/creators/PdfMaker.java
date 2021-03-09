package com.esferalia.aon.in.payroll.pdf.creators;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Optional;

import com.esferalia.aon.in.payroll.pdf.creators.budget.BudgetTemplate;
import com.esferalia.aon.in.payroll.pdf.creators.budget.beans.Budget;
import com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll.EnterprisePayrollTemplateAuto;
import com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll.beans.EnterprisePayroll;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.JsonParseException;
import com.esferalia.aon.in.payroll.pdf.creators.invoice.InvoiceMaker;
import com.esferalia.aon.in.payroll.pdf.creators.invoice.beans.Invoice;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.DefaultPayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayroll;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;

public class PdfMaker {

	// CREATE THE INVOICE WITH A JSON
	public static void print_invoice(OutputStream out,InputStream json, PrintInvoiceConfiguration config, InputStream qr_code)
			throws CanNotCreatePdfException, JsonParseException {
		new InvoiceMaker().create_with_json(out,json, config, qr_code);
	}

	// CREATE THE INVOICE WITH A JAVA OBJECT
	public static void print_invoice(OutputStream out, Invoice bill_obj, boolean adapt_background)
			throws IOException, CanNotCreatePdfException {
		new InvoiceMaker().create(out, bill_obj, adapt_background);
	}

	// CREATE DEMO INVOICE
	public static void print_demo_invoice(OutputStream out,PrintInvoiceConfiguration config, InputStream qr_code)
			throws IOException, CanNotCreatePdfException {
		new InvoiceMaker().demoPdf(out,config, qr_code);
	}

	// CREATE ENTERPRISE PAYROLL
	public static void print_enterprise_payroll(EnterprisePayroll payroll, OutputStream out, Optional<Locale> locale)
			throws IOException, CanNotCreatePdfException {
		EnterprisePayrollTemplateAuto.print(payroll, out, locale);
	}

	// CREATE PAYROLL
	public static void print_default_payroll(OutputStream out, DefaultPayroll payroll, InputStream logo,
			Locale language) throws CanNotCreatePdfException {
		new DefaultPayrollTemplate().print(out, payroll, Optional.ofNullable(logo), Optional.ofNullable(language));
	}

	// CREATE BUDGET
	public static void print_budget(OutputStream out, Budget budget) throws CanNotCreatePdfException {
		BudgetTemplate.print(out, budget, Optional.of(new Locale("Es")));
	}
}
