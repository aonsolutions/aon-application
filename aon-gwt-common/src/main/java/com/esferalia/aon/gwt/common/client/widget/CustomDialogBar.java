package com.esferalia.aon.gwt.common.client.widget;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CustomDialogBar extends Composite {
	
	public static interface Listener {
		void onClose();
	}

	static interface Binder extends UiBinder<Widget, CustomDialogBar> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField Label captionLabel;
	@UiField Button closeButton;
	
	private List<Listener> listeners;
	
	public CustomDialogBar() {
		initWidget(binder.createAndBindUi(this));
		
		listeners = new LinkedList<Listener>();
		closeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				fireOnClose();
			}
		});
	}
	
	public String getCaption(){
		return captionLabel.getText();
	}

	public void setCaption(String caption) {
		captionLabel.setText(caption);
	}
	
	public void addCloseHandler(Listener listener){
		listeners.add(listener);
	}

	public void removeCloseHandler(Listener listener){
		listeners.remove(listener);
	}
	

	
	
	
	private void fireOnClose(){
		for (Listener listener : listeners) {
			listener.onClose();
		}
	}

}
