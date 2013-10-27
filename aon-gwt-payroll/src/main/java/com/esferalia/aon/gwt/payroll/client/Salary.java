package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Salary extends ResizeComposite {

	private static final int ZOOM_STEP = 20;
	private static final int MIN_ZOOM = 25;
	private static final int MAX_ZOOM = 500;

	private static final int DEFAULT_ZOOM = 135;

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH_DAY);

	interface Binder extends UiBinder<Widget, Salary> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	HTML container;

	@UiField
	Button printButton;
	@UiField
	Button deleteButton;

	@UiField
	MenuItem printMenuItem;
	@UiField
	MenuItem downloadMenuItem;

	@UiField
	MenuItem reduceMenuItem;
	@UiField
	MenuItem enlargeMenuItem;

	@UiField
	ListBox dateListBox;
	@UiField
	ListBox salaryTypeListBox;

	private int zoom = DEFAULT_ZOOM;

	private SalaryDocuments salaryDocuments;

	private List<com.esferalia.aon.gwt.payroll.shared.Salary> salaries;
	private List<com.esferalia.aon.gwt.payroll.shared.Salary.Type> types;

	public Salary() {
		initWidget(binder.createAndBindUi(this));

		types = new ArrayList<com.esferalia.aon.gwt.payroll.shared.Salary.Type>(
				com.esferalia.aon.gwt.payroll.shared.Salary.Type.values().length);

		printMenuItem.setScheduledCommand(new Command() {

			@Override
			public void execute() {
				print();
			}
		});

		downloadMenuItem.setScheduledCommand(new Command() {

			@Override
			public void execute() {
				download();
			}
		});

		reduceMenuItem.setScheduledCommand(new Command() {

			@Override
			public void execute() {
				zoom = Math.max(MIN_ZOOM, zoom - ZOOM_STEP);
				getAsHTML();
			}
		});

		enlargeMenuItem.setScheduledCommand(new Command() {

			@Override
			public void execute() {
				zoom = Math.min(MAX_ZOOM, zoom + ZOOM_STEP);
				getAsHTML();
			}
		});

	}

	public void hideDeleteButton() {
		deleteButton.setVisible(false);
	}

	public void setSalaryDocuments(SalaryDocuments salaryDocuments) {
		this.salaryDocuments = salaryDocuments;
		onSalaryDocumentsChanged();
	}

	@UiHandler("deleteButton")
	void onDeleteButton(ClickEvent e) {
		delete();
	}

	@UiHandler("printButton")
	void onPrintButton(ClickEvent e) {
		print();
	}

	@UiHandler("dateListBox")
	void onDateListBox(ChangeEvent e) {
		onSalaryDateChanged();
		;
	}

	@UiHandler("salaryTypeListBox")
	void onSalaryTypeListBox(ChangeEvent e) {
		onSalaryTypeChanged();
	}

	private void print() {
		salaryDocuments.print();
	}

	private void delete() {
		salaryDocuments.delete(new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void v) {
				onSalaryDocumentsChanged();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
		
	}

	private void download() {
		salaryDocuments.download();
	}

	private void getAsHTML() {
		salaryDocuments.getAsHTML(zoom, new AsyncCallback<String>() {
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

	private void onSalaryDocumentsChanged() {
		if (salaryDocuments.size() > 0) {
			getAsHTML();
			syncSalaryTypeListBox();
			printButton.setEnabled(true);
			deleteButton.setEnabled(true);
		} else {
			container.setHTML("");
			dateListBox.clear();
			salaryTypeListBox.clear();
			printButton.setEnabled(false);
			deleteButton.setEnabled(false);
		} // end : No salaries

	}

	private void onSalaryTypeChanged() {
		selectNewestSalary(getSelectedType());
		syncSalaryDateListBox();
		onSalaryDateChanged();
	}

	private void onSalaryDateChanged() {
		com.esferalia.aon.gwt.payroll.shared.Salary currentSalary = salaries
				.get(dateListBox.getSelectedIndex());
		salaryDocuments.setCurrent(currentSalary);
		getAsHTML();
	}

	private void syncSalaryTypeListBox() {
		types.clear();
		salaryTypeListBox.clear();

		com.esferalia.aon.gwt.payroll.shared.Salary currentSalary = getCurrentSalary();

		com.esferalia.aon.gwt.payroll.shared.Salary.Type currentType = currentSalary
				.getType();

		for (com.esferalia.aon.gwt.payroll.shared.Salary salary : getSalaries()) {
			com.esferalia.aon.gwt.payroll.shared.Salary.Type type = salary
					.getType();

			if (types.contains(type))
				continue;

			types.add(type);
			salaryTypeListBox.addItem(type.getDescription());
		}

		salaryTypeListBox.setSelectedIndex(types.indexOf(currentType));

		syncSalaryDateListBox();
	}

	private void syncSalaryDateListBox() {
		dateListBox.clear();

		com.esferalia.aon.gwt.payroll.shared.Salary currentSalary = getCurrentSalary();

		com.esferalia.aon.gwt.payroll.shared.Salary.Type type = getSelectedType();

		salaries = getSalaries(type);

		int index = 0;
		for (com.esferalia.aon.gwt.payroll.shared.Salary salary : salaries) {
			dateListBox.addItem(DATE_FORMAT.format(salary.getEndDate()));
			if (salary.getEndDate().before(currentSalary.getEndDate())) {
				index++;
			}
		}

		dateListBox.setSelectedIndex(index);
	}
	
	private com.esferalia.aon.gwt.payroll.shared.Salary.Type getSelectedType(){
		return types
				.get(salaryTypeListBox.getSelectedIndex());
	}
	
	private void selectNewestSalary(
			com.esferalia.aon.gwt.payroll.shared.Salary.Type type) {
		List<com.esferalia.aon.gwt.payroll.shared.Salary> salaries = getSalaries();
		for (int i = salaries.size()-1; i >= 0; i--)
			if (salaries.get(i).getType() == type)
				salaryDocuments.setCurrentIndex(i);

	}

	private com.esferalia.aon.gwt.payroll.shared.Salary getCurrentSalary() {
		return salaryDocuments.getSalaries().get(
				salaryDocuments.getCurrentIndex());
	}

	private List<com.esferalia.aon.gwt.payroll.shared.Salary> getSalaries() {
		return salaryDocuments.getSalaries();
	}

	private List<com.esferalia.aon.gwt.payroll.shared.Salary> getSalaries(
			com.esferalia.aon.gwt.payroll.shared.Salary.Type type) {
		List<com.esferalia.aon.gwt.payroll.shared.Salary> salaries = new ArrayList<com.esferalia.aon.gwt.payroll.shared.Salary>();

		for (com.esferalia.aon.gwt.payroll.shared.Salary salary : getSalaries()) {
			if (salary.getType() == type) {
				salaries.add(salary);
			}
		}

		return salaries;
	}

}
