package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;

public class ContractDaysPeriodPanel extends FlowPanel {
	
	public static interface ContractDaysPeriodPanelCallback {
		void onAccept(Date start, Date end, Boolean extended, Boolean totals);
	}

	private AonCustomListBox monthStartListBox = new AonCustomListBox("Mes Inicio");
	private AonCustomIntegerBox yearStartBox = new AonCustomIntegerBox("A\u00f1o Inicio");
	
	private AonCustomListBox monthEndListBox = new AonCustomListBox("Mes Fin");
	private AonCustomIntegerBox yearEndBox = new AonCustomIntegerBox("A\u00f1o Fin");
	
	private AonCustomListBox typeListBox = new AonCustomListBox("Tipo");
	private AonCustomCheckBox totalsCB = new AonCustomCheckBox("Mostrar Totales");
	
	public ContractDaysPeriodPanel(ContractDaysPeriodPanelCallback callback) {
		setStyleName(AON.CSS.aonItemFlex());
		addStyleName(AON.CSS.aonFlexColumn());
		getElement().getStyle().setProperty("padding", "1rem 0");
		setWidth("20rem");
		
		HTMLPanel messagePanel = new HTMLPanel("");
		add(messagePanel);
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonItemFlex());
		rootPanel.addStyleName(AON.CSS.aonFlexColumn());
		rootPanel.setWidth("90%");
		
		FlowPanel startPanel = new FlowPanel();
		startPanel.setStyleName(AON.CSS.aonItemFlex());
		startPanel.setWidth("100%");
		
		fillMothsListBox(monthStartListBox);
		
		yearStartBox.setWidth("10rem");
		yearStartBox.hideNearBy();
		yearStartBox.getNumberBox().setMaxLength(4);
		yearStartBox.getNumberBox().getElement().setPropertyString("placeholder", "A\u00f1o");
		
		startPanel.add(monthStartListBox);
		startPanel.add(yearStartBox);
		
		FlowPanel endPanel = new FlowPanel();
		endPanel.setStyleName(AON.CSS.aonItemFlex());
		endPanel.setWidth("100%");
		
		fillMothsListBox(monthEndListBox);
		
		yearEndBox.setWidth("10rem");
		yearEndBox.hideNearBy();
		yearEndBox.getNumberBox().setMaxLength(4);
		yearEndBox.getNumberBox().getElement().setPropertyString("placeholder", "A\u00f1o");
		
		endPanel.add(monthEndListBox);
		endPanel.add(yearEndBox);
		
		FlowPanel typePanel = new FlowPanel();
		typePanel.setStyleName(AON.CSS.aonItemFlex());
		typePanel.setWidth("100%");
		
		typeListBox.clearItems();
		typeListBox.addItem("Acumulado", "false");
		typeListBox.addItem("Detallado", "true");
		
		totalsCB.setWidth("10rem");
		
		typePanel.add(typeListBox);
		typePanel.add(totalsCB);
		
		rootPanel.add(startPanel);
		rootPanel.add(endPanel);
		rootPanel.add(typePanel);
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	buttons.addStyleName(AON.CSS.aonMarginTop());
    	
    	Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept() );
    	
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
    		if(null == getStartDate()) {
    			AonMessagePanel.showWarning(messagePanel, "La F. Incio es obligatoria");
    			okButton.setEnabled(true);
    		} else
    			callback.onAccept(getStartDate(), getEndDate(), Boolean.parseBoolean(typeListBox.getValue()), totalsCB.getValue());
    	});
    	
    	buttons.add(okButton);
    	
    	rootPanel.add(buttons);
		
    	add(rootPanel);	
	}

	private void fillMothsListBox(AonCustomListBox listBox) {
		listBox.clearItems();
		listBox.addItem("Enero", "0");
		listBox.addItem("Febrero", "1");
		listBox.addItem("Marzo", "2");
		listBox.addItem("Abril", "3");
		listBox.addItem("Mayo", "4");
		listBox.addItem("Junio", "5");
		listBox.addItem("Julio", "6");
		listBox.addItem("Agosto", "7");
		listBox.addItem("Septiembre", "8");
		listBox.addItem("Octubre", "9");
		listBox.addItem("Noviembre", "10");
		listBox.addItem("Diciembre", "11");	
	}
	
	private Date getStartDate() {
		if(null == yearStartBox.getValue()) return null;
		try {
			return DateUtils.getFirstDayOfMonth( DateUtils.getDate(Integer.parseInt(monthStartListBox.getValue()), yearStartBox.getValue()) );
		} catch (Exception e) { return null; }
	}
	
	private Date getEndDate() {
		if(null == yearEndBox.getValue()) return null;
		try {
			return DateUtils.getLastDayOfMonth( DateUtils.getDate(Integer.parseInt(monthEndListBox.getValue()), yearEndBox.getValue()) );
		} catch (Exception e) { return null; }
	}

}
