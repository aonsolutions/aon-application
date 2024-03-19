package net.aonsolutions.aon.accounting.report;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class CustomerReportPDF {

	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00;(#,##0.00)");

	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");

	private static Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8);

	private static Font BODY_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);

	public void printReportPDF(Occam occam, OutputStream outputStream, Stream<CustomerFull> stream)
			throws DocumentException {

		String domainName = occam.getDomainName();

		String user = occam.getUser();

		int domainId = occam.getDomain();

		AonConfiguration config = AON.getConfiguration(domainName, domainId, user);

		Company company = config.getCompany();

		String companyName = company == null ? "" : company.getName();

		ReportMetadata metadata = new ReportMetadata()

				.setTitle("Listado de Clientes")

				.setCompanyName(companyName);

		Document document = new Document();

		document.setPageSize(PageSize.A4);

		document.setMargins(30, 30, 50, 30);

		PdfWriter writer = PdfWriter.getInstance(document, outputStream);

		writer.setPageEvent(new AccountReportPdfPageEvent(metadata));

		document.open();

		int columns = 6;

		PdfPTable table = new PdfPTable(columns);

		float[] widths = new float[] { 40, 50, 175, 70, 70, 40 };

		table.setTotalWidth(widths);

		table.setLockedWidth(true);
		// Code
		Paragraph codeParagrph = new Paragraph(8, "Code", BODY_FONT_BOLD);

		codeParagrph.setAlignment(Element.ALIGN_LEFT);

		PdfPCell codeCell = new PdfPCell();

		codeCell.setBorder(0);

		codeCell.setBorderWidthBottom(1);

		codeCell.addElement(codeParagrph);

		table.addCell(codeCell);
		// EIN/TIN
		Paragraph docParagrph = new Paragraph(8, "EIN/TIN", BODY_FONT_BOLD);

		docParagrph.setAlignment(Element.ALIGN_LEFT);

		PdfPCell descripCell = new PdfPCell();

		descripCell.setBorder(0);

		descripCell.setBorderWidthBottom(1);

		descripCell.addElement(docParagrph);

		table.addCell(descripCell);
		// Name
		Paragraph nameParagrph = new Paragraph(8, "Name", BODY_FONT_BOLD);

		nameParagrph.setAlignment(Element.ALIGN_LEFT);

		PdfPCell nameCell = new PdfPCell();

		nameCell.setBorder(0);

		nameCell.setBorderWidthBottom(1);

		nameCell.addElement(nameParagrph);

		table.addCell(nameCell);
		// Alias
		Paragraph aliasParagrph = new Paragraph(8, "Alias", BODY_FONT_BOLD);

		aliasParagrph.setAlignment(Element.ALIGN_LEFT);

		PdfPCell aliasCell = new PdfPCell();

		aliasCell.setBorder(0);

		aliasCell.setBorderWidthBottom(1);

		aliasCell.addElement(aliasParagrph);

		table.addCell(aliasCell);
		// Phone
		Paragraph phoneParagrph = new Paragraph(8, "Phone", BODY_FONT_BOLD);

		phoneParagrph.setAlignment(Element.ALIGN_LEFT);

		PdfPCell phoneCell = new PdfPCell();

		phoneCell.setBorder(0);

		phoneCell.setBorderWidthBottom(1);

		phoneCell.addElement(phoneParagrph);

		table.addCell(phoneCell);

		table.setHeaderRows(1);
		// Status
		Paragraph statusParagraph = new Paragraph(8, "Status", BODY_FONT_BOLD);

		statusParagraph.setAlignment(Element.ALIGN_LEFT);

		PdfPCell statusCell = new PdfPCell();

		statusCell.setBorder(0);

		statusCell.setBorderWidthBottom(1);

		statusCell.addElement(statusParagraph);

		table.addCell(statusCell);

		table.setHeaderRows(1);

		PDFAction action = new PDFAction(table);

		stream.forEach(action);

		stream.close();

		document.add(table);

		document.close();

	}

	private class PDFAction implements Consumer<CustomerFull> {

		private PdfPTable table;

		public PDFAction(PdfPTable table) {

			this.table = table;

		}

		@Override

		public void accept(CustomerFull entry) {
			// Code
			Paragraph code = new Paragraph(8, entry.getRegistry().getId().toString(), BODY_FONT);

			PdfPCell codeCell = new PdfPCell();

			codeCell.addElement(code);

			codeCell.setBorder(0);

			table.addCell(codeCell);

			// EIN/TIN
			Paragraph doc = new Paragraph(8, entry.getRegistry().getDocument(), BODY_FONT);

			PdfPCell docCell = new PdfPCell();

			docCell.addElement(doc);

			docCell.setBorder(0);

			table.addCell(docCell);

			// Name
			Paragraph name = new Paragraph(8, entry.getRegistry().getName(), BODY_FONT);

			PdfPCell nameCell = new PdfPCell();

			nameCell.addElement(name);

			nameCell.setBorder(0);

			table.addCell(nameCell);

			// Alias
			Paragraph alias = new Paragraph(8, entry.getRegistry().getAlias(), BODY_FONT);

			PdfPCell aliasCell = new PdfPCell();

			aliasCell.addElement(alias);

			aliasCell.setBorder(0);

			table.addCell(aliasCell);

			// Phone
			String phone = AonCollectionUtils.stream(entry.getMedias())
				.map(rm -> rm.getValue())
				.findFirst()
				.orElse(null);

			Paragraph phoneP = new Paragraph(8, phone, BODY_FONT);

			PdfPCell phoneCell = new PdfPCell();

			phoneCell.addElement(phoneP);

			phoneCell.setBorder(0);

			table.addCell(phoneCell);

			// Status
			Paragraph status = new Paragraph(8, entry.getRegistry().getStatus().toString(), BODY_FONT);

			PdfPCell statusCell = new PdfPCell();

			statusCell.addElement(status);

			statusCell.setBorder(0);
			
			table.addCell(statusCell);

		}

	}

}
