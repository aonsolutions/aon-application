package com.esferalia.aon.in.payroll.pdf.formParsers;

import com.esferalia.aon.in.payroll.pdf.creators.PDFToolkit;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class PdfFieldNameParser {

	//PARSER HANDLE EXCEPTIONS
	public static void parse(InputStream is,String filename) throws IOException{
		try (PDDocument doc = PDDocument.load(is)) {parser(doc,filename);}
	}

	//TOTAL DOCUMENT PARSER
	private static void parser(PDDocument doc,String filename) throws IOException {
		doc.setAllSecurityToBeRemoved(true);
		PDDocumentCatalog pdCatalog = doc.getDocumentCatalog();
		PDAcroForm pdAcroForm = pdCatalog.getAcroForm();

		for(PDField pdField : pdAcroForm.getFields()){
				if(pdField.getFieldType().equals("Tx")) 	pdField.setValue("");
				String name = pdField.getPartialName();
				PDRectangle rectangle = PDFToolkit.getFieldRectangle(pdField);
				PDPage page = PDFToolkit.get_field_page(pdField);
				PDPageContentStream contents = PDFToolkit.open_in_append_mode(doc,page);

				float x,y,w,h;
				x = rectangle.getLowerLeftX();
				y = rectangle.getLowerLeftY();
				w = rectangle.getWidth();
				h = rectangle.getHeight();

				if(w < 40) w = 40;

				PDFToolkit.drawBox(contents,x,y,w,h, new Color(0xf0f0f0));
				PDFToolkit.drawText(contents,name,x+ 3,y+ h/2,PDFToolkit.AON_BLUE, PDFToolkit.HELVETICA_BOLD,3f);

				contents.close();
		}
		pdAcroForm.flatten();
		doc.save(filename);
	}

	private void printRect(final PDPageContentStream contentStream, final PDRectangle rect) throws IOException {
		contentStream.setStrokingColor(Color.YELLOW);
		contentStream.drawLine(rect.getLowerLeftX(), rect.getLowerLeftY(), rect.getLowerLeftX(), rect.getUpperRightY()); // left
		contentStream.drawLine(rect.getLowerLeftX(), rect.getUpperRightY(), rect.getUpperRightX(), rect.getUpperRightY()); // top
		contentStream.drawLine(rect.getUpperRightX(), rect.getLowerLeftY(), rect.getUpperRightX(), rect.getUpperRightY()); // right
		contentStream.drawLine(rect.getLowerLeftX(), rect.getLowerLeftY(), rect.getUpperRightX(), rect.getLowerLeftY()); // bottom
		contentStream.setStrokingColor(Color.BLACK);
	}

}
