package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.aonsolutions.polymer.aon.widget.AonComboBox;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaboration;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaborationDetail;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaborationDetailComposition;
import com.esferalia.aon.gwt.api.client.warehouse.JsWarehouse;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.event.ChangeEvent;
import com.vaadin.polymer.paper.widget.event.ChangeEventHandler;

public class ElaborationSelect extends Composite{
	
	interface Binder extends UiBinder<Widget, ElaborationSelect> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	DockLayoutPanel splitLayoutPanel;
	@UiField
	VerticalPanel westPanel;
	@UiField(provided = true)
	DataGrid<JsElaborationDetailComposition> compositionDataGrid;

	DataGridResources resources = GWT.create(DataGridResources.class);

	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	ListBox detailList;
	Button newDetailButton;
	Button removeDetailButton;
	private MainElaboration parent;
	private API API;
	private JsElaboration jsElaboration;
	
	public ElaborationSelect(MainElaboration elaboration) {
		compositionDataGrid = new DataGrid<JsElaborationDetailComposition>(Integer.MAX_VALUE, resources,
				JsElaborationDetailComposition.PROVIDES_KEY);
		initWidget(binder.createAndBindUi(this));
		this.API = elaboration.API;
		this.parent = elaboration;
		this.jsElaboration = parent.getJsElaboration();
		load();
	}

	public ElaborationSelect(MainElaboration elaboration, JsElaboration jsElaboration) {
		compositionDataGrid = new DataGrid<JsElaborationDetailComposition>(Integer.MAX_VALUE, resources,
				JsElaborationDetailComposition.PROVIDES_KEY);
		initWidget(binder.createAndBindUi(this));
		this.API = elaboration.API;
		this.parent = elaboration;
		this.jsElaboration = jsElaboration;
		load();
	}
	
	private void load() {
		HorizontalPanel panel = new HorizontalPanel();
		newDetailButton = new Button();
		newDetailButton.setText("Nuevo elaborado");
		newDetailButton.setStyleName(AON.AON_CSS.aonIconReset());
		newDetailButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		newDetailButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clickAddDetail();
			}
		});
		panel.add(newDetailButton);
		
		removeDetailButton = new Button();
		removeDetailButton.setText("Borrar seleccionado");
		removeDetailButton.setStyleName(AON.AON_CSS.aonIconDelete());
		removeDetailButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		removeDetailButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		removeDetailButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clickRemoveDetail();
			}
		});
		panel.add(removeDetailButton);
		
		loadSelectable();
		westPanel.add(panel);
		westPanel.add(detailList);
	}
	
	private void clickAddDetail(){
		VerticalPanel panel = new VerticalPanel();
	
		PaperInput inputNumber = new PaperInput();
		PaperInput inputDate = new PaperInput();
		PaperInput inputQuantity = new PaperInput();
		AonComboBox inputWarehouse = new AonComboBox();
		
		inputNumber.setLabel("N\u00FAmero de lote");
		inputNumber.addChangeHandler(new ChangeEventHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				if(inputNumber.getValue()!=null 
						&& !"".equals(inputNumber.getValue())){
					inputDate.setVisible(true);
					inputDate.setFocused(true);
				} else {
					inputDate.setVisible(false);
					inputQuantity.setFocused(true);
				}
			}
		});
		panel.add(inputNumber);
		
		inputDate.setLabel("Fecha de lote");
		inputDate.setValue(getJsElaboration().getDate());
		inputDate.setMaxlength(10);
		inputDate.setVisible(false);
		panel.add(inputDate);

		inputQuantity.setLabel("Cantidad");
		inputQuantity.setValue(getJsElaboration().getQuantity()+"");
		inputQuantity.setMaxlength(10);
		inputQuantity.setRequired(true);
		inputQuantity.setErrorMessage("Valor requerido");;
		panel.add(inputQuantity);
	
		inputWarehouse.setLabel("Almac\u00E9n");
		inputWarehouse.setItemLabelPath("name");
		inputWarehouse.setItemValuePath("name");
		API.getWarehouse().getWarehouses(new AsyncCallback<JSON<JsWarehouse>>() {
			
			@Override
			public void onSuccess(JSON<JsWarehouse> result) {
				inputWarehouse.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		panel.add(inputWarehouse);
		
		AonDialog dialog = new AonDialog("Nuevo elaborado", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {
				DateTimeFormat format = DateTimeFormat.getFormat("yyyy/MM/dd");
				String number = inputNumber.getValue();
//				String date = Utils.formatDateTime(Utils.parseDate(inputDate.getValue()));
				String date = format.parse(inputDate.getValue()).getTime()+"";
				String quantity = inputQuantity.getValue();
				JsWarehouse js = (JsWarehouse) inputWarehouse.getSelectedItem();
				
				String requestData = "{"
						+ (number!=null && !"".equals(number)?"\"number\":\""+ number +"\",":"")
						+ "\"elaboration\":\""+ getJsElaboration().getId() +"\","
						+ "\"date\":\""+ date +"\","
						+ "\"quantity\":\""+ quantity +"\","
						+ "\"warehouse\":\"" + js.getId()+ "\"" 
						+ "}";
				
				API.getWarehouse().insertElaborationDetail(requestData, new AsyncCallback<JsElaborationDetail>() {
					
					@Override
					public void onSuccess(JsElaborationDetail result) {
						selectableItems(null);
						selectDetail(detailList.getItemCount());
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Ha ocurrido algun error al guardar. \n"+caught.getMessage());
					}
				});
				hide();
			}

		};
		dialog.addAutoHidePartner(inputWarehouse.getElementById("overlay"));
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	private void clickRemoveDetail(){		
		String value = detailList.getSelectedValue();
		if(value!=null){
			AonDialog dialog = new AonDialog("Solicitud de confirmaci\u00f3n", new Label("\u00bfBorrar\u003f")) {
				
				@Override protected void onCancel() {hide();}
				
				@Override 
				protected void onAccept() {	
					Integer id = Integer.parseInt(value);
					API.getWarehouse().deleteElaborationDetail(id, null, new AsyncCallback<JsElaborationDetail>() {
						
						@Override
						public void onSuccess(JsElaborationDetail result) {
							hide();
							selectableItems(null);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Ha ocurrido algun error al guardar. \n"+caught.getMessage());
						}
					});
				}
			};
			dialog.setAutoHideEnabled(true);
			dialog.getElement().getStyle().setWidth(310, Unit.PX);
			dialog.center();
		}		
	}
	
	public void refresh(){
		loadSelectable();
		westPanel.remove(1);
		westPanel.add(detailList);
	}
	
	private void loadSelectable() {
		detailList = new ListBox();
		detailList.getElement().getStyle().setBackgroundColor("#f6f5e3");
		detailList.setWidth("300px");
		detailList.setVisibleItemCount(20);
	
		HashMap<String, LinkedList<String>> map = new HashMap<>();

		selectableItems(map);
		detailList.addClickHandler(selectableClickHandler());
		selectedItems(0, true);
	}
	
	private void selectedItems(Integer detailId, Boolean isCreate){
		API.getWarehouse().getElaborationDetailComposition(detailId, new AsyncCallback<JSON<JsElaborationDetailComposition>>() {
			
			@Override
			public void onSuccess(JSON<JsElaborationDetailComposition> result) {
				if(isCreate){
					loadDatagrid(result.getData().toLinkedList());
				} else {
					addDataDisplay(compositionDataGrid, result.getData().toLinkedList());
				}
			}
		
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	private void selectableItems(HashMap<String, LinkedList<String>> map) {
		API.getWarehouse().getElaborationDetail(jsElaboration.getId(),
				new AsyncCallback<JSON<JsElaborationDetail>>() {

					@Override
					public void onSuccess(JSON<JsElaborationDetail> result) {
						detailList.clear();
						result.getData()
								.stream()
								.forEach(
										js -> {
											String serial = js.getItem()
													.getSerialNumber();
											String label = js.getQuantity()
													+ " uds. "
													+ (serial != null ? "(#"
															+ serial + ") "
															: "")
													+ js.getDate();
											detailList.addItem(label,
													js.getId() + "");
										});
						if (result.getData().length() == 1) {
							selectDetail(0);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
					}
				});
	}
	
	private void selectDetail(int idx){
		detailList.setSelectedIndex(idx);
		detailList.fireEvent(new GwtEvent<ClickHandler>() {
			@Override
			public GwtEvent.Type<ClickHandler> getAssociatedType() {
				return ClickEvent.getType();
			}

			@Override
			protected void dispatch(ClickHandler handler) {
				handler.onClick(null);
			}
		});
	}
	
	private ClickHandler selectableClickHandler(){
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String value = detailList.getSelectedValue();
				
				selectedItems(Integer.parseInt(value), false);
				
			}
		};
	}
	
	
	public JsElaboration getJsElaboration() {
		return jsElaboration;
	}

	
	// -------------------- DataGrid Utils
	
	private void loadDatagrid(LinkedList<JsElaborationDetailComposition> list) {		
		compositionDataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		compositionDataGrid.setAutoHeaderRefreshDisabled(true);
		compositionDataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
		addDataDisplay(compositionDataGrid, list);
		ListHandler<JsElaborationDetailComposition> sortHandler = getSortHandler();
		compositionDataGrid.addColumnSortHandler(sortHandler);
 		final SingleSelectionModel<JsElaborationDetailComposition> selectionModel = new SingleSelectionModel<JsElaborationDetailComposition>(
 				JsElaborationDetailComposition.PROVIDES_KEY);
		compositionDataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsElaborationDetailComposition> createCheckboxManager());
		compositionDataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}
	
	public void addItem(JsElaborationDetailComposition js){
		dataProvider.getList().add(js);
		compositionDataGrid.redraw();
	}
	
	private ListDataProvider<JsElaborationDetailComposition> dataProvider = new ListDataProvider<JsElaborationDetailComposition>();

	public void addDataDisplay(HasData<JsElaborationDetailComposition> display, LinkedList<JsElaborationDetailComposition> list) {
		dataProvider = new ListDataProvider<JsElaborationDetailComposition>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsElaborationDetailComposition> getSortHandler() {
		return new ListHandler<JsElaborationDetailComposition>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsElaborationDetailComposition> aux  = super.getList();
				List<JsElaborationDetailComposition> aux2 = new LinkedList<JsElaborationDetailComposition>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private class ActionHasCell implements HasCell<JsElaborationDetailComposition, JsElaborationDetailComposition> {
	    private ActionCell<JsElaborationDetailComposition> cell;
	    String s;
	    
	    public ActionHasCell(String text, Delegate<JsElaborationDetailComposition> delegate) {
	    	s = text;
	        cell = new ActionCell<JsElaborationDetailComposition>(text, delegate){
	        	String text = s;
	        	@Override
	        	public void render(com.google.gwt.cell.client.Cell.Context context,
	        			JsElaborationDetailComposition value, SafeHtmlBuilder sb) {
	        		if(text.equals("send")){ 	
	        			sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-mail\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");
	        		}
	        		
	        		if(text.equals("delete")){
	        			sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-delete\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");
	        		}
	        	}
	        };
	        
	    }

	    @Override
	    public Cell<JsElaborationDetailComposition> getCell() {
	        return cell;
	    }

	    @Override
	    public FieldUpdater<JsElaborationDetailComposition, JsElaborationDetailComposition> getFieldUpdater() {
	        return null;
	    }

	    @Override
	    public JsElaborationDetailComposition getValue(JsElaborationDetailComposition object) {
	        return object;
	    }
	}
	
	
	private void initTableColumns(final SelectionModel<JsElaborationDetailComposition> selectionModel, ListHandler<JsElaborationDetailComposition> sortHandler) {
		NumberFormat numberFormat = NumberFormat.getDecimalFormat().overrideFractionDigits(2);
		
		List<HasCell<JsElaborationDetailComposition, ?>> cells = new LinkedList<HasCell<JsElaborationDetailComposition, ?>>();
		
		cells.add(new ActionHasCell("del", new Delegate<JsElaborationDetailComposition>() {

	        @Override
	        public void execute(JsElaborationDetailComposition object) {
//	        	deleteElaboration(object);
	        }
	    }));
	    
//		cells.add(new ActionHasCell("delete", new Delegate<JsElaborationDetailComposition>() {
//
//	        @Override
//	        public void execute(JsElaborationDetailComposition object) {
//	        	deleteElaboration(object);
//	        }
//	    }));
		
//	    cells.add(new ActionHasCell("send", new Delegate<JsElaborationDetail>() {
//
//	        @Override
//	        public void execute(JsElaborationDetail object) {
//	        	send(object);
//	        }
//	    }));
		
		CompositeCell<JsElaborationDetailComposition> cell = new CompositeCell<JsElaborationDetailComposition>(cells);
		
		/** Warehouse Column **/
		Column<JsElaborationDetailComposition,String> warehouseColumn = new Column<JsElaborationDetailComposition, String>(new TextCell()) {
			
			@Override
			public String getValue(JsElaborationDetailComposition object) {
				return object.getWarehouse()!=null?object.getWarehouse().getName():" ";
			}
		};
		
		warehouseColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		warehouseColumn.setSortable(true); 
		sortHandler.setComparator(warehouseColumn,new Comparator<JsElaborationDetailComposition>() {
			
			@Override
			public int compare(JsElaborationDetailComposition o1, JsElaborationDetailComposition o2) {
				String w1 = o1.getWarehouse()!=null?o1.getWarehouse().getName():"";
				String w2 = o2.getWarehouse()!=null?o2.getWarehouse().getName():"";
				return w1.compareTo(w2);
			}
		});
		
		/** Item Column **/
		Column<JsElaborationDetailComposition,String> itemColumn = new Column<JsElaborationDetailComposition, String>(new TextCell()) {
			
			@Override
			public String getValue(JsElaborationDetailComposition object) {
				return object.getItem() != null ? object.getItem().getName() : "";
			}
		};
		
		itemColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		itemColumn.setSortable(true); 
		sortHandler.setComparator(itemColumn,new Comparator<JsElaborationDetailComposition>() {
			
			@Override
			public int compare(JsElaborationDetailComposition o1, JsElaborationDetailComposition o2) {
				return o1.getItem().getName().compareTo(o2.getItem().getName());
			}
		});

		/** Quantity Column **/
		Column<JsElaborationDetailComposition,Number> quantityColumn = new Column<JsElaborationDetailComposition, Number>(new NumberCell(numberFormat)) {
			
			@Override
			public Number getValue(JsElaborationDetailComposition object) {
				return object.getQuantity()!=null ? object.getQuantity() : null;
			}
		};
		
		quantityColumn.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		quantityColumn.setSortable(true); 
		sortHandler.setComparator(quantityColumn,new Comparator<JsElaborationDetailComposition>() {
			
			@Override
			public int compare(JsElaborationDetailComposition o1, JsElaborationDetailComposition o2) {
				return o1.getQuantity().compareTo(o2.getQuantity());
			}
		});

		/** Action Column **/
		Column<JsElaborationDetailComposition, JsElaborationDetailComposition> actionColumn = new Column<JsElaborationDetailComposition, JsElaborationDetailComposition>(cell){

			@Override
			public JsElaborationDetailComposition getValue(JsElaborationDetailComposition object) {
				return object;
			}
		};
		actionColumn.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);

		
		compositionDataGrid.getColumnSortList().push(warehouseColumn);
		compositionDataGrid.getColumnSortList().push(quantityColumn);
		compositionDataGrid.getColumnSortList().push(itemColumn);
		
		compositionDataGrid.addColumn(warehouseColumn, "Almac\u00e9n");
		compositionDataGrid.addColumn(itemColumn, "Producto");
		compositionDataGrid.addColumn(quantityColumn, AON.MSG.quantity());
		compositionDataGrid.addColumn(actionColumn, "");
		
		compositionDataGrid.setColumnWidth(warehouseColumn, 25, Unit.PCT);
		compositionDataGrid.setColumnWidth(itemColumn, 55, Unit.PCT);
		compositionDataGrid.setColumnWidth(quantityColumn, 15, Unit.PCT);
		compositionDataGrid.setColumnWidth(actionColumn, 5, Unit.PCT);
		
	
	}
}
