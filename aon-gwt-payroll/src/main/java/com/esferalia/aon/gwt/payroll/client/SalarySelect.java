package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.Salary.TypeVisitor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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

	static interface PeriodParser {
		Period parse(String str);
	}

	static class MonthParser implements PeriodParser {
		
		public Period parse(String str) {
			Date date = DATE_FORMAT.parseStrict(str);
			return new Period(DateUtils.getFirstDayOfMonth(date),
					DateUtils.getLastDayOfMonth(date));
		}
	}

	static class EndMonthParser implements PeriodParser {
		
		private Date start;
		
		public EndMonthParser(Date start) {
			this.start = start;
		}
		
		public Period parse(String str) {
			Date date = DATE_FORMAT.parseStrict(str);
			return new Period(start,
					DateUtils.getLastDayOfMonth(date));
		}
	}

	static interface Binder extends UiBinder<Widget, SalarySelect> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	ListBox typeListBox;
	@UiField
	ListBox dateListBox;

	private List<Listener> listeners;
	
	private PeriodParser periodParser;

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
		syncTypeListBox();
		syncDateListBox(getSelectedType());

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

		typeListBox.addItem(Salary.Type.SALARY.getDescription(),
				Salary.Type.SALARY.name());
		/*
		 * typeListBox.addItem(Salary.Type.EXTRA.getDescription(),
		 * Salary.Type.EXTRA.name());
		 */
		typeListBox.addItem(Salary.Type.DELAY.getDescription(),
				Salary.Type.DELAY.name());
		/*
		 * typeListBox.addItem(Salary.Type.SETTLE.getDescription(),
		 * Salary.Type.SETTLE.name());
		 * typeListBox.addItem(Salary.Type.NOT_ENJOYED_VACATIONS
		 * .getDescription(), Salary.Type.NOT_ENJOYED_VACATIONS.name());
		 */

		typeListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				Type type = getSelectedType();
				salaryPreview.setType(type);
				syncDateListBox(type);
				Period period = getSelectedPeriod();
				salaryPreview.setStartDate(period.getStart());
				salaryPreview.setEndDate(period.getEnd());
				fireOnChange();
			}
		});
	}

	private void initDateListBox() {

		dateListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				Period period = getSelectedPeriod();
				salaryPreview.setStartDate(period.getStart());
				salaryPreview.setEndDate(period.getEnd());
				fireOnChange();
			}
		});
	}

	private Type getSelectedType() {
		int selected = typeListBox.getSelectedIndex();
		String value = typeListBox.getValue(selected);
		return Type.valueOf(value);
	}

	private Period getSelectedPeriod() {
		int selected = dateListBox.getSelectedIndex();
		String text = dateListBox.getItemText(selected);
		return periodParser.parse(text);
	}

	private void syncDateListBox(Type type) {
		
		dateListBox.clear();

		Employee employee = salaryPreview.getEmployee();

		final Date contractStartDate = CalendarUtil.copyDate(employee.getStartDate());
		final Date draftEndDate = CalendarUtil.copyDate(salaryPreview.getEndDate());
		final Date contractEndDate = CalendarUtil.copyDate(employee.getEndDate());

		type.accept(new TypeVisitor<Void>() {

			@Override
			public Void visitSalary(Type type) {
				CalendarUtil.setToFirstDayOfMonth(contractStartDate);
				CalendarUtil.setToFirstDayOfMonth(draftEndDate);
				Date firstDate = getFirstDropDate(draftEndDate, contractStartDate);
				Date lastDate = getLastDropDate(draftEndDate, contractEndDate);
				
				Date date = CalendarUtil.copyDate(firstDate);
				while (draftEndDate.after(date)) {
					dateListBox.addItem(DATE_FORMAT.format(date));
					CalendarUtil.addMonthsToDate(date, 1);
				}
				int draftIndex = dateListBox.getItemCount();
				while (lastDate.after(date)) {
					dateListBox.addItem(DATE_FORMAT.format(date));
					CalendarUtil.addMonthsToDate(date, 1);
				}
				dateListBox.setSelectedIndex(draftIndex);
				periodParser = new MonthParser();
				return null;
			}

			@Override
			public Void visitExtra(Type type) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Void visitSettle(Type type) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Void visitDelay(Type type) {
				CalendarUtil.setToFirstDayOfMonth(contractStartDate);
				CalendarUtil.setToFirstDayOfMonth(draftEndDate);
				Date firstDate = getFirstDropDate(draftEndDate, contractStartDate);
				Date lastDate = getLastDropDate(draftEndDate, contractEndDate);
				Date date = CalendarUtil.copyDate(firstDate);
				while (draftEndDate.after(date)) {
					dateListBox.addItem(DATE_FORMAT.format(date));
					CalendarUtil.addMonthsToDate(date, 1);
				}
				int draftIndex = dateListBox.getItemCount();
				while (lastDate.after(date)) {
					dateListBox.addItem(DATE_FORMAT.format(date));
					CalendarUtil.addMonthsToDate(date, 1);
				}
				dateListBox.setSelectedIndex(draftIndex);
				periodParser = new EndMonthParser(contractStartDate);
				return null;
			}

			@Override
			public Void visitNotEnjoyedVacations(Type type) {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
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
