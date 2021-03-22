package com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfBox;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfImage;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfPage;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfText;

/**
 * <p><b>Description:</b> <i>This toolkit contains the basic methods for PDF printing and pdfbox abstractions.</i></p>
 * @author akrck02
 * @warning To create PDF elements use PdfAPI beans they are easier to implement and use and give you advanced methods by default.
 */
public class PDFToolkit {

	/**
	 * <p><b>Description:</b> <i>Creates an horizontal dinA4 page.</i></p>
	 * <p><b>warning:</b> Use PdfPage instead</p>
	 * @return PDPage page
	 * @see PdfPage
	 */
	public static PDPage createHorizontalPage() {
		float POINTS_PER_INCH = 72;
		float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;
		return new PDPage(new PDRectangle(297 * POINTS_PER_MM, 210 * POINTS_PER_MM));
	}
	
	/** 
	 * <p><b>Description:</b> <i>Creates an vertical dinA4 page.</i></p>
	 * <p><b>Warning:</b> Use PdfPage instead</p>
	 * @return PDPage page
	 * @see PdfPage
	 */
	public static PDPage createVerticalPage() {
		float POINTS_PER_INCH = 72;
		float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;
		return new PDPage(new PDRectangle(210 * POINTS_PER_MM, 297 * POINTS_PER_MM));
	}

	/**
	 * <p><b>Description:</b> <i>Draws a text. </i></p>
	 * <p><b>Warning:</b> Use PdfText instead</p>
	 * @return void
	 * @see PdfText
	 */
	public static void drawText(PDPageContentStream contents, String content, Float x, Float y, Color color, PDFont font, float fontSize) throws IOException {
		contents.setNonStrokingColor(color);
		contents.beginText();
		contents.setFont(font, fontSize);
		contents.newLineAtOffset(x, y);
		contents.showText(content);
		contents.endText();
	}
	
	/**
	 * <p><b>Description:</b> <i>Draws a justified text. </i></p>
	 * <p><b>Warning:</b> Use PdfText instead</p>
	 * @return void
	 * @see PdfText
	 */
	public static void drawTextJustified(ArrayList<String> lines,float max, float fontSize, PDFont font,float x,float y,Color color,PDPageContentStream stream) throws IOException {
		for (int i = 0; i < lines.size()-1; i++, y -= 10) drawTextJustified(lines.get(i), max, fontSize, font, x, y, stream); 
	}

	/**
	 * <p><b>Description:</b> <i>Draws a justified line. </i></p>
	 * <p><b>Warning:</b> Use PdfText instead</p>
	 * @return void
	 * @see PdfText
	 */
	public static void drawTextJustified(String line,float max,float fontSize,PDFont font, float x, float y, PDPageContentStream stream) throws IOException {

		stream.beginText();
		float charSpacing = 0;
		if (line.length() > 1){
			float size = fontSize * font.getStringWidth(line) / 1000;
			float free = max - size;
			if (free > 0)	charSpacing = free / (line.length() - 1);
		}
		
		stream.setFont(font, fontSize);
		stream.newLineAtOffset(x, y);
		stream.setCharacterSpacing(charSpacing);
		stream.showText(line);
		stream.endText();
	}
	
	/**
	 * <p><b>Description:</b> <i>Get the width of a text according to font and font size. </i></p>
	 * @return void
	 */
	public static float font_width(List<String> texts ,float fontSize, PDFont font ) throws IOException {
		float fw = 0;
		for(String text : texts) fw += font_width(text, fontSize, font);
		return fw;			
	}
	
	/**
	 * <p><b>Description:</b> <i>Get the width of a text according to font and font size. </i></p>
	 * @return void
	 */
	public static float font_width(String string ,float fontSize, PDFont font ) throws IOException 
	{return (font.getStringWidth(string) / 1000.0f) * fontSize;}

	/**
	 * <p><b>Description:</b> <i>Draws a right aligned text. </i></p>
	 * <p><b>Warning:</b> Use PdfText instead</p>
	 * @return void
	 * @see PdfText
	 */
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
	
	/**
	 * <p><b>Description:</b> <i>Draws a left aligned text. </i></p>
	 * <p><b>Warning:</b> Use PdfText instead.</p>
	 * @return void
	 * @see PdfText
	 */
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

	/**
	 * <p><b>Description:</b> <i>Draws a center aligned text. </i></p>
	 * <p><b>Warning:</b> Use PdfText instead.</p>
	 * @return void
	 * @see PdfText
	 */
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

	/**
	 * <p><b>Description:</b> <i>Draws an image in natural size.</i></p>
	 * <p><b>warning:</b> Use PdfImage instead</p>
	 * @return void
	 * @see PdfImage
	 */
	public static void drawImage(PDDocument doc, PDPageContentStream contents, byte[] logo, float x, float y) throws IOException {
		contents.drawImage(PDImageXObject.createFromByteArray(doc, logo, null), x, y);
	}

	/**
	 * <p><b>Description:</b> <i>Draws an image.</i></p>
	 * <p><b>warning:</b> Use PdfImage instead</p>
	 * @return void
	 * @see PdfImage
	 */
	public static void drawImage(PDDocument doc, PDPageContentStream contents, byte[] logo, float x, float y, float width, float height) throws IOException {
		contents.drawImage(PDImageXObject.createFromByteArray(doc, logo, null), x, y, width, height);
	}
	
	/**
	 * <p><b>Description:</b> <i>Draws a box.</i></p>
	 * <p><b>warning:</b> Use PdfBox instead</p>
	 * @return void
	 * @see PdfBox
	 */
	public static void drawBox(PDPageContentStream contents, float x, float y, float width, float height, Color color) throws IOException {
		contents.setNonStrokingColor(color);
		contents.addRect(x, y, width, height);
		contents.fill();
	}
	
	/**
	 * <p><b>Description:</b> <i>Draws a bordered box.</i></p>
	 * <p><b>warning:</b> Use PdfBox instead</p>
	 * @return void
	 * @see PdfBox
	 */
	public static void drawBorderedBox(PDPageContentStream contents, float x, float y, float width, float height, Color color) throws IOException {
		contents.setStrokingColor(color);
		contents.addRect(x, y, width, height);
		contents.setLineWidth(2);
		contents.stroke();
	}
	
	/**
	 * <p><b>Description:</b> <i>Gets fields of a form.</i></p>
	 * @return List of PDFields
	 * @deprecated this method is no longer in use.
	 */
	@Deprecated
	public static List<PDField> get_form_fields(PDDocument doc) {
		PDDocumentCatalog pdCatalog = doc.getDocumentCatalog();
		PDAcroForm pdAcroForm = pdCatalog.getAcroForm();
		return pdAcroForm.getFields();
	}

	/**
	 * <p><b>Description:</b> <i> Gets a box of a form field.</i></p>
	 * @return PDRectangle (Optional)
	 * @deprecated this method is no longer in use.
	 */
	@Deprecated
	public static Optional<PDRectangle> get_field_rectangle(Optional<PDField> field, int widget_index) {
		if (field.isEmpty()) return Optional.empty();
		PDField f = field.get();
		if (widget_index >= f.getWidgets().size()) return Optional.empty();
		return Optional.of(f.getWidgets().get(widget_index).getRectangle());
	}

	/**
	 * <p><b>Description:</b> <i> Gets the page of a form field.</i></p>
	 * @return PDPage (Optional)
	 * @deprecated this method is no longer in use.
	 */
	@Deprecated
	public static Optional<PDPage> get_field_page(Optional<PDField> field, int widget_index) {
		if (field.isEmpty()) return Optional.empty();
		PDField f = field.get();
		if (widget_index >= f.getWidgets().size()) return Optional.empty();
		return Optional.of(f.getWidgets().get(widget_index).getPage());
	}

	/**
	 * <p><b>Description:</b> <i>Open a PDF file in append mode.</i></p>
	 * @return PDPageContentStream (the pdf file stream)
	 */
	public static PDPageContentStream open_in_append_mode(PDDocument doc, PDPage page) {
		try {return new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);} 
		catch (IOException e) {return null;}
	}
	
	/**
	 * <p><b>Description:</b> <i> Gets lines of an String.</i></p>
	 * <p><b>warning:</b> Use get_lines() instead</p>
	 * @return List of Strings
	 * @deprecated this method is no longer in use.
	 * @see PDFToolkit.get_lines()
	 */
	@Deprecated
	public static List<String> divide_string_to_fit(String text, float max, PDFont font, float fontSize) throws IOException{return get_lines(text, max, font, fontSize);}
	
	/**
	 * <p><b>Description:</b> <i> Gets lines of an String based on max width, font size, and font. <q>puzzles are my passion</q><i></p>
	 * @param max  &nbsp; maximum width
	 * @return List of Strings
	 */
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
	
	/**
	 * <p><b>Description:</b> <i>Crops a String based on max width, font size, and font.<i></p>
	 * @param max  &nbsp; maximum width
	 * @return String
	 */
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
		
	/**
	 * <p><b>Description:</b> <i> Reescale an image.</i></p>
	 * <p><b>warning:</b> Use PdfImage instead</p>
	 * @return float array 
	 * <h4>Positions: </h4>
	 * <ol start='0'>
	 * <li>scaled width</li>
	 * <li>scaled height</li>
	 * </ol>
	 * @see PdfImage
	 */
	public static float[] reescale(float width, float height, float maxWidth, float maxHeight) {
		
		float rel = width / height;
		
		while(width > maxWidth || height > maxHeight) {
			width--;
			height = width / rel;
		}
		
		return new float[] {width,height};
	}
	
	/**
	 * <p><b>Description:</b> <i> Create a BufferedImage from byte array.</i></p>
	 * @param  imageData byte array
	 * @return BufferedImage
	 */
	public static BufferedImage create_image_from_bytes(byte[] imageData) {
	    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
	    try {return ImageIO.read(bais);} 
	    catch (IOException e) {}
		return null;
	}

}


