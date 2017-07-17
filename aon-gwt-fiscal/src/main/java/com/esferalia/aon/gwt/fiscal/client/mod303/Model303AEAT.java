package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model303AEAT extends Model303Base {
	
	
	public Model303AEAT(IFiscalModelCallback<Mod303> callback) {
		super(callback);
	}

	@Override
	public Widget getInfoPanel(Mod303 mod303) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel( mod303
				,"Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/G414.shtml"));
		panel.add(getAnchorPanel( mod303
				,"Informaci\u00F3n general." 
				,"https://www.agenciatributaria.gob.es/AEAT.sede/Ayuda/G414.shtml"));
		panel.add(getAnchorPanel(mod303
				,"Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/G414.shtml"));
		return panel;
	}


}
