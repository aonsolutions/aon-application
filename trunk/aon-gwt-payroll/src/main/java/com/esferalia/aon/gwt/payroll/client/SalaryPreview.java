package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class SalaryPreview extends ResizeComposite {

	private static final int DATE_DROP_RANGE = 12;

	private static final int ZOOM_STEP = 20;
	private static final int MIN_ZOOM =  25;
	private static final int MAX_ZOOM =  500;
	private static final int DEFAULT_ZOOM = 135;
	
	private static final DateTimeFormat DATE_FORMAT = 
			DateTimeFormat.getFormat(PredefinedFormat.YEAR_MONTH);


	interface Binder extends UiBinder<Widget, SalaryPreview> {
	}

	private static final Binder binder = GWT.create(Binder.class);


	@UiField
	MenuItem reduceMenuItem;
	@UiField
	MenuItem enlargeMenuItem;

	@UiField
	ListBox dateListBox;
	@UiField
	ListBox salaryTypeListBox;

	@UiField
	HTML container;

	private int zoom = DEFAULT_ZOOM;

	private SalaryPreviewDocument previewDocument;

	public SalaryPreview() {
		initWidget(binder.createAndBindUi(this));
		initSalaryTypeListBox();
		initDateListBox();

		
		reduceMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				zoom = Math.max(MIN_ZOOM, zoom - ZOOM_STEP);
				getAsHTML();
			}
		});

		enlargeMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				zoom = Math.min(MAX_ZOOM, zoom + ZOOM_STEP);
				getAsHTML();
			}
		});
	}

	public void setSalaryPreviewDocument(SalaryPreviewDocument draftDocument) {
		this.previewDocument = draftDocument;
		onSalaryPreviewDocumentChanged();
	}

	private void onSalaryPreviewDocumentChanged() {
		getAsHTML();
		syncStartDateBox();
	}

	private void getAsHTML() {
		previewDocument.getAsHTML(zoom, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String html) {
				container.setHTML(html);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				container.setHTML(caught.getLocalizedMessage());
			}
		});

	}

	private void syncStartDateBox() {
		
		dateListBox.clear();
		
		com.esferalia.aon.gwt.payroll.shared.SalaryPreview draft = previewDocument
				.getSalaryPreview();

		Employee employee = draft.getEmployee();

		Date startDate = CalendarUtil.copyDate(
				employee.getStartDate());
		Date draftDate = CalendarUtil.copyDate( 
				draft.getEndDate() );
		Date endDate = CalendarUtil.copyDate(
				employee.getEndDate());
		
		
		CalendarUtil.setToFirstDayOfMonth(startDate);
		CalendarUtil.setToFirstDayOfMonth(draftDate);
		
		Date firstDate = getFirstDropDate(draftDate, startDate );
		Date lastDate = getLastDropDate(draftDate, endDate );
		
		Date date = CalendarUtil.copyDate(firstDate);
		
		while ( draftDate.after(date) ) {
			dateListBox.addItem(DATE_FORMAT.format(date));
			CalendarUtil.addMonthsToDate(date, 1);
		}
		
		int draftIndex = dateListBox.getItemCount();
		
		while ( lastDate.after(date) ) {
			dateListBox.addItem(DATE_FORMAT.format(date));
			CalendarUtil.addMonthsToDate(date, 1);
		}

		dateListBox.setSelectedIndex(draftIndex);
	}

	
	private void initDateListBox() {

		dateListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int selectedIndex = 
						dateListBox.getSelectedIndex();
				String text = 
						dateListBox.getValue(selectedIndex);
				Date date = DATE_FORMAT.parse(text);
				Date startDate = DateUtils.getFirstDayOfMonth(date);
				Date endDate = DateUtils.getLastDayOfMonth(date);
				previewDocument.getSalaryPreview().setStartDate(startDate);
				previewDocument.getSalaryPreview().setEndDate(endDate);
				previewDocument.getSalaryPreview().setIssueDate(endDate);
				getAsHTML();
			}
		});
	}


	private void initSalaryTypeListBox() {
		salaryTypeListBox.addItem("Nomina", Salary.Type.SALARY.name());

		salaryTypeListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int selectedIndex = 
						salaryTypeListBox.getSelectedIndex();
				String name = 
						salaryTypeListBox.getValue(selectedIndex);
				Salary.Type type = Salary.Type.valueOf(name);
				previewDocument.getSalaryPreview().setType(type);
				getAsHTML();
			}
		});
	}


	private static Date getLastDropDate(Date date, Date end) {
		Date lastDate = CalendarUtil.copyDate(date);
		CalendarUtil.addMonthsToDate(lastDate, DATE_DROP_RANGE/2);
		return DateUtils.before(lastDate, end );

	}

	private static Date getFirstDropDate(Date date, Date start) {
		Date firstDate = CalendarUtil.copyDate(date);
		CalendarUtil.addMonthsToDate(firstDate, -DATE_DROP_RANGE/2);
		return DateUtils.after(firstDate, start );

	}
	
}
