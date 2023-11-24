package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AonSettleDateDialog extends AonCustomDialog {
	
	public AonSettleDateDialog(String caption) {
		this.setCaption(caption);
		this.showCloseButton(true);
		
		setWidth("400px");
		
		this.add(createContent());
		
		showDialog();
	}

	private Widget createContent() {
		HTMLPanel content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setPadding(1, Unit.EM);
		
		Label selectLabel = new Label("Fecha venicimiento de n\u00f3minas:");
		selectLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		content.add(selectLabel);
		
		HTMLPanel dateContent = new HTMLPanel("");
		dateContent.addStyleName(AON.CSS.aonItemFlex());
		content.add(dateContent);
		
		ListBox monthList = new ListBox();
		monthList.addItem("Enero", "0");
		monthList.addItem("Febrero", "1");
		monthList.addItem("Marzo", "2");
		monthList.addItem("Abril", "3");
		monthList.addItem("Mayo", "4");
		monthList.addItem("Junio", "5");
		monthList.addItem("Julio", "6");
		monthList.addItem("Agosto", "7");
		monthList.addItem("Septiembre", "8");
		monthList.addItem("Octubre", "9");
		monthList.addItem("Noviembre", "10");
		monthList.addItem("Diciembre", "11");
		dateContent.add(monthList);
		
		IntegerBox yearTB = new IntegerBox();
		yearTB.setMaxLength(4);
		yearTB.getElement().getStyle().setProperty("height", "1.1rem");
		dateContent.add(yearTB);
		
		// Set current date
		Date currentDate = new Date();
		setSelectedValueLB(monthList, currentDate.getMonth() + "");
		yearTB.setValue(currentDate.getYear() + 1900);
		
	    HTMLPanel buttonsPanel = new HTMLPanel("");
	    buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
	    
	    Button acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText("Generar");
		acceptBtnDialog.addClickHandler(e -> {
			Date date = new Date(yearTB.getValue() - 1900, Integer.parseInt(monthList.getSelectedValue()), 1);
			date =  DateUtils.getLastDayOfMonth(date);
			onAccept(date);
			hide();
		});

		buttonsPanel.add(acceptBtnDialog);
	    
		content.add(buttonsPanel);
		
		return content;
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
		String text = str;
		int indexToFind = 0;
		for (int i = 0; i < lBox.getItemCount(); i++) {
			if (lBox.getValue(i).equals(text)) {
				indexToFind = i;
				break;
			}
		}
		lBox.setSelectedIndex(indexToFind);
	}

	private void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	protected abstract void onAccept(Date settleDate);
	
}
