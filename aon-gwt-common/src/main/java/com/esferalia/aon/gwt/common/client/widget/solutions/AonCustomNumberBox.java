package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public class AonCustomNumberBox extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private HTMLPanel textBoxPanel = new HTMLPanel(EMPTY_STRING);
	private DoubleBox numberBox;
	private DoubleBox gtnumberBox;
	private DoubleBox ltnumberBox;
	private CheckBox nearCB;
	
	private HTMLPanel ltnumberBoxPanel;
	private HTMLPanel gtnumberBoxPanel;
	
	public AonCustomNumberBox(String title) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn2());
		addStyleName(AON.CSS.aonCustomTextBoxNoBorder());

		createTitle(title);
		createInput(null);
	}
	
	public AonCustomNumberBox(String title, Integer precision) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn2());
		addStyleName(AON.CSS.aonCustomTextBoxNoBorder());

		createTitle(title);
		createInput(precision);
	}

	private void createTitle(String title) {
		HTMLPanel titleLabel = new HTMLPanel(AonStringUtils.isNotBlank(title) ? title : EMPTY_STRING);
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
	}

	private void createInput(Integer precision) {
		textBoxPanel.addStyleName(AON.CSS.aonItemFlex());
		textBoxPanel.addStyleName(AON.CSS.aonFlexBetween());
		textBoxPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		textBoxPanel.getElement().getStyle().setProperty("border-bottom", "1px solid #b9b8b8");
		
		numberBox = new DoubleBox(12, null == precision ? 3 : 2);
		numberBox.setStyleName(AON.CSS.aonCustomTextBoxInput());
		numberBox.getElement().setPropertyString("placeholder", "Introduce un valor");
		
		nearCB = new CheckBox();
		nearCB.setTitle("Busqueda entre valores");
		nearCB.addValueChangeHandler(e -> {
			Double number = numberBox.getValue();
			numberBox.setValue(null);
			gtnumberBox.setValue(null == number ? null : number);
			ltnumberBox.setValue(null);
			
			numberBox.setEnabled(!nearCB.getValue());
			gtnumberBox.setEnabled(nearCB.getValue());
			ltnumberBox.setEnabled(nearCB.getValue());
			
			if(nearCB.getValue()) {
				getElement().getStyle().setProperty("height", "7rem");
				ltnumberBoxPanel.getElement().getStyle().clearDisplay();
				gtnumberBoxPanel.getElement().getStyle().clearDisplay();
			} else {
				getElement().getStyle().setProperty("height", "2.5rem");
				ltnumberBoxPanel.getElement().getStyle().setDisplay(Display.NONE);
				gtnumberBoxPanel.getElement().getStyle().setDisplay(Display.NONE);
			}
			
			if(!nearCB.getValue() || null != number)
				fireValueChangeEvent();
		});
		
		textBoxPanel.add(numberBox);
		textBoxPanel.add(nearCB);
		add(textBoxPanel);
		
		gtnumberBoxPanel = new HTMLPanel("");
		gtnumberBoxPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label gt = new Label("\u2265");
		
		gtnumberBox = new DoubleBox(12, null == precision ? 3 : 2);
		gtnumberBox.setStyleName(AON.CSS.aonBetweenInput());
		gtnumberBox.getElement().setPropertyString("placeholder", "Mayor o igual que...");
		gtnumberBoxPanel.getElement().getStyle().setDisplay(Display.NONE);
		gtnumberBox.getElement().getStyle().setProperty("border-bottom", "1px solid #b9b8b8");
		
		gtnumberBoxPanel.add(gt);
		gtnumberBoxPanel.add(gtnumberBox);
		
		add(gtnumberBoxPanel);
		
		ltnumberBoxPanel = new HTMLPanel("");
		ltnumberBoxPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label lt = new Label("\u2264");
		
		ltnumberBox = new DoubleBox(12, null == precision ? 3 : 2);
		ltnumberBox.setStyleName(AON.CSS.aonBetweenInput());
		ltnumberBox.getElement().setPropertyString("placeholder", "Menor o igual que...");
		ltnumberBoxPanel.getElement().getStyle().setDisplay(Display.NONE);
		ltnumberBox.getElement().getStyle().setProperty("border-bottom", "1px solid #b9b8b8");
		
		ltnumberBoxPanel.add(lt);
		ltnumberBoxPanel.add(ltnumberBox);
		
		add(ltnumberBoxPanel);
	}

	public DoubleBox getNumberBox() {
		return this.numberBox;
	}
	
	public void hideNearBy() {
		nearCB.setVisible(false);
		
		// Fix CSS
		textBoxPanel.getElement().getStyle().setProperty("border-bottom", "none");
		getElement().getStyle().setProperty("border-bottom", "1px solid rgb(185, 184, 184)");
	}
	
	public void setValue(Double value) {
		if(null == value) {
			nearCB.setValue(false);
			numberBox.setValue(null);
		} else numberBox.setValue(value);
	}
	
	public void setValue(Double value, boolean fireEvent) {
		if(null == value) {
			nearCB.setValue(false, fireEvent);
			numberBox.setValue(null, fireEvent);
		} else numberBox.setValue(value, fireEvent);
	}

	public Double getValue() {
		return numberBox.getValue();
	}
	
	public Double getGTValue() {
		return gtnumberBox.getValue();
	}
	
	public Double getLTValue() {
		return ltnumberBox.getValue();
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

	public void addValueChangeHandler(ValueChangeHandler<Double> valueChangeHandler) {
		numberBox.addValueChangeHandler(valueChangeHandler);
		gtnumberBox.addValueChangeHandler(valueChangeHandler);
		ltnumberBox.addValueChangeHandler(valueChangeHandler);
	}
	
	public void setMaxWidth(String maxWidth) {
		getElement().getStyle().setProperty("max-width", maxWidth);
	}
	
	public void setMinWidth(String minWidth) {
		getElement().getStyle().setProperty("min-width", minWidth);
	}

}
