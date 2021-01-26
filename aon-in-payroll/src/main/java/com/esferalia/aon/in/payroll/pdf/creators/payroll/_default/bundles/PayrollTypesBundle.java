package com.esferalia.aon.in.payroll.pdf.creators.payroll._default.bundles;

import java.util.ListResourceBundle;

public class PayrollTypesBundle extends ListResourceBundle{
	private Object[][] contents = {
		        { "SALARIO","Salario" },
		        { "ATRASOS", "Atrasos" },
		        { "FINIQUITO", "Finiquito" },
		        { "HORAS EXTRAS", "Horas extras" },
		    };
	
	@Override
	protected Object[][] getContents() {return contents;}

}
