package net.aonsolutions.aon.gwt.commercial.client.commission;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class CommissionCalculateToolbar extends Composite {

	interface ToolbarBinder extends UiBinder<Widget, CommissionCalculateToolbar> {
	}

	private static final ToolbarBinder binder = GWT.create(ToolbarBinder.class);
	
	@UiField Label label;
	
	@UiField Button clean;
	@UiField Button calculate;
	
	public CommissionCalculateToolbar(String title) {
		initWidget(binder.createAndBindUi(this));
		label.setText(title);
	}

	// -------------------------------------------------------------- UiHandler

	protected abstract void clean();
	protected abstract void calculate();
	
	@UiHandler("clean")
	public void onCleanClick(ClickEvent event) {
		clean();
	}
	
	public void setCleanVisible(Boolean visible){
		clean.setVisible(visible);
	}
	
	@UiHandler("calculate")
	public void onCalculateClick(ClickEvent event) {
		calculate();
	}
	
	public void setCalculateVisible(Boolean visible){
		calculate.setVisible(visible);
	}
}
