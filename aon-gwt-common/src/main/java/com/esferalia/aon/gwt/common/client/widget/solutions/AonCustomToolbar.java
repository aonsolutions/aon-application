package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AonCustomToolbar extends HTMLPanel {

	private static final String EMPTY_STRING = "";

	// Toolbar
	private AonToolbarButton backButton = new AonToolbarButton("Volver", AON.CSS.aonIconBack());
	private Label parentBreadCrumb;
	private Label titleLabel;
	private HTMLPanel toolbarRight = new HTMLPanel(EMPTY_STRING);
	
	public AonCustomToolbar(String title) {
		super(EMPTY_STRING);
		
		addStyleName(AON.CSS.aonFlexBetween());
		getElement().getStyle().setProperty("padding", "0 1rem");
		
		// Left
		HTMLPanel toolbarLeft = new HTMLPanel(EMPTY_STRING);
		toolbarLeft.addStyleName(AON.CSS.aonItemFlex());
		backButton.setVisible(false);
		toolbarLeft.add(backButton);
		
		parentBreadCrumb = new Label(EMPTY_STRING);
		parentBreadCrumb.getElement().getStyle().setProperty("font-size", "1rem");
		toolbarLeft.add(parentBreadCrumb);
		
		titleLabel = new Label(title.toUpperCase());
		titleLabel.getElement().getStyle().setProperty("font-weight", "700");
		titleLabel.getElement().getStyle().setProperty("font-size", "1.2rem");
		toolbarLeft.add(titleLabel);
		
		add(toolbarLeft);
		
		// Right
		toolbarRight.addStyleName(AON.CSS.aonItemFlex());
		add(toolbarRight);
	}
	
	public void addToolbarButton(Widget widget) {
		toolbarRight.add(widget);
	}
	
	public AonToolbarButton getBackButton() {
		return this.backButton;
	}

	public void showBackButton() {
		this.backButton.setVisible(true);
	}

	public void setToolbarTitle(String breadCrumb, String title) {
		parentBreadCrumb.setText(breadCrumb.toUpperCase());
		titleLabel.setText(title.toUpperCase());
	}

}
