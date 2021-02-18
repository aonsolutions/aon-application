package com.esferalia.aon.in.payroll.pdf.Pdf_API.beans;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.GRAY;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.WHITE;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT.CENTER;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT;

public class PdfTable extends PdfComponent{
	
	private int columns;
	
	private String[] headers;
	private TEXT_ALIGNMENT[] alignments;
	private float[] pixels;
	private float[] sizes;
	private Object[] cells;
	public Color[] colors;
	
	public float width;
	public float cell_height;
	public float header_height;
	
	public PDFont font;
	public float fontsize;
	public float header_fontsize;
	public Color text_color;
	public Color header_color;
	public Color header_text_color;
	
	public PdfTable(float x, float y, PDPageContentStream stream, float width, float cell_height, float spacing, float[] sizes, String[] headers) {
		super(x, y, width, 0, 0, 0, stream);
		this.columns = sizes.length;
		this.headers = headers;
		this.sizes = sizes;
		this.cell_height = cell_height;
		this.header_height = cell_height;
		this.width = width;
		
		calculate_pixels(spacing);
		cells = new Object[columns];
		colors = new Color[columns];
		
		font = HELVETICA;
		fontsize = 10;
		header_fontsize = fontsize;
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
			try{this.alignments[i] = alignments[i];}
			catch(Exception e) {}
		}
	}

	@Override
	public void draw() {System.out.println("PdfAPI.autotable : METHOD DRAW(): Not compatible yet.");}

	public void draw_header() throws IOException {
		
		for (int i = 0; i < columns; i++) {
			String text_content = (headers.length <= i)? "": headers[i];
			TEXT_ALIGNMENT align = (alignments[i] != null)? alignments[i] : CENTER;
			float height = ( font.getFontDescriptor().getCapHeight()) / 1000 * header_fontsize;
			
			PdfBox box = new PdfBox(pixels[i],y,width / (100 / sizes[i]),header_height,header_color,this.stream);
			PdfText text = new PdfText(pixels[i], y, width / (100 / sizes[i]), header_height, 5, (header_height - height)/2, stream, text_content, header_text_color, font, header_fontsize, align);
			
			box.draw();
			text.draw();
		}
		y -= header_height ; 
	}
	
	public void draw_line() throws IOException {
		PdfBox box = new PdfBox(pixels[0]+5,y+5,width,.2f,GRAY,this.stream);
		box.draw();
	}

	public void create_box(int start,int end, int rows) throws IOException {
		
		jump(cell_height);
		if(start < 0 ) start = 0;
		if(start >= columns) start = columns - 1;
		if(end < 0) end = 0;
		if(end >= columns) end = columns -1;
	
		if(start > end) {
			start += end;
			end = start - end;
			start -= end; 
		}
		
		PdfBox box = new PdfBox(pixels[start],y - (header_height*rows/2),pixels[end] - pixels[start] + (width / (100 / sizes[end])),header_height*rows,header_color,this.stream);
		box.draw();
		
		
	}
	
	public void new_row(String[] text_bundle) throws IOException {
		
		for (int i = 0; i < pixels.length; i++) {
			TEXT_ALIGNMENT align = (alignments[i] != null && alignments.length >= i)? alignments[i] : CENTER;
			String text_content = (text_bundle.length <= i)? "" : text_bundle[i];
			float height = ( font.getFontDescriptor().getCapHeight()) / 1000 * fontsize;
			
			PdfText text = new PdfText(pixels[i], y, width / (100 / sizes[i]), cell_height, 5, (cell_height - height)/2, stream, text_content, (colors[i] == null)? text_color : colors[i], font, fontsize, align);
			text.draw();
		}
		y -= cell_height; 		
	}
	
	public void new_row() throws IOException {
							
		for (int i = 0; i < pixels.length; i++) {
			TEXT_ALIGNMENT align = (alignments[i] != null && alignments.length >= i)? alignments[i] : CENTER;
			String text_content = (cells[i] == null)? "" : cells[i].toString();
			float height = ( font.getFontDescriptor().getCapHeight()) / 1000 * fontsize;
			
			PdfText text = new PdfText(pixels[i], y, width / (100 / sizes[i]), cell_height, 5, (cell_height - height)/2, stream, text_content,(colors[i] == null)? text_color : colors[i], font, fontsize, align);
			text.draw();
		}
		
		colors = new Color[columns];
		
		y -= cell_height; 	
	}
	
	public boolean add_to_cell(int cell,Object o){
		if(cell >= columns || cell < 0) return false;
			
		cells[cell] = o;
		return true;
	}
	
	public boolean pain_cell(int cell,Color c){
		if(cell >= columns || cell < 0) return false;
			
		colors[cell] = c;
		return true;
	}
	
	public int get_column(String name) {
		for (int i = 0; i < headers.length; i++) 
			if(headers[i].equalsIgnoreCase(name))
				return i;
		return -1;
	}
	
	public void jump(float pixels) {
		y -= pixels;
	}

	@Override
	public String toString() {
		return "PdfTable :\t\n{ \n\tcolumns: \t\t" + columns + ", \n\theaders: \t\t" + Arrays.toString(headers)
				+ ", \n\talignments: \t\t" + Arrays.toString(alignments) + ", \n\tpixels: \t\t"
				+ Arrays.toString(pixels) + ", \n\tsizes: \t\t" + Arrays.toString(sizes) + ", \n\twidth: \t\t" + width
				+ ", \n\tcell_height: \t\t" + cell_height + ", \n\tcells: \t\t" + Arrays.toString(cells)
				+ ", \n\tfont: \t\t" + font + ", \n\tfontsize: \t\t" + fontsize + ", \n\ttext_color: \t\t" + text_color
				+ ", \n\theader_color: \t\t" + header_color + ", \n\theader_text_color: \t\t" + header_text_color
				+ ", \n\tx: \t\t" + x + ", \n\ty: \t\t" + y + ", \n\theight: \t\t" + height + ", \n\tmargin_x: \t\t"
				+ margin_x + ", \n\tmargin_y: \t\t" + margin_y + ", \n\tstream: \t\t" + stream + "\n}";
	}	
}
