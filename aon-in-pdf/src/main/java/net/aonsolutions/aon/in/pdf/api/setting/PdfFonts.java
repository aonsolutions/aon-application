package net.aonsolutions.aon.in.pdf.api.setting;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
/**
 * <p>
 * <b>Description:</b> <i>The font set for PdfAPI</i>
 * </p>
 * 
 * @author akrck02
 */
public class PdfFonts {
	public final static PDFont HELVETICA = new PDType1Font(FontName.HELVETICA);
	public final static PDFont HELVETICA_BOLD = new PDType1Font(FontName.HELVETICA_BOLD);
	public final static PDFont COURIER = new PDType1Font(FontName.COURIER);
	public final static PDFont COURIER_BOLD = new PDType1Font(FontName.COURIER_BOLD);
}
