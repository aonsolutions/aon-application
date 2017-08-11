package net.aonsolutions.aon.gwt.udapa.client.quality;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
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

import net.aonsolutions.aon.gwt.udapa.client.Utils;

public class GridPanel extends ResizeComposite implements RequiresResize {

	interface GridBinder extends UiBinder<Widget, GridPanel> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);
	
	DataGridResources resources = GWT.create(DataGridResources.class);
	
	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) CustomDataGrid<JsDataResponse> dataGrid; 
	
	QualityPrincipal parent;
	Integer cont = 0;

	public GridPanel(QualityPrincipal parent, LinkedList<JsDataResponse> list) {
		this.parent = parent;		
		dataGrid = new CustomDataGrid<JsDataResponse>(Integer.MAX_VALUE, resources,
				JsDataResponse.PROVIDES_KEY);
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
					parent.getAPI().getCommon().getDataResponse(parent.getFilterMap(), new AsyncCallback<JSON<JsDataResponse>>() {
						
						@Override
						public void onSuccess(JSON<JsDataResponse> result) {
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
	
	private void load(LinkedList<JsDataResponse> list) {
		DefaultKeyboardSelectionHandler<JsDataResponse> selHandler = new DefaultKeyboardSelectionHandler<JsDataResponse>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsDataResponse> event) {
				if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
				    JsDataResponse object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());				    
				    LinkedList<String> list = new LinkedList<>();
				    list.add((dataGrid.getKeyboardSelectedRow()+1) + "");
				    HashMap<String, LinkedList<String>> map = parent.getFilterMap();
				    map.put("page", list);
				    parent.setFilterMap(map);
				    parent.selectDataResponse(object);
				}		
			}
		};
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
		addDataDisplay(dataGrid, list);
		ListHandler<JsDataResponse> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
		final SingleSelectionModel<JsDataResponse> selectionModel = new SingleSelectionModel<JsDataResponse>(
 				JsDataResponse.PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsDataResponse> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<JsDataResponse> dataProvider = new ListDataProvider<JsDataResponse>();

	public void addDataDisplay(HasData<JsDataResponse> display, LinkedList<JsDataResponse> list) {
		dataProvider = new ListDataProvider<JsDataResponse>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsDataResponse> getSortHandler() {
		return new ListHandler<JsDataResponse>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsDataResponse> aux  = super.getList();
				List<JsDataResponse> aux2 = new LinkedList<JsDataResponse>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initTableColumns(final SelectionModel<JsDataResponse> selectionModel, ListHandler<JsDataResponse> sortHandler) {
		/** ISSUE DATE Column **/
		Column<JsDataResponse,String> issueDateColumn = new Column<JsDataResponse, String>(new TextCell()) {
			
			@Override
			public String getValue(JsDataResponse object) {
				if(object.getIssueDate() != null && !"".equals(object.getIssueDate())){
					Date date = Utils.parseDateTime(object.getIssueDate());
					String dateStr = Utils.formatDate(date);
					return dateStr;
				}
				return "-";
			}
		};
		
		issueDateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		issueDateColumn.setSortable(true); 
		sortHandler.setComparator(issueDateColumn,new Comparator<JsDataResponse>() {
			
			@Override
			public int compare(JsDataResponse o1, JsDataResponse o2) {
				return o1.getIssueDate().compareTo(o2.getIssueDate());
			}
		});
		dataGrid.getColumnSortList().push(issueDateColumn);
		dataGrid.addColumn(issueDateColumn, AON.MSG.issueDate());
		dataGrid.setColumnWidth(issueDateColumn,10, Unit.PCT);

		
		/** Number Column **/
		Column<JsDataResponse, String> nameColumn = new Column<JsDataResponse, String>(new TextCell()) {

			@Override
			public String getValue(JsDataResponse object) {
				return object.getNumber();
			}
		
		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		nameColumn.setSortable(true); 
		sortHandler.setComparator(nameColumn,new Comparator<JsDataResponse>() {
			
			@Override
			public int compare(JsDataResponse o1, JsDataResponse o2) {
				String a = o1.getNumber(); 
				String b = o2.getNumber(); 
				return a.compareTo(b);
			}
		});
		dataGrid.getColumnSortList().push(nameColumn);
		dataGrid.addColumn(nameColumn, AON.MSG.number());
		dataGrid.setColumnWidth(nameColumn, 10, Unit.PCT);
		
		/** Supplier Column - Columna Proveedores **/
		Column<JsDataResponse, String> supplierColumn = new Column<JsDataResponse, String>(new TextCell()) {

			@Override
			public String getValue(JsDataResponse object) {
				return object.getSupplier().getName();
			}
		
		};
		supplierColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		supplierColumn.setSortable(true); 
		sortHandler.setComparator(supplierColumn,new Comparator<JsDataResponse>() {
			
			@Override
			public int compare(JsDataResponse o1, JsDataResponse o2) {
				return o1.getSupplier().getName().compareTo(o2.getSupplier().getName());
			}
		});
		dataGrid.getColumnSortList().push(supplierColumn);
		dataGrid.addColumn(supplierColumn, "Proveedor");
		dataGrid.setColumnWidth(supplierColumn, 15, Unit.PCT);
		
		/** Product Column **/
		Column<JsDataResponse, String> productColumn = new Column<JsDataResponse, String>(new TextCell()) {

			@Override
			public String getValue(JsDataResponse object) {
				return object.getProduct();
			}
		
		};
		productColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		productColumn.setSortable(false); 
		sortHandler.setComparator(productColumn,new Comparator<JsDataResponse>() {
			
			@Override
			public int compare(JsDataResponse o1, JsDataResponse o2) {
				String a = o1.getNumber(); 
				String b = o2.getNumber(); 
				return a.compareTo(b);
			}
		});
		dataGrid.getColumnSortList().push(productColumn);
		dataGrid.addColumn(productColumn, "Producto");
		dataGrid.setColumnWidth(productColumn, 15, Unit.PCT);
		
	}
	
}
