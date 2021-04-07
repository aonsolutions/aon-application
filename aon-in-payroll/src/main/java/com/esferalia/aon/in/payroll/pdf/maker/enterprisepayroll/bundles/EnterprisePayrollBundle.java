package com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.bundles;

import java.util.ListResourceBundle;

public class EnterprisePayrollBundle extends ListResourceBundle{

	 private Object[][] contents = {
			 {"DATE FORMAT","dd/MM/yyyy"},
			 {"EMPLOYEE","Trabajador"},
			 {"TYPE","Tipo"},
			 {"ACCRUED","Devengado"},
			 {"EMPLOYEE SS","S.S.Trab."},
			 {"IRPF","I.R.P.F"},
			 {"DEDUCTIONS","Deducciones"},
			 {"AMOUNT","Liquido"},
			 {"ENTERPRISE SS","S.S. Empr."},
			 {"TOTAL COST","Coste total"},
			 {"TOTAL SS","Total S.S"},
			 {"TOTAL","Total"},
			 {"SUBTOTAL","Subtotal"},
			 {"PAGE","Página"},
	 };
	
	@Override
	protected Object[][] getContents() {
		return contents;
	}

}
