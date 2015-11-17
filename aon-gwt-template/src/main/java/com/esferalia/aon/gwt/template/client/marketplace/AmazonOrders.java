package com.esferalia.aon.gwt.template.client.marketplace;


import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.template.client.ITemplate;
import com.esferalia.aon.gwt.template.client.ITemplateAsync;
import com.esferalia.aon.gwt.template.client.ProgressBarDialog;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractHasData.DefaultKeyboardSelectionHandler;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
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
		@Source("com/esferalia/aon/gwt/template/client/datagrid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) DataGrid<Order> dataGrid; 
	@UiField Button deliveryButton;
	
	List<Order> orderList;
	Integer domainId;
	String login;
	ProgressBarDialog pbd;

	
	public AmazonOrders(List<Order> orderList, Integer domainId, String login) {
		setOrderList(orderList);
		setDomainId(domainId);
		setLogin(login);
		
		dataGrid = new DataGrid<Order>(Integer.MAX_VALUE, resources); 
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
	           	+ "?domain_id=" + domainId
	           	+ "&username="+ getLogin();
		Window.open( fileDownloadURL, "_blank",null);
		
	}
	
	//------------------------------ DataGrid Utils
	
	private void loadDataGrid(){
		DefaultKeyboardSelectionHandler<Order> selHandler = getSelHandler();	
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("No hay ning\u00fan archivo."));
		addDataDisplay(dataGrid);
		ListHandler<Order> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
 		final SingleSelectionModel<Order> selectionModel = new SingleSelectionModel<Order>();
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<Order> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
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
			public void render(Context context, Order object,
					SafeHtmlBuilder sb) {

				sb.appendHtmlConstant("<span>" + object.getDateStr()
						+ "</span>");

			}

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
		dataGrid.addColumn(orderColumn, "Nº Pedido");
		dataGrid.setColumnWidth(orderColumn, 15, Unit.PCT);

		/** Customer Column **/
		Column<Order, String> customerColumn = new Column<Order, String>(
				new TextCell()) {

			@Override
			public void render(Context context, Order object,
					SafeHtmlBuilder sb) {

				sb.appendHtmlConstant("<span>" + object.getCustomerName()
						+ "</span>");

			}

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
	
	}

	
	//------------------------------ Getters & Setters
	
	public List<Order> getOrderList(){
		return orderList;
	}
	
	public void setOrderList(List<Order> orderList){
		this.orderList = orderList;
	}
	
	public Integer getDomainId(){
		return domainId;
	}
	
	public void setDomainId(Integer domainId){
		this.domainId = domainId;
	}
	
	public String getLogin(){
		return login;
	}
	
	public void setLogin(String login){
		this.login = login;
	}
}
