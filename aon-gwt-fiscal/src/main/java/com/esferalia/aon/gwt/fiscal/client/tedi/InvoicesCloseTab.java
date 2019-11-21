package com.esferalia.aon.gwt.fiscal.client.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
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

public class InvoicesCloseTab extends Composite implements HasCloseHandlers<Integer> {

	public InvoicesCloseTab(final String label, final String color, final boolean canClose) {

		final HTMLPanel hPanel = new HTMLPanel("");

		
		final HTMLPanel labelPanel = new HTMLPanel("");
		labelPanel.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
		
		if (color != null && color.startsWith("#")) {
			final HTML dot = new HTML(SafeHtmlUtils.fromString("\u2022"));
			dot.getElement().getStyle().setBackgroundColor(color);
			dot.getElement().getStyle().setPaddingLeft(2, Unit.PX);
			dot.getElement().getStyle().setPaddingRight(2, Unit.PX);
			dot.getElement().getStyle().setMarginRight(2, Unit.PX);
			dot.getElement().getStyle().setDisplay(Display.INLINE);
			labelPanel.add(dot);
		}
		
		// Add label
		final HTML html = new HTML(SafeHtmlUtils.fromString(label));
		html.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
		html.getElement().getStyle().setDisplay(Display.INLINE);
		labelPanel.add(html);
		
		hPanel.add(labelPanel);

		// Add close button if requested
		if (canClose) {

			Image closeButton = new Image(AON.AON_RESOURCES.aonIconClose());
			closeButton.setSize("13px", "13px");
			closeButton.getElement().getStyle().setCursor(Cursor.POINTER);
			closeButton.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);

			closeButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					CloseEvent.fire(InvoicesCloseTab.this, null);
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
