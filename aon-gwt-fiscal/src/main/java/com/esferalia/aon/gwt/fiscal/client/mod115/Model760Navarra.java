package com.esferalia.aon.gwt.fiscal.client.mod115;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model760Navarra extends Model115Base {

	
	public Model760Navarra(IFiscalModelCallback<Mod115> callback) {
		super(callback);
	}
	
	@Override
	public Widget getInfoPanel(Mod115 mod115) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel(mod115,"Informaci\u00F3n tributaria"
				,"http://www.navarra.es/home_es/servicios/ficha/1824/Retenciones"));
		return panel;
	}

}
