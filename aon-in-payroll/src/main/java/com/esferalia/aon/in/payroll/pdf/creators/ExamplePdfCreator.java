package com.esferalia.aon.in.payroll.pdf.creators;

import java.awt.*;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Creates a sample.pdf document and write a message at an offset with HELVETICA_BOLD font style.
 */
public class ExamplePdfCreator {
	public static void main(String[] args) throws IOException {
		String filename = "./sample.pdf";
		String message = "Aon solutions.";

		PDDocument doc = new PDDocument();
		try {
			PDPage page = new PDPage();
			doc.addPage(page);

			PDFont font = PDType1Font.HELVETICA_BOLD;

			PDPageContentStream contents = new PDPageContentStream(doc, page);

			contents.setNonStrokingColor(new Color(0x3a5b9e));
			contents.addRect(0, 700,800,100);
			contents.fill();

			contents.setNonStrokingColor(new Color(0xffffff));
			contents.beginText();
			contents.setFont(font, 18);
			contents.newLineAtOffset(50, 750);
			contents.showText(message);
			contents.endText();

			contents.beginText();
			contents.setFont(font, 15);
			contents.newLineAtOffset(50, 730);
			contents.showText("Ejemplo de nomina");
			contents.endText();

			contents.close();
			doc.save(filename);
		}
		finally {
			doc.close();
		}
	}
}