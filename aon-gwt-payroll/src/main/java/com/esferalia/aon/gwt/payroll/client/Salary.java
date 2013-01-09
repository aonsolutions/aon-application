package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Salary extends ResizeComposite {

	private static final int ZOOM_STEP = 20;
	private static final int MIN_ZOOM =  25;
	private static final int MAX_ZOOM =  500;

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

		salaryTypeListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onSalaryTypeChanged();
			}
		});
		dateListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onSalaryDateChanged();
			}
		});
	
		printButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				IDocument document = salaryDocuments.current();
				document.print();
			}
		});

		printMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				IDocument document = salaryDocuments.current();
				document.print();
			}
		});
		
		downloadMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				IDocument document = salaryDocuments.current();
				document.download();
			}
		});

		
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

	public void setSalaryDocuments(SalaryDocuments salaryDocuments) {
		this.salaryDocuments = salaryDocuments;
		onSalaryDocumentsChanged();
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
		getAsHTML();
		syncSalaryTypeListBox();
	}

	private void onSalaryTypeChanged() {
		syncSalaryDateListBox();
		onSalaryDateChanged();
	}

	private void onSalaryDateChanged() {
		com.esferalia.aon.gwt.payroll.shared.Salary currentSalary = 
				salaries.get(dateListBox.getSelectedIndex());
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

		com.esferalia.aon.gwt.payroll.shared.Salary.Type type = types
				.get(salaryTypeListBox.getSelectedIndex());
		
		salaries = getSalaries(type);
		
		SalaryComparator salaryComparator = new SalaryComparator();
		
		Collections.sort(salaries, salaryComparator);

		for (com.esferalia.aon.gwt.payroll.shared.Salary salary : salaries) {
			dateListBox.addItem(DATE_FORMAT.format(salary.getIssueDate()));
		}
		
		
		int index = Collections.binarySearch(salaries, currentSalary, salaryComparator);
		if ( index < 0 ) {
			// index = (-(insertion_point) - 1)
			// index + 1  = -insertion_point
			// insertion_point = -index -1)
			index = -index -1;

			int count = dateListBox.getItemCount();
			if ( index >= count ){ 
				index = count -1;
			}
		}
		
		
		dateListBox.setSelectedIndex(index);
	}
	
	
	private com.esferalia.aon.gwt.payroll.shared.Salary getCurrentSalary() {
		return salaryDocuments.getSalaries().get(salaryDocuments.getCurrentIndex());
	}
	
	private List<com.esferalia.aon.gwt.payroll.shared.Salary> getSalaries(){
		return salaryDocuments.getSalaries();
	}
	
	
	private List<com.esferalia.aon.gwt.payroll.shared.Salary> getSalaries(com.esferalia.aon.gwt.payroll.shared.Salary.Type type){
		List<com.esferalia.aon.gwt.payroll.shared.Salary> salaries = new ArrayList<com.esferalia.aon.gwt.payroll.shared.Salary>();
		
		for (com.esferalia.aon.gwt.payroll.shared.Salary salary : getSalaries()) {
			if (salary.getType() == type) {
				salaries.add(salary);
			}
		}
		
		return salaries;
	}
	
	private static class SalaryComparator implements Comparator<com.esferalia.aon.gwt.payroll.shared.Salary> {
		@Override
		public int compare(com.esferalia.aon.gwt.payroll.shared.Salary s1,
				com.esferalia.aon.gwt.payroll.shared.Salary s2) {
			return s1.getIssueDate().compareTo(s2.getIssueDate());
		}
	}
	

}
