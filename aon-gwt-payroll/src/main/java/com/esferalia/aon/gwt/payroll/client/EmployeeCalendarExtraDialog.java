package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeCalendarExtraDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarExtraDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	TextBox januaryTB;
	
	@UiField
	TextBox februaryTB;
	
	@UiField
	TextBox marchTB;
	
	@UiField
	TextBox aprilTB;
	
	@UiField
	TextBox mayTB;
	
	@UiField
	TextBox juneTB;
	
	@UiField
	TextBox julyTB;
	
	@UiField
	TextBox augustTB;
	
	@UiField
	TextBox septemberTB;
	
	@UiField
	TextBox octoberTB;
	
	@UiField
	TextBox novemberTB;
	
	@UiField
	TextBox decemberTB;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	// -------------------------------------------------------------------------------
	// --------------------------------- MAIN CLASS ----------------------------------
	// -------------------------------------------------------------------------------

	private int year;
	private EmployeeCalendarDraftObject employeeCalendarDraftObject;
	private TextBox[] textBoxes = new TextBox[12];
	
	public EmployeeCalendarExtraDialog(int year, EmployeeCalendarDraftObject employeeCalendarDraftObject) {
		this.year = year;
		this.employeeCalendarDraftObject = employeeCalendarDraftObject;
		
		if(this.employeeCalendarDraftObject.isFullTimeJourney())
			setCaption("HORAS EXTRAS " + (1900 + this.year));
		else
			setCaption("HORAS COMPLEMENTARIAS " + (1900 + this.year));
		
		setWidget(binder.createAndBindUi(this));
		
		textBoxes[0] = this.januaryTB;
		textBoxes[1] = this.februaryTB;
		textBoxes[2] = this.marchTB;
		textBoxes[3] = this.aprilTB;
		textBoxes[4] = this.mayTB;
		textBoxes[5] = this.juneTB;
		textBoxes[6] = this.julyTB;
		textBoxes[7] = this.augustTB;
		textBoxes[8] = this.septemberTB;
		textBoxes[9] = this.octoberTB;
		textBoxes[10] = this.novemberTB;
		textBoxes[11] = this.decemberTB;
		
		initExtraHourTB();
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				
			}
		});
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				accept();
			}
		});	
		
	}

	private void initExtraHourTB() {
		for(int month = 0; month < 12; month++) {
			Date date = new Date(year, month, 1);
			DateUtils.resetTime(date);
			
			String hourMonth = employeeCalendarDraftObject.getExtraHourByDate(date);
			textBoxes[month].setValue(hourMonth);
			
		}
	}
	
	private void accept() {
		for(int month = 0; month < 12; month++) {
			String hourMonth = textBoxes[month].getValue();
			
			Date date = new Date(this.year, month, 1);
			DateUtils.resetTime(date);
			
			employeeCalendarDraftObject.setExtraHourByDate(date, hourMonth);
		}
	}
}
