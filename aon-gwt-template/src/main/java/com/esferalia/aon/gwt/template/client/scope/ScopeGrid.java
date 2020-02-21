package com.esferalia.aon.gwt.template.client.scope;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.sii.JsSiiConfiguration;
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
import com.google.gwt.view.client.MultiSelectionModel;

public class ScopeGrid extends ResizeComposite implements RequiresResize {
	
	interface GridBinder extends UiBinder<Widget, ScopeGrid> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);
	
	DataGridResources resources = GWT.create(DataGridResources.class);
	
	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) CustomDataGrid<JsObject> dataGrid; 
	
	
	ScopePrincipal parent;
	Integer cont = 0;
	
	Boolean isFechaIVA;
	
	private API getAPI() {
		return parent.getAPI();
	}

	public ScopeGrid(ScopePrincipal parent, LinkedList<JsObject> list) {
		this.parent = parent;		
		dataGrid = new CustomDataGrid<JsObject>(Integer.MAX_VALUE, resources,
				JsObject.PROVIDES_KEY);
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
					parent.getAPI().getCommon().getScopes(parent.getFilterMap(), new AsyncCallback<JSON<JsObject>>() {
						
						@Override
						public void onSuccess(JSON<JsObject> result) {
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
	
		getAPI().getSii().getSiiConfiguration(new AsyncCallback<JSON<JsSiiConfiguration>>() {
			
			@Override
			public void onSuccess(JSON<JsSiiConfiguration> result) {
				isFechaIVA = "Fecha IVA".equals(result.getData().get(0).getOperationDate());
				load(list);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
		initWidget(binder.createAndBindUi(this));
	}	
	
	LinkedList<JsObject> selFiles = new LinkedList<>();
	private void load(LinkedList<JsObject> list) {
		DefaultKeyboardSelectionHandler<JsObject> selHandler = new DefaultKeyboardSelectionHandler<JsObject>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsObject> event) {
				 if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					 JsObject object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
					 parent.scopeSelection(object);
					
				 }
			}
		};
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
		addDataDisplay(dataGrid, list);
		ListHandler<JsObject> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
	//	final SingleSelectionModel<JsObject> selectionModel = new SingleSelectionModel<JsObject>(
	//			JsObject.PROVIDES_KEY);
		final MultiSelectionModel<JsObject> selectionModel = new MultiSelectionModel<JsObject>(JsObject.PROVIDES_KEY);
		
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsObject> createCheckboxManager());
	//	dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
		
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<JsObject> dataProvider = new ListDataProvider<JsObject>();

	public void addDataDisplay(HasData<JsObject> display, LinkedList<JsObject> list) {
		dataProvider = new ListDataProvider<JsObject>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsObject> getSortHandler() {
		return new ListHandler<JsObject>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsObject> aux  = super.getList();
				List<JsObject> aux2 = new LinkedList<JsObject>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initTableColumns(final MultiSelectionModel<JsObject> selectionModel, ListHandler<JsObject> sortHandler) {
		

		/** code Column **/
		Column<JsObject, String> descriptionColumn = new Column<JsObject, String>(new TextCell()) {

			@Override
			public String getValue(JsObject object) {
				return object.getName();
			}
		
		};
		descriptionColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		descriptionColumn.setSortable(true); 
		sortHandler.setComparator(descriptionColumn,new Comparator<JsObject>() {
			
			@Override
			public int compare(JsObject o1, JsObject o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		dataGrid.getColumnSortList().push(descriptionColumn);
		dataGrid.addColumn(descriptionColumn, "Descripcion");
		dataGrid.setColumnWidth(descriptionColumn, 15, Unit.PCT);

	}
}
