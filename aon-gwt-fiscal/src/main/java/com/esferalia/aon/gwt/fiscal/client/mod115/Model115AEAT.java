package com.esferalia.aon.gwt.fiscal.client.mod115;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model115AEAT extends Model115Base {
	
	
	public Model115AEAT(IFiscalModelCallback<Mod115> callback) {
		super(callback);
	}

	@Override
	public Widget getInfoPanel(Mod115 mod115) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel( mod115
				,"Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/GH01.shtml"));
		panel.add(getAnchorPanel( mod115
				,"Informaci\u00F3n general." 
				,"http://www.agenciatributaria.es/AEAT.internet/GH01/informacion.shtml"));
		panel.add(getAnchorPanel(mod115
				,"Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/GH01.shtml"));
		return panel;
	}


}
