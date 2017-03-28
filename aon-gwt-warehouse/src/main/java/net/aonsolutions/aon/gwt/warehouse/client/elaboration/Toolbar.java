package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class Toolbar extends Composite {

	interface ToolbarBinder extends UiBinder<Widget, Toolbar> {
	}

	private static final ToolbarBinder binder = GWT.create(ToolbarBinder.class);

	@UiField
	Label title;
	@UiField
	Label subtitle;
	
	@UiField
	Button accept;
	@UiField
	Button back;
	@UiField
	Button reset;
	@UiField
	Button remove;
	@UiField
	Button download;
	

	public Toolbar() {
		initWidget(binder.createAndBindUi(this));
	}

	// -------------------------------------------------------------- UiHandler

	protected abstract void accept();
	
	protected abstract void back();

	protected abstract void reset();

	protected abstract void remove();

	protected abstract void download();

	@UiHandler("accept")
	public void onAccept(ClickEvent event) {
		accept();
	}
	
	@UiHandler("back")
	public void onBack(ClickEvent event) {
		back();
	}

	@UiHandler("reset")
	public void onReset(ClickEvent event) {
		reset();
	}

	@UiHandler("remove")
	public void onRemove(ClickEvent event) {
		remove();
	}

	@UiHandler("download")
	public void onDownload(ClickEvent event) {
		download();
	}

}
