package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public abstract class Toolbar extends Composite {

	interface ToolbarBinder extends UiBinder<Widget, Toolbar> {
	}

	private static final ToolbarBinder binder = GWT.create(ToolbarBinder.class);
	
	@UiField Button back;
	@UiField Button reset;
	@UiField Button remove;
	@UiField Button print;
	@UiField Button email;
	
	public Toolbar() {
		initWidget(binder.createAndBindUi(this));
	}
	

	// -------------------------------------------------------------- UiHandler

	protected abstract void back();
	protected abstract void reset();
	protected abstract void remove();
	protected abstract void print();
	protected abstract void email();


	@UiHandler("back")
	public void onBackClick(ClickEvent event) {
		back();
	}
	
	public void setBackVisible(Boolean visible){
		back.setVisible(visible);
	}
	

	@UiHandler("reset")
	public void onResetClick(ClickEvent event) {
		reset();
	}

	public void setResetVisible(Boolean visible){
		reset.setVisible(visible);
	}
	
	@UiHandler("remove")
	public void onRemoveClick(ClickEvent event) {
		remove();
	}
	
	public void setRemoveVisible(Boolean visible){
		remove.setVisible(visible);
	}
	
	@UiHandler("print")
	public void onPrintClick(ClickEvent event) {
		print();
	}
	
	public void setPrintVisible(Boolean visible){
		print.setVisible(visible);
	}
	
	@UiHandler("email")
	public void onEmailClick(ClickEvent event) {
		email();
	}
	
	public void setEmailVisible(Boolean visible){
		email.setVisible(visible);
	}
	
	

}
