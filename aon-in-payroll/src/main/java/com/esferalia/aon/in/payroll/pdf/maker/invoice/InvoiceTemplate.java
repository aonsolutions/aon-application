package com.esferalia.aon.in.payroll.pdf.maker.invoice;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.Invoice;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.InvoiceEntry;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.InvoiceFinance;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.InvoiceTax;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.LIGHT_GRAY;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.WHITE;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.formatDate;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.toLatinNumber;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.createVerticalPage;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawBox;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawImage;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawText;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawTextCenter;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawTextRight;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.getLines;

import java.io.*;
import java.util.ArrayList;

public class InvoiceTemplate {

	OutputStream filename;
	float		 height;
	float		 top;
	float		 bottom;
	float		 x;
	float		 y;
	float		 topInfoHeight	  = 140;
	float		 bottomInfoHeight = 140;
	float		 limit;

	byte[] background;
	byte[] qrCode;

	boolean				adapt;
	PDPageContentStream	contents;
	Invoice				invoice;

	// THE PDF DOCUMENT
	public static void create(OutputStream os, Invoice invoice, boolean adaptBackground)
			throws IOException, CanNotCreatePdfException {
		try (PDDocument doc = new PDDocument())
		{
			InvoiceTemplate template = new InvoiceTemplate();

			if (os != null)
				template.filename = os;
			if (invoice == null)
				throw new CanNotCreatePdfException("No bill found.");
			template.invoice = invoice;

			template.adapt	= adaptBackground;
			template.top	= (float) template.invoice.getTopPx();
			template.bottom	= (float) template.invoice.getBottomPx();

			if (template.invoice.getBackground() != null)
				template.background = template.invoice.getBackground().readAllBytes();
			if (template.invoice.getQrCode() != null)
				template.qrCode = template.invoice.getQrCode().readAllBytes();

			template.contents = template.drawPage(doc);
			template.y		  = template.height - template.top - template.topInfoHeight - 5;

			if (template.invoice.isDetailed())
				template.drawDetailedEntries(doc);
			else
				template.draw_simplified_entries(doc);

			template.drawBottomInfo(doc);
			template.contents.close();
			doc.save(template.filename);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new CanNotCreatePdfException(e);
		}
	}

	// DRAW PAGE
	private PDPageContentStream drawPage(PDDocument doc) throws IOException {
		PDPage page = createVerticalPage();
		doc.addPage(page);

		contents = new PDPageContentStream(doc, page);
		if (background != null)
			if (adapt)
				drawImage(doc, contents, background, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
			else
				drawImage(doc, contents, background, 0, 0);

		height = page.getMediaBox().getHeight();
		limit  = bottomInfoHeight + bottom;

		x = 50f;
		y = height - top - 20;

		drawTopInfo();

		if (invoice.isDetailed())
			drawDetailedHeader();
		else
			drawSimpleHeader();

		return contents;
	}

	// DRAW DETAILED ENTRIES
	public void drawDetailedEntries(PDDocument doc) throws IOException {
		if (invoice.getEntries() != null)
		{
			for (InvoiceEntry entry : invoice.getEntries())
			{
				x = 50;
				if (y <= limit)
				{
					contents.close();
					contents = drawPage(doc);
					y		 = height - top - topInfoHeight - 5;
					x		 = 50;
				}

				ArrayList<String> divided = (ArrayList<String>) getLines(entry.getDescription(), 240,
						PdfFonts.HELVETICA, 8);

				float dy = y;

				for (String str : divided)
				{
					drawText(contents, str, x + 5, dy, PdfColors.BLACK, PdfFonts.HELVETICA, 8);
					dy -= 10;
				}

				x += 250;

				drawTextRight(contents, new PDRectangle(x, y, 69, 15), toLatinNumber(entry.getQuantity()) + "", BLACK, HELVETICA, 8, 4.5f, 0);
				x += 70;

				drawTextRight(contents, new PDRectangle(x, y, 69, 15),toLatinNumber(entry.getPrice()), BLACK, HELVETICA, 8, 4.5f, 0);
				x += 70;

				String percent = "";
				if (entry.getPercent() > 99)
					percent = "100%";
				else if (entry.getPercent() != 0)
					percent = toLatinNumber(entry.getPercent()) + "%";

				drawTextRight(contents, new PDRectangle(x, y, 39, 15), percent, BLACK, HELVETICA, 8, 4.5f, 0);
				x += 40;

				drawTextRight(contents, new PDRectangle(x, y, 69, 15), toLatinNumber(entry.getAmount()), BLACK, HELVETICA, 8, 4.5f, 0);
				y = dy - 10;

			}
		}
	}

	// DRAW SIMPLIFIED ENTRIES
	public void draw_simplified_entries(PDDocument doc) throws IOException {
		if (invoice.getEntries() != null)
		{
			for (InvoiceEntry entry : invoice.getEntries())
			{
				x = 50;
				if (y <= limit)
				{
					contents.close();
					contents = drawPage(doc);
					y		 = height - top - topInfoHeight - 5;
					x		 = 50;
				}

				PDFToolkit.drawText(contents,
						PDFToolkit.croppedString(entry.getDescription(), 420, PdfFonts.HELVETICA, 8), x + 5, y,
						PdfColors.BLACK, PdfFonts.HELVETICA, 8);
				x += 430;
				PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 69, 15),
						PdfFormats.toLatinNumber(entry.getAmount()), PdfColors.BLACK, PdfFonts.HELVETICA, 8, 4.5f, 0);
				y -= 10;
			}
		}
	}

	// DRAW UPPER INFO
	private void drawTopInfo() throws IOException {
		drawText(contents, "FACTURA", x, y, BLACK, HELVETICA_BOLD, 16);
		y -= 30;

		drawText(contents, "Numero: " + invoice.getReference(), x, y, BLACK, HELVETICA, 11);
		y -= 4;

		drawBox(contents, x, y, 200, .5f, BLACK);
		y -= 16;

		if (invoice.getDate() != null)
			drawText(contents, "Fecha: " + formatDate(invoice.getDate(), "dd,MM,yyyy").get(), x, y,BLACK, HELVETICA, 11);
		else
			drawText(contents, "Fecha: ", x, y, BLACK, HELVETICA, 11);
		y -= 4;

		drawBox(contents, x, y, 200, .5f, BLACK);
		y -= 16;

		drawText(contents, "N.I.F: " + invoice.getDocument(), x, y, BLACK, HELVETICA, 11);
		y -= 4;

		drawBox(contents, x, y, 200, .5f, BLACK);
		y -= 6;
		x += 250;

		drawBox(contents, x, y, 250, 80, LIGHT_GRAY);
		x += 10;
		y  = height - top - 45;

		drawText(contents, invoice.getName(), x, y, BLACK, HELVETICA_BOLD, 12);
		y -= 15;

		drawText(contents, invoice.getAddress(), x, y, BLACK, HELVETICA, 9);
		y -= 10;

		drawText(contents, invoice.getZipCityProvince(), x, y, BLACK, HELVETICA, 9);
	}

	// DRAW DETAILED HEADER
	private void drawDetailedHeader() throws IOException {
		y -= 60;
		x  = 50;

		drawBox(contents, x, y, 249, 15, BLACK);
		drawTextCenter(contents, new PDRectangle(x, y, 249, 15), "Descripci" + "\u00F3" + "n", WHITE, HELVETICA_BOLD, 9, 4.5f);
		x += 250;

		drawBox(contents, x, y, 69, 15, BLACK);
		drawTextCenter(contents, new PDRectangle(x, y, 69, 15), "Cantidad", WHITE, HELVETICA_BOLD, 9, 4.5f);
		x += 70;

		drawBox(contents, x, y, 69, 15, BLACK);
		drawTextCenter(contents, new PDRectangle(x, y, 69, 15), "Precio", WHITE, HELVETICA_BOLD, 9, 4.5f);
		x += 70;

		drawBox(contents, x, y, 39, 15, BLACK);
		drawTextCenter(contents, new PDRectangle(x, y, 39, 15), "%Dto.", WHITE, HELVETICA_BOLD, 9, 4.5f);
		x += 40;

		drawBox(contents, x, y, 69, 15, BLACK);
		drawTextRight(contents, new PDRectangle(x, y, 69, 15), "Importe", WHITE, HELVETICA_BOLD, 9, 5, 4.5f);
	}

	// DRAW SIMPLE HEADER
	private void drawSimpleHeader() throws IOException {
		y -= 60;
		x  = 50;
		drawBox(contents, x, y, 429, 15, BLACK);
		drawText(contents, "Descripción", x + 5f, y + 4.5f, WHITE, HELVETICA_BOLD, 9);
		x += 430;

		drawBox(contents, x, y, 69, 15, BLACK);
		drawText(contents, "Importe", x + 5f, y + 4.5f, WHITE, HELVETICA_BOLD, 9);
	}

	// DRAW BOTTOM INFO
	private void drawBottomInfo(PDDocument doc) throws IOException {
		x = 50;
		y = bottom + 10;
		if (qrCode != null)
			drawImage(doc, contents, qrCode, x, y, 120, 120);

		drawTaxes();
		drawFinances();
	}

	// DRAW TAXES
	private void drawTaxes() throws IOException {

		x = 240;
		y = bottom + 107;

		drawBox(contents, x, y, 79, 15, BLACK);
		drawTextRight(contents, new PDRectangle(x, y, 79, 15), "Base", WHITE, HELVETICA, 9, 5, 4.5f);
		x += 80;

		drawBox(contents, x, y, 49, 15, BLACK);
		drawTextRight(contents, new PDRectangle(x, y, 49, 15), "%", WHITE, HELVETICA, 9, 5, 4.5f);
		x += 50;

		drawBox(contents, x, y, 59, 15, BLACK);
		drawTextCenter(contents, new PDRectangle(x, y, 59, 15), "Tipo", WHITE, HELVETICA, 9, 4.5f);
		x += 60;

		drawBox(contents, x, y, 49, 15, BLACK);
		drawTextRight(contents, new PDRectangle(x, y, 49, 15), "Cuota", WHITE, HELVETICA, 9, 5, 4.5f);
		x += 50;

		drawBox(contents, x, y, 69, 15, BLACK);
		drawTextCenter(contents, new PDRectangle(x, y, 69, 15), "Total factura", WHITE, HELVETICA_BOLD, 9, 4.5f);

		double sum = 0;
		for (InvoiceTax tax : invoice.getTaxes())
		{
			x = 240;
			drawTextRight(contents, new PDRectangle(x, y, 79, 15), toLatinNumber(tax.getBase()), BLACK, HELVETICA, 7, 5, -12);
			x += 80;

			String percent = "";
			if (tax.getPercentage() > 99)
				percent = "100%";
			else if (tax.getPercentage() != 0)
				percent = toLatinNumber(tax.getPercentage()) + "%";

			drawTextRight(contents, new PDRectangle(x, y, 49, 15), percent, BLACK, HELVETICA, 7, 5, -12);
			x += 50;
			
			drawTextCenter(contents, new PDRectangle(x, y, 59, 15), tax.getType(), BLACK, HELVETICA, 7, -12);
			x += 60;
			
			drawTextRight(contents, new PDRectangle(x, y, 49, 15), toLatinNumber(tax.getQuota()), BLACK, HELVETICA, 7, 5, -12);
			x += 50;

			sum	+= tax.getQuota() + tax.getBase();
			y	-= 10;
		}

		drawTextRight(contents, new PDRectangle(x, bottom + 107, 69, 15), toLatinNumber(sum) + " \u20AC", BLACK, HELVETICA_BOLD, 8, 5, -14);
	}

	// DRAW FINANCES
	private void drawFinances() throws IOException {
		x = 180;
		y = bottom + 50;

		drawBox(contents, x, y, 59, 15, BLACK);
		drawText(contents, "Fecha", x + 5f, y + 4.5f, WHITE, HELVETICA, 9);
		x += 60;

		drawBox(contents, x, y, 79, 15, BLACK);
		drawText(contents, "Forma de pago", x + 5f, y + 4.5f, WHITE, HELVETICA, 9);
		x += 80;

		drawBox(contents, x, y, 159, 15, BLACK);
		drawText(contents, "Cuenta Bancaria", x + 5f, y + 4.5f, WHITE, HELVETICA, 9);
		x += 160;

		drawBox(contents, x, y, 69, 15, BLACK);
		drawTextRight(contents, new PDRectangle(x, y, 69, 15), "Importe", WHITE, HELVETICA, 9, 5, 4.5f);

		for (InvoiceFinance finance : invoice.getFinances())
		{
			x = 180;
			drawText(contents, formatDate(finance.getDueDate(), "dd/MM/yyyy").get(), x + 5f, y - 12, BLACK, HELVETICA, 7);
			x += 60;
			
			drawText(contents, finance.getPaymethod(), x + 5f, y - 12, BLACK, HELVETICA,
					7);
			x += 80;
			drawText(contents, finance.getIban(), x + 5f, y - 12, BLACK, HELVETICA, 7);
			x += 160;
			drawTextRight(contents, new PDRectangle(x, y, 69, 15), toLatinNumber(finance.getAmount()), BLACK, HELVETICA, 7, 5, -12);

			y -= 10;
		}

	}

}
