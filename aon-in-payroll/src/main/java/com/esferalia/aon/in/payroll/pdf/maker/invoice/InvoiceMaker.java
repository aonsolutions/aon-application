package com.esferalia.aon.in.payroll.pdf.maker.invoice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.exception.JsonParseException;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.Invoice;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.InvoiceEntry;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.InvoiceFinance;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.InvoiceTax;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;

import es.translogia.tedi.json.TediJSONUtils;

public class InvoiceMaker {

	// CREATE THE PDF WITH A JSON
	public static void createWithJson(
			OutputStream out, InputStream json, PrintInvoiceConfiguration config, InputStream qrCode
	) throws CanNotCreatePdfException, JsonParseException {
		try {
			String text = new BufferedReader(new InputStreamReader(json, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
			JSONObject jsonObj = new JSONObject(text);
			createWithJson(out, jsonObj, config, qrCode);
		} catch (Exception e) {
			throw new JsonParseException(e);
		}
	}
	
	public static void createWithJson(OutputStream out, JSONObject jsonObj, PrintInvoiceConfiguration config, InputStream qrCode) {
		JSONObject receiver		= jsonObj.optJSONObject(IJsonNames.RECEIVER);
		JSONObject addressInfo	= receiver.optJSONObject(IJsonNames.ADDRESS);
		JSONArray  taxesInfo	= jsonObj.optJSONArray(IJsonNames.TAXES);
		JSONArray  financesInfo	= jsonObj.optJSONArray(IJsonNames.FINANCES);
		JSONArray  entriesInfo	= jsonObj.optJSONArray(IJsonNames.DETAILS);

		String reference  = JsonUtils.getString(jsonObj, IJsonNames.REFERENCE);
		Date   date		  = PdfFormats.parseDate("" + jsonObj.get("date"), "yyyy-MM-dd");
		String document	  = JsonUtils.getString(receiver, IJsonNames.DOCUMENT);
		String name		  = JsonUtils.getString(receiver, IJsonNames.NAME);
		String address	  = JsonUtils.getString(addressInfo, IJsonNames.ADDRESS);
		String zip = JsonUtils.optString(addressInfo, IJsonNames.ZIP);
		String city = JsonUtils.optString(addressInfo, IJsonNames.CITY);
		String province = JsonUtils.optString(addressInfo, IJsonNames.PROVINCE);
		String addressLn2 = zip + " " + city + " " + province;

		ArrayList<InvoiceTax>	  taxes	   = new ArrayList<>();
		ArrayList<InvoiceFinance> finances = new ArrayList<>();
		ArrayList<InvoiceEntry>	  entries  = new ArrayList<>();

		for (Object tax : taxesInfo)
		{
			JSONObject taxObj = (JSONObject) tax;

			
			
			Double base = JsonUtils.getDouble(taxObj, IJsonNames.BASE);
			Double percent = JsonUtils.getDouble(taxObj, IJsonNames.PERCENTAGE);
			String type = JsonUtils.getString(taxObj, IJsonNames.TYPE);
			Double quota   = JsonUtils.getDouble(taxObj, IJsonNames.QUOTA);

			taxes.add(new InvoiceTax(base, percent, type, quota));
		}
		for (Object finance : financesInfo)
		{
			JSONObject financeObj = (JSONObject) finance;
			
			Date dueDate = TediJSONUtils.parseDate(financeObj.optString(IJsonNames.DUE_DATE));
			String payMethod = (String) financeObj.get("paymethod");
			String iban = JsonUtils.getString(financeObj, IJsonNames.IBAN);
			double amount	  = Double.parseDouble("" + financeObj.get("amount"));

			finances.add(new InvoiceFinance(dueDate, payMethod, iban, amount));
		}
		for (Object entry : entriesInfo)
		{
			JSONObject entryObj = (JSONObject) entry;

			String description = (String) entryObj.get("description");
			double quantity	   = Double.parseDouble("" + entryObj.get("quantity"));
			double price	   = Double.parseDouble("" + entryObj.get("price"));
			double discount	   = Double.parseDouble("" + entryObj.get("discount"));
			double amount	   = Double.parseDouble("" + entryObj.get("amount"));

			entries.add(new InvoiceEntry(description, quantity, price, discount, amount));
		}

		Invoice invoiceObj = new Invoice(config.getBackgroundImage(), config.getDetailed(), reference, date, document,
				name, address, addressLn2, entries, taxes, finances, config.getFooter(), config.getHeader(),
				qrCode);

		try {
			InvoiceTemplate.create(out, invoiceObj, config.getAdjustImage());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CanNotCreatePdfException e) {
			e.printStackTrace();
		}
	}
	// CREATE DEMO
	public static void demoPdf(OutputStream out, PrintInvoiceConfiguration config, InputStream qrCode)
			throws IOException, CanNotCreatePdfException {
		Invoice billObj = new Invoice(config.getBackgroundImage(), config.getDetailed(), "", null, "",
				"EMPRESA DEMO.SL", "AVDA. PRINCIPAL, 50", "66666 CIUDAD PROVINCIA", new ArrayList<>(),
				new ArrayList<>(), new ArrayList<>(), config.getFooter(), config.getHeader(), qrCode);

		InvoiceTemplate.create(out, billObj, config.getAdjustImage());
	}

	// CREATE PDF WITH OBJECT
	public static void create(OutputStream os, Invoice invoiceObj, boolean adaptBackground)
			throws IOException, CanNotCreatePdfException {
		InvoiceTemplate.create(os, invoiceObj, adaptBackground);
	}

}
