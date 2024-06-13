package net.aonsolutions.customer.report;

import java.io.OutputStream;
import java.util.Optional;
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

import net.aonsolutions.aon.accounting.report.AccountReportPdfPageEvent;

public class CustomerReportPDF  {

	private static Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8);
	private static Font BODY_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);

	public void printReportPDF(Occam occam, OutputStream outputStream, Stream<CustomerFull> stream)throws DocumentException {

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
		float[] widths = new float[] { 40, 80, 180, 85, 70, 50 };
		table.setTotalWidth(widths);
		table.setLockedWidth(true);

		Paragraph codeParagraph = new Paragraph(8, "C\u00F3digo", BODY_FONT_BOLD);
		codeParagraph.setAlignment(Element.ALIGN_LEFT);
		PdfPCell codeCell = new PdfPCell();
		codeCell.setBorder(0);
		codeCell.setBorderWidthBottom(1);
		codeCell.addElement(codeParagraph);
		table.addCell(codeCell);

		Paragraph documentParagraph = new Paragraph(8, "N.I.F.", BODY_FONT_BOLD);
		documentParagraph.setAlignment(Element.ALIGN_LEFT);
		PdfPCell documentCell = new PdfPCell();
		documentCell.setBorder(0);
		documentCell.setBorderWidthBottom(1);
		documentCell.addElement(documentParagraph);
		table.addCell(documentCell);

		Paragraph nameParagraph = new Paragraph(8, "Nombre", BODY_FONT_BOLD);
		nameParagraph.setAlignment(Element.ALIGN_LEFT);
		PdfPCell nameCell = new PdfPCell();
		nameCell.setBorder(0);
		nameCell.setBorderWidthBottom(1);
		nameCell.addElement(nameParagraph);
		table.addCell(nameCell);

		Paragraph aliasParagraph = new Paragraph(8, "Alias", BODY_FONT_BOLD);
		aliasParagraph.setAlignment(Element.ALIGN_LEFT);
		PdfPCell aliasCell = new PdfPCell();
		aliasCell.setBorder(0);
		aliasCell.setBorderWidthBottom(1);
		aliasCell.addElement(aliasParagraph);
		table.addCell(aliasCell);

		Paragraph phoneParagraph = new Paragraph(8, "Tel\u00E9fono", BODY_FONT_BOLD);
		phoneParagraph.setAlignment(Element.ALIGN_LEFT);
		PdfPCell phoneCell = new PdfPCell();
		phoneCell.setBorder(0);
		phoneCell.setBorderWidthBottom(1);
		phoneCell.addElement(phoneParagraph);
		table.addCell(phoneCell);
		
		Paragraph statusParagraph = new Paragraph(8, "Estado", BODY_FONT_BOLD);
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
		public void accept(CustomerFull customer) {

			Paragraph code = new Paragraph(8, customer.getId().toString(), BODY_FONT);
			PdfPCell codeCell = new PdfPCell();
			codeCell.addElement(code);
			codeCell.setBorder(0);
			table.addCell(codeCell);

			Paragraph document = new Paragraph(8, customer.getRegistry().getDocument(), BODY_FONT);
			PdfPCell documentCell = new PdfPCell();
			documentCell.addElement(document);
			documentCell.setBorder(0);
			table.addCell(documentCell);

			Paragraph name = new Paragraph(8, customer.getRegistry().getName(), BODY_FONT);
			PdfPCell nameCell = new PdfPCell();
			nameCell.addElement(name);
			nameCell.setBorder(0);
			table.addCell(nameCell);

		
			Paragraph alias = new Paragraph(8, customer.getRegistry().getAlias(), BODY_FONT);
			PdfPCell aliasCell = new PdfPCell();
			aliasCell.addElement(alias);
			aliasCell.setBorder(0);
			table.addCell(aliasCell);

			String value = AonCollectionUtils.stream(  customer.getMedias() )
				.map( rm -> rm.getValue())
				.findFirst()
				.orElse(null);
			Paragraph phone = new Paragraph(8, value, BODY_FONT);
			PdfPCell phoneCell = new PdfPCell();
			phoneCell.addElement(phone);
			phoneCell.setBorder(0);
			table.addCell(phoneCell);
			
			Optional.ofNullable(customer.getRegistry().getStatus())
				.map(rs -> rs.getDescription())
				.orElse( " " );
			Paragraph status = new Paragraph(8, customer.getRegistry().getStatus().getDescription(), BODY_FONT);
			PdfPCell statusCell = new PdfPCell();
			statusCell.addElement(status);
			statusCell.setBorder(0);
			table.addCell(statusCell);

		}

	}

	


}
