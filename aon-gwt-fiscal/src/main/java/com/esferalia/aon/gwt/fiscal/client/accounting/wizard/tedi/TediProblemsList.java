package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.tedi.TediError;
import com.esferalia.aon.occam.api.model.tedi.TediLevel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class TediProblemsList extends ScrollPanel {

	public TediProblemsList( LinkedList<TediError> messages) {
		setStyleName(AON.CSS.aonScrollArea());
		FlowPanel mainPanel = new FlowPanel();
		setWidget(mainPanel);
		if (messages != null && messages.size() > 0) {
			mainPanel.setStyleName(AON.CSS.aonMarginTopSep());
			mainPanel.addStyleName(AON.CSS.aonMarginLeft());
			mainPanel.addStyleName(AON.CSS.aonFixedFont());
			for (TediError error : messages) {
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

	private String getBackgroundColor(TediLevel curLevel) {
		String color = null;
		if (curLevel == null) {
			color = "#c1f9ba";
		} else if (curLevel == TediLevel.INF) {
			color = "RoyalBlue";
		} else if (curLevel == TediLevel.WRN) {
			color = "#ffa54f"; 
		} else if (curLevel == TediLevel.ERR) {
			color = "red";
		}
		return color;
	}

}
