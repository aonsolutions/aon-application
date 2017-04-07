package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class PrincipalFootPanel extends Composite {

	interface Binder extends UiBinder<Widget, PrincipalFootPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
		
	CarrierPackingPrincipal parent;
	
	public API getAPI(){
		return parent.getAPI();
	}
	
	public PrincipalFootPanel(CarrierPackingPrincipal parent) {
		this.parent = parent;
		initWidget(binder.createAndBindUi(this));
		parameterPanel();
	}
	
	public void parameterPanel() {
		parameterPanel.setWidget(new ParameterPanel(parent));
	}
	
	
	@UiField MinimizePanel footPanel;
	@UiField TabLayoutPanel tabPanel;
	@UiField SimpleLayoutPanel parameterPanel;
	
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
		parent.southContentSize(clientHeight.doubleValue() / 3);
		parent.contentSplitLayoutPanel.animate(500);
	}
	
	public void closeFootPanel() {
		parent.southContentSize(30.0);
		parent.contentSplitLayoutPanel.animate(500);
	}
}
