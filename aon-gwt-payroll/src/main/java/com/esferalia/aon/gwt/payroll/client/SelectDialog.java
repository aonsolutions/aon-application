package com.esferalia.aon.gwt.payroll.client;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.shared.HasId;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;

public class SelectDialog<T extends HasId<?>> extends CustomDialog {

	public static interface AcceptHandler extends EventHandler {
		void onAccept(AcceptEvent event);
	}

	public static class AcceptEvent extends GwtEvent<AcceptHandler> {

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

	/**
	 * A scrolling pager that automatically increases the range every time the
	 * scroll bar reaches the bottom.
	 */
	static class ShowMorePager extends AbstractPager {

		/**
		 * The default increment size.
		 */
		private static final int DEFAULT_INCREMENT = 20;

		/**
		 * The increment size.
		 */
		private int incrementSize = DEFAULT_INCREMENT;

		/**
		 * The last scroll position.
		 */
		private int lastScrollPos = 0;

		/**
		 * The scrollable panel.
		 */
		private final ScrollPanel scrollPanel;

		/**
		 * Construct a new {@link ShowMorePager}.
		 */
		public ShowMorePager(CustomDataGrid<?> dataGrid) {
			setDisplay(dataGrid);

			this.scrollPanel = (ScrollPanel) dataGrid.getScrollPanel();

			// Handle scroll events.
			scrollPanel.addScrollHandler(new ScrollHandler() {

				@Override
				public void onScroll(ScrollEvent event) {
					// If scrolling up, ignore the event.
					int oldScrollPos = ShowMorePager.this.lastScrollPos;
					ShowMorePager.this.lastScrollPos = scrollPanel
							.getVerticalScrollPosition();
					if (oldScrollPos >= ShowMorePager.this.lastScrollPos) {
						return;
					}

					int maxScrollTop = scrollPanel
							.getMaximumVerticalScrollPosition();

					if (ShowMorePager.this.lastScrollPos >= maxScrollTop) {
						// We are near the end, so increase the page size.
						int incrementSize = getIncrementSize();

						Range range = getDisplay().getVisibleRange();
						// We are near the end, so increase the page size.
						int newPageSize = range.getLength() + incrementSize;
						getDisplay().setVisibleRange(0, newPageSize);
					}
				}
			});
		}

		/**
		 * Get the number of rows by which the range is increased when the
		 * scrollbar reaches the bottom.
		 * 
		 * @return the increment size
		 */
		int getIncrementSize() {
			return incrementSize;
		}

		@Override
		protected void onRangeOrRowCountChanged() {
		}

	}

	interface Binder extends UiBinder<Widget, SelectDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	protected static final int PAGE_SIZE = 25;
	
	@UiField 
	Label selectLabel;

	@UiField
	Button acceptButton;
	@UiField
	Button cancelButton;

	/**
	 * The main DataGrid.
	 */
	@UiField(provided = true)
	DataGrid<T> selectDataGrid;

	ScrollPanel selectScrollPanel;
	MultiSelectionModel<T> selectionModel;
	ShowMorePager showMorePager;
	SelectAllHeader<T> selectAllHeader;
	
	public SelectDialog() {

		// Create a DataGrid

		/*
		 * Set a key provider that provides a unique key for each item.
		 */
		ProvidesKey<T> keyProvider = HasIdKeyProvider.getKeyProvider();
		selectDataGrid = new CustomDataGrid<T>(PAGE_SIZE, keyProvider);

		/*
		 * Do not refresh the headers every time the dataGrid is updated. The
		 * footer depends on the current dataGrid, so we do not disable auto
		 * refresh on the footer.
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
				return SelectDialog.this.selectionModel.isSelected(object);
			}
		};

		selectAllHeader = new SelectAllHeader<T>(selectionModel, selectDataGrid);

		selectDataGrid.addColumn(checkColumn, selectAllHeader);
		selectDataGrid.setColumnWidth(checkColumn, "40px");
		selectDataGrid.addStyleName(AON.AON_WIDTH_ALL);
		selectDataGrid.getElement().getStyle()
				.setPropertyPx("minHeight", Window.getClientHeight() / 3);
		selectDataGrid.setWidth("100%");

		setWidget(binder.createAndBindUi(this));

		showMorePager = new ShowMorePager((CustomDataGrid<T>) selectDataGrid);

		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						acceptButton.setEnabled(SelectDialog.this.enableAccept());
					}
				});
		
		acceptButton.setEnabled(false);

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

	// ------------------------------------------
	// Public
	// ------------------------------------------

	@Override
	public void show() {
		selectDataGrid.onResize();
		super.show();
	}

	@Override
	public void center() {
		selectDataGrid.onResize();
		super.center();
	}
	

	public void setData(List<T> data) {
		selectDataGrid.setRowData(data);
		selectionModel.clear();
	}

	public Set<T> getSelectedData() {
		return selectionModel.getSelectedSet();
	}

	public void setSelectedData(Collection<T> data) {
		selectionModel.clear();
		for (T t : data)
			selectionModel.setSelected(t, true);
	}

	public boolean isAllSelected() {
		return selectAllHeader.getValue();
	}

	public void setDataProvider(AbstractDataProvider<T> provider) {
		selectDataGrid.setVisibleRangeAndClearData(new Range(0, selectDataGrid.getPageSize()) , false);
		provider.addDataDisplay(selectDataGrid);
		selectionModel.clear();
	}

	public void addColumn(Column<T, ?> col, String headerString) {
		selectDataGrid.addColumn(col, headerString);
	}

	public HandlerRegistration addAcceptHandler(AcceptHandler handler) {
		return addHandler(handler, AcceptEvent.getType());
	}
	
	// ------------------------------------------------------------------------
	
	protected boolean enableAccept () {
		return selectionModel.getSelectedSet()
		.size() > 0;
	}
	
	protected void hideSelectLabel() {
		selectLabel.setVisible(false);
	}
}
