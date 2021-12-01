package com.esferalia.aon.in.payroll.pdf.maker.invoice;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceThemeConfiguration;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class InvoiceTemplate {
	
	public static final int MIN_HEADER_FOR_LOGO = 80;
	public static final int MAX_LOGO_HEIGHT = 55;
	public static final int MIN_FOOTER = 30;
	public static final float BOX_BORDER = .5f;
	public static final float BOTTOM_TOLERANCE = 0.3f;
	public static final float TITLE_BOX_SIZE = 17f;
	
	
	
	private static final String STANDARD_DATE_FORMAT = "dd/MM/yyyy";
	
	OutputStream filename;
	float height;
	float top;
	float bottom;
	float x;
	float y;
	float topInfoHeight	  = 140;
	float bottomInfoHeight = 140;
	float limit;
	float commentSize;
	float entriesStart;
	float bottomExtra;
	PDFont regularFont;
	PDFont boldFont;
	
	byte[] background;
	boolean adapt;
	PDPageContentStream	contents;
	int pageNumber;
	InvoiceTemplateMsg msg;
	PrintInvoiceConfiguration config;
	byte[] logo;
	
	// THE PDF DOCUMENT
	public static void create(OutputStream os, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config, String qrUrl, byte[] logo) throws IOException, CanNotCreatePdfException {
		try (PDDocument doc = new PDDocument())
		{
			

			InvoiceTemplate template = new InvoiceTemplate();
			
//			template.regularFont = PDType0Font.load(doc, InvoiceTemplate.class.getResourceAsStream("fonts/OpenSans-Regular.ttf"));
//			template.boldFont = PDType0Font.load(doc, InvoiceTemplate.class.getResourceAsStream("fonts/OpenSans-Bold.ttf"));
			template.regularFont = PdfFonts.HELVETICA;
			template.boldFont = PdfFonts.HELVETICA_BOLD;
					
			template.pageNumber = 0;
			template.msg = new InvoiceTemplateMsg(config.getLanguage());
			template.config = config;
			template.logo = logo;
			template.bottomExtra = 0;

			if (os != null)
				template.filename = os;
			if (invoice == null)
				throw new CanNotCreatePdfException("No invoice found.");

			template.adapt	= config.getAdjustImage();
			
			if ((template.logo != null || (company != null && config.isCompany())) && (config.getHeader() != null && config.getHeader() < MIN_HEADER_FOR_LOGO))
				template.top = MIN_HEADER_FOR_LOGO;
			else
				template.top	= config.getHeader();
			
			if (config.getFooter() == null || (config.getFooter() != null && config.getFooter() < MIN_FOOTER))
				template.bottom	= MIN_FOOTER;
			else
				template.bottom = config.getFooter();

			if (config.getBackground() != null)
				template.background = config.getBackground().getData();

			template.contents = template.drawFirstPage(doc, company, invoice, config);
			
			template.y -= 15;
			
			
			if (config.isDetailed())
				template.drawDetailedEntries(doc, company, invoice);
			else
				template.drawSimplifiedEntries(doc, company, invoice, config);

			template.drawBottomInfo(doc, invoice, qrUrl, config.getTheme());
			template.drawJail(template.limit);
			
			template.contents.close();
			template.drawFooter(doc, company, config.getTheme());				
			doc.save(template.filename);
			new OutputStreamWriter(os,StandardCharsets.ISO_8859_1);
		} catch (Exception e)
		{
			throw new CanNotCreatePdfException(e);
		}
	}
	
	
	private void drawJail(float end) throws IOException {
		if (config.isBoxBodyBorder()) {
			if (config.isDetailed()) {
//				drawBox(contents, 50f, entriesStart, 500f - BOX_BORDER, BOX_BORDER, config.getTheme().getBorderColor());
				drawBox(contents, 50f, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 300 - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 370 - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 440 - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 480 - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 550 - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 50f, end, 500f - BOX_BORDER, BOX_BORDER, config.getTheme().getBorderColor());
			} else {
//				drawBox(contents, 50f, entriesStart, 500f, BOX_BORDER, config.getTheme().getBorderColor());
				drawBox(contents, 50f, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 479f + BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 550f - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 50f, end, 500f, BOX_BORDER, config.getTheme().getBorderColor());
				
			}
		}
	}
	
	private void drawComment(PDDocument doc, CompanyFull company, Invoice invoice, String comment, PrintInvoiceThemeConfiguration theme) throws IOException {
		if (comment != null && !comment.isEmpty()) {
			float firstY = y;
			
			drawText(contents, "Notas:", 50f, y, theme.getTitleTextColor(), boldFont, 10);
			List<String> lines = PDFToolkit.getLinesRespectOriginal(comment, 575 - 100 - 50f, regularFont, 10);
			
			this.commentSize = lines.size() * 10f;
			
			for (String line : lines) {
				line = line != null ? line.trim() : line;
				drawText(contents, line, 100f, y, theme.getTextColor(), regularFont, 10);
				y-=10;
				if (y < bottom) {
					contents.close();
					contents = drawPage(doc, company, invoice, config, false);
					y = firstY;
				}
			}
			
			y -= 10;
		}
	}
	
	private void drawFooter(PDDocument doc, CompanyFull company, PrintInvoiceThemeConfiguration theme) throws IOException {
		if(company != null) {
			RecordData recordData = company.getRecordDatas() != null && !company.getRecordDatas().isEmpty() ? company.getRecordDatas().get(0) : null;
			String registration = "";
			String tomo = "";
			String folio = "";
			String hoja = "";
			String fechaRegistro = "";
			String registrationString = "";
			
			if (recordData != null && getConfig().isRecordData()) {
				registration = AonStringUtils.trimToEmpty(recordData.getRegistration());
				tomo = AonStringUtils.trimToEmpty(recordData.getVolume());
				folio = AonStringUtils.trimToEmpty(recordData.getPage());
				hoja = AonStringUtils.trimToEmpty(recordData.getSheet());
				Date registryDate = recordData.getRecordDate();
				fechaRegistro = registryDate != null ? new SimpleDateFormat(STANDARD_DATE_FORMAT).format(registryDate) : "";
			
				registrationString = 
					(!AonStringUtils.isEmpty(registration)	? registration						: "") +
					(!AonStringUtils.isEmpty(tomo)			? "  Tomo: "		+ tomo			: "") +
					(!AonStringUtils.isEmpty(folio)			? "  Folio: "		+ folio			: "") +
					(!AonStringUtils.isEmpty(hoja)			? "  Hoja: "		+ hoja			: "") +
					(!AonStringUtils.isEmpty(fechaRegistro)	? "  F.registro: "	+ fechaRegistro	: "");
			
			}
		
			String fullStr = "";
			String webStr = "";
			String phoneStr = "";
			String emailStr = "";
			String mediaStr = "";
			
			if (company.getMedias() != null && !company.getMedias().isEmpty() && getConfig().isContactData()) {
				LinkedList<RegistryMedia> medias = company.getMedias();
			
				List<RegistryMedia> webMedias = medias.stream().filter(m -> m.getMedia() != null && (m.getValue() != null && !m.getValue().isEmpty()) && m.getMedia().equals(MediaType.WEB)).collect(Collectors.toList());
				if (!webMedias.isEmpty()) {
					webStr = "Web: ";
					StringBuilder sb = new StringBuilder(webStr);
					for(RegistryMedia m : webMedias) {
						if (!sb.toString().equals("Web: "))
							sb.append(" | ");
						sb.append(m.getValue());
						if ((regularFont.getStringWidth(sb.toString()) / 1000.0f * 7) <= 185) {
							webStr = sb.toString();
						}
					}
					fullStr = sb.toString();
				}

				webMedias = medias.stream().filter(m -> m.getMedia() != null && (m.getValue() != null && !m.getValue().isEmpty()) && (m.getMedia().equals(MediaType.FIXED_PHONE) || m.getMedia().equals(MediaType.CELLULAR))).collect(Collectors.toList());
				if (!webMedias.isEmpty()) {
					phoneStr = "Teléfono/s: ";
					StringBuilder sb = new StringBuilder(phoneStr);
					for(RegistryMedia m : webMedias) {
						if (!sb.toString().equals("Teléfono/s: "))
							sb.append(" | ");
						sb.append(m.getValue());
						if ((regularFont.getStringWidth(sb.toString()) / 1000.0f * 7) <= 185) {
							phoneStr = sb.toString();
						}
					}
					fullStr += (!fullStr.isEmpty() ? "    " : "") + sb.toString();
				}	
			
				webMedias = medias.stream().filter(m -> m.getMedia() != null && (m.getValue() != null && !m.getValue().isEmpty()) && (m.getMedia().equals(MediaType.EMAIL) || m.getMedia().equals(MediaType.CELLULAR))).collect(Collectors.toList());
				if (!webMedias.isEmpty()) {
					emailStr = "Email: ";
					StringBuilder sb = new StringBuilder(emailStr);
					for(RegistryMedia m : webMedias) {
						if (!sb.toString().equals("Email: "))
							sb.append(" | ");
						sb.append(m.getValue());
						if ((regularFont.getStringWidth(sb.toString()) / 1000.0f * 7) <= 185) {
							emailStr = sb.toString();
						}
					}
					fullStr += (!fullStr.isEmpty() ? "    " : "") + sb.toString();
				}
				mediaStr = webStr + "    " + phoneStr + "    " + emailStr;
			}	
		
			for (int i=0; i<this.pageNumber; i++) {
				contents = new PDPageContentStream(doc, doc.getPage(i), PDPageContentStream.AppendMode.APPEND, true);
				
				if (company.getMedias() != null && !company.getMedias().isEmpty()) {
					if ((regularFont.getStringWidth(fullStr) / 1000.0f * 7) < 575) {
						mediaStr = fullStr;
					}
				
					drawText(contents
						, mediaStr
						, 10f
						, 20f
						, theme.getTitleTextColor()
						, regularFont
						, 7);
				
				}
			
				PDFToolkit.drawBox(contents, 10, 15, 575, 1, PdfColors.GRAY);
			
				String page = "Pag. " + (i+1) + " de " + pageNumber;
				
				drawTextRight(contents
					, new PDRectangle(570, 5, 15, 15)
					, page
					, theme.getTitleTextColor()
					, regularFont
					, 9
					, 0
					, 0);
			
				drawText(contents
					, registrationString
					, 10f
					, 5f
					, theme.getTitleTextColor()
					, regularFont
					, 7);
				contents.close();
			}
		}
	}

	// DRAW PAGE
	private PDPageContentStream drawPage(PDDocument doc, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config, boolean withHeader) throws IOException {
		PDPage page = createVerticalPage();
		
		doc.addPage(page);
		this.pageNumber++;
		contents = new PDPageContentStream(doc, page);
		height = page.getMediaBox().getHeight();
		if (background != null) {
			if (adapt) {
				drawImage(doc, contents, background, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
			} else {
				BufferedImage image = ImageIO.read(new ByteArrayInputStream(background));
			    int imgHeight = image.getHeight();
				drawImage(doc, contents, background, 0, this.height - imgHeight);
			}
		}
		
//		limit  = bottomInfoHeight + bottom;
		

		x = 50f;
		y = height - top - 20;

		drawTopInfo(doc, config, invoice, company);
		
		y -= 60;
		if (withHeader)
			if (config.isDetailed())
				drawDetailedHeader(config.getTheme());
			else
				drawSimpleHeader(config.getTheme());
		
		return contents;
	}
	
	// DRAW FIRST PAGE
	private PDPageContentStream drawFirstPage(PDDocument doc, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config) throws IOException {
		PDPage page = createVerticalPage();
		
		doc.addPage(page);
		this.pageNumber++;
		contents = new PDPageContentStream(doc, page);
		height = page.getMediaBox().getHeight();
		if (background != null) {
			if (adapt) {
				drawImage(doc, contents, background, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
			} else {
				BufferedImage image = ImageIO.read(new ByteArrayInputStream(background));
				int imgHeight = image.getHeight();
				drawImage(doc, contents, background, 0, this.height - imgHeight);
			}
		}
		
		limit  = bottomInfoHeight + bottom;
		
		int bottomStuff = invoice.getBreakdown() != null ? invoice.getBreakdown().size() : 0;
		bottomStuff += invoice.getFinances() != null ? invoice.getFinances().size() : 0;
		
		if (bottomStuff > 8) {
			bottomExtra = (bottomStuff - 7) * 10;
			limit += bottomExtra;
		}
		
		
		x = 50f;
		y = height - top - 20;
		
		drawTopInfo(doc, config, invoice, company);
		
		y -= 60;
		
		drawComment(doc, company, invoice, invoice.getComments(), config.getTheme());
		
		if (config.isDetailed())
			drawDetailedHeader(config.getTheme());
		else
			drawSimpleHeader(config.getTheme());
		
		return contents;
	}

	// DRAW DETAILED ENTRIES
	public void drawDetailedEntries(PDDocument doc, CompanyFull company, Invoice invoice) throws IOException {
		if (invoice.getDetails() != null){
			int i = 0;
			for (InvoiceDetail detail : invoice.getDetails()) {
				
				x = 50;
				String description = 
						safeString(detail.getDescription())
							.replace("\t", " ");
	
				ArrayList<String> divided = (ArrayList<String>) PDFToolkit.getLinesRespectOriginal(description, 240,regularFont, 8);				
				float lineDiff = 10;
				
				drawDetail(i, detail, divided, lineDiff, doc, invoice, company);
					
				i++;
	
			}
		}
	}


	private void drawExtraBoxBackground(PrintInvoiceConfiguration config) throws IOException {
		if (config.isDetailed()) {			
			drawBox(contents, 50, bottom, 250 - BOX_BORDER, limit - bottom, config.getTheme().getBoxBodyBackgroundColor());
			
			drawBox(contents, 300, bottom, 70 - BOX_BORDER, limit - bottom, config.getTheme().getBoxBodyBackgroundColor());
			drawBox(contents, 370, bottom, 70 - BOX_BORDER, limit - bottom, config.getTheme().getBoxBodyBackgroundColor());
			drawBox(contents, 440, bottom, 40 - BOX_BORDER, limit - bottom, config.getTheme().getBoxBodyBackgroundColor());
			drawBox(contents, 480, bottom, 70, limit - bottom, config.getTheme().getBoxBodyBackgroundColor());
			
		} else {
			drawBox(contents, 50, bottom, 429, limit - bottom, config.getTheme().getBoxBodyBackgroundColor());
			drawBox(contents, 480, bottom, 70, limit - bottom, config.getTheme().getBoxBodyBackgroundColor());
		}
	}

	private void jumpToNewPage(PDDocument doc, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config,
			byte[] logo) throws IOException {
		drawJail(bottom);
		contents.close();
		contents = drawPage(doc, company, invoice, config, true);
		y		 = height - top - topInfoHeight - 5;
		x		 = 50;
	}
	
	private void drawDetail(int i, InvoiceDetail detail, ArrayList<String> divided, float lineDiff,
			PDDocument doc, Invoice invoice, CompanyFull company) throws IOException {
		PrintInvoiceThemeConfiguration theme = config.getTheme();
		float dy = y;
		int line = 0;
		float originX = x;
		boolean extraBackground = dy < limit + 5;
		for (String str : divided)
		{
			drawText(contents, str.trim(), x + 5, dy, theme.getTextColor(), regularFont, 8, i + DETAIL_DESCRIPTION);
			
			if (line++ == 0) {
				x += 250;
				
				drawTextRight(contents, new PDRectangle(x, y, 69, 15), toLatinNumber(detail.getQuantity()) + "", theme.getTextColor(), regularFont, 8, 4.5f, 0, i + DETAIL_AMOUNT);
				x += 70;
				
				drawTextRight(contents, new PDRectangle(x, y, 69, 15),toLatinNumber(detail.getPrice()), theme.getTextColor(), regularFont, 8, 4.5f, 0, i + DETAIL_PRICE);
				x += 70;
				
				drawTextRight(contents, new PDRectangle(x, y, 39, 15), safeString(detail.getDiscountExpression()), theme.getTextColor(), regularFont, 8, 4.5f, 0, i + DETAIL_DISCOUNT);
				x += 40;
				
				drawTextRight(contents, new PDRectangle(x, y, 69, 15), toLatinNumber(detail.getTaxableBase()), theme.getTextColor(), regularFont, 8, 4.5f, 0, i + DETAIL_TOTAL);
				x = originX;
			}
			
			dy -= lineDiff;
			if (dy < bottom + 5) {
				jumpToNewPage(doc, company, invoice, config, logo);
				extraBackground = false;
				dy = y;
			} else if (dy < limit + 5 && !extraBackground) {
				if (config.getTheme().getBoxBodyBackgroundColor() != null)
					drawExtraBoxBackground(config);
				extraBackground = true;
			}
			
		}
		
		if (dy < limit + 5 && i == invoice.getDetails().size() - 1) {
			jumpToNewPage(doc, company, invoice, config, logo);
			dy = y;
		}
		
		y = dy - 3;
		
	}

	// DRAW SIMPLIFIED ENTRIES
	public void drawSimplifiedEntries(PDDocument doc, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config) throws IOException {
		if (invoice.getDetails() != null) {
			boolean xtraBack = false;
			for (InvoiceDetail detail : invoice.getDetails()) {
				x = 50;
				
				if (y < bottom + 5) {
					jumpToNewPage(doc, company, invoice, config, logo);
					xtraBack = false;
				}
				String description = detail.getDescription().replace("\t", " ");
//						.getBytes(Charset.forName("ASCII")), Charset.forName("UTF-8");
//				croppedString(description, 420, REGULAR_FONT, 8), x + 5, y,
				
				if (y < limit && !xtraBack) {
					if (config.getTheme().getBoxBodyBackgroundColor() != null)
						drawExtraBoxBackground(config);
					xtraBack = true;
				}
					
				drawText(
					contents,
					PDFToolkit.croppedStringWholeWord(description, 420, regularFont, 8), x + 5, y,
					config.getTheme().getTextColor(),
					regularFont,
					8,
					DETAIL_DESCRIPTION
				);
				x += 430;
				PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 69, 15),
						PdfFormats.toLatinNumber(detail.getTaxableBase()), config.getTheme().getTextColor(), regularFont, 8, 4.5f, 0);
				y -= 10;
			}
			
			if (y < limit + 5) {
				jumpToNewPage(doc, company, invoice, config, logo);
			}
			
		}
	}

	// DRAW UPPER INFO
	private void drawTopInfo(PDDocument doc, PrintInvoiceConfiguration config, Invoice invoice, CompanyFull company) throws IOException {
		
		float maxHeight = MAX_LOGO_HEIGHT;
		float maxWidth = 297 - x - 20;
		
		
		float tempY = height - MIN_HEADER_FOR_LOGO + 20;
		float logoX = x;
		float logoY = tempY - 10;
		String web = null;
		
		if (company != null && config.isCompany()) {
			Company registry = company.getRegistry();
			String companyName = registry != null ? AonStringUtils.trimToEmpty(registry.getName()) : "";
			companyName = croppedString(companyName, 240, regularFont, 9);
			String nif = registry != null ? AonStringUtils.trimToEmpty(registry.getDocument()) : "";
			String address;
			String zip = "";
			
			if (company.getAddresses() != null && !company.getAddresses().isEmpty()) {
				RegistryAddress addr = company.getAddresses().stream().filter(RegistryAddress::isMain).findFirst().orElse(company.getAddresses().getFirst());
				address = addr.getFullAddress();
				String cp = AonStringUtils.trimToEmpty(addr.getZip());
				String city = AonStringUtils.trimToEmpty(addr.getCity());
				String province = AonStringUtils.trimToEmpty(addr.getProvince());
				String country = AonStringUtils.trimToEmpty(addr.getCountry() != null ? addr.getCountry().getName() : "");
				
				String location = "";
				
				if (!city.isEmpty()) {
					location += city;
				}
				if (!province.isEmpty()) {
					location += ", " + province;
				}
				if (!country.isEmpty()) {
					location += ", " + country;
				}
				
				if (cp.isEmpty())
					zip = location;
				else {
					zip = cp + ", " + location;
				}
			} else {
				address = "";
				zip = "";
			}
			
			web = "";
			if (company.getMedias() != null && !company.getMedias().isEmpty()) {
				web = company.getMedias().stream().filter(m -> m.getValue() != null && !m.getValue().isEmpty() && m.getMedia() == MediaType.WEB).map(RegistryMedia::getValue).findFirst().orElse("");			
			}
			
			tempY -= 10;
			
			logoX = x;
			logoY = tempY;

			drawText(contents, companyName, x + 260, tempY + 35, config.getTheme().getTitleTextColor(), boldFont, 10);		
			
			drawText(contents, "NIF:", x + 260, tempY + 22, config.getTheme().getTitleTextColor(), regularFont, 8);		
			drawText(contents, nif, x + 280, tempY + 22, config.getTheme().getTitleTextColor(), regularFont, 8);

			List<String> addressLines = getLines(address, 235, regularFont, 8);
			float addrPlus = 11;
			
			if (!addressLines.isEmpty()) {
				drawText(contents, AonStringUtils.trimToEmpty(addressLines.get(0)), x + 260, tempY + addrPlus, config.getTheme().getTitleTextColor(), regularFont, 8);
				addrPlus -= 9;
				if (addressLines.size() > 1) {
					StringBuilder addressBuilder = new StringBuilder("");
					for(int i=1; i<addressLines.size(); i++) {
						addressBuilder.append(addressLines.get(i));
					}
					drawText(contents, croppedString(AonStringUtils.trimToEmpty(addressBuilder.toString()), 235, regularFont, 8), x + 260, tempY + addrPlus, config.getTheme().getTitleTextColor(), regularFont, 8);
					addrPlus -= 9;
				}
			}
			addrPlus -= 1;
			
				List<String> zipLines = getLines(zip, 235, regularFont, 8);
				if (!zipLines.isEmpty()) {
					drawText(contents, AonStringUtils.trimToEmpty(zipLines.get(0)), x + 260, tempY + addrPlus, config.getTheme().getTitleTextColor(), regularFont, 8);
					addrPlus -= 9;
					if (zipLines.size() > 1) {
						StringBuilder zipBuilder = new StringBuilder("");
						for(int i=1; i<zipLines.size(); i++) {
							zipBuilder.append(zipLines.get(i));
						}
						drawText(contents, croppedString(AonStringUtils.trimToEmpty(zipBuilder.toString()), 235, regularFont, 8), x + 260, tempY + addrPlus, config.getTheme().getTitleTextColor(), regularFont, 8);
					}
				}
		}
				
		if (logo != null) {				
			PDFToolkit.drawResizedLogo(doc, doc.getPage(pageNumber - 1), contents, logo, logoX, logoY, maxHeight, maxWidth, web);
		}
		
		drawText(contents, getMsg().invoice().toUpperCase(), x, y, config.getTheme().getTitleTextColor(), boldFont, 16);
		y -= 30;

		drawText(contents, getMsg().number() + ":", x, y, config.getTheme().getTitleTextColor(), boldFont, 11,REFERENCE_NUMBER);
		drawText(contents, safeString(invoice.getReferenceCode()), x + 50, y, config.getTheme().getTextColor(), regularFont, 11,REFERENCE_NUMBER);
		y -= 4;

		y -= 16;

		drawText(contents, getMsg().date() + ":", x, y, config.getTheme().getTitleTextColor(), boldFont, 11 , INVOICE_DATE);
		drawText(contents, formatDate(invoice.getIssueDate(), STANDARD_DATE_FORMAT).orElse(""), x + 50, y, config.getTheme().getTextColor(), regularFont, 11 , INVOICE_DATE);
		
		y -= 20;

		drawText(contents, "N.I.F.:", x, y, config.getTheme().getTitleTextColor(), boldFont, 11, NIF);
		drawText(contents, safeString(invoice.getRegistryDocument()), x + 50, y, config.getTheme().getTextColor(), regularFont, 11, NIF);
		
		y -= 10;
		x += 250;

		drawBox(contents, x, y, 250, 80, config.getTheme().getCustomerBackgroundColor());
		x += 10;
		y  = height - top - 35;
		String str = safeString(invoice.getRegistryName())
				.replace("\t", " ")
				/*.getBytes(Charset.forName("ASCII")), Charset.forName("UTF-8")*/;
		
		List<String> nameLines = PDFToolkit.getLines(str, 230, boldFont, 10);
		
		if (nameLines != null) {
			String line1 = nameLines.get(0).trim();
			String line2 = null;
			if (nameLines.size() > 1) {
				line2 = nameLines.get(1).trim();
			}
			
			if (line1 != null) {
				drawText(contents, line1, x, y, config.getTheme().getTextColor(), boldFont, 10, REGISTRY_NAME);
				y -= 10;
			}
			if (line2 != null) {
				drawText(contents, line2, x, y, config.getTheme().getTextColor(), boldFont, 10, REGISTRY_NAME);				
			}
		}
		y -= 15;
		
		if (invoice.getRegistryAddressData() != null && !invoice.getRegistryAddressData().isEmpty()) {
			RegistryAddress address = invoice.getRegistryAddressData();
			str = safeString(address.getFullAddress());
			str = croppedString(str, 230, regularFont, 9);
			drawText(contents,  str, x, y, config.getTheme().getTextColor(), regularFont, 9, ADDRESS);

			y -= 10;

			String zipCityProvince =  safeString(address.getZip()) + " "+  safeString(address.getCity()) + " " +  safeString(address.getProvince()); 
			drawText(contents, zipCityProvince.trim(), x, y, config.getTheme().getTextColor(), regularFont, 9, ADDRESS_LINE_TWO);
		} else {
			str = safeString(invoice.getAddress());
			str = croppedString(str, 230, regularFont, 9);
			drawText(contents,  str, x, y, config.getTheme().getTextColor(), regularFont, 9, ADDRESS);

			y -= 10;

			String zipCityProvince =  safeString(invoice.getAddressZIP()) + " "+  safeString(invoice.getAddressTown()) + " " +  safeString(invoice.getAddressProvince()); 
			drawText(contents, zipCityProvince.trim(), x, y, config.getTheme().getTextColor(), regularFont, 9, ADDRESS_LINE_TWO);
		}

	}

	// DRAW DETAILED HEADER
	private void drawDetailedHeader(PrintInvoiceThemeConfiguration theme) throws IOException {
		x  = 50;
		
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 250 - BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextCenter(contents, new PDRectangle(x, y, 249, TITLE_BOX_SIZE), getMsg().description(), theme.getBoxTitleTextColor(), boldFont, 9, 5.5f);
		x += 250;

		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 70 - BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextCenter(contents, new PDRectangle(x, y, 69, TITLE_BOX_SIZE), getMsg().quantity(), theme.getBoxTitleTextColor(), boldFont, 9, 5.5f);
		x += 70;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 70 - BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextCenter(contents, new PDRectangle(x, y, 69, TITLE_BOX_SIZE), getMsg().price(), theme.getBoxTitleTextColor(), boldFont, 9, 5.5f);
		x += 70;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 40 - BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextCenter(contents, new PDRectangle(x, y, 39, TITLE_BOX_SIZE), "%Dto.", theme.getBoxTitleTextColor(), boldFont, 9, 5.5f);
		x += 40;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 70, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextRight(contents, new PDRectangle(x, y, 69, TITLE_BOX_SIZE), getMsg().amount(), theme.getBoxTitleTextColor(), boldFont, 9, 5, 5.5f);
		
		if (config.isBoxTitleBorder()) {
			drawBox(contents, 50, y + TITLE_BOX_SIZE, 500, BOX_BORDER, theme.getBorderColor());
//			drawBox(contents, 50, y, BOX_BORDER, TITLE_BOX_SIZE, theme.getBorderColor());
//			drawBox(contents, 300 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE, theme.getBorderColor());
//			drawBox(contents, 370 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE, theme.getBorderColor());
//			drawBox(contents, 440 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE, theme.getBorderColor());
//			drawBox(contents, 480 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE, theme.getBorderColor());
//			drawBox(contents, 550 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE, theme.getBorderColor());
			drawBox(contents, 50, y, 500, BOX_BORDER, theme.getBorderColor());
		}
		
		if (theme.getBoxBodyBackgroundColor() != null) {			
			drawBox(contents, 50, limit - BOTTOM_TOLERANCE, 250 - BOX_BORDER, y - limit + BOTTOM_TOLERANCE, theme.getBoxBodyBackgroundColor());
			drawBox(contents, 300, limit - BOTTOM_TOLERANCE, 70 - BOX_BORDER, y - limit + BOTTOM_TOLERANCE, theme.getBoxBodyBackgroundColor());
			drawBox(contents, 370, limit - BOTTOM_TOLERANCE, 70 - BOX_BORDER, y - limit + BOTTOM_TOLERANCE, theme.getBoxBodyBackgroundColor());
			drawBox(contents, 440, limit - BOTTOM_TOLERANCE, 40 - BOX_BORDER, y - limit + BOTTOM_TOLERANCE, theme.getBoxBodyBackgroundColor());
			drawBox(contents, 480, limit - BOTTOM_TOLERANCE, 70, y - limit + BOTTOM_TOLERANCE, theme.getBoxBodyBackgroundColor());
		}
		
		entriesStart = y;
		
		
		
		
	}

	// DRAW SIMPLE HEADER
	private void drawSimpleHeader(PrintInvoiceThemeConfiguration theme) throws IOException {
		x  = 50;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 429 + BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawText(contents, getMsg().description(), x + 5f, y + 5.5f, theme.getBoxTitleTextColor(), boldFont, 9);
		x += 430;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 70, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawText(contents, getMsg().amount(), x + 5f, y + 5.5f, theme.getBoxTitleTextColor(), boldFont, 9);

		if (config.isBoxTitleBorder()) {
			drawBox(contents, 50, y + TITLE_BOX_SIZE, 500, BOX_BORDER, theme.getBorderColor());
//			drawBox(contents, 50, y, BOX_BORDER, TITLE_BOX_SIZE, theme.getBorderColor());
//			drawBox(contents, 480 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE, theme.getBorderColor());
//			drawBox(contents, 550 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE, theme.getBorderColor());
			drawBox(contents, 50, y, 500 - BOX_BORDER, BOX_BORDER, theme.getBorderColor());
		}
		
		if (theme.getBoxBodyBackgroundColor() != null) {			
			drawBox(contents, 50, limit - BOTTOM_TOLERANCE, 429 + BOX_BORDER, y - limit + BOTTOM_TOLERANCE, theme.getBoxBodyBackgroundColor());
			drawBox(contents, 480, limit - BOTTOM_TOLERANCE, 70, y - limit + BOTTOM_TOLERANCE, theme.getBoxBodyBackgroundColor());
		}
		
		
		entriesStart = y;
	}

	// DRAW BOTTOM INFO
	private void drawBottomInfo(PDDocument doc, Invoice invoice, String qrUrl, PrintInvoiceThemeConfiguration theme) throws IOException, WriterException {
		x = 50;
		y = bottom + 10 + bottomExtra;
		if(qrUrl != null) {
			byte[] qrCode = createQR(qrUrl, 300, 300);
			drawImage(doc, contents, qrCode, x, y, 120, 120);
		}
		drawTaxes(invoice, theme);
		drawFinances(invoice, theme);
	}

	// DRAW TAXES
	private void drawTaxes(Invoice invoice, PrintInvoiceThemeConfiguration theme) throws IOException {
		x = 180;
		y = bottom + 107 + bottomExtra;
		
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 80 - BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextRight(contents, new PDRectangle(x, y, 79, TITLE_BOX_SIZE), getMsg().base(), theme.getBoxTitleTextColor(), regularFont, 9, 5, 5.5f);
		x += 80;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 80 - BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextRight(contents, new PDRectangle(x, y, 79, TITLE_BOX_SIZE), "%", theme.getBoxTitleTextColor(), regularFont, 9, 5, 5.5f);
		x += 80;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 60 - BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextCenter(contents, new PDRectangle(x, y, 59, TITLE_BOX_SIZE), getMsg().type(), theme.getBoxTitleTextColor(), regularFont, 9, 5.5f);
		x += 60;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 50 - BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextRight(contents, new PDRectangle(x, y, 49, TITLE_BOX_SIZE), getMsg().quota(), theme.getBoxTitleTextColor(), regularFont, 9, 5, 5.5f);
		x += 50;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 100, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextCenter(contents, new PDRectangle(x, y, 99, TITLE_BOX_SIZE), getMsg().totalInvoice(), theme.getBoxTitleTextColor(), boldFont, 9, 5.5f);
		

		if (config.isBoxTitleBorder()) {
			drawBox(contents, 180, y + TITLE_BOX_SIZE, 370, BOX_BORDER, theme.getBorderColor());
//			drawBox(contents, 180, y, BOX_BORDER, TITLE_BOX_SIZE + BOX_BORDER, theme.getBorderColor());
//			drawBox(contents, 260 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE + BOX_BORDER, theme.getBorderColor());
//			drawBox(contents, 340 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE + BOX_BORDER, theme.getBorderColor());
//			drawBox(contents, 400 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE + BOX_BORDER, theme.getBorderColor());
//			drawBox(contents, 450 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE + BOX_BORDER, theme.getBorderColor());
//			drawBox(contents, 550 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE + BOX_BORDER, theme.getBorderColor());
			drawBox(contents, 180, y, 370, BOX_BORDER, theme.getBorderColor());
		}
		
		if (invoice.getBreakdown() != null){
			int i = 0;
			float initY = y;
			
			float bdSize = invoice.getBreakdown().size() * 10 + 10f;
			
			if (theme.getBoxBodyBackgroundColor() != null) {				
				drawBox(contents, 180, y, 80 - BOX_BORDER, -bdSize, theme.getBoxBodyBackgroundColor());
				drawBox(contents, 260, y, 80 - BOX_BORDER, -bdSize, theme.getBoxBodyBackgroundColor());
				drawBox(contents, 340, y, 60 - BOX_BORDER, -bdSize, theme.getBoxBodyBackgroundColor());
				drawBox(contents, 400, y, 50 - BOX_BORDER, -bdSize, theme.getBoxBodyBackgroundColor());
				drawBox(contents, 450, y, 100f, -bdSize, theme.getBoxBodyBackgroundColor());
			}
			
			
			for (InvoiceBreakdown tax : invoice.getBreakdown()) {
				
				x = 180;
				drawTextRight(contents, new PDRectangle(x, y, 79, 15), toLatinNumber(tax.getBase()), theme.getTextColor(), regularFont, 7, 5, -12, i + TAX_BASE);
				x += 80;
				
				String percent = "";
				if (tax.getPercentage() > 99)
					percent = "100%";
				else if (tax.getPercentage() != 0) {
					percent = toLatinNumber(tax.getPercentage()) + "%";
				
					if(tax.getSurcharge() != 0.00)
						percent += " + " +  toLatinNumber(tax.getSurcharge());
			
				}

				drawTextRight(contents, new PDRectangle(x, y, 79, 15), percent, theme.getTextColor(), regularFont, 7, 5, -12, i + TAX_PERCENTAGE);
				x += 80;
				
				drawTextCenter(contents, new PDRectangle(x, y, 59, 15), tax.getTaxType().getName(), theme.getTextColor(), regularFont, 7, -12, i + TAX_TYPE);
				x += 60;
				
				drawTextRight(contents, new PDRectangle(x, y, 49, 15), toLatinNumber(tax.getQuota() + tax.getSurchargeQuota()), theme.getTextColor(), regularFont, 7, 5, -12, i + TAX_QUOTE);
				x += 50;
				
				y	-= 10;
				
				i++;
			}
			drawTextRight(contents, new PDRectangle(x, bottom + 107 + bottomExtra, 99, 15), toLatinNumber(invoice.getTotal()) + " \u20AC", theme.getTextColor(), boldFont, 8, 5, -14, INVOICE_TOTAL);
			
			float finalY = y -10;
			float backHeight = initY - finalY;
			
			if (config.isBoxBodyBorder()) {
//				drawBox(contents, 180, finalY + backHeight, 370, BOX_BORDER, theme.getBorderColor());
				drawBox(contents, 180, finalY, BOX_BORDER, backHeight + + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 260 - BOX_BORDER, finalY, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 340 - BOX_BORDER, finalY, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 400 - BOX_BORDER, finalY, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 450 - BOX_BORDER, finalY, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 550 - BOX_BORDER, finalY, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 180, finalY, 370, BOX_BORDER, theme.getBorderColor());
			}
		}
	}

	// DRAW FINANCES
	private void drawFinances(Invoice invoice, PrintInvoiceThemeConfiguration theme) throws IOException {

		x = 180;
		y-= 28;
		
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 60 - BOX_BORDER, TITLE_BOX_SIZE,  theme.getBoxTitleBackgroundColor());
		drawText(contents, getMsg().date(), x + 5f, y + 5.5f, theme.getBoxTitleTextColor(), regularFont, 9);
		x += 60;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 80 - BOX_BORDER, TITLE_BOX_SIZE,  theme.getBoxTitleBackgroundColor());
		drawText(contents, getMsg().payMethod(), x + 5f, y + 5.5f, theme.getBoxTitleTextColor(), regularFont, 9);
		x += 80;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 160 - BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawText(contents, getMsg().bankAccount(), x + 5f, y + 5.5f, theme.getBoxTitleTextColor(), regularFont, 9);
		x += 160;

		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 70, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextRight(contents, new PDRectangle(x, y, 69, TITLE_BOX_SIZE), getMsg().amount(), theme.getBoxTitleTextColor(), regularFont, 9, 5, 5.5f);
		
		if (config.isBoxTitleBorder()) {
			drawBox(contents, 180, y + TITLE_BOX_SIZE, 370, BOX_BORDER, theme.getBorderColor());
//			drawBox(contents, 180, y, BOX_BORDER, TITLE_BOX_SIZE, theme.getBorderColor());
//			drawBox(contents, 240 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE, theme.getBorderColor());
//			drawBox(contents, 320 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE, theme.getBorderColor());
//			drawBox(contents, 480 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE, theme.getBorderColor());
//			drawBox(contents, 550 - BOX_BORDER, y, BOX_BORDER, TITLE_BOX_SIZE, theme.getBorderColor());
			drawBox(contents, 180, y, 370, BOX_BORDER, theme.getBorderColor());
		}

		if(invoice.getFinances() != null) {
			int i = 0;
			float initY = y;
			
			float fSize = invoice.getFinances().size() * 10 + 10f;
			
			if (theme.getBoxBodyBackgroundColor() != null) {
				drawBox(contents, 180, y, 60 - BOX_BORDER, -fSize, theme.getBoxBodyBackgroundColor());
				drawBox(contents, 240, y, 80 - BOX_BORDER, -fSize, theme.getBoxBodyBackgroundColor());
				drawBox(contents, 320, y, 160 - BOX_BORDER, -fSize, theme.getBoxBodyBackgroundColor());
				drawBox(contents, 480, y, 70f, -fSize, theme.getBoxBodyBackgroundColor());				
			}
			
			
			for (Finance finance : invoice.getFinances()) {
				x = 180;
				drawText(contents, formatDate(finance.getDueDate(), STANDARD_DATE_FORMAT).orElse(""), x + 5f, y - 12, theme.getTextColor(), regularFont, 7, i + FINANCE_DATE);
				x += 60;
				
				String altMethodName = finance.getPayMethodType() != null ? finance.getPayMethodType().getDescription() : "";
				String paymethod = finance.getPayMethodName() != null ? finance.getPayMethodName() : altMethodName;
				drawText(contents, paymethod, x + 5f, y - 12, theme.getTextColor(), regularFont,7, i + FINANCE_PAY_METHOD);
				x += 80;
			
				if(finance.getBankAccount() != null && finance.getBankAccount().getIban() != null)
					drawText(contents, finance.getBankAccount().getIban(), x + 5f, y - 12, theme.getTextColor(), regularFont, 7, i + FINANCE_BANK_ACCOUNT);
				else
					drawText(contents, "", x + 5f, y - 12, theme.getTextColor(), regularFont, 7, i + FINANCE_BANK_ACCOUNT);
			
				x += 160;
				drawTextRight(contents, new PDRectangle(x, y, 69, 15), toLatinNumber(finance.getAmount()), theme.getTextColor(), regularFont, 7, 5, -12, i + FINANCE_AMOUNT);
				
				y -= 10;
				i++;
			}
			
			if (config.isBoxBodyBorder()) {
				y -= 10;
				float backHeight  = initY - y;
//				drawBox(contents, 180, initY, 370 - BOX_BORDER, BOX_BORDER, theme.getBorderColor());
				drawBox(contents, 180, y, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 240 - BOX_BORDER, y, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 320 - BOX_BORDER, y, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 480 - BOX_BORDER, y, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 550 - BOX_BORDER, y, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 180, y, 370, BOX_BORDER, theme.getBorderColor());
			}
			
		}
	}
	
	public static byte[] createQR(String datos, int ancho, int altura) throws WriterException, IOException {
	    BitMatrix matrix;
	    Writer escritor = new QRCodeWriter();
	    matrix = escritor.encode(datos, BarcodeFormat.QR_CODE, ancho, altura);
	         
	    BufferedImage imagen = new BufferedImage(ancho, altura, BufferedImage.TYPE_INT_RGB);
	    
	    for(int y = 0; y < altura; y++) {
	        for(int x = 0; x < ancho; x++) {
	            int grayValue = (matrix.get(x, y) ? 0 : 1) & 0xff;
	            imagen.setRGB(x, y, (grayValue == 0 ? 0 : 0xFFFFFF));
	        }
	    }
	         
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(imagen, "PNG", baos);
	    return baos.toByteArray();        
	}
	
	private InvoiceTemplateMsg getMsg() {	
		return this.msg;
	}

	private PrintInvoiceConfiguration getConfig() {
		return config;
	}
}
