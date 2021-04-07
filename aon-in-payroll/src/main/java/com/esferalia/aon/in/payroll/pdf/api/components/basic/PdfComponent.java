package com.esferalia.aon.in.payroll.pdf.api.components.basic;

import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawBorderedBox;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawBox;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.api.beans.Pointer;
import com.esferalia.aon.in.payroll.pdf.api.settings.PdfSettings.BORDER_POSITION;

/**
 * <p>
 * <b>Description:</b> <i>This class represents a component in a PDF file. <br>
 * this class is the main component of the API, most beans extends of it. <br>
 * <br>
 * It contains basic data and methods. </i>
 * </p>
 * 
 * @author akrck02
 * @version 0.4-AK
 */
public abstract class PdfComponent {

	private Pointer pointer;
	private float width;
	private float height;
	private float marginX;
	private float marginY;
	private PDPageContentStream stream;

	protected PdfComponent() {
	}

	public abstract void draw();

	/**
	 * <p>
	 * <b>Description:</b> <i>Draws the imaginary box the element is in. </i>
	 * </p>
	 */
	public void square(Color boxColor) {
		try {
			drawBox(stream, x(), y(), width, height, boxColor);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Draws the border of imaginary box the element is in.
	 * </i>
	 * </p>
	 */
	public void border(Color borderColor) {
		try {
			drawBorderedBox(stream, x(), y(), width, height, borderColor);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void border(Color borderColor, BORDER_POSITION position, float size) {
		try {
			switch (position) {
			case ALL:
				drawBorderedBox(stream, x(), y(), width, height, borderColor, size);
				break;
			case BOTTOM:
				drawBorderedBox(stream, x(), y(), width, size, borderColor, size);
				break;
			case LEFT:
				drawBorderedBox(stream, x(), y(), size, height, borderColor, size);
				break;
			case RIGHT:
				drawBorderedBox(stream, x() + width - size, y(), size, height, borderColor, size);
				break;
			case TOP:
				drawBorderedBox(stream, x(), y() + height - size, width, size, borderColor, size);
				break;
			case NONE:
				break;
			default:
				this.border(borderColor);
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startPointer() {
		pointer = new Pointer(0, 0);
	}

	public void right(float d) {
		this.pointer.x(x() + d);
	}

	public void left(float d) {
		this.pointer.x(x() - d);
	}

	public void up(float d) {
		this.pointer.y(y() + d);
	}

	public void down(float d) {
		this.pointer.y(y() - d);
	}

	public float x() {
		return pointer.x();
	}

	public PdfComponent x(float x) {
		this.pointer.x(x);
		return this;
	}

	public float y() {
		return pointer.y();
	}

	public PdfComponent y(float y) {
		this.pointer.y(y);
		return this;
	}

	public float width() {
		return width;
	}

	public PdfComponent width(float width) {
		this.width = width;
		return this;
	}

	public float height() {
		return height;
	}

	public PdfComponent height(float height) {
		this.height = height;
		return this;
	}

	public float marginX() {
		return marginX;
	}

	public PdfComponent marginX(float marginX) {
		this.marginX = marginX;
		return this;
	}

	public float marginY() {
		return marginY;
	}

	public PdfComponent marginY(float marginY) {
		this.marginY = marginY;
		return this;
	}

	public PDPageContentStream stream() {
		return stream;
	}

	public PdfComponent stream(PDPageContentStream stream) {
		this.stream = stream;
		return this;
	}

	public static String describe() {
		return "PdfComponent:\t\t\tAbstract component father of most beans.";
	}

}
