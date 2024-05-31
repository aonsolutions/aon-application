package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class TediProblemsList extends ScrollPanel {

	public TediProblemsList( List<InvoiceError> messages) {
		setStyleName(AON.CSS.aonScrollArea());
		FlowPanel mainPanel = new FlowPanel();
		setWidget(mainPanel);
		if (messages != null && messages.size() > 0) {
			mainPanel.setStyleName(AON.CSS.aonMarginTopSep());
			mainPanel.addStyleName(AON.CSS.aonMarginLeft());
			mainPanel.addStyleName(AON.CSS.aonFixedFont());
			for (InvoiceError error : messages) {
				FlowPanel flowPanel = new FlowPanel();
				InlineLabel colorLabel = new InlineLabel("");
				colorLabel.setStyleName(AON.CSS.aonPaddingLeft());
				colorLabel.addStyleName(AON.CSS.aonPaddingRight());
				colorLabel.getElement().getStyle().setBackgroundColor(getBackgroundColor(error.getLevel()));
				flowPanel.add(colorLabel);

				InlineLabel errLabel = new InlineLabel(error.getLevel().getLabel());
				errLabel.setStyleName(AON.CSS.aonPaddingLeft());
				errLabel.addStyleName(AON.CSS.aonPaddingRight());
				errLabel.addStyleName(AON.CSS.aonBold());
				flowPanel.add(errLabel);

				InlineLabel msgLabel = new InlineLabel(error.getMessage());
				msgLabel.setStyleName(AON.CSS.aonMarginLeft());
				flowPanel.add(msgLabel);
				mainPanel.add(flowPanel);
			}
		}
	}

	private String getBackgroundColor(InvoiceErrorLevel curLevel) {
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

}
