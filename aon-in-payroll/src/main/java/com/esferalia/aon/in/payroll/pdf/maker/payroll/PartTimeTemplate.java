package com.esferalia.aon.in.payroll.pdf.maker.payroll;

import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawBox;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawImage;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.imageFromBytes;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.reescale;
import static com.esferalia.aon.watson.server.AonDateUtils.format;
import static com.esferalia.aon.watson.util.AonStringUtils.trimToEmpty;
import static java.awt.Color.BLACK;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;

import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfBox;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfText;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PartTimeParams;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PartTimeParams.PartTimeEntry;

public class PartTimeTemplate {

	private static final float MARGIN_TOP = 50f;
	private static final float MARGIN_SIDE = 50f;
	private static final float CELL_HEIGHT = 15f;
	private static final float TITLE_CELL_HEIGHT = 20f;
	private static final float[] COLUMN_PROPORTIONS = {15,42.5f,42.5f};
	
	private PDDocument document;
	private PDPageContentStream contentStream;
	private PartTimeParams params;
	private PDPage page;
	private float x;
	private float y;	
	
	public static void print(OutputStream os, PartTimeParams params) throws CanNotCreatePdfException {
		if (params == null)
			throw new CanNotCreatePdfException("Params cannot be null");
		
		try {
			PartTimeTemplate ptt = new PartTimeTemplate();
			ptt.document = new PDDocument();
			ptt.params = params;
			ptt.draw();
			ptt.closeDocument(os);
		} catch (Exception e) {
			throw new CanNotCreatePdfException(e);
		}
	}
	
	public static void append (PDDocument document, PartTimeParams params) throws CanNotCreatePdfException {
		if (params == null)
			throw new CanNotCreatePdfException("Params cannot be null");
		
		try {
			PartTimeTemplate ptt = new PartTimeTemplate();
			ptt.document = document;
			ptt.params = params;
			ptt.draw();
		} catch (Exception e) {
			throw new CanNotCreatePdfException(e);
		}
	}
	
	private void draw() throws CanNotCreatePdfException {
		try {
			this.page = PDFToolkit.createVerticalPage();
			this.contentStream = new PDPageContentStream(this.document, page);
			
			this.x = MARGIN_SIDE;
			this.y = getPageHeight()- MARGIN_TOP;
			
			drawHeader();
			drawEntries();
			drawFooter();
			contentStream.close();
			this.document.addPage(page);
		} catch (IOException e) {
			throw new CanNotCreatePdfException(e.getMessage());
		}
	}
	
	private void drawHeader() throws IOException {
		drawTitle();
		drawSubTitle();
		drawEnterpriseBox();
		drawEmployeeBox();
		y -= 10;
	}
	private void drawEntries() throws IOException {
		x = MARGIN_SIDE;
		float jailStartY = y;
		drawJail();
		y = jailStartY;
		drawContent();
	}
	
	
	private void drawContent() {
		y  -= TITLE_CELL_HEIGHT;
		String dayTitle = "DÍA";
		String ordinaryTitle = "HORAS ORDINARIAS";
		String complementaryTitle = "HORAS COMPLEMENTARIAS";
		
		float jailWidth = getPageWidth() - MARGIN_SIDE * 2;
		float titleCellFontSize = 11f;
		float normalCellFontSize = 10f;
		
		float[] startPoints = {MARGIN_SIDE, 0, 0};
		
		for (int i=1; i<COLUMN_PROPORTIONS.length; i++) {
			float proportion = COLUMN_PROPORTIONS[i-1] / 100;
			float width = jailWidth * proportion;
			x +=width;
			startPoints[i] = x;
		}
		
		PdfText text = new PdfText(startPoints[0], y, startPoints[1] - startPoints[0], TITLE_CELL_HEIGHT, contentStream, dayTitle, BLACK, new PDType1Font(FontName.HELVETICA), titleCellFontSize, PdfSettings.ALIGNMENT.CENTER);
		text.draw();
		text = new PdfText(startPoints[1], y, startPoints[2] - startPoints[1], TITLE_CELL_HEIGHT, contentStream, ordinaryTitle, BLACK, new PDType1Font(FontName.HELVETICA), titleCellFontSize, PdfSettings.ALIGNMENT.CENTER);
		text.draw();
		text = new PdfText(startPoints[2], y, getPageWidth() - MARGIN_SIDE - startPoints[2], TITLE_CELL_HEIGHT, contentStream, complementaryTitle, BLACK, new PDType1Font(FontName.HELVETICA), titleCellFontSize, PdfSettings.ALIGNMENT.CENTER);
		text.draw();
		
		for (int i=1; i<=params.getEntries().size(); i++) {
			y -= CELL_HEIGHT;
			String day = String.valueOf(i);
			
			text = new PdfText(MARGIN_SIDE, y, startPoints[1] - MARGIN_SIDE, CELL_HEIGHT, contentStream, day, BLACK, new PDType1Font(FontName.HELVETICA), normalCellFontSize, PdfSettings.ALIGNMENT.CENTER);
			text.draw();
			
			PartTimeEntry entry = params.getEntry(i);

			String ordinaryHours = entry.getOrdinary() != null ? String.format("%.2f", entry.getOrdinary()) : "0,00";
			text = new PdfText(startPoints[1], y, startPoints[2] - startPoints[1], CELL_HEIGHT, contentStream, entry.isHoliday() ? "VACACIONES" : ordinaryHours, BLACK, new PDType1Font(FontName.HELVETICA), normalCellFontSize, PdfSettings.ALIGNMENT.CENTER);
			text.draw();

			String complementaryHours = entry.getComplementary() != null ? String.format("%.2f", entry.getComplementary()) : "";
			text = new PdfText(startPoints[2], y, getPageWidth() - MARGIN_SIDE - startPoints[2], CELL_HEIGHT, contentStream, complementaryHours, BLACK, new PDType1Font(FontName.HELVETICA), normalCellFontSize, PdfSettings.ALIGNMENT.CENTER);
			text.draw();
		}
		
	}

	private void drawJail() throws IOException {
		int totalDays = params.getEntries().size();
		float jailHeight = totalDays * CELL_HEIGHT + TITLE_CELL_HEIGHT;
		float jailWidth = getPageWidth() - MARGIN_SIDE * 2;
		
		float[] startPoints = {MARGIN_SIDE, 0, 0};
		
		drawBox(contentStream, MARGIN_SIDE, y, 1f, -jailHeight, BLACK);
		for (int i=1; i<COLUMN_PROPORTIONS.length; i++) {
			float proportion = COLUMN_PROPORTIONS[i-1] / 100;
			float width = jailWidth * proportion;
			x +=width;
			startPoints[i] = x;
			drawBox(contentStream, x, y, 1f, -jailHeight, BLACK);
			
		}
		drawBox(contentStream, MARGIN_SIDE + jailWidth, y + 1f, 1f, -jailHeight - 1f, BLACK); //1f added to close properly
		
		x = MARGIN_SIDE;
		
		drawBox(contentStream, x, y, jailWidth, 1f, BLACK);
		y -= TITLE_CELL_HEIGHT;
		drawBox(contentStream, x, y, jailWidth, 1f, BLACK);
		for (int i=0; i<totalDays; i++) {
			y -= CELL_HEIGHT;
			drawBox(contentStream, x, y, jailWidth, 1f, BLACK);
		}
	}
	
	
	private void drawFooter() {
		y -= 20;
		
		float originalY = y;
		
		x = MARGIN_SIDE;
		
		String recibi = "Recibí el";
		PdfText text = new PdfText(x, y, getPageWidth() - MARGIN_SIDE * 2, 10f, contentStream, recibi, BLACK, new PDType1Font(FontName.HELVETICA), 10f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		
		x += 50;
		
		String paymentDateStr = trimToEmpty(format(params.getPaymentDate(), "dd/MM/yyyy"));
		text = new PdfText(x, y, getPageWidth() - MARGIN_SIDE * 2, 10f, contentStream, paymentDateStr, BLACK, new PDType1Font(FontName.HELVETICA), 10f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		
		x = MARGIN_SIDE;
		
		String byEnterprise = "POR LA EMPRESA";
		text = new PdfText(x, y, getPageWidth() - MARGIN_SIDE * 2, 10f, contentStream, byEnterprise, BLACK, new PDType1Font(FontName.HELVETICA), 10f, PdfSettings.ALIGNMENT.RIGHT);
		text.draw();
		
		y -= 20;
		
		String theWorker = "EL TRABAJADOR";
		text = new PdfText(x, y, 150, 10f, contentStream, theWorker, BLACK, new PDType1Font(FontName.HELVETICA), 10f, PdfSettings.ALIGNMENT.RIGHT);
		text.draw();
		
		y -= 15;
		
		String signed = "Fdo.: Nombre y apellidos";
		text = new PdfText(x, y, 150, 10f, contentStream, signed, BLACK, new PDType1Font(FontName.HELVETICA), 10f, PdfSettings.ALIGNMENT.RIGHT);
		text.draw();
		
		y = originalY - 55;
		
		x = getPageWidth() - MARGIN_SIDE - 25;

		try
		{
			BufferedImage img	 = imageFromBytes(params.getEnterpriseSignature());
			if (img != null) {				
				float[]		  scales = reescale(img.getWidth(), img.getHeight(), 100, 50);
				drawImage(document, contentStream, params.getEnterpriseSignature(), x - scales[0], y, scales[0], scales[1]);
			}
		} catch (IOException | NullPointerException e){
			e.printStackTrace();
		}
		y -= 15;
		text = new PdfText(MARGIN_SIDE, y, getPageWidth() - MARGIN_SIDE * 2, 10f, contentStream, signed, BLACK, new PDType1Font(FontName.HELVETICA), 10f, PdfSettings.ALIGNMENT.RIGHT);
		text.draw();
		
		y -= 15;
		
		String enterpriseSign = "Sello de la empresa";
		text = new PdfText(MARGIN_SIDE, y, getPageWidth() - MARGIN_SIDE * 2, 10f, contentStream, enterpriseSign, BLACK, new PDType1Font(FontName.HELVETICA), 10f, PdfSettings.ALIGNMENT.RIGHT);
		text.draw();

	}

	private void drawTitle() throws IOException {
		String titleText = "REGISTRO DE LA JORNADA DE LOS TRABAJADORES A TIEMPO PARCIAL";
		PdfText text = new PdfText(x, y, getPageWidth() - MARGIN_SIDE * 2, 14f, contentStream, titleText, BLACK, new PDType1Font(FontName.HELVETICA_BOLD), 12f, PdfSettings.ALIGNMENT.CENTER);
		text.draw();
		float titleWidth = getStringWidth(titleText, 12, new PDType1Font(FontName.HELVETICA_BOLD));
		x = (getPageWidth() - titleWidth) / 2;
		PdfBox underLine = new PdfBox(x, y, titleWidth, 2f, BLACK, contentStream);
		underLine.draw();
		y -= 40;
	}
	private void drawSubTitle() {
		x = MARGIN_SIDE;
		String subTitleText = "En cumplimiento de la obligación establecida en el Art. 12.4 c) del Estatuto de los Trabajadores";
		PdfText text = new PdfText(x, y, getPageWidth() - MARGIN_SIDE * 2, 11f, contentStream, subTitleText, BLACK, new PDType1Font(FontName.HELVETICA), 11f, PdfSettings.ALIGNMENT.CENTER);
		text.draw();
		
		y -= 60;
	}

	private void drawEnterpriseBox() throws IOException {
		x = MARGIN_SIDE;
		float boxWidth = (getPageWidth() - MARGIN_SIDE * 2) / 2 - 2.5f;
		PDFToolkit.drawBorderedBox(contentStream, x, y, boxWidth, 50f, BLACK, 1f);
		
		
		String enterpriseTitle = "Empresa:";
		String cccTitle = "CCC:";
		String nifTitle = "NIF:";
		
		PdfText text = new PdfText(x + 2.5f, y + 35, getPageWidth() - MARGIN_SIDE * 2, 11f, contentStream, enterpriseTitle, BLACK, new PDType1Font(FontName.HELVETICA), 11f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		float origin = getStringWidth(enterpriseTitle, 11f, new PDType1Font(FontName.HELVETICA)) + 5;
		text = new PdfText(x + origin, y + 35, boxWidth - origin, 10f, contentStream, trimToEmpty(params.getEnterpriseName()).toUpperCase(), BLACK, new PDType1Font(FontName.HELVETICA_BOLD), 10f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		
		text = new PdfText(x + 2.5f, y + 20, getPageWidth() - MARGIN_SIDE * 2, 11f, contentStream, cccTitle, BLACK, new PDType1Font(FontName.HELVETICA), 11f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		origin = getStringWidth(cccTitle, 11f, new PDType1Font(FontName.HELVETICA)) + 5;
		text = new PdfText(x + origin, y + 20, boxWidth - origin, 11f, contentStream, trimToEmpty(params.getEnterpriseCCC()), BLACK, new PDType1Font(FontName.HELVETICA), 11f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		
		text = new PdfText(x + 2.5f, y + 5, getPageWidth() - MARGIN_SIDE * 2, 11f, contentStream, nifTitle, BLACK, new PDType1Font(FontName.HELVETICA), 11f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		origin = getStringWidth(nifTitle, 11f, new PDType1Font(FontName.HELVETICA)) + 5;
		text = new PdfText(x + origin, y + 5, boxWidth - origin, 11f, contentStream, trimToEmpty(params.getEnterpriseDocument()), BLACK, new PDType1Font(FontName.HELVETICA), 11f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
	
	}
	private void drawEmployeeBox() throws IOException {
		x += (getPageWidth() - MARGIN_SIDE * 2) / 2 + 2.5f;
		float boxWidth = (getPageWidth() - MARGIN_SIDE * 2) / 2 - 2.5f;
		PDFToolkit.drawBorderedBox(contentStream, x, y, (getPageWidth() - MARGIN_SIDE * 2) / 2 - 2.5f, 50f, BLACK, 1f);
		
		String employeeTitle = "Trabajador:";
		String hoursTitle = "Nº horas según contrato:";
		String monthTitle = "Mes:";
		
		PdfText text = new PdfText(x + 2.5f, y + 35, getPageWidth() - MARGIN_SIDE * 2, 11f, contentStream, employeeTitle, BLACK, new PDType1Font(FontName.HELVETICA), 11f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		float origin = getStringWidth(employeeTitle, 11f, new PDType1Font(FontName.HELVETICA)) + 5;
		text = new PdfText(x + origin, y + 35, boxWidth - origin, 10f, contentStream, trimToEmpty(params.getEmployeeName()).toUpperCase(), BLACK, new PDType1Font(FontName.HELVETICA_BOLD), 10f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		
		text = new PdfText(x + 2.5f, y + 20, getPageWidth() - MARGIN_SIDE * 2, 11f, contentStream, hoursTitle, BLACK, new PDType1Font(FontName.HELVETICA), 11f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		origin = getStringWidth(hoursTitle, 11f, new PDType1Font(FontName.HELVETICA)) + 5;
		text = new PdfText(x + origin, y + 20, boxWidth - origin, 11f, contentStream, params.getContractHours() != null ? trimToEmpty(String.valueOf(params.getContractHours())) : "", BLACK, new PDType1Font(FontName.HELVETICA), 11f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		
		text = new PdfText(x + 2.5f, y + 5, getPageWidth() - MARGIN_SIDE * 2, 11f, contentStream, monthTitle, BLACK, new PDType1Font(FontName.HELVETICA), 11f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		String dateStr = trimToEmpty(format(params.getPayrollDate(), "MMMMMMMMMM"));
		dateStr = dateStr.length() > 0 ? dateStr.substring(0, 1).toUpperCase() + dateStr.substring(1) : dateStr;
		
		origin = getStringWidth(monthTitle, 11f, new PDType1Font(FontName.HELVETICA)) + 5;
		text = new PdfText(x + origin, y + 5, boxWidth - origin, 11f, contentStream, dateStr, BLACK, new PDType1Font(FontName.HELVETICA), 11f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
	}

		


	private void closeDocument(OutputStream os) throws IOException {
		contentStream.close();
		PDDocumentInformation pdd = document.getDocumentInformation();
		pdd.setTitle("REGISTRO JORNADA TIEMPO PARCIAL");
		pdd.setCreator("AON SOLUTIONS");
		this.document.save(os);
		this.document.close();
	}
	
	private float getPageHeight() {
		if (this.page != null)
			return page.getMediaBox().getHeight();
		else
			return 0;
	}
	
	private float getPageWidth() {
		if (this.page != null)
			return page.getMediaBox().getWidth();
		else
			return 0;
	}
	
	private static float getStringWidth(String str, float fontSize, PDFont font) {
		try {
			return fontSize * font.getStringWidth(str) / 1000;
		} catch (IOException e) {
			return 0;
		}
	}
}
