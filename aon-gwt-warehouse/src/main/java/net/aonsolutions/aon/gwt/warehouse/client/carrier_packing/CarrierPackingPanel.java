package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CarrierPackingPanel extends Composite{
	
	interface Binder extends UiBinder<Widget, CarrierPackingPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	public CarrierPackingPanel() {
		initWidget(binder.createAndBindUi(this));
	}
	
	public CarrierPackingPanel(JsCarrierPacking js) {
		initWidget(binder.createAndBindUi(this));
	}

}
