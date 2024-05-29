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
	private HTMLPanel toolbarLeft = new HTMLPanel(EMPTY_STRING);
	private HTMLPanel toolbarRight = new HTMLPanel(EMPTY_STRING);
	
	public AonCustomToolbar(String title) {
		super(EMPTY_STRING);
		
		addStyleName(AON.CSS.aonFlexBetween());
		getElement().getStyle().setProperty("padding", "0 1rem");
		
		// Left
		toolbarLeft.addStyleName(AON.CSS.aonItemFlex());
		backButton.setVisible(false);
		toolbarLeft.add(backButton);
		
		add(toolbarLeft);
		
		// Right
		toolbarRight.addStyleName(AON.CSS.aonItemFlex());
		
		parentBreadCrumb = new Label(EMPTY_STRING);
		parentBreadCrumb.getElement().getStyle().setProperty("font-size", "1rem");
		toolbarRight.add(parentBreadCrumb);
		
		titleLabel = new Label(title);
		titleLabel.getElement().getStyle().setProperty("font-weight", "700");
		titleLabel.getElement().getStyle().setProperty("font-size", "1rem");
		toolbarRight.add(titleLabel);
		add(toolbarRight);
	}
	
	public void addToolbarButton(Widget widget) {
		toolbarLeft.add(widget);
	}
	
	public AonToolbarButton getBackButton() {
		return this.backButton;
	}

	public void showBackButton() {
		this.backButton.setVisible(true);
	}

	public void setToolbarTitle(String breadCrumb, String title) {
		parentBreadCrumb.setText(breadCrumb);
		titleLabel.setText(title);
	}

}
