package com.esferalia.aon.in.payroll.pdf.maker.warehouse;

import java.awt.Color;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;

public class DeliveryTemplate {
	private static final PDFont DEFAULT_FONT = PdfFonts.HELVETICA;
	private static final PDFont DEFAULT_BOLD_FONT = PdfFonts.HELVETICA_BOLD;
	
	private static final Color DEFAULT_FONT_COLOR = PdfColors.BLACK;
	
	OutputStream filename;
	private float marginTop;
	
	private float marginBarCode;
	private float heightBarCode;
	
	private float nameMargin;
	
	private PDDocument document;
	private PDPageContentStream contents;
	private PDPage page;
	private float x;
	private float y;
	
	private Delivery delivery;
	
	
	public DeliveryTemplate(Delivery delivery) throws CanNotCreatePdfException {
		try {
			if (delivery == null) {
				throw new CanNotCreatePdfException("No delivery");
			}
			
			this.delivery = delivery;
			
			this.document = new PDDocument();
			this.page = new PDPage(PDRectangle.A5);
			this.document.addPage(this.page);
			this.contents = new PDPageContentStream(this.document, this.page);
			
			this.marginTop = this.getPageWidth() * (10f / 210f);

			this.marginBarCode = this.getPageWidth() * ((15f) / 148f);
			this.heightBarCode = this.getPageHeight() * ((32f) / 210f);
			
			this.draw();
			this.contents.close();
		} catch (Exception e) {
			throw new CanNotCreatePdfException(e);
		}
	}
	
	private float getPageWidth() {
		if (this.page != null)
			return page.getMediaBox().getWidth();
		else
			return 0;
	}
	
	private float getPageHeight() {
		if (this.page != null)
			return page.getMediaBox().getHeight();
		else
			return 0;
	}
	
	private void draw() throws Exception {
		this.x = 0;
		this.y = this.getPageHeight() - this.marginTop;
		
		//this.drawDelivery();
	}
	
}
