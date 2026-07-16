package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFlexGrid;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
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

public class AmortizationMassagesList extends FlowPanel implements HasClickHandlers, HasAllKeyHandlers, Focusable {
	
	private final Button closeButton = new Button();
	
	public AmortizationMassagesList(Amortization am) {
		this(am, true);
	}
	
	public AmortizationMassagesList( Amortization am, boolean showClose) {
		getElement().getStyle().setProperty("overflowY", "auto");
		getElement().getStyle().setProperty("maxHeight", "300px");
		addStyleName(AON.CSS.aonPaddingTop());
		String[] widths = {"1fr"};
		AonFlexGrid grid = new AonFlexGrid(widths,AON.CSS.aonMarginTopSep(),AON.CSS.aonMarginLeft());
		this.add(grid);
		
		am.messageStream().forEach(msg -> {
			SafeHtml safeHtml = SafeHtmlUtils.fromTrustedString(msg);
			HTML msgLabel = new HTML(safeHtml);
			msgLabel.setStyleName(AON.CSS.aonMarginLeft());
			grid.addCell(msgLabel);
		});
		if (showClose) {
			FlowPanel buttonsPanel = new FlowPanel();
			buttonsPanel.setStyleName(AON.CSS.aonPadding());
			buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
			buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
			closeButton.setStyleName(AON.CSS.aonOkButton());
			closeButton.setText( AON.MSG.close() );
			buttonsPanel.add(closeButton);
			this.add(buttonsPanel);
		}
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler arg0) {
		return closeButton.addClickHandler(arg0);
	}
	
	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler arg0) {
		return closeButton.addKeyUpHandler(arg0);
	}


	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler arg0) {
		return closeButton.addKeyDownHandler(arg0);
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler arg0) {
		return closeButton.addKeyPressHandler(arg0);
	}

	@Override
	public int getTabIndex() {
		return closeButton.getTabIndex();
	}

	@Override
	public void setAccessKey(char arg0) {
		closeButton.setAccessKey(arg0);
	}

	@Override
	public void setFocus(boolean arg0) {
		closeButton.setFocus(arg0);
	}

	@Override
	public void setTabIndex(int arg0) {
		closeButton.setTabIndex(arg0);
	}

}
