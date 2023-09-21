package com.esferalia.aon.in.payroll.pdf.maker.invoice;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceListTemplate {
	
	private static final float SIDE_MARGIN = 30f;
	private static final float FIRST_COLUMN = SIDE_MARGIN;
	private static final float SECOND_COLUMN = FIRST_COLUMN + 47.5f;
	private static final float THIRD_COLUMN = SECOND_COLUMN + 74f;
	private static final float FOURTH_COLUMN = THIRD_COLUMN + 47.5f;
	private static final float FIFTH_COLUMN = FOURTH_COLUMN + 190;
	private static final float SIXTH_COLUMN = FIFTH_COLUMN + 67.5f;
	private static final float SEVENTH_COLUMN = SIXTH_COLUMN + 47.5f;
	private static final float EIGTH_COLUMN = SEVENTH_COLUMN + 58.5f;
	private static final float DATA_FONT_SIZE = 7.5f;
	protected static final int MAX_ENTRIES_PER_PAGE = 54;
	
	//NORMAL TEXTS
	private static final String PAGE_TXT = "Página";
	private static final String OF_TXT = "de";
	
	private static final String TITLE_TXT = "Listado de Facturas";
	private static final String DATE_TXT = "Fecha";
	private static final String INVOICE_NUMBER_TXT = "Nº Factura";
	private static final String NIF_TXT = "N.I.F.";
	private static final String TITULAR_TXT = "Titular";
	//RIGHT TEXTS
	private static final String BASE_TXT = "Base Imp.";
	private static final String IVA_TXT = "I.V.A.";
	private static final String IRPF_TXT = "I.R.P.F.";
	private static final String TOTAL_TXT = "Total";

	private PDDocument document;
	private PDPageContentStream contentStream;
	private PDPage page;
	private float heigth;
	private float width;
	private int totalPages;
	private int currentPage;
	private int currentEntryIndex;
	private float x;
	private float y;
	
	//TOTALS
	private double totalBase;
	private double totalIva;
	private double totalIrpf;
	private double totalTotal;
	
	private String companyName;
	private List<InvoiceListEntry> entries;
	
	public InvoiceListTemplate(Company company, List<Invoice> invoices) throws CanNotCreatePdfException {
		this(company != null ? company.getName() : "", invoicesToEntries(invoices));
	}
	
	public InvoiceListTemplate(String companyName, List<InvoiceListEntry> entries) throws CanNotCreatePdfException {
		try {
			this.entries = getSortedEntries(entries);
			this.companyName = companyName;
			document = new PDDocument();
			//NÚMERO DE PÁGINAS QUE TENDRÁ EL PDF
			calculatePages();
			//INICIALIZACIÓN
			currentPage = 1;
			currentEntryIndex = 0;
			initializeTotals();
			//CREAR LAS PÁGINAS
			while (currentPage <= totalPages) {				
				createPage();
				drawHeader();
				drawEntries();
				if(currentPage == totalPages) {
					drawTotals();
				}
				drawFooter();
				endPage();
				currentPage++;
			}
		} catch (IOException e) {
			throw new CanNotCreatePdfException(e);
		}
	}

	private void createPage() throws IOException {
		page = new PDPage();
		
		heigth = page.getMediaBox().getHeight();
		width = page.getMediaBox().getWidth();
		contentStream = new PDPageContentStream(document, page);
	}
	
	private void endPage() throws IOException {		
		contentStream.close();
		document.addPage(page);
	}
	
	private void drawHeader() throws IOException {
		x = SIDE_MARGIN;
		y = heigth - SIDE_MARGIN;
		PDFToolkit.drawText(contentStream, TITLE_TXT, x, y, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, 15f);
		y -= 18;
		
		float companyNameMaxWidth = width - SIDE_MARGIN - 125 - x;
		String safeCompanyName = PDFToolkit.croppedString(AonStringUtils.trimToEmpty(companyName), companyNameMaxWidth, PdfFonts.HELVETICA, 11f);
		
		PDFToolkit.drawText(contentStream, safeCompanyName, x, y, PdfColors.BLACK, PdfFonts.HELVETICA, 11f);
		x = width - SIDE_MARGIN - 120;
		PDFToolkit.drawText(contentStream, DATE_TXT + ": " + com.esferalia.aon.watson.server.AonDateUtils.format(new Date(), com.esferalia.aon.watson.server.AonDateUtils.SIMPLE_DATE_FORMAT), x, y, PdfColors.BLACK, PdfFonts.HELVETICA, 11f);
		y -= 20;
		x = FIRST_COLUMN;
		PDFToolkit.drawText(contentStream, DATE_TXT, x, y, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, DATA_FONT_SIZE);
		x = SECOND_COLUMN;
		PDFToolkit.drawText(contentStream, INVOICE_NUMBER_TXT, x, y, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, DATA_FONT_SIZE);
		x = THIRD_COLUMN;
		PDFToolkit.drawText(contentStream, NIF_TXT, x, y, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, DATA_FONT_SIZE);
		x = FOURTH_COLUMN;
		PDFToolkit.drawText(contentStream, TITULAR_TXT, x, y, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, DATA_FONT_SIZE);
		x = FIFTH_COLUMN;
		PDFToolkit.drawText(contentStream, BASE_TXT, x, y, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, DATA_FONT_SIZE);
		x = SIXTH_COLUMN;
		PDFToolkit.drawText(contentStream, IVA_TXT, x, y, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, DATA_FONT_SIZE);
		x = SEVENTH_COLUMN;
		PDFToolkit.drawText(contentStream, IRPF_TXT, x, y, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, DATA_FONT_SIZE);
		x = EIGTH_COLUMN;
		PDFToolkit.drawText(contentStream, TOTAL_TXT, x, y, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, DATA_FONT_SIZE);
		y -= 5;
		PDFToolkit.drawBox(contentStream, SIDE_MARGIN, y, width - 2 * SIDE_MARGIN, 1.5f, PdfColors.BLACK);
		x = SIDE_MARGIN;
		y -= 10;
	}
	
	private void drawEntries() throws IOException {
		int from = currentEntryIndex;
		int to = (currentEntryIndex + (MAX_ENTRIES_PER_PAGE - 1)) < entries.size() ? (currentEntryIndex + (MAX_ENTRIES_PER_PAGE - 1)) : (entries.size() - 1);
		if (to < from) {
			to = from;
		}
		
		while (currentEntryIndex <= to && to < entries.size()) {
			InvoiceListEntry entry = entries.get(currentEntryIndex);
			x = FIRST_COLUMN;
			PDFToolkit.drawText(contentStream, getDateString(entry.getDate()), x, y, PdfColors.BLACK, PdfFonts.HELVETICA, DATA_FONT_SIZE);
			x = SECOND_COLUMN;
			PDFToolkit.drawText(contentStream, AonStringUtils.trimToEmpty(entry.getNumber()), x, y, PdfColors.BLACK, PdfFonts.HELVETICA, DATA_FONT_SIZE);			
			x = THIRD_COLUMN;
			PDFToolkit.drawText(contentStream, AonStringUtils.trimToEmpty(entry.getNif()), x, y, PdfColors.BLACK, PdfFonts.HELVETICA, DATA_FONT_SIZE);			
			x = FOURTH_COLUMN;
			String clientName = PDFToolkit.croppedString(AonStringUtils.trimToEmpty(entry.getName()), (FIFTH_COLUMN - FOURTH_COLUMN - 5), PdfFonts.HELVETICA, DATA_FONT_SIZE);
			PDFToolkit.drawText(contentStream, AonStringUtils.trimToEmpty(clientName), x, y, PdfColors.BLACK, PdfFonts.HELVETICA, DATA_FONT_SIZE);			
			x = FIFTH_COLUMN;
			float boxWidth = PDFToolkit.fontWidth(BASE_TXT, DATA_FONT_SIZE, PdfFonts.HELVETICA_BOLD);
			PDFToolkit.drawTextRight(contentStream, new PDRectangle(x, y, boxWidth, DATA_FONT_SIZE), PdfFormats.toLatinNumber(entry.getBase()), PdfColors.BLACK, PdfFonts.HELVETICA, DATA_FONT_SIZE, 0, 0);
			x = SIXTH_COLUMN;
			boxWidth = PDFToolkit.fontWidth(IVA_TXT, DATA_FONT_SIZE, PdfFonts.HELVETICA_BOLD);
			PDFToolkit.drawTextRight(contentStream, new PDRectangle(x, y, boxWidth, DATA_FONT_SIZE), PdfFormats.toLatinNumber(entry.getIva()), PdfColors.BLACK, PdfFonts.HELVETICA, DATA_FONT_SIZE, 0, 0);
			x = SEVENTH_COLUMN;
			boxWidth = PDFToolkit.fontWidth(IRPF_TXT, DATA_FONT_SIZE, PdfFonts.HELVETICA_BOLD);
			PDFToolkit.drawTextRight(contentStream, new PDRectangle(x, y, boxWidth, DATA_FONT_SIZE), PdfFormats.toLatinNumber(entry.getIrpf()), PdfColors.BLACK, PdfFonts.HELVETICA, DATA_FONT_SIZE, 0, 0);
			x = EIGTH_COLUMN;
			boxWidth = PDFToolkit.fontWidth(TOTAL_TXT, DATA_FONT_SIZE, PdfFonts.HELVETICA_BOLD);
			PDFToolkit.drawTextRight(contentStream, new PDRectangle(x, y, boxWidth, DATA_FONT_SIZE), PdfFormats.toLatinNumber(entry.getTotal()), PdfColors.BLACK, PdfFonts.HELVETICA, DATA_FONT_SIZE, 0, 0);
			
			addToTotals(entry);
			y -= 12.5;
			currentEntryIndex++;
		}
	}
	
	private void drawFooter() throws IOException {
		y = SIDE_MARGIN;
		PDFToolkit.drawBox(contentStream, SIDE_MARGIN, y, width - 2 * SIDE_MARGIN, 1.5f, PdfColors.BLACK);
		y -= 15;
		x = 475;
		String pageString = String.format(PAGE_TXT + " %d " + OF_TXT + " %d" , currentPage, totalPages);
		PDFToolkit.drawText(contentStream, pageString, x, y, PdfColors.BLACK, PdfFonts.HELVETICA, 10);
	}
	
	private void drawTotals() throws IOException {
		x = 300;
		PDFToolkit.drawBox(contentStream, x, y, width - x - SIDE_MARGIN, 1f, PdfColors.BLACK);
		x += 40;
		y -= 12.5;
		PDFToolkit.drawText(contentStream, TOTAL_TXT, x, y, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, DATA_FONT_SIZE);
		x = FIFTH_COLUMN;
		float boxWidth = PDFToolkit.fontWidth(BASE_TXT, DATA_FONT_SIZE, PdfFonts.HELVETICA_BOLD);
		PDFToolkit.drawTextRight(contentStream, new PDRectangle(x, y, boxWidth, DATA_FONT_SIZE), PdfFormats.toLatinNumber(totalBase), PdfColors.BLACK, PdfFonts.HELVETICA, DATA_FONT_SIZE, 0, 0);
		x = SIXTH_COLUMN;
		boxWidth = PDFToolkit.fontWidth(IVA_TXT, DATA_FONT_SIZE, PdfFonts.HELVETICA_BOLD);
		PDFToolkit.drawTextRight(contentStream, new PDRectangle(x, y, boxWidth, DATA_FONT_SIZE), PdfFormats.toLatinNumber(totalIva), PdfColors.BLACK, PdfFonts.HELVETICA, DATA_FONT_SIZE, 0, 0);
		x = SEVENTH_COLUMN;
		boxWidth = PDFToolkit.fontWidth(IRPF_TXT, DATA_FONT_SIZE, PdfFonts.HELVETICA_BOLD);
		PDFToolkit.drawTextRight(contentStream, new PDRectangle(x, y, boxWidth, DATA_FONT_SIZE), PdfFormats.toLatinNumber(totalIrpf), PdfColors.BLACK, PdfFonts.HELVETICA, DATA_FONT_SIZE, 0, 0);
		x = EIGTH_COLUMN;
		boxWidth = PDFToolkit.fontWidth(TOTAL_TXT, DATA_FONT_SIZE, PdfFonts.HELVETICA_BOLD);
		PDFToolkit.drawTextRight(contentStream, new PDRectangle(x, y, boxWidth, DATA_FONT_SIZE), PdfFormats.toLatinNumber(totalTotal), PdfColors.BLACK, PdfFonts.HELVETICA, DATA_FONT_SIZE, 0, 0);
	}
	
	public void print(OutputStream os) throws CanNotCreatePdfException {
		try {
			document.save(os);
			document.close();
			os.close();
		} catch (Exception e) {
			throw new CanNotCreatePdfException(e);
		}
	}
	
	private void calculatePages() {
		if (entries != null && !entries.isEmpty()) {
			int basePages = (int) AonMathUtils.ceil((double) entries.size() / MAX_ENTRIES_PER_PAGE, 0);
			//ENTRADAS / ENTRADAS MÁXIMAS + (SI HAY RESTO -> +1) + (SI FALTAN 2 O MENOS ENTRADAS PARA COMPLETAR LA ÚLTIMA PÁGINA -> +1)
			totalPages = basePages + (entries.size() % MAX_ENTRIES_PER_PAGE == 0 || entries.size() % MAX_ENTRIES_PER_PAGE > MAX_ENTRIES_PER_PAGE - 2 ? 1 : 0);
		} else {
			totalPages = 1;
		}
	}
	
	private void initializeTotals() {
		totalBase = 0;
		totalIva = 0;
		totalIrpf = 0;
		totalTotal = 0;
	}
	
	private void addToTotals(InvoiceListEntry entry) {
		if (entry != null) {			
			totalBase += AonNumberUtils.zeroIfNull(entry.getBase());
			totalIva += AonNumberUtils.zeroIfNull(entry.getIva());
			totalIrpf += AonNumberUtils.zeroIfNull(entry.getIrpf());
			totalTotal  += AonNumberUtils.zeroIfNull(entry.getTotal());
		}
	}
	
	private static List<InvoiceListEntry> getSortedEntries(List<InvoiceListEntry> entries) {
		List<InvoiceListEntry> sortedEntries = entries != null ? entries : Collections.emptyList();
		sortedEntries.removeIf(Objects::isNull);
		sortedEntries.sort((a,b) -> AonDateUtils.compare(a.getDate(), b.getDate()));
		return sortedEntries;
	}
	
	private static List<InvoiceListEntry> invoicesToEntries(List<Invoice> invoices) {
		List<InvoiceListEntry> entries = new LinkedList<>();
		if (invoices != null) {			
			invoices.stream().map(inv -> new InvoiceListEntry(inv)).forEach(entries::add);
		}
		return getSortedEntries(entries);
	}
	
	private static String getDateString(Date date) {
		return AonStringUtils.trimToEmpty(
				com.esferalia.aon.watson.server.AonDateUtils.format(date, com.esferalia.aon.watson.server.AonDateUtils.SIMPLE_DATE_FORMAT)
		);
	}
	
}