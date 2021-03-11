package com.esferalia.aon.in.payroll.pdf.Pdf_API.beans;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;

public class PdfImage extends PdfComponent{

	private PDDocument doc;
	private byte[] img;

	public PdfImage(float x, float y, float width, float height, float margin_x, float margin_y,PDPageContentStream stream, PDDocument doc, byte[] img) {
		super(x, y, width, height, margin_x, margin_y, stream);
		this.doc = doc;
		this.img = img;
	}

	@Override
	public void draw() {
		try {PDFToolkit.drawImage(doc, stream, img, x, y,width,height);} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	public PdfImage scale(float max_width, float max_height, ALIGNMENT align) {
		BufferedImage buff = PDFToolkit.create_image_from_bytes(img);
		
		System.out.println("HEIGHT : " + buff.getHeight() + "px");
		System.out.println("WIDTH : " + buff.getWidth() + "px");
		System.out.println();
		
		float[] sizes = PDFToolkit.reescale(buff.getWidth(), buff.getHeight(), max_width, max_height);		
		
		this.width = sizes[0];
		this.height = sizes[1];
		
		System.out.println("WIDTH : " + width + "px");
		System.out.println("HEIGHT : " + height + "px");
		
		switch (align) {
			case CENTER: 	
				this.x += max_width/2 - width/2;
				break;
			case RIGHT:  
				this.x += max_width - width;	
				break;
			default: break;
		}
		
		if(buff.getHeight() < max_height) y += max_height/2 - height/2;
	//	System.out.println("Y: " + y);
		return this;
	}

	public PDDocument getDoc() {
		return doc;
	}

	public void setDoc(PDDocument doc) {
		this.doc = doc;
	}

	public byte[] getImg() {
		return img;
	}

	public void setImg(byte[] img) {
		this.img = img;
	}
	
}
