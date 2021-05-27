package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaboration;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
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
	CustomDataGrid<JsElaboration> dataGrid; 
	
	Elaboration elaboration;
	API API;
	Integer cont = 0;
	
	public GridPanel(Elaboration elaboration, LinkedList<JsElaboration> list) {
		this.elaboration = elaboration;
		this.API = elaboration.getAPI(); 
		
		dataGrid = new CustomDataGrid<JsElaboration>(Integer.MAX_VALUE, resources,
				JsElaboration.PROVIDES_KEY);
		
		ScrollPanel scrollPanel = dataGrid.getScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				if(scrollPanel.getVerticalScrollPosition() >= scrollPanel.getMaximumVerticalScrollPosition()){
					Integer page = 2;
					if(elaboration.getFilterMap().containsKey("page")){
						page = Integer.parseInt(elaboration.getFilterMap().get("page").get(0)) + 1;
					}
					LinkedList<String> list = new LinkedList<>();
					list.add(page +"");
					elaboration.getFilterMap().put("page", list);
					API.getWarehouse().getElaborationList(elaboration.getFilterMap(), new AsyncCallback<JSON<JsElaboration>>() {
						
						@Override
						public void onSuccess(JSON<JsElaboration> result) {
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
	
		initWidget(binder.createAndBindUi(this));

		load(list);				
	}	
	
	private void load(LinkedList<JsElaboration> list) {
		DefaultKeyboardSelectionHandler<JsElaboration> selHandler = new DefaultKeyboardSelectionHandler<JsElaboration>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsElaboration> event) {
				if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
				    JsElaboration object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				    
				    elaboration.onSelectElaboration(object);
				}		
			}
		};
		
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
		addDataDisplay(dataGrid, list);
		ListHandler<JsElaboration> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
 		final SingleSelectionModel<JsElaboration> selectionModel = new SingleSelectionModel<JsElaboration>(
 				JsElaboration.PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsElaboration> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<JsElaboration> dataProvider = new ListDataProvider<JsElaboration>();

	public void addDataDisplay(HasData<JsElaboration> display, LinkedList<JsElaboration> list) {
		dataProvider = new ListDataProvider<JsElaboration>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsElaboration> getSortHandler() {
		return new ListHandler<JsElaboration>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsElaboration> aux  = super.getList();
				List<JsElaboration> aux2 = new LinkedList<JsElaboration>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initTableColumns(final SelectionModel<JsElaboration> selectionModel, ListHandler<JsElaboration> sortHandler) {
		NumberFormat numberFormat = NumberFormat.getDecimalFormat().overrideFractionDigits(2);
		
		/** Series-Number Column **/
		Column<JsElaboration, String> nameColumn = new Column<JsElaboration, String>(new TextCell()) {

			@Override
			public String getValue(JsElaboration object) {
				return (object.getSeries()!=null?object.getSeries():"") + "/" + object.getNumber();
			}
		
		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		nameColumn.setSortable(true); 
		sortHandler.setComparator(nameColumn,new Comparator<JsElaboration>() {
			
			@Override
			public int compare(JsElaboration o1, JsElaboration o2) {
				String a = (o1.getSeries()!=null?o1.getSeries():"") + "/" + o1.getNumber(); 
				String b = (o2.getSeries()!=null?o2.getSeries():"") + "/" + o2.getNumber(); 
				return a.compareTo(b);
			}
		});

		/** Description Column **/
		Column<JsElaboration,String> descriptionColumn = new Column<JsElaboration, String>(new TextCell()) {
			
			@Override
			public String getValue(JsElaboration object) {
				return object.getDescription();
			}
		};
		
		descriptionColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		descriptionColumn.setSortable(true); 
		sortHandler.setComparator(descriptionColumn,new Comparator<JsElaboration>() {
			
			@Override
			public int compare(JsElaboration o1, JsElaboration o2) {
				return o1.getDescription().compareTo(o2.getDescription());
			}
		});
		
		/** Quantity Column **/
		Column<JsElaboration,Number> quantityColumn = new Column<JsElaboration, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsElaboration object) {
				return object.getQuantity()!=null ? object.getQuantity() : null;
			}
		};
		
		quantityColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		quantityColumn.setSortable(true); 
		sortHandler.setComparator(quantityColumn,new Comparator<JsElaboration>() {
			
			@Override
			public int compare(JsElaboration o1, JsElaboration o2) {
				return o1.getQuantity().compareTo(o2.getQuantity());
			}
		});
		
		/** Warehouse Column **/
		Column<JsElaboration,String> warehouseColumn = new Column<JsElaboration, String>(new TextCell()) {
			
			@Override
			public String getValue(JsElaboration object) {
				return object.getWarehouse()!=null?object.getWarehouse().getName():" ";
			}
		};
		
		warehouseColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		warehouseColumn.setSortable(true); 
		sortHandler.setComparator(warehouseColumn,new Comparator<JsElaboration>() {
			
			@Override
			public int compare(JsElaboration o1, JsElaboration o2) {
				String w1 = o1.getWarehouse()!=null?o1.getWarehouse().getName():"";
				String w2 = o2.getWarehouse()!=null?o2.getWarehouse().getName():"";
				return w1.compareTo(w2);
			}
		});
		
		/** DATE Column **/
		Column<JsElaboration,String> dateColumn = new Column<JsElaboration, String>(new TextCell()) {
			
			@Override
			public String getValue(JsElaboration object) {
				return object.getDate() != null ? object.getDate() : null;
			}
		};
		
		dateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		dateColumn.setSortable(true); 
		sortHandler.setComparator(dateColumn,new Comparator<JsElaboration>() {
			
			@Override
			public int compare(JsElaboration o1, JsElaboration o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});
		
		/** Status Column **/
		Column<JsElaboration,String> statusColumn = new Column<JsElaboration, String>(new TextCell()) {
			
			@Override
			public String getValue(JsElaboration object) {
				return object.getStatus() != null ? object.getStatus().getName() : "";
			}
		};
		
		statusColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		statusColumn.setSortable(true); 
		sortHandler.setComparator(statusColumn,new Comparator<JsElaboration>() {
			
			@Override
			public int compare(JsElaboration o1, JsElaboration o2) {
				return o1.getStatus().getName().compareTo(o2.getStatus().getName());
			}
		});

		dataGrid.addColumn(nameColumn, AON.MSG.series() + "/" + AON.MSG.number());
		dataGrid.addColumn(descriptionColumn, AON.MSG.description());
		dataGrid.addColumn(quantityColumn, AON.MSG.quantity());
		dataGrid.addColumn(warehouseColumn, "Almacen");
		dataGrid.addColumn(dateColumn, AON.MSG.date());
		dataGrid.addColumn(statusColumn, AON.MSG.status());
		
		dataGrid.setColumnWidth(nameColumn, 10, Unit.PCT);
		dataGrid.setColumnWidth(descriptionColumn, 40, Unit.PCT);
		dataGrid.setColumnWidth(quantityColumn, 10, Unit.PCT);
		dataGrid.setColumnWidth(warehouseColumn, 20, Unit.PCT);
		dataGrid.setColumnWidth(dateColumn, 10, Unit.PCT);
		dataGrid.setColumnWidth(statusColumn, 10, Unit.PCT);
		
	}
}
