package com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.GRAY;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.Pointer;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.PAGE_TYPE;

/**
 * <p><b>Description:</b> 
 * 		<i>This class represents a PDF file.
 * 			<br> It contains basic data of a PDF file an every template should extend of it.
 * 
 * 			<h4>important parameters: </h4>
 * 			&nbsp; <b>x:</b>    	  	&nbsp; The general PDF x value (the one you must change); <br>
 * 			&nbsp; <b>y:</b>         	&nbsp; The general PDF y value (the one you must change); <br>
 * 			&nbsp; <b>page:</b> 	  	&nbsp; Automatic page index; <br>
 * 			&nbsp; <b>doc:</b>  	  	&nbsp; The PdfBox document; <br>
 * 			&nbsp; <b>contents:</b>  	&nbsp; The PdfBox content stream; <br>
 * 			&nbsp; <b>words:</b>  		&nbsp; The PDF resource bundle (texts)<br>
 * 			&nbsp; <b>out:</b>  	  	&nbsp; The OutputStream<br>
 * 			&nbsp; <b>lang:</b>  		&nbsp; The PDF Locale<br>
 * 			&nbsp; <b>y_limit:</b>   	&nbsp; The limit of page jump
 * 
 * 			<h4>default parameters: </h4>
 * 			&nbsp; <b>font:</b>         &nbsp; Default font<br>
 * 			&nbsp; <b>fontsize</b>      &nbsp; Default fontsize<br>
 * 			&nbsp; <b>primary</b>       &nbsp; Primary color<br>
 * 			&nbsp; <b>secondary</b>     &nbsp; Secondary color<br>
 *  	</i>
 *  </p>
 * @see java.io.OutputStream
 * @see java.awt.Color
 * @see java.util.Locale
 * @see java.util.ResourceBundle
 * @see org.apache.pdfbox.model.PDDocument
 * @version 1.1-AK
 * @author akrck02
 */
public class PdfFile {
	
	public Pointer pointer;
	public int page;
	
	public PDDocument doc;
	public PDPageContentStream contents;
	public ResourceBundle words;
	public OutputStream out;
	public Locale lang;  
	
	public float y_limit;
	
	public PDFont font;
	public float fontsize;
	public Color primary;
	public Color secondary;
	
	
	/**
	 * <p><b>Description:</b> <i>The constructor. </i></p>
	 * @param x 
	 * @param y 
	 * @param doc	
	 * @param words	
	 * @param out  
	 */
	public PdfFile(float x, float y, PDDocument doc, ResourceBundle words, OutputStream out) {
		this.pointer = Pointer.instance(x, y);
		this.page = 0;
		this.doc = doc;
		this.words = words;
		this.y_limit = 0;
		
		this.font = HELVETICA;
		this.fontsize = 10f;
		this.primary = BLACK;
		this.secondary = GRAY;
		this.out = out;
	}
	
	/**
	 * <p><b>Description:</b> <i>The constructor. </i></p>
	 * @param x 
	 * @param y 
	 * @param doc	
	 * @param words	
	 * @param out  
	 * @param y_limit  
	 */
	public PdfFile(float x, float y, PDDocument doc, ResourceBundle words, OutputStream out,
			float y_limit) {
		this.pointer = Pointer.instance(x, y);
		this.page = 0;
		this.doc = doc;
		this.words = words;
		this.y_limit = y_limit;
		
		this.font = HELVETICA;
		this.fontsize = 10f;
		this.primary = BLACK;
		this.secondary = GRAY;
		this.out = out;
	}

	/**
	 * <p><b>Description:</b> <i>Set the default values. </i></p> 
	 */
	public void set_defaults(PDFont font, float fontsize, Color primary, Color secondary) {
		this.font = font;
		this.fontsize = fontsize;
		this.primary = primary;
		this.secondary = secondary;
	}
	
	/**
	 * <p><b>Description:</b> <i>Creates and adds a new page to the file. </i></p> 
	 */
	public void new_page(PAGE_TYPE type) throws IOException {
		
		if(contents != null) contents.close();
		PdfPage page = new PdfPage(type);
		doc.addPage(page.getPage());
		contents = new PDPageContentStream(this.doc, page.getPage());
		this.page++;
	}
	
	/**
	 * <p><b>Description:</b> <i>Returns if y value exceed y_limit. </i></p> 
	 * @return if jumps
	 */
	public boolean jump() {
		return pointer.y() < y_limit;
	}
	
	/**
	 * <p><b>Description:</b> <i>Closes the PDF. </i></p> 
	 */
	public void close() throws IOException {
		if(contents != null) contents.close();
		if(doc != null) doc.close();
	}
	
	
	/**
	 * <p><b>Description:</b> <i>Prints the PDF. </i></p> 
	 */
	public void print() throws IOException {
		if(contents != null)  contents.close();
		if(doc != null) doc.save(out);
	}

	
	/**
	 * <p><b>Description:</b> <i>Gets text from bundle. </i></p> 
	 * @return text 
	 */
	public String text(String name) {
		if(words == null) return "";
		return words.getString(name);
	}
	
	/** <p><b>Description:</b> <i>Move down the pointer. </i></p> */
	public void down(float pixels) 		{pointer.down(pixels);}
	
	/** <p><b>Description:</b> <i>Move up the pointer. </i></p> */
	public void up(float pixels) 		{pointer.up(pixels);}
	
	/** <p><b>Description:</b> <i>Move left the pointer. </i></p> */
	public void left(float pixels) 		{pointer.left(pixels);}
	
	/** <p><b>Description:</b> <i>Move right the pointer. </i></p> */
	public void right(float pixels) 	{pointer.right(pixels);}
	
	
	public float y() {return pointer.y();}
	public void  y(float y) {pointer.y(y);}
	
	public float x() {return pointer.x();}
	public void  x(float x) {pointer.x(x);}
	
	//------------HELP INFO------------
	public static String describe() { return "PdfFile: \t\t\t\t\tRepresents the basic pdf file template." ;}	
}
