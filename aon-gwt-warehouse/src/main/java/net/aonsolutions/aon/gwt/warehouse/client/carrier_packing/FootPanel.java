package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class FootPanel extends Composite {

	interface Binder extends UiBinder<Widget, FootPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	CarrierPacking parent;
	public FootPanel(CarrierPacking parent) {
		this.parent = parent;
		initWidget(binder.createAndBindUi(this));
		getTabLayout().selectTab(1);

	}
	
	@UiField MinimizePanel footPanel;
	@UiField TabLayoutPanel tabLayout;
	@UiField ScrollPanel selectionPanel;
	@UiField ScrollPanel parameterPanel;
	
	
	public TabLayoutPanel getTabLayout() {
		return tabLayout;
	}
	
	public ScrollPanel getSelectionPanel() {
		return selectionPanel;
	}
	
	public ScrollPanel getParameterPanel() {
		return parameterPanel;
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
	}
	
	public void closeFootPanel() {
		parent.southContentSize(30.0);
	}
}
