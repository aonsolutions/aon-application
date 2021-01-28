package com.esferalia.aon.in.payroll.pdf.Pdf_API.beans;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;

public abstract class PdfComponent {

	public float x;
	public float y;
	
	public float width;
	public float height;
	
	public float margin_x;
	public float margin_y;
	
	public PDPageContentStream stream;
	
	public abstract void draw();
	
	public PdfComponent(float x, float y, float width, float height, float margin_x, float margin_y,
			PDPageContentStream stream) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.margin_x = margin_x;
		this.margin_y = margin_y;
		this.stream = stream;
	}

	public void square(Color box_color) {
		try {PDFToolkit.drawBox(stream, x, y, width, height, box_color);} 
		catch (IOException e) {e.printStackTrace();}
	}

	public void border(Color border_color) {
		try {PDFToolkit.drawBorderedBox(stream, x, y, width, height, border_color);} 
		catch (IOException e) {e.printStackTrace();}
	}
	

}
