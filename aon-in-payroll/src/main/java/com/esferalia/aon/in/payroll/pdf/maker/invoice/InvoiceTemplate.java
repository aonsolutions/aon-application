package com.esferalia.aon.in.payroll.pdf.maker.invoice;

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

import java.awt.Color;
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
	float boxBorder = .5f;
	float bottomExtra = 0;
	
	byte[] background;
	boolean adapt;
	PDPageContentStream	contents;
	int pageNumber;
	InvoiceTemplateMsg msg;
	PrintInvoiceConfiguration config;
	
	// THE PDF DOCUMENT
	public static void create(OutputStream os, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config, String qrUrl, byte[] logo) throws IOException, CanNotCreatePdfException {
		try (PDDocument doc = new PDDocument())
		{
			InvoiceTemplate template = new InvoiceTemplate();
			template.pageNumber = 0;
			template.msg = new InvoiceTemplateMsg(config.getLanguage());
			template.config = config;

			if (os != null)
				template.filename = os;
			if (invoice == null)
				throw new CanNotCreatePdfException("No invoice found.");

			template.adapt	= config.getAdjustImage();
			
			if ((logo != null || (company != null && config.isCompany())) && (config.getHeader() != null && config.getHeader() < MIN_HEADER_FOR_LOGO))
				template.top = MIN_HEADER_FOR_LOGO;
			else
				template.top	= config.getHeader();
			
			if (config.getFooter() == null || (config.getFooter() != null && config.getFooter() < MIN_FOOTER))
				template.bottom	= MIN_FOOTER;
			else
				template.bottom = config.getFooter();

			if (config.getBackground() != null)
				template.background = config.getBackground().getData();

			template.contents = template.drawFirstPage(doc, company, invoice, config, logo);
			
			template.y -= 15;
			
			
			if (config.isDetailed())
				template.drawDetailedEntries(doc, company, invoice, config, logo);
			else
				template.drawSimplifiedEntries(doc, company, invoice, config, logo);

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
		if (config.getTheme().isBoxBodyBorder()) {			
			if (config.isDetailed()) {
//				drawBox(contents, 50f, entriesStart, 499f - boxBorder, boxBorder, config.getTheme().getBoxTitleBackgroundColor());
				drawBox(contents, 50f, end, boxBorder, entriesStart - end, config.getTheme().getBoxTitleBackgroundColor());
				drawBox(contents, 300 - boxBorder, end, boxBorder, entriesStart - end, config.getTheme().getBoxTitleBackgroundColor());
				drawBox(contents, 370 - boxBorder, end, boxBorder, entriesStart - end, config.getTheme().getBoxTitleBackgroundColor());
				drawBox(contents, 440 - boxBorder, end, boxBorder, entriesStart - end, config.getTheme().getBoxTitleBackgroundColor());
				drawBox(contents, 480 - boxBorder, end, boxBorder, entriesStart - end, config.getTheme().getBoxTitleBackgroundColor());
				drawBox(contents, 550 - boxBorder, end, boxBorder, entriesStart - end, config.getTheme().getBoxTitleBackgroundColor());
				drawBox(contents, 50f, end, 500f - boxBorder, boxBorder, config.getTheme().getBoxTitleBackgroundColor());
			} else {
				drawBox(contents, 50f, entriesStart, 500f, boxBorder, config.getTheme().getBoxTitleBackgroundColor());
				drawBox(contents, 50f, end, boxBorder, entriesStart - end, config.getTheme().getBoxTitleBackgroundColor());
				drawBox(contents, 479f + boxBorder, end, boxBorder, entriesStart - end, config.getTheme().getBoxTitleBackgroundColor());
				drawBox(contents, 550f - boxBorder, end, boxBorder, entriesStart - end, config.getTheme().getBoxTitleBackgroundColor());
				drawBox(contents, 50f, end, 500f, boxBorder, config.getTheme().getBoxTitleBackgroundColor());
				
			}
		}
	}
	
	private void drawComment(PDDocument doc, CompanyFull company, Invoice invoice, String comment, byte[] logo, PrintInvoiceThemeConfiguration theme) throws IOException {
		if (comment != null && !comment.isEmpty()) {
			float firstY = y;
			
			drawText(contents, "Notas:", 50f, y, theme.getTextColor(), HELVETICA_BOLD, 10);
			List<String> lines = PDFToolkit.getLinesRespectOriginal(comment, 575-100-50, HELVETICA, 10);
			
			this.commentSize = lines.size() * 10;
			
			for (String line : lines) {
				line = line != null ? line.trim() : line;
				drawText(contents, line, 100f, y, theme.getTextColor(), HELVETICA, 10);
				y-=10;
				if (y < bottom) {
					contents.close();
					contents = drawPage(doc, company, invoice, config, logo, false);
					y = firstY;
				}
			}
			
			y -= 10;
		}
	}
	
	private void drawFooter(PDDocument doc, CompanyFull company, PrintInvoiceThemeConfiguration theme) throws IOException {
		if(company != null) {
			RecordData recordData = company.getRecordDatas() != null && !company.getRecordDatas().isEmpty() ? company.getRecordDatas().get(0) : null;
			Company reg = company.getRegistry();
			String companyName = "", registration = "", tomo = "", folio = "", hoja = "", fechaRegistro = "", nif = "";
			String registrationString = "";
			if (recordData != null && getConfig().isRecordData()) {
				companyName = AonStringUtils.trimToEmpty(reg != null ? reg.getName() : "");
				registration = AonStringUtils.trimToEmpty(recordData.getRegistration());
				tomo = AonStringUtils.trimToEmpty(recordData.getVolume());
				folio = AonStringUtils.trimToEmpty(recordData.getPage());
				hoja = AonStringUtils.trimToEmpty(recordData.getSheet());
				Date registryDate = recordData.getRecordDate();
				fechaRegistro = registryDate != null ? new SimpleDateFormat("dd/MM/yyyy").format(registryDate) : "";
				nif = AonStringUtils.trimToEmpty(reg != null ? reg.getDocument() : "");
			
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
						if ((HELVETICA.getStringWidth(sb.toString()) / 1000.0f * 7) <= 185) {
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
						if ((HELVETICA.getStringWidth(sb.toString()) / 1000.0f * 7) <= 185) {
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
						if ((HELVETICA.getStringWidth(sb.toString()) / 1000.0f * 7) <= 185) {
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
					if ((HELVETICA.getStringWidth(fullStr) / 1000.0f * 7) < 575) {
						mediaStr = fullStr;
					}
				
					drawText(contents
						, mediaStr
						, 10f
						, 20f
						, theme.getTextColor()
						, HELVETICA
						, 7);
				
				}
			
				PDFToolkit.drawBox(contents, 10, 15, 575, 1, PdfColors.GRAY);
			
				String page = "Pag. " + (i+1) + " de " + pageNumber;
			
				float pageNumWidth = HELVETICA.getStringWidth(page) / 1000f * 9;
				float pageNumInitX = 585 - pageNumWidth;
				
				float registrationStrWidth = HELVETICA.getStringWidth(registrationString) / 1000.0f * 7;
				float initRegistrationX = pageNumInitX - 5 - registrationStrWidth;
				
				drawTextRight(contents
					, new PDRectangle(570, 5, 15, 15)
					, page
					, theme.getTextColor()
					, HELVETICA
					, 9
					, 0
					, 0);
			
				drawText(contents
					, registrationString
					, 10f
					, 5f
					, theme.getTextColor()
					, HELVETICA
					, 7);
			
//			drawText(contents
//					, croppedString(companyName, initRegistrationX - 15 , HELVETICA, 7)
//					, 10f
//					, 5f
//					, BLACK
//					, HELVETICA
//					, 7);
//			
//			drawTextRight(contents
//					, new PDRectangle(initRegistrationX, 5, registrationStrWidth, 15)
//					, registrationString
//					, BLACK
//					, HELVETICA
//					, 7
//					, 0
//					, 0);
//			
				contents.close();
			}
		}
	}

	// DRAW PAGE
	private PDPageContentStream drawPage(PDDocument doc, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config, byte[] logo, boolean withHeader) throws IOException {
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

		drawTopInfo(doc, config, invoice, company, logo);
		
		y -= 60;
		if (withHeader)
			if (config.isDetailed())
				drawDetailedHeader(config.getTheme());
			else
				drawSimpleHeader(config.getTheme());
		
		return contents;
	}
	// DRAW FIRST PAGE
	private PDPageContentStream drawFirstPage(PDDocument doc, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config, byte[] logo) throws IOException {
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
		
		drawTopInfo(doc, config, invoice, company, logo);
		
		y -= 60;
		
		drawComment(doc, company, invoice, invoice.getComments(), logo, config.getTheme());
		
		if (config.isDetailed())
			drawDetailedHeader(config.getTheme());
		else
			drawSimpleHeader(config.getTheme());
		
		return contents;
	}

	// DRAW DETAILED ENTRIES
	public void drawDetailedEntries(PDDocument doc, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config, byte[] logo) throws IOException {
		boolean start = true;
		boolean shounenJump = false;
		if (invoice.getDetails() != null){
			int i = 0;
			int detailNum = invoice.getDetails().size();
			for (InvoiceDetail detail : invoice.getDetails()) {
				
				x = 50;
				String description = 
						safeString(detail.getDescription())
							.replace("\t", " ");

				ArrayList<String> divided = (ArrayList<String>) PDFToolkit.getLinesRespectOriginal(description, 240,PdfFonts.HELVETICA, 8);				
				float lineDiff = 10;
				
				int maxLinesLast = Math.round((height - top - topInfoHeight - limit) / lineDiff);
				int maxLinesNoLast = Math.round((height - top - topInfoHeight - bottom) / lineDiff);
				float stringHeight = lineDiff * divided.size();
				
				boolean isComment = invoice.getComments() != null && !invoice.getComments().isEmpty();
				
				float relativeLimit = (i < detailNum -1) ? bottom : limit;
				if (relativeLimit == bottom)
					drawExtraBoxBackground(config);
				
				if (start && isComment && y - stringHeight <= limit) {
					drawExtraBoxBackground(config);
					relativeLimit = bottom;
					start = true;
					if (y - stringHeight > bottom)
						shounenJump = true;
				}
					
				if (y <= relativeLimit) {
					jumpToNewPage(doc, company, invoice, config, logo);
				}
				
				if ( y - stringHeight <= relativeLimit) {
					drawExtraBoxBackground(config);
					StringBuilder extraBuilder = new StringBuilder("");
					while ( y - stringHeight <= bottom) {
						extraBuilder.insert(0, divided.get(divided.size() - 1) + "\n");
						divided.remove(divided.size() - 1);
						stringHeight = lineDiff * divided.size();
					}
					drawDetail(i, detail, divided, lineDiff, config.getTheme());
					jumpToNewPage(doc, company, invoice, config, logo);
					List<String> extraLines = PDFToolkit.getLinesRespectOriginal(AonStringUtils.trimToEmpty(extraBuilder.toString()), 240,PdfFonts.HELVETICA, 8);
					int extraLineNum = extraLines.size();
					
					List<String> xtra = new LinkedList<>();
					while (extraLineNum > maxLinesNoLast) {
						xtra.clear();
						for (int j=0;j<maxLinesNoLast && !extraLines.isEmpty();j++) {
							xtra.add(extraLines.get(0));
							extraLines.remove(0);
						}
						drawExtraBoxBackground(config);
						drawExtraLines(i, xtra, lineDiff, config.getTheme());
						extraLineNum = extraLines.size();
						jumpToNewPage(doc, company, invoice, config, logo);
						
					}
					
					if (extraLineNum > maxLinesLast) {
						drawExtraBoxBackground(config);
						xtra.clear();
						for (int j=0;j<maxLinesNoLast && !extraLines.isEmpty();j++) {
							xtra.add(extraLines.get(0));
							extraLines.remove(0);
						}
						drawExtraLines(i, xtra, lineDiff, config.getTheme());
						jumpToNewPage(doc, company, invoice, config, logo);
					}
					
					drawExtraLines(i, extraLines, lineDiff, config.getTheme());
				} else {
					drawDetail(i, detail, divided, lineDiff, config.getTheme());
				}
				
				if (shounenJump) {
					jumpToNewPage(doc, company, invoice, config, logo);
					shounenJump = false;
				}
					
				i++;

			}
		}
	}


	private void drawExtraBoxBackground(PrintInvoiceConfiguration config) throws IOException {
		if (config.isDetailed()) {			
			drawBox(contents, 50, bottom, 250 - boxBorder, limit - bottom/* + 0.3f*/, config.getTheme().getBoxBodyBackgroundColor());
			
			drawBox(contents, 300, bottom, 70 - boxBorder, limit - bottom/* + 0.3f*/, config.getTheme().getBoxBodyBackgroundColor());
			drawBox(contents, 370, bottom, 70 - boxBorder, limit - bottom/* + 0.3f*/, config.getTheme().getBoxBodyBackgroundColor());
			drawBox(contents, 440, bottom, 40 - boxBorder, limit - bottom/* + 0.3f*/, config.getTheme().getBoxBodyBackgroundColor());
			drawBox(contents, 480, bottom, 70, limit - bottom/* + 0.3f*/, config.getTheme().getBoxBodyBackgroundColor());
			
		} else {
			drawBox(contents, 50, bottom, 429, limit - bottom + 0.3f, config.getTheme().getBoxBodyBackgroundColor());
			drawBox(contents, 480, bottom, 70, limit - bottom + 0.3f, config.getTheme().getBoxBodyBackgroundColor());
		}
	}

	private void jumpToNewPage(PDDocument doc, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config,
			byte[] logo) throws IOException {
		drawJail(bottom);
		contents.close();
		contents = drawPage(doc, company, invoice, config, logo, true);
		y		 = height - top - topInfoHeight - 5;
		x		 = 50;
	}

	private void drawDetail(int i, InvoiceDetail detail, List<String> divided, float lineDiff, PrintInvoiceThemeConfiguration theme) throws IOException {
		float dy = y;

		for (String str : divided)
		{
			drawText(contents, str.trim(), x + 5, dy, theme.getBoxBodyTextColor(), PdfFonts.HELVETICA, 8, i + DETAIL_DESCRIPTION);
			dy -= lineDiff;
		}

		x += 250;

		drawTextRight(contents, new PDRectangle(x, y, 69, 15), toLatinNumber(detail.getQuantity()) + "", theme.getBoxBodyTextColor(), HELVETICA, 8, 4.5f, 0, i + DETAIL_AMOUNT);
		x += 70;

		drawTextRight(contents, new PDRectangle(x, y, 69, 15),toLatinNumber(detail.getPrice()), theme.getBoxBodyTextColor(), HELVETICA, 8, 4.5f, 0, i + DETAIL_PRICE);
		x += 70;

		drawTextRight(contents, new PDRectangle(x, y, 39, 15), safeString(detail.getDiscountExpression()), theme.getBoxBodyTextColor(), HELVETICA, 8, 4.5f, 0, i + DETAIL_DISCOUNT);
		x += 40;

		drawTextRight(contents, new PDRectangle(x, y, 69, 15), toLatinNumber(detail.getTaxableBase()), theme.getBoxBodyTextColor(), HELVETICA, 8, 4.5f, 0, i + DETAIL_TOTAL);
		y = dy - 3;
	}
	
	private void drawExtraLines(int i, List<String> divided, float lineDiff, PrintInvoiceThemeConfiguration theme) throws IOException {
		float dy = y;
		
		for (String str : divided)
		{
			drawText(contents, str.trim(), x + 5, dy, theme.getBoxBodyTextColor(), PdfFonts.HELVETICA, 8, i + DETAIL_DESCRIPTION);
			dy -= lineDiff;
		}
		y = dy - 3;
	}

	// DRAW SIMPLIFIED ENTRIES
	public void drawSimplifiedEntries(PDDocument doc, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config, byte[] logo) throws IOException {
		if (invoice.getDetails() != null) {
			boolean xtraBack = false;
			for (InvoiceDetail detail : invoice.getDetails()) {
				x = 50;
				
				if (y <= bottom) {
					jumpToNewPage(doc, company, invoice, config, logo);
				}
				String description = detail.getDescription().replace("\t", " ");
//						.getBytes(Charset.forName("ASCII")), Charset.forName("UTF-8");
//				croppedString(description, 420, PdfFonts.HELVETICA, 8), x + 5, y,
				
				if (y < limit && !xtraBack) {
					drawExtraBoxBackground(config);
					xtraBack = true;
				}
					
				drawText(
					contents,
					PDFToolkit.croppedStringWholeWord(description, 420, PdfFonts.HELVETICA, 8), x + 5, y,
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
			
			if (y < limit) {
				jumpToNewPage(doc, company, invoice, config, logo);
			}
			
		}
	}

	// DRAW UPPER INFO
	private void drawTopInfo(PDDocument doc, PrintInvoiceConfiguration config, Invoice invoice, CompanyFull company, byte[] logo) throws IOException {
		
		float maxHeight = MAX_LOGO_HEIGHT;
		float maxWidth = 297 - x - 20;
//		float logoHeight = PDFToolkit.getLogoFinalHeight(logo, maxHeight, maxWidth);
		
		
		float tempY = height - MIN_HEADER_FOR_LOGO + 20;
		float logoX = x;
		float logoY = tempY - 10;
		String web = null;
		
		if (company != null && config.isCompany()) {
			Company registry = company.getRegistry();
			String companyName = registry != null ? AonStringUtils.trimToEmpty(registry.getName()) : "";
			companyName = croppedString(companyName, 240, HELVETICA, 9);
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

			drawText(contents, companyName, x + 260, tempY + 35, config.getTheme().getTextColor(), HELVETICA_BOLD, 10);		
			
			drawText(contents, "NIF:", x + 260, tempY + 22, config.getTheme().getTextColor(), HELVETICA, 8);		
			drawText(contents, nif, x + 280, tempY + 22, config.getTheme().getTextColor(), HELVETICA, 8);

			List<String> addressLines = getLines(address, 235, HELVETICA, 8);
			float addrPlus = 11;
			
			if (!addressLines.isEmpty()) {
				drawText(contents, AonStringUtils.trimToEmpty(addressLines.get(0)), x + 260, tempY + addrPlus, config.getTheme().getTextColor(), HELVETICA, 8);
				addrPlus -= 9;
				if (addressLines.size() > 1) {
					StringBuilder addressBuilder = new StringBuilder("");
					for(int i=1; i<addressLines.size(); i++) {
						addressBuilder.append(addressLines.get(i));
					}
					drawText(contents, croppedString(AonStringUtils.trimToEmpty(addressBuilder.toString()), 235, HELVETICA, 8), x + 260, tempY + addrPlus, config.getTheme().getTextColor(), HELVETICA, 8);
					addrPlus -= 9;
				}
			}
			addrPlus -= 1;
			
				List<String> zipLines = getLines(zip, 235, HELVETICA, 8);
				if (!zipLines.isEmpty()) {
					drawText(contents, AonStringUtils.trimToEmpty(zipLines.get(0)), x + 260, tempY + addrPlus, config.getTheme().getTextColor(), HELVETICA, 8);
					addrPlus -= 9;
					if (zipLines.size() > 1) {
						StringBuilder zipBuilder = new StringBuilder("");
						for(int i=1; i<zipLines.size(); i++) {
							zipBuilder.append(zipLines.get(i));
						}
						drawText(contents, croppedString(AonStringUtils.trimToEmpty(zipBuilder.toString()), 235, HELVETICA, 8), x + 260, tempY + addrPlus, config.getTheme().getTextColor(), HELVETICA, 8);
					}
				}

//				y -= 30;
		}
				
		if (logo != null) {				
			PDFToolkit.drawResizedLogo(doc, doc.getPage(pageNumber - 1), contents, logo, logoX, logoY, maxHeight, maxWidth, web);
		}
		
		drawText(contents, getMsg().invoice().toUpperCase(), x, y, config.getTheme().getTextColor(), HELVETICA_BOLD, 16);
		y -= 30;

		drawText(contents, getMsg().number() + ":", x, y, config.getTheme().getTextColor(), HELVETICA_BOLD, 11,REFERENCE_NUMBER);
		drawText(contents, safeString(invoice.getReferenceCode()), x + 50, y, config.getTheme().getTextColor(), HELVETICA, 11,REFERENCE_NUMBER);
		y -= 4;

//		drawBox(contents, x + 46, y, 150, .5f, BLACK);
		y -= 16;

		drawText(contents, getMsg().date() + ":", x, y, config.getTheme().getTextColor(), HELVETICA_BOLD, 11 , INVOICE_DATE);
		drawText(contents, formatDate(invoice.getIssueDate(), "dd/MM/yyyy").orElse(""), x + 50, y, config.getTheme().getTextColor(), HELVETICA, 11 , INVOICE_DATE);

		y -= 4;

//		drawBox(contents, x + 46, y, 150, .5f, BLACK);
		y -= 16;

		drawText(contents, "N.I.F.:", x, y, config.getTheme().getTextColor(), HELVETICA_BOLD, 11, NIF);
		drawText(contents, safeString(invoice.getRegistryDocument()), x + 50, y, config.getTheme().getTextColor(), HELVETICA, 11, NIF);
		y -= 4;

//		drawBox(contents, x + 46, y, 150, .5f, BLACK);
		y -= 6;
		x += 250;

		drawBox(contents, x, y, 250, 80, config.getTheme().getCustomerBackgroundColor());
		x += 10;
		y  = height - top - 35 /*- (company != null ? 30 : 0)*/;
		String str = safeString(invoice.getRegistryName())
				.replace("\t", " ")
				/*.getBytes(Charset.forName("ASCII")), Charset.forName("UTF-8")*/;
		
		List<String> nameLines = PDFToolkit.getLines(str, 230, HELVETICA_BOLD, 10);
		
		if (nameLines != null) {
			String line1 = nameLines.get(0).trim();
			String line2 = null;
			if (nameLines.size() > 1) {
				line2 = nameLines.get(1).trim();
			}
			
			if (line1 != null) {
				drawText(contents, line1, x, y, config.getTheme().getTextColor(), HELVETICA_BOLD, 10, REGISTRY_NAME);
				y -= 10;
			}
			if (line2 != null) {
				drawText(contents, line2, x, y, config.getTheme().getTextColor(), HELVETICA_BOLD, 10, REGISTRY_NAME);				
			}
		}
		y -= 15;
		
		if (invoice.getRegistryAddressData() != null && !invoice.getRegistryAddressData().isEmpty()) {
			RegistryAddress address = invoice.getRegistryAddressData();
			str = safeString(address.getFullAddress());
			str = croppedString(str, 230, HELVETICA, 9);
			drawText(contents,  str, x, y, config.getTheme().getTextColor(), HELVETICA, 9, ADDRESS);

			y -= 10;

			String zipCityProvince =  safeString(address.getZip()) + " "+  safeString(address.getCity()) + " " +  safeString(address.getProvince()); 
			drawText(contents, zipCityProvince.trim(), x, y, config.getTheme().getTextColor(), HELVETICA, 9, ADDRESS_LINE_TWO);
		} else {
			str = safeString(invoice.getAddress());
			str = croppedString(str, 230, HELVETICA, 9);
			drawText(contents,  str, x, y, config.getTheme().getTextColor(), HELVETICA, 9, ADDRESS);

			y -= 10;

			String zipCityProvince =  safeString(invoice.getAddressZIP()) + " "+  safeString(invoice.getAddressTown()) + " " +  safeString(invoice.getAddressProvince()); 
			drawText(contents, zipCityProvince.trim(), x, y, config.getTheme().getTextColor(), HELVETICA, 9, ADDRESS_LINE_TWO);
		}

	}

	// DRAW DETAILED HEADER
	private void drawDetailedHeader(PrintInvoiceThemeConfiguration theme) throws IOException {
		x  = 50;

		drawBox(contents, x, y, 250 - boxBorder, 15, theme.getBoxTitleBackgroundColor());
		drawTextCenter(contents, new PDRectangle(x, y, 249, 15), getMsg().description(), theme.getBoxTitleTextColor(), HELVETICA_BOLD, 9, 4.5f);
		x += 250;

		drawBox(contents, x, y, 70 - boxBorder, 15, theme.getBoxTitleBackgroundColor());
		drawTextCenter(contents, new PDRectangle(x, y, 69, 15), getMsg().quantity(), theme.getBoxTitleTextColor(), HELVETICA_BOLD, 9, 4.5f);
		x += 70;

		drawBox(contents, x, y, 70 - boxBorder, 15, theme.getBoxTitleBackgroundColor());
		drawTextCenter(contents, new PDRectangle(x, y, 69, 15), getMsg().price(), theme.getBoxTitleTextColor(), HELVETICA_BOLD, 9, 4.5f);
		x += 70;

		drawBox(contents, x, y, 40 - boxBorder, 15, theme.getBoxTitleBackgroundColor());
		drawTextCenter(contents, new PDRectangle(x, y, 39, 15), "%Dto.", theme.getBoxTitleTextColor(), HELVETICA_BOLD, 9, 4.5f);
		x += 40;

		drawBox(contents, x, y, 70, 15, theme.getBoxTitleBackgroundColor());
		drawTextRight(contents, new PDRectangle(x, y, 69, 15), getMsg().amount(), theme.getBoxTitleTextColor(), HELVETICA_BOLD, 9, 5, 4.5f);
		
		if (config.getTheme().isBoxTitleBorder()) {
			drawBox(contents, 50, y + 14 + boxBorder, 500, boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 50, y, boxBorder, 14f + boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 300 - boxBorder, y, boxBorder, 15f, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 370 - boxBorder, y, boxBorder, 15f, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 440 - boxBorder, y, boxBorder, 15f, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 480 - boxBorder, y, boxBorder, 15f, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 550 - boxBorder, y, boxBorder, 15f, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 50, y, 500, boxBorder, theme.getBoxTitleBackgroundColor());
			
		}
		
		drawBox(contents, 50, limit, 250 - boxBorder, y - limit, theme.getBoxBodyBackgroundColor());
		drawBox(contents, 300, limit, 70 - boxBorder, y - limit, theme.getBoxBodyBackgroundColor());
		drawBox(contents, 370, limit, 70 - boxBorder, y - limit, theme.getBoxBodyBackgroundColor());
		drawBox(contents, 440, limit, 40 - boxBorder, y - limit, theme.getBoxBodyBackgroundColor());
		drawBox(contents, 480, limit, 70, y - limit, theme.getBoxBodyBackgroundColor());
		
		entriesStart = y;
		
		
		
		
	}

	// DRAW SIMPLE HEADER
	private void drawSimpleHeader(PrintInvoiceThemeConfiguration theme) throws IOException {
		x  = 50;
		drawBox(contents, x, y, 429 + boxBorder, 15, theme.getBoxTitleBackgroundColor());
		drawText(contents, getMsg().description(), x + 5f, y + 4.5f, theme.getBoxTitleTextColor(), HELVETICA_BOLD, 9);
		x += 430;

		drawBox(contents, x, y, 70, 15, theme.getBoxTitleBackgroundColor());
		drawText(contents, getMsg().amount(), x + 5f, y + 4.5f, theme.getBoxTitleTextColor(), HELVETICA_BOLD, 9);
		
		if (config.getTheme().isBoxTitleBorder()) {
			drawBox(contents, 50, y + 14 + boxBorder, 500, boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 50, y, boxBorder, 14f + boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 480 - boxBorder, y, boxBorder, 14f + boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 550 - boxBorder, y, boxBorder, 14f + boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 50, y, 500 - boxBorder, boxBorder, theme.getBoxTitleBackgroundColor());
			
		}
		
		drawBox(contents, 50, limit, 429 + boxBorder, y - limit, theme.getBoxBodyBackgroundColor());
		drawBox(contents, 480, limit, 70, y - limit, theme.getBoxBodyBackgroundColor());
		
		
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

		drawBox(contents, x, y, 80 - boxBorder, 15, theme.getBoxTitleBackgroundColor());
		drawTextRight(contents, new PDRectangle(x, y, 79, 15), getMsg().base(), theme.getBoxTitleTextColor(), HELVETICA, 9, 5, 4.5f);
		x += 80;

		drawBox(contents, x, y, 80 - boxBorder, 15, theme.getBoxTitleBackgroundColor());
		drawTextRight(contents, new PDRectangle(x, y, 79, 15), "%", theme.getBoxTitleTextColor(), HELVETICA, 9, 5, 4.5f);
		x += 80;

		drawBox(contents, x, y, 60 - boxBorder, 15, theme.getBoxTitleBackgroundColor());
		drawTextCenter(contents, new PDRectangle(x, y, 59, 15), getMsg().type(), theme.getBoxTitleTextColor(), HELVETICA, 9, 4.5f);
		x += 60;
		
		drawBox(contents, x, y, 50 - boxBorder, 15, theme.getBoxTitleBackgroundColor());
		drawTextRight(contents, new PDRectangle(x, y, 49, 15), getMsg().quota(), theme.getBoxTitleTextColor(), HELVETICA, 9, 5, 4.5f);
		x += 50;

		drawBox(contents, x, y, 100, 15, theme.getBoxTitleBackgroundColor());
		drawTextCenter(contents, new PDRectangle(x, y, 99, 15), getMsg().totalInvoice(), theme.getBoxTitleTextColor(), HELVETICA_BOLD, 9, 4.5f);
		
		if (config.getTheme().isBoxTitleBorder()) {
			drawBox(contents, 180, y + 14 + boxBorder, 370, boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 180, y, boxBorder, 14f + boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 260 - boxBorder, y, boxBorder, 14f + boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 340 - boxBorder, y, boxBorder, 14f + boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 400 - boxBorder, y, boxBorder, 14f + boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 450 - boxBorder, y, boxBorder, 14f + boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 550 - boxBorder, y, boxBorder, 14f + boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 180, y, 370, boxBorder, theme.getBoxTitleBackgroundColor());
		}
		
		if (invoice.getBreakdown() != null){
//			double sum = 0;
			int i = 0;
			float initY = y;
			
			float bdSize = invoice.getBreakdown().size() * 10 + 10;
			
			drawBox(contents, 180, y, 80 - boxBorder, -bdSize, theme.getBoxBodyBackgroundColor());
			drawBox(contents, 260, y, 80 - boxBorder, -bdSize, theme.getBoxBodyBackgroundColor());
			drawBox(contents, 340, y, 60 - boxBorder, -bdSize, theme.getBoxBodyBackgroundColor());
			drawBox(contents, 400, y, 50 - boxBorder, -bdSize, theme.getBoxBodyBackgroundColor());
			drawBox(contents, 450, y, 100f, -bdSize, theme.getBoxBodyBackgroundColor());
			
			
			for (InvoiceBreakdown tax : invoice.getBreakdown()) {
				
				x = 180;
				drawTextRight(contents, new PDRectangle(x, y, 79, 15), toLatinNumber(tax.getBase()), theme.getBoxBodyTextColor(), HELVETICA, 7, 5, -12, i + TAX_BASE);
				x += 80;
				
				String percent = "";
				if (tax.getPercentage() > 99)
					percent = "100%";
				else if (tax.getPercentage() != 0) {
					percent = toLatinNumber(tax.getPercentage()) + "%";
				
					if(tax.getSurcharge() != 0.00)
						percent += " + " +  toLatinNumber(tax.getSurcharge());
			
				}

				drawTextRight(contents, new PDRectangle(x, y, 79, 15), percent, theme.getBoxBodyTextColor(), HELVETICA, 7, 5, -12, i + TAX_PERCENTAGE);
				x += 80;
				
				drawTextCenter(contents, new PDRectangle(x, y, 59, 15), tax.getTaxType().getName(), theme.getBoxBodyTextColor(), HELVETICA, 7, -12, i + TAX_TYPE);
				x += 60;
				
				drawTextRight(contents, new PDRectangle(x, y, 49, 15), toLatinNumber(tax.getQuota() + tax.getSurchargeQuota()), theme.getBoxBodyTextColor(), HELVETICA, 7, 5, -12, i + TAX_QUOTE);
				x += 50;
				
//				sum	+= tax.getQuota() + tax.getSurchargeQuota() + tax.getBase();
				y	-= 10;
				
				i++;
			}
			drawTextRight(contents, new PDRectangle(x, bottom + 107 + bottomExtra, 99, 15), toLatinNumber(invoice.getTotal()) + " \u20AC", theme.getBoxBodyTextColor(), HELVETICA_BOLD, 8, 5, -14, INVOICE_TOTAL);
			
			float finalY = y -10;
			float height = initY - finalY;
			
			if (config.getTheme().isBoxBodyBorder()) {
				drawBox(contents, 180, finalY + height, 370, boxBorder, theme.getBoxTitleBackgroundColor());
				drawBox(contents, 180, finalY, boxBorder, height, theme.getBoxTitleBackgroundColor());
				drawBox(contents, 260 - boxBorder, finalY, boxBorder, height, theme.getBoxTitleBackgroundColor());
				drawBox(contents, 340 - boxBorder, finalY, boxBorder, height, theme.getBoxTitleBackgroundColor());
				drawBox(contents, 400 - boxBorder, finalY, boxBorder, height, theme.getBoxTitleBackgroundColor());
				drawBox(contents, 450 - boxBorder, finalY, boxBorder, height, theme.getBoxTitleBackgroundColor());
				drawBox(contents, 550 - boxBorder, finalY, boxBorder, height, theme.getBoxTitleBackgroundColor());
				drawBox(contents, 180, finalY, 370, boxBorder, theme.getBoxTitleBackgroundColor());
			}
		}
	}

	// DRAW FINANCES
	private void drawFinances(Invoice invoice, PrintInvoiceThemeConfiguration theme) throws IOException {

		x = 180;
//		y = bottom + 50 + bottomExtra;
		y-= 28;

		drawBox(contents, x, y, 60 - boxBorder, 15,  theme.getBoxTitleBackgroundColor());
		drawText(contents, getMsg().date(), x + 5f, y + 4.5f, theme.getBoxTitleTextColor(), HELVETICA, 9);
		x += 60;

		drawBox(contents, x, y, 80 - boxBorder, 15,  theme.getBoxTitleBackgroundColor());
		drawText(contents, getMsg().payMethod(), x + 5f, y + 4.5f, theme.getBoxTitleTextColor(), HELVETICA, 9);
		x += 80;

		drawBox(contents, x, y, 160 - boxBorder, 15, theme.getBoxTitleBackgroundColor());
		drawText(contents, getMsg().bankAccount(), x + 5f, y + 4.5f, theme.getBoxTitleTextColor(), HELVETICA, 9);
		x += 160;

		drawBox(contents, x, y, 70, 15, theme.getBoxTitleBackgroundColor());
		drawTextRight(contents, new PDRectangle(x, y, 69, 15), getMsg().amount(), theme.getBoxTitleTextColor(), HELVETICA, 9, 5, 4.5f);
		
		if (config.getTheme().isBoxTitleBorder()) {
			drawBox(contents, 180, y + 14 + boxBorder, 370, boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 180, y, boxBorder, 14f + boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 240 - boxBorder, y, boxBorder, 14f + boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 320 - boxBorder, y, boxBorder, 14f + boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 480 - boxBorder, y, boxBorder, 14f + boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 550 - boxBorder, y, boxBorder, 14f + boxBorder, theme.getBoxTitleBackgroundColor());
			drawBox(contents, 180, y, 370, boxBorder, theme.getBoxTitleBackgroundColor());
		}

		if(invoice.getFinances() != null) {
			int i = 0;
			float initY = y;
			
			float fSize = invoice.getFinances().size() * 10 + 10;
			
			drawBox(contents, 180, y, 60 - boxBorder, -fSize, theme.getBoxBodyBackgroundColor());
			drawBox(contents, 240, y, 80 - boxBorder, -fSize, theme.getBoxBodyBackgroundColor());
			drawBox(contents, 320, y, 160 - boxBorder, -fSize, theme.getBoxBodyBackgroundColor());
			drawBox(contents, 480, y, 70f, -fSize, theme.getBoxBodyBackgroundColor());
			
			
			for (Finance finance : invoice.getFinances()) {
				x = 180;
				drawText(contents, formatDate(finance.getDueDate(), "dd/MM/yyyy").orElse(""), x + 5f, y - 12, theme.getBoxBodyTextColor(), HELVETICA, 7, i + FINANCE_DATE);
				x += 60;
				
				String paymethod = finance.getPayMethodName() != null ? finance.getPayMethodName() : (finance.getPayMethodType() != null ? finance.getPayMethodType().getDescription(): "");
				drawText(contents, paymethod, x + 5f, y - 12, theme.getBoxBodyTextColor(), HELVETICA,7, i + FINANCE_PAY_METHOD);
				x += 80;
			
				if(finance.getBankAccount() != null && finance.getBankAccount().getIban() != null)
					drawText(contents, finance.getBankAccount().getIban(), x + 5f, y - 12, theme.getBoxBodyTextColor(), HELVETICA, 7, i + FINANCE_BANK_ACCOUNT);
				else
					drawText(contents, "", x + 5f, y - 12, theme.getBoxBodyTextColor(), HELVETICA, 7, i + FINANCE_BANK_ACCOUNT);
			
				x += 160;
				drawTextRight(contents, new PDRectangle(x, y, 69, 15), toLatinNumber(finance.getAmount()), theme.getBoxBodyTextColor(), HELVETICA, 7, 5, -12, i + FINANCE_AMOUNT);
				
				y -= 10;
				i++;
			}
			
			if (config.getTheme().isBoxBodyBorder()) {
				y -= 10;
				float height  = initY - y;
				drawBox(contents, 180, y, 370, boxBorder, theme.getBoxTitleBackgroundColor());
				drawBox(contents, 180, y, boxBorder, height, theme.getBoxTitleBackgroundColor());
				drawBox(contents, 240 - boxBorder, y, boxBorder, height, theme.getBoxTitleBackgroundColor());
				drawBox(contents, 320 - boxBorder, y, boxBorder, height, theme.getBoxTitleBackgroundColor());
				drawBox(contents, 480 - boxBorder, y, boxBorder, height, theme.getBoxTitleBackgroundColor());
				drawBox(contents, 550 - boxBorder, y, boxBorder, height, theme.getBoxTitleBackgroundColor());
				drawBox(contents, 180, initY, 370 - boxBorder, boxBorder, theme.getBoxTitleBackgroundColor());
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
