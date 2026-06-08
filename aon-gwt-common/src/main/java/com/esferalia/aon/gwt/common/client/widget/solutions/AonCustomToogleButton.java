package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;

public class AonCustomToogleButton extends HTMLPanel implements HasValue<Boolean> {

    private static final String EMPTY_STRING = "";
    private HTMLPanel toogleButtonPanel;
    private Button toogleButton;
    private boolean value = false;

    public AonCustomToogleButton(String title) {
        super(EMPTY_STRING);
        addStyleName(AON.CSS.aonFlexColumn2());
        addStyleName(AON.CSS.aonCustomTextBox());

        createTitle(title);
        createToogleButton();
    }

    private void createTitle(String title) {
        HTMLPanel titleLabel = new HTMLPanel(title);
        titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
        add(titleLabel);
    }

    private void createToogleButton() {
        toogleButtonPanel = new HTMLPanel("");
        toogleButtonPanel.getElement().getStyle().setProperty("padding-left", ".5rem");

        toogleButton = new Button();
        setValue(false, false); // inicializar sin disparar evento

        toogleButton.addClickHandler(e -> setValue(!value, true)); // dispara evento
        toogleButtonPanel.add(toogleButton);

        add(toogleButtonPanel);
    }

    @Override
    public void setValue(Boolean value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Boolean value, boolean fireEvents) {
        this.value = value;

        getEnableDisableButton(toogleButton, value);

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void getEnableDisableButton(Button button, boolean enabled) {
        button.removeStyleName(enabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
        button.removeStyleName(AON.AON_NO_MARGIN);
        button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);

        button.setStyleName(!enabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
        button.setStyleName(AON.AON_NO_MARGIN, true);
        button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
    }
}

