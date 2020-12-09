package com.esferalia.aon.in.payroll.pdf.creators.enterprisePayroll;

import com.esferalia.aon.in.payroll.pdf.creators.PDFToolkit;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class EnterprisePayrollTemplate {

	public static void createAonPdf(EnterprisePayroll payroll) throws IOException {

		//CONSTANTS
		final String filename = "./sample.pdf";
		final Color white = new Color(0xffffff);
		final Color black = new Color(0x000000);
		final PDType1Font helvetica_bold = PDType1Font.HELVETICA_BOLD;
		final PDType1Font helvetica = PDType1Font.HELVETICA;
		final DecimalFormat df = new DecimalFormat("#.00");


		try (PDDocument doc = new PDDocument()) {

			//CALCULATE PAGES
			Set<String> categories = payroll.getEntries().keySet();
			for (String key : categories) {
				System.out.println("\n---------------------"+key.toUpperCase()+"------------------");
				Map<String, EnterprisePayrollEntry> entries = payroll.getEntries().get(key);
				Set<String> entry_keys = entries.keySet();

				if(payroll.getSs_entries().containsKey(key)) System.err.println("Category found in ss: " + key);

				for (String employee : entry_keys) {
					System.out.println(">>>>>"+employee.toUpperCase()+"<<<<<");
					System.out.println("\t"+entries.get(employee) + "\n");

				}
			}



			//PRINT PDF
			PDPageContentStream contents = drawHeader(doc, payroll, black, white, helvetica_bold, helvetica);
			//drawContent();

			/*
			String month_str = PDFToolkit.formatDate(payroll.getMonth(), "MMMM, yyyy").get();
			month_str = String.valueOf(month_str.charAt(0)).toUpperCase() + month_str.substring(1);

			float x = 30; float y = 465;
			PDFToolkit.drawText(contents, month_str, x, 480f, black, helvetica_bold,7);
			PDFToolkit.drawText(contents, "Centro de EXTRAS", x + 115, 480f, black, helvetica_bold,7);


			for (EnterprisePayrollEntry entry : extras) {
				drawEmployee(contents,x,y,black, helvetica,7,entry,df);
				y -= 12;
			}

			y -= 10;
			PDFToolkit.drawText(contents, month_str, x, y, black, helvetica_bold, 7);
			PDFToolkit.drawText(contents, "Centro de ATRASOS", x + 110, y, black, helvetica_bold, 7);

			y -= 15; x = 30;
			for (EnterprisePayrollEntry entry : atrasos) {
				drawEmployee(contents, x, y, black, helvetica, 7,entry,df);
				y -= 12;
			}
			*/

			contents.close();
			doc.save(filename);

		}
	}

	private static PDPageContentStream drawHeader(PDDocument doc, EnterprisePayroll payroll, Color regular_color, Color header_color, PDType1Font bold_font, PDType1Font regular_font) throws IOException {
		final String[] tableHeaders = {"Trabajador", "Tipo", "Devengado", "S.S. Trab.", "I.R.P.F", "Deducciones", "Liquido", "S.S. Empr", "CosteTotal", "Total S.S"};

		String fileTitle = "Nómina de la empresa";
		String enterprise = "EMPRESA:";
		String date = PDFToolkit.formatDate(new Date(),"dd/MM/yyyy").get();

		PDPage page = PDFToolkit.createHorizontalPage();
		doc.addPage(page);
		PDPageContentStream contents = new PDPageContentStream(doc, page);

		float width = 75, height = width / 3.142857143f;
		PDFToolkit.drawImage(doc, contents, payroll.getLogo(), width, height);
		PDFToolkit.drawText(contents, fileTitle, 120f, 550f,regular_color,bold_font, 13);
		PDFToolkit.drawText(contents, enterprise, 120f, 535f,regular_color,bold_font, 9);
		PDFToolkit.drawText(contents, payroll.getSubheader(), 180f, 535f, regular_color,bold_font, 9);

		PDFToolkit.drawText(contents,  date, 770f, 535f, regular_color, regular_font, 8);
		PDFToolkit.drawBox(contents, 25, 495, 190, 14.5f,regular_color);
		PDFToolkit.drawText(contents, tableHeaders[0], 29f, 498f, header_color, bold_font, 9.2f);

		float x = 216, y = 495, w = 65, h = 14.5f;
		for (int i = 1; i <= 9; i++, x += 66) {
			PDFToolkit.drawBox(contents, x, y, w, h,regular_color);
			PDFToolkit.drawText(contents, tableHeaders[i], x + 4, y + 3,header_color, bold_font,9.2f);
		}

		return contents;
	}

	private static void drawEmployee(PDPageContentStream contents, float x, float y, Color color, PDType1Font font, float fontSize, EnterprisePayrollEntry entry, DecimalFormat df) throws IOException {
		PDFToolkit.drawText(contents, entry.empleado, x, y, color, font, fontSize);
		PDFToolkit.drawText(contents, entry.tipo, x+190, y, color, font, fontSize);
		PDFToolkit.drawText(contents, df.format(entry.devengado), x+257, y, color, font, fontSize);
		PDFToolkit.drawText(contents, df.format(entry.ssTrab), x+325, y, color, font, fontSize);
		PDFToolkit.drawText(contents, df.format(entry.irpf), x+390, y, color, font, fontSize);
		PDFToolkit.drawText(contents, df.format(entry.deducciones), x+457, y, color, font, fontSize);
		PDFToolkit.drawText(contents, df.format(entry.liquido), x+522, y, color, font, fontSize);
		PDFToolkit.drawText(contents, df.format(entry.ssEmpr), x+588, y, color, font, fontSize);
		PDFToolkit.drawText(contents, df.format(entry.costeTotal), x+654, y, color, font, fontSize);
		PDFToolkit.drawText(contents, df.format(entry.ssTotal), x+718, y, color, font, fontSize);
	}

	//[DEBUG] GETTING THINGS DONE...
	public static void main(String[] args) throws IOException {
		HashMap<String,EnterprisePayrollEntry> extras = new HashMap<>();
		HashMap<String,EnterprisePayrollEntry> atrasos = new HashMap<>();

		String[] extras_arr = {"DIAZ PEREZ ANA","COCA FUENTES PATRICIA"};
		String[] atrasos_arr = {"CASTELLANO HURTADO EUGENIO","RUESGAS GARCIA SHEILA","VALDEPEÑAS DEL POZO SERGIO","TREPIANA ZARATE RAUL","VASQUEZ BEAUPERTHUY RAY DE JESUS "};
		int base = 1000;

		for (int i = 0; i < 1; i++) for (String value : extras_arr)	extras.put(value,empleadoRandom(value));
		for (int i = 0; i < 1; i++) for (String value : atrasos_arr)	atrasos.put(value,empleadoRandom(value));

		HashMap<String, Map<String, EnterprisePayrollEntry>> entries = new HashMap<>();
		entries.put("extras", extras);
		entries.put("atrasos", atrasos);

		createAonPdf(new EnterprisePayroll("logo.png", new Date(119, Calendar.APRIL,20), "Nómina de la empresa", "Aon Solutions",entries,entries));
	}


	public static EnterprisePayrollEntry empleadoRandom(String value){
		int base = 1000;
		String[]tipos = {"Nomina","Otros"};

		EnterprisePayrollEntry entry = new EnterprisePayrollEntry();
		entry.setEmpleado(value);
		entry.setTipo((tipos[(int)(Math.random()*2)]));
		entry.setDevengado(Math.random()* base);
		entry.setSsTrab(Math.random()* base);
		entry.setIrpf(Math.random()* base);
		entry.setDeducciones(Math.random()* base);
		entry.setLiquido(Math.random()* base);
		entry.setSsEmpr(Math.random()* base);
		entry.setCosteTotal(Math.random()* base);
		entry.setSsTotal(Math.random()* base);
		return entry;
	}
}