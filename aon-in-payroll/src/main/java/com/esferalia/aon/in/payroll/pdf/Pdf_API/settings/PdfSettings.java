package com.esferalia.aon.in.payroll.pdf.Pdf_API.settings;

/**
 * <p><b>Description:</b> <i>The setting for PdfAPI beans and methods.</i></p>
 * @author akrck02
 */
public class PdfSettings {

	/**
	 * <p><b>Description:</b> <i>The alignment of an element.</i></p>
	 */
	public static enum ALIGNMENT {
	    CENTER,
	    LEFT,
	    RIGHT,
	    JUSTIFY;
	}
	
	/**
	 * <p><b>Description:</b> <i>Type of page for a PDF.</i></p>
	 */
	public static enum PAGE_TYPE{
		HORIZONTAL,
		VERTICAL
	}
	
	/**
	 * <p><b>Description:</b> <i>Text vertical align.</i></p>
	 */
	public static enum VERTICAL_ALIGNMENT{
		UP,
		DOWN,
		CENTER
	}
	
	/**
	 * <p><b>Description:</b> <i>Border position of an element.</i></p>
	 * <p><b>Warning:</b> <i>Not implemented yet.</i></p>
	 */
	public static enum BORDER_POSITION{
		BOTTOM,
		TOP,
		LEFT,
		RIGHT,
		ALL,
		NONE
	}
}
