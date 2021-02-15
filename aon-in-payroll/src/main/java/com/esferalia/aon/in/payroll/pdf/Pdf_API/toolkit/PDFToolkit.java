package com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit;

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

import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import javax.imageio.ImageIO;

import java.util.List;

public class PDFToolkit {


	public final static DecimalFormat df = PdfFormats.two_digit_decimal;

	//CREATE AN HORIZONTAL PAGE
	public static PDPage createHorizontalPage() {
		float POINTS_PER_INCH = 72;
		float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;
		return new PDPage(new PDRectangle(297 * POINTS_PER_MM, 210 * POINTS_PER_MM));
	}

	//CREATE AN VERTICAL PAGE
	public static PDPage createVerticalPage() {
		float POINTS_PER_INCH = 72;
		float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;
		return new PDPage(new PDRectangle(210 * POINTS_PER_MM, 297 * POINTS_PER_MM));
	}

	//DRAWS A TEXT
	public static void drawText(PDPageContentStream contents, String content, Float x, Float y, Color color, PDFont font, float fontSize) throws IOException {
		contents.setNonStrokingColor(color);
		contents.beginText();
		contents.setFont(font, fontSize);
		contents.newLineAtOffset(x, y);
		contents.showText(content);
		contents.endText();
	}

	//DRAWS A RIGHT ALIGNED TEXT
	public static void drawTextRight(PDPageContentStream contents, PDRectangle box, String content, Color color, PDFont font, float fontSize, float x_margin, float y_margin) throws IOException {
		float w = box.getWidth();
		float fw = (font.getStringWidth(content) / 1000.0f) * fontSize;

		float x = box.getLowerLeftX() + w - fw - x_margin;
		float y = y_margin + box.getLowerLeftY();

		contents.setNonStrokingColor(color);
		contents.beginText();
		contents.setFont(font, fontSize);
		contents.newLineAtOffset(x, y);
		contents.showText(content);
		contents.endText();
	}	
	
	//DRAWS A LEFT ALIGNED TEXT
	public static void drawTextLeft(PDPageContentStream contents, PDRectangle box, String content, Color color, PDFont font, float fontSize, float x_margin, float y_margin) throws IOException {

		float x = box.getLowerLeftX() + x_margin;
		float y = y_margin + box.getLowerLeftY();

		contents.setNonStrokingColor(color);
		contents.beginText();
		contents.setFont(font, fontSize);
		contents.newLineAtOffset(x, y);
		contents.showText(content);
		contents.endText();
	}	

	//DRAWS A CENTERED TEXT
	public static void drawTextCenter(PDPageContentStream contents, PDRectangle box, String content, Color
			color, PDFont font, float fontSize, float y_margin) throws IOException {
		float w = box.getWidth();
		float fw = (font.getStringWidth(content) / 1000.0f) * fontSize;

		float x = box.getLowerLeftX() + (w - fw)/2;
		float y = y_margin + box.getLowerLeftY();

		contents.setNonStrokingColor(color);
		contents.beginText();
		contents.setFont(font, fontSize);
		contents.newLineAtOffset(x, y);
		contents.showText(content);
		contents.endText();
	}

	//DRAWS AN IMAGE
	public static void drawImage(PDDocument doc, PDPageContentStream contents, String route, float x, float y, float width, float height) throws IOException {
		contents.drawImage(PDImageXObject.createFromFile(route, doc), x, y, width, height);
	}

	public static void drawImage(PDDocument doc, PDPageContentStream contents, byte[] logo, float x, float y) throws IOException {
		contents.drawImage(PDImageXObject.createFromByteArray(doc, logo, null), x, y);
	}


	public static void drawImage(PDDocument doc, PDPageContentStream contents, byte[] logo, float x, float y, float width, float height) throws IOException {
		contents.drawImage(PDImageXObject.createFromByteArray(doc, logo, null), x, y, width, height);
	}

	public static void drawImage(PDDocument doc, PDPageContentStream contents, InputStream logo, float x, float y, float width, float height) throws IOException {
		contents.drawImage(PDImageXObject.createFromByteArray(doc, logo.readAllBytes(), "image"), x, y, width, height);
	}

	public static void drawImage(PDDocument doc, PDPageContentStream contents, InputStream logo, float x, float y) throws IOException {
		contents.drawImage(PDImageXObject.createFromByteArray(doc, logo.readAllBytes(), "image"), x, y);
	}
	
	//DRAW A BOX
	public static void drawBox(PDPageContentStream contents, float x, float y, float width, float height, Color color) throws IOException {
		contents.setNonStrokingColor(color);
		contents.addRect(x, y, width, height);
		contents.fill();
	}
	
	//DRAW A BORDERED BOX
	public static void drawBorderedBox(PDPageContentStream contents, float x, float y, float width, float height, Color color) throws IOException {
		contents.setStrokingColor(color);
		contents.addRect(x, y, width, height);
		contents.setLineWidth(2);
		contents.stroke();
	}

	//GET PDF FORM FIELDS
	public static List<PDField> get_form_fields(PDDocument doc) {
		PDDocumentCatalog pdCatalog = doc.getDocumentCatalog();
		PDAcroForm pdAcroForm = pdCatalog.getAcroForm();
		return pdAcroForm.getFields();
	}

	//GET FIELD RECTANGLE
	public static Optional<PDRectangle> get_field_rectangle(Optional<PDField> field, int widget_index) {
		if (field.isEmpty()) return Optional.empty();
		PDField f = field.get();
		if (widget_index >= f.getWidgets().size()) return Optional.empty();
		return Optional.of(f.getWidgets().get(widget_index).getRectangle());
	}

	//GET FIELD RECTANGLES
	public static ArrayList<Optional<PDRectangle>> get_field_rectangles(Optional<PDField> field) {
		ArrayList<Optional<PDRectangle>> rectangles = new ArrayList<>();
		if (field.isPresent()) {
			for (int i = 0; i < field.get().getWidgets().size(); i++)
				rectangles.add(get_field_rectangle(field, i));

		}

		return rectangles;
	}

	//GET FIELD PAGE
	public static Optional<PDPage> get_field_page(Optional<PDField> field, int widget_index) {
		if (field.isEmpty()) return Optional.empty();
		PDField f = field.get();
		if (widget_index >= f.getWidgets().size()) return Optional.empty();
		return Optional.of(f.getWidgets().get(widget_index).getPage());
	}

	//GET FIELD PAGES
	public static ArrayList<Optional<PDPage>> get_field_pages(Optional<PDField> field) {
		ArrayList<Optional<PDPage>> pages = new ArrayList<>();
		if (field.isPresent()) {
			for (int i = 0; i < field.get().getWidgets().size(); i++)
				pages.add(get_field_page(field, i));
		}
		return pages;
	}


	//OPEN PAGE IN APPEND MODE
	public static PDPageContentStream open_in_append_mode(PDDocument doc, PDPage page) {
		try {
			return new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
		} catch (IOException e) {
			return null;
		}
	}
	

	//DIVIDE A STRING TO FIT A WIDTH
	public static List<String> divide_string_to_fit(String text, float max, PDFont font, float fontSize) throws IOException{
		ArrayList<String> lines = new ArrayList<>();
		ArrayList<String> words = (ArrayList<String>) StringToolkit.to_words(text);

		String line = "";
		
		for (String word : words) {
			float fw = (font.getStringWidth(line + " " + word) / 1000.0f) * fontSize;
			if(fw > max) {
				lines.add(line);
				line = "";
			}else line += " " + word;
		}	
		if(lines.size() > 1 && !lines.get(lines.size()-1).equals(line)) lines.add(line);
		
		return lines;
	}

	//CROP TEXT WITH ...
	public static String cropped_string(String text, double width, PDFont font, float fontSize) throws IOException {
		if(text == null) return text;

		String txt = text;
		float fw = (font.getStringWidth(text) / 1000.0f) * fontSize;
		while (fw > width){
			text = text.substring(0, text.length()-1);
			fw = (font.getStringWidth(text + "...") / 1000.0f) * fontSize;
		}

		return  (txt.equals(text))? text : text + "..." ;
	}	
		
	//REESCALE
	public static float[] reescale(float width, float height, float maxWidth, float maxHeight) {
		
		float rel = width / height;
		
		while(width > maxWidth || height > maxHeight) {
			width--;
			height = width / rel;
		}
		
		return new float[] {width,height};
	}
	
	//BYTE ARRAY TO IMAGE
	public static BufferedImage create_image_from_bytes(byte[] imageData) {
	    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
	    try {return ImageIO.read(bais);} 
	    catch (IOException e) {}
		return null;
	}

}

