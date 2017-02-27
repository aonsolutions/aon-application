package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.iron.widget.IronLabel;

public class MyEditor implements IsWidget , HasValue<String> , HasAllFocusHandlers , Focusable , HasEnabled{

	IronLabel ironLabel;
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		ironLabel.fireEvent(event);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setEnabled(boolean enabled) {
		ironLabel.setDisabled(!enabled);
		
	}

	@Override
	public int getTabIndex() {
		return ironLabel.getElement().getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFocus(boolean focused) {
		if(focused)
			ironLabel.getElement().focus();
		
	}

	@Override
	public void setTabIndex(int index) {
		ironLabel.setTabindex(index);
		
	}

	@Override
	public String getValue() {
		return ironLabel.getElement().getInnerText();
	}

	@Override
	public void setValue(String value) {
		ironLabel.getElement().setInnerText(value);
		
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		if(fireEvents)
			ironLabel.getElement().setInnerText(value);
		
	}

	@Override
	public Widget asWidget() {
		return ironLabel.asWidget();
	}

}
