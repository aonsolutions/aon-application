package com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.Pointer;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;

/**
 * <p><b>Description:</b> 
 * 		<i>This class represents a component in a PDF file.
 * 			<br> this class is the main component of the API, most beans extends of it.
 * 			<br><br> It contains basic data and methods.
 *  	</i>
 *  </p>
 * @author akrck02
 * @version 0.3-AK
 */
public abstract class PdfComponent {

	protected Pointer pointer;
	
	protected float width;
	protected float height;
	
	protected float margin_x;
	protected float margin_y;
	
	protected PDPageContentStream stream;
	public abstract void draw();
	
	/**
	 * <p><b>Description:</b> <i>The constructor. </i></p>
	 * @param x 
	 * @param y 
	 * @param width
	 * @param height
	 * @param margin_x
	 * @param margin_y
	 * @param stream the PDPageContentStream
	 */
	public PdfComponent(float x, float y, float width, float height, float margin_x, float margin_y,
			PDPageContentStream stream) {
		this.pointer = Pointer.instance(x, y);
		this.width = width;
		this.height = height;
		this.margin_x = margin_x;
		this.margin_y = margin_y;
		this.stream = stream;
	}

	/**
	 * <p><b>Description:</b> <i>Draws the imaginary box the element is in. </i></p>
	 */
	public void square(Color box_color) {
		try {PDFToolkit.drawBox(stream, x(), y(), width, height, box_color);} 
		catch (IOException e) {e.printStackTrace();}
	}

	/**
	 * <p><b>Description:</b> <i>Draws the border of imaginary box the element is in. </i></p>
	 */
	public void border(Color border_color) {
		try {PDFToolkit.drawBorderedBox(stream, x(), y(), width, height, border_color);} 
		catch (IOException e) {e.printStackTrace();}
	}

	
	public void  right(float d)	{this.pointer.x(x() + d);}
	public void  left(float d)	{this.pointer.x(x() - d);}
	
	public void  up(float d)	{this.pointer.y(y() + d);}
	public void  down(float d)	{this.pointer.y(y() - d);}
	
	
	public float x() 		{return pointer.x();}
	public void	 x(float x) {this.pointer.x(x);}

	public float y() 		{return pointer.y();}
	public void  y(float y) {this.pointer.y(y);}

	
	public float width() 			{return width;}
	public void  width(float width) {this.width = width;}

	public float height() 				{return height;}
	public void  height(float height) 	{this.height = height;}

	public float margin_x() 				{return margin_x;}
	public void  margin_x(float margin_x) 	{this.margin_x = margin_x;}

	public float margin_y() 				{return margin_y;}
	public void  margin_y(float margin_y) 	{this.margin_y = margin_y;}

	public PDPageContentStream stream() {return stream;}
	public void stream(PDPageContentStream stream) {this.stream = stream;}
	
	//------------HELP INFO------------
	public static String describe() {
		String info = "PdfComponent:\t\t\tAbstract component father of most beans.";
		return info;
	}	
}
