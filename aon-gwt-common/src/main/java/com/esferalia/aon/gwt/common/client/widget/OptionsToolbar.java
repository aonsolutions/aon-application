package com.esferalia.aon.gwt.common.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class OptionsToolbar extends Composite {
	
	public interface Listener {
		
		void onNewButtonClick(ClickEvent event);
		
		void onPasteButtonClick(ClickEvent event);
		
		void onCopyButtonClick(ClickEvent event);
		
		void onDraftButtonClick(ClickEvent event);
		
		void onViewButtonClick(ClickEvent event);
	}
	
	private static EditOptionsTooltbarUiBinder uiBinder = GWT
			.create(EditOptionsTooltbarUiBinder.class);

	interface EditOptionsTooltbarUiBinder extends
			UiBinder<Widget, OptionsToolbar> {
	}
	
	@UiField
	Button viewButton;
	@UiField
	Button pasteButton;
	@UiField
	Button copyButton;
	@UiField
	Button draftButton;
	@UiField
	Button newButton;
	
	private List<Listener> listeners;

	public OptionsToolbar() {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.listeners = new ArrayList<Listener>();
	}
	
	@UiHandler("viewButton")
	void onClickViewButton(ClickEvent event) {
		for(Listener listener : listeners)
			listener.onViewButtonClick(event);
	}
	
	@UiHandler("newButton")
	void onClickNewButton(ClickEvent event) {
		for(Listener listener : listeners)
			listener.onNewButtonClick(event);
	}
	
	@UiHandler("copyButton")
	void onClickCopyButton(ClickEvent event) {
		for(Listener listener : listeners)
			listener.onCopyButtonClick(event);
	}
	
	@UiHandler("pasteButton")
	void onClickPasteButton(ClickEvent event) {
		for(Listener listener : listeners)
			listener.onPasteButtonClick(event);
	}
	
	@UiHandler("draftButton")
	void onClickDraftButton(ClickEvent event) {
		for(Listener listener : listeners)
			listener.onDraftButtonClick(event);
	}

	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

}
