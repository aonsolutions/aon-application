package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AonToastModel extends FlowPanel {
	
	FlowPanel headerPanel;
	FlowPanel contentPanel;
	
	public AonToastModel(HasWidgets parentWidget) {
		super();
		setStyleName(AON.CSS.aonToastModel());
		headerPanel = new FlowPanel();
		Button close = new Button();
		close.setStyleName(AON.CSS.aonCustomDialogClose());
		close.addClickHandler(event -> hide());
		headerPanel.setStyleName(AON.CSS.aonToastHeader());
		headerPanel.add(close);
		contentPanel = new FlowPanel();
		contentPanel.setStyleName(AON.CSS.aonToastContent());
		add(headerPanel);
		add(contentPanel);
		parentWidget.add(this);
	}

	public void show(String caption,String content) {
		show(caption,  new Label(content));
	}
	public void show(String caption,Widget content) {
		Label header = new Label(caption);
		header.setStyleName(AON.CSS.aonFlexGrow1());
		headerPanel.insert(header,0);
		contentPanel.add(content);
	}
	public void hide() {
		this.removeFromParent();
	}
}
