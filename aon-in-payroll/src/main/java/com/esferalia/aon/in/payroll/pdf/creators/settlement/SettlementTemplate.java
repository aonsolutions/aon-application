package com.esferalia.aon.in.payroll.pdf.creators.settlement;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.LIGHT_GRAY;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats.formatDate;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats.to_latin_number;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT.LEFT;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.PAGE_TYPE.VERTICAL;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.OptionalToolkit.safeString;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.OptionalToolkit.safeValue;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawBox;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawText;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawTextRight;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfBox;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfFile;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfText;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfText.PdfTextBuilder;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.VERTICAL_ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.settlement.beans.Settlement;

public class SettlementTemplate extends PdfFile {

	private Settlement settlement;

	public SettlementTemplate(Settlement settlement, float x, float y, PDDocument doc, ResourceBundle words, OutputStream out) {
		super(x, y, doc, words, out);
		this.settlement = settlement;
	}

	/** Print a settlement
	 * 
	 * @param out
	 * @param settlement
	 * @param locale
	 * @throws CanNotCreatePdfException
	 */
	public static void print(OutputStream out, Settlement settlement, Locale locale) throws CanNotCreatePdfException{
		
		SettlementTemplate template = null;
		try {
			ResourceBundle words = ResourceBundle.getBundle("com.esferalia.aon.in.payroll.pdf.creators.settlement.bundles.SettlementBundle",(Locale) safeValue(locale,new Locale("Es")));
			
			template = new SettlementTemplate(settlement, 0, 800, new PDDocument(), words, out);;
			template.set_defaults(HELVETICA, 10f, BLACK, PdfColors.GRAY);
			
			new_page(template);
			
			draw_upper_texts(template);
			draw_accruals(template);
			draw_deductions(template);
			draw_legal_text(template);
			
			template.print();
		} catch (Exception e) {
			if (template != null) try {template.close();} catch (IOException e1) {}
			throw new CanNotCreatePdfException(e);
		}		
	}
	
	/**
	 * Draw the text in the upper
	 * @param template
	 */
	private static void draw_upper_texts(SettlementTemplate template) {
				
		final String title_txt   = template.text("TITLE");
		final String date_format = template.text("DATE FORMAT");
		final Settlement s 	     = template.settlement;

		
		
		String[] variables = {"enterprise_name", "enterprise_nif","enterprise_address"};
		String[] values = {
				safeString(s.enterprise_name()),
				safeString(s.enterprise_nif()),
				safeString(s.enterprise_address()),
		};
		
		final String enterpise_txt = replaceVariables(variables,values,template.text("ENTERPRISE"));
		
		variables = new String[]{"employee_name", "employee_nif","employee_category","employee_antiquity"};
		values 	  
		= new String[]{
				safeString(s.employee_name()),
				safeString(s.employee_nif()),
				safeString(s.employee_category()),
				safeString(formatDate(s.employee_antiquity(), date_format))
		};
		final String employee_txt  = replaceVariables(variables,values,template.text("EMPLOYEE"));
		
		final String declare_txt 	 = replaceVariables(variables,values,template.text("DECLARE"));
		
		variables = new String[]{"end_date", "end_cause", "text_total", "total_amount"};
		values 	  
		= new String[]{
				safeString(formatDate(s.end_date(),date_format)),
				safeString(s.end_cause()),
				"UNOS CUANTOS",
				to_latin_number(s.total().orElse(null))
		};
		final String declaration_txt = replaceVariables(variables,values,template.text("DECLARATION"));
		
		
		PdfTextBuilder builder = new PdfTextBuilder();
		builder
		.stream		(template.contents)
		.width		(595)
		.height		(0)
		.x			(template.x())
		.y			(template.y())
		.color		(template.primary)
		.font		(HELVETICA_BOLD)
		.font_size	(16f)
		.content	(title_txt)
		.vertical_alignment(VERTICAL_ALIGNMENT.CENTER)
		.horizontal_alignment(ALIGNMENT.CENTER);
		
		PdfText title = builder.build();
		title.draw();
	
		template.down(40);
		template.right(50);
		
		builder
		.x			(template.x())
		.width		(480)
		.y			(template.y())
		.font		(HELVETICA)
		.font_size	(10f)
		.content	(enterpise_txt)
		.line_spacing(6f)
		.horizontal_alignment(ALIGNMENT.JUSTIFY);
		
		PdfText enterpise = builder.build();
		enterpise.draw_multiple(template.y_limit);

		template.down(enterpise.height());
		
		builder
		.y			(template.y())
		.content	(employee_txt);
		
		PdfText employee = builder.build();
		employee.draw_multiple(template.y_limit);
		
		template.down(employee.height());
		
		builder
		.y			(template.y())
		.font		(HELVETICA)
		.font_size	(10f)
		.content	(declare_txt)
		.horizontal_alignment(LEFT);
		
		PdfText declare = builder.build();
		declare.draw();
		
		template.down(declare.height());
		
		builder
		.y			(template.y())
		.font		(HELVETICA)
		.font_size	(10f)
		.content	(declaration_txt)
		.horizontal_alignment(ALIGNMENT.JUSTIFY);
		
		PdfText declaration = builder.build();
		declaration.draw_multiple(template.y_limit);
		
		template.down(declaration.height() + 10);
	}
	
	/**
	 * Draw accruals
	 * @param template
	 * @throws IOException
	 */
	private static void draw_accruals(SettlementTemplate template) throws IOException {
		
		String title = template.text("ACCRUALS").toUpperCase();
		String totals = template.text("TOTALS").toUpperCase();
		String accrual_total_title = template.text("ACCRUAL TOTAL").toUpperCase();
		String accrual_total = to_latin_number(template.settlement.getAccrual_total().orElse(0.00)) + " " + template.text("CURRENCY");
		
		drawText(template.contents,title,template.x() + 3,template.y(),BLACK,HELVETICA_BOLD,template.fontsize + 3);
		drawBox(template.contents,template.x() + 400,template.y() - 7,80,20,LIGHT_GRAY);
		drawTextRight(template.contents,new PDRectangle(template.x() + 400,template.y()-7,80,20),totals,BLACK,HELVETICA,template.fontsize,7,7);
		
		template.down(20);
/*
		Optional<Map<Integer,ArrayList<Accrual>>> accruals = settlement.getAccruals();
		accruals.get().entrySet().stream().sorted(Map.Entry.<Integer,ArrayList<Accrual>>comparingByKey()).forEach(m -> {
			try {
				double local_total = m.getValue().stream().mapToDouble(accrual -> safeDouble(accrual.getAmount())).sum();
				if (local_total != 0) {
					String accrual_txt = m.getKey() + ". " + get_type(m.getKey(),lang);
					String accrual_total_txt = to_latin_number(local_total)  + " " + text("MONEDA");
					
					drawText(contents,accrual_txt,x,y,BLACK,HELVETICA_BOLD,fontSize);
					drawTextRight(contents,new PDRectangle(x + 355,y - 5,100,10),accrual_total_txt,BLACK,HELVETICA,fontSize,5,5);
					drawBox(contents,x,y - 2,455,.2f,BLACK);
					y -= 15;
	
					m.getValue().stream().forEach(n -> {
							String entry_value = to_latin_number(n.getAmount().orElse(null)) + " " + text("MONEDA");
							String entry_txt = " por " + safeString(n.getDescription());
							
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
*/
		template.down(5);

		
		drawTextRight(template.contents,new PDRectangle(template.x() + 280,template.y(),200,25),accrual_total,BLACK,HELVETICA,template.fontsize,7,5);
		drawTextRight(template.contents,new PDRectangle(template.x() + 190,template.y(),200,25),accrual_total_title,BLACK,HELVETICA,template.fontsize,5,5);
		template.down(15);
	}

	/**
	 * Draw deductions
	 * @param template
	 * @throws IOException
	 */
	private static void draw_deductions(SettlementTemplate template) throws IOException {
		
		
		String title =						template.text("DEDUCTIONS");
		String deduction_total_title = 		template.text("DEDUCTION TOTAL");
		String deduction_total = 			to_latin_number(template.settlement.deduction_total().orElse(null)) + " " + template.text("CURRENCY");
		String payroll_total_title = 		template.text("TOTAL");
		String payroll_total = 				to_latin_number(template.settlement.total().orElse(null)) + " " + template.text("CURRENCY");
	
		drawText(template.contents,title,template.x()+3,template.y() - 20 ,BLACK,HELVETICA_BOLD,template.fontsize + 3);
		template.down(40);
		
		/*
		x =  23.5f;
		
		//BUILD DEDUCTIONS
		Optional<Map<Integer,ArrayList<Deduction>>> deductions = p.getDeductions();		
		
		
		//FOR EACH DEDUCTION
		deductions.get().entrySet().stream().sorted(Map.Entry.<Integer,ArrayList<Deduction>>comparingByKey()).forEach(m -> {	
			try {

					double local_total = m.getValue().stream().mapToDouble(accrual->safeDouble(accrual.getAmount())).sum();
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
			*/
		drawTextRight(template.contents,new PDRectangle(template.x() + 280,template.y(),200,25),deduction_total,BLACK,HELVETICA,template.fontsize,5,5);
		drawTextRight(template.contents,new PDRectangle(template.x() + 190,template.y(),200,25),deduction_total_title,BLACK,HELVETICA,template.fontsize,5,5);
		
		template.down(25);
		
		PdfBox b = new PdfBox(450,template.y()-3,80,22,LIGHT_GRAY,template.contents);
		b.draw();
		
		drawTextRight(template.contents,new PDRectangle(template.x() + 280,template.y(),200,25),payroll_total,BLACK,HELVETICA_BOLD,template.fontsize,5,5);
		drawTextRight(template.contents,new PDRectangle(template.x() + 190,template.y(),200,25),payroll_total_title,BLACK,HELVETICA_BOLD,template.fontsize,5,5);
		
		template.down(20);
	}

	/**
	 * Draw compulsory legal stuff
	 * @param template
	 */
	private static void draw_legal_text(SettlementTemplate template) {
		template.down(20);
		String legal_txt 		= template.text("LEGAL DATA");
		String legal_advice_txt = template.text("LEGAL ADVICE");
		
		String[] variables = {"location"};
		String[] values = {safeString(template.settlement.location())};
		
		String date_txt 		= formatDate(new Date(),replaceVariables(variables, values,template.text("DATE"))).orElse(""); 
		
		PdfTextBuilder builder = new PdfTextBuilder();
		builder
		.stream		(template.contents)
		.x			(template.x())
		.width		(480)
		.y			(template.y())
		.font		(HELVETICA)
		.font_size	(10f)
		.content	(legal_txt)
		.line_spacing(6f)
		.horizontal_alignment(ALIGNMENT.JUSTIFY);
		
		PdfText legal_text = builder.build();
		legal_text.draw_multiple(template.y_limit);
		
		template.down(legal_text.height());
		
		builder
		.stream		(template.contents)
		.y			(template.y())
		.content	(legal_advice_txt);
		
		PdfText legal_advice_text = builder.build();
		legal_advice_text.draw_multiple(template.y_limit);
		
		template.down(legal_advice_text.height());
		
		builder
		.stream		(template.contents)
		.y			(template.y())
		.content	(date_txt);
		
		PdfText date_text = builder.build();
		date_text.draw_multiple(template.y_limit);
		
	}
	
	/**
	 * Replace variables in the bundle
	 * @param words
	 * @param values
	 * @param text
	 * @return
	 */
	public static String replaceVariables(String[] words,String[] values, String text) {
		for (int i = 0; i < words.length ; i++) text = text.replace("$" + words[i], values[i]);
		return text;
	}
	
	/**
	 * Print a new page
	 * @param t
	 */
	public static void new_page(SettlementTemplate t) {
		try {t.new_page(VERTICAL);} 
		catch (IOException e) {}
	}
	
}
