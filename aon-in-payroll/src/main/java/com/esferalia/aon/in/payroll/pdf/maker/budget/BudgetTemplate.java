package com.esferalia.aon.in.payroll.pdf.maker.budget;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.GRAY;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.LIGHT_GRAY;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.toLatinNumber;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT.JUSTIFY;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT.LEFT;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT.RIGHT;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.PAGE_TYPE.VERTICAL;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfFile;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfText;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfText.PdfTextBuilder;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.maker.budget.bean.Budget;
import com.esferalia.aon.in.payroll.pdf.maker.budget.bean.BudgetItem;
import com.esferalia.aon.in.payroll.pdf.maker.budget.bean.ClientData;
import com.esferalia.aon.in.payroll.pdf.maker.budget.bean.ClientData.ClientDataBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
/**
 * Class to print budget PDF files
 * @author akrck02
 * @version 0.1
 */
public class BudgetTemplate extends PdfFile {

	private Budget budget;

	/*
	 * TODO - THINGS TO FIX
	 * 
	 * 	1. Box have literally no margin :(
	 *  2. Text with overflow --> use drawTextLines(text); instead , is a PDFFile  Object native method :)
	 * 	3. Strong test bundle might be created before releasing 
	 * 
	 */
	
	
	public BudgetTemplate(
			float x, float y, PDDocument doc, ResourceBundle words, OutputStream out, Budget budget, float limitY
	) {
		super(x, y, doc, words, out, limitY);
		this.budget = budget;
	}

	public static void print(OutputStream out, BudgetPrintConfiguration config) throws CanNotCreatePdfException {
		BudgetTemplate template = null;
		try
		{

			Budget		   bg	 = config.getBudget();
			ResourceBundle words = ResourceBundle.getBundle(
					"com.esferalia.aon.in.payroll.pdf.maker.budget.bundle.BudgetBundle", config.getLanguage());
			template = new BudgetTemplate(10, 810, new PDDocument(), words, out, bg, 30);
			template.setDefaults(HELVETICA, 10f, BLACK, PdfColors.GRAY);
			template.limitY = 20;

			drawClientInfo(template);
			drawProducts(template);
			drawConditions(template);

			PdfTextBuilder builder = new PdfTextBuilder();

			builder.x(560).y(10).width(10).height(15).stream(template.contents).content(template.page + "").color(GRAY)
					.font(HELVETICA).fontSize(template.fontsize).horizontalAlignment(RIGHT);

			PdfText page = builder.build();
			page.draw();

			template.print();
		} catch (Exception e)
		{
			if (template != null)
				try
				{
					template.close();
				} catch (IOException ignored)
				{
				}
			throw new CanNotCreatePdfException(e);
		}
	}

	private static void drawClientInfo(BudgetTemplate template) throws IOException {

		template.newPage(VERTICAL);
		ClientData client = template.budget.getClient(new ClientDataBuilder().build());

		String numberTitleTxt		  = template.text("NUMBER") + ":";
		String numberTxt			  = template.budget.getBudgetNumber("");
		String dateTitleTxt			  = template.text("DATE") + ":";
		String dateTxt				  = PdfFormats.formatDate(new Date(), template.text("DATE FORMAT")).orElse("");
		String clientDataTitleTxt	  = template.text("CLIENT DATA").toUpperCase();
		String enterpriseNameTitleTxt = template.text("ENTERPRISE NAME") + ":";
		String enterpriseNameTxt	  = client.getEnterpriseName("");
		String nifTitleTxt			  = template.text("NIF") + ":";
		String nifTxt				  = client.getNif("");
		String addressTitleTxt		  = template.text("ADDRESS") + ":";
		String addressTxt			  = client.getAddress("");
		String cityTitleTxt			  = template.text("CITY") + ":";
		String cityTxt				  = client.getCity("");
		String postalCodeTitleTxt	  = template.text("POSTAL CODE") + ":";
		String postalCodeTxt		  = client.getPostalCode("");
		String provinceTitleTxt		  = template.text("PROVINCE") + ":";
		String provinceTxt			  = client.getProvince("");
		String phoneTitleTxt		  = template.text("PHONE") + ":";
		String phoneTxt				  = client.getPhone("");
		String mobileTitleTxt		  = template.text("MOBILE") + ":";
		String mobileTxt			  = client.getMobile("");
		String emailTitleTxt		  = template.text("EMAIL") + ":";
		String emailTxt				  = client.getEmail("");
		String contactTitleTxt		  = template.text("CONTACT") + ":";
		String contactTxt			  = client.getContact("");

		PdfTextBuilder builder = new PdfTextBuilder();

		builder.x(template.x()).y(template.y()).width(100).height(15).stream(template.contents).content(numberTitleTxt)
				.color(BLACK).font(HELVETICA_BOLD).fontSize(template.fontsize).horizontalAlignment(LEFT);
		PdfText numberTitle = builder.build();

		builder.x(template.x() + 100).content(numberTxt).font(HELVETICA);
		PdfText number = builder.build();
		template.down(15);

		builder.x(template.x()).y(template.y()).content(dateTitleTxt).font(HELVETICA_BOLD);
		PdfText dateTitle = builder.build();

		builder.x(template.x() + 100).content(dateTxt).font(HELVETICA);
		PdfText date = builder.build();
		template.down(25);

		builder.x(template.x()).y(template.y()).width(575).content(clientDataTitleTxt).font(HELVETICA);

		PdfText clientDataTitle = builder.build();
		template.down(25);

		builder.x(template.x()).y(template.y()).width(75).content(enterpriseNameTitleTxt).font(HELVETICA_BOLD);
		PdfText enterpriseNameTitle = builder.build();

		builder.x(template.x() + 75).y(template.y()).width(300).content(enterpriseNameTxt).font(HELVETICA);
		PdfText enterpriseName = builder.build();

		builder.x(template.x() + 375).y(template.y()).width(55).content(nifTitleTxt).font(HELVETICA_BOLD);
		PdfText nifTitle = builder.build();

		builder.x(template.x() + 430).y(template.y()).width(143).content(nifTxt).font(HELVETICA);
		PdfText nif = builder.build();
		template.down(15);

		builder.x(template.x()).y(template.y()).width(75).content(addressTitleTxt).font(HELVETICA_BOLD);
		PdfText addressTitle = builder.build();

		builder.x(template.x() + 75).y(template.y()).width(300).content(addressTxt).font(HELVETICA);
		PdfText address = builder.build();

		builder.x(template.x() + 375).y(template.y()).width(55).content(cityTitleTxt).font(HELVETICA_BOLD);
		PdfText cityTitle = builder.build();

		builder.x(template.x() + 430).y(template.y()).width(143).content(cityTxt).font(HELVETICA);
		PdfText city = builder.build();
		template.down(15);

		builder.x(template.x()).y(template.y()).width(75).content(postalCodeTitleTxt).font(HELVETICA_BOLD);
		PdfText postalCodeTitle = builder.build();

		builder.x(template.x() + 75).y(template.y()).width(90).content(postalCodeTxt).font(HELVETICA);
		PdfText postalCode = builder.build();

		builder.x(template.x() + 165).y(template.y()).width(55).content(provinceTitleTxt).font(HELVETICA_BOLD);
		PdfText provinceTitle = builder.build();

		builder.x(template.x() + 220).y(template.y()).width(155).content(provinceTxt).font(HELVETICA);
		PdfText province = builder.build();

		builder.x(template.x() + 375).y(template.y()).width(55).content(phoneTitleTxt).font(HELVETICA_BOLD);
		PdfText phoneTitle = builder.build();

		builder.x(template.x() + 430).y(template.y()).width(143).content(phoneTxt).font(HELVETICA);
		PdfText phone = builder.build();
		template.down(15);

		builder.x(template.x()).y(template.y()).width(75).content(mobileTitleTxt).font(HELVETICA_BOLD);
		PdfText mobileTitle = builder.build();

		builder.x(template.x() + 75).y(template.y()).width(90).content(mobileTxt).font(HELVETICA);
		PdfText mobile = builder.build();

		builder.x(template.x() + 165).y(template.y()).width(55).content(emailTitleTxt).font(HELVETICA_BOLD);
		PdfText emailTitle = builder.build();

		builder.x(template.x() + 220).y(template.y()).width(155).content(emailTxt).font(HELVETICA);
		PdfText email = builder.build();

		builder.x(template.x() + 375).y(template.y()).width(55).content(contactTitleTxt).font(HELVETICA_BOLD);
		PdfText contactTitle = builder.build();

		builder.x(template.x() + 430).y(template.y()).width(143).content(contactTxt).font(HELVETICA);
		PdfText contact = builder.build();
		template.down(30);

		numberTitle.draw();
		number.draw();

		dateTitle.draw();
		date.draw();

		clientDataTitle.square(PdfColors.LIGHT_GRAY);
		clientDataTitle.draw();

		enterpriseNameTitle.draw();
		enterpriseName.draw();

		nifTitle.draw();
		nif.draw();

		addressTitle.draw();
		address.draw();

		cityTitle.draw();
		city.draw();

		postalCodeTitle.draw();
		postalCode.draw();

		provinceTitle.draw();
		province.draw();

		phoneTitle.draw();
		phone.draw();

		mobileTitle.draw();
		mobile.draw();

		emailTitle.draw();
		email.draw();

		contactTitle.draw();
		contact.draw();

	}

	private static void drawProducts(BudgetTemplate template) {

		String currency = " \u20AC";
		template.down(10);

		String productDescriptionTitleTxt = template.text("PRODUCT DESCRIPTION").toUpperCase();
		String productServiceTitleTxt	  = template.text("PRODUCT/SERVICE");
		String amountTitleTxt			  = template.text("AMOUNT");
		String taxBaseTitleTxt			  = template.text("TAX BASE") + ":";
		String taxBaseTxt				  = template.budget.getTaxBase(0.00) + currency;
		String taxTitleTxt				  = template.budget.getTaxPercent(0.00) + template.text("TAX")
				+ template.budget.getTaxAdd(0.00);

		String taxTxt			   = template.budget.getTaxTotal(0.00) + currency;
		String totalAmountTitleTxt = template.text("TOTAL AMOUNT") + ":";
		String totalAmountTxt	   = template.budget.getBudgetTotal(0.00) + currency;

		PdfTextBuilder builder = new PdfTextBuilder();

		builder.x(template.x()).y(template.y()).width(575).height(15).stream(template.contents)
				.content(productDescriptionTitleTxt).color(BLACK).font(HELVETICA).fontSize(template.fontsize)
				.horizontalAlignment(LEFT);

		PdfText productDescriptionTitle = builder.build();
		template.down(20);

		builder.x(template.x()).y(template.y()).width(200).stream(template.contents).content(productServiceTitleTxt)
				.font(HELVETICA_BOLD);
		PdfText productServiceTitle = builder.build();

		builder.x(template.x() + 370).y(template.y()).stream(template.contents).content(amountTitleTxt)
				.font(HELVETICA_BOLD).horizontalAlignment(RIGHT);
		PdfText amountTitle = builder.build();
		template.down(8);

		productDescriptionTitle.square(LIGHT_GRAY);
		productDescriptionTitle.draw();

		productServiceTitle.draw();
		amountTitle.draw();

		ArrayList<BudgetItem> products = template.budget.getProducts();
		products.forEach(p ->
		{
			template.down(12);

			if (template.jump())
				newPage(template);

			builder.x(template.x()).y(template.y()).stream(template.contents).width(200).content(p.getName().orElse(""))
					.color(BLACK).font(HELVETICA).horizontalAlignment(LEFT);
			PdfText product = builder.build();

			builder.x(template.x() + 370).y(template.y()).content(toLatinNumber(p.getPrice().orElse(0.00)) + currency)
					.horizontalAlignment(RIGHT);

			PdfText amount = builder.build();

			product.draw();
			amount.draw();
		});

		template.down(20);

		builder.x(template.x() + 400).y(template.y()).width(80).content(taxBaseTitleTxt).color(GRAY)
				.horizontalAlignment(RIGHT);

		PdfText taxBaseTitle = builder.build();

		builder.x(template.x() + 480).y(template.y()).width(89).content(taxBaseTxt).color(BLACK);

		PdfText taxBase = builder.build();
		template.down(12);

		builder.x(template.x() + 400).y(template.y()).width(80).content(taxTitleTxt).color(GRAY)
				.horizontalAlignment(RIGHT);

		PdfText taxTitle = builder.build();

		builder.x(template.x() + 480).y(template.y()).width(89).content(taxTxt).color(BLACK);

		PdfText tax = builder.build();
		template.down(12);

		builder.x(template.x() + 280).y(template.y()).width(200).content(taxTitleTxt).font(HELVETICA_BOLD)
				.fontSize(template.fontsize + 1).horizontalAlignment(RIGHT);

		PdfText totalAmountTitle = builder.build();

		builder.x(template.x() + 480).y(template.y()).width(89).content(taxTxt);

		PdfText totalAmount = builder.build();
		template.down(30);

		taxBaseTitle.draw();
		taxBase.draw();

		taxTitle.draw();
		tax.draw();

		totalAmountTitle.draw();
		totalAmount.draw();

	}

	private static void drawConditions(BudgetTemplate template) {

		String conditionsTitleTxt = "CONDICIONES ECONÓMICAS";

		PdfTextBuilder builder = new PdfTextBuilder();
		builder.x(template.x()).y(template.y()).width(575).height(15).stream(template.contents)
				.content(conditionsTitleTxt).color(BLACK).font(HELVETICA).fontSize(template.fontsize)
				.horizontalAlignment(LEFT);

		PdfText conditionsTitle = builder.build();
		conditionsTitle.square(LIGHT_GRAY);
		conditionsTitle.draw();
		template.down(20);

		template.budget.getTerms().stream().forEach(term ->
		{

			if (template.jump())
				newPage(template);
			
			String termTxt = term.getTitle() + " : " + term.getDescription();
			builder.x(template.x()).y(template.y()).width(570).height(15).stream(template.contents).content(termTxt)
					.color(BLACK).font(HELVETICA).lineSpacing(4f).fontSize(template.fontsize - 2)
					.horizontalAlignment(JUSTIFY);

			PdfText	text		= builder.build();
			float y = template.drawTextLines(text);		
			template.y(y - 10);

		});

	}
	
	

	@Override
	public void drawHeader() {
		super.drawHeader();
	}

	public static void newPage(BudgetTemplate template) {
		try
		{
			PdfTextBuilder builder = new PdfTextBuilder();
			builder.x(560).y(10).width(10).stream(template.contents).content(template.page + "").color(GRAY)
					.font(HELVETICA).fontSize(template.fontsize).horizontalAlignment(RIGHT);

			PdfText page = builder.build();
			page.draw();
			template.newPage(VERTICAL);
			template.y(790);

		} catch (Exception ignored)
		{
		}
	}
}
