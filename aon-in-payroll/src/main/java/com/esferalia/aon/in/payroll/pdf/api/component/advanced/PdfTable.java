package com.esferalia.aon.in.payroll.pdf.api.component.advanced;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.GRAY;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.WHITE;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT.CENTER;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfBox;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfComponent;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfText;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT;

/**
 * <p>
 * <b>Description:</b><i> This class represents a table.</i><br>
 * <i>This is an automatic version of old PdfTable, it calculates the number of
 * columns according to the headers.</i>
 * </p>
 * <i><b>Warning:</b> This is an Alpha component, be careful, it can be
 * changed.</i><br>
 * 
 * @version 2.0-AK
 * @author akrck02
 */
public class PdfTable extends PdfComponent {

	private int columns;

	private String[] headers;
	private ALIGNMENT[] alignments;
	private float[] pixels;
	private float[] sizes;
	private Object[] cells;
	public Color[] colors;

	public float width;
	public float cellHeight;
	public float headerHeight;

	public PDFont font;
	public float fontsize;
	public float headerFontsize;
	public Color textColor;
	public Color headerColor;
	public Color headerTextColor;

	public PdfTable(float x, float y, PDPageContentStream stream, float width, float cellHeight, float spacing,
			float[] sizes, String[] headers) {
		this.startPointer();
		this.x(x);
		this.y(y);

		this.width(width);
		this.height(0);

		this.marginX(0);
		this.marginY(0);

		this.stream(stream);
		this.columns = sizes.length;
		this.headers = headers;
		this.sizes = sizes;
		this.cellHeight = cellHeight;
		this.headerHeight = cellHeight;
		this.width = width;

		calculatePixels(spacing);
		cells = new Object[columns];
		colors = new Color[columns];

		font = HELVETICA;
		fontsize = 10;
		headerFontsize = fontsize;
		textColor = BLACK;
		headerColor = BLACK;
		headerTextColor = WHITE;
		alignments = new ALIGNMENT[columns];
	}

	private void calculatePixels(float spacing) {
		pixels = new float[columns];
		float d = this.x();

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = d;
			d += spacing + width / (100 / sizes[i]);
		}
	}

	public void clearRow() {
		cells = new Object[columns];
	}

	public void setAlignment(ALIGNMENT[] alignments) {
		for (int i = 0; i < columns; i++) {
			try {
				this.alignments[i] = alignments[i];
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void draw() {
		System.out.println("PdfAPI.PdfTable : METHOD DRAW(): Not compatible yet.");
	}

	public void drawHeader() throws IOException {
		for (int i = 0; i < columns; i++) {
			String textContent = (headers.length <= i) ? "" : headers[i];
			ALIGNMENT align = (alignments[i] != null) ? alignments[i] : CENTER;
			float height = (font.getFontDescriptor().getCapHeight()) / 1000 * headerFontsize;

			PdfBox box = new PdfBox(pixels[i], y(), width / (100 / sizes[i]), headerHeight, headerColor, this.stream());
			PdfText text = new PdfText(pixels[i], y(), width / (100 / sizes[i]), headerHeight, 5,
					(headerHeight - height) / 2, stream(), textContent, headerTextColor, font, headerFontsize, align);

			box.draw();
			text.draw();
		}
		down(headerHeight);
	}

	public void drawLine() throws IOException {
		PdfBox box = new PdfBox(pixels[0] + 5, y() + 5, width, .2f, GRAY, this.stream());
		box.draw();
	}

	public void newRow(String[] textBundle) throws IOException {

		for (int i = 0; i < pixels.length; i++) {
			ALIGNMENT align = (alignments[i] != null && alignments.length >= i) ? alignments[i] : CENTER;
			String content = (textBundle.length <= i) ? "" : textBundle[i];
			float height = (font.getFontDescriptor().getCapHeight()) / 1000 * fontsize;

			PdfText text = new PdfText(pixels[i], y(), width / (100 / sizes[i]), cellHeight, 5,
					(cellHeight - height) / 2, stream(), content, (colors[i] == null) ? textColor : colors[i], font,
					fontsize, align);
			text.draw();
		}
		down(cellHeight);
	}

	public void newRow() throws IOException {

		for (int i = 0; i < pixels.length; i++) {
			ALIGNMENT align = (alignments[i] != null && alignments.length >= i) ? alignments[i] : CENTER;
			String content = (cells[i] == null) ? "" : cells[i].toString();
			float height = (font.getFontDescriptor().getCapHeight()) / 1000 * fontsize;

			PdfText text = new PdfText(pixels[i], y(), width / (100 / sizes[i]), cellHeight, 5,
					(cellHeight - height) / 2, stream(), content, (colors[i] == null) ? textColor : colors[i], font,
					fontsize, align);
			text.draw();
		}

		colors = new Color[columns];

		down(cellHeight);
	}

	public boolean fillCell(int cell, Object o) {
		if (cell >= columns || cell < 0)
			return false;

		cells[cell] = o;
		return true;
	}

	public boolean paintCell(int cell, Color c) {
		if (cell >= columns || cell < 0)
			return false;

		colors[cell] = c;
		return true;
	}

	public int getColumn(String name) {
		for (int i = 0; i < headers.length; i++)
			if (headers[i].equalsIgnoreCase(name))
				return i;
		return -1;
	}

	public boolean hasColumn(int o) {
		try {
			return headers[o] != null;
		} catch (Exception e) {
			return false;
		}
	}

	public void jump(float pixels) {
		down(pixels);
	}

	public boolean alignCell(int cell, ALIGNMENT alignment) {
		if (cell >= columns || cell < 0)
			return false;
		alignments[cell] = alignment;
		return true;
	}

	public static String describe() {
		String info = "PdfTable:\t\t\t\t\tAutomatic table.";
		return info;
	}

}
