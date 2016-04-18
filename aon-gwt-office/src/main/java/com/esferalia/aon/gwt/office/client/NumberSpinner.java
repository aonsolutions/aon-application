package com.esferalia.aon.gwt.office.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IntegerBox;

public class NumberSpinner extends Composite {

    private int rate = 5;
    private int max = 100;
    private IntegerBox integerBox;

    public NumberSpinner(Integer defaultValue) {
        AbsolutePanel absolutePanel = new AbsolutePanel();
        initWidget(absolutePanel);
        absolutePanel.setSize("55px", "15px");

        integerBox = new IntegerBox();
        absolutePanel.add(integerBox, 0, 0);
        integerBox.setSize("25px", "16px");
        integerBox.getElement().getStyle().setBorderWidth(0, Unit.PX);
        integerBox.setValue(defaultValue <= getMax() ? defaultValue : getMax());

        Button upButton = new Button();
        upButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	setValue((getValue() + rate) <= getMax() ? getValue() + rate : getMax());
            }
        });
        upButton.setStyleName("aon-icon-up-arrow");
        absolutePanel.add(upButton, 29, 0);
        upButton.getElement().getStyle().setBorderWidth(0, Unit.PX);
        upButton.setSize("16px", "8px");

        Button downButton = new Button();
        downButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setValue((getValue() - getRate()) > 0 ? getValue() - getRate() : 0);
            }
        });
        downButton.setStyleName("aon-icon-down-arrow");
        absolutePanel.add(downButton, 29, 8);
        downButton.getElement().getStyle().setBorderWidth(0, Unit.PX);
        downButton.setSize("16px", "7.5px");
    }

    public int getValue() {
        return integerBox.getValue() == null ? 0 : integerBox.getValue();
    }

    public void setValue(int value) {
        integerBox.setValue(value);
    }

    public Integer getRate(){
    	return rate;
    }

    public NumberSpinner setRate(Integer rate) {
        this.rate = rate;
        return this;
    }

	public Integer getMax() {
		return max;
	}

	public NumberSpinner setMax(Integer max) {
		this.max = max;
		return this;
	}
    
    
}