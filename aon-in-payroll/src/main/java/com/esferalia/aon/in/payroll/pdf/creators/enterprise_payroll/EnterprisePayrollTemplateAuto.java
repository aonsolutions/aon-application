package com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLUE;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.GRAY;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.GREEN;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.LIGHT_GRAY;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.RED;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats.to_latin_number;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.PAGE_TYPE.HORIZONTAL;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT.LEFT;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT.RIGHT;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfFile;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfTable;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfText;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll.beans.ColManager;
import com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll.beans.EnterprisePayroll;
import com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll.beans.EnterprisePayrollEntry;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;

public class EnterprisePayrollTemplateAuto extends PdfFile {


	private EnterprisePayroll payroll;
	private ColManager mg;

	public static void print(EnterprisePayroll payroll, OutputStream out, Optional<Locale> language) throws CanNotCreatePdfException {

		EnterprisePayrollTemplateAuto t = null;
		try {
			ResourceBundle words = ResourceBundle.getBundle("com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll.bundles.EnterprisePayrollBundle",	language.orElse(new Locale("Es")));
			t = new EnterprisePayrollTemplateAuto(7.5f, 500, new PDDocument(), words, out, payroll,75f);
			t.set_defaults(HELVETICA, 10f, BLACK, PdfColors.GRAY);
			t.new_page(HORIZONTAL);
			PdfTable table = calculate_colums(t);
			
			draw_header(t);
			draw_entries(t, table);	
			draw_totals(t,table);
			
			new PdfText(720, 20, 100, 20, t.contents, t.text("PAGE") + " " + t.page , GRAY, HELVETICA, t.fontsize, RIGHT).draw();		
			t.print();
		} catch (Exception e) {
			if (t != null)try {t.close();} catch (IOException e1) {}
			throw new CanNotCreatePdfException(e);
		}
	}

	private static void draw_header(EnterprisePayrollTemplateAuto t) {
		
		String date_txt = PdfFormats.formatDate(new Date(), t.words.getString("DATE FORMAT")).orElse("");
		String header_txt = t.payroll.getHeader().orElse("Nómina de empresa");
		String subheader_txt = t.payroll.getSubheader().orElse("");
		
		t.x = 7;
		t.y = 550;

		PdfText header = new PdfText(t.x, t.y, 200, 30, 5, 8, t.contents, header_txt, BLACK,HELVETICA_BOLD, 18f, LEFT);
		t.y -= 20;

		PdfText subheader = new PdfText(t.x, t.y, 200, 20, 5, 5, t.contents, subheader_txt, BLACK, HELVETICA_BOLD, 12f, LEFT);
		PdfText date = new PdfText(t.x + 700, t.y, 130, 20, 5, 5, t.contents, date_txt, BLACK, HELVETICA,12f, RIGHT);
		t.y -= 30;
		
		
		header.draw();
		subheader.draw();
		date.draw();
	}

	private static PdfTable calculate_colums(EnterprisePayrollTemplateAuto t) throws IOException {
		
		t.mg = new ColManager();
		
		Map<String, Map<String, EnterprisePayrollEntry>> entries = t.payroll.getEntries().orElse(new HashMap<>());
		entries.entrySet().stream().forEach(category -> category.getValue().entrySet().stream().forEach(entry -> t.mg.add(entry.getValue())));
	
		if(t.mg.entries_for_col("trabajador") > 0) 		t.mg.enable("trabajador");
		if(t.mg.entries_for_col("tipo") > 0) 			t.mg.enable("tipo");
		if(t.mg.entries_for_col("devengado") > 0)		t.mg.enable("devengado");
		if(t.mg.entries_for_col("ssTrab") > 0)			t.mg.enable("ssTrab");
		if(t.mg.entries_for_col("irpf") > 0)			t.mg.enable("irpf");
		if(t.mg.entries_for_col("deducciones") > 0)		t.mg.enable("deducciones");
		if(t.mg.entries_for_col("liquido") > 0)			t.mg.enable("liquido");
		if(t.mg.entries_for_col("ssEmpr") > 0)			t.mg.enable("ssEmpr");
		if(t.mg.entries_for_col("costeTotal") > 0)		t.mg.enable("costeTotal");
		if(t.mg.entries_for_col("bonificaciones") > 0)	t.mg.enable("bonificaciones");
		if(t.mg.entries_for_col("ssTotal") > 0)			t.mg.enable("ssTotal");
		
		int cols = t.mg.count_enabled();  
		float tb = 0;	
		float c = 0;
		if(t.mg.isActive("trabajador")) {
			tb = 25;
			c = (100 - tb) / (cols-1);
		}
		else c = (100 - tb) / (cols);
		
		
		
		float[] sizes = new float[cols];
		String[] headers = new String[cols];
		TEXT_ALIGNMENT[] alignments = new TEXT_ALIGNMENT[cols];
		
		int current = 0;
		if(t.mg.isActive("trabajador")) 		current = set_column(current,sizes,headers,alignments,tb,"Trabajador",LEFT);
		if(t.mg.isActive("tipo"))				current = set_column(current,sizes,headers,alignments,c,"Tipo",LEFT);
		if(t.mg.isActive("devengado"))  		current = set_column(current,sizes,headers,alignments,c,"Devengado",RIGHT);
		if(t.mg.isActive("ssTrab")) 			current = set_column(current,sizes,headers,alignments,c,"S.S. Trab.",RIGHT);
		if(t.mg.isActive("irpf")) 				current = set_column(current,sizes,headers,alignments,c,"I.R.P.F",RIGHT);
		if(t.mg.isActive("deducciones")) 		current = set_column(current,sizes,headers,alignments,c,"Deducciones",RIGHT);
		if(t.mg.isActive("liquido"))  			current = set_column(current,sizes,headers,alignments,c,"Liquido",RIGHT);
		if(t.mg.isActive("ssEmpr")) 	 		current = set_column(current,sizes,headers,alignments,c,"S.S. Empr.",RIGHT);
		if(t.mg.isActive("bonificaciones")) 	current = set_column(current,sizes,headers,alignments,c,"Bonificaciones",RIGHT);
		if(t.mg.isActive("ssTotal")) 			current = set_column(current,sizes,headers,alignments,c,"Total S.S",RIGHT);		
		if(t.mg.isActive("costeTotal")) 		current = set_column(current,sizes,headers,alignments,c,"Coste total",RIGHT);
		
		
		return create_table(t,sizes,headers,alignments);
	}
	
	private static int set_column(int current, float[] sizes, String[] headers, TEXT_ALIGNMENT[] alignments, float tb, String text, TEXT_ALIGNMENT align) {
		sizes[current] = tb;
		headers[current] = text;
		alignments[current] = align;
		current ++;
		return current;
	}

	private static PdfTable create_table(EnterprisePayrollTemplateAuto t, float[] sizes, String[] headers,TEXT_ALIGNMENT[] alignments) throws IOException {
		
		PdfTable table = new PdfTable(t.x, t.y, t.contents, 816, 15, 1,sizes,headers);
		table.header_height = 20;
		table.fontsize = 9;
		table.header_fontsize = 8;
		
		table.set_alignment(alignments);
		table.draw_header();
		t.y = table.getY();
		
		return table;
	}

	private static void draw_entries(EnterprisePayrollTemplateAuto t, PdfTable table) {

		Map<String, Map<String, EnterprisePayrollEntry>> entries = t.payroll.getEntries().orElse(new HashMap<>());
		entries.entrySet().stream().forEach(category -> {
						
			PdfText category_title = new PdfText(t.x + 100, table.getY() , 100, 20, 5, 6, t.contents, category.getKey(), BLACK,HELVETICA_BOLD, 9f, RIGHT);
			PdfText month_title = new PdfText(t.x, table.getY(), 100, 20, 5, 6, t.contents,PdfFormats.formatDate(t.payroll.getMonth().orElse(null), "MMMM, yyyy").orElse(""), BLACK,HELVETICA_BOLD, 9f, LEFT);

			category_title.draw();
			month_title.draw();
			table.jump(15);
			
			category.getValue().entrySet().stream().forEach(entry -> {
				try {
					draw_entry(t, entry,table);
					t.mg.add_to_subtotal(entry.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			try {draw_subtotal(t,table);}
			catch (IOException e) {e.printStackTrace();}
			table.jump(10f);
			t.mg.add_to_total();
		});
	}

	private static void draw_subtotal(EnterprisePayrollTemplateAuto t, PdfTable table) throws IOException {
		ArrayList<Double> subtotal_aon = 	t.mg.get_aon_subtotal();
		ArrayList<Double> subtotal_ss = 	t.mg.get_ss_subtotal();
		
		table.draw_line();
		table.jump(15f);
		
		if(subtotal_aon.stream().mapToDouble(p-> p).sum() != 0) {
			table.add_to_cell(0, "SUBTOTAL");
			
			for (int i = 2; i < subtotal_aon.size(); i++) 
				table.add_to_cell(i, to_latin_number(subtotal_aon.get(i)));
			
			table.new_row();
		}
		
		if(subtotal_ss.stream().mapToDouble(p-> p).sum() != 0) {
			table.add_to_cell(0, "SUBTOTAL SS");
			
			table.fontsize = 7.5f;
			
			for (int i = 2; i < subtotal_ss.size(); i++) 
				table.add_to_cell(i, to_latin_number(subtotal_ss.get(i)));
			
			table.new_row();
		}
		table.fontsize = 9;	
	}

	private static void draw_entry(EnterprisePayrollTemplateAuto t, Entry<String, EnterprisePayrollEntry> entry, PdfTable table)
			throws IOException {

		EnterprisePayrollEntry e = entry.getValue();
		if (e.Has_aon() && e.Has_ss()) draw_entry_aon_ss(t, e , table);
		else if (e.Has_aon()) 	draw_entry_only_aon(t, e, table);
		else if (e.Has_ss())  	draw_entry_only_ss(t, e, table);

		t.y = table.getY();
		check(t,table);
	}

	private static void draw_entry_aon_ss(EnterprisePayrollTemplateAuto t, EnterprisePayrollEntry e, PdfTable table) throws IOException {
		String empleado = 		e.getEmpleado().orElse(null);
		String tipo = 			e.getTipo().orElse(null);
		Double devengado = 		e.getDevengado().orElse(null);
		Double ssTrab = 		e.getSsTrab().orElse(null);
		Double irpf = 			e.getIrpf().orElse(null);
		Double deducciones = 	e.getDeducciones().orElse(null);
		Double liquido = 		e.getLiquido().orElse(null);
		Double ssEmpresa = 		e.getSsEmpr().orElse(null);
		Double bonificaciones = e.getBonificaciones().orElse(null);
		Double costeTotal = 	e.getCosteTotal().orElse(null);
		Double ssTotal = 		e.getSsTotal().orElse(null);
		
		String empleadoSS = 		e.getEmpleadoSS().orElse(null);
		String tipoSS = 			e.getTipoSS().orElse(null);
		Double devengadoSS = 		e.getDevengadoSS().orElse(null);
		Double ssTrabSS = 			e.getSsTrabSS().orElse(null);
		Double irpfSS = 			e.getIrpfSS().orElse(null);
		Double deduccionesSS = 		e.getDeduccionesSS().orElse(null);
		Double liquidoSS = 			e.getLiquidoSS().orElse(null);
		Double ssEmpresaSS = 		e.getSsEmprSS().orElse(null);
		Double bonificacionesSS = 	e.getBonificacionesSS().orElse(null);
		Double costeTotalSS = 		e.getCosteTotalSS().orElse(null);
		Double ssTotalSS = 			e.getSsTotalSS().orElse(null);
		
		table.add_to_cell(table.get_column("Trabajador"), 		empleado);
		table.add_to_cell(table.get_column("Tipo"), 			tipo);
		table.add_to_cell(table.get_column("Devengado"), 		to_latin_number(devengado));
		table.add_to_cell(table.get_column("S.S Trab."), 		to_latin_number(ssTrab));
		table.add_to_cell(table.get_column("I.R.P.F"), 			to_latin_number(irpf));
		table.add_to_cell(table.get_column("Deducciones"), 		to_latin_number(deducciones));
		table.add_to_cell(table.get_column("Liquido"), 			to_latin_number(liquido));
		table.add_to_cell(table.get_column("S.S Empr."), 		to_latin_number(ssEmpresa));
		table.add_to_cell(table.get_column("Bonificaciones"), 	to_latin_number(bonificaciones));
		table.add_to_cell(table.get_column("Total S.S"), 		to_latin_number(ssTotal));
		table.add_to_cell(table.get_column("Coste total"), 		to_latin_number(costeTotal));

		table.new_row();
		
		table.add_to_cell(table.get_column("Trabajador"), 		empleadoSS);
		table.add_to_cell(table.get_column("Tipo"), 			tipoSS);
		table.add_to_cell(table.get_column("Devengado"), 		to_latin_number(devengadoSS));
		table.add_to_cell(table.get_column("S.S Trab."), 		to_latin_number(ssTrabSS));
		table.add_to_cell(table.get_column("I.R.P.F"), 			to_latin_number(irpfSS));
		table.add_to_cell(table.get_column("Deducciones"), 		to_latin_number(deduccionesSS));
		table.add_to_cell(table.get_column("Liquido"), 			to_latin_number(liquidoSS));
		table.add_to_cell(table.get_column("S.S Empr."), 		to_latin_number(ssEmpresaSS));
		table.add_to_cell(table.get_column("Bonificaciones"), 	to_latin_number(bonificacionesSS));
		table.add_to_cell(table.get_column("Total S.S"), 		to_latin_number(ssTotalSS));
		table.add_to_cell(table.get_column("Coste total"), 		to_latin_number(costeTotalSS));
		
		table.pain_cell(table.get_column("Tipo"), 			(tipo != null && tipoSS != null && tipo.equals(tipoSS)) ? 											GREEN : RED);
		table.pain_cell(table.get_column("Devengado"), 		(devengado != null && devengadoSS != null && devengado.equals(devengadoSS)) ? 						GREEN : RED);
		table.pain_cell(table.get_column("S.S. Trab."), 	(ssTrab != null && ssTrabSS != null && ssTrab.equals(ssTrabSS)) ? 									GREEN : RED);
		table.pain_cell(table.get_column("I.R.P.F"), 		(irpf != null && irpfSS != null && tipo.equals(irpfSS)) ? 											GREEN : RED);
		table.pain_cell(table.get_column("Deducciones"), 	(deducciones != null && deduccionesSS != null && deducciones.equals(deduccionesSS)) ? 				GREEN : RED);
		table.pain_cell(table.get_column("Liquido"), 		(liquido != null && liquidoSS != null && liquido.equals(liquidoSS)) ? 								GREEN : RED);
		table.pain_cell(table.get_column("S.S. Empr."), 	(ssEmpresa != null && ssEmpresaSS != null && ssEmpresa.equals(ssEmpresaSS)) ? 						GREEN : RED);
		table.pain_cell(table.get_column("Bonificaciones"), (bonificaciones != null && bonificacionesSS != null && bonificaciones.equals(bonificacionesSS)) ? 	GREEN : RED);
		table.pain_cell(table.get_column("Total S.S"), 		(ssTotal != null && ssTotalSS != null && ssTotal.equals(ssTotalSS)) ? 								GREEN : RED);
		table.pain_cell(table.get_column("Coste total"), 	(costeTotal != null && costeTotalSS != null && costeTotal.equals(costeTotalSS)) ? 					GREEN : RED);

		table.new_row();
		t.y = table.getY();
	}

	private static void draw_entry_only_aon(EnterprisePayrollTemplateAuto t, EnterprisePayrollEntry e, PdfTable table) throws IOException {
		String empleado = 		e.getEmpleado().orElse(null);
		String tipo = 			e.getTipo().orElse(null);
		Double devengado = 		e.getDevengado().orElse(null);
		Double ssTrab = 		e.getSsTrab().orElse(null);
		Double irpf = 			e.getIrpf().orElse(null);
		Double deducciones = 	e.getDeducciones().orElse(null);
		Double liquido = 		e.getLiquido().orElse(null);
		Double ssEmpresa = 		e.getSsEmpr().orElse(null);;
		Double bonificaciones = e.getBonificaciones().orElse(null);
		Double costeTotal = 	e.getCosteTotal().orElse(null);
		Double ssTotal = 		e.getSsTotal().orElse(null);
		
		table.add_to_cell(table.get_column("Trabajador"), 		empleado);
		table.add_to_cell(table.get_column("Tipo"), 			tipo);
		table.add_to_cell(table.get_column("Devengado"), 		to_latin_number(devengado));
		table.add_to_cell(table.get_column("S.S Trab."), 		to_latin_number(ssTrab));
		table.add_to_cell(table.get_column("I.R.P.F"), 			to_latin_number(irpf));
		table.add_to_cell(table.get_column("Deducciones"), 		to_latin_number(deducciones));
		table.add_to_cell(table.get_column("Liquido"), 			to_latin_number(liquido));
		table.add_to_cell(table.get_column("S.S Empr."), 		to_latin_number(ssEmpresa));
		table.add_to_cell(table.get_column("Bonificaciones"), 	to_latin_number(bonificaciones));
		table.add_to_cell(table.get_column("Total S.S"), 		to_latin_number(ssTotal));
		table.add_to_cell(table.get_column("Coste total"), 		to_latin_number(costeTotal));
		
		table.pain_cell(table.get_column("Tipo"), BLUE);
		table.pain_cell(table.get_column("Devengado"), BLUE);
		table.pain_cell(table.get_column("S.S. Trab."), BLUE);
		table.pain_cell(table.get_column("I.R.P.F"), BLUE);
		table.pain_cell(table.get_column("Deducciones"), BLUE);
		table.pain_cell(table.get_column("Liquido"), BLUE);
		table.pain_cell(table.get_column("S.S. Empr."), BLUE);
		table.pain_cell(table.get_column("Bonificaciones"), BLUE);
		table.pain_cell(table.get_column("Total S.S"), BLUE);
		table.pain_cell(table.get_column("Coste total"), BLUE);
		
		table.new_row();
		t.y = table.getY();
	}

	private static void draw_entry_only_ss(EnterprisePayrollTemplateAuto t, EnterprisePayrollEntry e, PdfTable table) throws IOException {
		
		String empleadoSS = 		e.getEmpleadoSS().orElse(null);
		String tipoSS = 			e.getTipoSS().orElse(null);
		Double devengadoSS = 		e.getDevengadoSS().orElse(null);
		Double ssTrabSS = 			e.getSsTrabSS().orElse(null);
		Double irpfSS = 			e.getIrpfSS().orElse(null);
		Double deduccionesSS = 		e.getDeduccionesSS().orElse(null);
		Double liquidoSS = 			e.getLiquidoSS().orElse(null);
		Double ssEmpresaSS = 		e.getSsEmprSS().orElse(null);
		Double bonificacionesSS = 	e.getBonificacionesSS().orElse(null);
		Double costeTotalSS = 		e.getCosteTotalSS().orElse(null);
		Double ssTotalSS = 			e.getSsTotalSS().orElse(null);
		
		table.add_to_cell(table.get_column("Trabajador"), 		empleadoSS);
		table.add_to_cell(table.get_column("Tipo"), 			tipoSS);
		table.add_to_cell(table.get_column("Devengado"), 		to_latin_number(devengadoSS));
		table.add_to_cell(table.get_column("S.S. Trab."), 		to_latin_number(ssTrabSS));
		table.add_to_cell(table.get_column("I.R.P.F"), 			to_latin_number(irpfSS));
		table.add_to_cell(table.get_column("Deducciones"), 		to_latin_number(deduccionesSS));
		table.add_to_cell(table.get_column("Liquido"), 			to_latin_number(liquidoSS));
		table.add_to_cell(table.get_column("S.S. Empr."), 		to_latin_number(ssEmpresaSS));
		table.add_to_cell(table.get_column("Bonificaciones"), 	to_latin_number(bonificacionesSS));
		table.add_to_cell(table.get_column("Total S.S"), 		to_latin_number(ssTotalSS));
		table.add_to_cell(table.get_column("Coste total"), 		to_latin_number(costeTotalSS));
		
		table.pain_cell(table.get_column("Tipo"), GREEN);
		table.pain_cell(table.get_column("Devengado"), GREEN);
		table.pain_cell(table.get_column("S.S. Trab."), GREEN);
		table.pain_cell(table.get_column("I.R.P.F"), GREEN);
		table.pain_cell(table.get_column("Deducciones"), GREEN);
		table.pain_cell(table.get_column("Liquido"), GREEN);
		table.pain_cell(table.get_column("S.S. Empr."), GREEN);
		table.pain_cell(table.get_column("Bonificaciones"), GREEN);
		table.pain_cell(table.get_column("Total S.S"), GREEN);
		table.pain_cell(table.get_column("Coste total"), GREEN);
		
		table.new_row();
		t.y = table.getY();
	}

	private static void check(EnterprisePayrollTemplateAuto t, PdfTable table) throws IOException {
		if (t.jump()) {
			new PdfText(720, 20, 100, 20, t.contents, t.text("PAGE") + " " + t.page , GRAY, HELVETICA, t.fontsize, RIGHT).draw();
			t.new_page(HORIZONTAL);
			table.setStream(t.contents);

			draw_header(t);		
			table.setY(500);
			table.draw_header();	
		}
	}
	
	private static void draw_totals(EnterprisePayrollTemplateAuto t, PdfTable table) throws IOException {
		
		table.header_color = LIGHT_GRAY;
		table.create_box(0, 10, 3);
		table.header_color = BLACK;
		
		ArrayList<Double> total_aon = t.mg.get_aon_total();
		ArrayList<Double> total_ss = t.mg.get_ss_total();
		table.font = HELVETICA_BOLD;

		if(total_aon.stream().mapToDouble(p-> p).sum() != 0) {
			table.add_to_cell(0, "TOTAL:");
			for (int i = 2; i < total_aon.size(); i++) 
				table.add_to_cell(i, to_latin_number(total_aon.get(i)));
			table.new_row();
		}
		
		
		
		if(total_ss.stream().mapToDouble(p-> p).sum() != 0) {
			table.add_to_cell(0, "TOTAL SS:");
			for (int i = 2; i < total_ss.size(); i++) 
				table.add_to_cell(i, to_latin_number(total_ss.get(i)));
			
			table.new_row();
		}
		table.font = HELVETICA;
	}

	
	
	public EnterprisePayrollTemplateAuto(float x, float y, PDDocument doc, ResourceBundle words, OutputStream out,
			EnterprisePayroll payroll,float limit) {
		super(x, y, doc, words, out, limit);
		this.payroll = payroll;
	}

	
}
