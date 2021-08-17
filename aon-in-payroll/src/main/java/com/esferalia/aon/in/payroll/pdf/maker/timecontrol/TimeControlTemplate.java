package com.esferalia.aon.in.payroll.pdf.maker.timecontrol;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfText;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.timecontrol.bean.EmployeeData;

public class TimeControlTemplate {
	
	private static final DateFormat DF = new SimpleDateFormat("MMMMMMMMMM '/' yyyy", new Locale("es", "ES"));
//	private static final DateFormat DF_DAY = new SimpleDateFormat("d '-' EEEEEEEEE", new Locale("es", "ES"));
	private static final DateFormat DF_DAY_NAME = new SimpleDateFormat("EEEEEEEEE", new Locale("es", "ES"));
	
	PDDocument document;
	private float heigth;
	private float width;
	private float currentHeigth;
	PDPageContentStream contentStream;
	List<EmployeeData> employees;
	String enterpriseName;
	String cif;
	Date period;
	
	public static void print(OutputStream os, String enterpriseName, String cif, List<EmployeeData> employees, Date period) throws CanNotCreatePdfException {
		if (period == null)
			throw new CanNotCreatePdfException("Period cannot be null");
		else if (employees == null || employees.size() == 0)
			throw new CanNotCreatePdfException("No employees found");
		try {
			TimeControlTemplate tct = new TimeControlTemplate();
			tct.document = new PDDocument();
			tct.employees = employees;
			tct.enterpriseName = enterpriseName;
			tct.cif = cif;
			tct.period = period;
			for(EmployeeData emp : employees) {
				tct.addEmployeePage(emp);
			}
			tct.closeDocument(os);
		} catch (Exception e) {
			throw new CanNotCreatePdfException(e);
		}
	}
	
	
	public void addEmployeePage(EmployeeData employee) throws IOException {
		
		PDPage page = new PDPage();
		
		this.heigth = page.getMediaBox().getHeight();
		this.currentHeigth = heigth;
		this.width = page.getMediaBox().getWidth();
		
		this.contentStream = new PDPageContentStream(this.document, page);
		
		drawHeader(employee);
		drawTableHeader();
		drawTableBody();
		drawSignature();
		contentStream.close();
		
		this.document.addPage(page);
	}


	private void closeDocument(OutputStream os) throws IOException {
		contentStream.close();
		PDDocumentInformation pdd = document.getDocumentInformation();
		pdd.setTitle("REGISTRO JORNADA LABORAL");
		pdd.setCreator("AON SOLUTIONS");
		this.document.save("/home/igonzalez/Escritorio/pedefes/prueba.pdf");
		this.document.save(os);
		this.document.close();
	}
	
	private void drawTitle(PDPageContentStream contentStream, String employee, Date period) throws IOException {
		
		String employeeStr = employee != null ? PDFToolkit.croppedString(employee, width-100, PDType1Font.HELVETICA_BOLD, 10f) : "";
		
		currentHeigth -= 30;
		PdfText text = new PdfText(50f, currentHeigth, width-100, 20f, 6f, 6f, contentStream, employeeStr, Color.BLACK, PDType1Font.HELVETICA_BOLD, 10f, PdfSettings.ALIGNMENT.CENTER);
		text.draw();
		
		currentHeigth -= 15;
		text = new PdfText(50f, currentHeigth, width-100, 20f, 6f, 6f, contentStream, "REGISTRO JORNADA LABORAL", Color.BLACK, PDType1Font.HELVETICA_BOLD, 10f, PdfSettings.ALIGNMENT.CENTER);
		text.draw();
		
		currentHeigth -= 15;
		text = new PdfText(50, currentHeigth, width-100, 20f, 6f, 6f, contentStream, DF.format(period).toUpperCase(), Color.BLACK, PDType1Font.HELVETICA_BOLD, 10f, PdfSettings.ALIGNMENT.CENTER);
		text.draw();
	}
	
	private void drawFirstRow(PDPageContentStream contentStream, String enterpriseName, String cif) throws IOException {
		this.currentHeigth -= 25;
		PDFToolkit.drawBorderedBox(contentStream, 50, currentHeigth, 400, 17.5f, Color.DARK_GRAY, 1f);
		String entStr = enterpriseName != null ? enterpriseName : "";
		PdfText text = new PdfText(50f, currentHeigth, 400f, 17.5f, 4.5f, 4.5f, contentStream, "Empresa: " + entStr, Color.BLACK, PDType1Font.HELVETICA, 10f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		PDFToolkit.drawBorderedBox(contentStream, 450, currentHeigth, (width-50-400-50), 17.5f, Color.DARK_GRAY, 1f);
		String cifStr = cif != null ? cif : "";
		text = new PdfText(450f, currentHeigth, (width-50-400-50), 17.5f, 4.5f, 4.5f, contentStream, "NIF: " + cifStr, Color.BLACK, PDType1Font.HELVETICA, 10f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
	}
	
	private void drawSecondRow(PDPageContentStream contentStream, String dni, String contract, String naf) throws IOException {
		this.currentHeigth -= 17.5;
		PDFToolkit.drawBorderedBox(contentStream, 50, currentHeigth, 90, 17.5f, Color.DARK_GRAY, 1f);
		PdfText text = new PdfText(50f, currentHeigth, 90f, 17.5f, 4.5f, 4.5f, contentStream, "Datos trabajador:", Color.BLACK, PDType1Font.HELVETICA, 10f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		
		PDFToolkit.drawBorderedBox(contentStream, 140, currentHeigth, 310/2, 17.5f, Color.DARK_GRAY, 1f);
		String dniStr = dni != null ? dni : "";
		text = new PdfText(140f, currentHeigth, 310/2, 17.5f, 4.5f, 4.5f, contentStream, "DNI: "+ dniStr, Color.BLACK, PDType1Font.HELVETICA, 10f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		
		PDFToolkit.drawBorderedBox(contentStream, 295, currentHeigth, 310/2, 17.5f, Color.DARK_GRAY, 1f);
		String nafStr = naf != null ? naf : "";
		text = new PdfText(295f, currentHeigth, 310/2, 17.5f, 4.5f, 4.5f, contentStream, "NAF: "+ nafStr, Color.BLACK, PDType1Font.HELVETICA, 10f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
		
		PDFToolkit.drawBorderedBox(contentStream, 450, currentHeigth, (width-50-400-50), 17.5f, Color.DARK_GRAY, 1f);
		String contractStr = contract != null ? contract : "";
		text = new PdfText(450f, currentHeigth, (width-50-400-50), 17.5f, 4.5f, 4.5f, contentStream, "T.C.: " + contractStr, Color.BLACK, PDType1Font.HELVETICA, 10f, PdfSettings.ALIGNMENT.LEFT);
		text.draw();
	}
	
	private void drawHeader(EmployeeData employee) throws IOException {
		String employeeName = employee.getEmployeeName() != null ? employee.getEmployeeName().toUpperCase() : "";
		String dni = employee.getDni() != null ? employee.getDni().toUpperCase() : "";
		String contract = employee.getContract() != null ? employee.getContract().toUpperCase() : "";
		String naf = employee.getNaf() != null ? employee.getNaf().toUpperCase() : "";
		
		drawTitle(contentStream, employeeName, this.period);
		drawFirstRow(contentStream, enterpriseName, cif);
		drawSecondRow(contentStream, dni, contract, naf);
	}
	
	
	
	private void drawTableHeader() throws IOException {
		currentHeigth -= 25;
		PDFToolkit.drawBorderedBox(contentStream, 50, currentHeigth, 90, 15, Color.DARK_GRAY, 1f);
		PdfText text = new PdfText(50f, currentHeigth, 90f, 15f, 3f, 3f, contentStream, "DÍA", Color.BLACK, PDType1Font.HELVETICA_BOLD, 10f, PdfSettings.ALIGNMENT.CENTER);
		text.draw();
		
		float currentX = 140f;
		float each = (450 - 140) / 6f;
		for (int i=0; i<3; i++) {
			PDFToolkit.drawBorderedBox(contentStream, currentX, currentHeigth, each, 15, Color.DARK_GRAY, 1f);
			text = new PdfText(currentX, currentHeigth, each, 15f, 3f, 3f, contentStream, "Entrada", Color.BLACK, PDType1Font.HELVETICA_BOLD, 10f, PdfSettings.ALIGNMENT.CENTER);
			text.draw();
			currentX += each;
			PDFToolkit.drawBorderedBox(contentStream, currentX, currentHeigth, each, 15, Color.DARK_GRAY, 1f);
			text = new PdfText(currentX, currentHeigth, each, 15f, 3f, 3f, contentStream, "Salida", Color.BLACK, PDType1Font.HELVETICA_BOLD, 10f, PdfSettings.ALIGNMENT.CENTER);
			text.draw();
			currentX += each;
		}
		
		
		PDFToolkit.drawBorderedBox(contentStream, 450, currentHeigth, (width-50-400-50), 15, Color.DARK_GRAY, 1f);
		text = new PdfText(450f, currentHeigth, (width-50-400-50), 15f, 3f, 3f, contentStream, "Firma", Color.BLACK, PDType1Font.HELVETICA_BOLD, 10f, PdfSettings.ALIGNMENT.CENTER);
		text.draw();
	}
	
	private void drawTableBody() throws IOException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(period);
		int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		for (int day=1; day<=days; day++) {
			calendar.set(Calendar.DAY_OF_MONTH, day);
			String dayString = getDayName(calendar.getTime());
			
			currentHeigth -= 17.5;
			PDFToolkit.drawBorderedBox(contentStream, 50, currentHeigth, 90, 17.5f, Color.DARK_GRAY, 1f);
			PdfText text = new PdfText(50f, currentHeigth, 20f, 17.5f, 4f, 4f, contentStream, String.valueOf(day), Color.BLACK, PDType1Font.HELVETICA_BOLD, 10f, PdfSettings.ALIGNMENT.CENTER);
			text.draw();
			text = new PdfText(70f, currentHeigth, 70f, 17.5f, 4f, 4f, contentStream, dayString, Color.BLACK, PDType1Font.HELVETICA_BOLD, 10f, PdfSettings.ALIGNMENT.LEFT);
			text.draw();
			
			float currentX = 140f;
			float each = (450 - 140) / 6f;
			
			for (int i=0; i<3; i++) {
				PDFToolkit.drawBorderedBox(contentStream, currentX, currentHeigth, each, 17.5f, Color.DARK_GRAY, 1f);
				currentX += each;
				PDFToolkit.drawBorderedBox(contentStream, currentX, currentHeigth, each, 17.5f, Color.DARK_GRAY, 1f);
				currentX += each;
			}
			
			PDFToolkit.drawBorderedBox(contentStream, 450, currentHeigth, (width-50-400-50), 17.5f, Color.DARK_GRAY, 1f);
			
		}
	}
	private void drawSignature() throws IOException {
		currentHeigth -= 15;
		PdfText text = new PdfText(50, currentHeigth, width/2-50, 17.5f, 4f, 4f, contentStream, "Firma y Sello Empresa", Color.BLACK, PDType1Font.HELVETICA_BOLD, 10f, PdfSettings.ALIGNMENT.CENTER);
		text.draw();
		text = new PdfText(width/2, currentHeigth, width/2-50, 17.5f, 4f, 4f, contentStream, "Firma del Trabajador", Color.BLACK, PDType1Font.HELVETICA_BOLD, 10f, PdfSettings.ALIGNMENT.CENTER);
		text.draw();
		
		PDFToolkit.drawBorderedBox(contentStream, 50, 20, (width-100), currentHeigth - 60, Color.LIGHT_GRAY, 1f);
		PDFToolkit.drawBox(contentStream, 60, 20 + currentHeigth - 67.5f, 40, 15, Color.WHITE);
		
		text = new PdfText(60, 20 + currentHeigth - 67.5f, 40, 15f, 4.5f, 4.5f, contentStream, "Notas:", Color.GRAY, PDType1Font.HELVETICA, 10f, PdfSettings.ALIGNMENT.CENTER);
		text.draw();
		
	}
	
	private static String getDayName(Date date) {
		if (date == null)
			return null;
		String dateStr = DF_DAY_NAME.format(date);
		dateStr = dateStr.substring(0,1).toUpperCase() + dateStr.substring(1);
		return dateStr;
	}
	
	
}
