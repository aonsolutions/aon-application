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
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.List;

public class PDFToolkit {

	public final static Color AON_BLUE = new Color(0x3a5b9e);
	public final static PDFont HELVETICA = PDType1Font.HELVETICA;
	public final static PDFont HELVETICA_BOLD = PDType1Font.HELVETICA_BOLD;

	public final static Color WHITE = new Color(0xffffff);
	public final static Color BLACK = new Color(0x000000);
	public final static Color BLUE = new Color(0x3a5b9e);
	public final static Color RED = new Color(0xf44336);
	public final static Color GREEN = new Color(0x228b22);
	public final static Color LIGHT_GRAY = new Color(0xf8f8f8);

	public final static DecimalFormat df = new DecimalFormat("0.00");

	//CREATE AN HORIZONTAL PAGE
	public static PDPage createHorizontalPage() {
		float POINTS_PER_INCH = 72;
		float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;
		return new PDPage(new PDRectangle(297 * POINTS_PER_MM, 210 * POINTS_PER_MM));
	}

	public static String format(double num) {
		return df.format(num);
	}

	//CREATE AN HORIZONTAL PAGE
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

	//DRAWS A RIGHT ALIGN TEXT
	public static void drawTextRight(PDPageContentStream contents, PDRectangle box, String content, Color color, PDFont font, float fontSize, float x_margin, float y_margin) throws IOException {
		float h = box.getHeight();
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

	//DRAWS A CENTERED TEXT
	public static void drawTextCenter(PDPageContentStream contents, PDRectangle box, String content, Color
			color, PDFont font, float fontSize, float y_margin) throws IOException {

		float h = box.getHeight();
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

	public static void drawImage(PDDocument doc, PDPageContentStream contents, byte[] logo, float width, float height) throws IOException {
		contents.drawImage(PDImageXObject.createFromByteArray(doc, logo, null), 20, 535, width, height);
	}
	public static void drawImage(PDDocument doc, PDPageContentStream contents, byte[] logo, float x, float y, float width, float height) throws IOException {
		contents.drawImage(PDImageXObject.createFromByteArray(doc, logo, null), x, y, width, height);
	}

	public static void drawImage(PDDocument doc, PDPageContentStream contents, InputStream logo, float x, float y, float width, float height) throws IOException {
		contents.drawImage(PDImageXObject.createFromByteArray(doc, logo.readAllBytes(), "image"), x, y, width, height);
	}

	//DRAW A BOX
	public static void drawBox(PDPageContentStream contents, float x, float y, float width, float height, Color color) throws IOException {
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

}
