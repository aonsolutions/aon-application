package com.esferalia.aon.gwt.fiscal.client.mod425.e2025;

import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.ListBox;

public class ActivityTypeListBox extends ListBox {

	public ActivityTypeListBox() {
		setWidth("60px");
		
		// Clave
		// 1 - Actividades sujetas al Impuesto de Actividades Económicas (Actividades empresariales)
		// 2 - Actividades sujetas al Impuesto de Actividades Económicas (Actividades Profesionales y Artísticas)
		// 3 - Actividades arrendadoras de locales de negocios
		// 4 - Actividades Agrícolas y Ganaderas no sujetas al IAE
		// 5 - Sujetos pasivos que no hayan iniciado su actividad y no estén dados de alta en el IAE
		addItem("---","");
		addItem("Actividades sujetas al IAE (Actividades empresariales)", "1");
		addItem("Actividades sujetas al IAE (Actividades Profesionales y Art\u00EDsticas)", "2");
		addItem("Actividades arrendadoras de locales de negocios", "3");
		addItem("Actividades Agr\u00EDcolas y Ganaderas no sujetas al IAE", "4");
		addItem("Suj. pas. que no han iniciado su actividad y no est\u00E9n de alta en IAE", "5");
	}	
	
	public void setSelectedValue(String value) {
		setSelectedIndex(0);
		if (AonStringUtils.isNotBlank(value)) {
			for (int i = 0; i < getItemCount(); i++) {
				if (getValue(i).equals(value)) {
					setSelectedIndex(i);
				}
			}
		}
	}
	
}

