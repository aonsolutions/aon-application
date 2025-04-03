package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMonthSelect;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;

public class ContractDaysPeriodPanel extends FlowPanel {
	
	public static interface ContractDaysPeriodPanelCallback {
		void onAccept(Date start, Date end, Boolean extended, Boolean totals);
	}

	private AonCustomMonthSelect monthStartListBox = new AonCustomMonthSelect("F. Inicio");
//	private AonCustomIntegerBox yearStartBox = new AonCustomIntegerBox("A\u00f1o Inicio");
	
	private AonCustomMonthSelect monthEndListBox = new AonCustomMonthSelect("F. Fin");
//	private AonCustomIntegerBox yearEndBox = new AonCustomIntegerBox("A\u00f1o Fin");
	
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
		
		Date firstMonth = DateUtils.addYears2Date(DateUtils.getFirstDayOfMonth(new Date()), -3);
		Date lastMonth = DateUtils.addYears2Date(DateUtils.getFirstDayOfMonth(new Date()), 3);
		Date defaultDate = DateUtils.getFirstDayOfMonth(new Date());
		
		monthStartListBox.getAonCustomMonthListBox().setFirstMonth(firstMonth);
		monthStartListBox.getAonCustomMonthListBox().setLastMonth(lastMonth);
		monthStartListBox.getAonCustomMonthListBox().setSelectedMonth(defaultDate);
		
		monthEndListBox.getAonCustomMonthListBox().setFirstMonth(firstMonth);
		monthEndListBox.getAonCustomMonthListBox().setLastMonth(lastMonth);
		monthEndListBox.getAonCustomMonthListBox().setSelectedMonth(defaultDate);
		
		FlowPanel typePanel = new FlowPanel();
		typePanel.setStyleName(AON.CSS.aonItemFlex());
		typePanel.setWidth("100%");
		
		typeListBox.clearItems();
		typeListBox.addItem("Acumulado", "false");
		typeListBox.addItem("Detallado", "true");
		
		totalsCB.setWidth("10rem");
		
		typePanel.add(typeListBox);
		typePanel.add(totalsCB);
		
		rootPanel.add(monthStartListBox);
		rootPanel.add(monthEndListBox);
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
	
	private Date getStartDate() {
		return monthStartListBox.getAonCustomMonthListBox().getSelected();
//		if(null == yearStartBox.getValue()) return null;
//		try {
//			return DateUtils.getFirstDayOfMonth( DateUtils.getDate(Integer.parseInt(monthStartListBox.getValue()), yearStartBox.getValue()) );
//		} catch (Exception e) { return null; }
	}
	
	private Date getEndDate() {
		return null == monthEndListBox.getAonCustomMonthListBox().getSelected() ? null : DateUtils.getLastDayOfMonth(monthEndListBox.getAonCustomMonthListBox().getSelected());
//		if(null == yearEndBox.getValue()) return null;
//		try {
//			return DateUtils.getLastDayOfMonth( DateUtils.getDate(Integer.parseInt(monthEndListBox.getValue()), yearEndBox.getValue()) );
//		} catch (Exception e) { return null; }
	}

}
