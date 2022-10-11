package com.esferalia.aon.in.payroll.pdf.maker.warehouse;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

public class WarehouseTemplate implements AutoCloseable {
	
	private static final PDFont DEFAULT_FONT = PdfFonts.HELVETICA;
	private static final PDFont DEFAULT_BOLD_FONT = PdfFonts.HELVETICA_BOLD;
	
	private static final Color DEFAULT_FONT_COLOR = PdfColors.BLACK;
	
	OutputStream filename;
	private float marginTop;
	
	private float marginBarCode;
	private float heightBarCode;
	
	private float nameMargin;
	
	private PDDocument document;
	private PDPageContentStream contents;
	private PDPage page;
	private float x;
	private float y;
	
	private CompanyFull company;
	private Item item;
	private byte[] logo;
	
	private String barcode;
	private Double quantity;
	private String sscc;
	private String ean128;
	
	
	public WarehouseTemplate(CompanyFull company, Item item, byte[] logo, String barcode /*GTIN*/, Double quantity/*cajas*/, String ean128 /*código de barras*/, String sscc) throws CanNotCreatePdfException {
		try {
			if (company == null || company.getRegistry() == null || item == null) {
				throw new CanNotCreatePdfException("No item or company");
			}
			this.company = company;
			this.item = item;
			
			this.logo = logo;
			
			this.barcode = barcode;
			this.quantity = quantity;
			this.ean128= ean128;
			this.sscc = sscc;
			
			this.document = new PDDocument();
			this.page = new PDPage(PDRectangle.A5);
			this.document.addPage(this.page);
			this.contents = new PDPageContentStream(this.document, this.page);
			
			this.marginTop = this.getPageWidth() * (10f / 210f);

			this.marginBarCode = this.getPageWidth() * ((15f) / 148f);
			this.heightBarCode = this.getPageHeight() * ((32f) / 210f);
			
			this.draw();
			this.contents.close();
		} catch (Exception e) {
			throw new CanNotCreatePdfException(e);
		}
	}
	
	private void draw() throws Exception {
		this.x = 0;
		this.y = this.getPageHeight() - this.marginTop;
		
		this.drawCompany();
		this.drawItem();
		this.drawBarcodes();
		this.referenceBorder();//FOR TEST PURPOSES ONLY
	}
	
	private void referenceBorder() throws IOException {
		PDFToolkit.drawBorderedBox(contents, 0, 0, this.getPageWidth(), this.getPageHeight(), DEFAULT_FONT_COLOR, 1f);
	}
	
	private void drawCompany() throws IOException {
		this.drawCompanyName();
		this.drawCompanyAddress();
		this.y -= 8f;
		this.drawLogo();
		this.drawHorizontalSeparator();
	}
	
	private void drawLogo() throws IOException {
		if (logo != null) {
			this.x = this.nameMargin + (this.getPageWidth() - 2 * this.nameMargin) * 0.66f;
			float logoWidth = (this.nameMargin + (this.getPageWidth() - 2 * this.nameMargin) - this.x) - 10;
			float topY = this.getPageHeight() - this.marginTop;
			
			float maxHeight = Math.abs(this.y - topY) - 10;
			
			PDFToolkit.drawResizedCenteredLogo(this.document,
					this.page,
					this.contents,
					this.logo,
					this.x + 5,
					this.y + 5,
					maxHeight,
					logoWidth,
					null);
		}		
	}
	
	private void drawCompanyName() throws IOException {
		final float nameFontSize = 12f;
		this.nameMargin = this.getPageWidth() * 0.075f;
		final float nameWidth = logo != null ? (this.getPageWidth() - 2 * this.nameMargin) * 0.66f : this.getPageWidth() - nameMargin * 2;
		
		this.x = nameMargin;
		
		String companyName = AonStringUtils.trimToEmpty(this.company.getRegistry().getName());
		
		List<String> nameLines = PDFToolkit.getLines(
				companyName,
				nameWidth,
				DEFAULT_BOLD_FONT,
				nameFontSize);
		
		for (String line : nameLines) {
			this.y -= nameFontSize;
			PDFToolkit.drawText(
					this.contents,
					AonStringUtils.trimToEmpty(line),
					this.x,
					this.y,
					DEFAULT_FONT_COLOR,
					DEFAULT_BOLD_FONT,
					nameFontSize);
		}
		
	}
	
	private void drawCompanyAddress() throws IOException {
		this.y -= 12f;
		RegistryAddress mainAddress = company.getMainAddress();
		String mainAddrInfo = AonStringUtils.trimToEmpty(mainAddress.getFullAddress(AonLanguage.SPANISH));
		String zip = AonStringUtils.trimToEmpty(mainAddress.getZip()) + " ";
		String city = AonStringUtils.trimToEmpty(mainAddress.getCity());
		String province = AonStringUtils.trimToEmpty(mainAddress.getProvince());
		String country = AonStringUtils.trimToEmpty(mainAddress.getCountry() != null ? mainAddress.getCountry().getName() : null);
		
		String zipAndCity = !AonStringUtils.isBlank(zip) ? zip + city : city;
		String provinceAndCountry = !AonStringUtils.isBlank(province) ? "(" + province + ") " + country : country;
		
		final float addressFontSize = 10f;
		final float addressMargin = this.getPageWidth() * 0.085f;
		final float addressWidth = logo != null ? (this.getPageWidth() - 2 * addressMargin) * 0.66f : this.getPageWidth() - 2 * addressMargin;
		
		this.x = addressMargin;
		
		List<String> fullAddressLines = PDFToolkit.getLines(
				mainAddrInfo,
				addressWidth,
				DEFAULT_FONT,
				addressFontSize);
		
		int lineIdx = 0;
		for (String line : fullAddressLines) {
			this.y -= lineIdx++ > 0 ? addressFontSize + 1 : 5f;
			PDFToolkit.drawText(this.contents, AonStringUtils.trimToEmpty(line), this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, addressFontSize);
		}
		if (!AonStringUtils.isBlank(zipAndCity)) {
			this.y -= addressFontSize + 1;
			PDFToolkit.drawText(this.contents, zipAndCity, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, addressFontSize);			
		}
		if (!AonStringUtils.isBlank(provinceAndCountry)) {
			this.y -= addressFontSize + 1;
			PDFToolkit.drawText(this.contents, provinceAndCountry, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, addressFontSize);			
		}
		
	}
	
	private void drawHorizontalSeparator() throws IOException {
		final float lineMargin = this.getPageWidth() * 0.075f;
		final float lineWidth = this.getPageWidth() - lineMargin * 2;
		this.x = lineMargin;
		PDFToolkit.drawBox(this.contents, this.x, this.y, lineWidth, 2f, DEFAULT_FONT_COLOR);
	}
	
	private void drawVerticalSeparator(float yFrom, float yTo) throws IOException {
		float lineHeight = Math.abs(yFrom - yTo);
		PDFToolkit.drawBox(this.contents, this.x, this.y, 2f, lineHeight, DEFAULT_FONT_COLOR);		
	}
	
	private void drawItem() throws IOException {
		this.drawArticle();
		this.y -= 8f;
		this.drawHorizontalSeparator();
		float yTo = this.y;
		this.drawItemData();
		float currentY = this.y;
		this.y = yTo;
		this.drawConsumptionDate(currentY);
		
		this.y -= 8f;
		this.drawHorizontalSeparator();
		this.x = this.nameMargin + (this.getPageWidth() - 2 * this.nameMargin) * 0.66f;
		this.drawVerticalSeparator(this.y, yTo);
		this.x = this.nameMargin;
		this.drawSSCC();
		this.y -= 8f;
		this.drawHorizontalSeparator();
	}
	
	private void drawArticle() throws IOException {
		
		final float productMargin = this.getPageWidth() * 0.09f;
		final float productWidth = this.getPageWidth() - productMargin * 2;
		
		float articleFontSize = 15f;
		float articleTitleFontSize = 10f;
		this.y -= 12;
		this.x = this.nameMargin;
		PDFToolkit.drawText(this.contents, "ARTÍCULO:", this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, articleTitleFontSize);			

		this.x = productMargin;
		this.y -= 5;
		
		String product = AonStringUtils.trimToEmpty(item.getDescription());
		if (AonStringUtils.isBlank(product) && item.getProduct() != null) {
			
			product = AonStringUtils.trimToEmpty(item.getProduct().getName());
		}
		
		List<String> fullArticleLines = PDFToolkit.getLines(
				product,
				productWidth,
				DEFAULT_FONT,
				articleFontSize);
		
		for (String line : fullArticleLines) {
			this.y -= articleFontSize + 1;
			PDFToolkit.drawText(
					this.contents,
					AonStringUtils.trimToEmpty(line),
					this.x,
					this.y,
					DEFAULT_FONT_COLOR,
					DEFAULT_FONT,
					articleFontSize);
		}
		
	}
	
	private void drawItemData() throws IOException {
		float itemInfoFontSize = 12f;
		
		this.y -= 16;
		this.x = this.nameMargin;
		
		this.drawLeftItemInfoLine("GTIN:", this.barcode, itemInfoFontSize);
		this.y -= itemInfoFontSize * 1.75;
		
		String boxes = "";
		if (AonNumberUtils.isValid(this.quantity)) {
			if (this.quantity % 1 == 0) {
				boxes = AonNumberUtils.toString(AonNumberUtils.toInteger(this.quantity)); 
			} else {
				boxes = AonNumberUtils.toString(this.quantity); 				
			}
		}
		
		this.drawLeftItemInfoLine("CAJAS:", boxes, itemInfoFontSize);
		this.y -= itemInfoFontSize * 1.75;
		this.drawLeftItemInfoLine("LOTE:", item.getSerialNumber(), itemInfoFontSize);
	}
	
	private void drawConsumptionDate(float bottomY) throws IOException {
		float titleFontSize = 13f;
		float leftStart = this.nameMargin + (this.getPageWidth() - 2 * this.nameMargin) * 0.66f;
		float dateTitleWidth = (this.getPageWidth() - 2 * this.nameMargin) * 0.33f;
		
		
		this.x = leftStart;
		this.y -= titleFontSize;
		
		PDFToolkit.drawTextCenter(
				this.contents,
				new PDRectangle(this.x, this.y, dateTitleWidth, titleFontSize),
				"F.CONSUMO",
				DEFAULT_FONT_COLOR,
				DEFAULT_FONT,
				titleFontSize,
				0);
		this.y -= titleFontSize + 1;
		PDFToolkit.drawTextCenter(
				this.contents,
				new PDRectangle(this.x, this.y, dateTitleWidth, titleFontSize),
				"PREFERENTE",
				DEFAULT_FONT_COLOR,
				DEFAULT_FONT,
				titleFontSize,
				0);
		
		this.y = bottomY;
		
		Date consumptionDate = this.item.getSerialDate();
		String formattedDate = AonStringUtils.trimToEmpty(AonDateUtils.format(consumptionDate, "dd/MM/yy"));
		
		PDFToolkit.drawTextCenter(
				this.contents,
				new PDRectangle(this.x, this.y, dateTitleWidth, titleFontSize),
				formattedDate,
				DEFAULT_FONT_COLOR,
				DEFAULT_FONT,
				titleFontSize,
				0);
	}
	
	
	private void drawSSCC() throws IOException {
		
		float ssccFontSize = 20f;
		this.y -= ssccFontSize;
		
		float ssccWidth = this.getPageWidth() - this.nameMargin * 2f;
		
		if (!AonStringUtils.isBlank(this.sscc)) {
			String ssccLine = "SSCC: " + AonStringUtils.trimToEmpty(this.sscc);
			
			PDFToolkit.drawTextCenter(
					this.contents,
					new PDRectangle(this.x, this.y, ssccWidth, ssccFontSize),
					ssccLine,
					DEFAULT_FONT_COLOR,
					DEFAULT_FONT,
					ssccFontSize,
					0);
		} else {
			PDFToolkit.drawText(this.contents, "SSCC:", this.x  + 40, y, DEFAULT_FONT_COLOR, DEFAULT_FONT, ssccFontSize);
		}
	}
	
	private void drawBarcodes() throws Exception {
		
		if (!AonStringUtils.isBlank(this.ean128)) {			
			this.y -= this.heightBarCode + this.heightBarCode * 1/3;
			this.drawBarcode(AonStringUtils.trimToEmpty(this.ean128));
		}
		if (!AonStringUtils.isBlank(this.sscc)) {			
			this.y -= this.heightBarCode + this.heightBarCode * 1/3;
			this.drawBarcode("(00)" + AonStringUtils.trimToEmpty(this.sscc));
		}
		
	}
	
	//---------------------
	
	public static byte[] createBarcode(String datos, int ancho, int altura) throws Exception {
		BufferedImage imagen = generateCode128BarCodeImage(datos);
		
//	    BitMatrix matrix;
//	    Writer escritor = new Code128Writer();
//	    Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
//	    hints.put(EncodeHintType.GS1_FORMAT, true);
//	    
//	    matrix = escritor.encode(datos, BarcodeFormat.CODE_128, ancho, altura, hints);
//
//	    BufferedImage imagen = new BufferedImage(ancho, altura, BufferedImage.TYPE_INT_RGB);
//	    
//	    for(int y = 0; y < altura; y++) {
//	        for(int x = 0; x < ancho; x++) {
//	            int grayValue = (matrix.get(x, y) ? 0 : 1) & 0xff;
//	            imagen.setRGB(x, y, (grayValue == 0 ? 0 : 0xFFFFFF));
//	        }
//	    }
	         
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(imagen, "PNG", baos);
	    return baos.toByteArray();        
	}
	
	public static BufferedImage generateCode128BarCodeImage(final String barcodeText) throws Exception {
		final Barcode barcode = BarcodeFactory.createUCC128(barcodeText.substring(0,2), barcodeText.substring(2));
        barcode.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 0));
        barcode.setLabel(" ");
        return BarcodeImageHandler.getImage(barcode);
    }
	
	private void drawBarcode(String code) throws Exception {
		this.x = this.marginBarCode;
		float maxWidth = this.getPageWidth() - 2 * this.marginBarCode;
		byte[] barcodeBytes = createBarcode( code.replace("(", "").replace(")", ""), (int) maxWidth, (int) this.heightBarCode + 1);
		
		PDFToolkit.drawImage(this.document, this.contents, barcodeBytes, this.x, this.y, maxWidth, this.heightBarCode);
		
		final float codeFontSize = 12f;
		this.x = this.marginBarCode;
		this.y -= codeFontSize + 3;
		PDFToolkit.drawTextCenter(
				this.contents,
				new PDRectangle(this.x, this.y, maxWidth, codeFontSize),
				code.replace("\u001d", "").replace("\312", ""),
				DEFAULT_FONT_COLOR,
				DEFAULT_FONT,
				codeFontSize,
				0);
		
	}
	
	private void drawLeftItemInfoLine(String attribute, String value, float fontSize) throws IOException {
		float attributeLength = PDFToolkit.fontWidth(attribute, fontSize, DEFAULT_FONT);
		PDFToolkit.drawText(this.contents, attribute, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, fontSize);
		this.x += attributeLength + 10;
		PDFToolkit.drawText(this.contents, AonStringUtils.trimToEmpty(value), this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, fontSize);
		this.x = this.nameMargin;
	}

	public void save (OutputStream os) throws IOException {
		this.document.save(os);		
	}

	@Override
	public void close() throws IOException {
		this.document.close();
	}
	
	private float getPageHeight() {
		if (this.page != null)
			return page.getMediaBox().getHeight();
		else
			return 0;
	}
	
	private float getPageWidth() {
		if (this.page != null)
			return page.getMediaBox().getWidth();
		else
			return 0;
	}
	
	public void print(OutputStream os) throws CanNotCreatePdfException {
		if (os != null)
			this.filename = os;
		try {
			document.save(this.filename);
			document.close();
			new OutputStreamWriter(os,StandardCharsets.ISO_8859_1);
		} catch (IOException e) {
			throw new CanNotCreatePdfException(e);
		}
	}
}