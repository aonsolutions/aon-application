package com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill;

import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.JsonParseException;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnterpriseBillTemplate {

	private static String filename = "./EnterpriseBill.pdf";
	private static float height;
	private static float width;
	private static float top;
	private static float bottom;
	private static float x;
	private static float y;
	private final static float top_info_height = 140;
	private final static float bottom_info_height = 140;
	private static float limit;

	private static byte[] background;
	private static byte[] qr_code;

	private static boolean adapt;
	private static PDPageContentStream contents;
	private static EnterpriseBill bill;

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

			EnterpriseBill bill = new EnterpriseBill(
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

			create("JSON_Bill_test.pdf", bill, config.getAdjustImage());
		} catch (CanNotCreatePdfException e) {
			throw e;
		} catch (Exception e) {
			throw new JsonParseException(e);
		}
	}

	//CREATE DEMO
	public static void demoPdf(PrintInvoiceConfiguration config, InputStream qr_code) throws IOException, CanNotCreatePdfException {
		EnterpriseBill bill = new EnterpriseBill(
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

		create("demo_bill.pdf", bill, config.getAdjustImage());
	}

	//CREATE THE PDF DOCUMENT
	public static void create(String name, EnterpriseBill bill_obj, boolean adapt_background) throws IOException, CanNotCreatePdfException {
		try (PDDocument doc = new PDDocument()) {

			if (name != null) 		filename = name;
			if (bill_obj == null) 	throw new CanNotCreatePdfException("No bill found.");
			bill = bill_obj;

			adapt = 	adapt_background;
			top = 		(float) bill.getTop_px();
			bottom = 	(float) bill.getBottom_px();

			if (bill.getBackground() != null) 	background = bill.getBackground().readAllBytes();
			if (bill.getQr_code() != null) 		qr_code = bill.getQr_code().readAllBytes();

			contents = draw_page(doc);
			y = height - top - top_info_height - 5;

			if (bill.isDetailed()) 	draw_detailed_entries(doc);
			else  					draw_simplified_entries(doc);

			contents.close();
			doc.save(filename);
		} catch (Exception e) {throw new CanNotCreatePdfException(e);}
	}

	//DRAW PAGE
	private static PDPageContentStream draw_page(PDDocument doc) throws IOException {
		PDPage page = PDFToolkit.createVerticalPage();
		doc.addPage(page);

		contents = new PDPageContentStream(doc, page);
		if (background != null)
			if (adapt)	PDFToolkit.drawImage(doc, contents, background, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
			else 		PDFToolkit.drawImage(doc, contents, background, 0, 0);

		width = 	page.getMediaBox().getWidth();
		height = 	page.getMediaBox().getHeight();
		limit = 	bottom_info_height + bottom;

		x = 50f;
		y = height - top - 20;

		draw_top_info();

		if (bill.isDetailed()) 	draw_detailed_header();
		else 					draw_simple_header();

		draw_bottom_info(doc);
		return contents;
	}

	//DRAW DETAILED ENTRIES
	public static void draw_detailed_entries(PDDocument doc) throws IOException {
		if (bill.getEntries() != null) {
			for (EnterpriseBillEntry entry : bill.getEntries()) {
				x = 50;
				if (y <= limit) {
					contents.close();
					contents = draw_page(doc);
					y = height - top - top_info_height - 5;
					x = 50;
				}

				PDFToolkit.drawText(contents, PDFToolkit.cropped_string(entry.getDescription(), 240, PDFToolkit.HELVETICA, 8), x + 5, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 8);
				x += 250;

				PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 69, 15), PDFToolkit.to_latin_number(entry.getQuantity()) + "", PDFToolkit.BLACK, PDFToolkit.HELVETICA, 8, 4.5f, 0);
				x += 70;

				PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 69, 15), PDFToolkit.to_latin_number(entry.getPrice()), PDFToolkit.BLACK, PDFToolkit.HELVETICA, 8, 4.5f, 0);
				x += 70;

				String percent = "";
				if (entry.getPercent() > 99)			percent = "100%";
				else if (entry.getPercent() != 0) 		percent = PDFToolkit.to_latin_number(entry.getPercent()) + "%";


				PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 39, 15), percent, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 8, 4.5f, 0);
				x += 40;

				PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 69, 15), PDFToolkit.to_latin_number(entry.getAmount()), PDFToolkit.BLACK, PDFToolkit.HELVETICA, 8, 4.5f, 0);
				y -= 10;
			}
		}
	}

	//DRAW SIMPLIFIED ENTRIES
	public static void draw_simplified_entries(PDDocument doc) throws IOException {
		if (bill.getEntries() != null) {
			for (EnterpriseBillEntry entry : bill.getEntries()) {
				x = 50;
				if (y <= limit) {
					contents.close();
					contents = draw_page(doc);
					y = height - top - top_info_height - 5;
					x = 50;
				}

				PDFToolkit.drawText(contents, PDFToolkit.cropped_string(entry.getDescription(), 420, PDFToolkit.HELVETICA, 8), x + 5, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 8);
				x += 430;
				PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 69, 15), PDFToolkit.to_latin_number(entry.getAmount()), PDFToolkit.BLACK, PDFToolkit.HELVETICA, 8, 4.5f, 0);
				y -= 10;
			}
		}
	}

	//DRAW UPPER INFO
	private static void draw_top_info() throws IOException {
		PDFToolkit.drawText(contents, "FACTURA", x, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 16);
		y -= 30;

		PDFToolkit.drawText(contents, "Numero: " + bill.getReference(), x, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 11);
		y -= 4;

		PDFToolkit.drawBox(contents, x, y, 200, .5f, PDFToolkit.BLACK);
		y -= 16;

		if (bill.getDate() != null)
			PDFToolkit.drawText(contents, "Fecha: " + PDFToolkit.formatDate(bill.getDate(), "dd,MM,yyyy").get(), x, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 11);
		else PDFToolkit.drawText(contents, "Fecha: ", x, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 11);
		y -= 4;

		PDFToolkit.drawBox(contents, x, y, 200, .5f, PDFToolkit.BLACK);
		y -= 16;

		PDFToolkit.drawText(contents, "N.I.F: " + bill.getDocument(), x, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 11);
		y -= 4;

		PDFToolkit.drawBox(contents, x, y, 200, .5f, PDFToolkit.BLACK);
		y -= 6;
		x += 250;

		PDFToolkit.drawBox(contents, x, y, 250, 80, PDFToolkit.LIGHT_GRAY);
		x += 10;
		y = height - top - 45;

		PDFToolkit.drawText(contents, bill.getName(), x, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 12);
		y -= 15;

		PDFToolkit.drawText(contents, bill.getAddress(), x, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 9);
		y -= 10;

		PDFToolkit.drawText(contents, bill.getZip_city_province(), x, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 9);
	}

	//DRAW DETAILED HEADER
	private static void draw_detailed_header() throws IOException {
		y -= 60;
		x = 50;

		PDFToolkit.drawBox(contents, x, y, 249, 15, PDFToolkit.BLACK);
		PDFToolkit.drawTextCenter(contents, new PDRectangle(x, y, 249, 15), "Descripción", PDFToolkit.WHITE, PDFToolkit.HELVETICA_BOLD, 9, 4.5f);
		x += 250;

		PDFToolkit.drawBox(contents, x, y, 69, 15, PDFToolkit.BLACK);
		PDFToolkit.drawTextCenter(contents, new PDRectangle(x, y, 69, 15), "Cantidad", PDFToolkit.WHITE, PDFToolkit.HELVETICA_BOLD, 9, 4.5f);
		x += 70;

		PDFToolkit.drawBox(contents, x, y, 69, 15, PDFToolkit.BLACK);
		PDFToolkit.drawTextCenter(contents, new PDRectangle(x, y, 69, 15), "Precio", PDFToolkit.WHITE, PDFToolkit.HELVETICA_BOLD, 9, 4.5f);
		x += 70;

		PDFToolkit.drawBox(contents, x, y, 39, 15, PDFToolkit.BLACK);
		PDFToolkit.drawTextCenter(contents, new PDRectangle(x, y, 39, 15), "%Dto.", PDFToolkit.WHITE, PDFToolkit.HELVETICA_BOLD, 9, 4.5f);
		x += 40;

		PDFToolkit.drawBox(contents, x, y, 69, 15, PDFToolkit.BLACK);
		PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 69, 15), "Importe", PDFToolkit.WHITE, PDFToolkit.HELVETICA_BOLD, 9, 5, 4.5f);
	}

	//DRAW SIMPLE HEADER
	private static void draw_simple_header() throws IOException {
		y -= 60;
		x = 50;
		PDFToolkit.drawBox(contents, x, y, 429, 15, PDFToolkit.BLACK);
		PDFToolkit.drawText(contents, "Descripción", x + 5f, y + 4.5f, PDFToolkit.WHITE, PDFToolkit.HELVETICA_BOLD, 9);
		x += 430;

		PDFToolkit.drawBox(contents, x, y, 69, 15, PDFToolkit.BLACK);
		PDFToolkit.drawText(contents, "Importe", x + 5f, y + 4.5f, PDFToolkit.WHITE, PDFToolkit.HELVETICA_BOLD, 9);
	}

	//DRAW BOTTOM INFO
	private static void draw_bottom_info(PDDocument doc) throws IOException {
		x = 50;
		y = bottom + 10;
		if (qr_code != null) PDFToolkit.drawImage(doc, contents, qr_code, x, y, 120, 120);

		draw_taxes();
		draw_finances();
	}

	//DRAW TAXES
	private static void draw_taxes() throws IOException {

		x = 240;
		y = bottom + 107;

		PDFToolkit.drawBox(contents, x, y, 79, 15, PDFToolkit.BLACK);
		PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 79, 15), "Base", PDFToolkit.WHITE, PDFToolkit.HELVETICA, 9, 5, 4.5f);
		x += 80;

		PDFToolkit.drawBox(contents, x, y, 49, 15, PDFToolkit.BLACK);
		PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 49, 15), "%", PDFToolkit.WHITE, PDFToolkit.HELVETICA, 9, 5, 4.5f);
		x += 50;

		PDFToolkit.drawBox(contents, x, y, 59, 15, PDFToolkit.BLACK);
		PDFToolkit.drawTextCenter(contents, new PDRectangle(x, y, 59, 15), "Tipo", PDFToolkit.WHITE, PDFToolkit.HELVETICA, 9, 4.5f);
		x += 60;

		PDFToolkit.drawBox(contents, x, y, 49, 15, PDFToolkit.BLACK);
		PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 49, 15), "Cuota", PDFToolkit.WHITE, PDFToolkit.HELVETICA, 9, 5, 4.5f);
		x += 50;

		PDFToolkit.drawBox(contents, x, y, 69, 15, PDFToolkit.BLACK);
		PDFToolkit.drawTextCenter(contents, new PDRectangle(x, y, 69, 15), "Total factura", PDFToolkit.WHITE, PDFToolkit.HELVETICA_BOLD, 9, 4.5f);

		double sum = 0;
		for (EnterpriseBillTax tax : bill.getTaxes()) {
			x = 240;
			PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 79, 15), PDFToolkit.to_latin_number(tax.getBase()), PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, -12);
			x += 80;

			String percent = "";
			if (tax.getPercentage() > 99) percent = "100%";
			else if (tax.getPercentage() != 0) percent = PDFToolkit.to_latin_number(tax.getPercentage()) + "%";

			PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 49, 15), percent, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, -12);
			x += 50;
			PDFToolkit.drawTextCenter(contents, new PDRectangle(x, y, 59, 15), tax.getType(), PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, -12);
			x += 60;
			PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 49, 15), PDFToolkit.to_latin_number(tax.getQuota()), PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, -12);
			x += 50;

			sum += tax.getQuota() + tax.getBase();
			y -= 10;
		}

		PDFToolkit.drawTextRight(contents, new PDRectangle(x, bottom + 107, 69, 15), PDFToolkit.to_latin_number(sum) + " \u20AC", PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 8, 5, -14);
	}

	//DRAW FINANCES
	private static void draw_finances() throws IOException {
		x = 180;
		y = bottom + 50;

		PDFToolkit.drawBox(contents, x, y, 59, 15, PDFToolkit.BLACK);
		PDFToolkit.drawText(contents, "Fecha", x + 5f, y + 4.5f, PDFToolkit.WHITE, PDFToolkit.HELVETICA, 9);
		x += 60;

		PDFToolkit.drawBox(contents, x, y, 79, 15, PDFToolkit.BLACK);
		PDFToolkit.drawText(contents, "Forma de pago", x + 5f, y + 4.5f, PDFToolkit.WHITE, PDFToolkit.HELVETICA, 9);
		x += 80;

		PDFToolkit.drawBox(contents, x, y, 159, 15, PDFToolkit.BLACK);
		PDFToolkit.drawText(contents, "Cuenta Bancaria", x + 5f, y + 4.5f, PDFToolkit.WHITE, PDFToolkit.HELVETICA, 9);
		x += 160;

		PDFToolkit.drawBox(contents, x, y, 69, 15, PDFToolkit.BLACK);
		PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 69, 15), "Importe", PDFToolkit.WHITE, PDFToolkit.HELVETICA, 9, 5, 4.5f);


		for (EnterpriseBillFinance finance : bill.getFinances()) {
			x = 180;
			PDFToolkit.drawText(contents, PDFToolkit.formatDate(finance.getDue_date(), "dd/MM/yyyy").get(), x + 5f, y - 12, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			x += 60;
			PDFToolkit.drawText(contents, finance.getPaymethod(), x + 5f, y - 12, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			x += 80;
			PDFToolkit.drawText(contents, finance.getIban(), x + 5f, y - 12, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			x += 160;
			PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 69, 15), PDFToolkit.to_latin_number(finance.getAmount()), PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, -12);

			y -= 10;
		}


	}

}
