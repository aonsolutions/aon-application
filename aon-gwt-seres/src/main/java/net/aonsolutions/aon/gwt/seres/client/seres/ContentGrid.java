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

import net.aonsolutions.aon.gwt.seres.shared.CommunicationTarget;
import net.aonsolutions.aon.gwt.seres.shared.SeresFileStatus;

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
//				if(scrollPanel.getVerticalScrollPosition() >= scrollPanel.getMaximumVerticalScrollPosition()){
//					Integer page = 2;
//					if(parent.getFilterMap().containsKey("page")){
//						page = Integer.parseInt(parent.getFilterMap().get("page").get(0)) + 1;
//					}
//					LinkedList<String> list = new LinkedList<>();
//					list.add(page +"");
//					parent.getFilterMap().put("page", list);
//					parent.reloadContentGrid();
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
					
					boolean enable = selFiles.size() > 0;
					if(parent.command.equals(CommunicationTarget.OUTCOME_DELIVERY)
			        		|| parent.command.equals(CommunicationTarget.OUTCOME_INVOICE)){
						parent.getSendAll().setVisible(enable);
					} else if(parent.command.equals(CommunicationTarget.INCOME_SALES)
			        		|| parent.command.equals(CommunicationTarget.INCOME_INVOICE)){
						parent.getRetrieveAll().setVisible(enable);
					} else if(parent.command.equals(CommunicationTarget.INGENET_DELIVERY)){
						parent.getProcessAll().setVisible(enable);
					}
					
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
		
		/** Code Column **/
		Column<JsSeresFile, String> referenceCodeColumn = new Column<JsSeresFile, String>(new TextCell()) {

			@Override
			public String getValue(JsSeresFile object) {
				return object.getReferenceCode();
			}
		};
		referenceCodeColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		referenceCodeColumn.setSortable(true); 
		sortHandler.setComparator(referenceCodeColumn,new Comparator<JsSeresFile>() {
			
			@Override
			public int compare(JsSeresFile o1, JsSeresFile o2) {
				return o1.getReferenceCode().compareTo(o2.getReferenceCode());
			}
		});

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

		/** Name Column **/
		Column<JsSeresFile, String> customerColumn = new Column<JsSeresFile, String>(new TextCell()) {

			@Override
			public String getValue(JsSeresFile object) {
				return object.getRegistryName();
			}
		};
		customerColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		customerColumn.setSortable(true); 
		sortHandler.setComparator(customerColumn,new Comparator<JsSeresFile>() {
			
			@Override
			public int compare(JsSeresFile o1, JsSeresFile o2) {
				return o1.getRegistryName().compareTo(o2.getRegistryName());
			}
		});
		
		/** Status Column **/
		Column<JsSeresFile, String> statusColumn = new Column<JsSeresFile, String>(new TextCell()) {
			@Override	
			public void render(Context context, JsSeresFile object, SafeHtmlBuilder sb) {
				String icon = AON.AON_CSS.aonIconPointLightGreen();
				if(object.getStatus().equals(SeresFileStatus.PENDING.getDescription())) icon = AON.AON_CSS.aonIconQuestion();
				else if(object.getStatus().equals(SeresFileStatus.SEND.getDescription())) icon = AON.AON_CSS.aonIconPointLightGreen();
				else if(object.getStatus().equals(SeresFileStatus.CANCEL.getDescription())) icon = AON.AON_CSS.aonIconPointYellow();
				else if(object.getStatus().equals(SeresFileStatus.ACCEPT.getDescription())) icon = AON.AON_CSS.aonIconPointGreen();
//				else if(object.getStatus().equals("AceptadoConErrores")) icon = AON.AON_CSS.aonIconPointOrange();
				else if(object.getStatus().equals(SeresFileStatus.FAIL.getDescription())) icon= AON.AON_CSS.aonIconPointRed();
				sb.appendHtmlConstant("<g:Label class=\""+ icon + "\" style=\"padding-left: 16px;\" >"+ "&nbsp;&nbsp;" + object.getStatus());
			}
			
			@Override
			public String getValue(JsSeresFile object) {
				return object.getStatus();
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
		
		dataGrid.addColumn(checkColumn, new CheckboxHeader(selectionModel, dataProvider));
//		dataGrid.addColumn(checkColumn, "");
		dataGrid.addColumn(referenceCodeColumn, AON.MSG.number());
		dataGrid.addColumn(taxDateColumn, AON.MSG.date());
		dataGrid.addColumn(customerColumn, AON.MSG.customer());
		dataGrid.addColumn(statusColumn, AON.MSG.status());
		
		dataGrid.getColumnSortList().push(taxDateColumn);
		dataGrid.getColumnSortList().push(customerColumn);
		dataGrid.getColumnSortList().push(statusColumn);
		dataGrid.getColumnSortList().push(referenceCodeColumn);
		
		dataGrid.setColumnWidth(checkColumn, 40, Unit.PX);
		dataGrid.setColumnWidth(referenceCodeColumn, 15, Unit.PCT);
		dataGrid.setColumnWidth(taxDateColumn, 15, Unit.PCT);
		dataGrid.setColumnWidth(customerColumn, 55, Unit.PCT);
		dataGrid.setColumnWidth(statusColumn, 15, Unit.PCT);		
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
	        
	        if(parent.command==CommunicationTarget.OUTCOME_DELIVERY
	        		|| parent.command==CommunicationTarget.OUTCOME_INVOICE){
	        	parent.getSendAll().setVisible(isChecked);
			} else if(parent.command==CommunicationTarget.INCOME_SALES
	        		|| parent.command==CommunicationTarget.INCOME_INVOICE){
	        	parent.getRetrieveAll().setVisible(isChecked);
			} else if(parent.command==CommunicationTarget.INGENET_DELIVERY){
				parent.getProcessAll().setVisible(isChecked);
			}
	        
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
		if(action.equals(CommunicationTarget.OUTCOME_DELIVERY.getValue())){
			sendDeliveries();
		} else if(action.equals(CommunicationTarget.OUTCOME_INVOICE.getValue())){
			sendInvoices();
		}
	}
	
	public void retrieve(String action) {
		if(action.equals(CommunicationTarget.INCOME_SALES.getValue())){
			retrieveSales();
		} else if(action.equals(CommunicationTarget.INCOME_INVOICE.getValue())){
			retrieveDeliveries();
		}
	}
	
	public void process(String action) {
		if(action.equals(CommunicationTarget.INGENET_DELIVERY.getValue())){
			// nada
		}
	}

	private void retrieveDeliveries() {
		// TODO Auto-generated method stub
		Window.alert("RECUPERAR ALBARANES \n En desarrollo...");
		
	}

	private void retrieveSales() {
		// TODO Auto-generated method stub
		Window.alert("RECUPERAR PEDIDOS \n En desarrollo...");
		
	}

	private void sendDeliveries() {
		VerticalPanel vp = new VerticalPanel();
		if(selFiles.size()==1)
			vp.add(new Label("Albaran n. " + selFiles.get(0).getReferenceCode()));
		else
			vp.add(new Label("Total albaranes: " + selFiles.size()));
		
		AonDialog dialog = new AonDialog("Enviar datos", vp) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				HashMap<String, LinkedList<String>> map =  new HashMap<>();
				LinkedList<String> list = selFiles.stream().map(s -> s.getId() + "").collect(Collectors.toCollection(LinkedList::new));
				map.put("id_list", list);
				parent.consoleLog("selFiles.size -> " + list.size());
				
				getAPI().getSeres().sendDeliveries(map, new AsyncCallback<JSON<JsObject>>() {
					
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
				map.put("id_list", list);
				parent.consoleLog("selFiles.size -> " + list.size());
				
				getAPI().getSeres().sendInvoices(map, new AsyncCallback<JSON<JsObject>>() {
					
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
