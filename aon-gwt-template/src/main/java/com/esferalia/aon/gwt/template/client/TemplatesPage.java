package com.esferalia.aon.gwt.template.client;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.template.client.i18n.TemplatesMessages;
import com.esferalia.aon.gwt.template.shared.Dialog;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractHasData.DefaultKeyboardSelectionHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class TemplatesPage extends Composite{
	
	class DocumentContextMenu extends ContextMenu {

		ScheduledCommand viewCommand = new ScheduledCommand() {
			public void execute() {
				//TODO VISUALIZAR EXCEL!!
			};
		};		

		ScheduledCommand editCommand = new ScheduledCommand() {
			public void execute() {
				TemplateInfo object;
				object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				edit(object);
			};
		};
		
		ScheduledCommand removeCommand = new ScheduledCommand() {
			public void execute() {
				TemplateInfo object;
				object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				delete(object);
			};
		};
		
		ScheduledCommand downloadCommand = new ScheduledCommand() {
			public void execute() {	
				TemplateInfo object;
				object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				download(object);
			};
		};
		
		//private MenuItem viewItem;
		private MenuItem editItem;
		private MenuItem removeItem;
		private MenuItem downloadItem;
		private Integer heigth;
		private Integer width;

		public Integer getHeigth() {
			heigth = 74;
			return heigth;
		}

		public void setHeigth(Integer heigth) {
			this.heigth = heigth;
		}

		public Integer getWidth() {
			width = 117;
			return width;
		}

		public void setWidth(Integer width) {
			this.width = width;
		}
		
		public DocumentContextMenu(TemplateInfo object){
			if(!object.getIsParent()){
				/*viewItem = addItem("Visualizar",viewCommand,
						"aon-icon-open-popup",AON.AON_ICON_CMD_BUTTON);
				viewItem.setEnabled(true);
				addSeparator();*/
			
				editItem = addItem("Editar", editCommand,
						"aon-icon-edit", AON.AON_ICON_CMD_BUTTON);
				editItem.setEnabled(true);
				removeItem = addItem("Borrar", removeCommand,
						AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);
				removeItem.setEnabled(true);
				addSeparator();
				downloadItem = addItem("Descargar",downloadCommand,
						"aon-icon-google-drive-excel",AON.AON_ICON_CMD_BUTTON);
				downloadItem.setEnabled(true);
			}
		}

		@Override
		public void show() {
			super.show();
		}
	}
	
	final ITemplateAsync item = GWT.create(ITemplate.class);
	private static final TemplatesMessages MSG = GWT.create(TemplatesMessages.class);

	interface PageBinder extends UiBinder<Widget, TemplatesPage> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);
	DataGridResources resources = GWT.create(DataGridResources.class);

	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
		Style dataGridStyle();
	}
	
	@UiField(provided = true) DataGrid<TemplateInfo> dataGrid; 
	
	@UiField Button new_button;
	@UiField Button edit_button;
	@UiField Button delete_button;
	
	@UiField(provided = true) TextBox nameSearchBox;
	@UiField(provided = true) TextBox typeSearchBox;
	@UiField Button nameSearchButton;
	@UiField Button typeSearchButton;
	
	AonData aonData;
	Integer column = 1;
	ListBox list_box = new ListBox();
	LinkedList<TemplateInfo> templateList;
	TemplatesDialog popup;
	
	public TemplatesPage(AonData aonData, LinkedList<TemplateInfo> template_list) {
		this.aonData = aonData;
		this.templateList = template_list;
		
		new_button = new Button();
		edit_button = new Button();
		delete_button = new Button();
		
		nameSearchButton = new Button();
		typeSearchButton = new Button();
		nameSearchBox = new TextBox();
		typeSearchBox = new TextBox();
		dataGrid = new DataGrid<TemplateInfo>(Integer.MAX_VALUE, resources,
				TemplateInfo.PROVIDES_KEY);
		
		Widget ui = pageBinder.createAndBindUi(this);
	
		RootLayoutPanel.get(getAonData().getRootPanel()).add(ui);
		Load();
	}
	
	public AonData getAonData() {
		return aonData;
	}
	
	public Domain getDomain(){
		return getAonData().getDomain();
	}
	
	public User getUser(){
		return getAonData().getUser();
	}
	
	public void Load() {
		nameSearchBox.addBitlessDomHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				nameSearchButton.click();
			}
		}, ChangeEvent.getType());
		
		nameSearchBox.addKeyUpHandler(new KeyUpHandler() {
		
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(!Utils.isNotAlpKey(event.getNativeEvent().getKeyCode())){
					nameSearchButton.click();
				}
			}
		});
		
		typeSearchBox.addBitlessDomHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				typeSearchButton.click();
			}
		}, ChangeEvent.getType());
		
		typeSearchBox.addKeyUpHandler(new KeyUpHandler() {
		
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(!Utils.isNotAlpKey(event.getNativeEvent().getKeyCode())){
					typeSearchButton.click();
				}
			}
		});
		
		DefaultKeyboardSelectionHandler<TemplateInfo> selHandler = new DefaultKeyboardSelectionHandler<TemplateInfo>(dataGrid){
			@Override
			public void onCellPreview(CellPreviewEvent<TemplateInfo> event) {
				if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
				    TemplateInfo object = dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
				    if(object.getIsParent()){
				    	edit_button.setVisible(false);
				    	delete_button.setVisible(false);
				    }
				    else{
				    	edit_button.setVisible(true);
				    	delete_button.setVisible(true);
				    }
				}
				if(BrowserEvents.CONTEXTMENU.equals(event.getNativeEvent().getType())){
					Integer relRow = event.getIndex() - dataGrid.getPageStart();
				    Integer subrow = event.getContext().getSubIndex();
				    dataGrid.setKeyboardSelectedRow(relRow, subrow, true); 
   					NativeEvent nativeEvent = event.getNativeEvent();

			    	DocumentContextMenu contextMenu = new DocumentContextMenu(dataProvider.getList().get(dataGrid.getKeyboardSelectedRow()));

			    	Integer heigth = contextMenu.getHeigth();
			    	Integer width = contextMenu.getWidth();
			    	
			    	if(nativeEvent.getClientY()>590){
   						if(nativeEvent.getClientX()>994)
   							contextMenu.setPopupPosition(nativeEvent.getClientX()-width,
   								nativeEvent.getClientY()-heigth);
   						else contextMenu.setPopupPosition(nativeEvent.getClientX(),
   								nativeEvent.getClientY()-heigth);
   					}
   					else{
   						if(nativeEvent.getClientX()>994)
   							contextMenu.setPopupPosition(nativeEvent.getClientX()-width,
   								nativeEvent.getClientY());
   						else contextMenu.setPopupPosition(nativeEvent.getClientX(),
   								nativeEvent.getClientY());
   					}
   			        contextMenu.show();
				}				
			}
		};
		
 		dataGrid.addHandler(selHandler, CellPreviewEvent.getType());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label(MSG.notFiles()));
		addDataDisplay(dataGrid);
		ListHandler<TemplateInfo> sortHandler = getSortHandler();
		dataGrid.addColumnSortHandler(sortHandler);
 		final SingleSelectionModel<TemplateInfo> selectionModel = new SingleSelectionModel<TemplateInfo>(
				TemplateInfo.PROVIDES_KEY);
		dataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<TemplateInfo> createCheckboxManager());
		dataGrid.setSelectionModel(selectionModel);
		initTableColumns(selectionModel, sortHandler);
	}

//------------------------------ DataGrid Utils
	
	private ListDataProvider<TemplateInfo> dataProvider = new ListDataProvider<TemplateInfo>();

	public void addDataDisplay(HasData<TemplateInfo> display) {
		dataProvider = new ListDataProvider<TemplateInfo>(templateList);
		dataProvider.addDataDisplay(display);
	}
	
	private ListHandler<TemplateInfo> getSortHandler() {
		return new ListHandler<TemplateInfo>(dataProvider.getList()){
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.setList(dataProvider.getList());
				super.onColumnSort(event);
				List<TemplateInfo> aux  = super.getList();
				List<TemplateInfo> aux2 = new LinkedList<TemplateInfo>();
 				for(Integer i = 0 ; i< aux.size()-1;i++){
 					aux2.set(i, aux.get(aux.size()-1-i ));
 				} 				
				dataProvider.setList(aux2);
			}
		};
	}
	
	private void initContextMenu() {
		ContextMenuHandler contextMenuHandler = new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
				event.stopPropagation();				
			}
		};
		dataGrid.addDomHandler(contextMenuHandler, ContextMenuEvent.getType());
	}
	
	private class ActionHasCell implements HasCell<TemplateInfo, TemplateInfo> {
	    private ActionCell<TemplateInfo> cell;
	    String s;
	    
	    public ActionHasCell(String text, Delegate<TemplateInfo> delegate) {
	    	s = text;
	        cell = new ActionCell<TemplateInfo>(text, delegate){
	        	String text = s;
	        	@Override
	        	public void render(com.google.gwt.cell.client.Cell.Context context,
	        			TemplateInfo value, SafeHtmlBuilder sb) {
	        		if(text.equals("download")){
	        			sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-google-drive-excel\" tabindex=\"-1\">");
						sb.appendHtmlConstant("</button>");		
	        		}
	        		if(text.equals("edit")){
	        			if(!value.getIsParent()){
	        				sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-edit\" tabindex=\"-1\">");
							sb.appendHtmlConstant("</button>");
	        			}
	        		}
	        		if(text.equals("delete")){
	        			if(!value.getIsParent()){
	        				sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-icon-delete\" tabindex=\"-1\">");
							sb.appendHtmlConstant("</button>");
	        			}
	        		}
	        	}
	        };
	        
	    }

	    @Override
	    public Cell<TemplateInfo> getCell() {
	        return cell;
	    }

	    @Override
	    public FieldUpdater<TemplateInfo, TemplateInfo> getFieldUpdater() {
	        return null;
	    }

	    @Override
	    public TemplateInfo getValue(TemplateInfo object) {
	        return object;
	    }
	}
	
	private void initTableColumns(
			final SelectionModel<TemplateInfo> selectionModel,
			ListHandler<TemplateInfo> sortHandler) {
		initContextMenu();
		List<HasCell<TemplateInfo, ?>> cells = new LinkedList<HasCell<TemplateInfo, ?>>();
	    
		cells.add(new ActionHasCell("edit", new Delegate<TemplateInfo>() {

	        @Override
	        public void execute(TemplateInfo object) {
	           // EDIT CODE
	        	edit(object);
	        }
	    }));
		
	    cells.add(new ActionHasCell("delete", new Delegate<TemplateInfo>() {

	        @Override
	        public void execute(TemplateInfo object) {
	            // DELETE CODE
	        	delete(object);
	        }
	    }));
	    
	    cells.add(new ActionHasCell("download", new Delegate<TemplateInfo>() {
	    	
	        @Override
	        public void execute(TemplateInfo object) {
	            // DOWNLOAD CODE
	        	download(object);
	        }
	    }));
		
		CompositeCell<TemplateInfo> cell = new CompositeCell<TemplateInfo>(cells);
		/** Name Column **/
		Column<TemplateInfo, String> nameColumn = new Column<TemplateInfo, String>(
				new TextCell()) {
			
			@Override
			public void render(Context context, TemplateInfo object,
					SafeHtmlBuilder sb) {
				if(object.getIsParent()){
					sb.appendHtmlConstant("<span style= 'padding-right: 5px;'>"+object.getName()+"</span><span title='Documento heredado' class='aon-editDataTable-button aon-icon-shield'>&nbsp;</span>");
				}
				else {
					sb.appendHtmlConstant("<span>"+object.getName()+"</span>");
				}
			}
			
			@Override
			public String getValue(TemplateInfo object) {

				return object.getName();
			}
		
		};
		nameColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		nameColumn.setSortable(true); 
		sortHandler.setComparator(nameColumn,new Comparator<TemplateInfo>() {
			
			@Override
			public int compare(TemplateInfo o1, TemplateInfo o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		dataGrid.getColumnSortList().push(nameColumn);
		dataGrid.addColumn(nameColumn, MSG.name());
		dataGrid.setColumnWidth(nameColumn, 30, Unit.PCT);
		
		/** Type Column **/
		Column<TemplateInfo,String> typeColumn = new Column<TemplateInfo, String>(new TextCell()) {
			
			@Override
			public String getValue(TemplateInfo object) {
				return object.getType();
			}
		};
		
		typeColumn.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		typeColumn.setSortable(true); 
		sortHandler.setComparator(typeColumn,new Comparator<TemplateInfo>() {
			
			@Override
			public int compare(TemplateInfo o1, TemplateInfo o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		dataGrid.getColumnSortList().push(typeColumn);
		dataGrid.addColumn(typeColumn, MSG.type());
		dataGrid.setColumnWidth(typeColumn, 20, Unit.PCT);

		/** Download Column **/
		Column<TemplateInfo,TemplateInfo> downloadColumn = 	new Column<TemplateInfo, TemplateInfo>(cell){

			@Override
			public TemplateInfo getValue(TemplateInfo object) {
				return object;
			}
		};
		downloadColumn.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		dataGrid.addColumn(downloadColumn, MSG.actions());
		dataGrid.setColumnWidth(downloadColumn, 10, Unit.PCT);
	}
	
//------------------------------ Utils	
	
	private void download(TemplateInfo object) {
		String driveId="";
		if(object.getDriveId()!=null)driveId= object.getDriveId();
		String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download/"
            	+ "?id=" + Integer.toString(object.getId())
            	+ "&drive_id=" +URL.encode(driveId)
            	+ "&name=" +URL.encode(object.getName())
            	+ "&username="+ getUser().getLogin()
            	+ "&domain_name=" + getDomain().getName()
            	+ "&domain_id="+ getDomain().getId();
		Window.open( fileDownloadURL, "_blank",null);
	}
	
	private void edit(TemplateInfo object){
		Dialog d = new Dialog("Editar Plantilla","Grabar",true,"Cancelar",true,"edit");
		d.setTemplateInfo(object);
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				hide();
				Integer aux = dataGrid.getKeyboardSelectedRow();
				
				TemplateInfo ti2 = dataProvider.getList().get(aux);
				
				
				TextBox tb = (TextBox) flex_table.getWidget(0, 1);
				ti2.setName(tb.getText());

				ListBox lb = (ListBox) flex_table.getWidget(1, 1);
				ti2.setType(lb.getItemText(lb.getSelectedIndex()));
				
				ti2.sethasWarehouse(false);
				LinkedList<String> v = new LinkedList<String>();
				Integer i = 2;
				while(flex_table.isCellPresent(i, 1)){
					ListBox lbn = (ListBox) flex_table.getWidget(i, 1);
					if(lbn.getItemText(lbn.getSelectedIndex()) != "-")
						v.add(lbn.getItemText(lbn.getSelectedIndex()));
					if(lbn.getItemText(lbn.getSelectedIndex()).equals(MSG.targetWarehouse()))
						ti2.sethasWarehouse(true);
					i++;
				}
				ti2.setColumns(v);
				ti =  ti2;
				item.editTemplate(getDomain(), getUser(), ti2, new AsyncCallback<TemplateInfo>() {
					@Override
					public void onSuccess(TemplateInfo result) { 
						Integer index = dataGrid.getKeyboardSelectedRow();
						templateList.set(index, result);
						addDataDisplay(dataGrid);
						dataGrid.redraw();
					}
					
					@Override
					public void onFailure(Throwable caught) {

					}
				});
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	private void delete(TemplateInfo object){
		Dialog d = new Dialog("Eliminar Plantilla","Borrar",true,"Cancelar",true,"delete");
		d.setTemplateInfo(object);
		TemplatesDialog popup = new TemplatesDialog(getAonData(), d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				hide();
				item.deleteTemplate(getDomain(), getUser(), ti, new AsyncCallback<Void>() {
					TemplateInfo templateInfo = ti;
					@Override
					public void onSuccess(Void result) {
						templateList.remove(templateInfo);
						addDataDisplay(dataGrid);
						dataGrid.redraw();
					}
					
					@Override
					public void onFailure(Throwable caught) {

					}
				});
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}

	private Integer mandatoryIndex(String type) {
		if(type.equalsIgnoreCase(MSG.product())) return 7;
		else if(type.equalsIgnoreCase(MSG.stock())) return 5;
		else if(type.equalsIgnoreCase(MSG.fee())) return 10;
		else if(type.equalsIgnoreCase(MSG.consumption())) return 12;
		else if(type.equalsIgnoreCase(MSG.closedInventory())) return 7;
		else if(type.equalsIgnoreCase(MSG.valuedInventory())) return 8;
		else return 2;
	}
//------------------------------ UI Handlers
	
	@UiHandler("nameSearchButton")
	void namesbutton(ClickEvent event) {
		typeSearchBox.setText("");
		String searchStr = nameSearchBox.getText();
		LinkedList<TemplateInfo> vaux = new LinkedList<TemplateInfo>();
		vaux.addAll(templateList);
		//dataProvider.getList().stream().forEach(f-> vaux.add(f));
 		
		item.searchNameTemplate(searchStr, vaux, new AsyncCallback<LinkedList<TemplateInfo>>() {

			@Override
			public void onSuccess(LinkedList<TemplateInfo> result) {
				dataProvider = new ListDataProvider<TemplateInfo>(result);
				dataProvider.addDataDisplay(dataGrid);
				dataGrid.redraw();
			}

			@Override
			public void onFailure(Throwable caught) {
		
			}
		});
	}
	
	@UiHandler("typeSearchButton")
	void typesbutton(ClickEvent event) {
		nameSearchBox.setText("");
		String searchStr = typeSearchBox.getText();
		LinkedList<TemplateInfo> vaux = new LinkedList<TemplateInfo>();
		vaux.addAll(templateList); 		
		item.searchTypeTemplate(searchStr, vaux, new AsyncCallback<LinkedList<TemplateInfo>>() {

			@Override
			public void onSuccess(LinkedList<TemplateInfo> result) {
				dataProvider = new ListDataProvider<TemplateInfo>(result);
				dataProvider.addDataDisplay(dataGrid);
				dataGrid.redraw();
			}

			@Override
			public void onFailure(Throwable caught) {
			
			}
		});
	}
	
	@UiHandler("new_button")
	void newButton(ClickEvent event){
		Dialog d = new Dialog("Nueva Plantilla","Guardar",true,"Cancelar",true,"new");
		popup = new TemplatesDialog(getAonData(), d) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				hide();
				TemplateInfo ti = new TemplateInfo();

				TextBox tb = (TextBox) flex_table.getWidget(0, 1);
				ti.setName(tb.getText());

				ListBox lb = (ListBox) flex_table.getWidget(1, 1);
				ti.setType(lb.getSelectedItemText());
				
				ti.sethasWarehouse(false);
				LinkedList<String> v = new LinkedList<String>();
				Integer index = mandatoryIndex(lb.getSelectedItemText());
				for(Integer j = 2; j< index-1 ; j++){
					Label label = (Label) flex_table.getWidget(j, 1);
					v.add(label.getText());
				}
				Integer i = index-1;
				while(flex_table.isCellPresent(i, 1)){
					ListBox lbn = (ListBox) flex_table.getWidget(i, 1);
					if(lbn.getItemText(lbn.getSelectedIndex()) != "-")
						v.add(lbn.getSelectedItemText());
					if(lbn.getSelectedItemText().equals(MSG.targetWarehouse()))
						ti.sethasWarehouse(true);
					
					i++;
				}
				ti.setColumns(v);
				item.newTemplate(getDomain(), getUser(), ti, new AsyncCallback<TemplateInfo>() {
					@Override
					public void onSuccess(TemplateInfo result) {
						templateList.add(result);
						addDataDisplay(dataGrid);
						dataGrid.redraw();				
					}
					
					@Override
					public void onFailure(Throwable caught) {
					}
				});
			}
		};
		popup.addStyleName("gwt-PopupPanel-template");
		popup.setGlassEnabled(true);
		popup.show();
	}
	
	@UiHandler("edit_button")
	void editButton(ClickEvent event){
		TemplateInfo object;
		object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
		edit(object);
	}
	
	@UiHandler("delete_button")
	void deleteButton(ClickEvent event){
		TemplateInfo object;
		object= dataProvider.getList().get(dataGrid.getKeyboardSelectedRow());
		delete(object);
	}	
}

