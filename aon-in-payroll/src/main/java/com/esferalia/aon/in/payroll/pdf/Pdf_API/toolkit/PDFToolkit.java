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
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.jump;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.log_warning;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.slog;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.start_section;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.tb;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.untab;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import javax.imageio.ImageIO;

import java.util.List;

public class PDFToolkit {

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


	public static void drawImage(PDDocument doc, PDPageContentStream contents, byte[] logo, float x, float y) throws IOException {
		contents.drawImage(PDImageXObject.createFromByteArray(doc, logo, null), x, y);
	}

	public static void drawImage(PDDocument doc, PDPageContentStream contents, byte[] logo, float x, float y, float width, float height) throws IOException {
		contents.drawImage(PDImageXObject.createFromByteArray(doc, logo, null), x, y, width, height);
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
	@Deprecated
	public static List<PDField> get_form_fields(PDDocument doc) {
		PDDocumentCatalog pdCatalog = doc.getDocumentCatalog();
		PDAcroForm pdAcroForm = pdCatalog.getAcroForm();
		return pdAcroForm.getFields();
	}

	//GET FIELD RECTANGLE
	@Deprecated
	public static Optional<PDRectangle> get_field_rectangle(Optional<PDField> field, int widget_index) {
		if (field.isEmpty()) return Optional.empty();
		PDField f = field.get();
		if (widget_index >= f.getWidgets().size()) return Optional.empty();
		return Optional.of(f.getWidgets().get(widget_index).getRectangle());
	}

	//GET FIELD PAGE
	@Deprecated
	public static Optional<PDPage> get_field_page(Optional<PDField> field, int widget_index) {
		if (field.isEmpty()) return Optional.empty();
		PDField f = field.get();
		if (widget_index >= f.getWidgets().size()) return Optional.empty();
		return Optional.of(f.getWidgets().get(widget_index).getPage());
	}

	//OPEN PAGE IN APPEND MODE
	public static PDPageContentStream open_in_append_mode(PDDocument doc, PDPage page) {
		try {
			return new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
		} catch (IOException e) {
			return null;
		}
	}
		
	//PUZZLES ARE MY PASSION
	public static List<String> get_lines(String text, float max, PDFont font, float fontSize) throws  IOException{
		
		ArrayList<String> lines = new ArrayList<>();
		ArrayList<String> words = (ArrayList<String>) StringToolkit.to_words(text);
		String line = "";
		
		for (int i = 0; i < words.size(); i++) {
			float fw = (font.getStringWidth(line + " " + words.get(i)) / 1000.0f) * fontSize;
			if(fw < max) {
				line += " " + words.get(i);
				if(i == words.size() - 1) lines.add(line); 
			}
			else {
				lines.add(line);
				line = "" + words.get(i);
			}
		}
		if(lines.size() == 0) lines.add(line);
		
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

	//------------HELP INFO------------
	public static void help() {
		jump(1);

		start_section("Pdf Toolkit: 									General PDF API core.");
		slog("This toolkit contains the basic methods for PDF printing");
		slog("and pdfbox abstractions.");
		jump(1);
		log_warning("To create PDF elements use PdfAPI beans");
		slog("they are easier to implement and use and give you advanced methods by default.");
		jump(2);
		
		slog("Available methods");
		slog("-----------------------------------------------------------------------------------------------------------------------------------------------");
		slog("create_horizontal_page()" 	+ tb(4) + "Creates an horizontal page. (use PdfPage instead)");
		slog("create_vertical_page()" 		+ tb(5) + "Creates an vertical page. (use PdfPage instead)");		
		slog("drawText()" 					+ tb(7) + "Draws a text. (use PdfText instead)");		
		slog("drawTextRight()" 				+ tb(6) + "Draws a right aligned text. (use PdfText instead)");		
		slog("drawTextLeft()" 				+ tb(6) + "Draws a left aligned text. (use PdfText instead)");		
		slog("drawTextCenter()" 			+ tb(6) + "Draws a center aligned text. (use PdfText instead)");		
		slog("drawImage()" 					+ tb(7) + "Draws an image. (use PdfImage instead)");		
		slog("drawBox()" 					+ tb(7) + "Draws a box. (use PdfBox instead)");		
		slog("drawImage()" 					+ tb(7) + "Draws a bordered box. (use PdfBox instead)");		
		slog("open_in_append_mode()" 		+ tb(4) + "Opens a PDF file in append mode.");	
		slog("get_lines()" 					+ tb(7) + "Divides a String in lines based in font, max-width and font size.");	
		slog("cropped_string()" 			+ tb(6) + "Crops a String based in font, max-width and font size.");	
		slog("reescale()" 					+ tb(7) + "Scales an image maintaining aspect ratio. (use PdfImage instead)");	
		slog("create_image_from_bytes()" 	+ tb(3) + "Create BufferedImage from byte array.");	
		
		jump(1);
		log_warning("Discouraged methods: ");
		slog("-----------------------------------------------------------------------------------------------------------------------------------------------");
		slog("get_form_fields()");
		slog("get_form_rectangle()");
		slog("get_form_page()");
		
		
		jump(1);
		untab();
	
	}	
}


