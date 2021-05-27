package com.esferalia.aon.in.payroll.pdf.maker.payroll.bean;

import java.util.Locale;
import java.util.ResourceBundle;

public class PayrollTypes {

	public static enum Type {
		SALARY, ARREARS_WAGE, SETTLEMENT, EXTRAS;
	}

	public static String toString(Type type, Locale lang) {

		ResourceBundle words = ResourceBundle
				.getBundle("com.esferalia.aon.in.payroll.pdf.maker.payroll.bundle.PayrollTypesBundle", lang);
		switch (type)
		{
		case SALARY:
			return words.getString("SALARIO");
		case ARREARS_WAGE:
			return words.getString("ATRASOS");
		case SETTLEMENT:
			return words.getString("FINIQUITO");
		case EXTRAS:
			return words.getString("PAGAS EXTRAS");
		default:
			break;
		}

		return "";
	}

}
