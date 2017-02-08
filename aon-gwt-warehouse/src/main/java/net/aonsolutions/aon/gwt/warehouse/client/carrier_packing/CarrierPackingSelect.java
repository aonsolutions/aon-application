package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
	
	ListBox selectable;
	DateBoxEx datebox;
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
		HorizontalPanel p = new HorizontalPanel();
		Label label = new Label("Fecha ");
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		label.getElement().getStyle().setPadding(3, Unit.PX);
		p.add(label);
		datebox = new DateBoxEx();
		datebox.setStyleName(AON.AON_CSS.aonTextBox());
		datebox.setValue(new Date());
		datebox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				loadSelectable();
				westPanel.remove(1);
				westPanel.add(selectable);
			}
		});
		p.add(datebox);
		loadSelectable();
		loadSelected(true);
		westPanel.add(p);
		westPanel.add(selectable);
	}
	
	public void refresh(){
		loadSelectable();
		westPanel.remove(1);
		westPanel.add(selectable);
		loadSelected(false);
	}
	
	private void loadSelectable() {
		selectable = new ListBox();
		selectable.getElement().getStyle().setBackgroundColor("#f6f5e3");
		selectable.setWidth("300px");
		selectable.setVisibleItemCount(20);
	
		HashMap<String, LinkedList<String>> map = new HashMap<>();
		LinkedList<String> carrierList = new LinkedList<>();
		carrierList.add(jsCarrierPacking.getCarrier().getId() + "");
		map.put("carrier", carrierList);
		LinkedList<String> ncarrierList = new LinkedList<>();
		ncarrierList.add("");
		map.put("not_carrier_packing", ncarrierList);
		LinkedList<String> nDate = new LinkedList<>();
		nDate.add(Long.toString(datebox.getValue().getTime()));
		map.put("issue_date", nDate);	
		selectableItems(map);
		selectable.addClickHandler(selectableClickHandler());
		selectable.addDoubleClickHandler(selectableDoubleClickHandler());
	}
	
	private void loadSelected(Boolean isCreate) {
		HashMap<String, LinkedList<String>> map2 = new HashMap<>();
		LinkedList<String> list = new LinkedList<>();
		list.add(jsCarrierPacking.getId() + "");
		map2.put("carrier_packing", list);
		selectedItems(map2, isCreate);
	}
	
	private void selectedItems(HashMap<String, LinkedList<String>> map, Boolean isCreate){
		if(jsCarrierPacking.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
			API.getWarehouse().getPurchases(map,new AsyncCallback<JSON<JsOrder>>() {
			
				@Override
				public void onSuccess(JSON<JsOrder> result) {
					parent.setEnableType(result.getData().length() <= 0);
					if(isCreate){
						loadDatagrid(result.getData().toLinkedList());
					} else {
						addDataDisplay(dataGrid, result.getData().toLinkedList());
					}
				}
			
				@Override
				public void onFailure(Throwable caught) {}
			});
		} else {
			API.getWarehouse().getDeliveries(map,new AsyncCallback<JSON<JsOrder>>() {
				
				@Override
				public void onSuccess(JSON<JsOrder> result) {
					parent.setEnableType(result.getData().length() <= 0);
					if(isCreate){
						loadDatagrid(result.getData().toLinkedList());
					} else {
						addDataDisplay(dataGrid, result.getData().toLinkedList());
					}
				}
			
				@Override
				public void onFailure(Throwable caught) {}
			});
		}
	}
	
	private void selectableItems(HashMap<String, LinkedList<String>> map){
		if(jsCarrierPacking.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
			API.getWarehouse().getPurchases(map,new AsyncCallback<JSON<JsOrder>>() {
				
				@Override
				public void onSuccess(JSON<JsOrder> result) {
					result.getData().stream().forEach(js -> 
						selectable.addItem(js.getIssueDate() + "-" + js.getSeries() + "/" + js.getNumber() 
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
						selectable.addItem(js.getIssueDate() + "-" + js.getSeries() + "/" + js.getNumber() 
							+ "-" + js.getRegistry().getName(),js.getId()+""));
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			});
		}
	}
	
	private ClickHandler selectableClickHandler(){
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String value = selectable.getSelectedValue();
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
		};
	}
	
	private DoubleClickHandler selectableDoubleClickHandler(){
		return new DoubleClickHandler() {
			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				String value = selectable.getSelectedValue();
				
				selectable.removeItem(selectable.getSelectedIndex());
				
				String requestData = "{\"carrier_packing\":\""+ jsCarrierPacking.getId() +"\","
						+ "\"purchase\":\""+ Integer.parseInt(value) +"\","
						+ "\"action\":\"add\"" + "}";
				if(jsCarrierPacking.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
					API.getWarehouse().addAllCarrierPacking("purchase", requestData, new AsyncCallback<JSON<JsOrderDetail>>() {
						@Override public void onSuccess(JSON<JsOrderDetail> result) {
							API.getWarehouse().getPurchase(Integer.parseInt(value), new AsyncCallback<JSON<JsOrder>>() {
								@Override public void onFailure(Throwable caught) {}
								@Override public void onSuccess(JSON<JsOrder> result) {
									addItem(result.getOneData());
									parent.refreshSouth2(result.getOneData());
								}
							});
						}
						@Override public void onFailure(Throwable caught) {}
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
		};
	}
	
	
	public JsCarrierPacking getJsCarrierPacking() {
		return jsCarrierPacking;
	}
	
	// -------------------- Actions
	private void deleteOrder(JsOrder js){
		String requestData = "{\"carrier_packing\":\"\"}";
		if(jsCarrierPacking.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
			requestData = "{\"carrier_packing\":\""+ jsCarrierPacking.getId() +"\","
					+ "\"purchase\":\""+ js.getId() +"\"," 
					+ "\"action\":\"delete\""+ "}";

			API.getWarehouse().addAllCarrierPacking("purchase", requestData, new AsyncCallback<JSON<JsOrderDetail>>() {
				@Override public void onSuccess(JSON<JsOrderDetail> result) {
					refresh();
					parent.refreshSouth2(js);
				}
				@Override public void onFailure(Throwable caught) {}
			});
		} else {
			API.getWarehouse().updateDelivery(js.getId(), requestData, new AsyncCallback<JsOrder>() {
				@Override public void onFailure(Throwable caught) {}
				@Override public void onSuccess(JsOrder result) {
					refresh();
				}
			});		
		}
	}
	
	// -------------------- DataGrid Utils
	
	private void loadDatagrid(LinkedList<JsOrder> list) {
		DefaultKeyboardSelectionHandler<JsOrder> selHandler = new DefaultKeyboardSelectionHandler<JsOrder>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsOrder> event) {
				if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
				    JsOrder object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				    object.getOrderType();

				    API.getWarehouse().getDetails(object.getId(), object.getOrderType(), new AsyncCallback<JSON<JsOrderDetail>>() {
						
						@Override
						public void onSuccess(JSON<JsOrderDetail> result) {
							parent.southContent(jsCarrierPacking, object, result.getData());							
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
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
	
	public void addItem(JsOrder js){
		dataProvider.getList().add(js);
		dataGrid.redraw();
		parent.setEnableType(false);
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

		/** Delete Column **/
		ActionCell<JsOrder> cell = new ActionCell<JsOrder>("delete", new Delegate<JsOrder>() {
			 @Override
		        public void execute(JsOrder object) {
		        	deleteOrder(object);
		        }
		});
		
		Column<JsOrder, JsOrder> deleteColumn = new Column<JsOrder, JsOrder>(cell){
				@Override
				public void render(Context context, JsOrder object, SafeHtmlBuilder sb) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-delete\" tabindex=\"-1\">");
					sb.appendHtmlConstant("</button>");
				}
				
				@Override
				public JsOrder getValue(JsOrder object) {
					return object;
				}
			};
			deleteColumn.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
			dataGrid.addColumn(deleteColumn, "");
			dataGrid.setColumnWidth(deleteColumn, 10, Unit.PCT);
	}
}
