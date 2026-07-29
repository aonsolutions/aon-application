package net.aonsolutions.aon.in.pdf.maker.warehouse;

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

import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.in.pdf.api.setting.PdfColors;
import net.aonsolutions.aon.in.pdf.api.setting.PdfFonts;
import net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit;
import net.aonsolutions.aon.in.pdf.maker.exception.CanNotCreatePdfException;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.linear.ean.UCCEAN128Barcode;

public class MultipleWarehouseTemplate implements AutoCloseable {
	
	private final PDFont DEFAULT_FONT = PdfFonts.helvetica();
	private final PDFont DEFAULT_BOLD_FONT = PdfFonts.helveticaBold();
	
	private static final Color DEFAULT_FONT_COLOR = PdfColors.BLACK;
	
	OutputStream filename;
	private float marginTop;
	
	private float marginBarCode;
	private float heightBarCode;
	
	private float nameMargin;
	
	private PDDocument document;

	private float x;
	private float y;
	
	
	
	public MultipleWarehouseTemplate(PackagingTag packagingTag) throws CanNotCreatePdfException {
		try {
			
			this.document = new PDDocument();
			for (PackagingTagDetail detail : packagingTag.getDetails()) {
				PDPage page = new PDPage(PDRectangle.A5);
				this.document.addPage(page);
				
				PDPageContentStream contents = new PDPageContentStream(this.document, page);
				
				this.marginTop = this.getPageWidth(page) * (10f / 210f);

				this.marginBarCode = this.getPageWidth(page) * ((15f) / 148f);
				this.heightBarCode = this.getPageHeight(page) * ((32f) / 210f);
				
				this.draw(packagingTag, detail, page, contents);
				contents.close();				
			}

		} catch (Exception e) {
			throw new CanNotCreatePdfException(e);
		}
	}
	
	private void draw(PackagingTag packagingTag, PackagingTagDetail detail, PDPage page, PDPageContentStream contents) throws Exception {
		this.x = 0;
		this.y = this.getPageHeight(page) - this.marginTop;
		
		this.drawCompany(packagingTag, page, contents);
		this.drawItem(detail, page, contents);
		this.drawBarcodes(detail, page, contents);
		this.referenceBorder(page, contents);//FOR TEST PURPOSES ONLY
	}
	
	private void referenceBorder(PDPage page, PDPageContentStream contents) throws IOException {
		PDFToolkit.drawBorderedBox(contents, 0, 0, this.getPageWidth(page), this.getPageHeight(page), DEFAULT_FONT_COLOR, 1f);
	}
	
	private void drawCompany(PackagingTag packagingTag, PDPage page, PDPageContentStream contents) throws IOException {
		this.drawCompanyName(packagingTag, page, contents);
		this.drawCompanyAddress(packagingTag, page, contents);
		this.y -= 8f;
		this.drawLogo(packagingTag, page, contents);
		this.drawHorizontalSeparator(page, contents);
	}
	
	private void drawLogo(PackagingTag packagingTag, PDPage page, PDPageContentStream contents) throws IOException {
		if (packagingTag.getLogo() != null) {
			this.x = this.nameMargin + (this.getPageWidth(page) - 2 * this.nameMargin) * 0.66f;
			float logoWidth = (this.nameMargin + (this.getPageWidth(page) - 2 * this.nameMargin) - this.x) - 10;
			float topY = this.getPageHeight(page) - this.marginTop;
			
			float maxHeight = Math.abs(this.y - topY) - 10;
			
			PDFToolkit.drawResizedCenteredLogo(this.document,
					page,
					contents,
					packagingTag.getLogo(),
					this.x + 5,
					this.y + 5,
					maxHeight,
					logoWidth,
					null);
		}		
	}
	
	private void drawCompanyName(PackagingTag packagingTag, PDPage page, PDPageContentStream contents) throws IOException {
		final float nameFontSize = 12f;
		this.nameMargin = this.getPageWidth(page) * 0.075f;
		final float nameWidth = packagingTag.getLogo() != null ? (this.getPageWidth(page) - 2 * this.nameMargin) * 0.66f : this.getPageWidth(page) - nameMargin * 2;
		
		this.x = nameMargin;
		
		String companyName = AonStringUtils.trimToEmpty(packagingTag.getCompany().getRegistry().getName());
		
		List<String> nameLines = PDFToolkit.getLines(
				companyName,
				nameWidth,
				DEFAULT_BOLD_FONT,
				nameFontSize);
		
		for (String line : nameLines) {
			this.y -= nameFontSize;
			PDFToolkit.drawText(
					contents,
					AonStringUtils.trimToEmpty(line),
					this.x,
					this.y,
					DEFAULT_FONT_COLOR,
					DEFAULT_BOLD_FONT,
					nameFontSize);
		}
		
	}
	
	private void drawCompanyAddress(PackagingTag packagingTag, PDPage page, PDPageContentStream contents) throws IOException {
		this.y -= 12f;
		RegistryAddress mainAddress = packagingTag.getCompany().getMainAddress();
		String mainAddrInfo = AonStringUtils.trimToEmpty(mainAddress.getFullAddress(AonLanguage.SPANISH));
		String zip = AonStringUtils.trimToEmpty(mainAddress.getZip()) + " ";
		String city = AonStringUtils.trimToEmpty(mainAddress.getCity());
		String province = AonStringUtils.trimToEmpty(mainAddress.getProvince());
		String country = AonStringUtils.trimToEmpty(mainAddress.getCountry() != null ? mainAddress.getCountry().getName() : null);
		
		String zipAndCity = !AonStringUtils.isBlank(zip) ? zip + city : city;
		String provinceAndCountry = !AonStringUtils.isBlank(province) ? "(" + province + ") " + country : country;
		
		final float addressFontSize = 10f;
		final float addressMargin = this.getPageWidth(page) * 0.085f;
		final float addressWidth = packagingTag.getLogo() != null ? (this.getPageWidth(page) - 2 * addressMargin) * 0.66f : this.getPageWidth(page) - 2 * addressMargin;
		
		this.x = addressMargin;
		
		List<String> fullAddressLines = PDFToolkit.getLines(
				mainAddrInfo,
				addressWidth,
				DEFAULT_FONT,
				addressFontSize);
		
		int lineIdx = 0;
		for (String line : fullAddressLines) {
			this.y -= lineIdx++ > 0 ? addressFontSize + 1 : 5f;
			PDFToolkit.drawText(contents, AonStringUtils.trimToEmpty(line), this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, addressFontSize);
		}
		if (!AonStringUtils.isBlank(zipAndCity)) {
			this.y -= addressFontSize + 1;
			PDFToolkit.drawText(contents, zipAndCity, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, addressFontSize);			
		}
		if (!AonStringUtils.isBlank(provinceAndCountry)) {
			this.y -= addressFontSize + 1;
			PDFToolkit.drawText(contents, provinceAndCountry, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, addressFontSize);			
		}
		
	}
	
	private void drawHorizontalSeparator(PDPage page, PDPageContentStream contents) throws IOException {
		final float lineMargin = this.getPageWidth(page) * 0.075f;
		final float lineWidth = this.getPageWidth(page) - lineMargin * 2;
		this.x = lineMargin;
		PDFToolkit.drawBox(contents, this.x, this.y, lineWidth, 2f, DEFAULT_FONT_COLOR);
	}
	
	private void drawVerticalSeparator(PDPageContentStream contents, float yFrom, float yTo) throws IOException {
		float lineHeight = Math.abs(yFrom - yTo);
		PDFToolkit.drawBox(contents, this.x, this.y, 2f, lineHeight, DEFAULT_FONT_COLOR);		
	}
	
	private void drawItem(PackagingTagDetail detail, PDPage page, PDPageContentStream contents) throws IOException {
		this.drawArticle(detail, page, contents);
		this.y -= 8f;
		this.drawHorizontalSeparator(page, contents);
		float yTo = this.y;
		this.drawItemData(detail, contents);
		float currentY = this.y;
		this.y = yTo;
		this.drawConsumptionDate(detail, page, contents, currentY);
		
		this.y -= 8f;
		this.drawHorizontalSeparator(page, contents);
		this.x = this.nameMargin + (this.getPageWidth(page) - 2 * this.nameMargin) * 0.66f;
		this.drawVerticalSeparator(contents, this.y, yTo);
		this.x = this.nameMargin;
		this.drawSSCC(page, detail, contents);
		this.y -= 8f;
		this.drawHorizontalSeparator(page, contents);
	}
	
	private void drawArticle(PackagingTagDetail detail, PDPage page, PDPageContentStream contents) throws IOException {
		
		final float productMargin = this.getPageWidth(page) * 0.09f;
		final float productWidth = this.getPageWidth(page) - productMargin * 2;
		
		float articleFontSize = 15f;
		float articleTitleFontSize = 10f;
		this.y -= 12;
		this.x = this.nameMargin;
		PDFToolkit.drawText(contents, "ARTÍCULO:", this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, articleTitleFontSize);			

		this.x = productMargin;
		this.y -= 5;
		
		String product = AonStringUtils.trimToEmpty(detail.getItem().getDescription());
		if (AonStringUtils.isBlank(product) && detail.getItem().getProduct() != null) {
			product = AonStringUtils.trimToEmpty(detail.getItem().getProduct().getName());
		}
		
		List<String> fullArticleLines = PDFToolkit.getLines(
				product,
				productWidth,
				DEFAULT_FONT,
				articleFontSize);
		
		for (String line : fullArticleLines) {
			this.y -= articleFontSize + 1;
			PDFToolkit.drawText(
					contents,
					AonStringUtils.trimToEmpty(line),
					this.x,
					this.y,
					DEFAULT_FONT_COLOR,
					DEFAULT_FONT,
					articleFontSize);
		}
		
	}
	
	private void drawItemData(PackagingTagDetail detail, PDPageContentStream contents) throws IOException {
		float itemInfoFontSize = 12f;
		
		this.y -= 16;
		this.x = this.nameMargin;
		
		this.drawLeftItemInfoLine(contents, "GTIN:", detail.getBarcode(), itemInfoFontSize);
		this.y -= itemInfoFontSize * 1.75;
		
		String boxes = "";
		if (AonNumberUtils.isValid(detail.getQuantity())) {
			if (detail.getQuantity() % 1 == 0) {
				boxes = AonNumberUtils.toString(AonNumberUtils.toInteger(detail.getQuantity())); 
			} else {
				boxes = AonNumberUtils.toString(detail.getQuantity()); 				
			}
		}
		
		this.drawLeftItemInfoLine(contents, "CAJAS:", boxes, itemInfoFontSize);
		this.y -= itemInfoFontSize * 1.75;
		this.drawLeftItemInfoLine(contents, "LOTE:", detail.getItem().getSerialNumber(), itemInfoFontSize);
	}
	
	private void drawConsumptionDate(PackagingTagDetail detail, PDPage page, PDPageContentStream contents, float bottomY) throws IOException {
		float titleFontSize = 13f;
		float leftStart = this.nameMargin + (this.getPageWidth(page) - 2 * this.nameMargin) * 0.66f;
		float dateTitleWidth = (this.getPageWidth(page) - 2 * this.nameMargin) * 0.33f;
		
		
		this.x = leftStart;
		this.y -= titleFontSize;
		
		PDFToolkit.drawTextCenter(
				contents,
				new PDRectangle(this.x, this.y, dateTitleWidth, titleFontSize),
				"F.CONSUMO",
				DEFAULT_FONT_COLOR,
				DEFAULT_FONT,
				titleFontSize,
				0);
		this.y -= titleFontSize + 1;
		PDFToolkit.drawTextCenter(
				contents,
				new PDRectangle(this.x, this.y, dateTitleWidth, titleFontSize),
				"PREFERENTE",
				DEFAULT_FONT_COLOR,
				DEFAULT_FONT,
				titleFontSize,
				0);
		
		this.y = bottomY;
		
		Date consumptionDate = detail.getItem().getSerialDate();
		String formattedDate = AonStringUtils.trimToEmpty(AonDateUtils.format(consumptionDate, "dd/MM/yy"));
		
		PDFToolkit.drawTextCenter(
				contents,
				new PDRectangle(this.x, this.y, dateTitleWidth, titleFontSize),
				formattedDate,
				DEFAULT_FONT_COLOR,
				DEFAULT_FONT,
				titleFontSize,
				0);
	}
	
	
	private void drawSSCC(PDPage page, PackagingTagDetail detail, PDPageContentStream contents) throws IOException {
		
		float ssccFontSize = 20f;
		this.y -= ssccFontSize;
		
		float ssccWidth = this.getPageWidth(page) - this.nameMargin * 2f;
		
		if (!AonStringUtils.isBlank(detail.getSscc())) {
			String ssccLine = "SSCC: " + AonStringUtils.trimToEmpty(detail.getSscc());
			
			PDFToolkit.drawTextCenter(
					contents,
					new PDRectangle(this.x, this.y, ssccWidth, ssccFontSize),
					ssccLine,
					DEFAULT_FONT_COLOR,
					DEFAULT_FONT,
					ssccFontSize,
					0);
		} else {
			PDFToolkit.drawText(contents, "SSCC:", this.x  + 40, y, DEFAULT_FONT_COLOR, DEFAULT_FONT, ssccFontSize);
		}
	}
	
	private void drawBarcodes(PackagingTagDetail detail, PDPage page, PDPageContentStream contents) throws Exception {
		
		if (!AonStringUtils.isBlank(detail.getEan128())) {			
			this.y -= this.heightBarCode + this.heightBarCode * 1/3;
			this.drawBarcode(page, contents, AonStringUtils.trimToEmpty(detail.getEan128()));
		}
		if (!AonStringUtils.isBlank(detail.getSscc())) {			
			this.y -= this.heightBarCode + this.heightBarCode * 1/3;
			this.drawBarcode(page, contents, "(00)" + AonStringUtils.trimToEmpty(detail.getSscc()));
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
		final Barcode barcode = new UCCEAN128Barcode(barcodeText.substring(0,2), barcodeText.substring(2), false);
        barcode.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 0));
        barcode.setLabel(" ");
        return BarcodeImageHandler.getImage(barcode);
    }
	
	private void drawBarcode(PDPage page, PDPageContentStream contents, String code) throws Exception {
		this.x = this.marginBarCode;
		float maxWidth = this.getPageWidth(page) - 2 * this.marginBarCode;
		byte[] barcodeBytes = createBarcode( code.replace("(", "").replace(")", ""), (int) maxWidth, (int) this.heightBarCode + 1);
		
		PDFToolkit.drawImage(this.document, contents, barcodeBytes, this.x, this.y, maxWidth, this.heightBarCode);
		
		final float codeFontSize = 12f;
		this.x = this.marginBarCode;
		this.y -= codeFontSize + 3;
		PDFToolkit.drawTextCenter(
				contents,
				new PDRectangle(this.x, this.y, maxWidth, codeFontSize),
				code.replace("\u001d", "").replace("\312", ""),
				DEFAULT_FONT_COLOR,
				DEFAULT_FONT,
				codeFontSize,
				0);
		
	}
	
	private void drawLeftItemInfoLine(PDPageContentStream contents, String attribute, String value, float fontSize) throws IOException {
		float attributeLength = PDFToolkit.fontWidth(attribute, fontSize, DEFAULT_FONT);
		PDFToolkit.drawText(contents, attribute, this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, fontSize);
		this.x += attributeLength + 10;
		PDFToolkit.drawText(contents, AonStringUtils.trimToEmpty(value), this.x, this.y, DEFAULT_FONT_COLOR, DEFAULT_FONT, fontSize);
		this.x = this.nameMargin;
	}

	public void save (OutputStream os) throws IOException {
		this.document.save(os);		
	}

	@Override
	public void close() throws IOException {
		this.document.close();
	}
	
	private float getPageHeight(PDPage page) {
		if (page != null)
			return page.getMediaBox().getHeight();
		else
			return 0;
	}
	
	private float getPageWidth(PDPage page) {
		if (page != null)
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