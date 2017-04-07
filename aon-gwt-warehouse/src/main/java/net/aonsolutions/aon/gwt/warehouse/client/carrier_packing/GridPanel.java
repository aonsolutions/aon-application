package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.common.client.AON;
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

public class GridPanel extends ResizeComposite implements RequiresResize {

	interface GridBinder extends UiBinder<Widget, GridPanel> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);
	
	DataGridResources resources = GWT.create(DataGridResources.class);
	
	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) CustomDataGrid<JsCarrierPacking> dataGrid; 
	
	CarrierPackingPrincipal parent;
	Integer cont = 0;

	public GridPanel(CarrierPackingPrincipal carrierPacking, LinkedList<JsCarrierPacking> list) {
		this.parent = carrierPacking;		
		dataGrid = new CustomDataGrid<JsCarrierPacking>(Integer.MAX_VALUE, resources,
				JsCarrierPacking.PROVIDES_KEY);
		
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
					parent.getAPI().getWarehouse().getCarrierPacking(parent.getFilterMap(), new AsyncCallback<JSON<JsCarrierPacking>>() {
						
						@Override
						public void onSuccess(JSON<JsCarrierPacking> result) {
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
	
	private void load(LinkedList<JsCarrierPacking> list) {
		DefaultKeyboardSelectionHandler<JsCarrierPacking> selHandler = new DefaultKeyboardSelectionHandler<JsCarrierPacking>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsCarrierPacking> event) {
				if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
				    JsCarrierPacking object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());				    
				    LinkedList<String> list = new LinkedList<>();
				    list.add((dataGrid.getKeyboardSelectedRow()+1) + "");
				    HashMap<String, LinkedList<String>> map = parent.getFilterMap();
				    map.put("page", list);
				    parent.setFilterMap(map);
				    parent.selectCarrierPacking(object);
				}		
			}
		};
		
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
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
				return o1.getType().getName().compareTo(o2.getType().getName());
			}
		});
		dataGrid.getColumnSortList().push(typeColumn);
		dataGrid.addColumn(typeColumn, AON.MSG.type());
		dataGrid.setColumnWidth(typeColumn, 20, Unit.PCT);
		
		
		/** Serie/Number Column **/
		Column<JsCarrierPacking, String> nameColumn = new Column<JsCarrierPacking, String>(new TextCell()) {

			@Override
			public String getValue(JsCarrierPacking object) {
				return object.getSeries() + "/" + object.getNumber();
			}
		
		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		nameColumn.setSortable(true); 
		sortHandler.setComparator(nameColumn,new Comparator<JsCarrierPacking>() {
			
			@Override
			public int compare(JsCarrierPacking o1, JsCarrierPacking o2) {
				String a = o1.getSeries() + "/" + o1.getNumber(); 
				String b = o2.getSeries() + "/" + o2.getNumber(); 
				return a.compareTo(b);
			}
		});
		dataGrid.getColumnSortList().push(nameColumn);
		dataGrid.addColumn(nameColumn, AON.MSG.series() + "/" + AON.MSG.number());
		dataGrid.setColumnWidth(nameColumn, 15, Unit.PCT);
		
		
		/** S/RefColumn **/
		Column<JsCarrierPacking, String> referenceColumn = new Column<JsCarrierPacking, String>(new TextCell()) {

			@Override
			public String getValue(JsCarrierPacking object) {
				return object.getCarrierReference();
			}
		
		};
		referenceColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		referenceColumn.setSortable(true); 
		sortHandler.setComparator(referenceColumn,new Comparator<JsCarrierPacking>() {
			
			@Override
			public int compare(JsCarrierPacking o1, JsCarrierPacking o2) {
				return o1.getCarrierReference().compareTo(o2.getCarrierReference());
			}
		});
		dataGrid.getColumnSortList().push(referenceColumn);
		dataGrid.addColumn(referenceColumn, "Referencia");
		dataGrid.setColumnWidth(referenceColumn, 20, Unit.PCT);
		
		/** ISSUE DATE Column **/
		Column<JsCarrierPacking,String> issueDateColumn = new Column<JsCarrierPacking, String>(new TextCell()) {
			
			@Override
			public String getValue(JsCarrierPacking object) {
				return object.getIssueDate() != null ? object.getIssueDate() : "";
			}
		};
		
		issueDateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		issueDateColumn.setSortable(true); 
		sortHandler.setComparator(issueDateColumn,new Comparator<JsCarrierPacking>() {
			
			@Override
			public int compare(JsCarrierPacking o1, JsCarrierPacking o2) {
				return o1.getIssueDate().compareTo(o2.getIssueDate());
			}
		});
		dataGrid.getColumnSortList().push(issueDateColumn);
		dataGrid.addColumn(issueDateColumn, AON.MSG.issueDate());
		dataGrid.setColumnWidth(issueDateColumn, 15, Unit.PCT);

		
		/** delivery DATE Column **/
		Column<JsCarrierPacking,String> deliveryDateColumn = new Column<JsCarrierPacking, String>(new TextCell()) {
			
			@Override
			public String getValue(JsCarrierPacking object) {
				return object.getDeliveryDate() != null ? object.getDeliveryDate() : "";
			}
		};
		
		deliveryDateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		deliveryDateColumn.setSortable(true); 
		sortHandler.setComparator(deliveryDateColumn,new Comparator<JsCarrierPacking>() {
			
			@Override
			public int compare(JsCarrierPacking o1, JsCarrierPacking o2) {
				return o1.getDeliveryDate().compareTo(o2.getDeliveryDate());
			}
		});
		dataGrid.getColumnSortList().push(deliveryDateColumn);
		dataGrid.addColumn(deliveryDateColumn, AON.MSG.deliveryDate());
		dataGrid.setColumnWidth(deliveryDateColumn, 15, Unit.PCT);
		
		/** CARRIER Column **/
		Column<JsCarrierPacking,String> carrierColumn = new Column<JsCarrierPacking, String>(new TextCell()) {
			
			@Override
			public String getValue(JsCarrierPacking object) {
				return object.getCarrier() != null ? object.getCarrier().getName() : "";
			}
		};
		
		carrierColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		carrierColumn.setSortable(true); 
		sortHandler.setComparator(carrierColumn,new Comparator<JsCarrierPacking>() {
			
			@Override
			public int compare(JsCarrierPacking o1, JsCarrierPacking o2) {
				return o1.getCarrier().getName().compareTo(o2.getCarrier().getName());
			}
		});
		dataGrid.getColumnSortList().push(carrierColumn);
		dataGrid.addColumn(carrierColumn, AON.MSG.carrier());
		dataGrid.setColumnWidth(carrierColumn, 30, Unit.PCT);

		
		/** Matricula Column **/
		Column<JsCarrierPacking,String> plateColumn = new Column<JsCarrierPacking, String>(new TextCell()) {
			
			@Override
			public String getValue(JsCarrierPacking object) {
				return object.getNumberPlate() != null ? object.getNumberPlate() : "";
			}
		};
		
		plateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		plateColumn.setSortable(true); 
		sortHandler.setComparator(plateColumn,new Comparator<JsCarrierPacking>() {
			
			@Override
			public int compare(JsCarrierPacking o1, JsCarrierPacking o2) {
				return o1.getNumberPlate().compareTo(o2.getNumberPlate());
			}
		});
		dataGrid.getColumnSortList().push(plateColumn);
		dataGrid.addColumn(plateColumn, "Matricula");
		dataGrid.setColumnWidth(plateColumn, 10, Unit.PCT);
		
		/** Line number Column **/
		Column<JsCarrierPacking,String> lineColumn = new Column<JsCarrierPacking, String>(new TextCell()) {
			
			@Override
			public String getValue(JsCarrierPacking object) {
				return "-"; //object.getLines() +"";
			}
		};
		
		lineColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		lineColumn.setSortable(true); 
		sortHandler.setComparator(lineColumn,new Comparator<JsCarrierPacking>() {
			
			@Override
			public int compare(JsCarrierPacking o1, JsCarrierPacking o2) {
				return o1.getStatus().getName().compareTo(o2.getStatus().getName());
			}
		});
		dataGrid.getColumnSortList().push(lineColumn);
		dataGrid.addColumn(lineColumn, "Lineas");
		dataGrid.setColumnWidth(lineColumn, 10, Unit.PCT);
		
		/** Status Column **/
		Column<JsCarrierPacking,String> statusColumn = new Column<JsCarrierPacking, String>(new TextCell()) {
			
			@Override
			public String getValue(JsCarrierPacking object) {
				return object.getStatus() != null ? object.getStatus().getName() : "";
			}
		};
		
		statusColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		statusColumn.setSortable(true); 
		sortHandler.setComparator(statusColumn,new Comparator<JsCarrierPacking>() {
			
			@Override
			public int compare(JsCarrierPacking o1, JsCarrierPacking o2) {
				return o1.getStatus().getName().compareTo(o2.getStatus().getName());
			}
		});
		dataGrid.getColumnSortList().push(statusColumn);
		dataGrid.addColumn(statusColumn, AON.MSG.status());
		dataGrid.setColumnWidth(statusColumn, 15, Unit.PCT);
	}
	
}
