package com.esferalia.aon.in.payroll.pdf.Pdf_API.beans;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;

public abstract class PdfComponent {

	protected float x;
	protected float y;
	
	protected float width;
	protected float height;
	
	protected float margin_x;
	protected float margin_y;
	
	protected PDPageContentStream stream;
	abstract void draw();
	
	public PdfComponent(float x, float y, float width, float height, float margin_x, float margin_y,
			PDPageContentStream stream) {
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

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getMargin_x() {
		return margin_x;
	}

	public void setMargin_x(float margin_x) {
		this.margin_x = margin_x;
	}

	public float getMargin_y() {
		return margin_y;
	}

	public void setMargin_y(float margin_y) {
		this.margin_y = margin_y;
	}

	public PDPageContentStream getStream() {
		return stream;
	}

	public void setStream(PDPageContentStream stream) {
		this.stream = stream;
	}
	
	
	//------------HELP INFO------------
		public static String describe() {
			String info = "PdfComponent:\t\t\tAbstract component father of most beans.";
			return info;
		}	
}
