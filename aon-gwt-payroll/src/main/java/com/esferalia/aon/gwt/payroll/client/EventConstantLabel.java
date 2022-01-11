package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.client.Constants.EXPRESSION_MAX_LENGTH;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.payroll.client.Employees.Template;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.Color;

public class EventConstantLabel implements IsWidget , HasValue<String> , HasAllFocusHandlers , Focusable , HasEnabled{

	private HTML button;
	private TextBox textBox;
	private HorizontalPanel panel;
	

	public EventConstantLabel() {
		
		textBox = new TextBox();
		textBox.setEnabled(false);
		
		textBox.addStyleName(AON.AON_BOLD);
		textBox.addStyleName(AON.AON_READ_ONLY);
		textBox.setMaxLength(EXPRESSION_MAX_LENGTH);
		
		button = new HTML(AON.MATERIAL.icon("calendar_month", new SafeStylesBuilder().fontSize(24, Unit.PX).trustedColor("black").toSafeStyles()));
		

		panel = new HorizontalPanel();
		panel.add(textBox);
		panel.add(new InlineHTML("&nbsp;"));
		panel.add(button);
		
		panel.addAttachHandler( e -> textBox.getElement().getStyle().setProperty("width",panel.getElement().getStyle().getWidth()));
		
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		panel.fireEvent(event);
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
		textBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		textBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		textBox.setTabIndex(index);
		
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
		button.ensureDebugId("button-" + id);
		textBox.ensureDebugId("editor-" + id);
	}

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return button.addClickHandler(handler);
	}
	

}
