package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;

public abstract class AonCustomNumberBox extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private HTMLPanel textBoxPanel = new HTMLPanel(EMPTY_STRING);
	private TextBox numberBox;
	private CheckBox nearCB;
	
	public AonCustomNumberBox(String title) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn());
		addStyleName(AON.CSS.aonCustomTextBox());

		createTitle(title);
		createInput();
	}

	private void createTitle(String title) {
		HTMLPanel titleLabel = new HTMLPanel(AonStringUtils.isNotBlank(title) ? title : EMPTY_STRING);
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
	}

	private void createInput() {
		textBoxPanel.addStyleName(AON.CSS.aonItemFlex());
		textBoxPanel.addStyleName(AON.CSS.aonFlexBetween());
		textBoxPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		
		numberBox = new TextBox();
		numberBox.setStyleName(AON.CSS.aonCustomTextBoxInput());
		numberBox.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                char charCode = event.getCharCode();
                
                // Solo permitimos dígitos, punto y caracteres de control como 'Backspace'
                if (!Character.isDigit(charCode) && charCode != '.' && charCode != KeyCodes.KEY_BACKSPACE) {
                	numberBox.cancelKey();
                } else {
                    // Prevenir múltiples puntos decimales
                    if (charCode == '.' && numberBox.getText().contains(".")) {
                    	numberBox. cancelKey();
                    }
                }
            }
        });
		
		nearCB = new CheckBox();
		nearCB.setTitle("Cant. cercanas");
		nearCB.addValueChangeHandler(e -> onNearChange());
		
		textBoxPanel.add(numberBox);
		textBoxPanel.add(nearCB);
		add(textBoxPanel);
	}

	public TextBox getNumberBox() {
		return this.numberBox;
	}

	public void setValueSrint(String value) {
		if(AonStringUtils.isBlank(value)) nearCB.setValue(false);
		this.numberBox.setValue(value);
	}
	
	public void setValue(Double value) {
		if(null == value) {
			nearCB.setValue(false);
			numberBox.setValue(null);
		} else numberBox.setValue(value.toString());
	}

	public Double getValue() {
		if(AonStringUtils.isBlank(this.numberBox.getValue())) return null;
		try {
		 return Double.parseDouble(this.numberBox.getValue());
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean isNearBy() {
		return nearCB.getValue();
	}
	
	public void setEnable(boolean enabled) {
		this.numberBox.setEnabled(enabled);
	}

	public void setFocus(boolean focused) {
		this.numberBox.setFocus(focused);
	}

	public void addButton(AonTableButton button) {
		textBoxPanel.add(button);
	}

	public void addValueChangeHandler(ValueChangeHandler<String> valueChangeHandler) {
		numberBox.addValueChangeHandler(valueChangeHandler);
	}

	protected abstract void onNearChange();	

}
