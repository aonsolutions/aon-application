package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
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
import com.google.gwt.user.client.ui.FlexTable;
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
					if(event.getColumn() != 7) {
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
				String[] a1 = o1.getIssueDate().split("/");
				String a = "";
				for(Integer i = a1.length - 1; i >= 0 ; i--){
					a = a + a1[i];
				}
				
				String[] b1 = o2.getIssueDate().split("/");
				String b = "";
				for(Integer i = b1.length - 1; i >= 0 ; i--){
					b = b + b1[i];
				}
				return a.compareTo(b);
			}
		});
		dataGrid.getColumnSortList().push(issueDateColumn);
		dataGrid.addColumn(issueDateColumn, "F. Emisi\u00f3n");
		dataGrid.setColumnWidth(issueDateColumn, 12.5, Unit.PCT);

		
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
		dataGrid.addColumn(deliveryDateColumn, "F. Entrega");
		dataGrid.setColumnWidth(deliveryDateColumn, 12.5, Unit.PCT);
		
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
		dataGrid.addColumn(plateColumn, "Matr\u00edcula");
		dataGrid.setColumnWidth(plateColumn, 15, Unit.PCT);
		
		/** Status Column **/		
		List<HasCell<JsCarrierPacking, ?>> cells = new LinkedList<HasCell<JsCarrierPacking, ?>>();
	    
		cells.add(new ActionHasCell("status", new Delegate<JsCarrierPacking>() {
	        @Override public void execute(JsCarrierPacking object) {}
	    }));
	    
	    cells.add(new ActionHasCell("info", new Delegate<JsCarrierPacking>() {
	    	
	        @Override
	        public void execute(JsCarrierPacking object) {
	        	info(object);
	        }
	    }));
		
		CompositeCell<JsCarrierPacking> cell = new CompositeCell<JsCarrierPacking>(cells);
		
		Column<JsCarrierPacking,JsCarrierPacking> statusColumn = 	new Column<JsCarrierPacking, JsCarrierPacking>(cell){

			@Override
			public JsCarrierPacking getValue(JsCarrierPacking object) {
				return object;
			}
		};
		statusColumn.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		dataGrid.addColumn(statusColumn, "Estado");
		dataGrid.setColumnWidth(statusColumn, 10, Unit.PCT);
	}
	
	private void info(JsCarrierPacking js){
		HashMap<String, LinkedList<String>> selectedFilter = new HashMap<>();
		
		LinkedList<String> list = new LinkedList<>();
		list.add(js.getId() + "");
		selectedFilter.put("carrier_packing", list);
		
		// -------------------- per page
		list = new LinkedList<>();
		list.add("30");
		selectedFilter.put("per_page", list);
		
		// -------------------- page
		list = new LinkedList<>();
		list.add("1");
		selectedFilter.put("page", list);
		Boolean isSc = (js.getType().getId() +"") == "0";
		parent.getAPI().getWarehouse().getOrders(isSc ? "purchase" : "delivery",
				selectedFilter, new AsyncCallback<JSON<JsOrder>>() {
			
			@Override
			public void onSuccess(JSON<JsOrder> result) {

				
				parent.getAPI().getWarehouse().getOrders("income", selectedFilter, new AsyncCallback<JSON<JsOrder>>() {
					
					@Override
					public void onSuccess(JSON<JsOrder> result2) {
						FlexTable grid = new FlexTable();
						grid.setStyleName("aon-panelGrid");
						grid.setWidget(0, 0, new Label(isSc ? "Proveedores" : "Clientes"));

						String registry = result.getData().toLinkedList().size() > 0 ? result.getData().toLinkedList().get(0).getRegistry().getName() : "-";						
						for(Integer i = 1 ; i < result.getData().toLinkedList().size(); i++) {
							registry = registry + "; " + result.getData().toLinkedList().get(i).getRegistry().getName();
						}
						grid.setWidget(0, 1, new Label(registry));
						
						grid.setWidget(1, 0, new Label("Lote"));
						
						if(result2.getData().toLinkedList().size() <= 0 ) {
							grid.setWidget(1, 1, new Label("-"));
						} else {
							String desc = "";
							for(Integer i = 0 ; i < result2.getData().toLinkedList().size(); i++) {
								for(Integer j = 0; j < result2.getData().toLinkedList().get(i).getDetails().toLinkedList().size(); j++) {
									if(i == 0 && j == 0) {
										desc = result2.getData().toLinkedList().get(i).getDetails().get(j).getDescription();
									} else {
										desc = desc + "; " + result2.getData().toLinkedList().get(i).getDetails().get(j).getDescription();
									}
								}
							}
							grid.setWidget(1, 1, new Label(desc));
						}
						for (int i = 0; i < grid.getRowCount(); i++) {
							for (int j = 0; j < grid.getCellCount(i); j++) {
								if ((j % 2) == 0) {
									grid.getCellFormatter().setStyleName(i, j,
											"aon-panelGrid-odd");
								} else {
									grid.getCellFormatter().setStyleName(i, j,
											"aon-panelGrid-even");
								}   
							}
						}	
						
						AonDialog dialog = new AonDialog("Informaci\u00f3n Adicional", grid) {
							
							@Override 
							protected void onCancel() {
								hide();
							}
							
							@Override 
							protected void onAccept() {
								hide();
							}
						};
						dialog.getCancel().setVisible(false);
						dialog.setAutoHideEnabled(true);
						dialog.getElement().getStyle().setWidth(310, Unit.PX);
						dialog.center();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});	
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	private class ActionHasCell implements HasCell<JsCarrierPacking, JsCarrierPacking> {
	    private ActionCell<JsCarrierPacking> cell;
	    String s;
	    
	    public ActionHasCell(String text, Delegate<JsCarrierPacking> delegate) {
	    	s = text;
	        cell = new ActionCell<JsCarrierPacking>(text, delegate){
	        	String text = s;
	        	@Override
	        	public void render(com.google.gwt.cell.client.Cell.Context context,
	        			JsCarrierPacking value, SafeHtmlBuilder sb) {
	        		if(text.equals("status")){	
	        			String icon = "aon-icon-point-red";	
	        			if("En ruta".equals(value.getStatus().getName())) icon = "aon-icon-point-green";
	        			if("Finalizada".equals(value.getStatus().getName())) icon = "aon-icon-point-gray";
	        			sb.appendHtmlConstant("<button  alt=\""+ value.getStatus().getName() +"\" type=\"button\" class=\"aon-editDataTable-button " + icon + "\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");		
	        		}
	        		if(text.equals("info")){
	        			sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-info\" tabindex=\"-1\">");
	        			sb.appendHtmlConstant("</button>");
	        		}
	        	}
	        };
	        
	    }

		@Override
		public JsCarrierPacking getValue(JsCarrierPacking object) {
			return object;
		}


		@Override
		public Cell<JsCarrierPacking> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<JsCarrierPacking, JsCarrierPacking> getFieldUpdater() {
			return null;
		}
	}
}
