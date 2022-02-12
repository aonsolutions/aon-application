package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;

public abstract class AonToolbarSearchBox extends FlowPanel{

	
	public AonToolbarSearchBox() {
		super();

		this.setStyleName(AON.CSS.aonToolbarSearchBox());
		AonToolbarButton searchButton = new AonToolbarButton(AON.MSG.searchAction(), AON.CSS.aonIconSearch());
		this.add(searchButton);

		TextBox text = new TextBox();
		text.setStyleName(AON.CSS.aonToolbarTextBox());
		text.setTitle(AON.MSG.searchAction());
		text.setName(AON.MSG.searchAction());
		text.setVisible(false);
		text.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				onValueChange(text.getValue());
			}
		});
		this.add(text);
		
		AonToolbarButton moreButton = new AonToolbarButton(AON.MSG.advancedSearch(), AON.CSS.aonIconDown());
		moreButton.addClickHandler(event -> {
			// TODO
		});
		
		moreButton.setVisible(false);
		this.add(moreButton);
		
		searchButton.addClickHandler(event -> {
			text.setVisible(!text.isVisible());
			moreButton.setVisible(!moreButton.isVisible());
			text.setFocus(true);
		});	
	}
	
	public abstract void onValueChange(String value);

}
