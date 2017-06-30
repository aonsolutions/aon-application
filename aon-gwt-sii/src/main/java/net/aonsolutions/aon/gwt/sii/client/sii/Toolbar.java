package net.aonsolutions.aon.gwt.sii.client.sii;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.aon.gwt.sii.client.ISii;
import net.aonsolutions.aon.gwt.sii.client.ISiiAsync;

public abstract class Toolbar extends Composite {

	final ISiiAsync impl = GWT.create(ISii.class);
	
	interface ToolbarBinder extends UiBinder<Widget, Toolbar> {
	}

	private static final ToolbarBinder binder = GWT.create(ToolbarBinder.class);
	
	@UiField Label label;
	@UiField HTMLPanel buttonPanel;
	
	public Toolbar(String title) {
		initWidget(binder.createAndBindUi(this));
		label.setText(title);
	}
	
	public Button addButton(String text, String icon){
		Button button = new Button();
		button.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		button.addStyleName(icon);
		button.setText(text);
		buttonPanel.add(button);
		return button;
	}
}
