package com.esferalia.aon.gwt.fiscal.client.mod131;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class Model131Activities extends SimplePanel {
	
	private static int MAX_ACTIVITIES = 5;
	final private FlowPanel rootPanel;
	final private SimplePanel activityPanel;
	
	public Model131Activities(IFiscalModelCallback<Mod131> callback) {
		rootPanel = new FlowPanel();
		rootPanel.add(getSelectorPanel(callback));
		activityPanel = new SimplePanel();
		rootPanel.add(activityPanel);
		setWidget(rootPanel);
	}

	private FlexTable getSelectorPanel(IFiscalModelCallback<Mod131> callback) {
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.setStyleName(AON.AON_CSS.aonMarginBottom());
		table.addStyleName(AON.AON_CSS.aonMarginTop());
		
		
		for (int i = 0 ; i < MAX_ACTIVITIES; i++) {
			FocusPanel focus = new FocusPanel();
			focus.setStyleName(AON.AON_CSS.aonTextCenter());
			focus.addStyleName(AON.AON_CSS.aonIconButton());
			focus.addStyleName(AON.AON_CSS.aonBold());
			focus.addStyleName(AON.AON_CSS.aonCursorPointer());
			final Mod131Activity act = callback.getFiscalModel().getActivities().get(i);
			String text = act.getFullDescription();
			if (AonStringUtils.isEmpty(text)) {
				text = AON.MSG.activity() + " " + i;
			}
			text = AonStringUtils.abbreviate(text, 25);
			Label label = new Label(text);
			focus.add(label);
			table.getColumnFormatter().setWidth(i, (100 / MAX_ACTIVITIES) + "%");
			table.setWidget(0, i, focus);
			
			focus.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					activityPanel.setWidget(new Model131Activity(act));;
				}
			});
		}
		return table;
	}
}
