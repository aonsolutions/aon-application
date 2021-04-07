package com.esferalia.aon.in.payroll.pdf.creators.budget;

import static org.junit.Assert.fail;

import java.io.FileOutputStream;
import java.util.ArrayList;

import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.maker.budget.beans.Budget;
import com.esferalia.aon.in.payroll.pdf.maker.budget.beans.Budget_item;
import com.esferalia.aon.in.payroll.pdf.maker.budget.beans.Client_data;
import com.esferalia.aon.in.payroll.pdf.maker.budget.beans.Term;

public class budgetTest {

	@Test
	public void budgetPrintTest() {

		Client_data client = new Client_data("AON PRUEBAS", "CL. Dato 23", "ES-123789147189", "Vitoria - Gazteiz",
				"01003", "ARABA/ALAVA", "+34900231276", "609584736", "admin@aonsolutions.com", "Juan");
		ArrayList<Budget_item> products = new ArrayList<>();
		ArrayList<Term> terms = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			Budget_item it = new Budget_item("Producto Ejemplo " + i, i * 10 + .99);
			products.add(it);

			Term term = new Term("CONDICION ECONOMICA NÚMERO 1",
					"La parte contratante de la primera parte será considerada como la parte contratante "
					+ "de la primera parte. La parte contratante de la segunda parte será considerada como "
					+ "la parte contratante de la segunda parte. Siendo la tercera parte considerada como la cuarta "
					+ "parte mientras que la quinta parte será considerada parte de la tercera parte, siendo esta tercera "
					+ "parte parte de la primera y segunda parteLa parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante La parte contratante de la primera parte será considerada como la parte contratante ");
			
			terms.add(term);
		}

		Budget budget = new Budget("2018/000012/0", client, products, terms, 99.99, 99.99, 99.99, 99.99, 99.99);

		try {PdfMaker.printBudget(new FileOutputStream("./budget.pdf"), budget);}
		catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception " + e);
		}

	}

}
