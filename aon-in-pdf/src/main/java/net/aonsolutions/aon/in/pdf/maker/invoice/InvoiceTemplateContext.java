package net.aonsolutions.aon.in.pdf.maker.invoice;

import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceThemeConfiguration;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceTemplateContext {

	Administration administration;
	CompanyFull company;
	List<Invoice> invoices;
	PrintInvoiceConfiguration config;
	String qrUrl;
	byte[] logo;
	String tbaiId;
	
	// GENERATE PDF
	
	PDDocument document;
	PDPageContentStream contents;
	InvoiceTemplateMsg msg;
	// Selected Invoice for multi-invoice templates
	Invoice invoice;
	AonLanguage addressLanguage;
	
	
	public InvoiceTemplateContext() {

	}
	
	public InvoiceTemplateContext(CompanyFull company, List<Invoice> list, PrintInvoiceConfiguration config, String qrUrl, byte[] logo, String tbaiId) {
		this.company = company;
		this.invoices = list;
		this.config = config;
		this.qrUrl = qrUrl;
		this.logo = logo;
		this.tbaiId = tbaiId;
	}

	public Administration getAdministration() {
		return administration;
	}
	public InvoiceTemplateContext setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	public boolean isCanarias() {
		return getAdministration() != null && getAdministration().isCanarias();
	}
	
	public CompanyFull getCompany() {
		return company;
	}
	
	public InvoiceTemplateContext setCompany(CompanyFull company) {
		this.company = company;
		return this;
	}
	
	public List<Invoice> getInvoices() {
		return invoices;
	}	
	
	public InvoiceTemplateContext setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
		return this;
	}
	
	public PrintInvoiceConfiguration getConfig() {
		return config;
	}
	
	public PrintInvoiceThemeConfiguration getTheme() {
		if(getConfig() == null)
			return null;
		return getConfig().getTheme();
	}

	
	public InvoiceTemplateContext setConfig(PrintInvoiceConfiguration config) {
		this.config = config;
		return this;
	}
	
	public String getQrUrl() {
		return qrUrl;
	}
	
	public InvoiceTemplateContext setQrUrl(String qrUrl) {
		this.qrUrl = qrUrl;
		return this;
	}
	
	public byte[] getLogo() {
		return logo;
	}
	
	public InvoiceTemplateContext setLogo(byte[] logo) {
		this.logo = logo;
		return this;
	}
	
	public String getTbaiId() {
		return tbaiId;
	}
	
	public InvoiceTemplateContext setTbaiId(String tbaiId) {
		this.tbaiId = tbaiId;
		return this;
	}
	
	public PDDocument getDocument() {
		return document;
	}
	
	public InvoiceTemplateContext setDocument(PDDocument document) {
		this.document = document;
		return this;
	}
	
	public PDPageContentStream getContents() {
		return contents;
	}
	
	public InvoiceTemplateContext setContents(PDPageContentStream contents) {
		this.contents = contents;
		return this;
	}
	
	public Invoice getInvoice() {
		return invoice;
	}
	
	public InvoiceTemplateContext setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public AonLanguage getAddressLanguage() {
		return addressLanguage;
	}
	
	public InvoiceTemplateContext setAddressLanguage(AonLanguage addressLanguage) {
		this.addressLanguage = addressLanguage;
		return this;
	}
	
	public boolean isTbai() {
		return AonStringUtils.isNotBlank(getTbaiId());
	}
	
	public boolean isVerifactu() {
		return getQrUrl() != null && 
			(getQrUrl().contains("agenciatributaria")
			|| getQrUrl().contains("prewww2.aeat.es"));
	}
	
	public boolean isNoVerifactu() {
		return getQrUrl() != null && 
			(getQrUrl().contains("agenciatributaria")
			|| getQrUrl().contains("prewww2.aeat.es")) 
			&& getQrUrl().contains("ValidarQRNoVerifactu");
	}
	
	public boolean isVerifactuTest() {
		return getQrUrl() != null && getQrUrl().contains("prewww2.aeat.es");
	}
	
	
	public InvoiceTemplateMsg getMsg() {
		return msg;
	}
	
	public InvoiceTemplateContext setMsg(InvoiceTemplateMsg msg) {
		this.msg = msg;
		return this;
	}
	
	public String getInvoiceTitle() {
		String invoiceTitle = getMsg().invoice().toUpperCase();
		if (getInvoice().isRectifier()) invoiceTitle = getMsg().rectifiedInvoice();
		else if(getInvoice().isSimplified()) invoiceTitle = getMsg().simplifiedInvoice();
		return invoiceTitle;
	}
}
