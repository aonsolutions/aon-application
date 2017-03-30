package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.aonsolutions.aon.gwt.warehouse.client.Utils;
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
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.shared.GwtEvent;
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
	private MainElaboration parent;
	private API API;
	private JsElaboration jsElaboration;
//	private JsElaborationDetail jsElaborationDetail;
	
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
//		Label label = new Label("Fecha ");
//		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
//		label.getElement().getStyle().setPadding(3, Unit.PX);
//		p.add(label);
//		datebox = new DateBoxEx();
//		datebox.setStyleName(AON.AON_CSS.aonTextBox());
//		datebox.setValue(new Date());
//		datebox.addValueChangeHandler(new ValueChangeHandler<Date>() {
//			
//			@Override
//			public void onValueChange(ValueChangeEvent<Date> event) {
//				loadSelectable();
//				westPanel.remove(1);
//				westPanel.add(selectable);
//			}
//		});
//		p.add(datebox);
		
//		Label label = new Label("Finalizados ");
//		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
//		label.getElement().getStyle().setPadding(3, Unit.PX);
//		panel.add(label);
		newDetailButton = new Button();
		newDetailButton.setAccessKey( 'L' );
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
		
		loadSelectable();
		loadSelected(true);
		westPanel.add(panel);
		westPanel.add(detailList);
	}
	
	private void clickAddDetail(){
		VerticalPanel panel = new VerticalPanel();
	
		PaperInput inputNumber = new PaperInput();
		inputNumber.setLabel("Numero");
		panel.add(inputNumber);
		
		PaperInput inputDate = new PaperInput();
		inputDate.setLabel("Fecha");
		inputDate.setValue(getJsElaboration().getDate());
		inputDate.setMaxlength(10);
		panel.add(inputDate);

		PaperInput inputQuantity = new PaperInput();
		inputQuantity.setLabel("Cantidad");
		inputQuantity.setValue(getJsElaboration().getQuantity()+"");
		inputQuantity.setMaxlength(10);
		panel.add(inputQuantity);
	
		AonComboBox inputWarehouse = new AonComboBox();
		inputWarehouse.setLabel("Almacen");
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
				Window.alert("before accept");
				Window.alert("before input");
				String number = inputNumber.getValue();
				Window.alert("before date");
//				String date = Utils.formatDateTime(Utils.parseDate(inputDate.getValue()));
				String date = Utils.parseDate(inputDate.getValue()).getTime()+"";
				Window.alert("before quantity");
				String quantity = inputQuantity.getValue();
				Window.alert("before warehouse");
				JsWarehouse js = (JsWarehouse) inputWarehouse.getSelectedItem();
				
				String requestData = "{\"serial_number\":\""+ number +"\","
						+ "\"elaboration\":\""+ getJsElaboration().getId() +"\","
						+ "\"date\":\""+ date +"\","
						+ "\"quantity\":\""+ quantity +"\","
						+ "\"workplace\":\"" + js.getWorkplace() + "\"" + "}";
				Window.alert("before insert");
				API.getWarehouse().insertElaborationDetail(requestData, new AsyncCallback<JsElaborationDetail>() {
					
					@Override
					public void onSuccess(JsElaborationDetail result) {
						Window.alert("before success");
//						selectedIncome = result.getId();
//						VerticalPanel vp = (VerticalPanel)receivePanel.getWidget();
						
//						PaperItem pincome = buildIncome(result);
//						SimplePanel sp = new SimplePanel();
////						Window.alert(result.getId() + ", " + selectedIncome + " -> " + (result.getId() == selectedIncome));
//						sp.setVisible(result.getId() == selectedIncome);
//						buildIncomeDetail(sp, result);
//						pincome.addClickHandler(new ClickHandler() {
//		        			
//		        			@Override
//		        			public void onClick(ClickEvent arg0) {
//		        				sp.setVisible(!sp.isVisible());
//		        			}
//		        		});
//						vp.insert(pincome, 0);
//						vp.insert(sp, 1);
						selectableItems(null);
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
	
	public void refresh(){
		loadSelectable();
		westPanel.remove(1);
		westPanel.add(detailList);
		loadSelected(false);
	}
	
	private void loadSelectable() {
		detailList = new ListBox();
		detailList.getElement().getStyle().setBackgroundColor("#f6f5e3");
		detailList.setWidth("300px");
		detailList.setVisibleItemCount(20);
	
		HashMap<String, LinkedList<String>> map = new HashMap<>();
//		LinkedList<String> carrierList = new LinkedList<>();
//		carrierList.add(jsCarrierPacking.getCarrier().getId() + "");
//		map.put("carrier", carrierList);
//		LinkedList<String> ncarrierList = new LinkedList<>();
//		ncarrierList.add("");
//		map.put("not_carrier_packing", ncarrierList);
//		LinkedList<String> nDate = new LinkedList<>();
//		nDate.add(Long.toString(datebox.getValue().getTime()));
//		map.put("date", nDate);	
		selectableItems(map);
		detailList.addClickHandler(selectableClickHandler());
		detailList.addDoubleClickHandler(selectableDoubleClickHandler());
		selectedItems(0, true);
	}
	
	private void loadSelected(Boolean isCreate) {
//		HashMap<String, LinkedList<String>> map2 = new HashMap<>();
//		LinkedList<String> list = new LinkedList<>();
//		list.add(jsElaboration.getId() + "");
//		map2.put("elaboration", list);
//		selectedItems(jsElaborationDetail.getId(), isCreate);
	}
	
	private void selectedItems(Integer detailId, Boolean isCreate){
		API.getWarehouse().getElaborationDetailComposition(detailId, new AsyncCallback<JSON<JsElaborationDetailComposition>>() {
			
			@Override
			public void onSuccess(JSON<JsElaborationDetailComposition> result) {
//				parent.setEnableType(result.getData().length() <= 0);
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
	
	private void selectableItems(HashMap<String, LinkedList<String>> map){
		API.getWarehouse().getElaborationDetail(jsElaboration.getId(),new AsyncCallback<JSON<JsElaborationDetail>>() {
			
			@Override
			public void onSuccess(JSON<JsElaborationDetail> result) {
				result.getData().stream().forEach(js -> detailList
						.addItem(js.getQuantity() + " uds. ("+js.getItem().getSerialNumber()+") " + js.getDate(), js.getId() + ""));
				if (result.getData().length() == 1) {
					detailList.setSelectedIndex(0);
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
			}
			
			@Override
			public void onFailure(Throwable caught) {}
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
	
	private DoubleClickHandler selectableDoubleClickHandler(){
		return new DoubleClickHandler() {
			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				String value = detailList.getSelectedValue();
				
				detailList.removeItem(detailList.getSelectedIndex());
				
//				String requestData = "{\"carrier_packing\":\""+ jsCarrierPacking.getId() +"\","
//						+ "\"purchase\":\""+ Integer.parseInt(value) +"\","
//						+ "\"action\":\"add\"" + "}";
//				if(jsCarrierPacking.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
//					API.getWarehouse().addAllCarrierPacking("purchase", requestData, new AsyncCallback<JSON<JsOrderDetail>>() {
//						@Override public void onSuccess(JSON<JsOrderDetail> result) {
//							API.getWarehouse().getPurchase(Integer.parseInt(value), new AsyncCallback<JSON<JsOrder>>() {
//								@Override public void onFailure(Throwable caught) {}
//								@Override public void onSuccess(JSON<JsOrder> result) {
//									addItem(result.getOneData());
//									parent.refreshSouth2(result.getOneData());
//								}
//							});
//						}
//						@Override public void onFailure(Throwable caught) {}
//					});
//				} else {
//					API.getWarehouse().updateDelivery(Integer.parseInt(value), requestData, new AsyncCallback<JsOrder>() {
//						@Override public void onFailure(Throwable caught) {}
//						@Override public void onSuccess(JsOrder result) {
//							addItem(result);
//						}
//					});
//				}
			}
		};
	}
	
	
	public JsElaboration getJsElaboration() {
		return jsElaboration;
	}
	
	// -------------------- Actions
	private void deleteElaboration(JsElaboration js){
		String requestData = "{\"carrier_packing\":\"\"}";
//		if(jsCarrierPacking.getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
//			requestData = "{\"carrier_packing\":\""+ jsCarrierPacking.getId() +"\","
//					+ "\"purchase\":\""+ js.getId() +"\"," 
//					+ "\"action\":\"delete\""+ "}";
//
//			API.getWarehouse().addAllCarrierPacking("purchase", requestData, new AsyncCallback<JSON<JsOrderDetail>>() {
//				@Override public void onSuccess(JSON<JsOrderDetail> result) {
//					refresh();
//					parent.refreshSouth2(js);
//				}
//				@Override public void onFailure(Throwable caught) {}
//			});
//		} else {
//			API.getWarehouse().updateDelivery(js.getId(), requestData, new AsyncCallback<JsOrder>() {
//				@Override public void onFailure(Throwable caught) {}
//				@Override public void onSuccess(JsOrder result) {
//					refresh();
//				}
//			});		
//		}
	}
	
	// -------------------- DataGrid Utils
	
	private void loadDatagrid(LinkedList<JsElaborationDetailComposition> list) {
//		DefaultKeyboardSelectionHandler<JsElaborationDetailComposition> selHandler = new DefaultKeyboardSelectionHandler<JsElaborationDetailComposition>(dataGrid){
//			@Override
//			public void onCellPreview(CellPreviewEvent<JsElaborationDetailComposition> event) {
//				if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
//					Integer relRow = event.getIndex() - dataGrid.getPageStart();
//				    Integer subrow = event.getContext().getSubIndex();
//				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
//				    JsElaborationDetailComposition object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
////				    object.getOrderType();
//
//				    API.getWarehouse().getElaborationDetail(object.getId(), new AsyncCallback<JSON<JsElaborationDetail>>() {
//						
//						@Override
//						public void onSuccess(JSON<JsElaborationDetail> result) {
//							parent.southContent(jsElaboration, result.getData());							
//						}
//						
//						@Override public void onFailure(Throwable caught) {}
//					});
//				    // TODO MOSTRAR DETAILS EN EL SOUTH!				  
//				}		
//			}
//		};
		
//		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		
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
		
		// TODO add button
//		final Button addButton = new Button();
//		addButton.setAccessKey( 'L' );
//		addButton.setStyleName(AON.AON_CSS.aonIconReset());
//		addButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
//		addButton.addFocusHandler(new FocusHandler() {
//			@Override
//			public void onFocus(FocusEvent event) {
//				if (canAddLine()) {
//					addLine();
//					remove(addButton);
//					setFocus(true);
//				}
//			}
//		});
//		
//		addButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				addLine();
//				remove(addButton);
//				Focusable focusable = (Focusable) getWidget( getRowCount() - 2, COLS.ACC.ordinal());
//				focusable.setFocus(true);
//			}
//		});
//		setWidget(getRowCount() - 1, COLS.NUM.ordinal(),  addButton );
		
		// TODO remove button
//		Button removeButton = new Button();
//		removeButton.setStyleName(AON.AON_CSS.aonIconDelete());
//		removeButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
//		removeButton.setTabIndex(Integer.MAX_VALUE);
//		removeButton.addClickHandler( new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				if (aed.getId() == null) {
//					wizardContent.getMainEntry().getDetails().remove(aed);
//					removeRow(curRow);
//					removeRow(getRowCount() - 1);
//					paintFooter();
//					paintAddButton();
//				} else {
//					aed.setId( aed.getId() * -1 );
//					paintDeletedRow(curRow, aed);
//					refreshTotals();
//				}
//			}
//		});
//		setWidget(row, COLS.BUT.ordinal(), removeButton);
	}
	
	public void addItem(JsElaborationDetailComposition js){
		dataProvider.getList().add(js);
		compositionDataGrid.redraw();
//		parent.setEnableType(false);
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
	
	private class ActionHasCell implements HasCell<JsElaborationDetail, JsElaborationDetail> {
	    private ActionCell<JsElaborationDetail> cell;
	    String s;
	    
	    public ActionHasCell(String text, Delegate<JsElaborationDetail> delegate) {
	    	s = text;
	        cell = new ActionCell<JsElaborationDetail>(text, delegate){
	        	String text = s;
	        	@Override
	        	public void render(com.google.gwt.cell.client.Cell.Context context,
	        			JsElaborationDetail value, SafeHtmlBuilder sb) {
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
	    public Cell<JsElaborationDetail> getCell() {
	        return cell;
	    }

	    @Override
	    public FieldUpdater<JsElaborationDetail, JsElaborationDetail> getFieldUpdater() {
	        return null;
	    }

	    @Override
	    public JsElaborationDetail getValue(JsElaborationDetail object) {
	        return object;
	    }
	}
	
//	private void send(JsElaboration js) {
//		VerticalPanel panel = new VerticalPanel();
//		panel.setStyleName(AON.AON_CSS.aonWidthAll());
//		AonComboBox emailComboBox = new AonComboBox();
//    	emailComboBox.setLabel("De");
//    	emailComboBox.setItemLabelPath("name");
//    	emailComboBox.setItemValuePath("name");
//		API.getCommon().getMailAccounts(new AsyncCallback<JSON<JsObject>>() {
//			
//			@Override
//			public void onSuccess(JSON<JsObject> result) {
//		    	emailComboBox.setItems(result.getData());
//			}
//			
//			@Override public void onFailure(Throwable caught) {}
//		});
//		panel.add(emailComboBox);
//		
//		PaperInput toText = new PaperInput();
//		toText.setLabel("Para");
////		API.getIncidence().getEnterpriseRmediaList(js.getRegistry().getId(), new AsyncCallback<JSON<JsRmedia>>() {
////			
////			@Override
////			public void onSuccess(JSON<JsRmedia> result) {
////				StringBuilder emails = new StringBuilder();
////				result.getData().stream().forEach(rmedia -> {
////					String media = rmedia.getMedia() + "";
////					if(media.equals("4")){	
////						emails.append(rmedia.getValue());
////						emails.append(";");
////					}
////				});
////				toText.setValue(emails.toString());
////			}
////			
////			@Override public void onFailure(Throwable caught) {}
////		});
//		panel.add(toText);
//		
//		AonComboBox signComboBox = new AonComboBox();
//    	signComboBox.setLabel("Firma de Correo");
//    	signComboBox.setItemLabelPath("name");
//    	signComboBox.setItemValuePath("name");
//    	API.getCommon().getSignatures(new AsyncCallback<JSON<JsObject>>() {
//			
//			@Override
//			public void onSuccess(JSON<JsObject> result) {
//				signComboBox.setItems(result.getData());
//			}
//			
//			@Override public void onFailure(Throwable caught) {}
//		});
//    	panel.add(signComboBox);
//    	
//      	AonDialog dialog = new AonDialog("Enviar Packing List", panel) {
//			
//			@Override protected void onCancel() {hide();}
//			
//			@Override 
//			protected void onAccept() {
//				JsObject jsEmail = (JsObject) emailComboBox.getSelectedItem();
//				JsObject jsSign = (JsObject) signComboBox.getSelectedItem();
//				String requestData = "{\"carrier_packing\":\""+ jsElaboration.getId() +"\","
//						+ "\"mail_account\":\""+ jsEmail.getId() +"\","
//						+ "\"signature\":\""+ ((jsSign != null) ? jsSign.getId() : "-1" )+"\"," 
//						+ "\"to\":\""+ toText.getValue() + "\","
//						+ "\"order\":\""+ js.getId() + "\","
//						+ "\"type\":\"registry\"" + "}";
//
//				API.getWarehouse().sendPackingList(requestData);
//				hide();
//			}
//		};
//		dialog.addAutoHidePartner(emailComboBox.getElementById("overlay"));
//		dialog.addAutoHidePartner(signComboBox.getElementById("overlay"));
//		dialog.setAutoHideEnabled(true);
//		dialog.getElement().getStyle().setWidth(310, Unit.PX);
//		dialog.center();
//	}
	
	private void initTableColumns(final SelectionModel<JsElaborationDetailComposition> selectionModel, ListHandler<JsElaborationDetailComposition> sortHandler) {
		NumberFormat numberFormat = NumberFormat.getDecimalFormat().overrideFractionDigits(2);
		
		List<HasCell<JsElaborationDetailComposition, ?>> cells = new LinkedList<HasCell<JsElaborationDetailComposition, ?>>();
	    
//		cells.add(new ActionHasCell("delete", new Delegate<JsElaborationDetail>() {
//
//	        @Override
//	        public void execute(JsElaborationDetail object) {
//	        	deleteElaboration(object);
//	        }
//	    }));
//		
//	    cells.add(new ActionHasCell("send", new Delegate<JsElaborationDetail>() {
//
//	        @Override
//	        public void execute(JsElaborationDetail object) {
//	        	send(object);
//	        }
//	    }));
		
		CompositeCell<JsElaborationDetailComposition> cell = new CompositeCell<JsElaborationDetailComposition>(cells);

		/** Date Column **/
		Column<JsElaborationDetailComposition,String> dateColumn = new Column<JsElaborationDetailComposition, String>(new TextCell()) {
			
			@Override
			public String getValue(JsElaborationDetailComposition object) {
				return object.getDate() != null ? object.getDate() : "";
			}
		};
		
		dateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		dateColumn.setSortable(true); 
		sortHandler.setComparator(dateColumn,new Comparator<JsElaborationDetailComposition>() {
			
			@Override
			public int compare(JsElaborationDetailComposition o1, JsElaborationDetailComposition o2) {
				return o1.getDate().compareTo(o2.getDate());
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
		
		compositionDataGrid.getColumnSortList().push(dateColumn);
		compositionDataGrid.getColumnSortList().push(quantityColumn);
		compositionDataGrid.getColumnSortList().push(itemColumn);
		
		compositionDataGrid.addColumn(dateColumn, AON.MSG.date());
		compositionDataGrid.addColumn(itemColumn, "Producto");
		compositionDataGrid.addColumn(quantityColumn, AON.MSG.quantity());
		
		compositionDataGrid.setColumnWidth(dateColumn, 15, Unit.PCT);
		compositionDataGrid.setColumnWidth(itemColumn, 70, Unit.PCT);
		compositionDataGrid.setColumnWidth(quantityColumn, 15, Unit.PCT);
		
	
	}
}
