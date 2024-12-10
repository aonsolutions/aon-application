package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AonToolbarSearchBox extends FlowPanel{

	Widget advancedSearch;
	
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
		
		AonToolbarButton moreButton = new AonToolbarButton(AON.MSG.advancedSearch(), AON.CSS.aonIconFilterList());
		moreButton.addClickHandler(event -> {
			if(getAdvancedSearch() != null) {
				PopupPanel popup = new PopupPanel();
				popup.getElement().getStyle().setBorderWidth(1, Unit.PX);
				popup.setWidth("224px");
				popup.setAutoHideEnabled(true);
				popup.setPopupPosition(moreButton.getAbsoluteLeft() - 195, moreButton.getAbsoluteTop() + 39);
				popup.add(getAdvancedSearch());
				popup.show();
			}
		});
		
		moreButton.setVisible(false);
		this.add(moreButton);
		
		searchButton.addClickHandler(event -> {
			text.setVisible(!text.isVisible());
			moreButton.setVisible(!moreButton.isVisible());
			text.setFocus(true);
		});	
	}
	
	public Widget getAdvancedSearch() {
		return advancedSearch;
	}
	
	public void setAdvancedSearch(Widget advancedSearch) {
		this.advancedSearch = advancedSearch;
	}
	
	public abstract void onValueChange(String value);

}
