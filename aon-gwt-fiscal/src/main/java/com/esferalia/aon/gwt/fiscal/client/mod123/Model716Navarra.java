package com.esferalia.aon.gwt.fiscal.client.mod123;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model716Navarra extends Model123Base {

	
	public Model716Navarra(IFiscalModelCallback<Mod123> callback) {
		super(callback);
	}
	
	@Override
	public Widget getInfoPanel(Mod123 mod123) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel(mod123,"Informaci\u00F3n tributaria"
				,"http://www.navarra.es/home_es/servicios/ficha/1824/Retenciones"));
		return panel;
	}

}
