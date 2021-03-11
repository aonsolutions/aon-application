package com.esferalia.aon.in.payroll.pdf.Pdf_API.beans;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;

public class PdfText extends PdfComponent{

	private String content;
	private Color  color;
	private PDFont font;
	private Float font_size;
	private ALIGNMENT alignment;
	private List<String> lines;
	
	public PdfText(float x, float y, float width, float height, float margin_x, float margin_y, PDPageContentStream stream, String content, Color color, PDFont font, Float font_size,ALIGNMENT alignment) {
		super(x, y, width, height, margin_x, margin_y, stream);
		this.content = content;
		this.color = color;
		this.font = font;
		this.font_size = font_size;
		this.alignment = alignment;
		get_lines();
	}

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
		this.y = y;
		get_lines();
	}
	
	public PdfText(float x, float y, float margin_x, float width, float height, PDPageContentStream stream, String content, Color color, PDFont font, Float font_size, ALIGNMENT alignment) {
		super(x, y, width, height, margin_x, 0, stream);
		
		float fh = (font.getFontDescriptor().getCapHeight()) / 1000 * font_size;
		
		this.content = content;
		this.color = color;
		this.font = font;
		this.font_size = font_size;
		this.alignment = alignment;
		this.margin_y = (height - fh)/2;
		this.y = y;
		get_lines();
	}

	public PdfText(float width, float height, float margin_x, float margin_y, String content, PdfFile file, boolean primary, ALIGNMENT alignment) {
		super(file.x, file.y, width, height, margin_x, margin_y, file.contents);

		this.content = content;
		this.color = (primary) ? file.primary : file.secondary;
		this.font = file.font;
		this.font_size = file.fontsize;
		this.alignment = alignment;
		this.y = file.y;
		get_lines();
	}
	
	public PdfText(float width, float height, float margin_x, String content, PdfFile file, boolean primary, ALIGNMENT alignment) {
		super(file.x, file.y, width, height, margin_x, 0, file.contents);
		float fh = (font.getFontDescriptor().getCapHeight()) / 1000 * font_size;
		
		this.content = content;
		this.color = (primary) ? file.primary : file.secondary;
		this.font = file.font;
		this.font_size = file.fontsize;
		this.alignment = alignment;
		this.margin_y = (height - fh*2)/2;
		this.y = file.y;
		get_lines();
	}

	private void get_lines() {
		try {
			this.lines = PDFToolkit.get_lines(content, width - margin_x, font, font_size);
		} catch (IOException e) {e.printStackTrace();
		}
	}
	
	@Override
	public void draw() {
		try {
			float fh = (font.getFontDescriptor().getCapHeight()) / 1000 * font_size;
			for (String line : lines) {
				switch (alignment) {
					case CENTER: 	
						PDFToolkit.drawTextCenter(stream, new PDRectangle(x, y, width, height), line, color, font, font_size, margin_y);
					
					break;
					
					case RIGHT:		
						PDFToolkit.drawTextRight(stream, new PDRectangle(x, y, width, height), line, color, font, font_size, margin_x, margin_y);
					break;
					
					default: 		
						PDFToolkit.drawTextLeft(stream, new PDRectangle(x, y, width, height), line, color, font, font_size, margin_x, margin_y);
					break;
				}	
				y -= fh + 4;
				height += fh + 4;
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

	public ALIGNMENT getAlignment() {
		return alignment;
	}

	public void setAlignment(ALIGNMENT alignment) {
		this.alignment = alignment;
	}	
	
	//HELP INFO
	public static String describe() {
		String info = "PdfText:\t\t\t\t\tNormal text.";
		return info;
	}
}

