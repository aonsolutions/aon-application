package net.aonsolutions.aon.in.pdf.api.toolkit;

import static net.aonsolutions.aon.in.pdf.api.setting.PdfFormats.toLatinNumber;
import static net.aonsolutions.aon.in.pdf.api.toolkit.DataToolkit.safeDouble;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.text.PDFMarkedContentExtractor;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.in.pdf.api.component.basic.PdfBox;
import net.aonsolutions.aon.in.pdf.api.component.basic.PdfImage;
import net.aonsolutions.aon.in.pdf.api.component.basic.PdfPage;
import net.aonsolutions.aon.in.pdf.api.component.basic.PdfText;
import net.aonsolutions.aon.in.pdf.api.setting.PdfFonts;

/**
 * <p>
 * <b>Description:</b> <i>This toolkit contains the basic methods for PDF
 * printing and pdfbox abstractions.</i>
 * </p>
 * 
 * @author akrck02
 * @warning To create PDF elements use PdfAPI beans they are easier to implement
 *          and use and give you advanced methods by default.
 */
public class PDFToolkit {

	/**
	 * <p>
	 * <b>Description:</b> <i>Creates an horizontal dinA4 page.</i>
	 * </p>
	 * <p>
	 * <b>warning:</b> Use PdfPage instead
	 * </p>
	 * 
	 * @return PDPage page
	 * @see PdfPage
	 */
	public static PDPage createHorizontalPage() {
		final float	POINTS_PER_INCH	= 72;
		final float	POINTS_PER_MM	= 1 / (10 * 2.54f) * POINTS_PER_INCH;
		return new PDPage(new PDRectangle(297 * POINTS_PER_MM, 210 * POINTS_PER_MM));
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Creates an vertical dinA4 page.</i>
	 * </p>
	 * <p>
	 * <b>Warning:</b> Use PdfPage instead
	 * </p>
	 * 
	 * @return PDPage page
	 * @see PdfPage
	 */
	public static PDPage createVerticalPage() {
		final float	POINTS_PER_INCH	= 72;
		final float	POINTS_PER_MM	= 1 / (10 * 2.54f) * POINTS_PER_INCH;
		return new PDPage(new PDRectangle(210 * POINTS_PER_MM, 297 * POINTS_PER_MM));
	}
	

	/**
	 * <p>
	 * <b>Description:</b> <i>Draws a text. </i>
	 * </p>
	 * <p>
	 * <b>Warning:</b> Use PdfText instead
	 * </p>
	 * 
	 * @return void
	 * @see PdfText
	 */
	public static void drawText(
			PDPageContentStream contents, String content, Float x, Float y, Color color, PDFont font, float fontSize, String aName
	) throws IOException {
		contents.beginMarkedContent(COSName.getPDFName(aName));
		drawText(contents, content, x, y, color, font, fontSize);
		contents.endMarkedContent();
	}
	

	/**
	 * <p>
	 * <b>Description:</b> <i>Draws a text. </i>
	 * </p>
	 * <p>
	 * <b>Warning:</b> Use PdfText instead
	 * </p>
	 * 
	 * @return void
	 * @see PdfText
	 */
	public static void drawText(
			PDPageContentStream contents, String content, Float x, Float y, Color color, PDFont font, float fontSize
	) throws IOException {
		//METER AQUÍ LAS EXCEPCIONES QUE VAYAN SURGIENDO
		String textToWrite = content;
		if (PdfFonts.HELVETICA.equals(font)) {
			textToWrite = AonStringUtils.replace(textToWrite, "\u0009", "  ");
		}
		contents.setNonStrokingColor(color);
		contents.beginText();
		contents.setFont(font, fontSize);
		contents.newLineAtOffset(x, y);
//		try {
			contents.showText(textToWrite);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		contents.endText();
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Draws a justified text. </i>
	 * </p>
	 * <p>
	 * <b>Warning:</b> Use PdfText instead
	 * </p>
	 * 
	 * @return void
	 * @see PdfText
	 */
	public static void drawTextJustified(
			List<String> lines, float max, float fontSize, PDFont font, float x, float y, Color color,
			PDPageContentStream stream
	) throws IOException {
		for (int i = 0; i < lines.size() - 1; i++, y -= 10) {
			if (lines.get(i).isEmpty())
			drawTextJustified(lines.get(i), max, fontSize, font, x, y, stream);
		}
	}
	
	public static void drawTextWellJustified(
			List<String> lines, float max, float fontSize, PDFont font, float x, float y, Color color,
			PDPageContentStream stream
	) throws IOException {
		stream.setNonStrokingColor(color);
		for (int i = 0; i < lines.size(); i++, y -= fontSize) {
			String line = lines.get(i) != null ? lines.get(i).trim() : "";
			boolean isNextEmpty = false;
			if (i < lines.size() -1) {
				isNextEmpty = lines.get(i + 1).isEmpty();
			} else
				isNextEmpty = true;
			
			if (!isNextEmpty) {
				
				float size = fontSize * font.getStringWidth(line) / 1000;
				float free = max - size;
				
				if (free > max*0.1) {					
					drawText(stream, line, x, y, color, font, fontSize);
				} else {					
					drawTextJustified(line, max, fontSize, font, x, y, stream);
				}
				
			} else {
				drawText(stream, line, x, y, color, font, fontSize);
			}
			
		}
	}
	
	public static void drawTextWellJustified(
			List<String> lines, float max, float fontSize, PDFont font, float x, float y, Color color,
			PDPageContentStream stream, float lineSeparation
			) throws IOException {
		stream.setNonStrokingColor(color);
		for (int i = 0; i < lines.size(); i++, y -= lineSeparation) {
			String line = lines.get(i) != null ? lines.get(i).trim() : "";
			boolean isNextEmpty = false;
			if (i < lines.size() -1) {
				isNextEmpty = lines.get(i + 1).isEmpty();
			} else
				isNextEmpty = true;
			
			if (!isNextEmpty) {
				
				float size = fontSize * font.getStringWidth(line) / 1000;
				float free = max - size;
				
				if (free > max*0.1) {					
					drawText(stream, line, x, y, color, font, fontSize);
				} else {					
					drawTextJustified(line, max, fontSize, font, x, y, stream);
				}
				
			} else {
				drawText(stream, line, x, y, color, font, fontSize);
			}
			
		}
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Draws a justified line. </i>
	 * </p>
	 * <p>
	 * <b>Warning:</b> Use PdfText instead
	 * </p>
	 * 
	 * @return void
	 * @see PdfText
	 */
	public static void drawTextJustified(
			String line, float max, float fontSize, PDFont font, float x, float y, PDPageContentStream stream
	) throws IOException {

		stream.beginText();
		float charSpacing = 0;
		if (line.length() > 1)
		{
			float size = fontSize * font.getStringWidth(line) / 1000;
			float free = max - size;
			if (free > 0)
				charSpacing = free / (line.length() - 1);
		}

		stream.setFont(font, fontSize);
		stream.newLineAtOffset(x, y);
		stream.setCharacterSpacing(charSpacing);
		stream.showText(line);
		stream.endText();
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Get the width of a text according to font and font
	 * size. </i>
	 * </p>
	 * 
	 * @return void
	 */
	public static float fontWidth(List<String> texts, float fontSize, PDFont font) throws IOException {
		float fw = 0;
		for (String text : texts)
			fw += fontWidth(text, fontSize, font);
		return fw;
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Get the width of a text according to font and font
	 * size. </i>
	 * </p>
	 * 
	 * @return void
	 */
	public static float fontWidth(String string, float fontSize, PDFont font) throws IOException {
		return (font.getStringWidth(string) / 1000.0f) * fontSize;
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Draws a right aligned text. </i>
	 * </p>
	 * <p>
	 * <b>Warning:</b> Use PdfText instead
	 * </p>
	 * 
	 * @return void
	 * @see PdfText
	 */
	public static void drawTextRight(
			PDPageContentStream contents, PDRectangle box, String content, Color color, PDFont font, float fontSize,
			float marginX, float marginY
	) throws IOException {
		float w	 = box.getWidth();
		float fw = (font.getStringWidth(content) / 1000.0f) * fontSize;

		float x	= box.getLowerLeftX() + w - fw - marginX;
		float y	= marginY + box.getLowerLeftY();

		contents.setNonStrokingColor(color);
		contents.beginText();
		contents.setFont(font, fontSize);
		contents.newLineAtOffset(x, y);
		contents.showText(content);
		contents.endText();
	}

	public static void drawTextRight(PDPageContentStream contents, PDRectangle box, String content, Color color, PDFont font, float fontSize,
			float marginX, float marginY, String name) throws IOException {
		contents.beginMarkedContent(COSName.getPDFName(name));
		drawTextRight(contents, box, content, color, font, fontSize, marginX, marginY);
		contents.endMarkedContent();
	}
	
	/**
	 * <p>
	 * <b>Description:</b> <i>Draws a left aligned text. </i>
	 * </p>
	 * <p>
	 * <b>Warning:</b> Use PdfText instead.
	 * </p>
	 * 
	 * @return void
	 * @see PdfText
	 */
	public static void drawTextLeft(
			PDPageContentStream contents, PDRectangle box, String content, Color color, PDFont font, float fontSize,
			float marginX, float marginY
	) throws IOException {
		float x	= box.getLowerLeftX() + marginX;
		float y	= marginY + box.getLowerLeftY();

		contents.setNonStrokingColor(color);
		contents.beginText();
		contents.setFont(font, fontSize);
		contents.newLineAtOffset(x, y);
		contents.showText(content);
		contents.endText();
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Draws a center aligned text. </i>
	 * </p>
	 * <p>
	 * <b>Warning:</b> Use PdfText instead.
	 * </p>
	 * 
	 * @return void
	 * @see PdfText
	 */
	public static void drawTextCenter(
			PDPageContentStream contents, PDRectangle box, String content, Color color, PDFont font, float fontSize,
			float marginY
	) throws IOException {
		float w	 = box.getWidth();
		float fw = (font.getStringWidth(content) / 1000.0f) * fontSize;

		float x	= box.getLowerLeftX() + (w - fw) / 2;
		float y	= marginY + box.getLowerLeftY();

		contents.setNonStrokingColor(color);
		contents.beginText();
		contents.setFont(font, fontSize);
		contents.newLineAtOffset(x, y);
		contents.showText(content);
		contents.endText();
	}

	public static void drawTextCenter(
			PDPageContentStream contents, PDRectangle box, String content, Color color, PDFont font, float fontSize,
			float marginY, String name
	) throws IOException {
		contents.beginMarkedContent(COSName.getPDFName(name));
		drawTextCenter(contents, box, content, color, font, fontSize, marginY);
		contents.endMarkedContent();
	}
	
	/**
	 * <p>
	 * <b>Description:</b> <i>Draws an image in natural size.</i>
	 * </p>
	 * <p>
	 * <b>warning:</b> Use PdfImage instead
	 * </p>
	 * 
	 * @return void
	 * @see PdfImage
	 */
	public static void drawImage(PDDocument doc, PDPageContentStream contents, byte[] logo, float x, float y)
			throws IOException {
		contents.drawImage(PDImageXObject.createFromByteArray(doc, logo, null), x, y);
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Draws an image.</i>
	 * </p>
	 * <p>
	 * <b>warning:</b> Use PdfImage instead
	 * </p>
	 * 
	 * @return void
	 * @see PdfImage
	 */
	public static void drawImage(
			PDDocument doc, PDPageContentStream contents, byte[] logo, float x, float y, float width, float height
	) throws IOException {

		contents.drawImage(PDImageXObject.createFromByteArray(doc, logo, ""), x, y, width, height);
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Draws a box.</i>
	 * </p>
	 * <p>
	 * <b>warning:</b> Use PdfBox instead
	 * </p>
	 * 
	 * @return void
	 * @see PdfBox
	 */
	public static void drawBox(PDPageContentStream contents, float x, float y, float width, float height, Color color)
			throws IOException {
		contents.setNonStrokingColor(color);
		contents.addRect(x, y, width, height);
		contents.fill();
	}

	public static void drawBox(PDPageContentStream contents, float x, float y, float width, float height, Color color, float opacity)
			throws IOException {
		contents.saveGraphicsState();
		PDExtendedGraphicsState extendedGraphicsState = new PDExtendedGraphicsState();
		extendedGraphicsState.setNonStrokingAlphaConstant(opacity);
	 
		contents.setGraphicsStateParameters(extendedGraphicsState);
		contents.setNonStrokingColor(color);
	 
		contents.addRect(x, y, width, height);
		contents.fill();
		
		contents.restoreGraphicsState();
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Draws a bordered box.</i>
	 * </p>
	 * <p>
	 * <b>warning:</b> Use PdfBox instead
	 * </p>
	 * 
	 * @return void
	 * @see PdfBox
	 */
	public static void drawBorderedBox(
			PDPageContentStream contents, float x, float y, float width, float height, Color color
	) throws IOException {
		contents.setStrokingColor(color);
		contents.addRect(x, y, width, height);
		contents.setLineWidth(2);
		contents.stroke();
	}

	public static void drawBorderedBox(
			PDPageContentStream contents, float x, float y, float width, float height, Color color, float size
	) throws IOException {
		contents.setStrokingColor(color);
		contents.addRect(x, y, width, height);
		contents.setLineWidth(size);
		contents.stroke();
	}
	
	public static void drawDashedLine(PDPageContentStream contents, float x, float y, float width, float thickness, Color color, float[] pattern, float phase) throws IOException {
		contents.saveGraphicsState();
		contents.setLineDashPattern(pattern, phase);
		contents.setStrokingColor(color);
		contents.setLineWidth(thickness);
		contents.moveTo(x, y);
		contents.lineTo(x + width, y);
		contents.stroke();
		contents.restoreGraphicsState();
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Gets fields of a form.</i>
	 * </p>
	 * 
	 * @return List of PDFields
	 * @deprecated this method is no longer in use.
	 */
	@Deprecated
	public static List<PDField> getFormFields(PDDocument doc) {
		PDDocumentCatalog pdCatalog	 = doc.getDocumentCatalog();
		PDAcroForm		  pdAcroForm = pdCatalog.getAcroForm();
		return pdAcroForm.getFields();
	}

	/**
	 * <p>
	 * <b>Description:</b> <i> Gets a box of a form field.</i>
	 * </p>
	 * 
	 * @return PDRectangle (Optional)
	 * @deprecated this method is no longer in use.
	 */
	@Deprecated
	public static Optional<PDRectangle> getFieldRectangle(Optional<PDField> field, int widgetIndex) {
		if (field.isEmpty())
			return Optional.empty();
		PDField f = field.get();
		if (widgetIndex >= f.getWidgets().size())
			return Optional.empty();
		return Optional.of(f.getWidgets().get(widgetIndex).getRectangle());
	}

	/**
	 * <p>
	 * <b>Description:</b> <i> Gets the page of a form field.</i>
	 * </p>
	 * 
	 * @return PDPage (Optional)
	 * @deprecated this method is no longer in use.
	 */
	@Deprecated
	public static Optional<PDPage> getFieldPage(Optional<PDField> field, int widgetIndex) {
		if (field.isEmpty())
			return Optional.empty();
		PDField f = field.get();
		if (widgetIndex >= f.getWidgets().size())
			return Optional.empty();
		return Optional.of(f.getWidgets().get(widgetIndex).getPage());
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Open a PDF file in append mode.</i>
	 * </p>
	 * 
	 * @return PDPageContentStream (the pdf file stream)
	 */
	public static PDPageContentStream openInAppendMode(PDDocument doc, PDPage page) {
		try
		{
			return new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
		} catch (IOException e)
		{
			return null;
		}
	}

	/**
	 * <p>
	 * <b>Description:</b> <i> Gets lines of an String.</i>
	 * </p>
	 * <p>
	 * <b>warning:</b> Use getLines() instead
	 * </p>
	 * 
	 * @return List of Strings
	 * @deprecated this method is no longer in use.
	 * @see PDFToolkit.getLines()
	 */
	@Deprecated
	public static List<String> divideStringToFit(String text, float max, PDFont font, float fontSize)
			throws IOException {
		return getLines(text, max, font, fontSize);
	}

	/**
	 * <p>
	 * <b>Description:</b> <i> Gets lines of an String based on max width, font
	 * size, and font.
	 * <q>puzzles are my passion</q><i>
	 * </p>
	 * 
	 * @param max &nbsp; maximum width
	 * @return List of Strings
	 */
	public static List<String> getLines(String text, float max, PDFont font, float fontSize) throws IOException {

		ArrayList<String> lines	= new ArrayList<>();
		ArrayList<String> words	= (ArrayList<String>) StringToolkit.toWords(text);
		String			  line	= "";

		for (int i = 0; i < words.size(); i++) {
			float fw = (font.getStringWidth(line + " " + words.get(i)) / 1000.0f) * fontSize;
			if (fw < max)
			{
				line += " " + words.get(i);
			} else {
				lines.add(line);
				line = "" + words.get(i);
			}
			if (i == words.size() - 1)
				lines.add(line);
		}
		if (lines.isEmpty()) {
			lines.add(line);			
		}
		return lines;
	}
	
	
	public static List<String> getLinesRespectOriginal(String text, float max, PDFont font, float fontSize) throws IOException {
		try {
			ArrayList<String> lines	= new ArrayList<>();
			ArrayList<String> words	= (ArrayList<String>) StringToolkit.toWordsWithLines(text);
			String line	= "";

			for (int i = 0; i < words.size(); i++) {
				if (!words.get(i).equals("\n")) {
					String aux = words.get(i).replace("\u00A0", " ");
					//String aux = words.get(i).replace(" ", " ");
					float fw = (font.getStringWidth(line + " " + aux) / 1000.0f) * fontSize;
					if (fw < max) {
						line += (!AonStringUtils.isEmpty(line) ? " " : "") + aux;
					} else {
						lines.add(line);
						line = "" + aux;
					}
					if (i == words.size() - 1)
						lines.add(line);
				} else {
					lines.add(line);
					line = "";
				}
			}
			if (lines.isEmpty()) {
				lines.add(line);			
			}
			return lines;
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	public static String getFirstLine(String text, float max, PDFont font, float fontSize) throws IOException {

		ArrayList<String> words	= (ArrayList<String>) StringToolkit.toWords(text);
		String			  line	= "";

		for (int i = 0; i < words.size(); i++) {
			float fw = (font.getStringWidth(line + " " + words.get(i)) / 1000.0f) * fontSize;
			if (fw < max) {
				line += " " + words.get(i);
			} else {
				return line;
			}
			if (i == words.size() - 1)
				return line;
		}
		return line;
	}
	
	
	public static String croppedStringWholeWord(String text, double width, PDFont font, float fontSize) throws IOException {
		if (text == null)
			return null;
		String str = AonStringUtils.trimToEmpty(getFirstLine(text, (float) width, font, fontSize));
		if (AonStringUtils.equals(AonStringUtils.trimToEmpty(text), str)) {
			return str;
		} else
			return str + "...";
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Crops a String based on max width, font size, and
	 * font.<i>
	 * </p>
	 * 
	 * @param max &nbsp; maximum width
	 * @return String
	 */
	public static String croppedString(String text, double width, PDFont font, float fontSize) throws IOException {
		if (text == null)
			return text;

		String txt = text;
		float  fw  = (font.getStringWidth(text) / 1000.0f) * fontSize;
		while (fw > width)
		{
			text = text.substring(0, text.length() - 1);
			fw	 = (font.getStringWidth(text + "...") / 1000.0f) * fontSize;
		}

		return (txt.equals(text)) ? text : text + "...";
	}

	/**
	 * <p>
	 * <b>Description:</b> <i> Reescale an image.</i>
	 * </p>
	 * <p>
	 * <b>warning:</b> Use PdfImage instead
	 * </p>
	 * 
	 * @return float array
	 *         <h4>Positions:</h4>
	 *         <ol start='0'>
	 *         <li>scaled width</li>
	 *         <li>scaled height</li>
	 *         </ol>
	 * @see PdfImage
	 */
	public static float[] reescale(float width, float height, float maxWidth, float maxHeight) {

		float rel = width / height;

		while (width > maxWidth || height > maxHeight)
		{
			width--;
			height = width / rel;
		}

		return new float[] { width, height };
	}

	/**
	 * <p>
	 * <b>Description:</b> <i> Create a BufferedImage from byte array.</i>
	 * </p>
	 * 
	 * @param imageData byte array
	 * @return BufferedImage
	 */
	public static BufferedImage imageFromBytes(byte[] imageData) {
		ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
		try
		{
			BufferedImage data = ImageIO.read(bais);
			return data;
		} catch (IOException e)
		{
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Description:</b>
	 * </p>
	 * <i> Returns the string to be written where the percentage of the costs are.
	 * </i>
	 * </p>
	 * 
	 * @param percentage
	 * @return String to be written
	 */
	public static String drawCostPercentage(Optional<Double> percentage) {
		if (percentage.isEmpty())
			return "";
		else
			return toLatinNumber(safeDouble(percentage)) + " %";
	}
	
	/** 
	 * Get content by tag in pdf page 
	 * @param page - The page.
	 * @param name - The name of the tag
	 **/
	public static Optional<PDMarkedContent> getContent(PDPage page, String name) {
		
		try {
			PDFMarkedContentExtractor contentExtractor = new PDFMarkedContentExtractor();
			contentExtractor.processPage(page);
			List<PDMarkedContent> markedContents = contentExtractor.getMarkedContents();			
			
			for (PDMarkedContent pdMarkedContent : markedContents) {
				if(pdMarkedContent.getTag() != null && pdMarkedContent.getTag().equals(name))
					return Optional.ofNullable(pdMarkedContent);
			}
		} catch (IOException e) {}
		return Optional.empty();
	}

	public static Optional<PDMarkedContent> getContent(PDDocument document, String name) {

		PDPageTree pages = document.getPages();
		for (PDPage page : pages) {
			
			Optional<PDMarkedContent> content = getContent(page, name);
			if(content.isPresent())	
				return content;				
			
		}
		
		return Optional.empty();
	}

	public static void drawResizedLogo(PDDocument doc, PDPage page, PDPageContentStream contents, byte[] logo, float x, float y, float maxHeight, float maxWidth, String externalLink) throws IOException {
		float logoHeigth = 0;
		float logoWidth = 0;
		
		if (logo != null) {
			BufferedImage bufferedImage = null;
			bufferedImage = ImageIO.read(new ByteArrayInputStream(logo));
			logoHeigth = bufferedImage.getHeight();
			logoWidth = bufferedImage.getWidth();
			float proportion = logoHeigth/logoWidth;
			
			if (logoHeigth > maxHeight) {
				logoHeigth = maxHeight;
				logoWidth = logoHeigth / proportion;
			}
			if (logoWidth > maxWidth) {
				logoWidth = maxWidth;
				logoHeigth = logoWidth * proportion;
			}
			
			drawImage(doc, contents, logo, x, y, logoWidth, logoHeigth);
			
			if (externalLink != null) {
				PDRectangle rectangle = new PDRectangle(x, y, logoWidth, logoHeigth);
				PDAnnotationLink txtLink = new PDAnnotationLink();
				PDActionURI action = new PDActionURI();
				action.setURI(externalLink);
				txtLink.setAction(action);
				txtLink.setHidden(true);
				txtLink.setRectangle(rectangle);
				page.getAnnotations().add(txtLink);
			}
		}
	}
	
	public static void drawResizedCenteredLogo(PDDocument doc, PDPage page, PDPageContentStream contents, byte[] logo, float x, float y, float maxHeight, float maxWidth, String externalLink) throws IOException {
		float logoHeigth = 0;
		float logoWidth = 0;
		
		if (logo != null) {
			BufferedImage bufferedImage = null;
			bufferedImage = ImageIO.read(new ByteArrayInputStream(logo));
			logoHeigth = bufferedImage.getHeight();
			logoWidth = bufferedImage.getWidth();
			float proportion = logoHeigth/logoWidth;
			
			if (logoHeigth > maxHeight) {
				logoHeigth = maxHeight;
				logoWidth = logoHeigth / proportion;
			}
			if (logoWidth > maxWidth) {
				logoWidth = maxWidth;
				logoHeigth = logoWidth * proportion;
			}
			
			float finalX = x + (maxWidth - logoWidth) / 2;
			float finalY = y + (maxHeight - logoHeigth) / 2;
			
			drawImage(doc, contents, logo, finalX, finalY, logoWidth, logoHeigth);
			
			if (externalLink != null) {
				PDRectangle rectangle = new PDRectangle(x, y, logoWidth, logoHeigth);
				PDAnnotationLink txtLink = new PDAnnotationLink();
				PDActionURI action = new PDActionURI();
				action.setURI(externalLink);
				txtLink.setAction(action);
				txtLink.setHidden(true);
				txtLink.setRectangle(rectangle);
				page.getAnnotations().add(txtLink);
			}
		}
	}
	
	public static float getLogoFinalHeight(byte[] logo, float maxHeight, float maxWidth) throws IOException {
		if (logo == null)
			return 0;
		BufferedImage bufferedImage = null;
		bufferedImage = ImageIO.read(new ByteArrayInputStream(logo));
		float logoHeigth = bufferedImage.getHeight();
		float logoWidth = bufferedImage.getWidth();
		float proportion = logoHeigth/logoWidth;
		
		if (logoHeigth > maxHeight) {
			logoHeigth = maxHeight;
			logoWidth = logoHeigth / proportion;
		}
		if (logoWidth > maxWidth) {
			logoWidth = maxWidth;
			logoHeigth = logoWidth * proportion;
		}
		
		return logoHeigth;
	}
	
	public static float getLogoFinalWidth (byte[] logo, float maxHeight, float maxWidth) throws IOException {
		if (logo == null)
			return 0;
		BufferedImage bufferedImage = null;
		bufferedImage = ImageIO.read(new ByteArrayInputStream(logo));
		float logoHeigth = bufferedImage.getHeight();
		float logoWidth = bufferedImage.getWidth();
		float proportion = logoHeigth/logoWidth;
		
		if (logoHeigth > maxHeight) {
			logoHeigth = maxHeight;
			logoWidth = logoHeigth / proportion;
		}
		if (logoWidth > maxWidth) {
			logoWidth = maxWidth;
			logoHeigth = logoWidth * proportion;
		}
		
		return logoWidth;
	}
	
	

}
