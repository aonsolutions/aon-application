package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
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
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class CarrierPackingSelect extends Composite{
	
	interface Binder extends UiBinder<Widget, CarrierPackingSelect> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField DockLayoutPanel splitLayoutPanel;
	@UiField VerticalPanel westPanel;
	@UiField(provided = true) DataGrid<JsOrder> dataGrid; 

	DataGridResources resources = GWT.create(DataGridResources.class);
	
	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	private CarrierPacking parent;
	private API API;
	private JsCarrierPacking jsCarrierPacking;
	
	public CarrierPackingSelect(CarrierPacking carrierPacking, JsCarrierPacking js) {
		dataGrid = new DataGrid<JsOrder>(Integer.MAX_VALUE, resources,
				JsOrder.PROVIDES_KEY);
		initWidget(binder.createAndBindUi(this));
		this.API = carrierPacking.API;
		this.parent = carrierPacking;
		this.jsCarrierPacking = js;
		load();
	}
	
	private void load() {
		ListBox l = new ListBox();
		l.getElement().getStyle().setBackgroundColor("#f6f5e3");
		l.setWidth("300px");
		l.setVisibleItemCount(20);
		
		Label label = new Label("Pedidos sin seleccionar");
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		westPanel.add(label);
		westPanel.add(l);
		
		HashMap<String, LinkedList<String>> map = new HashMap<>();
		LinkedList<String> carrierList = new LinkedList<>();
		carrierList.add(jsCarrierPacking.getCarrier().getId() + "");
		map.put("carrier", carrierList);
		LinkedList<String> ncarrierList = new LinkedList<>();
		ncarrierList.add("");
		map.put("not_carrier_packing", ncarrierList);
			
		if(jsCarrierPacking.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
			API.getWarehouse().getPurchases(map,new AsyncCallback<JSON<JsOrder>>() {
				
				@Override
				public void onSuccess(JSON<JsOrder> result) {
					result.getData().stream().forEach(js -> 
						l.addItem(js.getIssueDate() + "-" + js.getSeries() + "/" + js.getNumber() 
							+ "-" + js.getRegistry().getName(),js.getId()+""));
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
		} else {
			API.getWarehouse().getDeliveries(map,new AsyncCallback<JSON<JsOrder>>() {
				
				@Override
				public void onSuccess(JSON<JsOrder> result) {
					result.getData().stream().forEach(js -> 
						l.addItem(js.getIssueDate() + "-" + js.getSeries() + "/" + js.getNumber() 
							+ "-" + js.getRegistry().getName(),js.getId()+""));
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
		}

		
		HashMap<String, LinkedList<String>> map2 = new HashMap<>();
		LinkedList<String> list = new LinkedList<>();
		list.add(jsCarrierPacking.getId() + "");
		map2.put("carrier_packing", list);
		if(jsCarrierPacking.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
			API.getWarehouse().getPurchases(map2,new AsyncCallback<JSON<JsOrder>>() {
			
				@Override
				public void onSuccess(JSON<JsOrder> result) {
					loadDatagrid(result.getData().toLinkedList());
				}
			
				@Override
				public void onFailure(Throwable caught) {}
			});
		} else {
			API.getWarehouse().getDeliveries(map2,new AsyncCallback<JSON<JsOrder>>() {
				
				@Override
				public void onSuccess(JSON<JsOrder> result) {
					loadDatagrid(result.getData().toLinkedList());
				}
			
				@Override
				public void onFailure(Throwable caught) {}
			});
		}
		l.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String value = l.getSelectedValue();
				if(jsCarrierPacking.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
					API.getWarehouse().getPurchase(Integer.parseInt(value),new AsyncCallback<JSON<JsOrder>>() {
					
						@Override
						public void onSuccess(JSON<JsOrder> result) {
							API.getWarehouse().getPurchaseDetails(result.getOneData().getId(), new AsyncCallback<JSON<JsOrderDetail>>() {
							
								@Override
								public void onSuccess(JSON<JsOrderDetail> details) {
									parent.southContent(jsCarrierPacking, result.getOneData(), details.getData());
								}
								
								@Override public void onFailure(Throwable caught) {}
							});
						}
					
						@Override
						public void onFailure(Throwable caught) {}
					});
				} else {
					API.getWarehouse().getDelivery(Integer.parseInt(value),new AsyncCallback<JSON<JsOrder>>() {
						
						@Override
						public void onSuccess(JSON<JsOrder> result) {
							API.getWarehouse().getDeliveryDetails(result.getOneData().getId(), new AsyncCallback<JSON<JsOrderDetail>>() {
							
								@Override
								public void onSuccess(JSON<JsOrderDetail> details) {
									parent.southContent(jsCarrierPacking, result.getOneData(), details.getData());
								}
								
								@Override public void onFailure(Throwable caught) {}
							});
						}
					
						@Override
						public void onFailure(Throwable caught) {}
					});
				}
			}
		});
		
		l.addDoubleClickHandler(new DoubleClickHandler() {
			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				String label = l.getSelectedItemText();
				String value = l.getSelectedValue();
				
				l.removeItem(l.getSelectedIndex());
				
				String requestData = "{\"carrier_packing\":\""+ jsCarrierPacking.getId() +"\"}";
				if(jsCarrierPacking.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
					API.getWarehouse().updatePurchase(Integer.parseInt(value), requestData, new AsyncCallback<JsOrder>() {
						@Override public void onFailure(Throwable caught) {}
						@Override public void onSuccess(JsOrder result) {
							addItem(result);
						}
					});
				} else {
					API.getWarehouse().updateDelivery(Integer.parseInt(value), requestData, new AsyncCallback<JsOrder>() {
						@Override public void onFailure(Throwable caught) {}
						@Override public void onSuccess(JsOrder result) {
							addItem(result);
						}
					});
				}
			}
		});	
	}

	
	public JsCarrierPacking getJsCarrierPacking() {
		return jsCarrierPacking;
	}
	
	
	private void loadDatagrid(LinkedList<JsOrder> list) {
		DefaultKeyboardSelectionHandler<JsOrder> selHandler = new DefaultKeyboardSelectionHandler<JsOrder>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsOrder> event) {
				if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
				    JsOrder object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				    
				    // TODO MOSTRAR DETAILS EN EL SOUTH!				  
				}		
			}
		};
		
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
		addDataDisplay(dataGrid, list);
		ListHandler<JsOrder> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
 		final SingleSelectionModel<JsOrder> selectionModel = new SingleSelectionModel<JsOrder>(
 				JsOrder.PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsOrder> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}
	
	//------------------------------ DataGrid Utils
	
	public void addItem(JsOrder js){
		dataProvider.getList().add(js);
		dataGrid.redraw();
	}
	
	private ListDataProvider<JsOrder> dataProvider = new ListDataProvider<JsOrder>();

	public void addDataDisplay(HasData<JsOrder> display, LinkedList<JsOrder> list) {
		dataProvider = new ListDataProvider<JsOrder>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsOrder> getSortHandler() {
		return new ListHandler<JsOrder>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsOrder> aux  = super.getList();
				List<JsOrder> aux2 = new LinkedList<JsOrder>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initTableColumns(final SelectionModel<JsOrder> selectionModel, ListHandler<JsOrder> sortHandler) {
		/** Date Column **/
		Column<JsOrder,String> dateColumn = new Column<JsOrder, String>(new TextCell()) {
			
			@Override
			public String getValue(JsOrder object) {
				return object.getIssueDate() != null ? object.getIssueDate() : "";
			}
		};
		
		dateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		dateColumn.setSortable(true); 
		sortHandler.setComparator(dateColumn,new Comparator<JsOrder>() {
			
			@Override
			public int compare(JsOrder o1, JsOrder o2) {
				return o1.getIssueDate().compareTo(o2.getIssueDate());
			}
		});
		dataGrid.getColumnSortList().push(dateColumn);
		dataGrid.addColumn(dateColumn, AON.MSG.date());
		dataGrid.setColumnWidth(dateColumn, 10, Unit.PCT);
		
		/** Name Column **/
		Column<JsOrder, String> nameColumn = new Column<JsOrder, String>(new TextCell()) {

			@Override
			public String getValue(JsOrder object) {
				return object.getSeries() + "/" + object.getNumber();
			}
		
		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		nameColumn.setSortable(true); 
		sortHandler.setComparator(nameColumn,new Comparator<JsOrder>() {
			
			@Override
			public int compare(JsOrder o1, JsOrder o2) {
				String a = o1.getSeries() + "/" + o1.getNumber(); 
				String b = o2.getSeries() + "/" + o2.getNumber(); 
				return a.compareTo(b);
			}
		});
		dataGrid.getColumnSortList().push(nameColumn);
		dataGrid.addColumn(nameColumn, AON.MSG.series() + "/" + AON.MSG.number());
		dataGrid.setColumnWidth(nameColumn, 10, Unit.PCT);

		/** Registry Column **/
		Column<JsOrder,String> registryColumn = new Column<JsOrder, String>(new TextCell()) {
			
			@Override
			public String getValue(JsOrder object) {
				return object.getRegistry().getName() != null ? object.getRegistry().getName() : "";
			}
		};
		
		registryColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		registryColumn.setSortable(true); 
		sortHandler.setComparator(registryColumn,new Comparator<JsOrder>() {
			
			@Override
			public int compare(JsOrder o1, JsOrder o2) {
				return o1.getRegistry().getName().compareTo(o2.getRegistry().getName());
			}
		});
		dataGrid.getColumnSortList().push(registryColumn);
		dataGrid.addColumn(registryColumn, AON.MSG.name());
		dataGrid.setColumnWidth(registryColumn, 50, Unit.PCT);

		
		/** price Column **/
		Column<JsOrder,String> priceColumn = new Column<JsOrder, String>(new TextCell()) {
			
			@Override
			public String getValue(JsOrder object) {
				return "";
			}
		};
		
		priceColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		priceColumn.setSortable(true); 
		sortHandler.setComparator(priceColumn,new Comparator<JsOrder>() {
			
			@Override
			public int compare(JsOrder o1, JsOrder o2) {
				return o1.getBankAccount().compareTo(o2.getBankAccount());
			}
		});
		dataGrid.getColumnSortList().push(priceColumn);
		dataGrid.addColumn(priceColumn, "Importe");
		dataGrid.setColumnWidth(priceColumn, 10, Unit.PCT);
	}
}
