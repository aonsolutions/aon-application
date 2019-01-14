package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AgrarianAFIDialog extends CustomDialog {
	
	interface Callback {
		void onAccept(AgrarianAFIDialog dialog);
	}
	
	interface Binder extends UiBinder<Widget, AgrarianAFIDialog> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String bold();
		String hide();
		String paddingDays();
		String widthDays();
		String widthName();
		String textCenter();
	}
	
	@UiField
	TableElement dataTable;
	
	@UiField
	Label enterprise;
	
	@UiField
	ListBox monthList;
	
	@UiField
	ListBox yearList;
	
	@UiField
	Button searchAgrarian;
	
	@UiField
	VerticalPanel employeePanel;
	
	@UiField
	Grid employeeTable;
	
	private Callback cb;
	
	public AgrarianAFIDialog() {
		setCaption("JORNADAS REALES AGRARIAS");
		setWidget(binder.createAndBindUi(this));
		
		//Enterprise Name
		enterprise.setText("REGIMEN GENERAL");
		enterprise.addStyleName(style.bold());
		
		//Add Months
		monthList.addItem("Enero");
		monthList.addItem("Febrero");
		monthList.addItem("Marzo");
		monthList.addItem("Abril");
		monthList.addItem("Mayo");
		monthList.addItem("Junio");
		monthList.addItem("Julio");
		monthList.addItem("Agosto");
		monthList.addItem("Septiembre");
		monthList.addItem("Octubre");
		monthList.addItem("Noviembre");
		monthList.addItem("Diciembre");
		
		//Add Years
		Date currentDate = new Date();
		Integer currentYear = currentDate.getYear();
		Integer currentParseYear = currentYear + 1900;
		Integer previusYear = currentParseYear -1;
		
		yearList.addItem(currentParseYear+"");
		yearList.addItem(previusYear+"");
		
		//Style Data Table
		dataTable.getStyle().setMargin(5, Unit.PX);
		
		//Hide EmployeePanel
		employeePanel.addStyleName(style.hide());
	}

	private HorizontalPanel createMonthPanel() {
		HorizontalPanel hPanel = new HorizontalPanel();
		Integer maxDays = DateUtils.getLastDayOfMonth(new Date(Integer.parseInt(yearList.getSelectedItemText())-1900, monthList.getSelectedIndex(), 1)).getDate();
		for(int i = 0; i < maxDays; i++) {
			Label day = new Label((i+1)+"");
			day.addStyleName(style.widthDays());
			day.addStyleName(style.paddingDays());
			day.addStyleName(style.bold());
			day.addStyleName(style.textCenter());
			hPanel.add(day);
		}
		return hPanel;
	}
	
	private HorizontalPanel createAgrarianMonthPanel() {
		HorizontalPanel hPanel = new HorizontalPanel();
		Integer maxDays = DateUtils.getLastDayOfMonth(new Date(Integer.parseInt(yearList.getSelectedItemText())-1900, monthList.getSelectedIndex(), 1)).getDate();
		for(int i = 0; i < maxDays; i++) {
			Label day = new Label();
			if(i % 2 != 0)
				day.setText("S");
			else
				day.setText("-");
			
			day.addStyleName(style.widthDays());
			day.addStyleName(style.paddingDays());
			day.addStyleName(style.textCenter());
			hPanel.add(day);
		}
		return hPanel;
	}
	

	private HorizontalPanel createAgrarianMonthPanel2() {
		HorizontalPanel hPanel = new HorizontalPanel();
		Integer maxDays = DateUtils.getLastDayOfMonth(new Date(Integer.parseInt(yearList.getSelectedItemText())-1900, monthList.getSelectedIndex(), 1)).getDate();
		for(int i = 0; i < maxDays; i++) {
			Label day = new Label();
			if(i % 3 != 0)
				day.setText("S");
			else
				day.setText("-");
			
			day.addStyleName(style.widthDays());
			day.addStyleName(style.paddingDays());
			day.addStyleName(style.textCenter());
			hPanel.add(day);
		}
		return hPanel;
	}

	public void show(Callback cb) {
		this.cb = cb;
		super.show();
	}
	
	public void setPopupPositionAndShow(PositionCallback positionCallback, Callback callback) {
		this.cb = callback;
		super.setPopupPositionAndShow(positionCallback);
	}
	
	public void setAgrarianAFIDialogObject() {

	}

	// ------------------------------------------------------------------------
	//						Initialize Logic Window
	// ------------------------------------------------------------------------
	
	

	// ------------------------------------------------------------------------
	//							UiHandler Accept/Cancel
	// ------------------------------------------------------------------------
	
	@UiHandler("searchAgrarian")
	void onSearchAgrariantButtonClick(ClickEvent clickEvent) {
		//Remove hide
		employeePanel.removeStyleName(style.hide());
		
		//Header Grid
		employeeTable.resize(0,3);
		Integer newRow = employeeTable.insertRow(employeeTable.getRowCount());
		Label selectEmployee = new Label();
		employeeTable.setWidget(newRow, 0, selectEmployee);
		Label employeeLabel = new Label("Empleado");
		employeeLabel.addStyleName(style.bold());
		employeeTable.setWidget(newRow, 1, employeeLabel);
		HorizontalPanel month = createMonthPanel();
		employeeTable.setWidget(newRow, 2, month);
		
		//Fill Practice Row
		Integer newRow2 = employeeTable.insertRow(employeeTable.getRowCount());
		CheckBox select = new CheckBox();
		employeeTable.setWidget(newRow2, 0, select);
		Label employeeName = new Label("Valdepenas, Sergio");
		employeeName.addStyleName(style.widthName());
		employeeTable.setWidget(newRow2, 1, employeeName);
		HorizontalPanel agrarianMonth = createAgrarianMonthPanel();
		employeeTable.setWidget(newRow2, 2, agrarianMonth);
		
		//Fill Practice Row
		Integer newRow3 = employeeTable.insertRow(employeeTable.getRowCount());
		CheckBox select2 = new CheckBox();
		employeeTable.setWidget(newRow3, 0, select2);
		Label employeeName2 = new Label("Calvo, Patricia");
		employeeName.addStyleName(style.widthName());
		employeeTable.setWidget(newRow3, 1, employeeName2);
		HorizontalPanel agrarianMonth2 = createAgrarianMonthPanel2();
		employeeTable.setWidget(newRow3, 2, agrarianMonth2);
		
		this.center();
	}
	
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent clickEvent) {
		hide();
	}
	
	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent clickEvent) {
		
	}
	
	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------
	
}
