package com.esferalia.aon.in.payroll.pdf.creators.budget.bundles;

import java.util.ListResourceBundle;

public class BudgetBundle extends ListResourceBundle{

	 private Object[][] contents = {
	  	{"Presu","Percepciones salariales"}
	 };
	
	@Override
	protected Object[][] getContents() {
		return contents;
	}

}
