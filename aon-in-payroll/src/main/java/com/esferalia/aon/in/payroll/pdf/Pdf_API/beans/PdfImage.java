package com.esferalia.aon.in.payroll.pdf.Pdf_API.beans;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.create_image_from_bytes;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;


/**
 * <p><b>Description:</b><i>This class represents an image.</i></p>
 * @see PdfComponent
 * @version 1.1-AK
 * @author akrck02
 */
public class PdfImage extends PdfComponent{

	private PDDocument doc;
	private byte[] img;
		
	/**
	 * <h1>This is the recommended image constructor! (Automatic, safe)</h1>
	 * <p><b>Description:</b> <i>The constructor (Autoscale). </i></p>
	 * @param x 
	 * @param y 
	 * @param width
	 * @param height
	 * @param align 	the alignment of the image scaled
	 * @param stream	
	 * @param doc	
	 * @param img  		the image byte array
	 */
	public PdfImage(float x, float y, float width, float height, ALIGNMENT align,PDPageContentStream stream, PDDocument doc, byte[] img) {
		super(x, y, width, height, 0, 0, stream);		
		this.doc = doc;
		this.img = img;
		this.scale(width, height, align);
	} 
		
	/**
	 * <h1>This is the recommended image constructor! (Automatic, safe)</h1>
	 * <p><b>Description:</b> <i>The constructor (Autoscale). </i></p>
	 * @param x 
	 * @param y 
	 * @param width
	 * @param height
	 * @param align 	the alignment of the image scaled
	 * @param stream	
	 * @param doc	
	 * @param img  		the image byte array
	 */
	public PdfImage(float x, float y, float width, float height, ALIGNMENT align,PDPageContentStream stream, PDDocument doc, InputStream img) {
		super(x, y, width, height, 0, 0, stream);		
		this.doc = doc;
		try {this.img = img.readAllBytes();}
		catch (IOException e) {e.printStackTrace();}
		this.scale(width, height, align);
	} 
	
	/**
	 * <p><b>Description:</b> <i>Draws the image. </i></p>
	 */
	@Override
	public void draw() {
		try {PDFToolkit.drawImage(doc, stream, img, x, y,width,height);} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * <p><b>Description:</b> <i>Scales the image. </i></p>
	 */
	public PdfImage scale(float max_width, float max_height, ALIGNMENT align) {
		BufferedImage buff = create_image_from_bytes(img);	
		float[] sizes = PDFToolkit.reescale(buff.getWidth(), buff.getHeight(), max_width, max_height);		
		
		this.width = sizes[0];
		this.height = sizes[1];
		
		switch (align) {
			case CENTER: 	
				this.x += max_width/2 - width/2;
				break;
			case RIGHT:  
				this.x += max_width - width;	
				break;
			default: break;
		}
		
		if(buff.getHeight() <= max_height) y += max_height/2 - height/2;
		return this;
	}

	public PDDocument getDoc() {return doc;}
	public void setDoc(PDDocument doc) {this.doc = doc;}

	public byte[] getImg() {return img;}
	public void setImg(byte[] img) {this.img = img;}
	
	//------------HELP INFO------------
	public static String describe() { return "PdfImage:\t\t\t\tNormal Image";}	
}
