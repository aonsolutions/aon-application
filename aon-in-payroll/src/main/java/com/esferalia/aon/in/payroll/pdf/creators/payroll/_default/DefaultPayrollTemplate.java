package com.esferalia.aon.in.payroll.pdf.creators.payroll._default;

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
import com.esferalia.aon.occam.api.model.type.DeductionType;

import antlr.collections.List;

public class DefaultPayrollTemplate {
	
	String filename = "./payroll.pdf";
	float limit;
	
	PDPageContentStream contents;
	DefaultPayroll payroll;
	float x;
	float y;
	
	//PRINT THE PDF
	public static void print(String out, DefaultPayroll payroll, Optional<InputStream> logo) throws CanNotCreatePdfException {
		try (PDDocument doc = new PDDocument()) {
			
			ResourceBundle resourceBundle = ResourceBundle.getBundle("com.esferalia.aon.in.payroll.pdf.creators.payroll._default.bundles.payroll", new Locale("Es"));
			System.out.println(resourceBundle.getString("titulo"));
			
			DefaultPayrollTemplate template = new DefaultPayrollTemplate();
			
			template.filename = out;
			template.limit = 800;
			template.payroll = payroll;
			
			if (template.payroll == null) 	throw new CanNotCreatePdfException("No payroll found.");
			
			PDPage page = PDFToolkit.createVerticalPage();
			doc.addPage(page);
			
			template.contents = new PDPageContentStream(doc, page);
			
			draw_header(template);
			boolean jump = calculate(template);
			
			if(jump) {
				draw_accruals(template);	
				PDFToolkit.drawBorderedBox(template.contents,22, 20, 550, 678, PDFToolkit.LIGHT_GRAY);
				template.contents.close();
				
				page = PDFToolkit.createVerticalPage();
				doc.addPage(page);
				template.contents = new PDPageContentStream(doc, page);
				
				draw_header(template);
				PDFToolkit.drawBorderedBox(template.contents,22, 220, 550, 478, PDFToolkit.LIGHT_GRAY);
				template.y -= 15;
				
				draw_deductions(template);
				draw_footer(template);
				
			}else {
				PDFToolkit.drawBorderedBox(template.contents,22, 220, 550, 480, PDFToolkit.LIGHT_GRAY);
				draw_accruals(template);
				draw_deductions(template);
				draw_footer(template);
			}
			
			template.contents.close();
			doc.save(new File(template.filename));
			
		} catch (Exception e) {throw new CanNotCreatePdfException(e);}
		
	}
	
	//DRAW THE HEADER
	public static void draw_header(DefaultPayrollTemplate template) throws IOException {
		
		template.x = 32;
		template.y = 780;
		
		PDFToolkit.drawBorderedBox(template.contents, template.x-10, template.y - 75, 550, 110, PDFToolkit.LIGHT_GRAY);
		
		PDFToolkit.drawTextCenter(template.contents, new PDRectangle(template.x, template.y, 530, 100),
				"RECIBO INDIVIDUAL JUSTIFICATIVO DEL PAGO DE SALARIO", PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 12, 12);
		
		template.y -= 55;		
		PDFToolkit.drawBox(template.contents, template.x, template.y, 260, 55, PDFToolkit.LIGHT_GRAY);
		PDFToolkit.drawBox(template.contents, template.x + 270, template.y, 260, 55, PDFToolkit.LIGHT_GRAY);
		
		template.y += 42.5;
		template.x += 10;
		PDFToolkit.drawText(template.contents, "Empresa: " + template.payroll.getEnterprise().orElse(""), template.x, template.y,
				PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		PDFToolkit.drawText(template.contents, "Trabajador: " + template.payroll.getEmployee().orElse(""), template.x + 270, template.y,
				PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		
		
		template.y -= 12;
		PDFToolkit.drawText(template.contents, "Domicilio: " + template.payroll.getAddress().orElse(""), template.x, template.y,
				PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		PDFToolkit.drawText(template.contents, "NIF: " + template.payroll.getNif().orElse(""), template.x+270, template.y,
				PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		
		
		template.y -= 12;
		PDFToolkit.drawText(template.contents,template.payroll.getAddress_2().orElse(""), template.x, template.y,
				PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		PDFToolkit.drawText(template.contents,"Nss:" + template.payroll.getNss().orElse(""), template.x + 270, template.y,
				PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		
		
		
		template.y -= 12;
		PDFToolkit.drawText(template.contents,"CIF: " + template.payroll.getCif().orElse(""), template.x, template.y,
				PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		PDFToolkit.drawText(template.contents,"G.Cotiz: " + template.payroll.getQuotation_group().orElse(""), template.x+270, template.y,
				PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		
		template.x += 100;
		PDFToolkit.drawText(template.contents,"CCC: " + template.payroll.getCcc().orElse(""), template.x, template.y,
				PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		PDFToolkit.drawText(template.contents,"G.Profesional: " + template.payroll.getProfessional_group().orElse(""), template.x+270, template.y,
				PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);

		template.y -= 18;
		template.x -= 100;
		
		String liquid_period = "Periodo Liquidación: ";
		if(template.payroll.getLiquid_period_start().isPresent()) liquid_period += PDFToolkit.formatDate(template.payroll.getLiquid_period_start().get(), "dd/MM/yyyy").get() + " - "; 
		if(template.payroll.getLiquid_period_end().isPresent()) liquid_period += PDFToolkit.formatDate(template.payroll.getLiquid_period_end().get(), "dd/MM/yyyy").get();
		
		PDFToolkit.drawText(template.contents,liquid_period, template.x, template.y,
				PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		PDFToolkit.drawText(template.contents,"Total dias: " + template.payroll.getTotal_days().orElse(0), template.x+270, template.y,
				PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		
		
		
	}
	
	//CHECK IF JUMPS
	public static boolean calculate(DefaultPayrollTemplate template) throws IOException { 
		
		
		double total = PDFToolkit.createVerticalPage().getMediaBox().getHeight();
		
		Optional<Map<Integer, ArrayList<DefaultPayrollAccrual>>> accruals = template.payroll.getAccruals();
		Optional<Map<Integer, ArrayList<DefaultPayrollDeduction>>> deductions = template.payroll.getDeductions();
		
		double sum = 0;
		
		sum += 25; 
		sum += accruals.get().entrySet().size() * 12;
		sum += accruals.get().entrySet().stream().mapToDouble(entry -> entry.getValue().stream().count()).sum()*12;
		
		sum += 25;
		sum += deductions.get().entrySet().size() * 12;
		sum += deductions.get().entrySet().stream().mapToDouble(entry -> entry.getValue().stream().count()).sum()*12;

		return sum > 370;
	}

	//DRAW ACCRUALS
	public static void draw_accruals(DefaultPayrollTemplate template) throws IOException{

		template.x = 40;
		template.y = 675;
		
		
		PDFToolkit.drawText(template.contents, "DEVENGOS .", template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 10);
		PDFToolkit.drawBox(template.contents, template.x + 420, template.y-7, 100, 20, PDFToolkit.LIGHT_GRAY);
		PDFToolkit.drawText(template.contents,"TOTALES", template.x + 455, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
		template.y -= 20;
		
		Optional<Map<Integer, ArrayList<DefaultPayrollAccrual>>> accruals = template.payroll.getAccruals();

		//FOR EACH ACCRUAL
		accruals.get().entrySet().stream().forEach(m -> {	
			try {
				PDFToolkit.drawText(template.contents,m.getKey() + ". " + CraTypes.get_type(m.getKey()), template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 6);
				double local_total = m.getValue().stream().mapToDouble(accrual->accrual.getAmount().orElse(0.00)).sum();
				PDFToolkit.drawTextRight(template.contents,
						new PDRectangle(template.x + 300, template.y - 5, 100, 10),
						PDFToolkit.to_latin_number(local_total),
						PDFToolkit.BLACK,
						PDFToolkit.HELVETICA,
						6,
						5,
						5
				);
				PDFToolkit.drawBox(template.contents,template.x, template.y -2, 400, .2f, PDFToolkit.BLACK);
				template.y -= 10;
				
				m.getValue().stream().forEach(
						n ->{
							try {
								PDFToolkit.drawText(template.contents, 
										 PDFToolkit.to_latin_number(n.getAmount().orElse(0.00))+ "\u20ac en " + n.getDescription().orElse("Concepto desconocido.") 
								, template.x + 9, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 6);
							} catch (IOException e) {e.printStackTrace();}
							template.y -= 10;
						}
				);			
				
			} 
			catch (IOException | UnknownCraException e) {e.printStackTrace();}
			template.y -= 5;
			
		});
		
		template.y -= 5;
		PDFToolkit.drawTextRight(template.contents,
				new PDRectangle(template.x + 315, template.y, 200, 25),
				PDFToolkit.to_latin_number(template.payroll.getAccrual_total().orElse(0.00)),
				PDFToolkit.BLACK,
				PDFToolkit.HELVETICA, 7, 5, 5);
		
		PDFToolkit.drawTextRight(template.contents,
				new PDRectangle(template.x + 225,template.y, 200, 25),
				"A.TOTAL DEVENGADO: ",
				PDFToolkit.BLACK,
				PDFToolkit.HELVETICA, 7, 5, 5);

	}
	
	//DRAW DEDUCTIONS
	public static void draw_deductions(DefaultPayrollTemplate template) throws IOException {
		
		Optional<Map<Integer, ArrayList<DefaultPayrollDeduction>>> deductions = template.payroll.getDeductions();
		
		template.y -= 20;
		PDFToolkit.drawText(template.contents, "DEDUCCIONES .", template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 10);
		template.y -= 20;
		
		//FOR EACH DEDUCTION
		deductions.get().entrySet().stream().forEach(m -> {	
			
			try {
				PDFToolkit.drawText(template.contents,m.getKey() + ". " + DeductionTypes.getType(m.getKey()), template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 6);
				double local_total = m.getValue().stream().mapToDouble(accrual->accrual.getAmount().orElse(0.00)).sum();
				PDFToolkit.drawTextRight(template.contents,
						new PDRectangle(template.x + 300, template.y - 5, 100, 10),
						PDFToolkit.to_latin_number(local_total),
						PDFToolkit.BLACK,
						PDFToolkit.HELVETICA,
						6,
						5,
						5
				);
				PDFToolkit.drawBox(template.contents,template.x, template.y -2, 400, .2f, PDFToolkit.BLACK);
				template.y -= 10;
				m.getValue().stream().forEach(
						n ->{
							try {
								PDFToolkit.drawText(template.contents, 
										 PDFToolkit.to_latin_number(n.getAmount().orElse(0.00))+ " por " + n.getDescription().orElse("Concepto desconocido.") 
								, template.x + 9, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 6);
								
								PDFToolkit.drawText(template.contents,  PDFToolkit.to_latin_number(n.getPercent().orElse(0.00))+ " % " , template.x + 249, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 6);
							} catch (IOException e) {e.printStackTrace();}
							template.y -= 10;
						}
				);			
				
			} 
			catch (IOException e) {e.printStackTrace();}
			template.y -= 5;
			
		});
		
		template.y -= 5;
		PDFToolkit.drawTextRight(template.contents,
				new PDRectangle(template.x + 315, template.y, 200, 25),
				PDFToolkit.to_latin_number(template.payroll.getDeduction_total().orElse(0.00)),
				PDFToolkit.BLACK,
				PDFToolkit.HELVETICA, 7, 5, 5);
		
		PDFToolkit.drawTextRight(template.contents,
				new PDRectangle(template.x + 225,template.y, 200, 25),
				"B.TOTAL DEDUCIR: ",
				PDFToolkit.BLACK,
				PDFToolkit.HELVETICA, 7, 5, 5);
		
		template.y -= 10;
		PDFToolkit.drawTextRight(template.contents,
				new PDRectangle(template.x + 315, template.y, 200, 25),
				PDFToolkit.to_latin_number(template.payroll.getPayroll_total().orElse(0.00)),
				PDFToolkit.BLACK,
				PDFToolkit.HELVETICA, 7, 5, 5);
		
		PDFToolkit.drawTextRight(template.contents,
				new PDRectangle(template.x + 225,template.y, 200, 25),
				"LIQUIDO TOTAL A PERCIBIR (A-B): ",
				PDFToolkit.BLACK,
				PDFToolkit.HELVETICA, 7, 5, 5);
		
		template.y -= 15;
		PDFToolkit.drawTextRight(template.contents,
				new PDRectangle(template.x + 315,template.y, 100, 25),
				"Sello y firma de la empresa",
				PDFToolkit.BLACK,
				PDFToolkit.HELVETICA, 6, 5, 5);
		
		PDFToolkit.drawTextRight(template.contents,
				new PDRectangle(template.x + 415,template.y, 100, 25),
				"El "+ PDFToolkit.formatDate(new Date(), "dd").get() +" de "+ PDFToolkit.formatDate(new Date(), "MMMM").get() +" recibí",
				PDFToolkit.BLACK,
				PDFToolkit.HELVETICA, 6, 5, 5);
	}
	
	//DRAW FOOTER
	public static void draw_footer(DefaultPayrollTemplate template) throws IOException {
		
		Optional<Contingency_bases> contigencies = template.payroll.getContingencies();
		if(contigencies.isPresent()) {
			template.y = 195;
			template.x = 37;
			
			PDFToolkit.drawBorderedBox(template.contents, 22, 18, 550, 195, PDFToolkit.LIGHT_GRAY);
			
			PDFToolkit.drawText(template.contents,"Determinación de las bases de cotización a la Seguridad Social y conceptos de recaudación conjunta", template.x, template.y,
					PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 8);		
			
			template.y -= 10;
			PDFToolkit.drawText(template.contents,"y de la base sujeta a retención del IRPF y aportación de la empresa", template.x, template.y,
					PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 8);
			
			template.y -= 27;
			PDFToolkit.drawBox(template.contents, template.x + 450, template.y, 70, 20, PDFToolkit.LIGHT_GRAY);
			PDFToolkit.drawText(template.contents,"AP.EMPRESA", template.x + 462, template.y + 7,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			
			PDFToolkit.drawBox(template.contents, template.x + 378, template.y, 70, 20, PDFToolkit.LIGHT_GRAY);
			PDFToolkit.drawText(template.contents,"TIPO", template.x + 405, template.y + 7,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			
			PDFToolkit.drawBox(template.contents, template.x + 306, template.y, 70, 20, PDFToolkit.LIGHT_GRAY);
			PDFToolkit.drawText(template.contents,"BASE", template.x +332, template.y + 7,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			
			PDFToolkit.drawText(template.contents,"1. Contingencias comunes", template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 7);
			
			
			template.y -= 12;
			template.x += 15;
			PDFToolkit.drawText(template.contents,"Importe de remuneración mensual", template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 215, template.y, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getMonthly_amount().orElse(0.00))+"",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);
			
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 290, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getCommon_cont_base().orElse(0.00))+"",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);		
			
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 362, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getCommon_cont_type().orElse(0.00))+"%",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);	
			
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 434, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getCommon_cont_ap_enterprise().orElse(0.00))+"",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);	
			
			template.y -= 10;
			PDFToolkit.drawText(template.contents,"Importe prorrata de paga extraordinarias", template.x, template.y,
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 215, template.y, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getExtra_proration_amount().orElse(0.00))+"",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);
			
			template.y -= 12;
			template.x -= 15;
			PDFToolkit.drawText(template.contents,"2. Contingencias profesionales y conceptos de recaudación conjunta", template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 7);
			
			template.y -= 12;
			template.x += 15;
			PDFToolkit.drawText(template.contents,"AT y EP", template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 362, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getAt_ep_type().orElse(0.00))+"%",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);	
			
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 434, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getAt_ep_ap_enterprise().orElse(0.00))+"",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);	
			
			template.y -= 10;
			PDFToolkit.drawText(template.contents,"Desempleo", template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 362, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getUnemployment_type().orElse(0.00))+"%",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);	
			
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 434, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getUnemployment_ap_enterprise().orElse(0.00))+"",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);	
			
			template.y -= 10;
			PDFToolkit.drawText(template.contents,"Formación profesional", template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 362, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getProfes_form_type().orElse(0.00))+"%",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);	
			
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 290, template.y, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getProfessional_cont_base().orElse(0.00))+"",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);
			
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 434, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getProfes_form_ap_enterprise().orElse(0.00))+"",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);
			
			template.y -= 10;
			PDFToolkit.drawText(template.contents,"Fondo de garantía salarial", template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 362, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getFogasa_type().orElse(0.00))+"%",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);	
			
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 434, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getFogasa_ap_enterprise().orElse(0.00))+"",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);
			
			template.y -= 12;
			template.x -= 15;
			PDFToolkit.drawText(template.contents,"3. Cotización adicional por horas extraordinarias", template.x, template.y, PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 7);
			
			template.y -= 12;
			template.x += 15;
			PDFToolkit.drawText(template.contents,"Fuerza mayor o", template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 290, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getForce_majeure_base().orElse(0.00))+"",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);		
			
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 362, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getForce_majeure_type().orElse(0.00))+"%",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);	
			
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 434, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getForce_majeure_ap_enterprise().orElse(0.00))+"",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);	
			
			template.y -= 10;
			PDFToolkit.drawText(template.contents,"No estructurales", template.x, template.y,PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 290, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getNo_struct_base().orElse(0.00))+"",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);		
			
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 362, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getNo_struct_type().orElse(0.00))+"%",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);	
			
			PDFToolkit.drawTextRight(template.contents, new PDRectangle( template.x + 434, template.y-5, 70, 70), PDFToolkit.to_latin_number(contigencies.get().getNo_struct_ap_enterprise().orElse(0.00))+"",
					PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7, 5, 1);	
	
			template.y -= 12;
			template.x -= 15;
			PDFToolkit.drawText(template.contents,"4. Base sujeta a retención del IRPF", template.x, template.y,
					PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 7);
		
		}
	}
	
}
