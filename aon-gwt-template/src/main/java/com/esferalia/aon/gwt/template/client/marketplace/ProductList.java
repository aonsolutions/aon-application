package com.esferalia.aon.gwt.template.client.marketplace;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.product.OldItem;
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
	
	@UiField(provided = true) DataGrid<OldItem> dataGrid;
	@UiField(provided = true) TextBox nameSearchBox;
	
	@UiField(provided = true) Button searchButton;
	
	private AonData aonData;
	
	private List<OldItem> list;
	private ListDataProvider<OldItem> dataProvider = new ListDataProvider<OldItem>();
	
	public ProductList(AonData aonData, List<OldItem> list) {
		this.aonData = aonData;
		setList(list);
		dataGrid = new DataGrid<OldItem>(Integer.MAX_VALUE, resources); 
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
	public List<OldItem> getList(){
		return list;
	}
	
	public void setList(List<OldItem> list){
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
		Vector<OldItem> vaux = new Vector<OldItem>();
		vaux.addAll(getList());
 		
		impl.searchItemByProductName(searchStr, vaux, new AsyncCallback<Vector<OldItem>>() {
			@Override
			public void onSuccess(Vector<OldItem> result) {
				dataProvider = new ListDataProvider<OldItem>(result);
				dataProvider.addDataDisplay(dataGrid);
				dataGrid.redraw();
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
		
	}
	
	private void loadDataGrid(){
		final SingleSelectionModel<OldItem> selectionModel = new SingleSelectionModel<OldItem>();
		dataGrid.setSelectionModel(selectionModel);
		dataGrid.addDomHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(final DoubleClickEvent event) {
				OldItem selected = selectionModel.getSelectedObject();
				if (selected != null) {
				    new ProductValuesDialog(getAonData(), selected).show();
				}
			}
		}, DoubleClickEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("No hay ning\u00fan archivo."));
		dataProvider = new ListDataProvider<OldItem>(getList());
		dataProvider.addDataDisplay(dataGrid);
		ListHandler<OldItem> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
		
		initTableColumns(selectionModel, sortHandler);
	}
	
	
	private ListHandler<OldItem> getSortHandler() {
		return new ListHandler<OldItem>(dataProvider.getList()) {
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<OldItem> aux = super.getList();
				List<OldItem> aux2 = new Vector<OldItem>();
				for (Integer i = 0; i < aux.size() - 1; i++) {
					aux2.set(i, aux.get(aux.size() - 1 - i));
				}
				dataProvider.setList(aux2);
			}
		};
	}

	private void initTableColumns(
			final SelectionModel<OldItem> selectionModel,
			ListHandler<OldItem> sortHandler) {
		
		/**
		 * Code Column
		 */
		Column<OldItem, String> codeColumn = new Column<OldItem, String>(
				new TextCell()) {
			@Override
			public void render(Context context, OldItem object, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span>" + object.getProduct().getCode() + "</span>");
			}
			@Override
			public String getValue(OldItem object) {
				return object.getProduct().getCode();
			}
		};
		codeColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		dataGrid.addColumn(codeColumn, "C\u00f3digo");
		dataGrid.setColumnWidth(codeColumn, 15, Unit.PCT);
		
		/**
		 * Detail Column
		 */
		Column<OldItem, String> detailColumn = new Column<OldItem, String>(
				new TextCell()) {
			@Override
			public void render(Context context, OldItem object, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span>" 
						+ (object.getDetail()!=null?object.getDetail():"")
						+ (object.getDetail2()!=null?" / " + object.getDetail2():"")
						+ (object.getDetail3()!=null?" / " + object.getDetail3():"")
						+ "</span>");
			}
			@Override
			public String getValue(OldItem object) {
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
		Column<OldItem, String> nameColumn = new Column<OldItem, String>(
				new TextCell()) {
			@Override
			public String getValue(OldItem object) {
				return object.getProduct().getName();
			}
		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		nameColumn.setSortable(true);
		sortHandler.setComparator(nameColumn,
				new Comparator<OldItem>() {
					@Override
					public int compare(OldItem o1, OldItem o2) {
						return o1.getProduct().getName().compareTo(o2.getProduct().getName());
					}
				});
		dataGrid.getColumnSortList().push(nameColumn);
		dataGrid.addColumn(nameColumn, "Nombre");
		dataGrid.setColumnWidth(nameColumn, 85, Unit.PCT);
	
	}
	
	
}
