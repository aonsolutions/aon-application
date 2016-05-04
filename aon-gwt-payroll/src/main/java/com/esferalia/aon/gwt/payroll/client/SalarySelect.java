package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.DateListBox;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.Salary.TypeVisitor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

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

	abstract class AbstractDateProvider extends AbstractDataProvider<Date> {
		@Override
		protected void onRangeChanged(HasData<Date> display) {
			if (salaryPreview == null)
				return;
			Range range = display.getVisibleRange();
			int start = range.getStart();
			int length = range.getLength();
			updateRowData(display, start, getDates(start, length));
		}

		public boolean hasDataDisplay(HasData<Date> display) {
			return getDataDisplays().contains(display);
		}

		protected abstract List<Date> getDates(int start, int length);
	}

	class ExtrasDateProvider extends AbstractDateProvider {

		@Override
		protected List<Date> getDates(int start, int length) {
			return getExtraDates(start, length);
		}

	}

	class SettleDateProvider extends AbstractDateProvider {
		@Override
		protected List<Date> getDates(int start, int length) {
			return getSettleDates(start, length);
		}
	}

	@UiField
	ListBox typeListBox;
	@UiField
	DateListBox dateListBox;
	@UiField
	MonthListBox monthListBox;
	@UiField
	MonthListBox fromMonthListBox;

	private List<Extra> extras;
	private List<Listener> listeners;
	private ExtrasDateProvider extrasDatesProvider;
	private SettleDateProvider settleDatesProvider;
	private com.esferalia.aon.gwt.payroll.shared.SalaryPreview salaryPreview;

	public SalarySelect() {
		initWidget(binder.createAndBindUi(this));
		initTypeListBox();
		initMonthListBox();
		listeners = new LinkedList<SalarySelect.Listener>();
		extrasDatesProvider = new ExtrasDateProvider();
		settleDatesProvider = new SettleDateProvider();
	}

	public void setExtras(List<Extra> extras) {
		this.extras = new ArrayList<Extra>(extras);
		sort(extras);
		initExtraTypeItem();
	}

	public void setSalaryPreview(
			com.esferalia.aon.gwt.payroll.shared.SalaryPreview salaryPreview) {
		this.salaryPreview = salaryPreview;
		syncTypeListBox();
		syncDateListBox(getSelectedType());
		//reset start, end & issue dates
		//syncSalarySelectDates();
		
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	

	// -------------------------------------------------------------------------
	@UiHandler("dateListBox")
	void onDateListBoxChanged(ChangeEvent event) {
		syncSalarySelectDates();
		fireOnChange();
	}

	@UiHandler("monthListBox")
	void onMonthListBoxChanged(ChangeEvent event) {
		syncSalarySelectDates();
		fireOnChange();
	}

	@UiHandler("fromMonthListBox")
	void onFromMonthListBoxChanged(ChangeEvent event) {
		syncSalarySelectDates();
		fireOnChange();
	}

	@UiHandler("typeListBox")
	void onTypeListBoxChanged(ChangeEvent event) {
		Type type = getSelectedType();
		syncDateListBox(type);
		salaryPreview.setType(type);
		syncSalarySelectDates();
		fireOnChange();
	}

	// -------------------------------------------------------------------------

	private void fireOnChange() {
		for (Listener listener : listeners) {
			listener.onChange(this);
		}
	}

	private void initTypeListBox() {

		typeListBox.addItem(Salary.Type.SALARY.getDescription(),
				Salary.Type.SALARY.name());

		typeListBox.addItem(Salary.Type.DELAY.getDescription(),
				Salary.Type.DELAY.name());

		typeListBox.addItem(Salary.Type.SETTLE.getDescription(),
				Salary.Type.SETTLE.name());

		typeListBox.addItem(Salary.Type.NOT_ENJOYED_VACATIONS.getDescription(),
				Salary.Type.NOT_ENJOYED_VACATIONS.name());

	}

	private void initExtraTypeItem() {
		int extraIndex = findItemIndex(Type.EXTRA);
		if (extras == null || extras.isEmpty()) {
			if (extraIndex >= 0)
				typeListBox.removeItem(extraIndex);
		} else {
			if (extraIndex < 0)
				typeListBox.insertItem(Salary.Type.EXTRA.getDescription(),
						Salary.Type.EXTRA.name(), 1);
		}
	}

	private int findItemIndex(Type type) {
		int items = typeListBox.getItemCount();
		for (int i = 0; i < items; i++) {
			if (type.name().equals(typeListBox.getValue(i)))
				return i;
		}
		return -1;
	}

	private void initMonthListBox() {

	}

	private Type getSelectedType() {
		int selected = typeListBox.getSelectedIndex();
		String value = typeListBox.getValue(selected);
		return Type.valueOf(value);
	}

	private void syncSalarySelectDates() {
		Type type = getSelectedType();
		type.accept(new TypeVisitor<Void>() {

			@Override
			public Void visitSalary(Type type) {
				Date month = monthListBox.getSelectedMonth();
				Date startDate = DateUtils.getFirstDayOfMonth(month);
				Date endDate = DateUtils.getLastDayOfMonth(month);
				SalarySelect.this.salaryPreview.setStartDate(startDate);
				SalarySelect.this.salaryPreview.setEndDate(endDate);
				SalarySelect.this.salaryPreview.setIssueDate(endDate);
				return null;
			}

			@Override
			public Void visitExtra(Type type) {

				Date issueDate = dateListBox.getSelected();
				if (issueDate == null) {
					Date contractStartDate = SalarySelect.this.salaryPreview
							.getEmployee().getStartDate();
					Date salaryDate = SalarySelect.this.salaryPreview
							.getIssueDate();
					for (Extra extra : extras) {
						Date endDate = getEndDate(extra, salaryDate);
						if (endDate.compareTo(contractStartDate) < 0)
							continue;

						issueDate = parseExtraDate(extra.getIssueDate(),
								DateUtils.copyDateOnly(salaryDate));
						break;
					}
				}
				Extra extra = SalarySelect.this.getExtraByIssueDate(issueDate);
				
				if ( extra == null ) {
					issueDate = SalarySelect.this.salaryPreview.getIssueDate();
					extra = SalarySelect.this.getExtraByIssueDate(issueDate);
				} // TODO: syncDateListBox & scheduleFinally
				
				Date refDate = SalarySelect.getRefrenceDate(extra, issueDate);
				
				Date startDate = SalarySelect.getStartDate(extra, refDate);
				Date endDate = SalarySelect.getEndDate(extra, refDate);

				SalarySelect.this.salaryPreview.setStartDate(startDate);
				SalarySelect.this.salaryPreview.setEndDate(endDate);
				SalarySelect.this.salaryPreview.setIssueDate(issueDate);

				return null;
			}

			@Override
			public Void visitSettle(Type type) {

				Employee employee = SalarySelect.this.salaryPreview
						.getEmployee();
				Date contractStartDate = employee.getStartDate();
				Date contractEndDate = employee.getEndDate();

				Date settleDate = dateListBox.getSelected();
				if (settleDate == null)
					settleDate = contractEndDate;
				if (settleDate == null)
					settleDate = DateUtils.copyDateOnly(new Date());

				SalarySelect.this.salaryPreview.setStartDate(contractStartDate);
				SalarySelect.this.salaryPreview.setEndDate(settleDate);
				SalarySelect.this.salaryPreview.setIssueDate(settleDate);

				return null;
			}

			@Override
			public Void visitDelay(Type type) {
				Date month = monthListBox.getSelectedMonth();
				Date fromMonth = fromMonthListBox.getSelectedMonth();
				Date endDate = DateUtils.getLastDayOfMonth(month);
				Date startDate = DateUtils.getFirstDayOfMonth(fromMonth);

				SalarySelect.this.salaryPreview.setStartDate(startDate);
				SalarySelect.this.salaryPreview.setEndDate(endDate);
				SalarySelect.this.salaryPreview.setIssueDate(endDate);
				return null;
			}

			@Override
			public Void visitNotEnjoyedVacations(Type type) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

	private void syncDateListBox(Type type) {

		Employee employee = salaryPreview.getEmployee();

		final Date contractStartDate = CalendarUtil.copyDate(employee
				.getStartDate());
		final Date draftEndDate = CalendarUtil.copyDate(salaryPreview
				.getEndDate());
		final Date contractEndDate = CalendarUtil.copyDate(employee
				.getEndDate());

		type.accept(new TypeVisitor<Void>() {

			@Override
			public Void visitSalary(Type type) {

				dateListBox.setVisible(false);
				fromMonthListBox.setVisible(false);
				monthListBox.setVisible(true);

				monthListBox.setFirstMonth(contractStartDate);
				monthListBox.setLastMonth(contractEndDate);
				monthListBox.setSelectedMonth(draftEndDate);

				return null;
			}

			@Override
			public Void visitExtra(Type type) {
				try {
					monthListBox.setVisible(false);
					fromMonthListBox.setVisible(false);
					dateListBox.setVisible(true);

					if (settleDatesProvider.hasDataDisplay(dateListBox))
						settleDatesProvider.removeDataDisplay(dateListBox);
					if (!extrasDatesProvider.hasDataDisplay(dateListBox))
						extrasDatesProvider.addDataDisplay(dateListBox);

					final Date issueDate = SalarySelect.this.salaryPreview
							.getIssueDate();
					int index = getIndexOfExtra(issueDate);

					int length = dateListBox.getPageSize();
					int start = Math.max(0, index - length / 2);

					dateListBox.setVisibleRangeAndClearData(new Range(start,
							length), true);

					Scheduler.get().scheduleFinally(new ScheduledCommand() {
						@Override
						public void execute() {
							dateListBox.setSelected(issueDate, true);
						}
					});
				} catch (Throwable t) {
					Window.alert(t.getMessage());
				}

				return null;
			}

			@Override
			public Void visitSettle(Type type) {
				monthListBox.setVisible(false);
				fromMonthListBox.setVisible(false);
				dateListBox.setVisible(true);
				if (extrasDatesProvider.hasDataDisplay(dateListBox))
					extrasDatesProvider.removeDataDisplay(dateListBox);
				if (!settleDatesProvider.hasDataDisplay(dateListBox))
					settleDatesProvider.addDataDisplay(dateListBox);

				final Date issueDate = SalarySelect.this.salaryPreview
						//.getEndDate();
						.getIssueDate();
				
				int index = getIndexOfSettle(issueDate);

				int length = dateListBox.getPageSize();
				int start = Math.max(0, index - length / 2);

				dateListBox.setVisibleRangeAndClearData(
						new Range(start, length), true);

				Scheduler.get().scheduleFinally(new ScheduledCommand() {
					@Override
					public void execute() {
						dateListBox.setSelected(issueDate, true);
					}
				});

				return null;
			}

			@Override
			public Void visitDelay(Type type) {

				dateListBox.setVisible(false);
				fromMonthListBox.setVisible(true);
				monthListBox.setVisible(true);

				Date draftStartDate = null;
				if (salaryPreview.getType() == type)
					draftStartDate = CalendarUtil.copyDate(salaryPreview
							.getStartDate());
				else
					draftStartDate = max(contractStartDate,
							DateUtils.getFirstDayOfYear(draftEndDate));

				monthListBox.setFirstMonth(draftStartDate);
				monthListBox.setLastMonth(new Date());
				monthListBox.setSelectedMonth(draftEndDate);

				fromMonthListBox.setFirstMonth(contractStartDate);
				fromMonthListBox.setLastMonth(draftEndDate);
				fromMonthListBox.setSelectedMonth(draftStartDate);

				return null;

			}

			@Override
			public Void visitNotEnjoyedVacations(Type type) {
				return visitSettle(type);
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

	private int getOffsetFirstExtra() {
		Employee employee = salaryPreview.getEmployee();
		Date contractStartDate = employee.getStartDate();

		int firstExtra = -1;
		while (++firstExtra < extras.size()) {
			Date extraEndDate = getEndDate(extras.get(firstExtra),
					contractStartDate);
			if (extraEndDate.compareTo(contractStartDate) >= 0) {
				break;
			}
		}
		return firstExtra;
	}

	private List<Date> getSettleDates(int start, int length) {

		Employee employee = salaryPreview.getEmployee();
		Date contractEndDate = employee.getEndDate();

		List<Date> dates = new ArrayList<Date>(length);

		
		Date date = DateUtils.copyDateOnly(employee.getStartDate());
		
		for (DateUtils.addDays2Date(date, start); dates.size() < length
				&& DateUtils.compare(date, contractEndDate) <= 0; DateUtils
				.addDays2Date(date, 1)) {
			dates.add(DateUtils.copyDateOnly(date));
		}

		return dates;

	}

	private List<Date> getExtraDates(int start, int length) {

		Employee employee = salaryPreview.getEmployee();
		Date contractStartDate = employee.getStartDate();
		Date contractEndDate = employee.getEndDate();

		int firstExtra = -1;
		while (++firstExtra < extras.size()) {
			Date extraEndDate = getEndDate(extras.get(firstExtra),
					contractStartDate);
			if (extraEndDate.compareTo(contractStartDate) >= 0) {
				break;
			}
		}

		int firstExtras = extras.size() - firstExtra;

		int years;
		int offset;
		if (start <= firstExtras) {
			years = 0;
			offset = firstExtra + start;
		} else {
			offset = (start - firstExtra) % extras.size();
			years = (start - firstExtra) / extras.size() + 1;
		}

		List<Date> extraIssueDates = new ArrayList<Date>(length);

		Extra extra = extras.get(offset);
		Date extraStartDate = DateUtils.copyDateOnly(contractStartDate);
		DateUtils.addYears2Date(extraStartDate, years);

		while ((contractEndDate == null || !getStartDate(extra, extraStartDate)
				.after(contractEndDate)) && extraIssueDates.size() < length) {
			int yearsOffset = (offset / extras.size());
			Date extraIssueDate = DateUtils.copyDateOnly(contractStartDate);
			DateUtils.addYears2Date(extraIssueDate, years + yearsOffset);
			Date extraIssuedate = parseExtraDate(extra.getIssueDate(),
					extraIssueDate);
			extraIssueDates.add(extraIssuedate);

			extra = extras.get(++offset % extras.size());

		}

		return extraIssueDates;

	}

	private int getIndexOfExtra(Date date) {

		int offset = getOffsetFirstExtra();

		date = DateUtils.copyDateOnly(date);

		Employee employee = salaryPreview.getEmployee();
		Date contractStartDate = employee.getStartDate();

		// Brute force.
		int index = offset - 1;
		Date issueDate = null;
		do {
			index++;
			Extra extra = extras.get(index % extras.size());
			issueDate = DateUtils.copyDateOnly(contractStartDate);
			DateUtils.addYears2Date(issueDate, (int) (index / extras.size()));
			parseExtraDate(extra.getIssueDate(), issueDate);
		} while (issueDate.compareTo(date) != 0
				&& DateUtils.getYears(issueDate, date) <= 1);

		return index - offset;

	}

	private int getIndexOfSettle(Date date) {
		return DateUtils.getDaysBetween(new Date(), date);

	}

	private Extra getExtraByIssueDate(Date date) {
		for (Extra extra : extras) {
			Date extraDate = parseExtraDate(extra.getIssueDate(), CalendarUtil.copyDate(date));
			if (extraDate.getDate() == date.getDate() && extraDate.getMonth() == date.getMonth() )
				return extra;
		}
		return null;

	}

	// -------------------------------------------------------------------------

	private static Date getEndDate(Extra extra, Date date) {
		return parseExtraDate(extra.getEndDate(), DateUtils.copyDateOnly(date));
	}

	private static Date getStartDate(Extra extra, Date date) {
		return parseExtraDate(extra.getStartDate(),
				DateUtils.copyDateOnly(date));
	}

	private static Date getRefrenceDate(Extra extra, Date date) {
		return AgreementDraft.getReferenceDate(extra.getIssueDate(),
				DateUtils.copyDateOnly(date));
	}

	private static void sort(List<Extra> extras) {
		class EndDateComparator implements Comparator<Extra> {
			@Override
			public int compare(Extra e1, Extra e2) {
				Date date = new Date();
				Date d1 = getEndDate(e1, date);
				Date d2 = getEndDate(e2, date);
				return d1.compareTo(d2);
			}
		}
		EndDateComparator comparator = new EndDateComparator();
		Collections.sort(extras, comparator);
	}

	private static Date parseExtraDate(String text, Date date) {
		return AgreementDraft.parseExtraDate(text, date);
	}

	private static Date max(Date a, Date b) {
		return a.compareTo(b) >= 0 ? a : b;
	}

	private static Date min(Date a, Date b) {
		return a.compareTo(b) <= 0 ? a : b;
	}
}
