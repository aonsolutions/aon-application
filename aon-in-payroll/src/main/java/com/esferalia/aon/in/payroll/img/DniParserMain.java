package com.esferalia.aon.in.payroll.img;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.xml.sax.SAXException;

import com.amazonaws.services.textract.model.Document;

import es.translogia.tedi.ewok.TediNif;
import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.img.InvoiceIMGException;
import solutions.aon.in.invoice.templates.ParserContext;

public class DniParserMain {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, InvoiceIMGException, UnknownInvoiceException {

		InputStream is = new FileInputStream("/tmp/dniJuanmaDelante.jpg");
		InputStream is2 = new FileInputStream("/tmp/dniPapaDelante.jpg");
		MyDniDataListener listener = new MyDniDataListener();
		byte[] bytes = IOUtils.toByteArray(is);
		String text =	DNIParser.extract(bytes);
		DNIParser.getDataNewFormatDni(text, listener);
		bytes = IOUtils.toByteArray(is2);
		text = DNIParser.extract(bytes);
		DNIParser.getDataOldFormatDni(text, listener);
	}
	
	
	

	
}
