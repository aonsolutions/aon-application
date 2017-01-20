package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class Grid extends Composite {

	interface GridBinder extends UiBinder<Widget, Grid> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);
	
	DataGridResources resources = GWT.create(DataGridResources.class);
	
	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/datagrid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) DataGrid<JsCarrierPacking> dataGrid; 
	
	CarrierPacking carrierPacking;
	API API;
	public Grid(CarrierPacking carrierPacking) {
		this.carrierPacking = carrierPacking;
		this.API = carrierPacking.API; 
		
		dataGrid = new DataGrid<JsCarrierPacking>(Integer.MAX_VALUE, resources,
				JsCarrierPacking.PROVIDES_KEY);
		
		initWidget(binder.createAndBindUi(this));
		API.getWarehouse().getCarrierPacking(new AsyncCallback<JSON<JsCarrierPacking>>() {
			
			@Override
			public void onSuccess(JSON<JsCarrierPacking> result) {
				load(result.getData().toLinkedList());				
			}
			
			@Override public void onFailure(Throwable caught) {}
		});

	}	
	
	private void load(LinkedList<JsCarrierPacking> list) {
		DefaultKeyboardSelectionHandler<JsCarrierPacking> selHandler = new DefaultKeyboardSelectionHandler<JsCarrierPacking>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsCarrierPacking> event) {
				if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
				    JsCarrierPacking object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				    
				    // TODO Entrar a la pantalla del carrier!!!
				    
				    carrierPacking.carrierPackingContent(object);
				}		
			}
		};
		
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY CARRIER PACKINGS!"));
		addDataDisplay(dataGrid, list);
		ListHandler<JsCarrierPacking> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
 		final SingleSelectionModel<JsCarrierPacking> selectionModel = new SingleSelectionModel<JsCarrierPacking>(
 				JsCarrierPacking.PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsCarrierPacking> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<JsCarrierPacking> dataProvider = new ListDataProvider<JsCarrierPacking>();

	public void addDataDisplay(HasData<JsCarrierPacking> display, LinkedList<JsCarrierPacking> list) {
		dataProvider = new ListDataProvider<JsCarrierPacking>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsCarrierPacking> getSortHandler() {
		return new ListHandler<JsCarrierPacking>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsCarrierPacking> aux  = super.getList();
				List<JsCarrierPacking> aux2 = new LinkedList<JsCarrierPacking>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initTableColumns(final SelectionModel<JsCarrierPacking> selectionModel, ListHandler<JsCarrierPacking> sortHandler) {
		/** Name Column **/
		Column<JsCarrierPacking, String> nameColumn = new Column<JsCarrierPacking, String>(new TextCell()) {
			
			@Override
			public void render(Context context, JsCarrierPacking object,
					SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span>"+object.getName()+"</span>");
			}
			
			@Override
			public String getValue(JsCarrierPacking object) {
				return object.getName();
			}
		
		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		nameColumn.setSortable(true); 
		sortHandler.setComparator(nameColumn,new Comparator<JsCarrierPacking>() {
			
			@Override
			public int compare(JsCarrierPacking o1, JsCarrierPacking o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		dataGrid.getColumnSortList().push(nameColumn);
		dataGrid.addColumn(nameColumn, AON.MSG.name());
		dataGrid.setColumnWidth(nameColumn, 30, Unit.PCT);
		
		/** Type Column **/
		Column<JsCarrierPacking,String> typeColumn = new Column<JsCarrierPacking, String>(new TextCell()) {
			
			@Override
			public String getValue(JsCarrierPacking object) {
				return object.getType() != null ? object.getType().getName() : "";
			}
		};
		
		typeColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		typeColumn.setSortable(true); 
		sortHandler.setComparator(typeColumn,new Comparator<JsCarrierPacking>() {
			
			@Override
			public int compare(JsCarrierPacking o1, JsCarrierPacking o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		dataGrid.getColumnSortList().push(typeColumn);
		dataGrid.addColumn(typeColumn, AON.MSG.type());
		dataGrid.setColumnWidth(typeColumn, 20, Unit.PCT);
	}
}
