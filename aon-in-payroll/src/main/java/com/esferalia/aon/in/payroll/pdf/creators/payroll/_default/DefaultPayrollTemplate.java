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

import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.payroll.CraTypes;
import com.esferalia.aon.in.payroll.pdf.creators.payroll.DeductionTypes;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.Contingency_bases;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayroll;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayrollAccrual;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayrollDeduction;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.UnknownCraException;
import com.esferalia.aon.in.payroll.pdf.toolkits.PDFToolkit;

public class DefaultPayrollTemplate {

	String filename = "./payroll.pdf";
	float limit;
	PDPageContentStream contents;

	PDDocument doc;
	DefaultPayroll payroll;
	Optional<InputStream> logo;

	float x;
	float y;
	ResourceBundle words;

	// PRINT THE PDF
	public static void print(String out, DefaultPayroll payroll, Optional<InputStream> logo)
			throws CanNotCreatePdfException {
		try (PDDocument doc = new PDDocument()) {

			DefaultPayrollTemplate template = new DefaultPayrollTemplate();
			template.words = ResourceBundle.getBundle("com.esferalia.aon.in.payroll.pdf.creators.payroll._default.bundles.PayrollBundle",new Locale("Es"));

			template.filename = out;
			template.limit = 800;
			template.payroll = payroll;

			if (template.payroll == null)
				throw new CanNotCreatePdfException("No payroll found.");

			PDPage page = PDFToolkit.createVerticalPage();
			doc.addPage(page);

			template.contents = new PDPageContentStream(doc, page);

			draw_header(template);
			boolean jump = calculate(template);

			template.doc = doc;
			template.logo = logo;

			if (jump) {
				draw_accruals(template);
				PDFToolkit.drawBorderedBox(template.contents, 22, 20, 550, 678, PDFToolkit.LIGHT_GRAY);
				template.contents.close();

				page = PDFToolkit.createVerticalPage();
				doc.addPage(page);
				template.contents = new PDPageContentStream(doc, page);

				draw_header(template);
				PDFToolkit.drawBorderedBox(template.contents, 22, 220, 550, 478, PDFToolkit.LIGHT_GRAY);
				template.y -= 15;

				draw_deductions(template);
				draw_footer(template);

			} else {
				PDFToolkit.drawBorderedBox(template.contents, 22, 220, 550, 480, PDFToolkit.LIGHT_GRAY);
				draw_accruals(template);
				draw_deductions(template);
				draw_footer(template);
			}

			template.contents.close();
			doc.save(new File(template.filename));

		} catch (Exception e) {throw new CanNotCreatePdfException(e);}
	}

	// DRAW THE HEADER
	public static void draw_header(DefaultPayrollTemplate template) throws IOException {

		template.x = 32;
		template.y = 780;
		String date_format = template.words.getString("FORMATO FECHA");
		
		//HEADER CONTENT
		String title = template.words.getString("TITULO").toUpperCase();
		String enterprise = template.words.getString("EMPRESA") + ": " + template.payroll.getEnterprise().orElse("");
		String employee = template.words.getString("TRABAJADOR") + ": " + template.payroll.getEmployee().orElse("");
		String address = template.words.getString("DOMICILIO") + ": " + template.payroll.getAddress().orElse("");
		String nif = template.words.getString("NIF") + ": " + template.payroll.getNif().orElse("");
		String nss = template.words.getString("NSS") + ": " + template.payroll.getNss().orElse("");
		String profes_group = template.words.getString("G.PROFESIONAL") + ": " + template.payroll.getProfessional_group().orElse("");
		String ccc = template.words.getString("CCC") + ": " + template.payroll.getCcc().orElse("");
		String cif = template.words.getString("CIF") + ": " + template.payroll.getCif().orElse("");
		String cotiz_group = template.words.getString("G.COTIZ") + ": " + template.payroll.getQuotation_group().orElse("");
		String antiqu_date = template.words.getString("FECHA ANTIGUEDAD") + ": " + PDFToolkit.formatDate(template.payroll.getAntiquity().get(),date_format).orElse("");
		String day_total = template.words.getString("TOTAL DIAS") + ": " + template.payroll.getTotal_days().orElse(0);
		String liquid_period = template.words.getString("PERIODO LIQUIDACION") + ": "
				+ PDFToolkit.formatDate(template.payroll.getLiquid_period_start().orElse(null), date_format).get() + " - "
				+ PDFToolkit.formatDate(template.payroll.getLiquid_period_end().orElse(null), date_format).get();
		
		
		//BUILDING THE HEADER
		PDFToolkit.drawBorderedBox(template.contents,template.x - 10,template.y - 75, 550, 110,PDFToolkit.LIGHT_GRAY);
		PDFToolkit.drawTextCenter(template.contents,new PDRectangle(template.x, template.y, 530, 100),title, PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 12, 12);

		template.y -= 55;
		PDFToolkit.drawBox(template.contents, template.x, template.y, 260, 55, PDFToolkit.LIGHT_GRAY);
		PDFToolkit.drawBox(template.contents, template.x + 270, template.y, 260, 55, PDFToolkit.LIGHT_GRAY);

		template.y += 42.5;
		template.x += 10;
		PDFToolkit.drawText(template.contents,enterprise, template.x,template.y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		PDFToolkit.drawText(template.contents,employee,template.x + 270, template.y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);

		template.y -= 12;
		PDFToolkit.drawText(template.contents, address, template.x,template.y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		PDFToolkit.drawText(template.contents,nif, template.x + 270,template.y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		
		PDFToolkit.drawText(template.contents,nss,template.x + 370, template.y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);

		template.y -= 12;
		PDFToolkit.drawText(template.contents, template.payroll.getAddress_2().orElse(""), template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		PDFToolkit.drawText(template.contents, profes_group,template.x + 270, template.y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);

		template.y -= 12;
		PDFToolkit.drawText(template.contents,cif,template.x,template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		PDFToolkit.drawText(template.contents,cotiz_group,template.x + 270, template.y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
	

		template.x += 100;
		PDFToolkit.drawText(template.contents,ccc,template.x,template.y,PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7);
		PDFToolkit.drawText(template.contents,antiqu_date,template.x+270, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		

		template.y -= 18;
		template.x -= 100;
		PDFToolkit.drawText(template.contents, liquid_period, template.x, template.y, PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7);
		PDFToolkit.drawText(template.contents,day_total,template.x + 270, template.y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
	}

	// CHECK IF JUMPS
	public static boolean calculate(DefaultPayrollTemplate template) throws IOException {

		double total = PDFToolkit.createVerticalPage().getMediaBox().getHeight();

		Optional<Map<Integer, ArrayList<DefaultPayrollAccrual>>> accruals = template.payroll.getAccruals();
		Optional<Map<Integer, ArrayList<DefaultPayrollDeduction>>> deductions = template.payroll.getDeductions();

		double sum = 0;

		sum += 25;
		sum += accruals.get().entrySet().size() * 12;
		sum += accruals.get().entrySet().stream().mapToDouble(entry -> entry.getValue().stream().count()).sum() * 12;

		sum += 25;
		sum += deductions.get().entrySet().size() * 12;
		sum += deductions.get().entrySet().stream().mapToDouble(entry -> entry.getValue().stream().count()).sum() * 12;

		return sum > 370;
	}

	// DRAW ACCRUALS
	public static void draw_accruals(DefaultPayrollTemplate template) throws IOException {

		template.x = 40;
		template.y = 675;

		//ACCRUAL CONTENT
		String title = template.words.getString("DEVENGOS").toUpperCase() + ".";
		String totals = template.words.getString("TOTALES").toUpperCase();
		String accrual_total_title = "A." + template.words.getString("TOTAL DEVENGADO").toUpperCase() + ":";
		String accrual_total = PDFToolkit.to_latin_number(template.payroll.getAccrual_total().orElse(0.00));
		
		//BUILD THE ACCRUALS
		PDFToolkit.drawText(template.contents, title, template.x,template.y, PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 10);
		PDFToolkit.drawBox(template.contents, template.x + 420, template.y - 7, 100, 20, PDFToolkit.LIGHT_GRAY);
		PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 420, template.y - 7 ,100,20), totals, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 8, 7, 7);
		template.y -= 20;

		Optional<Map<Integer, ArrayList<DefaultPayrollAccrual>>> accruals = template.payroll.getAccruals();

		// FOR EACH ACCRUAL
		accruals.get().entrySet().stream().forEach(m -> {
			try {
				//CONTENT
				double local_total = m.getValue().stream().mapToDouble(accrual -> accrual.getAmount().orElse(0.00)).sum();
				String accrual_txt = m.getKey() + ". " + CraTypes.get_type(m.getKey());
				String accrual_total_txt = PDFToolkit.to_latin_number(local_total);
				
				//BUILD 
				PDFToolkit.drawText(template.contents, accrual_txt, template.x,template.y, PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 6);
				PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 300, template.y - 5, 100, 10), accrual_total_txt, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 6, 5, 5);
				PDFToolkit.drawBox(template.contents, template.x, template.y - 2, 400, .2f, PDFToolkit.BLACK);
				template.y -= 10;

				m.getValue().stream().forEach(n -> {
					try {
						String entry_txt = PDFToolkit.to_latin_number(n.getAmount().orElse(0.00)) + " en " + n.getDescription().orElse("");
						PDFToolkit.drawText(template.contents,entry_txt,template.x + 9, template.y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 6);
					} catch (IOException e) {e.printStackTrace();}
					template.y -= 10;
				});

			} catch (IOException | UnknownCraException e) {e.printStackTrace();}
			template.y -= 5;

		});

		template.y -= 5;
		PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 315, template.y, 200, 25),accrual_total, PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7, 5, 5);
		PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 225, template.y, 200, 25),accrual_total_title, PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7, 5, 5);
	}

	// DRAW DEDUCTIONS
	public static void draw_deductions(DefaultPayrollTemplate template) throws IOException {
		
		//DEDUCTIONS CONTENT
		String title = template.words.getString("DEDUCCIONES").toUpperCase() + ".";
		String deduction_total_title = "B. " + template.words.getString("TOTAL DEDUCIR").toUpperCase() + ": ";
		String deduction_total = PDFToolkit.to_latin_number(template.payroll.getDeduction_total().orElse(0.00));
		String payroll_total_title = template.words.getString("TOTAL PERCIBIR").toUpperCase() + " (A-B): ";
		String payroll_total = PDFToolkit.to_latin_number(template.payroll.getPayroll_total().orElse(0.00));
		String enterprise_sign = template.words.getString("FIRMA EMPRESA").toUpperCase();
		String employee_sign = PDFToolkit.formatDate(new Date(),template.words.getString("FIRMA TRABAJADOR")).get();
		
		//BUILD DEDUCTIONS
		Optional<Map<Integer, ArrayList<DefaultPayrollDeduction>>> deductions = template.payroll.getDeductions();		
		PDFToolkit.drawText(template.contents, title, template.x, template.y-20 ,PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 10);
		template.y -= 40;
		
		//FOR EACH DEDUCTION
		deductions.get().entrySet().stream().forEach(m -> {	
			try {
				
				//CONTENT
				double local_total = m.getValue().stream().mapToDouble(accrual->accrual.getAmount().orElse(0.00)).sum();
				String deduction_txt = m.getKey() + ". " + DeductionTypes.getType(m.getKey());
				String deduction_total_txt = PDFToolkit.to_latin_number(local_total);
					
				//BUILD 
				PDFToolkit.drawText(template.contents,deduction_txt, template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 6);
				PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 300, template.y - 5, 100, 10),deduction_total_txt,PDFToolkit.BLACK,PDFToolkit.HELVETICA,6,5,5);
				PDFToolkit.drawBox(template.contents,template.x, template.y -2, 400, .2f, PDFToolkit.BLACK);
				
				template.y -= 10;
				m.getValue().stream().forEach(
						n ->{
							try {
								String entry_txt =  PDFToolkit.to_latin_number(n.getAmount().orElse(0.00))+ " por " + n.getDescription().orElse("");
								String entry_percent = PDFToolkit.to_latin_number(n.getPercent().orElse(0.00))+ " % " ;
								
								PDFToolkit.drawText(template.contents,entry_txt, template.x + 9, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 6);
								PDFToolkit.drawText(template.contents, entry_percent, template.x + 265, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 6);
							} catch (IOException e) {e.printStackTrace();}
							template.y -= 10;
						}
				);			
				
			} 
			catch (IOException e) {e.printStackTrace();}
			template.y -= 5;
		});
		
		template.y -= 5;
		PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 315, template.y, 200, 25),deduction_total,PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7, 5, 5);
		PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 225,template.y, 200, 25),deduction_total_title,PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7, 5, 5);
		
		if(template.logo.isPresent()) {
			byte[] bytes =  template.logo.get().readAllBytes();
			BufferedImage img = PDFToolkit.create_image_from_bytes(bytes);
			float[] scales = PDFToolkit.reescale(img.getWidth(), img.getHeight(), 150, 50);
			PDFToolkit.drawImage(template.doc, template.contents, new ByteArrayInputStream(bytes), template.x+10, template.y - 30, scales[0], scales[1]);
		}
		
		
		template.y -= 30;
		PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 315, template.y, 200, 25),payroll_total,PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7, 5, 5);
		PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 225,template.y, 200, 25),payroll_total_title,PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7, 5, 5);
		
		template.y -= 15;
		PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 315,template.y, 100, 25),enterprise_sign,PDFToolkit.BLACK,PDFToolkit.HELVETICA, 6, 5, 5);
		PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 415,template.y, 100, 25),employee_sign,PDFToolkit.BLACK,PDFToolkit.HELVETICA, 6, 5, 5);
	}

	// DRAW FOOTER
	public static void draw_footer(DefaultPayrollTemplate template) throws IOException {

		Optional<Contingency_bases> contigencies = template.payroll.getContingencies();
		if (contigencies.isPresent()) {
			template.y = 195;
			template.x = 37;

			//FOOTER CONTENT
			String title = 						template.words.getString("TITULO PIE");
			String title_2 = 					template.words.getString("TITULO PIE 2");
			String ap_ent = 					template.words.getString("AP EMPRESA").toUpperCase();
			String type = 						template.words.getString("TIPO").toUpperCase();
			String base = 						template.words.getString("BASE").toUpperCase();
			String common_contingencies_title = "1. " + template.words.getString("CONTINGENCIAS COMUNES");
			String monthly_ammount_title = 		template.words.getString("IMPORTE DE REMUNERACION MENSUAL");
			String extra_h_prorration_title =   template.words.getString("IMPORTE PRORRATA DE PAGA EXTRAORDINARIA");
			String prof_contingencies_title =  	"2. " + template.words.getString("CONTINGENCIAS PROFESIONALES Y CONCEPTOS DE RECAUDACION CONJUNTA");
			String at_ep_title = 				template.words.getString("AT Y EP");
			String unemployment_title = 		template.words.getString("DESEMPLEO");
			String profes_form_title = 			template.words.getString("FORMACION PROFESIONAL");
			String fogasa_title = 				template.words.getString("FONDO DE GARANTIA SALARIAL");
			String extra_hours_title = 			"3. " + template.words.getString("COTIZACION ADICIONAL POR HORAS EXTRAS");
			String force_majeure_title = 		template.words.getString("FUERZA MAYOR O");
			String no_struct_title =			template.words.getString("NO ESTRUCTURALES");
			String irpf_title =					"4. " + template.words.getString("BASE SUJETA A RETENCION IRPF") + " ";
			String total_contingencies_title =	template.words.getString("TOTAL APORTACIONES");
			
			String monthly_ammount =  			PDFToolkit.to_latin_number(contigencies.get().getMonthly_amount().orElse(0.00)) + "";
			String comm_cont_base = 			PDFToolkit.to_latin_number(contigencies.get().getCommon_cont_base().orElse(0.00)) + "";
			String comm_cont_type = 			PDFToolkit.to_latin_number(contigencies.get().getCommon_cont_type().orElse(0.00)) + "%";
			String comm_cont_ap_ent = 			PDFToolkit.to_latin_number(contigencies.get().getCommon_cont_ap_enterprise().orElse(0.00)) + "";
			String extra_prorration_amount =    PDFToolkit.to_latin_number(contigencies.get().getExtra_proration_amount().orElse(0.00)) + "";
			String prof_contingencies_base = 	PDFToolkit.to_latin_number(contigencies.get().getProfessional_cont_base().orElse(0.00)) + "";
			String at_ep_type = 				PDFToolkit.to_latin_number(contigencies.get().getAt_ep_type().orElse(0.00)) + "%";
			String at_ep_ap_ent =				PDFToolkit.to_latin_number(contigencies.get().getAt_ep_ap_enterprise().orElse(0.00)) + "";
			String unemployment_type = 			PDFToolkit.to_latin_number(contigencies.get().getUnemployment_type().orElse(0.00)) + "%";
			String unemployment_ap_ent = 		PDFToolkit.to_latin_number(contigencies.get().getUnemployment_ap_enterprise().orElse(0.00)) + "";
			String profes_form_type = 			PDFToolkit.to_latin_number(contigencies.get().getProfes_form_type().orElse(0.00)) + "%";
			String profes_form_ap_ent = 		PDFToolkit.to_latin_number(contigencies.get().getProfes_form_ap_enterprise().orElse(0.00)) + "";
			String fogasa_type = 				PDFToolkit.to_latin_number(contigencies.get().getFogasa_type().orElse(0.00)) + "%";
			String fogasa_ap_ent = 				PDFToolkit.to_latin_number(contigencies.get().getFogasa_ap_enterprise().orElse(0.00)) + "";
			String force_majeure_base = 		PDFToolkit.to_latin_number(contigencies.get().getForce_majeure_base().orElse(0.00)) + "";
			String force_majeure_type = 		PDFToolkit.to_latin_number(contigencies.get().getForce_majeure_type().orElse(0.00)) + "%";
			String force_majeure_ap_ent = 		PDFToolkit.to_latin_number(contigencies.get().getForce_majeure_ap_enterprise().orElse(0.00)) + "";
			String no_struct_base = 			PDFToolkit.to_latin_number(contigencies.get().getNo_struct_base().orElse(0.00)) + "";
			String no_struct_type = 			PDFToolkit.to_latin_number(contigencies.get().getNo_struct_type().orElse(0.00)) + "%";
			String no_struct_ap_ent = 			PDFToolkit.to_latin_number(contigencies.get().getNo_struct_ap_enterprise().orElse(0.00)) + "";
			String total_irpf = 				PDFToolkit.to_latin_number(contigencies.get().getIrpf_esp().orElse(0.00) + contigencies.get().getIrpf_retrib_diner().orElse(0.00)) + "\u20ac";
			String total_contingencies_amount =	PDFToolkit.to_latin_number(contigencies.get().getTotal().orElse(0.00));
			
			//BUILD FOOTER
			
			Double irpf_esp = contigencies.get().getIrpf_esp().orElse(0.00) ;
			Double irpf_ret_din = contigencies.get().getIrpf_retrib_diner().orElse(0.00);
					
			if(irpf_esp != 0) irpf_title += PDFToolkit.to_latin_number(irpf_esp) + "\u20ac " + template.words.getString("EN ESPECIE") + " ";
			if(irpf_ret_din != 0) irpf_title += PDFToolkit.to_latin_number(irpf_ret_din) + "\u20ac " + template.words.getString("EN RETRIBUCIONES DINERARIAS");
			
			PDFToolkit.drawBorderedBox(template.contents, 22, 18, 550, 195, PDFToolkit.LIGHT_GRAY);
			PDFToolkit.drawText(template.contents,title,template.x,template.y,PDFToolkit.BLACK,PDFToolkit.HELVETICA_BOLD,8);

			template.y -= 10;
			PDFToolkit.drawText(template.contents,title_2,template.x,template.y,PDFToolkit.BLACK,PDFToolkit.HELVETICA_BOLD,8);

			template.y -= 27;
			PDFToolkit.drawBox(template.contents,template.x + 450,template.y,70,20,PDFToolkit.LIGHT_GRAY);
			PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 450,template.y, 70,20),ap_ent,PDFToolkit.BLACK,PDFToolkit.HELVETICA,7,5,7);

			PDFToolkit.drawBox(template.contents,template.x + 378,template.y,70,20,PDFToolkit.LIGHT_GRAY);
			PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 378,template.y, 70,20),type,PDFToolkit.BLACK,PDFToolkit.HELVETICA,7,5,7);

			PDFToolkit.drawBox(template.contents,template.x + 306,template.y,70,20,PDFToolkit.LIGHT_GRAY);
			PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 306,template.y, 70,20),base,PDFToolkit.BLACK,PDFToolkit.HELVETICA,7,5,7);
			
			PDFToolkit.drawText(template.contents,common_contingencies_title,template.x,template.y,PDFToolkit.BLACK,PDFToolkit.HELVETICA_BOLD, 7);

			template.y -= 12;
			template.x += 15;
			PDFToolkit.drawText(template.contents,monthly_ammount_title, template.x,template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA,7);
			PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 215,template.y, 70, 70),monthly_ammount,PDFToolkit.BLACK,PDFToolkit.HELVETICA,7,5,1);
			
			PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 290,template.y - 5, 70, 70),comm_cont_base,PDFToolkit.BLACK,PDFToolkit.HELVETICA,7,5,1);
			PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 362,template.y - 5, 70, 70),comm_cont_type,PDFToolkit.BLACK,PDFToolkit.HELVETICA,7,5,1);
			PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 434,template.y - 5, 70, 70),comm_cont_ap_ent,PDFToolkit.BLACK,PDFToolkit.HELVETICA,7,5,1);

			template.y -= 10;
			PDFToolkit.drawText(template.contents,extra_h_prorration_title,template.x, template.y,PDFToolkit.BLACK,PDFToolkit.HELVETICA,7);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 215, template.y, 70, 70),extra_prorration_amount,PDFToolkit.BLACK,PDFToolkit.HELVETICA,7,5,1);

			template.y -= 12;
			PDFToolkit.drawText(template.contents,prof_contingencies_title,template.x-15,template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 7);

			template.y -= 12;
			PDFToolkit.drawText(template.contents,at_ep_title,template.x,template.y, PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 362, template.y - 5, 70, 70),at_ep_type, PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7, 5, 1);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 434, template.y - 5, 70, 70),at_ep_ap_ent,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);

			template.y -= 10;
			PDFToolkit.drawText(template.contents,unemployment_title, template.x, template.y, PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 362, template.y - 5, 70, 70),unemployment_type,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 434, template.y - 5, 70, 70),unemployment_ap_ent,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);

			template.y -= 10;
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 290, template.y, 70, 70),prof_contingencies_base,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);
			PDFToolkit.drawText(template.contents,profes_form_title, template.x, template.y, PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 362, template.y - 5, 70, 70),profes_form_type,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 434, template.y - 5, 70, 70),profes_form_ap_ent,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);

			template.y -= 10;
			PDFToolkit.drawText(template.contents,fogasa_title, template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 362, template.y - 5, 70, 70),fogasa_type,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 434, template.y - 5, 70, 70),fogasa_ap_ent,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);

			template.y -= 12;
			PDFToolkit.drawText(template.contents,extra_hours_title, template.x-15,template.y, PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 7);

			template.y -= 12;
			PDFToolkit.drawText(template.contents, force_majeure_title, template.x, template.y, PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 290, template.y - 5, 70, 70),force_majeure_base,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 362, template.y - 5, 70, 70),force_majeure_type,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 434, template.y - 5, 70, 70),force_majeure_ap_ent,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);

			template.y -= 10;
			PDFToolkit.drawText(template.contents,no_struct_title, template.x, template.y, PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 290, template.y - 5, 70, 70),no_struct_base,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 362, template.y - 5, 70, 70),no_struct_type,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle(template.x + 434, template.y - 5, 70, 70),no_struct_ap_ent,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);

			template.y -= 20;
			template.x -= 15;
			PDFToolkit.drawText(template.contents,irpf_title,template.x,template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 6.5f);
			PDFToolkit.drawBox(template.contents,template.x, template.y -2, 372, .2f, PDFToolkit.BLACK);
			PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 275, template.y - 5, 100, 10),total_irpf,PDFToolkit.BLACK,PDFToolkit.HELVETICA,6,5,5);
			PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 417, template.y - 5, 30, 10),total_contingencies_title,PDFToolkit.BLACK,PDFToolkit.HELVETICA_BOLD,6.5f,5,5);
			PDFToolkit.drawTextRight(template.contents,new PDRectangle(template.x + 487, template.y - 5, 30, 10),total_contingencies_amount,PDFToolkit.BLACK,PDFToolkit.HELVETICA_BOLD,6.5f,3,5);
			

		}
	}
}
