package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class IbanTextBox extends SimplePanel {
	
	private static final AonResources AON_RESOURCES = GWT.create(AonResources.class);
	
	TextBox iban1 = new TextBox();
	TextBox iban2 = new TextBox();
	TextBox iban3 = new TextBox();
	TextBox iban4 = new TextBox();
	TextBox iban5 = new TextBox();
	TextBox iban6 = new TextBox();
	
	private TextBox[] textBoxes = new TextBox[]{
			iban1,iban2,iban3,iban4,iban5,iban6};
	
	public IbanTextBox() {
		super( DOM.createSpan());
		FlowPanel panel = new FlowPanel();
		panel.addStyleName(AON_RESOURCES.css().aonNowrap());
		panel.addStyleName(AON_RESOURCES.css().aonInline());
		this.add(panel);
		for (TextBox textBox : textBoxes ) {
			textBox.addStyleName(AON_RESOURCES.css().aonMarginLeft());
			textBox.setMaxLength(4);
			textBox.setVisibleLength(5);
			panel.add(textBox);
		}
	}
	
	public String getValue() {
		return iban1.getValue() + iban2.getValue() + iban3.getValue()
			 + iban4.getValue() + iban5.getValue() + iban6.getValue();
	}
	
	public void setValue(String value) {
		iban1.setValue( AonUtil.substring(value, 0,4));
		iban2.setValue( AonUtil.substring(value, 4,8));
		iban3.setValue( AonUtil.substring(value, 8,12));
		iban4.setValue( AonUtil.substring(value, 12,16));
		iban5.setValue( AonUtil.substring(value, 16,20));
		iban6.setValue( AonUtil.substring(value, 20,24));
	}
	
	
}
