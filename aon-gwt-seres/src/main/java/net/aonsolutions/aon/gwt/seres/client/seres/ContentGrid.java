package net.aonsolutions.aon.gwt.seres.client.seres;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.seres.JsSeresFile;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
		this(parent, new LinkedList<>());	
	}

	public ContentGrid(SeresPrincipal parent, List<JsSeresFile> list) {
		this.parent = parent;		
		dataGrid = new CustomDataGrid<JsSeresFile>(Integer.MAX_VALUE, resources,
				JsSeresFile.PROVIDES_KEY);
		ScrollPanel scrollPanel = dataGrid.getScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				// TODO
				if(scrollPanel.getVerticalScrollPosition() >= scrollPanel.getMaximumVerticalScrollPosition()){
					Integer page = 2;
					if(parent.getFilterMap().containsKey("page")){
						page = Integer.parseInt(parent.getFilterMap().get("page").get(0)) + 1;
					}
					LinkedList<String> list = new LinkedList<>();
					list.add(page +"");
					parent.getFilterMap().put("page", list);
					parent.reloadContentGrid();
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
	
	LinkedList<JsSeresFile> selFiles = new LinkedList<>();
	
	private void load(List<JsSeresFile> list) {
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
//					String sii = parent.getFilterMap().get("sii").get(0);
//					parent.getBaja().setVisible(selFiles.size() > 0 && !"cp_cobros_pagos".equals(sii)
//							&& !"cp_cobros".equals(sii) && !"cp_pagos".equals(sii));
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
		final MultiSelectionModel<JsSeresFile> selectionModel = new MultiSelectionModel<JsSeresFile>(JsSeresFile.PROVIDES_KEY);
		
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsSeresFile> createCheckboxManager());
		initTableColumns(selectionModel, sortHandler);
		
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<JsSeresFile> dataProvider = new ListDataProvider<JsSeresFile>();
	
	public void addAll(List<JsSeresFile> list){
		dataProvider.getList().addAll(list);
	}

	public void addDataDisplay(HasData<JsSeresFile> display, List<JsSeresFile> list) {
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

//		dataGrid.addColumn(checkColumn, new CheckboxHeader(selectionModel, dataProvider));
		dataGrid.addColumn(checkColumn, "");
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
				return object.getDate().replaceAll("-", "/");
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
		dataGrid.setColumnWidth(contraparteColumn, 40, Unit.PCT);
	
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
	
	public final class CheckboxHeader extends Header<Boolean> {

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
	        
	        if(parent.command.equals(parent.parent.OUTCOME_DELIVERY)
	        		|| parent.command.equals(parent.parent.OUTCOME_INVOICE)){
	        	parent.getSendAll().setVisible(isChecked);
			} else if(parent.command.equals(parent.parent.INCOME_SALES)
	        		|| parent.command.equals(parent.parent.INCOME_INVOICE)){
	        	parent.getRetrieveAll().setVisible(isChecked);
			} else if(parent.command.equals(parent.parent.INGENET_DELIVERY)){
				// TODO
			}
	        
//	        parent.getBaja().setVisible(isChecked);
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
	
	
	public void send(String action) {
		if(action.equals(parent.parent.OUTCOME_DELIVERY)){
			Window.alert("En desarrollo...");
//			sendDeliveries();
		} else if(action.equals(parent.parent.OUTCOME_INVOICE)){
			Window.alert("En desarrollo...");
//			sendInvoices();
//		} else if(parent.command.equals(SeresMain.INGENET_DELIVERY)){
//			Window.alert("En desarrollo...");
		}
	}
	
	public void retrieve(String action) {
		if(action.equals(parent.parent.INCOME_SALES)){
			Window.alert("En desarrollo...");
//			retrieveSales();
		} else if(action.equals(parent.parent.INCOME_INVOICE)){
			Window.alert("En desarrollo...");
//			retrieveDeliveries();
		}
	}

	private void retrieveDeliveries() {
		// TODO Auto-generated method stub
		
	}

	private void retrieveSales() {
		// TODO Auto-generated method stub
		
	}

	private void sendDeliveries() {
		// TODO Auto-generated method stub
		
	}

	// TODO 
	private void sendInvoices() {
		VerticalPanel vp = new VerticalPanel();
		if(selFiles.size()==1)
			vp.add(new Label("Factura n. " + selFiles.get(0).getReferenceCode()));
		else
			vp.add(new Label("Total facturas: " + selFiles.size()));
		
		
		AonDialog dialog = new AonDialog("Enviar datos", vp) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				HashMap<String, LinkedList<String>> map =  new HashMap<>();
				LinkedList<String> list = selFiles.stream().map(s -> s.getId() + "").collect(Collectors.toCollection(LinkedList::new));
				map.put("id", list);
//		    	getAPI().getFinance().sendSii(map, new AsyncCallback<JSON<JsObject>>() {
//					
//					@Override
//					public void onSuccess(JSON<JsObject> result) {
//						VerticalPanel vp = new VerticalPanel();
//						result.getData().stream().forEach(r -> {
//							Label label = new Label(r.getName());
//							String str = r.getId() + "";
//							String color = "red";
//							if(str.equals("200")) color = "green";
//							else if(str.substring(0, 1).equals("2")) color = "orange";
//							label.getElement().getStyle().setColor(color);
//							vp.add(label);
//						});
//						parent.errorPanel.setWidget(vp);
//						parent.tabLayout.selectTab(0);
//						parent.openFootPanel();
//						parent.gridContent();
//					}
//					
//					@Override
//					public void onFailure(Throwable caught) {
//						
//					}
//				});
			}
		};
		dialog.center();
	}
}
