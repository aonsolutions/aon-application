package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.client.AgreementDraft.parseExtraEndDate;
import static com.esferalia.aon.gwt.payroll.client.AgreementDraft.parseExtraIssueDate;
import static com.esferalia.aon.gwt.payroll.client.AgreementDraft.parseExtraStartDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.widget.DateListBox;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.HasId;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.Salary.TypeVisitor;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;

public class CalcDialog<T extends HasId<?>> extends SelectDialog<T> {

	
	private static class TypeVisitorImpl<T> implements TypeVisitor<T>{

		@Override
		public T visitSalary(Type type) {
			throw new RuntimeException("Not implemented");
		}

		@Override
		public T visitExtra(Type type) {
			throw new RuntimeException("Not implemented");
		}

		@Override
		public T visitSettle(Type type) {
			throw new RuntimeException("Not implemented");
		}

		@Override
		public T visitDelay(Type type) {
			throw new RuntimeException("Not implemented");
		}
		
		@Override
		public T visitProcedural(Type type) {
			throw new RuntimeException("Not implemented");
		}

	}
	
	interface Binder extends UiBinder<Widget, CalcDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	

	@UiField
	TableRowElement monthTR;

	@UiField
	TableRowElement extraTR;

	@UiField
	ListBox typeListBox;

	@UiField
	MonthListBox monthListBox;

	@UiField
	DateListBox payDateListBox;

	@UiField
	ActualExtraListBox extraListBox;

	@UiField
	CheckBox saveCheckBox;

	@UiField
	RadioButton keepRadioButton;

	@UiField
	RadioButton overwriteRadioButton;
	// @UiField
	// RadioButton duplicateRadioButton;

	public CalcDialog() {

		// Create a DataGrid
		super();
		setCaption("Calcular...");
		setWidget(binder.createAndBindUi(this));

		initTypeListBox();
		initMonthListBox();
		initExtraListBox();
		initPayDateListBox();

		initSelectionChangeHandler();
	}

	// --------------------------------------------------------------- Handlers

	@Override
	public void show() {
		monthListBox.onResizeDropDownPopup();
		super.show();
	}

	@UiHandler("saveCheckBox")
	void onSaveClicked(ClickEvent event) {

		keepRadioButton.setEnabled(saveCheckBox.getValue());
		overwriteRadioButton.setEnabled(saveCheckBox.getValue());
		// duplicateRadioButton.setEnabled(saveCheckBox.getValue());

	}

	@UiHandler("monthListBox")
	void onMonthChanged(ChangeEvent event) {
		// refresh range
		selectDataGrid.setVisibleRangeAndClearData(new Range(0, PAGE_SIZE), true);
		changePayDateListBoxRange(payDateListBox, getIssueDate());
	}

	@UiHandler("typeListBox")
	void onTypeChanged(ChangeEvent event) {
		onTypeChanged(Salary.Type.valueOf(typeListBox.getSelectedValue()));
	}

	@UiHandler("extraListBox")
	void onExtraChanged(ChangeEvent event) {
	}

	// ----------------------------------------------------------------- Public
	
	public Salary.Type getType(){
		return Salary.Type.valueOf(typeListBox.getSelectedValue());
	}

//	public Date getMonth() {
//		return monthListBox.getSelectedMonth();
//	}

	public void setMonth(Date month) {
		monthListBox.setSelectedMonth(month);
	}

	public void setStartMonth(Date month) {
		monthListBox.setFirstMonth(month);
	}

	public void setEndMonth(Date month) {
		monthListBox.setLastMonth(month);
	}

	public void setCalculatedMonths(Set<Date> months) {
		monthListBox.setHighLightMonths(months);
	}

	public boolean isSaveSelected() {
		return saveCheckBox.getValue();
	}

	public boolean isOverwriteSelected() {
		return overwriteRadioButton.getValue();
	}

	public boolean isDuplicateSelected() {
		return false; // duplicateRadioButton.getValue();
	}
	
	public Integer getExtra() {
		return getType().accept(new TypeVisitorImpl<Integer>() {
			@Override
			public Integer visitExtra(Type type) {
				return extraListBox.getSelected().getId();
			}

			@Override
			public Integer visitSalary(Type type) {
				return -1;
			}
		});
	}
	
	public Date getEndDate(){
		return getType().accept(new TypeVisitorImpl<Date>() {
			@Override
			public Date visitExtra(Type type) {
				return extraListBox.getSelected().getEndDate();
			}

			@Override
			public Date visitSalary(Type type) {
				return DateUtils.getLastDayOfMonth(monthListBox.getSelectedMonth());
			}
		});
	}

	public Date getStartDate(){
		return getType().accept(new TypeVisitorImpl<Date>() {
			@Override
			public Date visitExtra(Type type) {
				return extraListBox.getSelected().getStartDate();
			}

			@Override
			public Date visitSalary(Type type) {
				return DateUtils.getFirstDayOfMonth(monthListBox.getSelectedMonth());
			}
		});
	}

	public Date getIssueDate(){
		return getType().accept(new TypeVisitorImpl<Date>() {
			@Override
			public Date visitExtra(Type type) {
				return extraListBox.getSelected().getIssueDate();
			}
			@Override
			public Date visitSalary(Type type) {
				return DateUtils.getLastDayOfMonth(monthListBox.getSelectedMonth());
			}
		});
	}

	public Date getChargeDate(){
		return payDateListBox.getSelectedDate();
	}

	// -------------------------------------------------------------- Protected

	protected void getExtras(Set<T> ts, AsyncCallback<List<Extra>> callback) {
		callback.onFailure(new NoSuchElementException());
	}

	// ---------------------------------------------------------------- Private

	private void initTypeListBox() {

		typeListBox.addItem(Salary.Type.SALARY.getDescription(), Salary.Type.SALARY.name());
		typeListBox.addItem(Salary.Type.EXTRA.getDescription(), Salary.Type.EXTRA.name());

		typeListBox.setSelectedIndex(0);
		onTypeChanged(Salary.Type.SALARY);
	}

	private void initMonthListBox() {
		monthListBox.setSelectedMonth(new Date());
	}

	private void initExtraListBox() {
		disableExtra();
	}
	
	private void initPayDateListBox() {
	    initPayDateListBox(payDateListBox, this::getIssueDate);
	}

	private void onTypeChanged(Salary.Type type) {
		type.accept(new TypeVisitor<Void>() {

			@Override
			public Void visitSalary(Type type) {
				extraTR.getStyle().setDisplay(Display.NONE);
				monthTR.getStyle().setDisplay(Display.TABLE_ROW);
				return null;
			}

			@Override
			public Void visitExtra(Type type) {
				monthTR.getStyle().setDisplay(Display.NONE);
				extraTR.getStyle().setDisplay(Display.TABLE_ROW);
				return null;
			}

			@Override
			public Void visitSettle(Type type) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Void visitDelay(Type type) {
				extraTR.getStyle().setDisplay(Display.NONE);
				monthTR.getStyle().setDisplay(Display.TABLE_ROW);
				return null;
			}
			
			@Override
			public Void visitProcedural(Type type) {
				return visitSalary(type);
			}
			
		});

	}

	private void initSelectionChangeHandler() {
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				Set<T> selected = selectionModel.getSelectedSet();
				if ( selected.isEmpty() )
					disableExtra();

				CalcDialog.this.getExtras(selected, new AsyncCallback<List<Extra>>() {
					
					@Override
					public void onSuccess(List<Extra> extras) {
						if ( extras.isEmpty() )
							disableExtra();
						else
							enableExtra(extras);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						disableExtra();
					}
				});

			}
		});
	}
	
	private void disableExtra() {
		cleanExtraListBox();
		extraListBox.setEnabled(false);
	}
	
	private void enableExtra(List<Extra> extras ) {
		cleanExtraListBox();
		setExtraListBox(extras);
		extraListBox.setEnabled(true);
	}
	
	
	private void cleanExtraListBox() {
		extraListBox.setItemText(0, "Los empleados seleccionados no tienen extras");
	}
	

	private void setExtraListBox(final List<Extra> extras) {
		
		class ExtrasDateProvider extends AbstractDataProvider<ActualExtra> {
			
			private Date startDate;
			private List<Extra> extras;
			
			public ExtrasDateProvider(List<Extra> extras) {
				this(extras, DateUtils.getFirstDayOfYear(DateUtils.addYears2Date(new Date(), -1)));
			}
			
			public ExtrasDateProvider(List<Extra> extras, Date startDate) {
				this.extras = extras; 
				this.startDate = startDate;
				
				Collections.sort(this.extras, new Comparator<Extra>(){
					@Override
					public int compare(Extra e1, Extra e2) {
						Date d1 = parseExtraIssueDate(e1.getIssueDate(), ExtrasDateProvider.this.startDate);
						Date d2 = parseExtraIssueDate(e2.getIssueDate(), ExtrasDateProvider.this.startDate);
						return d1.compareTo(d2);
					}
				});
				
			}

			@Override
			protected void onRangeChanged(HasData<ActualExtra> display) {
				Range visibleRange = display.getVisibleRange();
				int start = visibleRange.getStart();
				int length = visibleRange.getLength();
				updateRowData(display, start, getExtraDates(start, length));
			}
			
			
			private List<ActualExtra> getExtraDates(int start, int length) {
				
				int years = start / extras.size();
				
				Date date = DateUtils.copyDateOnly(startDate);
				DateUtils.addYears2Date(date, years);
				
				List<ActualExtra> actualExtras = new ArrayList<ActualExtra>(length);
				for ( int i = 0 ; i < length ; i++ ) {
					int index = (start+i) % extras.size();
					Extra extra = extras.get(index);
					Date extraDate = DateUtils.copyDateOnly(date);
					DateUtils.addYears2Date(extraDate, (int)(i / extras.size()));
					actualExtras.add(new ActualExtra()
							.setId(extra.getId())
							.setPaymentDescription(extra.getPaymentDescription())
							.setAgreementDescription(extra.getAgreementDescription())
							.setEndDate(parseExtraEndDate(extra.getEndDate(), extraDate ))
							.setStartDate(parseExtraStartDate(extra.getStartDate(), extraDate ))
							.setIssueDate(parseExtraIssueDate(extra.getIssueDate(), extraDate ))
							);

				}
				
				return actualExtras ;
			}


		}
		
		new ExtrasDateProvider(extras).addDataDisplay(extraListBox);
		extraListBox.setVisibleRangeAndClearData(new Range(0,
				25), true);

		Scheduler.get().scheduleFinally(new ScheduledCommand() {
			@Override
			public void execute() {
				extraListBox.setSelected(extras.size(), true);
			}
		});
	}
	
	protected static void initPayDateListBox(DateListBox payDateListBox, Supplier<Date> issueDateSupplier) {
	    AbstractDataProvider<Date> payDateProvider =
	    new AbstractDataProvider<Date>() {
		@Override
		protected void onRangeChanged(HasData<Date> display) {
			Range range = display.getVisibleRange();
			int start = range.getStart();
			int length = range.getLength();
			updateRowData(display, start, getPayDates(start, length));
		}
		
		private List<Date> getPayDates(int start, int length) {

			Date issueDate = issueDateSupplier.get();

			List<Date> dates = new ArrayList<>(length);

			Date date = DateUtils.copyDateOnly(issueDate);
			date = DateUtils.addDays2Date(date,  -20);
			
			for (DateUtils.addDays2Date(date, start); dates.size() < length ; DateUtils
					.addDays2Date(date, 1)) {
				dates.add(DateUtils.copyDateOnly(date));
			}

			return dates;

		}
		
	    };
	    
	    payDateProvider.addDataDisplay(payDateListBox);
	    
	    changePayDateListBoxRange(payDateListBox, issueDateSupplier.get());
	}

	protected static void changePayDateListBoxRange(DateListBox payDateListBox, Date issueDate) {
	    
	    Date payStartDate = DateUtils.copyDateOnly(issueDate);
	    payStartDate = DateUtils.addDays2Date(payStartDate, -20);

	    Date payDate = DateUtils.after(new Date(), issueDate);

	    int index = DateUtils.getDaysBetween(payStartDate, payDate);
	    int length = payDateListBox.getPageSize();
	    int start = Math.max(0, index - length / 2);

	    payDateListBox.setVisibleRangeAndClearData(new Range(start, length), true);
	    Scheduler.get().scheduleFinally(() -> payDateListBox.setSelected(payDate, true));
	}
	

	
	
}
