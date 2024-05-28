package net.aonsolutions.aon.report;

import java.io.OutputStream;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import net.aonsolutions.aon.accounting.report.AccountReportPdfPageEvent;

public abstract class AbsSimpleReportPDF<T>  {
	
	private final Document document;
	private final ReportMetadata metadata;
	
	protected AbsSimpleReportPDF(Occam occam) {
		AonConfiguration config = AON.getConfiguration(occam);
		this.metadata = new ReportMetadata()
			.setTitle("Listado de Clientes")
			.setCompanyName(Optional.ofNullable(config.getCompany()).map(c -> c.getName()).orElse(""));
		this.document = new Document();
		this.document.setPageSize(PageSize.A4);
		this.document.setMargins(30, 30, 50, 30);
	}

	protected void printReportPDF(OutputStream outputStream, AbsReportTablePDF<T> table, Stream<T> stream) throws AonReportException {
		try {
			PdfWriter writer = PdfWriter.getInstance(document, outputStream);
			writer.setPageEvent(new AccountReportPdfPageEvent(metadata));
			this.document.open();
			stream.forEach(table);
			this.document.add(table);
			this.document.close();
		} catch (DocumentException e) {
			throw new AonReportException(e); 
		}
	}
	
	protected abstract void print(OutputStream outputStream, Stream<T> stream) throws AonReportException;

}
