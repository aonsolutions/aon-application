package com.esferalia.aon.in.payroll.img;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.xml.sax.SAXException;

import com.amazonaws.services.textract.model.Document;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfImage;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;

import es.translogia.tedi.ewok.TediNif;
import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.img.InvoiceIMGException;
import solutions.aon.in.invoice.templates.ParserContext;

public class DniParserMain {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, InvoiceIMGException, UnknownInvoiceException, UnknownPDFException {

		InputStream is6 = new FileInputStream("/tmp/dniPapaDelante.pdf");
		InputStream is = new FileInputStream("/tmp/dniPapaDelante.jpg");
		MyDniDataListener listener = new MyDniDataListener();
		
		DNIParser.parse(is6);
		String text = DNIParser.getText();
		DNIParser.getOldDniFront(text, listener);
		
		byte[] bytes = IOUtils.toByteArray(is);
		text =	DNIParser.extractImage(bytes);
		DNIParser.getOldDniFront(text, listener);


	}
	
	
	

	
}
