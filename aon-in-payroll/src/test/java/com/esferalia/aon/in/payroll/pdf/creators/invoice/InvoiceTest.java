package com.esferalia.aon.in.payroll.pdf.creators.invoice;

import com.esferalia.aon.in.payroll.pdf.creators.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.JsonParseException;
import com.esferalia.aon.in.payroll.pdf.creators.invoice.beans.Invoice;
import com.esferalia.aon.in.payroll.pdf.creators.invoice.beans.InvoiceEntry;
import com.esferalia.aon.in.payroll.pdf.creators.invoice.beans.InvoiceFinance;
import com.esferalia.aon.in.payroll.pdf.creators.invoice.beans.InvoiceTax;
import com.esferalia.aon.in.payroll.pdf.creators.invoice.templates.InvoiceTemplate;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;


import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static junit.framework.Assert.fail;

public class InvoiceTest {
	
	@Test
	public void EnterpriseBillCreationTest(){
		System.out.println("\n\n-----------------------------------");
		System.out.println(" INVOICE CREATOR");
		System.out.println("-----------------------------------");
		System.out.println("\n Starting.....");
		
		ArrayList<InvoiceEntry> entries = new ArrayList<>();
		ArrayList<InvoiceTax> taxes = new ArrayList<>();
		ArrayList<InvoiceFinance> finances = new ArrayList<>();

		System.out.println(" Creating entries.....");
		for (int i = 1; i < 20; i++)
			entries.add(new InvoiceEntry("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris." + i,99999999.99,99999999.99,100,99999999.99));
		for (int i = 0; i < 3; i++)
			taxes.add(new InvoiceTax(999999.99,100,"IVA",999999.99));
		for (int i = 0; i < 3; i++)
			finances.add(new InvoiceFinance(new Date(), "Efectivo", "ES6621000418401234567891",99999999.99));

		Invoice bill = new Invoice(
				InvoiceTest.class.getResourceAsStream("TestLayer.png"),
				true,
				"1239022342",
				new Date(),
				"59153583F",
				"AON SOLUTIONS",
				"Plaza Castilla 15, 6A",
				"Villabajo",
				entries,
				taxes,
				finances,
				75,
				105,
				InvoiceTest.class.getResourceAsStream("qrcode.png")
		);
		
		System.out.println(" Setting up invoice.....");
		try {
			System.out.println(" Printing PDF file..... \n");
			InvoiceTemplate.create(new ByteArrayOutputStream(),bill,true);
			System.out.println(" >> DONE.");
		}
		catch (IOException e){fail("Can not read test resources");}
		catch (CanNotCreatePdfException e) {fail("Can not create pdf");}
	}

	@Test
	public void EnterpriseBillCreationTestWithJSONTest() {
		
		System.out.println("\n\n-----------------------------------");
		System.out.println(" INVOICE CREATOR (JSON MODE)");
		System.out.println("-----------------------------------");
		System.out.println("\n Starting.....");
		
		PrintInvoiceConfiguration config = new PrintInvoiceConfiguration();
		config.setAdjustImage(true)
			  .setDetailed(true)
			  .setBackgroundImage(InvoiceTest.class.getResourceAsStream("TestLayer.png"))
			  .setHeader(105)
			  .setFooter(75);
		System.out.println(" Setting up invoice.....");
		
		try {
			System.out.println(" Parsing json file.....");
			System.out.println(" Printing PDF file.....");
			PdfMaker.print_invoice(new ByteArrayOutputStream(),InvoiceTest.class.getResourceAsStream("factura.json"), config, InvoiceTest.class.getResourceAsStream("qrcode.png"));
			System.out.println(" >> DONE.");
		}
		catch (CanNotCreatePdfException e) {fail("Can not create pdf");}
		catch (JsonParseException e) {fail("Can not parse json");}
	}

	@Test
	public void EnterpriseBillCreationDemoTest() {
		
		System.out.println("\n\n-----------------------------------");
		System.out.println(" INVOICE CREATOR (DEMO MODE)");
		System.out.println("-----------------------------------");
		System.out.println("\n Starting.....");
		
		PrintInvoiceConfiguration config = new PrintInvoiceConfiguration();
		config.setAdjustImage(true)
				.setDetailed(true)
				.setBackgroundImage(InvoiceTest.class.getResourceAsStream("TestLayer.png"))
				.setHeader(105)
				.setFooter(75);

		try {PdfMaker.print_demo_invoice(new ByteArrayOutputStream(),config, InvoiceTest.class.getResourceAsStream("qrcode.png"));}
		catch (CanNotCreatePdfException e) {
			System.out.println(" Printing PDF file.....");
			fail("Can not create pdf" );
			System.out.println(" >> DONE.");
		}
		catch (IOException e) {fail("Can not access test resources");}
	}

	//RANDOM BETWEEN 0 AND Y
	public double random(double x, double y){
		return Math.random()*y;
	}

}
