package com.esferalia.aon.gwt.common.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class OptionsToolbar extends Composite {
	
	public interface Listener {
		
		void onNewButtonClick(ClickEvent event);
		
		void onPasteButtonClick(ClickEvent event);
		
		void onCopyButtonClick(ClickEvent event);
		
		void onDraftButtonClick(ClickEvent event);
				
		void onCollapseAllButtonClick(ClickEvent event);
		
		void onKeyUpSearchTextBox(KeyUpEvent event);
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
	@UiField
	Button collapseAllButton;
	
	@UiField
	TextBox searchTextBox;

	private List<Listener> listeners;

	public OptionsToolbar() {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.listeners = new ArrayList<Listener>();
	}

	public void setSearchTextBox(boolean visible) {
		searchTextBox.setVisible(visible);
	}
	
	public void setVisiblePasteButton(boolean visible) {
		pasteButton.setVisible(visible);
	}
	
	public void setVisibleCopyButton(boolean visible) {
		copyButton.setVisible(visible);
	}
	
	public void setVisibleDraftButton(boolean visible) {
		draftButton.setVisible(visible);
	}
	
	public void setVisibleNewButton(boolean visible) {
		newButton.setVisible(visible);
	}
	
	public void setVisibleViewButton(boolean visible) {
		viewButton.setVisible(visible);
	}
	
	public void setVisibleCollapseButton(boolean visible) {
		collapseAllButton.setVisible(visible);
	}

	public void setEnabledPasteButton(boolean enabled) {
		pasteButton.setEnabled(enabled);
	}
	
	public void setEnabledCopyButton(boolean enabled) {
		copyButton.setEnabled(enabled);
	}
	
	public void setEnabledDraftButton(boolean enabled) {
		draftButton.setEnabled(enabled);
	}
	
	public void setEnabledNewButton(boolean enabled) {
		newButton.setEnabled(enabled);
	}
	
	public void setEnabledViewButton(boolean enabled) {
		viewButton.setEnabled(enabled);		
	}
	
	public void setEnabledCollapseButton(boolean enabled) {
		collapseAllButton.setEnabled(enabled);
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
	
	@UiHandler("collapseAllButton")
	void onClickCollapseAllButton(ClickEvent event) {
		for(Listener listener : listeners)
			listener.onCollapseAllButtonClick(event);
	}

	@UiHandler("searchTextBox")
	void onKeyPressSearchTextBox(KeyUpEvent event) {
		for(Listener listener : listeners)
			listener.onKeyUpSearchTextBox(event);
	}
	
	public Button getViewButton() {
		return this.viewButton;
	}

	public TextBox getSearchTextBox() {
		return searchTextBox;
	}
	
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

}
