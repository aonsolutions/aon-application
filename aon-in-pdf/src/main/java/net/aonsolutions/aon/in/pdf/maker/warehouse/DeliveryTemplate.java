package net.aonsolutions.aon.in.pdf.maker.warehouse;

import static net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit.createVerticalPage;
import static net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit.drawTextRight;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;


import org.apache.commons.lang.StringUtils;

import net.aonsolutions.aon.in.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.in.pdf.api.setting.PdfColors;
import net.aonsolutions.aon.in.pdf.api.setting.PdfFonts;
import net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit;

public class DeliveryTemplate implements AutoCloseable  {
	private static final PDFont DEFAULT_FONT = PdfFonts.HELVETICA;
	private static final PDFont DEFAULT_BOLD_FONT = PdfFonts.HELVETICA_BOLD;
	private static final Color DEFAULT_FONT_COLOR = PdfColors.DARKEST;
	
	private static final float TITLEFONTSIZE = 9f;
	private static final float TEXTFONTSIZE = 8f;
	private static final float EXTRATITLEFONTSIZE = 12f;
	
	OutputStream filename;
	
	private PDDocument document;
	private PDPageContentStream contents;
	private PDPage page;
	
	private Delivery delivery;
	private Warehouse warehouse;
	public Workplace workplace;
	private CompanyFull company;
	private CustomerFull customer;
	private byte[] logo;
	
	private float x;
	private float y;

	private float marginTop;
	private float marginSide;
	
	private float separateElementsMargin;
	private float marginText;
	
	private float initialX;
	private float initialY;
	
	private float totalY;
	
	private float maxLogoWidth;
	private float maxLogoHeight;
	
	private int pageNumber;
	int currentFirstPage;

	
	/**
	 * Constructor
	 * @param delivery
	 * @param warehouse
	 * @param workplace
	 * @param company
	 * @param customer
	 * @param logo
	 * @throws CanNotCreatePdfException
	 */
	public DeliveryTemplate(Delivery delivery, Warehouse warehouse, Workplace workplace, CompanyFull company, CustomerFull customer,
	byte[] logo) throws CanNotCreatePdfException {
		try {
			if (delivery == null || warehouse == null || company == null || customer == null) {
				throw new CanNotCreatePdfException("No delivery");
			}

			this.delivery = delivery;
			this.warehouse = warehouse;
			this.workplace = workplace;
			this.company = company;
			this.customer = customer;
			this.logo = logo;
			
			this.document = new PDDocument();
			this.page = new PDPage(PDRectangle.A4);
			this.document.addPage(this.page);
			this.contents = new PDPageContentStream(this.document, this.page);
			
			this.marginTop = getPageWidth() * 0.055f;
			this.marginSide = getPageWidth() * 0.036f;
			this.separateElementsMargin = getPageWidth() * 0.075f;
			this.marginText = getPageWidth() * 0.095f;
			
			this.pageNumber = 0;
						
			initialX = 0 + marginSide*2;
			initialY = getPageHeight() - marginTop;
			
			maxLogoWidth = this.getPageWidth()/6;
			maxLogoHeight = this.getPageWidth()/6;
			
			this.draw();
			this.contents.close();
			this.drawFooter();
		} catch (Exception e) {
			throw new CanNotCreatePdfException(e);
		}
	}
	
	/**
	 * Call the methods that draw the pdf
	 * @throws IOException
	 */
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
		drawPayment();
	}
	
	/**
	 * Draws the company data
	 * @throws IOException
	 */
	private void drawCompany() throws IOException {
		drawLogo();
		drawCompanyName();
		drawCustomerCIF();
		drawCompanyAddress();
		drawCompanyMedia();
	}
	
	/**
	 * Draws the delivery data
	 * @throws IOException
	 */
	private void drawDelivery() throws IOException {
		drawDeliveryTableFirstRow();
		drawDeliveryTableOtherRows();
	}
	
	/**
	 * Draws the payment data
	 * @throws IOException
	 */
	private void drawPayment() throws IOException {
		drawPaymentTableSecondRow();
	}
	
	/**
	 * Draws the company's logo
	 * @throws IOException
	 */
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
	
	/**
	 * Draws the company's name
	 * @throws IOException
	 */
	private void drawCompanyName() throws IOException {
		x = marginSide + initialX + maxLogoWidth + separateElementsMargin;
		y -= TITLEFONTSIZE;
		
		final float nameWidth = maxLogoWidth*2;
				
		String companyName = AonStringUtils.trimToEmpty(company.getRegistry().getName());
		
		List<String> nameLines = PDFToolkit.getLines(companyName, nameWidth, DEFAULT_BOLD_FONT, TITLEFONTSIZE);
		
		for (String line : nameLines) {
			PDFToolkit.drawText(this.contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_BOLD_FONT,TITLEFONTSIZE);
			y -= TITLEFONTSIZE;
		}
	}
	
	/**
	 * Draws the customer's cif
	 * @throws IOException
	 */
	private void drawCustomerCIF() throws IOException {
		y -= TITLEFONTSIZE; 
				
		String cif = "CIF: " + AonStringUtils.trimToEmpty(delivery.getCustomer().getDocument());
		
		PDFToolkit.drawText(this.contents,cif,x,y,DEFAULT_FONT_COLOR,DEFAULT_FONT,TEXTFONTSIZE);
	}
	
	/**
	 * Draws the company's address
	 * @throws IOException
	 */
	private void drawCompanyAddress() throws IOException {
		y -= TEXTFONTSIZE;
		
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
	
	/**
	 * Draw the company's medias
	 * @throws IOException
	 */
	private void drawCompanyMedia() throws IOException {
		
		List<RegistryMedia> medias = company.getMedias();
		
		String phone = StringUtils.EMPTY;
		String email = StringUtils.EMPTY;
		String fax = StringUtils.EMPTY;
		String web = StringUtils.EMPTY;
		
		if (medias != null) {
			for (RegistryMedia media : medias) {
				if (media.getMedia() != null && media.getValue() != null) {
					phone = (media.getMedia().equals(MediaType.FIXED_PHONE)) ? media.getValue() : phone;
					email = (media.getMedia().equals(MediaType.EMAIL)) ? media.getValue() : email;
					fax   = (media.getMedia().equals(MediaType.FAX)) ? media.getValue() : fax;
					web   = (media.getMedia().equals(MediaType.WEB)) ? media.getValue() : web;
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
	
	/**
	 * Draws the reference and date of the delivery
	 * @throws IOException
	 */
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
	
	/**
	 * Draws the customer data
	 * @throws IOException
	 */
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
	
	/**
	 * Draws the address's title
	 * @throws IOException
	 */
	private void drawDestinyAddressTitle() throws IOException {
		x = (initialX + getPageWidth()/2);
		y = initialY - marginTop - maxLogoWidth - TITLEFONTSIZE;
		
		String title = "DIRECCI\u00D3N DE ENTREGA";
		
		y -= TEXTFONTSIZE + TEXTFONTSIZE + 1;
		PDFToolkit.drawText(this.contents, title, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_BOLD_FONT, TITLEFONTSIZE);
		
	}
	
	/**
	 * Draws the address direction to send the delivery
	 * @throws IOException
	 */
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
	
	/**
	 * Draws the delivery title
	 * @throws IOException
	 */
	private void drawDeliveryTitle() throws IOException {
		x = marginSide + initialX + maxLogoWidth + separateElementsMargin;
		y = (float) (initialY - marginTop - maxLogoWidth*2.35);
		
		final float nameWidth = (float) (getPageWidth()/3.25);
				
		String albaranDeEntrega = AonStringUtils.trimToEmpty("ALBAR\u00C1N DE ENTREGA");
		
		List<String> nameLines = PDFToolkit.getLines(albaranDeEntrega, nameWidth, DEFAULT_BOLD_FONT, TITLEFONTSIZE);
		
		for (String line : nameLines) {
			this.y -= TEXTFONTSIZE + 1;
			PDFToolkit.drawText(contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_BOLD_FONT,EXTRATITLEFONTSIZE);
		}
	}
	
	/**
	 * Draws the workplace and warehouse information
	 * @throws IOException
	 */
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
		
		String workplaceName = "Centro de Trabajo: ";
		
		if (workplace != null && workplace.getDescription() != null) {
			workplaceName = workplaceName.concat(workplace.getDescription());
		}
		
		nameLines = PDFToolkit.getLines(workplaceName, nameWidth, DEFAULT_FONT, TEXTFONTSIZE);
		
		for (String line : nameLines) {
			PDFToolkit.drawText(this.contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_FONT,TEXTFONTSIZE);
			this.y -= TEXTFONTSIZE + 1;
		}
		
		// Store
		x += maxLogoWidth*2 + separateElementsMargin;
		y = (float) (initialY - marginTop - maxLogoWidth*2.5 - TITLEFONTSIZE*2 - EXTRATITLEFONTSIZE - TITLEFONTSIZE);
		
		nameWidth = (maxLogoWidth*2);
		
		String warehouseName = "Almac\u00E9n: ";
		
		if (warehouse != null && warehouse.getName() != null) {
			warehouseName = warehouseName.concat(warehouse.getName());
		}
		
		nameLines = PDFToolkit.getLines(warehouseName, nameWidth, DEFAULT_FONT, TEXTFONTSIZE);
		
		for (String line : nameLines) {
			PDFToolkit.drawText(this.contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_FONT,TEXTFONTSIZE);
			this.y -= TEXTFONTSIZE + 1;
		}
	}
	
	/**
	 * Draw the titles of the information of the delivery (description, quantity, price and amounth)
	 * @throws IOException
	 */
	private void drawDeliveryTableFirstRow() throws IOException {
		y = (float) (initialY - marginTop - maxLogoWidth*2.5 - TITLEFONTSIZE*2 - EXTRATITLEFONTSIZE - TITLEFONTSIZE - EXTRATITLEFONTSIZE*3 - 1);
		float width = this.getPageWidth()-this.marginSide*4;
		PDFToolkit.drawBox(contents, initialX, y, width, 15, PdfColors.GRAY);
		
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
	
	/**
	 * Draws the delivery's data (description, quantity, price and amounth) for each delivery
	 * @throws IOException
	 */
	private void drawDeliveryTableOtherRows() throws IOException {
		x = initialX;
		y -= TEXTFONTSIZE*2;
		
		pageNumber++;
		
		List<DeliveryDetail> details = delivery.getDetails();
		
		BigDecimal bigDecimal;
		List<String> nameLines;
		
		String description;
		String quantity;
		String price;
		String amounth;
		
		float nameWidth;
		
		TreeMap<Integer, List<DeliveryDetail>> deliveryDetailMap = getDeliveryDetailById(details);
		
        for (Entry<Integer, List<DeliveryDetail>> detail : deliveryDetailMap.entrySet()) {
			if (detail != null && detail.getKey() != null && detail.getValue() != null) {
				
				// page break
				if (y <= maxLogoWidth*2) {
					contents.close();
					PDPage newPage = createVerticalPage();
					document.addPage(newPage);
					pageNumber++;
					contents = new PDPageContentStream(document,newPage);
					
					y = (float) (initialY - maxLogoWidth*0.5);
					x = initialX;
					
					drawLogo();
					
					y = initialY;
					
					drawCompanyName();							
					drawCustomerCIF();
					drawCompanyAddress();
					drawCompanyMedia();
					
					drawReferenceAndDate();
					drawCustomer();
					drawDestinyAddressTitle();
					drawDestinyAddress();
					drawDeliveryTitle();
					drawOriginInfo();
					drawDeliveryTableFirstRow();
					
					y -= maxLogoWidth*0.15;
				}
			
				x = initialX;
				
				// Draw id of sales
				
				nameWidth = (maxLogoWidth*3);
				
				Integer id  = detail.getKey();
				
				if (id != -1) {
					String date = StringUtils.EMPTY;
					String reference = StringUtils.EMPTY;
					
					if (detail.getValue().get(0) != null && detail.getValue().get(0).getCreationDate() != null) {
						date = AonDateUtils.format(detail.getValue().get(0).getCreationDate(), "dd/MM/yyyy");
					}
					
					if (detail.getValue().get(0) != null && detail.getValue().get(0).getPurchaseReference() != null) {
						reference = detail.getValue().get(0).getPurchaseReference();
					}
					
					String pedido = "Pedido: " + id.toString() + " del " + date + " Ref. compra: " + reference;
					
					nameLines = PDFToolkit.getLines(pedido, nameWidth, DEFAULT_BOLD_FONT, TEXTFONTSIZE);
					
					for (String line : nameLines) {
						PDFToolkit.drawText(contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_BOLD_FONT,TEXTFONTSIZE);
						this.y -= TEXTFONTSIZE + 1;
					}
				}
				
				// Draw sales' items
				
				List<DeliveryDetail> items = detail.getValue();
				
				if (items != null) {
					
					for (DeliveryDetail i : items) {
						
						// page break
						if (y <= maxLogoWidth*2) {
							contents.close();
							PDPage newPage = createVerticalPage();
							document.addPage(newPage);
							pageNumber++;
							contents = new PDPageContentStream(document,newPage);
							
							y = (float) (initialY - maxLogoWidth*0.5);
							x = initialX;
							
							drawLogo();
							
							y = initialY;
							
							drawCompanyName();							
							drawCustomerCIF();
							drawCompanyAddress();
							drawCompanyMedia();
							
							drawReferenceAndDate();
							drawCustomer();
							drawDestinyAddressTitle();
							drawDestinyAddress();
							drawDeliveryTitle();
							drawOriginInfo();
							drawDeliveryTableFirstRow();
							
							y -= maxLogoWidth*0.15;
						}
						
						x = initialX;
						DecimalFormat format = new DecimalFormat("0");
						
						// description
						if (i.getDescription() != null) {
							nameWidth = (maxLogoWidth*3);
			
							description = i.getDescription();
							
							nameLines = PDFToolkit.getLines(description, nameWidth, DEFAULT_FONT, TEXTFONTSIZE);
							
							for (String line : nameLines) {
								PDFToolkit.drawText(contents,AonStringUtils.trimToEmpty(line),x,y,DEFAULT_FONT_COLOR,DEFAULT_FONT,TEXTFONTSIZE);
							}
						}
						
						// quantity
						x = (float) (maxLogoWidth*3.25);
									
						quantity = format.format(i.getQuantity());
												
						drawTextRight(contents
							, new PDRectangle(x, y, 15, 15)
							, quantity
							, DEFAULT_FONT_COLOR
							, DEFAULT_FONT
							, TEXTFONTSIZE
							, 0
							, 0
						);
						
						format = new DecimalFormat("0.00");
								
						// price
						x += (float) (maxLogoWidth*0.85);
									
						bigDecimal = BigDecimal.valueOf(i.getPrice()).setScale(2, RoundingMode.HALF_UP);
						price = format.format(bigDecimal.doubleValue());
											
						drawTextRight(contents
							, new PDRectangle(x, y, 15, 15)
							, price
							, DEFAULT_FONT_COLOR
							, DEFAULT_FONT
							, TEXTFONTSIZE
							, 0
							, 0
						);
						
						// amounth
						x += (maxLogoWidth*1.05);
							
						
						bigDecimal = BigDecimal.valueOf(i.getAmount()).setScale(2, RoundingMode.HALF_UP);
						amounth = format.format(bigDecimal.doubleValue());
						
						drawTextRight(contents
							, new PDRectangle(x, y, 15, 15)
							, amounth
							, DEFAULT_FONT_COLOR
							, DEFAULT_FONT
							, TEXTFONTSIZE
							, 0
							, 0
						);
						
						y -= TEXTFONTSIZE + 1;
					}
					
				}
				y -= TEXTFONTSIZE + 1;
			}		
		}
        
        totalY = y -= TEXTFONTSIZE*2 + 1;
	}
	
	/**
	 * Draws the payment's data
	 * @throws IOException
	 */
	private void drawPaymentTableFirstRow() throws IOException {
		x = initialX + (float) (maxLogoWidth*0.25);
		y -= TEXTFONTSIZE*3;
		
		DecimalFormat format = new DecimalFormat("0.00");
		
		float width = (this.getPageWidth()-this.marginSide*15);
		PDFToolkit.drawBox(contents, x, y, width, 15, PdfColors.GRAY);
		
		totalY = y;
		
		// base
		float auxY = y + (float) (maxLogoWidth*0.05);
		String base = "Base Imponible";
		
		PDFToolkit.drawText(contents,base,x,auxY,PdfColors.WHITE,DEFAULT_BOLD_FONT,TITLEFONTSIZE);
		
		// tax
		String tax = "I.V.A.";
		x += (float) (maxLogoWidth*1.25);
		
		PDFToolkit.drawText(contents,tax,x,auxY,PdfColors.WHITE,DEFAULT_BOLD_FONT,TITLEFONTSIZE);
		
		// tax coute
		String taxesCuote = "Cuota I.V.A.";
		x += (float) (maxLogoWidth*0.75);
				
		PDFToolkit.drawText(contents,taxesCuote,x,auxY,PdfColors.WHITE,DEFAULT_BOLD_FONT,TITLEFONTSIZE);
		
		auxY = y;
		
		TreeMap<Double,List<DeliveryDetail>> detailsByTax = getDeliveryDetailByTax(delivery.getDetails());
		
		for (Entry<Double, List<DeliveryDetail>> detail : detailsByTax.entrySet()) {
			if (detail != null && detail.getKey() != null && detail.getValue() != null ) {
				
				// page break
				if (y <= maxLogoWidth*2) {
					contents.close();
					PDPage newPage = createVerticalPage();
					document.addPage(newPage);
					pageNumber++;
					contents = new PDPageContentStream(document,newPage);
					
					y = (float) (initialY - maxLogoWidth*0.5);
					x = initialX;
					
					drawCompany();
					drawReferenceAndDate();
					drawCustomer();
					drawDestinyAddressTitle();
					drawDestinyAddress();
					drawDeliveryTitle();
					drawOriginInfo();
					drawDeliveryTableFirstRow();
				}
				
				x = initialX + (float) (maxLogoWidth*0.25);
				
				float pricesY = auxY - TITLEFONTSIZE*2 + 1;
				float pricesX = x + TITLEFONTSIZE*3;
				
				float rectanguleX = (float) (initialX+maxLogoWidth*1.50);
				float rectanguleY = (float) (initialY-maxLogoWidth*8.13);
				float rectanguleWidth = (float) (maxLogoWidth*0.355);
				float rectanguleHeight = (TEXTFONTSIZE);
				
				// base				
				Double basePrice = getAmounthWithTax(detail.getKey());
				BigDecimal bigDecimal = BigDecimal.valueOf(basePrice).setScale(2, RoundingMode.HALF_UP);
				String basePriceString = format.format(bigDecimal.doubleValue());
				
				PDRectangle rectangule = new PDRectangle(rectanguleX,rectanguleY,rectanguleWidth,rectanguleHeight);
				
				PDFToolkit.drawTextRight(contents, rectangule, basePriceString, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE, pricesX, pricesY);
				
				// tax
				Double taxPrice = detail.getKey();
				bigDecimal = BigDecimal.valueOf(taxPrice).setScale(2, RoundingMode.HALF_UP);
				String taxPriceString = format.format(bigDecimal.doubleValue());
				pricesX += (maxLogoWidth*1);

				rectanguleX += (float) (maxLogoWidth*1.80)+1;
								
				rectangule = new PDRectangle(rectanguleX,rectanguleY,rectanguleWidth,rectanguleHeight);
				
				PDFToolkit.drawTextRight(contents, rectangule, taxPriceString, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE, pricesX, pricesY);
				

				// tax coute
				Double taxCuotePrice = basePrice * (taxPrice/100);
				bigDecimal = BigDecimal.valueOf(taxCuotePrice).setScale(2, RoundingMode.HALF_UP);
				String taxCuoteString = format.format(bigDecimal.doubleValue());
				pricesX += (float) (maxLogoWidth*0.75);
				
				rectanguleX += (float) (maxLogoWidth*1.75)+1;
								
				rectangule = new PDRectangle(rectanguleX,rectanguleY,rectanguleWidth,rectanguleHeight);
				
				PDFToolkit.drawTextRight(contents, rectangule, taxCuoteString, DEFAULT_FONT_COLOR, DEFAULT_FONT, TEXTFONTSIZE, pricesX, pricesY);
				
				
				x = initialX + (float) (maxLogoWidth*0.25);
				auxY -= TEXTFONTSIZE+4;
			}
		}
	}
	
	/**
	 * Draws the base price, the taxes and the total price
	 * @throws IOException
	 */
	private void drawPaymentTableSecondRow() throws IOException {
		Customer simpleCustomer = customer.getRegistry();
		
		if (simpleCustomer.getTransaction() != InvoiceTransactionType.NATIONAL) {
			drawAmounthNotNational();
		} else {
			drawAmounthNational();
		}
	}
	
	/**
	 * Draws the total amounth
	 * @param amounth the total amounth, depending on if it's national or not
	 * @throws IOException
	 */
	private void drawAmounth(Double amounth) throws IOException {
		// box
		x = initialX + this.maxLogoWidth*4;
		y = totalY;
		float width = (maxLogoWidth);
		PDFToolkit.drawBox(contents, x, y, width, 12, PdfColors.DARKEST);
		
		// title
		String descripcion = "Total Albar\u00E1n";
		x += (float) (maxLogoWidth*0.20);
		y += (float) (maxLogoWidth*0.025);
				
		PDFToolkit.drawText(contents,descripcion,x,y,PdfColors.WHITE,DEFAULT_BOLD_FONT,TITLEFONTSIZE);
		
		// amounth
		DecimalFormat format = new DecimalFormat("0.00");
		
		x += maxLogoWidth*0.15;
		y -= maxLogoWidth*0.2;

		BigDecimal bigDecimal = BigDecimal.valueOf(amounth).setScale(2, RoundingMode.HALF_UP);
		String amounthString = format.format(bigDecimal.doubleValue()) + " \u20AC";

		
		PDFToolkit.drawText(contents, amounthString, x, y, DEFAULT_FONT_COLOR, DEFAULT_BOLD_FONT, TEXTFONTSIZE);
	}
		
	/**
	 * Draws the amonunth if the customer is not national (it does not shows the taxes)
	 * @throws IOException
	 */
	private void drawAmounthNotNational() throws IOException {
		Double amounth = getAmounthWithoutTax();
		
		drawAmounth(amounth);
	}
	
	/**
	 * Draws the amonunth if the customer is not national (it shows the taxes)
	 * @throws IOException
	 */
	private void drawAmounthNational() throws IOException {
		drawPaymentTableFirstRow();
		
		Double amounth = getTotalAmounthWithTax();
		
		drawAmounth(amounth);
	}
	
	/**
	 * Calculates the total price without the taxes (base price)
	 * @return
	 */
	private Double getAmounthWithoutTax() {
		List<DeliveryDetail> details = delivery.getDetails();
		Double totalAmounth = (double) 0;
		
		for(DeliveryDetail detail : details) {
			totalAmounth += detail.getAmount();
		}
		
		return totalAmounth;
	}
	
	/**
	 * Calculates the total price of all the items that have that given tax
	 * @param tax
	 * @return
	 */
	private Double getAmounthWithTax(Double tax) {
		TreeMap<Double,List<DeliveryDetail>> deliveryDetailMap = getDeliveryDetailByTax(delivery.getDetails());
		Double totalAmounth = (double) 0;
		
        for (Entry<Double, List<DeliveryDetail>> detail : deliveryDetailMap.entrySet()) {
			if (detail != null && detail.getKey() != null && detail.getValue() != null && tax.equals(detail.getKey())) {
				for (DeliveryDetail d : detail.getValue()) {
					totalAmounth += detail.getKey() * d.getAmount();
				}
			}
        }
		
		return totalAmounth;
	}
	
	/**
	 * Calculates the total price of all the items without the taxes
	 * @return
	 */
	private Double getTotalAmounthWithTax() {
		TreeMap<Double,List<DeliveryDetail>> deliveryDetailMap = getDeliveryDetailByTax(delivery.getDetails());
		Double totalAmounth = (double) 0;
		
        for (Entry<Double, List<DeliveryDetail>> detail : deliveryDetailMap.entrySet()) {
			if (detail != null && detail.getKey() != null && detail.getValue() != null) {
				for (DeliveryDetail d : detail.getValue()) {
					totalAmounth += detail.getKey() * d.getAmount();
				}
			}
        }
		
		return totalAmounth;
	}
	
	/**
	 * Returns a map with the DeliveryDetails and their id
	 * @param details list of DeliveryDetail
	 * @return map with the id and the deliveryDetail
	 */
	private TreeMap<Integer,List<DeliveryDetail>> getDeliveryDetailById(List<DeliveryDetail> details) {
		TreeMap<Integer, List<DeliveryDetail>> map = new TreeMap<>();
		List<DeliveryDetail> detailsById;
		
		for (DeliveryDetail detail : details) {
			if (detail.getSalesDetail() != null) {
				detailsById = getDetailsById(detail.getSalesDetail(),details);
				map.put(detail.getSalesDetail(), detailsById);
			} else {
				detailsById = getDetailsById(-1,details);
				map.put(-1, detailsById);
			}
		}
		
		return map;
	}
	
	/**
	 * Returns a list with the DeliveryDetails with the given salesDetail
	 * @param id the salesDetail
	 * @param details list of all DelivaryDetails
	 * @return list of DeliveryDetails with that given id
	 */
	private LinkedList<DeliveryDetail> getDetailsById(Integer id, List<DeliveryDetail> details) {
		LinkedList<DeliveryDetail> list = new LinkedList<>();
		
		for (DeliveryDetail detail : details) {
			if ((detail.getSalesDetail() == null && id.equals(-1)) || id.equals(detail.getSalesDetail())) {
				list.add(detail);
			}
		}
		
		return list;
	}
	
	/**
	 * Returns a map with the DeliveryDetails and their tax
	 * @param details list of DeliveryDetail
	 * @return map with the id and the deliveryDetail
	 */
	private TreeMap<Double,List<DeliveryDetail>> getDeliveryDetailByTax(List<DeliveryDetail> details) {
		TreeMap<Double, List<DeliveryDetail>> map = new TreeMap<>();
		List<DeliveryDetail> detailsByTax;
		
		for (DeliveryDetail detail : details) {
			if (detail != null && detail.getItem() != null && detail.getItem().getProduct() != null && detail.getItem().getProduct().getVat() != null) {
				double taxPercentage = detail.getItem().getProduct().getVat().getPercentage();
				detailsByTax = getDetailsByTax(taxPercentage,details);
				map.put(taxPercentage, detailsByTax);
			}
		}
		
		return map;
	}
	
	/**
	 * Returns a list with the DeliveryDetails with the given percentage
	 * @param percentage the salesDetail
	 * @param details list of all DelivaryDetails
	 * @return list of DeliveryDetails with that given percentage
	 */
	private LinkedList<DeliveryDetail> getDetailsByTax(Double percentage, List<DeliveryDetail> details) {
		LinkedList<DeliveryDetail> list = new LinkedList<>();
		
		for (DeliveryDetail detail : details) {
			if ((detail != null && detail.getItem() != null && detail.getItem().getProduct() != null && detail.getItem().getProduct().getVat() != null)
			&& detail.getItem().getProduct().getVat().getPercentage() == percentage) {
				list.add(detail);
			}
		}
		
		return list;
	}
	
	/**
	 * Draws the footer wich includes the page and other data
	 * @throws IOException
	 */
	private void drawFooter() throws IOException {
		for (int i = currentFirstPage ; i < this.pageNumber ; i++) {
			contents = new PDPageContentStream(document, document.getPage(i), PDPageContentStream.AppendMode.APPEND, true);
			
			float width = (this.getPageWidth()-this.marginSide*4);
			PDFToolkit.drawBox(contents, initialX, maxLogoWidth, width, 1, PdfColors.DARKEST);
			
			String recibi = "Recib\u00ED: ";
			String fecha = "Fecha: ";
			String firma = "Firma y sello: ";
			String nombre = "Nombre: ";
			
			drawTextRight(contents
				, new PDRectangle((float) (initialX + maxLogoWidth*0.15), 15, 15, 15)
				, recibi
				, DEFAULT_FONT_COLOR
				, DEFAULT_FONT
				, TEXTFONTSIZE
				, 0
				, (float) (maxLogoWidth*0.65)
			);
			
			drawTextRight(contents
				, new PDRectangle((float) (initialX + maxLogoWidth*1.15), 15, 15, 15)
				, fecha
				, DEFAULT_FONT_COLOR
				, DEFAULT_FONT
				, TEXTFONTSIZE
				, 0
				, (float) (maxLogoWidth*0.65)
			);
			
			drawTextRight(contents
				, new PDRectangle((initialX + maxLogoWidth*3), 15, 15, 15)
				, firma
				, DEFAULT_FONT_COLOR
				, DEFAULT_FONT
				, TEXTFONTSIZE
				, 0
				, (float) (maxLogoWidth*0.65)
			);
			
			drawTextRight(contents
				, new PDRectangle((float) (initialX + maxLogoWidth*1.22), 15, 15, 15)
				, nombre
				, DEFAULT_FONT_COLOR
				, DEFAULT_FONT
				, TEXTFONTSIZE
				, 0
				, (float) (maxLogoWidth*0.30)
			);
			
			

			String pagina = "Pag. " + (i+1 - currentFirstPage) + " de " + (pageNumber - currentFirstPage);
			
			drawTextRight(contents
				, new PDRectangle(560, 15, 15, 15)
				, pagina
				, DEFAULT_FONT_COLOR
				, DEFAULT_FONT
				, TEXTFONTSIZE
				, (float) (maxLogoWidth*0.20)
				, (float) (maxLogoWidth*0.20)
			);
		
			contents.close();
		}
	}
	
	/**
	 * Gets the page width
	 * @return width
	 */
	private float getPageWidth() {
		if (this.page != null)
			return page.getMediaBox().getWidth();
		else
			return 0;
	}
	
	/**
	 * Gets the page height
	 * @return height
	 */
	private float getPageHeight() {
		if (this.page != null)
			return page.getMediaBox().getHeight();
		else
			return 0;
	}

	/**
	 * Closes the document
	 */
	@Override
	public void close() throws IOException {
		this.document.close();
		
	}

	/**
	 * Saves the document
	 * @param os
	 * @throws IOException
	 */
	public void save(OutputStream os) throws IOException {
		this.document.save(os);
	}
	
}
