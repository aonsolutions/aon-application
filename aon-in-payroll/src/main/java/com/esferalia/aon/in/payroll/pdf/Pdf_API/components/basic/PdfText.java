package com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT.JUSTIFY;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.VERTICAL_ALIGNMENT.DOWN;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.OptionalToolkit.safeFloat;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.OptionalToolkit.safeString;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.OptionalToolkit.safeValue;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawTextCenter;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawTextJustified;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawTextLeft;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawTextRight;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.VERTICAL_ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;

/**
 * <p>
 * <b>Description:</b><i> This class represents a text.</i>
 * </p>
 * 
 * @version 0.4-AK
 * @author akrck02
 */

public class PdfText extends PdfComponent {

	private String content;
	private Color color;
	private PDFont font;
	private Float font_size;

	private ALIGNMENT horizontal_alignment;
	private VERTICAL_ALIGNMENT vertical_alignment;

	private List<String> lines;
	private float line_spacing;

	private PdfText() {}
	private static PdfText instance() {return new PdfText();}

	@Deprecated
	public PdfText(float x, float y, float width, float height, PDPageContentStream stream, String content, Color color,
			PDFont font, Float font_size, ALIGNMENT alignment) {
		this.start_pointer();
		this.x(x);
		this.y(y);
		this.width(width);
		this.height(height);
		this.stream(stream);

		float fh = (font.getFontDescriptor().getCapHeight()) / 1000 * font_size;

		this.content = content;
		this.color = color;
		this.font = font;
		this.font_size = font_size;
		this.horizontal_alignment = alignment;
		this.margin_x(0);
		this.margin_y((height - fh) / 2);
		get_lines();
	}
	
	@Deprecated
	public PdfText(float x, float y, float width, float height, float margin_x, PDPageContentStream stream,
			String content, Color color, PDFont font, Float font_size, ALIGNMENT alignment) {
		this.start_pointer();
		this.x(x);
		this.y(y);
		this.width(width);
		this.height(height);
		this.stream(stream);

		float fh = (font.getFontDescriptor().getCapHeight()) / 1000 * font_size;

		this.content = content;
		this.color = color;
		this.font = font;
		this.font_size = font_size;
		this.horizontal_alignment = alignment;
		this.margin_x(margin_x);
		this.margin_y((height - fh) / 2);
		get_lines();
	}

	
	@Deprecated
	public PdfText(float x, float y, float width, float height, float margin_x, float margin_y, PDPageContentStream stream,
			String content, Color color, PDFont font, Float font_size, ALIGNMENT alignment) {
		this.start_pointer();
		this.x(x);
		this.y(y);
		this.width(width);
		this.height(height);
		this.stream(stream);
		this.content = content;
		this.color = color;
		this.font = font;
		this.font_size = font_size;
		this.horizontal_alignment = alignment;
		this.margin_x(margin_x);
		this.margin_y(margin_y);
		get_lines();
	}

	private void vertical_align_center() {
		float fh = (font.getFontDescriptor().getCapHeight()) / 1000 * font_size;
		this.margin_y((height() - fh) / 2);
	}
	
	private void get_lines() {
		try {
			this.lines = PDFToolkit.get_lines(content, width() - margin_x(), font, font_size);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Draws the text according to alignment</i><br>
	 * <b>Warning:</b> <i>to multiple line text use draw_multiple()</i>
	 * @see PdfText.draw_multiple()
	 * </p>
	 */
	@Override
	public void draw() {
		try {
			float fh = (font.getFontDescriptor().getCapHeight()) / 1000 * font_size;				
				String line = lines.get(0);
				if(lines.size() > 1) line = PDFToolkit.cropped_string(lines.get(0), width(), font, font_size()) + "...";

				line = line.trim();
				
				switch (horizontal_alignment) {
					case CENTER: 	drawTextCenter(stream(), new PDRectangle(x(), y(), width(), height()), line, color, font, font_size, margin_y()); 				break;
					case RIGHT:  	drawTextRight(stream(), new PDRectangle(x(), y(), width(), height()), line, color, font, font_size, margin_x(), margin_y()); 	break;
					case JUSTIFY:	drawTextJustified(line, width(), font_size, font, x(), y(), stream()); break;
					default: drawTextLeft(stream(), new PDRectangle(x(), y(), width(), height()), line, color, font, font_size,margin_x(), margin_y()); break;
				}
				
			down(fh + line_spacing);	
			height(height() + fh + line_spacing);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public int draw_multiple(float limit) {
		try {
			float fh = (font.getFontDescriptor().getCapHeight()) / 1000 * font_size;
			for (int i = 0; i < lines.size(); i++)  {
				if(y() - (fh + line_spacing) <= limit) return i;
				String line = lines.get(i);
				if(i != 0) line = " " + line;
				switch (horizontal_alignment) {
					case CENTER: 	drawTextCenter	(stream(), new PDRectangle(x(), y(), width(), height()), line, color, font, font_size, margin_y()); 				break;
					case RIGHT:  	drawTextRight	(stream(), new PDRectangle(x(), y(), width(), height()), line, color, font, font_size, margin_x(), margin_y()); 	break;
					case JUSTIFY:
						if(i < lines.size() -1) 	drawTextJustified	(line, width(), font_size, font, x(), y(), stream());
						else 						drawTextLeft		(stream(), new PDRectangle(x(), y(), width(), height()), line, color, font, font_size, 0,0);
						break;
					default:	drawTextLeft(stream(), new PDRectangle(x(), y(), width(), height()), line, color, font, font_size,margin_x(), margin_y()); break;
				}
				
			down(fh + line_spacing);
			height(height() + fh + line_spacing);
			}
			height(height() + fh + line_spacing);
		} catch (IOException e) {e.printStackTrace();}
		return -1;
	}
	
	public PdfText restart(PDPageContentStream contents, int line, float y) {
		stream(contents);
		lines = lines.subList(line,lines.size()-1);
	
		height(height() / line);
		y(y);
		return this;
	}

	public String  content() {return content;}
	public PdfText content(String content) {this.content = content; return this;}

	public Color   color() {return color;}
	public PdfText color(Color color) {this.color = color; return this;}

	public PDFont  font() {return font;}
	public PdfText font(PDFont font) {this.font = font; return this;}

	public Float font_size() {return font_size;}
	public PdfText font_size(Float font_size) {this.font_size = font_size; return this;}

	public Float line_spacing() {return this.line_spacing;}
	public PdfText line_spacing(Float line_spacing) {this.line_spacing = line_spacing; return this;}
	
	public ALIGNMENT horizontal_alignment() {return horizontal_alignment;}
	public PdfText horizontal_alignment(ALIGNMENT alignment) {this.horizontal_alignment = alignment; return this;}

	public VERTICAL_ALIGNMENT vertical_alignment() {return vertical_alignment;}
	public PdfText vertical_alignment(VERTICAL_ALIGNMENT alignment) {this.vertical_alignment = alignment; return this;}

	public static String describe() {return "PdfText:\t\t\t\t\tNormal text.";}
	
	
	//BUILDER
	public static class PdfTextBuilder {
		private float x;
		private float y;

		private float width;
		private float height;

		private float margin_x;
		private float margin_y;

		private PDPageContentStream stream;

		private String content;
		private Color color;
		private PDFont font;
		private Float font_size;

		private ALIGNMENT horizontal_alignment;
		private VERTICAL_ALIGNMENT vertical_alignment;

		private float line_spacing;

		public PdfTextBuilder x(float x) 										{this.x = x; return this;}
		public PdfTextBuilder y(float y) 										{this.y = y; return this;}

		public PdfTextBuilder width(float width)   								{this.width = width; return this;}
		public PdfTextBuilder height(float height) 								{this.height = height; return this;}

		public PdfTextBuilder margin_x(float margin_x) 							{this.margin_x = margin_x; return this;}
		public PdfTextBuilder margin_y(float margin_y) 							{this.margin_y = margin_y; return this;}

		public PdfTextBuilder stream(PDPageContentStream stream) 				{this.stream = stream; return this;}
		public PdfTextBuilder content(String content) 							{this.content = content;return this;}

		public PdfTextBuilder color(Color color) 								{this.color = color; return this;}
		public PdfTextBuilder font(PDFont font)  								{this.font = font; return this;}

		public PdfTextBuilder line_spacing(Float line_spacing) 					{this.line_spacing = line_spacing; return this;}
		public PdfTextBuilder font_size(Float font_size) 						{this.font_size = font_size; return this;}
		
		public PdfTextBuilder horizontal_alignment(ALIGNMENT alignment) 		{this.horizontal_alignment = alignment; return this;}
		public PdfTextBuilder vertical_alignment(VERTICAL_ALIGNMENT alignment) 	{this.vertical_alignment = alignment; return this;}
		
		public PdfText build() {
			PdfText component = instance();
			if(this.stream == null) 		return null;
			
			component.start_pointer();
			component.x						(safeFloat(this.x, 0f));
			component.y						(safeFloat(this.y, 0f));
			component.width					(safeFloat(this.width, 0f));
			component.height				(safeFloat(this.height, 0f));
			component.margin_x				(safeFloat(this.margin_x, 0f));
			component.margin_y				(safeFloat(this.margin_y, 0f));
			component.stream				(this.stream);
			component.content				(safeString(this.content,""));
			component.color					((Color)  safeValue (this.color, BLACK));
			component.font					((PDFont) safeValue (this.font, HELVETICA));
			component.font_size				(safeFloat(this.font_size, 10f));
			component.line_spacing			(safeFloat(this.line_spacing, 4f));
			component.horizontal_alignment	((ALIGNMENT)  safeValue (this.horizontal_alignment, JUSTIFY));
			component.vertical_alignment	((VERTICAL_ALIGNMENT)  safeValue (this.vertical_alignment, DOWN));
			
			component.get_lines();
			component.vertical_align_center();
			
			return component;
		}
	}

}
