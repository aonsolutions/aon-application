package net.aonsolutions.aon.report.pdf;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import net.aonsolutions.aon.registry.report.IHeader;

public abstract class AbsReportTablePDF<T> extends PdfPTable implements Consumer<T> {
	
	private static final Font BODY_FONT = new Font(Font.FontFamily.HELVETICA, 8);
	private static final Font BODY_FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
	
	protected AbsReportTablePDF(IHeader[] headers) {
		super(headers.length);
		this.setLockedWidth(true);
		this.setHeaderRows(1);
		try {
			float[] widths = new float[ headers.length ];
			IntStream.range(0, headers.length).forEach( i -> widths[i] = headers[i].getpdfWidth());
			this.setTotalWidth(widths);
		} catch (DocumentException e) {
			// Imposible
		}
		Arrays.stream(headers).forEach(h -> addHeader( h.getLabel()));
	}
	
	protected void addStringCell(String content) {
		Paragraph paragraph = new Paragraph(8, AonStringUtils.defaultString(content), BODY_FONT);
		PdfPCell cell = new PdfPCell();
		cell.addElement(paragraph);
		cell.setBorder(0);
		this.addCell(cell);
	}
	
	protected void addIntegerCell(Integer content) {
		Paragraph paragraph = new Paragraph(8, AonStringUtils.defaultString(AonNumberUtils.toString(content)), BODY_FONT);
		PdfPCell cell = new PdfPCell();
		cell.addElement(paragraph);
		cell.setBorder(0);
		this.addCell(cell);
	}
	
	private AbsReportTablePDF<T> addHeader(String label) {
		Paragraph paragraph = new Paragraph (8,label,BODY_FONT_BOLD);
		paragraph.setAlignment(Element.ALIGN_LEFT);
		PdfPCell cell = new PdfPCell();
		cell.setBorder(0);
		cell.setBorderWidthBottom(1);
		cell.addElement(paragraph);
		this.addCell(cell);
		return this;
	}
}
