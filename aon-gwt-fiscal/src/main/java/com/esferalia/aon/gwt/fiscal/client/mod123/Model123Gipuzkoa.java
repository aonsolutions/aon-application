package com.esferalia.aon.gwt.fiscal.client.mod123;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model123Gipuzkoa extends Model123Base {
	
	public Model123Gipuzkoa(IFiscalModelCallback<Mod123> callback) {
		super(callback);
	}
	
	@Override
	public Widget getInfoPanel(Mod123 mod123) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel(mod123,"Informaci\u00F3n tributaria"
			, "https://w390w.gipuzkoa.net/WAS/CORP/LIATramitesWEB/"
			+ "cambiarLocale.do?cambiarLocale=true&idioma=es&tipoBusq="
			+ "busq_mat&ms=1421096256948&ver=1648"));
		return panel;
	}
}
