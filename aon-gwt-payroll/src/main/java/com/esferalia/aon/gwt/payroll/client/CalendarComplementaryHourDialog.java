package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CalendarDaysType.DayType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class CalendarComplementaryHourDialog extends AonCustomDialog {

	// ------------------------------------ UiBinder
	
	interface Binder extends UiBinder<Widget, CalendarComplementaryHourDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ------------------------------------ UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String cell();
	}
	
	@UiField
	ListBox monthLB;
	
	@UiField
	HTMLPanel header;
	
	@UiField
	HTMLPanel table;
	
	@UiField
	Label errorMessage;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------ Variables
	
	private EmployeeCalendarDraftObject employeeCalendarDraftObject;
	private List<Date> selectedDates;
	
	private Date contractStartDate;
	private Date contractEndDate;
	
	private Integer selectedYear;
	
	// ------------------------------------ Consructor
	
	protected CalendarComplementaryHourDialog(List<Date> selectedDates, Date contractStartDate, Date contractEndDate, Integer year, EmployeeCalendarDraftObject employeeCalendarDraftObject) {
		setCaption("HORAS COMPLEMENTARIAS");
		setWidget(binder.createAndBindUi(this));
		
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.selectedYear = year;
		this.selectedDates = selectedDates;
		this.employeeCalendarDraftObject = employeeCalendarDraftObject;
		
		getButtonsPanel();
		initView();
		createTable();
		showDialog();
	}
	
	// ------------------------------------ View
	
	private void initView() {
		createMonthLB();
		setDefaultView();
	}

	private void createMonthLB() {
		this.monthLB.clear();
		this.monthLB.addItem("Enero", "0");
		this.monthLB.addItem("Febrero", "1");
		this.monthLB.addItem("Marzo", "2");
		this.monthLB.addItem("Abril", "3");
		this.monthLB.addItem("Mayo", "4");
		this.monthLB.addItem("Junio", "5");
		this.monthLB.addItem("Julio", "6");
		this.monthLB.addItem("Agosto", "7");
		this.monthLB.addItem("Septiembre", "8");
		this.monthLB.addItem("Octubre", "9");
		this.monthLB.addItem("Noviembre", "10");
		this.monthLB.addItem("Diciembre", "11");
		
		this.monthLB.addChangeHandler(e -> createTable());	
	}

	private void setDefaultView() {
		Date date = new Date();
		if(!selectedDates.isEmpty())
			date = selectedDates.get(0);
		
		int month = DateUtils.getMonth(date);
		setSelectedValueLB(monthLB, month + "");
	}

	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (AonStringUtils.equalsIgnoreCase(lBox.getValue(i), text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}
	
	// ------------------------------------ Create table
	
	private void createTable() {
		createHeader();
		createTableRow();
	}

	private void createHeader() {
		header.clear();
		
		Date date = DateUtils.getDate(Integer.parseInt(monthLB.getSelectedValue()), selectedYear);
		int maxDateMonth = DateUtils.getLastDayOfMonth(date).getDate();
		
		for(int day=1; day <= maxDateMonth; day++) {
			Label dayL = new Label(day + "");
			dayL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			dayL.addStyleName(style.cell());
			
			Date findingDate = DateUtils.copyDateOnly(date);
			findingDate.setDate(day);
			
			DayType dayType = this.employeeCalendarDraftObject.getDayTypeByDate(findingDate);
			if(null != dayType && DayType.BAJAIT.equals(dayType)) {
				dayL.getElement().getStyle().setColor("red");
				dayL.setTitle("Baja IT");
			}
			
			header.add(dayL);
		}
	}

	private void createTableRow() {
		table.clear();
		
		Date date = DateUtils.getDate(Integer.parseInt(monthLB.getSelectedValue()), selectedYear);
		int maxDateMonth = DateUtils.getLastDayOfMonth(date).getDate();
		
		for(int day=1; day <= maxDateMonth; day++) {
			DoubleBox value = new DoubleBox(6, 2);
			value.addStyleName(style.cell());
			
			Date findingDate = DateUtils.copyDateOnly(date);
			findingDate.setDate(day);
			value.setValue(employeeCalendarDraftObject.getComplementaryHourByDate(findingDate));
			
			DayType dayType = this.employeeCalendarDraftObject.getDayTypeByDate(findingDate);
			if(null != dayType && DayType.BAJAIT.equals(dayType)) {
				value.setEnabled(false);
				value.setTitle("Baja IT");
			}
			
			table.add(value);
		}
	}

	// ------------------------------------ Accept dialog
	
	private void accept() {
		Date date = DateUtils.getDate(Integer.parseInt(monthLB.getSelectedValue()), selectedYear);
		int maxDateMonth = DateUtils.getLastDayOfMonth(date).getDate();
		
		for(int day=1; day <= maxDateMonth; day++) {
			Date findingDate = DateUtils.copyDateOnly(date);
			findingDate.setDate(day);
			
			if(betweenContratPeriod(findingDate)) {
				DoubleBox doubleBox = (DoubleBox) table.getWidget(day-1);
				if(null != doubleBox.getValue() && 0.00 != doubleBox.getValue())
					employeeCalendarDraftObject.addComplementaryHour(findingDate, doubleBox.getValue());
				else
					employeeCalendarDraftObject.removeComplementaryHour(findingDate);
			}
		}
	}
	
	private boolean betweenContratPeriod(Date date) {
		if(null == contractEndDate)
			return date.after(contractStartDate) || date.equals(contractStartDate);
		
		return (date.after(contractStartDate) || date.equals(contractStartDate)) && (date.before(contractEndDate) || date.equals(contractEndDate));
	}
	
	// ------------------------------------ Buttons panel
	
	private void getButtonsPanel() {
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.addClickHandler(e -> hide());
		
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		
		buttonsPanel.add(closeBtnDialog);
		
		Button acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> {
			accept();
			onAccept();
			hide();
		});
		
		buttonsPanel.add(acceptBtnDialog);
	}

	// ------------------------------------ Abstract method
	
	protected abstract void onAccept();
	
	// ------------------------------------ Show dialog
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
}
