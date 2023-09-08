package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.nuevo;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.registry.JsRmedia;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class GridPanel extends ResizeComposite implements RequiresResize {

	interface GridBinder extends UiBinder<Widget, GridPanel> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);
	
	private static final CarrierPackingServiceAsync CARRIER_PACKING_SERVICE;
	static {
		CarrierPackingServiceAsync raw = GWT.create(CarrierPackingService.class);
		CARRIER_PACKING_SERVICE = new CarrierPackingServiceAsyncDecorator(raw); 
	}
	
	DataGridResources resources = GWT.create(DataGridResources.class);
	
	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/DataGrid.css")
		Style dataGridStyle();
	}
		
	@UiField(provided = true) CustomDataGrid<CarrierPacking> dataGrid; 
	
	CarrierPackingEntryPoint parent;
	Integer cont = 0;
	
	public static final ProvidesKey<CarrierPacking> PROVIDES_KEY = new ProvidesKey<CarrierPacking>() {
		@Override
		public Object getKey(CarrierPacking carrierPacking) {
			return carrierPacking == null ? null : carrierPacking.getId();
		}
	};

	public GridPanel(CarrierPackingEntryPoint carrierPacking, List<CarrierPacking> list) {
		this.parent = carrierPacking;		
		dataGrid = new CustomDataGrid<CarrierPacking>(Integer.MAX_VALUE, resources, PROVIDES_KEY);
		
		ScrollPanel scrollPanel = dataGrid.getScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				if(scrollPanel.getVerticalScrollPosition() >= scrollPanel.getMaximumVerticalScrollPosition()){
					Integer page = parent.getParams().getPage() + 1;
					parent.getParams().setPage(page);

					CARRIER_PACKING_SERVICE.getCarrierPackingList(parent.getOccam(), parent.getParams(), new AsyncCallback<List<CarrierPacking>>() {
						
						@Override
						public void onSuccess(List<CarrierPacking> result) {
							dataProvider.getList().addAll(result);
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
	
	private void load(List<CarrierPacking> list) {
		DefaultKeyboardSelectionHandler<CarrierPacking> selHandler = new DefaultKeyboardSelectionHandler<CarrierPacking>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<CarrierPacking> event) {
				if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					if(event.getColumn() != 5 && event.getColumn() != 1) {
						Integer relRow = event.getIndex() - dataGrid.getPageStart();
						Integer subrow = event.getContext().getSubIndex();
						dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
						CarrierPacking object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());				    
						parent.getParams().setPage(1);
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
		ListHandler<CarrierPacking> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
 		final SingleSelectionModel<CarrierPacking> selectionModel = new SingleSelectionModel<CarrierPacking>(PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<CarrierPacking> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<CarrierPacking> dataProvider = new ListDataProvider<CarrierPacking>();

	public void addDataDisplay(HasData<CarrierPacking> display, List<CarrierPacking> list) {
		dataProvider = new ListDataProvider<CarrierPacking>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<CarrierPacking> getSortHandler() {
		return new ListHandler<CarrierPacking>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<CarrierPacking> aux  = super.getList();
				List<CarrierPacking> aux2 = new LinkedList<CarrierPacking>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initTableColumns(final SelectionModel<CarrierPacking> selectionModel, ListHandler<CarrierPacking> sortHandler) {
		/** Type Column **/
		Column<CarrierPacking,String> typeColumn = new Column<CarrierPacking, String>(new TextCell()) {
			
			@Override
			public String getValue(CarrierPacking object) {
				String type = object.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName()) ? "SC" : "HR";
				String ref = object.getSeries() + "/" + object.getNumber();
				return type + "-" + ref;
			}
		};
		
		typeColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		typeColumn.setSortable(true); 
		sortHandler.setComparator(typeColumn,new Comparator<CarrierPacking>() {
			
			@Override
			public int compare(CarrierPacking o1, CarrierPacking o2) {
				String type1 = o1.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName()) ? "SC" : "HR";
				String ref1 = o1.getSeries() + "/" + o1.getNumber();
				String r1 = type1 + "-" + ref1;
				String type2 = o2.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName()) ? "SC" : "HR";
				String ref2 = o2.getSeries() + "/" + o2.getNumber();
				String r2 = type2 + "-" + ref2;
				return r1.compareTo(r2);
			}
		});
		dataGrid.getColumnSortList().push(typeColumn);
		dataGrid.addColumn(typeColumn, AON.MSG.type());
		dataGrid.setColumnWidth(typeColumn, 15, Unit.PCT);
		
		/** CUSTOMER/SUPPLIER **/
		List<HasCell<CarrierPacking, ?>> cellsReg = new LinkedList<HasCell<CarrierPacking, ?>>();
	    
		cellsReg.add(new ActionHasCell("regInfo", new Delegate<CarrierPacking>() {
	    	
	        @Override
	        public void execute(CarrierPacking object) {
	        	regInfo(object);
	        }
	    }));
		
		CompositeCell<CarrierPacking> cellReg = new CompositeCell<CarrierPacking>(cellsReg);
		
		Column<CarrierPacking,CarrierPacking> regColumn = 	new Column<CarrierPacking, CarrierPacking>(cellReg){

			@Override
			public CarrierPacking getValue(CarrierPacking object) {
				return object;
			}
		};		
		regColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		regColumn.setSortable(false); 
//		sortHandler.setComparator(regColumn,new Comparator<CarrierPacking>() {
//			
//			@Override
//			public int compare(CarrierPacking o1, CarrierPacking o2) {
//				String s1 = o1.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName()) ? o1.getSupplier() : o1.getCustomer();
//				String s2 = o2.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName()) ? o2.getSupplier() : o2.getCustomer();
//				return s1.compareTo(s2);
//			}
//		});
		dataGrid.getColumnSortList().push(regColumn);
		dataGrid.addColumn(regColumn, "Cliente" + "/" + "Proveedor");
		dataGrid.setColumnWidth(regColumn, 30, Unit.PCT);

		/** S/RefColumn **/
		Column<CarrierPacking, String> referenceColumn = new Column<CarrierPacking, String>(new TextCell()) {

			@Override
			public String getValue(CarrierPacking object) {
				return object.getCarrierReference();
			}
		
		};
		referenceColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		referenceColumn.setSortable(true); 
		sortHandler.setComparator(referenceColumn,new Comparator<CarrierPacking>() {
			
			@Override
			public int compare(CarrierPacking o1, CarrierPacking o2) {
				return o1.getCarrierReference().compareTo(o2.getCarrierReference());
			}
		});
		dataGrid.getColumnSortList().push(referenceColumn);
		dataGrid.addColumn(referenceColumn, "Referencia");
		dataGrid.setColumnWidth(referenceColumn, 20, Unit.PCT);
		
		/** ISSUE DATE Column **/
		Column<CarrierPacking,String> issueDateColumn = new Column<CarrierPacking, String>(new TextCell()) {
			
			@Override
			public String getValue(CarrierPacking object) {
				return object.getIssueDate() != null ? AonDateUtils.formatDate(object.getIssueDate()) : "";
			}
		};

		issueDateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		issueDateColumn.setSortable(true); 
		sortHandler.setComparator(issueDateColumn,new Comparator<CarrierPacking>() {
			
			@Override
			public int compare(CarrierPacking o1, CarrierPacking o2) {
				return o1.getIssueDate().compareTo(o2.getIssueDate());
			}
		});
		dataGrid.getColumnSortList().push(issueDateColumn);
		dataGrid.addColumn(issueDateColumn, "F. Carga");
		dataGrid.setColumnWidth(issueDateColumn, 12.5, Unit.PCT);

		
		/** delivery DATE Column **/
		Column<CarrierPacking,String> deliveryDateColumn = new Column<CarrierPacking, String>(new TextCell()) {
			
			@Override
			public String getValue(CarrierPacking object) {
				return object.getDeliveryDate() != null ? AonDateUtils.formatDate(object.getDeliveryDate()) : "";
			}
		};
		
		deliveryDateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		deliveryDateColumn.setSortable(true); 
		sortHandler.setComparator(deliveryDateColumn,new Comparator<CarrierPacking>() {
			
			@Override
			public int compare(CarrierPacking o1, CarrierPacking o2) {
				return o1.getDeliveryDate().compareTo(o2.getDeliveryDate());
			}
		});
		dataGrid.getColumnSortList().push(deliveryDateColumn);
		dataGrid.addColumn(deliveryDateColumn, "F. Entrega");
		dataGrid.setColumnWidth(deliveryDateColumn, 12.5, Unit.PCT);
		
		/** CARRIER Column **/
		List<HasCell<CarrierPacking, ?>> cellsCarrier = new LinkedList<HasCell<CarrierPacking, ?>>();
	    
		cellsCarrier.add(new ActionHasCell("info", new Delegate<CarrierPacking>() {
	    	
	        @Override
	        public void execute(CarrierPacking object) {
	        	carrierInfo(object);
	        }
	    }));
		
		CompositeCell<CarrierPacking> cellCarrier = new CompositeCell<CarrierPacking>(cellsCarrier);
		
		Column<CarrierPacking,CarrierPacking> carrierColumn = 	new Column<CarrierPacking, CarrierPacking>(cellCarrier){

			@Override
			public CarrierPacking getValue(CarrierPacking object) {
				return object;
			}
		};		
		carrierColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		carrierColumn.setSortable(true); 
		sortHandler.setComparator(carrierColumn,new Comparator<CarrierPacking>() {
			
			@Override
			public int compare(CarrierPacking o1, CarrierPacking o2) {
				return o1.getCarrierName().compareTo(o2.getCarrierName());
			}
		});
		dataGrid.getColumnSortList().push(carrierColumn);
		dataGrid.addColumn(carrierColumn, AON.MSG.carrier());
		dataGrid.setColumnWidth(carrierColumn, 30, Unit.PCT);

		
		/** Matricula Column **/
		Column<CarrierPacking,String> plateColumn = new Column<CarrierPacking, String>(new TextCell()) {
			
			@Override
			public String getValue(CarrierPacking object) {
				return object.getNumberPlate() != null ? object.getNumberPlate() : "";
			}
		};
		
		plateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		plateColumn.setSortable(true); 
		sortHandler.setComparator(plateColumn,new Comparator<CarrierPacking>() {
			
			@Override
			public int compare(CarrierPacking o1, CarrierPacking o2) {
				return o1.getNumberPlate().compareTo(o2.getNumberPlate());
			}
		});
		dataGrid.getColumnSortList().push(plateColumn);
		dataGrid.addColumn(plateColumn, "Matr\u00edcula");
		dataGrid.setColumnWidth(plateColumn, 15, Unit.PCT);
		
		/** Status Column **/		
		List<HasCell<CarrierPacking, ?>> cells = new LinkedList<HasCell<CarrierPacking, ?>>();
	    
		cells.add(new ActionHasCell("status", new Delegate<CarrierPacking>() {
	        @Override public void execute(CarrierPacking object) {}
	    }));
	    
	   
		
		CompositeCell<CarrierPacking> cell = new CompositeCell<CarrierPacking>(cells);
		
		Column<CarrierPacking,CarrierPacking> statusColumn = 	new Column<CarrierPacking, CarrierPacking>(cell){

			@Override
			public CarrierPacking getValue(CarrierPacking object) {
				return object;
			}
		};
		statusColumn.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		dataGrid.addColumn(statusColumn, "Estado");
		dataGrid.setColumnWidth(statusColumn, 10, Unit.PCT);
	}
	
	private void carrierInfo(CarrierPacking js){
		String registry = js.getCarrier() + "";
		parent.getAPI().getIncidence().getEnterpriseRmediaList(Integer.parseInt(registry), new AsyncCallback<JSON<JsRmedia>>() {
			
			@Override
			public void onSuccess(JSON<JsRmedia> result) {
				FlexTable grid = new FlexTable();
				grid.setStyleName("aon-panelGrid");

				LinkedList<JsRmedia> r = result.getData().toLinkedList();
				for(Integer i=0; i < r.size(); i++) {
					String m = r.get(i).getMedia() + "";
					grid.setWidget(i, 0, new Label(getMediaName(Integer.parseInt(m))));
					grid.setWidget(i, 1, new Label(r.get(i).getValue()));
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
			
			@Override
			public void onFailure(Throwable caught) {
			
			}
		});
	
	}

	private void regInfo(CarrierPacking js){
		VerticalPanel vp = new  VerticalPanel();
		
// 	TODO
//		js.getCustomerArray().stream().forEach(customer -> regInfo2(vp, customer));
//		js.getSupplierArray().stream().forEach(supplier -> regInfo2(vp, supplier));
				
		AonDialog dialog = new AonDialog("Informaci\u00f3n Adicional", vp) {
			
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
	
	private void regInfo2(VerticalPanel vp, JsObject o) {
		String key = o.getId() + "";

		parent.getAPI().getIncidence().getEnterpriseRmediaList(Integer.parseInt(key), new AsyncCallback<JSON<JsRmedia>>() {
			
			@Override
			public void onSuccess(JSON<JsRmedia> result) {
				parent.getAPI().getIncidence().getEnterpriseRaddressList(Integer.parseInt(key), new AsyncCallback<JSON<JsObject>>() {
					@Override
					public void onSuccess(JSON<JsObject> res) {
						
						Label lbl = new Label(o.getName());
						lbl.addStyleName(AON.AON_CSS.aonBold());
						lbl.addStyleName(AON.AON_CSS.aonPadding());
						vp.add(lbl);
						FlexTable grid = new FlexTable();
						grid.setStyleName("aon-panelGrid");
						
						if(res.getData().length() > 0) {
							grid.setWidget(0, 0, new Label("Direcci\u00f3n"));
							grid.setWidget(0, 1, new Label(res.getData().get(0).getName()));
						}
						LinkedList<JsRmedia> r = result.getData().toLinkedList();
						
						for(Integer i=1; i < r.size(); i++) {
							String m = r.get(i).getMedia() + "";
							grid.setWidget(i, 0, new Label(getMediaName(Integer.parseInt(m))));
							grid.setWidget(i, 1, new Label(r.get(i).getValue()));
						}
						vp.add(grid);
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
					}
					
					@Override public void onFailure(Throwable caught) {}
				});

			}
			
			@Override
			public void onFailure(Throwable caught) {
			
			}
		});
		
		
	}

	private String getMediaName(Integer media) {
		if(MediaType.EMAIL.equals(MediaType.values()[media])){
			return "Email";
		} else if(MediaType.CELLULAR.equals(MediaType.values()[media])){
			return "M\u00f3vil";
		} else if(MediaType.FAX.equals(MediaType.values()[media])){
			return "Fax";
		} else if(MediaType.FIXED_PHONE.equals(MediaType.values()[media])){
			return "Tel\u00e9fono";
		} else if(MediaType.WEB.equals(MediaType.values()[media])){
			return "Web";
		} else return "desconocido";
	}
	
	private class ActionHasCell implements HasCell<CarrierPacking, CarrierPacking> {
	    private ActionCell<CarrierPacking> cell;
	    String s;
	    
	    public ActionHasCell(String text, Delegate<CarrierPacking> delegate) {
	    	s = text;
	        cell = new ActionCell<CarrierPacking>(text, delegate){
	        	String text = s;
	        	@Override
	        	public void render(com.google.gwt.cell.client.Cell.Context context,
	        			CarrierPacking value, SafeHtmlBuilder sb) {
	        		if(text.equals("status")){	
	        			String icon = "aon-icon-point-red";	
	        			if(CarrierPackingStatus.ON_ROUTE.getName().equals(value.getStatus().getName())) icon = "aon-icon-point-orange";
	        			if(CarrierPackingStatus.FINISHED.getName().equals(value.getStatus().getName())) icon = "aon-icon-point-gray";
	        			if(CarrierPackingStatus.ON_BASCULA.getName().equals(value.getStatus().getName())) icon = "aon-icon-point-green";
	        			sb.appendHtmlConstant("<button  alt=\""+ value.getStatus().getName() +"\" type=\"button\" class=\"aon-editDataTable-button " + icon + "\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");		
	        		}
	        		if(text.equals("info")){
	        			sb.appendHtmlConstant(value.getCarrierName() + "<button type=\"button\" class=\"aon-editDataTable-button aon-icon-info\" tabindex=\"-1\" style=\"margin-left: 5px;position: absolute;\">");
	        			sb.appendHtmlConstant("</button>");
	        		}
	        		if(text.equals("regInfo")){
	        			// TODO
	        			String v = ""; // value.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName()) ? value.getSupplier() : value.getCustomer();
	        			if("-".equals(v)) {
	        				sb.appendHtmlConstant(v);
	        			}else {
	        				sb.appendHtmlConstant(v + "<button type=\"button\" class=\"aon-editDataTable-button aon-icon-info\" tabindex=\"-1\" style=\"margin-left: 5px;position: absolute;\">");
	        				sb.appendHtmlConstant("</button>");
	        			}
	        		}
	        	}
	        };
	        
	    }

		@Override
		public CarrierPacking getValue(CarrierPacking object) {
			return object;
		}


		@Override
		public Cell<CarrierPacking> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<CarrierPacking, CarrierPacking> getFieldUpdater() {
			return null;
		}
	}
}
