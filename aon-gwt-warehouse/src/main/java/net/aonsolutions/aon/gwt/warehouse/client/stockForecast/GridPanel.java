package net.aonsolutions.aon.gwt.warehouse.client.stockForecast;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.warehouse.JsStockForecast;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.AbstractHasData.DefaultKeyboardSelectionHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class GridPanel extends Composite {

	interface GridBinder extends UiBinder<Widget, GridPanel> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);
	
	DataGridResources resources = GWT.create(DataGridResources.class);
	
	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true)
	CustomDataGrid<JsStockForecast> dataGrid; 
	
	StockForecast parent;
	API API;
	Integer cont = 0;
	
	public GridPanel(StockForecast parent, LinkedList<JsStockForecast> linkedList) {
		this.parent = parent;
		this.API = parent.getAPI(); 
		
		dataGrid = new CustomDataGrid<JsStockForecast>(Integer.MAX_VALUE, resources,
				JsStockForecast.PROVIDES_KEY);
		
		dataGrid.addHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				if(cont < 2){
					dataGrid.redraw();
					cont++;
				}
			}
		}, MouseOverEvent.getType());
	
		initWidget(binder.createAndBindUi(this));

		load(linkedList);				
	}	
	
	private void load(LinkedList<JsStockForecast> linkedList) {
		DefaultKeyboardSelectionHandler<JsStockForecast> selHandler = new DefaultKeyboardSelectionHandler<JsStockForecast>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsStockForecast> event) {
//				if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
//					Integer relRow = event.getIndex() - dataGrid.getPageStart();
//				    Integer subrow = event.getContext().getSubIndex();
//				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
//				    JsStockForecast object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
//				    
//				    parent.onSelectElaboration(object);
//				}		
			}
		};
		
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
		addDataDisplay(dataGrid, linkedList);
		ListHandler<JsStockForecast> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
 		final SingleSelectionModel<JsStockForecast> selectionModel = new SingleSelectionModel<JsStockForecast>(
 				JsStockForecast.PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsStockForecast> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<JsStockForecast> dataProvider = new ListDataProvider<JsStockForecast>();

	public void addDataDisplay(HasData<JsStockForecast> display, LinkedList<JsStockForecast> list) {
		dataProvider = new ListDataProvider<JsStockForecast>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsStockForecast> getSortHandler() {
		return new ListHandler<JsStockForecast>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsStockForecast> aux  = super.getList();
				List<JsStockForecast> aux2 = new LinkedList<JsStockForecast>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initTableColumns(final SelectionModel<JsStockForecast> selectionModel, ListHandler<JsStockForecast> sortHandler) {
		NumberFormat numberFormat = NumberFormat.getDecimalFormat().overrideFractionDigits(2);
		
		/** Product **/
		Column<JsStockForecast, String> nameColumn = new Column<JsStockForecast, String>(new TextCell()) {

			@Override
			public String getValue(JsStockForecast object) {
				return object.getProductName();
			}
		
		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		nameColumn.setSortable(true); 
		sortHandler.setComparator(nameColumn,new Comparator<JsStockForecast>() {
			
			@Override
			public int compare(JsStockForecast o1, JsStockForecast o2) {
				String a = o1.getProductName(); 
				String b = o2.getProductName(); 
				return a.compareTo(b);
			}
		});
		
		/** Consumo **/
		Column<JsStockForecast,Number> quantityColumn = new Column<JsStockForecast, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockForecast object) {
				return object.getQuantity()!=null ? object.getQuantity() : null;
			}
		};
		
		quantityColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		quantityColumn.setSortable(true); 
		sortHandler.setComparator(quantityColumn,new Comparator<JsStockForecast>() {
			
			@Override
			public int compare(JsStockForecast o1, JsStockForecast o2) {
				return o1.getQuantity().compareTo(o2.getQuantity());
			}
		});
		
		/** Consumo por dia**/
		Column<JsStockForecast,Number> dailyQuantityColumn = new Column<JsStockForecast, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockForecast object) {
				return object.getDailyQuantity()!=null ? object.getDailyQuantity() : null;
			}
		};
		
		dailyQuantityColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		dailyQuantityColumn.setSortable(true); 
		sortHandler.setComparator(dailyQuantityColumn,new Comparator<JsStockForecast>() {
			
			@Override
			public int compare(JsStockForecast o1, JsStockForecast o2) {
				return o1.getDailyQuantity().compareTo(o2.getDailyQuantity());
			}
		});
		
		/** Acopio **/
		Column<JsStockForecast,Number> accumulationColumn = new Column<JsStockForecast, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockForecast object) {
				return object.getAccumulation()!=null ? object.getAccumulation() : null;
			}
		};
		
		accumulationColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		accumulationColumn.setSortable(true); 
		sortHandler.setComparator(accumulationColumn,new Comparator<JsStockForecast>() {
			
			@Override
			public int compare(JsStockForecast o1, JsStockForecast o2) {
				return o1.getAccumulation().compareTo(o2.getAccumulation());
			}
		});
		
		/** Stock **/
		Column<JsStockForecast,Number> stockColumn = new Column<JsStockForecast, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockForecast object) {
				return object.getStock()!=null ? object.getStock() : null;
			}
		};
		
		stockColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		stockColumn.setSortable(true); 
		sortHandler.setComparator(stockColumn,new Comparator<JsStockForecast>() {
			
			@Override
			public int compare(JsStockForecast o1, JsStockForecast o2) {
				return o1.getStock().compareTo(o2.getStock());
			}
		});
		
		/** Pendiente de recibir **/
		Column<JsStockForecast,Number> pendingPurchasesColumn = new Column<JsStockForecast, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockForecast object) {
				return object.getPendingPurchases()!=null ? object.getPendingPurchases() : null;
			}
		};
		
		pendingPurchasesColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		pendingPurchasesColumn.setSortable(true); 
		sortHandler.setComparator(pendingPurchasesColumn,new Comparator<JsStockForecast>() {
			
			@Override
			public int compare(JsStockForecast o1, JsStockForecast o2) {
				return o1.getPendingPurchases().compareTo(o2.getPendingPurchases());
			}
		});
		
		/** Pendiente de servir **/
		Column<JsStockForecast,Number> pendingSalesColumn = new Column<JsStockForecast, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockForecast object) {
				return object.getPendingSales()!=null ? object.getPendingSales() : null;
			}
		};
		
		pendingSalesColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		pendingSalesColumn.setSortable(true); 
		sortHandler.setComparator(pendingSalesColumn,new Comparator<JsStockForecast>() {
			
			@Override
			public int compare(JsStockForecast o1, JsStockForecast o2) {
				return o1.getPendingSales().compareTo(o2.getPendingSales());
			}
		});
		
		/** Propuesta **/
		Column<JsStockForecast,Number> proposalColumn = new Column<JsStockForecast, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockForecast object) {
				return object.getProposal()!=null ? object.getProposal() : null;
			}
		};
		
		proposalColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		proposalColumn.setSortable(true); 
		sortHandler.setComparator(proposalColumn,new Comparator<JsStockForecast>() {
			
			@Override
			public int compare(JsStockForecast o1, JsStockForecast o2) {
				return o1.getProposal().compareTo(o2.getProposal());
			}
		});
	
		
		dataGrid.addColumn(nameColumn, AON.MSG.product());
		dataGrid.addColumn(quantityColumn, "Consumo");
		dataGrid.addColumn(dailyQuantityColumn, "Consumo/Dia");
		dataGrid.addColumn(accumulationColumn, "Acopio");
		dataGrid.addColumn(stockColumn, "Stock");
		dataGrid.addColumn(pendingPurchasesColumn, "Pte.Recibir");
		dataGrid.addColumn(pendingSalesColumn, "Pte.Servir");
		dataGrid.addColumn(proposalColumn, "Propuesta");
		
		dataGrid.setColumnWidth(nameColumn, 100, Unit.PCT);
		dataGrid.setColumnWidth(quantityColumn, 100, Unit.PX);
		dataGrid.setColumnWidth(dailyQuantityColumn, 100, Unit.PX);
		dataGrid.setColumnWidth(accumulationColumn, 100, Unit.PX);
		dataGrid.setColumnWidth(stockColumn, 100, Unit.PX);
		dataGrid.setColumnWidth(pendingPurchasesColumn, 100, Unit.PX);
		dataGrid.setColumnWidth(pendingSalesColumn, 100, Unit.PX);
		dataGrid.setColumnWidth(proposalColumn, 100, Unit.PX);
		
	}
}
