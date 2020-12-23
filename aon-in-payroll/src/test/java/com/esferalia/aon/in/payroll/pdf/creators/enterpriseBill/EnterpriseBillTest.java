package com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class EnterpriseBillTest {

	@Test
	public void EnterpriseBillCreationTest(){
		ArrayList<EnterpriseBillEntry> entries = new ArrayList<>();
		for (int i = 0; i < 10; i++)
			entries.add(new EnterpriseBillEntry("Description",(int)random(0,100),random(0,100),(int)random(0,100),random(0,100)));

		EnterpriseBill bill = new EnterpriseBill(
				null,
				true,
				"1239022342",
				new Date(),
				"59153583F",
				"",
				"Plaza Castilla 15, 6A",
				"Villabajo",
				entries,
				random(0,100),
				20,
				random(0,100),
				random(0,100),
				new Date(),
				"efectivo",
				"0021 1213 1231 0001",
				random(1,100),
				100,
				100,
				null
		);
		try {EnterpriseBillTemplate.create(null,bill);}
		catch (IOException e) {e.printStackTrace();}
	}


	//RANDOM BETWEEN X AND Y
	public double random(double x, double y){
		return x - Math.random()*y;
	}

}
