package com.esferalia.aon.in.payroll.pdf.maker.offer;

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
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceThemeConfiguration;
import com.esferalia.aon.occam.api.model.management.Offer;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class OfferTemplate {
	
	public static final Pattern DISCOUNT_PATTERN = Pattern.compile("[\\d,'.]+", Pattern.CASE_INSENSITIVE);
	
	private static final InvoiceSource[] SORTED_SOURCES = {InvoiceSource.DELIVERY, InvoiceSource.SALES, InvoiceSource.INCOME, InvoiceSource.OFFER};
	
	public static final int MIN_HEADER_FOR_LOGO = 80;
	public static final int MAX_LOGO_HEIGHT = 55;
	public static final int MIN_FOOTER = 40;
	public static final float BOX_BORDER = .5f;
	public static final float BOTTOM_TOLERANCE = /*0.3f*/0f;
	public static final float TITLE_BOX_SIZE = 17f;
	public static final float LEGAL_TEXT_SIZE = 6;
	
	
	
	private static final String STANDARD_DATE_FORMAT = "dd/MM/yyyy";
	
	PDDocument document;
	
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
	int predictedPages;
	int currentInvoiceFirstPage;
	PDFont regularFont;
	PDFont boldFont;
	
	CompanyFull company;
	AonLanguage addressLanguage;
	
	float opacity = 0.5f;
	
	byte[] background;
	boolean adapt;
	PDPageContentStream	contents;
	int pageNumber;
	OfferTemplateMsg msg;
	PrintInvoiceConfiguration config;
	byte[] logo;
	List<String> legalLines;
	
	List<InvoiceDetail> specialTaxes;
	
	Map<DetailCategory, List<InvoiceDetail>> detailMap;
	
	public OfferTemplate(CompanyFull company, Offer offer, PrintInvoiceConfiguration config, String qrUrl, byte[] logo, String tbaiId) throws CanNotCreatePdfException {
		try {
			this.document = new PDDocument();
			this.regularFont = PdfFonts.HELVETICA;
			this.boldFont = PdfFonts.HELVETICA_BOLD;
			this.company = company;
			
			this.legalLines = Collections.emptyList();
			this.pageNumber = 0;
			this.msg = new OfferTemplateMsg(config.getLanguage());
			this.config = config;
			this.logo = logo;
			this.bottomExtra = 0;
			
			
			if (offer == null)
				throw new CanNotCreatePdfException("No invoice found.");
			
			this.specialTaxes = new LinkedList<>();
//			this.manageSupplies(invoice);
			String clientZip = offer.getAddress() != null ? offer.getAddress().getZip() : "";
			
			this.addressLanguage = determineStreetTypeLanguage(clientZip, config.getLanguage());

			this.adapt	= config.getAdjustImage();
			
			if ((this.logo != null || (company != null && config.isCompany())) && (config.getHeader() != null && config.getHeader() < MIN_HEADER_FOR_LOGO))
				this.top = MIN_HEADER_FOR_LOGO;
			else
				this.top	= config.getHeader();
			
			if (config.getFooter() == null || (config.getFooter() != null && config.getFooter() < MIN_FOOTER))
				this.bottom	= MIN_FOOTER;
			else
				this.bottom = config.getFooter();

			if (config.getBackground() != null)
				this.background = config.getBackground().getData();
			
			this.contents = this.drawFirstPage(document, company, offer, config);
			
			this.y -= 10;
			
			if (config.isDetailed())
				this.drawDetailedEntries(document, company, offer);
			else
				this.drawSimplifiedEntries(document, company, offer, config);

//			this.drawBottomInfo(document, invoice, qrUrl, config.getTheme(), tbaiId);
			this.drawJail(this.limit);
			
			this.contents.close();
//			this.drawFooter(document, company, config.getTheme());
		} catch (Exception e)
		{
			throw new CanNotCreatePdfException(e);
		}
	}
	
	public void print(OutputStream os) throws CanNotCreatePdfException {
		if (os != null)
			this.filename = os;
		try {
			document.save(this.filename);
			document.close();
			new OutputStreamWriter(os,StandardCharsets.ISO_8859_1);
		} catch (IOException e) {
			throw new CanNotCreatePdfException(e);
		}
	}
	
	private void drawJail(float end) throws IOException {
		if (config.isBoxBodyBorder()) {
			if (config.isDetailed()) {
				drawBox(contents, 50f, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 300 - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 370 - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 440 - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 480 - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 550 - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 50f, end, 500f - BOX_BORDER, BOX_BORDER, config.getTheme().getBorderColor());
			} else {
				drawBox(contents, 50f, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 479f + BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 550f - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(contents, 50f, end, 500f, BOX_BORDER, config.getTheme().getBorderColor());
				
			}
		}
	}
	
	private void drawComment(PDDocument doc, CompanyFull company, Offer offer, String comment, PrintInvoiceThemeConfiguration theme) throws IOException {
		float firstY = y;
		
		if (comment != null && !comment.isEmpty()) {
			
			drawText(contents, getMsg().notes() + ":", 50f, y, theme.getTitleTextColor(), boldFont, 10);
			List<String> lines = PDFToolkit.getLinesRespectOriginal(comment, 575 - 100 - 50f, regularFont, 10);
			
			this.commentSize = lines.size() * 10f;
			
			for (String line : lines) {
				line = line != null ? line.trim() : line;
				drawText(contents, line, 100f, y, theme.getTextColor(), regularFont, 10);
				y-=10;
				if (y < bottom) {
					contents.close();
					contents = drawPage(doc, company, offer, config, false);
					y = firstY;
				}
			}
			
			y -= 17.5;
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
						if ((regularFont.getStringWidth(sb.toString()) / 1000.0f * 7) <= 180) {
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
						if ((regularFont.getStringWidth(sb.toString()) / 1000.0f * 7) <= 180) {
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
						if ((regularFont.getStringWidth(sb.toString()) / 1000.0f * 7) <= 180) {
							emailStr = sb.toString();
						}
					}
					fullStr += (!fullStr.isEmpty() ? "    " : "") + sb.toString();
				}
				mediaStr = webStr + "    " + phoneStr + "    " + emailStr;
			}	
		
			for (int i=currentInvoiceFirstPage; i<this.pageNumber; i++) {
				contents = new PDPageContentStream(doc, doc.getPage(i), PDPageContentStream.AppendMode.APPEND, true);
				
				if (company.getMedias() != null && !company.getMedias().isEmpty()) {
					if ((regularFont.getStringWidth(fullStr) / 1000.0f * 7) < 555) {
						mediaStr = fullStr;
					}
				
					drawText(contents
						, mediaStr
						, 20f
						, 30f
						, theme.getTitleTextColor()
						, regularFont
						, 7);
				
				}
			
				PDFToolkit.drawBox(contents, 20, 25, 555, 1, PdfColors.GRAY);
			
				String page = "Pag. " + (i+1 - currentInvoiceFirstPage) + " de " + (pageNumber - currentInvoiceFirstPage);
				
				drawTextRight(contents
					, new PDRectangle(560, 15, 15, 15)
					, page
					, theme.getTitleTextColor()
					, regularFont
					, 9
					, 0
					, 0);
			
				drawText(contents
					, registrationString
					, 20f
					, 15f
					, theme.getTitleTextColor()
					, regularFont
					, 7);
				contents.close();
			}
		}
	}

	// DRAW PAGE
	private PDPageContentStream drawPage(PDDocument doc, CompanyFull company, Offer offer, PrintInvoiceConfiguration config, boolean withHeader) throws IOException {
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

		x = 50f;
		y = height - top - 20;

		drawTopInfo(doc, config, offer, company);
		
		y -= 60;
		if (withHeader) {			
			if (config.isDetailed()) {				
				drawDetailedHeader(config.getTheme());
			} else {				
				drawSimpleHeader(config.getTheme());
			}
		}
		
		return contents;
	}
	
	// DRAW FIRST PAGE
	private PDPageContentStream drawFirstPage(PDDocument doc, CompanyFull company, Offer offer, PrintInvoiceConfiguration config) throws IOException {
		PDPage page = createVerticalPage();
		doc.addPage(page);
		this.currentInvoiceFirstPage = this.pageNumber;
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
		
		int bottomStuff = 0;/*offer.getBreakdown() != null ? offer.getBreakdown().size() : 0;
		bottomStuff += offer.getFinances() != null ? offer.getFinances().size() : 0;
		bottomStuff += specialTaxes.size();*/
		
		if (bottomStuff > 8) {
			bottomExtra = (bottomStuff - 7) * 10;
		}
		
		if (config.getLegal() != null && !config.getLegal().isEmpty()) {
			legalLines = PDFToolkit.getLinesRespectOriginal(config.getLegal(), 500, regularFont, LEGAL_TEXT_SIZE);
			limit += legalLines.size() * LEGAL_TEXT_SIZE;
		}
		
		limit += bottomExtra;
		
		
		x = 50f;
		y = height - top - 20;
		
		drawTopInfo(doc, config, offer, company);
		
		y -= 60;
		
		drawComment(doc, company, offer, offer.getComments(), config.getTheme());
		
		entriesStart = y;
		
		if (config.isDetailed()) {
			float realY = y;
			y-=10;
			float realEntriesStart = entriesStart;
			predictedPages = predictNumberOfDetailedPages(offer);
			entriesStart = realEntriesStart;
			y = realY;
			drawDetailedHeader(config.getTheme());
		} else {
			float originalEntriesStart = entriesStart;
			float originalY = y;
			y-=10;
			predictedPages = predictSimplifiedPages(offer);
			entriesStart = originalEntriesStart;
			y = originalY;
			drawSimpleHeader(config.getTheme());
		}
		
		return contents;
	}
	
	@FunctionalInterface
	private interface IdCallback {
		Integer get(InvoiceDetail detail);
	}
	
	@FunctionalInterface
	private interface ReferenceCallback {
		String get(InvoiceDetail detail);
	}
	
	@FunctionalInterface
	private interface IssueDateCallback {
		Date get(InvoiceDetail detail);
	}

	@FunctionalInterface
	private interface DescriptionCallback {
		String get(InvoiceDetail detail);
	}
	
	private static void iterateDetailsBySource(List<InvoiceDetail> details, InvoiceSource source,  Map<DetailCategory, List<InvoiceDetail>> map, IdCallback idCallback, ReferenceCallback referenceCallback, IssueDateCallback issueDateCallback, DescriptionCallback descriptionCallback) {
		if (source == null)
			return;
		details.stream()
		.filter(detail -> detail != null && source.equals(detail.getSource()))
		.forEach(detail -> {
			DetailCategory key = new DetailCategory(source, idCallback.get(detail), referenceCallback.get(detail), issueDateCallback.get(detail), descriptionCallback.get(detail));
			List<InvoiceDetail> detailList = map.getOrDefault(key, new LinkedList<>());
			detailList.add(detail);
			map.put(key, detailList);
		});
	}
	
	private static void sortOtherSources(List<InvoiceDetail> details, Map<DetailCategory, List<InvoiceDetail>> map) {
		List<InvoiceSource> sortedTypes = Arrays.asList(SORTED_SOURCES);
		details.stream()
		.filter(detail -> detail != null && !sortedTypes.contains(detail.getSource()))
		.forEach(detail -> {
			List<InvoiceDetail> detailList = map.getOrDefault(null, new LinkedList<>());
			detailList.add(detail);
			map.put(null, detailList);
		});
	}
	
	private static String safeProjectDescription(Project project) {
		if (project != null && AonStringUtils.isNotBlank(project.getName())) {
			return project.getName();
		}
		return null;
	}
	
	private static void sortDeliveries(List<InvoiceDetail> details, Map<DetailCategory, List<InvoiceDetail>> map) {
		iterateDetailsBySource(details, InvoiceSource.DELIVERY, map,
			detail -> detail.getDeliveryDetail() != null && detail.getDeliveryDetail().getDelivery() != null ? detail.getDeliveryDetail().getDelivery().getId() : null,
			detail -> detail.getDeliveryDetail() != null && detail.getDeliveryDetail().getDelivery() != null ? AonStringUtils.trimToEmpty(detail.getDeliveryDetail().getDelivery().getReferenceCode()) : "",
			detail -> detail.getDeliveryDetail() != null && detail.getDeliveryDetail().getDelivery() != null ? detail.getDeliveryDetail().getDelivery().getDate() : null,
			detail -> detail.getDeliveryDetail() != null && detail.getDeliveryDetail().getDelivery() != null ? safeProjectDescription(detail.getDeliveryDetail().getDelivery().getProject()) : ""
		);
	}
	private static void sortSales(List<InvoiceDetail> details, Map<DetailCategory, List<InvoiceDetail>> map) {
		iterateDetailsBySource(details, InvoiceSource.SALES, map,
			detail -> detail.getSalesDetail() != null && detail.getSalesDetail().getSales() != null ? detail.getSalesDetail().getSales().getId() : null,
			detail -> detail.getSalesDetail() != null && detail.getSalesDetail().getSales() != null ? AonStringUtils.trimToEmpty(detail.getSalesDetail().getSales().getReferenceCode()) : "",
			detail -> detail.getSalesDetail() != null && detail.getSalesDetail().getSales() != null ? detail.getSalesDetail().getSales().getDate() : null,
			detail -> detail.getSalesDetail() != null && detail.getSalesDetail().getSales() != null ? safeProjectDescription(detail.getSalesDetail().getSales().getProject()) : ""
		);
	}
	private static void sortIncome(List<InvoiceDetail> details, Map<DetailCategory, List<InvoiceDetail>> map) {
		iterateDetailsBySource(details, InvoiceSource.INCOME, map,
			detail -> detail.getIncomeDetail() != null && detail.getIncomeDetail().getIncome() != null ? detail.getIncomeDetail().getIncome().getId() : null,
			detail -> detail.getIncomeDetail() != null && detail.getIncomeDetail().getIncome() != null ? AonStringUtils.trimToEmpty(detail.getIncomeDetail().getIncome().getReferenceCode()) : "",
			detail -> detail.getIncomeDetail() != null && detail.getIncomeDetail().getIncome() != null ? detail.getIncomeDetail().getIncome().getIssueDate() : null,
			detail -> detail.getIncomeDetail() != null && detail.getIncomeDetail().getIncome() != null ? safeProjectDescription(detail.getIncomeDetail().getIncome().getProject()) : ""
		);
	}
	private static void sortOffer(List<InvoiceDetail> details, Map<DetailCategory, List<InvoiceDetail>> map) {
		iterateDetailsBySource(details, InvoiceSource.OFFER, map,
			detail -> detail.getOfferDetail() != null && detail.getOfferDetail().getOffer() != null ? detail.getOfferDetail().getOffer().getId() : null,
			detail -> detail.getOfferDetail() != null && detail.getOfferDetail().getOffer() != null ? AonStringUtils.trimToEmpty(detail.getOfferDetail().getOffer().getReferenceCode()) : "",
			detail -> detail.getOfferDetail() != null && detail.getOfferDetail().getOffer() != null ? detail.getOfferDetail().getOffer().getIssueDate() : null,	
			detail -> detail.getOfferDetail() != null && detail.getOfferDetail().getOffer() != null ? safeProjectDescription(detail.getOfferDetail().getOffer().getProject()) : ""
		);
	}
	
	
	private static Map<DetailCategory, List<InvoiceDetail>> groupBySource(List<InvoiceDetail> details) {
		Map<DetailCategory, List<InvoiceDetail>> map = new LinkedHashMap<>();
		if (details == null)
			return map;
		
		//OTROS
		sortOtherSources(details, map);
		//DELIVERIES
		sortDeliveries(details, map);
		//SALES
		sortSales(details, map);
		//INCOME
		sortIncome(details, map);
		//OFFER
		sortOffer(details, map);
		
		return map;
	}
	
	private static Map<DetailCategory, List<InvoiceDetail>> groupByProductType(Invoice invoice) {
		Map<DetailCategory, List<InvoiceDetail>> map = new LinkedHashMap<>();
		if (invoice == null)
			return map;
		
		if (invoice.getDetails() != null) {
			for (InvoiceDetail detail : invoice.getDetails()) {
				if (detail != null) {
					if (detail.getItem() != null && detail.getItem().getProduct() != null) {
						ProductType productType = detail.getItem().getProduct().getType();
						DetailCategory category = new DetailCategory(productType);
						List<InvoiceDetail> detailList = map.getOrDefault(category, new LinkedList<>());
						detailList.add(detail);
						map.put(category, detailList);
					} else {
						List<InvoiceDetail> detailList = map.getOrDefault(null, new LinkedList<>());
						detailList.add(detail);
						map.put(null, detailList);
					}
				}
			}
		}
		
		return map;
	}
	
	private static Map<DetailCategory, List<InvoiceDetail>> groupByProductType(List<InvoiceDetail> details, CompanyFull company) {
		Map<DetailCategory, List<InvoiceDetail>> map = new LinkedHashMap<>();
		if (details == null)
			return map;
		
		for (InvoiceDetail detail : details) {
			if (detail != null) {
				if (detail.getItem() != null && detail.getItem().getProduct() != null) {
					ProductType productType = detail.getItem().getProduct().getType();
					DetailCategory category = new DetailCategory(productType);
					
					if (ProductType.AUXILIARY.equals(productType) && isUdapa(company)) {
						category.setName("");
					}
					
					List<InvoiceDetail> detailList = map.getOrDefault(category, new LinkedList<>());
					detailList.add(detail);
					map.put(category, detailList);
				} else {
					List<InvoiceDetail> detailList = map.getOrDefault(null, new LinkedList<>());
					detailList.add(detail);
					map.put(null, detailList);
				}
			}
		}
		
		return map;
	}
	
	private static Map<DetailCategory, List<InvoiceDetail>> groupBySalesReference(List<InvoiceDetail> details, CompanyFull company) {
		Map<DetailCategory, List<InvoiceDetail>> map = new LinkedHashMap<>();
		if (details == null)
			return map;
		
		for (InvoiceDetail detail : details) {
			if (detail != null &&
				detail.getDeliveryDetail() != null &&
				detail.getDeliveryDetail().getSalesDetailData() != null &&
				detail.getDeliveryDetail().getSalesDetailData().getSales() != null &&
				detail.getDeliveryDetail().getSalesDetailData().getSales().getId() != null
			) {
				Sales sales = detail.getDeliveryDetail().getSalesDetailData().getSales();
				String detailStr = "Pedido de venta: " + AonStringUtils.trimToEmpty(sales.getReferenceCode()) + " con referencia de compra " + AonStringUtils.trimToEmpty(sales.getPurchaseReference());
				DetailCategory detailCategory = new DetailCategory(null, sales.getId(), null, null, null);
				detailCategory.setName(detailStr);
				List<InvoiceDetail> salesList = map.getOrDefault(detailCategory, new LinkedList<>());
				salesList.add(detail);
				map.put(detailCategory, salesList);
			} else {
				List<InvoiceDetail> othersList = map.getOrDefault(null, new LinkedList<>());
				othersList.add(detail);
				map.put(null, othersList);
				
			}
		}
		
		return map;
	}

	// DRAW DETAILED ENTRIES
	public void drawDetailedEntries(PDDocument doc, CompanyFull company, Offer offer) throws IOException {
		PrintInvoiceThemeConfiguration theme = config.getTheme();
		
		AtomicInteger atomicI = new AtomicInteger();
		try {
			atomicI.set(drawDetailedCategory(doc, company, offer, theme, atomicI.get()));
		} catch (IOException e) {
		}
	}
	
	private void drawCategoryName(DetailCategory category, PDDocument doc, Offer offer, PrintInvoiceThemeConfiguration theme, boolean indent, boolean detailed) throws IOException {
		if (category != null && !AonStringUtils.isBlank(category.getName())) {
			float lineLength = (detailed ? 240 : 420) - (indent ? 10 : 0);
			List<String> lines = getLines(category.toString(), lineLength, boldFont, 9);
			
			if (y - 5 - 10 * lines.size() < bottom + 5) {
				jumpToNewPage(doc, company, offer, config);
				y = entriesStart - 10;
			}
			x = 50;
			y-= 5;
			
			for (String line : lines) {				
				drawText(contents, AonStringUtils.trim(line), x + 5 + (indent ? 10 : 0), y, theme.getTextColor(), boldFont, 9);
				y-= 10;
			}
			if (AonStringUtils.isNotBlank(category.getDescription())) {				
				drawText(contents, category.getDescription(), x + 5 + (indent ? 10 : 0), y, theme.getTextColor(), boldFont, 9);
				y-= 10;
			}
		}
	}
	
//	private void simulateCategoryName(DetailCategory category, Invoice invoice, boolean indent, boolean detailed, AtomicInteger numberOfPages) throws IOException {
//		if (category != null && !AonStringUtils.isBlank(category.getName())) {
//			float lineLength = (detailed ? 240 : 420) - (indent ? 10 : 0);
//			List<String> lines = getLines(category.toString(), lineLength, boldFont, 9);
//			
//			if (y - 5 - 10 * lines.size() < bottom + 5) {
//				fictionalPageJump(invoice);
//				numberOfPages.getAndIncrement();
//				y = entriesStart - 10;
//			}
//			y-= 5;
//			for (String line : lines) {				
//				y-= 10;
//			}
//			if (AonStringUtils.isNotBlank(category.getDescription())) {				
//				y-= 10;
//			}
//		}
//	}

	private int drawDetailedCategory(PDDocument doc, CompanyFull company, Offer offer, PrintInvoiceThemeConfiguration theme, int i) throws IOException {
		if (offer != null && offer.getDetails() != null) {
			
			for (OfferDetail detail : offer.getDetails()) {
				x = 50;
				String description = 
						safeString(detail.getDescription())
							.replace("\t", " ");
				
				ArrayList<String> divided = (ArrayList<String>) PDFToolkit.getLinesRespectOriginal(description, 240, regularFont, 8);				
				float lineDiff = 10;
				
				drawDetail(i, detail, divided, lineDiff, doc, offer, company);
					
				i++;
			}
		}
		return i;
	}
	
	private int simulateDetailedCategory(
			CompanyFull company, Offer invoice, PrintInvoiceThemeConfiguration theme, int i, AtomicInteger numberOfPages) throws IOException {
		if (invoice != null && invoice.getDetails() != null) {
			
			for (OfferDetail detail : invoice.getDetails()) {
				x = 50;
				String description = 
						safeString(detail.getDescription())
						.replace("\t", " ");
				
				ArrayList<String> divided = (ArrayList<String>) PDFToolkit.getLinesRespectOriginal(description, 240,regularFont, 8);				
				float lineDiff = 10;
				
				simulateDetail(i, divided, lineDiff, invoice, numberOfPages);
				
				i++;
			}
		}
		return i;
	}
	
	private static boolean isGarage(CompanyFull company) {
		if (company != null && company.getRegistry() != null && company.getRegistry().getDomain() != null)
			return DomainType.GARAGE.equals(company.getRegistry().getDomain().getDomainType());
		return false;
	}
	
	private void jumpToNewPage(PDDocument doc, CompanyFull company, Offer offer, PrintInvoiceConfiguration config) throws IOException {
		drawJail(bottom);
		contents.close();
		contents = drawPage(doc, company, offer, config, true);
		y		 = height - top - topInfoHeight - 5;
		x		 = 50;
	}
	

	
	
	private int predictNumberOfDetailedPages(Offer offer) throws IOException {
		AtomicInteger numberOfPages = new AtomicInteger(1);
		PrintInvoiceThemeConfiguration theme = config.getTheme();
		
		AtomicInteger atomicI = new AtomicInteger();

				try {
					atomicI.set(simulateDetailedCategory(company, offer, theme, atomicI.get(), numberOfPages));
				} catch (IOException e) {
				}
		return numberOfPages.get();
	}
	
	private void fictionalPageJump(Offer offer) throws IOException {
		
		RegistryAddress transmitterAddr = null;
		
		if (company != null && config.isCompany()) {
			Company registry = company.getRegistry();
			String companyName = registry != null ? AonStringUtils.trimToEmpty(registry.getName()) : "";
			companyName = croppedString(companyName, 240, regularFont, 9);
			String address;
			String zip = "";
			
			if (company.getAddresses() != null && !company.getAddresses().isEmpty()) {
				transmitterAddr = company.getAddresses().stream().filter(RegistryAddress::isMain).findFirst().orElse(company.getAddresses().getFirst());
				address = transmitterAddr.getFullAddress(addressLanguage);
				String cp = AonStringUtils.trimToEmpty(transmitterAddr.getZip());
				String city = AonStringUtils.trimToEmpty(transmitterAddr.getCity());
				String province = AonStringUtils.trimToEmpty(transmitterAddr.getProvince());
				String country = AonStringUtils.trimToEmpty(transmitterAddr.getCountry() != null ? transmitterAddr.getCountry().getName() : "");
				
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
			
			List<String> addressLines = getLines(address, 235, regularFont, 8);
			
			if (!addressLines.isEmpty() && addressLines.size() > 1) {
				StringBuilder addressBuilder = new StringBuilder("");
				for(int i=1; i<addressLines.size(); i++) {
					addressBuilder.append(addressLines.get(i));
				}
			}
			
			List<String> zipLines = getLines(zip, 235, regularFont, 8);
			if (!zipLines.isEmpty() && zipLines.size() > 1) {
				StringBuilder zipBuilder = new StringBuilder("");
				for(int i=1; i<zipLines.size(); i++) {
					zipBuilder.append(zipLines.get(i));
				}
			}
		}
		
		y -= 30;
		y -= 4;
		y -= 16;	
		y -= 20;	
		y -= 10;
		y  = height - top - 35;

		String countryCode = "";
//		if (invoice.isExtracommunity() && countryCode != null) {
//			countryCode = AonStringUtils.trimToEmpty(invoice.getRegistryDocumentCountry().getIso2());
//		}
		
		Target target = offer.getTarget();
		
		if (target != null && target.getDocumentCountry() != null) {			
			countryCode = AonStringUtils.trimToEmpty(offer.getTarget().getDocumentCountry().getIso2());
		}
		
		String str = safeString(target != null ? target.getName() : "")
				.replace("\t", " ");
		
		List<String> nameLines = PDFToolkit.getLines(str, 230, boldFont, 10);
		
		if (nameLines != null) {
			String line1 = nameLines.get(0).trim();
			
			if (line1 != null) {
				y -= 10;
			}
		}
		y -= 15;
		
		String fullAddress = "";
		String province = "";
		
		if (offer.getAddress() != null && !offer.getAddress().isEmpty()) {
			RegistryAddress address = offer.getAddress();
			fullAddress = safeString(address.getFullAddress(addressLanguage));
			boolean isProvince = address.getProvince() != null && !address.getProvince().isEmpty() && !AonStringUtils.equalsIgnoreCase(address.getProvince(), address.getCity());
			if (isProvince) {
				province = "(" + address.getProvince().trim() + ") ";
			}
			
			if (transmitterAddr != null && transmitterAddr.getCountry() != null && address.getCountry() != null) {
				Country transmitterCountry = transmitterAddr.getCountry();
				if (!transmitterCountry.equals(address.getCountry())) {
					float textWidth = PDFToolkit.fontWidth(province + transmitterAddr.getCountry().getName(), 9, regularFont);
					province += textWidth <= 230 ? address.getCountry().getName() : address.getCountry().getIso3();
				}
			}		
		}
		
		List<String> addressLines = PDFToolkit.getLines(fullAddress, 230, regularFont, 9);
		
		if (addressLines != null && !addressLines.isEmpty()) {
			y -= 10;
			if (addressLines.size() > 1) {
				y -= 12.5;
			}
			
		}
		
		y -= 10;
		
		y -= 60;
		
//		y		 = height - top - topInfoHeight - 5;
		entriesStart = y;
		
	}
	
	private void drawDetail(int i, OfferDetail detail, ArrayList<String> divided, float lineDiff,
			PDDocument doc, Offer offer, CompanyFull company) throws IOException {
		PrintInvoiceThemeConfiguration theme = config.getTheme();
		float dy = y;
		int line = 0;
		float originX = x;
		for (String str : divided) {
			drawText(contents, str.trim(), x + 5, dy, theme.getTextColor(), regularFont, 8, i + DETAIL_DESCRIPTION);
			
			if (line++ == 0) {
				x += 250;
				
				String amount = detail.getQuantity() != 0 ? toLatinNumber(detail.getQuantity()) : "";
				String price = detail.getPrice() != 0 ? toLatinNumber(detail.getPrice()) : "";
				String discount = "";
				
				try {
					String expression = detail.getDiscountExpression() != null ? detail.getDiscountExpression() : "";
					
					Matcher matcher = DISCOUNT_PATTERN.matcher(expression);
					if (matcher.find()) {
						String numStr = matcher.group();
						numStr = numStr.replaceAll("[,']", ".");
						double percent = Double.parseDouble(numStr);
						if (percent != 0) {
							discount = safeString(detail.getDiscountExpression());
						}
					} else {
						discount = safeString(detail.getDiscountExpression());
					}
					
				} catch (NumberFormatException e) {
					discount = safeString(detail.getDiscountExpression());
				}
				
				
				
				drawTextRight(contents, new PDRectangle(x, y, 69, 15), amount, theme.getTextColor(), regularFont, 8, 4.5f, 0, i + DETAIL_AMOUNT);
				x += 70;
				
				drawTextRight(contents, new PDRectangle(x, y, 69, 15),price, theme.getTextColor(), regularFont, 8, 4.5f, 0, i + DETAIL_PRICE);
				x += 70;
				
				drawTextRight(contents, new PDRectangle(x, y, 39, 15), discount, theme.getTextColor(), regularFont, 8, 4.5f, 0, i + DETAIL_DISCOUNT);
				x += 40;
					
				String total = (AonNumberUtils.isValid(detail.getQuantity()) && AonNumberUtils.isValid(detail.getPrice())) ? toLatinNumber(detail.getPrice()*detail.getQuantity()) : "";
				/*String total = (detail.getQuantity() != 0 && detail.getPrice() != 0 && detail.getTaxableBase() != 0) ? toLatinNumber(detail.getTaxableBase()) : ""*/;
				
				drawTextRight(contents, new PDRectangle(x, y, 69, 15), total, theme.getTextColor(), regularFont, 8, 4.5f, 0, i + DETAIL_TOTAL);
				x = originX;
			}
			
			dy -= lineDiff;
			if (dy < bottom + 5) {
				jumpToNewPage(doc, company, offer, config);
				dy = entriesStart - 10;
			}
			
		}
		
		if (dy < limit + 5 && i == offer.getDetails().size() - 1) {
			jumpToNewPage(doc, company, offer, config);
			dy = entriesStart - 10;
		}
		
		y = dy - 3;
	}
	
	private void simulateDetail(int i, ArrayList<String> divided, float lineDiff, Offer offer, AtomicInteger numberOfPages) throws IOException {
		float dy = y;
		for (String str : divided) {
			dy -= lineDiff;
			if (dy < bottom + 5) {
				fictionalPageJump(offer);
				numberOfPages.getAndIncrement();
				dy = entriesStart - 10;
			}
		}
		
		if (dy < limit + 5 && i == offer.getDetails().size() - 1) {
			fictionalPageJump(offer);
			numberOfPages.getAndIncrement();
			dy = entriesStart - 10;
		}
		y = dy - 3;
	}
	
	
	public int predictSimplifiedPages (Offer offer) throws IOException {
		AtomicInteger numberOfPages = new AtomicInteger(1);
		
		simulateSimplifiedDetails(offer, false, numberOfPages);
		
		if (y < limit + 5) {
			fictionalPageJump(offer);
			numberOfPages.getAndIncrement();
		}
		
		return numberOfPages.get();
	}

	// DRAW SIMPLIFIED ENTRIES
	public void drawSimplifiedEntries(PDDocument doc, CompanyFull company, Offer offer, PrintInvoiceConfiguration config) throws IOException {
		PrintInvoiceThemeConfiguration theme = config.getTheme();
		
		try {
			drawSimplifiedDetails(offer, doc, company, config, theme);
		} catch (IOException e) {
		}
		
		if (y < limit + 5) {
			jumpToNewPage(doc, company, offer, config);
		}
	}

	private void drawSimplifiedDetails(Offer offer,
			PDDocument doc, CompanyFull company, PrintInvoiceConfiguration config, PrintInvoiceThemeConfiguration theme)
			throws IOException {
		for (OfferDetail detail : offer.getDetails()) {
			x = 50;
			
			if (y < bottom + 5) {
				jumpToNewPage(doc, company, offer, config);
				y = entriesStart - 10;
			}
			String description = AonStringUtils.trimToEmpty(detail.getDescription()).replace("\t", " ");
			
			x += 430;
			
			String total = (AonNumberUtils.isValid(detail.getQuantity()) && AonNumberUtils.isValid(detail.getPrice())) ? toLatinNumber(detail.getPrice()*detail.getQuantity()) : "";
			
			PDFToolkit.drawTextRight(contents, new PDRectangle(x, y, 69, 15), total, config.getTheme().getTextColor(), regularFont, 8, 4.5f, 0);
			
			x -= 430;
			
			List<String> lines = PDFToolkit.getLinesRespectOriginal(description, 420, regularFont, 8);
			if (lines.isEmpty()) {
				y -= 10;
			}
			for (String line : lines) {
				drawText(contents, AonStringUtils.trimToEmpty(line), x + 5, y, theme.getTextColor(), regularFont, 8, DETAIL_DESCRIPTION);
				y -= 10;
				if (y < bottom + 5) {
					jumpToNewPage(doc, company, offer, config);
					y = entriesStart - 10;
				}
			}
		}
	}

	private void simulateSimplifiedDetails(Offer offer,
			boolean indent, AtomicInteger numberOfPages)
			throws IOException {
		for (OfferDetail detail : offer.getDetails()) {
			if (y < bottom + 5) {
				fictionalPageJump(offer);
				numberOfPages.getAndIncrement();
				y = entriesStart - 10;
			}
			String description = AonStringUtils.trimToEmpty(detail.getDescription()).replace("\t", " ");
			
			List<String> lines = PDFToolkit.getLinesRespectOriginal(description, 420 - (indent ? 10 : 0), regularFont, 8);
			if (lines.isEmpty()) {
				y -= 10;
			}
			for (String line : lines) {
				y -= 10;
				if (y < bottom + 5) {
					fictionalPageJump(offer);
					numberOfPages.getAndIncrement();
					y = entriesStart - 10;
				}
			}

		}
	}
	
	
	// DRAW UPPER INFO
	private void drawTopInfo(PDDocument doc, PrintInvoiceConfiguration config, Offer offer, CompanyFull company) throws IOException {
		
		float maxHeight = MAX_LOGO_HEIGHT;
		float maxWidth = 297 - x - 20;
		
		
		float tempY = height - MIN_HEADER_FOR_LOGO + 20;
		float logoX = x;
		float logoY = tempY - 10;
		String web = null;
		RegistryAddress transmitterAddr = null;
		
		if (company != null && config.isCompany()) {
			Company registry = company.getRegistry();
			String companyName = registry != null ? AonStringUtils.trimToEmpty(registry.getName()) : "";
			companyName = croppedString(companyName, 240, regularFont, 9);
			String nif = registry != null ? AonStringUtils.trimToEmpty(registry.getDocument()) : "";
			String address;
			String zip = "";
			
			if (company.getAddresses() != null && !company.getAddresses().isEmpty()) {
				transmitterAddr = company.getAddresses().stream().filter(RegistryAddress::isMain).findFirst().orElse(company.getAddresses().getFirst());
				address = transmitterAddr.getFullAddress(addressLanguage);
				String cp = AonStringUtils.trimToEmpty(transmitterAddr.getZip());
				String city = AonStringUtils.trimToEmpty(transmitterAddr.getCity());
				String province = AonStringUtils.trimToEmpty(transmitterAddr.getProvince());
				String country = AonStringUtils.trimToEmpty(transmitterAddr.getCountry() != null ? transmitterAddr.getCountry().getName() : "");
				
				String location = "";
				
				if (!city.isEmpty()) {
					location += city;
				}
				if (!province.isEmpty() && !AonStringUtils.equalsIgnoreCase(city, province)) {
					location += " (" + province + ")";
				}
				if (!country.isEmpty()) {
					RegistryAddress clientAddress = offer.getAddress();
					Country clientCountry = clientAddress != null ? clientAddress.getCountry() : null;
					Country transmitterCountry = transmitterAddr.getCountry() != null ? transmitterAddr.getCountry() : null;
					if (!(clientCountry != null && transmitterCountry != null && transmitterCountry.equals(clientCountry))) {						
						location += " - " + country;
					}
					
				}
				
				if (cp.isEmpty())
					zip = location;
				else {
					zip = cp + " " + location;
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
			
			String enterpriseCountry = "";
			if (company != null && company.getRegistry() != null && company.getRegistry().getDocumentCountry() != null) {
				enterpriseCountry = AonStringUtils.trimToEmpty(company.getRegistry().getDocumentCountry().getIso2());
			
			}
			
			drawText(contents, (!AonStringUtils.isEmpty(enterpriseCountry) ? enterpriseCountry + " " : "") + nif, x + 280, tempY + 22, config.getTheme().getTitleTextColor(), regularFont, 8);

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
			try {
				PDFToolkit.drawResizedLogo(doc, doc.getPage(pageNumber - 1), contents, logo, logoX, logoY, maxHeight, maxWidth, web);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String invoiceTitle = "";
			invoiceTitle = getMsg().invoice().toUpperCase();
		
		drawText(contents, invoiceTitle, x, y, config.getTheme().getTitleTextColor(), boldFont, 16);
		y -= 30;

		drawText(contents, getMsg().number() + ":", x, y, config.getTheme().getTitleTextColor(), boldFont, 11,REFERENCE_NUMBER);
		String reference = offer.getReferenceCode();
		drawText(contents, safeString(reference), x + 50, y, config.getTheme().getTextColor(), regularFont, 11,REFERENCE_NUMBER);
		y -= 4;

		y -= 16;

		drawText(contents, getMsg().date() + ":", x, y, config.getTheme().getTitleTextColor(), boldFont, 11 , INVOICE_DATE);
		drawText(contents, formatDate(offer.getIssueDate(), STANDARD_DATE_FORMAT).orElse(""), x + 50, y, config.getTheme().getTextColor(), regularFont, 11 , INVOICE_DATE);
		
		y -= 20;

		drawText(contents, "N.I.F.:", x, y, config.getTheme().getTitleTextColor(), boldFont, 11, NIF);
		
		String countryCode = "";
//		if (invoice.isExtracommunity() && countryCode != null) {
//			countryCode = AonStringUtils.trimToEmpty(invoice.getRegistryDocumentCountry().getIso2());
//		}
		
		Target target = offer.getTarget();
		
		if (target != null && target.getDocumentCountry() != null) {			
			countryCode = AonStringUtils.trimToEmpty(offer.getTarget().getDocumentCountry().getIso2());
		}
		
		drawText(contents, safeString((!AonStringUtils.isEmpty(countryCode) ? countryCode + " " : "") + target != null ? target.getDocument() : ""), x + 50, y, config.getTheme().getTextColor(), regularFont, 11, NIF);
		
		y -= 10;
		x += 250;

		drawBox(contents, x, y, 250, 80, config.getTheme().getCustomerBackgroundColor(), opacity);
		x += 10;
		y  = height - top - 35;
		String str = safeString(target != null ? target.getName() : "")
				.replace("\t", " ");
		
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
		
		String fullAddress = "";
		String zipCity = "";
		String province = "";
		
		if (offer.getAddress() != null && !offer.getAddress().isEmpty()) {
			RegistryAddress address = offer.getAddress();
			fullAddress = safeString(address.getFullAddress(addressLanguage));
			boolean isProvince = address.getProvince() != null && !address.getProvince().isEmpty() && !AonStringUtils.equalsIgnoreCase(address.getProvince(), address.getCity());
			if (isProvince) {
				province = "(" + address.getProvince().trim() + ") ";
			}
			zipCity =  safeString(address.getZip()) + " "+  safeString(address.getCity());
			
			if (transmitterAddr != null && transmitterAddr.getCountry() != null && address.getCountry() != null) {
				Country transmitterCountry = transmitterAddr.getCountry();
				if (!transmitterCountry.equals(address.getCountry())) {
					float textWidth = PDFToolkit.fontWidth(province + address.getCountry().getName(), 9, regularFont);
					province += textWidth <= 230 ? address.getCountry().getName() : address.getCountry().getIso3();
				}
			}		
		}
		
		List<String> addressLines = PDFToolkit.getLines(fullAddress, 230, regularFont, 9);
		
		if (addressLines != null && !addressLines.isEmpty()) {
			drawText(contents,  addressLines.get(0).trim(), x, y, config.getTheme().getTextColor(), regularFont, 9, ADDRESS);
			y -= 10;
			if (addressLines.size() > 1) {
				String line2 = croppedString(addressLines.get(1), 230, regularFont, 9);
				drawText(contents,  line2, x, y, config.getTheme().getTextColor(), regularFont, 9, ADDRESS);
				y -= 12.5;
			}
			
		}
		
		drawText(contents, zipCity.trim(), x, y, config.getTheme().getTextColor(), regularFont, 9, ADDRESS_LINE_TWO);
		y -= 10;
		drawText(contents, province, x, y, config.getTheme().getTextColor(), regularFont, 9, ADDRESS_LINE_TWO);
		
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
			drawBox(contents, 50, y, 500, BOX_BORDER, theme.getBorderColor());
		}
		
		if (theme.getBoxBodyBackgroundColor() != null) {
			
			float startPoint = pageNumber - currentInvoiceFirstPage < predictedPages ? bottom - BOTTOM_TOLERANCE : limit - BOTTOM_TOLERANCE;
			float boxHeight = pageNumber - currentInvoiceFirstPage < predictedPages ? y - bottom + BOTTOM_TOLERANCE : y - limit + BOTTOM_TOLERANCE;
			
			drawBox(contents, 50, startPoint, 250 - BOX_BORDER, boxHeight, theme.getBoxBodyBackgroundColor(), opacity);
			drawBox(contents, 300, startPoint, 70 - BOX_BORDER, boxHeight, theme.getBoxBodyBackgroundColor(), opacity);
			drawBox(contents, 370, startPoint, 70 - BOX_BORDER, boxHeight, theme.getBoxBodyBackgroundColor(), opacity);
			drawBox(contents, 440, startPoint, 40 - BOX_BORDER, boxHeight, theme.getBoxBodyBackgroundColor(), opacity);
			drawBox(contents, 480, startPoint, 70, boxHeight, theme.getBoxBodyBackgroundColor(), opacity);
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
			drawBox(contents, 50, y, 500 - BOX_BORDER, BOX_BORDER, theme.getBorderColor());
		}
		
		if (theme.getBoxBodyBackgroundColor() != null) {
			
			float startPoint = pageNumber - currentInvoiceFirstPage < predictedPages ? bottom - BOTTOM_TOLERANCE : limit - BOTTOM_TOLERANCE;
			float boxHeight = pageNumber - currentInvoiceFirstPage < predictedPages ? y - bottom + BOTTOM_TOLERANCE : y - limit + BOTTOM_TOLERANCE;
			
			drawBox(contents, 50, startPoint, 429 + BOX_BORDER, boxHeight, theme.getBoxBodyBackgroundColor(), opacity);
			drawBox(contents, 480, startPoint, 70, boxHeight, theme.getBoxBodyBackgroundColor(), opacity);
		}
		
		
		entriesStart = y;
	}

	// DRAW BOTTOM INFO
	private void drawBottomInfo(PDDocument doc, Invoice invoice, String qrUrl, PrintInvoiceThemeConfiguration theme, String tbaiId) throws IOException, WriterException {
		x = 50;
		float legalSize = legalLines.size() * LEGAL_TEXT_SIZE;
		y = bottom + 10 + bottomExtra + legalSize;
		if(qrUrl != null && !invoice.isProforma()) {
			byte[] qrCode = createQR(qrUrl, 300, 300);
			drawImage(doc, contents, qrCode, x, y, 120, 120);
			if(tbaiId != null)
				drawText(contents, tbaiId, x + 5f, y + 2f + 110, Color.BLACK, regularFont, 5);
		}
		drawTaxes(invoice, theme);
		drawFinances(invoice, theme);
		y = bottom + 10 + legalSize;
		drawLegal(theme);
	}

	// DRAW TAXES
	private void drawTaxes(Invoice invoice, PrintInvoiceThemeConfiguration theme) throws IOException {
		x = 180;
		float legalSize = legalLines.size() * LEGAL_TEXT_SIZE;
		y = bottom + 113 + bottomExtra + legalSize;
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
			drawBox(contents, 180, y, 370, BOX_BORDER, theme.getBorderColor());
		}
		
		if (invoice.getBreakdown() != null){
			int i = 0;
			float initY = y;
			
			float bdSize = (invoice.getBreakdown().size() + this.specialTaxes.size()) * 10 + 10f;
			
			if (theme.getBoxBodyBackgroundColor() != null) {				
				drawBox(contents, 180, y, 80 - BOX_BORDER, -bdSize, theme.getBoxBodyBackgroundColor(), opacity);
				drawBox(contents, 260, y, 80 - BOX_BORDER, -bdSize, theme.getBoxBodyBackgroundColor(), opacity);
				drawBox(contents, 340, y, 60 - BOX_BORDER, -bdSize, theme.getBoxBodyBackgroundColor(), opacity);
				drawBox(contents, 400, y, 50 - BOX_BORDER, -bdSize, theme.getBoxBodyBackgroundColor(), opacity);
				drawBox(contents, 450, y, 100f, -bdSize, theme.getBoxBodyBackgroundColor(), opacity);
			}
			
			float initiaruY = y - 12;
			
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
			
			for (InvoiceDetail detail : specialTaxes) {
				
				String total = (detail.getQuantity() != 0 && detail.getPrice() != 0 && detail.getTaxableBase() != 0) ? toLatinNumber(detail.getTaxableBase()) : "";
				
				x = 180;
				x += 80;

				x += 80;
				
				String name = "OTRO";
				
				if (detail != null && detail.getItem() != null && detail.getItem().getProduct() != null && detail.getItem().getProduct().getType() != null) {
					
					name = AonStringUtils.trimToEmpty(detail.getItem().getProduct().getType().getName());
				}
				
				drawTextCenter(contents, new PDRectangle(x, y, 59, 15), name, theme.getTextColor(), regularFont, 7, -12, i + TAX_TYPE);
				x += 60;
				
				drawTextRight(contents, new PDRectangle(x, y, 49, 15), total, theme.getTextColor(), regularFont, 7, 5, -12, i + TAX_QUOTE);
				x += 50;
				
				y	-= 10;
				
				i++;
			}
			if ((invoice.getBreakdown() != null && (!invoice.getBreakdown().isEmpty() || !specialTaxes.isEmpty()))) {
				int totalThings = invoice.getBreakdown().size() + specialTaxes.size();
				float middle = /*(totalThings % 2 != 0 ? totalThings / 2f -0.5f: totalThings / 2f - 0.5f)*/(totalThings / 2f -0.5f) * 10;
				middle = initiaruY - middle;
				drawTextRight(contents, new PDRectangle(x, middle, 99, 8), toLatinNumber(invoice.getTotal()) + " \u20AC", theme.getTextColor(), boldFont, 8, 5, -.5f, INVOICE_TOTAL);
			} else {
				drawTextRight(contents, new PDRectangle(x, initiaruY, 99, 8), toLatinNumber(invoice.getTotal()) + " \u20AC", theme.getTextColor(), boldFont, 8, 5, -.5f, INVOICE_TOTAL);
				y	-= 10;
			}
			
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
			drawBox(contents, x, y, 45 - BOX_BORDER, TITLE_BOX_SIZE,  theme.getBoxTitleBackgroundColor());
		drawText(contents, getMsg().date(), x + 5f, y + 5.5f, theme.getBoxTitleTextColor(), regularFont, 9);
		x += 45;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 110 - BOX_BORDER, TITLE_BOX_SIZE,  theme.getBoxTitleBackgroundColor());
		drawText(contents, getMsg().payMethod(), x + 5f, y + 5.5f, theme.getBoxTitleTextColor(), regularFont, 9);
		x += 110;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 160 - BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawText(contents, getMsg().bankAccount(), x + 5f, y + 5.5f, theme.getBoxTitleTextColor(), regularFont, 9);
		x += 160;

		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(contents, x, y, 55, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextRight(contents, new PDRectangle(x, y, 54, TITLE_BOX_SIZE), getMsg().amount(), theme.getBoxTitleTextColor(), regularFont, 9, 5, 5.5f);
		
		if (config.isBoxTitleBorder()) {
			drawBox(contents, 180, y + TITLE_BOX_SIZE, 370, BOX_BORDER, theme.getBorderColor());
			drawBox(contents, 180, y, 370, BOX_BORDER, theme.getBorderColor());
		}

		if(invoice.getFinances() != null) {
			int i = 0;
			float initY = y;
			
			float fSize = invoice.getFinances().size() * 10 + 10f;
			
			if (theme.getBoxBodyBackgroundColor() != null) {
				drawBox(contents, 180, y, 45 - BOX_BORDER, -fSize, theme.getBoxBodyBackgroundColor(), opacity);
				drawBox(contents, 225, y, 110 - BOX_BORDER, -fSize, theme.getBoxBodyBackgroundColor(), opacity);
				drawBox(contents, 335, y, 160 - BOX_BORDER, -fSize, theme.getBoxBodyBackgroundColor(), opacity);
				drawBox(contents, 495, y, 55f, -fSize, theme.getBoxBodyBackgroundColor(), opacity);				
			}
			
			
			for (Finance finance : invoice.getFinances()) {
				x = 180;
				drawText(contents, formatDate(finance.getDueDate(), STANDARD_DATE_FORMAT).orElse(""), x + 5f, y - 12, theme.getTextColor(), regularFont, 7, i + FINANCE_DATE);
				x += 45;
				
				String altMethodName = finance.getPayMethodType() != null ? finance.getPayMethodType().getDescription() : "";
				String paymethod = finance.getPayMethodName() != null ? finance.getPayMethodName() : altMethodName;
				drawText(contents, paymethod != null ? croppedString(paymethod, 105 - BOX_BORDER, regularFont, 7) : "", x + 5f, y - 12, theme.getTextColor(), regularFont,7, i + FINANCE_PAY_METHOD);
				x += 110;
			
				if(finance.getBankAccount() != null && finance.getBankAccount().getIban() != null) {
					String bicCode = !AonStringUtils.isEmpty(finance.getBic()) ? finance.getBic() : "";
					drawText(contents, finance.getBankAccount().getIbanLength() <= 24 ? finance.getBankAccount().getSeparatedIban() : finance.getBankAccount().getIban(), x + 5f, y - 12, theme.getTextColor(), regularFont, 7, i + FINANCE_BANK_ACCOUNT);
					drawTextRight(contents, new PDRectangle(x + 92, y, 69, 15), bicCode, theme.getTextColor(), regularFont, 5.5f, 5, -12, i + FINANCE_AMOUNT);
				} else
					drawText(contents, "", x + 5f, y - 12, theme.getTextColor(), regularFont, 7, i + FINANCE_BANK_ACCOUNT);
			
				x += 145;
				drawTextRight(contents, new PDRectangle(x, y, 69, 15), toLatinNumber(finance.getAmount()), theme.getTextColor(), regularFont, 7, 5, -12, i + FINANCE_AMOUNT);
				
				y -= 10;
				i++;
			}
			
			if (config.isBoxBodyBorder()) {
				y -= 10;
				float backHeight  = initY - y;
//				drawBox(contents, 180, initY, 370 - BOX_BORDER, BOX_BORDER, theme.getBorderColor());
				drawBox(contents, 180, y, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 225 - BOX_BORDER, y, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 335 - BOX_BORDER, y, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 495 - BOX_BORDER, y, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 550 - BOX_BORDER, y, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(contents, 180, y, 370, BOX_BORDER, theme.getBorderColor());
			}
			
		}
	}
	
	private void drawLegal(PrintInvoiceThemeConfiguration theme) throws IOException {
		x = 50;
		y-= 10;
		
		PDFToolkit.drawTextWellJustified(legalLines, 500, LEGAL_TEXT_SIZE, regularFont, x, y -= LEGAL_TEXT_SIZE, PdfColors.BLACK, contents);
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
	
	private static AonLanguage determineStreetTypeLanguage (String zipCode, AonLanguage invoiceLanguage) {
		if (zipCode == null || zipCode.length() != 5) {
			return AonLanguage.SPANISH;
		}
		String[] basqueZips = {"01", "20", "31", "48"};
		String[] catalanZips = {"07", "08", "17", "43", "25"};
		String[] galicianZips = {"15", "32", "27", "36"};
		String[] valencianZips = {"46", "12", "03"};
		
		String zipStart = !AonStringUtils.isBlank(zipCode) && zipCode.length() > 2
				? zipCode.substring(0, 2) : "";
		
		if (AonLanguage.BASQUE.equals(invoiceLanguage) && Arrays.asList(basqueZips).contains(zipStart)) {
			return AonLanguage.BASQUE;
		} else if (AonLanguage.CATALAN.equals(invoiceLanguage) && Arrays.asList(catalanZips).contains(zipStart)) {
			return AonLanguage.CATALAN;
		} else if (AonLanguage.GALICIAN.equals(invoiceLanguage) && Arrays.asList(galicianZips).contains(zipStart)) {
			return AonLanguage.GALICIAN;
		} else if (AonLanguage.VALENCIAN.equals(invoiceLanguage) && Arrays.asList(valencianZips).contains(zipStart)) {
			return AonLanguage.VALENCIAN;
		} else {
			return AonLanguage.SPANISH;
		}
	}
	
	private OfferTemplateMsg getMsg() {	
		return this.msg;
	}

	private PrintInvoiceConfiguration getConfig() {
		return config;
	}
	
	private static class DetailCategory implements Serializable {
		private static final long serialVersionUID = 6029475582594296883L;
		private Integer id;
		private String reference;
		private Date date;
		private String name;
		private String description;
		private InvoiceSource invoiceSource;
		
		public DetailCategory (InvoiceSource source, Integer id, String reference, Date date, String description) {
			super();
			this.id = id;
			this.invoiceSource = source;
			this.name = source != null ? AonStringUtils.trimToEmpty(source.getDescription()) : null;
			this.reference = reference;
			this.date = date;
			this.description = description;
		}

		public DetailCategory (ProductType productType) {
			super();
			this.name = productType != null ? AonStringUtils.trimToEmpty(productType.getName()) : null;
		}
		
		public String getReference() {
			return reference;
		}
		public Date getDate() {
			return date;
		}
		public String getName() {
			return name;
		}
		public InvoiceSource getInvoiceSource() {
			return invoiceSource;
		}
		public String getDescription() {
			return description;
		}
		
		public DetailCategory setName(String name) {
			this.name = name;
			return this;
		}
		
		@Override
		public String toString() {
			String ref =  AonStringUtils.trimToEmpty(getReference());
			String dateStr = AonStringUtils.trimToEmpty(AonDateUtils.format(getDate(), "dd/MM/yyyy"));
			String typeStr = AonStringUtils.trimToEmpty(getName());
			String str = (!typeStr.isEmpty() ? typeStr + ": " : "") + ref + (!dateStr.isEmpty() ? " del " + dateStr : "");
			if (AonStringUtils.isBlank(str)) {
				return getName();
			} else {				
				return (!typeStr.isEmpty() ? typeStr + ": " : "") + ref + (!dateStr.isEmpty() ? " del " + dateStr : "");
			}
		}
		

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DetailCategory other = (DetailCategory) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		
	}
	
	private static boolean isUdapa(CompanyFull company) {
		if (company != null &&
				company.getRegistry() != null &&
				company.getRegistry().getDomain() != null) {
			return AonStringUtils.containsIgnoreCase(company.getRegistry().getDomain().getName(), "udapa")
				 || AonStringUtils.containsIgnoreCase(company.getRegistry().getDomain().getName(), "paturpat");
		}
		return false;
	}
	
	private static InvoiceDetail copyInvoiceDetail(InvoiceDetail original) {
		InvoiceDetail newInvoiceDetail = new InvoiceDetail()
				.setId(original.getId())
				.setDomain(original.getDomain())
				.setInvoice(original.getInvoice());
		newInvoiceDetail.setInvestAsset(original.getInvestAsset().orElse(null));
		newInvoiceDetail.setProject(original.getProject());
		newInvoiceDetail.setProjectName(original.getProjectName());
		newInvoiceDetail.setSeller(original.getSeller());
		newInvoiceDetail.setItem(original.getItem());
		newInvoiceDetail.setLine(original.getLine());
		newInvoiceDetail.setDescription(original.getDescription());
		newInvoiceDetail.setQuantity(original.getQuantity());
		newInvoiceDetail.setPrice(original.getPrice());
		newInvoiceDetail.setDiscountExpression(original.getDiscountExpression());
		newInvoiceDetail.setTaxableBase(original.getTaxableBase());
		newInvoiceDetail.setTaxes(original.getTaxes());
		newInvoiceDetail.setPrepayment(original.isPrepayment());
		newInvoiceDetail.setWarehouse(original.getWarehouse());
		newInvoiceDetail.setWorkplace(original.getWorkplace());
		newInvoiceDetail.setExpAccount(original.getExpAccount());
		newInvoiceDetail.setInvoiceTaxes(original.getInvoiceTaxes());
		newInvoiceDetail.setSource(original.getSource());
		newInvoiceDetail.setSourceId(original.getSourceId());
		newInvoiceDetail.setPurchaseDetail(original.getPurchaseDetail());
		newInvoiceDetail.setSalesDetail(original.getSalesDetail());
		newInvoiceDetail.setDeliveryDetail(original.getDeliveryDetail());
		newInvoiceDetail.setIncomeDetail(original.getIncomeDetail());
		newInvoiceDetail.setOfferDetail(original.getOfferDetail());
		
		return newInvoiceDetail;
	}
	
    private static String fillProductPackage(CompanyFull company, String description, InvoiceDetail invoiceDetail) {
        Item item = invoiceDetail.getItem();
        if(item!=null && item.getProduct().isPackaged() ){
                StringBuilder builder = new StringBuilder("  ");
                if( item.getPackMeasurementTag() != null ){
                    Tag tag = AON.getTag(company.getRegistry().getDomain().getName(), company.getRegistry().getDomain().getId(),
                            "", f -> f.getIdProperty().eq(item.getPackMeasurementTag().getId()));
                    builder.append( String.format("%.2f", invoiceDetail.getQuantity()) )
                        .append( " " )
                        .append( tag.getName() )
                        .append( ": " );
                }
                if( item.getPackUnitsTag() != null ){
                        Tag tag = AON.getTag(company.getRegistry().getDomain().getName(), company.getRegistry().getDomain().getId(),
                            "", f -> f.getIdProperty().eq(item.getPackUnitsTag().getId()));
                        builder.append( String.format("%.2f",invoiceDetail.getQuantity()
                                        / item.getPackMeasurement()) )
                                .append( " " )
                                .append( tag.getName() );
                }
                if( item.getPackUnitsTag() != null
                                && item.getPackFormatTag()!=null ){
                        builder.append( ", " );
                }
                if( item.getPackFormatTag() != null ){
                    Tag tag = AON.getTag(company.getRegistry().getDomain().getName(), company.getRegistry().getDomain().getId(),
                            "", f -> f.getIdProperty().eq(item.getPackFormatTag().getId()));
                    builder.append( String.format("%.2f",(invoiceDetail.getQuantity()
                            / item.getPackMeasurement())
                            / item.getPackUnits()) )
                        .append( " " )
                        .append(tag.getName());
                }
                return description + builder.toString();
        }
		return description;
    }
}
