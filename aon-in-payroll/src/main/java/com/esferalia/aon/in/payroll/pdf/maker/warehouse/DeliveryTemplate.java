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
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
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
	
	private Delivery delivery;
	private Warehouse warehouse;
	private CompanyFull company;
	private CustomerFull customer;
	private Item item;
	private byte[] logo;
	
	private float x;
	private float y;

	private float marginTop;
	private float marginSide;
	
	private float separateElementsMargin;
	private float marginText;
	
	private float initialX;
	private float initialY;
	
	private float maxLogoWidth;
	private float maxLogoHeight;

	
	
	public DeliveryTemplate(Delivery delivery, Warehouse warehouse, CompanyFull company, CustomerFull customer, byte[] logo) throws CanNotCreatePdfException {
		try {
			if (delivery == null || warehouse == null || company == null || customer == null) {
				throw new CanNotCreatePdfException("No delivery");
			}

			this.delivery = delivery;
			this.warehouse = warehouse;
			this.company = company;
			this.customer = customer;
			this.logo = logo;
			
			this.document = new PDDocument();
			this.page = new PDPage(PDRectangle.A5);
			this.document.addPage(this.page);
			this.contents = new PDPageContentStream(this.document, this.page);
			
			this.marginTop = getPageWidth() * 0.055f;
			this.marginSide = getPageWidth() * 0.036f;
			this.separateElementsMargin = getPageWidth() * 0.075f;
			this.marginText = getPageWidth() * 0.095f;
						
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
	
	private void draw() throws IOException {
		x = initialX;
		y = initialY;
		
		drawCompany();
		drawReferenceAndDate();
		drawCustomer();
		drawDestinyAddressTitle();
		drawDestinyAddress();
		drawDeliveryTitle();
		drawOriginInfo();
		drawDelivery();
	}
	
	private void drawCompany() throws IOException {
		drawLogo();
		drawCompanyName();
		drawCompanyAddress();
		drawCompanyMedia();
	}
	
	private void drawDelivery() throws IOException {
		drawDeliveryTableFirstRow();
		drawDeliveryTableSecondRow();
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
		y -= TITLEFONTSIZE;
		
		final float nameWidth = maxLogoWidth*2;
				
		String companyName = AonStringUtils.trimToEmpty(this.company.getRegistry().getName());
		
		List<String> nameLines = PDFToolkit.getLines(companyName, nameWidth, DEFAULT_BOLD_FONT, TITLEFONTSIZE);
		
		for (String line : nameLines) {
			PDFToolkit.drawText(this.contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_BOLD_FONT,TITLEFONTSIZE);
			y -= TITLEFONTSIZE;
		}
	}
	
	private void drawCompanyAddress() throws IOException {
		y = initialY - TITLEFONTSIZE - TITLEFONTSIZE;
		
		RegistryAddress mainAddress = company.getMainAddress();
		
		String fullAddress = StringUtils.EMPTY;
		String zip = StringUtils.EMPTY;
		String city = StringUtils.EMPTY;
		String province = StringUtils.EMPTY;
		
		if (mainAddress != null) {
			fullAddress = (mainAddress.getFullAddress() != null) ? mainAddress.getFullAddress() : StringUtils.EMPTY;
			zip = (mainAddress.getZip() != null) ? mainAddress.getZip() : StringUtils.EMPTY;
			city = (mainAddress.getCity() != null) ? mainAddress.getCity() : StringUtils.EMPTY;
			province = (mainAddress.getProvince() != null) ? mainAddress.getProvince() : StringUtils.EMPTY;
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
	}
	
	private void drawCompanyMedia() throws IOException {
		
		List<RegistryMedia> medias = company.getMedias();
		
		String phone = StringUtils.EMPTY;
		String email = StringUtils.EMPTY;
		String fax = StringUtils.EMPTY;
		String web = StringUtils.EMPTY;
		
		if (medias != null) {
			for (RegistryMedia media : medias) {
				if (media.getMedia() != null && media.getValue() != null) {
					phone = (media.getMedia().equals(MediaType.FIXED_PHONE)) ? media.getValue() : StringUtils.EMPTY;
					email = (media.getMedia().equals(MediaType.EMAIL)) ? media.getValue() : StringUtils.EMPTY;
					fax   = (media.getMedia().equals(MediaType.FAX)) ? media.getValue() : StringUtils.EMPTY;
					web   = (media.getMedia().equals(MediaType.WEB)) ? media.getValue() : StringUtils.EMPTY;
				}
			}
		}
		
		String telephoneEmail = "t: " + phone + " - e: " + email ;
		String faxWeb = "f: " + fax + " w: " + web;
		
		if (!AonStringUtils.isBlank(phone) && !AonStringUtils.isBlank(email)) {
			y -= TEXTFONTSIZE + TEXTFONTSIZE + 1;
			PDFToolkit.drawText(this.contents, telephoneEmail, x, y, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE);			
		}
		
		if (!AonStringUtils.isBlank(fax) && !AonStringUtils.isBlank(web)) {
			y -= TEXTFONTSIZE + TEXTFONTSIZE + 1;
			PDFToolkit.drawText(this.contents, faxWeb, x, y, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE);			
		}
	}
	
	private void drawReferenceAndDate() throws IOException {
		this.x = (float) (maxLogoWidth*3.80 + marginText);
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
		x = initialX;
		y = initialY - marginTop - maxLogoWidth - TITLEFONTSIZE;
		y -= TEXTFONTSIZE + TEXTFONTSIZE + 1 + TEXTFONTSIZE;
		
		List<RegistryMedia> medias = company.getMedias();
		
		String name = "Cliente: ";
		String phone = "Tel\u00E9fono: ";
		String cellphone = "M\u00F3vil: ";
		
		if (customer.getRegistry() != null && customer.getRegistry().getName() != null) {
			name = "Cliente: " + customer.getRegistry().getName();
		}
		
		if (medias != null) {
			for (RegistryMedia media : medias) {
				if (media.getMedia() != null && media.getValue() != null) {
					String phoneValue = (media.getMedia().equals(MediaType.FIXED_PHONE)) ? media.getValue() : StringUtils.EMPTY;
					String cellphoneValue = (media.getMedia().equals(MediaType.CELLULAR)) ? media.getValue() : StringUtils.EMPTY;
					phone = phone.concat(phoneValue);
					cellphone = cellphone.concat(cellphoneValue);
				}
			}
		}
		
		final float nameWidth = (float) (maxLogoWidth*1.25);
		
		List<String> nameLines = PDFToolkit.getLines(name, nameWidth, DEFAULT_FONT, TEXTFONTSIZE);

		for (String line : nameLines) {
			y -= TEXTFONTSIZE + 1;
			PDFToolkit.drawText(contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_FONT,TEXTFONTSIZE);
		}
		
		y -= TEXTFONTSIZE + 1;
		
		nameLines = PDFToolkit.getLines(phone, nameWidth, DEFAULT_FONT, TEXTFONTSIZE);

		for (String line : nameLines) {
			y -= TEXTFONTSIZE + 	1;
			PDFToolkit.drawText(contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_FONT,TEXTFONTSIZE);
		}
		
		y -= TEXTFONTSIZE + 1;
		
		nameLines = PDFToolkit.getLines(cellphone, nameWidth, DEFAULT_FONT, TEXTFONTSIZE);

		for (String line : nameLines) {
			y -= TEXTFONTSIZE + 	1;
			PDFToolkit.drawText(contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_FONT,TEXTFONTSIZE);
		}
	}
	
	private void drawDestinyAddressTitle() throws IOException {
		x = (initialX + getPageWidth()/2);
		y = initialY - marginTop - maxLogoWidth - TITLEFONTSIZE;
		
		String title = "DIRECCI\u00D3N DE ENTREGA";
		
		y -= TEXTFONTSIZE + TEXTFONTSIZE + 1;
		PDFToolkit.drawText(this.contents, title, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_BOLD_FONT, TITLEFONTSIZE);
		
	}
	
	private void drawDestinyAddress() throws IOException {
		x = (float) (initialX + getPageWidth()/3.15 + marginSide);
		y = initialY - marginTop - maxLogoWidth - TITLEFONTSIZE;
		y -= TEXTFONTSIZE + TEXTFONTSIZE + 1 + TEXTFONTSIZE;
		
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
		address = "Direcci\u00F3n: " + address + " " + zip + " " + city + " (" + province + ")";
		
		final float nameWidth = (maxLogoWidth*3);
		
		List<String> nameLines = PDFToolkit.getLines(destinatary, nameWidth, DEFAULT_FONT, TEXTFONTSIZE);

		for (String line : nameLines) {
			y -= TEXTFONTSIZE + 1;
			PDFToolkit.drawText(contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_FONT,TEXTFONTSIZE);
		}
		
		y -= TEXTFONTSIZE + 1;
		
		nameLines = PDFToolkit.getLines(address, nameWidth, DEFAULT_FONT, TEXTFONTSIZE);

		for (String line : nameLines) {
			y -= TEXTFONTSIZE + 	1;
			PDFToolkit.drawText(contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_FONT,TEXTFONTSIZE);
		}
	}
	
	private void drawDeliveryTitle() throws IOException {
		x = marginSide + initialX + maxLogoWidth + separateElementsMargin;
		y = (float) (initialY - marginTop - maxLogoWidth*2.5 - TITLEFONTSIZE);
		
		final float nameWidth = (float) (getPageWidth()/3.25);
				
		String albaranDeEntrega = AonStringUtils.trimToEmpty("ALBAR\u00C1N DE ENTREGA");
		
		List<String> nameLines = PDFToolkit.getLines(albaranDeEntrega, nameWidth, DEFAULT_BOLD_FONT, TITLEFONTSIZE);
		
		for (String line : nameLines) {
			this.y -= TEXTFONTSIZE + 1;
			PDFToolkit.drawText(contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_BOLD_FONT,EXTRATITLEFONTSIZE);
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
			workplace = "Centro de Trabajo: " + warehouse.getName();
		}
		
		nameLines = PDFToolkit.getLines(workplace, nameWidth, DEFAULT_FONT, TEXTFONTSIZE);
		
		for (String line : nameLines) {
			PDFToolkit.drawText(this.contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_FONT,TEXTFONTSIZE);
			this.y -= TEXTFONTSIZE + 1;
		}
		
		// Store
		x += maxLogoWidth*2 + separateElementsMargin;
		y = (float) (initialY - marginTop - maxLogoWidth*2.5 - TITLEFONTSIZE*2 - EXTRATITLEFONTSIZE - TITLEFONTSIZE);
		
		nameWidth = (maxLogoWidth*2);
		
		String store = "Almac\u00E9n: ";
		
		if (delivery != null) {
			store = "Almac\u00E9n: ";
		}
		
		nameLines = PDFToolkit.getLines(store, nameWidth, DEFAULT_FONT, TEXTFONTSIZE);
		
		for (String line : nameLines) {
			PDFToolkit.drawText(this.contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_FONT,TEXTFONTSIZE);
			this.y -= TEXTFONTSIZE + 1;
		}
	}
	
	private void drawDeliveryTableFirstRow() throws IOException {
		y = (float) (initialY - marginTop - maxLogoWidth*2.5 - TITLEFONTSIZE*2 - EXTRATITLEFONTSIZE - TITLEFONTSIZE - EXTRATITLEFONTSIZE*3 - 1);
		float height = this.getPageWidth()-this.marginSide*4;
		PDFToolkit.drawBox(contents, initialX, y, height, 12, PdfColors.GRAY);
		
		String descripcion = "Descripci\u00F3n";
		y += (float) (3.5);
				
		PDFToolkit.drawText(contents,descripcion,initialX,y,PdfColors.WHITE,DEFAULT_BOLD_FONT,TITLEFONTSIZE);
		
		String cantidad = "Cantidad";
		x = (float) (maxLogoWidth*3.25);
		
		PDFToolkit.drawText(contents,cantidad,x,y,PdfColors.WHITE,DEFAULT_BOLD_FONT,TITLEFONTSIZE);

		String precio = "Precio";
		x += (float) (maxLogoWidth*0.75);
		
		PDFToolkit.drawText(contents,precio,x,y,PdfColors.WHITE,DEFAULT_BOLD_FONT,TITLEFONTSIZE);
		
		String importe = "Importe";
		x += (maxLogoWidth);
		
		PDFToolkit.drawText(contents,importe,x,y,PdfColors.WHITE,DEFAULT_BOLD_FONT,TITLEFONTSIZE);
	}
	
	private void drawDeliveryTableSecondRow() throws IOException {
		x = initialX;
		y -= TEXTFONTSIZE + TEXTFONTSIZE;
		
		List<DeliveryDetail> details = delivery.getDetails();
		
		String order = "Pedido: ";
				
		float nameWidth = (maxLogoWidth*3);
				
		if (delivery != null) {
			order = "Pedido: ";
		}
		
		List<String> nameLines = PDFToolkit.getLines(order, nameWidth, DEFAULT_FONT, TEXTFONTSIZE);
		
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
