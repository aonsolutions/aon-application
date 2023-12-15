package com.esferalia.aon.in.payroll.pdf.modAlava;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.esferalia.aon.in.payroll.pdf.util.PDFTextStripper;

class PDFExtracter  {

	public boolean accept(byte [] bytes) {
		try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
			PDDocument doc = Loader.loadPDF(is);
			return (doc != null);
		} catch (IOException e) {
			return false;
		}
	}

	public String extract(byte [] bytes) {
		try {
			PDDocument document = Loader.loadPDF(bytes);			
			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(document);
			if (isBlank(text)) {
				text = getImages(document)
					.stream()
					.filter(b -> new ImageExtracter().accept(b))
					.map(b -> new ImageExtracter().extract(b))
					.collect(Collectors.joining(System.lineSeparator()));
			}
			return text;
		} catch (Exception e) {
			return null;
		}
	}
	
	private static boolean isBlank(String cs) {
		if (cs == null || (cs.length()) == 0) {
			return true;
		}
		return cs.trim().length() == 0;
	}

	private static Collection<byte[]> getImages(PDDocument document) throws IOException {
		LinkedList<byte[]> images = new LinkedList<>();
		for (PDPage page : document.getPages()) {
			PDResources pdResources = page.getResources();
			for (COSName name : pdResources.getXObjectNames()) {
				PDXObject o = pdResources.getXObject(name);
				if (o instanceof PDImageXObject) {
					PDImageXObject image = (PDImageXObject) o;
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					ImageIO.write(image.getImage(), "PNG", byteArrayOutputStream);
					images.add(byteArrayOutputStream.toByteArray());
				}
			}
		}
		return images;
	}

}
