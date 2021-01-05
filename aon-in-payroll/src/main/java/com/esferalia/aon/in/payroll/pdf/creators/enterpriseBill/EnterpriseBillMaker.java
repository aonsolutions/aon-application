package com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;

import com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill.beans.EnterpriseBill;
import com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill.beans.EnterpriseBillEntry;
import com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill.beans.EnterpriseBillFinance;
import com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill.beans.EnterpriseBillTax;
import com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill.templates.EnterpriseBillTemplate;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.JsonParseException;
import com.esferalia.aon.in.payroll.pdf.toolkits.PDFToolkit;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;

public class EnterpriseBillMaker {
	
	//CREATE THE PDF WITH A JSON
	public static void create_with_json(InputStream json, PrintInvoiceConfiguration config, InputStream qr_code) throws CanNotCreatePdfException, JsonParseException {

		JSONParser parser = new JSONParser();
		try {
			String text = new BufferedReader(new InputStreamReader(json, StandardCharsets.UTF_8))
					.lines()
					.collect(Collectors.joining("\n"));

			Object obj = parser.parse(text);

			JSONObject jsonObj = (JSONObject) obj;
			JSONObject receiver = (JSONObject) jsonObj.get("receiver");
			JSONObject address_info = (JSONObject) receiver.get("address");
			JSONArray taxes_info = (JSONArray) jsonObj.get("taxes");
			JSONArray finances_info = (JSONArray) jsonObj.get("finances");
			JSONArray entries_info = (JSONArray) jsonObj.get("details");

			String reference = (String) jsonObj.get("reference");
			Date date = PDFToolkit.parseDate("" + jsonObj.get("date"), "yyyy-MM-dd");
			String document = (String) receiver.get("document");
			String name = (String) receiver.get("name");
			String address = (String) address_info.get("address");
			String address_ln_2 = address_info.get("zip") + " " + address_info.get("city") + " " + address_info.get("province");

			ArrayList<EnterpriseBillTax> taxes = new ArrayList<>();
			ArrayList<EnterpriseBillFinance> finances = new ArrayList<>();
			ArrayList<EnterpriseBillEntry> entries = new ArrayList<>();

			for (Object tax : taxes_info) {
				JSONObject tax_obj = (JSONObject) tax;

				double base = Double.parseDouble("" + tax_obj.get("base"));
				double percent = Double.parseDouble("" + tax_obj.get("percentage"));
				String type = (String) tax_obj.get("type");
				double quota = Double.parseDouble("" + tax_obj.get("quota"));

				taxes.add(new EnterpriseBillTax(base, percent, type, quota));
			}
			for (Object finance : finances_info) {
				JSONObject finance_obj = (JSONObject) finance;

				Date due_date = PDFToolkit.parseDate("" + finance_obj.get("due_date"), "yyyy-MM-dd");
				String pay_method = (String) finance_obj.get("paymethod");
				String iban = (String) finance_obj.get("iban");
				double amount = Double.parseDouble("" + finance_obj.get("amount"));

				finances.add(new EnterpriseBillFinance(due_date, pay_method, iban, amount));
			}
			for (Object entry : entries_info) {
				JSONObject entry_obj = (JSONObject) entry;

				String description = (String) entry_obj.get("description");
				double quantity = Double.parseDouble("" + entry_obj.get("quantity"));
				double price = Double.parseDouble("" + entry_obj.get("price"));
				double discount = Double.parseDouble("" + entry_obj.get("discount"));
				double amount = Double.parseDouble("" + entry_obj.get("amount"));

				entries.add(new EnterpriseBillEntry(description, quantity, price, discount, amount));
			}

			EnterpriseBill bill_obj = new EnterpriseBill(
					config.getBackgroundImage(),
					config.getDetailed(),
					reference,
					date,
					document,
					name,
					address,
					address_ln_2,
					entries,
					taxes,
					finances,
					config.getFooter(),
					config.getHeader(),
					qr_code
			);
			
			EnterpriseBillTemplate.create("Invoice.pdf", bill_obj, config.getAdjustImage());
		} catch (CanNotCreatePdfException e) {throw e;}
		catch (Exception e) {
			throw new JsonParseException(e);
		}
	}
	
	//CREATE DEMO
	public static void demoPdf(PrintInvoiceConfiguration config, InputStream qr_code) throws IOException, CanNotCreatePdfException {
		EnterpriseBill bill_obj = new EnterpriseBill(
				config.getBackgroundImage(),
				config.getDetailed(),
				"",
				null,
				"",
				"EMPRESA DEMO.SL",
				"AVDA. PRINCIPAL, 50",
				"66666 CIUDAD PROVINCIA",
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				config.getFooter(),
				config.getHeader(),
				qr_code
		);

		EnterpriseBillTemplate.create("demo_bill.pdf", bill_obj, config.getAdjustImage());
	}
	
	//CREATE PDF WITH OBJECT
	public static void create(String name, EnterpriseBill bill_obj, boolean adapt_background) throws IOException, CanNotCreatePdfException {
		EnterpriseBillTemplate.create(name, bill_obj, adapt_background);
	}
	
}
