package com.esferalia.aon.in.payroll.pdf.creators;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Optional;

import com.esferalia.aon.in.payroll.pdf.creators.budget.BudgetTemplate;
import com.esferalia.aon.in.payroll.pdf.creators.budget.beans.Budget;
import com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll.EnterprisePayroll;
import com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll.EnterprisePayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.JsonParseException;
import com.esferalia.aon.in.payroll.pdf.creators.invoice.InvoiceMaker;
import com.esferalia.aon.in.payroll.pdf.creators.invoice.beans.Invoice;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.DefaultPayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayroll;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;


public class PdfMaker {

	//CREATE THE INVOICE WITH A JSON
	public static void print_invoice(InputStream json, PrintInvoiceConfiguration config, InputStream qr_code) throws CanNotCreatePdfException, JsonParseException {
		new InvoiceMaker().create_with_json(json, config, qr_code);
	}
	
	//CREATE THE INVOICE WITH A JAVA OBJECT
	public static void print_invoice(String name, Invoice bill_obj, boolean adapt_background) throws IOException, CanNotCreatePdfException {
		new InvoiceMaker().create(name, bill_obj, adapt_background);
	}
	
	//CREATE DEMO INVOICE
	public static void print_demo_invoice(PrintInvoiceConfiguration config, InputStream qr_code) throws IOException, CanNotCreatePdfException {
		new InvoiceMaker().demoPdf(config, qr_code);
	}
	
	//CREATE ENTERPRISE PAYROLL
	public static void print_enterprise_payroll(EnterprisePayroll payroll, String name) throws IOException {
		new EnterprisePayrollTemplate().print_enterprise_payroll(payroll, name);
	}
	
	//CREATE PAYROLL
	public static void print_default_payroll(String out, DefaultPayroll payroll, Optional<InputStream> logo, Optional<Locale> language) throws CanNotCreatePdfException {
		new DefaultPayrollTemplate().print(out, payroll, logo, language);
	}

	//CREATE BUDGET
	public static void print_budget(Budget budget) throws CanNotCreatePdfException {
		new BudgetTemplate().print("./buget.pdf",budget, Optional.of(new Locale("Es")));
	}
}
