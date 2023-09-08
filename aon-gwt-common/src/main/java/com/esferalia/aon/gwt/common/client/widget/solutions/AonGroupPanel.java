package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class AonGroupPanel extends FlowPanel {
	
	private final InlineLabel headerLabel;
	private final FlowPanel groupBodyPanel;
	
	public AonGroupPanel() {
		this.setStyleName(AON.CSS.aonGroup());
		this.addStyleName(AON.CSS.aonMarginTop());
		headerLabel = new InlineLabel();
		
		FlowPanel groupHeaderPanel = new FlowPanel();
		groupHeaderPanel.setStyleName(AON.CSS.aonGroupTitle());
		groupHeaderPanel.add ( headerLabel); 
		this.add(groupHeaderPanel);
		
		groupBodyPanel = new FlowPanel();
		groupBodyPanel.setStyleName(AON.CSS.aonGroupBody());
		this.add(groupBodyPanel);
	}
	
	public AonGroupPanel setHeaderLabel(String label) {
		headerLabel.setText(label);
		AonGroupPanel aonGroupPanel = this;
		return aonGroupPanel;
	}
	
	public AonGroupPanel addContent(Widget widget) {
		groupBodyPanel.add(widget);
		return this;
	}
	
	public static FlowPanel get(String label, Widget widget) {
		return new AonGroupPanel()
			.setHeaderLabel(label)
			.addContent(widget);
	}

	
}
