package com.esferalia.aon.in.payroll.img;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.services.textract.model.Document;
import com.amazonaws.services.textract.model.UnsupportedDocumentException;

import solutions.aon.in.invoice.pdf.InvoicePDFException;

public class DNIParserTest {

	@Test
	public void testExtractImage() {
		String file = "/tmp/document-31.pdf";
		try {
			InputStream is = new FileInputStream(file);
			byte[] bytes = IOUtils.toByteArray(is);
			DNIParser.extractImage(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedDocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testParse() {
		String file = "/tmp/dniJuanmaDelante.jpg";
		try {
			InputStream is = new FileInputStream(file);
			PDDocument doc = Loader.loadPDF(is);
			DNIParser.parser(doc);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetImages() {
		String file = "/tmp/afk.pdf";
		try {
			InputStream is = new FileInputStream(file);
			PDDocument doc = Loader.loadPDF(is);
			DNIParser.getImages(doc);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testParser() {
		String file = "/tmp/afk.pdf";
		try {
			InputStream is = new FileInputStream(file);
			PDDocument doc = Loader.loadPDF(is);
			DNIParser.parser(doc);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvoicePDFException e) {
			e.printStackTrace();
		}

	}


	@Test
	public void testGetNewDniFront() {
		String file = "/tmp/dniJuanmaDelante.jpg";
		MyDniDataListener listener = new MyDniDataListener();

		try {
			InputStream is = new FileInputStream(file);
			DNIParser.parse(is);
			String text = DNIParser.getText();
			DNIParser.getNewDniFront(text, listener);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetNewDniBackJpg() {
		String file = "/tmp/dniJuanmaDelante.jpg";
		MyDniDataListener listener = new MyDniDataListener();

		try {
			InputStream is = new FileInputStream(file);
			byte[] bytes = IOUtils.toByteArray(is);
			String text = DNIParser.extractImage(bytes);
			DNIParser.getNewDniBackJpg(text, listener);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedDocumentException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testGetNewDniBothjpg() {
		String file = "/tmp/dniJuanmaDelante.pdf";
		MyDniDataListener listener = new MyDniDataListener();

		try {
			InputStream is = new FileInputStream(file);
			byte[] bytes = IOUtils.toByteArray(is);
			String text = DNIParser.extractImage(bytes);
			DNIParser.getNewDniBothJpg(text, listener);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedDocumentException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testGetOldDniFront() {
		String file = "/tmp/dniBorjita.jpg";
//		String file2 = "/tmp/dniPapaDelante.pdf";
		MyDniDataListener listener = new MyDniDataListener();

		try {
			InputStream is = new FileInputStream(file);
			byte[] bytes = IOUtils.toByteArray(is);
			String text = DNIParser.extractImage(bytes);
			DNIParser.getOldDniFront(text, listener);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedDocumentException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetOldDniBackJpg() {
		String file = "/tmp/dniJuanmaDelante.jpg";
		MyDniDataListener listener = new MyDniDataListener();

		try {
			InputStream is = new FileInputStream(file);
			byte[] bytes = IOUtils.toByteArray(is);
			String text = DNIParser.extractImage(bytes);
			DNIParser.getOldDniBackJpg(text, listener);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedDocumentException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testGetoldDniBoth() {
		String file = "/tmp/dniJuanmaDelante.pdf";
		MyDniDataListener listener = new MyDniDataListener();

		try {
			InputStream is = new FileInputStream(file);
			DNIParser.parse(is);
			String text = DNIParser.getText();
			DNIParser.getOldDniBoth(text, listener);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedDocumentException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetNewDniBackPdf() {
		String file = "/tmp/dniJuanmaDelante.pdf";
		MyDniDataListener listener = new MyDniDataListener();
		try {
			InputStream is = new FileInputStream(file);
			DNIParser.parse(is);
			String text = DNIParser.getText();
			DNIParser.getNewDniBackPdf(text, listener);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedDocumentException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testgetNewDniBothPdf() {
		String file = "/tmp/dniJuanmaCompleto.pdf";
		MyDniDataListener listener = new MyDniDataListener();
		try {
			InputStream is = new FileInputStream(file);
			DNIParser.parse(is);
			String text = DNIParser.getText();
			DNIParser.getNewDniBothPdf(text, listener);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedDocumentException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetOldDniBackPdf() {
		String file = "/tmp/dniJuanmaCompleto.pdf";
		MyDniDataListener listener = new MyDniDataListener();
		try {
			InputStream is = new FileInputStream(file);
			DNIParser.parse(is);
			String text = DNIParser.getText();
			DNIParser.getOldDniBackPdf(text, listener);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedDocumentException e) {
			e.printStackTrace();
		}
	}

}
