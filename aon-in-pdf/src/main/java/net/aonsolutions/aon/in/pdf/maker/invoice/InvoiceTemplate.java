package net.aonsolutions.aon.in.pdf.maker.invoice;

import static net.aonsolutions.aon.in.pdf.api.setting.PdfFormats.formatDate;
import static net.aonsolutions.aon.in.pdf.api.setting.PdfFormats.toLatinNumber;
import static net.aonsolutions.aon.in.pdf.api.toolkit.DataToolkit.safeString;
import static net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit.createVerticalPage;
import static net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit.croppedString;
import static net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit.drawBox;
import static net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit.drawImage;
import static net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit.drawText;
import static net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit.drawTextCenter;
import static net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit.drawTextRight;
import static net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit.getLines;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.ADDRESS;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.ADDRESS_LINE_TWO;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_AMOUNT;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_DESCRIPTION;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_DISCOUNT;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_PRICE;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_TOTAL;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_AMOUNT;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_BANK_ACCOUNT;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_DATE;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_PAY_METHOD;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.INVOICE_DATE;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.INVOICE_TOTAL;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.NIF;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.REFERENCE_NUMBER;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.REGISTRY_NAME;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.TAX_BASE;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.TAX_PERCENTAGE;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.TAX_QUOTE;
import static net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateTags.TAX_TYPE;

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

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceThemeConfiguration;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import net.aonsolutions.aon.in.pdf.api.setting.PdfColors;
import net.aonsolutions.aon.in.pdf.api.setting.PdfFonts;
import net.aonsolutions.aon.in.pdf.api.setting.PdfFormats;
import net.aonsolutions.aon.in.pdf.api.toolkit.PDFImage;
import net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit;
import net.aonsolutions.aon.in.pdf.maker.exception.CanNotCreatePdfException;

public class InvoiceTemplate {
	
	public static final Pattern DISCOUNT_PATTERN = Pattern.compile("[\\d,'.]+", Pattern.CASE_INSENSITIVE);
	
	private static final InvoiceSource[] SORTED_SOURCES = {InvoiceSource.DELIVERY, InvoiceSource.SALES, InvoiceSource.INCOME, InvoiceSource.OFFER};
	
	public static final int MIN_HEADER_FOR_LOGO = 80;
	public static final int MIN_HEADER_FOR_VERIFACTU_QR = 100;
	public static final int MAX_LOGO_HEIGHT = 55;
	public static final int MIN_FOOTER = 40;
	public static final float BOX_BORDER = .5f;
	public static final float BOTTOM_TOLERANCE = /*0.3f*/0f;
	public static final float TITLE_BOX_SIZE = 17f;
	public static final float LEGAL_TEXT_SIZE = 6;
	
	private static final String STANDARD_DATE_FORMAT = "dd/MM/yyyy";

	public static final PDFont FONT = PdfFonts.HELVETICA;
	public static final PDFont BOLD_FONT = PdfFonts.HELVETICA_BOLD;
	
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
		
	float opacity = 0.5f;
	
	int pageNumber;
	List<String> legalLines;
	
	Map<DetailCategory, List<InvoiceDetail>> detailMap;
	
	public InvoiceTemplate(InvoiceTemplateContext ctx) throws CanNotCreatePdfException {		
		if (ctx.getInvoices() == null || ctx.getInvoices().isEmpty() || ctx.getInvoices().stream().allMatch(Objects::isNull))
			throw new CanNotCreatePdfException("No invoice found.");
		ctx.setDocument(new PDDocument());
		buildAonMetadata(ctx);

		this.legalLines = Collections.emptyList();
		this.pageNumber = 0;
		
		ctx.setMsg(new InvoiceTemplateMsg(ctx.getConfig().getLanguage()));
		
		try {
			for (Invoice invoice : ctx.getInvoices()) {
				if (invoice == null)
					continue;
				ctx.setInvoice(invoice);
				
				this.bottomExtra = 0;
				determineStreetTypeLanguage(ctx);
				
				float minHeader = ctx.isVerifactu() ? MIN_HEADER_FOR_VERIFACTU_QR : MIN_HEADER_FOR_LOGO;
				this.top = ((ctx.getLogo() != null || ctx.isVerifactu()) && (ctx.getConfig().getHeader() != null && ctx.getConfig().getHeader() < minHeader))
						? minHeader : ctx.getConfig().getHeader();
				this.bottom	= (ctx.getConfig().getFooter() == null || (ctx.getConfig().getFooter() != null && ctx.getConfig().getFooter() < MIN_FOOTER))
						? MIN_FOOTER : ctx.getConfig().getFooter();				
				detailMap = sortInvoiceDetails(ctx);
				this.drawFirstPage(ctx);
			
				this.y -= 10;
				
				if(ctx.getConfig().isDetailed())
					this.drawDetailedEntries(ctx);
				else this.drawSimplifiedEntries(ctx);

				this.drawBottomInfo(ctx);
				this.drawJail(ctx, this.limit);
				ctx.getContents().close();
				this.drawFooter(ctx);
			}
		} catch (Exception e) {
			throw new CanNotCreatePdfException(e);
		}
	}
	
	private void buildAonMetadata(InvoiceTemplateContext ctx) {
		if(ctx.getInvoices() != null && !ctx.getInvoices().isEmpty()) {
			ctx.getDocument().getDocumentInformation().setTitle(
					ctx.getInvoices().size() > 1 
					? "Facturas" 
					: "Factura " + ctx.getInvoices().get(0).getReferenceCode());
		}
		
		ctx.getDocument().getDocumentInformation().setCustomMetadataValue("ref_homologation", "RGE405069592024");
		ctx.getDocument().getDocumentInformation().setCustomMetadataValue("software_name", "Aon Solutions");
		ctx.getDocument().getDocumentInformation().setCustomMetadataValue("software_version", "9.23");
		ctx.getDocument().getDocumentInformation().setCustomMetadataValue("timestamp", AonDateUtils.format(new Date(), "hh:mm dd/MM/yyyy"));
	}
		
	public void print(InvoiceTemplateContext ctx, OutputStream os) throws CanNotCreatePdfException {
		if (os != null)
			this.filename = os;
		try {
			ctx.getDocument().save(this.filename);
			ctx.getDocument().close();
			new OutputStreamWriter(os,StandardCharsets.ISO_8859_1);
		} catch (IOException e) {
			throw new CanNotCreatePdfException(e);
		}
	}
	
	private void drawJail(InvoiceTemplateContext ctx, float end) throws IOException {
		PrintInvoiceConfiguration config = ctx.getConfig();
		if (config.isBoxBodyBorder()) {
			if (config.isDetailed()) {
				drawBox(ctx.getContents(), 50f, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(ctx.getContents(), 300 - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(ctx.getContents(), 370 - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(ctx.getContents(), 440 - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(ctx.getContents(), 480 - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(ctx.getContents(), 550 - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(ctx.getContents(), 50f, end, 500f - BOX_BORDER, BOX_BORDER, config.getTheme().getBorderColor());
			} else {
				drawBox(ctx.getContents(), 50f, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(ctx.getContents(), 479f + BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(ctx.getContents(), 550f - BOX_BORDER, end, BOX_BORDER, entriesStart - end + TITLE_BOX_SIZE, config.getTheme().getBorderColor());
				drawBox(ctx.getContents(), 50f, end, 500f, BOX_BORDER, config.getTheme().getBorderColor());
				
			}
		}
	}
	
	private void drawComment(InvoiceTemplateContext ctx) throws IOException {
		float firstY = y;
		if (ctx.getInvoice().isRectifier() && (ctx.getInvoice().getRectificationInvoiceNumber() != null || !AonStringUtils.isEmpty(ctx.getInvoice().getRectificationInvoiceSeries()))) {
			String rn = AonStringUtils.trimToEmpty(AonNumberUtils.toString(ctx.getInvoice().getRectificationInvoiceNumber()));
			String rectNum = !AonStringUtils.isBlank(rn) ? AonStringUtils.leftPad(rn, 6, '0') : "";
			String message = ctx.getMsg().rectifies() + " " + AonStringUtils.trimToEmpty(ctx.getInvoice().getRectificationInvoiceSeries()) + "/" + rectNum;
			drawText(ctx.getContents(), message, 50f, y, ctx.getConfig().getTheme().getTitleTextColor(), BOLD_FONT, 10);
			y-=20;
		}
		
		if (ctx.getInvoice().getComments() != null && !ctx.getInvoice().getComments().isEmpty()) {
			
			drawText(ctx.getContents(), ctx.getMsg().notes() + ":", 50f, y, ctx.getConfig().getTheme().getTitleTextColor(), BOLD_FONT, 10);
			List<String> lines = PDFToolkit.getLinesRespectOriginal(ctx.getInvoice().getComments(), 575 - 100 - 50f, FONT, 10);
			
			this.commentSize = lines.size() * 10f;
			
			for (String line : lines) {
				line = line != null ? line.trim() : line;
				drawText(ctx.getContents(), line, 100f, y, ctx.getConfig().getTheme().getTextColor(), FONT, 10);
				y-=10;
				if (y < bottom) {
					ctx.getContents().close();
					drawPage(ctx, false);
					y = firstY;
				}
			}
			
			y -= 17.5;
		}
	}
	
	private void drawFooter(InvoiceTemplateContext ctx) throws IOException {
		CompanyFull company = ctx.getCompany();
		PrintInvoiceThemeConfiguration theme = ctx.getTheme();
		PDDocument doc = ctx.getDocument();
		
		if(company != null) {
			RecordData recordData = company.getRecordDatas() != null && !company.getRecordDatas().isEmpty() ? company.getRecordDatas().get(0) : null;
			String registration = "";
			String tomo = "";
			String folio = "";
			String hoja = "";
			String fechaRegistro = "";
			String registrationString = "";
			
			if (recordData != null && ctx.getConfig().isRecordData()) {
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
			
			if (company.getMedias() != null && !company.getMedias().isEmpty() && ctx.getConfig().isContactData()) {
				LinkedList<RegistryMedia> medias = company.getMedias();
			
				List<RegistryMedia> webMedias = medias.stream().filter(m -> m.getMedia() != null && (m.getValue() != null && !m.getValue().isEmpty()) 
						&& m.getMedia().equals(MediaType.WEB)).toList();
				if (!webMedias.isEmpty()) {
					webStr = "Web: ";
					StringBuilder sb = new StringBuilder(webStr);
					for(RegistryMedia m : webMedias) {
						if (!sb.toString().equals("Web: "))
							sb.append(" | ");
						sb.append(m.getValue());
						if ((FONT.getStringWidth(sb.toString()) / 1000.0f * 7) <= 180) {
							webStr = sb.toString();
						}
					}
					fullStr = sb.toString();
				}

				webMedias = medias.stream().filter(m -> m.getMedia() != null && (m.getValue() != null && !m.getValue().isEmpty()) 
						&& (m.getMedia().equals(MediaType.FIXED_PHONE) || m.getMedia().equals(MediaType.CELLULAR))).toList();
				if (!webMedias.isEmpty()) {
					phoneStr = "Teléfono/s: ";
					StringBuilder sb = new StringBuilder(phoneStr);
					for(RegistryMedia m : webMedias) {
						if (!sb.toString().equals("Teléfono/s: "))
							sb.append(" | ");
						sb.append(m.getValue());
						if ((FONT.getStringWidth(sb.toString()) / 1000.0f * 7) <= 180) {
							phoneStr = sb.toString();
						}
					}
					fullStr += (!fullStr.isEmpty() ? "    " : "") + sb.toString();
				}	
			
				webMedias = medias.stream().filter(m -> m.getMedia() != null && (m.getValue() != null && !m.getValue().isEmpty()) 
						&& m.getMedia().equals(MediaType.EMAIL)).toList();
				if (!webMedias.isEmpty()) {
					emailStr = "Email: ";
					StringBuilder sb = new StringBuilder(emailStr);
					for(RegistryMedia m : webMedias) {
						if (!sb.toString().equals("Email: "))
							sb.append(" | ");
						sb.append(m.getValue());
						if ((FONT.getStringWidth(sb.toString()) / 1000.0f * 7) <= 180) {
							emailStr = sb.toString();
						}
					}
					fullStr += (!fullStr.isEmpty() ? "    " : "") + sb.toString();
				}
				mediaStr = webStr + "    " + phoneStr + "    " + emailStr;
			}	
			
			for (int i=currentInvoiceFirstPage; i<this.pageNumber; i++) {
				PDPageContentStream contents = new PDPageContentStream(doc, doc.getPage(i), PDPageContentStream.AppendMode.APPEND, true);
				
				if (company.getMedias() != null && !company.getMedias().isEmpty()) {
					if ((FONT.getStringWidth(fullStr) / 1000.0f * 7) < 555) {
						mediaStr = fullStr;
					}
				
					drawText(contents
						, mediaStr
						, 20f
						, 30f
						, theme.getTitleTextColor()
						, FONT
						, 7);
				
				}
			
				PDFToolkit.drawBox(contents, 20, 25, 555, 1, PdfColors.GRAY);
			
				String page = "Pag. " + (i+1 - currentInvoiceFirstPage) + " de " + (pageNumber - currentInvoiceFirstPage);
				
				drawTextRight(contents
					, new PDRectangle(560, 15, 15, 15)
					, page
					, theme.getTitleTextColor()
					, FONT
					, 9
					, 0
					, 0);
			
				drawText(contents
					, registrationString
					, 20f
					, 15f
					, theme.getTitleTextColor()
					, FONT
					, 7);
				contents.close();
			}
		}
	}

	// DRAW PAGE
	private PDPageContentStream drawPage(InvoiceTemplateContext ctx, boolean withHeader) throws IOException {
		PDPage page = createVerticalPage();
		ctx.getDocument().addPage(page);
		this.pageNumber++;
		ctx.setContents(new PDPageContentStream(ctx.getDocument(), page));
		
		height = page.getMediaBox().getHeight();
		if (ctx.getConfig().getBackground() != null && ctx.getConfig().getBackground().getData() != null) {
			if (ctx.getConfig().getAdjustImage()) {
				drawImage(ctx.getDocument(), ctx.getContents(), ctx.getConfig().getBackground().getData(), 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
			} else {
				BufferedImage image = ImageIO.read(new ByteArrayInputStream(ctx.getConfig().getBackground().getData()));
			    int imgHeight = image.getHeight();
				drawImage(ctx.getDocument(), ctx.getContents(), ctx.getConfig().getBackground().getData(), 0, this.height - imgHeight);
			}
		}

		x = 50f;
		y = height - top - 20;

		drawTopInfo(ctx);
		
		y -= 30;
		
		if (withHeader) {			
			if (ctx.getConfig().isDetailed()) {				
				drawDetailedHeader(ctx);
			} else {				
				drawSimpleHeader(ctx);
			}
		}
		return ctx.getContents();
	}
	
	// DRAW FIRST PAGE
	private void drawFirstPage(InvoiceTemplateContext ctx) throws IOException, WriterException {
		PDPage page = createVerticalPage();
		ctx.getDocument().addPage(page);
		this.currentInvoiceFirstPage = this.pageNumber;
		this.pageNumber++;
		ctx.setContents(new PDPageContentStream(ctx.getDocument(), page));
		height = page.getMediaBox().getHeight();
		if (ctx.getConfig().getBackground() != null && ctx.getConfig().getBackground().getData() != null) {
			byte[] background = ctx.getConfig().getBackground().getData();
			if (ctx.getConfig().getAdjustImage()) {
				drawImage(ctx.getDocument(), ctx.getContents(), background, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
			} else {
				BufferedImage image = ImageIO.read(new ByteArrayInputStream(background));
				int imgHeight = image.getHeight();
				drawImage(ctx.getDocument(), ctx.getContents(), background, 0, this.height - imgHeight);
			}
		}
		
		limit  = bottomInfoHeight + bottom;
		
		long bottomStuff = ctx.getInvoice().getBreakdown().stream().count()
				+ ctx.getInvoice().financeStream().count()
				+ ctx.getInvoice().detailStream().filter(f -> f.isPrepayment()).count();
		
		if (bottomStuff > 8) {
			bottomExtra = (bottomStuff - 7) * 10;
		}
		
		if (ctx.getConfig().getLegal() != null && !ctx.getConfig().getLegal().isEmpty()) {
			legalLines = PDFToolkit.getLinesRespectOriginal(ctx.getConfig().getLegal(), 500, FONT, LEGAL_TEXT_SIZE);
			limit += legalLines.size() * LEGAL_TEXT_SIZE;
		}
		
		limit += bottomExtra;
		
		x = 50f;
		y = height - top - 20;
		
		if(ctx.isVerifactu() && ctx.getQrUrl() != null && !ctx.getInvoice().isProforma()) {
			float tempY = height - MIN_HEADER_FOR_LOGO;

			byte[] qrCode = createQR(ctx.getQrUrl(), 300, 300);
			drawImage(ctx.getDocument(), ctx.getContents(), qrCode, x + 390, tempY - 40, 120, 120);
			drawText(ctx.getContents(), ctx.isNoVerifactu() ? "No Veri*Factu" : "Veri*Factu", x + 440, tempY + 72, Color.BLACK, FONT, 5);
		}
		drawTopInfo(ctx);
		
		y -= 30;
		
		drawComment(ctx);
		
		entriesStart = y;
		if (ctx.getConfig().isDetailed()) {
			float realY = y;
			y-=10;
			float realEntriesStart = entriesStart;
			predictedPages = predictNumberOfDetailedPages(ctx);
			entriesStart = realEntriesStart;
			y = realY;
			drawDetailedHeader(ctx);
		} else {
			float originalEntriesStart = entriesStart;
			float originalY = y;
			y-=10;
			predictedPages = predictSimplifiedPages(ctx);
			entriesStart = originalEntriesStart;
			y = originalY;
			drawSimpleHeader(ctx);
		}
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
	
	private static void sortOtherSources(InvoiceTemplateContext ctx, List<InvoiceDetail> details, Map<DetailCategory, List<InvoiceDetail>> map) {
		List<InvoiceSource> sortedTypes = Arrays.asList(SORTED_SOURCES);
		details.stream()
		.filter(detail -> detail != null && !sortedTypes.contains(detail.getSource()))
		.forEach(detail -> {
			if(isGarage(ctx.getCompany()) && detail.getProject() != null && AonStringUtils.isNotBlank(detail.getProjectName())) {
				DetailCategory key = new DetailCategory(detail.getSource(), detail.getProject(), detail.getProjectName(), null, null);
				List<InvoiceDetail> detailList = map.getOrDefault(key, new LinkedList<>());
				detailList.add(detail);
				map.put(key, detailList);
			} else {
				List<InvoiceDetail> detailList = map.getOrDefault(null, new LinkedList<>());
				detailList.add(detail);
				map.put(null, detailList);
			}
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
	
	
	private static Map<DetailCategory, List<InvoiceDetail>> groupBySource(InvoiceTemplateContext ctx, List<InvoiceDetail> details) {
		Map<DetailCategory, List<InvoiceDetail>> map = new LinkedHashMap<>();
		if (details == null)
			return map;
		
		//OTROS
		sortOtherSources(ctx, details, map);
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
	
	private static Map<DetailCategory, List<InvoiceDetail>> sortInvoiceDetails(InvoiceTemplateContext ctx) {
		LinkedList<InvoiceDetail> udapaAuxiliaryList = new LinkedList<>();
		LinkedList<InvoiceDetail> filteredList = new LinkedList<>();
		filteredList.addAll(ctx.getInvoice().getDetails());
		
		if (isUdapa(ctx.getCompany())) {
			ctx.getInvoice().detailStream().filter(d ->
			d.getItem() != null && d.getItem().getProduct() != null &&
			ProductType.AUXILIARY.equals(d.getItem().getProduct().getType())
					).forEach(d -> {
						udapaAuxiliaryList.add(d);
						filteredList.remove(d);
					});			
		}
		
		Map<DetailCategory, List<InvoiceDetail>> detailMap = groupBySource(ctx, filteredList);
		
		if (!udapaAuxiliaryList.isEmpty()) {
			DetailCategory auxCat = new DetailCategory(ProductType.AUXILIARY);
			auxCat.setName("ENVASES");
			Map<Integer, InvoiceDetail> auxiliaryMap = new LinkedHashMap<>();
			List<InvoiceDetail> auxiliarySortedList = new LinkedList<>();
			udapaAuxiliaryList.forEach(d -> {
				if (d != null &&
					d.getItem() != null &&
					d.getItem().getProduct() != null &&
					d.getItem().getProduct().getId() != null
				) {
					InvoiceDetail detail = auxiliaryMap.getOrDefault(d.getItem().getProduct().getId(), copyInvoiceDetail(d));
					if (auxiliaryMap.containsKey(d.getItem().getProduct().getId())) {
						detail.setQuantity(AonNumberUtils.zeroIfNull(detail.getQuantity()) + AonNumberUtils.zeroIfNull(d.getQuantity()));
					}
					auxiliaryMap.put(d.getItem().getProduct().getId(), detail);
				} else {
					auxiliarySortedList.add(d);
				}
			});
			auxiliarySortedList.addAll(auxiliaryMap.values());
			detailMap.put(auxCat, auxiliarySortedList);
		}
		
		return detailMap;
	}

	// DRAW DETAILED ENTRIES
	public void drawDetailedEntries(InvoiceTemplateContext ctx) throws IOException {
		PrintInvoiceThemeConfiguration theme = ctx.getConfig().getTheme();
		
		AtomicInteger atomicI = new AtomicInteger();
		detailMap.entrySet().stream().sorted((a, b) -> {
			String aKey = AonStringUtils.trimToEmpty(a.getKey() != null ? a.getKey().getName() : "");
			String bKey = AonStringUtils.trimToEmpty(b.getKey() != null ? b.getKey().getName() : "");
			return aKey.compareTo(bKey);
		}).forEach(entry -> {
			try {
				DetailCategory category = entry.getKey();
				if (isUdapa(ctx.getCompany()) && category != null && InvoiceSource.DELIVERY.equals(category.getInvoiceSource())) {
					drawCategoryName(ctx, category, false, true);
					Map<DetailCategory, List<InvoiceDetail>> salesMap = groupBySalesReference(entry.getValue(), ctx.getCompany());
					salesMap.entrySet().stream().sorted((a, b) -> {
						String aKey = AonStringUtils.trimToEmpty(a.getKey() != null ? a.getKey().getName() : "");
						String bKey = AonStringUtils.trimToEmpty(b.getKey() != null ? b.getKey().getName() : "");
						return aKey.compareTo(bKey);
					}).forEach(detail -> {
						try {
							atomicI.set(drawDetailedCategory(ctx, detail, atomicI.get(), category != null));
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
					
				} else if (isGarage(ctx.getCompany())) {
					drawCategoryName(ctx, category, false, true);
	
					Map<DetailCategory, List<InvoiceDetail>> productMap = groupByProductType(entry.getValue(), ctx.getCompany());
					
					productMap.entrySet().stream().sorted((a, b) -> {
						String aKey = AonStringUtils.trimToEmpty(a.getKey() != null ? a.getKey().getName() : "");
						String bKey = AonStringUtils.trimToEmpty(b.getKey() != null ? b.getKey().getName() : "");
						return aKey.compareTo(bKey);
					}).forEach(productEntry -> {
						try {
							atomicI.set(drawDetailedCategory(ctx, productEntry, atomicI.get(), category != null));
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
					
				} else {				
					try {
						atomicI.set(drawDetailedCategory(ctx, entry, atomicI.get(), false));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}
	
	private void drawCategoryName(InvoiceTemplateContext ctx, DetailCategory category, boolean indent, boolean detailed) throws IOException {
		if (category != null && !AonStringUtils.isBlank(category.getName())) {
			float lineLength = (detailed ? 240 : 420) - (indent ? 10 : 0);
			List<String> lines = getLines(category.toString(), lineLength, BOLD_FONT, 9);
			
			if (y - 5 - 10 * lines.size() < bottom + 5) {
				jumpToNewPage(ctx);
				y = entriesStart - 10;
			}
			x = 50;
			y-= 5;
			
			for (String line : lines) {				
				drawText(ctx.getContents(), AonStringUtils.trim(line), x + 5 + (indent ? 10 : 0), y, ctx.getConfig().getTheme().getTextColor(), BOLD_FONT, 9);
				y-= 10;
			}
			if (AonStringUtils.isNotBlank(category.getDescription())) {				
				drawText(ctx.getContents(), category.getDescription(), x + 5 + (indent ? 10 : 0), y, ctx.getConfig().getTheme().getTextColor(), BOLD_FONT, 9);
				y-= 10;
			}
		}
	}
	
	private void simulateCategoryName(InvoiceTemplateContext ctx, DetailCategory category, boolean indent, boolean detailed, AtomicInteger numberOfPages) throws IOException {
		if (category != null && !AonStringUtils.isBlank(category.getName())) {
			float lineLength = (detailed ? 240 : 420) - (indent ? 10 : 0);
			List<String> lines = getLines(category.toString(), lineLength, BOLD_FONT, 9);
			
			if (y - 5 - 10 * lines.size() < bottom + 5) {
				fictionalPageJump(ctx);
				numberOfPages.getAndIncrement();
				y = entriesStart - 10;
			}
			y-= 5;
			for (String line : lines) {				
				y-= 10;
			}
			if (AonStringUtils.isNotBlank(category.getDescription())) {				
				y-= 10;
			}
		}
	}

	private int drawDetailedCategory(InvoiceTemplateContext ctx, Entry<DetailCategory, List<InvoiceDetail>> entry, int i, boolean indent) throws IOException {
		DetailCategory category = entry.getKey();
		List<InvoiceDetail> details = entry.getValue();
		if (details != null && !details.isEmpty()) {
			drawCategoryName(ctx, category, indent, true);
			
			for (InvoiceDetail detail : details) {
				x = 50;
				String description = 
						safeString(detail.getDescription())
							.replace("\t", " ");
				
				if (isUdapa(ctx.getCompany())) {
					String appended = fillProductPackage(ctx.getCompany(), "", detail);
					if (AonStringUtils.isNotBlank(appended)) {
						description += " (" + appended + ")";
					}
				}
				
				ArrayList<String> divided = (ArrayList<String>) PDFToolkit.getLinesRespectOriginal(description, 240 - (indent ? 10 : 0),FONT, 8);				
				float lineDiff = 10;
				
				drawDetail(ctx, i, detail, divided, lineDiff, indent);
					
				i++;
			}
		}
		return i;
	}
	
	private int simulateDetailedCategory(InvoiceTemplateContext ctx, Entry<DetailCategory, List<InvoiceDetail>> entry, int i, boolean indent, AtomicInteger numberOfPages) throws IOException {
		DetailCategory category = entry.getKey();
		List<InvoiceDetail> details = entry.getValue();
		if (details != null && !details.isEmpty()) {
			simulateCategoryName(ctx, category, indent, true, numberOfPages);
			
			for (InvoiceDetail detail : details) {
				x = 50;
				String description = 
						safeString(detail.getDescription())
						.replace("\t", " ");
				
				if (isUdapa(ctx.getCompany())) {
					String appended = fillProductPackage(ctx.getCompany(), "", detail);
					if (AonStringUtils.isNotBlank(appended)) {
						description += " (" + appended + ")";
					}
				}
				
				ArrayList<String> divided = (ArrayList<String>) PDFToolkit.getLinesRespectOriginal(description, 240 - (indent ? 10 : 0),FONT, 8);				
				float lineDiff = 10;
				
				simulateDetail(ctx, i, divided, lineDiff, indent, numberOfPages);
				
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
	
	private void jumpToNewPage(InvoiceTemplateContext ctx) throws IOException {
		drawJail(ctx, bottom);
		ctx.getContents().close();
		drawPage(ctx, true);
		y = height - top - topInfoHeight - 5;
		x = 50;
	}

	private int predictNumberOfDetailedPages(InvoiceTemplateContext ctx) throws IOException {
		Invoice invoice = ctx.getInvoice();
		CompanyFull company = ctx.getCompany();
		PrintInvoiceConfiguration config = ctx.getConfig();
		
		AtomicInteger numberOfPages = new AtomicInteger(1);
		PrintInvoiceThemeConfiguration theme = config.getTheme();
		
		AtomicInteger atomicI = new AtomicInteger();
		detailMap.entrySet().stream().sorted((a, b) -> {
			String aKey = AonStringUtils.trimToEmpty(a.getKey() != null ? a.getKey().getName() : "");
			String bKey = AonStringUtils.trimToEmpty(b.getKey() != null ? b.getKey().getName() : "");
			return aKey.compareTo(bKey);
		}).forEach(entry -> {
			try {
				DetailCategory category = entry.getKey();
				if (isUdapa(company) && category != null && InvoiceSource.DELIVERY.equals(category.getInvoiceSource())) {
					simulateCategoryName(ctx, category, false, true, numberOfPages);
					Map<DetailCategory, List<InvoiceDetail>> salesMap = groupBySalesReference(entry.getValue(), company);
					salesMap.entrySet().stream().sorted((a, b) -> {
						String aKey = AonStringUtils.trimToEmpty(a.getKey() != null ? a.getKey().getName() : "");
						String bKey = AonStringUtils.trimToEmpty(b.getKey() != null ? b.getKey().getName() : "");
						return aKey.compareTo(bKey);
					}).forEach(detail -> {
						try {
							atomicI.set(simulateDetailedCategory(ctx, detail, atomicI.get(), category != null, numberOfPages));
						} catch (IOException e) {
						}
					});
					
				}else if (isGarage(company)) {
					simulateCategoryName(ctx, category, false, true, numberOfPages);
	
					Map<DetailCategory, List<InvoiceDetail>> productMap = groupByProductType(entry.getValue(), company);
					
					productMap.entrySet().stream().sorted((a, b) -> {
						String aKey = AonStringUtils.trimToEmpty(a.getKey() != null ? a.getKey().getName() : "");
						String bKey = AonStringUtils.trimToEmpty(b.getKey() != null ? b.getKey().getName() : "");
						return aKey.compareTo(bKey);
					}).forEach(productEntry -> {
						try {
							atomicI.set(simulateDetailedCategory(ctx, productEntry, atomicI.get(), category != null, numberOfPages));
						} catch (IOException e) {
						}
					});
					
				} else {				
					try {
						atomicI.set(simulateDetailedCategory(ctx, entry, atomicI.get(), false, numberOfPages));
					} catch (IOException e) {
					}
				}
			} catch (IOException e1) {
			}
		});
		return numberOfPages.get();
	}
	
	private void fictionalPageJump(InvoiceTemplateContext ctx) throws IOException {
		CompanyFull company = ctx.getCompany();
		Invoice invoice = ctx.getInvoice();
		PrintInvoiceConfiguration config = ctx.getConfig();
		RegistryAddress transmitterAddr = null;
		
		if (company != null && config.isCompany()) {
			Company registry = company.getRegistry();
			String companyName = registry != null ? AonStringUtils.trimToEmpty(registry.getName()) : "";
			companyName = croppedString(companyName, 240, FONT, 9);
			String address;
			String zip = "";
			
			if (company.getAddresses() != null && !company.getAddresses().isEmpty()) {
				transmitterAddr = company.getAddresses().stream().filter(RegistryAddress::isMain).findFirst().orElse(company.getAddresses().getFirst());
				address = transmitterAddr.getFullAddress(ctx.getAddressLanguage());
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
			
			List<String> addressLines = getLines(address, 235, FONT, 8);
			
			if (!addressLines.isEmpty() && addressLines.size() > 1) {
				StringBuilder addressBuilder = new StringBuilder("");
				for(int i=1; i<addressLines.size(); i++) {
					addressBuilder.append(addressLines.get(i));
				}
			}
			
			List<String> zipLines = getLines(zip, 235, FONT, 8);
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
		
		String str = safeString(invoice.getRegistryName())
				.replace("\t", " ");
		
		List<String> nameLines = PDFToolkit.getLines(str, 230, BOLD_FONT, 10);
		
		if (nameLines != null) {
			String line1 = nameLines.get(0).trim();
			
			if (line1 != null) {
				y -= 10;
			}
		}
		y -= 15;
		
		String fullAddress = "";
		String province = "";
		
		if (invoice.getAddress() != null && !invoice.getAddress().isEmpty()) {
			RegistryAddress address = invoice.getAddress();
			fullAddress = safeString(address.getFullAddress(ctx.getAddressLanguage()));
			boolean isProvince = address.getProvince() != null && !address.getProvince().isEmpty() && !AonStringUtils.equalsIgnoreCase(address.getProvince(), address.getCity());
			if (isProvince) {
				province = "(" + address.getProvince().trim() + ") ";
			}
			
			if (transmitterAddr != null && transmitterAddr.getCountry() != null && address.getCountry() != null) {
				Country transmitterCountry = transmitterAddr.getCountry();
				if (!transmitterCountry.equals(address.getCountry())) {
					float textWidth = PDFToolkit.fontWidth(province + transmitterAddr.getCountry().getName(), 9, FONT);
					province += textWidth <= 230 ? address.getCountry().getName() : address.getCountry().getIso3();
				}
			}		
		}
		
		List<String> addressLines = PDFToolkit.getLines(fullAddress, 230, FONT, 9);
		
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
	
	private void drawDetail(InvoiceTemplateContext ctx, int i, InvoiceDetail detail, ArrayList<String> divided, float lineDiff, boolean indent) throws IOException {
		if(i == 7 || i == 8) {
			System.out.println("Detail " + i + ": " + detail.getDescription());
		}
		PrintInvoiceThemeConfiguration theme = ctx.getTheme();
		float dy = y;
		int line = 0;
		float originX = x;
		float a = divided.size() * lineDiff;
		for (String str : divided) {
			drawText(ctx.getContents(), str.trim(), x + 5 + (indent ? 10 : 0), dy, theme.getTextColor(), FONT, 8, i + DETAIL_DESCRIPTION);
			if (line++ == 0) {
				System.out.println("PASÓ!");
				x += 250;
				
				String amount = detail.getQuantity() != 0 ? toLatinNumber(detail.getQuantity(), 3) : "";
				String price = detail.getPrice() != 0 ? toLatinNumber(detail.getPrice(), 4) : "";
				String discount = "";
				
				try {
					String expression = detail.getDiscountExpression() != null ? detail.getDiscountExpression().getDiscountExpr() : "";
					
					Matcher matcher = DISCOUNT_PATTERN.matcher(expression);
					if (matcher.find()) {
						String numStr = matcher.group();
						numStr = numStr.replaceAll("[,']", ".");
						double percent = Double.parseDouble(numStr);
						if (percent != 0) {
							discount = safeString(detail.getDiscountExpression().getDiscountExpr());
						}
					} else {
						discount = safeString(detail.getDiscountExpression().getDiscountExpr());
					}
					
				} catch (NumberFormatException e) {
					discount = safeString(detail.getDiscountExpression().getDiscountExpr());
				}
				
				
				
				drawTextRight(ctx.getContents(), new PDRectangle(x, y, 69, 15), amount, theme.getTextColor(), FONT, 8, 4.5f, 0, i + DETAIL_AMOUNT);
				x += 70;
				
				drawTextRight(ctx.getContents(), new PDRectangle(x, y, 69, 15),price, theme.getTextColor(), FONT, 8, 4.5f, 0, i + DETAIL_PRICE);
				x += 70;
				
				drawTextRight(ctx.getContents(), new PDRectangle(x, y, 39, 15), discount, theme.getTextColor(), FONT, 8, 4.5f, 0, i + DETAIL_DISCOUNT);
				x += 40;
				
				String total = (detail.getQuantity() != 0 && detail.getPrice() != 0 && detail.getTaxableBase() != 0) ? toLatinNumber(detail.getTaxableBase()) : "";
				
				drawTextRight(ctx.getContents(), new PDRectangle(x, y, 69, 15), total, theme.getTextColor(), FONT, 8, 4.5f, 0, i + DETAIL_TOTAL);
				x = originX;
			}
			
			dy -= lineDiff;

			if (dy < bottom + 5) {
				System.out.println("***** " + detail.getDescription() + " DY after line: " + dy + " bottom: " + bottom + " lineDiff: " + lineDiff);
				jumpToNewPage(ctx);
				dy = entriesStart - 10;
			}
			
		}
		
		if (dy < limit + 5 && i == ctx.getInvoice().detailStream().count() - 1) {
			System.out.println("***** " + detail.getDescription() + " DY after line: " + dy + " bottom: " + bottom + " lineDiff: " + lineDiff);
			jumpToNewPage(ctx);
			dy = entriesStart - 10;
		}
		
		y = dy - 3;
	}
	
	private void simulateDetail(InvoiceTemplateContext ctx, int i, ArrayList<String> divided, float lineDiff, boolean indent, AtomicInteger numberOfPages) throws IOException {
		float dy = y;
		for (String str : divided) {
			dy -= lineDiff;
			if (dy < bottom + 5) {
				fictionalPageJump(ctx);
				numberOfPages.getAndIncrement();
				dy = entriesStart - 10;
			}
		}
		List<InvoiceDetail> realDetails = new LinkedList<>();
		detailMap.values().forEach(vals -> realDetails.addAll(vals));
		
		if (dy < limit + 5 && i == realDetails/*invoice.getDetails()*/.size() - 1) {
			fictionalPageJump(ctx);
			numberOfPages.getAndIncrement();
			dy = entriesStart - 10;
		}
		y = dy - 3;
	}
	
	
	public int predictSimplifiedPages(InvoiceTemplateContext ctx) throws IOException {
		Invoice invoice = ctx.getInvoice();
		CompanyFull company = ctx.getCompany();
		PrintInvoiceConfiguration config = ctx.getConfig();
		AtomicInteger numberOfPages = new AtomicInteger(1);
		
		detailMap.entrySet().stream().sorted((a, b) -> {
			String aKey = AonStringUtils.trimToEmpty(a.getKey() != null ? a.getKey().getName() : "");
			String bKey = AonStringUtils.trimToEmpty(b.getKey() != null ? b.getKey().getName() : "");
			return aKey.compareTo(bKey);
		}).forEach(entry -> {
			try {
				DetailCategory category = entry.getKey();
				if (isUdapa(company) && category != null && InvoiceSource.DELIVERY.equals(category.getInvoiceSource())) {
					simulateCategoryName(ctx, category, false, false, numberOfPages);
					Map<DetailCategory, List<InvoiceDetail>> salesMap = groupBySalesReference(entry.getValue(), company);
					salesMap.entrySet().stream().sorted((a, b) -> {
						String aKey = AonStringUtils.trimToEmpty(a.getKey() != null ? a.getKey().getName() : "");
						String bKey = AonStringUtils.trimToEmpty(b.getKey() != null ? b.getKey().getName() : "");
						return aKey.compareTo(bKey);
					}).forEach(detail -> {
						try {
							simulateSimplifiedCategory(ctx, detail, category != null, numberOfPages);
						} catch (IOException e) {
						}
					});
					
				} else if (isGarage(company)) {
					simulateCategoryName(ctx, category, false, false, numberOfPages);
					Map<DetailCategory, List<InvoiceDetail>> productMap = groupByProductType(entry.getValue(), company);
					productMap.entrySet().stream().sorted((a, b) -> {
						String aKey = AonStringUtils.trimToEmpty(a.getKey() != null ? a.getKey().getName() : "");
						String bKey = AonStringUtils.trimToEmpty(b.getKey() != null ? b.getKey().getName() : "");
						return aKey.compareTo(bKey);
					}).forEach(productEntry -> {
						try {						
							simulateSimplifiedCategory(ctx, productEntry, category != null, numberOfPages);
						} catch (IOException e ) {
						}
					});
				} else {				
					simulateSimplifiedCategory(ctx, entry, false, numberOfPages);
				}
			} catch (IOException e) {
			}

		});
		
		if (y < limit + 5) {
			fictionalPageJump(ctx);
			numberOfPages.getAndIncrement();
		}
		
		return numberOfPages.get();
	}

	// DRAW SIMPLIFIED ENTRIES
	public void drawSimplifiedEntries(InvoiceTemplateContext ctx) throws IOException {		
		detailMap.entrySet().stream().sorted((a, b) -> {
			String aKey = AonStringUtils.trimToEmpty(a.getKey() != null ? a.getKey().getName() : "");
			String bKey = AonStringUtils.trimToEmpty(b.getKey() != null ? b.getKey().getName() : "");
			return aKey.compareTo(bKey);
		}).forEach(entry -> {
			try {
				
				DetailCategory category = entry.getKey();
				if (isUdapa(ctx.getCompany()) && category != null && InvoiceSource.DELIVERY.equals(category.getInvoiceSource())) {
					drawCategoryName(ctx, category, false, false);
					Map<DetailCategory, List<InvoiceDetail>> salesMap = groupBySalesReference(entry.getValue(), ctx.getCompany());
					salesMap.entrySet().stream().sorted((a, b) -> {
						String aKey = AonStringUtils.trimToEmpty(a.getKey() != null ? a.getKey().getName() : "");
						String bKey = AonStringUtils.trimToEmpty(b.getKey() != null ? b.getKey().getName() : "");
						return aKey.compareTo(bKey);
					}).forEach(detail -> {
						try {
							drawSimplifiedCategory(ctx, detail, category != null);
						} catch (IOException e) {
						}
					});
					
				} else if (isGarage(ctx.getCompany())) {
					drawCategoryName(ctx, category, false, false);
					Map<DetailCategory, List<InvoiceDetail>> productMap = groupByProductType(entry.getValue(), ctx.getCompany());
					productMap.entrySet().stream().sorted((a, b) -> {
						String aKey = AonStringUtils.trimToEmpty(a.getKey() != null ? a.getKey().getName() : "");
						String bKey = AonStringUtils.trimToEmpty(b.getKey() != null ? b.getKey().getName() : "");
						return aKey.compareTo(bKey);
					}).forEach(productEntry -> {
						try {
							drawSimplifiedCategory(ctx, productEntry, category != null);
						} catch (IOException e ) {
							e.printStackTrace();
						}
					});
				} else {
					drawSimplifiedCategory(ctx, entry, false);
				}
			} catch (IOException e) {
			}

		});
		
		if (y < limit + 5) {
			jumpToNewPage(ctx);
		}
	}

	private void drawSimplifiedCategory(InvoiceTemplateContext ctx, Entry<DetailCategory, List<InvoiceDetail>> entry, boolean indent) throws IOException {
		DetailCategory category = entry.getKey();
		List<InvoiceDetail> details = entry.getValue();
		
		if (details != null && !details.isEmpty() && category != null) {
			drawCategoryName(ctx, category, indent, false);
		}
		for (InvoiceDetail detail : details) {
			x = 50;
			
			if (y < bottom + 5) {
				jumpToNewPage(ctx);
				y = entriesStart - 10;
			}
			String description = AonStringUtils.trimToEmpty(detail.getDescription()).replace("\t", " ");
			
			x += 430;
			
			String total = detail.getTaxableBase() != 0 ? PdfFormats.toLatinNumber(detail.getTaxableBase()) : "";
			
			PDFToolkit.drawTextRight(ctx.getContents(), new PDRectangle(x, y, 69, 15), total, ctx.getConfig().getTheme().getTextColor(), FONT, 8, 4.5f, 0);
			
			x -= 430;
			
			if (isUdapa(ctx.getCompany())) {
				String appended = fillProductPackage(ctx.getCompany(), "", detail);
				if (AonStringUtils.isNotBlank(appended)) {
					description += " (" + appended + ")";
				}
			}
			
			List<String> lines = PDFToolkit.getLinesRespectOriginal(description, 420 - (indent ? 10 : 0), FONT, 8);
			if (lines.isEmpty()) {
				y -= 10;
			}
			for (String line : lines) {
				drawText(ctx.getContents(), AonStringUtils.trimToEmpty(line), x + 5 + (indent ? 10 : 0), y, ctx.getConfig().getTheme().getTextColor(), FONT, 8, DETAIL_DESCRIPTION);
				y -= 10;
				if (y < bottom + 5) {
					jumpToNewPage(ctx);
					y = entriesStart - 10;
				}
			}
		}
	}

	private void simulateSimplifiedCategory(InvoiceTemplateContext ctx, Entry<DetailCategory, List<InvoiceDetail>> entry, boolean indent, AtomicInteger numberOfPages) throws IOException {
		DetailCategory category = entry.getKey();
		List<InvoiceDetail> details = entry.getValue();
		
		if (details != null && !details.isEmpty() && category != null) {
			simulateCategoryName(ctx, category, indent, false, numberOfPages);
		}
		for (InvoiceDetail detail : details) {
			if (y < bottom + 5) {
				fictionalPageJump(ctx);
				numberOfPages.getAndIncrement();
				y = entriesStart - 10;
			}
			String description = AonStringUtils.trimToEmpty(detail.getDescription()).replace("\t", " ");
			
			if (isUdapa(ctx.getCompany())) {
				String appended = fillProductPackage(ctx.getCompany(), "", detail);
				if (AonStringUtils.isNotBlank(appended)) {
					description += " (" + appended + ")";
				}
			}
			
			List<String> lines = PDFToolkit.getLinesRespectOriginal(description, 420 - (indent ? 10 : 0), FONT, 8);
			if (lines.isEmpty()) {
				y -= 10;
			}
			for (String line : lines) {
				y -= 10;
				if (y < bottom + 5) {
					fictionalPageJump(ctx);
					numberOfPages.getAndIncrement();
					y = entriesStart - 10;
				}
			}

		}
	}
	
	// DRAW UPPER INFO
	private void drawTopInfo(InvoiceTemplateContext ctx) throws IOException {
		PrintInvoiceConfiguration config = ctx.getConfig();
		Invoice invoice = ctx.getInvoice();
		
		float maxHeight = MAX_LOGO_HEIGHT;
		float maxWidth = 297 - x - 20;
		float tempY = height - MIN_HEADER_FOR_VERIFACTU_QR + 20;
		float logoX = x;
		float logoY = height - MIN_HEADER_FOR_LOGO + 10;

		if(ctx.isVerifactuTest()) {
			drawText(ctx.getContents(), "FACTURA NO VÁLIDA", x , (float) 450, Color.LIGHT_GRAY, BOLD_FONT, 40);
			drawText(ctx.getContents(), "ENTORNO DE PRUEBAS", x , (float) 400, Color.LIGHT_GRAY, BOLD_FONT, 40);
		}
		
		drawText(ctx.getContents(), ctx.getInvoiceTitle(), x + 255, tempY + 20, config.getTheme().getTitleTextColor(), BOLD_FONT, 14);
		if((ctx.isVerifactu() && !ctx.isNoVerifactu()) || ctx.isTbai()) {
			drawText(ctx.getContents(), "Comunicada a " + (ctx.isTbai() ? "TicketBAI" : "Veri*factu"), x + 255, tempY + 10, config.getTheme().getTitleTextColor(), FONT, 6);
			drawText(ctx.getContents(), "Verificable en la sede electrónica de Hacienda", x + 255, tempY, config.getTheme().getTitleTextColor(), FONT, 6);
		}
				
		if (ctx.getLogo() != null) {				
			try {
				PDPage page = ctx.getDocument().getPage(pageNumber - 1);
				PDFImage image = new PDFImage(ctx.getLogo(), logoX, logoY, maxWidth, maxHeight); 
				String web = ctx.getCompany().getMedias().stream()
					.filter(m -> AonStringUtils.isBlank(m.getValue()) && m.getMedia() == MediaType.WEB)
					.map(RegistryMedia::getValue).findFirst().orElse(null);
				drawImage(ctx.getDocument(), page, ctx.getContents(), image, web);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		float left = 100; 
	
		if(ctx.getConfig().isCompany())
			drawCompanyInfo(ctx, x, y);
		
		y -= 20;
		
		String number = ctx.getMsg().invoiceNumber();
		drawText(ctx.getContents(), number + ":", x, y, config.getTheme().getTitleTextColor(), BOLD_FONT, 11,REFERENCE_NUMBER);
		String reference = invoice.isProforma() ? "PROFORMA" : invoice.getReferenceCode();
		drawText(ctx.getContents(), safeString(reference), x + left, y, config.getTheme().getTextColor(), FONT, 11,REFERENCE_NUMBER);
		y -= 15;
	
		drawText(ctx.getContents(), ctx.getMsg().operationDate() + ":", x, y, config.getTheme().getTitleTextColor(), BOLD_FONT, 11 , INVOICE_DATE);
		drawText(ctx.getContents(), formatDate(invoice.getIssueDate(), STANDARD_DATE_FORMAT).orElse(""), x + left, y, config.getTheme().getTextColor(), FONT, 11 , INVOICE_DATE);

		y -= 15;

		Date expDate = invoice != null && invoice.getFiscal() != null && invoice.getFiscal().getExpDate() != null ? invoice.getFiscal().getExpDate() : invoice.getIssueDate();
			
		drawText(ctx.getContents(), ctx.getMsg().expeditionDate() + ":", x, y, config.getTheme().getTitleTextColor(), BOLD_FONT, 11 , INVOICE_DATE);
		drawText(ctx.getContents(), formatDate(expDate, STANDARD_DATE_FORMAT).orElse(""), x + left, y, config.getTheme().getTextColor(), FONT, 11 , INVOICE_DATE);			
		
		y -= 15;
		if(!ctx.getInvoice().isSimplified()) {
			String nif = ctx.getMsg().customerNif(); 
			drawText(ctx.getContents(), nif + ":", x, y, config.getTheme().getTitleTextColor(), BOLD_FONT, 11, NIF);
			
			String countryCode = "";
			if (invoice.getRegistryDocumentCountry() != null) {			
				countryCode = AonStringUtils.trimToEmpty(invoice.getRegistryDocumentCountry().getIso2());
			}
			drawText(ctx.getContents(), safeString((!AonStringUtils.isEmpty(countryCode) ? countryCode + " " : "") + invoice.getRegistryDocument()), x + left, y, config.getTheme().getTextColor(), FONT, 11, NIF);		

			y -= 10;
			x += 250;

			drawBox(ctx.getContents(), x, y, 250, 80, config.getTheme().getCustomerBackgroundColor(), opacity);
			x += 10;
		
			float boxY = y + 65;
			String str = safeString(invoice.getRegistryName())
				.replace("\t", " ");
		
			List<String> nameLines = PDFToolkit.getLines(str, 230, BOLD_FONT, 10);
		
			if (nameLines != null) {
				String line1 = nameLines.get(0).trim();
				String line2 = null;
				if (nameLines.size() > 1) {
					line2 = nameLines.get(1).trim();
				}
			
				if (line1 != null) {
					drawText(ctx.getContents(), line1, x, boxY, config.getTheme().getTextColor(), BOLD_FONT, 10, REGISTRY_NAME);
					boxY -= 10;
				}
				if (line2 != null) {
					drawText(ctx.getContents(), line2, x, boxY, config.getTheme().getTextColor(), BOLD_FONT, 10, REGISTRY_NAME);				
				}
			}
			boxY -= 15;
		
			String fullAddress = "";
			String zipCity = "";
			String province = "";
		
			if (invoice.getAddress() != null && !invoice.getAddress().isEmpty()) {
				RegistryAddress address = invoice.getAddress();
				fullAddress = safeString(address.getFullAddress(ctx.getAddressLanguage()));
				boolean isProvince = address.getProvince() != null && !address.getProvince().isEmpty() && !AonStringUtils.equalsIgnoreCase(address.getProvince(), address.getCity());
				if (isProvince) {
					province = "(" + address.getProvince().trim() + ") ";
				}
				zipCity =  safeString(address.getZip()) + " "+  safeString(address.getCity());
			
				if (address.getCountry() != null) {
					float textWidth = PDFToolkit.fontWidth(province + address.getCountry().getName(), 9, FONT);
					province += textWidth <= 230 ? address.getCountry().getName() : address.getCountry().getIso3();	
				}		
			}
		
			List<String> addressLines = PDFToolkit.getLines(fullAddress, 230, FONT, 9);
		
			if (addressLines != null && !addressLines.isEmpty()) {
				drawText(ctx.getContents(),  addressLines.get(0).trim(), x, boxY, config.getTheme().getTextColor(), FONT, 9, ADDRESS);
				boxY -= 10;
				if (addressLines.size() > 1) {
					String line2 = croppedString(addressLines.get(1), 230, FONT, 9);
					drawText(ctx.getContents(),  line2, x, boxY, config.getTheme().getTextColor(), FONT, 9, ADDRESS);
					boxY -= 12.5;
				}	
			}
		
			drawText(ctx.getContents(), zipCity.trim(), x, boxY, config.getTheme().getTextColor(), FONT, 9, ADDRESS_LINE_TWO);
			boxY -= 10;
			drawText(ctx.getContents(), province, x, boxY, config.getTheme().getTextColor(), FONT, 9, ADDRESS_LINE_TWO);
		}
	}
	
	// draw company info
	private void drawCompanyInfo(InvoiceTemplateContext ctx, float x, float y) throws IOException {
		Company registry = ctx.getCompany().getRegistry();
		String companyName = registry != null ? AonStringUtils.trimToEmpty(registry.getName()) : "";
		String nif = registry != null ? AonStringUtils.trimToEmpty(registry.getDocument()) : "";
		String address;
		String zip = "";
		RegistryAddress transmitterAddr = null;
		if (ctx.getCompany().getAddresses() != null && !ctx.getCompany().getAddresses().isEmpty()) {
			transmitterAddr = ctx.getCompany().getAddresses().stream().filter(RegistryAddress::isMain).findFirst().orElse(ctx.getCompany().getAddresses().getFirst());
			address = transmitterAddr.getFullAddress(ctx.getAddressLanguage());
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
				RegistryAddress clientAddress = ctx.getInvoice().getAddress();
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
			

		Integer companyNameFontSize = 8;
		if(companyName.length() > 50) companyNameFontSize = 6;
		else if(companyName.length() > 40) companyNameFontSize = 7;
		drawText(ctx.getContents(), companyName, x, y, ctx.getConfig().getTheme().getTitleTextColor(), BOLD_FONT, companyNameFontSize);		
		String enterpriseCountry = "";
		if (ctx.getCompany() != null && ctx.getCompany().getRegistry() != null && ctx.getCompany().getRegistry().getDocumentCountry() != null) {
			enterpriseCountry = AonStringUtils.trimToEmpty(ctx.getCompany().getRegistry().getDocumentCountry().getIso2()) + " ";	
		}
		y -= 9;
		drawText(ctx.getContents(), "NIF: " + enterpriseCountry + nif, x, y, ctx.getConfig().getTheme().getTitleTextColor(), FONT, 6);

		List<String> addressLines = getLines(address, 235, FONT, 6);
		y -= 9;
		if (!addressLines.isEmpty()) {
			drawText(ctx.getContents(), AonStringUtils.trimToEmpty(addressLines.get(0)), x , y, ctx.getConfig().getTheme().getTitleTextColor(), FONT, 6);
			y -= 9;
			if (addressLines.size() > 1) {
				StringBuilder addressBuilder = new StringBuilder("");
				for(int i=1; i<addressLines.size(); i++) {
					addressBuilder.append(addressLines.get(i));
				}
				drawText(ctx.getContents(), croppedString(AonStringUtils.trimToEmpty(addressBuilder.toString()), 235, FONT, 6), x, y, ctx.getConfig().getTheme().getTitleTextColor(), FONT, 6);
				y -= 9;
			}
		}			
		List<String> zipLines = getLines(zip, 235, FONT, 6);
		if (!zipLines.isEmpty()) {
				drawText(ctx.getContents(), AonStringUtils.trimToEmpty(zipLines.get(0)), x, y, ctx.getConfig().getTheme().getTitleTextColor(), FONT, 6);
				y -= 9;
				if (zipLines.size() > 1) {
					StringBuilder zipBuilder = new StringBuilder("");
					for(int i=1; i<zipLines.size(); i++) {
						zipBuilder.append(zipLines.get(i));
					}
					drawText(ctx.getContents(), croppedString(AonStringUtils.trimToEmpty(zipBuilder.toString()), 235, FONT, 6), x, y, ctx.getConfig().getTheme().getTitleTextColor(), FONT, 6);
				}
		}
		this.y = y;
	}

	
	// DRAW DETAILED HEADER
	private void drawDetailedHeader(InvoiceTemplateContext ctx) throws IOException {
		PrintInvoiceThemeConfiguration theme = ctx.getConfig().getTheme();
		PrintInvoiceConfiguration config = ctx.getConfig();
		x  = 50;
		
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 250 - BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextCenter(ctx.getContents(), new PDRectangle(x, y, 249, TITLE_BOX_SIZE), ctx.getMsg().description(), theme.getBoxTitleTextColor(), BOLD_FONT, 9, 5.5f);
		x += 250;

		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 70 - BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextCenter(ctx.getContents(), new PDRectangle(x, y, 69, TITLE_BOX_SIZE), ctx.getMsg().quantity(), theme.getBoxTitleTextColor(), BOLD_FONT, 9, 5.5f);
		x += 70;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 70 - BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextCenter(ctx.getContents(), new PDRectangle(x, y, 69, TITLE_BOX_SIZE), ctx.getMsg().price(), theme.getBoxTitleTextColor(), BOLD_FONT, 9, 5.5f);
		x += 70;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 40 - BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextCenter(ctx.getContents(), new PDRectangle(x, y, 39, TITLE_BOX_SIZE), "%Dto.", theme.getBoxTitleTextColor(), BOLD_FONT, 9, 5.5f);
		x += 40;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 70, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextRight(ctx.getContents(), new PDRectangle(x, y, 69, TITLE_BOX_SIZE), ctx.getMsg().amount(), theme.getBoxTitleTextColor(), BOLD_FONT, 9, 5, 5.5f);
		
		if (config.isBoxTitleBorder()) {
			drawBox(ctx.getContents(), 50, y + TITLE_BOX_SIZE, 500, BOX_BORDER, theme.getBorderColor());
			drawBox(ctx.getContents(), 50, y, 500, BOX_BORDER, theme.getBorderColor());
		}
		
		if (theme.getBoxBodyBackgroundColor() != null) {
			
			float startPoint = pageNumber - currentInvoiceFirstPage < predictedPages ? bottom - BOTTOM_TOLERANCE : limit - BOTTOM_TOLERANCE;
			float boxHeight = pageNumber - currentInvoiceFirstPage < predictedPages ? y - bottom + BOTTOM_TOLERANCE : y - limit + BOTTOM_TOLERANCE;
			
			drawBox(ctx.getContents(), 50, startPoint, 250 - BOX_BORDER, boxHeight, theme.getBoxBodyBackgroundColor(), opacity);
			drawBox(ctx.getContents(), 300, startPoint, 70 - BOX_BORDER, boxHeight, theme.getBoxBodyBackgroundColor(), opacity);
			drawBox(ctx.getContents(), 370, startPoint, 70 - BOX_BORDER, boxHeight, theme.getBoxBodyBackgroundColor(), opacity);
			drawBox(ctx.getContents(), 440, startPoint, 40 - BOX_BORDER, boxHeight, theme.getBoxBodyBackgroundColor(), opacity);
			drawBox(ctx.getContents(), 480, startPoint, 70, boxHeight, theme.getBoxBodyBackgroundColor(), opacity);
		}
		
		entriesStart = y;
	}
	

	// DRAW SIMPLE HEADER
	private void drawSimpleHeader(InvoiceTemplateContext ctx) throws IOException {
		PrintInvoiceThemeConfiguration theme = ctx.getConfig().getTheme();
		PrintInvoiceConfiguration config = ctx.getConfig();
		x  = 50;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 429 + BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawText(ctx.getContents(), ctx.getMsg().description(), x + 5f, y + 5.5f, theme.getBoxTitleTextColor(), BOLD_FONT, 9);
		x += 430;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 70, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawText(ctx.getContents(), ctx.getMsg().amount(), x + 5f, y + 5.5f, theme.getBoxTitleTextColor(), BOLD_FONT, 9);

		if (config.isBoxTitleBorder()) {
			drawBox(ctx.getContents(), 50, y + TITLE_BOX_SIZE, 500, BOX_BORDER, theme.getBorderColor());
			drawBox(ctx.getContents(), 50, y, 500 - BOX_BORDER, BOX_BORDER, theme.getBorderColor());
		}
		
		if (theme.getBoxBodyBackgroundColor() != null) {
			
			float startPoint = pageNumber - currentInvoiceFirstPage < predictedPages ? bottom - BOTTOM_TOLERANCE : limit - BOTTOM_TOLERANCE;
			float boxHeight = pageNumber - currentInvoiceFirstPage < predictedPages ? y - bottom + BOTTOM_TOLERANCE : y - limit + BOTTOM_TOLERANCE;
			
			drawBox(ctx.getContents(), 50, startPoint, 429 + BOX_BORDER, boxHeight, theme.getBoxBodyBackgroundColor(), opacity);
			drawBox(ctx.getContents(), 480, startPoint, 70, boxHeight, theme.getBoxBodyBackgroundColor(), opacity);
		}
		
		
		entriesStart = y;
	}

	// DRAW BOTTOM INFO
	private void drawBottomInfo(InvoiceTemplateContext ctx) throws IOException, WriterException {
 		x = 50;
		float legalSize = legalLines.size() * LEGAL_TEXT_SIZE;
		if(y < limit) {
			jumpToNewPage(ctx);
		}
		y = bottom + 10 + bottomExtra + legalSize;
		if(!ctx.isVerifactu() && ctx.getQrUrl() != null && !ctx.getInvoice().isProforma()) {
			byte[] qrCode = createQR(ctx.getQrUrl(), 300, 300);
			drawImage(ctx.getDocument(), ctx.getContents(), qrCode, x, y, 120, 120);
			if(ctx.getTbaiId() != null)
				drawText(ctx.getContents(), ctx.getTbaiId(), x + 5f, y + 2f + 110, Color.BLACK, FONT, 5);
		}
		drawTaxes(ctx);
		drawFinances(ctx);
		y = bottom + 10 + legalSize;
		drawLegal(ctx);
	}

	// DRAW TAXES
	private void drawTaxes(InvoiceTemplateContext ctx) throws IOException {
		x = 180;
		float legalSize = legalLines.size() * LEGAL_TEXT_SIZE;
		y = bottom + 113 + bottomExtra + legalSize;
		if (ctx.getTheme().getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 80 - BOX_BORDER, TITLE_BOX_SIZE, ctx.getConfig().getTheme().getBoxTitleBackgroundColor());
		drawTextRight(ctx.getContents(), new PDRectangle(x, y, 79, TITLE_BOX_SIZE), ctx.getMsg().base(), ctx.getTheme().getBoxTitleTextColor(), FONT, 9, 5, 5.5f);
		x += 80;
		if (ctx.getTheme().getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 80 - BOX_BORDER, TITLE_BOX_SIZE, ctx.getTheme().getBoxTitleBackgroundColor());
		drawTextRight(ctx.getContents(), new PDRectangle(x, y, 79, TITLE_BOX_SIZE), "%", ctx.getTheme().getBoxTitleTextColor(), FONT, 9, 5, 5.5f);
		x += 80;
		if (ctx.getTheme().getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 60 - BOX_BORDER, TITLE_BOX_SIZE, ctx.getTheme().getBoxTitleBackgroundColor());
		drawTextCenter(ctx.getContents(), new PDRectangle(x, y, 59, TITLE_BOX_SIZE), ctx.getMsg().type(), ctx.getTheme().getBoxTitleTextColor(), FONT, 9, 5.5f);
		x += 60;
		if (ctx.getTheme().getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 50 - BOX_BORDER, TITLE_BOX_SIZE, ctx.getTheme().getBoxTitleBackgroundColor());
		drawTextRight(ctx.getContents(), new PDRectangle(x, y, 49, TITLE_BOX_SIZE), ctx.getMsg().quota(), ctx.getTheme().getBoxTitleTextColor(), FONT, 9, 5, 5.5f);
		x += 50;
		if (ctx.getTheme().getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 100, TITLE_BOX_SIZE, ctx.getTheme().getBoxTitleBackgroundColor());
		drawTextCenter(ctx.getContents(), new PDRectangle(x, y, 99, TITLE_BOX_SIZE), ctx.getMsg().totalInvoice(), ctx.getTheme().getBoxTitleTextColor(), BOLD_FONT, 9, 5.5f);
		

		if (ctx.getConfig().isBoxTitleBorder()) {
			drawBox(ctx.getContents(), 180, y + TITLE_BOX_SIZE, 370, BOX_BORDER, ctx.getTheme().getBorderColor());
			drawBox(ctx.getContents(), 180, y, 370, BOX_BORDER, ctx.getTheme().getBorderColor());
		}
		
		if (ctx.getInvoice().getBreakdown() != null){
			int i = 0;
			float initY = y;
			
			float bdSize = (ctx.getInvoice().getBreakdown().size() + ctx.getInvoice().detailStream().filter(f -> f.isPrepayment()).count()) * 10 + 10f;
			
			if (ctx.getTheme().getBoxBodyBackgroundColor() != null) {				
				drawBox(ctx.getContents(), 180, y, 80 - BOX_BORDER, -bdSize, ctx.getTheme().getBoxBodyBackgroundColor(), opacity);
				drawBox(ctx.getContents(), 260, y, 80 - BOX_BORDER, -bdSize, ctx.getTheme().getBoxBodyBackgroundColor(), opacity);
				drawBox(ctx.getContents(), 340, y, 60 - BOX_BORDER, -bdSize, ctx.getTheme().getBoxBodyBackgroundColor(), opacity);
				drawBox(ctx.getContents(), 400, y, 50 - BOX_BORDER, -bdSize, ctx.getTheme().getBoxBodyBackgroundColor(), opacity);
				drawBox(ctx.getContents(), 450, y, 100f, -bdSize, ctx.getTheme().getBoxBodyBackgroundColor(), opacity);
			}
			
			float initiaruY = y - 12;
			
			for (InvoiceBreakdown tax : ctx.getInvoice().getBreakdown()) {
				
				x = 180;
				drawTextRight(ctx.getContents(), new PDRectangle(x, y, 79, 15), toLatinNumber(tax.getBase()), ctx.getTheme().getTextColor(), FONT, 7, 5, -12, i + TAX_BASE);
				x += 80;
				
				String percent = "";
				if (tax.getPercentage() > 99)
					percent = "100%";
				else if (tax.getPercentage() != 0) {
					percent = toLatinNumber(tax.getPercentage()) + "%";
				
					if(tax.getSurcharge() != 0.00)
						percent += " + " +  toLatinNumber(tax.getSurcharge());
			
				}

				drawTextRight(ctx.getContents(), new PDRectangle(x, y, 79, 15), percent, ctx.getTheme().getTextColor(), FONT, 7, 5, -12, i + TAX_PERCENTAGE);
				x += 80;
				
				String taxName = TaxType.VAT.equals(tax.getTaxType()) && ctx.getAdministration().isCanarias()
						? "IGIC" : tax.getTaxType().getName();
				drawTextCenter(ctx.getContents(), new PDRectangle(x, y, 59, 15), taxName, ctx.getTheme().getTextColor(), FONT, 7, -12, i + TAX_TYPE);
				x += 60;
				
				drawTextRight(ctx.getContents(), new PDRectangle(x, y, 49, 15), toLatinNumber(tax.getQuota() + tax.getSurchargeQuota()), ctx.getTheme().getTextColor(), FONT, 7, 5, -12, i + TAX_QUOTE);
				x += 50;
				
				y	-= 10;
				
				i++;
			}
			double totalPrepayment = ctx.getInvoice().detailStream().filter(f -> f.isPrepayment()).mapToDouble(InvoiceDetail::getTaxableBase).sum();
			if(totalPrepayment != 0.0) {
				x = 340;
				drawTextCenter(ctx.getContents(), new PDRectangle(x, y, 59, 15), "Suplidos", ctx.getTheme().getTextColor(), FONT, 7, -12, 0 + TAX_TYPE);
				x += 60;
				drawTextRight(ctx.getContents(), new PDRectangle(x, y, 49, 15), toLatinNumber(totalPrepayment), ctx.getTheme().getTextColor(), FONT, 7, 5, -12, 0 + TAX_QUOTE);
				x += 50;
				y -= 10;				
			}
		
			if ((ctx.getInvoice().getBreakdown() != null && (!ctx.getInvoice().getBreakdown().isEmpty()))) {
				int totalThings = ctx.getInvoice().getBreakdown().size() + (totalPrepayment != 0.0 ? 1 : 0);
				float middle = /*(totalThings % 2 != 0 ? totalThings / 2f -0.5f: totalThings / 2f - 0.5f)*/(totalThings / 2f -0.5f) * 10;
				middle = initiaruY - middle;
				drawTextRight(ctx.getContents(), new PDRectangle(x, middle, 99, 8), toLatinNumber(ctx.getInvoice().getTotal()) + " \u20AC", ctx.getTheme().getTextColor(), BOLD_FONT, 8, 5, -.5f, INVOICE_TOTAL);
			} else {
				drawTextRight(ctx.getContents(), new PDRectangle(x, initiaruY, 99, 8), toLatinNumber(ctx.getInvoice().getTotal()) + " \u20AC", ctx.getTheme().getTextColor(), BOLD_FONT, 8, 5, -.5f, INVOICE_TOTAL);
				y	-= 10;
			}
			
			float finalY = y -10;
			float backHeight = initY - finalY;
			
			if (ctx.getConfig().isBoxBodyBorder()) {
//				drawBox(contents, 180, finalY + backHeight, 370, BOX_BORDER, theme.getBorderColor());
				drawBox(ctx.getContents(), 180, finalY, BOX_BORDER, backHeight + + TITLE_BOX_SIZE, ctx.getTheme().getBorderColor());
				drawBox(ctx.getContents(), 260 - BOX_BORDER, finalY, BOX_BORDER, backHeight + TITLE_BOX_SIZE, ctx.getTheme().getBorderColor());
				drawBox(ctx.getContents(), 340 - BOX_BORDER, finalY, BOX_BORDER, backHeight + TITLE_BOX_SIZE, ctx.getTheme().getBorderColor());
				drawBox(ctx.getContents(), 400 - BOX_BORDER, finalY, BOX_BORDER, backHeight + TITLE_BOX_SIZE, ctx.getTheme().getBorderColor());
				drawBox(ctx.getContents(), 450 - BOX_BORDER, finalY, BOX_BORDER, backHeight + TITLE_BOX_SIZE, ctx.getTheme().getBorderColor());
				drawBox(ctx.getContents(), 550 - BOX_BORDER, finalY, BOX_BORDER, backHeight + TITLE_BOX_SIZE, ctx.getTheme().getBorderColor());
				drawBox(ctx.getContents(), 180, finalY, 370, BOX_BORDER, ctx.getTheme().getBorderColor());
			}
		}
	}

	// DRAW FINANCES
	private void drawFinances(InvoiceTemplateContext ctx) throws IOException {
		Invoice invoice = ctx.getInvoice();
		PrintInvoiceConfiguration config = ctx.getConfig();
		PrintInvoiceThemeConfiguration theme = ctx.getConfig().getTheme();	
		x = 180;
		y-= 28;
		
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 45 - BOX_BORDER, TITLE_BOX_SIZE,  theme.getBoxTitleBackgroundColor());
		drawText(ctx.getContents(), ctx.getMsg().date(), x + 5f, y + 5.5f, theme.getBoxTitleTextColor(), FONT, 9);
		x += 45;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 110 - BOX_BORDER, TITLE_BOX_SIZE,  theme.getBoxTitleBackgroundColor());
		drawText(ctx.getContents(), ctx.getMsg().payMethod(), x + 5f, y + 5.5f, theme.getBoxTitleTextColor(), FONT, 9);
		x += 110;
		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 160 - BOX_BORDER, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawText(ctx.getContents(), ctx.getMsg().bankAccount(), x + 5f, y + 5.5f, theme.getBoxTitleTextColor(), FONT, 9);
		x += 160;

		if (theme.getBoxTitleBackgroundColor() != null)
			drawBox(ctx.getContents(), x, y, 55, TITLE_BOX_SIZE, theme.getBoxTitleBackgroundColor());
		drawTextRight(ctx.getContents(), new PDRectangle(x, y, 54, TITLE_BOX_SIZE), ctx.getMsg().amount(), theme.getBoxTitleTextColor(), FONT, 9, 5, 5.5f);
		
		if (config.isBoxTitleBorder()) {
			drawBox(ctx.getContents(), 180, y + TITLE_BOX_SIZE, 370, BOX_BORDER, theme.getBorderColor());
			drawBox(ctx.getContents(), 180, y, 370, BOX_BORDER, theme.getBorderColor());
		}

		if(invoice.getFinances() != null) {
			int i = 0;
			float initY = y;
			
			float fSize = invoice.getFinances().size() * 10 + 10f;
			
			if (theme.getBoxBodyBackgroundColor() != null) {
				drawBox(ctx.getContents(), 180, y, 45 - BOX_BORDER, -fSize, theme.getBoxBodyBackgroundColor(), opacity);
				drawBox(ctx.getContents(), 225, y, 110 - BOX_BORDER, -fSize, theme.getBoxBodyBackgroundColor(), opacity);
				drawBox(ctx.getContents(), 335, y, 160 - BOX_BORDER, -fSize, theme.getBoxBodyBackgroundColor(), opacity);
				drawBox(ctx.getContents(), 495, y, 55f, -fSize, theme.getBoxBodyBackgroundColor(), opacity);				
			}
			
			
			for (Finance finance : invoice.getFinances()) {
				x = 180;
				drawText(ctx.getContents(), formatDate(finance.getDueDate(), STANDARD_DATE_FORMAT).orElse(""), x + 5f, y - 12, theme.getTextColor(), FONT, 7, i + FINANCE_DATE);
				x += 45;
				
				String altMethodName = finance.getPayMethodType() != null ? finance.getPayMethodType().getDescription() : "";
				String paymethod = finance.getPayMethodName() != null ? finance.getPayMethodName() : altMethodName;
				drawText(ctx.getContents(), paymethod != null ? croppedString(paymethod, 105 - BOX_BORDER, FONT, 7) : "", x + 5f, y - 12, theme.getTextColor(), FONT,7, i + FINANCE_PAY_METHOD);
				x += 110;
			
				if(finance.getBankAccount() != null && finance.getBankAccount().getIban() != null) {
					String bicCode = !AonStringUtils.isEmpty(finance.getBic()) ? finance.getBic() : "";
					drawText(ctx.getContents(), formatIban(finance), x + 5f, y - 12, theme.getTextColor(), FONT, 7, i + FINANCE_BANK_ACCOUNT);
					drawTextRight(ctx.getContents(), new PDRectangle(x + 92, y, 69, 15), bicCode, theme.getTextColor(), FONT, 5.5f, 5, -12, i + FINANCE_AMOUNT);
				} else
					drawText(ctx.getContents(), "", x + 5f, y - 12, theme.getTextColor(), FONT, 7, i + FINANCE_BANK_ACCOUNT);
			
				x += 145;
				drawTextRight(ctx.getContents(), new PDRectangle(x, y, 69, 15), toLatinNumber(finance.getAmount()), theme.getTextColor(), FONT, 7, 5, -12, i + FINANCE_AMOUNT);
				
				y -= 10;
				i++;
			}
			
			if (config.isBoxBodyBorder()) {
				y -= 10;
				float backHeight  = initY - y;
//				drawBox(contents, 180, initY, 370 - BOX_BORDER, BOX_BORDER, theme.getBorderColor());
				drawBox(ctx.getContents(), 180, y, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(ctx.getContents(), 225 - BOX_BORDER, y, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(ctx.getContents(), 335 - BOX_BORDER, y, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(ctx.getContents(), 495 - BOX_BORDER, y, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(ctx.getContents(), 550 - BOX_BORDER, y, BOX_BORDER, backHeight + TITLE_BOX_SIZE, theme.getBorderColor());
				drawBox(ctx.getContents(), 180, y, 370, BOX_BORDER, theme.getBorderColor());
			}
			
		}
	}
	
	private static String formatIban(Finance finance) {
		if (finance != null && finance.getBankAccount() != null) {
			if (PayMethodType.NEGOTIABLE_DOCUMENT.equals(finance.getPayMethodType())) {
				String hiddenWithDots = finance.getBankAccount().getMaskedIban();
				
				if (finance.getBankAccount().getIbanLength() <= 24) {
					return AonStringUtils.replace(hiddenWithDots, ".", " ");
				} else {
					return AonStringUtils.replace(hiddenWithDots, ".", "");				
				}
			} else {			
				if (finance.getBankAccount().getIbanLength() <= 24) {
					return finance.getBankAccount().getSeparatedIban();
				} else {
					return finance.getBankAccount().getIban();
				}
			}
		}
		return "";
	}

	private void drawLegal(InvoiceTemplateContext ctx) throws IOException {
		x = 50;
		y-= 10;
		
		PDFToolkit.drawTextWellJustified(legalLines, 500, LEGAL_TEXT_SIZE, FONT, x, y -= LEGAL_TEXT_SIZE, PdfColors.BLACK, ctx.getContents());
	}
	
	public static byte[] createQR(String datos, int ancho, int altura) throws WriterException, IOException {
	    BitMatrix matrix;
	    Writer escritor = new QRCodeWriter();
	    matrix = escritor.encode(datos, BarcodeFormat.QR_CODE, ancho, altura);
	         
	    BufferedImage imagen = new BufferedImage(ancho, altura, BufferedImage.TYPE_INT_ARGB);
	    
	    for(int y = 0; y < altura; y++) {
	        for(int x = 0; x < ancho; x++) {
	            boolean isBlack = matrix.get(x, y);
	            int color = isBlack ? 0xFF000000 : 0x00FFFFFF;
	            imagen.setRGB(x, y, color);
	        }
	    }
	    
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(imagen, "PNG", baos);
	    return baos.toByteArray();        
	}
	
	private void determineStreetTypeLanguage (InvoiceTemplateContext ctx) {
		String zipCode = ctx.getInvoice().getAddress() != null ? ctx.getInvoice().getAddress().getZip() : "";

		if (zipCode == null || zipCode.length() != 5) {
			ctx.setAddressLanguage(AonLanguage.SPANISH);
		} else {
			String[] basqueZips = {"01", "20", "31", "48"};
			String[] catalanZips = {"07", "08", "17", "43", "25"};
			String[] galicianZips = {"15", "32", "27", "36"};
			String[] valencianZips = {"46", "12", "03"};
		
			String zipStart = !AonStringUtils.isBlank(zipCode) && zipCode.length() > 2
				? zipCode.substring(0, 2) : "";
			
			if(ctx.getConfig().getLanguage().isBasque() && Arrays.asList(basqueZips).contains(zipStart)
					|| ctx.getConfig().getLanguage().isCatalan() && Arrays.asList(catalanZips).contains(zipStart)
					|| ctx.getConfig().getLanguage().isGalician() && Arrays.asList(galicianZips).contains(zipStart)
					|| ctx.getConfig().getLanguage().isValencian() && Arrays.asList(valencianZips).contains(zipStart))
				ctx.setAddressLanguage(ctx.getConfig().getLanguage());
			else ctx.setAddressLanguage(AonLanguage.SPANISH);
		}
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
		newInvoiceDetail.setInvestAsset(original.getInvestAsset());
		newInvoiceDetail.setInvestAssetData(original.getInvestAssetData());
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
		newInvoiceDetail.setWarehouseName(original.getWarehouseName());
		newInvoiceDetail.setWorkplace(original.getWorkplace());
		newInvoiceDetail.setAccountId(original.getAccountId());
		newInvoiceDetail.setAccountCode(original.getAccountCode());
		newInvoiceDetail.setAccountDescription(original.getAccountDescription());
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
