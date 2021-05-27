package com.esferalia.aon.in.payroll.pdf.maker.budget.bundle;

import java.util.ListResourceBundle;

public class BudgetBundle extends ListResourceBundle{

	 private Object[][] contents = {
			 {"DATE FORMAT","dd/MM/yyyy"},
			 {"CLIENT DATA","Datos del cliente"},
			 {"NUMBER","Presupuesto Nº"},
			 {"DATE","Fecha"},
			 {"ENTERPRISE NAME","Razón social"},
			 {"NIF","N.I.F."},
			 {"ADDRESS","Dirección"},
			 {"CITY","Población"},
			 {"POSTAL CODE","C.Postal"},
			 {"PROVINCE","Provincia"},
			 {"PHONE","Teléfono"},
			 {"MOBILE","Móvil"},
			 {"EMAIL","Email"},
			 {"CONTACT","Contacto"},
			 {"PRODUCT DESCRIPTION","Descripción del producto o servicio a contratar"},
			 {"PRODUCT/SERVICE","Producto / Servicio"},
			 {"AMOUNT","Importe"},
			 {"TAX BASE","Base Imponible"},
			 {"TAX"," % IVA + "},
			 {"TOTAL AMOUNT","Importe total"},
			 {"ECONOMIC CONDITIONS","Consiciones económicas"},
	 };
	
	@Override
	protected Object[][] getContents() {
		return contents;
	}

}
