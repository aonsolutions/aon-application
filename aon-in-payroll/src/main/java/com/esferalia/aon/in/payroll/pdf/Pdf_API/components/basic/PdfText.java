package com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawTextCenter;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawTextLeft;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawTextRight;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;

/**
 * <p><b>Description:</b><i> This class represents a text.</i></p>
 * @version 0.2-AK
 * @author akrck02
 */
public class PdfText extends PdfComponent{

	private String content;
	private Color  color;
	private PDFont font;
	private Float font_size;
	private ALIGNMENT alignment;
	private List<String> lines;
	
	/**
	 * <p><b>Description:</b> <i>The constructor. (Complete) </i></p>
	 * @param x 
	 * @param y 
	 * @param width
	 * @param height
	 * @param margin_x
	 * @param margin_y
	 * @param stream	
	 * @param content	the text String
	 * @param color	
	 * @param font  		
	 * @param fontsize	
	 * @param alignment	
	 */
	public PdfText(float x, float y, float width, float height, float margin_x, float margin_y, PDPageContentStream stream, String content, Color color, PDFont font, Float font_size,ALIGNMENT alignment) {
		super(x, y, width, height, margin_x, margin_y, stream);
		this.content = content;
		this.color = color;
		this.font = font;
		this.font_size = font_size;
		this.alignment = alignment;
		get_lines();
	}

	/**
	 * <p><b>Description:</b> <i>The constructor. (No margin) </i></p>
	 * @param x 
	 * @param y 
	 * @param width
	 * @param height
	 * @param stream	
	 * @param content	the text String
	 * @param color	
	 * @param font  		
	 * @param fontsize	
	 * @param alignment	
	 */
	public PdfText(float x, float y, float width, float height, PDPageContentStream stream, String content, Color color, PDFont font, Float font_size, ALIGNMENT alignment) {
		super(x, y, width, height, 0, 0, stream);
		float fh = (font.getFontDescriptor().getCapHeight()) / 1000 * font_size;
		
		this.content = content;
		this.color = color;
		this.font = font;
		this.font_size = font_size;
		this.alignment = alignment;
		this.margin_x = 0;
		this.margin_y = (height - fh*2)/2;
		get_lines();
	}
	
	/**
	 * <p><b>Description:</b> <i>The constructor. (auto y margin) </i></p>
	 * @param x 
	 * @param y 
	 * @param margin_x
	 * @param width
	 * @param height
	 * @param stream	
	 * @param content	the text String
	 * @param color	
	 * @param font  		
	 * @param fontsize	
	 * @param alignment	
	 */
	public PdfText(float x, float y, float margin_x, float width, float height, PDPageContentStream stream, String content, Color color, PDFont font, Float font_size, ALIGNMENT alignment) {
		super(x, y, width, height, margin_x, 0, stream);
		
		float fh = (font.getFontDescriptor().getCapHeight()) / 1000 * font_size;
		this.content = content;
		this.color = color;
		this.font = font;
		this.font_size = font_size;
		this.alignment = alignment;
		this.margin_y = (height - fh)/2;
		get_lines();
	}
	
	private void get_lines() {
		try {this.lines = PDFToolkit.get_lines(content, width - margin_x, font, font_size);} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * <p><b>Description:</b> <i>Draws the text according to alignment</i></p>	
	 */
	@Override
	public void draw() {
		try {
			float fh = (font.getFontDescriptor().getCapHeight()) / 1000 * font_size;
			for (String line : lines) {
				switch (alignment) {
					case CENTER:	drawTextCenter(stream, new PDRectangle(x(), y(), width, height), line, color, font, font_size, margin_y);		   	break;
					case RIGHT: 	drawTextRight(stream, new PDRectangle(x(), y(), width, height), line, color, font, font_size, margin_x, margin_y); 	break;
					default: 		drawTextLeft(stream, new PDRectangle(x(), y(), width, height), line, color, font, font_size, margin_x, margin_y);	break;
				}	
				down(fh + 4);
				height += fh + 4;
			}
		} catch (IOException e) {e.printStackTrace();}
	}

	public String getContent() {return content;}
	public void setContent(String content) {this.content = content;}

	public Color getColor() {return color;}
	public void setColor(Color color) {this.color = color;}

	public PDFont getFont() {return font;}

	public void setFont(PDFont font) {this.font = font;}
	public Float getFont_size() {return font_size;}

	public void setFont_size(Float font_size) {this.font_size = font_size;}

	public ALIGNMENT getAlignment() {return alignment;}
	public void setAlignment(ALIGNMENT alignment) {this.alignment = alignment;}	
	
	//HELP INFO
	public static String describe() {return "PdfText:\t\t\t\t\tNormal text.";}
}

