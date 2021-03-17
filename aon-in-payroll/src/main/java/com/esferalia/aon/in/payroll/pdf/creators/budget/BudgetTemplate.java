package com.esferalia.aon.in.payroll.pdf.creators.budget;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.GRAY;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.LIGHT_GRAY;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats.to_latin_number;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT.JUSTIFY;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT.LEFT;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT.RIGHT;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.PAGE_TYPE.VERTICAL;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfFile;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfText;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfText.PdfTextBuilder;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.creators.budget.beans.Budget;
import com.esferalia.aon.in.payroll.pdf.creators.budget.beans.Budget_item;
import com.esferalia.aon.in.payroll.pdf.creators.budget.beans.Client_data;
import com.esferalia.aon.in.payroll.pdf.creators.budget.beans.Term;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;

public class BudgetTemplate extends PdfFile{

	private Budget budget;	
	
	public BudgetTemplate(float x, float y, PDDocument doc, ResourceBundle words, OutputStream out, Budget budget,float limit_y) {
		super(x, y, doc, words, out,limit_y);
		this.budget = budget;
	}

	public static void print(OutputStream out,Budget bg,Optional<Locale> language) throws CanNotCreatePdfException {
		BudgetTemplate template = null;
		try{
			ResourceBundle words = ResourceBundle.getBundle("com.esferalia.aon.in.payroll.pdf.creators.budget.bundles.BudgetBundle",language.orElse(new Locale("Es")));
			template = new BudgetTemplate(10, 810, new PDDocument(), words, out, bg,30);
			template.set_defaults(HELVETICA, 10f, BLACK, PdfColors.GRAY);
			
			draw_client_info(template);
			draw_products(template);
			draw_conditions(template);
			
			PdfTextBuilder builder = new PdfTextBuilder();
			
			builder
			.x						(560)
			.y						(10)
			.width					(10)
			.height					(15)
			.stream					(template.contents)
			.content				(template.page + "")
			.color					(GRAY)
			.font					(HELVETICA)
			.font_size				(template.fontsize)
			.horizontal_alignment	(RIGHT);
			
			
			PdfText page = builder.build();
			page.draw();
			
			
			template.print();			
		} catch (Exception e) {
			if(template != null) try {template.close();} catch (IOException e1) {}
			throw new CanNotCreatePdfException(e);
		}
	}

	private static void draw_client_info(BudgetTemplate template) throws IOException {

		template.new_page(VERTICAL);
		Client_data client = template.budget.getClient().orElse(new Client_data(null, null, null, null, null, null, null, null, null, null)); 
		
		String number_title_txt = 			template.text("NUMBER") + ":";
		String number_txt = 				template.budget.getBudget_number().orElse("");
		String date_title_txt = 			template.text("DATE") + ":";
		String date_txt = 					PdfFormats.formatDate(new Date(),template.text("DATE FORMAT")).orElse("");
		String client_data_title_txt =  	template.text("CLIENT DATA").toUpperCase();
		String enterprise_name_title_txt = 	template.text("ENTERPRISE NAME") + ":";
		String enterprise_name_txt = 		client.getBusiness_name().orElse("");
		String nif_title_txt = 				template.text("NIF") + ":";
		String nif_txt = 					client.getNif().orElse("");
		String address_title_txt = 			template.text("ADDRESS") + ":";
		String address_txt = 				client.getAddress().orElse("");
		String city_title_txt = 			template.text("CITY") + ":";
		String city_txt = 					client.getCity().orElse("");
		String postal_code_title_txt = 		template.text("POSTAL CODE") + ":";
		String postal_code_txt = 			client.getPostal_code().orElse("");
		String province_title_txt = 		template.text("PROVINCE") + ":";
		String province_txt = 				client.getProvince().orElse("");
		String phone_title_txt = 			template.text("PHONE") + ":";
		String phone_txt = 					client.getPhone().orElse("");
		String mobile_title_txt = 			template.text("MOBILE") + ":";
		String mobile_txt = 				client.getMobile().orElse("");
		String email_title_txt = 			template.text("EMAIL") + ":";
		String email_txt = 					client.getEmail().orElse("");
		String contact_title_txt =			template.text("CONTACT") + ":";
		String contact_txt = 				client.getContact().orElse("");

		PdfTextBuilder builder = new PdfTextBuilder();
		
		builder
		.x						(template.x())
		.y						(template.y())
		.width					(100)
		.height					(15)
		.stream					(template.contents)
		.content				(number_title_txt)
		.color					(BLACK)
		.font					(HELVETICA_BOLD)
		.font_size				(template.fontsize)
		.horizontal_alignment	(LEFT);
		
		PdfText number_title = builder.build();
		
		builder
		.x				(template.x() + 100)
		.content		(number_txt)
		.font			(HELVETICA);
		
		PdfText number = builder.build();
		template.down(15);
		
		builder
		.x				(template.x())
		.y				(template.y())
		.content		(date_title_txt)
		.font			(HELVETICA_BOLD);
		
		PdfText date_title = builder.build();	
		
		builder
		.x				(template.x() + 100)
		.content		(date_txt)
		.font			(HELVETICA);

		PdfText date = builder.build();
		template.down(25);
		
		builder
		.x				(template.x())
		.y				(template.y())
		.width			(575)
		.content		(client_data_title_txt)
		.font			(HELVETICA);
		
		PdfText client_data_title = builder.build();
		template.down(25);
		
		builder
		.x				(template.x())
		.y				(template.y())
		.width			(75)
		.content		(enterprise_name_title_txt)
		.font			(HELVETICA_BOLD);
		
		PdfText enterprise_name_title = builder.build();
		
		builder
		.x				(template.x() + 75)
		.y				(template.y())
		.width			(300)
		.content		(enterprise_name_txt)
		.font			(HELVETICA);
		
		PdfText enterprise_name = builder.build();
		
		builder
		.x				(template.x() + 375)
		.y				(template.y())
		.width			(55)
		.content		(nif_title_txt)
		.font			(HELVETICA_BOLD);
		
		PdfText nif_title = builder.build();			
		
		builder
		.x				(template.x() + 430)
		.y				(template.y())
		.width			(143)
		.content		(nif_txt)
		.font			(HELVETICA);
		
		PdfText nif =  		builder.build();	
		template.down(15);
		
		builder	
		.x				(template.x())
		.y				(template.y())
		.width			(75)
		.content		(address_title_txt)
		.font			(HELVETICA_BOLD);
		
		PdfText address_title = builder.build();	
		
		builder	
		.x				(template.x() + 75)
		.y				(template.y())
		.width			(300)
		.content		(address_txt)
		.font			(HELVETICA); 
		
		PdfText address =  	builder.build();
	
		builder	
		.x				(template.x() + 375)
		.y				(template.y())
		.width			(55)
		.content		(city_title_txt)
		.font			(HELVETICA_BOLD); 
		
		PdfText city_title =  	builder.build();
	
		builder	
		.x				(template.x() + 430)
		.y				(template.y())
		.width			(143)
		.content		(city_txt)
		.font			(HELVETICA); 
		
		PdfText city =  	builder.build();
		template.down(15);
		
		builder	
		.x				(template.x())
		.y				(template.y())
		.width			(75)
		.content		(postal_code_title_txt)
		.font			(HELVETICA_BOLD); 
		
		PdfText postal_code_title =  	builder.build();
		
		builder	
		.x				(template.x() + 75)
		.y				(template.y())
		.width			(90)
		.content		(postal_code_txt)
		.font			(HELVETICA); 
		
		PdfText postal_code =  	builder.build();
		
		builder	
		.x				(template.x() + 165)
		.y				(template.y())
		.width			(55)
		.content		(province_title_txt)
		.font			(HELVETICA_BOLD); 
		
		PdfText province_title =  	builder.build();
	
		builder	
		.x				(template.x() + 220)
		.y				(template.y())
		.width			(155)
		.content		(province_txt)
		.font			(HELVETICA); 
		
		PdfText province =  	builder.build();
		
		builder	
		.x				(template.x() + 375)
		.y				(template.y())
		.width			(55)
		.content		(phone_title_txt)
		.font			(HELVETICA_BOLD); 
		
		PdfText phone_title =  	builder.build();
		
		builder	
		.x				(template.x() + 430)
		.y				(template.y())
		.width			(143)
		.content		(phone_txt)
		.font			(HELVETICA); 
		
		PdfText phone =  	builder.build();
		template.down(15);
		
		builder	
		.x				(template.x())
		.y				(template.y())
		.width			(75)
		.content		(mobile_title_txt)
		.font			(HELVETICA_BOLD); 
		
		PdfText mobile_title =  	builder.build();
		
		builder	
		.x				(template.x() + 75)
		.y				(template.y())
		.width			(90)
		.content		(mobile_txt)
		.font			(HELVETICA); 
		
		PdfText mobile =  	builder.build();
		
		builder	
		.x				(template.x() + 165)
		.y				(template.y())
		.width			(55)
		.content		(email_title_txt)
		.font			(HELVETICA_BOLD); 
		
		PdfText email_title =  	builder.build();
		
		builder	
		.x				(template.x() + 220)
		.y				(template.y())
		.width			(155)
		.content		(email_txt)
		.font			(HELVETICA); 
		
		PdfText email =  	builder.build();
		
		builder	
		.x				(template.x() + 375)
		.y				(template.y())
		.width			(55)
		.content		(contact_title_txt)
		.font			(HELVETICA_BOLD); 
		
		PdfText contact_title = builder.build();
		
		builder	
		.x				(template.x() + 430)
		.y				(template.y())
		.width			(143)
		.content		(contact_txt)
		.font			(HELVETICA); 
		
		PdfText contact = builder.build();
		template.down(30);
		
		number_title.draw();
		number.draw();
		
		date_title.draw();
		date.draw();
		
		client_data_title.square(PdfColors.LIGHT_GRAY);
		client_data_title.draw();
		
		enterprise_name_title.draw();
		enterprise_name.draw();
		
		nif_title.draw();
		nif.draw();
		
		address_title.draw();
		address.draw();
		
		city_title.draw();
		city.draw();
		
		postal_code_title.draw();
		postal_code.draw();
		
		province_title.draw();
		province.draw();
		
		phone_title.draw();
		phone.draw();
		
		mobile_title.draw();
		mobile.draw();
		
		email_title.draw();
		email.draw();
		
		contact_title.draw();
		contact.draw();
		
	}
	
	private static void draw_products(BudgetTemplate template) {
		
		String currency = " \u20AC";
		template.down(10);
		
		String product_description_title_txt = 	template.text("PRODUCT DESCRIPTION").toUpperCase();
		String product_service_title_txt = 		template.text("PRODUCT/SERVICE");
		String amount_title_txt = 				template.text("AMOUNT");
		String tax_base_title_txt = 			template.text("TAX BASE") + ":";
		String tax_base_txt = 					template.budget.getTax_base().orElse(0.00) 		+ currency;
		String tax_title_txt = 					template.budget.getTax_percent().orElse(0.00) 	+ template.text("TAX") + template.budget.getTax_add().orElse(0.00);
		String tax_txt = 						template.budget.getTax_total().orElse(0.00) 	+ currency;
		String total_amount_title_txt = 		template.text("TOTAL AMOUNT") + ":";
		String total_amount_txt = 				template.budget.getBudget_total().orElse(0.00) 	+ currency;
		
		PdfTextBuilder builder = new PdfTextBuilder();
		
		builder
		.x						(template.x())
		.y						(template.y())
		.width					(575)
		.height					(15)
		.stream					(template.contents)
		.content				(product_description_title_txt)
		.color					(BLACK)
		.font					(HELVETICA)
		.font_size				(template.fontsize)
		.horizontal_alignment	(LEFT);
		
		PdfText product_description_title = builder.build();
		template.down(20);
		
		builder
		.x						(template.x())
		.y						(template.y())
		.width					(200)
		.stream					(template.contents)
		.content				(product_service_title_txt)
		.font					(HELVETICA_BOLD);
		
		PdfText product_service_title = builder.build();
	
		builder
		.x						(template.x() + 370)
		.y						(template.y())
		.stream					(template.contents)
		.content				(amount_title_txt)
		.font					(HELVETICA_BOLD)
		.horizontal_alignment	(RIGHT);
		
		PdfText amount_title = builder.build();
		template.down(8);
		
		product_description_title.square(LIGHT_GRAY);
		product_description_title.draw();
		
		product_service_title.draw();
		amount_title.draw();
		
		
		ArrayList<Budget_item> products = template.budget.getProducts().orElse(new ArrayList<Budget_item>());
		products.forEach( p ->{
			template.down(12);
			
			if(template.jump()) new_page(template);
			
			builder
			.x						(template.x())
			.y						(template.y())
			.stream					(template.contents)
			.width					(200)
			.content				(p.getName().orElse(""))
			.color					(BLACK)
			.font					(HELVETICA)
			.horizontal_alignment	(LEFT);
			
			PdfText product = builder.build();	
			
			builder
			.x						(template.x() + 370)
			.y						(template.y())
			.content				(to_latin_number(p.getPrice().orElse(0.00)) + currency)
			.horizontal_alignment	(RIGHT);
			
			PdfText amount = builder.build();	
			
			product.draw();
			amount.draw();	
		});
		
		template.down(20);
		
		builder
		.x						(template.x() + 400)
		.y						(template.y())
		.width					(80)
		.content				(tax_base_title_txt)
		.color					(GRAY)
		.horizontal_alignment	(RIGHT);
		
		PdfText tax_base_title = builder.build();
		
		builder
		.x						(template.x() + 480)
		.y						(template.y())
		.width					(89)
		.content				(tax_base_txt)
		.color					(BLACK);
		
		PdfText tax_base = builder.build();
		template.down(12);
		
		builder
		.x						(template.x() + 400)
		.y						(template.y())
		.width					(80)
		.content				(tax_title_txt)
		.color					(GRAY)
		.horizontal_alignment	(RIGHT);
		
		PdfText tax_title = builder.build();
		
		builder
		.x						(template.x() + 480)
		.y						(template.y())
		.width					(89)
		.content				(tax_txt)
		.color					(BLACK);
		
		PdfText tax = builder.build();	
		template.down(12);
		
		builder
		.x						(template.x() + 280)
		.y						(template.y())
		.width					(200)
		.content				(tax_title_txt)
		.font					(HELVETICA_BOLD)
		.font_size				(template.fontsize + 1)
		.horizontal_alignment	(RIGHT);
		
		PdfText total_amount_title = builder.build();
		
		builder
		.x						(template.x() + 480)
		.y						(template.y())
		.width					(89)
		.content				(tax_txt);
		
		PdfText total_amount = builder.build();	
		template.down(30);
		
		tax_base_title.draw();
		tax_base.draw();
		
		tax_title.draw();
		tax.draw();
		
		total_amount_title.draw();
		total_amount.draw();
		
	}
	
	
	private static void draw_conditions(BudgetTemplate template) {
		
		String conditions_title_txt = "CONDICIONES ECONÓMICAS";
		
		PdfTextBuilder builder = new PdfTextBuilder();
		
		builder
		.x						(template.x())
		.y						(template.y())
		.width					(575)
		.height					(15)
		.stream					(template.contents)
		.content				(conditions_title_txt)
		.color					(BLACK)
		.font					(HELVETICA)
		.font_size				(template.fontsize)
		.horizontal_alignment	(LEFT);
		
		PdfText conditions_title = builder.build();
		conditions_title.square(LIGHT_GRAY);
		conditions_title.draw();
		template.down(20);
		
		template.budget.getTerms().orElse(new ArrayList<Term>()).stream().forEach(term ->{
			
			if(template.jump())	new_page(template);			
			String term_txt = term.getTitle() + " : " +  term.getDescription();
			
			builder
			.x						(template.x())
			.y						(template.y())
			.width					(570)
			.height					(15)
			.stream					(template.contents)
			.content				(term_txt)
			.color					(BLACK)
			.font					(HELVETICA)
			.line_spacing			(5f)
			.font_size				(template.fontsize - 2)
			.horizontal_alignment	(JUSTIFY);
			
			PdfText text = builder.build();
			int jumping_line = text.draw_multiple(template.y_limit);
			
			if(jumping_line != -1) {
				new_page(template);
				text.restart(template.contents,jumping_line, template.y()).draw_multiple(template.y_limit);
			}
			template.y(text.y() - 10);
			
		});
		
	}

	@Override
	public String toString() {
		return "BudgetTemplate :\t\n{ \n\tbudget: \t\t" + budget + ", \n\tx: \t\t" + x() + ", \n\ty: \t\t" + y()
				+ ", \n\tpage: \t\t" + page + ", \n\tdoc: \t\t" + doc + ", \n\tcontents: \t\t" + contents
				+ ", \n\twords: \t\t" + words + ", \n\tout: \t\t" + out + ", \n\tlang: \t\t" + lang
				+ ", \n\ty_limit: \t\t" + y_limit + ", \n\tfont: \t\t" + font + ", \n\tfontsize: \t\t" + fontsize
				+ ", \n\tprimary: \t\t" + primary + ", \n\tsecondary: \t\t" + secondary + "\n}";
	}

	public static void new_page(BudgetTemplate template) {
		try {
			PdfTextBuilder builder = new PdfTextBuilder();
			builder
			.x						(560)
			.y						(10)
			.width					(10)
			.stream					(template.contents)
			.content				(template.page + "")
			.color					(GRAY)
			.font					(HELVETICA)
			.font_size				(template.fontsize)
			.horizontal_alignment	(RIGHT);
			
			
			PdfText page = builder.build();
			page.draw();
			template.new_page(VERTICAL);
			template.y(790);

		} 
		catch (Exception e) {e.printStackTrace();}
	}
}


