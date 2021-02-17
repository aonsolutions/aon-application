package com.esferalia.aon.in.payroll.pdf.creators.pdf_lab.bundles;

import java.util.ListResourceBundle;

public class Demo_bundle extends ListResourceBundle{

	 private Object[][] contents = {
			 {"DATE FORMAT","dd/MM/yyyy"},
			 {"TITLE","Titulo del pdf"},
			 {"SUBTITLE","subtitulo del pdf"},
			 {"TEXT1","Juan"},
			 {"TEXT2","Diaz Delgado"},
			 {"TEXT3","18"},
			 {"TEXT4","álava"},
			 {"TEXT5","álava es una de las mayores regiones del pais vasco y su extensión alcanza un tercio d él."},
			 {"PAGE","Página"},
	 };
	
	@Override
	protected Object[][] getContents() {
		return contents;
	}

}