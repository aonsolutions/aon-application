package com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLUE;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.DARKEST;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.GREEN;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.LIGHT_GRAY;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.RED;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.WHITE;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.PAGE_TYPE.HORIZONTAL;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT.CENTER;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT.LEFT;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT.RIGHT;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfBox;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfPage;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfText;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.PAGE_TYPE;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll.beans.EnterprisePayroll;
import com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll.beans.EnterprisePayrollEntry;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;

public class EnterprisePayrollTemplate {

	private final static int y_limit = 75;

	private float x;
	private float y;
	private int page;
	private PDDocument doc;
	private PDPageContentStream contents;
	private EnterprisePayroll payroll;
	private ResourceBundle words;
	private double[] aon_totals;
	private double[] ss_totals;
	
	public static void print(EnterprisePayroll payroll, OutputStream out) throws CanNotCreatePdfException {
		try (PDDocument doc = new PDDocument()) {

			ResourceBundle words = ResourceBundle.getBundle("com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll.bundles.EnterprisePayrollBundle",Locale.forLanguageTag("Es"));
			PdfPage page = new PdfPage(HORIZONTAL);
			EnterprisePayrollTemplate t = new EnterprisePayrollTemplate();

			doc.addPage(page.getPage());
			PDPageContentStream contents = page.stream(doc);

			t.doc = doc;
			t.contents = contents;
			t.words = words;
			t.payroll = payroll;
			t.x = 0;
			t.y = 0;
			t.page = 0;
			t.aon_totals  = new double[] {0,0,0,0,0,0,0,0};
			t.ss_totals  = new double[] {0,0,0,0,0,0,0,0};
			
			draw_header(t);
			draw_entries(t);
			draw_total(t);

			PdfText page_number = new PdfText(765, 15, 50, 30, 5, 5, t.contents, t.words.getString("PAGE") + " " + t.page, BLACK, HELVETICA, 8f, RIGHT);
			page_number.draw();
			
			t.contents.close();
			t.doc.save(out);
		} catch (Exception e) {
			throw new CanNotCreatePdfException(e);
		}
	}

	private static void draw_header(EnterprisePayrollTemplate t) {

		Optional<InputStream> logo = t.payroll.getLogo();
		String date_txt = PdfFormats.formatDate(new Date(), t.words.getString("DATE FORMAT")).orElse("");
		String header_txt = t.payroll.getHeader().orElse("Nómina de empresa");
		String subheader_txt = t.payroll.getSubheader().orElse("");
		String employee_title_txt = t.words.getString("EMPLOYEE");
		String type_title_txt = t.words.getString("TYPE");
		String accrued_title_txt = t.words.getString("ACCRUED");
		String employee_ss_title_txt = t.words.getString("EMPLOYEE SS");
		String irpf_title_txt = t.words.getString("IRPF");
		String deduction_title_txt = t.words.getString("DEDUCTIONS");
		String amount_title_txt = t.words.getString("AMOUNT");
		String enterprise_ss_title_txt = t.words.getString("ENTERPRISE SS");
		String total_cost_title_txt = t.words.getString("TOTAL COST");
		String total_ss_title_txt = t.words.getString("TOTAL SS");

		t.x =20;
		t.y = 550;

		PdfText header = new PdfText(t.x, t.y, 200, 30, 5, 8, t.contents, header_txt, BLACK,HELVETICA_BOLD, 18f, LEFT);
		t.y -= 20;

		PdfText subheader = new PdfText(t.x, t.y, 200, 20, 5, 5, t.contents, subheader_txt, BLACK, HELVETICA_BOLD, 12f, LEFT);
		PdfText date = new PdfText(t.x + 668, t.y, 130, 20, 5, 5, t.contents, date_txt, BLACK, HELVETICA,12f, RIGHT);
		t.y -= 30;

		PdfText employee_title = new PdfText(t.x, t.y, 200, 20, 5, 6, t.contents, employee_title_txt, WHITE, HELVETICA_BOLD, 9f, LEFT);
		t.x += 201;

		float col_width = 65;
		PdfText type_title = new PdfText(t.x, t.y, col_width, 20, 5, 6, t.contents, type_title_txt, WHITE, HELVETICA_BOLD, 9f, RIGHT);
		t.x += (col_width + 1);

		PdfText accrued_title = new PdfText(t.x, t.y, col_width, 20, 5, 6, t.contents, accrued_title_txt, WHITE, HELVETICA_BOLD, 9f, RIGHT);
		t.x += (col_width + 1);

		PdfText employee_ss_title = new PdfText(t.x, t.y, col_width, 20, 5, 6, t.contents, employee_ss_title_txt, WHITE, HELVETICA_BOLD, 9f, RIGHT);
		t.x += (col_width + 1);

		PdfText irpf_title = new PdfText(t.x, t.y, col_width, 20, 5, 6, t.contents, irpf_title_txt, WHITE, HELVETICA_BOLD, 9f, RIGHT);
		t.x += (col_width + 1);

		PdfText deduction_title = new PdfText(t.x, t.y, col_width, 20, 5, 6, t.contents, deduction_title_txt, WHITE, HELVETICA_BOLD, 9f, CENTER);
		t.x += (col_width + 1);

		PdfText amount_title = new PdfText(t.x, t.y, col_width, 20, 5, 6, t.contents, amount_title_txt, WHITE, HELVETICA_BOLD, 9f, RIGHT);
		t.x += (col_width + 1);

		PdfText enterprise_ss_title = new PdfText(t.x, t.y, col_width, 20, 5, 6, t.contents, enterprise_ss_title_txt,WHITE, HELVETICA_BOLD, 9f, RIGHT);
		t.x += (col_width + 1);

		PdfText total_cost_title = new PdfText(t.x, t.y, col_width, 20, 5, 6, t.contents, total_cost_title_txt,WHITE, HELVETICA_BOLD, 9f, RIGHT);
		t.x += (col_width + 1);

		PdfText total_ss_title = new PdfText(t.x, t.y, col_width, 20, 5, 6, t.contents, total_ss_title_txt, WHITE, HELVETICA_BOLD, 9f, RIGHT);

		header.draw();
		subheader.draw();
		date.draw();

		employee_title.square(DARKEST);
		employee_title.draw();

		type_title.square(DARKEST);
		type_title.draw();

		accrued_title.square(DARKEST);
		accrued_title.draw();

		employee_ss_title.square(DARKEST);
		employee_ss_title.draw();

		irpf_title.square(DARKEST);
		irpf_title.draw();

		deduction_title.square(DARKEST);
		deduction_title.draw();

		amount_title.square(DARKEST);
		amount_title.draw();

		enterprise_ss_title.square(DARKEST);
		enterprise_ss_title.draw();

		total_cost_title.square(DARKEST);
		total_cost_title.draw();

		total_ss_title.square(DARKEST);
		total_ss_title.draw();

	}

	private static void draw_entries(EnterprisePayrollTemplate t) {

		t.y -= 20;
		t.x = 20;

		Map<String, Map<String, EnterprisePayrollEntry>> entries = t.payroll.getEntries().orElse(new HashMap<>());
		entries.entrySet().stream().forEach(category -> {
			t.y -= 10;
			PdfText category_title = new PdfText(t.x + 100, t.y, 100, 20, 5, 6, t.contents, category.getKey(),
					BLACK, HELVETICA_BOLD, 9f, RIGHT);
			PdfText month_title = new PdfText(t.x, t.y, 100, 20, 5, 6, t.contents,
					PdfFormats.formatDate(t.payroll.getMonth().orElse(null), "MMMM, yyyy").orElse(""), BLACK,
					HELVETICA_BOLD, 9f, LEFT);

			category_title.draw();
			month_title.draw();
			t.y -= 15;
			
			double[] subtotal_aon = new double[] {0,0,0,0,0,0,0,0};
			double[] subtotal_ss = new double[] {0,0,0,0,0,0,0,0};
			category.getValue().entrySet().stream().forEach(entry -> {
				try {
					draw_entry(t, entry);
					add_to_subtotal(subtotal_aon,subtotal_ss,entry.getValue());
				} catch (IOException e) {e.printStackTrace();}
			});
			
			add_to_total(subtotal_aon,subtotal_ss,t);
			
			try {draw_subtotal(t,subtotal_aon,subtotal_ss);} 
			catch (IOException e) {e.printStackTrace();}
			
			t.y -= 10;
		});
	}

	private static void add_to_subtotal(double[] subtotal_aon, double[] subtotal_ss, EnterprisePayrollEntry e) {
		
		subtotal_aon[0] += e.getDevengado().orElse(0.00);
		subtotal_aon[1] += e.getSsTrab().orElse(0.00);
		subtotal_aon[2] += e.getIrpf().orElse(0.00);
		subtotal_aon[3] += e.getDeducciones().orElse(0.00);
		subtotal_aon[4] += e.getLiquido().orElse(0.00);
		subtotal_aon[5] += e.getSsEmpr().orElse(0.00);
		subtotal_aon[6] += e.getCosteTotal().orElse(0.00);
		subtotal_aon[7] += e.getSsTotal().orElse(0.00);
		
		subtotal_ss[0] += e.getDevengadoSS().orElse(0.00);
		subtotal_ss[1] += e.getSsTrabSS().orElse(0.00);
		subtotal_ss[2] += e.getIrpfSS().orElse(0.00);
		subtotal_ss[3] += e.getDeduccionesSS().orElse(0.00);
		subtotal_ss[4] += e.getLiquidoSS().orElse(0.00);
		subtotal_ss[5] += e.getSsEmprSS().orElse(0.00);
		subtotal_ss[6] += e.getCosteTotalSS().orElse(0.00);
		subtotal_ss[7] += e.getSsTotalSS().orElse(0.00);
	}

	private static void add_to_total(double[] subtotal_aon, double[] subtotal_ss, EnterprisePayrollTemplate t) {
		t.aon_totals[0] += subtotal_aon[0];
		t.aon_totals[1] += subtotal_aon[1];
		t.aon_totals[2] += subtotal_aon[2];
		t.aon_totals[3] += subtotal_aon[3];
		t.aon_totals[4] += subtotal_aon[4];
		t.aon_totals[5] += subtotal_aon[5];
		t.aon_totals[6] += subtotal_aon[6];
		t.aon_totals[7] += subtotal_aon[7];
		
		t.ss_totals[0] += subtotal_ss[0];
		t.ss_totals[1] += subtotal_ss[1];
		t.ss_totals[2] += subtotal_ss[2];
		t.ss_totals[3] += subtotal_ss[3];
		t.ss_totals[4] += subtotal_ss[4];
		t.ss_totals[5] += subtotal_ss[5];
		t.ss_totals[6] += subtotal_ss[6];
		t.ss_totals[7] += subtotal_ss[7];
		
	}
	
	private static void draw_entry(EnterprisePayrollTemplate t, Entry<String, EnterprisePayrollEntry> entry) throws IOException {
		
		EnterprisePayrollEntry e = entry.getValue();
		if(e.Has_aon() && e.Has_ss()) draw_entry_aon_ss(t,e);
		else 
			if(e.Has_aon()) 	draw_entry_only_aon(t,e);
			else if(e.Has_ss()) draw_entry_only_ss(t,e);			
		
		check(t);
	}
	
	private static void draw_entry_aon_ss(EnterprisePayrollTemplate t, EnterprisePayrollEntry e) {
		
		PdfText aon_employee_name = new PdfText(t.x, t.y, 200, 20, 5, 6, t.contents, e.getEmpleado().orElse(""), BLACK, HELVETICA, 9f, LEFT);
		PdfText ss_employee_name = new PdfText(t.x, t.y - 10, 200, 20, 5, 6, t.contents, e.getEmpleadoSS().orElse(""), BLACK, HELVETICA, 9f, LEFT);
		
		Color ssEntryColor = (e.getTipo().equals(e.getTipoSS())) ? GREEN : RED;		
		PdfText aon_type = new PdfText(t.x, t.y, 266, 20, 5, 6, t.contents, e.getTipo().orElse(""), BLACK, HELVETICA, 9f, RIGHT);
		PdfText ss_type = new PdfText(t.x, t.y - 10, 266, 20, 5, 6, t.contents, e.getTipoSS().orElse(""), ssEntryColor, HELVETICA, 9f, RIGHT);
		
		ssEntryColor = (e.getDevengado().equals(e.getDevengadoSS())) ? GREEN : RED;	
		PdfText aon_accrued = new PdfText(t.x, t.y, 332, 20, 5, 6, t.contents, e.getDevengado().orElse(0.00) + "", PdfColors.BLACK, HELVETICA, 9f, RIGHT);
		PdfText ss_accrued = new PdfText(t.x, t.y - 10, 332, 20, 5, 6, t.contents, e.getDevengadoSS().orElse(0.00) + "", ssEntryColor, HELVETICA, 9f, RIGHT);
		
		ssEntryColor = (e.getSsTrab().equals(e.getSsTrabSS())) ? GREEN : RED;	
		PdfText aon_employee_ss = new PdfText(t.x, t.y, 398, 20, 5, 6, t.contents, e.getSsTrab().orElse(0.00) + "", BLACK, HELVETICA, 9f, RIGHT);
		PdfText ss_employee_ss = new PdfText(t.x, t.y - 10, 398, 20, 5, 6, t.contents, e.getSsTrabSS().orElse(0.00) + "", ssEntryColor, HELVETICA, 9f, RIGHT);
		
		ssEntryColor = (e.getIrpf().equals(e.getIrpfSS())) ? GREEN : RED;	
		PdfText aon_irpf = new PdfText(t.x, t.y, 464, 20, 5, 6, t.contents, e.getIrpf().orElse(0.00) + "", BLACK, HELVETICA, 9f, RIGHT);
		PdfText ss_irpf = new PdfText(t.x, t.y - 10, 464, 20, 5, 6, t.contents, e.getIrpfSS().orElse(0.00) + "", ssEntryColor, HELVETICA, 9f, RIGHT);
		
		ssEntryColor = (e.getDeducciones().equals(e.getDeduccionesSS())) ? GREEN : RED;	
		PdfText aon_deductions = new PdfText(t.x, t.y, 530, 20, 5, 6, t.contents, e.getDeducciones().orElse(0.00) + "",BLACK, PdfFonts.HELVETICA, 9f, TEXT_ALIGNMENT.RIGHT);
		PdfText ss_deductions = new PdfText(t.x, t.y - 10, 530, 20, 5, 6, t.contents, e.getDeduccionesSS().orElse(0.00) + "",ssEntryColor, PdfFonts.HELVETICA, 9f, TEXT_ALIGNMENT.RIGHT);
		
		ssEntryColor = (e.getLiquido().equals(e.getLiquidoSS())) ? GREEN : RED;	
		PdfText aon_amount = new PdfText(t.x, t.y, 596, 20, 5, 6, t.contents, e.getLiquido().orElse(0.00) + "", BLACK, HELVETICA, 9f, RIGHT);
		PdfText ss_amount = new PdfText(t.x, t.y - 10, 596, 20, 5, 6, t.contents, e.getLiquidoSS().orElse(0.00) + "", ssEntryColor, HELVETICA, 9f, RIGHT);
		
		ssEntryColor = (e.getSsEmpr().equals(e.getSsEmprSS())) ? PdfColors.GREEN : RED;	
		PdfText aon_enterprise_ss = new PdfText(t.x, t.y, 662, 20, 5, 6, t.contents, e.getSsEmpr().orElse(0.00) + "", BLACK, HELVETICA, 9f, RIGHT);
		PdfText ss_enterprise_ss = new PdfText(t.x, t.y - 10, 662, 20, 5, 6, t.contents, e.getSsEmprSS().orElse(0.00) + "", ssEntryColor, HELVETICA, 9f, RIGHT);
		
		ssEntryColor = (e.getCosteTotal().equals(e.getCosteTotalSS())) ? GREEN : RED;	
		PdfText aon_total_cost = new PdfText(t.x, t.y, 728, 20, 5, 6, t.contents, e.getCosteTotal().orElse(0.00) + "", BLACK, HELVETICA, 9f, RIGHT);
		PdfText ss_total_cost = new PdfText(t.x, t.y - 10, 728, 20, 5, 6, t.contents, e.getCosteTotalSS().orElse(0.00) + "",ssEntryColor, HELVETICA, 9f, RIGHT);
		
		ssEntryColor = (e.getSsTotal().equals(e.getSsTotalSS())) ? GREEN : RED;	
		PdfText aon_total_ss = new PdfText(t.x, t.y, 794, 20, 5, 6, t.contents, e.getSsTotal().orElse(0.00) + "", BLACK,HELVETICA, 9f, RIGHT);
		PdfText ss_total_ss = new PdfText(t.x, t.y - 10, 794, 20, 5, 6, t.contents, e.getSsTotalSS().orElse(0.00) + "", ssEntryColor, HELVETICA, 9f, RIGHT);		
		
		
		aon_employee_name.draw();
		ss_employee_name.draw();
		
		aon_type.draw();
		ss_type.draw();
		
		aon_accrued.draw();
		ss_accrued.draw();
		
		aon_employee_ss.draw();
		ss_employee_ss.draw();
		
		aon_irpf.draw();
		ss_irpf.draw();
		
		aon_deductions.draw();
		ss_deductions.draw();
		
		aon_amount.draw();
		ss_amount.draw();
		
		aon_enterprise_ss.draw();
		ss_enterprise_ss.draw();
		
		aon_total_cost.draw();
		ss_total_cost.draw();
		
		aon_total_ss.draw();
		ss_total_ss.draw();
		
		t.y -= 22;
	}
	
	private static void draw_entry_only_aon( EnterprisePayrollTemplate t, EnterprisePayrollEntry e) {
		PdfText aon_employee_name = 	new PdfText(t.x, t.y, 200, 20, 5, 6, t.contents, e.getEmpleado().orElse(""), BLUE, HELVETICA, 9f, LEFT);		
		PdfText aon_type = 				new PdfText(t.x, t.y, 266, 20, 5, 6, t.contents, e.getTipo().orElse(""), BLUE, HELVETICA, 9f, RIGHT);
		PdfText aon_accrued = 			new PdfText(t.x, t.y, 332, 20, 5, 6, t.contents, e.getDevengado().orElse(0.00) + "", BLUE, HELVETICA, 9f, RIGHT);
		PdfText aon_employee_ss = 		new PdfText(t.x, t.y, 398, 20, 5, 6, t.contents, e.getSsTrab().orElse(0.00) + "", BLUE, HELVETICA, 9f, RIGHT);
		PdfText aon_irpf = 				new PdfText(t.x, t.y, 464, 20, 5, 6, t.contents, e.getIrpf().orElse(0.00) + "", BLUE, HELVETICA, 9f, RIGHT);
		PdfText aon_deductions = 		new PdfText(t.x, t.y, 530, 20, 5, 6, t.contents, e.getDeducciones().orElse(0.00) + "",BLUE, HELVETICA, 9f, RIGHT);
		PdfText aon_amount = 			new PdfText(t.x, t.y, 596, 20, 5, 6, t.contents, e.getLiquido().orElse(0.00) + "", BLUE, HELVETICA, 9f, RIGHT);
		PdfText aon_enterprise_ss = 	new PdfText(t.x, t.y, 662, 20, 5, 6, t.contents, e.getSsEmpr().orElse(0.00) + "", BLUE, HELVETICA, 9f, RIGHT);
		PdfText aon_total_cost = 		new PdfText(t.x, t.y, 728, 20, 5, 6, t.contents, e.getCosteTotal().orElse(0.00) + "",BLUE, HELVETICA, 9f, RIGHT);
		PdfText aon_total_ss = 			new PdfText(t.x, t.y, 794, 20, 5, 6, t.contents, e.getSsTotal().orElse(0.00) + "", BLUE, HELVETICA, 9f, RIGHT);
		
		aon_employee_name.draw();
		aon_type.draw();
		aon_accrued.draw();
		aon_employee_ss.draw();
		aon_irpf.draw();
		aon_deductions.draw();
		aon_amount.draw();
		aon_enterprise_ss.draw();
		aon_total_cost.draw();
		aon_total_ss.draw();
		
		t.y -= 12;
	}
	
	private static void draw_entry_only_ss( EnterprisePayrollTemplate t, EnterprisePayrollEntry e) {
		PdfText ss_employee_name = new PdfText(t.x, t.y, 200, 20, 5, 6, t.contents, e.getEmpleadoSS().orElse(""), BLACK, HELVETICA, 9f, LEFT);
		
		Color ssEntryColor = 		GREEN;
		PdfText ss_type = 			new PdfText(t.x, t.y, 266, 20, 5, 6, t.contents, e.getTipoSS().orElse(""), ssEntryColor,HELVETICA, 9f, RIGHT);	
		PdfText ss_accrued = 		new PdfText(t.x, t.y, 332, 20, 5, 6, t.contents, e.getDevengadoSS().orElse(0.00) + "", ssEntryColor,HELVETICA, 9f, RIGHT);
		PdfText ss_employee_ss =	new PdfText(t.x, t.y, 398, 20, 5, 6, t.contents, e.getSsTrabSS().orElse(0.00) + "", ssEntryColor,HELVETICA, 9f, RIGHT);
		PdfText ss_irpf = 			new PdfText(t.x, t.y, 464, 20, 5, 6, t.contents, e.getIrpfSS().orElse(0.00) + "", ssEntryColor,HELVETICA, 9f, RIGHT);
		PdfText ss_deductions = 	new PdfText(t.x, t.y, 530, 20, 5, 6, t.contents, e.getDeduccionesSS().orElse(0.00) + "",ssEntryColor, HELVETICA, 9f, RIGHT);
		PdfText ss_amount = 		new PdfText(t.x, t.y, 596, 20, 5, 6, t.contents, e.getLiquidoSS().orElse(0.00) + "", ssEntryColor,HELVETICA, 9f, RIGHT);
		PdfText ss_enterprise_ss = 	new PdfText(t.x, t.y, 662, 20, 5, 6, t.contents, e.getSsEmprSS().orElse(0.00) + "", ssEntryColor, HELVETICA, 9f, RIGHT);
		PdfText ss_total_cost = 	new PdfText(t.x, t.y, 728, 20, 5, 6, t.contents, e.getCosteTotalSS().orElse(0.00) + "",ssEntryColor, HELVETICA, 9f, RIGHT);
		PdfText ss_total_ss = 		new PdfText(t.x, t.y, 794, 20, 5, 6, t.contents, e.getSsTotalSS().orElse(0.00) + "", ssEntryColor,HELVETICA, 9f, RIGHT);		
		
		ss_employee_name.draw();
		ss_type.draw();
		ss_accrued.draw();
		ss_employee_ss.draw();
		ss_irpf.draw();
		ss_deductions.draw();
		ss_amount.draw();
		ss_enterprise_ss.draw();
		ss_total_cost.draw();
		ss_total_ss.draw();
		
		t.y -= 12;
	}
	
	private static void check(EnterprisePayrollTemplate t) throws IOException {
		if(t.y <= y_limit) {
			
			PdfText page_number = new PdfText(765, 15, 50, 30, 5, 5, t.contents, t.words.getString("PAGE") + " " + t.page, BLACK, HELVETICA, 8f, RIGHT);
			page_number.draw();
			
			PdfPage page = new PdfPage(HORIZONTAL);
			t.doc.addPage(page.getPage());
			t.contents.close();
			t.contents = page.stream(t.doc);
			t.page++;
			
			draw_header(t);
			t.x = 20;
			t.y = 475;

		}
	}
	

	private static void draw_subtotal(EnterprisePayrollTemplate t, double[] subtotal, double[] subtotal_ss) throws IOException {
		
		PdfBox box = new PdfBox(23, t.y + 10, 790, .15f, PdfColors.GRAY, t.contents);
		t.y -= 10;
		
		PdfText title = new PdfText(t.x, t.y, 266, 15, 5, 5, t.contents, "SUBTOTAL", BLACK, HELVETICA_BOLD, 9f, RIGHT);  
		PdfText devengado = new PdfText(t.x, t.y, 332, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal[0]), BLACK, HELVETICA, 8f, RIGHT);  
		PdfText ss_trab = new PdfText(t.x, t.y, 398, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal[1]), BLACK, HELVETICA, 8f, RIGHT);  
		PdfText irpf = new PdfText(t.x, t.y, 464, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal[2]), BLACK, HELVETICA, 8f, RIGHT);  
		PdfText deductions = new PdfText(t.x, t.y, 530, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal[3]), BLACK, HELVETICA, 8f, RIGHT);  
		PdfText liquido = new PdfText(t.x, t.y, 596, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal[4]), BLACK, HELVETICA, 8f, RIGHT);  
		PdfText ss_empr = new PdfText(t.x, t.y, 662, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal[5]), BLACK, HELVETICA, 8f, RIGHT);  
		PdfText total_cost = new PdfText(t.x, t.y, 728, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal[6]), BLACK, HELVETICA, 8f, RIGHT);  
		PdfText total_ss = new PdfText(t.x, t.y, 794, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal[7]), BLACK, HELVETICA, 8f, RIGHT);  
		
		box.draw();
		title.draw();
		devengado.draw();
		ss_trab.draw();
		irpf.draw();
		deductions.draw();
		liquido.draw();
		ss_empr.draw();
		total_cost.draw();
		total_ss.draw();
		
		double sum = subtotal_ss[0] + subtotal_ss[1] + subtotal_ss[2] + subtotal_ss[3] + subtotal_ss[4] + subtotal_ss[5] + subtotal_ss[6] + subtotal_ss[7];
		if(sum != 0) {
			
			t.y -= 10;
			
			title = new PdfText(t.x, t.y, 266, 15, 5, 5, t.contents, "SUBTOTAL SS", BLACK, HELVETICA_BOLD, 7f, RIGHT);  
			devengado = new PdfText(t.x, t.y, 332, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal_ss[0]), BLACK, HELVETICA, 8f, RIGHT);  
			ss_trab = new PdfText(t.x, t.y, 398, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal_ss[1]), BLACK, HELVETICA, 8f, RIGHT);  
			irpf = new PdfText(t.x, t.y, 464, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal_ss[2]), BLACK, HELVETICA, 8f, RIGHT);  
			deductions = new PdfText(t.x, t.y, 530, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal_ss[3]), BLACK, HELVETICA, 8f, RIGHT);  
			liquido = new PdfText(t.x, t.y, 596, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal_ss[4]), BLACK, HELVETICA, 8f, RIGHT);  
			ss_empr = new PdfText(t.x, t.y, 662, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal_ss[5]), BLACK, HELVETICA, 8f, RIGHT);  
			total_cost = new PdfText(t.x, t.y, 728, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal_ss[6]), BLACK, HELVETICA, 8f, RIGHT);  
			total_ss = new PdfText(t.x, t.y, 794, 15, 5, 5, t.contents, PdfFormats.to_latin_number(subtotal_ss[7]), BLACK, HELVETICA, 8f, RIGHT);  
	
			title.draw();
			devengado.draw();
			ss_trab.draw();
			irpf.draw();
			deductions.draw();
			liquido.draw();
			ss_empr.draw();
			total_cost.draw();
			total_ss.draw();
			
		}
		t.x = 20;
		
	}

	private static void draw_total(EnterprisePayrollTemplate t) throws IOException {
		
		t.y -= 30;
		PdfBox box = new PdfBox(23, t.y - 22.5f, 790, 50f, LIGHT_GRAY, t.contents);
		
		
		PdfText title = new PdfText(t.x, t.y, 266, 15, 10, 5, t.contents, "TOTAL EMPRESA: ", BLACK, HELVETICA_BOLD, 9f, LEFT);  
		PdfText devengado = new PdfText(t.x, t.y, 332, 15, 7, 5, t.contents, PdfFormats.to_latin_number(t.aon_totals[0]), BLACK, HELVETICA, 8f, RIGHT);  
		PdfText ss_trab = new PdfText(t.x, t.y, 398, 15, 7, 5, t.contents, PdfFormats.to_latin_number(t.aon_totals[1]), BLACK, HELVETICA, 8f, RIGHT);  
		PdfText irpf = new PdfText(t.x, t.y, 464, 15, 7, 5, t.contents, PdfFormats.to_latin_number(t.aon_totals[2]), BLACK, HELVETICA, 8f, RIGHT);  
		PdfText deductions = new PdfText(t.x, t.y, 530, 15, 7, 5, t.contents, PdfFormats.to_latin_number(t.aon_totals[3]), BLACK, HELVETICA, 8f, RIGHT);  
		PdfText liquido = new PdfText(t.x, t.y, 596, 15, 7, 5, t.contents, PdfFormats.to_latin_number(t.aon_totals[4]), BLACK, HELVETICA, 8f, RIGHT);  
		PdfText ss_empr = new PdfText(t.x, t.y, 662, 15, 7, 5, t.contents, PdfFormats.to_latin_number(t.aon_totals[5]), BLACK, HELVETICA, 8f, RIGHT);  
		PdfText total_cost = new PdfText(t.x, t.y, 728, 15, 7, 5, t.contents, PdfFormats.to_latin_number(t.aon_totals[6]), BLACK, HELVETICA, 8f, RIGHT);  
		PdfText total_ss = new PdfText(t.x, t.y, 794, 15, 7, 5, t.contents, PdfFormats.to_latin_number(t.aon_totals[7]), BLACK, HELVETICA, 8f, RIGHT);  
		
		box.draw();
		title.draw();
		devengado.draw();
		ss_trab.draw();
		irpf.draw();
		deductions.draw();
		liquido.draw();
		ss_empr.draw();
		total_cost.draw();
		total_ss.draw();
		
		double sum = t.ss_totals[0] + t.ss_totals[1] + t.ss_totals[2] + t.ss_totals[3] + t.ss_totals[4] + t.ss_totals[5] + t.ss_totals[6] + t.ss_totals[7];
		if(sum != 0) {
			
			t.y -= 10;
			
			title = new PdfText(t.x, t.y, 266, 15, 10, 5, t.contents, "TOTAL SS: ", BLACK, HELVETICA_BOLD, 7f, LEFT);  
			devengado = new PdfText(t.x, t.y, 332, 15, 5, 5, t.contents, PdfFormats.to_latin_number(t.ss_totals[0]), BLACK, HELVETICA, 8f, RIGHT);  
			ss_trab = new PdfText(t.x, t.y, 398, 15, 7, 5, t.contents, PdfFormats.to_latin_number(t.ss_totals[1]), BLACK, HELVETICA, 8f, RIGHT);  
			irpf = new PdfText(t.x, t.y, 464, 15, 7, 5, t.contents, PdfFormats.to_latin_number(t.ss_totals[2]), BLACK, HELVETICA, 8f, RIGHT);  
			deductions = new PdfText(t.x, t.y, 530, 15, 7, 5, t.contents, PdfFormats.to_latin_number(t.ss_totals[3]), BLACK, HELVETICA, 8f, RIGHT);  
			liquido = new PdfText(t.x, t.y, 596, 15, 7, 5, t.contents, PdfFormats.to_latin_number(t.ss_totals[4]), BLACK, HELVETICA, 8f, RIGHT);  
			ss_empr = new PdfText(t.x, t.y, 662, 15, 7, 5, t.contents, PdfFormats.to_latin_number(t.ss_totals[5]), BLACK, HELVETICA, 8f, RIGHT);  
			total_cost = new PdfText(t.x, t.y, 728, 15, 7, 5, t.contents, PdfFormats.to_latin_number(t.ss_totals[6]), BLACK, HELVETICA, 8f, RIGHT);  
			total_ss = new PdfText(t.x, t.y, 794, 15, 7, 5, t.contents, PdfFormats.to_latin_number(t.ss_totals[7]), BLACK, HELVETICA, 8f, RIGHT);  
	
			title.draw();
			devengado.draw();
			ss_trab.draw();
			irpf.draw();
			deductions.draw();
			liquido.draw();
			ss_empr.draw();
			total_cost.draw();
			total_ss.draw();
			
		}
		
		t.x = 20;
		
	}
}
