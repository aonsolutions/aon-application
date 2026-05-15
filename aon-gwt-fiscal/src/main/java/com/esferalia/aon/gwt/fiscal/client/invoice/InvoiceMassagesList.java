package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFlexGrid;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class InvoiceMassagesList extends ScrollPanel implements HasClickHandlers, HasAllKeyHandlers, Focusable {

	private static final String NO_CLOSE_BUTTON_AVAILABLE = "No close button available";
	private final Button closeButton;
	
	public InvoiceMassagesList( Invoice invoice) {
		this(invoice, true);
	}
	
	public InvoiceMassagesList( Invoice invoice, boolean showClose) {
		setStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonPaddingTop());
		FlowPanel container = new FlowPanel();
		setWidget(container);
		String[] widths = {"80px", "1fr"};
		AonFlexGrid grid = new AonFlexGrid(widths,AON.CSS.aonMarginTopSep(),AON.CSS.aonMarginLeft());
		container.add(grid);
		
		invoice.messageStream().forEach(err -> {
			InvoiceErrorLevel level = err.getLevel();
			String msg = err.getMessage();
			
			if (err.getContext() != null && err.getContext().getKey() == InvoiceErrorKey.REGISTRY_STATUS ) {
				level = InvoiceErrorLevel.WRN;
			}
			
			InlineLabel errLabel = new InlineLabel(level.getLabel());
			errLabel.getElement().getStyle().setColor(getColor(level));
			errLabel.addStyleName(AON.CSS.aonBold());
			errLabel.addStyleName(AON.CSS.aonTextCenter());

			SafeHtml safeHtml = SafeHtmlUtils.fromTrustedString(msg);
			HTML msgLabel = new HTML(safeHtml);
			msgLabel.setStyleName(AON.CSS.aonMarginLeft());
			
			grid.addCell(errLabel)
				.addCell(msgLabel)
			;
		});
		if (showClose) {
			FlowPanel buttonsPanel = new FlowPanel();
			buttonsPanel.setStyleName(AON.CSS.aonPadding());
			buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
			buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
			
			closeButton = new Button();		
			closeButton.setStyleName(AON.CSS.aonOkButton());
			closeButton.setText( AON.MSG.close() );
			
			buttonsPanel.add(closeButton);
			container.add(buttonsPanel);
		} else {
			closeButton = null;
		}
	}

	public static String getColor(InvoiceErrorLevel curLevel) {
		String color = null;
		if (curLevel == null) {
			color = "#c1f9ba";
		} else if (curLevel == InvoiceErrorLevel.INF) {
			color = "RoyalBlue";
		} else if (curLevel == InvoiceErrorLevel.WRN) {
			color = "#ffa54f"; 
		} else if (curLevel == InvoiceErrorLevel.ERR) {
			color = "red";
		}
		return color;
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler arg0) {
		if (closeButton != null) {
			return closeButton.addClickHandler(arg0);
		} else {
			throw new IllegalStateException(NO_CLOSE_BUTTON_AVAILABLE);
		}
	}
	
	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler arg0) {
		if (closeButton != null) {
			return closeButton.addKeyUpHandler(arg0);
		} else {
			throw new IllegalStateException(NO_CLOSE_BUTTON_AVAILABLE);
		}
	}


	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler arg0) {
		if (closeButton != null) {
			return closeButton.addKeyDownHandler(arg0);
		} else {
			throw new IllegalStateException(NO_CLOSE_BUTTON_AVAILABLE);
		}
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler arg0) {
		if (closeButton != null) {
			return closeButton.addKeyPressHandler(arg0);
		} else {
			throw new IllegalStateException(NO_CLOSE_BUTTON_AVAILABLE);
		}
	}

	@Override
	public int getTabIndex() {
		if (closeButton != null) {
			return closeButton.getTabIndex();
		}
		return 0;
	}

	@Override
	public void setAccessKey(char arg0) {
		if (closeButton != null) {
			closeButton.setAccessKey(arg0);
		}
	}

	@Override
	public void setFocus(boolean arg0) {
		if (closeButton != null) {
			closeButton.setFocus(arg0);
		}
	}

	@Override
	public void setTabIndex(int arg0) {
		if (closeButton != null) {
			closeButton.setTabIndex(arg0);
		}
	}

}
