package com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class EnterpriseBillTest {

	@Test
	public void EnterpriseBillCreationTest(){
		ArrayList<EnterpriseBillEntry> entries = new ArrayList<>();
		ArrayList<EnterpriseBillTax> taxes = new ArrayList<>();
		ArrayList<EnterpriseBillFinance> finances = new ArrayList<>();

		for (int i = 1; i < 100; i++)
			entries.add(new EnterpriseBillEntry("Description " + i,(int)random(0,100),random(0,100),(int)random(0,100),random(0,100)));
		for (int i = 0; i < 3; i++)
			taxes.add(new EnterpriseBillTax(random(0,100),(int)random(0,100),"IVA",random(0,100)));
		for (int i = 0; i < 3; i++)
			finances.add(new EnterpriseBillFinance(new Date(), "Efectivo", "ES6621000418401234567891",random(0,100)));

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
		try {EnterpriseBillTemplate.create(null,bill);}
		catch (IOException e) {e.printStackTrace();}
	}


	//RANDOM BETWEEN X AND Y
	public double random(double x, double y){
		return Math.random()*y;
	}

}
