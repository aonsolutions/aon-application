package net.aonsolutions.aon.in.pdf.maker.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import net.aonsolutions.aon.in.pdf.api.toolkit.PDFToolkit;
import net.aonsolutions.aon.in.pdf.maker.exception.CanNotCreatePdfException;

public class ImageToPdf implements AutoCloseable {
	
	private PDDocument document;
	private PDPageContentStream contents;
	private PDPage page;

	public ImageToPdf(byte[] image) throws CanNotCreatePdfException {
		imageToPdf(image, new HashMap<String, String>());
	}
	
	public ImageToPdf(byte[] image, Map<String, String> metadata) throws CanNotCreatePdfException {
		imageToPdf(image, metadata);
	}
	
	public ImageToPdf(List<byte[]> images) throws CanNotCreatePdfException {
		imagesToPdf(images, new HashMap<String, String>());
	}
	
	public ImageToPdf(List<byte[]> images, Map<String, String> metadata) throws CanNotCreatePdfException {
		imagesToPdf(images, metadata);
	}
	
	private void imageToPdf(byte[] image, Map<String, String> metadata) throws CanNotCreatePdfException {
		createDocument(metadata);
		createImagePage(image);
	}
	
	private void imagesToPdf(List<byte[]> images, Map<String, String> metadata) throws CanNotCreatePdfException {
		createDocument(metadata);
		for (byte[] image : images)
			createImagePage(image);
	}
	
	private void createDocument(Map<String, String> metadata) {
		this.document = new PDDocument();
		
		Calendar calendar = Calendar.getInstance();
		document.getDocumentInformation().setAuthor("Aon Solutions");
		document.getDocumentInformation().setCreator("Aon Solutions");
		document.getDocumentInformation().setProducer("Aon Solutions");
		document.getDocumentInformation().setCreationDate(calendar);
		document.getDocumentInformation().setModificationDate(calendar);
		
		metadata.keySet().stream().forEach(key -> document.getDocumentInformation().setCustomMetadataValue(key, metadata.get(key)));
	}
	
	private void createImagePage(byte[] image) throws CanNotCreatePdfException {
		try {
			InputStream is = new ByteArrayInputStream(image);
			BufferedImage bufferedImage = ImageIO.read(is);
	
            float imageWidth = bufferedImage.getWidth();
            float imageHeight = bufferedImage.getHeight();
			
						
			this.page = new PDPage( new PDRectangle(imageWidth, imageHeight));
			this.document.addPage(this.page);
			this.contents = new PDPageContentStream(this.document, this.page);
				
			PDFToolkit.drawImage(this.document, this.contents, image, 0, 0, imageWidth, imageHeight);
		
			this.contents.close();
		} catch (Exception e) {
			throw new CanNotCreatePdfException(e);
		}
	}

	public File toFile() throws IOException {
		return toFile("image");
	}
	
	public File toFile(String name) throws IOException {
		File file = File.createTempFile(name, ".pdf");
		this.document.save(file);		
		return file;
	}
	
	public byte[] toByteArray() throws IOException{
		File file = toFile();
		FileInputStream in = new FileInputStream(file);
		return IOUtils.toByteArray(in);
	}
	
	public byte[] toByteArray(String name) throws IOException{
		File file = toFile(name);
		FileInputStream in = new FileInputStream(file);
		return IOUtils.toByteArray(in);
	}
	
	public void save (OutputStream os) throws IOException {
		this.document.save(os);		
	}

	@Override
	public void close() throws IOException {
		this.document.close();
	}
	
}
