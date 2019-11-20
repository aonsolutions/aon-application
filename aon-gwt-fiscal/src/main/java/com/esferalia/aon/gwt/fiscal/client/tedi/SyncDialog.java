package com.esferalia.aon.gwt.fiscal.client.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class SyncDialog extends PopupPanel  {
	
    interface Binder extends UiBinder<HTMLPanel, SyncDialog> {}
  
    private static Binder binder = GWT.create(Binder.class);

	@UiField Label title;
	@UiField HTMLPanel content;
	@UiField Button accept;
	@UiField CheckBox check;
	@UiField Label text;

	public SyncDialog() {
		setWidget(binder.createAndBindUi(this));
    	this.title.setText("SINCRONIZAR");
    	setTitle("SINCRONIZAR");
    	check.setVisible(true);
    	text.setVisible(false);
    	//setContent(widget);
		setGlassEnabled(true);
		setAutoHideEnabled(true);
		setStyleName(AON.AON_CSS.aonDialogBoxShadow());
		getElement().getStyle().setBackgroundColor("#FFF");
	}

	public SyncDialog(Boolean sure) {
		setWidget(binder.createAndBindUi(this));
    	this.title.setText(sure ? "CREAR EMPRESA" : "SINCRONIZAR");
    	setTitle(sure ? "CREAR EMPRESA" : "SINCRONIZAR");
    	check.setVisible(!sure);
    	text.setVisible(sure);
    	//setContent(widget);
		setGlassEnabled(true);
		setAutoHideEnabled(true);
		setStyleName(AON.AON_CSS.aonDialogBoxShadow());
		getElement().getStyle().setBackgroundColor("#FFF");
	}
	
	public SyncDialog(String title, Widget widget) {
		setWidget(binder.createAndBindUi(this));
    	this.title.setText(title);
    	setTitle(title);
    	setContent(widget);
		setGlassEnabled(true);
		setAutoHideEnabled(true);
		setStyleName(AON.AON_CSS.aonDialogBoxShadow());
		getElement().getStyle().setBackgroundColor("#FFF");
	}
	
	protected abstract void onAccept();
	
	void setTitleText(String title){
		this.title.setText(title);
	}
	
	private void setContent(Widget widget) {
		this.content.add(widget);
	}
	
	public HTMLPanel getContentWidget(){
		return content;
	}
	
	public Button getAccept(){
		return accept;
	}
	
	@UiHandler("accept")
	void acceptClick(ClickEvent event) {
    	onAccept();
    }
	
}
