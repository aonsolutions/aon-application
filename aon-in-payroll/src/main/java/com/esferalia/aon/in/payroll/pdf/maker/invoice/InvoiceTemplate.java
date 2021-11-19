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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.watson.util.AonStringUtils;

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

	byte[] background;
	byte[] qrCode;
	boolean adapt;
	PDPageContentStream	contents;
	int pageNumber;
	InvoiceTemplateMsg msg;
	PrintInvoiceConfiguration config;
	
	// THE PDF DOCUMENT
	public static void create(OutputStream os, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config, byte[] qrCode, byte[] logo) throws IOException, CanNotCreatePdfException {
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
			template.qrCode = qrCode;

			template.contents = template.drawFirstPage(doc, company, invoice, config, logo);
			
			template.y -= 15;
			
			
			if (config.isDetailed())
				template.drawDetailedEntries(doc, company, invoice, config, logo);
			else
				template.drawSimplifiedEntries(doc, company, invoice, config, logo);

			template.drawBottomInfo(doc, invoice);
			
			template.contents.close();
			template.drawFooter(doc, company);				
			doc.save(template.filename);
			new OutputStreamWriter(os,StandardCharsets.ISO_8859_1);
		} catch (Exception e)
		{
			throw new CanNotCreatePdfException(e);
		}
	}
	
	private void drawComment(PDDocument doc, CompanyFull company, Invoice invoice, String comment, byte[] logo) throws IOException {
		if (comment != null && !comment.isEmpty()) {
			float firstY = y;
			
			drawText(contents, "Notas:", 50f, y, BLACK, HELVETICA_BOLD, 10);
			List<String> lines = PDFToolkit.getLinesRespectOriginal(comment, 575-100-50, HELVETICA, 10);
			
			this.commentSize = lines.size() * 10;
			
			for (String line : lines) {
				line = line != null ? line.trim() : line;
				drawText(contents, line, 100f, y, BLACK, HELVETICA, 10);
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
	
	private void drawFooter(PDDocument doc, CompanyFull company) throws IOException {
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
						, BLACK
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
					, BLACK
					, HELVETICA
					, 9
					, 0
					, 0);
			
				drawText(contents
					, registrationString
					, 10f
					, 5f
					, BLACK
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
	
	private void drawPageNums(PDDocument doc) throws IOException {
		for (int i=0; i<this.pageNumber; i++) {
			contents = new PDPageContentStream(doc, doc.getPage(i), PDPageContentStream.AppendMode.APPEND, true);
			drawPageNumber(i+1);
			contents.close();
		}
	}
	
	//DRAW PAGE NUMBER
	private void drawPageNumber(int num) throws IOException {
		drawTextRight(contents
				, new PDRectangle(570, 5, 15, 15)
				, "Pag. " + num + " de " + pageNumber
				, BLACK
				, HELVETICA
				, 9
				, 0
				, 0);
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
		
		limit  = bottomInfoHeight + bottom;

		x = 50f;
		y = height - top - 20;

		drawTopInfo(doc, config, invoice, company, logo);
		
		y -= 60;
		if (withHeader)
			if (config.isDetailed())
				drawDetailedHeader();
			else
				drawSimpleHeader();

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
		
		x = 50f;
		y = height - top - 20;
		
		drawTopInfo(doc, config, invoice, company, logo);
		
		y -= 60;
		
		drawComment(doc, company, invoice, invoice.getComments(), logo);
		
		if (config.isDetailed())
			drawDetailedHeader();
		else
			drawSimpleHeader();
		
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
				
				if (start && isComment && y - stringHeight <= limit) {
					relativeLimit = bottom;
					start = true;
					if (y - stringHeight > bottom)
						shounenJump = true;
				}
				
				
				if (y <= relativeLimit) {
					jumpToNewPage(doc, company, invoice, config, logo);
				}
				
				if ( y - stringHeight <= relativeLimit) {
					
					
					StringBuilder extraBuilder = new StringBuilder("");
					while ( y - stringHeight <= bottom) {
						extraBuilder.insert(0, divided.get(divided.size() - 1) + "\n");
						divided.remove(divided.size() - 1);
						stringHeight = lineDiff * divided.size();
					}
					drawDetail(i, detail, divided, lineDiff);
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
						drawExtraLines(i, xtra, lineDiff);
						extraLineNum = extraLines.size();
						jumpToNewPage(doc, company, invoice, config, logo);
						
					}
					
					if (extraLineNum > maxLinesLast) {
						xtra.clear();
						for (int j=0;j<maxLinesNoLast && !extraLines.isEmpty();j++) {
							xtra.add(extraLines.get(0));
							extraLines.remove(0);
						}
						drawExtraLines(i, xtra, lineDiff);
						jumpToNewPage(doc, company, invoice, config, logo);
					}
					
					drawExtraLines(i, extraLines, lineDiff);
				} else {
					drawDetail(i, detail, divided, lineDiff);
				}
				
				if (shounenJump) {
					jumpToNewPage(doc, company, invoice, config, logo);
					shounenJump = false;
				}
					
				i++;

			}
		}
	}

	private void jumpToNewPage(PDDocument doc, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config,
			byte[] logo) throws IOException {
		contents.close();
		contents = drawPage(doc, company, invoice, config, logo, true);
		y		 = height - top - topInfoHeight - 5;
		x		 = 50;
	}

	private void drawDetail(int i, InvoiceDetail detail, List<String> divided, float lineDiff) throws IOException {
		float dy = y;

		for (String str : divided)
		{
			drawText(contents, str.trim(), x + 5, dy, PdfColors.BLACK, PdfFonts.HELVETICA, 8, i + DETAIL_DESCRIPTION);
			dy -= lineDiff;
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
	}
	
	private void drawExtraLines(int i, List<String> divided, float lineDiff) throws IOException {
		float dy = y;

		for (String str : divided)
		{
			drawText(contents, str.trim(), x + 5, dy, PdfColors.BLACK, PdfFonts.HELVETICA, 8, i + DETAIL_DESCRIPTION);
			dy -= lineDiff;
		}
		y = dy - 3;
	}

	// DRAW SIMPLIFIED ENTRIES
	public void drawSimplifiedEntries(PDDocument doc, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config, byte[] logo) throws IOException {
		if (invoice.getDetails() != null) {
			
			for (InvoiceDetail detail : invoice.getDetails()) {
				x = 50;
				
				if (y <= bottom) {
					contents.close();
					contents = drawPage(doc, company, invoice, config, logo, true);
					y		 = height - top - topInfoHeight - 5;
					x		 = 50;
				}
				String description = detail.getDescription().replace("\t", " ");
//						.getBytes(Charset.forName("ASCII")), Charset.forName("UTF-8");
//				croppedString(description, 420, PdfFonts.HELVETICA, 8), x + 5, y,
				
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

			drawText(contents, companyName, x + 260, tempY + 35, Color.BLACK, HELVETICA_BOLD, 10);		
			
			drawText(contents, "NIF:", x + 260, tempY + 22, Color.BLACK, HELVETICA, 8);		
			drawText(contents, nif, x + 280, tempY + 22, Color.BLACK, HELVETICA, 8);

			List<String> addressLines = getLines(address, 235, HELVETICA, 8);
			float addrPlus = 11;
			
			if (!addressLines.isEmpty()) {
				drawText(contents, AonStringUtils.trimToEmpty(addressLines.get(0)), x + 260, tempY + addrPlus, Color.BLACK, HELVETICA, 8);
				addrPlus -= 9;
				if (addressLines.size() > 1) {
					StringBuilder addressBuilder = new StringBuilder("");
					for(int i=1; i<addressLines.size(); i++) {
						addressBuilder.append(addressLines.get(i));
					}
					drawText(contents, croppedString(AonStringUtils.trimToEmpty(addressBuilder.toString()), 235, HELVETICA, 8), x + 260, tempY + addrPlus, Color.BLACK, HELVETICA, 8);
					addrPlus -= 9;
				}
			}
			addrPlus -= 1;
			
				List<String> zipLines = getLines(zip, 235, HELVETICA, 8);
				if (!zipLines.isEmpty()) {
					drawText(contents, AonStringUtils.trimToEmpty(zipLines.get(0)), x + 260, tempY + addrPlus, Color.BLACK, HELVETICA, 8);
					addrPlus -= 9;
					if (zipLines.size() > 1) {
						StringBuilder zipBuilder = new StringBuilder("");
						for(int i=1; i<zipLines.size(); i++) {
							zipBuilder.append(zipLines.get(i));
						}
						drawText(contents, croppedString(AonStringUtils.trimToEmpty(zipBuilder.toString()), 235, HELVETICA, 8), x + 260, tempY + addrPlus, Color.BLACK, HELVETICA, 8);
					}
				}

//				y -= 30;
		}
				
		if (logo != null) {				
			PDFToolkit.drawResizedLogo(doc, doc.getPage(pageNumber - 1), contents, logo, logoX, logoY, maxHeight, maxWidth, web);
		}
		
		drawText(contents, getMsg().invoice().toUpperCase(), x, y, BLACK, HELVETICA_BOLD, 16);
		y -= 30;

		drawText(contents, getMsg().number() + ":", x, y, BLACK, HELVETICA_BOLD, 11,REFERENCE_NUMBER);
		drawText(contents, safeString(invoice.getReferenceCode()), x + 50, y, BLACK, HELVETICA, 11,REFERENCE_NUMBER);
		y -= 4;

//		drawBox(contents, x + 46, y, 150, .5f, BLACK);
		y -= 16;

		drawText(contents, getMsg().date() + ":", x, y,BLACK, HELVETICA_BOLD, 11 , INVOICE_DATE);
		drawText(contents, formatDate(invoice.getIssueDate(), "dd/MM/yyyy").orElse(""), x + 50, y,BLACK, HELVETICA, 11 , INVOICE_DATE);

		y -= 4;

//		drawBox(contents, x + 46, y, 150, .5f, BLACK);
		y -= 16;

		drawText(contents, "N.I.F.:", x, y, BLACK, HELVETICA_BOLD, 11, NIF);
		drawText(contents, safeString(invoice.getRegistryDocument()), x + 50, y, BLACK, HELVETICA, 11, NIF);
		y -= 4;

//		drawBox(contents, x + 46, y, 150, .5f, BLACK);
		y -= 6;
		x += 250;

		drawBox(contents, x, y, 250, 80, LIGHT_GRAY);
		x += 10;
		y  = height - top - 45 /*- (company != null ? 30 : 0)*/;
		String str = safeString(invoice.getRegistryName())
				.replace("\t", " ")
				/*.getBytes(Charset.forName("ASCII")), Charset.forName("UTF-8")*/;
		
		str = croppedString(str, 230, HELVETICA_BOLD, 12);
		drawText(contents, str, x, y, BLACK, HELVETICA_BOLD, 12, REGISTRY_NAME);
		y -= 15;
		
		if (invoice.getRegistryAddressData() != null) {
			RegistryAddress address = invoice.getRegistryAddressData();
			str = safeString(address.getFullAddress());
			str = croppedString(str, 230, HELVETICA, 9);
			drawText(contents,  str, x, y, BLACK, HELVETICA, 9, ADDRESS);

			y -= 10;

			String zipCityProvince =  safeString(address.getZip()) + " "+  safeString(address.getCity()) + " " +  safeString(address.getProvince()); 
			drawText(contents, zipCityProvince.trim(), x, y, BLACK, HELVETICA, 9, ADDRESS_LINE_TWO);
		} else {
			str = safeString(invoice.getAddress());
			str = croppedString(str, 230, HELVETICA, 9);
			drawText(contents,  str, x, y, BLACK, HELVETICA, 9, ADDRESS);

			y -= 10;

			String zipCityProvince =  safeString(invoice.getAddressZIP()) + " "+  safeString(invoice.getAddressTown()) + " " +  safeString(invoice.getAddressProvince()); 
			drawText(contents, zipCityProvince.trim(), x, y, BLACK, HELVETICA, 9, ADDRESS_LINE_TWO);
		}

	}

	// DRAW DETAILED HEADER
	private void drawDetailedHeader() throws IOException {
		x  = 50;

		drawBox(contents, x, y, 249, 15, BLACK);
		drawTextCenter(contents, new PDRectangle(x, y, 249, 15), getMsg().description(), WHITE, HELVETICA_BOLD, 9, 4.5f);
		x += 250;

		drawBox(contents, x, y, 69, 15, BLACK);
		drawTextCenter(contents, new PDRectangle(x, y, 69, 15), getMsg().quantity(), WHITE, HELVETICA_BOLD, 9, 4.5f);
		x += 70;

		drawBox(contents, x, y, 69, 15, BLACK);
		drawTextCenter(contents, new PDRectangle(x, y, 69, 15), getMsg().price(), WHITE, HELVETICA_BOLD, 9, 4.5f);
		x += 70;

		drawBox(contents, x, y, 39, 15, BLACK);
		drawTextCenter(contents, new PDRectangle(x, y, 39, 15), "%Dto.", WHITE, HELVETICA_BOLD, 9, 4.5f);
		x += 40;

		drawBox(contents, x, y, 69, 15, BLACK);
		drawTextRight(contents, new PDRectangle(x, y, 69, 15), getMsg().amount(), WHITE, HELVETICA_BOLD, 9, 5, 4.5f);
	}

	// DRAW SIMPLE HEADER
	private void drawSimpleHeader() throws IOException {
		x  = 50;
		drawBox(contents, x, y, 429, 15, BLACK);
		drawText(contents, getMsg().description(), x + 5f, y + 4.5f, WHITE, HELVETICA_BOLD, 9);
		x += 430;

		drawBox(contents, x, y, 69, 15, BLACK);
		drawText(contents, getMsg().amount(), x + 5f, y + 4.5f, WHITE, HELVETICA_BOLD, 9);
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
		drawTextRight(contents, new PDRectangle(x, y, 79, 15), getMsg().base(), WHITE, HELVETICA, 9, 5, 4.5f);
		x += 80;

		drawBox(contents, x, y, 79, 15, BLACK);
		drawTextRight(contents, new PDRectangle(x, y, 79, 15), "%", WHITE, HELVETICA, 9, 5, 4.5f);
		x += 80;

		drawBox(contents, x, y, 59, 15, BLACK);
		drawTextCenter(contents, new PDRectangle(x, y, 59, 15), getMsg().type(), WHITE, HELVETICA, 9, 4.5f);
		x += 60;
		
		drawBox(contents, x, y, 49, 15, BLACK);
		drawTextRight(contents, new PDRectangle(x, y, 49, 15), getMsg().quota(), WHITE, HELVETICA, 9, 5, 4.5f);
		x += 50;

		drawBox(contents, x, y, 99, 15, BLACK);
		drawTextCenter(contents, new PDRectangle(x, y, 99, 15), getMsg().totalInvoice(), WHITE, HELVETICA_BOLD, 9, 4.5f);

		if (invoice.getBreakdown() != null){
//			double sum = 0;
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
				
//				sum	+= tax.getQuota() + tax.getSurchargeQuota() + tax.getBase();
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
		drawText(contents, getMsg().date(), x + 5f, y + 4.5f, WHITE, HELVETICA, 9);
		x += 60;

		drawBox(contents, x, y, 79, 15, BLACK);
		drawText(contents, getMsg().payMethod(), x + 5f, y + 4.5f, WHITE, HELVETICA, 9);
		x += 80;

		drawBox(contents, x, y, 159, 15, BLACK);
		drawText(contents, getMsg().bankAccount(), x + 5f, y + 4.5f, WHITE, HELVETICA, 9);
		x += 160;

		drawBox(contents, x, y, 69, 15, BLACK);
		drawTextRight(contents, new PDRectangle(x, y, 69, 15), getMsg().amount(), WHITE, HELVETICA, 9, 5, 4.5f);


		if(invoice.getFinances() != null) {
			int i = 0;
			for (Finance finance : invoice.getFinances()) {
				x = 180;
				drawText(contents, formatDate(finance.getDueDate(), "dd/MM/yyyy").orElse(""), x + 5f, y - 12, BLACK, HELVETICA, 7, i + FINANCE_DATE);
				x += 60;
				
				String paymethod = finance.getPayMethodName() != null ? finance.getPayMethodName() : (finance.getPayMethodType() != null ? finance.getPayMethodType().getDescription(): "");
				drawText(contents, paymethod, x + 5f, y - 12, BLACK, HELVETICA,7, i + FINANCE_PAY_METHOD);
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
	
	private InvoiceTemplateMsg getMsg() {	
		return this.msg;
	}

	private PrintInvoiceConfiguration getConfig() {
		return config;
	}
}
