package com.esferalia.aon.in.payroll.pdf.formParsers;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.pdf.creators.PDFToolkit;
import com.esferalia.aon.in.payroll.tgss.report.Employee;
import com.esferalia.aon.watson.util.AonStringUtils;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.text.PDFTextStripper;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;

public class InternshipsWorkParser {

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
			try{
				if(pdField.getFieldType().equals("Tx"))pdField.setValue("");
				System.out.println(pdField.getPartialName());
				String name = pdField.getPartialName();
				PDRectangle rectangle = pdField.getWidgets().get(0).getRectangle();
				PDPage page = pdField.getWidgets().get(0).getPage();
				PDPageContentStream contents = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);

				float x,y,w,h;
				x = rectangle.getLowerLeftX();
				y = rectangle.getLowerLeftY();
				w = rectangle.getWidth();
				h = rectangle.getHeight();

				if(w < 40) w = 40;

				PDFToolkit.drawBox(contents,x,y,w,h, new Color(0xf0f0f0));
				PDFToolkit.drawText(contents,name,x+ 3,y+ h/2,new Color(0x3a5b9e), PDType1Font.HELVETICA_BOLD,3f);

				contents.close();


			}
			catch(Exception e){ }
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
