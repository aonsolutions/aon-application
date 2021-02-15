package com.esferalia.aon.in.payroll.pdf.Pdf_API.beans;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;

public class PdfText extends PdfComponent{

	public String content;
	public Color  color;
	public PDFont font;
	public Float font_size;
	public TEXT_ALIGNMENT alignment;
	
	public PdfText(float x, float y, float width, float height, float margin_x, float margin_y,
			PDPageContentStream stream, String content, Color color, PDFont font, Float font_size,
			TEXT_ALIGNMENT alignment) {
		super(x, y, width, height, margin_x, margin_y, stream);
		this.content = content;
		this.color = color;
		this.font = font;
		this.font_size = font_size;
		this.alignment = alignment;
	}

	@Override
	public void draw() {
		try {
			
			switch (alignment) {
				case CENTER: 	PDFToolkit.drawTextCenter(stream, new PDRectangle(x, y, width, height), content, color, font, font_size, margin_y);
				break;
				
				case RIGHT:		PDFToolkit.drawTextRight(stream, new PDRectangle(x, y, width, height), content, color, font, font_size, margin_x, margin_y);
				break;
				
				default: 		PDFToolkit.drawTextLeft(stream, new PDRectangle(x, y, width, height), content, color, font, font_size, margin_x, margin_y);
				break;
			}					
		} catch (IOException e) {e.printStackTrace();}
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public PDFont getFont() {
		return font;
	}

	public void setFont(PDFont font) {
		this.font = font;
	}

	public Float getFont_size() {
		return font_size;
	}

	public void setFont_size(Float font_size) {
		this.font_size = font_size;
	}

	public TEXT_ALIGNMENT getAlignment() {
		return alignment;
	}

	public void setAlignment(TEXT_ALIGNMENT alignment) {
		this.alignment = alignment;
	}	
	
	
	
}
