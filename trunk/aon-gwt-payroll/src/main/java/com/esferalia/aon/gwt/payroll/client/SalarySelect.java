package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.payroll.sql.AbstractSQL.ISalary;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class SalarySelect extends Composite {

	private static final int DATE_DROP_RANGE = 12;

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH);

	static interface Listener {
		void onChange(SalarySelect salarySelect);
	}

	static interface Binder extends UiBinder<Widget, SalarySelect> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	ListBox typeListBox;
	@UiField
	ListBox dateListBox;

	private List<Listener> listeners;

	private com.esferalia.aon.gwt.payroll.shared.SalaryPreview salaryPreview;

	public SalarySelect() {
		initWidget(binder.createAndBindUi(this));
		initTypeListBox();
		initDateListBox();
		listeners = new LinkedList<SalarySelect.Listener>();
	}

	public void setSalaryPreview(
			com.esferalia.aon.gwt.payroll.shared.SalaryPreview salaryPreview) {
		this.salaryPreview = salaryPreview;
		syncDateListBox();
		syncTypeListBox();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	

	private void fireOnChange() {
		for (Listener listener : listeners) {
			listener.onChange(this);
		}
	}

	private void initTypeListBox() {

		// Salary.Type types [] = Salary.Type.values();
		// for ( int i = 0; i< types.length; i++) {
		// typeListBox.addItem(types[i].getDescription(), types[i].name() );
		// }
		// TODO : Not only standard salary.
		typeListBox.addItem(Salary.Type.SALARY.getDescription(),
				Salary.Type.SALARY.name());

		typeListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
			}
		});
	}

	private void initDateListBox() {

		dateListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				Date date = getSelectedDate();
				salaryPreview.setStartDate(DateUtils.getFirstDayOfMonth(date));
				salaryPreview.setEndDate(DateUtils.getLastDayOfMonth(date));
				fireOnChange();
			}
		});
	}
	
	private Date getSelectedDate() {
		int selected = dateListBox.getSelectedIndex();
		String text = dateListBox.getItemText(selected);
		return DATE_FORMAT.parseStrict(text);
	}

	private void syncDateListBox() {

		dateListBox.clear();

		Employee employee = salaryPreview.getEmployee();

		Date startDate = CalendarUtil.copyDate(employee.getStartDate());
		Date draftDate = CalendarUtil.copyDate(salaryPreview.getEndDate());
		Date endDate = CalendarUtil.copyDate(employee.getEndDate());

		CalendarUtil.setToFirstDayOfMonth(startDate);
		CalendarUtil.setToFirstDayOfMonth(draftDate);

		Date firstDate = getFirstDropDate(draftDate, startDate);
		Date lastDate = getLastDropDate(draftDate, endDate);

		Date date = CalendarUtil.copyDate(firstDate);

		while (draftDate.after(date)) {
			dateListBox.addItem(DATE_FORMAT.format(date));
			CalendarUtil.addMonthsToDate(date, 1);
		}

		int draftIndex = dateListBox.getItemCount();

		while (lastDate.after(date)) {
			dateListBox.addItem(DATE_FORMAT.format(date));
			CalendarUtil.addMonthsToDate(date, 1);
		}

		dateListBox.setSelectedIndex(draftIndex);
	}

	private void syncTypeListBox() {
		Salary.Type type = salaryPreview.getType();
		for (int i = 0; i < typeListBox.getItemCount(); i++) {
			if (typeListBox.getValue(i).equals(type.name())) {
				typeListBox.setSelectedIndex(i);
				break;
			}
		}
	}

	private static Date getLastDropDate(Date date, Date end) {
		Date lastDate = CalendarUtil.copyDate(date);
		CalendarUtil.addMonthsToDate(lastDate, DATE_DROP_RANGE / 2);
		return DateUtils.before(lastDate, end);

	}

	private static Date getFirstDropDate(Date date, Date start) {
		Date firstDate = CalendarUtil.copyDate(date);
		CalendarUtil.addMonthsToDate(firstDate, -DATE_DROP_RANGE / 2);
		return DateUtils.after(firstDate, start);

	}

}
