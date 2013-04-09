package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.payroll.shared.HasId;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

public class CalcDialog<T extends HasId<?>> extends CustomDialog {

	static interface AcceptHandler extends EventHandler {
		void onAccept(AcceptEvent event);
	}

	static class AcceptEvent extends GwtEvent<AcceptHandler> {

		private static final Type TYPE = new Type<AcceptHandler>();

		public static Type<AcceptHandler> getType() {
			return TYPE;
		}

		public static void fire(HasHandlers source) {
			source.fireEvent(new AcceptEvent(source));
		}

		public AcceptEvent(Object source) {
			setSource(source);
		}

		@Override
		public Type<AcceptHandler> getAssociatedType() {
			// TODO Auto-generated method stub
			return TYPE;
		}

		@Override
		protected void dispatch(AcceptHandler handler) {
			handler.onAccept(this);
		}

	}

	static class HasIdKeyProvider<T extends HasId<?>> implements ProvidesKey<T> {

		@Override
		public Object getKey(T item) {
			return item.getId();
		}

		public static <T extends HasId<?>> HasIdKeyProvider<T> getKeyProvider() {
			return new HasIdKeyProvider<T>();
		}
	}

	interface Binder extends UiBinder<Widget, CalcDialog> {

	}

	private static final int PAGE_SIZE = 25;

	private static final Binder binder = GWT.create(Binder.class);
	private static final DateTimeFormat MONTH_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH);

	
	@UiField
	CheckBox saveCheckBox;
	
	@UiField
	Button acceptButton;
	@UiField
	Button cancelButton;
	@UiField
	MonthListBox monthListBox;

	/**
	 * The main DataGrid.
	 */
	@UiField(provided = true)
	DataGrid<T> selectDataGrid;

	
	
	MultiSelectionModel<T> selectionModel;

	public CalcDialog() {

		// Create a DataGrid

		/*
		 * Set a key provider that provides a unique key for each item.
		 */
		ProvidesKey<T> keyProvider = HasIdKeyProvider.<T> getKeyProvider();
		selectDataGrid = new DataGrid<T>(PAGE_SIZE, keyProvider);

		/*
		 * Do not refresh the headers every time the data is updated. The footer
		 * depends on the current data, so we do not disable auto refresh on the
		 * footer.
		 */
		selectDataGrid.setAutoHeaderRefreshDisabled(true);


		// Set the message to display when the table is empty.
		// TODO : selectDataGrid.setEmptyTableWidget(new Label());

		// Add a selection model to handle user selection.
		selectionModel = new MultiSelectionModel<T>(keyProvider);
		selectDataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<T> createCheckboxManager(0));

		// Checkbox column. This table will uses a checkbox column for
		// selection.
		Column<T, Boolean> checkColumn = new Column<T, Boolean>(
				new CheckboxCell()) {
			@Override
			public Boolean getValue(T object) {
				return CalcDialog.this.selectionModel.isSelected(object);
			}
		};

		Header<Boolean> checkAllHeader = new Header<Boolean>(new CheckboxCell()) {
			@Override
			public Boolean getValue() {
				// TODO Auto-generated method stub
				return null;
			}
		};

		selectDataGrid.addColumn(checkColumn, checkAllHeader);
		selectDataGrid.setColumnWidth(checkColumn, "40px");
		selectDataGrid.addStyleName(AON.AON_WIDTH_ALL);
		selectDataGrid.getElement().getStyle().setPropertyPx("minHeight", Window.getClientHeight()/3);
		
		
		selectDataGrid.setWidth("100%");


		setCaption("Calcular...");
		setWidget(binder.createAndBindUi(this));

		monthListBox.setDateTimeFormat(MONTH_FORMAT);
		
	}

	// ------------------------------------------
	// Handlers
	// ------------------------------------------

	@UiHandler("acceptButton")
	void onAcceptClick(ClickEvent event) {
		hide();
		AcceptEvent.fire(this);
	}

	@UiHandler("cancelButton")
	void onCancelClick(ClickEvent event) {
		hide();
	}

	/*
	 * @UiHandler("selectScrollPanel") void onSelectScroll(ScrollEvent event) {
	 * int scrollPos = selectScrollPanel.getVerticalScrollPosition(); int
	 * maxScroll = selectScrollPanel.getMaximumVerticalScrollPosition(); if
	 * (scrollPos >= maxScroll) { // We are near the end, so increase the page
	 * size. selectDataGrid.setVisibleRange(0, selectDataGrid.getPageSize() +
	 * PAGE_SIZE); } }
	 */
	
	@UiHandler("monthListBox")
	void onMonthChange(ChangeEvent event) {
		// refresh range
		selectDataGrid.setVisibleRange(0, PAGE_SIZE);
	}

	// ------------------------------------------
	// Public
	// ------------------------------------------

	public Date getMonth() {
		return monthListBox.getSelectedMonth();
	}

	public void setData(List<T> data) {
		selectDataGrid.setRowData(data);
	}

	public Set<T> getSelectedData() {
		return selectionModel.getSelectedSet();
	}
	
	public boolean isSaveSelected() {
		return saveCheckBox.getValue();
	}
	
	public void setDataProvider(AbstractDataProvider<T> provider) {
		provider.addDataDisplay(selectDataGrid);
		// Resets visible range
		// selectDataGrid.setVisibleRange(0, selectDataGrid.getPageSize());
	}

	public void addColumn(Column<T, ?> col, String headerString) {
		selectDataGrid.addColumn(col, headerString);
	}

	public void setMonth(Date startMonth, Date endMonth, Date actualMonth) {
		monthListBox.clear();
		monthListBox.setFirstMonth(startMonth);
		monthListBox.setLastMonth(endMonth);
		monthListBox.setSelectedMonth(actualMonth);
	}

	public HandlerRegistration addAcceptHandler(AcceptHandler handler) {
		return addHandler(handler, AcceptEvent.getType());
	}
}
