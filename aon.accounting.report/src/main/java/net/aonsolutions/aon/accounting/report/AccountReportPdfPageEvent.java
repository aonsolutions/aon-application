package net.aonsolutions.aon.accounting.report;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class AccountReportPdfPageEvent extends PdfPageEventHelper {

	private ReportMetadata metadata;
	private static SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static Font HEADER_FONT_COVER_0 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
	private static Font HEADER_FONT_COVER_1 = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);
	private static Font HEADER_FONT_0 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
	private static Font HEADER_FONT_1 = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
	private static Font HEADER_FONT_2 = new Font(Font.FontFamily.HELVETICA, 6);

	public AccountReportPdfPageEvent(ReportMetadata metadata) {
		this.metadata = metadata;
	}
	
	@Override
	public void onStartPage(PdfWriter writer, Document document) {
		PdfContentByte canvas = writer.getDirectContent();

		float pageWidth = document.getPageSize().getWidth();
		float pageHeight = document.getPageSize().getHeight();
		float w = pageWidth - document.leftMargin() - document.rightMargin();
		
		if (metadata.isShowCover() && writer.getPageNumber() == 1) {
			ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, new Phrase(this.metadata.getCompanyName(), HEADER_FONT_COVER_0)
					,(pageWidth - document.rightMargin() - 40)
					,pageHeight - (pageHeight / 5)
					,0);
			ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(this.metadata.getTitle(), HEADER_FONT_COVER_1)
					,(pageWidth / 2)
					,(pageHeight / 2)
					,0);
			ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, new Phrase(this.metadata.getFilterDescription(), HEADER_FONT_0)
					,(pageWidth - document.rightMargin() - 40)
					,(pageHeight / 5)
					,0);
			document.newPage();
		} else {
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
					new Phrase(this.metadata.getCompanyName(), HEADER_FONT_1), document.leftMargin(), pageHeight - 15, 0);
			
			Rectangle rect = new Rectangle((w / 2), pageHeight - 10, w + document.leftMargin(), pageHeight - 30);
			
			canvas.rectangle(rect);
			String filterDescrition = null;
			if (!metadata.isHideFilter()) {
				filterDescrition = this.metadata.getFilterDescription();
			}
			Paragraph p = new Paragraph(filterDescrition, HEADER_FONT_2);
			ColumnText ct = new ColumnText(canvas);
			ct.setAlignment(Element.ALIGN_RIGHT);
			ct.setLeading(8);
			ct.setSimpleColumn(rect);
			ct.setUseAscender(true);
			ct.addText(p);
			try {
				ct.go();
			} catch (DocumentException e) {
				throw new ExceptionConverter(e);
			}
			ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(this.metadata.getTitle(), HEADER_FONT_0),
					(pageWidth / 2), pageHeight - 40, 0);
		}
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		if (metadata.isShowCover() && writer.getPageNumber() == 1) {
			// Nothing
		} else {
			PdfContentByte canvas = writer.getDirectContent();
			
			float pageWidth = document.getPageSize().getWidth();
			
			canvas.setColorStroke(BaseColor.BLACK);
			canvas.moveTo(document.leftMargin(), document.bottom() - 10);
			canvas.lineTo(pageWidth - document.rightMargin(), document.bottom() - 10);
			canvas.closePathStroke();
			
			String footerText = metadata.isHideDateTimeOnFooter()
					?metadata.getFooterText()
							:TIME_FORMATTER.format(new Date());
					ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
							new Phrase(footerText, HEADER_FONT_1), document.leftMargin(),
							document.bottom() - 20, 0);
					if (metadata.getPageOffset() > 0) {
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase( 
										(AonStringUtils.isBlank(metadata.getPageOffsetText())?"Total P\u00E1g: ":metadata.getPageOffsetText() + " ")
										+ (metadata.getPageOffset() + writer.getPageNumber()), HEADER_FONT_1)
								,(document.getPageSize().getWidth() - document.rightMargin() - 40) / 2
								, document.bottom() - 20
								, 0);
					}
					ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
							new Phrase("P\u00E1g: " + writer.getPageNumber(), HEADER_FONT_1),
							(document.getPageSize().getWidth() - document.rightMargin() - 40), document.bottom() - 20, 0);
		}
		writer.flush();
	}
}
