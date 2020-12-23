package com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill;

import com.esferalia.aon.in.payroll.pdf.creators.PDFToolkit;
import netscape.javascript.JSObject;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.jooq.tools.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class EnterpriseBillTemplate {
	private static String filename = "./EnterpriseBill.pdf";

	public static void create(String name,EnterpriseBill bill) throws IOException {
		try (PDDocument doc = new PDDocument()) {
			if(name != null) filename = name;

			PDPage page = PDFToolkit.createVerticalPage();
			doc.addPage(page);

			PDPageContentStream contents = new PDPageContentStream(doc, page);

			float top = (float) bill.getTop_px();
			float bottom = (float) bill.getBottom_px();

			float width = page.getMediaBox().getWidth();
			float height = page.getMediaBox().getHeight();

			PDFToolkit.drawText(contents, "FACTURA", 50f, height - top - 10, PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 16);
			PDFToolkit.drawText(contents, "Numero: " + bill.getBill_number(), 50f, height - top - 40, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 12);
			PDFToolkit.drawText(contents, "Fecha: " + formatDate(bill.getDate(),"dd,MM,yyyy").get(), 50f, height - top - 60, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 12);
			PDFToolkit.drawText(contents, "N.I.F: " + bill.getNif(), 50f, height - top - 80, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 12);

			contents.close();
			doc.save(filename);
		}
	}


	//FORMAT DATE TO STRING IN A SPECIFIC FORMAT
	public static Optional<String> formatDate(Date date, String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Optional<String> formattedDate;
		formattedDate = Optional.of(dateFormatter.format(date));
		return formattedDate;
	}
}
