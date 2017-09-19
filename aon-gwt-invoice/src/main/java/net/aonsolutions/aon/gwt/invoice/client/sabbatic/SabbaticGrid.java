package net.aonsolutions.aon.gwt.invoice.client.sabbatic;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.api.client.common.JsDataResponseDetail;
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
import com.google.gwt.view.client.MultiSelectionModel;

public class SabbaticGrid extends ResizeComposite implements RequiresResize {
	
	interface GridBinder extends UiBinder<Widget, SabbaticGrid> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);
	
	DataGridResources resources = GWT.create(DataGridResources.class);
	
	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) CustomDataGrid<JsDataResponse> dataGrid; 
	
	
	SabbaticPrincipal parent;
	Integer cont = 0;
	
	private API getAPI() {
		return parent.getAPI();
	}
	
	private HashMap<String, LinkedList<String>> getFilterMap() {
		return parent.getFilterMap();
	}

	public SabbaticGrid(SabbaticPrincipal parent, LinkedList<JsDataResponse> list) {
		this.parent = parent;		
		dataGrid = new CustomDataGrid<JsDataResponse>(Integer.MAX_VALUE, resources,
				JsDataResponse.PROVIDES_KEY);
		ScrollPanel scrollPanel = dataGrid.getScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				if(scrollPanel.getVerticalScrollPosition() >= scrollPanel.getMaximumVerticalScrollPosition()){
					Integer page = 2;
					if(getFilterMap().containsKey("page")){
						page = Integer.parseInt(getFilterMap().get("page").get(0)) + 1;
					}
					LinkedList<String> list = new LinkedList<>();
					list.add(page +"");
					getFilterMap().put("page", list);
					getAPI().getCommon().getDataResponseJS(getFilterMap(), new AsyncCallback<JSON<JsDataResponse>>() {
						
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
	
	LinkedList<JsDataResponse> selFiles = new LinkedList<>();
	private void load(LinkedList<JsDataResponse> list) {
		DefaultKeyboardSelectionHandler<JsDataResponse> selHandler = new DefaultKeyboardSelectionHandler<JsDataResponse>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsDataResponse> event) {
				 if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					 JsDataResponse object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
					 if(event.getColumn() == 0){
						 if(dataGrid.getSelectionModel().isSelected(object))
							 dataGrid.getSelectionModel().setSelected(object, false);
						 else dataGrid.getSelectionModel().setSelected(object, true);
					 } else {
						 for(JsDataResponse f :dataProvider.getList()){
							 dataGrid.getSelectionModel().setSelected(f, false);
						 }
						 dataGrid.getSelectionModel().setSelected(object, true);
					 }
					 
					selFiles = new LinkedList<>();
					for(JsDataResponse f :dataProvider.getList()){
						if(dataGrid.getSelectionModel().isSelected(f)){
							selFiles.add(f);
						}
					}
					parent.getSendButton().setVisible(selFiles.size() > 0);
					parent.getRemoveButton().setVisible(true);
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

		final MultiSelectionModel<JsDataResponse> selectionModel = new MultiSelectionModel<JsDataResponse>(JsDataResponse.PROVIDES_KEY);
		
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsDataResponse> createCheckboxManager());

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
	
	private void initTableColumns(final MultiSelectionModel<JsDataResponse> selectionModel, ListHandler<JsDataResponse> sortHandler) {
		
		/** Check Column **/
		
		Column<JsDataResponse, Boolean> checkColumn = new Column<JsDataResponse, Boolean>(new CheckboxCell(true, true) {
			@Override
			public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Boolean value,
					NativeEvent event, com.google.gwt.cell.client.ValueUpdater<Boolean> valueUpdater) {
			}
		}) {
			
			@Override
			public Boolean getValue(JsDataResponse object) {
				return selectionModel.isSelected(object);
			}
		};

		dataGrid.addColumn(checkColumn, new CheckboxHeader(selectionModel, dataProvider));
		dataGrid.setColumnWidth(checkColumn, 40, Unit.PX);
		
		/** Code Column **/
		Column<JsDataResponse, String> codeColumn = new Column<JsDataResponse, String>(new TextCell()) {

			@Override
			public String getValue(JsDataResponse object) {
				return object.getCode();
			}
		
		};
		codeColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		codeColumn.setSortable(true); 
		sortHandler.setComparator(codeColumn,new Comparator<JsDataResponse>() {
			
			@Override
			public int compare(JsDataResponse o1, JsDataResponse o2) {
				return o1.getCode().compareTo(o2.getCode());
			}
		});
		dataGrid.getColumnSortList().push(codeColumn);
		dataGrid.addColumn(codeColumn, "Codigo");
		dataGrid.setColumnWidth(codeColumn, 15, Unit.PCT);

		/** Date Column **/
		Column<JsDataResponse, String> dateColumn = new Column<JsDataResponse, String>(new TextCell()) {

			@Override
			public String getValue(JsDataResponse object) {
				return object.getDate();
			}
		};
		dateColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		dateColumn.setSortable(true); 
		sortHandler.setComparator(dateColumn,new Comparator<JsDataResponse>() {
			
			@Override
			public int compare(JsDataResponse o1, JsDataResponse o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});
		dataGrid.getColumnSortList().push(dateColumn);
		dataGrid.addColumn(dateColumn, "Fecha");
		dataGrid.setColumnWidth(dateColumn, 15, Unit.PCT);
	
		/** Status Column **/
		
		Column<JsDataResponse, String> statusColumn = new Column<JsDataResponse, String>(new TextCell()) {
			@Override
			public String getValue(JsDataResponse object) {
				Optional<JsDataResponseDetail> detail = object.getDetail().stream().filter(r -> "status".equals(r.getVariable())).findFirst();
				return detail.isPresent() ? detail.get().getValue() : "Pendiente";
			}	
		};
		statusColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		statusColumn.setSortable(false); 
		
		dataGrid.addColumn(statusColumn, "Estado");
		dataGrid.setColumnWidth(statusColumn, 10, Unit.PCT);
	}
	
	public final class CheckboxHeader extends Header {

	    private final MultiSelectionModel<JsDataResponse> selectionModel;
	    private final ListDataProvider<JsDataResponse> provider;

	    public CheckboxHeader(MultiSelectionModel<JsDataResponse> selectionModel,
	    		ListDataProvider<JsDataResponse> provider) {
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
	        parent.getSendButton().setVisible(isChecked);
	        parent.getRemoveButton().setVisible(isChecked);
	        for (JsDataResponse element : provider.getList()) {
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
	
	public void removeAction(){

	}
	
	public void sendAction(){

	}
}
