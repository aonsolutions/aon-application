package com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.GRAY;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.GREEN;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.RED;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.toLatinNumber;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT.CENTER;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT.LEFT;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT.RIGHT;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.PAGE_TYPE.HORIZONTAL;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawImage;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.imageFromBytes;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.reescale;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.esferalia.aon.in.payroll.pdf.api.component.advanced.PdfTable;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfFile;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfImage;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfText;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.ColManager;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayroll;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayrollEntry;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;

public class EnterprisePayrollTemplate extends PdfFile {

	private EnterprisePayroll payroll;
	private ColManager		  mg;
	private byte[]	  		logoB;

	public static void print(EnterprisePayroll payroll, OutputStream out, Optional<Locale> language)
			throws CanNotCreatePdfException {

		EnterprisePayrollTemplate template = null;
		try
		{
			ResourceBundle words = ResourceBundle.getBundle(
					"com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.bundle.EnterprisePayrollBundle",
					language.orElse(new Locale("Es")));
			template = new EnterprisePayrollTemplate(7.5f, 500, new PDDocument(), words, out, payroll, 75f);
			template.setDefaults(HELVETICA, 10f, BLACK, GRAY);
			template.newPage(HORIZONTAL);
			PdfTable table = calculateColums(template);

			template.logoB = template.payroll.getLogo().map(l ->
			{
				try
				{
					return l.readAllBytes();
				} catch (Exception e)
				{
					return null;
				}
			}).orElse(null);

			drawHeader(template);
			drawEntries(template, table);
			if (table.hasColumn(0))
				drawTotals(template, table);
			else
				drawError(template);

			new PdfText(720, 20, 100, 20, template.contents, template.text("PAGE") + " " + template.page, GRAY,
					HELVETICA, template.fontsize, RIGHT).draw();
			template.print();
		} catch (Exception e)
		{
			if (template != null)
				try
				{
					template.close();
				} catch (IOException ignored){}
			throw new CanNotCreatePdfException(e);
		}
	}

	private static void drawError(EnterprisePayrollTemplate t) {
		PdfText msg = new PdfText(t.x() + 15, t.y() - 200, 800, 50, t.contents, "No hay datos disponibles", BLACK, HELVETICA, 32f, CENTER);
		msg.draw();

		byte[] b = getErrLogo();

		PdfImage image = new PdfImage(t.x() + 150, t.y() - 225, 0, 0, ALIGNMENT.CENTER, t.contents, t.doc, b);
		image.scale(75, 75, CENTER).draw();

	}

	private static void drawHeader(EnterprisePayrollTemplate template) {

		String dateTxt		= PdfFormats.formatDate(new Date(), template.words.getString("DATE FORMAT")).orElse("");
		String headerTxt	= template.payroll.getHeader().orElse("Nómina de empresa");
		String subheaderTxt	= template.payroll.getSubheader().orElse("");

		template.x(7);
		template.y(550);

		PdfText header = new PdfText(template.x(), template.y(), 200, 30, 5, 8, template.contents, headerTxt, BLACK, HELVETICA_BOLD, 18f,LEFT);
		template.down(20);

		PdfText	subheader = new PdfText(template.x(), template.y(), 600, 20, 5, 5, template.contents, subheaderTxt, BLACK, HELVETICA_BOLD,12f, LEFT);
		PdfText	date	  = new PdfText(template.x() - 62, 22, 130, 20, 5, 5, template.contents, dateTxt, GRAY, HELVETICA, 9f, RIGHT);
		template.down(30);

		try
		{
			BufferedImage img	 = imageFromBytes(template.logoB);
			float[]		  scales = reescale(img.getWidth(), img.getHeight(), 150, 50);
			drawImage(template.doc, template.contents, template.logoB, 670 + 150 - scales[0], template.y() + 50 - scales[1] / 2, scales[0], scales[1]);
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (NullPointerException e)
		{
			System.err.println("Null logo");
		}
		template.down(30);

		header.draw();
		subheader.draw();
		date.draw();
	}

	private static PdfTable calculateColums(EnterprisePayrollTemplate t) throws IOException {

		t.mg = new ColManager();

		Map<String, Map<String, EnterprisePayrollEntry>> entries = t.payroll.getEntries().orElse(new HashMap<>());
		entries.entrySet().stream().forEach(
				category -> category.getValue().entrySet().stream().forEach(entry -> t.mg.add(entry.getValue())));

		if (t.mg.entriesForCol("trabajador") > 0)
			t.mg.enable("trabajador");
		if (t.mg.entriesForCol("tipo") > 0)
			t.mg.enable("tipo");
		if (t.mg.entriesForCol("devengado") > 0)
			t.mg.enable("devengado");
		if (t.mg.entriesForCol("ssTrab") > 0)
			t.mg.enable("ssTrab");
		if (t.mg.entriesForCol("irpf") > 0)
			t.mg.enable("irpf");
		if (t.mg.entriesForCol("Otr. ded.") > 0)
			t.mg.enable("Otr. ded.");
		if (t.mg.entriesForCol("liquido") > 0)
			t.mg.enable("liquido");
		if (t.mg.entriesForCol("ssEmpr") > 0)
			t.mg.enable("ssEmpr");
		if (t.mg.entriesForCol("costeTotal") > 0)
			t.mg.enable("costeTotal");
		if (t.mg.entriesForCol("bonificaciones") > 0)
			t.mg.enable("bonificaciones");
		if (t.mg.entriesForCol("ssTotal") > 0)
			t.mg.enable("ssTotal");

		int	  cols = t.mg.countEnabled();
		float tb   = 0;
		float c	   = 0;
		if (t.mg.isActive("trabajador"))
		{
			tb = 25;
			c  = (100 - tb) / (cols - 1);
		} else
			c = (100 - tb) / (cols);

		float[]		sizes	   = new float[cols];
		String[]	headers	   = new String[cols];
		ALIGNMENT[]	alignments = new ALIGNMENT[cols];

		int current = 0;
		if (t.mg.isActive("trabajador"))
			current = setColumn(current, sizes, headers, alignments, tb, "Trabajador", LEFT);
		if (t.mg.isActive("tipo"))
			current = setColumn(current, sizes, headers, alignments, c, "Tipo", LEFT);
		if (t.mg.isActive("devengado"))
			current = setColumn(current, sizes, headers, alignments, c, "Devengado", RIGHT);
		if (t.mg.isActive("ssTrab"))
			current = setColumn(current, sizes, headers, alignments, c, "S.S. Trab.", RIGHT);
		if (t.mg.isActive("irpf"))
			current = setColumn(current, sizes, headers, alignments, c, "I.R.P.F", RIGHT);
		if (t.mg.isActive("Otr. ded."))
			current = setColumn(current, sizes, headers, alignments, c, "Otr. ded.", RIGHT);
		if (t.mg.isActive("liquido"))
			current = setColumn(current, sizes, headers, alignments, c, "Liquido", RIGHT);
		if (t.mg.isActive("ssEmpr"))
			current = setColumn(current, sizes, headers, alignments, c, "S.S. Empr.", RIGHT);
		if (t.mg.isActive("bonificaciones"))
			current = setColumn(current, sizes, headers, alignments, c, "Bonificaciones", RIGHT);
		if (t.mg.isActive("ssTotal"))
			current = setColumn(current, sizes, headers, alignments, c, "Total S.S", RIGHT);
		if (t.mg.isActive("costeTotal"))
			current = setColumn(current, sizes, headers, alignments, c, "Coste total", RIGHT);

		t.mg.showEnabled();

		return createTable(t, sizes, headers, alignments);
	}

	private static int setColumn(
			int current, float[] sizes, String[] headers, ALIGNMENT[] alignments, float tb, String text, ALIGNMENT align
	) {
		sizes[current]		= tb;
		headers[current]	= text;
		alignments[current]	= align;
		current++;
		return current;
	}

	private static PdfTable createTable(
			EnterprisePayrollTemplate t, float[] sizes, String[] headers, ALIGNMENT[] alignments
	) throws IOException {

		PdfTable table = new PdfTable(t.x(), t.y(), t.contents, 816, 15, 1, sizes, headers);
		table.headerHeight	 = 20;
		table.fontsize		 = 9;
		table.headerFontsize = 8;

		table.setAlignment(alignments);
		table.drawHeader();
		t.y(table.y());

		return table;
	}

	private static void drawEntries(EnterprisePayrollTemplate t, PdfTable table) {

		Map<String, Map<String, EnterprisePayrollEntry>> entries = t.payroll.getEntries().orElse(new HashMap<>());
		entries.entrySet().stream().forEach(category ->
		{

			PdfText	categoryTitle = new PdfText(t.x() + 100, table.y(), 100, 20, 5, 6, t.contents, category.getKey(),	BLACK, HELVETICA_BOLD, 9f, RIGHT);
			PdfText	monthTitle	   = new PdfText(t.x(), table.y(), 100, 20, 5, 6, t.contents,PdfFormats.formatDate(t.payroll.getMonth().orElse(null), "MMMM, yyyy").orElse(""),
									BLACK,HELVETICA_BOLD, 9f, LEFT);

			categoryTitle.draw();
			monthTitle.draw();
			table.jump(15);

			category.getValue().entrySet().stream().forEach(entry ->
			{
				try
				{
					drawEntry(t, entry, table);
					t.mg.addToSubtotal(entry.getValue());
				} catch (Exception ignored){}
			});
			try
			{
				drawSubtotal(t, table);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			table.jump(10f);
			t.mg.addToTotal();
		});
	}

	private static void drawSubtotal(EnterprisePayrollTemplate t, PdfTable table) throws IOException {
		table.clearRow();
		ArrayList<Double> subtotalAon = t.mg.getAonSubtotal();
		ArrayList<Double> subtotalSs  = t.mg.getSsSubtotal();

		table.drawLine();
		table.jump(15f);
		table.alignCell(0, CENTER);
		table.font = HELVETICA_BOLD;

		boolean painted = false;

		if (subtotalAon.stream().mapToDouble(p -> p).sum() != 0)
		{
			table.fillCell(0, "CENTRO DE TRABAJO");
			table.fillCell(1, "SUBTOTAL");
			for (int i = 2; i < subtotalAon.size(); i++)
				table.fillCell(i, toLatinNumber(subtotalAon.get(i)));
			table.newRow();
			painted = !painted;
		}

		if (subtotalSs.stream().mapToDouble(p -> p).sum() != 0)
		{
			if (!painted)
				table.fillCell(0, "CENTRO DE TRABAJO");
			table.fillCell(1, "SUBTOTAL SS");

			table.fontsize = 7.5f;

			for (int i = 2; i < subtotalSs.size(); i++)
				table.fillCell(i, toLatinNumber(subtotalSs.get(i)));
			table.newRow();
		}
		table.fontsize = 9;
		table.alignCell(0, LEFT);
		table.font = HELVETICA;
	}

	private static void drawEntry(
			EnterprisePayrollTemplate t, Entry<String, EnterprisePayrollEntry> entry, PdfTable table
	) throws IOException {

		EnterprisePayrollEntry e = entry.getValue();
		if (e.HasAon() && e.HasSs())
			drawEntryAonSs(t, e, table);
		else if (e.HasAon())
			drawEntryOnlyAon(t, e, table);
		else if (e.HasSs())
			drawEntryOnlySs(t, e, table);

		t.y(table.y());
		check(t, table);
	}

	private static void drawEntryAonSs(EnterprisePayrollTemplate t, EnterprisePayrollEntry e, PdfTable table)
			throws IOException {
		String empleado		  = e.getEmpleado().orElse(null);
		String tipo			  = e.getTipo().orElse(null);
		Double devengado	  = e.getDevengado().orElse(null);
		Double ssTrab		  = e.getSsTrab().orElse(null);
		Double irpf			  = e.getIrpf().orElse(null);
		Double deducciones	  = e.getDeducciones().orElse(null);
		Double liquido		  = e.getLiquido().orElse(null);
		Double ssEmpresa	  = e.getSsEmpr().orElse(null);
		Double bonificaciones = e.getBonificaciones().orElse(null);
		Double costeTotal	  = e.getCosteTotal().orElse(null);
		Double ssTotal		  = e.getSsTotal().orElse(null);

		String empleadoSS		= e.getEmpleadoSS().orElse(null);
		String tipoSS			= e.getTipoSS().orElse(null);
		Double devengadoSS		= e.getDevengadoSS().orElse(null);
		Double ssTrabSS			= e.getSsTrabSS().orElse(null);
		Double irpfSS			= e.getIrpfSS().orElse(null);
		Double deduccionesSS	= e.getDeduccionesSS().orElse(null);
		Double liquidoSS		= e.getLiquidoSS().orElse(null);
		Double ssEmpresaSS		= e.getSsEmprSS().orElse(null);
		Double bonificacionesSS	= e.getBonificacionesSS().orElse(null);
		Double costeTotalSS		= e.getCosteTotalSS().orElse(null);
		Double ssTotalSS		= e.getSsTotalSS().orElse(null);

		table.fillCell(table.getColumn("Trabajador"), empleado);
		table.fillCell(table.getColumn("Tipo"), tipo);
		table.fillCell(table.getColumn("Devengado"), toLatinNumber(devengado));
		table.fillCell(table.getColumn("S.S. Trab."), toLatinNumber(ssTrab));
		table.fillCell(table.getColumn("I.R.P.F"), toLatinNumber(irpf));
		table.fillCell(table.getColumn("Otr. ded."), toLatinNumber(deducciones));
		table.fillCell(table.getColumn("Liquido"), toLatinNumber(liquido));
		table.fillCell(table.getColumn("S.S. Empr."), toLatinNumber(ssEmpresa));
		table.fillCell(table.getColumn("Bonificaciones"), toLatinNumber(bonificaciones));
		table.fillCell(table.getColumn("Total S.S"), toLatinNumber(ssTotal));
		table.fillCell(table.getColumn("Coste total"), toLatinNumber(costeTotal));

		table.newRow();

		table.fillCell(table.getColumn("Trabajador"), empleadoSS);
		table.fillCell(table.getColumn("Tipo"), tipoSS);
		table.fillCell(table.getColumn("Devengado"), toLatinNumber(devengadoSS));
		table.fillCell(table.getColumn("S.S. Trab."), toLatinNumber(ssTrabSS));
		table.fillCell(table.getColumn("I.R.P.F"), toLatinNumber(irpfSS));
		table.fillCell(table.getColumn("Otr. ded."), toLatinNumber(deduccionesSS));
		table.fillCell(table.getColumn("Liquido"), toLatinNumber(liquidoSS));
		table.fillCell(table.getColumn("S.S. Empr."), toLatinNumber(ssEmpresaSS));
		table.fillCell(table.getColumn("Bonificaciones"), toLatinNumber(bonificacionesSS));
		table.fillCell(table.getColumn("Total S.S"), toLatinNumber(ssTotalSS));
		table.fillCell(table.getColumn("Coste total"), toLatinNumber(costeTotalSS));

		table.paintCell(table.getColumn("Tipo"), 			(tipo != null && tipoSS != null && tipo.equals(tipoSS)) ? GREEN : RED);
		table.paintCell(table.getColumn("Devengado"),  		(devengado != null && devengadoSS != null && devengado.equals(devengadoSS)) ? GREEN : RED);
		table.paintCell(table.getColumn("S.S. Trab."), 		(ssTrab != null && ssTrabSS != null && ssTrab.equals(ssTrabSS)) ? GREEN : RED);
		table.paintCell(table.getColumn("I.R.P.F"), 		(irpf != null && irpfSS != null && tipo.equals(irpfSS)) ? GREEN : RED);
		table.paintCell(table.getColumn("Otr. ded."),  		(deducciones != null && deduccionesSS != null && deducciones.equals(deduccionesSS)) ? GREEN : RED);
		table.paintCell(table.getColumn("Liquido"),			(liquido != null && liquidoSS != null && liquido.equals(liquidoSS)) ? GREEN : RED);
		table.paintCell(table.getColumn("S.S. Empr."),		(ssEmpresa != null && ssEmpresaSS != null && ssEmpresa.equals(ssEmpresaSS)) ? GREEN : RED);
		table.paintCell(table.getColumn("Bonificaciones"),	(bonificaciones != null && bonificacionesSS != null && bonificaciones.equals(bonificacionesSS)) ? GREEN	: RED);
		table.paintCell(table.getColumn("Total S.S"),		(ssTotal != null && ssTotalSS != null && ssTotal.equals(ssTotalSS)) ? GREEN : RED);
		table.paintCell(table.getColumn("Coste total"),		(costeTotal != null && costeTotalSS != null && costeTotal.equals(costeTotalSS)) ? GREEN : RED);

		table.newRow();
		t.y(table.y());
	}

	private static void drawEntryOnlyAon(EnterprisePayrollTemplate t, EnterprisePayrollEntry e, PdfTable table)
			throws IOException {
		String empleado	   = e.getEmpleado().orElse(null);
		String tipo		   = e.getTipo().orElse(null);
		Double devengado   = e.getDevengado().orElse(null);
		Double ssTrab	   = e.getSsTrab().orElse(null);
		Double irpf		   = e.getIrpf().orElse(null);
		Double deducciones = e.getDeducciones().orElse(null);
		Double liquido	   = e.getLiquido().orElse(null);
		Double ssEmpresa   = e.getSsEmpr().orElse(null);
		;
		Double bonificaciones = e.getBonificaciones().orElse(null);
		Double costeTotal	  = e.getCosteTotal().orElse(null);
		Double ssTotal		  = e.getSsTotal().orElse(null);

		table.fillCell(table.getColumn("Trabajador"), empleado);
		table.fillCell(table.getColumn("Tipo"), tipo);
		table.fillCell(table.getColumn("Devengado"), toLatinNumber(devengado));
		table.fillCell(table.getColumn("S.S. Trab."), toLatinNumber(ssTrab));
		table.fillCell(table.getColumn("I.R.P.F"), toLatinNumber(irpf));
		table.fillCell(table.getColumn("Otr. ded."), toLatinNumber(deducciones));
		table.fillCell(table.getColumn("Liquido"), toLatinNumber(liquido));
		table.fillCell(table.getColumn("S.S. Empr."), toLatinNumber(ssEmpresa));
		table.fillCell(table.getColumn("Bonificaciones"), toLatinNumber(bonificaciones));
		table.fillCell(table.getColumn("Total S.S"), toLatinNumber(ssTotal));
		table.fillCell(table.getColumn("Coste total"), toLatinNumber(costeTotal));

		table.paintCell(table.getColumn("Tipo"), BLACK);
		table.paintCell(table.getColumn("Devengado"), BLACK);
		table.paintCell(table.getColumn("S.S. Trab."), BLACK);
		table.paintCell(table.getColumn("I.R.P.F"), BLACK);
		table.paintCell(table.getColumn("Otr. ded."), BLACK);
		table.paintCell(table.getColumn("Liquido"), BLACK);
		table.paintCell(table.getColumn("S.S. Empr."), BLACK);
		table.paintCell(table.getColumn("Bonificaciones"), BLACK);
		table.paintCell(table.getColumn("Total S.S"), BLACK);
		table.paintCell(table.getColumn("Coste total"), BLACK);

		table.newRow();
		t.y(table.y());
	}

	private static void drawEntryOnlySs(EnterprisePayrollTemplate t, EnterprisePayrollEntry e, PdfTable table)
			throws IOException {

		String empleadoSS		= e.getEmpleadoSS().orElse(null);
		String tipoSS			= e.getTipoSS().orElse(null);
		Double devengadoSS		= e.getDevengadoSS().orElse(null);
		Double ssTrabSS			= e.getSsTrabSS().orElse(null);
		Double irpfSS			= e.getIrpfSS().orElse(null);
		Double deduccionesSS	= e.getDeduccionesSS().orElse(null);
		Double liquidoSS		= e.getLiquidoSS().orElse(null);
		Double ssEmpresaSS		= e.getSsEmprSS().orElse(null);
		Double bonificacionesSS	= e.getBonificacionesSS().orElse(null);
		Double costeTotalSS		= e.getCosteTotalSS().orElse(null);
		Double ssTotalSS		= e.getSsTotalSS().orElse(null);

		table.fillCell(table.getColumn("Trabajador"), empleadoSS);
		table.fillCell(table.getColumn("Tipo"), tipoSS);
		table.fillCell(table.getColumn("Devengado"), toLatinNumber(devengadoSS));
		table.fillCell(table.getColumn("S.S. Trab."), toLatinNumber(ssTrabSS));
		table.fillCell(table.getColumn("I.R.P.F"), toLatinNumber(irpfSS));
		table.fillCell(table.getColumn("Otr. ded."), toLatinNumber(deduccionesSS));
		table.fillCell(table.getColumn("Liquido"), toLatinNumber(liquidoSS));
		table.fillCell(table.getColumn("S.S. Empr."), toLatinNumber(ssEmpresaSS));
		table.fillCell(table.getColumn("Bonificaciones"), toLatinNumber(bonificacionesSS));
		table.fillCell(table.getColumn("Total S.S"), toLatinNumber(ssTotalSS));
		table.fillCell(table.getColumn("Coste total"), toLatinNumber(costeTotalSS));

		table.paintCell(table.getColumn("Tipo"), GREEN);
		table.paintCell(table.getColumn("Devengado"), GREEN);
		table.paintCell(table.getColumn("S.S. Trab."), GREEN);
		table.paintCell(table.getColumn("I.R.P.F"), GREEN);
		table.paintCell(table.getColumn("Otr. ded."), GREEN);
		table.paintCell(table.getColumn("Liquido"), GREEN);
		table.paintCell(table.getColumn("S.S. Empr."), GREEN);
		table.paintCell(table.getColumn("Bonificaciones"), GREEN);
		table.paintCell(table.getColumn("Total S.S"), GREEN);
		table.paintCell(table.getColumn("Coste total"), GREEN);

		table.newRow();
		t.y(table.y());
	}

	private static void check(EnterprisePayrollTemplate t, PdfTable table) throws IOException {
		if (t.jump())
		{
			new PdfText(720, 20, 100, 20, t.contents, t.text("PAGE") + " " + t.page, GRAY, HELVETICA, t.fontsize, RIGHT).draw();
			t.newPage(HORIZONTAL);
			table.stream(t.contents);

			drawHeader(t);
			table.y(500);
			table.drawHeader();
		}
	}

	private static void drawTotals(EnterprisePayrollTemplate t, PdfTable table) throws IOException {
		table.clearRow();
		t.limitY += 50;
		check(t, table);

		table.headerColor = BLACK;
		table.textColor	  = BLACK;

		table.alignCell(0, CENTER);

		ArrayList<Double> totalAon	= t.mg.getAonTotal();
		ArrayList<Double> totalSs	= t.mg.getSsTotal();
		table.font = HELVETICA_BOLD;

		boolean painted = false;

		if (totalAon.stream().mapToDouble(p -> p).sum() != 0)
		{
			table.fillCell(0, "EMPRESA");
			table.fillCell(1, "TOTAL:");

			for (int i = 2; i < totalAon.size(); i++)
				table.fillCell(i, toLatinNumber(totalAon.get(i)));
			table.newRow();

			painted = !painted;
		}

		if (totalSs.stream().mapToDouble(p -> p).sum() != 0)
		{
			table.fillCell(1, "TOTAL SS:");
			for (int i = 2; i < totalSs.size(); i++)
				table.fillCell(i, toLatinNumber(totalSs.get(i)));

			table.newRow();
			if (!painted)
				table.fillCell(0, "EMPRESA");
		}
		table.font = HELVETICA;
		table.alignCell(0, LEFT);
	}

	public EnterprisePayrollTemplate(
			float x, float y, PDDocument doc, ResourceBundle words, OutputStream out, EnterprisePayroll payroll,
			float limit
	) {
		super(x, y, doc, words, out, limit);
		this.payroll = payroll;
	}

	private static byte[] getErrLogo() {
		return new byte[] { -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -6, 0, 0, 0, -6, 8,
				6, 0, 0, 0, -120, -20, 90, 61, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 11, 19, 0, 0, 11, 19, 1, 0, -102,
				-100, 24, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4, 103, 65, 77, 65, 0, 0, -79,
				-113, 11, -4, 97, 5, 0, 0, 17, -51, 73, 68, 65, 84, 120, 1, -19, -35, -81, -109, 28, -57, 21, 7, -16,
				111, -100, -16, -56, -52, -52, 99, 102, 102, -119, -123, 121, 100, -28, 32, -99, -96, -111, -9, 88,
				-104, 36, 22, -90, 61, 24, 100, 9, -59, -52, -93, -65, -64, 18, 74, -103, -40, 35, 102, 102, -103, -123,
				121, -52, -62, 114, -122, 33, -86, -12, -69, 121, 99, -81, 79, 119, -89, -69, -35, 126, -81, 95, -9,
				124, 63, 85, 83, 123, 81, -91, 106, -49, 55, -13, -99, -41, -65, -90, 7, 32, 34, 34, 34, 34, 34, 34, 34,
				34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34,
				34, 34, 34, 34, -94, -88, -2, 0, 106, 90, -33, -9, -35, 53, -2, 111, -89, -29, 56, -98, -126, -102, -59,
				-96, 87, 40, -123, -9, 86, -6, -112, -29, 118, 58, -70, 116, -68, -81, -1, -69, -45, 3, 59, -97, 55, 53,
				-91, -29, 84, 15, -7, -7, 103, -3, -7, 21, -26, 27, -62, 43, 80, 117, 24, -12, -32, -76, 34, -9, -23,
				-8, 8, 115, 120, -105, 112, -105, 36, 97, -97, -46, -15, -29, -14, 51, 111, 0, -79, 49, -24, -127, 104,
				-91, -106, 32, -33, -45, 79, 57, 110, -95, 14, 75, -43, 127, -103, -114, 81, -128, -62, 96, -48, 11, 75,
				-31, -18, -45, -57, -57, -104, -85, 118, -113, -74, -116, -23, 120, -127, 57, -8, -84, -8, 5, 49, -24,
				-50, -76, 106, 31, 97, 14, -73, 124, -42, 82, -79, 15, 53, 65, -125, -97, 66, -1, 28, -28, -118, 65,
				119, -80, 19, -18, -49, 81, 87, 115, -36, -118, 52, -13, 37, -20, 12, -67, 19, 6, -35, -112, 54, -53,
				-91, -65, -67, 1, -61, 125, -103, 9, 115, -91, 127, -54, -26, -67, 29, 6, 61, 51, -83, -34, 15, 48, 87,
				-16, -37, -96, -101, -112, -96, 75, -32, 7, 80, 86, 12, 122, 38, 41, -32, 18, 106, 105, -102, 111, -64,
				-22, 125, -88, 9, 115, -107, 63, 73, -95, -97, 64, 7, 99, -48, 15, -92, -51, -13, -57, 104, 111, -60,
				60, -118, 1, 12, -4, -63, 24, -12, 61, 49, -32, -18, 6, 48, -16, 123, 99, -48, 111, -120, 1, 47, 110, 0,
				3, 127, 99, 12, -6, 53, 105, 31, -4, 11, 48, -32, 81, 12, 96, -32, -81, -115, 65, 127, 11, 29, 69, -105,
				10, -2, 16, 20, -47, 54, 29, -49, 24, -8, -85, -3, 17, 116, -87, 20, 114, -103, 38, -109, 5, 29, 61, 40,
				-86, 62, 29, 71, 93, -41, -3, 50, 77, 19, -25, -31, 47, -63, -118, 126, 1, -10, -61, -85, 53, -90, -29,
				-104, -43, -3, 77, 12, -6, 14, 54, -45, -101, -79, 77, 97, 63, 1, -3, -118, 65, 87, 90, -59, -65, 66,
				-7, 103, -67, 41, -113, 41, 29, 119, 89, -35, 103, -17, -128, 36, -28, 50, -102, -2, 29, 24, -14, -106,
				116, -23, -8, 41, -99, -37, -57, -96, 117, 87, 116, -35, -67, -27, 107, 112, 77, 122, -21, 100, -112,
				-18, -2, -102, -85, -5, 106, -125, -82, 35, -22, 91, -76, -69, 46, 125, 58, -9, 121, -2, -25, 101, -33,
				-71, 69, 119, -63, -65, -75, 100, -62, 60, -17, 62, 96, -123, 86, 23, -12, -122, 6, -36, -106, -83,
				-101, 38, -52, 123, -73, -19, 110, -32, 56, -31, 0, -38, -46, -111, 99, 119, -61, 73, -39, -77, -82,
				-123, 103, -23, 87, 57, 80, -73, -86, -96, 87, -36, 84, 95, 66, 44, -127, 30, -27, -25, 82, -51, -48,
				-99, 125, -19, 122, -52, -69, -28, -44, 24, -2, -43, 53, -27, 87, 19, 116, 93, -62, 42, 33, -17, 16,
				-33, 18, 108, -39, 111, -19, 85, -12, -115, 22, 117, -58, 98, -39, -44, -78, 71, 29, 38, -84, 104, 84,
				126, 21, 65, -41, -2, -8, 19, -60, -10, -21, -10, 74, -104, 55, 83, -84, -14, -123, 10, 90, -15, 123,
				-52, 27, 111, 72, -16, 35, 87, 123, -7, 27, 31, -81, 97, 59, -85, -26, -125, -82, -45, 43, 91, -60, 36,
				23, -38, -104, -114, -89, -104, 43, 119, -107, -31, -66, 74, -6, -5, 75, -32, -105, -3, -14, -94, 106,
				-66, -33, -34, 116, -48, 117, 126, 60, -30, -96, -37, -120, -71, 114, 15, 107, 121, 21, -46, -50, -117,
				40, -28, -58, -37, 33, -98, -90, -61, -34, 100, -48, -75, -7, 40, 33, -33, 32, -106, 17, -13, 20, -49,
				-120, 21, -45, 62, -3, 6, -15, -86, -4, -109, 116, 106, 30, -95, 65, -51, 5, 93, 67, 46, -85, -36, -94,
				-116, -84, 75, -59, -106, -90, -7, -64, -27, -104, -65, -89, 85, 126, -117, 88, -127, -105, -13, 116,
				-116, -58, 52, 21, -12, 96, 33, 95, 2, -2, 100, 45, -51, -13, 125, 5, 12, -68, -52, 120, -36, 109, -23,
				-68, 53, 19, -12, 96, 33, -105, -128, 111, 25, -16, -101, 9, 22, -8, -90, -62, -34, 68, -48, 3, -123,
				124, 4, -97, -121, 62, 88, -96, -64, 55, 19, -10, -22, -125, 30, 36, -28, 19, -26, -128, -113, -96, 108,
				-46, -71, -35, -96, -4, 40, 125, 19, 125, -10, -22, -73, -110, -22, -70, -18, -97, -23, -29, 83, -108,
				35, 83, 50, 18, -14, 127, -125, -78, -110, -83, -95, -46, -15, 52, -99, 99, 41, 72, 61, -54, -72, -99,
				-66, -65, 75, -65, -57, 11, 84, -84, -22, -118, 94, 120, -98, 92, -102, 117, -57, 124, 95, -104, 15,
				109, -50, -105, -36, 51, -96, -22, 121, -10, 106, 43, -70, -82, 120, -5, 59, -54, -112, -9, -125, -35,
				79, 119, -7, -1, -128, 92, -92, -65, -11, 105, -31, -22, -34, -89, -17, -106, -33, -29, 37, 42, 84, 101,
				69, 47, -72, 118, 125, 2, -5, -30, -59, 21, 126, 64, 105, -109, 78, -1, 51, 84, -90, -70, -118, -82, 39,
				-71, -60, 67, 8, -14, -99, 127, 101, 95, -68, 60, 105, 73, -91, -22, 42, 125, -26, 119, -31, 63, 8, 43,
				-107, -3, -101, -38, 90, 115, 85, 85, -12, -126, -3, 52, 89, -74, -70, 5, -123, -109, -82, -119, 45,
				-26, -111, 121, 79, 19, 42, 123, -60, -75, -74, -51, 33, -67, 67, 46, -13, -89, 119, 25, -14, -72, -12,
				-36, -36, -57, -17, -73, -55, -78, -42, 97, -34, 49, -72, 26, -43, 4, 93, 71, -40, 59, -8, -103, -46,
				113, -121, -3, -15, -8, -12, 121, -14, -69, -16, 13, 123, -81, -41, 100, 21, -86, 104, -70, -21, -62, 9,
				-49, 59, -24, -22, -74, 26, 106, 65, -95, -83, -62, -18, -41, -80, 113, 69, -8, -96, -21, -55, -5, 1,
				126, 59, -107, -56, 73, 59, -26, 58, -11, 58, -23, 74, 73, 41, 10, 71, -16, 33, -41, -55, -99, -24, 69,
				-31, 79, -120, 79, -6, -27, 94, 33, -105, -73, 114, 110, 64, -43, -46, 27, -12, -3, 20, -8, 1, 62, 107,
				-27, -105, 27, -53, 93, 4, 22, 122, 122, 77, 23, -59, 120, -35, -103, 25, -14, -122, -92, -23, -81, -25,
				105, 26, -20, 3, -8, 52, -29, 59, 125, -101, -21, -9, 8, 42, 108, -45, 93, -101, -20, 63, -63, 7, 67,
				-34, 40, -57, -54, 46, -18, 68, 93, 18, 29, 121, -44, -3, 59, -8, 96, -56, 27, -90, -25, -42, 107, 37,
				91, -40, 41, -73, -112, 77, 119, -57, 38, -5, -13, 116, 33, 124, 6, 106, -102, 54, -29, -91, 9, -1, 33,
				108, -67, 39, 107, -15, -45, -9, -115, 8, 38, 92, 69, -33, -39, 116, -64, -38, -39, -45, 103, -96, -75,
				-112, 115, -19, -47, -84, 126, -84, -41, 112, 40, 17, -101, -18, 30, 77, -10, 9, -13, -4, 39, -89, -48,
				86, 98, 25, -115, -121, -49, -94, -102, 112, 77, -8, 80, 65, -41, -123, 49, 29, 108, 77, 88, -47, -85,
				120, -24, 55, 122, -50, 101, 26, -52, -6, 6, 47, -85, -26, 74, -19, -109, 112, -95, 48, -93, -18, -114,
				15, -84, 84, -79, -110, -23, -90, 116, -95, 72, -18, -11, 6, -89, -115, -66, 61, -90, -121, 125, -53,
				81, -2, 110, 31, 68, -7, -5, 69, 90, 48, -29, -79, 55, -40, 73, -117, 33, 87, 50, 120, -103, -69, -55,
				40, -3, -38, 1, -115, -111, -25, 23, 82, -42, 101, -73, 24, -53, -89, -34, -28, -90, 43, 85, 125, -117,
				0, 66, 52, -35, -75, -102, 111, 96, 107, -32, 83, 104, -76, -48, 107, -63, -6, -90, -1, 32, -54, -64,
				92, -108, 62, -70, -11, -13, -60, 19, -26, 77, 28, -119, 118, 73, -117, 101, -126, -99, -27, -43, 96,
				-59, 21, 15, -70, -18, 24, -77, -127, 45, 62, -119, 70, 111, -48, -2, -77, -11, 20, -21, -111, -114, 9,
				20, 21, -95, -94, 91, -33, -15, 78, -72, 83, 43, 93, 70, -9, 27, 120, 10, 91, -34, 59, -32, -68, -95,
				104, -48, -11, 78, -41, -61, -50, -60, 126, 57, -67, 77, -70, 70, 100, -48, -52, -78, 24, -12, -91, -85,
				122, -23, -118, 110, 125, -89, 11, -3, -24, 32, -123, 98, -35, -124, 47, 90, -43, -117, 5, -35, -95,
				-102, -97, -80, 95, 78, -41, -91, -35, 59, -53, 1, -37, -94, 85, -67, 100, 69, -73, 124, 116, -112, 77,
				118, -38, -57, 19, -40, -114, -62, 23, -85, -22, 69, -126, -18, 48, 111, -50, -87, 52, -70, 49, -121,
				81, -8, -66, -44, -68, 122, -87, -118, 110, 121, 103, -109, -123, 49, 3, -120, -10, -96, -93, -16, 35,
				-20, 108, 80, -128, 123, -48, -11, -114, 102, -7, -84, 57, -85, 57, 29, -54, -78, -86, 63, -48, -25, 18,
				92, -107, -88, -24, 61, -20, 54, 123, 28, 56, 0, 71, -121, -46, 107, -56, 106, 110, 93, -82, -3, 13,
				-100, -107, 8, -70, 101, -77, -99, -43, -100, 114, -39, -62, -18, 113, -42, 123, 112, -26, 26, 116, -99,
				94, -24, 96, -125, -43, -100, -78, -47, -127, 57, -85, -86, -18, 62, -43, -26, 93, -47, 45, -89, -44,
				88, -51, 41, 55, -103, 110, -77, -86, -22, 94, -37, -104, -97, -15, 14, 122, 15, 27, -84, -26, -108,
				-99, 113, 85, -1, -36, 115, 80, -50, 45, -24, -23, 63, 74, -18, 96, 29, 108, -80, -102, -109, -107, 1,
				54, 36, -28, 30, 47, -105, 56, -29, 89, -47, -83, 6, 32, 88, -51, -55, -116, 94, 91, 35, 108, 120, -67,
				88, -62, 53, -24, 27, -40, -16, -38, -100, -97, -42, -53, -86, -59, 120, -28, -43, 124, 119, 9, -70, 54,
				-37, 45, 76, -70, -110, -119, -56, -116, -31, 106, 57, -73, -26, -69, 87, 69, -73, 106, -74, -77, 111,
				78, 94, 94, -64, -122, 75, -13, -35, 43, -24, 61, 108, -116, 32, -14, 49, -64, 102, -86, -51, 101, -102,
				-51, 60, -24, -122, -117, 100, 56, 8, 71, 110, 116, -86, 109, 68, 126, -73, 60, 22, -49, 120, 84, -12,
				30, 54, -84, -102, 82, 68, -105, 49, 91, 41, 7, 99, 30, 65, -1, 24, -7, -99, 54, -4, 34, 6, -118, 75,
				118, -95, -79, 104, -66, 91, 100, -28, 119, 76, -125, -82, 83, 7, 61, -14, 99, -56, -55, -99, 54, -33,
				45, 90, -110, -67, -11, 52, -101, 117, 69, -73, -102, 58, 96, -77, -99, 74, -79, 42, 50, -90, -45, 108,
				-42, 65, -17, 97, 99, 4, 81, 25, 35, 108, -102, -17, 61, 12, 89, 7, -35, -94, -17, 49, 70, 121, 67, 37,
				-83, -113, 94, 123, 22, 123, -64, -101, -10, -45, 107, 108, -70, -77, -39, 78, -91, 89, 92, -125, 117,
				54, -35, -11, -99, 106, 22, 3, 12, 124, -67, 18, -107, 102, 113, 13, -34, -6, -28, -109, 79, -34, -121,
				17, -53, -118, -34, 33, -65, 83, -82, 109, -89, -46, -12, 26, -52, -34, 125, 124, -3, -6, -75, -39,
				-101, -123, 44, -125, -34, 35, 63, 86, 115, -118, -62, -30, 90, 52, 107, -66, 91, 6, -3, 35, -28, -9,
				18, 68, 49, -4, -120, -4, 58, 24, -79, 12, -70, 69, -1, 124, 4, 81, 12, 35, -14, -77, 40, -114, 103, 44,
				-125, 110, -47, 12, 97, -45, -99, -94, -80, -72, 22, 59, -85, 21, 114, 38, 65, -41, 17, -9, -36, 78, 57,
				127, 78, 81, -24, -109, -109, -39, -81, -57, 119, -34, 121, -25, -49, 48, 96, 85, -47, 57, -83, 70, 107,
				-112, -3, -102, 76, 35, -17, 119, 96, -64, 42, -24, 22, 21, -35, 98, -16, -125, -24, 16, 63, 35, -65,
				14, 6, 106, -86, -24, 19, -120, 98, 49, -23, -89, -61, -128, 85, -48, 59, -28, 55, -127, 40, 22, -117,
				49, -93, -86, -6, -24, 22, 75, -7, 56, 16, 71, -47, -84, -66, -94, 103, 111, -70, -89, -47, -56, -97,
				64, 20, -117, 69, -15, -23, 96, -96, -102, -96, 127, -5, -19, -73, 22, 3, 31, 68, 123, -85, 105, 115,
				-46, 90, -6, -24, 108, -74, 83, 84, 19, -14, -22, 96, -64, -5, 109, -86, -5, 98, -48, -119, 14, -112,
				61, -24, 70, 75, -8, 38, 16, -59, 52, 33, 51, -117, 12, 89, 84, 116, -73, 119, 62, 19, -75, -56, 98, 25,
				108, 45, 77, 119, 34, 58, 64, 45, 65, -1, 5, 68, 49, 85, 49, 27, 84, 75, -48, -1, 11, 34, -38, 27, -101,
				-18, 68, 43, -64, -96, 19, 29, -58, 100, 109, 122, 110, -75, 4, -67, 3, 81, 76, 85, -52, 50, -79, -94,
				19, -83, -128, 69, -48, -71, -118, -115, -24, 0, -81, 95, -65, -50, 62, -53, -108, 61, -24, 70, -5, -70,
				117, 32, -118, -87, 67, 102, 22, 25, 98, -45, -99, -24, 48, -85, -18, -93, 79, -56, -85, 3, 81, 76, -71,
				-125, 62, -63, 64, 53, 21, -35, -14, 5, 116, 68, -5, -24, -5, -66, 67, 126, 38, 99, 92, -75, 84, 116,
				25, -96, 120, 23, 68, -79, 116, -56, -49, 36, -24, 127, -126, 13, -117, -11, -65, -78, -123, 52, -9,
				118, -65, -36, -104, -114, 99, -28, 53, -126, -82, 82, -51, 35, -39, 86, 65, -73, -72, 43, -15, -15,
				-41, 43, -24, -74, 70, 3, -56, 83, -121, -4, 76, 30, -110, -87, -90, -23, 14, -61, 23, -48, 17, -19,
				-87, 67, 126, -21, -18, -93, -125, 35, -17, 20, -113, 69, -15, 49, -23, -98, 90, 5, -67, -86, -105, -60,
				19, -19, -55, -28, 101, -94, 48, 96, 21, 116, -109, 62, 58, -89, -40, 40, 10, -35, -41, 45, -5, -72, 81,
				26, 107, -87, -89, -94, -21, 18, -66, 9, -103, 89, -67, 105, -110, 104, 15, 22, -43, -36, 108, 86, -55,
				114, -63, -116, -59, 47, -35, -125, 40, -122, 30, -7, -103, 61, 16, 102, 25, 116, -117, 105, 2, -114,
				-68, 83, 20, 31, 35, -65, -105, 48, 82, 91, 69, -25, -128, 28, 69, -63, -90, -69, 26, -111, -33, -83,
				52, 8, -46, -125, -88, 32, -67, 6, -85, 122, 81, -119, 89, -48, 117, -91, -106, 69, -97, -125, 85, -99,
				74, 51, -103, 86, -77, 26, 113, 23, -42, 79, -81, 89, -4, -30, -9, 64, 84, -106, -59, 53, 104, -6, 28,
				-121, 117, -48, 45, 6, 23, 110, 27, -67, -33, -115, -24, -83, -12, -38, -21, -111, -97, -39, 64, -100,
				-80, 14, -6, -120, -4, -84, -2, -48, 68, -41, -47, -61, -58, 8, 67, 53, 54, -35, 5, -101, -17, 84, -118,
				-43, -75, 87, 111, -45, 93, 87, -56, -115, -56, -17, -120, -51, 119, 42, -28, 8, -7, -115, 70, -101,
				-86, -2, -54, 99, 43, 41, -117, -66, -121, -124, -100, -93, -17, -28, 42, 21, 23, 9, -71, 69, -127, 49,
				-19, -97, 11, -113, -96, -113, -80, -15, 0, 68, -66, -84, -102, -19, 35, -116, -3, 1, 14, -46, -99, 80,
				-34, -122, -102, -5, 78, 40, 77, -99, 15, -84, -101, 60, 68, 66, 55, -126, -4, 9, -7, 77, -23, 26, -2,
				0, -58, -68, 118, -127, 125, -127, -4, -28, -58, -79, 1, -111, -113, 30, 54, 70, 56, -80, -38, 51, -18,
				-68, 33, 29, -97, 35, 63, 105, 74, 61, 1, 45, -53, 50, 31, 35, -81, -89, -87, -38, 60, 7, -119, -36,
				127, -37, -123, 69, 17, 124, -125, 87, -48, 101, -22, 64, -102, -40, -71, -101, -17, 114, 125, -9, 50,
				100, 9, -22, -112, -65, -22, 60, 3, 45, 55, -47, 14, -7, -99, 122, -35, 72, 93, -102, -18, -38, -113,
				-74, -102, 39, -76, -70, -45, 18, 45, 44, 90, -93, -62, -83, -75, -28, -7, -90, -106, 19, -40, -24,
				-115, -34, -104, 65, -76, 12, -62, 109, 96, -61, -91, -39, 46, 60, -125, -66, 52, -33, 45, 108, 64, 100,
				-61, -86, -59, 56, 121, -114, 127, -72, 5, 93, -101, -17, 86, 125, -66, 7, 92, 41, 71, -71, 25, 87, -13,
				17, -114, -68, 95, -78, 104, 117, 7, -109, -112, 63, 4, 81, 94, -106, -29, 63, 79, -31, -56, 53, -24,
				58, 58, 62, -62, 6, -85, 58, 101, 99, 92, -51, 95, 89, 110, 50, 113, -111, 18, -81, 77, -74, 26, -128,
				-112, -112, 115, 4, -98, 114, 105, -90, -102, -117, 18, 65, 31, 96, 55, 40, -9, -112, 35, -16, 116, 40,
				-29, 106, 62, -63, 113, 90, 109, -31, 30, 116, -29, 65, 57, -15, 21, -120, 14, 99, 89, -51, -51, 31, 73,
				-67, 72, -119, -118, 46, 44, -105, -83, -10, -36, 41, -106, -10, -107, 46, -99, 13, 108, -89, 107, -83,
				-42, -109, 92, -87, 72, -48, 117, -121, -40, 17, 118, -66, -30, -64, 28, -19, -55, -78, -102, 15, 122,
				-19, -69, 43, 85, -47, -123, -27, -99, -83, 3, -89, -37, -24, -122, 82, 113, -112, -112, 119, -80, 83,
				-92, -102, -117, 98, 65, 55, -98, 106, 19, -113, -45, -119, -29, 46, 52, 116, 45, 58, 0, -73, -123, -99,
				23, -91, -86, -71, 40, 89, -47, -123, -11, 29, -114, 3, 115, 116, 93, -33, -63, 86, -47, -57, -87, -117,
				6, -35, -95, -86, -53, 30, -16, 95, -128, -24, 10, 14, 77, -10, -95, -12, -93, -44, -91, 43, -70, -80,
				-82, -22, 15, 57, 10, 79, -105, -47, -18, -35, 22, -74, -118, -11, -51, 23, -59, -125, -18, 80, -43, 5,
				71, -31, -23, 13, -38, 47, -1, 26, -74, -122, -110, 125, -13, 69, -124, -118, 46, -114, 97, -85, 3, -5,
				-21, -12, 38, -21, 38, -69, 40, 94, -51, 69, -120, -96, -21, 29, -49, 122, -3, -17, -111, -10, -59,
				-120, -106, 126, -7, 6, -74, -98, 70, -88, -26, -62, 107, -49, -72, -21, -40, 98, -34, -78, -57, -78,
				-119, -67, 77, 39, -8, 101, -93, 123, -52, -55, -6, -23, 17, 121, 53, -71, -107, -74, -66, -120, 97, 11,
				91, 19, 2, 109, 92, -22, -78, -81, -5, 117, -91, 19, 32, -117, 92, -84, 71, -55, -27, -30, -67, 19, -27,
				78, 75, -66, -76, 95, 46, 83, 105, 29, 108, 29, -89, 107, 108, 64, 16, -95, -126, 46, -46, -119, -112,
				-109, -48, -61, -42, -108, -114, -69, 12, -5, -70, 56, -122, 92, -98, 55, -65, -125, 64, -94, 12, -58,
				-19, 122, 4, 123, 93, 58, -66, -26, 72, -4, 122, -24, -71, -106, 17, -10, 14, -10, -18, 35, -104, 112,
				65, -41, -99, 55, 60, 30, -52, -105, -7, 83, -114, -60, -81, -121, -100, 107, -113, 37, -47, 39, 17, 91,
				-118, 17, 43, -70, -40, 98, 110, 94, 91, -109, -111, 120, -122, -67, 113, 122, -114, 45, 94, 119, 124,
				-34, -108, 66, -66, 69, 64, 33, -125, -82, 15, -26, 91, -49, -83, 47, 54, 12, 123, -69, -12, -36, 110,
				-32, -29, 46, -126, -6, 35, -126, -102, -110, -82, -21, -34, 77, 63, -2, 5, -10, 110, -89, -17, -22,
				-46, 87, -70, 109, -88, 79, -10, -100, 67, 126, 18, -7, 61, 117, -31, 70, -35, -49, 75, 39, -21, 7, -8,
				-12, -83, -124, 44, 87, -12, 106, 73, -112, 33, -25, -112, -121, 27, 101, 63, 47, -46, -126, -103, -53,
				-56, 8, -90, -124, -35, 99, -124, 124, -93, -93, -77, -57, 35, -33, -69, 94, 37, 61, 127, 94, 125, 114,
				49, 33, -32, 40, -5, 121, 97, -101, -18, -117, -44, -100, 62, 77, -83, -22, -1, -91, 31, 63, -123, -113,
				15, -27, -69, -46, 119, 126, 35, -33, 13, -86, -122, -50, -109, -1, 11, -10, -21, 48, 118, 61, -86, 97,
				-91, 101, -8, -96, -117, 20, -72, -17, 29, -5, -21, -30, -67, 116, 28, -91, -17, 124, -63, -80, -41, 97,
				103, 49, -52, -121, -16, 35, -3, -14, 48, -53, 92, -81, 18, 117, 122, -19, 34, 91, -40, -67, 122, -7,
				34, 93, 58, -66, -45, 117, -47, 20, -104, -18, 55, 32, -35, -69, 14, 126, 94, 69, -99, 74, -69, 72, 53,
				65, -41, 62, -77, -12, -123, 38, -8, -23, 48, -81, -96, -29, 83, 111, 65, -23, -71, -111, 74, -18, -71,
				-54, 113, 66, 5, -3, -14, 93, -31, 71, -35, -49, -45, 29, 65, 126, -128, -65, 1, 65, 87, 61, -83, 81,
				-127, 65, -73, 69, -107, 15, 69, -43, -44, 116, 63, -93, 75, 100, 75, 76, -127, 109, 48, 55, -27, -71,
				-77, 108, 97, 59, 77, -11, 18, -35, -86, -29, 26, 111, -10, 85, 12, -58, -99, -105, 6, -56, 94, -91,
				-127, 50, 105, -115, -12, -16, 37, 85, -28, 111, -23, -69, -27, 119, 120, 9, 114, -89, -101, 125, 126,
				9, -33, -90, -6, 66, 90, 116, 95, -94, 66, -43, 53, -35, 119, -91, -109, 46, 35, -98, 15, 80, -58, 4,
				62, -22, -22, 70, 91, 82, 94, 15, -90, 92, -28, -92, -90, -63, -73, -13, -86, 14, -70, 72, 23, -64,
				-128, 121, 103, -102, 82, -74, -23, 2, 8, -79, 47, 88, -117, -76, 47, 46, 55, -13, 45, -54, 121, -106,
				-50, -15, 6, 21, 107, 33, -24, 114, 33, -56, -88, 107, -55, -66, -13, -124, -7, -114, 63, -128, -78,
				-47, -66, -72, 84, -15, 14, -27, 84, 31, 114, 81, 125, -48, 69, -112, -80, -117, 1, 28, -103, 63, -104,
				46, 126, -111, -128, -9, 40, 43, -4, 26, -10, -21, 106, 34, -24, 34, 80, -40, -59, 0, 6, -2, -58, -12,
				28, -54, -68, -8, 67, -108, 39, -77, 59, 119, 91, 121, -26, -95, -103, -96, -117, 96, 97, 23, 3, 24, -8,
				-73, -38, -23, -121, 75, -64, 35, 108, -17, -43, 84, -56, 69, 83, 65, 23, 1, -61, 46, 6, 48, -16, 111,
				-48, 38, -70, 12, -92, 70, 9, -72, 104, -94, 79, 126, 94, 115, 65, 23, 26, 118, -103, 122, 43, 57, 26,
				127, -111, 1, -13, -123, 52, 98, -59, 116, -112, 77, -102, -24, 61, 98, 105, 50, -28, -94, -55, -96, 47,
				10, -49, -77, 95, 101, -62, -4, -86, -98, 113, 45, 85, 94, 111, -66, -101, 116, -36, 67, -68, -128,
				-117, -86, -25, -55, -33, -90, -23, -96, -117, 116, -127, 109, 49, 87, -113, -88, -122, 116, -68, -120,
				-68, 13, -47, -66, 52, -36, -46, -123, -110, -101, 109, -113, 56, -51, -13, -13, -102, 14, -71, 104, 62,
				-24, -62, -23, 13, 48, -121, -110, -127, 31, 9, -69, -20, 91, 55, -42, 58, 16, -92, -31, -18, 49, 87,
				110, 89, -117, 30, 53, -36, 66, -2, -58, -113, -42, -80, -2, 97, 21, 65, 23, -70, -124, -46, 107, 3, -1,
				28, 70, -52, -95, 127, 21, -67, 79, -81, 125, 110, -7, -5, -34, -45, -49, -56, -31, 94, 76, -23, -72,
				-81, 15, 73, 53, 111, 53, 65, 23, -114, -81, -28, -55, 77, 42, -113, 92, -112, -14, 32, -51, -120, 57,
				-4, 69, 42, -66, -2, 13, 37, -52, 31, -21, 103, 45, -63, -34, 53, -94, -46, -89, -48, -10, -75, -86,
				-96, 47, 2, 15, -46, -35, -60, 18, 126, 57, 126, -58, 92, -95, -28, -33, -90, 67, 47, 96, 13, -13, -46,
				-65, -106, -49, -113, 48, -33, 28, 107, 12, -11, 121, -14, 42, -29, 8, 11, 114, 92, -83, 50, -24, 66,
				-5, -19, 50, 72, 87, -5, -123, 123, -103, 83, 61, -90, 11, -2, 109, -47, 93, -16, 115, -121, 54, -99,
				-67, 20, -92, -59, 65, -49, -21, 88, 109, -48, 69, -59, 77, 121, -70, -103, 17, 43, 107, -86, -97, -73,
				-22, -96, 47, 42, -104, -126, -93, -3, 61, -86, 101, -89, 86, 75, 12, -70, 10, -14, 72, 36, -27, 115,
				-74, -27, -40, 90, 70, -43, -33, -122, 65, -33, -95, 115, -64, 75, -33, -99, -22, 36, 125, -15, -89,
				-83, 47, -128, -71, 41, 6, -3, 2, -20, -69, 87, 107, -60, -54, -5, -30, -105, 97, -48, -81, -96, 35,
				-13, 50, 13, -41, -127, 34, 91, -51, 10, -73, 125, 85, -71, 11, -84, 23, 125, 21, -108, -84, 78, -109,
				-41, 65, 69, 122, -20, -107, 102, 18, -16, 127, -92, -29, -77, 20, -14, -17, 65, -105, 98, 69, -65, 38,
				109, -50, 111, 17, -17, -47, -41, -75, 26, -64, 103, -4, -81, -115, 65, -65, -95, -64, -49, 82, -81,
				-59, -120, 57, -32, 35, -24, -38, 24, -12, 61, 49, -16, -18, 70, 48, -32, 123, 99, -48, 15, -92, -127,
				-33, -128, 77, 122, 43, 3, -72, 43, -49, -63, 24, -12, 76, -40, -121, -49, -22, 108, 46, 60, 29, 79, 90,
				-38, -96, -79, 36, 6, 61, 51, 13, 124, -113, -71, 89, -33, -127, 110, 98, -60, -4, 12, -2, -64, -128,
				-25, -59, -96, 27, -38, 105, -42, -53, -122, 12, -83, 62, 37, 119, 40, 9, -12, -77, 116, 60, 103, -13,
				-36, 14, -125, -18, 36, 101, 126, -125, -33, -74, 87, 90, -69, 101, -37, 44, -10, -67, -99, 48, -24,
				-50, 116, 61, -67, -124, -67, -57, -70, 42, -3, -81, -107, 27, 5, 119, -56, 89, 43, 6, -67, 48, 109,
				-34, 75, -16, 101, 23, -105, 30, -19, 88, 118, -64, -87, 98, -33, -69, -42, 49, -24, -127, -20, 108,
				-113, -68, 4, -65, -90, -83, -101, 36, -40, 35, -26, 125, -19, -50, -74, -72, 98, -43, -114, -125, 65,
				15, 78, 119, -81, -19, 48, 87, 123, 9, -1, 114, 51, 40, 105, -62, 111, 123, -43, -55, -25, 106, 94, 68,
				81, 43, 6, -67, 82, 59, 55, -128, -27, 120, 31, -13, 77, 96, 57, 58, -20, 103, -62, -17, -9, -101, -37,
				-35, 120, -14, 21, 3, 93, 39, 6, -67, 113, -38, 29, 120, 91, -13, -1, -108, -51, 108, 34, 34, 34, 34,
				34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34,
				34, 34, 34, 34, 34, 34, 34, 34, -94, 50, -2, 15, 49, -98, -2, -111, 111, -27, 77, -127, 0, 0, 0, 0, 73,
				69, 78, 68, -82, 66, 96, -126 };
	}

}
