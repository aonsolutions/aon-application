package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

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
	
	private MainElaboration parent;
	
	public FootPanel(MainElaboration parent) {
		this.parent = parent;
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
		parent.changeSouthContentSize(clientHeight.doubleValue() / 3);
	}
	
	public void closeFootPanel() {
		parent.changeSouthContentSize(30.0);
	}
}
