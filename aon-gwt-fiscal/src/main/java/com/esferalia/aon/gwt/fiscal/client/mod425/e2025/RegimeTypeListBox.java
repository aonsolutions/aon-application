package com.esferalia.aon.gwt.fiscal.client.mod425.e2025;

import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.ListBox;

public class RegimeTypeListBox extends ListBox {

	public RegimeTypeListBox() {
		setWidth("60px");
		
		// Régimen aplicable
		// 1 - Régimen ordinario
		// 2 - Régimen especial de bienes usados
		// 3 - Régimen especial de objetos de arte, antigüedades y objetos de colección
		// 4 - Régimen especial de comerciantes minoristas
		// 5 - Régimen especial simplificado
		// 6 - Régimen especial de la agricultura, ganadería y pesca
		// 7 - Régimen especial de agencias de viajes
		// 8 - Régimen especial aplicable a las operaciones con oro de inversión
		// 9 - Régimen especial del grupo de entidades
		// 10 - Régimen especial del pequeño empresario o profesional
		// 11 - Régimen especial del criterio de caja	
		addItem("---","");
		addItem("R\u00E9gimen ordinario","1");
		addItem("R\u00E9gimen especial de bienes usados","2");
		addItem("R\u00E9gimen especial de obj. de arte, antig. y obj. de colecci\u00F3n","3");
		addItem("R\u00E9gimen especial de comerciantes minoristas","4");
		addItem("R\u00E9gimen especial simplificado","5"); 
		addItem("R\u00E9gimen especial de la agricultura, ganader\u00EDa y pesca","6");
		addItem("R\u00E9gimen especial de agencias de viajes","7");
		addItem("R\u00E9gimen especial aplicable a las op. con oro de inversi\u00F3n","8");
		addItem("R\u00E9gimen especial del grupo de entidades","9");
		addItem("R\u00E9gimen especial del peque\u00F1o empresario o profesional","10");
		addItem("R\u00E9gimen especial del criterio de caja","11");
		
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
