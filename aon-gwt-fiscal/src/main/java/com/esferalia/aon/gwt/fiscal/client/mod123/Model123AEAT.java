package com.esferalia.aon.gwt.fiscal.client.mod123;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model123AEAT extends Model123Base {
	
	
	public Model123AEAT(IFiscalModelCallback<Mod123> callback) {
		super(callback);
	}

	@Override
	public Widget getInfoPanel(Mod123 mod123) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel( mod123
				,"Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientoini/GH04.shtml"));
		panel.add(getAnchorPanel( mod123
				,"Informaci\u00F3n general." 
				,"http://www.agenciatributaria.es/AEAT.internet/GH04/informacion.shtml"));
		panel.add(getAnchorPanel(mod123
				,"Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/GH04.shtml"));
		return panel;
	}


}
