package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

public class CloseTab extends Composite implements HasCloseHandlers<Integer> {

	public CloseTab(final String label, final boolean canClose) {

		final HTMLPanel hPanel = new HTMLPanel("");

		// Add label
		final HTML html = new HTML(SafeHtmlUtils.fromString(label));
		html.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
		html.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
		hPanel.add(html);

		// Add close button if requested
		if (canClose) {

			Image closeButton = new Image(AON.AON_RESOURCES.aonIconClose());
			closeButton.setSize("13px", "13px");
			closeButton.getElement().getStyle().setCursor(Cursor.POINTER);
			closeButton.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);

			closeButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					CloseEvent.fire(CloseTab.this, null);
				}
			});

			html.getElement().getStyle().setMarginRight(8, Unit.PX);
			hPanel.add(closeButton);
		}

		initWidget(hPanel);
	}

	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<Integer> handler) {
		return addHandler(handler, CloseEvent.getType());
	}
}
