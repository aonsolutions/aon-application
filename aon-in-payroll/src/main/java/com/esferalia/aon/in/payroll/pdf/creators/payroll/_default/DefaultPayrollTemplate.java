package com.esferalia.aon.in.payroll.pdf.creators.payroll._default;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.Contingency_bases;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayroll;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayrollAccrual;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayrollDeduction;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.UnknownCraException;
import com.esferalia.aon.in.payroll.pdf.creators.payroll.commons.CraTypes;
import com.esferalia.aon.in.payroll.pdf.creators.payroll.commons.DeductionTypes;
import com.esferalia.aon.in.payroll.pdf.creators.payroll.commons.PayrollTypes;

public class DefaultPayrollTemplate {

	static float fontSize = 9f;
	
	String filename = "./payroll.pdf";
	float limit;
	PDPageContentStream contents;

	PDDocument doc;
	DefaultPayroll payroll;
	Optional<InputStream> logo;
	Locale lang;
	
	float x;
	float y;
	ResourceBundle words;

	// PRINT THE PDF
	public void print(String out, DefaultPayroll payroll, Optional<InputStream> logo, Optional<Locale> language) throws CanNotCreatePdfException {
		try (PDDocument doc = new PDDocument()) {

			
			lang = language.orElse(new Locale("Es"));
			words = ResourceBundle.getBundle("com.esferalia.aon.in.payroll.pdf.creators.payroll._default.bundles.PayrollBundle",lang);

			filename = out;
			limit = 800;
			this.payroll = payroll;

			if (payroll == null)
				throw new CanNotCreatePdfException("No payroll found.");

			PDPage page = PDFToolkit.createVerticalPage();
			doc.addPage(page);

			contents = new PDPageContentStream(doc, page);

			draw_header();
			boolean jump = calculate();

			this.doc = doc;
			this.logo = logo;

			if (jump) {
				draw_accruals();
				PDFToolkit.drawBorderedBox(contents, 10, 10, 575, 695, PdfColors.LIGHT_GRAY);
				contents.close();

				page = PDFToolkit.createVerticalPage();
				doc.addPage(page);
				contents = new PDPageContentStream(doc, page);

				draw_header();
				PDFToolkit.drawBorderedBox(contents, 10, 175, 575, 532, PdfColors.LIGHT_GRAY);
				y -= 15;

				draw_deductions();
				draw_footer();

			} else {
				PDFToolkit.drawBorderedBox(contents, 10, 175, 575, 532, PdfColors.LIGHT_GRAY);
				draw_accruals();
				draw_deductions();
				draw_footer();
			}

			contents.close();
			doc.save(new File(filename));
		} catch (Exception e) {throw new CanNotCreatePdfException(e);}
	}

	// DRAW THE HEADER
	private void draw_header() throws IOException {

		x = 20;
		y = 795;
		String date_format = words.getString("FORMATO FECHA");
		String replace = PayrollTypes.toString(payroll.getPayrollType().orElse(PayrollTypes.Type.SALARY),lang); 
		
		//HEADER CONTENT
		String title = 			words.getString("TITULO").replace("*",replace).toUpperCase();
		String enterprise = 	words.getString("EMPRESA") + ": " + payroll.getEnterprise().orElse("");
		String employee = 		words.getString("TRABAJADOR") + ": " + payroll.getEmployee().orElse("");
		String address = 		words.getString("DOMICILIO") + ": " + payroll.getAddress().orElse("");
		String nif = 			words.getString("NIF") + ": " + payroll.getNif().orElse("");
		String nss = 			words.getString("NSS") + ": " + payroll.getNss().orElse("");
		String profes_group = 	words.getString("G.PROFESIONAL") + ": " + payroll.getProfessional_group().orElse("");
		String ccc = 			words.getString("CCC") + ": " + payroll.getCcc().orElse("");
		String cif = 			words.getString("CIF") + ": " + payroll.getCif().orElse("");
		String cotiz_group = 	words.getString("G.COTIZ") + ": " + payroll.getQuotation_group().orElse("");
		String antiqu_date = 	words.getString("FECHA ANTIGUEDAD") + ": " + PdfFormats.formatDate(payroll.getAntiquity().get(),date_format).orElse("");
		String day_total = 		words.getString("TOTAL DIAS") + ": " + payroll.getTotal_days().orElse(0);
		String liquid_period = 	words.getString("PERIODO LIQUIDACION") + ": " + PdfFormats.formatDate(payroll.getLiquid_period_start().orElse(null), date_format).get() + " - "
				+ PdfFormats.formatDate(payroll.getLiquid_period_end().orElse(null), date_format).get();
		
		
		//BUILDING THE HEADER
		PDFToolkit.drawBorderedBox(contents,x - 10,y - 85, 575, 120,PdfColors.LIGHT_GRAY);
		PDFToolkit.drawTextCenter(contents,new PDRectangle(x, y + 4, 530, 100),title, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, 12, 12);

		y -= 60;
		PDFToolkit.drawBox(contents, x, y, 275, 65, PdfColors.LIGHT_GRAY);
		PDFToolkit.drawBox(contents, x + 282, y, 275, 65, PdfColors.LIGHT_GRAY);

		y += 50;
		x += 10;
		PDFToolkit.drawText(contents,enterprise,x,y, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize);
		PDFToolkit.drawText(contents,employee,x + 280, y, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize);

		y -= 13.5;
		PDFToolkit.drawText(contents, address, x,y, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize);
		PDFToolkit.drawText(contents,nif,x + 280,y, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize);
		
		PDFToolkit.drawText(contents,nss,x + 380,y, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize);

		y -= 13.5;
		PDFToolkit.drawText(contents, payroll.getAddress_2().orElse(""), x, y,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize);
		PDFToolkit.drawText(contents, profes_group,x + 280, y, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize);

		y -= 13.5;
		PDFToolkit.drawText(contents,cif,x,y,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize);
		PDFToolkit.drawText(contents,cotiz_group,x + 280,y, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize);
	

		x += 100;
		PDFToolkit.drawText(contents,ccc,x,y,PdfColors.BLACK,PdfFonts.HELVETICA, fontSize);
		PDFToolkit.drawText(contents,antiqu_date,x+280,y,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize);
		

		y -= 25;
		x -= 100;
		PDFToolkit.drawText(contents, liquid_period,x,y,PdfColors.BLACK,PdfFonts.HELVETICA, fontSize);
		PDFToolkit.drawText(contents,day_total,x + 280,y,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize);
	}

	// CHECK IF JUMPS
	private boolean calculate() throws IOException {

		Optional<Map<Integer, ArrayList<DefaultPayrollAccrual>>> accruals = payroll.getAccruals();
		Optional<Map<Integer, ArrayList<DefaultPayrollDeduction>>> deductions = payroll.getDeductions();

		double sum = 0;

		sum += 20;
		sum += accruals.get().entrySet().size() * 12;
		sum += accruals.get().entrySet().stream().mapToDouble(entry -> entry.getValue().stream().count()).sum() * 12;

		sum += 20;
		sum += deductions.get().entrySet().size() * 12;
		sum += deductions.get().entrySet().stream().mapToDouble(entry -> entry.getValue().stream().count()).sum() * 12;

		return sum > 370;
	}

	// DRAW ACCRUALS
	private  void draw_accruals() throws IOException {

		x = 25;
		y = 675;

		//ACCRUAL CONTENT
		String title = words.getString("DEVENGOS").toUpperCase() + ".";
		String totals = words.getString("TOTALES").toUpperCase();
		String accrual_total_title = "A." + words.getString("TOTAL DEVENGADO").toUpperCase() + ":";
		String accrual_total = PdfFormats.to_latin_number(payroll.getAccrual_total().orElse(0.00));
		
		//BUILD THE ACCRUALS
		PDFToolkit.drawText(contents,title,x,y,PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize + 3);
		PDFToolkit.drawBox(contents,x + 470,y - 7, 80, 20,PdfColors.LIGHT_GRAY);
		PDFToolkit.drawTextRight(contents,new PDRectangle(x + 450,y-7,100,20), totals, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, 7, 7);
		y -= 20;

		Optional<Map<Integer, ArrayList<DefaultPayrollAccrual>>> accruals = payroll.getAccruals();
		// FOR EACH ACCRUAL
		accruals.get().entrySet().stream().sorted(Map.Entry.<Integer, ArrayList<DefaultPayrollAccrual>>comparingByKey()).forEach(m -> {
			try {
				//CONTENT
				double local_total = m.getValue().stream().mapToDouble(accrual -> accrual.getAmount().orElse(0.00)).sum();
				String accrual_txt = m.getKey() + ". " + CraTypes.get_type(m.getKey(),lang);
				String accrual_total_txt = PdfFormats.to_latin_number(local_total);
				
				//BUILD 
				PDFToolkit.drawText(contents, accrual_txt,x,y, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize+1);
				PDFToolkit.drawTextRight(contents, new PDRectangle(x + 355, y - 5, 100, 10), accrual_total_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-1, 5, 5);
				PDFToolkit.drawBox(contents,x,y - 2, 455, .2f, PdfColors.BLACK);
				y -= 12;

				m.getValue().stream().forEach(n -> {
					try {
						String entry_txt = PdfFormats.to_latin_number(n.getAmount().orElse(0.00)) + " en " + n.getDescription().orElse("");
						PDFToolkit.drawText(contents,entry_txt,x + 9,y, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize);
					} catch (IOException e) {e.printStackTrace();}
					y -= 10.5f;
				});

			} catch (IOException | UnknownCraException e) {e.printStackTrace();}
			y -= 5;

		});

		y -= 5;
		PDFToolkit.drawTextRight(contents, new PDRectangle(x + 355,y, 200, 25),accrual_total, PdfColors.BLACK,PdfFonts.HELVETICA, fontSize, 7, 5);
		PDFToolkit.drawTextRight(contents, new PDRectangle(x + 265,y, 200, 25),accrual_total_title, PdfColors.BLACK,PdfFonts.HELVETICA, fontSize, 5, 5);
	}

	// DRAW DEDUCTIONS
	private void draw_deductions() throws IOException {
		
		//DEDUCTIONS CONTENT
		String title =						words.getString("DEDUCCIONES").toUpperCase() + ".";
		String deduction_total_title = 		"B. " + words.getString("TOTAL DEDUCIR").toUpperCase() + ": ";
		String deduction_total = 			PdfFormats.to_latin_number(payroll.getDeduction_total().orElse(0.00));
		String payroll_total_title = 		words.getString("TOTAL PERCIBIR").toUpperCase() + " (A-B): ";
		String payroll_total = 				PdfFormats.to_latin_number(payroll.getPayroll_total().orElse(0.00));
		String enterprise_sign = 			words.getString("FIRMA EMPRESA").toUpperCase();
		String employee_sign = 				PdfFormats.formatDate(new Date(),words.getString("FIRMA TRABAJADOR")).get();
		
		x =  23.5f;
		
		//BUILD DEDUCTIONS
		Optional<Map<Integer, ArrayList<DefaultPayrollDeduction>>> deductions = payroll.getDeductions();		
		PDFToolkit.drawText(contents, title, x, y-20 ,PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize+3);
		y -= 40;
		
		//FOR EACH DEDUCTION
		deductions.get().entrySet().stream().sorted(Map.Entry.<Integer, ArrayList<DefaultPayrollDeduction>>comparingByKey()).forEach(m -> {	
			try {
				//CONTENT
				double local_total = m.getValue().stream().mapToDouble(accrual->accrual.getAmount().orElse(0.00)).sum();
				String deduction_txt = m.getKey() + ". " + DeductionTypes.getType(m.getKey());
				String deduction_total_txt = PdfFormats.to_latin_number(local_total);
					
				//BUILD 
				PDFToolkit.drawText(contents,deduction_txt,x,y,PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize);
				PDFToolkit.drawTextRight(contents,new PDRectangle(x + 355,y - 5, 100, 10),deduction_total_txt,PdfColors.BLACK,PdfFonts.HELVETICA,fontSize-2,2,5);
				PDFToolkit.drawBox(contents,x, y -2, 455, .2f, PdfColors.BLACK);
				
				y -= 12;
				m.getValue().stream().forEach(
						n ->{
							try {
								String entry_txt =  PdfFormats.to_latin_number(n.getAmount().orElse(0.00))+ " por " + n.getDescription().orElse("");
								String entry_percent = PdfFormats.to_latin_number(n.getPercent().orElse(0.00))+ " % " ;
								
								PDFToolkit.drawText(contents,entry_txt,x + 9,y,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize);
								PDFToolkit.drawText(contents,entry_percent,x + 345,y,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-1);
							} catch (IOException e) {e.printStackTrace();}
							y -= 10;
						}
				);			
				
			} 
			catch (IOException e) {e.printStackTrace();}
			y -= 5;
		});
		
		y -= 8;
		PDFToolkit.drawTextRight(contents,new PDRectangle(x + 355,y, 200, 25),deduction_total,PdfColors.BLACK,PdfFonts.HELVETICA, fontSize, 5, 5);
		PDFToolkit.drawTextRight(contents,new PDRectangle(x + 265,y, 200, 25),deduction_total_title,PdfColors.BLACK,PdfFonts.HELVETICA, fontSize, 5, 5);
		
		if(logo.isPresent()) {
			byte[] bytes =  logo.get().readAllBytes();
			BufferedImage img = PDFToolkit.create_image_from_bytes(bytes);
			float[] scales = PDFToolkit.reescale(img.getWidth(), img.getHeight(), 150, 50);
			PDFToolkit.drawImage(doc,contents, new ByteArrayInputStream(bytes),x+5,y - scales[1]/2 - 10, scales[0], scales[1]);
		}
		
		
		y -= 20;
		PDFToolkit.drawTextRight(contents,new PDRectangle(x + 355,y, 200, 25),payroll_total,PdfColors.BLACK,PdfFonts.HELVETICA, fontSize, 5, 5);
		PDFToolkit.drawTextRight(contents,new PDRectangle(x + 265,y, 200, 25),payroll_total_title,PdfColors.BLACK,PdfFonts.HELVETICA, fontSize, 5, 5);
		
		y -= 15;
		PDFToolkit.drawTextRight(contents,new PDRectangle(x + 355,y, 100, 25),enterprise_sign,PdfColors.BLACK,PdfFonts.HELVETICA, fontSize-2, 0, 5);
		PDFToolkit.drawTextRight(contents,new PDRectangle(x + 455,y, 100, 25),employee_sign,PdfColors.BLACK,PdfFonts.HELVETICA, fontSize-2, 5, 5);
	}

	// DRAW FOOTER
	public void draw_footer() throws IOException {

		Optional<Contingency_bases> contigencies = payroll.getContingencies();
		if (contigencies.isPresent()) {
			y = 155;
			x = 25;

			//FOOTER CONTENT
			String title = 						words.getString("TITULO PIE");
			String title_2 = 					words.getString("TITULO PIE 2");
			String ap_ent = 					words.getString("AP EMPRESA").toUpperCase();
			String type = 						words.getString("TIPO").toUpperCase();
			String base = 						words.getString("BASE").toUpperCase();
			String common_contingencies_title = "1. " + words.getString("CONTINGENCIAS COMUNES");
			String monthly_ammount_title = 		words.getString("IMPORTE DE REMUNERACION MENSUAL");
			String extra_h_prorration_title =   words.getString("IMPORTE PRORRATA DE PAGA EXTRAORDINARIA");
			String prof_contingencies_title =  	"2. " + words.getString("CONTINGENCIAS PROFESIONALES Y CONCEPTOS DE RECAUDACION CONJUNTA");
			String at_ep_title = 				words.getString("AT Y EP");
			String unemployment_title = 		words.getString("DESEMPLEO");
			String profes_form_title = 			words.getString("FORMACION PROFESIONAL");
			String fogasa_title = 				words.getString("FONDO DE GARANTIA SALARIAL");
			String extra_hours_title = 			"3. " + words.getString("COTIZACION ADICIONAL POR HORAS EXTRAS");
			String force_majeure_title = 		words.getString("FUERZA MAYOR O");
			String no_struct_title =			words.getString("NO ESTRUCTURALES");
			String irpf_title =					"4. " + words.getString("BASE SUJETA A RETENCION IRPF") + " ";
			String total_contingencies_title =	words.getString("TOTAL APORTACIONES");
			
			String monthly_ammount =  			PdfFormats.to_latin_number(contigencies.get().getMonthly_amount().orElse(0.00)) + "";
			String comm_cont_base = 			PdfFormats.to_latin_number(contigencies.get().getCommon_cont_base().orElse(0.00)) + "";
			String comm_cont_type = 			PdfFormats.to_latin_number(contigencies.get().getCommon_cont_type().orElse(0.00)) + "%";
			String comm_cont_ap_ent = 			PdfFormats.to_latin_number(contigencies.get().getCommon_cont_ap_enterprise().orElse(0.00)) + "";
			String extra_prorration_amount =    PdfFormats.to_latin_number(contigencies.get().getExtra_proration_amount().orElse(0.00)) + "";
			String prof_contingencies_base = 	PdfFormats.to_latin_number(contigencies.get().getProfessional_cont_base().orElse(0.00)) + "";
			String at_ep_type = 				PdfFormats.to_latin_number(contigencies.get().getAt_ep_type().orElse(0.00)) + "%";
			String at_ep_ap_ent =				PdfFormats.to_latin_number(contigencies.get().getAt_ep_ap_enterprise().orElse(0.00)) + "";
			String unemployment_type = 			PdfFormats.to_latin_number(contigencies.get().getUnemployment_type().orElse(0.00)) + "%";
			String unemployment_ap_ent = 		PdfFormats.to_latin_number(contigencies.get().getUnemployment_ap_enterprise().orElse(0.00)) + "";
			String profes_form_type = 			PdfFormats.to_latin_number(contigencies.get().getProfes_form_type().orElse(0.00)) + "%";
			String profes_form_ap_ent = 		PdfFormats.to_latin_number(contigencies.get().getProfes_form_ap_enterprise().orElse(0.00)) + "";
			String fogasa_type = 				PdfFormats.to_latin_number(contigencies.get().getFogasa_type().orElse(0.00)) + "%";
			String fogasa_ap_ent = 				PdfFormats.to_latin_number(contigencies.get().getFogasa_ap_enterprise().orElse(0.00)) + "";
			String force_majeure_base = 		PdfFormats.to_latin_number(contigencies.get().getForce_majeure_base().orElse(0.00)) + "";
			String force_majeure_type = 		PdfFormats.to_latin_number(contigencies.get().getForce_majeure_type().orElse(0.00)) + "%";
			String force_majeure_ap_ent = 		PdfFormats.to_latin_number(contigencies.get().getForce_majeure_ap_enterprise().orElse(0.00)) + "";
			String no_struct_base = 			PdfFormats.to_latin_number(contigencies.get().getNo_struct_base().orElse(0.00)) + "";
			String no_struct_type = 			PdfFormats.to_latin_number(contigencies.get().getNo_struct_type().orElse(0.00)) + "%";
			String no_struct_ap_ent = 			PdfFormats.to_latin_number(contigencies.get().getNo_struct_ap_enterprise().orElse(0.00)) + "";
			String total_irpf = 				PdfFormats.to_latin_number(contigencies.get().getIrpf_esp().orElse(0.00) + contigencies.get().getIrpf_retrib_diner().orElse(0.00)) + "\u20ac";
			String total_contingencies_amount =	PdfFormats.to_latin_number(contigencies.get().getTotal().orElse(0.00));
			
			//BUILD FOOTER
			
			Double irpf_esp = contigencies.get().getIrpf_esp().orElse(0.00) ;
			Double irpf_ret_din = contigencies.get().getIrpf_retrib_diner().orElse(0.00);
					
			if(irpf_esp != 0) irpf_title += 		PdfFormats.to_latin_number(irpf_esp) + "\u20ac " + words.getString("EN ESPECIE") + " ";
			if(irpf_ret_din != 0) irpf_title += 	PdfFormats.to_latin_number(irpf_ret_din) + "\u20ac " + words.getString("EN RETRIBUCIONES DINERARIAS");
			
			PDFToolkit.drawBorderedBox(contents, 10, 10, 575, 160, PdfColors.LIGHT_GRAY);
			PDFToolkit.drawText(contents,title,x,y,PdfColors.BLACK,PdfFonts.HELVETICA_BOLD,fontSize-1);

			y -= 10;
			PDFToolkit.drawText(contents,title_2,x,y,PdfColors.BLACK,PdfFonts.HELVETICA_BOLD,fontSize-1);

			y -= 18;
			PDFToolkit.drawBox(contents,x + 480,y,70,20,PdfColors.LIGHT_GRAY);
			PDFToolkit.drawTextRight(contents,new PDRectangle(x + 480,y, 70,20),ap_ent,PdfColors.BLACK,PdfFonts.HELVETICA,fontSize-1,5,7);

			PDFToolkit.drawBox(contents,x + 408,y,70,20,PdfColors.LIGHT_GRAY);
			PDFToolkit.drawTextRight(contents,new PDRectangle(x + 408,y, 70,20),type,PdfColors.BLACK,PdfFonts.HELVETICA,fontSize-1,5,7);

			PDFToolkit.drawBox(contents,x + 336,y,70,20,PdfColors.LIGHT_GRAY);
			PDFToolkit.drawTextRight(contents,new PDRectangle(x + 336,y, 70,20),base,PdfColors.BLACK,PdfFonts.HELVETICA,fontSize-1,5,7);
			
			PDFToolkit.drawText(contents,common_contingencies_title,x,y,PdfColors.BLACK,PdfFonts.HELVETICA_BOLD, fontSize-1);

			y -= 9;
			x += 15;
			PDFToolkit.drawText(contents,monthly_ammount_title,x,y,PdfColors.BLACK, PdfFonts.HELVETICA,fontSize-3);
			PDFToolkit.drawTextRight(contents,new PDRectangle(x + 215,y, 70, 70),monthly_ammount,PdfColors.BLACK,PdfFonts.HELVETICA,fontSize-3,5,1);
			
			PDFToolkit.drawTextRight(contents,new PDRectangle(x + 322,y - 5, 70, 70),comm_cont_base,PdfColors.BLACK,PdfFonts.HELVETICA,fontSize-3,5,1);
			PDFToolkit.drawTextRight(contents,new PDRectangle(x + 396,y - 5, 70, 70),comm_cont_type,PdfColors.BLACK,PdfFonts.HELVETICA,fontSize-3,5,1);
			PDFToolkit.drawTextRight(contents,new PDRectangle(x + 465,y - 5, 70, 70),comm_cont_ap_ent,PdfColors.BLACK,PdfFonts.HELVETICA,fontSize-3,5,1);

			y -= 8;
			PDFToolkit.drawText(contents,extra_h_prorration_title,x,y,PdfColors.BLACK,PdfFonts.HELVETICA,fontSize-3);
			PDFToolkit.drawTextRight(contents, new PDRectangle(x + 215, y, 70, 70),extra_prorration_amount,PdfColors.BLACK,PdfFonts.HELVETICA,fontSize-3,5,1);

			y -= 12;
			PDFToolkit.drawText(contents,prof_contingencies_title,x-15,y,PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, 7);

			y -= 10;
			PDFToolkit.drawText(contents,at_ep_title,x,y, PdfColors.BLACK,PdfFonts.HELVETICA, fontSize-3);
			PDFToolkit.drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70),at_ep_type, PdfColors.BLACK,PdfFonts.HELVETICA, fontSize-3, 5, 1);
			PDFToolkit.drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70),at_ep_ap_ent,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-3, 5, 1);

			y -= 8;
			PDFToolkit.drawText(contents,unemployment_title,x,y,PdfColors.BLACK,PdfFonts.HELVETICA, fontSize-3);
			PDFToolkit.drawTextRight(contents,new PDRectangle(x + 396,y - 5, 70, 70),unemployment_type,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-3, 5, 1);
			PDFToolkit.drawTextRight(contents,new PDRectangle(x + 465,y - 5, 70, 70),unemployment_ap_ent,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-3, 5, 1);

			y -= 8;
			PDFToolkit.drawTextRight(contents, new PDRectangle(x + 322,y, 70, 70),prof_contingencies_base,PdfColors.BLACK, PdfFonts.HELVETICA,fontSize-3, 5, 1);
			PDFToolkit.drawText(contents,profes_form_title,x, y, PdfColors.BLACK,PdfFonts.HELVETICA, fontSize-3);
			PDFToolkit.drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70),profes_form_type,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-3, 5, 1);
			PDFToolkit.drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70),profes_form_ap_ent,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-3, 5, 1);

			y -= 8;
			PDFToolkit.drawText(contents,fogasa_title, x, y,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-3);
			PDFToolkit.drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70),fogasa_type,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-3, 5, 1);
			PDFToolkit.drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70),fogasa_ap_ent,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-3, 5, 1);

			y -= 12;
			PDFToolkit.drawText(contents,extra_hours_title, x-15,y, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize-2);

			y -= 10;
			PDFToolkit.drawText(contents, force_majeure_title, x, y, PdfColors.BLACK,PdfFonts.HELVETICA, fontSize-3);
			PDFToolkit.drawTextRight(contents, new PDRectangle(x + 322, y - 2, 70, 70),force_majeure_base,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-3, 5, 1);
			PDFToolkit.drawTextRight(contents, new PDRectangle(x + 396, y - 2, 70, 70),force_majeure_type,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-3, 5, 1);
			PDFToolkit.drawTextRight(contents, new PDRectangle(x + 465, y - 2, 70, 70),force_majeure_ap_ent,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-3, 5, 1);

			y -= 8;
			PDFToolkit.drawText(contents,no_struct_title, x, y, PdfColors.BLACK,PdfFonts.HELVETICA, fontSize-3);
			PDFToolkit.drawTextRight(contents, new PDRectangle(x + 322, y - 2, 70, 70),no_struct_base,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-3, 5, 1);
			PDFToolkit.drawTextRight(contents, new PDRectangle(x + 396, y - 2, 70, 70),no_struct_type,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-3, 5, 1);
			PDFToolkit.drawTextRight(contents, new PDRectangle(x + 465, y - 2, 70, 70),no_struct_ap_ent,PdfColors.BLACK, PdfFonts.HELVETICA, fontSize-3, 5, 1);

			y -= 12;
			x -= 15;
			PDFToolkit.drawText(contents,irpf_title,x,y,PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize-3);
			PDFToolkit.drawBox(contents,x,y -2,405,.2f,PdfColors.BLACK);
			PDFToolkit.drawTextRight(contents,new PDRectangle(x + 308, y - 5, 100, 10),total_irpf,PdfColors.BLACK,PdfFonts.HELVETICA,fontSize-3,5,5);
			PDFToolkit.drawTextRight(contents,new PDRectangle(x + 451, y - 5, 30, 10),total_contingencies_title,PdfColors.BLACK,PdfFonts.HELVETICA_BOLD,6.5f,5,5);
			PDFToolkit.drawTextRight(contents,new PDRectangle(x + 518, y - 5, 30, 10),total_contingencies_amount,PdfColors.BLACK,PdfFonts.HELVETICA_BOLD,6.5f,3,5);
			

		}
	}

}
