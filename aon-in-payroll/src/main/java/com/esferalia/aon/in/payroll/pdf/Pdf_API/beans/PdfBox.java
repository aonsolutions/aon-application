package com.esferalia.aon.in.payroll.pdf.Pdf_API.beans;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;

public class PdfBox {

	private float x, y;
	private float width, height;
	private Color color;
	private PDPageContentStream stream;
	
	public PdfBox(float x, float y, float width, float height, Color color, PDPageContentStream stream) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
		this.stream = stream;
	}
		
	public void draw() throws IOException {
		PDFToolkit.drawBox(stream, x, y, width, height, color);
	}
	
	public void border() throws IOException {
		PDFToolkit.drawBorderedBox(stream, x, y, width, height, color);
	}
	
	
}
