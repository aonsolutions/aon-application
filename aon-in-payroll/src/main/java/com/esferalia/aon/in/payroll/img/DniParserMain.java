package com.esferalia.aon.in.payroll.img;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
//
//		InputStream inputStream1 = new FileInputStream("/tmp/dniGuille.pdf");
//		InputStream inputStream2 = new FileInputStream("/tmp/dniGuille2.pdf");
//		InputStream inputStream1 = new FileInputStream("/tmp/dniJaviDelante.pdf");
//		InputStream inputStream2 = new FileInputStream("/tmp/dniJaviDetras.pdf");
		InputStream inputStream1 = new FileInputStream("/tmp/dniJuanma1.pdf");
		InputStream inputStream2 = new FileInputStream("/tmp/dniJuanma2.pdf");
		
		DNIParser dnip = new DNIParser();
		MyDniDataListener listener = new MyDniDataListener();
		
		List<InputStream> inputStreams = new ArrayList<InputStream>();        
		
		inputStreams.add(inputStream1);
		inputStreams.add(inputStream2);
        
		dnip.parse(inputStreams);
		String text = dnip.getText();
		dnip.getNewDniBothPdf(text, listener);
	}
	
	
	

	
}
