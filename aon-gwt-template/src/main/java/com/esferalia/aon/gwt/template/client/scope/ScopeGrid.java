package com.esferalia.aon.gwt.template.client.scope;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsCompany;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
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
import com.google.gwt.user.client.Window;
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
	
	@UiField(provided = true) CustomDataGrid<JsCompany> dataGrid; 
	
	
	ScopePrincipal parent;
	Integer cont = 0;
		
	private API getAPI() {
		return parent.getAPI();
	}

	public ScopeGrid(ScopePrincipal parent, LinkedList<JsCompany> list) {
		this.parent = parent;		
		dataGrid = new CustomDataGrid<JsCompany>(Integer.MAX_VALUE, resources,
				JsCompany.PROVIDES_KEY);
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
					parent.getAPI().getCommon().getCompanies(parent.getFilterMap(), new AsyncCallback<JSON<JsCompany>>() {
						
						@Override
						public void onSuccess(JSON<JsCompany> result) {
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
	
	LinkedList<JsCompany> selFiles = new LinkedList<>();
	private void load(LinkedList<JsCompany> list) {
		DefaultKeyboardSelectionHandler<JsCompany> selHandler = new DefaultKeyboardSelectionHandler<JsCompany>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsCompany> event) {
				 if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					 JsCompany object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
					 parent.companySelection(object);
					
				 }
			}
		};
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
		addDataDisplay(dataGrid, list);
		ListHandler<JsCompany> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
	//	final SingleSelectionModel<JsCompany> selectionModel = new SingleSelectionModel<JsCompany>(
	//			JsCompany.PROVIDES_KEY);
		final MultiSelectionModel<JsCompany> selectionModel = new MultiSelectionModel<JsCompany>(JsCompany.PROVIDES_KEY);
		
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsCompany> createCheckboxManager());
	//	dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
		
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<JsCompany> dataProvider = new ListDataProvider<JsCompany>();

	public void addDataDisplay(HasData<JsCompany> display, LinkedList<JsCompany> list) {
		dataProvider = new ListDataProvider<JsCompany>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsCompany> getSortHandler() {
		return new ListHandler<JsCompany>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsCompany> aux  = super.getList();
				List<JsCompany> aux2 = new LinkedList<JsCompany>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private class ActionHasCell implements HasCell<JsCompany, JsCompany> {
	    private ActionCell<JsCompany> cell;
	    String s;
	    
	    public ActionHasCell(String text, Delegate<JsCompany> delegate) {
	    	s = text;
	        cell = new ActionCell<JsCompany>(text, delegate){
	        	String text = s;
	        	@Override
	        	public void render(com.google.gwt.cell.client.Cell.Context context,
	        			JsCompany value, SafeHtmlBuilder sb) {
	        		
//	        		if(value.getScope().getId() != null && text.equals("edit")){
//        				sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-edit\" tabindex=\"-1\">");
//						sb.appendHtmlConstant("</button>");
//	        		}
	        		
	        		if(value.getScope().getId() == null && text.equals("new")){
	        			sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-reset\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");		
	        		}
	        		
	        	}
	        };
	        
	    }

	    @Override
	    public Cell<JsCompany> getCell() {
	        return cell;
	    }

	    @Override
	    public FieldUpdater<JsCompany, JsCompany> getFieldUpdater() {
	        return null;
	    }

	    @Override
	    public JsCompany getValue(JsCompany object) {
	        return object;
	    }
	}
	
	private void initTableColumns(final MultiSelectionModel<JsCompany> selectionModel, ListHandler<JsCompany> sortHandler) {
	
		List<HasCell<JsCompany, ?>> cells = new LinkedList<HasCell<JsCompany, ?>>();
	    
		cells.add(new ActionHasCell("edit", new Delegate<JsCompany>() {

	        @Override
	        public void execute(JsCompany object) {
	           // EDIT CODE
	        	edit(object);
	        }
	    }));
		
	    cells.add(new ActionHasCell("new", new Delegate<JsCompany>() {

	        @Override
	        public void execute(JsCompany object) {
	        	nuevo(object);
	        }
	    }));
	    
		
		CompositeCell<JsCompany> cell = new CompositeCell<JsCompany>(cells);
			
		/** Name Column **/
		Column<JsCompany, String> nameColumn = new Column<JsCompany, String>(new TextCell()) {

			@Override
			public String getValue(JsCompany object) {
				return object.getName();
			}
		
		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		nameColumn.setSortable(true); 
		sortHandler.setComparator(nameColumn, new Comparator<JsCompany>() {
			
			@Override
			public int compare(JsCompany o1, JsCompany o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		dataGrid.getColumnSortList().push(nameColumn);
		dataGrid.addColumn(nameColumn, "Razon Social");
		dataGrid.setColumnWidth(nameColumn, 15, Unit.PCT);

		/** Document Column **/
		Column<JsCompany, String> documentColumn = new Column<JsCompany, String>(new TextCell()) {

			@Override
			public String getValue(JsCompany object) {
				return object.getDocument();
			}
		
		};
		documentColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		documentColumn.setSortable(true); 
		sortHandler.setComparator(documentColumn,new Comparator<JsCompany>() {
			
			@Override
			public int compare(JsCompany o1, JsCompany o2) {
				return o1.getDocument().compareTo(o2.getDocument());
			}
		});
		dataGrid.getColumnSortList().push(documentColumn);
		dataGrid.addColumn(documentColumn, "NIF");
		dataGrid.setColumnWidth(documentColumn, 15, Unit.PCT);
		
		/** Scope Column **/
		Column<JsCompany, String> scopeColumn = new Column<JsCompany, String>(new TextCell()) {

			@Override
			public String getValue(JsCompany object) {
				return object.getScope().getName();
			}
		
		};
		scopeColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		scopeColumn.setSortable(true); 
		sortHandler.setComparator(scopeColumn,new Comparator<JsCompany>() {
			
			@Override
			public int compare(JsCompany o1, JsCompany o2) {
				return o1.getScope().getName().compareTo(o2.getScope().getName());
			}
		});
		dataGrid.getColumnSortList().push(scopeColumn);
		dataGrid.addColumn(scopeColumn, "Ambito");
		dataGrid.setColumnWidth(scopeColumn, 15, Unit.PCT);
		

		/** Action Column **/
		Column<JsCompany,JsCompany> actionColumn = 	new Column<JsCompany, JsCompany>(cell){

			
			@Override
			public JsCompany getValue(JsCompany object) {
				return object;
			}
		};
		actionColumn.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		dataGrid.addColumn(actionColumn, "");
		dataGrid.setColumnWidth(actionColumn, 5, Unit.PCT);

	}
	
	private void edit(JsCompany object) {

	}
	
	private void nuevo(JsCompany object) {	
		getAPI().getCommon().generateCompanyScope(object.getId(), new AsyncCallback<JSON<JsCompany>>() {
			
			@Override
			public void onSuccess(JSON<JsCompany> result) {
				parent.gridContent();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
	}
}
