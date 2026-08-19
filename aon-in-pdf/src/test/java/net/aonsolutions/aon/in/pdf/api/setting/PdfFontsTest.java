package net.aonsolutions.aon.in.pdf.api.setting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.junit.Test;

/**
 * <p>
 * <b>Description:</b> <i>Guards the invariant that a PDFont is never shared
 * between documents.</i>
 * </p>
 * <p>
 * PDFBox stores the object number it assigns to a font inside that font's own
 * COSDictionary when the document is saved. A font instance reused by a second
 * document makes the writer re-emit the stale object number and skip writing the
 * font, so the /Font resource points at whatever object owns that number in the
 * new document and no text can be rendered.
 * </p>
 */
public class PdfFontsTest {

	@Test
	public void everyAccessorReturnsAFreshInstance() {
		assertNotSame(PdfFonts.helvetica(), PdfFonts.helvetica());
		assertNotSame(PdfFonts.helveticaBold(), PdfFonts.helveticaBold());
		assertNotSame(PdfFonts.courier(), PdfFonts.courier());
		assertNotSame(PdfFonts.courierBold(), PdfFonts.courierBold());

		// the COSDictionary is what carries the object number, so it must differ too
		assertNotSame(PdfFonts.helvetica().getCOSObject(), PdfFonts.helvetica().getCOSObject());
	}

	@Test
	public void isHelveticaMatchesByNameNotByInstance() {
		assertTrue(PdfFonts.isHelvetica(PdfFonts.helvetica()));
		// a different instance of the same font must still match
		assertTrue(PdfFonts.isHelvetica(PdfFonts.helvetica()));
	}

	/**
	 * Two documents of different shape saved by the same JVM. The second one used
	 * to come out with its /Font entries pointing at an image and an annotation.
	 */
	@Test
	public void fontResourcesStayValidAcrossDocumentsOfDifferentShape() throws IOException {
		assertFontsAreFonts(textOnlyDocument());
		assertFontsAreFonts(documentWithImagesAndAnnotations());
		assertFontsAreFonts(textOnlyDocument());
		assertFontsAreFonts(documentWithImagesAndAnnotations());
	}

	private byte[] textOnlyDocument() throws IOException {
		try (PDDocument doc = new PDDocument())
		{
			PDPage page = new PDPage(PDRectangle.A4);
			doc.addPage(page);
			try (PDPageContentStream contents = new PDPageContentStream(doc, page))
			{
				write(contents, PdfFonts.helveticaBold(), 11f, 700, "cabecera");
				write(contents, PdfFonts.helvetica(), 9f, 680, "cuerpo");
			}
			return save(doc);
		}
	}

	private byte[] documentWithImagesAndAnnotations() throws IOException {
		try (PDDocument doc = new PDDocument())
		{
			PDPage page = new PDPage(PDRectangle.A4);
			doc.addPage(page);
			try (PDPageContentStream contents = new PDPageContentStream(doc, page))
			{
				// images and annotations shift the object numbering vs the text only document
				PDImageXObject qr = LosslessFactory
						.createFromImage(doc, new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB));
				contents.drawImage(qr, 440, 700, 120, 120);
				PDImageXObject logo = LosslessFactory
						.createFromImage(doc, new BufferedImage(402, 126, BufferedImage.TYPE_INT_RGB));
				contents.drawImage(logo, 50, 771, 175, 55);

				PDActionURI action = new PDActionURI();
				action.setURI("www.agenciatributaria.es");
				PDAnnotationLink link = new PDAnnotationLink();
				link.setAction(action);
				page.getAnnotations().add(link);

				write(contents, PdfFonts.helveticaBold(), 14f, 660, "FACTURA");
				write(contents, PdfFonts.helvetica(), 9f, 640, "Honorarios");
			}
			return save(doc);
		}
	}

	private void write(PDPageContentStream contents, PDFont font, float size, float y, String text)
			throws IOException {
		contents.beginText();
		contents.setFont(font, size);
		contents.newLineAtOffset(50, y);
		contents.showText(text);
		contents.endText();
	}

	private byte[] save(PDDocument doc) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		doc.save(out);
		return out.toByteArray();
	}

	/**
	 * Every entry of every /Font resource must resolve to a real /Type /Font object.
	 */
	private void assertFontsAreFonts(byte[] pdf) throws IOException {
		try (PDDocument doc = Loader.loadPDF(pdf))
		{
			int checked = 0;
			for (PDPage page : doc.getPages())
			{
				COSDictionary fonts = page.getResources().getCOSObject().getCOSDictionary(COSName.FONT);
				if (fonts == null)
					continue;
				for (COSName name : fonts.keySet())
				{
					COSBase target = fonts.getDictionaryObject(name);
					assertTrue(
							"/" + name.getName() + " does not point to a dictionary but to " + target,
							target instanceof COSDictionary
					);
					assertEquals(
							"/" + name.getName() + " does not point to a font",
							COSName.FONT, ((COSDictionary) target).getCOSName(COSName.TYPE)
					);
					checked++;
				}
			}
			assertTrue("no font resource was found to check", checked > 0);
		}
	}
}
