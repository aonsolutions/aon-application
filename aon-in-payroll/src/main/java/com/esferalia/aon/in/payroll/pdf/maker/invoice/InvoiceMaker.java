package com.esferalia.aon.in.payroll.pdf.maker.invoice;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.parseDate;
import static java.lang.Double.parseDouble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.exception.JsonParseException;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.Invoice;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.InvoiceEntry;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.InvoiceFinance;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;

public class InvoiceMaker {

	// CREATE THE PDF WITH A JSON
	public static void createWithJson(
			OutputStream out, InputStream json, PrintInvoiceConfiguration config, InputStream qrCode
	) throws CanNotCreatePdfException, JsonParseException {

		JSONParser parser = new JSONParser();
		try
		{
			String text = new BufferedReader(new InputStreamReader(json, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
			Object obj = parser.parse(text);

			JSONObject jsonObj		= (JSONObject) obj;
			JSONObject receiver		= (JSONObject) jsonObj.get("receiver");
			JSONObject addressInfo	= (JSONObject) receiver.get("address");
			JSONArray  taxesInfo	= (JSONArray) jsonObj.get("taxes");
			JSONArray  financesInfo	= (JSONArray) jsonObj.get("finances");
			JSONArray  entriesInfo	= (JSONArray) jsonObj.get("details");

			String reference  = (String) jsonObj.get("reference");
			Date   date		  = PdfFormats.parseDate("" + jsonObj.get("date"), "yyyy-MM-dd");
			String document	  = (String) receiver.get("document");
			String name		  = (String) receiver.get("name");
			String address	  = (String) addressInfo.get("address");
			String addressLn2 = addressInfo.get("zip") + " " + addressInfo.get("city") + " " + addressInfo.get("province");

			ArrayList<InvoiceTax>	  taxes	   = new ArrayList<>();
			ArrayList<InvoiceFinance> finances = new ArrayList<>();
			ArrayList<InvoiceEntry>	  entries  = new ArrayList<>();

			for (Object tax : taxesInfo)
			{
				JSONObject taxObj = (JSONObject) tax;

				double base	   = parseDouble("" + taxObj.get("base"));
				double percent = parseDouble("" + taxObj.get("percentage"));
				String type	   = (String) taxObj.get("type");
				double quota   = parseDouble("" + taxObj.get("quota"));

				taxes.add(new InvoiceTax(base, percent, type, quota));
			}
			for (Object finance : financesInfo)
			{
				JSONObject financeObj = (JSONObject) finance;

				Date   dueDate	  = parseDate("" + financeObj.get("due_date"), "yyyy-MM-dd");
				String payMethod  = (String) financeObj.get("paymethod");
				String iban		  = (String) financeObj.get("iban");
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

			InvoiceTemplate.create(out, invoiceObj, config.getAdjustImage());
		} catch (CanNotCreatePdfException e)
		{
			throw e;
		} catch (Exception e)
		{
			throw new JsonParseException(e);
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
