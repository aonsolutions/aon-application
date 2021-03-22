package com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.Pointer;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.BORDER_POSITION;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;

/**
 * <p><b>Description:</b> 
 * 		<i>This class represents a component in a PDF file.
 * 			<br> this class is the main component of the API, most beans extends of it.
 * 			<br><br> It contains basic data and methods.
 *  	</i>
 *  </p>
 * @author akrck02
 * @version 0.4-AK
 */
public abstract class PdfComponent {

	private Pointer pointer;
	private float width;
	private float height;
	private float margin_x;
	private float margin_y;
	private PDPageContentStream stream;
	
	protected PdfComponent() {}	
	public abstract void draw();
	
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
	
	public void border(Color border_color,BORDER_POSITION position,float size) {
		try {
			switch (position) {
				case ALL :		PDFToolkit.drawBorderedBox(stream, x(), y(), width, height, border_color,size); break;
				case BOTTOM:	PDFToolkit.drawBorderedBox(stream, x(), y(), width, size, border_color,size); break;
				case LEFT:		PDFToolkit.drawBorderedBox(stream, x(), y(), size,  height, border_color,size); break;
				case RIGHT:		PDFToolkit.drawBorderedBox(stream, x() + width - size, y(), size, height, border_color,size); break;
				case TOP: 		PDFToolkit.drawBorderedBox(stream, x(), y() + height - size, width, size, border_color,size); break;
				case NONE:      break;		
				default: this.border(border_color); break;
			}
		} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	
	public void  start_pointer() {pointer = new Pointer(0, 0);}
	
	public void  right(float d)	{this.pointer.x(x() + d);}
	public void  left(float d)	{this.pointer.x(x() - d);}
	
	public void  up(float d)	{this.pointer.y(y() + d);}
	public void  down(float d)	{this.pointer.y(y() - d);}
	
	public float x() 										{return pointer.x();}
	public PdfComponent	 x(float x) 						{this.pointer.x(x); 		return this;}

	public float y() 										{return pointer.y();}
	public PdfComponent  y(float y) 						{this.pointer.y(y);			return this;}
	
	public float width() 									{return width;}
	public PdfComponent  width(float width) 				{this.width = width; 		return this;}

	public float height() 									{return height;}
	public PdfComponent  height(float height) 				{this.height = height; 		return this;}

	public float margin_x() 								{return margin_x;}
	public PdfComponent  margin_x(float margin_x) 			{this.margin_x = margin_x;	return this;}

	public float margin_y() 								{return margin_y;}
	public PdfComponent  margin_y(float margin_y) 			{this.margin_y = margin_y;	return this;}

	public PDPageContentStream stream() 					{return stream;}
	public PdfComponent stream(PDPageContentStream stream) 	{this.stream = stream; 	return this;}
	
	public static String describe() {return "PdfComponent:\t\t\tAbstract component father of most beans.";}	
		
}
