package com.esferalia.aon.in.payroll.pdf.maker.budget;

import static com.esferalia.aon.in.payroll.pdf.maker.PdfMaker.printBudget;
import static org.junit.Assert.fail;

import java.io.FileOutputStream;
import java.util.Locale;

import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.maker.budget.bean.Budget.BudgetBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.budget.bean.BudgetItem;
import com.esferalia.aon.in.payroll.pdf.maker.budget.bean.ClientData;
import com.esferalia.aon.in.payroll.pdf.maker.budget.bean.ClientData.ClientDataBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.budget.bean.Term;

public class budgetTest {

	@Test
	public void budgetPrintTest() {

		BudgetBuilder budgetBuilder = new BudgetBuilder();
		ClientDataBuilder ClientDataBuilder = new ClientDataBuilder();

		ClientDataBuilder
		.setEnterpriseName("AON PRUEBAS")
		.setAddress("CL. Dato 23")
		.setCity("Vitoria - Gazteiz")
		.setPostalCode("01003")
		.setProvince("ARABA/ALAVA")
		.setEmail("admin@aonsolutions.com")
		.setContact("Juan")
		.setPhone("+34900231276")
		.setMobile("609584736")
		.setNif("ES-123789147189");

		ClientData client = ClientDataBuilder.build();

		for (int i = 0; i < 3; i++)
		{
			BudgetItem it = new BudgetItem("Producto Ejemplo " + i, i * 10 + .99);
			Term term = new Term("CONDICION ECONOMICA NÚMERO " + i,
				"La parte contratante de la primera parte será considerada como la parte contratante "
				+ "de la primera parte. La parte contratante de la segunda parte será considerada como "
				+ "la parte contratante de la segunda parte. Siendo la tercera parte considerada como la cuarta "
				+ "parte mientras que la quinta parte será considerada parte de la tercera parte, siendo esta tercera "
				+ "parte parte de la primera y segunda parteLa parte contratante de la primera parte será considerada "
				+ "como la parte contratante La parte contratante de la primera parte será considerada como la parte "
				+ "contratante La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
				+ "La parte contratante de la primera parte será considerada como la parte contratante "
			);

			
			budgetBuilder.addProduct(it);
			budgetBuilder.addTerm(term);
		}

		budgetBuilder
		.setBudgetNumber("2018/000012/0")
		.setBudgetTotal(99999.99)
		.setTaxAdd(99999.99)
		.setTaxBase(99999.99)
		.setTaxPercent(99.99)
		.setTaxTotal(99999.99)
		.setClient(client);

		try
		{
			printBudget(new FileOutputStream("./budget.pdf"),
					new BudgetPrintConfiguration(new Locale("Es"), budgetBuilder.build()));
		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception " + e);
		}

	}

}
