package com.esferalia.aon.gwt.fiscal.client.mod111;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model111AEAT extends Model111Base {
	
	
	public Model111AEAT(IFiscalModelCallback<Mod111> callback) {
		super(callback);
	}

	@Override
	public Widget getInfoPanel(Mod111 mod111) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel( mod111
				,"Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/GH01.shtml"));
		panel.add(getAnchorPanel( mod111
				,"Informaci\u00F3n general." 
				,"http://www.agenciatributaria.es/AEAT.internet/GH01/informacion.shtml"));
		panel.add(getAnchorPanel(mod111
				,"Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/GH01.shtml"));
		return panel;
	}


}
