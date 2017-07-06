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
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

import net.aonsolutions.aon.gwt.sii.client.ISii;
import net.aonsolutions.aon.gwt.sii.client.ISiiAsync;

public class InvoiceGrid extends ResizeComposite implements RequiresResize {

	final ISiiAsync impl = GWT.create(ISii.class);
	
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
						 if(dataGrid.getSelectionModel().isSelected(object))
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
					String sii = parent.getFilterMap().get("sii").get(0);
					parent.getBaja().setVisible(selFiles.size() > 0 && !"cp_cobros_pagos".equals(sii)
							&& !"cp_cobros".equals(sii) && !"cp_pagos".equals(sii));
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
		final MultiSelectionModel<JsInvoice> selectionModel = new MultiSelectionModel<JsInvoice>(JsInvoice.PROVIDES_KEY);
		
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
	
	private void initTableColumns(final MultiSelectionModel<JsInvoice> selectionModel, ListHandler<JsInvoice> sortHandler) {
		
		/** Check Column **/
		
		Column<JsInvoice, Boolean> checkColumn = new Column<JsInvoice, Boolean>(new CheckboxCell(true, true) {
			@Override
			public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Boolean value,
					NativeEvent event, com.google.gwt.cell.client.ValueUpdater<Boolean> valueUpdater) {
			}
		}) {
			
			@Override
			public Boolean getValue(JsInvoice object) {
				return selectionModel.isSelected(object);
			}
		};

		dataGrid.addColumn(checkColumn, new CheckboxHeader(selectionModel, dataProvider));
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
	
		/** Status Column **/
		
		Column<JsInvoice, String> statusColumn = new Column<JsInvoice, String>(new TextCell()) {
			@Override	
			public void render(Context context, JsInvoice object, SafeHtmlBuilder sb) {
				String icon = AON.AON_CSS.aonIconPointLightGreen();
				if(object.getSiiStatus().equals("Correcto") || object.getSiiStatus().equals("Pagado")) icon = AON.AON_CSS.aonIconPointGreen();
				else if(object.getSiiStatus().equals("AceptadoConErrores") || object.getSiiStatus().equals("Parcial")) icon = AON.AON_CSS.aonIconPointOrange();
				else if(object.getSiiStatus().equals("Incorrecto")) icon= AON.AON_CSS.aonIconPointRed();
				else if(object.getSiiStatus().equals("Anulada")) icon = AON.AON_CSS.aonIconPointYellow();
				sb.appendHtmlConstant("<g:Label class=\""+ icon + "\" style=\"padding-left: 16px;\" >"+ "&nbsp;&nbsp;" + object.getSiiStatus());
			}
			
			@Override
			public String getValue(JsInvoice object) {
				return object.getSiiStatus();
			}
		
		};
		statusColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		statusColumn.setSortable(true); 
		sortHandler.setComparator(statusColumn,new Comparator<JsInvoice>() {
			
			@Override
			public int compare(JsInvoice o1, JsInvoice o2) {
				return o1.getSiiStatus().compareTo(o2.getSiiStatus());
			}
		});
		dataGrid.getColumnSortList().push(statusColumn);
		dataGrid.addColumn(statusColumn, "Estado");
		dataGrid.setColumnWidth(statusColumn, 10, Unit.PCT);
	}
	
	public final class CheckboxHeader extends Header {

	    private final MultiSelectionModel<JsInvoice> selectionModel;
	    private final ListDataProvider<JsInvoice> provider;

	    public CheckboxHeader(MultiSelectionModel<JsInvoice> selectionModel,
	    		ListDataProvider<JsInvoice> provider) {
	        super(new CheckboxCell());
	        this.selectionModel = selectionModel;
	        this.provider = provider;
	    }

	    @Override
	    public Boolean getValue() {
	        boolean allItemsSelected = selectionModel.getSelectedSet().size() == provider
	                .getList().size();
	        return allItemsSelected;
	    }

	    @Override
	    public void onBrowserEvent(Context context, Element elem, NativeEvent event) {
	        InputElement input = elem.getFirstChild().cast();
	        Boolean isChecked = input.isChecked();
	        for (JsInvoice element : provider.getList()) {
	            selectionModel.setSelected(element, isChecked);
	        }
	    }
	}
	
	public void anular(String sii){
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
		
		HorizontalPanel hp3 = new HorizontalPanel();
		hp3.addStyleName(AON.AON_CSS.aonPaddingTop());
		Label l = new Label("NIF");
		l.getElement().getStyle().setPaddingTop(5, Unit.PX);
		l.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		l.setVisible(false);
		TextBox t = new TextBox();t.setStyleName(AON.AON_CSS.aonInputText());
		t.setVisible(false);
		CheckBox cb = new CheckBox("Por terceros");
		cb.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				l.setVisible(cb.getValue());
				t.setVisible(cb.getValue());	
			}
		});
		hp3.add(cb);
		hp3.add(l);
		hp3.add(t);
		vp.add(hp3);
		
		AonDialog dialog = new AonDialog("Anular Operaci\u00f3n", vp) {
			
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
		    	list.add("baja");
		    	map.put("action", list);
		    	list = new LinkedList<>();
		    	list.add(lb.getSelectedValue());
		    	map.put("cert", list);
		    	list = new LinkedList<>();
		    	list.add(tb.getText());
		    	map.put("pass", list);
		    	list = new LinkedList<>();
		    	list.add(sii);
		    	map.put("option", list);
		    	
		    	list = new LinkedList<>();
		    	list.add(cb.getValue() ? t.getValue() : "false");
		    	map.put("terceros", list);
				
		    	hide();
		    	getAPI().getFinance().sendSii(map, new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						VerticalPanel vp = new VerticalPanel();
						result.getData().stream().forEach(r -> {
							Label label = new Label(r.getName());
							String str = r.getId() + "";
							String color = "red";
							if(str.equals("200")) color = "green";
							else if(str.substring(0, 1).equals("2")) color = "orange";
							label.getElement().getStyle().setColor(color);
							vp.add(label);
						});
						parent.errorPanel.setWidget(vp);
						parent.openFootPanel();
						parent.gridContent();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
			}
		};
		dialog.center();
	}
	
	public void sendSii(String sii){
		VerticalPanel vp = new VerticalPanel();
		
		HorizontalPanel hp0 = new HorizontalPanel();
		hp0.add(new Label("Tipo de Operacion"));
		ListBox lb0 = new ListBox();
		lb0.addItem("Articulo 70, apartado uno, n\u00famero 7\u00BA, Ley del Impuesto(Ley 37/1992)", "A");
		lb0.addItem("Articulo 16, apartado 2\u00BA, Ley del Impuesto(Ley 37/1992)", "B");
		hp0.add(lb0);
		if("intracomunitarias".equalsIgnoreCase(sii)){
			vp.add(hp0);
		}
		
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.addStyleName(AON.AON_CSS.aonPaddingTop());
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
		
		HorizontalPanel hp3 = new HorizontalPanel();
		hp3.addStyleName(AON.AON_CSS.aonPaddingTop());
		Label l = new Label("NIF");
		l.getElement().getStyle().setPaddingTop(5, Unit.PX);
		l.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		l.setVisible(false);
		TextBox t = new TextBox();t.setStyleName(AON.AON_CSS.aonInputText());
		t.setVisible(false);
		CheckBox cb = new CheckBox("Por terceros");
		cb.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				l.setVisible(cb.getValue());
				t.setVisible(cb.getValue());	
			}
		});
		hp3.add(cb);
		hp3.add(l);
		hp3.add(t);
		vp.add(hp3);
		
		AonDialog dialog = new AonDialog("Enviar Facturas", vp) {
			
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
		    	list.add(sii);
		    	map.put("option", list);
		    	hide();
		    	
		    	list = new LinkedList<>();
		    	list.add(lb0.getSelectedValue());
		    	map.put("tipo_operacion", list);
	
		    	list = new LinkedList<>();
		    	list.add(cb.getValue() ? t.getValue() : "false");
		    	map.put("terceros", list);
		    	getAPI().getFinance().sendSii(map, new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						VerticalPanel vp = new VerticalPanel();
						result.getData().stream().forEach(r -> {
							Label label = new Label(r.getName());
							String str = r.getId() + "";
							String color = "red";
							if(str.equals("200")) color = "green";
							else if(str.substring(0, 1).equals("2")) color = "orange";
							label.getElement().getStyle().setColor(color);
							vp.add(label);
						});
						parent.errorPanel.setWidget(vp);
						parent.openFootPanel();
						parent.gridContent();
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
