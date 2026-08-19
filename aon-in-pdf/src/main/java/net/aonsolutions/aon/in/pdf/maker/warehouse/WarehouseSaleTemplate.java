package net.aonsolutions.aon.in.pdf.maker.warehouse;

import static net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit.drawImage;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import net.aonsolutions.aon.in.pdf.api.setting.PdfColors;
import net.aonsolutions.aon.in.pdf.api.setting.PdfFonts;
import net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit;
import net.aonsolutions.aon.in.pdf.maker.exception.CanNotCreatePdfException;

public class WarehouseSaleTemplate implements AutoCloseable {
	
	// per instance, not static: a PDFont cannot be shared between documents
	private final PDFont DEFAULT_FONT = PdfFonts.helvetica();
	private final PDFont DEFAULT_BOLD_FONT = PdfFonts.helveticaBold();
	
	private static final Color DEFAULT_FONT_COLOR = PdfColors.BLACK;
	
	OutputStream filename;
	private float marginTop;
	
	private float nameMargin;
	
	private PDDocument document;
	private PDPageContentStream contents;
	private PDPage page;
	private float x;
	private float y;
	
	private Sales sales;
	private Delivery delivery;
		
	
	public WarehouseSaleTemplate(Sales sales, Delivery delivery) throws CanNotCreatePdfException {
		try {
			this.sales = sales;		
			this.delivery = delivery;
			
			this.document = new PDDocument();
			this.page = new PDPage(PDRectangle.A5);
			this.document.addPage(this.page);
			this.contents = new PDPageContentStream(this.document, this.page);
			
			this.marginTop = this.getPageWidth() * (10f / 210f);
			
			this.draw();
			this.contents.close();
		} catch (Exception e) {
			throw new CanNotCreatePdfException(e);
		}
	}
	
	private void draw() throws Exception {
		this.x = 0;
		this.y = this.getPageHeight() - this.marginTop;
		
		this.drawCustomer();
	}
	

	
	private void drawCustomer() throws IOException {
		this.drawCustomerName();
		this.drawCustomerAddress();
	}
	

	
	private void drawCustomerName() throws IOException {
		final float nameFontSize = 20f;
		this.nameMargin = this.getPageWidth() * 0.075f;
		final float nameWidth = this.getPageWidth() - nameMargin * 2;
		
		this.x = nameMargin;
		
		String companyName = AonStringUtils.trimToEmpty(this.sales.getCustomer().getName());
		
		List<String> nameLines = PDFToolkit.getLines(
				companyName,
				nameWidth,
				DEFAULT_BOLD_FONT,
				nameFontSize);
		
		for (String line : nameLines) {
			this.y -= nameFontSize;
			PDFToolkit.drawTextCenter(
					this.contents,
					new PDRectangle(this.x, this.y, nameWidth, nameFontSize),
					AonStringUtils.trimToEmpty(line),
					PdfColors.RED,
					DEFAULT_BOLD_FONT,
					nameFontSize,
					0);
		}
		
	}
	
	private void drawCustomerAddress() throws IOException {
		this.y -= 14f;
		RegistryAddress mainAddress = this.sales.getShippingAddress();
		String mainAddrInfo = AonStringUtils.trimToEmpty(mainAddress.getFullAddress(AonLanguage.SPANISH));
		String zip = AonStringUtils.trimToEmpty(mainAddress.getZip()) + " ";
		String city = AonStringUtils.trimToEmpty(mainAddress.getCity());
		String province = AonStringUtils.trimToEmpty(mainAddress.getProvince());
		String country = AonStringUtils.trimToEmpty(mainAddress.getCountry() != null ? mainAddress.getCountry().getName() : null);
		
		String zipAndCity = !AonStringUtils.isBlank(zip) ? zip + city : city;
		String provinceAndCountry = !AonStringUtils.isBlank(province) ? "(" + province + ") " + country : country;
		
		final float addressFontSize = 14f;
		final float addressMargin = this.getPageWidth() * 0.085f;
		final float addressWidth = this.getPageWidth() - 2 * addressMargin;
		
		this.x = addressMargin;
		
		List<String> fullAddressLines = PDFToolkit.getLines(
				mainAddrInfo,
				addressWidth,
				DEFAULT_FONT,
				addressFontSize);
		
		int lineIdx = 0;
		for (String line : fullAddressLines) {
			this.y -= lineIdx++ > 0 ? addressFontSize + 1 : 20f;
			PDFToolkit.drawTextCenter(
					this.contents,
					new PDRectangle(this.x, this.y, addressWidth, addressFontSize),
					AonStringUtils.trimToEmpty(line),
					DEFAULT_FONT_COLOR,
					DEFAULT_FONT,
					addressFontSize,
					0);
		}
		if (!AonStringUtils.isBlank(zipAndCity)) {
			this.y -= addressFontSize + 1;
			
			PDFToolkit.drawTextCenter(
					this.contents,
					new PDRectangle(this.x, this.y, addressWidth, addressFontSize),
					zipAndCity,
					DEFAULT_FONT_COLOR,
					DEFAULT_FONT,
					addressFontSize,
					0);
			
		}
		if (!AonStringUtils.isBlank(provinceAndCountry)) {
			this.y -= addressFontSize + 1;
			PDFToolkit.drawTextCenter(
					this.contents,
					new PDRectangle(this.x, this.y, addressWidth, addressFontSize),
					provinceAndCountry,
					DEFAULT_FONT_COLOR,
					DEFAULT_FONT,
					addressFontSize,
					0);
		}

		this.y -= 50f;
		PDFToolkit.drawTextCenter(
				this.contents,
				new PDRectangle(this.x, this.y, addressWidth, addressFontSize),
				"FECHA CARGA: " + AonDateUtils.format(this.delivery.getStatusModificationDate(), "dd/MM/yyyy") ,
				DEFAULT_FONT_COLOR,
				DEFAULT_FONT,
				addressFontSize,
				0);
		
		this.y -= 50f;
		PDFToolkit.drawTextCenter(
				this.contents,
				new PDRectangle(this.x, this.y, addressWidth, addressFontSize),
				"FECHA ENTREGA: " + AonDateUtils.format(this.sales.getDeliveryDate(), "dd/MM/yyyy") ,
				DEFAULT_FONT_COLOR,
				DEFAULT_FONT,
				addressFontSize,
				0);
		
		this.y -= 50f;
		PDFToolkit.drawTextCenter(
				this.contents,
				new PDRectangle(this.x, this.y, addressWidth, 20f),
				this.sales.getCarrier().getName(),
				PdfColors.BLUE,
				DEFAULT_BOLD_FONT,
				20f,
				0);
		
		this.y -= 50f;
		PDFToolkit.drawTextCenter(
				this.contents,
				new PDRectangle(this.x, this.y, addressWidth, addressFontSize),
				"NUMERO PEDIDO: " + this.sales.getPurchaseReference() ,
				DEFAULT_FONT_COLOR,
				DEFAULT_FONT,
				addressFontSize,
				0);
		
		
		
		try {
			this.y -= 200f;
			String deliveryId = this.sales.getDetails().get(0).getDelivery().toString();
			byte[] qrCode = createQR(deliveryId, 300, 300);
			drawImage(document, contents, qrCode, 140f, y, 150, 150);
		} catch (WriterException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static byte[] createQR(String datos, int ancho, int altura) throws WriterException, IOException {
	    BitMatrix matrix;
	    Writer escritor = new QRCodeWriter();
	    matrix = escritor.encode(datos, BarcodeFormat.QR_CODE, ancho, altura);
	         
	    BufferedImage imagen = new BufferedImage(ancho, altura, BufferedImage.TYPE_INT_RGB);
	    
	    for(int y = 0; y < altura; y++) {
	        for(int x = 0; x < ancho; x++) {
	            int grayValue = (matrix.get(x, y) ? 0 : 1) & 0xff;
	            imagen.setRGB(x, y, (grayValue == 0 ? 0 : 0xFFFFFF));
	        }
	    }
	         
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(imagen, "PNG", baos);
	    return baos.toByteArray();        
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