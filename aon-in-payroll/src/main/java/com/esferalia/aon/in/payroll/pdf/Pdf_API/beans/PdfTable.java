package com.esferalia.aon.in.payroll.pdf.Pdf_API.beans;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.WHITE;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT.CENTER;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT;

public class PdfTable extends PdfComponent{
	
	private int columns;
	private String[] headers;
	private TEXT_ALIGNMENT[] alignments;
	private float[] pixels;
	private float[] sizes;
	private float width;
	private float cell_height;
	
	public PDFont font;
	public float fontsize;
	public Color text_color;
	public Color header_color;
	public Color header_text_color;
	
	public PdfTable(float x, float y, PDPageContentStream stream, float width, float cell_height, float spacing, float[] sizes, String[] headers) {
		super(x, y, width, 0, 0, 0, stream);
		this.columns = sizes.length;
		this.headers = headers;
		this.sizes = sizes;
		this.cell_height = cell_height;
		this.width = width;
		calculate_pixels(spacing);
		
		font = HELVETICA;
		fontsize = 10;
		text_color = BLACK;
		header_color = BLACK;
		header_text_color = WHITE;
		alignments = new TEXT_ALIGNMENT[columns];
	}

	private void calculate_pixels(float spacing) {
		
		pixels = new float[columns];
		float d = this.x;
		
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = d;
			d += spacing + width / (100 / sizes[i]) ;
			
		}
	}

	public void set_alignment(TEXT_ALIGNMENT[] alignments) {
		
		for (int i = 0; i < columns; i++) {
			try{
				this.alignments[i] = alignments[i];
			}catch(Exception e) {}
		}
	}

	@Override
	public void draw() {System.out.println("PdfAPI.autotable : METHOD DRAW(): Not compatible yet.");}

	public void draw_header() throws IOException {
		
		for (int i = 0; i < columns; i++) {
			
			String text_content = (headers.length <= i)? "": headers[i];
			TEXT_ALIGNMENT align = (alignments[i] != null)? alignments[i] : CENTER;
			float height = ( font.getFontDescriptor().getCapHeight()) / 1000 * fontsize;
			
			
			
			PdfBox box = new PdfBox(pixels[i],y,width / (100 / sizes[i]),cell_height,header_color,this.stream);
			PdfText text = new PdfText(pixels[i], y, width / (100 / sizes[i]), cell_height, 5, (cell_height - height)/2, stream, text_content, header_text_color, font, fontsize, align);
			
			box.draw();
			text.draw();
		}
		
		y -= cell_height ; 
	}

	public void new_row(String[] text_bundle) throws IOException {
		
		for (int i = 0; i < pixels.length; i++) {
			
			TEXT_ALIGNMENT align = (alignments[i] != null && alignments.length >= i)? alignments[i] : CENTER;
			String text_content = (text_bundle.length <= i)? "" : text_bundle[i];
			float height = ( font.getFontDescriptor().getCapHeight()) / 1000 * fontsize;
			
			PdfText text = new PdfText(pixels[i], y, width / (100 / sizes[i]), cell_height, 5, (cell_height - height)/2, stream, text_content, text_color, font, fontsize, align);
			text.draw();
		}
		
		y -= cell_height; 		
	}
}
