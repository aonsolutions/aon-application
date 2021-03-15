package com.esferalia.aon.in.payroll.pdf.creators.budget;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.GRAY;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.LIGHT_GRAY;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats.to_latin_number;
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
			
			PdfText page = new PdfText(560, 10, 10, 20, template.contents, template.page + "", GRAY, HELVETICA, template.fontsize, RIGHT); 
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
		
		

		System.out.println("--------------RUNNING PDF MAKER-------------");
		System.out.println("FONT SIZE: \t" + template.fontsize);
		System.out.println("FONT FAMILY: \t" + template.font);
	
		PdfText number_title = 			new PdfText(template.x(), 			template.y(), 5, 100, 20, template.contents,	number_title_txt, 			BLACK, HELVETICA_BOLD, 	template.fontsize, LEFT);		
		PdfText number = 				new PdfText(template.x() + 100, 	template.y(), 5, 100, 20, template.contents,	number_txt, 				BLACK, HELVETICA, 		template.fontsize, LEFT);
		template.down(15);
		
		PdfText date_title = 			new PdfText(template.x(), 			template.y(), 5, 100, 20, template.contents,	date_title_txt, 			BLACK, HELVETICA_BOLD, 	template.fontsize, LEFT);		
		PdfText date = 					new PdfText(template.x() + 100, 	template.y(), 5, 100, 20, template.contents,	date_txt, 					BLACK, HELVETICA, 		template.fontsize, LEFT);
		template.down(25);
		
		PdfText client_data_title = 	new PdfText(template.x(), 			template.y(), 5, 575, 20, template.contents, 	client_data_title_txt, 		BLACK, HELVETICA, 		template.fontsize, LEFT);
		template.down(25);
		
		PdfText enterprise_name_title = new PdfText(template.x(), 			template.y(), 5, 75,  20, template.contents, 	enterprise_name_title_txt, 	BLACK, HELVETICA_BOLD, 	template.fontsize, LEFT);
		PdfText enterprise_name =  		new PdfText(template.x() + 75, 		template.y(), 5, 300, 20, template.contents, 	enterprise_name_txt, 		BLACK, HELVETICA, 		template.fontsize, LEFT);
		PdfText nif_title =  			new PdfText(template.x() + 375, 	template.y(), 5, 55,  20, template.contents, 	nif_title_txt, 				BLACK, HELVETICA_BOLD, 	template.fontsize, LEFT);
		PdfText nif =  					new PdfText(template.x() + 430, 	template.y(), 5, 143, 20, template.contents, 	nif_txt, 					BLACK, HELVETICA, 		template.fontsize, LEFT);
		template.down(15);
	
		PdfText address_title = 		new PdfText(template.x(), 			template.y(), 5, 75,  20, template.contents, 	address_title_txt, 			BLACK, HELVETICA_BOLD, 	template.fontsize, LEFT);
		PdfText address =  				new PdfText(template.x() + 75, 		template.y(), 5, 300, 20, template.contents, 	address_txt, 				BLACK, HELVETICA, 		template.fontsize, LEFT);
		PdfText city_title =  			new PdfText(template.x() + 375, 	template.y(), 5, 55,  20, template.contents, 	city_title_txt, 			BLACK, HELVETICA_BOLD, 	template.fontsize, LEFT);
		PdfText city =  				new PdfText(template.x() + 430, 	template.y(), 5, 143, 20, template.contents, 	city_txt, 					BLACK, HELVETICA, 		template.fontsize, LEFT);
		template.down(15);
		
		
		PdfText postal_code_title = 	new PdfText(template.x(),       	template.y(), 5, 75,  20, template.contents, 	postal_code_title_txt, 		BLACK, HELVETICA_BOLD, 	template.fontsize, LEFT);
		PdfText postal_code =  			new PdfText(template.x() + 75,  	template.y(), 5, 90,  20, template.contents, 	postal_code_txt, 			BLACK, HELVETICA, 		template.fontsize, LEFT);
		PdfText province_title =  		new PdfText(template.x() + 165, 	template.y(), 5, 55,  20, template.contents, 	province_title_txt, 		BLACK, HELVETICA_BOLD, 	template.fontsize, LEFT);
		PdfText province =  			new PdfText(template.x() + 220, 	template.y(), 5, 155, 20, template.contents, 	province_txt, 				BLACK, HELVETICA, 		template.fontsize, LEFT);
		PdfText phone_title =  			new PdfText(template.x() + 375, 	template.y(), 5, 55,  20, template.contents, 	phone_title_txt, 			BLACK, HELVETICA_BOLD, 	template.fontsize, LEFT);
		PdfText phone =  				new PdfText(template.x() + 430, 	template.y(), 5, 143, 20, template.contents, 	phone_txt, 					BLACK, HELVETICA, 		template.fontsize, LEFT);
		template.down(15);
		
		PdfText mobile_title =			new PdfText(template.x(), 			template.y(), 5, 75,  20, template.contents, 	mobile_title_txt, 			BLACK, HELVETICA_BOLD, 	template.fontsize, LEFT);
		PdfText mobile =	  			new PdfText(template.x() + 75, 		template.y(), 5, 90,  20, template.contents, 	mobile_txt, 				BLACK, HELVETICA, 		template.fontsize, LEFT);
		PdfText email_title =  			new PdfText(template.x() + 165, 	template.y(), 5, 55,  20, template.contents, 	email_title_txt, 			BLACK, HELVETICA_BOLD, 	template.fontsize, LEFT);
		PdfText email =  				new PdfText(template.x() + 220, 	template.y(), 5, 155, 20, template.contents, 	email_txt, 					BLACK, HELVETICA,		template.fontsize, LEFT);
		PdfText contact_title =  		new PdfText(template.x() + 375, 	template.y(), 5, 55,  20, template.contents, 	contact_title_txt, 			BLACK, HELVETICA_BOLD, 	template.fontsize, LEFT);
		PdfText contact =  				new PdfText(template.x() + 430, 	template.y(), 5, 143, 20, template.contents, 	contact_txt, 				BLACK, HELVETICA, 		template.fontsize, LEFT);
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
		
		String product_description_title_txt = 	template.text("PRODUCT DESCRIPTION").toUpperCase();
		String product_service_title_txt = 		template.text("PRODUCT/SERVICE");
		String amount_title_txt = 				template.text("AMOUNT");
		String tax_base_title_txt = 			template.text("TAX BASE") + ":";
		String tax_base_txt = 					template.budget.getTax_base().orElse(0.00) 		+ currency;
		String tax_title_txt = 					template.budget.getTax_percent().orElse(0.00) 	+ template.text("TAX") + template.budget.getTax_add().orElse(0.00);
		String tax_txt = 						template.budget.getTax_total().orElse(0.00) 	+ currency;
		String total_amount_title_txt = 		template.text("TOTAL AMOUNT") + ":";
		String total_amount_txt = 				template.budget.getBudget_total().orElse(0.00) 	+ currency;
		
		PdfText product_description_title = new PdfText(template.x(), 			template.y(), 5, 575, 20, template.contents, product_description_title_txt, 	BLACK, HELVETICA, 		template.fontsize, LEFT);
		template.down(20);
		
		PdfText product_service_title = 	new PdfText(template.x(), 			template.y(), 5, 200, 20, template.contents, product_service_title_txt, 		BLACK, HELVETICA_BOLD, 	template.fontsize, LEFT);
		PdfText amount_title = 				new PdfText(template.x() + 370, 	template.y(), 5, 200, 20, template.contents, amount_title_txt, 				BLACK, HELVETICA_BOLD, 	template.fontsize, RIGHT);
		template.down(8);
		
		product_description_title.square(LIGHT_GRAY);
		product_description_title.draw();
		
		product_service_title.draw();
		amount_title.draw();
		
		
		ArrayList<Budget_item> products = template.budget.getProducts().orElse(new ArrayList<Budget_item>());
		products.forEach( p ->{
			template.down(12);
			
			if(template.jump())
				try {
					PdfText page = new PdfText(560, 10, 10, 20, template.contents, template.page + "", GRAY, HELVETICA, template.fontsize, RIGHT); 
					page.draw();
					
					template.new_page(VERTICAL);
					template.y(790);
				} 
				catch (IOException e) {}
			
			PdfText product = 	new PdfText(template.x(), 			template.y(), 5, 200, 20, template.contents, p.getName().orElse(""), BLACK, HELVETICA, template.fontsize, LEFT);
			PdfText amount =	new PdfText(template.x() + 370, 	template.y(), 5, 200, 20, template.contents, to_latin_number(p.getPrice().orElse(0.00)) + currency, 	BLACK, HELVETICA, template.fontsize, RIGHT);
				
			product.draw();
			amount.draw();	
		});
		
		template.down(20);
		PdfText tax_base_title = 		new PdfText(template.x() + 400, template.y(), 5, 80, 20, template.contents, tax_base_title_txt, 	GRAY, 	HELVETICA, 		template.fontsize,		RIGHT);
		PdfText tax_base = 				new PdfText(template.x() + 480, template.y(), 5, 89, 20, template.contents, tax_base_txt, 			BLACK,	HELVETICA, 		template.fontsize, 		RIGHT);
		
		template.down(12);
		PdfText tax_title = 			new PdfText(template.x() + 400, template.y(), 5, 80, 20, template.contents, tax_title_txt, 			GRAY, 	HELVETICA, 		template.fontsize, 		RIGHT);
		PdfText tax = 					new PdfText(template.x() + 480, template.y(), 5, 89, 20, template.contents, tax_txt, 				BLACK, 	HELVETICA, 		template.fontsize,		RIGHT);
		
		template.down(12);
		PdfText total_amount_title = 	new PdfText(template.x() + 400, template.y(), 5, 80, 20, template.contents, total_amount_title_txt, BLACK, 	HELVETICA_BOLD, 1 + template.fontsize, 	RIGHT);
		PdfText total_amount = 			new PdfText(template.x() + 480, template.y(), 5, 89, 20, template.contents, total_amount_txt, 		BLACK, 	HELVETICA_BOLD, 1 + template.fontsize, 	RIGHT);
		
		template.down(30);
		
		tax_base_title.draw();
		tax_base.draw();
		
		tax_title.draw();
		tax.draw();
		
		total_amount_title.draw();
		total_amount.draw();
		
	}
	
	
	private static void draw_conditions(BudgetTemplate temp) {
		String conditions_title_txt = "CONDICIONES ECONÓMICAS";
		PdfText conditions_title = new PdfText(temp.x(), temp.y(), 5, 575, 20, temp.contents, conditions_title_txt, BLACK, HELVETICA, temp.fontsize, LEFT);
		conditions_title.square(LIGHT_GRAY);
		conditions_title.draw();
		temp.down(20);
		
		temp.budget.getTerms().orElse(new ArrayList<Term>()).stream().forEach(term ->{
			
			if(temp.jump())
				try {
					
					PdfText page = new PdfText(560, 10, 10, 20, temp.contents, temp.page + "", GRAY, HELVETICA, temp.fontsize, RIGHT); 
					page.draw();
					
					temp.new_page(VERTICAL);
					temp.y(790);
				} 
				catch (IOException e) {}
			
			String term_txt = term.getTitle() + " : " +  term.getDescription();
			
			PdfText text = new PdfText(temp.x(), temp.y(), 5, 570, 20, temp.contents, term_txt, BLACK, HELVETICA, temp.fontsize -2, LEFT);
			text.draw();
			temp.y(text.y() - 10);
			
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
}


