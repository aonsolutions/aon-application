package com.esferalia.aon.in.payroll.pdf.maker.invoice;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.formatDate;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.toLatinNumber;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.DataToolkit.safeString;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.croppedString;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.getContent;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.StringToolkit.joinCharacterList;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.jump;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.log;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.start;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Separator.ARROW;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Status.COMPARE;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Status.GENERATE;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Status.GET;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Status.INFO;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Status.SUCCESS;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Status.TEST;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.ADDRESS;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.ADDRESS_LINE_TWO;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_DESCRIPTION;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_DISCOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_PRICE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_TOTAL;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_BANK_ACCOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_DATE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_PAY_METHOD;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.INVOICE_DATE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.INVOICE_TOTAL;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.NIF;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.REFERENCE_NUMBER;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.REGISTRY_NAME;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.TAX_BASE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.TAX_PERCENTAGE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.TAX_QUOTE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.TAX_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

<<<<<<< Updated upstream
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.maker.Logger;
import com.esferalia.aon.in.payroll.pdf.maker.Logger.Separator;
import com.esferalia.aon.in.payroll.pdf.maker.Logger.Status;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
=======
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.exception.JsonParseException;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.Invoice;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.InvoiceEntry;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.InvoiceFinance;
import com.esferalia.aon.in.payroll.pdf.maker.invoice.bean.InvoiceTax;
>>>>>>> Stashed changes
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.TaxType;

import bsh.Console;


public class InvoiceTest {

	@Rule
	public TestName testName = new TestName();
	
	@Before
	public void prepare() {
		Logger.start(testName.getMethodName());
	}
	
	
	@Test
	public void DataIntegrationTest() {
		
		log(GENERATE, "Creating invoice data");
		
		String address = "Calle de los tejeros alados en la roca veraz de los paramos";
		String addressNumber  = "12";
		String addressProvince = "Almeria";
		String addressTown = "Andrajosía de vera";
		String addressZIP = "191238";
		
		String registryName = "AON SOLUTIONS MASTER XL PLUS MASTER PRO S GAMING MAX";
		String referenceCode = "8901389091AAA";
		double total = 19216376.75;
		String registryDocument = "1826738401Y";
		Date issueDate = new Date();
		
		log(GENERATE, "Address", address);
		log(GENERATE, "Address number", addressNumber);
		log(GENERATE, "Address province", addressProvince);
		log(GENERATE, "Address town", addressTown);
		log(GENERATE, "Address ZIP", addressZIP);
		
		log(GENERATE, "Registry name", registryName);
		log(GENERATE, "Reference code", referenceCode);
		log(GENERATE, "Invoice total", total + "");
		log(GENERATE, "Registry document", registryDocument);
		log(GENERATE, "Date", issueDate + "");
		
		
		/** INVOICE BASIC DATA */
		Invoice invoice = new Invoice();		
		invoice.setAddress(address);
		invoice.setAddressNumber(addressNumber);
		invoice.setAddressProvince(addressProvince);
		invoice.setAddressTown(addressTown);
		invoice.setAddressZIP(addressZIP);
		
		invoice.setRegistryName(registryName);
		
		invoice.setTotal(total);
		invoice.setReferenceCode(referenceCode);
		invoice.setIssueDate(issueDate);
		invoice.setRegistryDocument(registryDocument);
		
		/** BREAKDOWNS */
		jump();
		log(GENERATE, "Creating breakdowns.");
		LinkedList<InvoiceBreakdown> breakdowns = new java.util.LinkedList<>();
		
		InvoiceBreakdown breakdownOne = new InvoiceBreakdown();
		breakdownOne.setBase(123890.12);
		breakdownOne.setPercentage(6.18);
		breakdownOne.setQuota(18230.123);
		breakdownOne.setSurcharge(23.123);
		breakdownOne.setTaxType(TaxType.RETENTION);
		breakdownOne.setSurchargeQuota(91.12);
		
		InvoiceBreakdown breakdownTwo = new InvoiceBreakdown();
		breakdownTwo.setBase(12546.99);
		breakdownTwo.setPercentage(01.05);
		breakdownTwo.setQuota(100.123);
		breakdownTwo.setSurcharge(2.123);
		breakdownTwo.setTaxType(TaxType.UNKNOWN);
		breakdownTwo.setSurchargeQuota(1.12);
		
		InvoiceBreakdown breakdownThree = new InvoiceBreakdown();
		breakdownThree.setBase(890.12);
		breakdownThree.setPercentage(21);
		breakdownThree.setQuota(430.123);
		breakdownThree.setSurcharge(0.1);
		breakdownThree.setTaxType(TaxType.VAT);
		breakdownThree.setSurchargeQuota(91.12);
		
		breakdowns.add(breakdownOne);
		breakdowns.add(breakdownTwo);
		breakdowns.add(breakdownThree);
		invoice.setBreakdown(breakdowns);		
		
		/** FINANCES */
		LinkedList<Finance> finances = new LinkedList<>();
		log(GENERATE, "Creating finances.");
		
		BankAccount accountOne = new BankAccount("ES9121000418450200051332");
		BankAccount accountTwo = new BankAccount("ES9121421818450296751237");
		BankAccount accountThree = new BankAccount("ES9999921999950296759997");
		
		Finance financeOne = new Finance();
		financeOne.setAdvance(true);
		financeOne.setAmount(39687.23);
		financeOne.setBankAccount(accountOne);
		financeOne.setPayMethodType(PayMethodType.DEBIT_CARD);
		financeOne.setDueDate(new Date());
		
		Finance financeTwo = new Finance();
		financeTwo.setAdvance(true);
		financeTwo.setAmount(1287.23);
		financeTwo.setBankAccount(accountTwo);
		financeTwo.setPayMethodType(PayMethodType.CASH_BASIS);
		financeTwo.setDueDate(new Date());
		
		Finance financeThree = new Finance();
		financeThree.setAdvance(true);
		financeThree.setAmount(3287.23);
		financeThree.setBankAccount(accountThree);
		financeThree.setPayMethodType(PayMethodType.CHEQUE);
		financeThree.setDueDate(new Date());
		
		Finance financeFour = new Finance();
		financeFour.setAdvance(true);
		financeFour.setAmount(87.23);
		financeFour.setBankAccount(accountThree);
		financeFour.setPayMethodType(PayMethodType.NEGOTIABLE_DOCUMENT);
		financeFour.setDueDate(new Date());
		
		finances.add(financeOne);
		finances.add(financeTwo);
		finances.add(financeThree);
		finances.add(financeFour);
		invoice.setFinances(finances);
		
		/** TAXES */
		LinkedList<InvoiceDetail> details = new LinkedList<>();
		log(GENERATE, "Creating details.");
		jump();
		
		InvoiceDetail detailOne = new InvoiceDetail();
		detailOne.setAccountCode("0192831092");
		detailOne.setDescription
		(
		    "Three Rings for the Elven-kings under the sky"
		  + " Seven for the Dwarf-lords in their halls of stone,"
		  + " Nine for Mortal Men doomed to die,"
		  + " One for the Dark Lord on his dark throne"
		  + " In the Land of Mordor where the Shadows lie."
		  + " One Ring to rule them all, One Ring to find them,"
		  + " One Ring to bring them all and in the darkness bind them"
		  + " In the Land of Mordor where the Shadows lie."
		);
		detailOne.setPrice(1239675601.12);
		detailOne.setDiscountExpression("97.19");
		detailOne.setQuantity(781212783);
		detailOne.setTaxableBase(712382113);
		
		InvoiceDetail detailTwo = new InvoiceDetail();
		detailTwo.setAccountCode("0192831092");
		detailTwo.setDescription("RTX 3080TI MAX PRO Founders edition");
		detailTwo.setPrice(1239675601.12);
		detailTwo.setDiscountExpression("1.19");
		detailTwo.setQuantity(1);
		detailTwo.setTaxableBase(712382113);
		
		
		details.add(detailOne);
		details.add(detailTwo);
		invoice.setDetails(details);
		
		/** PRINT CONFIGURATIONS */
		ByteArrayOutputStream os;
		try {
			
			OutputStream dos = new FileOutputStream("./InvoiceIntegrationTest.pdf");
			os = new ByteArrayOutputStream();
			byte[] qrCode = new InvoiceTest().getClass().getResourceAsStream("qr.png").readAllBytes();
			byte[] back = new InvoiceTest().getClass().getResourceAsStream("bg.jpg").readAllBytes();
			
			PrintInvoiceConfiguration config = new PrintInvoiceConfiguration();
			Attach attach = new Attach();
			attach.setData(back);
			
			config.setAdjustImage(false);
			config.setBackground(attach);
			config.setDetailed(true);
			config.setAdjustImage(true);
			config.setHeader(50);
			config.setFooter(50);
			

			InvoiceTemplate2.create(os, invoice, config, qrCode);
			InvoiceTemplate2.create(dos, invoice, config, qrCode);
			ByteArrayInputStream bis = new ByteArrayInputStream(os.toByteArray());
			
			PDDocument document = PDDocument.load(bis);
			PDPageTree pages = document.getPages();
			
			
			/** CHECKING PDF DATA **/
			String PDFaddress = "";
			String PDFaddressLineTwo = "";
			
			String PDFregistryName = "";
			String PDFreferenceCode = "";
			String PDFtotal = "";
			String PDFregistryDocument = "";
			String PDFissueDate = "";
			
			
			Optional<PDMarkedContent> addressMark = getContent(document, ADDRESS);
			Optional<PDMarkedContent> addressLineTwoMark = getContent(document, ADDRESS_LINE_TWO);
			Optional<PDMarkedContent> registryNameMark = getContent(document, REGISTRY_NAME);
			Optional<PDMarkedContent> referenceCodeMark = getContent(document, REFERENCE_NUMBER);
			Optional<PDMarkedContent> registryDocumentMark = getContent(document, NIF);
			Optional<PDMarkedContent> totalMark = getContent(document, INVOICE_TOTAL);
			Optional<PDMarkedContent> issueDateMark = getContent(document, INVOICE_DATE);
			
			if(addressMark.isPresent()) 
				PDFaddress = joinCharacterList(addressMark.get().getContents());
			
			
			if(addressLineTwoMark.isPresent()) 
				PDFaddressLineTwo = joinCharacterList(addressLineTwoMark.get().getContents());
			
			
			if(registryNameMark.isPresent()) 
				PDFregistryName = joinCharacterList(registryNameMark.get().getContents());
			
			
			if(referenceCodeMark.isPresent()) 
				PDFreferenceCode = joinCharacterList(referenceCodeMark.get().getContents());
			
			
			if(registryDocumentMark.isPresent()) 
				PDFregistryDocument = joinCharacterList(registryDocumentMark.get().getContents());
			
			
			if(totalMark.isPresent()) 
				PDFtotal = joinCharacterList(totalMark.get().getContents());
			
			
			if(issueDateMark.isPresent()) 
				PDFissueDate = joinCharacterList(issueDateMark.get().getContents());
			
				
			PDFaddress = removeSpecialCharacters(PDFaddress);			
			PDFaddressLineTwo = removeSpecialCharacters(PDFaddressLineTwo);			
			PDFregistryName = removeSpecialCharacters(PDFregistryName);			
			PDFreferenceCode = removeSpecialCharacters(PDFreferenceCode).replaceAll("Numero:", "").trim();			
			PDFregistryDocument = removeSpecialCharacters(PDFregistryDocument).replaceAll("N.I.F:", "").trim();			
			PDFtotal = removeSpecialCharacters(PDFtotal).trim();			
			PDFissueDate = removeSpecialCharacters(PDFissueDate).replaceAll("Fecha:", "").trim();
			
			start("Getting PDF data");
			log(GET, "ADDR", PDFaddress);
			log(GET, "ADDR2", PDFaddressLineTwo );
			log(GET, "NAME", PDFregistryName );
			log(GET, "CODE", PDFreferenceCode );
			log(GET, "DOCUMENT", PDFregistryDocument );
			log(GET, "TOTAL", PDFtotal );
			log(GET, "ISSUE", PDFissueDate );
			
			start("Comparing original/PDF data");
			assertPdfData("Address", croppedString(address, 230, HELVETICA, 9), PDFaddress);
			assertPdfData("Address line two", addressZIP + " " +  addressTown + " " + addressProvince, PDFaddressLineTwo);
			assertPdfData("Name", croppedString(registryName, 230, HELVETICA_BOLD, 12), PDFregistryName);
			assertPdfData("code", referenceCode, PDFreferenceCode);
			assertPdfData("NIF", registryDocument, PDFregistryDocument);
			assertPdfData("TOTAL", toLatinNumber(total), PDFtotal);
			assertPdfData("DATE", formatDate(issueDate, "dd/MM/yyyy").get(), PDFissueDate);
			
			
			/** Assert details **/
			start("Comparing original/PDF details");
			
			String detailAmount = "";
			String detailDescription = "";
			String detailPrice = "";
			String detailDiscount = "";
			String detailTotal = "";
	
			Optional<PDMarkedContent> detailAmountMark = getContent(document, 1 + DETAIL_AMOUNT);
			Optional<PDMarkedContent> detailDescriptionMark = getContent(document, 1 + DETAIL_DESCRIPTION);
			Optional<PDMarkedContent> detailPriceMark = getContent(document, 1 + DETAIL_PRICE);
			Optional<PDMarkedContent> detailDiscountMark = getContent(document, 1 + DETAIL_DISCOUNT);
			Optional<PDMarkedContent> detailTotalMark = getContent(document, 1 + DETAIL_TOTAL);
			
			if(detailAmountMark.isPresent()) 
				detailAmount = joinCharacterList(detailAmountMark.get().getContents());
			
			
			if(detailDescriptionMark.isPresent())
				detailDescription = joinCharacterList(detailDescriptionMark.get().getContents());
			
			if(detailPriceMark.isPresent()) 
				detailPrice = joinCharacterList(detailPriceMark.get().getContents());
			
			
			if(detailDiscountMark.isPresent())
				detailDiscount = joinCharacterList(detailDiscountMark.get().getContents());

			
			if(detailTotalMark.isPresent())
				detailTotal = joinCharacterList(detailTotalMark.get().getContents());
						
			assertPdfData("Description", detailTwo.getDescription(), detailDescription);
			assertPdfData("Amount", toLatinNumber(detailTwo.getQuantity()), detailAmount);
			assertPdfData("Price", toLatinNumber(detailTwo.getPrice()), detailPrice);
			assertPdfData("Discount", detailTwo.getDiscountExpression(), detailDiscount);
			assertPdfData("Total", toLatinNumber(detailTwo.getTaxableBase()), detailTotal);
			
			/** Assert finances **/
			start("Comparing original/PDF finances");
			
			String financeDate = "";
			String financePayMethod = "";
			String financeBankAccount = "";
			String financeAmount = "";
			
			Optional<PDMarkedContent> financeDateMark = getContent(document, 0 + FINANCE_DATE);
			Optional<PDMarkedContent> financePayMethodMark = getContent(document, 0 + FINANCE_PAY_METHOD);
			Optional<PDMarkedContent> financeBankAccountMark = getContent(document, 0 + FINANCE_BANK_ACCOUNT);
			Optional<PDMarkedContent> financeAmountMark = getContent(document, 0 + FINANCE_AMOUNT);

			financeDate =  joinCharacterList(financeDateMark.get().getContents());
			financePayMethod =  joinCharacterList(financePayMethodMark.get().getContents());
			financeBankAccount =  joinCharacterList(financeBankAccountMark.get().getContents());
			financeAmount =  joinCharacterList(financeAmountMark.get().getContents());
			
			assertPdfData("Date", formatDate(financeOne.getDueDate(), "dd/MM/yyyy").orElse("-"), financeDate);
			assertPdfData("Paymethod", financeOne.getPayMethodType().getDescription(), financePayMethod);
			assertPdfData("Bankaccount",  financeOne.getBankAccount().getIban(), financeBankAccount);
			assertPdfData("Amount",  toLatinNumber(financeOne.getAmount()), financeAmount);
			
			/** Assert taxes **/
			start("Comparing original/PDF taxes");
			
			
			String taxBase = "";
			String taxPercentage = "";
			String taxType = "";
			String taxQuote = "";
			
		
			Optional<PDMarkedContent> taxBaseMark = getContent(document, 0 + TAX_BASE);
			Optional<PDMarkedContent> taxPercentageMark = getContent(document, 0 + TAX_PERCENTAGE);
			Optional<PDMarkedContent> taxTypeMark = getContent(document, 0 + TAX_TYPE);
			Optional<PDMarkedContent> taxQuoteMark = getContent(document, 0 + TAX_QUOTE);
			
			if(taxBaseMark.isPresent())
				taxBase = joinCharacterList(taxBaseMark.get().getContents());
			
			if(taxPercentageMark.isPresent())
				taxPercentage = joinCharacterList(taxPercentageMark.get().getContents());
			
			if(taxTypeMark.isPresent())
				taxType = joinCharacterList(taxTypeMark.get().getContents());
			
			if(taxQuoteMark.isPresent())
				taxQuote = joinCharacterList(taxQuoteMark.get().getContents());
			
			assertPdfData("Base", toLatinNumber(breakdownOne.getBase()), taxBase);
			assertPdfData("Percentage", toLatinNumber(breakdownOne.getPercentage()) + "% + " + toLatinNumber(breakdownOne.getSurcharge()), taxPercentage);
			assertPdfData("Type", breakdownOne.getTaxType().getName(), taxType);
			assertPdfData("Quota", toLatinNumber(breakdownOne.getQuota() + breakdownOne.getSurchargeQuota()), taxQuote);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("File not found Exception");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException");
		} catch (CanNotCreatePdfException e) {
			e.printStackTrace();
			fail("Can not create exception");
		}
	}
	
	/**
	 * Remove special characters        	  				 <br>
	 * ---------------------------------------------------   <br>
	 * [ \u20AC ] Euro                 	  				 	 <br>
	 * [ % ] Percentaje Symbol         	  				 	 <br>
	 * ---------------------------------------------------   <br>
	 * 
	 * @param text - original text
	 * @return text without special charaters
	 */
	public String removeSpecialCharacters(String text) {		
		return text.replaceAll("%", "").replaceAll("\u20AC", "");
	}
	
	/**
	 * Assert data with pdf data
	 * @param data - The original data
	 * @param pdf - The pdf data
	 */
	public void assertPdfData(String name, String data, String pdf) {
		log(COMPARE, ARROW, "Original " + name, data);
		log(COMPARE, ARROW, "Pdf " + name, pdf);
		
		assertEquals(data, pdf);
		log(SUCCESS, "DONE.");
		jump();
	}
	

}
