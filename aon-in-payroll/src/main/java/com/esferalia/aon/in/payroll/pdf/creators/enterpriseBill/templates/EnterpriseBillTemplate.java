package com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill.templates;

import com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill.beans.EnterpriseBill;
import com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill.beans.EnterpriseBillEntry;
import com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill.beans.EnterpriseBillFinance;
import com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill.beans.EnterpriseBillTax;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.toolkits.PDFToolkit;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.*;
import java.util.ArrayList;

public class EnterpriseBillTemplate {

	String filename = "./EnterpriseBill.pdf";
	float height;
	float top;
	float bottom;
	float x;
	float y;
	float top_info_height = 140;
	float bottom_info_height = 140;
	float limit;

	byte[] background;
	byte[] qr_code;

	boolean adapt;
	PDPageContentStream contents;
	EnterpriseBill bill;


	//THE PDF DOCUMENT
	public static void create(String name, EnterpriseBill bill_obj, boolean adapt_background) throws IOException, CanNotCreatePdfException {
		try (PDDocument doc = new PDDocument()) {
			

			EnterpriseBillTemplate template = new EnterpriseBillTemplate();
			
			
			if (name != null) 		template.filename = name;
			if (bill_obj == null) 	throw new CanNotCreatePdfException("No bill found.");
			template.bill = bill_obj;

			template.adapt = 	adapt_background;
			template.top = 		(float) template.bill.getTop_px();
			template.bottom = 	(float) template.bill.getBottom_px();

			if (template.bill.getBackground() != null) 	template.background = template.bill.getBackground().readAllBytes();
			if (template.bill.getQr_code() != null) 		template.qr_code = template.bill.getQr_code().readAllBytes();

			template.contents = template.draw_page(doc);
			template.y = template.height - template.top - template.top_info_height - 5;

			if (template.bill.isDetailed()) 	template.draw_detailed_entries(doc);
			else  					template.draw_simplified_entries(doc);

			template.draw_bottom_info(doc);
			template.contents.close();
			doc.save(template.filename);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CanNotCreatePdfException(e);}
	}

	//DRAW PAGE
	private PDPageContentStream draw_page(PDDocument doc) throws IOException {
		PDPage page = PDFToolkit.createVerticalPage();
		doc.addPage(page);

		contents = new PDPageContentStream(doc, page);
		if (background != null)
			if (adapt)	PDFToolkit.drawImage(doc, contents, background, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
			else 		PDFToolkit.drawImage(doc, contents, background, 0, 0);

		height = 	page.getMediaBox().getHeight();
		limit = 	bottom_info_height + bottom;

		x = 50f;
		y = height - top - 20;

		draw_top_info();

		if (bill.isDetailed()) 	draw_detailed_header();
		else 					draw_simple_header();

		return contents;
	}

	//DRAW DETAILED ENTRIES
	public void draw_detailed_entries(PDDocument doc) throws IOException {
		if (bill.getEntries() != null) {
			for (EnterpriseBillEntry entry : bill.getEntries()) {
				x = 50;
				if (y <= limit) {
					contents.close();
					contents = draw_page(doc);
					y = height - top - top_info_height - 5;
					x = 50;
				}
				
				ArrayList<String> divided = (ArrayList<String>) PDFToolkit.divide_string_to_fit(entry.getDescription(), 240, PDFToolkit.HELVETICA, 8);
				
				float dy = y;
				
				for (String str : divided) {
					PDFToolkit.drawText(contents,str, x + 5, dy, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 8);
					dy -= 10;
				}
				
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
				y = dy - 10;
				
			}
		}
	}

	//DRAW SIMPLIFIED ENTRIES
	public void draw_simplified_entries(PDDocument doc) throws IOException {
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
	private void draw_top_info() throws IOException {
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
	private void draw_detailed_header() throws IOException {
		y -= 60;
		x = 50;

		PDFToolkit.drawBox(contents, x, y, 249, 15, PDFToolkit.BLACK);
		PDFToolkit.drawTextCenter(contents, new PDRectangle(x, y, 249, 15), "Descripci"+ "\u00F3" + "n", PDFToolkit.WHITE, PDFToolkit.HELVETICA_BOLD, 9, 4.5f);
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
	private void draw_simple_header() throws IOException {
		y -= 60;
		x = 50;
		PDFToolkit.drawBox(contents, x, y, 429, 15, PDFToolkit.BLACK);
		PDFToolkit.drawText(contents, "Descripción", x + 5f, y + 4.5f, PDFToolkit.WHITE, PDFToolkit.HELVETICA_BOLD, 9);
		x += 430;

		PDFToolkit.drawBox(contents, x, y, 69, 15, PDFToolkit.BLACK);
		PDFToolkit.drawText(contents, "Importe", x + 5f, y + 4.5f, PDFToolkit.WHITE, PDFToolkit.HELVETICA_BOLD, 9);
	}

	//DRAW BOTTOM INFO
	private void draw_bottom_info(PDDocument doc) throws IOException {
		x = 50;
		y = bottom + 10;
		if (qr_code != null) PDFToolkit.drawImage(doc, contents, qr_code, x, y, 120, 120);

		draw_taxes();
		draw_finances();
	}

	//DRAW TAXES
	private void draw_taxes() throws IOException {

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
	private void draw_finances() throws IOException {
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
