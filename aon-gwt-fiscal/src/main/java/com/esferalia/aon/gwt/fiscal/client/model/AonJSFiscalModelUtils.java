package com.esferalia.aon.gwt.fiscal.client.model;

import com.google.gwt.core.client.JsDate;

public class AonJSFiscalModelUtils {
	
	private AonJSFiscalModelUtils() {
	}
	
	public static JsDate getJsDate(int day, int month, int year) {
		return JsDate.create( year , month, day);
	}

	/**
	 * @return En Nov. y Dic. se devuelve el año actual, si no suponemos que se quiere hacer el modelo del ejercicio anterior.
	 */
	public static int guessModelYear() {
		return guessModelYear(JsDate.create( ));
	}
	public static int guessModelYear(JsDate jsDate) {
		return jsDate.getMonth() > 10 ?  jsDate.getFullYear() : (jsDate.getFullYear() - 1);
	}

}

