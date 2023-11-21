package com.esferalia.aon.in.payroll.pdf.maker.warehouse;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;

import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DeliveryTemplate implements AutoCloseable  {
	private static final PDFont DEFAULT_FONT = PdfFonts.HELVETICA;
	private static final PDFont DEFAULT_BOLD_FONT = PdfFonts.HELVETICA_BOLD;
	private static final Color DEFAULT_FONT_COLOR = PdfColors.DARKEST;
	
	private static final float TITLEFONTSIZE = 7f;
	private static final float TEXTFONTSIZE = 6f;
	private static final float EXTRATITLEFONTSIZE = 10f;
	
	OutputStream filename;
	
	private PDDocument document;
	private PDPageContentStream contents;
	private PDPage page;
	
	private CompanyFull company;
	private Item item;
	private Delivery delivery;
	private byte[] logo;
	
	private float x;
	private float y;

	private float marginTop;
	private float marginSide;
	
	private float marginBarCode;
	private float heightBarCode;
	private float separateElementsMargin;
	private float marginText;
	
	private float initialX;
	private float initialY;
	
	private float maxLogoWidth;
	private float maxLogoHeight;

	
	
	public DeliveryTemplate(Delivery delivery, CompanyFull company, byte[] logo) throws CanNotCreatePdfException {
		try {
			if (delivery == null) {
				throw new CanNotCreatePdfException("No delivery");
			}
			
			this.company = company;
			this.logo = logo;
			this.delivery = delivery;
			
			this.document = new PDDocument();
			this.page = new PDPage(PDRectangle.A5);
			this.document.addPage(this.page);
			this.contents = new PDPageContentStream(this.document, this.page);
			
			this.marginTop = getPageWidth() * 0.055f;
			this.marginSide = getPageWidth() * 0.036f;
			this.separateElementsMargin = getPageWidth() * 0.075f;
			this.marginText = getPageWidth() * 0.095f;
			
			this.marginBarCode = this.getPageWidth() * ((15f) / 148f);
			this.heightBarCode = this.getPageHeight() * ((32f) / 210f);
						
			initialX = 0 + marginSide*2;
			initialY = getPageHeight() - marginTop;
			
			maxLogoWidth = this.getPageWidth()/6;
			maxLogoHeight = this.getPageWidth()/6;
			
			this.draw();
			this.contents.close();
		} catch (Exception e) {
			throw new CanNotCreatePdfException(e);
		}
	}
	
	private void draw() throws Exception {
		x = initialX;
		y = initialY;
		
		drawCompany();
		drawReferenceAndDate();
		drawCustomer();
		drawDestinyAddress();
		drawDeliveryTitle();
		drawOriginInfo();
	}
	
	private void drawCompany() throws IOException {
		this.drawLogo();
		this.drawCompanyName();
		this.drawCompanyAddressAndMedia();
	}
	
	private void drawLogo() throws IOException {
		if (logo != null) {
			
			float logoX = x;
			float logoY = this.getPageHeight() - maxLogoHeight - this.marginTop;
									
			try {
				PDFToolkit.drawResizedLogo(this.document, this.page, this.contents, this.logo, logoX, logoY, maxLogoHeight, maxLogoWidth, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	private void drawCompanyName() throws IOException {
		
		x = marginSide + initialX + maxLogoWidth + separateElementsMargin;
		
		final float nameWidth = maxLogoWidth*2;
				
		String companyName = AonStringUtils.trimToEmpty(this.company.getRegistry().getName());
		
		List<String> nameLines = PDFToolkit.getLines(companyName, nameWidth, DEFAULT_BOLD_FONT, TITLEFONTSIZE);
		
		for (String line : nameLines) {
			this.y -= TITLEFONTSIZE;
			PDFToolkit.drawText(this.contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_BOLD_FONT,TITLEFONTSIZE);
		}
	}
	
	private void drawCompanyAddressAndMedia() throws IOException {
		this.y = initialY - TITLEFONTSIZE - TITLEFONTSIZE;
		
		// address
		
		RegistryAddress mainAddress = company.getMainAddress();
		
		String fullAddress = StringUtils.EMPTY;
		String zip = StringUtils.EMPTY;
		String city = StringUtils.EMPTY;
		String province = StringUtils.EMPTY;
		
		if (mainAddress != null) {
			if (mainAddress.getFullAddress() != null) {
				fullAddress = mainAddress.getFullAddress();
			}
			
			if (mainAddress.getZip() != null) {
				zip = mainAddress.getZip();
			}
			
			if (mainAddress.getCity() != null) {
				city = mainAddress.getCity();
			}
			
			if (mainAddress.getProvince() != null) {
				province = mainAddress.getProvince();
			}
		}
		
		String zipCityProvince = zip + " " + city + " (" + province.toUpperCase() + ")";
		
		final float addressWidth = maxLogoWidth*2;
		
		List<String> fullAddressLines = PDFToolkit.getLines(fullAddress, addressWidth, DEFAULT_FONT, TEXTFONTSIZE);
		
		int lineIdx = 0;
		for (String line : fullAddressLines) {
			this.y -= lineIdx++ > 0 ? TEXTFONTSIZE + 1 : 5f;
			PDFToolkit.drawText(this.contents, AonStringUtils.trimToEmpty(line), this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE);
		}
				
		if (!AonStringUtils.isBlank(zipCityProvince)) {
			this.y -= TEXTFONTSIZE + TEXTFONTSIZE + 1;
			PDFToolkit.drawText(this.contents, zipCityProvince, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE);			
		}
		
		// media
		
		List<RegistryMedia> medias = company.getMedias();
		
		String phone = StringUtils.EMPTY;
		String email = StringUtils.EMPTY;
		String fax = StringUtils.EMPTY;
		String web = StringUtils.EMPTY;
		
		if (medias != null) {
			for (RegistryMedia media : medias) {
				if (media.getMedia() != null && media.getValue() != null) {
					if (media.getMedia().equals(MediaType.FIXED_PHONE)) {
						phone = media.getValue();
					}
					
					if (media.getMedia().equals(MediaType.EMAIL)) {
						email = media.getValue();
					}
					
					if (media.getMedia().equals(MediaType.FAX)) {
						fax = media.getValue();
					}
					
					if (media.getMedia().equals(MediaType.WEB)) {
						web = media.getValue();
					}
				}
			}
		}
		
		String telephoneEmail = "t: " + phone + " - e: " + email ;
		String faxWeb = "f: " + fax + " w: " + web;
		
		if (!AonStringUtils.isBlank(telephoneEmail)) {
			this.y -= TEXTFONTSIZE + TEXTFONTSIZE + 1;
			PDFToolkit.drawText(this.contents, telephoneEmail, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE);			
		}
		
		if (!AonStringUtils.isBlank(faxWeb)) {
			this.y -= TEXTFONTSIZE + TEXTFONTSIZE + 1;
			PDFToolkit.drawText(this.contents, faxWeb, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE);			
		}
	}
	
	private void drawReferenceAndDate() throws IOException {
		this.x = (float) (maxLogoWidth*3.5 + marginText);
		this.y = initialY - TITLEFONTSIZE;
		
		String reference = delivery.getReferenceCode();
		
		String formatReference = "N\u00FAmero: " + reference;
		String formatDate = "Fecha: " + AonDateUtils.format(delivery.getDate(), "dd/MM/yyyy");
		
		if (!AonStringUtils.isBlank(formatReference)) {
			PDFToolkit.drawText(this.contents, formatReference, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE);			
		}
		
		if (!AonStringUtils.isBlank(formatDate)) {
			this.y -= TEXTFONTSIZE + TEXTFONTSIZE + 1;
			PDFToolkit.drawText(this.contents, formatDate, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE);			
		}
	}
	
	private void drawCustomer() throws IOException {
		this.x = initialX;
		this.y = initialY - marginTop - maxLogoWidth - TITLEFONTSIZE;
		
		Customer customer = delivery.getCustomer();
	
		String name = "Cliente: ";
		String phone = "Tel\u00E9fono: ";
		String cellPhone = "M\u00F3vil: ";
		
		if (customer != null) {
			if (customer.getName() != null) {
				name = "Cliente: " + customer.getName();
			}
		}
		
		this.y -= TEXTFONTSIZE + TEXTFONTSIZE + 1 + TEXTFONTSIZE + TEXTFONTSIZE + 1;
		PDFToolkit.drawText(this.contents, name, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE);			
		
		this.y -= TEXTFONTSIZE + TEXTFONTSIZE + 1;
		PDFToolkit.drawText(this.contents, phone, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE);	
		
		this.y -= TEXTFONTSIZE + TEXTFONTSIZE + 1;
		PDFToolkit.drawText(this.contents, cellPhone, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE);	
	}
	
	private void drawDestinyAddress() throws IOException {
		x = (initialX + getPageWidth()/2);
		y = initialY - marginTop - maxLogoWidth - TITLEFONTSIZE;
		
		String title = "DIRECCI\u00D3N DE ENTREGA";

		String address = delivery.getShippingAlternativeAddress();
		String contact = delivery.getShippingContact();
		String zip = delivery.getShippingAlternativeZip();
		String city = delivery.getShippingAlternativeCity();
		String province = delivery.getAddress().getProvince();
		
		if (address == null || address.isEmpty() || contact == null || contact.isEmpty() &&
			zip == null || zip.isEmpty() || city == null || city.isEmpty()) {
			address = delivery.getAddress().getFullAddress();
			zip = delivery.getAddress().getZip();
			city = delivery.getAddress().getCity();
		}
		
		address = (address == null) ? StringUtils.EMPTY : address;
		contact = (contact == null) ? StringUtils.EMPTY : contact;
		zip = (zip == null) ? StringUtils.EMPTY : zip;
		city = (city == null) ? StringUtils.EMPTY : city;

		String destinatary = "Destinatario: " + contact;
		String address1    = "Direcci\u00F3n: " + address;
		String address2;
		
		if (province == null) {
			address2 = "                 " + zip + " " + city + " ()";
		} else {
			address2 = "                 " + zip + " " + city + " (" + province.toUpperCase() + ")";
		}
		
		address2 = ((zip == null || zip.isEmpty()) && (city == null || city.isEmpty())) ? StringUtils.EMPTY : address2;
		
		y -= TEXTFONTSIZE + TEXTFONTSIZE + 1;
		PDFToolkit.drawText(this.contents, title, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_BOLD_FONT, TITLEFONTSIZE);
		
		x = (float) (initialX + getPageWidth()/3.15 + marginSide);
		
		y -= TEXTFONTSIZE + TEXTFONTSIZE + 1;
		PDFToolkit.drawText(this.contents, destinatary, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE);	
		
		y -= TEXTFONTSIZE + TEXTFONTSIZE + 1;
		PDFToolkit.drawText(this.contents, address1, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE);	
		
		y -= TEXTFONTSIZE + TEXTFONTSIZE + 1;
		PDFToolkit.drawText(this.contents, address2, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE);
	}
	
	private void drawDeliveryTitle() throws IOException {
		x = marginSide + initialX + maxLogoWidth + separateElementsMargin;
		y = (float) (initialY - marginTop - maxLogoWidth*2.5 - TITLEFONTSIZE);
		
		final float nameWidth = getPageWidth();
				
		String albaranDeEntrega = AonStringUtils.trimToEmpty("ALBAR\u00C1N DE ENTREGA");
		
		List<String> nameLines = PDFToolkit.getLines(albaranDeEntrega, nameWidth, DEFAULT_BOLD_FONT, TITLEFONTSIZE);
		
		for (String line : nameLines) {
			this.y -= TITLEFONTSIZE;
			PDFToolkit.drawText(this.contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_BOLD_FONT,EXTRATITLEFONTSIZE);
		}
	}
	
	private void drawOriginInfo() throws IOException {
		// Title
		x = initialX;
		y = (float) (initialY - marginTop - maxLogoWidth*2.5 - TITLEFONTSIZE*2 - EXTRATITLEFONTSIZE - TITLEFONTSIZE);

		float nameWidth = (float) (maxLogoWidth*0.50);
		
		String origen = AonStringUtils.trimToEmpty("ORIGEN:");
		
		List<String> nameLines = PDFToolkit.getLines(origen, nameWidth, DEFAULT_BOLD_FONT, TITLEFONTSIZE);
		
		for (String line : nameLines) {
			PDFToolkit.drawText(this.contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_BOLD_FONT,TITLEFONTSIZE);
			this.y -= TEXTFONTSIZE + 1;
		}
		
		// Workplace
		x += maxLogoWidth*0.5 + separateElementsMargin;
		y = (float) (initialY - marginTop - maxLogoWidth*2.5 - TITLEFONTSIZE*2 - EXTRATITLEFONTSIZE - TITLEFONTSIZE);
		
		nameWidth = (maxLogoWidth*2);
		
		String workplace = "Centro de Trabajo: ";
		
		if (delivery.getWorkplace() != null && delivery.getWorkplace().getDescription() != null) {
			workplace = "Centro de Trabajo: " + delivery.getWorkplace().getDescription();
		}
		
		nameLines = PDFToolkit.getLines(workplace, nameWidth, DEFAULT_FONT, TEXTFONTSIZE);
		
		for (String line : nameLines) {
			PDFToolkit.drawText(this.contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_FONT,TEXTFONTSIZE);
			this.y -= TEXTFONTSIZE + 1;
		}
		
		// Workplace
		x += maxLogoWidth*2 + separateElementsMargin;
		y = (float) (initialY - marginTop - maxLogoWidth*2.5 - TITLEFONTSIZE*2 - EXTRATITLEFONTSIZE - TITLEFONTSIZE);
		
		nameWidth = (maxLogoWidth*2);
		
		String store = "Almac\u00E9n: ";
		
		if (delivery.getWorkplace() != null && delivery.getWorkplace().getDescription() != null) {
			store = "Almac\u00E9n: " + delivery.getWorkplace().getDescription();
		}
		
		nameLines = PDFToolkit.getLines(store, nameWidth, DEFAULT_FONT, TEXTFONTSIZE);
		
		for (String line : nameLines) {
			PDFToolkit.drawText(this.contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_FONT,TEXTFONTSIZE);
			this.y -= TEXTFONTSIZE + 1;
		}
	
	}
	
	private float getPageWidth() {
		if (this.page != null)
			return page.getMediaBox().getWidth();
		else
			return 0;
	}
	
	private float getPageHeight() {
		if (this.page != null)
			return page.getMediaBox().getHeight();
		else
			return 0;
	}

	@Override
	public void close() throws IOException {
		this.document.close();
		
	}

	public void save(OutputStream os) throws IOException {
		this.document.save(os);
	}
	
}
