package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

public class PlusTab extends Composite implements HasClickHandlers {

	public PlusTab() {

		final HTMLPanel hPanel = new HTMLPanel("");

		// Add label
		// final HTML html = new HTML(SafeHtmlUtils.fromString(label));
		final HTML html = new HTML();
		hPanel.add(html);

		Image plusButton = new Image(AON.AON_RESOURCES.aonIconPlus());
		plusButton.setTitle("Nueva pesta\u00F1a");
		plusButton.setSize("13px", "13px");
		plusButton.getElement().getStyle().setCursor(Cursor.POINTER);

		plusButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ClickEvent.fireNativeEvent(event.getNativeEvent(), PlusTab.this);
			}
		});

		html.getElement().getStyle().setMarginRight(8, Unit.PX);
		hPanel.add(plusButton);

		initWidget(hPanel);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addHandler(handler, ClickEvent.getType());
	}
}
