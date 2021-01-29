package com.esferalia.aon.in.payroll.pdf.creators.budget;


import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Optional;

import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.creators.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.creators.budget.beans.Budget;
import com.esferalia.aon.in.payroll.pdf.creators.budget.beans.Client_data;

public class budgetTest {

	@Test
	public void budgetPrintTest() {
		
		Client_data client = new Client_data("AON PRUEBAS", "CL. Dato 23", "ES-123789147189", "Vitoria - Gazteiz","01003", "ARABA/ALAVA", "+34900231276", "609584736", "admin@aonsolutions.com", "Juan");
		HashMap<Optional<String>, Optional<Double>> products = new HashMap<Optional<String>, Optional<Double>>();
		Budget budget = new Budget("2018/000012/0",client,products);
		
		try {
			PdfMaker.print_budget(budget);
		}catch(Exception e) {
			e.printStackTrace();
			fail("Unexpected exception " + e);
		}
		
		
		
		
		
	}

}
