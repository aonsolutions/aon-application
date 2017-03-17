package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.nuevo;

import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class FootPanel extends Composite {

	interface Binder extends UiBinder<Widget, FootPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	CarrierPackingPrincipal principalParent;
	CarrierPackingDetail detailParent;
	Boolean isPrincipal;
	
	public FootPanel(CarrierPackingPrincipal parent) {
		this.principalParent = parent;
		this.isPrincipal = true;
		initWidget(binder.createAndBindUi(this));
	}
	
	public FootPanel(CarrierPackingDetail parent) {
		this.detailParent = parent;
		this.isPrincipal = false;
		initWidget(binder.createAndBindUi(this));
	}
	
	@UiField MinimizePanel footPanel;
	@UiField TabLayoutPanel tabPanel;
	
	public TabLayoutPanel getTabPanel() {
		return tabPanel;
	}
	
	
	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	
	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		openFootPanel();
	}
	
	
	public void openFootPanel() {
		Integer clientHeight = Window.getClientHeight();
		if(isPrincipal){principalParent.southContentSize(clientHeight.doubleValue() / 3);}
		else detailParent.southContentSize(clientHeight.doubleValue() / 3);
	}
	
	public void closeFootPanel() {
		if(isPrincipal){principalParent.southContentSize(30.0);}
		else detailParent.southContentSize(30.0);
	}
}
