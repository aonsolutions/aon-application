package net.aonsolutions.aon.gwt.seres.client.seres;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
//import com.esferalia.aon.gwt.api.client.finance.JsInvoice;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.seres.JsSeresFile;
import com.esferalia.aon.gwt.api.client.warehouse.JsDelivery;
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

public class ContentGrid extends ResizeComposite implements RequiresResize {

	interface GridBinder extends UiBinder<Widget, ContentGrid> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);
	
	DataGridResources resources = GWT.create(DataGridResources.class);
	
	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) CustomDataGrid<JsSeresFile> dataGrid; 
	
	private SeresPrincipal parent;
	private Integer cont = 0;
	
	private API getAPI() {
		return parent.getAPI();
	}
	
	public ContentGrid(SeresPrincipal parent) {
		
	}

	public ContentGrid(SeresPrincipal parent, LinkedList<JsSeresFile> list) {
		this.parent = parent;		
		dataGrid = new CustomDataGrid<JsSeresFile>(Integer.MAX_VALUE, resources,
				JsSeresFile.PROVIDES_KEY);
		ScrollPanel scrollPanel = dataGrid.getScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				// TODO
//				if(scrollPanel.getVerticalScrollPosition() >= scrollPanel.getMaximumVerticalScrollPosition()){
//					Integer page = 2;
//					if(parent.getFilterMap().containsKey("page")){
//						page = Integer.parseInt(parent.getFilterMap().get("page").get(0)) + 1;
//					}
//					LinkedList<String> list = new LinkedList<>();
//					list.add(page +"");
//					parent.getFilterMap().put("page", list);
//					parent.getAPI().getFinance().getInvoices(parent.getFilterMap(), new AsyncCallback<JSON<JsSeresFile>>() {
//						
//						@Override
//						public void onSuccess(JSON<JsSeresFile> result) {
//							dataProvider.getList().addAll(result.getData().toLinkedList());
//							dataGrid.redraw();
//						}
//						
//						@Override public void onFailure(Throwable caught) {}
//					});	
//				}
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
	
	LinkedList<JsSeresFile> selFiles = new LinkedList<>();
	private void load(LinkedList<JsSeresFile> list) {
		DefaultKeyboardSelectionHandler<JsSeresFile> selHandler = new DefaultKeyboardSelectionHandler<JsSeresFile>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsSeresFile> event) {
				 if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					 JsSeresFile object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
					 if(event.getColumn() == 0){
						 if(dataGrid.getSelectionModel().isSelected(object))
							 dataGrid.getSelectionModel().setSelected(object, false);
						 else dataGrid.getSelectionModel().setSelected(object, true);
					 } else {
						 for(JsSeresFile f :dataProvider.getList()){
							 dataGrid.getSelectionModel().setSelected(f, false);
						 }
						 dataGrid.getSelectionModel().setSelected(object, true);
					 }
					 
					selFiles = new LinkedList<>();
					for(JsSeresFile f :dataProvider.getList()){
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
		ListHandler<JsSeresFile> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
	//	final SingleSelectionModel<JsSeresFile> selectionModel = new SingleSelectionModel<JsSeresFile>(
	//			JsSeresFile.PROVIDES_KEY);
		final MultiSelectionModel<JsSeresFile> selectionModel = new MultiSelectionModel<JsSeresFile>(JsSeresFile.PROVIDES_KEY);
		
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsSeresFile> createCheckboxManager());
	//	dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
		
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<JsSeresFile> dataProvider = new ListDataProvider<JsSeresFile>();

	public void addDataDisplay(HasData<JsSeresFile> display, LinkedList<JsSeresFile> list) {
		dataProvider = new ListDataProvider<JsSeresFile>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsSeresFile> getSortHandler() {
		return new ListHandler<JsSeresFile>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsSeresFile> aux  = super.getList();
				List<JsSeresFile> aux2 = new LinkedList<JsSeresFile>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initTableColumns(final MultiSelectionModel<JsSeresFile> selectionModel, ListHandler<JsSeresFile> sortHandler) {
		
		/** Check Column **/
		Column<JsSeresFile, Boolean> checkColumn = new Column<JsSeresFile, Boolean>(new CheckboxCell(true, true) {
			@Override
			public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Boolean value,
					NativeEvent event, com.google.gwt.cell.client.ValueUpdater<Boolean> valueUpdater) {
			}
		}) {
			
			@Override
			public Boolean getValue(JsSeresFile object) {
				return selectionModel.isSelected(object);
			}
		};

		dataGrid.addColumn(checkColumn, new CheckboxHeader(selectionModel, dataProvider));
		dataGrid.setColumnWidth(checkColumn, 40, Unit.PX);
		
		/** Code Column **/
		Column<JsSeresFile, String> codeColumn = new Column<JsSeresFile, String>(new TextCell()) {

			@Override
			public String getValue(JsSeresFile object) {
				return object.getReferenceCode();
			}
		
		};
		codeColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		codeColumn.setSortable(true); 
		sortHandler.setComparator(codeColumn,new Comparator<JsSeresFile>() {
			
			@Override
			public int compare(JsSeresFile o1, JsSeresFile o2) {
				return o1.getReferenceCode().compareTo(o2.getReferenceCode());
			}
		});
		dataGrid.getColumnSortList().push(codeColumn);
		dataGrid.addColumn(codeColumn, "Codigo");
		dataGrid.setColumnWidth(codeColumn, 10, Unit.PCT);

		/** Date Column **/
		Column<JsSeresFile, String> taxDateColumn = new Column<JsSeresFile, String>(new TextCell()) {

			@Override
			public String getValue(JsSeresFile object) {
				return object.getDate();
			}
		};
		taxDateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		taxDateColumn.setSortable(true); 
		sortHandler.setComparator(taxDateColumn,new Comparator<JsSeresFile>() {
			
			@Override
			public int compare(JsSeresFile o1, JsSeresFile o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});
		dataGrid.getColumnSortList().push(taxDateColumn);
		dataGrid.addColumn(taxDateColumn, "Fecha");
		dataGrid.setColumnWidth(taxDateColumn, 10, Unit.PCT);
		
		/** Name Column **/
		Column<JsSeresFile, String> contraparteColumn = new Column<JsSeresFile, String>(new TextCell()) {

			@Override
			public String getValue(JsSeresFile object) {
				return object.getRegistryName();
			}
		
		};
		contraparteColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		contraparteColumn.setSortable(true); 
		sortHandler.setComparator(contraparteColumn,new Comparator<JsSeresFile>() {
			
			@Override
			public int compare(JsSeresFile o1, JsSeresFile o2) {
				return o1.getRegistryName().compareTo(o2.getRegistryName());
			}
		});
		dataGrid.getColumnSortList().push(contraparteColumn);
		dataGrid.addColumn(contraparteColumn, "Cliente");
		dataGrid.setColumnWidth(contraparteColumn, 30, Unit.PCT);
	
		/** Status Column **/
		Column<JsSeresFile, String> statusColumn = new Column<JsSeresFile, String>(new TextCell()) {
			@Override	
			public void render(Context context, JsSeresFile object, SafeHtmlBuilder sb) {
//				String icon = AON.AON_CSS.aonIconPointLightGreen();
//				if(object.getSiiStatus().equals("Correcto") || object.getSiiStatus().equals("Pagado")) icon = AON.AON_CSS.aonIconPointGreen();
//				else if(object.getSiiStatus().equals("AceptadoConErrores") || object.getSiiStatus().equals("Parcial")) icon = AON.AON_CSS.aonIconPointOrange();
//				else if(object.getSiiStatus().equals("Incorrecto")) icon= AON.AON_CSS.aonIconPointRed();
//				else if(object.getSiiStatus().equals("Anulada")) icon = AON.AON_CSS.aonIconPointYellow();
//				sb.appendHtmlConstant("<g:Label class=\""+ icon + "\" style=\"padding-left: 16px;\" >"+ "&nbsp;&nbsp;" + object.getSiiStatus());
			}
			
			@Override
			public String getValue(JsSeresFile object) {
//				return object.getSiiStatus();
				return "";
			}
		
		};
		statusColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		statusColumn.setSortable(true); 
		sortHandler.setComparator(statusColumn,new Comparator<JsSeresFile>() {
			
			@Override
			public int compare(JsSeresFile o1, JsSeresFile o2) {
				return o1.getStatus().compareTo(o2.getStatus());
			}
		});
		dataGrid.getColumnSortList().push(statusColumn);
		dataGrid.addColumn(statusColumn, "Estado");
		dataGrid.setColumnWidth(statusColumn, 10, Unit.PCT);
		
	}
	
	public final class CheckboxHeader extends Header {

	    private final MultiSelectionModel<JsSeresFile> selectionModel;
	    private final ListDataProvider<JsSeresFile> provider;

	    public CheckboxHeader(MultiSelectionModel<JsSeresFile> selectionModel,
	    		ListDataProvider<JsSeresFile> provider) {
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
	        parent.getSendAll().setVisible(isChecked);
	        for (JsSeresFile element : provider.getList()) {
	            selectionModel.setSelected(element, isChecked);
	            if(isChecked){
					selFiles.add(element);
				}
	        }
	        if(!isChecked){
	        	selFiles = new LinkedList<>();
	        }
	    }
	}
	
	
	
	// TODO 
	public void send(String action){
		VerticalPanel vp = new VerticalPanel();
		
		HorizontalPanel hp0 = new HorizontalPanel();
		hp0.add(new Label("Tipo de Operacion"));
		ListBox lb0 = new ListBox();
		lb0.addItem("Articulo 70, apartado uno, n\u00famero 7\u00BA, Ley del Impuesto(Ley 37/1992)", "A");
		lb0.addItem("Articulo 16, apartado 2\u00BA, Ley del Impuesto(Ley 37/1992)", "B");
		hp0.add(lb0);
		if("intracomunitarias".equalsIgnoreCase(action)){
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
		hp3.add(l);
		hp3.add(t);
		
		HorizontalPanel hp4 = new HorizontalPanel();
		hp3.addStyleName(AON.AON_CSS.aonPaddingTop());
		Label l4 = new Label("Autorizaci\u00f3n");
		l4.getElement().getStyle().setPaddingTop(5, Unit.PX);
		l4.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		l4.setVisible(false);
		TextBox t4 = new TextBox();t4.setStyleName(AON.AON_CSS.aonInputText());
		t4.setVisible(false);
		hp4.add(l4);
		hp4.add(t4);
		
		CheckBox cb = new CheckBox("Por terceros");
		cb.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				l.setVisible(cb.getValue());
				t.setVisible(cb.getValue());	
				l4.setVisible(cb.getValue());
				t4.setVisible(cb.getValue());	
			}
		});
		vp.add(cb);
		vp.add(hp3);
	//	vp.add(hp4);
		
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
		    	list.add(action);
		    	map.put("option", list);
		    	hide();
		    	
		    	list = new LinkedList<>();
		    	list.add(lb0.getSelectedValue());
		    	map.put("tipo_operacion", list);
	
		    	list = new LinkedList<>();
		    	list.add(cb.getValue() ? t.getValue() : "false");
		    	map.put("terceros", list);
		  /*  	
		    	list = new LinkedList<>();
		    	list.add(cb.getValue() ? t4.getValue() : "");
		    	map.put("auth", list);
		    */
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
						parent.tabLayout.selectTab(0);
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
