package net.aonsolutions.aon.gwt.warehouse.client.stockForecast;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.warehouse.JsStockStat;
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
	CustomDataGrid<JsStockStat> dataGrid; 
	
	StockForecast parent;
	API API;
	Integer cont = 0;
	
	public GridPanel(StockForecast parent, LinkedList<JsStockStat> linkedList) {
		this.parent = parent;
		this.API = parent.getAPI(); 
		
		dataGrid = new CustomDataGrid<JsStockStat>(Integer.MAX_VALUE, resources,
				JsStockStat.PROVIDES_KEY);
		
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
	
	private void load(LinkedList<JsStockStat> linkedList) {
		DefaultKeyboardSelectionHandler<JsStockStat> selHandler = new DefaultKeyboardSelectionHandler<JsStockStat>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsStockStat> event) {
				
			}
		};
		
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
		addDataDisplay(dataGrid, linkedList);
		ListHandler<JsStockStat> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
 		final SingleSelectionModel<JsStockStat> selectionModel = new SingleSelectionModel<JsStockStat>(
 				JsStockStat.PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsStockStat> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<JsStockStat> dataProvider = new ListDataProvider<JsStockStat>();

	public void addDataDisplay(HasData<JsStockStat> display, LinkedList<JsStockStat> list) {
		dataProvider = new ListDataProvider<JsStockStat>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsStockStat> getSortHandler() {
		return new ListHandler<JsStockStat>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsStockStat> aux  = super.getList();
				List<JsStockStat> aux2 = new LinkedList<JsStockStat>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initTableColumns(final SelectionModel<JsStockStat> selectionModel, ListHandler<JsStockStat> sortHandler) {
		NumberFormat numberFormat = NumberFormat.getDecimalFormat().overrideFractionDigits(2);
		
		/** Product **/
		Column<JsStockStat, String> nameColumn = new Column<JsStockStat, String>(new TextCell()) {

			@Override
			public String getValue(JsStockStat object) {
				return object.getProductName();
			}
		
		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		nameColumn.setSortable(true); 
		sortHandler.setComparator(nameColumn,new Comparator<JsStockStat>() {
			
			@Override
			public int compare(JsStockStat o1, JsStockStat o2) {
				String a = o1.getProductName(); 
				String b = o2.getProductName(); 
				return a.compareTo(b);
			}
		});
		
		/** Consumo **/
		Column<JsStockStat,Number> outputsColumn = new Column<JsStockStat, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockStat object) {
				return object.getOutputs()!=null ? object.getOutputs() : null;
			}
		};
		
		outputsColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		outputsColumn.setSortable(true); 
		sortHandler.setComparator(outputsColumn,new Comparator<JsStockStat>() {
			
			@Override
			public int compare(JsStockStat o1, JsStockStat o2) {
				return o1.getOutputs().compareTo(o2.getOutputs());
			}
		});
		
		/** Consumo por dia**/
		Column<JsStockStat,Number> dailyOutputsColumn = new Column<JsStockStat, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockStat object) {
				return object.getDailyOutputs()!=null ? object.getDailyOutputs() : null;
			}
		};
		
		dailyOutputsColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		dailyOutputsColumn.setSortable(true); 
		sortHandler.setComparator(dailyOutputsColumn,new Comparator<JsStockStat>() {
			
			@Override
			public int compare(JsStockStat o1, JsStockStat o2) {
				return o1.getDailyOutputs().compareTo(o2.getDailyOutputs());
			}
		});
		
		/** Acopio **/
		Column<JsStockStat,Number> accumulationColumn = new Column<JsStockStat, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockStat object) {
				return object.getAccumulation()!=null ? object.getAccumulation() : null;
			}
		};
		
		accumulationColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		accumulationColumn.setSortable(true); 
		sortHandler.setComparator(accumulationColumn,new Comparator<JsStockStat>() {
			
			@Override
			public int compare(JsStockStat o1, JsStockStat o2) {
				return o1.getAccumulation().compareTo(o2.getAccumulation());
			}
		});
		
		/** Stock **/
		Column<JsStockStat,Number> stockColumn = new Column<JsStockStat, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockStat object) {
				return object.getStock()!=null ? object.getStock() : null;
			}
		};
		
		stockColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		stockColumn.setSortable(true); 
		sortHandler.setComparator(stockColumn,new Comparator<JsStockStat>() {
			
			@Override
			public int compare(JsStockStat o1, JsStockStat o2) {
				return o1.getStock().compareTo(o2.getStock());
			}
		});
		
		/** Pendiente de recibir **/
		Column<JsStockStat,Number> pendingPurchasesColumn = new Column<JsStockStat, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockStat object) {
				return object.getPendingPurchases()!=null ? object.getPendingPurchases() : null;
			}
		};
		
		pendingPurchasesColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		pendingPurchasesColumn.setSortable(true); 
		sortHandler.setComparator(pendingPurchasesColumn,new Comparator<JsStockStat>() {
			
			@Override
			public int compare(JsStockStat o1, JsStockStat o2) {
				return o1.getPendingPurchases().compareTo(o2.getPendingPurchases());
			}
		});
		
		/** Pendiente de servir **/
		Column<JsStockStat,Number> pendingSalesColumn = new Column<JsStockStat, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockStat object) {
				return object.getPendingSales()!=null ? object.getPendingSales() : null;
			}
		};
		
		pendingSalesColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		pendingSalesColumn.setSortable(true); 
		sortHandler.setComparator(pendingSalesColumn,new Comparator<JsStockStat>() {
			
			@Override
			public int compare(JsStockStat o1, JsStockStat o2) {
				return o1.getPendingSales().compareTo(o2.getPendingSales());
			}
		});
		
		/** Propuesta **/
		Column<JsStockStat,Number> proposalColumn = new Column<JsStockStat, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockStat object) {
				return object.getProposal()!=null ? object.getProposal() : null;
			}
		};
		
		proposalColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		proposalColumn.setSortable(true); 
		sortHandler.setComparator(proposalColumn,new Comparator<JsStockStat>() {
			
			@Override
			public int compare(JsStockStat o1, JsStockStat o2) {
				return o1.getProposal().compareTo(o2.getProposal());
			}
		});
	
		
		dataGrid.addColumn(nameColumn, AON.MSG.product());
		dataGrid.addColumn(outputsColumn, "Consumo");
		dataGrid.addColumn(dailyOutputsColumn, "Cns./D\u00EDa");
		dataGrid.addColumn(accumulationColumn, "Acopio");
		dataGrid.addColumn(stockColumn, "Stock");
		dataGrid.addColumn(pendingPurchasesColumn, "Pte.Recibir");
		dataGrid.addColumn(pendingSalesColumn, "Pte.Servir");
		dataGrid.addColumn(proposalColumn, "Propuesta");
		
		dataGrid.setColumnWidth(nameColumn, 100, Unit.PCT);
		dataGrid.setColumnWidth(outputsColumn, 100, Unit.PX);
		dataGrid.setColumnWidth(dailyOutputsColumn, 100, Unit.PX);
		dataGrid.setColumnWidth(accumulationColumn, 100, Unit.PX);
		dataGrid.setColumnWidth(stockColumn, 100, Unit.PX);
		dataGrid.setColumnWidth(pendingPurchasesColumn, 100, Unit.PX);
		dataGrid.setColumnWidth(pendingSalesColumn, 100, Unit.PX);
		dataGrid.setColumnWidth(proposalColumn, 100, Unit.PX);
		
	}
}
