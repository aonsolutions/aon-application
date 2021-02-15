package com.esferalia.aon.in.payroll.pdf.creators.budget;


import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.creators.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.creators.budget.beans.Budget;
import com.esferalia.aon.in.payroll.pdf.creators.budget.beans.Budget_item;
import com.esferalia.aon.in.payroll.pdf.creators.budget.beans.Client_data;

public class budgetTest {

	@Test
	public void budgetPrintTest() {
		
		Client_data client = new Client_data("AON PRUEBAS", "CL. Dato 23", "ES-123789147189", "Vitoria - Gazteiz","01003", "ARABA/ALAVA", "+34900231276", "609584736", "admin@aonsolutions.com", "Juan");
		ArrayList<Budget_item> products = new ArrayList<>();
		
		for (int i = 0; i < 10; i++) {
			Budget_item it = new Budget_item("Producto Ejemplo " + i, i*10000000 + .99);
			products.add(it);
		}		
		
		Budget budget = new Budget("2018/000012/0",client,products,99.99,99.99,99.99,99.99,99.99);
		
		try {
			PdfMaker.print_budget(budget);
		}catch(Exception e) {
			e.printStackTrace();
			fail("Unexpected exception " + e);
		}
		
		
		
		
		
	}

}
