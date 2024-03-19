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

		createHeader("C\u00F3digo",table);
		createHeader("N.I.F.",table);
		createHeader("Nombre",table);
		createHeader("Alias",table);
		createHeader("Tel\u00E9fono",table);
		createHeader("Estado",table);
		table.setHeaderRows(1);

		PDFAction action = new PDFAction(table);
		stream.forEach(action);
		stream.close();
		document.add(table);
		document.close();

	}
	
	private void createHeader(String columnName, PdfPTable table) {
		Paragraph paragraph = new Paragraph (8,columnName,BODY_FONT_BOLD);
		paragraph.setAlignment(Element.ALIGN_LEFT);
		PdfPCell cell = new PdfPCell();
		cell.setBorder(0);
		cell.setBorderWidthBottom(1);
		cell.addElement(paragraph);
		table.addCell(cell);
	}

	private class PDFAction implements Consumer<CustomerFull> {

		private PdfPTable table;

		public PDFAction(PdfPTable table) {
			this.table = table;
		}

		@Override
		public void accept(CustomerFull customer) {

			createParagraph(customer.getId().toString());
			createParagraph(customer.getRegistry().getDocument());
			createParagraph(customer.getRegistry().getName());
			createParagraph(customer.getRegistry().getAlias());

			String value = AonCollectionUtils.stream(  customer.getMedias() )
				.map( rm -> rm.getValue())
				.findFirst()
				.orElse(null);
			createParagraph(value);
			
			Optional.ofNullable(customer.getRegistry().getStatus())
				.map(rs -> rs.getDescription())
				.orElse( " " );
			createParagraph(customer.getRegistry().getStatus().getDescription());

		}
		
		private void createParagraph(String content) {
			Paragraph paragraph = new Paragraph(8, content, BODY_FONT);
			PdfPCell cell = new PdfPCell();
			cell.addElement(paragraph);
			cell.setBorder(0);
			table.addCell(cell);
		}
		
		

	}

	


}
