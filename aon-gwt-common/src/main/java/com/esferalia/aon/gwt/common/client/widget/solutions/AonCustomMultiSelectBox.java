package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class AonCustomMultiSelectBox extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private HTMLPanel customListBox;
	private Label selectionLabel;
	
	private PopupPanel optionsPopup = new PopupPanel(true);
	private Set<String> selectedOptions = new LinkedHashSet<String>();
	private Map<String, CheckBox> valuesCB = new HashMap<>();
	
//	private boolean hasFocus = false;
	
	public AonCustomMultiSelectBox(String title) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn());
		addStyleName(AON.CSS.aonCustomTextBox());
		
		// Agregar manejador de blur en el componente
//      RootPanel.get().addDomHandler(event -> {
//            if (hasFocus && !optionsPopup.isShowing()) {
//                fireBlurEvent(); // Lanzar el evento si se pierde el foco
//                hasFocus = false;
//            }
//      }, ClickEvent.getType());

		createTitle(title);
		createInput();
	}

	private void createTitle(String title) {
		HTMLPanel titleLabel = new HTMLPanel(AonStringUtils.isNotBlank(title) ? title : EMPTY_STRING);
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
	}

	private void createInput() {
		customListBox = new HTMLPanel(EMPTY_STRING);
		customListBox.setStyleName(AON.CSS.aonItemFlex());
		customListBox.addStyleName(AON.CSS.aonFlexBetween());
		customListBox.addDomHandler(e -> onOptionsOpen(), ClickEvent.getType());
		
		selectionLabel = new Label("Seleccione un valor");
		selectionLabel.getElement().getStyle().setProperty("width", "14rem");
		selectionLabel.getElement().getStyle().setProperty("white-space", "nowrap");
		selectionLabel.getElement().getStyle().setProperty("overflow", "hidden");
		selectionLabel.getElement().getStyle().setProperty("text-overflow", "ellipsis");
		customListBox.add(selectionLabel);
		
		AonTableButton downBtn = new AonTableButton("", AON.CSS.aonIconDown());
		customListBox.add(downBtn);
		
		add(customListBox);
	}

	private void onOptionsOpen() {
		int top = this.getAbsoluteTop() + this.getOffsetHeight();
        int left = this.getAbsoluteLeft();
        optionsPopup.setPopupPosition(left, top);
        optionsPopup.getElement().getStyle().setZIndex(3);
        optionsPopup.show(); // Muestra con animación
//        hasFocus = true;
	}
	
	public void setOptions(Set<String> options) {
		HTMLPanel popupContent = new HTMLPanel("");
        popupContent.setStyleName(AON.CSS.aonSearchFilterPopup());
        popupContent.getElement().getStyle().setProperty("width", "14.5rem");
        popupContent.getElement().getStyle().setProperty("height", "10rem");
        
        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setStyleName(AON.CSS.aonSearchFilterScroll());
        
    	// Filter
        HTMLPanel filterMenu = new HTMLPanel("");
  		filterMenu.addStyleName(AON.CSS.aonFlexColumn());
        
        options.forEach(option -> {
        	HTMLPanel checkBoxPanel = new HTMLPanel(EMPTY_STRING);
        	checkBoxPanel.setStyleName(AON.CSS.aonItemFlex());
        	checkBoxPanel.getElement().getStyle().setProperty("border-bottom", "1px solid #c6c6c6");
        	checkBoxPanel.getElement().getStyle().setProperty("padding-bottom", ".3rem");
        	checkBoxPanel.getElement().getStyle().setProperty("cursor", "pointer");
        	
        	CheckBox checkBox = new CheckBox();
        	checkBox.getElement().getStyle().setProperty("cursor", "pointer");
        	checkBoxPanel.add(checkBox);
        	
        	Label valueLabel = new Label(option);
        	checkBoxPanel.add(valueLabel);
        	
        	filterMenu.add(checkBoxPanel);
        	
        	checkBox.addValueChangeHandler(e -> changeCheckBoxValue(checkBox, option));
        	checkBoxPanel.addDomHandler(e -> changeCheckBoxValue(checkBox, option), ClickEvent.getType());
        	
        	valuesCB.put(option, checkBox);
        });
        
        scrollPanel.add(filterMenu);
        popupContent.add(scrollPanel);
		
        optionsPopup.setWidget(popupContent);
    }
	
	private void changeCheckBoxValue(CheckBox checkBox, String option) {
		checkBox.setValue(!checkBox.getValue());
		
		if(checkBox.getValue()) selectedOptions.add(option);
		else selectedOptions.remove(option);
		
		selectionLabel.setText(selectedOptions.isEmpty() ? "Seleccione un valor" : String.join(", ", selectedOptions));
		
		fireBlurEvent();
	}
	
	public void setSelectedOptions(Set<String> options) {
		selectedOptions = options;
		options.forEach(option -> valuesCB.get(option).setValue(true));
		selectionLabel.setText(selectedOptions.isEmpty() ? "Seleccione un valor" : String.join(", ", selectedOptions));
	}
	
	public Set<String> getSelectedOptions(){
		return this.selectedOptions;
	}
	
	private void fireBlurEvent() {
        BlurEvent blurEvent = new BlurEvent() {};
        fireEvent(blurEvent);
    }

    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return addDomHandler(handler, BlurEvent.getType());
    }

}
