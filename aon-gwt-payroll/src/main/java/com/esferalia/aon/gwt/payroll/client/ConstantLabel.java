package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.client.Constants.EXPRESSION_MAX_LENGTH;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ConstantLabel implements IsWidget , HasValue<String> , HasAllFocusHandlers , Focusable , HasEnabled{

	private TextBox textBox;
	private HorizontalPanel panel;
	
	public ConstantLabel() {
		
		textBox = new TextBox();
		textBox.setEnabled(false);
		
		textBox.addStyleName(AON.AON_BOLD);
		textBox.addStyleName(AON.AON_READ_ONLY);
		textBox.setMaxLength(EXPRESSION_MAX_LENGTH);

		panel = new HorizontalPanel();
		panel.add(textBox);
		panel.add(new InlineHTML("&nbsp;"));
		
		panel.addAttachHandler( e -> textBox.getElement().getStyle().setProperty("width",panel.getElement().getStyle().getWidth()));
		
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return null;
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		panel.fireEvent(event);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return null;
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return null;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public void setEnabled(boolean enabled) {
	}

	@Override
	public int getTabIndex() {
		return panel.getElement().getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
	}

	@Override
	public void setFocus(boolean focused) {
	}

	@Override
	public void setTabIndex(int index) {
	}

	@Override
	public String getValue() {
		return textBox.getValue();
	}

	@Override
	public void setValue(String value) {
		textBox.setValue(value);
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		textBox.setValue(value, fireEvents);
	}

	@Override
	public Widget asWidget() {
		return panel.asWidget();
	}
	
	public void ensureDebugId(String id){
		textBox.ensureDebugId(id);
	}


}
