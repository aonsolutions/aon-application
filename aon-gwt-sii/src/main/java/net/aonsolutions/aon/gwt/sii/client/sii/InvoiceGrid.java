package net.aonsolutions.aon.gwt.sii.client.sii;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.esferalia.aon.gwt.api.client.finance.JsInvoice;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.AbstractHasData.DefaultKeyboardSelectionHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

public class InvoiceGrid extends ResizeComposite implements RequiresResize {

	interface GridBinder extends UiBinder<Widget, InvoiceGrid> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);
	
	DataGridResources resources = GWT.create(DataGridResources.class);
	
	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) CustomDataGrid<JsInvoice> dataGrid; 
	
	
	SiiPrincipal parent;
	Integer cont = 0;
	
	private API getAPI() {
		return parent.getAPI();
	}

	public InvoiceGrid(SiiPrincipal parent, LinkedList<JsInvoice> list) {
		this.parent = parent;		
		dataGrid = new CustomDataGrid<JsInvoice>(Integer.MAX_VALUE, resources,
				JsInvoice.PROVIDES_KEY);
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
					parent.getAPI().getFinance().getInvoices(parent.getFilterMap(), new AsyncCallback<JSON<JsInvoice>>() {
						
						@Override
						public void onSuccess(JSON<JsInvoice> result) {
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
	
	LinkedList<JsInvoice> selFiles = new LinkedList<>();
	private void load(LinkedList<JsInvoice> list) {
		DefaultKeyboardSelectionHandler<JsInvoice> selHandler = new DefaultKeyboardSelectionHandler<JsInvoice>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsInvoice> event) {
				 if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					 JsInvoice object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
					 if(event.getColumn() == 0){
						 if(dataGrid.getSelectionModel().isSelected(object) || object.isSiiSent())
							 dataGrid.getSelectionModel().setSelected(object, false);
						 else dataGrid.getSelectionModel().setSelected(object, true);
					 } else {
						 for(JsInvoice f :dataProvider.getList()){
							 dataGrid.getSelectionModel().setSelected(f, false);
						 }
						 dataGrid.getSelectionModel().setSelected(object, true);
					 }
					 
					selFiles = new LinkedList<>();
					for(JsInvoice f :dataProvider.getList()){
						if(dataGrid.getSelectionModel().isSelected(f)){
							selFiles.add(f);
						}
					}
					parent.getSendAll().setVisible(selFiles.size() > 0);
				 }
			}
		};
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
		addDataDisplay(dataGrid, list);
		ListHandler<JsInvoice> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
	//	final SingleSelectionModel<JsInvoice> selectionModel = new SingleSelectionModel<JsInvoice>(
	//			JsInvoice.PROVIDES_KEY);
		final SelectionModel<JsInvoice> selectionModel = new MultiSelectionModel<JsInvoice>(JsInvoice.PROVIDES_KEY);

		
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsInvoice> createCheckboxManager());
	//	dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
		
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<JsInvoice> dataProvider = new ListDataProvider<JsInvoice>();

	public void addDataDisplay(HasData<JsInvoice> display, LinkedList<JsInvoice> list) {
		dataProvider = new ListDataProvider<JsInvoice>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsInvoice> getSortHandler() {
		return new ListHandler<JsInvoice>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsInvoice> aux  = super.getList();
				List<JsInvoice> aux2 = new LinkedList<JsInvoice>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initTableColumns(final SelectionModel<JsInvoice> selectionModel, ListHandler<JsInvoice> sortHandler) {
		
		/** Check Column **/
		
		Column<JsInvoice, Boolean> checkColumn = new Column<JsInvoice, Boolean>(new CheckboxCell(true, true) {
			@Override
			public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Boolean value,
					NativeEvent event, com.google.gwt.cell.client.ValueUpdater<Boolean> valueUpdater) {
			}
		}) {
			@Override
			public void render(Context context, JsInvoice object, SafeHtmlBuilder sb) {
				if(object.isSiiSent()){
					sb.append(SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" disabled=\"disabled\"/>"));
				} else super.render(context, object, sb);
			}
			
			@Override
			public Boolean getValue(JsInvoice object) {
				return selectionModel.isSelected(object);
			}
		};
		dataGrid.addColumn(checkColumn);
		dataGrid.setColumnWidth(checkColumn, 40, Unit.PX);
		
		/** code Column **/
		Column<JsInvoice, String> codeColumn = new Column<JsInvoice, String>(new TextCell()) {

			@Override
			public String getValue(JsInvoice object) {
				return object.getReferenceCode();
			}
		
		};
		codeColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		codeColumn.setSortable(true); 
		sortHandler.setComparator(codeColumn,new Comparator<JsInvoice>() {
			
			@Override
			public int compare(JsInvoice o1, JsInvoice o2) {
				return o1.getReferenceCode().compareTo(o2.getReferenceCode());
			}
		});
		dataGrid.getColumnSortList().push(codeColumn);
		dataGrid.addColumn(codeColumn, "Codigo de Referencia");
		dataGrid.setColumnWidth(codeColumn, 15, Unit.PCT);

		/** VAT DATE Column **/
		Column<JsInvoice, String> taxDateColumn = new Column<JsInvoice, String>(new TextCell()) {

			@Override
			public String getValue(JsInvoice object) {
				return object.getTaxDate();
			}
		};
		taxDateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		taxDateColumn.setSortable(true); 
		sortHandler.setComparator(taxDateColumn,new Comparator<JsInvoice>() {
			
			@Override
			public int compare(JsInvoice o1, JsInvoice o2) {
				return o1.getTaxDate().compareTo(o2.getTaxDate());
			}
		});
		dataGrid.getColumnSortList().push(taxDateColumn);
		dataGrid.addColumn(taxDateColumn, "Fecha IVA");
		dataGrid.setColumnWidth(taxDateColumn, 15, Unit.PCT);
		
		/** Contraparte Column **/
		Column<JsInvoice, String> contraparteColumn = new Column<JsInvoice, String>(new TextCell()) {

			@Override
			public String getValue(JsInvoice object) {
				return object.getRegistryName();
			}
		
		};
		contraparteColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		contraparteColumn.setSortable(true); 
		sortHandler.setComparator(contraparteColumn,new Comparator<JsInvoice>() {
			
			@Override
			public int compare(JsInvoice o1, JsInvoice o2) {
				return o1.getRegistryName().compareTo(o2.getRegistryName());
			}
		});
		dataGrid.getColumnSortList().push(contraparteColumn);
		dataGrid.addColumn(contraparteColumn, "Contraparte");
		dataGrid.setColumnWidth(contraparteColumn, 15, Unit.PCT);
		
		/** action Column **/
		List<HasCell<JsInvoice, ?>> cells = new LinkedList<HasCell<JsInvoice, ?>>();
	    
		cells.add(new ActionHasCell("enviar", new Delegate<JsInvoice>() {

	        @Override
	        public void execute(JsInvoice object) {
	        	sendSii(object);
	        }
	    }));
		
	    cells.add(new ActionHasCell("anular", new Delegate<JsInvoice>() {

	        @Override
	        public void execute(JsInvoice object) {
	        	anular(object);
	        }
	    }));
	    
	    cells.add(new ActionHasCell("cobros_pagos", new Delegate<JsInvoice>() {

	        @Override
	        public void execute(JsInvoice object) {
	        	cobrosPagos(object);
	        }
	    }));
		
		CompositeCell<JsInvoice> cell = new CompositeCell<JsInvoice>(cells);
		
		Column<JsInvoice,JsInvoice> actionColumn = new Column<JsInvoice, JsInvoice>(cell){

			@Override
			public JsInvoice getValue(JsInvoice object) {
				return object;
			}
		};
		actionColumn.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		dataGrid.addColumn(actionColumn, "Acciones");
		dataGrid.setColumnWidth(actionColumn, 10, Unit.PCT);
	}
	
	private class ActionHasCell implements HasCell<JsInvoice, JsInvoice> {
	    private ActionCell<JsInvoice> cell;
	    String s;
	    
	    public ActionHasCell(String text, Delegate<JsInvoice> delegate) {
	    	s = text;
	        cell = new ActionCell<JsInvoice>(text, delegate){
	        	String text = s;
	        	@Override
	        	public void render(com.google.gwt.cell.client.Cell.Context context,
	        			JsInvoice value, SafeHtmlBuilder sb) {
	        		if(text.equals("enviar")){
	        			if(!value.isSiiSent()){
	        				sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-accept\" tabindex=\"-1\">");
	        				sb.appendHtmlConstant("</button>");		
	        			}
	        		}
	        		if(text.equals("anular")){
	        			if(value.isSiiSent()){
	        				sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-removed\" tabindex=\"-1\">");
							sb.appendHtmlConstant("</button>");
	        			}
	        		}
	        		
	        		if(text.equals("cobros_pagos")){
	        			if(value.isSiiSent() && value.isVatAccrualPayment()){
	        				sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-pay\" tabindex=\"-1\">");
							sb.appendHtmlConstant("</button>");
	        			}
	        		}
	        	}
	        };
	        
	    }

	    @Override
	    public Cell<JsInvoice> getCell() {
	        return cell;
	    }

	    @Override
	    public FieldUpdater<JsInvoice, JsInvoice> getFieldUpdater() {
	        return null;
	    }

	    @Override
	    public JsInvoice getValue(JsInvoice object) {
	        return object;
	    }
	}
	
	private void anular(JsInvoice invoice){
		VerticalPanel vp = new VerticalPanel();
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.add(new Label("Certificado"));
		ListBox lb = new ListBox();
		getAPI().getAttachment().getCertificates(new AsyncCallback<JSON<JsAttach>>() {
			
			@Override
			public void onSuccess(JSON<JsAttach> result) {
				result.getData().stream().forEach(a -> {
					lb.addItem(a.getTitle(), a.getId() + "");
				});
			}

			@Override public void onFailure(Throwable caught) {}
		});
		hp1.add(lb);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.addStyleName(AON.AON_CSS.aonPaddingTop());
		hp2.add(new Label("Contrase\u00f1a"));
		PasswordTextBox tb = new PasswordTextBox();
		tb.setStyleName(AON.AON_CSS.aonInputText());
		hp2.add(tb);
		vp.add(hp1);
		vp.add(hp2);
		AonDialog dialog = new AonDialog("Anular Operaci\u00f3n", vp) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				HashMap<String, LinkedList<String>> map =  new HashMap<>();
				LinkedList<String> list =new LinkedList<>();
				list.add(invoice.getId() + "");
				map.put("id", list);
		    	list = new LinkedList<>();
		    	list.add("baja");
		    	map.put("action", list);
		    	list = new LinkedList<>();
		    	list.add(lb.getSelectedValue());
		    	map.put("cert", list);
		    	list = new LinkedList<>();
		    	list.add(tb.getText());
		    	map.put("pass", list);
		    	list = new LinkedList<>();
		    	list.add("baja");
		    	map.put("option", list);
		    	hide();
		    	getAPI().getFinance().sendSii(map, new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						Window.alert(result.getOneData().getId() + ": " + result.getOneData().getName());
					}
					
					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
			}
		};
		dialog.center();
	}
	
	private void cobrosPagos(JsInvoice invoice){
		VerticalPanel vp = new VerticalPanel();
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.add(new Label("Certificado"));
		ListBox lb = new ListBox();
		getAPI().getAttachment().getCertificates(new AsyncCallback<JSON<JsAttach>>() {
			
			@Override
			public void onSuccess(JSON<JsAttach> result) {
				result.getData().stream().forEach(a -> {
					lb.addItem(a.getTitle(), a.getId() + "");
				});
			}

			@Override public void onFailure(Throwable caught) {}
		});
		hp1.add(lb);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.addStyleName(AON.AON_CSS.aonPaddingTop());
		hp2.add(new Label("Contrase\u00f1a"));
		PasswordTextBox tb = new PasswordTextBox();
		tb.setStyleName(AON.AON_CSS.aonInputText());
		hp2.add(tb);
		vp.add(hp1);
		vp.add(hp2);
		AonDialog dialog = new AonDialog(invoice.getType().equalsIgnoreCase("Ventas") ? "Enviar Cobros Factura": "Enviar Pagos Factura", vp) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				HashMap<String, LinkedList<String>> map =  new HashMap<>();
				LinkedList<String> list =new LinkedList<>();
				list.add(invoice.getId() + "");
				map.put("id", list);
		    	list = new LinkedList<>();
		    	list.add("suministro");
		    	map.put("action", list);
		    	list = new LinkedList<>();
		    	list.add(lb.getSelectedValue());
		    	map.put("cert", list);
		    	list = new LinkedList<>();
		    	list.add(tb.getText());
		    	map.put("pass", list);
		    	list = new LinkedList<>();
		    	list.add(invoice.getType().equalsIgnoreCase("Ventas") ? "cobros": "pagos");
		    	map.put("option", list);
		    	hide();
		    	getAPI().getFinance().sendSii(map, new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						Window.alert(result.getOneData().getId() + ": " + result.getOneData().getName());
					}
					
					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
			}
		};
		dialog.center();
	}
	
	private void sendSii(JsInvoice invoice){
		VerticalPanel vp = new VerticalPanel();
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.add(new Label("Certificado"));
		ListBox lb = new ListBox();
		getAPI().getAttachment().getCertificates(new AsyncCallback<JSON<JsAttach>>() {
			
			@Override
			public void onSuccess(JSON<JsAttach> result) {
				result.getData().stream().forEach(a -> {
					lb.addItem(a.getTitle(), a.getId() + "");
				});
			}

			@Override public void onFailure(Throwable caught) {}
		});
		hp1.add(lb);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.addStyleName(AON.AON_CSS.aonPaddingTop());
		hp2.add(new Label("Contrase\u00f1a"));
		PasswordTextBox tb = new PasswordTextBox();
		tb.setStyleName(AON.AON_CSS.aonInputText());
		hp2.add(tb);
		vp.add(hp1);
		vp.add(hp2);
		AonDialog dialog = new AonDialog("Enviar Factura", vp) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				HashMap<String, LinkedList<String>> map =  new HashMap<>();
				LinkedList<String> list =new LinkedList<>();
				list.add(invoice.getId() + "");
				map.put("id", list);
		    	list = new LinkedList<>();
		    	list.add("suministro");
		    	map.put("action", list);
		    	list = new LinkedList<>();
		    	list.add(lb.getSelectedValue());
		    	map.put("cert", list);
		    	list = new LinkedList<>();
		    	list.add(tb.getText());
		    	map.put("pass", list);
		    	list = new LinkedList<>();
		    	list.add("general");
		    	map.put("option", list);
		    	hide();
		    	getAPI().getFinance().sendSii(map, new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						Window.alert(result.getOneData().getId() + ": " + result.getOneData().getName());
						parent.content();

					}
					
					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
			}
		};
		dialog.center();
	}
	
	public void sendSii(){
		VerticalPanel vp = new VerticalPanel();
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.add(new Label("Certificado"));
		ListBox lb = new ListBox();
		getAPI().getAttachment().getCertificates(new AsyncCallback<JSON<JsAttach>>() {
			
			@Override
			public void onSuccess(JSON<JsAttach> result) {
				result.getData().stream().forEach(a -> {
					lb.addItem(a.getTitle(), a.getId() + "");
				});
			}

			@Override public void onFailure(Throwable caught) {}
		});
		hp1.add(lb);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.addStyleName(AON.AON_CSS.aonPaddingTop());
		hp2.add(new Label("Contrase\u00f1a"));
		PasswordTextBox tb = new PasswordTextBox();
		tb.setStyleName(AON.AON_CSS.aonInputText());
		hp2.add(tb);
		vp.add(hp1);
		vp.add(hp2);
		AonDialog dialog = new AonDialog("Enviar Factura", vp) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				HashMap<String, LinkedList<String>> map =  new HashMap<>();
				LinkedList<String> list = selFiles.stream().map(s -> s.getId() + "").collect(Collectors.toCollection(LinkedList::new));
				map.put("id", list);
		    	list = new LinkedList<>();
		    	list.add("suministro");
		    	map.put("action", list);
		    	list = new LinkedList<>();
		    	list.add(lb.getSelectedValue());
		    	map.put("cert", list);
		    	list = new LinkedList<>();
		    	list.add(tb.getText());
		    	map.put("pass", list);
		    	list = new LinkedList<>();
		    	list.add("general");
		    	map.put("option", list);
		    	hide();
		    	getAPI().getFinance().sendSii(map, new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						result.getData().stream().forEach(r -> {
							Window.alert(r.getId() + ": " + r.getName());
						});
						parent.content();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
			}
		};
		dialog.center();
	}
}
