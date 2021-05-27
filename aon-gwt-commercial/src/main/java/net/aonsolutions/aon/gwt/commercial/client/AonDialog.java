package net.aonsolutions.aon.gwt.commercial.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AonDialog extends PopupPanel  {
	
    interface Binder extends UiBinder<HTMLPanel, AonDialog> {}
  
    private static Binder binder = GWT.create(Binder.class);

	@UiField Label title;
	@UiField HTMLPanel content;
	@UiField Button close;
	@UiField Button accept;
	@UiField Button cancel;

	public AonDialog(String title, Widget widget) {
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
	
	protected abstract void onCancel();
	
	void setTitleText(String title){
		this.title.setText(title);
	}
	
	private void setContent(Widget widget) {
		this.content.add(widget);
	}
	
	public Button getAccept(){
		return accept;
	}
	
	public Button getCancel(){
		return cancel;
	}
	
	public Button getClose(){
		return close;
	}
	
	@UiHandler("accept")
	void acceptClick(ClickEvent event) {
    	onAccept();
    }
	
	@UiHandler("cancel")
	void cancelClick(ClickEvent event) {
    	onCancel();
    }
	
	@UiHandler("close")
	void closeClick(ClickEvent event) {
    	hide();
    }
	
}
