package com.esferalia.aon.gwt.issues.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperDialog;

public abstract class AonDialog  extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, AonDialog> {}
  
    private static Binder binder = GWT.create(Binder.class);

	@UiField PaperDialog dialog;
	@UiField Label title;
	@UiField HTMLPanel content;
	@UiField PaperButton accept;
	@UiField PaperButton cancel;
	
	public AonDialog(String title, Widget widget) {
		initWidget(binder.createAndBindUi(this));
    	
    	this.title.setText(title);
    	setTitle(title);
    	setContent(widget);
	}
	
	protected abstract void onAccept();
	
	protected abstract void onCancel();
	
	void setTitleText(String title){
		this.title.setText(title);
	}
	
	private void setContent(Widget widget) {
		this.content.add(widget);
	}
	
	public void open(){
		dialog.open();
	}
	
	@UiHandler("accept")
	void acceptClick(ClickEvent event) {
    	onAccept();
    }
	
	@UiHandler("cancel")
	void cancelClick(ClickEvent event) {
    	onCancel();
    }
	
}
