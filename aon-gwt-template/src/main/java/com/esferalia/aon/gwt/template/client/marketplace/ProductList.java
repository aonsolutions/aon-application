package com.esferalia.aon.gwt.template.client.marketplace;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class ProductList extends ResizeComposite{
	
	final IMarketplaceAsync impl = GWT.create(IMarketplace.class);
	
	private static final ProductListBinder binder = GWT.create(ProductListBinder.class);
	
	interface ProductListBinder extends UiBinder<Widget, ProductList> {
		
	}

	DataGridResources resources = GWT.create(DataGridResources.class);

	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) DataGrid<Item> dataGrid;
	@UiField(provided = true) TextBox nameSearchBox;
	
	@UiField(provided = true) Button searchButton;
	
	private AonData aonData;
	
	private List<Item> list;
	private ListDataProvider<Item> dataProvider = new ListDataProvider<Item>();
	
	public ProductList(AonData aonData, List<Item> list) {
		this.aonData = aonData;
		setList(list);
		dataGrid = new DataGrid<Item>(Integer.MAX_VALUE, resources); 
		nameSearchBox = new TextBox();
		
		searchButton = new Button("Buscar");

		Widget ui = binder.createAndBindUi(this);
		
		initWidget(ui);
		initSearchBox();
		loadDataGrid();
	}
	
	public AonData getAonData() {
		return aonData;
	}

	public Domain getDomain() {
		return getAonData().getDomain();
	}

	public User getUser() {
		return getAonData().getUser();
	}
	public List<Item> getList(){
		return list;
	}
	
	public void setList(List<Item> list){
		this.list = list;
	}
	
	//------------------------------ Actions
	
	
	
	//------------------------------ DataGrid Utils
	
	private void initSearchBox(){
		nameSearchBox.setValue(null);
		
		searchButton.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				searchByName();
			}
		}, ClickEvent.getType());

	}
	
	private void searchByName() {
		String searchStr = nameSearchBox.getText();
		Vector<Item> vaux = new Vector<Item>();
		vaux.addAll(getList());
 		
		impl.searchItemByProductName(searchStr, vaux, new AsyncCallback<Vector<Item>>() {
			@Override
			public void onSuccess(Vector<Item> result) {
				dataProvider = new ListDataProvider<Item>(result);
				dataProvider.addDataDisplay(dataGrid);
				dataGrid.redraw();
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
		
	}
	
	private void loadDataGrid(){
		final SingleSelectionModel<Item> selectionModel = new SingleSelectionModel<Item>();
		dataGrid.setSelectionModel(selectionModel);
		dataGrid.addDomHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(final DoubleClickEvent event) {
				Item selected = selectionModel.getSelectedObject();
				if (selected != null) {
				    new ProductValuesDialog(getAonData(), selected).show();
				}
			}
		}, DoubleClickEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("No hay ning\u00fan archivo."));
		dataProvider = new ListDataProvider<Item>(getList());
		dataProvider.addDataDisplay(dataGrid);
		ListHandler<Item> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
		
		initTableColumns(selectionModel, sortHandler);
	}
	
	
	private ListHandler<Item> getSortHandler() {
		return new ListHandler<Item>(dataProvider.getList()) {
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<Item> aux = super.getList();
				List<Item> aux2 = new Vector<Item>();
				for (Integer i = 0; i < aux.size() - 1; i++) {
					aux2.set(i, aux.get(aux.size() - 1 - i));
				}
				dataProvider.setList(aux2);
			}
		};
	}

	private void initTableColumns(
			final SelectionModel<Item> selectionModel,
			ListHandler<Item> sortHandler) {
		
		/**
		 * Code Column
		 */
		Column<Item, String> codeColumn = new Column<Item, String>(
				new TextCell()) {
			@Override
			public void render(Context context, Item object, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span>" + object.getProduct().getCode() + "</span>");
			}
			@Override
			public String getValue(Item object) {
				return object.getProduct().getCode();
			}
		};
		codeColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		dataGrid.addColumn(codeColumn, "C\u00f3digo");
		dataGrid.setColumnWidth(codeColumn, 15, Unit.PCT);
		
		/**
		 * Detail Column
		 */
		Column<Item, String> detailColumn = new Column<Item, String>(
				new TextCell()) {
			@Override
			public void render(Context context, Item object, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span>" 
						+ (object.getDetail()!=null?object.getDetail():"")
						+ (object.getDetail2()!=null?" / " + object.getDetail2():"")
						+ (object.getDetail3()!=null?" / " + object.getDetail3():"")
						+ "</span>");
			}
			@Override
			public String getValue(Item object) {
				return (object.getDetail()!=null?object.getDetail():"")
						+ (object.getDetail2()!=null?" / " + object.getDetail2():"")
						+ (object.getDetail3()!=null?" / " + object.getDetail3():"");
			}
		};
		detailColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		dataGrid.addColumn(detailColumn, "Detalle");
		dataGrid.setColumnWidth(detailColumn, 20, Unit.PCT);
		
		/** 
		 * Name Column 
		 */
		Column<Item, String> nameColumn = new Column<Item, String>(
				new TextCell()) {
			@Override
			public String getValue(Item object) {
				return object.getProduct().getName();
			}
		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		nameColumn.setSortable(true);
		sortHandler.setComparator(nameColumn,
				new Comparator<Item>() {
					@Override
					public int compare(Item o1, Item o2) {
						return o1.getProduct().getName().compareTo(o2.getProduct().getName());
					}
				});
		dataGrid.getColumnSortList().push(nameColumn);
		dataGrid.addColumn(nameColumn, "Nombre");
		dataGrid.setColumnWidth(nameColumn, 85, Unit.PCT);
	
	}
	
	
}
