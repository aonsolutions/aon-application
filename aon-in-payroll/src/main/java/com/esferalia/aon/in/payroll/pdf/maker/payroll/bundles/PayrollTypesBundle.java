package com.esferalia.aon.in.payroll.pdf.maker.payroll.bundles;

import java.util.ListResourceBundle;

public class PayrollTypesBundle extends ListResourceBundle{
	private Object[][] contents = {
		        { "SALARIO","Salario" },
		        { "ATRASOS", "Atrasos" },
		        { "FINIQUITO", "Finiquito" },
		        { "PAGAS EXTRAS", "Pagas extras" },
		    };
	
	@Override
	protected Object[][] getContents() {return contents;}

}
