package com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;

import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class EnterprisePayrollTemplate {

	private static String filename = "./sample.pdf";
	private final static Color white = new Color(0xffffff);
	private final static Color black = new Color(0x000000);
	private final static Color blue = new Color(0x3a5b9e);
	private final static Color red = new Color(0xf44336);
	private final static Color green = new Color(0x228b22);

	private final static PDType1Font helvetica_bold = PDType1Font.HELVETICA_BOLD;
	private final static PDType1Font helvetica = PDType1Font.HELVETICA;

	private final static DecimalFormat df = new DecimalFormat("0.00");
	private final static int y_limit = 100;

	private static double[] totalEmpresa = new double[8];
	private static double[] totalEmpresaSS = new double[8];

	public void print_enterprise_payroll(EnterprisePayroll payroll, String name) throws IOException {
		try (PDDocument doc = new PDDocument()) {

			if(name != null) filename = name;

			//CREATE COMMON DATA MAP
			Map<String, Map<String, EnterprisePayrollEntry>> category_entries = payroll.getEntries();
			Set<String> categories = category_entries.keySet();

			//PRINT PDF
			PDPageContentStream contents = drawHeader(doc, payroll);
			String month_str = PdfFormats.formatDate(payroll.getMonth(), "MMMM, yyyy").get();
			month_str = String.valueOf(month_str.charAt(0)).toUpperCase() + month_str.substring(1);

			float x = 30; float y = 480f;

			for (String category : categories) {
				if(y < y_limit || y - y_limit < 50){
					contents = newPage(payroll,doc,contents);
					y = 480f;
				}
				PDFToolkit.drawText(contents, month_str, x, y, black, helvetica_bold,7);
				PDFToolkit.drawText(contents, category, x + 115, y, black, helvetica_bold,7);

				double[] totales_ss = new double[8];
				double[] totales_aon = new double[8];

				Map<String, EnterprisePayrollEntry> employe_entries = category_entries.get(category);
				Set<String> employees = employe_entries.keySet();

				y-=20;
				for (String employee: employees) {

					EnterprisePayrollEntry emp = employe_entries.get(employee);
					add_to_total(totales_aon,totales_ss,emp);

					if(y < y_limit){
						contents = newPage(payroll,doc,contents);
						y = 480f;
					}
					if(employe_entries.get(employee).has_aon){
						draw_entry(contents,x,y,emp);
						y-=10;
					}
					if(y < y_limit){
						contents = newPage(payroll,doc,contents);
						y = 480f;
					}
					if(employe_entries.get(employee).has_ss){
						if(draw_ss_entry(contents,x,y,emp)) y-= 10;
					}
					y-= 5;
				}
				y = draw_subtotals(contents,x,y,totales_aon,totales_ss);
			}

			y = draw_totals(contents,x,y);
			contents.close();
			doc.save(filename);

		}
	}

	//ADDS ENTRY VALUES INTO GENERAL TOTALS
	private static void add_to_total(double[] totales_aon, double[] totales_ss, EnterprisePayrollEntry emp) {
		if(emp.getDevengado() != null) 		{
			totales_aon[0] += emp.getDevengado();
			totalEmpresa[0] += emp.getDevengado();
		}
		if(emp.getSsTrab() != null) 		{
			totales_aon[1] += emp.getSsTrab();
			totalEmpresa[1] += emp.getSsTrab();
		}
		if(emp.getIrpf() != null) 			{
			totales_aon[2] += emp.getIrpf();
			totalEmpresa[2] += emp.getIrpf();
		}
		if(emp.getDeducciones() != null) 	{
			totales_aon[3] += emp.getDeducciones();
			totalEmpresa[3] += emp.getDeducciones();
		}
		if(emp.getLiquido() != null) 		{
			totales_aon[4] += emp.getLiquido();
			totalEmpresa[4] += emp.getLiquido();
		}
		if(emp.getSsEmpr() != null) 		{
			totales_aon[5] += emp.getSsEmpr();
			totalEmpresa[5] += emp.getSsEmpr();
		}
		if(emp.getCosteTotal() != null) 	{
			totales_aon[6] += emp.getCosteTotal();
			totalEmpresa[6] += emp.getCosteTotal();
		}
		if(emp.getSsTotal() != null) 		{
			totales_aon[7] += emp.getSsTotal();
			totalEmpresa[7] += emp.getSsTotal();
		}


		if(emp.getDevengadoSS() != null) 	{
			totales_ss[0] += emp.getDevengadoSS();
			totalEmpresaSS[0] += emp.getDevengadoSS();
		}
		if(emp.getSsTrabSS() != null) 		{
			totales_ss[1] += emp.getSsTrabSS();
			totalEmpresaSS[1] += emp.getSsTrabSS();
		}
		if(emp.getIrpfSS() != null) 		{
			totales_ss[2] += emp.getIrpfSS();
			totalEmpresaSS[2] += emp.getIrpfSS();
		}
		if(emp.getDeduccionesSS() != null) 	{
			totales_ss[3] += emp.getDeduccionesSS();
			totalEmpresaSS[3] += emp.getDeduccionesSS();
		}
		if(emp.getLiquidoSS() != null) 		{
			totales_ss[4] += emp.getLiquidoSS();
			totalEmpresaSS[4] += emp.getLiquidoSS();
		}
		if(emp.getSsEmprSS() != null) 		{
			totales_ss[5] += emp.getSsEmprSS();
			totalEmpresaSS[5] += emp.getSsEmprSS();
		}
		if(emp.getCosteTotalSS() != null) 	{
			totales_ss[6] += emp.getCosteTotalSS();
			totalEmpresaSS[6] += emp.getCosteTotalSS();
		}
		if(emp.getSsTotalSS() != null) 		{
			totales_ss[7] += emp.getSsTotalSS();
			totalEmpresaSS[7] += emp.getSsTotalSS();
		}
	}


	//STARTS A NEW PAGE
	private static PDPageContentStream newPage(EnterprisePayroll payroll, PDDocument doc, PDPageContentStream contents) throws IOException {
		contents.close();
		contents = drawHeader(doc,payroll);
		return contents;
	}

	//DRAWS THE HEADER OF THE PDF FILE
	private static PDPageContentStream drawHeader(PDDocument doc, EnterprisePayroll payroll) throws IOException {
		final String[] tableHeaders = {"Trabajador", "Tipo", "Devengado", "S.S. Trab.", "I.R.P.F", "Deducciones", "Liquido", "S.S. Empr", "CosteTotal", "Total S.S"};

		String fileTitle = "Nómina de la empresa";
		String enterprise = "EMPRESA:";
		String date = PdfFormats.formatDate(new Date(),"dd/MM/yyyy").get();

		PDPage page = PDFToolkit.createHorizontalPage();
		doc.addPage(page);
		PDPageContentStream contents = new PDPageContentStream(doc, page);

		float width = 75, height = width / 3.142857143f;
		float x = 25;
		if(payroll.getLogo() != null){
			PDFToolkit.drawImage(doc, contents, payroll.getLogo(),x,535, width, height);
			x+=95;
		}

		PDFToolkit.drawText(contents, fileTitle, x, 550f,black,helvetica_bold, 13);
		PDFToolkit.drawText(contents, enterprise, x, 535f,black,helvetica_bold, 9);
		PDFToolkit.drawText(contents, payroll.getSubheader(), x+60, 535f, black,helvetica_bold, 9);

		PDFToolkit.drawText(contents,  date, 770f, 535f, black, helvetica, 8);
		PDFToolkit.drawBox(contents, 25, 495, 190, 14.5f,black);
		PDFToolkit.drawText(contents, tableHeaders[0], 29f, 498f, white, helvetica_bold, 9.2f);

		x = 216;
		float y = 495, w = 65, h = 14.5f;
		for (int i = 1; i <= 9; i++, x += 66) {
			PDFToolkit.drawBox(contents, x, y, w, h,black);
			PDFToolkit.drawText(contents, tableHeaders[i], x + 4, y + 3,white, helvetica_bold,9.2f);
		}

		return contents;
	}

	//DRAWS AON ENTRY
	private static void draw_entry(PDPageContentStream contents, float x, float y, EnterprisePayrollEntry entry) throws IOException {
		if(entry.empleado != null) 		PDFToolkit.drawText(contents, entry.empleado, x, y, black, helvetica, 7);

		if(entry.tipo != null)
			if(entry.tipoSS == null) PDFToolkit.drawText(contents, entry.tipo, x+190, y, blue, helvetica, 7);
			else
				if (entry.tipo.equals(entry.tipoSS)) PDFToolkit.drawText(contents, entry.tipo, x+190, y, black, helvetica, 7);
				else  PDFToolkit.drawText(contents, entry.tipo, x+190, y, red, helvetica, 7);

		if(entry.devengado != null)
			if(entry.devengadoSS == null) PDFToolkit.drawText(contents, df.format(entry.devengado), x+257, y, blue, helvetica, 7);
			else
				if(entry.devengado.equals(entry.devengadoSS)) PDFToolkit.drawText(contents, df.format(entry.devengado), x+257, y, black, helvetica, 7);
				else PDFToolkit.drawText(contents, df.format(entry.devengado), x+257, y, red, helvetica, 7);

		if(entry.ssTrab != null)
			if(entry.ssTrabSS == null) PDFToolkit.drawText(contents, df.format(entry.ssTrab), x+325, y, blue, helvetica, 7);
			else
				if (entry.ssTrab.equals(entry.ssTrabSS)) PDFToolkit.drawText(contents, df.format(entry.ssTrab), x+325, y, black, helvetica, 7);
				else PDFToolkit.drawText(contents, df.format(entry.ssTrab), x+325, y, red, helvetica, 7);

		if(entry.irpf != null)
			if(entry.irpfSS == null) PDFToolkit.drawText(contents, df.format(entry.irpf), x+390, y, blue, helvetica, 7);
			else
				if (entry.irpf.equals(entry.irpfSS)) PDFToolkit.drawText(contents, df.format(entry.irpf), x+390, y, black, helvetica, 7);
				else PDFToolkit.drawText(contents, df.format(entry.irpf), x+390, y, red, helvetica, 7);

		if(entry.deducciones != null)
			if(entry.deduccionesSS == null) PDFToolkit.drawText(contents, df.format(entry.deducciones), x+457, y, blue, helvetica, 7);
			else
				if(entry.deducciones.equals(entry.deduccionesSS)) PDFToolkit.drawText(contents, df.format(entry.deducciones), x+457, y, black, helvetica, 7);
				else PDFToolkit.drawText(contents, df.format(entry.deducciones), x+457, y, red, helvetica, 7);

		if(entry.liquido != null)
			if(entry.liquidoSS == null) PDFToolkit.drawText(contents, df.format(entry.liquido), x+522, y, blue, helvetica, 7);
			else
				if(entry.liquido.equals(entry.liquidoSS)) PDFToolkit.drawText(contents, df.format(entry.liquido), x+522, y, black, helvetica, 7);
				else PDFToolkit.drawText(contents, df.format(entry.liquido), x+522, y, red, helvetica, 7);

		if(entry.ssEmpr != null)
			if(entry.ssEmprSS == null) PDFToolkit.drawText(contents, df.format(entry.ssEmpr), x+588, y, blue, helvetica, 7);
			else
				if(entry.ssEmpr.equals(entry.ssEmprSS)) PDFToolkit.drawText(contents, df.format(entry.ssEmpr), x+588, y, black, helvetica, 7);
				else PDFToolkit.drawText(contents, df.format(entry.ssEmpr), x+588, y, red, helvetica, 7);

		if(entry.costeTotal != null)
			if(entry.costeTotalSS == null) PDFToolkit.drawText(contents, df.format(entry.costeTotal), x+654, y, blue, helvetica, 7);
			else
				if(entry.costeTotal.equals(entry.costeTotalSS)) PDFToolkit.drawText(contents, df.format(entry.costeTotal), x+654, y, black, helvetica, 7);
				else PDFToolkit.drawText(contents, df.format(entry.costeTotal), x+654, y, red, helvetica, 7);

		if(entry.ssTotal != null)
			if(entry.ssTotalSS == null) PDFToolkit.drawText(contents, df.format(entry.ssTotal), x+718, y, blue, helvetica, 7);
			else
				if(entry.ssTotal.equals(entry.ssTotalSS))PDFToolkit.drawText(contents, df.format(entry.ssTotal), x+718, y, black, helvetica, 7);
				else PDFToolkit.drawText(contents, df.format(entry.ssTotal), x+718, y, red, helvetica, 7);
	}

	//DRAWS SS ENTRY
	private static boolean draw_ss_entry(PDPageContentStream contents, float x, float y, EnterprisePayrollEntry entry) throws IOException {

		if(entry.empleadoSS == null
				&&  entry.tipoSS == null
				&&  entry.devengadoSS == null
				&&  entry.ssTrabSS == null
				&&  entry.irpfSS  == null
				&&  entry.deduccionesSS  == null
				&&  entry.liquidoSS  == null
				&&  entry.ssEmprSS  == null
				&&  entry.costeTotalSS  == null
				&&  entry.ssTotalSS  == null
		) return false;

		if(entry.empleadoSS != null) 	PDFToolkit.drawText(contents, entry.empleadoSS, x, y, green, helvetica, 7);
		if(entry.tipoSS != null)		PDFToolkit.drawText(contents, entry.tipoSS, x+190, y, green, helvetica, 7);
		if(entry.devengadoSS != null)	PDFToolkit.drawText(contents, df.format(entry.devengadoSS), x+257, y, green, helvetica, 7);
		if(entry.ssTrabSS != null)		PDFToolkit.drawText(contents, df.format(entry.ssTrabSS), x+325, y, green, helvetica, 7);
		if(entry.irpfSS != null)		PDFToolkit.drawText(contents, df.format(entry.irpfSS), x+390, y, green, helvetica, 7);
		if(entry.deduccionesSS != null)	PDFToolkit.drawText(contents, df.format(entry.deduccionesSS), x+457, y, green, helvetica, 7);
		if(entry.liquidoSS != null)		PDFToolkit.drawText(contents, df.format(entry.liquidoSS), x+522, y, green, helvetica, 7);
		if(entry.ssEmprSS != null) 		PDFToolkit.drawText(contents, df.format(entry.ssEmprSS), x+588, y, green, helvetica, 7);
		if(entry.costeTotalSS != null) 	PDFToolkit.drawText(contents, df.format(entry.costeTotalSS), x+654, y, green, helvetica, 7);
		if(entry.ssTotalSS != null) 	PDFToolkit.drawText(contents, df.format(entry.ssTotalSS), x+718, y, green, helvetica, 7);

		return true;
	}

	//DRAWS SUBTOTAL
	private static float draw_subtotals(PDPageContentStream contents, float x, float y, double[] totales_aon, double[] totales_ss) throws IOException {

		PDFToolkit.drawBox(contents,x,y,780,.3f,black);
		y -= 10;

		PDFToolkit.drawText(contents, "", x, y, black, helvetica_bold, 7);
		PDFToolkit.drawText(contents, "SUBTOTAL: ", x+190, y, black, helvetica_bold, 7);

		if(totales_ss[0] != totales_aon[0]) PDFToolkit.drawText(contents, df.format(totales_aon[0]), x+257, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totales_aon[0]), x+257, y, black, helvetica, 7);

		if(totales_ss[1] != totales_aon[1]) PDFToolkit.drawText(contents, df.format(totales_aon[1]), x+325, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totales_aon[1]), x+325, y, black, helvetica, 7);

		if(totales_ss[2] != totales_aon[2]) PDFToolkit.drawText(contents, df.format(totales_aon[2]), x+390, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totales_aon[2]), x+390, y, black, helvetica, 7);

		if(totales_ss[3] != totales_aon[3]) PDFToolkit.drawText(contents, df.format(totales_aon[3]), x+457, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totales_aon[3]), x+457, y, black, helvetica, 7);

		if(totales_ss[4] != totales_aon[4]) PDFToolkit.drawText(contents, df.format(totales_aon[4]), x+522, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totales_aon[4]), x+522, y, black, helvetica, 7);

		if(totales_ss[5] != totales_aon[5]) PDFToolkit.drawText(contents, df.format(totales_aon[5]), x+588, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totales_aon[5]), x+588, y, black, helvetica, 7);

		if(totales_ss[6] != totales_aon[6]) PDFToolkit.drawText(contents, df.format(totales_aon[6]), x+654, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totales_aon[6]), x+654, y, black, helvetica, 7);

		if(totales_ss[7] != totales_aon[7]) PDFToolkit.drawText(contents, df.format(totales_aon[7]), x+718, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totales_aon[7]), x+718, y, black, helvetica, 7);
		y -= 10;

		PDFToolkit.drawText(contents, "", x, y, black, helvetica_bold, 7);
		PDFToolkit.drawText(contents, "SUBTOTAL SS: ", x+190, y, black, helvetica_bold, 7);

		PDFToolkit.drawText(contents, df.format(totales_ss[0]), x+257, y, green, helvetica, 7);
		PDFToolkit.drawText(contents, df.format(totales_ss[1]), x+325, y, green, helvetica, 7);
		PDFToolkit.drawText(contents, df.format(totales_ss[2]), x+390, y, green, helvetica, 7);
		PDFToolkit.drawText(contents, df.format(totales_ss[3]), x+457, y, green, helvetica, 7);
		PDFToolkit.drawText(contents, df.format(totales_ss[4]), x+522, y, green, helvetica, 7);
		PDFToolkit.drawText(contents, df.format(totales_ss[5]), x+588, y, green, helvetica, 7);
		PDFToolkit.drawText(contents, df.format(totales_ss[6]), x+654, y, green, helvetica, 7);
		PDFToolkit.drawText(contents, df.format(totales_ss[7]), x+718, y, green, helvetica, 7);

		y -= 20;

		return y;
	}

	private static float draw_totals(PDPageContentStream contents, float x, float y) throws IOException {
		PDFToolkit.drawBox(contents,x,y-32.5f,780,40f,new Color(0xf8f8f8));
		y -= 10;

		PDFToolkit.drawText(contents, "TOTAL EMPRESA: ", x+20, y, black, helvetica_bold, 7);

		if(totalEmpresaSS[0] != totalEmpresa[0]) PDFToolkit.drawText(contents, df.format(totalEmpresa[0]), x+257, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totalEmpresa[0]), x+257, y, black, helvetica, 7);

		if(totalEmpresaSS[1] != totalEmpresa[1]) PDFToolkit.drawText(contents, df.format(totalEmpresa[1]), x+325, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totalEmpresa[1]), x+325, y, black, helvetica, 7);

		if(totalEmpresaSS[2] != totalEmpresa[2]) PDFToolkit.drawText(contents, df.format(totalEmpresa[2]), x+390, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totalEmpresa[2]), x+390, y, black, helvetica, 7);

		if(totalEmpresaSS[3] != totalEmpresa[3]) PDFToolkit.drawText(contents, df.format(totalEmpresa[3]), x+457, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totalEmpresa[3]), x+457, y, black, helvetica, 7);

		if(totalEmpresaSS[4] != totalEmpresa[4]) PDFToolkit.drawText(contents, df.format(totalEmpresa[4]), x+522, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totalEmpresa[4]), x+522, y, black, helvetica, 7);

		if(totalEmpresaSS[5] != totalEmpresa[5]) PDFToolkit.drawText(contents, df.format(totalEmpresa[5]), x+588, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totalEmpresa[5]), x+588, y, black, helvetica, 7);

		if(totalEmpresaSS[6] != totalEmpresa[6]) PDFToolkit.drawText(contents, df.format(totalEmpresa[6]), x+654, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totalEmpresa[6]), x+654, y, black, helvetica, 7);

		if(totalEmpresaSS[7] != totalEmpresa[7]) PDFToolkit.drawText(contents, df.format(totalEmpresa[7]), x+718, y, red, helvetica, 7);
		else PDFToolkit.drawText(contents, df.format(totalEmpresa[7]), x+718, y, black, helvetica, 7);
		y -= 10;

		PDFToolkit.drawText(contents, "TOTAL EMPRESA SS: ", x + 20, y, black, helvetica_bold, 7);
		PDFToolkit.drawText(contents, df.format(totalEmpresaSS[0]), x+257, y, green, helvetica, 7);
		PDFToolkit.drawText(contents, df.format(totalEmpresaSS[1]), x+325, y, green, helvetica, 7);
		PDFToolkit.drawText(contents, df.format(totalEmpresaSS[2]), x+390, y, green, helvetica, 7);
		PDFToolkit.drawText(contents, df.format(totalEmpresaSS[3]), x+457, y, green, helvetica, 7);
		PDFToolkit.drawText(contents, df.format(totalEmpresaSS[4]), x+522, y, green, helvetica, 7);
		PDFToolkit.drawText(contents, df.format(totalEmpresaSS[5]), x+588, y, green, helvetica, 7);
		PDFToolkit.drawText(contents, df.format(totalEmpresaSS[6]), x+654, y, green, helvetica, 7);
		PDFToolkit.drawText(contents, df.format(totalEmpresaSS[7]), x+718, y, green, helvetica, 7);
		return y;
	}
}