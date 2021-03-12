package com.esferalia.aon.in.payroll.pdf.Pdf_API.beans;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;
/**
 * <p><b>Description:</b> <i>This class represents a box in the PDF file. </i></p>
 * @author akrck02
 * @version 0.2-AK
 */
public class PdfBox {

	private float x, y;
	private float width, height;
	private Color color;
	private PDPageContentStream stream;
	
	/**
	 * <p><b>Description:</b> <i>The constructor. </i></p>
	 * @param x 
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param stream the PDPageContentStream
	 */
	public PdfBox(float x, float y, float width, float height, Color color, PDPageContentStream stream) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
		this.stream = stream;
	}
		
	/**
	 * <p><b>Description:</b> <i>Draws the box. </i></p>
	 */
	public void draw() throws IOException {
		PDFToolkit.drawBox(stream, x, y, width, height, color);
	}
	
	/**
	 * <p><b>Description:</b> <i>Draws the box  border. </i></p>
	 */
	public void border() throws IOException {
		PDFToolkit.drawBorderedBox(stream, x, y, width, height, color);
	}
	
	/**
	 * <p><b>Description:</b> <i>Draws the box  border. </i></p>
	 */
	public void border(Color color) throws IOException {
		PDFToolkit.drawBorderedBox(stream, x, y, width, height, color);
	}
	
	//------------HELP INFO------------
	public static String describe() {
		String info = "PdfBox: \t\t\t\t\tImaginary box.";
		return info;
	}	
}
