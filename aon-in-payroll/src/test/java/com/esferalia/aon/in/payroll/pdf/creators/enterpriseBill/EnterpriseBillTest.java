package com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill;

import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.JsonParseException;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static junit.framework.Assert.fail;

public class EnterpriseBillTest {

	@Test
	public void EnterpriseBillCreationTest(){
		ArrayList<EnterpriseBillEntry> entries = new ArrayList<>();
		ArrayList<EnterpriseBillTax> taxes = new ArrayList<>();
		ArrayList<EnterpriseBillFinance> finances = new ArrayList<>();

		for (int i = 1; i < 100; i++)
			entries.add(new EnterpriseBillEntry("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum." + i,99999999.99,99999999.99,100,99999999.99));
		for (int i = 0; i < 3; i++)
			taxes.add(new EnterpriseBillTax(999999.99,100,"IVA",999999.99));
		for (int i = 0; i < 3; i++)
			finances.add(new EnterpriseBillFinance(new Date(), "Efectivo", "ES6621000418401234567891",99999999.99));

		EnterpriseBill bill = new EnterpriseBill(
				EnterpriseBillTest.class.getResourceAsStream("TestLayer.png"),
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
				EnterpriseBillTest.class.getResourceAsStream("qrcode.png")
		);
		try {EnterpriseBillTemplate.create(null,bill,true);}
		catch (IOException e){fail("Can not read test resources");}
		catch (CanNotCreatePdfException e) {fail("Can not create pdf");}
	}

	@Test
	public void EnterpriseBillCreationTestWithJSONTest() {
		PrintInvoiceConfiguration config = new PrintInvoiceConfiguration();
		config.setAdjustImage(true)
			  .setDetailed(true)
			  .setBackgroundImage(EnterpriseBillTest.class.getResourceAsStream("TestLayer.png"))
			  .setHeader(105)
			  .setFooter(75);

		try {EnterpriseBillTemplate.create_with_json(EnterpriseBillTest.class.getResourceAsStream("factura.json"), config, EnterpriseBillTest.class.getResourceAsStream("qrcode.png"));}
		catch (CanNotCreatePdfException e) {fail("Can not create pdf");}
		catch (JsonParseException e) {fail("Can not parse json");}
	}

	@Test
	public void EnterpriseBillCreationDemoTest() {
		PrintInvoiceConfiguration config = new PrintInvoiceConfiguration();
		config.setAdjustImage(true)
				.setDetailed(true)
				.setBackgroundImage(EnterpriseBillTest.class.getResourceAsStream("TestLayer.png"))
				.setHeader(105)
				.setFooter(75);

		try {EnterpriseBillTemplate.demoPdf(config, EnterpriseBillTest.class.getResourceAsStream("qrcode.png"));}
		catch (CanNotCreatePdfException e) {fail("Can not create pdf" );}
		catch (IOException e) {fail("Can not access test resources");}
	}

	//RANDOM BETWEEN X AND Y
	public double random(double x, double y){
		return Math.random()*y;
	}

}
