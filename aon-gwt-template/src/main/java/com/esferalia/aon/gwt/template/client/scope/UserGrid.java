package com.esferalia.aon.gwt.template.client.scope;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsUser;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
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
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
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
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperItem;

public class UserGrid extends ResizeComposite implements RequiresResize {
	
	interface GridBinder extends UiBinder<Widget, UserGrid> {
	}

	private static final GridBinder binder = GWT.create(GridBinder.class);
	
	DataGridResources resources = GWT.create(DataGridResources.class);
	
	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) CustomDataGrid<JsUser> dataGrid; 
	
	
	ScopePrincipal parent;
	Integer cont = 0;
		
	private API getAPI() {
		return parent.getAPI();
	}

	public UserGrid(ScopePrincipal parent, LinkedList<JsUser> list) {
		this.parent = parent;		
		dataGrid = new CustomDataGrid<JsUser>(Integer.MAX_VALUE, resources,
				JsUser.PROVIDES_KEY);
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
					parent.getAPI().getCommon().getUsers(parent.getFilterMap(), new AsyncCallback<JSON<JsUser>>() {
						
						@Override
						public void onSuccess(JSON<JsUser> result) {
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
	
	LinkedList<JsUser> selFiles = new LinkedList<>();
	private void load(LinkedList<JsUser> list) {
		DefaultKeyboardSelectionHandler<JsUser> selHandler = new DefaultKeyboardSelectionHandler<JsUser>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<JsUser> event) {
				 if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					 JsUser object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
					 parent.userSelection(object);
					
				 }
			}
		};
		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label("NO HAY DATOS DISPONIBLES"));
		addDataDisplay(dataGrid, list);
		ListHandler<JsUser> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
	//	final SingleSelectionModel<JsUser> selectionModel = new SingleSelectionModel<JsUser>(
	//			JsUser.PROVIDES_KEY);
		final MultiSelectionModel<JsUser> selectionModel = new MultiSelectionModel<JsUser>(JsUser.PROVIDES_KEY);
		
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<JsUser> createCheckboxManager());
	//	dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
		
	}
	
	//------------------------------ DataGrid Utils
	
	private ListDataProvider<JsUser> dataProvider = new ListDataProvider<JsUser>();

	public void addDataDisplay(HasData<JsUser> display, LinkedList<JsUser> list) {
		dataProvider = new ListDataProvider<JsUser>(list);
		dataProvider.addDataDisplay(display);
	}
		
	private ListHandler<JsUser> getSortHandler() {
		return new ListHandler<JsUser>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<JsUser> aux  = super.getList();
				List<JsUser> aux2 = new LinkedList<JsUser>();
				for(Integer i = 0 ; i< aux.size()-1;i++){
					aux2.set(i, aux.get(aux.size()-1-i ));
				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private class ActionHasCell implements HasCell<JsUser, JsUser> {
	    private ActionCell<JsUser> cell;
	    String s;
	    
	    public ActionHasCell(String text, Delegate<JsUser> delegate) {
	    	s = text;
	        cell = new ActionCell<JsUser>(text, delegate){
	        	String text = s;
	        	@Override
	        	public void render(com.google.gwt.cell.client.Cell.Context context,
	        			JsUser value, SafeHtmlBuilder sb) {
	        		
//	        		if(value.getScope().getId() != null && text.equals("edit")){
//        				sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-edit\" tabindex=\"-1\">");
//						sb.appendHtmlConstant("</button>");
//	        		}
	        		
	        		if(text.equals("delete")){
        				sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-delete\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");
	        		}
	        		
	        		if(text.equals("copy")){
	        			sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-reset\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");		
	        		}
	        		
	        	}
	        };
	        
	    }

	    @Override
	    public Cell<JsUser> getCell() {
	        return cell;
	    }

	    @Override
	    public FieldUpdater<JsUser, JsUser> getFieldUpdater() {
	        return null;
	    }

	    @Override
	    public JsUser getValue(JsUser object) {
	        return object;
	    }
	}
	
	private void initTableColumns(final MultiSelectionModel<JsUser> selectionModel, ListHandler<JsUser> sortHandler) {
		List<HasCell<JsUser, ?>> cells = new LinkedList<HasCell<JsUser, ?>>();
		
		cells.add(new ActionHasCell("delete", new Delegate<JsUser>() {

	        @Override
	        public void execute(JsUser object) {
	        	action(object, false);
	        }
	    }));
		
	    cells.add(new ActionHasCell("copy", new Delegate<JsUser>() {

	        @Override
	        public void execute(JsUser object) {
	        	action(object, true);
	        }
	    }));
	    
		
		CompositeCell<JsUser> cell = new CompositeCell<JsUser>(cells);
			
		/** Name Column **/
		Column<JsUser, String> nameColumn = new Column<JsUser, String>(new TextCell()) {

			@Override
			public String getValue(JsUser object) {
				return object.getLogin();
			}
		
		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		nameColumn.setSortable(true); 
		sortHandler.setComparator(nameColumn, new Comparator<JsUser>() {
			
			@Override
			public int compare(JsUser o1, JsUser o2) {
				return o1.getLogin().compareTo(o2.getLogin());
			}
		});
		dataGrid.getColumnSortList().push(nameColumn);
		dataGrid.addColumn(nameColumn, "Usuario");
		dataGrid.setColumnWidth(nameColumn, 25, Unit.PCT);
		
		/** Action Column **/
		Column<JsUser,JsUser> actionColumn = 	new Column<JsUser, JsUser>(cell){

			
			@Override
			public JsUser getValue(JsUser object) {
				return object;
			}
		};
		actionColumn.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		dataGrid.addColumn(actionColumn, "");
		dataGrid.setColumnWidth(actionColumn, 5, Unit.PCT);
	}
	
	Integer user;
	private void action(JsUser object, Boolean isCopy) {	
		parent.getAPI().getIncidence().getApplicationUsers(new AsyncCallback<JSON<JsUser>>() {
			
			@Override
			public void onSuccess(JSON<JsUser> result) {
				
				ScrollPanel sp = new ScrollPanel();
				sp.setWidth("100%");
				sp.setHeight("200px");
				VerticalPanel vp = new VerticalPanel();
				vp.setWidth("100%");

				PaperInput pi = new PaperInput();
				pi.setPlaceholder("Filtro");
				pi.addDomHandler(new KeyUpHandler() {
					
					@Override
					public void onKeyUp(KeyUpEvent event) {
						for (Integer i = 1; i < vp.getWidgetCount(); i++) {
							PaperItem pitem = (PaperItem) vp.getWidget(i);
							Label label = (Label) pitem.getWidget(1);
							pitem.setVisible(label.getText().contains(pi.getValue()));
						}
					}
				}, KeyUpEvent.getType());
				vp.add(pi);
				result.getData().stream().forEach(r-> {
					PaperItem pi2 = new PaperItem();
					IronIcon ironIcon = new IronIcon();
					ironIcon.setIcon("account-box");
					ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
					pi2.add(ironIcon);
					pi2.add(new Label(r.getLogin()));
					pi2.setStyle("min-height:24px;font-size:12px;padding:0px;");

					pi2.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							for(Integer k = 1; k < vp.getWidgetCount(); k++ ) {
								PaperItem pi3 = (PaperItem) vp.getWidget(k);
								if(pi3.getWidgetCount() > 2) pi3.remove(2);
							}
							user = r.getId();
							IronIcon ii = new IronIcon();
							ii.setIcon("check");
							ii.addStyleName(AON.AON_CSS.aonMinWidth24());
							ii.getElement().getStyle().setPosition(Position.ABSOLUTE);
							ii.getElement().getStyle().setRight(15, Unit.PX);
							pi2.add(ii);
						}
					});
					vp.add(pi2);
				});
				sp.add(vp);
				AonDialog d= new AonDialog(isCopy ? "Copiar \u00c1mbitos de" : "Desvincular \u00c1mbitos de" , sp) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						hide();
						if(isCopy) {
							getAPI().getCommon().copyUserScope(object.getId(), user,  new AsyncCallback<JSON<JsUser>>() {
								@Override public void onSuccess(JSON<JsUser> result) {}
								@Override public void onFailure(Throwable caught) {}
							});
						} else {
							getAPI().getCommon().deleteUserScope(object.getId(), user,  new AsyncCallback<JSON<JsUser>>() {
								@Override public void onSuccess(JSON<JsUser> result) {}
								@Override public void onFailure(Throwable caught) {}
							});
						}
						
					}
				};
				d.getElement().getStyle().setWidth(255, Unit.PX);
				d.getElement().getStyle().setHeight(300, Unit.PX);	
				d.center();	
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
	}
	
}
