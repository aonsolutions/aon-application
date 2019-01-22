package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AgrarianAFI extends MainEntryPoint {
	
	final AgrarianAFIServiceAsync impl = GWT.create(AgrarianAFIService.class);
	
	interface Binder extends UiBinder<Widget, AgrarianAFI> {
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
		String widthFirstColumn();
		String paddingText();
	}
	
	@UiField
	TableElement dataTable;
	
	@UiField
	Label enterprise;
	
	@UiField
	ListBox cccs;
	
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
	
	@Override
	public void onModuleLoad() {
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get("rootPanel").add(ui);
		
		//Enterprise Name
		enterprise.setText("REGIMEN GENERAL");
		enterprise.addStyleName(style.bold());
		enterprise.addStyleName(style.paddingText());
		
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
		
//		Window.alert("IMPL : " + impl);
		
		impl.getDomainName(new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
//				Window.alert("DOMAIN NAME : " + result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
//				Window.alert("NO DOMAIN NAME");
			}
		});
				
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
		employeeTable.getWidget(newRow, 1).addStyleName(style.widthFirstColumn());
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
	}
	
	
	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------
	
}
