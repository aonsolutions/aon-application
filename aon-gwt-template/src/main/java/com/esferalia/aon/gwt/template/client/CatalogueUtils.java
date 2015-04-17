package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

public class CatalogueUtils {
	public static Vector<String> catalogueList(){
		Vector<String> v = new Vector<String>();
		v.add("Lugar de Trabajo");
		v.add("Departamento");
		v.add("Código");
		v.add("Nombre");

		return v;
	}
}
