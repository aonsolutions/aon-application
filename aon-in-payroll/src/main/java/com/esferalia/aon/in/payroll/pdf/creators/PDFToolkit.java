package com.esferalia.aon.in.payroll.pdf.creators;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.List;

public class PDFToolkit {

	public final static Color AON_BLUE = new Color(0x3a5b9e);
	public final static PDFont HELVETICA = PDType1Font.HELVETICA;
	public final static PDFont HELVETICA_BOLD = PDType1Font.HELVETICA_BOLD;

	//CREATE AN HORIZONTAL PAGE
	public static PDPage createHorizontalPage(){
		float POINTS_PER_INCH = 72;
		float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;

		return new PDPage(new PDRectangle(297 * POINTS_PER_MM, 210 * POINTS_PER_MM));
	}

	//CREATE AN HORIZONTAL PAGE
	public static PDPage createVerticalPage(){
		float POINTS_PER_INCH = 72;
		float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;

		return new PDPage(new PDRectangle(210 * POINTS_PER_MM,297 * POINTS_PER_MM));
	}

	//DRAWS A TEXT
	public static void drawText(PDPageContentStream contents, String content, Float x, Float y,Color color, PDFont font, float fontSize) throws IOException {
		contents.setNonStrokingColor(color);
		contents.beginText();
		contents.setFont(font, fontSize);
		contents.newLineAtOffset(x,y);
		contents.showText(content);
		contents.endText();
	}


	//DRAWS AN IMAGE
	public static void drawImage(PDDocument doc, PDPageContentStream contents, String route,float x, float y, float width, float height) throws IOException {
		contents.drawImage(PDImageXObject.createFromFile(route,doc),x,y,width,height);
	}

	public static void drawImage(PDDocument doc, PDPageContentStream contents, byte[] logo, float width, float height) throws IOException {
		contents.drawImage(PDImageXObject.createFromByteArray(doc,logo,null),20,535,width,height);
	}

	//DRAW A BOX
	public static void drawBox(PDPageContentStream contents,float x,float y,float width,float height,Color color) throws IOException {
		contents.setNonStrokingColor(color);
		contents.addRect(x, y, width, height);
		contents.fill();
	}

	//FORMAT DATE TO STRING IN A SPECIFIC FORMAT
	public static Optional<String> formatDate(Date date, String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Optional<String> formattedDate;
		formattedDate = Optional.of(dateFormatter.format(date));
		return formattedDate;
	}

	//GET PDF FORM FIELDS
	public static List<PDField> getFormFields(PDDocument doc){
		PDDocumentCatalog pdCatalog = doc.getDocumentCatalog();
		PDAcroForm pdAcroForm = pdCatalog.getAcroForm();
		return  pdAcroForm.getFields();
	}

	//GET FIELD RECTANGLE
	public static PDRectangle getFieldRectangle(PDField field){
		return field.getWidgets().get(0).getRectangle();
	}

	//GET FIELD PAGE
	public static PDPage get_field_page(PDField field){
		return field.getWidgets().get(0).getPage();
	}

	//OPEN PAGE IN APPEND MODE
	public static PDPageContentStream open_in_append_mode(PDDocument doc, PDPage page){
		try { return new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);}
		catch (IOException e) {return null;}
	}

}
