package com.esferalia.aon.in.payroll.pdf.creators.payroll._default;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLUE;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.LIGHT_GRAY;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats.formatDate;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats.to_latin_number;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT.CENTER;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT.LEFT;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT.RIGHT;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.OptionalToolkit.getDouble;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.OptionalToolkit.getInteger;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.OptionalToolkit.getString;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.createVerticalPage;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.create_image_from_bytes;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.cropped_string;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawBorderedBox;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawBox;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawImage;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawText;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawTextCenter;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawTextRight;
import static com.esferalia.aon.in.payroll.pdf.creators.payroll.commons.CraTypes.get_type;
import static com.esferalia.aon.in.payroll.pdf.creators.payroll.commons.DeductionTypes.getType;
import static java.util.ResourceBundle.getBundle;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.poi.hssf.util.HSSFColor.BLUE;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfBox;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfImage;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfText;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.Contingency_bases;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayroll;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayrollAccrual;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayrollDeduction;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.UnknownCraException;
import com.esferalia.aon.in.payroll.pdf.creators.payroll.commons.PayrollTypes;

public class DefaultPayrollTemplate {

	static float fontSize = 9f;
	
	String filename = "./payroll.pdf";
	float limit;
	PDPageContentStream contents;

	PDDocument doc;
	DefaultPayroll p;
	Optional<InputStream> logo;
	Locale lang;
	
	float x;
	float y;
	ResourceBundle words;
	
	

	// PRINT THE PDF
	public void print(OutputStream os,DefaultPayroll payroll,Optional<InputStream> logo,Optional<Locale> language) throws CanNotCreatePdfException {
		try (PDDocument doc = print(payroll,logo,language)) {
			doc.save(os);
		} catch (Exception e) {throw new CanNotCreatePdfException(e);}
	}
	
	// PRINT THE PDF FROM COLLECTION
	public void print(OutputStream os,Collection<DefaultPayroll> payrolls,Optional<InputStream> logo,Optional<Locale> language) throws CanNotCreatePdfException,IOException {
		try (PDDocument doc = print(payrolls,logo,language)) {
			doc.save(os);
		} catch (Exception e) {throw new CanNotCreatePdfException(e);}
	}

	// PRINT THE PDF FROM COLLECTION
	private PDDocument print(Collection<DefaultPayroll> payrolls,Optional<InputStream> logo,Optional<Locale> language) throws CanNotCreatePdfException,IOException {
			PDDocument doc = new PDDocument();

			lang = language.orElse(new Locale("Es"));
			words = getBundle("com.esferalia.aon.in.payroll.pdf.creators.payroll._default.bundles.PayrollBundle",lang);
			limit = 800;
			
			byte[] bLogo = null;
			if (logo.isPresent()) bLogo = logo.get().readAllBytes();
			
			for (DefaultPayroll payroll : payrolls) {
				if (bLogo != null) logo = Optional.ofNullable(new ByteArrayInputStream(bLogo));
				
				this.p = payroll;
				if (payroll == null) throw new CanNotCreatePdfException("No payroll found.");
				
				PDPage page = PDFToolkit.createVerticalPage();
				doc.addPage(page);

				contents = new PDPageContentStream(doc,page);
	
				draw_header();
				boolean jump = calculate();
	
				this.doc = doc;
				this.logo = logo;
	
				if (jump) {
					draw_accruals();
					drawBorderedBox(contents,10,10,575,695,PdfColors.LIGHT_GRAY);
					contents.close();
	
					page = createVerticalPage();
					doc.addPage(page);
					contents = new PDPageContentStream(doc,page);
	
					draw_header();
					drawBorderedBox(contents,10,175,575,532,PdfColors.LIGHT_GRAY);
					y -= 15;
	
					draw_deductions();
					draw_footer();
				} else {
					drawBorderedBox(contents,10,175,575,532,PdfColors.LIGHT_GRAY);
					draw_accruals();
					draw_deductions();
					draw_footer();
				}
				contents.close();
			}
			return doc;
	}
	
	
	
	private PDDocument print(DefaultPayroll payroll,Optional<InputStream> logo,Optional<Locale> language) throws CanNotCreatePdfException,IOException {
		PDDocument doc = new PDDocument();
		
		lang = language.orElse(new Locale("Es"));
		words = getBundle("com.esferalia.aon.in.payroll.pdf.creators.payroll._default.bundles.PayrollBundle",lang);

		limit = 800;
		this.p = payroll;

		if (payroll == null) throw new CanNotCreatePdfException("No payroll found.");

		PDPage page = createVerticalPage();
		doc.addPage(page);
		contents = new PDPageContentStream(doc,page);

		draw_header();
		boolean jump = calculate();

		this.doc = doc;
		this.logo = logo;

		if (jump) {
			draw_accruals();
			drawBorderedBox(contents,10,10,575,695,PdfColors.LIGHT_GRAY);
			contents.close();

			page = createVerticalPage();
			doc.addPage(page);
			contents = new PDPageContentStream(doc,page);

			draw_header();
			drawBorderedBox(contents,10,175,575,532,PdfColors.LIGHT_GRAY);
			y -= 15;

			draw_deductions();
			draw_footer();

		} else {
			drawBorderedBox(contents,10,175,575,532,PdfColors.LIGHT_GRAY);
			draw_accruals();
			draw_deductions();
			draw_footer();
		}

		contents.close();
		return doc;
}
	
	// DRAW THE HEADER
	private void draw_header() throws IOException {

		x = 20;
		y = 795;
		String date_format = text("FORMATO FECHA");
		String date_l_format = "dd 'de' MMMM 'de' yyyy";
		String replace = PayrollTypes.toString(p.getPayrollType().orElse(PayrollTypes.Type.SALARY),lang); 
		
		//HEADER CONTENT
		String title = 			text("TITULO").replace("*",replace).toUpperCase();
		String enterprise = 	getString(p.getEnterprise());
		String employee = 		getString(p.getEmployee());
		String address = 		getString(p.getAddress());
		
		String nif = 			text("NIF") 				+ ": " + getString(p.getNif());
		String nss = 			text("NSS") 				+ ": " + getString(p.getNss());
		String profes_group = 	text("G.PROFESIONAL") 		+ ": " + getString(p.getProfessional_group());
		String ccc = 			text("CCC") 				+ ": " + getString(p.getCcc());
		String cif = 			text("CIF") 				+ ": " + getString(p.getCif());
		String cotiz_group = 	text("G.COTIZ") 			+ ": " + getString(p.getQuotation_group());
		String antiqu_date = 	text("FECHA ANTIGUEDAD") 	+ ": " + getString(formatDate(p.getAntiquity().get(),date_format));
		String day_total = 		text("TOTAL DIAS") 			+ ": " + getInteger(p.getTotal_days());
		
		String liquid_period = text("PERIODO LIQUIDACION")  + ": del " 
								+ formatDate(p.getLiquid_period_start(),date_l_format).get() 
								+ " a " + formatDate(p.getLiquid_period_end(),date_l_format).get();
		
		//BUILDING THE HEADER
		float headerFontSize = fontSize;
		
		enterprise = cropped_string(enterprise,255,HELVETICA_BOLD,fontSize);
		employee = cropped_string(employee,255,HELVETICA_BOLD,fontSize);
		
		drawBorderedBox(contents,x - 10,y - 85,575,120,LIGHT_GRAY);
		drawTextCenter(contents,new PDRectangle(x,y + 4,530,100),title,BLACK,HELVETICA_BOLD,12,12);

		y -= 60;
		drawBox(contents,x,y,275,65,LIGHT_GRAY);
		drawBox(contents,x + 282,y,275,65,LIGHT_GRAY);

		y += 50;
		x += 10;
		drawText(contents,enterprise,x,y,BLACK,HELVETICA_BOLD,headerFontSize);
		drawText(contents,employee,x + 280,y,BLACK,HELVETICA_BOLD,headerFontSize);

		y -= 13.5;
		drawText(contents,address,x,y,BLACK,HELVETICA,headerFontSize);
		drawText(contents,nif,x + 280,y,BLACK,HELVETICA,headerFontSize);
		
		drawText(contents,nss,x + 380,y,BLACK,HELVETICA,headerFontSize);

		y -= 13.5;
		drawText(contents,getString(p.getAddress_2()),x,y,BLACK,HELVETICA,headerFontSize);
		drawText(contents,profes_group,x + 280,y,BLACK,HELVETICA,headerFontSize);

		y -= 13.5;
		drawText(contents,ccc,x,y,BLACK,HELVETICA,headerFontSize);
		drawText(contents,cotiz_group,x + 280,y,BLACK,HELVETICA,headerFontSize);
	

		x += 100;
		drawText(contents,cif,x + 30,y,BLACK,HELVETICA,headerFontSize);
		drawText(contents,antiqu_date,x+280,y,BLACK,HELVETICA,headerFontSize);
		

		y -= 25;
		x -= 100;
		drawText(contents,liquid_period,x-8,y,BLACK,HELVETICA,fontSize);
		new PdfText(x + 280,y,265,12,contents,day_total,BLACK,HELVETICA,fontSize,RIGHT).draw();
		
	}

	// CHECK IF JUMPS
	private boolean calculate() throws IOException {

		Optional<Map<Integer,ArrayList<DefaultPayrollAccrual>>> accruals = p.getAccruals();
		Optional<Map<Integer,ArrayList<DefaultPayrollDeduction>>> deductions = p.getDeductions();

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

		String title = text("DEVENGOS").toUpperCase();
		String totals = text("TOTALES").toUpperCase();
		String accrual_total_title = "A." + text("TOTAL DEVENGADO").toUpperCase() + ":";
		String accrual_total = to_latin_number(p.getAccrual_total().orElse(0.00)) + " " + text("MONEDA");
		
		drawText(contents,title,x,y,BLACK,HELVETICA_BOLD,fontSize + 3);
		drawBox(contents,x + 470,y - 7,80,20,LIGHT_GRAY);
		drawTextRight(contents,new PDRectangle(x + 450,y-7,100,20),totals,BLACK,HELVETICA,fontSize,7,7);
		
		y -= 20;

		Optional<Map<Integer,ArrayList<DefaultPayrollAccrual>>> accruals = p.getAccruals();
		accruals.get().entrySet().stream().sorted(Map.Entry.<Integer,ArrayList<DefaultPayrollAccrual>>comparingByKey()).forEach(m -> {
			try {
				double local_total = m.getValue().stream().mapToDouble(accrual -> getDouble(accrual.getAmount())).sum();
				if (local_total != 0) {
					String accrual_txt = m.getKey() + ". " + get_type(m.getKey(),lang);
					String accrual_total_txt = to_latin_number(local_total)  + " " + text("MONEDA");
					
					drawText(contents,accrual_txt,x,y,BLACK,HELVETICA_BOLD,fontSize);
					drawTextRight(contents,new PDRectangle(x + 355,y - 5,100,10),accrual_total_txt,BLACK,HELVETICA,fontSize,5,5);
					drawBox(contents,x,y - 2,455,.2f,BLACK);
					y -= 15;
	
					m.getValue().stream().forEach(n -> {
							String entry_value = to_latin_number(n.getAmount().orElse(null)) + " " + text("MONEDA");
							String entry_txt = " por " + getString(n.getDescription());
							
							PdfText text = new PdfText(x ,y,60,15,contents,entry_value,BLACK,HELVETICA,fontSize,RIGHT);
							text.draw();
							
							PdfText t2 = new PdfText(x + 64,y,300,15,contents,entry_txt,BLACK,HELVETICA,fontSize,LEFT);
							t2.draw();

						y -= 10.5f;
					});
				}
			} catch (IOException | UnknownCraException e) {e.printStackTrace();}
			y -= 5;

		});

		y -= 5;

		
		drawTextRight(contents,new PDRectangle(x + 350,y,200,25),accrual_total,BLACK,HELVETICA,fontSize,7,5);
		drawTextRight(contents,new PDRectangle(x + 265,y,200,25),accrual_total_title,BLACK,HELVETICA,fontSize,5,5);
	}

	// DRAW DEDUCTIONS
	private void draw_deductions() throws IOException {
		
		//DEDUCTIONS CONTENT
		String title =						text("DEDUCCIONES").toUpperCase();
		String deduction_total_title = 		"B. " + text("TOTAL DEDUCIR").toUpperCase() + ": ";
		String deduction_total = 			to_latin_number(getDouble(p.getDeduction_total())) + " " + text("MONEDA");
		String payroll_total_title = 		text("TOTAL PERCIBIR").toUpperCase() + " (A-B): ";
		String payroll_total = 				to_latin_number(getDouble(p.getPayroll_total())) + " " + text("MONEDA");
		String enterprise_sign = 			text("FIRMA EMPRESA").toUpperCase();
		String employee_sign = 				formatDate(new Date(),text("FIRMA TRABAJADOR")).get();
		
		x =  23.5f;
		
		//BUILD DEDUCTIONS
		Optional<Map<Integer,ArrayList<DefaultPayrollDeduction>>> deductions = p.getDeductions();		
		drawText(contents,title,x,y-20 ,BLACK,HELVETICA_BOLD,fontSize+3);
		y -= 40;
		
		//FOR EACH DEDUCTION
		deductions.get().entrySet().stream().sorted(Map.Entry.<Integer,ArrayList<DefaultPayrollDeduction>>comparingByKey()).forEach(m -> {	
			try {

					double local_total = m.getValue().stream().mapToDouble(accrual->getDouble(accrual.getAmount())).sum();
					String deduction_txt = m.getKey() + ". " + getType(m.getKey());
					String deduction_total_txt = to_latin_number(local_total) + " " + text("MONEDA");
						
					drawText(contents,deduction_txt,x,y,BLACK,HELVETICA_BOLD,fontSize);
					drawTextRight(contents,new PDRectangle(x + 355,y - 5,100,10),deduction_total_txt,BLACK,HELVETICA,fontSize,2,5);
					drawBox(contents,x,y -2,455,.2f,BLACK);
					
					y -= 15;
					
				 if(local_total != 0)
					m.getValue().stream().forEach(
						n ->{								
							String entry_value 		= to_latin_number(n.getAmount().orElse(null)) + " " + text("MONEDA");
							String entry_txt 		= " por " + n.getDescription().orElse("");
							String entry_percent 	= (n.getPercent().isEmpty())? "" : to_latin_number(n.getPercent().get())+ " % " ;
								
							if(n.getAmount().isPresent() && n.getAmount().get() != 0) {
								PdfText quantity = new PdfText(x ,y,60,15,contents,entry_percent,BLACK,HELVETICA,fontSize,RIGHT);
								quantity.draw();
										
								PdfText t2 = new PdfText(x + 64,y,270,15,contents,entry_txt,BLACK,HELVETICA,fontSize,LEFT);
								t2.draw();
										
								PdfText t3 = new PdfText(x + 64 + 270,y,60,15,contents,entry_value,BLACK,HELVETICA,fontSize,RIGHT);
								t3.draw();
				
								y -= 10;
							}
						}
					);	
			} 
			catch (IOException e) {e.printStackTrace();}
			y -= 5;
		});
		
		y -= 15;
		
		drawTextRight(contents,new PDRectangle(x + 350,y,200,25),deduction_total,BLACK,HELVETICA,fontSize,5,5);
		drawTextRight(contents,new PDRectangle(x + 265,y,200,25),deduction_total_title,BLACK,HELVETICA,fontSize,5,5);
		
		PdfText ent = new PdfText(x,y + 7,150,25,contents,enterprise_sign,BLACK,HELVETICA,fontSize-2,CENTER);
		ent.draw();
		y-=10;
		
		if(logo.isPresent()) {
			byte[] bytes =  logo.get().readAllBytes();
			PdfImage img = new PdfImage(x + 20, y - 50, 170, 70, 0, 0, contents, doc, bytes);
			
			PdfBox box = new PdfBox( x + 20, y - 50 , 170, 70, BLACK, contents);			
			img.scale(170, 70, LEFT).draw();
		}
		
		y -= 15;
		
		PdfBox b = new PdfBox(495,y-3,80,22,LIGHT_GRAY,contents);
		b.draw();
		
		drawTextRight(contents,new PDRectangle(x + 350,y,200,25),payroll_total,BLACK,HELVETICA_BOLD,fontSize,5,5);
		drawTextRight(contents,new PDRectangle(x + 265,y,200,25),payroll_total_title,BLACK,HELVETICA_BOLD,fontSize,5,5);
		
		y -= 20;
		drawTextRight(contents,new PDRectangle(x + 455,y,100,25),employee_sign,BLACK,HELVETICA,fontSize-2,5,5);
	}

	// DRAW FOOTER
	public void draw_footer() throws IOException {

		Optional<Contingency_bases> contigencies = p.getContingencies();
		if (contigencies.isPresent()) {
			y = 155;
			x = 25;

			final String title = 						text("TITULO PIE");
			final String title_2 = 						text("TITULO PIE 2");
			final String ap_ent = 						text("AP EMPRESA").toUpperCase();
			final String type = 						text("TIPO").toUpperCase();
			final String base = 						text("BASE").toUpperCase();
			
			final String common_contingencies_title = 	"1. " + text("CONTINGENCIAS COMUNES");
			final String monthly_ammount_title = 		text("IMPORTE DE REMUNERACION MENSUAL");
			final String extra_h_prorration_title =   	text("IMPORTE PRORRATA DE PAGA EXTRAORDINARIA");
			
			final String prof_contingencies_title =  	"2. " + text("CONTINGENCIAS PROFESIONALES Y CONCEPTOS DE RECAUDACION CONJUNTA");
			final String at_ep_title = 					text("AT Y EP");
			final String unemployment_title = 			text("DESEMPLEO");
			final String profes_form_title = 			text("FORMACION PROFESIONAL");
			final String fogasa_title = 				text("FONDO DE GARANTIA SALARIAL");
			
			final String extra_hours_title = 			"3. " + text("COTIZACION ADICIONAL POR HORAS EXTRAS");
			final String force_majeure_title = 			text("FUERZA MAYOR O");
			final String no_struct_title =				text("NO ESTRUCTURALES");
				  String irpf_title =					"4. " + text("BASE SUJETA A RETENCION IRPF") + " ";
			final String total_contingencies_title =	text("TOTAL APORTACIONES");
			final Contingency_bases conts = 			contigencies.get();
			
			final String monthly_ammount =  			to_latin_number(getDouble(conts.getMonthly_amount())) 	+  " " 	+ text("MONEDA");
			final String comm_cont_base = 				to_latin_number(getDouble(conts.getCommon_cont_base())) +  " " 	+ text("MONEDA");
			String comm_cont_type = 					to_latin_number(getDouble(conts.getCommon_cont_type())) +  " %" ;
			if (comm_cont_type.contains("-1"))     		comm_cont_type = "";
			
			final String comm_cont_ap_ent = 			to_latin_number(getDouble(conts.getCommon_cont_ap_enterprise()))  + " " + text("MONEDA");
			final String extra_prorration_amount =    	to_latin_number(getDouble(conts.getExtra_proration_amount())) 	  + " " + text("MONEDA");
			final String prof_contingencies_base = 		to_latin_number(getDouble(conts.getProfessional_cont_base())) 	  + " " + text("MONEDA");
			String at_ep_type = 						to_latin_number(getDouble(conts.getAt_ep_type())) + " %";
			if (at_ep_type.contains("-1"))				at_ep_type = "";
			
			final String at_ep_ap_ent =					to_latin_number(getDouble(conts.getAt_ep_ap_enterprise())) + " " + text("MONEDA");
				  String unemployment_type = 			to_latin_number(getDouble(conts.getUnemployment_type()))   + " %";
			if (unemployment_type.contains("-1"))		unemployment_type = "";
			
			final String unemployment_ap_ent = 			to_latin_number(getDouble(conts.getUnemployment_ap_enterprise())) + " " + text("MONEDA");
			final String profes_form_type = 			to_latin_number(getDouble(conts.getProfes_form_type())) + " %";
			final String profes_form_ap_ent = 			to_latin_number(getDouble(conts.getProfes_form_ap_enterprise())) + " " + text("MONEDA");
				  String fogasa_type = 					to_latin_number(getDouble(conts.getFogasa_type())) + " %";
			if (fogasa_type.contains("-1"))				fogasa_type = "";
			
			String fogasa_ap_ent = 						to_latin_number(getDouble(conts.getFogasa_ap_enterprise())) + " " + text("MONEDA");
			String force_majeure_base = 				to_latin_number(getDouble(conts.getForce_majeure_base())) + " " + text("MONEDA");
			String force_majeure_type = 				to_latin_number(getDouble(conts.getForce_majeure_type())) + " %";
			if (force_majeure_type.contains("-1"))		force_majeure_type = "";
			
			String force_majeure_ap_ent = 				to_latin_number(getDouble(conts.getForce_majeure_ap_enterprise())) + " " + text("MONEDA");
			String no_struct_base = 					to_latin_number(getDouble(conts.getNo_struct_base())) + " " + text("MONEDA");
			String no_struct_type = 					to_latin_number(getDouble(conts.getNo_struct_type())) + " %";
			if (no_struct_type.contains("-1"))			no_struct_type = "";
			
			String no_struct_ap_ent = 					to_latin_number(getDouble(conts.getNo_struct_ap_enterprise())) + " " + text("MONEDA");
			String total_irpf = 						to_latin_number(getDouble(conts.getIrpf_esp()) + conts.getIrpf_retrib_diner().orElse(0.00)) + " " + text("MONEDA");
			String total_contingencies_amount =			to_latin_number(getDouble(conts.getTotal())) + " " + text("MONEDA");
			
			Double irpf_esp 	= getDouble(conts.getIrpf_esp()) ;
			Double irpf_ret_din = getDouble(conts.getIrpf_retrib_diner());
					
			if(irpf_esp != 0) irpf_title += 		to_latin_number(irpf_esp) + " " + text("MONEDA") + " " + text("EN ESPECIE") + " ";
			if(irpf_ret_din != 0) irpf_title += 	to_latin_number(irpf_ret_din) + " " + text("MONEDA") + " " + text("EN RETRIBUCIONES DINERARIAS");
			
			drawBorderedBox(contents,10,10,575,160,LIGHT_GRAY);
			drawText(contents,title,x,y,BLACK,HELVETICA_BOLD,fontSize-1);

			y -= 10;
			drawText(contents,title_2,x,y,BLACK,HELVETICA_BOLD,fontSize-1);

			y -= 18;
			drawBox(contents,x + 480,y,70,20,LIGHT_GRAY);
			drawTextRight(contents,new PDRectangle(x + 480,y,70,20),ap_ent,BLACK,HELVETICA,fontSize-1,5,7);

			drawBox(contents,x + 408,y,70,20,LIGHT_GRAY);
			drawTextRight(contents,new PDRectangle(x + 408,y,70,20),type,BLACK,HELVETICA,fontSize-1,5,7);

			drawBox(contents,x + 336,y,70,20,LIGHT_GRAY);
			drawTextRight(contents,new PDRectangle(x + 336,y,70,20),base,BLACK,HELVETICA,fontSize-1,5,7);
			
			drawText(contents,common_contingencies_title,x,y,BLACK,HELVETICA_BOLD,fontSize-1);

			y -= 9;
			x += 15;
			drawText(contents,monthly_ammount_title,x,y,BLACK,HELVETICA,fontSize-3);
			drawTextRight(contents,new PDRectangle(x + 215,y,70,70),monthly_ammount,BLACK,HELVETICA,fontSize-3,5,1);
			
			drawTextRight(contents,new PDRectangle(x + 322,y - 5,70,70),comm_cont_base,BLACK,HELVETICA,fontSize-3,5,1);
			drawTextRight(contents,new PDRectangle(x + 396,y - 5,70,70),comm_cont_type,BLACK,HELVETICA,fontSize-3,5,1);
			drawTextRight(contents,new PDRectangle(x + 465,y - 5,70,70),comm_cont_ap_ent,BLACK,HELVETICA,fontSize-3,5,1);

			y -= 8;
			drawText(contents,extra_h_prorration_title,x,y,BLACK,HELVETICA,fontSize-3);
			drawTextRight(contents,new PDRectangle(x + 215,y,70,70),extra_prorration_amount,BLACK,HELVETICA,fontSize-3,5,1);

			y -= 12;
			drawText(contents,prof_contingencies_title,x-15,y,BLACK,HELVETICA_BOLD,7);

			y -= 10;
			drawText(contents,at_ep_title,x,y,BLACK,HELVETICA,fontSize-3);
			drawTextRight(contents,new PDRectangle(x + 396,y - 5,70,70),at_ep_type,BLACK,HELVETICA,fontSize-3,5,1);
			drawTextRight(contents,new PDRectangle(x + 465,y - 5,70,70),at_ep_ap_ent,BLACK,HELVETICA,fontSize-3,5,1);

			y -= 8;
			drawText(contents,unemployment_title,x,y,BLACK,HELVETICA,fontSize-3);
			drawTextRight(contents,new PDRectangle(x + 396,y - 5,70,70),unemployment_type,BLACK,HELVETICA,fontSize-3,5,1);
			drawTextRight(contents,new PDRectangle(x + 465,y - 5,70,70),unemployment_ap_ent,BLACK,HELVETICA,fontSize-3,5,1);

			y -= 8;
			drawTextRight(contents,new PDRectangle(x + 322,y,70,70),prof_contingencies_base,BLACK,HELVETICA,fontSize-3,5,1);
			drawText(contents,profes_form_title,x,y,BLACK,HELVETICA,fontSize-3);
			drawTextRight(contents,new PDRectangle(x + 396,y - 5,70,70),profes_form_type,BLACK,HELVETICA,fontSize-3,5,1);
			drawTextRight(contents,new PDRectangle(x + 465,y - 5,70,70),profes_form_ap_ent,BLACK,HELVETICA,fontSize-3,5,1);

			y -= 8;
			drawText(contents,fogasa_title,x,y,BLACK,HELVETICA,fontSize-3);
			drawTextRight(contents,new PDRectangle(x + 396,y - 5,70,70),fogasa_type,BLACK,HELVETICA,fontSize-3,5,1);
			drawTextRight(contents,new PDRectangle(x + 465,y - 5,70,70),fogasa_ap_ent,BLACK,HELVETICA,fontSize-3,5,1);

			y -= 12;
			drawText(contents,extra_hours_title,x-15,y,BLACK,HELVETICA_BOLD,fontSize-2);

			y -= 10;
			drawText(contents,force_majeure_title,x,y,BLACK,HELVETICA,fontSize-3);
			drawTextRight(contents,new PDRectangle(x + 322,y - 2,70,70),force_majeure_base,BLACK,HELVETICA,fontSize-3,5,1);
			drawTextRight(contents,new PDRectangle(x + 396,y - 2,70,70),force_majeure_type,BLACK,HELVETICA,fontSize-3,5,1);
			drawTextRight(contents,new PDRectangle(x + 465,y - 2,70,70),force_majeure_ap_ent,BLACK,HELVETICA,fontSize-3,5,1);

			y -= 8;
			drawText(contents,no_struct_title,x,y,BLACK,HELVETICA,fontSize-3);
			drawTextRight(contents,new PDRectangle(x + 322,y - 2,70,70),no_struct_base,BLACK,HELVETICA,fontSize-3,5,1);
			drawTextRight(contents,new PDRectangle(x + 396,y - 2,70,70),no_struct_type,BLACK,HELVETICA,fontSize-3,5,1);
			drawTextRight(contents,new PDRectangle(x + 465,y - 2,70,70),no_struct_ap_ent,BLACK,HELVETICA,fontSize-3,5,1);

			y -= 12;
			x -= 15;
			drawText(contents,irpf_title,x,y,BLACK,HELVETICA_BOLD,fontSize-3);
			drawBox(contents,x,y -2,405,.2f,BLACK);
			drawTextRight(contents,new PDRectangle(x + 308,y - 5,100,10),total_irpf,BLACK,HELVETICA,fontSize-3,5,5);
			drawTextRight(contents,new PDRectangle(x + 451,y - 5,30,10),total_contingencies_title,BLACK,HELVETICA_BOLD,6.5f,5,5);
			drawTextRight(contents,new PDRectangle(x + 518,y - 5,30,10),total_contingencies_amount,BLACK,HELVETICA_BOLD,6.5f,3,5);
			
			
			Date d = new Date();
			String vs = "v0.36-AK";
			
			PdfText version = new PdfText(5f,1f,200f,10f,contents,vs,BLACK,HELVETICA ,5f,LEFT);
			version.draw();

		}
		
	}
	
	public String text(String name) {
		return words.getString(name);
	}

}
