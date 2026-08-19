package net.aonsolutions.aon.in.pdf.api.setting;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
/**
 * <p>
 * <b>Description:</b> <i>The font set for PdfAPI</i>
 * </p>
 *
 * <p>
 * <b>Every accessor returns a new instance on purpose, never a shared constant.</b>
 * PDFBox stores the object number it assigns to a font inside that font's own
 * COSDictionary when the document is saved. If a single PDFont instance is
 * reused by a second document, the writer re-emits the stale object number and
 * skips writing the font, so the /Font resource ends up pointing at whatever
 * object holds that number in the new document (an image, an annotation...) and
 * no text can be rendered.
 * </p>
 *
 * @author akrck02
 */
public class PdfFonts {

	public static PDFont helvetica() {
		return new PDType1Font(FontName.HELVETICA);
	}

	public static PDFont helveticaBold() {
		return new PDType1Font(FontName.HELVETICA_BOLD);
	}

	public static PDFont courier() {
		return new PDType1Font(FontName.COURIER);
	}

	public static PDFont courierBold() {
		return new PDType1Font(FontName.COURIER_BOLD);
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Tells whether the given font is plain Helvetica.</i>
	 * </p>
	 * <p>
	 * Compares by font name on purpose: PDFont.equals() compares the underlying
	 * COSDictionary by reference, so it only ever matches the very same instance.
	 * </p>
	 */
	public static boolean isHelvetica(PDFont font) {
		return font != null && FontName.HELVETICA.getName().equals(font.getName());
	}
}
