package net.aonsolutions.aon.gwt.warehouse.client.movementList;

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
import com.google.gwt.dom.client.BrowserEvents;
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
	
	MovementList parent;
	API API;
	Integer cont = 0;
	
	public GridPanel(MovementList parent, LinkedList<JsStockStat> linkedList) {
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
		if(linkedList!=null && !linkedList.isEmpty())
			dataGrid.addStyleName(AON.AON_CSS.aonClickableBlock());
		
		DefaultKeyboardSelectionHandler<JsStockStat> selHandler = new DefaultKeyboardSelectionHandler<JsStockStat>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsStockStat> event) {
				if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
				    JsStockStat object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
					parent.getMainContent().onSelectProduct(object);
				}
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
		Column<JsStockStat, String> productColumn = new Column<JsStockStat, String>(new TextCell()) {

			@Override
			public String getValue(JsStockStat object) {
				return object.getProductName();
			}
		
		};
		productColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		productColumn.setSortable(true); 
		sortHandler.setComparator(productColumn,new Comparator<JsStockStat>() {
			
			@Override
			public int compare(JsStockStat o1, JsStockStat o2) {
				String a = o1.getProductName(); 
				String b = o2.getProductName(); 
				return a.compareTo(b);
			}
		});
		
		/** Entradas **/
		Column<JsStockStat,Number> inputsColumn = new Column<JsStockStat, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockStat object) {
				return object.getInputs()!=null ? object.getInputs() : null;
			}
		};
		
		inputsColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		inputsColumn.setSortable(true); 
		sortHandler.setComparator(inputsColumn,new Comparator<JsStockStat>() {
			
			@Override
			public int compare(JsStockStat o1, JsStockStat o2) {
				return o1.getInputs().compareTo(o2.getInputs());
			}
		});
		
		/** Salidas **/
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
		
		/** Saldo **/
		Column<JsStockStat,Number> balanceColumn = new Column<JsStockStat, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsStockStat object) {
				return object.getBalance()!=null ? object.getBalance() : null;
			}
		};
		
		balanceColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		balanceColumn.setSortable(true); 
		sortHandler.setComparator(balanceColumn,new Comparator<JsStockStat>() {
			
			@Override
			public int compare(JsStockStat o1, JsStockStat o2) {
				return o1.getBalance().compareTo(o2.getBalance());
			}
		});
		
		dataGrid.addColumn(productColumn, AON.MSG.product());
		dataGrid.addColumn(inputsColumn, "Entradas");
		dataGrid.addColumn(outputsColumn, "Salidas");
		dataGrid.addColumn(balanceColumn, "Saldo");
		
		dataGrid.setColumnWidth(productColumn, 100, Unit.PCT);
		dataGrid.setColumnWidth(inputsColumn, 100, Unit.PX);
		dataGrid.setColumnWidth(outputsColumn, 100, Unit.PX);
		dataGrid.setColumnWidth(balanceColumn, 100, Unit.PX);
		
	}
}
