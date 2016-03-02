package com.esferalia.aon.gwt.document.client;

import com.esferalia.aon.gwt.common.client.css.ViewerResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class Cargando extends PopupPanel {
	
	@UiField FocusPanel focusPanel;
	@UiField Label load;

	public Cargando() {
		super(true, true);
		GWT.<ViewerResources> create(ViewerResources.class).css().ensureInjected();		
		setWidget(binder.createAndBindUi(this));
	}

	interface Binder extends UiBinder<Widget, Cargando> {
		
	}

	private static final Binder binder = GWT.create(Binder.class);

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return focusPanel.addClickHandler(handler);
	}
	
	public void showLoad() {
		load.setVisible(true);
	}

	public void hideLoad() {
		load.setVisible(false);
	}

}
