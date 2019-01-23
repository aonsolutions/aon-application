package net.aonsolutions.aon.gwt.commercial.client.commission;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.commercial.JsCommission;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.occam.api.model.commission.OfferDetailCommissionStatus;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
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

import net.aonsolutions.aon.gwt.commercial.client.AonDialog;

public class CommissionCalculateGrid extends ResizeComposite implements RequiresResize {

	interface GridBinder extends UiBinder<Widget, CommissionCalculateGrid> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);
	
	DataGridResources resources = GWT.create(DataGridResources.class);
	
	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	} 
	
	@UiField(provided = true) CustomDataGrid<JsCommission> dataGrid; 
	
	CommissionCalculatePrincipal parent;
	Integer cont = 0;

	public CommissionCalculateGrid(CommissionCalculatePrincipal parent, LinkedList<JsCommission> list) {

		this.parent = parent;
		dataGrid = new CustomDataGrid<JsCommission>(Integer.MAX_VALUE, resources, JsCommission.PROVIDES_KEY);
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
					parent.getAPI().getCommission().getInvoiceCalculatedCommission(parent.getFilterMap(), new AsyncCallback<JSON<JsCommission>>() {
						
						@Override
						public void onSuccess(JSON<JsCommission> result) {
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
	
	private void load(LinkedList<JsCommission> list) {
		DefaultKeyboardSelectionHandler<JsCommission> selHandler = new DefaultKeyboardSelectionHandler<JsCommission>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsCommission> event) {
				
			}
		};
		
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
		addDataDisplay(dataGrid, list);
		ListHandler<JsCommission> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
 		final SingleSelectionModel<JsCommission> selectionModel = new SingleSelectionModel<JsCommission>(
 				JsCommission.PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsCommission> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<JsCommission> dataProvider = new ListDataProvider<JsCommission>();

	public void addDataDisplay(HasData<JsCommission> display, LinkedList<JsCommission> list) {
		dataProvider = new ListDataProvider<JsCommission>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsCommission> getSortHandler() {
		return new ListHandler<JsCommission>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsCommission> aux  = super.getList();
				List<JsCommission> aux2 = new LinkedList<JsCommission>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initTableColumns(final SelectionModel<JsCommission> selectionModel, ListHandler<JsCommission> sortHandler) {

		/** COMERCIAL / COMMERCIAL **/
		Column<JsCommission, String> commercialColumn = new Column<JsCommission, String>(new TextCell()) {

			@Override
			public String getValue(JsCommission object) {
				return object.getSeller();
			}
		
		};
		commercialColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		commercialColumn.setSortable(true); 
		sortHandler.setComparator(commercialColumn,new Comparator<JsCommission>() {
			
			@Override
			public int compare(JsCommission o1, JsCommission o2) {
				return o1.getSeller().compareTo(o2.getSeller());
			}
		});
		dataGrid.getColumnSortList().push(commercialColumn);
		dataGrid.addColumn(commercialColumn, "COMERCIAL");
		dataGrid.setColumnWidth(commercialColumn, 35, Unit.PCT);
		
		/** FECHA / DATE **/
		Column<JsCommission, String> dateColumn = new Column<JsCommission, String>(new TextCell()) {

			@Override
			public String getValue(JsCommission object) {
				return object.getDate();
			}
		
		};
		dateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		dateColumn.setSortable(true); 
		sortHandler.setComparator(dateColumn,new Comparator<JsCommission>() {
			
			@Override
			public int compare(JsCommission o1, JsCommission o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});
		dataGrid.getColumnSortList().push(dateColumn);
		dataGrid.addColumn(dateColumn, "FECHA");
		dataGrid.setColumnWidth(dateColumn, 20, Unit.PCT);

		
		/** FACTURA / INVOICE **/
		
		List<HasCell<JsCommission, ?>> cellsInvoice = new LinkedList<HasCell<JsCommission, ?>>();
	    
		cellsInvoice.add(new ActionHasCell("info", new Delegate<JsCommission>() {
	    	
	        @Override
	        public void execute(JsCommission object) {
	        	invoiceInfo(object);
	        }
	    }));
		
		CompositeCell<JsCommission> cellInvoice = new CompositeCell<JsCommission>(cellsInvoice);
		
		Column<JsCommission,JsCommission> invoiceColumn = 	new Column<JsCommission, JsCommission>(cellInvoice){

			@Override
			public JsCommission getValue(JsCommission object) {
				return object;
			}
		};		
		invoiceColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		invoiceColumn.setSortable(true); 
		sortHandler.setComparator(invoiceColumn,new Comparator<JsCommission>() {
			
			@Override
			public int compare(JsCommission o1, JsCommission o2) {
				return o1.getDescription().compareTo(o2.getDescription());
			}
		});
		dataGrid.getColumnSortList().push(invoiceColumn);
		dataGrid.addColumn(invoiceColumn, "FACTURA");
		dataGrid.setColumnWidth(invoiceColumn, 25, Unit.PCT);
		
		/** PRODUCTO / PRODUCT **/
		Column<JsCommission, String> productColumn = new Column<JsCommission, String>(new TextCell()) {

			@Override
			public String getValue(JsCommission object) {
				return object.getProduct();
			}
		
		};
		productColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		productColumn.setSortable(true); 
		sortHandler.setComparator(productColumn,new Comparator<JsCommission>() {
			
			@Override
			public int compare(JsCommission o1, JsCommission o2) {
				return o1.getProduct().compareTo(o2.getProduct());
			}
		});
		dataGrid.getColumnSortList().push(productColumn);
		dataGrid.addColumn(productColumn, "PRODUCTO");
		dataGrid.setColumnWidth(productColumn, 35, Unit.PCT);
		
		/** BASE IMPONIBLE / TAXABLE BASE **/
		Column<JsCommission, String> baseColumn = new Column<JsCommission, String>(new TextCell()) {

			@Override
			public String getValue(JsCommission object) {
				return object.getBase() + "";
			}
		
		};
		baseColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		baseColumn.setSortable(true); 
		sortHandler.setComparator(baseColumn,new Comparator<JsCommission>() {
			
			@Override
			public int compare(JsCommission o1, JsCommission o2) {
				return o1.getBase().compareTo(o2.getBase());
			}
		});
		dataGrid.getColumnSortList().push(baseColumn);
		dataGrid.addColumn(baseColumn, "BASE");
		dataGrid.setColumnWidth(baseColumn, 15, Unit.PCT);
		
		/** PORCENTAJE / PERCENTAGE **/
		Column<JsCommission, String> percentageColumn = new Column<JsCommission, String>(new TextInputCell()) {
			
			@Override
			public void render(Context context, JsCommission object, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<input type=\"text\" value=\""+ object.getPercentage()+ "\" class=\"aon-inputText\" style=\"width:45px\"></input>");
			}
			
			@Override
			public String getValue(JsCommission object) {
				return object.getPercentage() + "";
			}
		};

		percentageColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		percentageColumn.setSortable(true); 
		sortHandler.setComparator(percentageColumn,new Comparator<JsCommission>() {
			
			@Override
			public int compare(JsCommission o1, JsCommission o2) {
				return o1.getPercentage().compareTo(o2.getPercentage());
			}
		});
		dataGrid.getColumnSortList().push(percentageColumn);
		dataGrid.addColumn(percentageColumn, "% COM.");
		percentageColumn.setFieldUpdater(new FieldUpdater<JsCommission, String>() {
	          @Override
	          public void update(int index, JsCommission object, String value) {
	        	  String requestData = "{\"id\":" + object.getId() 
	        	  					+ ",\"percentage\":"+value +"}";
	        	  if(parent.isOffer()) {
	        		  parent.getAPI().getCommission().updateOfferCommissionCalculate(requestData);
	        	  } else {
	        		  parent.getAPI().getCommission().updateInvoiceCommissionCalculate(requestData);
	        	  }
	          }
	    });
		dataGrid.setColumnWidth(percentageColumn, 15, Unit.PCT);
		
	    
		/** IMPORTE / AMOUNT **/
		Column<JsCommission, String> amountColumn = new Column<JsCommission, String>(new TextInputCell()) {
			
			@Override
			public void render(Context context, JsCommission object, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<input type=\"text\" value=\""+ object.getAmount()+ "\" class=\"aon-inputText\" style=\"width:50px\" size=\"{2}\"></input>");
			}
			
			@Override
			public String getValue(JsCommission object) {
				return object.getAmount() + "";
			}
		};
		amountColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		amountColumn.setSortable(true);
		sortHandler.setComparator(amountColumn,new Comparator<JsCommission>() {
			
			@Override
			public int compare(JsCommission o1, JsCommission o2) {
				return o1.getAmount().compareTo(o2.getAmount());
			}
		});
		dataGrid.getColumnSortList().push(amountColumn);
		dataGrid.addColumn(amountColumn, "COM.");

		amountColumn.setFieldUpdater(new FieldUpdater<JsCommission, String>() {
	          @Override
	          public void update(int index, JsCommission object, String value) {
	        	  String requestData = "{\"id\":" + object.getId() 
					+ ",\"amount\":"+value +"}";
	        	  if(parent.isOffer()) {
	        		  parent.getAPI().getCommission().updateOfferCommissionCalculate(requestData);
	        	  } else {
	        		  parent.getAPI().getCommission().updateInvoiceCommissionCalculate(requestData);
	        	  }
	          }
	    });
		dataGrid.setColumnWidth(amountColumn, 15, Unit.PCT);
		
		/** ESTADO / STATUS **/
		
	    List<String> statusList = new ArrayList<String>();
	    for (OfferDetailCommissionStatus status : OfferDetailCommissionStatus.values()) {
	    	statusList.add(status.getName());
	    }
		Column<JsCommission, String> statusColumn = new Column<JsCommission, String>(new SelectionCell(statusList)) {

			@Override
			public String getValue(JsCommission object) {
				return object.getStatus();
			}
		
		};
		statusColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		statusColumn.setSortable(true);
		sortHandler.setComparator(statusColumn,new Comparator<JsCommission>() {
			
			@Override
			public int compare(JsCommission o1, JsCommission o2) {
				return o1.getStatus().compareTo(o2.getStatus());
			}
		});
		dataGrid.getColumnSortList().push(statusColumn);
		dataGrid.addColumn(statusColumn, "ESTADO");
		statusColumn.setFieldUpdater(new FieldUpdater<JsCommission, String>() {
	      @Override
	      public void update(int index, JsCommission object, String value) {
	        for (OfferDetailCommissionStatus status : OfferDetailCommissionStatus.values()) {
	          if (status.getName().equals(value)) {
	        	  String requestData = "{\"id\":" + object.getId() 
					+ ",\"status\":"+ status.value()+"}";
	        	  if(parent.isOffer()) {
	        		  parent.getAPI().getCommission().updateOfferCommissionCalculate(requestData);
	        	  } else {
	        		  parent.getAPI().getCommission().updateInvoiceCommissionCalculate(requestData);
	        	  } 
	          }
	        }
	      }
	    });
		dataGrid.setColumnWidth(statusColumn, 25, Unit.PCT);
		
		/** Delete Column **/
		List<HasCell<JsCommission, ?>> cellsDelete = new LinkedList<HasCell<JsCommission, ?>>();
	    
		cellsDelete.add(new ActionHasCell("delete", new Delegate<JsCommission>() {
	    	
	        @Override
	        public void execute(JsCommission object) {
	        	deleteCommision(object);
	        }
	    }));
		
		CompositeCell<JsCommission> cellDelete = new CompositeCell<JsCommission>(cellsDelete);
		
		Column<JsCommission,JsCommission> deleteColumn = 	new Column<JsCommission, JsCommission>(cellDelete){

			@Override
			public JsCommission getValue(JsCommission object) {
				return object;
			}
		};		
		deleteColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		dataGrid.addColumn(deleteColumn, "");
		dataGrid.setColumnWidth(deleteColumn, 10, Unit.PCT);
	}
	
	private void deleteCommision(JsCommission commission){
		
		AonDialog dialog = new AonDialog("Informaci\u00f3n Factura", new Label("Est\u00e1s seguro de eliminar la comisi\u00f3n seleccionada.")) {
					
			@Override 
			protected void onCancel() {
				hide();
			}
				
			@Override 
			protected void onAccept() {
				JSONObject json = new JSONObject();
				json.put("id", new JSONString(commission.getId() + ""));
				if(parent.isOffer()) {
					parent.getAPI().getCommission().deleteOfferCommission(JsonUtils.stringify(json.getJavaScriptObject()), new AsyncCallback<JSON<JsCommission>>() {
					
						@Override
						public void onSuccess(JSON<JsCommission> result) {
							parent.gridContent();
							hide();
						}
					
						@Override
						public void onFailure(Throwable caught) {
							hide();
						}
					});
				} else if(parent.isInvoice()) {
					parent.getAPI().getCommission().deleteInvoiceCommission(JsonUtils.stringify(json.getJavaScriptObject()), new AsyncCallback<JSON<JsCommission>>() {
						
						@Override
						public void onSuccess(JSON<JsCommission> result) {
							parent.gridContent();
							hide();
						}
					
						@Override
						public void onFailure(Throwable caught) {
							hide();
						}
					});
				}
			}
		};

		dialog.getAccept().setVisible(true);
		dialog.getCancel().setVisible(false);
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	private class ActionHasCell implements HasCell<JsCommission, JsCommission> {
	    private ActionCell<JsCommission> cell;
	    String s;
	    
	    public ActionHasCell(String text, Delegate<JsCommission> delegate) {
	    	s = text;
	        cell = new ActionCell<JsCommission>(text, delegate){
	        	String text = s;
	        	@Override
	        	public void render(com.google.gwt.cell.client.Cell.Context context,
	        			JsCommission value, SafeHtmlBuilder sb) {
	        		if("info".equals(text)) {
	        			String v = value.getDescription();
	        			sb.appendHtmlConstant(v + "<button type=\"button\" class=\"aon-editDataTable-button aon-icon-info\" tabindex=\"-1\" style=\"margin-left: 5px;position: absolute;\">");
	        			sb.appendHtmlConstant("</button>");        		
	        		} else if("delete".equals(text)) {
	        			sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-delete\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");
	        		}
	        	}
	        };
	        
	    }

		@Override
		public JsCommission getValue(JsCommission object) {
			return object;
		}


		@Override
		public Cell<JsCommission> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<JsCommission, JsCommission> getFieldUpdater() {
			return null;
		}
	}
	
	private void invoiceInfo(JsCommission js){
		FlexTable grid = new FlexTable();
		grid.setStyleName("aon-panelGrid");

		
		grid.setWidget(0, 0, new Label("Cliente"));
		grid.setWidget(0, 1, new Label(js.getInvoice().getRegistryName()));

		for (int i = 0; i < grid.getRowCount(); i++) {
			for (int j = 0; j < grid.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					grid.getCellFormatter().setStyleName(i, j, "aon-panelGrid-odd");
				} else {
					grid.getCellFormatter().setStyleName(i, j, "aon-panelGrid-even");
				}   
			}
		}	

		AonDialog dialog = new AonDialog("Informaci\u00f3n Factura", grid) {
					
			@Override 
			protected void onCancel() {
				hide();
			}
				
			@Override 
			protected void onAccept() {
				hide();
			}
		};

		dialog.getAccept().setVisible(false);
		dialog.getCancel().setVisible(false);
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}

}
