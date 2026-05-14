package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class AonCustomTextArea extends HTMLPanel implements RequiresResize  {

	private static final String EMPTY_STRING = "";
	private HTMLPanel textBoxPanel = new HTMLPanel(EMPTY_STRING);
	private TextArea textArea;
	
    public AonCustomTextArea(String title) {
        super(EMPTY_STRING);
        addStyleName(AON.CSS.aonFlexColumn2());
        addStyleName(AON.CSS.aonCustomTextArea());

        createTitle(title);
        createInput(title);
    }

    @Override
    public void onResize() {
        Widget parent = getParent();
        if (parent != null && parent instanceof RequiresResize) {
            ((RequiresResize) parent).onResize();
        }

        // Ajustar el textarea al alto disponible del contenedor
        int height = getOffsetHeight();
        if (height > 0) {
            textArea.setHeight(height + "px");
        }
    }


	private void createTitle(String title) {
		HTMLPanel titleLabel = new HTMLPanel(AonStringUtils.isNotBlank(title) ? title : EMPTY_STRING);
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
	}

	private void createInput(String title) {
		textBoxPanel.addStyleName(AON.CSS.aonItemFlex());
		textBoxPanel.addStyleName(AON.CSS.aonFlexBetween());
		textBoxPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		
		textArea = new TextArea();
		textArea.setVisibleLines(6);
		textArea.setStyleName(AON.CSS.aonCustomTextAreaInput());
		textArea.getElement().setPropertyString("placeholder", "Introduce un valor");

		textArea.getElement().getStyle().setProperty("flex", "1");
		textArea.getElement().getStyle().setProperty("width", "100%");
		textArea.getElement().getStyle().setProperty("box-sixing", "border-box");
		
		textBoxPanel.add(textArea);
		add(textBoxPanel);
	}
	
	public TextArea getTextBox() {
		return this.textArea;
	}

	public void setValue(String value) {
		this.textArea.setValue(value);
	}

	public String getValue() {
		return this.textArea.getValue();
	}
	
	public void setEnable(boolean enabled) {
		this.textArea.setEnabled(enabled);
	}

	public void setFocus(boolean focused) {
		this.textArea.setFocus(focused);
	}

	public void addButton(AonTableButton button) {
		textBoxPanel.add(button);
	}

	public void addValueChangeHandler(ValueChangeHandler<String> valueChangeHandler) {
		textArea.addValueChangeHandler(valueChangeHandler);
	}

}
