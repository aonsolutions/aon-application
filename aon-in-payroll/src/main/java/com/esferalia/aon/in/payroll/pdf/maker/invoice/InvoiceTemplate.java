package com.esferalia.aon.in.payroll.pdf.maker.invoice;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.LIGHT_GRAY;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.WHITE;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.formatDate;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.toLatinNumber;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.DataToolkit.safeString;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.createVerticalPage;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.croppedString;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawBox;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawImage;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawText;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawTextCenter;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawTextRight;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.getLines;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.ADDRESS;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.ADDRESS_LINE_TWO;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_DESCRIPTION;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_DISCOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_PRICE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_TOTAL;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_BANK_ACCOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_DATE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_PAY_METHOD;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.INVOICE_DATE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.INVOICE_TOTAL;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.NIF;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.REFERENCE_NUMBER;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.REGISTRY_NAME;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.TAX_BASE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.TAX_PERCENTAGE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.TAX_QUOTE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.TAX_TYPE;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;

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

	// THE PDF DOCUMENT
	public static void create(OutputStream os, Invoice invoice, PrintInvoiceConfiguration config, byte[] qrCode) throws IOException, CanNotCreatePdfException {
		try (PDDocument doc = new PDDocument())
		{
			
			InvoiceTemplate template = new InvoiceTemplate();

			if (os != null)
				template.filename = os;
			if (invoice == null)
				throw new CanNotCreatePdfException("No invoice found.");

			template.adapt	= config.getAdjustImage();
			template.top	= (float) config.getHeader();
			template.bottom	= (float) config.getFooter();

			if (config.getBackground() != null)
				template.background = config.getBackground().getData();
			template.qrCode = qrCode;

			template.contents = template.drawPage(doc, invoice, config);
			template.y		  = template.height - template.top - template.topInfoHeight - 5;

			if (config.isDetailed())
				template.drawDetailedEntries(doc, invoice, config);
			else
				template.draw_simplified_entries(doc, invoice, config);

			template.drawBottomInfo(doc, invoice);
			template.contents.close();
			doc.save(template.filename);
			new OutputStreamWriter(os,"ISO-8859-1");
		} catch (Exception e)
		{
			throw new CanNotCreatePdfException(e);
		}
	}

	// DRAW PAGE
	private PDPageContentStream drawPage(PDDocument doc, Invoice invoice, PrintInvoiceConfiguration config) throws IOException {
		PDPage page = createVerticalPage();
		doc.addPage(page);

		contents = new PDPageContentStream(doc, page);
		height = page.getMediaBox().getHeight();
		if (background != null)
			if (adapt)
				drawImage(doc, contents, background, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
			else {
				BufferedImage image = ImageIO.read(new ByteArrayInputStream(background));
			    int height = image.getHeight();
				drawImage(doc, contents, background, 0, this.height - height);
			}

		
		limit  = bottomInfoHeight + bottom;

		x = 50f;
		y = height - top - 20;

		drawTopInfo(invoice);

		if (config.isDetailed())
			drawDetailedHeader();
		else
			drawSimpleHeader();

		return contents;
	}

	// DRAW DETAILED ENTRIES
	public void drawDetailedEntries(PDDocument doc, Invoice invoice, PrintInvoiceConfiguration config) throws IOException {
		if (invoice.getDetails() != null){
			int i = 0;
			for (InvoiceDetail detail : invoice.getDetails()) {
				x = 50;
				if (y <= limit) {
					contents.close();
					contents = drawPage(doc, invoice, config);
					y		 = height - top - topInfoHeight - 5;
					x		 = 50;
				}
				String description = 
					new String(
						safeString(detail.getDescription())
							.replaceAll("\t", " ")
							.getBytes(Charset.forName("ASCII")),
						Charset.forName("UTF-8") 
					);
				
				ArrayList<String> divided = (ArrayList<String>) getLines(description, 240,PdfFonts.HELVETICA, 8);				
				float dy = y;

				for (String str : divided)
				{
					drawText(contents, str.trim(), x + 5, dy, PdfColors.BLACK, PdfFonts.HELVETICA, 8, i + DETAIL_DESCRIPTION);
					dy -= 10;
				}

				x += 250;

				drawTextRight(contents, new PDRectangle(x, y, 69, 15), toLatinNumber(detail.getQuantity()) + "", BLACK, HELVETICA, 8, 4.5f, 0, i + DETAIL_AMOUNT);
				x += 70;

				drawTextRight(contents, new PDRectangle(x, y, 69, 15),toLatinNumber(detail.getPrice()), BLACK, HELVETICA, 8, 4.5f, 0, i + DETAIL_PRICE);
				x += 70;

				drawTextRight(contents, new PDRectangle(x, y, 39, 15), safeString(detail.getDiscountExpression()), BLACK, HELVETICA, 8, 4.5f, 0, i + DETAIL_DISCOUNT);
				x += 40;

				drawTextRight(contents, new PDRectangle(x, y, 69, 15), toLatinNumber(detail.getTaxableBase()), BLACK, HELVETICA, 8, 4.5f, 0, i + DETAIL_TOTAL);
				y = dy - 3;
				
				i++;

			}
		}
	}

	// DRAW SIMPLIFIED ENTRIES
	public void draw_simplified_entries(PDDocument doc, Invoice invoice, PrintInvoiceConfiguration config) throws IOException {
		if (invoice.getDetails() != null)
		{
			for (InvoiceDetail detail : invoice.getDetails())
			{
				x = 50;
				if (y <= limit)
				{
					contents.close();
					contents = drawPage(doc, invoice, config);
					y		 = height - top - topInfoHeight - 5;
					x		 = 50;
				}
				String description = new String(detail.getDescription().replaceAll("\t", " ").getBytes(Charset.forName("ASCII")), Charset.forName("UTF-8") );
				drawText(
					contents,
					croppedString(description, 420, PdfFonts.HELVETICA, 8), x + 5, y,
					PdfColors.BLACK,
					PdfFonts.HELVETICA,
					8,
					DETAIL_DESCRIPTION
				);
				x += 430;
				PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 69, 15),
						PdfFormats.toLatinNumber(detail.getTaxableBase()), PdfColors.BLACK, PdfFonts.HELVETICA, 8, 4.5f, 0);
				y -= 10;
			}
		}
	}

	// DRAW UPPER INFO
	private void drawTopInfo(Invoice invoice) throws IOException {
		drawText(contents, "FACTURA", x, y, BLACK, HELVETICA_BOLD, 16);
		y -= 30;

		drawText(contents, "Numero: " + safeString(invoice.getReferenceCode()), x, y, BLACK, HELVETICA, 11,REFERENCE_NUMBER);
		y -= 4;

		drawBox(contents, x, y, 200, .5f, BLACK);
		y -= 16;

		drawText(contents, "Fecha: " + formatDate(invoice.getIssueDate(), "dd/MM/yyyy").orElse(""), x, y,BLACK, HELVETICA, 11 , INVOICE_DATE);

		y -= 4;

		drawBox(contents, x, y, 200, .5f, BLACK);
		y -= 16;

		drawText(contents, "N.I.F: " +  safeString(invoice.getRegistryDocument()), x, y, BLACK, HELVETICA, 11, NIF);
		y -= 4;

		drawBox(contents, x, y, 200, .5f, BLACK);
		y -= 6;
		x += 250;

		drawBox(contents, x, y, 250, 80, LIGHT_GRAY);
		x += 10;
		y  = height - top - 45;
		String str = new String(safeString(invoice.getRegistryName()).replaceAll("\t", " ").getBytes(Charset.forName("ASCII")), Charset.forName("UTF-8") );
		
		str = croppedString(str, 230, HELVETICA_BOLD, 12);
		drawText(contents, str, x, y, BLACK, HELVETICA_BOLD, 12, REGISTRY_NAME);
		y -= 15;
		if(invoice.getAddress() != null) {
			str = safeString(invoice.getAddress());
			str = croppedString(str, 230, HELVETICA, 9);
			drawText(contents,  str, x, y, BLACK, HELVETICA, 9, ADDRESS);
		}
		y -= 10;

		String zipCityProvince =  safeString(invoice.getAddressZIP()) + " "+  safeString(invoice.getAddressTown()) + " " +  safeString(invoice.getAddressProvince()); 
		drawText(contents, zipCityProvince.trim(), x, y, BLACK, HELVETICA, 9, ADDRESS_LINE_TWO);
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
		drawText(contents, "Descripci" + "\u00F3" + "n", x + 5f, y + 4.5f, WHITE, HELVETICA_BOLD, 9);
		x += 430;

		drawBox(contents, x, y, 69, 15, BLACK);
		drawText(contents, "Importe", x + 5f, y + 4.5f, WHITE, HELVETICA_BOLD, 9);
	}

	// DRAW BOTTOM INFO
	private void drawBottomInfo(PDDocument doc, Invoice invoice) throws IOException {
		x = 50;
		y = bottom + 10;
		if (qrCode != null)
			drawImage(doc, contents, qrCode, x, y, 120, 120);

		drawTaxes(invoice);
		drawFinances(invoice);
	}

	// DRAW TAXES
	private void drawTaxes(Invoice invoice) throws IOException {
		x = 180;
		y = bottom + 107;

		drawBox(contents, x, y, 79, 15, BLACK);
		drawTextRight(contents, new PDRectangle(x, y, 79, 15), "Base", WHITE, HELVETICA, 9, 5, 4.5f);
		x += 80;

		drawBox(contents, x, y, 79, 15, BLACK);
		drawTextRight(contents, new PDRectangle(x, y, 79, 15), "%", WHITE, HELVETICA, 9, 5, 4.5f);
		x += 80;

		drawBox(contents, x, y, 59, 15, BLACK);
		drawTextCenter(contents, new PDRectangle(x, y, 59, 15), "Tipo", WHITE, HELVETICA, 9, 4.5f);
		x += 60;
		
		drawBox(contents, x, y, 49, 15, BLACK);
		drawTextRight(contents, new PDRectangle(x, y, 49, 15), "Cuota", WHITE, HELVETICA, 9, 5, 4.5f);
		x += 50;

		drawBox(contents, x, y, 99, 15, BLACK);
		drawTextCenter(contents, new PDRectangle(x, y, 99, 15), "Total factura", WHITE, HELVETICA_BOLD, 9, 4.5f);

		if (invoice.getBreakdown() != null){
			double sum = 0;
			int i = 0;
			for (InvoiceBreakdown tax : invoice.getBreakdown()) {
				
				x = 180;
				drawTextRight(contents, new PDRectangle(x, y, 79, 15), toLatinNumber(tax.getBase()), BLACK, HELVETICA, 7, 5, -12, i + TAX_BASE);
				x += 80;
				
				String percent = "";
				if (tax.getPercentage() > 99)
					percent = "100%";
				else if (tax.getPercentage() != 0) {
					percent = toLatinNumber(tax.getPercentage()) + "%";
				
					if(tax.getSurcharge() != 0.00)
						percent += " + " +  toLatinNumber(tax.getSurcharge());
			
				}

				drawTextRight(contents, new PDRectangle(x, y, 79, 15), percent, BLACK, HELVETICA, 7, 5, -12, i + TAX_PERCENTAGE);
				x += 80;
				
				drawTextCenter(contents, new PDRectangle(x, y, 59, 15), tax.getTaxType().getName(), BLACK, HELVETICA, 7, -12, i + TAX_TYPE);
				x += 60;
				
				drawTextRight(contents, new PDRectangle(x, y, 49, 15), toLatinNumber(tax.getQuota() + tax.getSurchargeQuota()), BLACK, HELVETICA, 7, 5, -12, i + TAX_QUOTE);
				x += 50;
				
				sum	+= tax.getQuota() + tax.getSurchargeQuota() + tax.getBase();
				y	-= 10;
				
				i++;
			}	
			drawTextRight(contents, new PDRectangle(x, bottom + 107, 99, 15), toLatinNumber(invoice.getTotal()) + " \u20AC", BLACK, HELVETICA_BOLD, 8, 5, -14, INVOICE_TOTAL);
		}
	}

	// DRAW FINANCES
	private void drawFinances(Invoice invoice) throws IOException {

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


		if(invoice.getFinances() != null) {
			int i = 0;
			for (Finance finance : invoice.getFinances()) {
				x = 180;
				drawText(contents, formatDate(finance.getDueDate(), "dd/MM/yyyy").orElse(""), x + 5f, y - 12, BLACK, HELVETICA, 7, i + FINANCE_DATE);
				x += 60;
			
				drawText(contents, finance.getPayMethodType() == null ?  "" :  finance.getPayMethodType().getDescription(), x + 5f, y - 12, BLACK, HELVETICA,7, i + FINANCE_PAY_METHOD);
				x += 80;
			
				if(finance.getBankAccount() != null && finance.getBankAccount().getIban() != null)
					drawText(contents, finance.getBankAccount().getIban(), x + 5f, y - 12, BLACK, HELVETICA, 7, i + FINANCE_BANK_ACCOUNT);
				else
					drawText(contents, "", x + 5f, y - 12, BLACK, HELVETICA, 7, i + FINANCE_BANK_ACCOUNT);
			
				x += 160;
				drawTextRight(contents, new PDRectangle(x, y, 69, 15), toLatinNumber(finance.getAmount()), BLACK, HELVETICA, 7, 5, -12, i + FINANCE_AMOUNT);
				
				y -= 10;
				i++;
			}
		}
	}

}
