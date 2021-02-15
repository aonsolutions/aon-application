package com.esferalia.aon.in.payroll.pdf.creators.budget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfPage;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfText;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.PAGE_TYPE;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.creators.budget.beans.Budget;
import com.esferalia.aon.in.payroll.pdf.creators.budget.beans.Budget_item;
import com.esferalia.aon.in.payroll.pdf.creators.budget.beans.Client_data;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;

public class BudgetTemplate {
	
	static float fontSize = 9f;
	static String filename = "./budget.pdf";
	static float limit = 100;
	
	PDPageContentStream contents;
	PDDocument doc;
	Budget budget;
	Locale lang;
	
	float x;
	float y;
	
	ResourceBundle words;
	
	public void print(String out,Budget bg,Optional<Locale> language) throws CanNotCreatePdfException {
		try (PDDocument doc = new PDDocument()) {
			
			this.lang = language.orElse(new Locale("Es"));
			this.words = ResourceBundle.getBundle("com.esferalia.aon.in.payroll.pdf.creators.budget.bundles.BudgetBundle",lang);
			this.budget = bg;
			this.doc = doc;
			
			PdfPage page = new PdfPage(PAGE_TYPE.VERTICAL);
			this.doc.addPage(page.getPage());
			this.contents = page.stream(doc);	
			
			draw_client_info();
			draw_conditions();
			draw_products();
			
			contents.close();
			doc.save(filename);
		}catch (Exception e) {throw new CanNotCreatePdfException(e);}
	}

	private void draw_client_info() {
		
		x = 10;
		y = 800;

		//PDF DATA
		System.out.println(this.budget);
		Client_data client = this.budget.getClient().orElse(new Client_data(null, null, null, null, null, null, null, null, null, null)); 
		
		String number_title_txt = words.getString("NUMBER") + ":";
		String number_txt = budget.getBudget_number().orElse("");
		
		String date_title_txt = words.getString("DATE") + ":";
		String date_txt = PdfFormats.formatDate(new Date(),words.getString("DATE FORMAT")).orElse("");
		
		String client_data_title_txt =  words.getString("CLIENT DATA").toUpperCase();
		
		String enterprise_name_title_txt = words.getString("ENTERPRISE NAME") + ":";
		String enterprise_name_txt = client.getBusiness_name().orElse("");
		
		String nif_title_txt = words.getString("NIF") + ":";
		String nif_txt = client.getNif().orElse("");
		
		String address_title_txt = words.getString("ADDRESS") + ":";
		String address_txt = client.getAddress().orElse("");
		
		String city_title_txt = words.getString("CITY") + ":";
		String city_txt = client.getCity().orElse("");
		
		String postal_code_title_txt = words.getString("POSTAL CODE") + ":";
		String postal_code_txt = client.getPostal_code().orElse("");
		
		String province_title_txt = words.getString("PROVINCE") + ":";
		String province_txt = client.getProvince().orElse("");
		
		String phone_title_txt = words.getString("PHONE") + ":";
		String phone_txt = client.getPhone().orElse("");
		
		String mobile_title_txt = words.getString("MOBILE") + ":";
		String mobile_txt = client.getMobile().orElse("");
		
		String email_title_txt = words.getString("EMAIL") + ":";
		String email_txt = client.getEmail().orElse("");
		
		String contact_title_txt = words.getString("CONTACT") + ":";
		String contact_txt = client.getContact().orElse("");
		
		
		//PDF ELEMENTS
		PdfText number_title = 	new PdfText(x, y, 100, 20, 5, 7, contents,number_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize, TEXT_ALIGNMENT.LEFT);		
		PdfText number = 		new PdfText(x + 100, y, 100, 20, 5, 7, contents,number_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.LEFT);
		y -= 15;
		
		PdfText date_title = 	new PdfText(x, y, 100, 20, 5, 7, contents,date_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize, TEXT_ALIGNMENT.LEFT);		
		PdfText date = 			new PdfText(x + 100, y, 100, 20, 5, 7, contents,date_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.LEFT);
		y -= 25;

		PdfText client_data_title = new PdfText(x, y, 575, 20, 5, 7, contents, client_data_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.LEFT);
		y -= 25;
		
		PdfText enterprise_name_title = new PdfText(x, y, 75, 20, 5, 7, contents, enterprise_name_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText enterprise_name =  		new PdfText(x + 75, y, 300, 20, 5, 7, contents, enterprise_name_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText nif_title =  			new PdfText(x + 375, y, 55, 20, 5, 7, contents, nif_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText nif =  					new PdfText(x + 430, y, 143, 20, 5, 7, contents, nif_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.LEFT);
		y -= 15;
		
		PdfText address_title = new PdfText(x, y, 75, 20, 5, 7, contents, address_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText address =  		new PdfText(x + 75, y, 300, 20, 5, 7, contents, address_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText city_title =  	new PdfText(x + 375, y, 55, 20, 5, 7, contents, city_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText city =  		new PdfText(x + 430, y, 143, 20, 5, 7, contents, city_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.LEFT);
		y -= 15;
		
		PdfText postal_code_title = new PdfText(x, y, 75, 20, 5, 7, contents, postal_code_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText postal_code =  		new PdfText(x + 75, y, 90, 20, 5, 7, contents, postal_code_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText province_title =  	new PdfText(x + 165, y, 55, 20, 5, 7, contents, province_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText province =  		new PdfText(x + 220, y, 155, 20, 5, 7, contents, province_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText phone_title =  		new PdfText(x + 375, y, 55, 20, 5, 7, contents, phone_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText phone =  			new PdfText(x + 430, y, 143, 20, 5, 7, contents, phone_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.LEFT);
		y -= 15;
		
		PdfText mobile_title =		new PdfText(x, y, 75, 20, 5, 7, contents, mobile_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText mobile =	  		new PdfText(x + 75, y, 90, 20, 5, 7, contents, mobile_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText email_title =  		new PdfText(x + 165, y, 55, 20, 5, 7, contents, email_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText email =  			new PdfText(x + 220, y, 155, 20, 5, 7, contents, email_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText contact_title =  	new PdfText(x + 375, y, 55, 20, 5, 7, contents, contact_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText contact =  			new PdfText(x + 430, y, 143, 20, 5, 7, contents, contact_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.LEFT);
		y -= 30;
		
		//DRAW 
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
	
	private void draw_products() {
		
		String currency = " \u20AC";
		
		//PDF DATA
		String product_description_title_txt = words.getString("PRODUCT DESCRIPTION").toUpperCase();
		String product_service_title_txt = words.getString("PRODUCT/SERVICE");
		String amount_title_txt = words.getString("AMOUNT");
		String tax_base_title_txt = words.getString("TAX BASE") + ":";
		String tax_base_txt = budget.getTax_base().orElse(0.00) + currency;
		String tax_title_txt = budget.getTax_percent().orElse(0.00) + words.getString("TAX") + budget.getTax_add().orElse(0.00);
		String tax_txt = budget.getTax_total().orElse(0.00) + currency;
		String total_amount_title_txt = words.getString("TOTAL AMOUNT") + ":";
		String total_amount_txt = budget.getBudget_total().orElse(0.00) + currency;
		
		//PDF ELEMENTS
		PdfText product_description_title = new PdfText(x, y, 575, 20, 5, 7, contents, product_description_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.LEFT);
		y -= 20;
		
		PdfText product_service_title = new PdfText(x, y, 200, 20, 5, 7, contents, product_service_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize, TEXT_ALIGNMENT.LEFT);
		PdfText amount_title = new PdfText(x+370, y, 200, 20, 5, 7, contents, amount_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, fontSize, TEXT_ALIGNMENT.RIGHT);
		y -= 8;
		
		//DRAW
		product_description_title.square(PdfColors.LIGHT_GRAY);
		product_description_title.draw();
		
		product_service_title.draw();
		amount_title.draw();
		
		
		//FOR EACH PRODUCT
		ArrayList<Budget_item> products = budget.getProducts().orElse(new ArrayList<Budget_item>());
		products.forEach( p ->{
			y -= 12;
			
			if(y <= 20) {
				try {this.contents.close();}
				catch (IOException e) {e.printStackTrace();}
				
				PdfPage page = new PdfPage(PAGE_TYPE.VERTICAL);
				this.doc.addPage(page.getPage());
				this.contents = page.stream(doc);	
				y = 800;
			}
			
			PdfText product = new PdfText(x, y, 200, 20, 5, 7, contents, p.getName().orElse(""), PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.LEFT);
			PdfText amount = new PdfText(x+370, y, 200, 20, 5, 7, contents,PdfFormats.to_latin_number(p.getPrice().orElse(0.00)) + currency, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.RIGHT);
				
			product.draw();
			amount.draw();
			
		});
		
		y-= 20;
		PdfText tax_base_title = new PdfText(x+400, y, 80, 20, 5, 7, contents, tax_base_title_txt, PdfColors.GRAY, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.RIGHT);
		PdfText tax_base = new PdfText(x+480, y, 89, 20, 5, 7, contents, tax_base_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.RIGHT);
		
		y -= 12;
		PdfText tax_title = new PdfText(x+400, y, 80, 20, 5, 7, contents, tax_title_txt, PdfColors.GRAY, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.RIGHT);
		PdfText tax = new PdfText(x+480, y, 89, 20, 5, 7, contents, tax_txt, PdfColors.BLACK, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.RIGHT);
		
		y-= 12;
		PdfText total_amount_title = new PdfText(x+400, y, 80, 20, 5, 5, contents, total_amount_title_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, 1 + fontSize, TEXT_ALIGNMENT.RIGHT);
		PdfText total_amount = new PdfText(x+480, y, 89, 20, 5, 5, contents, total_amount_txt, PdfColors.BLACK, PdfFonts.HELVETICA_BOLD, 1 + fontSize, TEXT_ALIGNMENT.RIGHT);

		
		tax_base_title.draw();
		tax_base.draw();
		
		tax_title.draw();
		tax.draw();
		
		total_amount_title.draw();
		total_amount.draw();
		
		
	}
	
	
	private void draw_conditions() {
		
		
		
		
	}
}


