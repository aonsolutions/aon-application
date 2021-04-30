package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AonExpandButton extends Composite {

	private static AonExpandButtonUiBinder uiBinder = GWT.create(AonExpandButtonUiBinder.class);

	interface AonExpandButtonUiBinder extends UiBinder<Widget, AonExpandButton> {}
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String footerButton();
		String innerButton();
	}
	
	@UiField
	HTMLPanel mainButton;
	
	private AonToolbarButton innerButton;
	private AonToolbarButton dropdownButton;
	
	public AonExpandButton(String toolTip, String iconStyle) {
		initWidget(uiBinder.createAndBindUi(this));
		setStyleName(AON.CSS.aonExpandPanel());
		addStyleName(AON.CSS.aonToolbarButton());
		getElement().getStyle().setWidth(46, Unit.PX);
		createExpandButton(toolTip, iconStyle);
	}
	
	private void createExpandButton(String toolTip, String iconStyle) {
		innerButton = new AonToolbarButton(toolTip, iconStyle);
		innerButton.removeStyleName(AON.CSS.aonButton());
		innerButton.removeStyleName(AON.CSS.aonToolbarButton());
		innerButton.addStyleName(AON.CSS.aonExpandButton());
		innerButton.addStyleName(style.innerButton());
		innerButton.addClickHandler(e -> {
			onDefaultClick(e);
		});
		mainButton.add(innerButton);
		
		dropdownButton = new AonToolbarButton("", AON.CSS.aonIconDropDown());
		dropdownButton.removeStyleName(AON.CSS.aonButton());
		dropdownButton.removeStyleName(AON.CSS.aonToolbarButton());
		dropdownButton.addStyleName(AON.CSS.aonExpandButton());
		dropdownButton.addStyleName(style.footerButton());
		dropdownButton.addClickHandler(e -> {
			onExpandClick(e);
		});
		mainButton.add(dropdownButton);
	}

	public abstract void onDefaultClick(ClickEvent evet);
	public abstract void onExpandClick(ClickEvent evet);
	
}
