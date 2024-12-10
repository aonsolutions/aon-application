package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;

public class AonCustomNumberBox extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private HTMLPanel textBoxPanel = new HTMLPanel(EMPTY_STRING);
	private TextBox numberBox;
	private TextBox gtnumberBox;
	private TextBox ltnumberBox;
	private CheckBox nearCB;
	
	public AonCustomNumberBox(String title) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn());
//		addStyleName(AON.CSS.aonCustomTextBox());
		addStyleName(AON.CSS.aonCustomTextBoxNoBorder());

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
		textBoxPanel.getElement().getStyle().setProperty("border-bottom", "1px solid #b9b8b8");
		
		numberBox = new TextBox();
		numberBox.setStyleName(AON.CSS.aonCustomTextBoxInput());
		numberBox.getElement().setPropertyString("placeholder", "Introduce un valor numerico");
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
		nearCB.setTitle("Busqueda entre valores");
		nearCB.addValueChangeHandler(e -> {
			numberBox.setValue(null);
			gtnumberBox.setValue(null);
			ltnumberBox.setValue(null);
			
			numberBox.setEnabled(!nearCB.getValue());
			gtnumberBox.setEnabled(nearCB.getValue());
			ltnumberBox.setEnabled(nearCB.getValue());
			
			if(nearCB.getValue()) {
				getElement().getStyle().setProperty("height", "7rem");
				gtnumberBox.getElement().getStyle().clearDisplay();
				ltnumberBox.getElement().getStyle().clearDisplay();
			} else {
				getElement().getStyle().setProperty("height", "2.5rem");
				gtnumberBox.getElement().getStyle().setDisplay(Display.NONE);
				ltnumberBox.getElement().getStyle().setDisplay(Display.NONE);
			}
			
			if(!nearCB.getValue())
				fireValueChangeEvent();
		});
		
		textBoxPanel.add(numberBox);
		textBoxPanel.add(nearCB);
		add(textBoxPanel);
		
		gtnumberBox = new TextBox();
		gtnumberBox.setStyleName(AON.CSS.aonBetweenInput());
		gtnumberBox.getElement().setPropertyString("placeholder", "Mayor o igual que...");
		gtnumberBox.getElement().getStyle().setDisplay(Display.NONE);
		gtnumberBox.getElement().getStyle().setProperty("border-bottom", "1px solid #b9b8b8");
		gtnumberBox.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                char charCode = event.getCharCode();
                
                // Solo permitimos dígitos, punto y caracteres de control como 'Backspace'
                if (!Character.isDigit(charCode) && charCode != '.' && charCode != KeyCodes.KEY_BACKSPACE) {
                	gtnumberBox.cancelKey();
                } else {
                    // Prevenir múltiples puntos decimales
                    if (charCode == '.' && numberBox.getText().contains(".")) {
                    	gtnumberBox. cancelKey();
                    }
                }
            }
        });
		
		add(gtnumberBox);
		
		ltnumberBox = new TextBox();
		ltnumberBox.setStyleName(AON.CSS.aonBetweenInput());
		ltnumberBox.getElement().setPropertyString("placeholder", "Menor o igual que...");
		ltnumberBox.getElement().getStyle().setDisplay(Display.NONE);
		ltnumberBox.getElement().getStyle().setProperty("border-bottom", "1px solid #b9b8b8");
		ltnumberBox.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                char charCode = event.getCharCode();
                
                // Solo permitimos dígitos, punto y caracteres de control como 'Backspace'
                if (!Character.isDigit(charCode) && charCode != '.' && charCode != KeyCodes.KEY_BACKSPACE) {
                	ltnumberBox.cancelKey();
                } else {
                    // Prevenir múltiples puntos decimales
                    if (charCode == '.' && numberBox.getText().contains(".")) {
                    	ltnumberBox. cancelKey();
                    }
                }
            }
        });
		
		add(ltnumberBox);
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
	
	public Double getGTValue() {
		if(AonStringUtils.isBlank(this.gtnumberBox.getValue())) return null;
		try {
		 return Double.parseDouble(this.gtnumberBox.getValue());
		} catch (Exception e) {
			return null;
		}
	}
	
	public Double getLTValue() {
		if(AonStringUtils.isBlank(this.ltnumberBox.getValue())) return null;
		try {
		 return Double.parseDouble(this.ltnumberBox.getValue());
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean isBetweenNumbers() {
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
	
	private void fireValueChangeEvent() {
        ValueChangeEvent<String> valueChangeEvent = new ValueChangeEvent<String>(null) {};
        numberBox.fireEvent(valueChangeEvent);
    }

	public void addValueChangeHandler(ValueChangeHandler<String> valueChangeHandler) {
		numberBox.addValueChangeHandler(valueChangeHandler);
		gtnumberBox.addValueChangeHandler(valueChangeHandler);
		ltnumberBox.addValueChangeHandler(valueChangeHandler);
	}

}
