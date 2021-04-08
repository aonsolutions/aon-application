package com.esferalia.aon.in.payroll.pdf.api.component.basic;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT.JUSTIFY;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.VERTICAL_ALIGNMENT.DOWN;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.OptionalToolkit.safeFloat;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.OptionalToolkit.safeString;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.OptionalToolkit.safeValue;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.croppedString;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawTextCenter;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawTextJustified;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawTextLeft;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawTextRight;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.VERTICAL_ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;

/**
 * <p>
 * <b>Description:</b><i> This class represents a text.</i>
 * </p>
 * 
 * @version 0.4-AK
 * @author akrck02
 */

public class PdfText extends PdfComponent {

    private String content;
    private Color  color;
    private PDFont font;
    private Float  fontSize;

    private ALIGNMENT	       horizontalAlignment;
    private VERTICAL_ALIGNMENT verticalAlignment;

    private List<String> lines;
    private float	 lineSpacing;

    private PdfText() {
    }

    private static PdfText instance() {
	return new PdfText();
    }

    // @Deprecated
    public PdfText(
	    float x, float y, float width, float height, PDPageContentStream stream, String content, Color color,
	    PDFont font, Float fontSize, ALIGNMENT alignment
    ) {
	this.startPointer();
	this.x(x);
	this.y(y);
	this.width(width);
	this.height(height);
	this.stream(stream);

	float fh = (font.getFontDescriptor().getCapHeight()) / 1000 * fontSize;

	this.content		 = content;
	this.color		 = color;
	this.font		 = font;
	this.fontSize		 = fontSize;
	this.horizontalAlignment = alignment;
	this.marginX(0);
	this.marginY((height - fh) / 2);
	getLines();
    }

    // @Deprecated
    public PdfText(
	    float x, float y, float width, float height, float marginX, PDPageContentStream stream, String content,
	    Color color, PDFont font, Float fontSize, ALIGNMENT alignment
    ) {
	this.startPointer();
	this.x(x);
	this.y(y);
	this.width(width);
	this.height(height);
	this.stream(stream);

	float fh = (font.getFontDescriptor().getCapHeight()) / 1000 * fontSize;

	this.content		 = content;
	this.color		 = color;
	this.font		 = font;
	this.fontSize		 = fontSize;
	this.horizontalAlignment = alignment;
	this.marginX(marginX);
	this.marginY((height - fh) / 2);
	getLines();
    }

    // @Deprecated
    public PdfText(
	    float x, float y, float width, float height, float marginX, float marginY, PDPageContentStream stream,
	    String content, Color color, PDFont font, Float fontSize, ALIGNMENT alignment
    ) {
	this.startPointer();
	this.x(x);
	this.y(y);
	this.width(width);
	this.height(height);
	this.stream(stream);
	this.content		 = content;
	this.color		 = color;
	this.font		 = font;
	this.fontSize		 = fontSize;
	this.horizontalAlignment = alignment;
	this.marginX(marginX);
	this.marginY(marginY);
	getLines();
    }

    private void verticalAlignCenter() {
	float fh = (font.getFontDescriptor().getCapHeight()) / 1000 * fontSize;
	this.marginY((height() - fh) / 2);
    }

    private void getLines() {
	try
	{
	    this.lines = PDFToolkit.getLines(content, width() - marginX(), font, fontSize);
	} catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

    /**
     * <p>
     * <b>Description:</b> <i>Draws the text according to alignment</i><br>
     * <b>Warning:</b> <i>to multiple line text use draw_multiple()</i>
     * 
     * @see PdfText.draw_multiple()
     *      </p>
     */
    @Override
    public void draw() {
	try
	{
	    float  fh	= (font.getFontDescriptor().getCapHeight()) / 1000 * fontSize;
	    String line	= lines.get(0);
	    if (lines.size() > 1)
		line = croppedString(lines.get(0), width(), font, font_size()) + "...";

	    line = line.trim();

	    switch (horizontalAlignment)
	    {
	    case CENTER:
		drawTextCenter(stream(), new PDRectangle(x(), y(), width(), height()), line, color, font, fontSize,
			marginY());
		break;
	    case RIGHT:
		drawTextRight(stream(), new PDRectangle(x(), y(), width(), height()), line, color, font, fontSize,
			marginX(), marginY());
		break;
	    case JUSTIFY:
		drawTextJustified(line, width(), fontSize, font, x(), y(), stream());
		break;
	    default:
		drawTextLeft(stream(), new PDRectangle(x(), y(), width(), height()), line, color, font, fontSize,
			marginX(), marginY());
		break;
	    }

	    down(fh + lineSpacing);
	    height(height() + fh + lineSpacing);
	} catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

    public int drawMultiple(float limit) {
	try
	{
	    float fh = (font.getFontDescriptor().getCapHeight()) / 1000 * fontSize;
	    for (int i = 0; i < lines.size(); i++)
	    {
		if (y() - (fh + lineSpacing) <= limit)
		    return i;
		String line = lines.get(i);
		if (i != 0)
		    line = " " + line;
		switch (horizontalAlignment)
		{
		case CENTER:
		    drawTextCenter(stream(), new PDRectangle(x(), y(), width(), height()), line, color, font, fontSize,
			    marginY());
		    break;
		case RIGHT:
		    drawTextRight(stream(), new PDRectangle(x(), y(), width(), height()), line, color, font, fontSize,
			    marginX(), marginY());
		    break;
		case JUSTIFY:
		    if (i < lines.size() - 1)
			drawTextJustified(line, width(), fontSize, font, x(), y(), stream());
		    else
			drawTextLeft(stream(), new PDRectangle(x(), y(), width(), height()), line, color, font,
				fontSize, 0, 0);
		    break;
		default:
		    drawTextLeft(stream(), new PDRectangle(x(), y(), width(), height()), line, color, font, fontSize,
			    marginX(), marginY());
		    break;
		}

		down(fh + lineSpacing);
		height(height() + fh + lineSpacing);
	    }
	    height(height() + fh + lineSpacing);
	} catch (IOException e)
	{
	    e.printStackTrace();
	}
	return -1;
    }

    public PdfText restart(PDPageContentStream contents, int line, float y) {
	stream(contents);
	lines = lines.subList(line, lines.size() - 1);

	height(height() / line);
	y(y);
	return this;
    }

    public String content() {
	return content;
    }

    public PdfText content(String content) {
	this.content = content;
	return this;
    }

    public Color color() {
	return color;
    }

    public PdfText color(Color color) {
	this.color = color;
	return this;
    }

    public PDFont font() {
	return font;
    }

    public PdfText font(PDFont font) {
	this.font = font;
	return this;
    }

    public Float font_size() {
	return fontSize;
    }

    public PdfText font_size(Float font_size) {
	this.fontSize = font_size;
	return this;
    }

    public Float line_spacing() {
	return this.lineSpacing;
    }

    public PdfText line_spacing(Float line_spacing) {
	this.lineSpacing = line_spacing;
	return this;
    }

    public ALIGNMENT horizontal_alignment() {
	return horizontalAlignment;
    }

    public PdfText horizontalAlignment(ALIGNMENT alignment) {
	this.horizontalAlignment = alignment;
	return this;
    }

    public VERTICAL_ALIGNMENT vertical_alignment() {
	return verticalAlignment;
    }

    public PdfText verticalAlignment(VERTICAL_ALIGNMENT alignment) {
	this.verticalAlignment = alignment;
	return this;
    }

    public static String describe() {
	return "PdfText:\t\t\t\t\tNormal text.";
    }

    // BUILDER
    public static class PdfTextBuilder {
	private float x;
	private float y;

	private float width;
	private float height;

	private float margin_x;
	private float marginY;

	private PDPageContentStream stream;

	private String content;
	private Color  color;
	private PDFont font;
	private Float  fontSize;

	private ALIGNMENT	   horizontal_alignment;
	private VERTICAL_ALIGNMENT vertical_alignment;

	private float lineSpacing;

	public PdfTextBuilder x(float x) {
	    this.x = x;
	    return this;
	}

	public PdfTextBuilder y(float y) {
	    this.y = y;
	    return this;
	}

	public PdfTextBuilder width(float width) {
	    this.width = width;
	    return this;
	}

	public PdfTextBuilder height(float height) {
	    this.height = height;
	    return this;
	}

	public PdfTextBuilder margin_x(float margin_x) {
	    this.margin_x = margin_x;
	    return this;
	}

	public PdfTextBuilder marginY(float marginY) {
	    this.marginY = marginY;
	    return this;
	}

	public PdfTextBuilder stream(PDPageContentStream stream) {
	    this.stream = stream;
	    return this;
	}

	public PdfTextBuilder content(String content) {
	    this.content = content;
	    return this;
	}

	public PdfTextBuilder color(Color color) {
	    this.color = color;
	    return this;
	}

	public PdfTextBuilder font(PDFont font) {
	    this.font = font;
	    return this;
	}

	public PdfTextBuilder lineSpacing(Float lineSpacing) {
	    this.lineSpacing = lineSpacing;
	    return this;
	}

	public PdfTextBuilder fontSize(Float fontSize) {
	    this.fontSize = fontSize;
	    return this;
	}

	public PdfTextBuilder horizontalAlignment(ALIGNMENT alignment) {
	    this.horizontal_alignment = alignment;
	    return this;
	}

	public PdfTextBuilder verticalAlignment(VERTICAL_ALIGNMENT alignment) {
	    this.vertical_alignment = alignment;
	    return this;
	}

	public PdfText build() {
	    PdfText component = instance();
	    if (this.stream == null)
		return null;

	    component.startPointer();
	    component.x(safeFloat(this.x, 0f));
	    component.y(safeFloat(this.y, 0f));
	    component.width(safeFloat(this.width, 0f));
	    component.height(safeFloat(this.height, 0f));
	    component.marginX(safeFloat(this.margin_x, 0f));
	    component.marginY(safeFloat(this.marginY, 0f));
	    component.stream(this.stream);
	    component.content(safeString(this.content, ""));
	    component.color((Color) safeValue(this.color, BLACK));
	    component.font((PDFont) safeValue(this.font, HELVETICA));
	    component.font_size(safeFloat(this.fontSize, 10f));
	    component.line_spacing(safeFloat(this.lineSpacing, 4f));
	    component.horizontalAlignment((ALIGNMENT) safeValue(this.horizontal_alignment, JUSTIFY));
	    component.verticalAlignment((VERTICAL_ALIGNMENT) safeValue(this.vertical_alignment, DOWN));

	    component.getLines();
	    component.verticalAlignCenter();

	    return component;
	}
    }

}
