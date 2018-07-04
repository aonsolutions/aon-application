package net.aonsolutions.aon.gwt.warehouse.client.dataSheet;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.product.JsProduct;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.AbstractHasData.DefaultKeyboardSelectionHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class DataSheetGridPanel extends ResizeComposite implements RequiresResize {

	interface GridBinder extends UiBinder<Widget, DataSheetGridPanel> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);
	
	DataGridResources resources = GWT.create(DataGridResources.class);
	
	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) CustomDataGrid<JsProduct> dataGrid; 
	
	DataSheet parent;
	Integer cont = 0;

	public DataSheetGridPanel(DataSheet parent, LinkedList<JsProduct> list) {
		this.parent = parent;		
		dataGrid = new CustomDataGrid<JsProduct>(Integer.MAX_VALUE, resources,
				JsProduct.PROVIDES_KEY);
		ScrollPanel scrollPanel = dataGrid.getScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				if(scrollPanel.getVerticalScrollPosition() >= scrollPanel.getMaximumVerticalScrollPosition()){
					Integer page = 2;
					if(parent.getFilterMap().containsKey("page")){
						page = Integer.parseInt(parent.getFilterMap().get("page").get(0)) + 1;
					}
					LinkedList<String> list = new LinkedList<>();
					list.add(page +"");
					parent.getFilterMap().put("page", list);
					
					parent.getAPI().getProduct().getProductList(parent.getFilterMap(), new AsyncCallback<JSON<JsProduct>>() {
						
						@Override
						public void onSuccess(JSON<JsProduct> result) {
							dataProvider.getList().addAll(result.getData().toLinkedList());
							dataGrid.redraw();			
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
				}
			}
		});
		dataGrid.addHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				if(cont < 2){
					dataGrid.redraw();
					cont++;
				}
			}
		}, MouseOverEvent.getType());
		load(list);	
		initWidget(binder.createAndBindUi(this));
	}	
	
	private void load(LinkedList<JsProduct> list) {
		DefaultKeyboardSelectionHandler<JsProduct> selHandler = new DefaultKeyboardSelectionHandler<JsProduct>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsProduct> event) {
				if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
				    JsProduct object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());				    
				    LinkedList<String> list = new LinkedList<>();
				    list.add((dataGrid.getKeyboardSelectedRow()+1) + "");
				    HashMap<String, LinkedList<String>> map = parent.getFilterMap();
				    map.put("page", list);
				    parent.setFilterMap(map);
				    parent.sheetContent(object, map);
				}		
			}
		};
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
		addDataDisplay(dataGrid, list);
		ListHandler<JsProduct> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
		final SingleSelectionModel<JsProduct> selectionModel = new SingleSelectionModel<JsProduct>(
 				JsProduct.PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsProduct> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<JsProduct> dataProvider = new ListDataProvider<JsProduct>();

	public void addDataDisplay(HasData<JsProduct> display, LinkedList<JsProduct> list) {
		dataProvider = new ListDataProvider<JsProduct>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsProduct> getSortHandler() {
		return new ListHandler<JsProduct>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsProduct> aux  = super.getList();
				List<JsProduct> aux2 = new LinkedList<JsProduct>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initTableColumns(final SelectionModel<JsProduct> selectionModel, ListHandler<JsProduct> sortHandler) {
		/** CODE Column **/
		Column<JsProduct,String> issueDateColumn = new Column<JsProduct, String>(new TextCell()) {
			
			@Override
			public String getValue(JsProduct object) {
				if(object.getCode() != null && !"".equals(object.getCode())){
					return object.getCode();
				}
				return "-";
			}
		};
		
		issueDateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		issueDateColumn.setSortable(false); 
		sortHandler.setComparator(issueDateColumn,new Comparator<JsProduct>() {
			
			@Override
			public int compare(JsProduct o1, JsProduct o2) {
				return o1.getCode().compareTo(o2.getCode());
			}
		});
		dataGrid.getColumnSortList().push(issueDateColumn);
		dataGrid.addColumn(issueDateColumn, "C\u00f3digo Producto");
		dataGrid.setColumnWidth(issueDateColumn,10, Unit.PCT);

		
	
		/** Product Column **/
		Column<JsProduct, String> productColumn = new Column<JsProduct, String>(new TextCell()) {

			@Override
			public String getValue(JsProduct object) {
				return object.getName();
			}
		
		};
		productColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		productColumn.setSortable(false); 
		sortHandler.setComparator(productColumn,new Comparator<JsProduct>() {
			
			@Override
			public int compare(JsProduct o1, JsProduct o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		dataGrid.getColumnSortList().push(productColumn);
		dataGrid.addColumn(productColumn, "Nombre Producto");
		dataGrid.setColumnWidth(productColumn, 15, Unit.PCT);
		
	}
	
}
