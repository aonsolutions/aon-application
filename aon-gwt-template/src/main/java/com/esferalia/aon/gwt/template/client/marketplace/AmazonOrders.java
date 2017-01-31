package com.esferalia.aon.gwt.template.client.marketplace;


import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.template.client.CustomDataGrid;
import com.esferalia.aon.gwt.template.client.ITemplate;
import com.esferalia.aon.gwt.template.client.ITemplateAsync;
import com.esferalia.aon.gwt.template.client.JsTemplates;
import com.esferalia.aon.gwt.template.client.ProgressBarDialog;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.esferalia.aon.occam.api.model.Domain;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractHasData.DefaultKeyboardSelectionHandler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class AmazonOrders  extends ResizeComposite{
	
	final IMarketplaceAsync impl = GWT.create(IMarketplace.class);
	final ITemplateAsync item = GWT.create(ITemplate.class);
	
	interface AmazonOrdersBinder extends UiBinder<Widget, AmazonOrders> {
	
	}
	
	private static final AmazonOrdersBinder binder = GWT.create(AmazonOrdersBinder.class);

	DataGridResources resources = GWT.create(DataGridResources.class);

	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) DataGrid<Order> dataGrid; 
	@UiField Button deliveryButton;
	
	List<Order> orderList;
	String login;
	ProgressBarDialog pbd;
	ShowMorePager showMorePager;

	
	public AmazonOrders(List<Order> orderList, String login) {
		setOrderList(orderList);
		setLogin(login);
		
		dataGrid = new CustomDataGrid<Order>();//new DataGrid<Order>(Integer.MAX_VALUE, resources); 
		deliveryButton = new Button();

		
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
		load();
	}
	
	private void load() {
		loadDataGrid();
	}
	
	//------------------------------ Actions
	
	@UiHandler("deliveryButton")
	void newButton(ClickEvent event){
		String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_amazon_delivery/"
	           	+ "?domain_id=" + getDomain().getId()
	           	+ "&username="+ getLogin();
		Window.open( fileDownloadURL, "_blank",null);
		dataProvider = new ListDataProvider<Order>(new LinkedList<Order>());
		dataProvider.addDataDisplay(dataGrid);
		dataGrid.redraw();
	}
	
	//------------------------------ DataGrid Utils
	
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

		CustomDataGrid<?> dataGridAux;
		
		/**
		 * Construct a new {@link ShowMorePager}.
		 */
		
		public ShowMorePager(CustomDataGrid<?> dataGrid) {
			setDisplay(dataGrid);

			this.scrollPanel = (ScrollPanel) dataGrid.getScrollPanel();
			dataGridAux = dataGrid;
			// Handle scroll events.
			scrollPanel.addScrollHandler(new ScrollHandler() {
				CustomDataGrid<?> dataGrid = dataGridAux;
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
						Integer rowCount = dataGrid.getRowCount();
						if(rowCount > range.getLength()){
							if(rowCount <= newPageSize)
								getDisplay().setVisibleRange(0, rowCount);
							else getDisplay().setVisibleRange(0, newPageSize);
						}
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
	
	private void loadDataGrid(){
		DefaultKeyboardSelectionHandler<Order> selHandler = getSelHandler();	
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("No hay ning\u00fan pedido."));
		addDataDisplay(dataGrid);
		ListHandler<Order> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
 		final SingleSelectionModel<Order> selectionModel = new SingleSelectionModel<Order>();
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<Order> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
		
		showMorePager = new ShowMorePager((CustomDataGrid<Order>) dataGrid);

	}
	
	private DefaultKeyboardSelectionHandler<Order> getSelHandler(){
		return new DefaultKeyboardSelectionHandler<Order>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<Order> event) {
							
			}
		};
	}
	
	private ListDataProvider<Order> dataProvider = new ListDataProvider<Order>();

	public void addDataDisplay(HasData<Order> display) {
		dataProvider = new ListDataProvider<Order>(getOrderList());
		dataProvider.addDataDisplay(display);
	}

	private ListHandler<Order> getSortHandler() {
		return new ListHandler<Order>(dataProvider.getList()) {
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<Order> aux = super.getList();
				List<Order> aux2 = new Vector<Order>();
				for (Integer i = 0; i < aux.size() - 1; i++) {
					aux2.set(i, aux.get(aux.size() - 1 - i));
				}
				dataProvider.setList(aux2);
			}
		};
	}

	private void initContextMenu() {
		ContextMenuHandler contextMenuHandler = new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}
		};
		dataGrid.addDomHandler(contextMenuHandler, ContextMenuEvent.getType());
	}

	private void initTableColumns(
			final SelectionModel<Order> selectionModel,
			ListHandler<Order> sortHandler) {

		initContextMenu();
		
		/** Fecha Column **/
		Column<Order, String> dateColumn = new Column<Order, String>(
				new TextCell()) {

			@Override
			public String getValue(Order object) {

				return object.getDateStr();
			}

		};
		dateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		dateColumn.setSortable(true);
		sortHandler.setComparator(dateColumn,
				new Comparator<Order>() {

					@Override
					public int compare(Order o1, Order o2) {
						return o1.getDateStr()
								.compareTo(o2.getDateStr());
					}
				});
		dataGrid.getColumnSortList().push(dateColumn);
		dataGrid.addColumn(dateColumn, "Fecha");
		dataGrid.setColumnWidth(dateColumn, 15, Unit.PCT);

		/** Order Column **/
		Column<Order, String> orderColumn = new Column<Order, String>(
				new TextCell()) {

			@Override
			public String getValue(Order object) {
				return object.getOrder();
			}
		};

		orderColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		orderColumn.setSortable(true);
		sortHandler.setComparator(orderColumn,
				new Comparator<Order>() {

					@Override
					public int compare(Order o1, Order o2) {
						return o1.getOrder()
								.compareTo(o2.getOrder());
					}
				});
		dataGrid.getColumnSortList().push(orderColumn);
		dataGrid.addColumn(orderColumn, "N\u00BA Pedido");
		dataGrid.setColumnWidth(orderColumn, 15, Unit.PCT);

		/** Customer Column **/
		Column<Order, String> customerColumn = new Column<Order, String>(
				new TextCell()) {

			@Override
			public String getValue(Order object) {
				return object.getCustomerName();
			}

		};
		customerColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		customerColumn.setSortable(true);
		sortHandler.setComparator(customerColumn,
				new Comparator<Order>() {

					@Override
					public int compare(Order o1, Order o2) {
						return o1.getCustomerName()
								.compareTo(o2.getCustomerName());
					}
				});
		dataGrid.getColumnSortList().push(customerColumn);
		dataGrid.addColumn(customerColumn, "Cliente");
		dataGrid.setColumnWidth(customerColumn, 30, Unit.PCT);

		/** Seller Column **/
		Column<Order, String> sellerColumn = new Column<Order, String>(
				new TextCell()) {

			@Override
			public String getValue(Order object) {
				return object.getSellerName();
			}

		};
		sellerColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		sellerColumn.setSortable(true);
		sortHandler.setComparator(customerColumn,
				new Comparator<Order>() {

					@Override
					public int compare(Order o1, Order o2) {
						return o1.getSellerName()
								.compareTo(o2.getSellerName());
					}
				});
		dataGrid.getColumnSortList().push(sellerColumn);
		dataGrid.addColumn(sellerColumn, "Comercial");
		dataGrid.setColumnWidth(sellerColumn, 30, Unit.PCT);

		/** Purchase Reference Column **/
		Column<Order, String> purcaseRefColumn = new Column<Order, String>(
				new TextCell()) {

			@Override
			public String getValue(Order object) {
				return object.getOrderId();
			}
		};

		purcaseRefColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		purcaseRefColumn.setSortable(true);
		sortHandler.setComparator(purcaseRefColumn,
				new Comparator<Order>() {

					@Override
					public int compare(Order o1, Order o2) {
						return o1.getOrderId()
								.compareTo(o2.getOrderId());
					}
				});
		dataGrid.getColumnSortList().push(purcaseRefColumn);
		dataGrid.addColumn(purcaseRefColumn, "Ref. Compra");
		dataGrid.setColumnWidth(purcaseRefColumn, 15, Unit.PCT);
	
	}

	
	//------------------------------ Getters & Setters
	
	public List<Order> getOrderList(){
		return orderList;
	}
	
	public void setOrderList(List<Order> orderList){
		this.orderList = orderList;
	}
	
	public String getLogin(){
		return login;
	}
	
	public void setLogin(String login){
		this.login = login;
	}
	
	private Domain getDomain() {
		return new Domain().setId(JsTemplates.getCurrentDomain()).setName(JsTemplates.getCurrentDomainName());
	}
}
