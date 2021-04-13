package com.esferalia.aon.in.payroll.pdf.api.component.advanced;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT.LEFT;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfComponent;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT;

/**
 * <p>
 * <b>Description:</b><i> This class represents a table.</i><br>
 * <i>This is a magic version of old PdfTable.</i>
 * </p>
 * <i><b>Warning:</b> This is an Alpha component, be careful, it can be
 * changed.</i><br>
 * 
 * @version 0.1
 * @author akrck02
 */
public class PdfTableMagic extends PdfComponent {

	/**
	 * Default values
	 */
	private final static Color	   DEFAULT_FONT_COLOR = BLACK;
	private final static float	   DEFAULT_FONTSIZE	  = 7f;
	private final static ALIGNMENT DEFAULT_ALIGNMENT  = LEFT;

	private int					 columns;
	private ArrayList<String>	 headers;
	private ArrayList<ALIGNMENT> alignments;
	private ArrayList<Float>	 pixels;
	private ArrayList<Float>	 sizes;
	private ArrayList<Object>	 cells;
	private ArrayList<Color>	 colors; 

	public float width;
	public float cellHeight;
	public float headerHeight;

	public PDFont font;
	public float  fontsize;
	public float  headerFontsize;
	public Color  textColor;
	public Color  headerColor;
	public Color  headerTextColor;

	public PdfTableMagic() {
		headers	   = new ArrayList<>();
		alignments = new ArrayList<>();
		pixels	   = new ArrayList<>();
		sizes	   = new ArrayList<>();
		cells	   = new ArrayList<>();
		colors	   = new ArrayList<>();
	}

	private void calculatePixels(float spacing) {
	}

	public void clearRow() {
	}

	public void setAlignment(ALIGNMENT[] alignments) {
	}

	@Override
	public void draw() {
	}

	public void drawHeader() throws IOException {
	}

	public void drawLine() throws IOException {
	}

	public void newRow(String[] textBundle) throws IOException {
	}

	public void newRow() throws IOException {
	}

	public void fillCell(int cell, Object o) {
	}

	public void paintCell(int cell, Color c) {
	}

	public void getColumn(String name) {
	}

	public void hasColumn(int o) {
	}

	public void jump(float pixels) {
		down(pixels);
	}

	public void alignCell(int cell, ALIGNMENT alignment) {
	}

	public static class PdfTableMagicBuilder {

	}

}
